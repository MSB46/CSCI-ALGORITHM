#include <iostream>
#include <fstream>
using namespace std ;
// Assignment 3: Radix Sort 
// Author: Michael Saulon Boodoosingh
// Date: 02/28/2021

class listNode{
	private:
		int data;
		listNode *next;
		
	public:
		listNode(int a){
			data = a;
			
		}
		
		void printNode(listNode* n, ofstream & write){
			write << "(" << n->data << ", " << n->next->data << ")-->" << endl;
		}
		
	
	friend class LLStack;
	friend class LLQueue;
	friend class RadixSort;

	
};

class LLStack{
	private:
		listNode* top;
		
	public:
		LLStack(){
			listNode *dummy = new listNode(-99999);
			dummy->next = NULL;
			
			top = dummy;		
			
		}
		
		void push(listNode *n){
			n->next = top->next;
			top->next = n;
			
		}
		
		listNode* pop(){
			if(isEmpty()){
				return NULL;
			}
			
			listNode *temp = top->next;
			top->next = top->next->next;		//delete the non dummy head node (aka the node after the dummy "head" node)
				
			return temp;
		}
		
		bool isEmpty(){
			if(top == NULL) return true;
			return false;
		}
		
		void printStack(ofstream & write){
			write << "*** Performing printStack\n";
			int lastData;	
			
			listNode* temp;
			temp = top;
			write <<  "Top -->";
			while(temp->next != NULL){
				write << "(" << (temp->data)
							<< ", " << to_string(temp->next->data) 
							<<  ")-->";
				
				lastData = temp->next->data;
				temp = temp->next; 
				
				
			}
		
			write << "(" << lastData << ", " "NULL)--> NULL" << endl << endl;			
			
		}

	friend class RadixSort;
};

class LLQueue{
	private:
		listNode* head;
		listNode* tail;
		
		
	public:
		LLQueue(){

			listNode* dummy = new listNode(-99999);
			 
			head = dummy;
			tail = dummy;
			
			head->next = NULL;
			tail->next = NULL;
			
			
		}
		void insertQ(listNode *n){ 
			//cout << "inserting " << n->data << "\n";
			
			if(isEmpty()){
				head->next = n;
				//cout << head->next->data << endl;
			}
			else
				tail->next = n;
				
			tail = n;
							
		}
		
		
		listNode* deleteQ(){
			//cout << "deleting " << head->next->data << "\n";
			
			if(isEmpty()){							//Empty
				return NULL;	
			}
			
			else if(head->next == tail){			//Has one node
	            listNode * temp = tail;
	            tail = head;
	            head->next = NULL;
	            
	            return temp;
        	}
        		
       		else{									// Has two or more nodes
	            listNode *temp = head->next;
				head->next = head->next->next;
				
	            return temp;
      		}
        
		}
		
		bool isEmpty(){
			return (tail == NULL || head->next == NULL) ;		// If tail points to the dummy node, Q is empty. Only time head is equal to the tail is if
			
		}	

	friend class RadixSort;
};

class RadixSort{
	private:
		const static int tableSize = 10;
		LLQueue *hashTable[2][tableSize];
		int data;
		int currentTable;
		int previousTable;
		int numDigits;			// the number of digit in the largest integer that controls the number of iterations of Radix sort
		int offset;			 	// the absolute value of the largest negative integer in the data;
		
		int currentPosition;	// The digit position of the number while sorting.
		
	public:	
		RadixSort(){		
			for(int i = 0; i < 2; i++)
				for(int j = 0; j < tableSize; j++)
					hashTable[i][j] = new LLQueue();					
		}
		
		void firstReading(ifstream & input, ofstream & output){
			output << "*** Performing firstReading\n";
			int smallestNum = 0;
			int largestNum = 0;
			string data;
			while(!input.eof()){
				input >> data;
				
				if(stoi(data) < smallestNum)
					smallestNum = stoi(data);
					
				if(stoi(data) > largestNum)
					largestNum = stoi(data);

			}
			
			if(smallestNum < 0)
				offset = abs(smallestNum);
			else
				offset = 0;
				
			largestNum += offset;
			numDigits = getLength(largestNum);
			output << "largestNum: " << largestNum << " smallestNum: " << smallestNum 
					<< " offset: " << offset << " numDigits: " << numDigits << endl;
			
		}
		
		LLStack loadStack(ifstream & input, ofstream & output){
			output << "*** Performing loadStack\n";
			LLStack *s = new LLStack();
			
			int data = -99999;
			string dataString;
			int lastData;
			
			while(!input.eof()){
				input >> dataString;
				data = stoi(dataString);
				
				if(lastData != data){						// Without checking lastData, the last number in the file (999 in Data2.txt) would be pushed into the stack twice. 
					data += offset;
					listNode *newNode = new listNode(data);
					s->push(newNode);
					//cout << data << endl;
				}

				lastData = data - offset;				
				
			}
			
			return *s;
		}
		
