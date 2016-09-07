package com.datumbox.opensource.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.datumbox.opensource.ai.AIsolver.Player;
import com.datumbox.opensource.dataobjects.Direction;
import com.datumbox.opensource.game.Board;

public class NewSolver {
	private static final  Random r = new Random(System.currentTimeMillis());

	public enum Player {
        COMPUTER, 
        USER
    }
	public static Direction findBestMove(Board theBoard, int depth) throws CloneNotSupportedException {
		int bestScore= Integer.MIN_VALUE;
		Direction bestDirection = null;
		
        for(Direction direction : Direction.values()) {
        	  Board newBoard = (Board) theBoard.clone();
        	  int points=newBoard.move(direction);
              
              if(points==0 && theBoard.isEqual(newBoard.getBoardArray(), theBoard.getBoardArray())) {
              	  continue;
              }
              
              int result=3*mcSearch(newBoard,5,1)+mcSearch(newBoard,1,0);
              if(result>bestScore){
            	  bestScore=result;
            	  bestDirection=direction;
              }
              
        }
        return bestDirection;
    }
	
	private static int mcSearch(Board theBoard, int depth, int mode) throws CloneNotSupportedException{
		
		if(depth==0){
			return hScore(theBoard);
		}
		
		List<Integer> allmoves = theBoard.getEmptyCellIds();
		List<Integer> moves = new ArrayList<Integer>();
		
		if(mode==0){
			moves.addAll(allmoves);
		}else{
			int movenum = allmoves.size();
	    	
	        for (int k = 0; k< 3;k++){
	        	int rnum = r.nextInt(movenum);
	        	int rvalue=allmoves.get(rnum);
	        	if(moves.contains(rvalue)){
	        		continue;
	        	}
	        	moves.add(rvalue);
	        }
	    
		}
		
        
        
        int i,j;
        int score=0;
        for(int cellId: moves){
        	i = cellId/Board.BOARD_SIZE;
            j = cellId%Board.BOARD_SIZE;
        	int value=(r.nextDouble()< 0.9)?2:4;
        	Board newBoard = (Board) theBoard.clone();
            newBoard.setEmptyCell(i, j, value);
            
            int bestScore= Integer.MIN_VALUE;
            for(Direction direction : Direction.values()) {
            	  Board newnewBoard = (Board) newBoard.clone();
            	  int points=newnewBoard.move(direction);
                  
                  if(points==0 && newBoard.isEqual(newnewBoard.getBoardArray(), newBoard.getBoardArray())) {
                  	  continue;
                  }
                  
                  int result=mcSearch(newnewBoard,depth-1,mode);
                  if(result>bestScore){
                	  bestScore=result;
                  }
                  
            }
            
            score+=bestScore;
          
        }
        score=score/moves.size();
        
        return score;
	}
    
	private static int hScore(Board theBoard){
		int boardScore = theBoard.getScore();
		int cs = cornerstone(theBoard.getBoardArray());
		int ec = 10*theBoard.getNumberOfEmptyCells();
		int mo = monot(theBoard.getBoardArray());
		
		
		return (boardScore+cs+ec+mo)/4;
	}
	
	private static int cornerstone(int[][] boardArray){
		return boardArray[0][0]*7+boardArray[0][1]*5+boardArray[0][2]*3+boardArray[0][3]*1;
	}
	
	private static int monot(int[][] boardArray){
		int s=0;
		int ct=0;
		for(int i=0;i<4;i++){
			if(boardArray[i][0]>0 && boardArray[i][2]>0){
				ct++;
			
				s+=cosign(boardArray[i][0]-boardArray[i][1],boardArray[i][1]-boardArray[i][2]);
			}
			if(boardArray[i][1]>0 && boardArray[i][3]>0){
				ct++;
				s+=cosign(boardArray[i][1]-boardArray[i][2],boardArray[i][2]-boardArray[i][3]);
				
			}
			if(boardArray[0][i]>0 && boardArray[2][i]>0){
				
				ct++;
				s+=cosign(boardArray[0][i]-boardArray[1][i],boardArray[1][i]-boardArray[2][i]);
				
			}
			if(boardArray[1][i]>0 && boardArray[3][i]>0){
				
				ct++;
				s+=cosign(boardArray[1][i]-boardArray[2][i],boardArray[2][i]-boardArray[3][i]);
				
			
			
		    }
	}
		return  s*1000/Math.max(ct,1);
	}
	private static int cosign(int n1,int n2){
		if (n1*n2>=0){
			return 1;
		}else{
			return 0;
		}
	}
}
