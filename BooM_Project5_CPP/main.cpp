#include <iostream>
#include <fstream>
#include <string>
using namespace std ;
// Assignment 5: Quad Tree 
// Author: Michael Saulon Boodoosingh
// Date: 03/30/2021


class image{
	private:
		int numRows;
		int numCols;
		int minVal;
		int maxVal;
		
		
	public:
		int **imgAry;
		int power2Size;
		
		image(int r, int c, int min, int max){
			numRows = r;
			numCols = c;
			minVal = min;
			maxVal = max;
			
			power2Size =  computePower2();
			
		}
		
		int computePower2(){
			int size = max(numRows, numCols);
			int power2 = 2;
			
			while(size > power2)
				power2 *= 2; 	
			
			return power2;
		
		}
		
		void loadImage(ifstream & inFile, int** imgAry, ofstream & outFile2){	//load the input data onto imgAry, begins at (0,0)
			outFile2 <<"Original Size: " << numCols << "(cols) x " << numRows << "(rows).  Converted to: " << power2Size << " x " << power2Size << endl;
			int line;
			int rowPos = 0;
			int colPos = 0;
			
			string nextLine;
			getline(inFile, nextLine);		//Used to skip the first line in the img file (The line with the row, col, minVal, and maxVal)
			
			while(inFile){		
				inFile >> line;
		
				imgAry[rowPos][colPos] = line;
								
				colPos++;
						
				if(colPos >= numCols){
					colPos = 0;
					rowPos++;
				}						
						
			}
			
			for(int i = 0; i < power2Size; i++){
				for (int j = 0 ; j < power2Size; j++){
					outFile2 << imgAry[i][j] << " ";
				}
					outFile2 << endl;
			}

		}
		
		void zero2DAry(){
			imgAry = new int* [power2Size] {};				
			
			for(int i = 0; i < power2Size; i++)			//Initialize every possible spot in the array to be 0 (Start with the rows then the columns)
				imgAry[i] = new int[power2Size] {} ;
			
		}
	
};

class qtTreeNode{
	private:
		int color;
		int upperR;
		int upperC;
		int size;
		
		qtTreeNode* NWkid = NULL;
		qtTreeNode* NEkid = NULL;
		qtTreeNode* SWkid = NULL;
		qtTreeNode* SEkid = NULL;
		
		
	public:
		qtTreeNode(int ur, int uc, int s, qtTreeNode* nw, qtTreeNode* ne, qtTreeNode* sw, qtTreeNode* se){
			upperR = ur;
			upperC = uc;
			size = s;
			
			NWkid = nw;
			NEkid = ne;
			SWkid = sw;
			SEkid = se;
			
		}
		
		void printQtNode(ofstream & write){
			write << endl;
			if(NWkid == NULL && NEkid == NULL && SWkid == NULL && SEkid == NULL){
				write << "Color: " << color << " Upper R: " << upperR << " Upper C: " << upperC << ", NWkid color: NULL" 
				<<  ", NEkid color: NULL" << ", SWkid color: NULL" << ", SEkid color: NULL" << endl;
			}
			
			else{
				write << "Color: " << color << " Upper R: " << upperR << " Upper C: " << upperC << ", NWkid color: " 
				<< NWkid->color << ", NEkid color: " << NEkid->color << ", SWkid color: " 
				<< SWkid->color << ", SEkid color: " << SEkid->color << endl;
			}

			// Note that not every node will be a leaf so in that case, just print the color, upper row pos, and upper col pos
			// output the given node’s: color, upperR, upperC, NWkid’s color, NEkid’s color, SWkid’s color, SEkid’s color),
			
		}
		
	friend class quadTree;
};

class quadTree{
	private:
		qtTreeNode* qtRoot;
		
