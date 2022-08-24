

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {

   public static void main(String[] args) throws IOException {
      String initialConfig = args[0];
      File inFile = new File(initialConfig);
      
      String goalConfig = args[1];
      File inFile2 = new File(goalConfig);
      
      
      
      Scanner sc = new Scanner(new FileReader(inFile));
      int[] initial = new int[9];
      for(int i = 0; i < initial.length; i++) {
         initial[i] = sc.nextInt();
        
      }
      System.out.println("\n");
      Scanner sc2 = new Scanner(new FileReader(inFile2));
      int[] goal = new int[9];
      for(int i = 0; i < goal.length; i++) {
         goal[i] = sc2.nextInt();
         
      }
      
      
      File outFile1 = new File(args[2]);
      FileWriter fw = new FileWriter(outFile1);
      
      File outFile2 = new File(args[3]);
      BufferedWriter bw = new BufferedWriter(new FileWriter(outFile2));
      
      aStar as = new aStar();
      
      aStarNode startNode = new aStarNode(initial);
      aStarNode goalNode = new aStarNode(goal);
      linkedList openList = new linkedList();
      linkedList closeList = new linkedList();

      as.startNode = startNode;
      as.goalNode = goalNode;
      
      startNode.gStar = 0;
      startNode.hStar = as.computeHstar(startNode);
      startNode.fStar = startNode.gStar + startNode.hStar;
      
      openList.insertAscend(startNode);
      
      fw.write("This is Open list: \n");    //9
      openList.printList(fw);
      
      fw.write("\nThis is Close list: \n");
      closeList.printList(fw);
      
      aStarNode currentNode = new aStarNode(initial);
      linkedList childList = new linkedList();
      
      while(!as.isGoalNode(currentNode) && openList.listHead.next != null){  //2-10 until currentNode is goalNode or openList is empty
         int printCount = 0;
         currentNode = openList.remove();
         
         if(as.isGoalNode(currentNode)) {
            bw.write("START\n");
            printSolution(bw,currentNode);
            bw.write("GOAL\n");
            bw.flush();
            System.out.print("DONE");
            System.exit(0);
           
          }
         
         
         as.constructChildList(currentNode, childList);
         
         while(childList.listHead.next != null){  // 5-7 until child list is empty
            //TODO: Continue to Figure out open List
            aStarNode child = childList.remove();  //5
            
            child.gStar = as.computeGstar(child);
            child.hStar = as.computeHstar(child);
            child.fStar = child.gStar + child.hStar;
            //System.out.println(child.fStar);

            
            //If child is not in open list and not in close list
            if(!openList.searchNode(child) && !closeList.searchNode(child)) {
               openList.insertAscend(child);
               
               child.parent = currentNode;
               
               
            }
            
            else if(openList.searchNode(child) && child.fStar < openList.listHead.next.fStar) {
               openList.remove();
               openList.insertAscend(child);
               child.parent = currentNode;
            }
            
            else if(closeList.searchNode(child) && child.fStar < closeList.listHead.next.fStar) {
               closeList.remove();
               openList.insertAscend(child);
               child.parent = currentNode;
            }
            
         }
         
         closeList.insertNoOrder(currentNode);
         
         if(printCount <= 30) {
            fw.write("\nThis is Open list: \n");    //9
            openList.printList(fw);
            
            fw.write("\nThis is Close list: \n");
            closeList.printList(fw);
            
            printCount++;
         }
   }
   
   if(openList.listHead.next == null && !as.isGoalNode(currentNode)) 
      fw.write("Error: No solution can be found in the search.");
      
      

   }
   
   public static void printSolution(BufferedWriter bw, aStarNode n) throws IOException {
      if(n == null) return;

      printSolution(bw, n.parent);
   
      for (int i = 0; i < n.config.length; i++) {
         if(i == 3 || i == 6) bw.write("\n");
         bw.write(n.config[i] +" ");
         
      }
      bw.write("\n\n");
      bw.flush();

   }
   
   public static class linkedList{
      aStarNode listHead;
      
      
      public linkedList() {
         int dummyConfig [] = new int[9];
         aStarNode dummy = new aStarNode(dummyConfig);
         listHead = dummy;
         
      }
      
      public boolean searchNode(aStarNode n) {
         aStarNode temp = listHead;
         
    
         while(temp != null) {
            if(n.config == temp.config) return true;
         
            temp = temp.next;
            
         }
         
         return false;
      }
      
      
      
      
      public void insertAscend(aStarNode newNode) {
         
         
           int fs = newNode.fStar;
           aStarNode temp = listHead;
           while(temp.next != null && temp.next.fStar < fs){
              temp = temp.next;
           }
           
    
           newNode.next = temp.next;
           temp.next = newNode;
 
         
      }
      
      public void insertNoOrder(aStarNode newNode) {
         
         if(listHead.next == null) {
            listHead.next = newNode;
         }
         
         else {
            aStarNode temp = listHead;
         
            newNode.next = temp.next;
            temp.next = newNode;

         }
      }
      
      public aStarNode remove() {
         
         if(listHead.next == null) return null;
         
         aStarNode temp = listHead.next;
         listHead.next = listHead.next.next;
         
         return temp;
      }
      
      public void printList(FileWriter fw) throws IOException {
         aStarNode temp = listHead;
         while(temp.next != null) {
            //print node
            temp.printNode(fw);
            temp = temp.next;
         }
         temp.printNode(fw);
         
      }
      
   }
   public static class aStarNode{
      int config[] = new int[9]; 
      int gStar, hStar, fStar;
      aStarNode parent , next;
      
      public aStarNode(int [] c){
         parent = null;
         next = null;
         config = c;
      }
      
      
      public void printNode(FileWriter fw) throws IOException {
         fw.write("<" + fStar + ":: ");
         for(int i = 0; i < config.length; i++) {
            fw.write(config[i] + " ");
         }
         fw.write(":: ");
         
         if(parent != null) {
            for(int i = 0; i < parent.config.length; i++) {
               fw.write(parent.config[i] + " ");
            }
         }
         fw.write(">\n");
      }
      
      
      
      
      
   }
   
   public static class aStar{
      aStarNode startNode, goalNode;
      linkedList open, close, childList;
      
      public aStar() {
         
      }
      
      public int computeGstar(aStarNode n) {
         if(n.parent == null) return 1;
         return n.parent.gStar + 1;
        
      }
      
      public int computeHstar(aStarNode n) {
         //Mapping represents h*2 which tells us how far each number is from their goal positions (also known as the Manhattan Distance). 
         //The rows represent the current position on the grid. The columns represent the destination. For instance, to go from pos 0 to pos 8, it would take 4 spots 
         int [][] mapping = new int [9][9];
         int newHstar = 0;
         
         mapping[0][1] = 1;    mapping[1][0] = 1;    mapping[2][0] = 2;  
         mapping[0][2] = 2;    mapping[1][2] = 1;    mapping[2][1] = 1;  
         mapping[0][3] = 1;    mapping[1][3] = 2;    mapping[2][3] = 3;  
         mapping[0][4] = 2;    mapping[1][4] = 1;    mapping[2][4] = 2;  
         mapping[0][5] = 3;    mapping[1][5] = 2;    mapping[2][5] = 1;  
         mapping[0][6] = 2;    mapping[1][6] = 3;    mapping[2][6] = 4;  
         mapping[0][7] = 3;    mapping[1][7] = 2;    mapping[2][7] = 3;  
         mapping[0][8] = 4;    mapping[1][8] = 3;    mapping[2][8] = 2;  
         mapping[0][0] = 0;    mapping[1][1] = 0;    mapping[2][2] = 0;
         
         mapping[3][0] = 1;    mapping[4][0] = 2;    mapping[5][0] = 3; 
         mapping[3][1] = 2;    mapping[4][1] = 1;    mapping[5][1] = 2; 
         mapping[3][2] = 3;    mapping[4][2] = 2;    mapping[5][2] = 1; 
         mapping[3][4] = 1;    mapping[4][3] = 1;    mapping[5][3] = 2; 
         mapping[3][5] = 2;    mapping[4][5] = 1;    mapping[5][4] = 1; 
         mapping[3][6] = 1;    mapping[4][6] = 2;    mapping[5][6] = 3; 
         mapping[3][7] = 2;    mapping[4][7] = 1;    mapping[5][7] = 2; 
         mapping[3][8] = 3;    mapping[4][8] = 2;    mapping[5][8] = 1;                  
         mapping[3][3] = 0;    mapping[4][4] = 0;    mapping[5][5] = 0;
         
         mapping[6][0] = 2;    mapping[7][0] = 3;    mapping[8][0] = 4;
         mapping[6][1] = 3;    mapping[7][1] = 2;    mapping[8][1] = 3;
         mapping[6][2] = 4;    mapping[7][2] = 3;    mapping[8][2] = 2;
         mapping[6][3] = 1;    mapping[7][3] = 2;    mapping[8][3] = 3;
         mapping[6][4] = 2;    mapping[7][4] = 1;    mapping[8][4] = 2;
         mapping[6][5] = 3;    mapping[7][5] = 2;    mapping[8][5] = 1;
         mapping[6][7] = 1;    mapping[7][6] = 1;    mapping[8][6] = 2;
         mapping[6][8] = 2;    mapping[7][8] = 1;    mapping[8][7] = 1;
         mapping[6][6] = 0;    mapping[7][7] = 0;    mapping[8][8] = 0;
        
         for(int i = 0; i < 9; i++) {
               if(n.config[i] != goalNode.config[i] )
                  newHstar+= mapping[ i ] [ findGoalIndex(n.config[i]) ];
               
               // System.out.println("Current Position: " + i + "\tconfig num: " + n.config[i] + "\t Goal Position: " + findGoalIndex(n.config[i]) + "\t Con Num Dist to Goal: " + 
                //     mapping[ i ] [ findGoalIndex(n.config[i]) ]);
               //System.out.print("Position: " + i + "\tConfigNum: " + n.config[i] + " \tgnConfigNum: " + goalNode.config[i] +"\t Con Num Dist to Goal: ");
               //System.out.println(mapping[ n.config[i] ] [ findGoalIndex(n.config[i]) ]);
               
         }
         
         n.hStar = newHstar;
         //System.out.println(n.hStar);
         
         return n.hStar;
        
      }
      
      public int findGoalIndex(int c) {
         for(int i = 0; i < goalNode.config.length; i++) {
            if(c == goalNode.config[i]) return i;
         }
         
         return -1;
         
      }
      
      public boolean isGoalNode(aStarNode n) {
         if(match(n.config, goalNode.config)) return true;
         else return false;
            
      }
      
      public boolean match(int x[], int y[]) {
         for(int i = 0; i < x.length; i++) {
            if(x[i] != y[i]) return false;
            else continue;
         }
         return true;
      }
      
      public boolean checkAncestors(aStarNode currentNode, int newConfig[]) {
         
         if(currentNode.parent == null) return false;
         
         if(match(currentNode.config, newConfig)) {
            //System.out.println("Found ancestor");
            return true;
         }
         return checkAncestors(currentNode.parent, newConfig);

 
      }
      
      public aStarNode constructChildList(aStarNode currentNode, linkedList childList) {
         //Here, we list out the possible ways 0 can move from it's current position
         aStarNode tempNode = currentNode;
         int configCheck[] = tempNode.config;
         int zeroPosition = findZero(configCheck);
         
         //printArray(configCheck);
         
         int m [][] = { {1,3},      // Possible places the 0 can move from position 0 to 8         
               {0, 2, 4},
               {1,5},
               {0,4,6},             // 1 2 3
               {1,3,5, 7},          // 4 5 6
               {2,4,8},             // 7 8 9        position labels
               {3,7},
               {4,6,8},
               {5,7} } ;
         
         
         for(int i = 0; i < m[zeroPosition].length; i++) {
            int [] tempConfig = swapPosition(tempNode.config, zeroPosition, m[zeroPosition][i]);
            aStarNode newNode = new aStarNode(tempConfig);
            if(!checkAncestors(currentNode, tempConfig ) )
               childList.insertNoOrder(newNode);        
         }
        
            
         return childList.listHead;
      }
      
      
      public int findZero(int config[]) {
         
         for(int i = 0; i < config.length; i++) {           // find which index the '0' is located
            if(config[i] == 0) return i;
         }
         
         return -1;
      }
      
      public int[] swapPosition(int config[], int zeroPos, int newPos) {
         int newConfig[] = new int[9];          // Used to fetch out new configs based on 0's current position. Swap the positions of 0 with the an adjacent number
         //newConfig = config;
         for(int i = 0; i < config.length; i++)
            newConfig[i] = config[i];
         
         int zeroValue = config[zeroPos];
         newConfig[zeroPos] = newConfig[newPos];
         newConfig[newPos] = zeroValue;
         
         
         return newConfig;
         
      }
      
      public void printArray(int config[]) {
         for (int i=0; i< config.length; i++) {
            System.out.print(config[i] +" ");
         }
         System.out.println();
      }
      
      
   }
}


