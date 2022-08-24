import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class main {

   public static void main(String[] args) throws IOException {    
        HuffmanCoding.binaryTree bt = new HuffmanCoding.binaryTree();
        
        int [] charCount = HuffmanCoding.binaryTree.charCountAry;
        
        String nameInFile = args[0];
        File inFile = new File(nameInFile);
        
        String nameDebugFile = nameInFile + "_DeBug.txt";
        File debugFile = new File(nameDebugFile);
                
        FileInputStream fis = new FileInputStream(inFile);
        FileWriter fw = new FileWriter(debugFile);
        
        bt.computeCharCounts(charCount, fis); //1
        
        bt.printCountAry(fw); //2
        
        bt.constructHuffmanLL(charCount, fw); //3
        HuffmanCoding.treeNode lh = HuffmanCoding.binaryTree.list.listHead;

        
        bt.constructHuffmanBinTree(lh, fw); //4
        
        HuffmanCoding.treeNode root = HuffmanCoding.binaryTree.root;
        HuffmanCoding.binaryTree.constructCharCode(root, ""); //5
        
        
        fw.write("\nPrinting List (After Char Code Construction)\n");

        HuffmanCoding.binaryTree.list.printList(fw); //6
        fw.write("\n ------------------------------------- \n");
        
        fw.write("\nPreorder: \n");

        HuffmanCoding.binaryTree.preOrderTraversal(root, fw);  //7 
        
        fw.write("\nInorder: \n");

        HuffmanCoding.binaryTree.inOrderTraversal(root, fw);  
        
        fw.write("\nPostorder:\n");

        HuffmanCoding.binaryTree.postOrderTraversal(root, fw);  
        
        bt.userInterface();  //9

        
   }

}
class HuffmanCoding{
   
   public static class treeNode{
      String chStr;
      int freq;
      String code;
      treeNode left;
      treeNode right;
      treeNode next;
          
      public treeNode(String str, int f, String c, treeNode l, treeNode r, treeNode n){
          chStr = str;
          freq = f;
          code = c;
          left = l;
          right = r;
          next = n;     
       }
      
      void printNode(treeNode t, FileWriter debugFile) throws IOException{
         
         treeNode temp = t;
         
         if(t.left != null && t.right != null) 
            debugFile.write("['" + temp.chStr + "', "+ temp.freq + ", code: " + temp.code + ", left: '" + temp.left.chStr + "', right: '" + temp.right.chStr + "' ] ");
            // Format is : chStr, freq, code, left, right. However, not every node has a valid left/right so we just provide chStr, freq, and code for those nodes.
         else
            debugFile.write("['" + temp.chStr + "', "+ temp.freq + ", code: " + temp.code + " ] ");

         
     }
      
      
   }
   
   public static class linkedList{
      treeNode listHead;
    
         public linkedList() {
            treeNode dummy = new treeNode("", 0, "", null, null, null);
            
            listHead = dummy;
         }
         
         public void insertNewNode(treeNode head, treeNode newNode){
            int in_elm = newNode.freq;
            treeNode temp = listHead;
            
            while(temp.next != null && temp.next.freq < in_elm){
                temp = temp.next;
            }
            
            
            newNode.next = temp.next;
            temp.next = newNode;
            
         }
         
         public void printList(FileWriter debugFile) throws IOException {
         
         debugFile.write("Head --> ");
         treeNode temp = listHead;
         while(temp.next != null){
            
            if(temp.chStr == "" && temp.freq == 0) {        //Indicator of a dummy node
               debugFile.write("[ DUMMY ] -->  ");
               
            }
            else {
               temp.printNode(temp ,debugFile);
               debugFile.write(" --> ");
            }
            temp = temp.next;
         }
         
         debugFile.write("['" + temp.chStr + "', "+ temp.freq + ", code: '" + temp.code + "' , NULL] --> NULL \n");         //print last node
         
      }
      
   }
   
   public static class binaryTree{
      static linkedList list = new linkedList(); 
      static treeNode root;
      static int charCountAry[] = new int [256];    // a 1-D array to store the character counts.
      static String charCode[] = new String [256] ;  // a 1-D array to store the Huffman code table
      
