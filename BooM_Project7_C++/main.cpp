#include <iostream>
#include <fstream>
#include <string>
using namespace std ;
// Assignment 7: Prim's Algorithm 
// Author: Michael Saulon Boodoosingh
// Date: 04/28/2021


class uEdge{
	private:
		int Nj;
		int Ni;
		int cost;
		uEdge* next;
		
		
	public:
		
		uEdge(int i, int j, int c, uEdge* n){
			Nj = j;
			Ni = i;
			cost = c;
			next = n;
					
		}
		
		
		int getNj(){
			return Nj;
		}
		
		int getNi(){
			return Ni;
		}
		
		void printEdge(ofstream & outFile){	
			outFile << "\n\nNi: " << Ni
			<< " Nj: " << Nj
			<< " Cost: " << cost << endl;
		}
		
	friend class primMST;
};

class primMST{
	private:
		int numNodes;
		int nodeInSetA;
		
		uEdge * edgelistHead;
		uEdge * MSTlistHead;
		
		int totalMSTCost;
		
	public:	
		int* whichSet;
		
		int getCost(){
			return totalMSTCost;
		}
		
		primMST(int num){
			numNodes = num;
			
			whichSet = new int[numNodes + 1];
			
			uEdge *dummy = new uEdge(0,0,0, NULL);
			
			edgelistHead = dummy;
			MSTlistHead = dummy;
			
			totalMSTCost = 0;
		}
		
		void listInsert(uEdge* e){
			int tempCost = e->cost;
			uEdge* temp = edgelistHead;
			
			while(temp->next!= NULL && temp->next->cost < tempCost){
				temp = temp->next;
			}
					
			e->next = temp->next;
			temp->next = e;
			
		}
		
		uEdge* removeEdge(){
			if(edgelistHead->next == NULL){
				return NULL;
				
			}
			
			uEdge *temp = edgelistHead;
			uEdge * currentNext = temp->next;
			
			while(currentNext->next != NULL && 
			( !(whichSet[currentNext->Ni] != whichSet[currentNext->Nj])  ||
			!(whichSet[currentNext->Ni] == 1 || whichSet[currentNext->Nj] == 1) )){
				
				temp = temp->next;
				currentNext = currentNext->next; 
				//cout << currentNext->Ni << " " << currentNext->Nj << endl;
	
			}
			
			temp->next = temp->next->next;	
				
			return currentNext;
		}
		
		void addEdge (uEdge* e){
			e->next = MSTlistHead->next;
			MSTlistHead->next = e;
		}
		
		void printSet(ofstream & debugFile){
			debugFile << "-----------------------------------------" << endl;
			for(int i = 1; i < numNodes + 1; i++)
				debugFile << whichSet[i] << " ";
			
			debugFile << "\n-----------------------------------------" << endl;
			
		}
		
		void printEdgeList(ofstream & outFile){
			outFile << "edgeListHead --> ";
			
			uEdge* temp = edgelistHead;

			while(temp->next != NULL){
				outFile << " <" << temp->Ni << ", " << (temp->Nj) << ", " << (temp->cost)// << ", " << temp->next 
				<< "> -->";  
				
				temp = temp->next; 
					
			} 
			
			outFile << " <" << (temp->Ni)
					<< ", " << (temp->Nj) << ", "
					<< (temp->cost) << ", NULL>\n";
						
			
		}
		
		void printMSTList(ofstream & outFile){
			outFile << "MSTlistHead --> ";
			uEdge* temp = MSTlistHead;

			while(temp->next != NULL){
				outFile << " <" << temp->Ni << ", " << (temp->Nj) << ", " << (temp->cost) << "> -->";  
				
				temp = temp->next; 
					
			} 
			
			outFile << " <" << (temp->Ni)
					<< ", " << (temp->Nj) << ", "
					<< (temp->cost) << ", NULL>\n";

		}
		
		
		bool setBisEmpty(){
			for(int i = 1; i < numNodes + 1; i++)
				if(whichSet[i] != 1) return false;
			
			return true;
		}
		
		void updateMST(uEdge *newEdge){
			addEdge(newEdge);
			
			totalMSTCost += newEdge->cost;
	
			
			if(whichSet[newEdge->Ni] == 1)
				whichSet[newEdge->Nj] = 1;
				
			else
				whichSet[newEdge->Ni] = 1;
			
		}
		
};



int main(int argc, char *argv[]){
	string inFile = argv[1];		
	ifstream input;
	input.open(inFile);

	int nodeInSetA = stoi(argv[2]);
		
	string MSTfile = argv[3];
	ofstream output;
	output.open(MSTfile);
	
	string debugFile = argv[4];
	ofstream output2;
	output2.open(debugFile);
	
	int numNodes;
	string currentLine;
	
	input >> currentLine;
	
	numNodes = stoi(currentLine);
	//cout << numNodes << endl;
	
	int * whichS = new int [numNodes + 1];
	for(int i = 0; i < numNodes + 1; i++){
		whichS[i] = 2;
	}
	
	whichS[nodeInSetA] = 1;
	
	primMST *p = new primMST(numNodes);
	p->whichSet = whichS;
	
	int position = 0;
	int currentI, currentJ, currentCost;
	uEdge *newEdge = new uEdge(currentI, currentJ, currentCost, NULL);
	
	while(!input.eof()){				//1
		input >> currentLine;
			
		switch(position){
			case(0):
				currentI = stoi(currentLine);
			case(1):
				currentJ = stoi(currentLine);
			case(2):
				currentCost = stoi(currentLine);
		}
		
		if(position == 2){
			newEdge = new uEdge(currentI, currentJ, currentCost, NULL);
			p->listInsert(newEdge);
			position = position % 2;
			
			cout << currentI << " " << currentJ << " " << currentCost << endl;
			
			
			
		}
		else
			position++;
	
	}
	
	p->printEdgeList(output2);
	
	
	while(!p->setBisEmpty()) {		
		uEdge * nextEdge  = p->removeEdge();   //4
		
		nextEdge->printEdge(output2);		   //5 

		
		p->updateMST(nextEdge);				   //6
		
		p->printSet(output2);				   //7
		
		p->printEdgeList(output2);		   //8
		p->printMSTList(output2);
		
	}
	
	
	
	output << "Num of nodes: " << numNodes << endl;
	output << "Prim’s MST of the input graph, G is: "  << endl;
	p->printMSTList(output);
	output << "MST total cost = " << p->getCost() << endl;
	
	input.close();
	
	output.close();
	output2.close();

	return 0;

}
