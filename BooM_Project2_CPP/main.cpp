#include <iostream>
#include <fstream>
#include <string>
using namespace std ;

class listNode{
	private:
		string firstName;
		string lastName;
		listNode *next;
		
	public:
		listNode(string fn, string ln){
			firstName = fn;
			lastName = ln;
			
		}
		
		void printNode(listNode* n, ofstream & write){
			write << "(First: " << n->firstName << ", Last: " << n->lastName << ", Next: " << n->next->firstName <<")-->";
		}
		
	
	friend class hashTable;	
};

class hashTable{
	private:
		char op;
		int bucketSize;
		listNode *hashTable[];
		
	public:
		
		void createHashTable(int b){
				
			
			bucketSize = b;
			
			for(int i = 0; i < b; i++){
				hashTable[i] = new listNode("dummyfirst","dummylast");
			}
		
		}
		
		int doit(string ln){
			unsigned int hash_value = 1;
			for(int i = 0; i < ln.length(); i++){
				hash_value = hash_value * 32 + (int) ln[i];
			}
			
			return hash_value % bucketSize;
			
		}
		
		void informationProcessing(ifstream & input, ofstream & output){
			string str;
			int position = -1;
			int index;
			string fn, ln;
			
			while(!input.eof()){
				position = ++position % 3;
				input >> str;
				
				
				// 0 = op; 1 = firstName; 2 = lastName
				if(position == 0){
					op = str[0];
					if(op == '+' || op == '-' || op == '?' )
						output << "Op: " << op << endl;
					
				}
				
				else if(position == 1){
					fn = str;
					output << "First: " << fn << endl;
				}
				
				else if(position == 2){
					ln = str;
					output << "Last: " << ln << endl; 
					index = doit(ln);
					output << "Index of " << ln << ": " << index << endl;
					printList(index,output);
										
						if(op == '+'){
							hashInsert(index, fn, ln, output);
						}
						
						if(op == '-'){
							hashDelete(index, fn, ln, output);
						}
						
						if(op == '?'){
							hashRetrieval(index, fn, ln, output);
						}
						
						
				}
				
				
			}
		}
		
		listNode* findSpot(int index, string fn, string ln){
			
			listNode* spot = hashTable[index];
			
			while(spot->next != NULL && spot->next->lastName < ln)
				spot = spot->next;
				
			while(  (spot->next != NULL && spot->next->lastName == ln) && spot->next->firstName < fn) 
				spot = spot->next;
				
			return spot;
			
			
		}
	
		void hashInsert(int index, string fn, string ln, ofstream & output){
			output << "\n***Peforming hashInsert on " << fn << ", " << ln << endl;
			listNode *spot = findSpot(index, fn, ln);
			
			if(spot->next != NULL && (spot->next->firstName == fn && spot->next->lastName == ln) )
				output << "***WARNING, the record is already in the database\n\n";
			
			else{
				listNode *newNode = new listNode(fn,ln);
				newNode->next = spot->next;
				spot->next = newNode;
				printList(index, output); 
			}
			
		}
		
		void hashDelete(int index, string fn, string ln, ofstream & output){
			output << "\n***Deleting hashDelete on " << fn << ", " << ln << endl;
			listNode *s = findSpot(index, fn, ln);
			
			if(s->next != NULL && (s->next->firstName == fn && s->next->lastName == ln) ){
				
				listNode *junk = s->next;
				s->next = s->next->next;
				junk->next = NULL;
				delete junk;
				
				printList(index, output);
				
			}
				
			
			else
				output << "***WARNING, the record NOT on the database\n\n";
			
		
		}
		
		void hashRetrieval(int index, string fn, string ln, ofstream & output){
			output << "\n***Peforming hashRetrieval on " << fn << ", " << ln << endl;
			listNode *s = findSpot(index, fn, ln);
			
			if(s->next != NULL && (s->next->firstName == fn && s->next->lastName == ln) )
				output << "Yes! The record is already in the database!\n\n";
			
			else
				output << "No! The record not in the database!\n\n";
			
		
		}
	
		
		void printList(int index, ofstream & output){
			listNode* temp = hashTable[index];
			string finalFirstName = temp->firstName;
			string finalLastName = temp->lastName;
			
			output <<  "HashTable[" << index << "]: ";
			
			if(temp->next == NULL) output << "(First: " <<finalFirstName << ", Last: " << finalLastName << ", Next: NULL)-->" << "NULL\n\n";
			
			else{
				
				while(temp->next != NULL){
					temp->printNode(temp, output);
					
					finalFirstName = temp->next->firstName;
					finalLastName = temp->next->lastName;				
					
					temp = temp->next; 
						
				}
				output << "(" << finalFirstName <<", " << finalLastName <<  ", NULL)-->" << "NULL\n\n";
			}
		}
		
		void printHashTable(ofstream & output){
			cout << "Bucket size is " << bucketSize << endl;
			for(int i = 0; i < bucketSize - 1; i++)
				printList(i, output);
				
			cout << "Finished printing Hash Table\n";
		}
	
};


int main(int argc, char *argv[]){
	
	string inFile = argv[1];
	ifstream input;
	input.open(inFile);
	
	int bucketSize = stoi(argv[2]);
	
	string outFile1 = argv[3];	
	ofstream output1 ;
	output1.open(outFile1);
	
	string outFile2 = argv[4];		
	ofstream output2 ;
	output2.open(outFile2);
	
	hashTable *ht1 = new hashTable();
	ht1->createHashTable(bucketSize);
	ht1->informationProcessing(input, output2);
	ht1->printHashTable(output1);

	input.close();
	output1.close();
	output2.close();

	return 0;
	
}