           public binaryTree() {
              root = null;          
           }
             
           public static void preOrderTraversal(treeNode r, FileWriter debugFile) throws IOException {          // Current, Left, Right
              if(isLeaf(r)) {
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
              }
              else {
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
                 preOrderTraversal(r.left, debugFile);
                 preOrderTraversal(r.right, debugFile);
              }
           
           }
        
           public static void inOrderTraversal(treeNode r, FileWriter debugFile) throws IOException {           // Left, Current, Right
              if(isLeaf(r)) {
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
              }
              else {
                 inOrderTraversal(r.left, debugFile);
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
                 inOrderTraversal(r.right, debugFile);
              }
              
           }
        
           public static void postOrderTraversal(treeNode r, FileWriter debugFile) throws IOException {         // Left, Right, Current
              if(isLeaf(r)) {
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
                 
              }
              
              else {
                 postOrderTraversal(r.left, debugFile);
                 postOrderTraversal(r.right, debugFile);
                 r.printNode(r, debugFile); 
                 debugFile.write("\n");
                 
              }
           }
        
           public void computeCharCounts(int [] charCount ,FileInputStream fis) throws IOException {
              
              int index = fis.read();
              while(index != -1) {              // -1 means that the end of file is reached
                 
                 char charIn = (char) index;
                 int i = (int) charIn;
                 
                 charCount[i]++;                // "We counted another character, add it to the tally for their respective index"
                 
                 index = fis.read();
                 
              }
              
           }
           
           public static void constructCharCode(treeNode t, String c) {
              if(isLeaf(t)) {
                 t.code = c;
                 char o = (char) t.chStr.charAt(0);
                 int index = (int) o; 
                 charCode[index] = c;
              
              }
              
              else {
                    constructCharCode(t.left, c+"0");
                    constructCharCode(t.right, c+"1");
              } 
              
           }
           
           public void printCountAry(FileWriter debugFile) throws IOException {
              
            BufferedWriter bw = new BufferedWriter(debugFile);
              
              for(int i = 32; i < charCountAry.length; i++) {               //Originally, there were a few counts of carraige return and new line (ascii values of 13 and 10 respectively) 
                 if(charCountAry[i] != 0 ) {                                //For this program, we are excluding all of the non printing characters (ascci values 0 to 31) since including them
                    char ch = (char) i;                                     //seems to cause more issues with the huffman binary tree and the linked list.                    
                    debugFile.write("'"+ch+"'\t"+charCountAry[i]+"\n");
                    bw.flush();
                 }
              }
              debugFile.write("----------------------------------\n");
              bw.flush();
              
           }
           
           public void constructHuffmanLL(int [] charCount , FileWriter debugFile) throws IOException {
              linkedList newList = new linkedList();
              treeNode lh = new treeNode("dummy", 0, "", null, null, null);
              int index = 32;
              
              
              while(index < 256) {
                 if(charCount[index] > 0) {

                    char ch = (char) index; 
                    int prob = charCount[index];
                    
                    treeNode newNode = new treeNode( ""+ch, prob, "", null, null, null);
                    newList.insertNewNode(lh, newNode);
                    newList.printList(debugFile);
                   
                    
                 }
                 
                 index++;
              }
              
              list = newList;

              
           }
         
           public void constructHuffmanBinTree(treeNode listHead, FileWriter outFile) throws IOException {
              treeNode temp = list.listHead;
         
              while(temp.next.next != null){
                 treeNode newNode = new treeNode(temp.next.chStr + temp.next.next.chStr, temp.next.freq + temp.next.next.freq,
                       "", temp.next, temp.next.next, null);

                  list.insertNewNode(temp, newNode);
                  temp.next = temp.next.next.next;

                  list.printList(outFile);
                  
              }
     
              root = temp.next;
              
           }