		void RSort(LLStack *s, ofstream & output1 , ofstream & output2){
			output2 << "*** Performing RSort\n";	//0
			currentPosition = 0;				
			currentTable = 0;					//1
			
			moveStack(s, currentPosition, currentTable, output2);		//2
			
			printTable(currentTable, output2);		//3
			
			currentPosition++;		//4
			currentTable = 1;
			previousTable = 0;
			int currentQueue = 0; 
						
			int hashIndex;
			
			while(currentPosition < numDigits){ //11
				
				while(currentQueue <= tableSize - 1){	//8
				
					while(!hashTable[previousTable][currentQueue]->isEmpty()){ //6
	
						listNode *newNode =  hashTable[previousTable][currentQueue]->head->next;
						hashTable[previousTable][currentQueue]->deleteQ();	//5
						
						int deletedNodeData = newNode->data;
						
					 	hashIndex = getDigit(deletedNodeData, currentPosition);
					 			
						hashTable[currentTable][hashIndex]->insertQ(newNode);

					}
						
					currentQueue++;	//7
					
				}
				
				printTable(currentTable, output2);	//9
				
				previousTable = currentTable;		//10
				currentTable = ( (currentTable + 1) % 2);
				currentQueue = 0;
				currentPosition++;
							
			}
		
			printSortedData(previousTable, output1); //12
			
			cout << "Finished RSort" << " | Offset: " << offset << " | Max digits: " << numDigits;	
		}
		
		void moveStack(LLStack *s, int whichPos, int whichTable, ofstream & output){
			output << "*** Performing moveStack" << endl;		
			int hashIndex;
			
			while( (s->top->next != NULL)  ){
				listNode *newNode = s->pop();
				hashIndex = getDigit(newNode->data, whichPos);
				// get the currentPosition of the data in the node, returns a single digit
				
				hashTable[whichTable][hashIndex]->insertQ(newNode);
				//add newNode at the tail of the queue at hashTable
			}
			
		}
		
		int getLength(int d){
			string s = to_string(d);
			return s.length();
		}
		
		int getDigit(int d, int position){
			// If the integer d has less digits than the max digits, fill in the missing 0s

			string s = to_string(d);
			
			while(s.length() != numDigits){
				s = "0" + s;
			}
			
			string targetChar = "";
			
			targetChar += s[s.length()-1 - position];
		
			return stoi(targetChar);
			
		}
		
		void printTable(int whichTable, ofstream & output){
				output << "*** Performing printTable" << endl;
				for(int j = 0; j < tableSize; j++){
					if(!hashTable[whichTable][j]->isEmpty())
						printQ(whichTable, j, output);
						
					else continue;	
				}
			
		}
		
		void printSortedData(int whichTable, ofstream & output){
			// Print each none empty queue in hashTable[whichTable], one data per text line;
			int lastData;
			for(int i = 0; i < tableSize; i++){	
				listNode* temp = hashTable[whichTable][i]->head->next;
			
				while(temp != NULL && temp->next != hashTable[whichTable][i]-> tail-> next ){
				
					output << ((temp->data) - offset) << endl;
					
					lastData = temp->next->data - offset;
					
					temp = temp->next; 
					
				}
			}
			
			output << lastData << endl;
		}

		
		void printQ(int whichTable, int index, ofstream & write){
			
			int lastData = hashTable[whichTable][index]->head->data;	// -99999 by default
			
			listNode* temp = hashTable[whichTable][index]->head;
			write <<  "Table[" << whichTable << "][" << index << "]: ";
			
			while(temp != NULL && temp->next != hashTable[whichTable][index]-> tail-> next ){
				
				write << " (" << (temp->data)
							<< ", " << (temp->next->data) 
							<<  ")-->";
				
				lastData = temp->next->data;
				
				temp = temp->next; 
				
			}
		
			write << "(" << lastData <<", " << "NULL)--> NULL" << endl;
			
		}
		
};

int main(int argc, char *argv[]){
	
	string inFile = argv[1];		//Input	
	ifstream input ;
	input.open(inFile);

	string outFile1 = argv[ 2 ];		
	ofstream output1 ;
	output1.open(outFile1);

	string outFile2 = argv[ 3 ];	
	ofstream output2 ;
	output2.open(outFile2);
	
		
	RadixSort *rs = new RadixSort();
	rs->firstReading(input,output2);
	
	input.close();
	input.open(inFile);
	
	LLStack *s = new LLStack();
	*s = rs->loadStack(input,output2);
	
	s->printStack(output2);
	
	rs->RSort(s, output1, output2); 
	
	input.close();

	output1.close();
	output2.close();

	return 0 ;
	
}
