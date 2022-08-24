import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//Assignment 6: Dependency Graph / Scheduling 
//Author: Michael Saulon Boodoosingh
//Date: 04/09/2021


public class main {
   public static void main(String[] args) throws IOException {      
      String depGraph = args[0];
      File inFile = new File(depGraph);
      
      String procTime = args[1];
      
      Scanner sc = new Scanner(new FileReader(inFile));

      String firstInt = sc.nextLine();
      
      int numNodes = Integer.parseInt(firstInt);
      
      int numProcs = Integer.parseInt(args[2]);
      if(numProcs <= 0) {
         System.out.println("Error: Need 1 or more processors");
         System.exit(0);
      }
      else if(numProcs > numNodes)
         numProcs = numNodes;
      
      
      String schTable = args[3];
      
      String debug = args[4];
      
      FileWriter fw = new FileWriter(schTable);
      FileWriter fw2 = new FileWriter(debug);
      
      schedule s = new schedule(numNodes, numProcs);
      
      s.loadMatrix(depGraph);
      s.loadJobTimeAry(procTime);
      
      s.setMatrix();     

      s.printMatrix(fw2);
      
      int ID = 0;
      int availProcessor = 1;
      
      while(!s.isGraphEmpty() && s.currentTime < s.totalJobTime){
         
         while(ID != -1) {
            ID = s.findOrphan();
            if(ID > 0){
               node newNode = new node(ID, s.jobTimeAry[ID], null);
               s.openInsert(newNode);
               s.printOPEN(fw2); // debug print
            }
         }
         
         
         while(s.OPEN.next != null && s.procsUsed < numProcs && availProcessor > 0) {
            availProcessor = s.getNextProc(s.currentTime);
            if(availProcessor > 0){ 
               s.procsUsed++;
               node newJob = s.deleteNode(); // newJob is a node!
               s.putJobOnTable(availProcessor, s.currentTime, newJob.jobID, newJob.jobTime);
            }
         }
         
         if(s.currentTime < s.totalJobTime)
         s.printTable(fw,  s.currentTime);   //8
         
         if(s.checkCycle()) {
            
            System.out.println("Error: There is a cycle in the graph");
            
            s.printError(fw);
            s.printError(fw2);
            System.exit(0);
         }
         
         s.currentTime++;

         int proc = 0;
         while(proc <= s.numProcs) {
            if(s.table[proc][s.currentTime] <= 0 && s.table[proc][ s.currentTime - 1] > 0) {               
               ID = s.table[proc][s.currentTime - 1];
               s.deleteJob(ID);
               System.out.print(ID + " is done \n");
               // the processor, proc, just finished a job in the 
               // previous time cycle.
               s.printMatrix(fw2);
            } 
            
            proc++;
         }
         
      }
      
      s.printTable(fw, s.currentTime);
      System.out.println("DONE");
   }
   
   
   public static class node{
      int jobID;
      int jobTime;
      node next;
      
      public node(int ID, int jt, node n){
         jobID = ID;
         jobTime = jt;
         next = n;
      }
      
      public void printNode(FileWriter write) throws IOException {
         BufferedWriter bw = new BufferedWriter(write);      
         bw.write("JOB ID: " + jobID + " JOB TIME: " + jobTime + " Next: ");
      
      }
      
   }
   
   public static class schedule{
      int numNodes;
      int numProcs;
      int procsUsed;
      int currentTime;
      int totalJobTime;
      int jobTimeAry[];
      
      int adjMatrix[][];
      int table[][];
      
      node OPEN;
      
      
      public schedule(int nn, int np){
         numNodes = nn;
         numProcs = np;
         
         OPEN = new node(0, -9, null);
         currentTime = 0;
         procsUsed = 0;
         
  
      }
      public void loadMatrix(String inFile1) throws FileNotFoundException {
         adjMatrix = new int[numNodes + 1][];  
         for(int i = 0; i < adjMatrix.length ; i++) 
            adjMatrix[i] = new int[numNodes + 1];
         
         adjMatrix[0][0] = numNodes;
         
         Scanner sc = new Scanner(new FileReader(inFile1));
         String currentLine = sc.nextLine();
         numNodes = Integer.parseInt(currentLine);
         
         while(sc.hasNextInt()) {
            int nodeVal = sc.nextInt();
            int dependent = sc.nextInt();
            adjMatrix[nodeVal][dependent] = 1;
            // Find out what is between the job ID and the time
         }
         
      }
      
      public int loadJobTimeAry(String inFile2) throws FileNotFoundException {
         Scanner sc = new Scanner(new FileReader(inFile2));
         sc.nextLine();
         
         jobTimeAry = new int[numNodes + 1];
         
         while(sc.hasNextInt()) {
            int ID = sc.nextInt();
            int time = sc.nextInt();
            
            totalJobTime += time;
            
            jobTimeAry[ID] = time;
            // Find out what is between the job ID and the time
         }
         
         
         table = new int[numProcs + 1][];
         for(int i = 0; i < table.length; i++)
            table[i] = new int[totalJobTime + 1];

         return totalJobTime;
         // read each pair <jobID, time> from inFile2 and load to jobTimeAry;
      }
      
      public void setMatrix() {
         for(int i = 0; i < adjMatrix.length; i++) {
            for(int j = 0; j < adjMatrix[0].length; j++) {
               
               if(i == j) {
                  adjMatrix[i][j] = 1; 
                  continue;
               }
               
               if(adjMatrix[i][j] != 0) { 
                  adjMatrix[i][0]++;
                  adjMatrix[0][j]++;
               }
                  
            }
         }
         
         adjMatrix[0][0] = numNodes;

      }
      