           public static void encode(String inFile, String outFile) throws IOException {
              Scanner sc = new Scanner(new FileReader(inFile));
              BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

              String line = sc.nextLine();
              
              while(sc.hasNextLine()) {
                 for(int i = 0; i < line.length(); i++){
                      char charIn = line.charAt(i);
                      int index = (int)charIn;

                      if(index >= 32 && index < 256){ // Possible ASCII values
                          String code = charCode[index];
                          bw.write(code);
                          bw.flush();
                      }
                      
                 }
                 bw.write("\n");
                 bw.flush();
                 line = sc.nextLine();
              }
              
              //Final Line to be printed
              for(int i = 0; i < line.length(); i++){
                 char charIn = line.charAt(i);
                 int index = (int)charIn;

                 if(index < 256){ 
                     String code = charCode[index];
                     bw.write(code);
                     bw.flush();
                 }
              }
              bw.close();
           }
           
           public void decode(String inFile, String outFile) throws IOException {
              
              Scanner sc = new Scanner(new FileReader(inFile));
              BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));     
              
              treeNode spot = root;
              
              String currentLine = sc.nextLine();
              while(sc.hasNextLine()) {
                 for(int i = 0; i < currentLine.length(); i++) {       
                    
                    char oneBit = currentLine.charAt(i);            //3
                    if(oneBit == '0') { 
                       spot = spot.left;
                       
                       if(isLeaf(spot)) {         //2       //Doing step 2 after step 3 seems to prevent an extra space that would begins each line after the first
                          bw.write(spot.chStr);
                          spot = root;
                       }
                    
                       
                    }
                    else if(oneBit == '1') { 
                       spot = spot.right;
                       
                       if(isLeaf(spot)) {         //2
                          bw.write(spot.chStr);
                          spot = root;
                       }
                    
                    }
                    else { 
                       System.out.println("Error! The compress file contains an invalid character: " + oneBit);
                       System.exit(0);
                    }   

                 }
                 
                 currentLine = sc.nextLine();
                 bw.write("\n");
                 
              }
              
              String lastLine = currentLine;                    //last line of code to be decoded
              for(int i = 0; i < lastLine.length(); i++) {       
                 if(isLeaf(spot)) {         //2
                    bw.write(spot.chStr);
                    spot = root;
                 }
                 char oneBit = lastLine.charAt(i);            //3
                 if(oneBit == '0') spot = spot.left;
                 else if(oneBit == '1') spot = spot.right;
                 else { 
                    System.out.println("Error! The compress file contains an invalid character: " + oneBit);
                    System.exit(0);
                 } 
              }
              
              
              if(!isLeaf(spot)) System.out.println("Error: The compress file is corrupted");
              
              sc.close();
              bw.close();
 
           }
           
           public void userInterface() throws IOException {
              Scanner input = new Scanner(System.in);
              String nameOrg = "";
              String nameCompress;
              String deCompress;
              String yesNo = "";
              
              while(true) {

                 System.out.print("Encode a file? Type in 'no' or 'n' to terminate. Type 'yes' or 'y' to encode. \n");
                 yesNo = input.nextLine(); 
                 
                 if(yesNo.equalsIgnoreCase("N") || yesNo.equalsIgnoreCase("no")) {
                    System.out.println("\nFinished");
                    System.exit(0);
                 }
                 
                 else if (yesNo.equalsIgnoreCase("Y") || yesNo.equalsIgnoreCase("yes")){
                    System.out.print("\nName the file WITHOUT the file extension\n");
                    nameOrg = input.nextLine();
                    
                    nameCompress = nameOrg + "_Compressed.txt";
                    deCompress = nameOrg + "_DeCompress.txt";
                    nameOrg = nameOrg + ".txt";
 
                    encode(nameOrg, nameCompress);
                    //open and closed the file reader for nameOrg within the encode function
                    System.out.println("Finished compressing\n");
                    decode(nameCompress, deCompress);
                    //Any writer/reader used in the function are already closed by the time the function is done
                    System.out.println("Finished decompressing\n");
                 }
                 
                 else 
                    System.out.println("Invalid input. Try again.");

              }
              
           }
       
           public static boolean isLeaf(treeNode n) {
           
              return (n.left == null && n.right == null);
          
           }
  
   }

}