	public:
		qtTreeNode* buildQuadTree(int** imgAry, int upR, int upC, int size, ofstream & output){
			qtTreeNode* newQtNode = new qtTreeNode(upR, upC, size, NULL, NULL, NULL, NULL);
			newQtNode->color = -1;
			//Printing the new node here would cause an issue where the color of the current new node would always be -1 (i.e. the color would not be properly updated 
			

			if (size == 1) // one pixel
				newQtNode->color = imgAry[upR][upC]; // either 1 or 0		

			
			else{
			
				int halfSize = size / 2 ;
				
				newQtNode-> NWkid = buildQuadTree (imgAry, upR, upC, halfSize,output);
				newQtNode-> NEkid = buildQuadTree (imgAry, upR, upC + halfSize, halfSize, output);
				newQtNode-> SWkid = buildQuadTree (imgAry, upR + halfSize, upC, halfSize, output);
				newQtNode-> SEkid = buildQuadTree (imgAry, upR + halfSize, upC + halfSize, halfSize, output);
				
				int sumColor = 	  newQtNode->NWkid->color 
									+ newQtNode->NEkid->color
									+ newQtNode->SWkid->color 
									+ newQtNode->SEkid->color;
					
				if(sumColor == 0){ 
					//all 4 kids are 0
					newQtNode->color = 0;
					kidsToNull(newQtNode);				
					// newQtNode is now a leaf node
				}
					
				else if (sumColor == 4){ 
					//all 4 kids are 1
					newQtNode->color = 1;
					kidsToNull(newQtNode);
					// newQtNode is now a leaf node
				}
					
				else
					newQtNode->color = 5;
		
			}
				
				
				newQtNode->printQtNode (output);
				qtRoot = newQtNode;
				return newQtNode;
		}
		
		bool isLeaf(qtTreeNode* q){
			return( q->NWkid == NULL && q->NEkid == NULL && q->SWkid == NULL && q->SEkid == NULL && ( q->color == 0 || q->color == 1 ) ) ;
			// All leaf nodes in a quad tree have no kids and their color is either black or white (not gray)
		}
		
		void kidsToNull(qtTreeNode *q){
			q->NWkid = NULL;
			q->NEkid = NULL;
			q->SWkid = NULL;
			q->SEkid = NULL;
		}
		
		void preOrder(qtTreeNode* q, ofstream & outFile){
			if(isLeaf(q))
				q->printQtNode(outFile);
				
			else{
				q->printQtNode(outFile);
				preOrder(q->NWkid, outFile);
				preOrder(q->NEkid, outFile);
				preOrder(q->SWkid, outFile);
				preOrder(q->SEkid, outFile);
			}
		}
		
		void postOrder(qtTreeNode* q, ofstream & outFile){
			if(isLeaf(q))
				q->printQtNode(outFile);
				
			else{	
				postOrder(q->NWkid, outFile);
				postOrder(q->NEkid, outFile);
				postOrder(q->SWkid, outFile);
				postOrder(q->SEkid, outFile);
				q->printQtNode(outFile);
			}
		}
};

int main(int argc, char *argv[]){
	int row, col , minVal, maxVal; 
	int power2size;
	
	string inFile = argv[1];		//img1.txt
	ifstream input ;
	input.open(inFile);

	string outFile1 = argv[2];		//out1.txt
	ofstream output1 ;
	output1.open(outFile1);

	string outFile2 = argv[3];	//out2.txt
	ofstream output2 ;
	output2.open(outFile2);
	
	string l;
	int pos = 0;
	while(!input.eof()){			// Assign row, col. minVal, and maxVal by reading the first 4 numbers on the file
		if(pos >= 4) break;
		input >> l;
		switch(pos){
			case(0):
				row =  stoi(l);
			case(1):
				col = stoi(l);
			case(2):
				minVal = stoi(l);
			case(3):
				maxVal = stoi(l);
			default:
				break;
		}
		pos++;
	}
	
	input.close();
	input.open(inFile);
	
	image *img = new image(row, col, minVal, maxVal);
	int power2Size = img->power2Size;
	
	img->zero2DAry();

	img->loadImage(input, img->imgAry, output2);

	quadTree *qt = new quadTree();
	
	qtTreeNode *qtRoot = qt->buildQuadTree(img->imgAry, 0, 0, power2Size, output2);
	
	output1 << "***********Beginning preOrder***********\n";
	qt->preOrder(qtRoot, output1);
	output1 << "\n***********preOrder complete***********\n";
	
	output1 << "\n***********Beginning postOrder***********\n";
	qt->postOrder(qtRoot, output1);
	output1 << "\n***********postOrder complete***********\n";

	cout << "DONE";
	input.close();
	output1.close();
	output2.close();

	

	return 0;

}