      public void printMatrix(FileWriter outFile) throws IOException {
         BufferedWriter bw = new BufferedWriter(outFile);
         bw.write("Printing Matrix\n=====================\n");
         
         
         for(int i = 0; i < adjMatrix[0].length; i++)
            if(i <= 9)
               bw.write(i + "  ");
            else
               bw.write(i + " ");
         
         bw.write("\n---"); 
         for(int i = 0; i <= adjMatrix[0].length; i++) {
            bw.write("--"); 
         }
         bw.write("---\n"); 
         
         for(int i = 0; i < adjMatrix.length; i++) {
            for(int j = 0; j < adjMatrix[0].length; j++) {
              
               if(adjMatrix[i][j]  < 9)
                  bw.write(adjMatrix[i][j]  + "  ");
               else
                  bw.write(adjMatrix[i][j]  + " ");
            }
            bw.write("\n");
            bw.flush();
         }
         //bw.close();
      
      }
      
      public void printTable(FileWriter outFile1, int currentTime) throws IOException {
         BufferedWriter bw = new BufferedWriter(outFile1);
         bw.write("Printing Table\n=====================\n");
         bw.write("ProcUsed: " + procsUsed + " currentTime: " + currentTime + "\n\t  ");
         
         for(int i = 0; i <= currentTime; i++) {        //Neatness goes a long way
            if(i<=9)
               bw.write(i + "    ");
            else
               bw.write(i + "   ");
            
         }
         bw.write("\n\t");
         
         for(int i = 0; i <= currentTime; i++) {
            bw.write("-----"); 
         }
         bw.write("-\n");
         bw.flush();
         
         for(int i = 1; i <= numProcs; i++) {
            bw.write("P(" +i+")\t");
            for(int j = 0; j <= currentTime; j++) {
               
               if(table[i][j] == 0) 
                  bw.write("|  -" + " ");
               else 
                  if(table[i][j] <= 9)
                     bw.write("|  " + table[i][j] + " ");
                  else
                     bw.write("| " + table[i][j] + " ");
            }
            bw.write("|\n\n");
         }
         bw.flush();
         
      }
      
      public int findOrphan() {
         int found = -1;
         for(int j = 1; j < adjMatrix[0].length; j++) {
            if(adjMatrix[0][j] == 0 && adjMatrix[j][j] == 1) {
               adjMatrix[j][j] = 2;
               found = j;
               break;
            }
         }
         
         return found;
         
         // Check AdjMatrix[0][j] to find the next un-marked orphan node, j, i.e., AdjMatrix[0][j] == 0 &&
         //AdjMatrix[j][j] == 1. If found, mark the orphan, i.e., set AdjMatrix[j][j] = 2, then returns j;
         // if no such j, returns -1.
      }
      
      public void openInsert(node n) {
         int value = n.jobID;
         node temp = OPEN;
         
         if(OPEN.next == null)
            OPEN.next = n;
         
         else {
            while(temp.next != null && adjMatrix[value][0] <= adjMatrix[temp.next.jobID][0]){
                temp = temp.next;
            }
   
            n.next = temp.next;
            temp.next = n;
         }
         // on your own. Perform a linked list insertion;
         // insert node into OPEN in the descending order by the # of dependents.
      }
      
      public void printOPEN(FileWriter outFile2) throws IOException {
         BufferedWriter bw = new BufferedWriter(outFile2);
         bw.write("\nPrinting List\n=====================\n");
         
         node temp = OPEN;
         while(temp.next != null){
            
            if(temp.jobID == 0) {        //Indicator of a dummy node
               bw.write("[ DUMMY ] -->  ");
               //bw.flush();
               
            }
            else {
               bw.write("[ID: "+temp.jobID + " JT: " + temp.jobTime);
               bw.write("] --> ");
            }
            bw.flush();
            temp = temp.next;
         }
         
         bw.write("[ID: "+temp.jobID + " JT: " + temp.jobTime + "] --> [NULL]");         //print last node
         bw.write("\nEnd of List\n=====================\n");
         bw.flush();
      }
      
      public node deleteNode() {
         node temp = OPEN.next;
         OPEN.next = OPEN.next.next;      //delete the non dummy head node (aka the node after the dummy "head" node)
                 
         return temp;
         
      }
      
      public int getNextProc(int currentTime) {
         int found = -1;
         for(int i = 1; i <= numProcs; i++) {
            if(table[i][currentTime] == 0) { 
               found = i;
               break;
            }
         }
         
         return found;
         // check Table [i][ currentTime] to find the first i where Table [i][ currentTime] == 0
         // if found returns i, else returns -1, means no available processor.
      }
   
      public void putJobOnTable (int availProc, int currentTime, int jobId, int jobTime) {
         int time = currentTime;
         int endTime = time + jobTime;
         while(time < endTime) {
            table[availProc][time] = jobId;
            time++;
         }
      }
      
      public boolean isGraphEmpty() {
         return adjMatrix[0][0] == 0;
      }
      
      public boolean checkCycle() {
         
         return OPEN.next == null 
               && !isGraphEmpty() 
               && procsUsed == 0;
      
      }
      
      public void deleteJob(int ID) {
         procsUsed--;
         adjMatrix[ID][ID] = 0;
         adjMatrix[0][0]--;
         
         for(int j = 1; j <= numNodes; j++) {
            if(adjMatrix[ID][j] > 0)
               adjMatrix[0][j]--;
            
         }

      }
      
      public void printError(FileWriter fw) throws IOException {
         //Prints that an error has occured due to a detected cycle in the graph
         BufferedWriter bw = new BufferedWriter(fw);
         bw.write("\nERROR: A cycle has been detected in the graph\nPrinting has stopped");
         bw.flush();
      }
      
   }
   
}
