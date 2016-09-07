/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox.opensource.ai;

import com.datumbox.opensource.dataobjects.Direction;
import com.datumbox.opensource.game.Board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The AIsolver class that uses Artificial Intelligence to estimate the next move.
 * 
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class AIsolver {
    
	private final static Random randomGenerator = new Random(System.currentTimeMillis());
	
    /**
     * Player vs Computer enum class
     */
    public enum Player {
        /**
         * Computer
         */
        COMPUTER, 

        /**
         * User
         */
        USER
    }
    
    /**
     * Method that finds the best next move.
     * 
     * @param theBoard
     * @param depth
     * @return
     * @throws CloneNotSupportedException 
     */
    public static Direction findBestMove(Board theBoard, int depth) throws CloneNotSupportedException {
        //Map<String, Object> result = minimax(theBoard, depth, Player.USER);
        
        Map<String, Object> result = alphabeta(theBoard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, Player.USER);
        
        return (Direction)result.get("Direction");
    }
    
    /**
     * Finds the best move by using the Minimax algorithm.
     * 
     * @param theBoard
     * @param depth
     * @param player
     * @return
     * @throws CloneNotSupportedException 
     */
    private static Map<String, Object> minimax(Board theBoard, int depth, Player player) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        
        Direction bestDirection = null;
        int bestScore;
        
        if(depth==0 || theBoard.isGameTerminated()) {
        	bestScore=heuristicScore(theBoard.getScore(),theBoard.getNumberOfEmptyCells(),calculateClusteringScore(theBoard.getBoardArray()),monotonicity(theBoard.getBoardArray()));
        }
        else {
            if(player == Player.USER) {
                bestScore = Integer.MIN_VALUE;

                for(Direction direction : Direction.values()) {
                    Board newBoard = (Board) theBoard.clone();

                    int points=newBoard.move(direction);
                    
                    if(points==0 && newBoard.isEqual(theBoard.getBoardArray(), newBoard.getBoardArray())) {
                    	continue;
                    }

                    Map<String, Object> currentResult = minimax(newBoard, depth-1, Player.COMPUTER);
                    int currentScore=((Number)currentResult.get("Score")).intValue();
                    if(currentScore>bestScore) { //maximize score
                        bestScore=currentScore;
                        bestDirection=direction;
                    }
                }
            }
            else {
                bestScore = Integer.MAX_VALUE;

                List<Integer> moves = theBoard.getEmptyCellIds();
                if(moves.isEmpty()) {
                    bestScore=0;
                }
                int[] possibleValues = {2, 4};

                int i,j;
                int[][] boardArray;
                for(Integer cellId : moves) {
                    i = cellId/Board.BOARD_SIZE;
                    j = cellId%Board.BOARD_SIZE;

                    for(int value : possibleValues) {
                        Board newBoard = (Board) theBoard.clone();
                        newBoard.setEmptyCell(i, j, value);

                        Map<String, Object> currentResult = minimax(newBoard, depth-1, Player.USER);
                        int currentScore=((Number)currentResult.get("Score")).intValue();
                        if(currentScore<bestScore) { //minimize best score
                            bestScore=currentScore;
                        }
                    }
                }
            }
        }
        
        result.put("Score", bestScore);
        result.put("Direction", bestDirection);
        
        return result;
    }
    
    /**
     * Finds the best move bay using the Alpha-Beta pruning algorithm.
     * 
     * @param theBoard
     * @param depth
     * @param alpha
     * @param beta
     * @param player
     * @return
     * @throws CloneNotSupportedException 
     */
    private static Map<String, Object> alphabeta(Board theBoard, int depth, int alpha, int beta, Player player) throws CloneNotSupportedException {
        Map<String, Object> result = new HashMap<>();
        
        Direction bestDirection = null;
        int bestScore;
        
        if(theBoard.isGameTerminated()) {
            if(theBoard.hasWon()) {
                bestScore=Integer.MAX_VALUE; //highest possible score
            }
            else {
                bestScore=Math.min(theBoard.getScore(), 1); //lowest possible score
            }
        }
        else if(depth==0) {
            bestScore=heuristicScore(theBoard.getScore(),theBoard.getNumberOfEmptyCells(),calculateClusteringScore(theBoard.getBoardArray()),monotonicity(theBoard.getBoardArray()));
        }
        else {
            if(player == Player.USER) {
                for(Direction direction : Direction.values()) {
                    Board newBoard = (Board) theBoard.clone();

                    int points=newBoard.move(direction);
                    
                    if(points==0 && newBoard.isEqual(theBoard.getBoardArray(), newBoard.getBoardArray())) {
                    	continue;
                    }
                    
                    Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.COMPUTER);
                    int currentScore=((Number)currentResult.get("Score")).intValue();
                                        
                    if(currentScore>alpha) { //maximize score
                        alpha=currentScore;
                        bestDirection=direction;
                    }
                    
                    if(beta<=alpha) {
                        break; //beta cutoff
                    }
                }
                
                bestScore = alpha;
            }
            else {
                List<Integer> allmoves = theBoard.getEmptyCellIds();
                //List<Integer> moves = new ArrayList<Integer>();
                //int movenum = allmoves.size();
                //moves.add(12);
            	/*
                for (int k = 0; k< movenum/2;k++){
                	int rnum = randomGenerator.nextInt(movenum);
                	if(moves.contains(rnum)){
                		continue;
                	}
                	moves.add(rnum);
                }
                */
                int i,j;
                abloop: for(Integer cellId : allmoves) {
                    i = cellId/Board.BOARD_SIZE;
                    j = cellId%Board.BOARD_SIZE;

                    //int value=(randomGenerator.nextDouble()< 0.9)?2:4;
                    for(int value=2;value<=4;value+=2){
                    	
                    
                        Board newBoard = (Board) theBoard.clone();
                        newBoard.setEmptyCell(i, j, value);

                        Map<String, Object> currentResult = alphabeta(newBoard, depth-1, alpha, beta, Player.USER);
                        int currentScore=((Number)currentResult.get("Score")).intValue();
                        if(currentScore<beta) { //minimize best score
                            beta=currentScore;
                        }
                        
                        if(beta<=alpha) {
                            break abloop; //alpha cutoff
                        }
                    }
                }
                
                bestScore = beta;
                
                if(allmoves.isEmpty()) {
                    bestScore=0;
                }
            }
        }
        
        result.put("Score", bestScore);
        result.put("Direction", bestDirection);
        
        return result;
    }
    
    /**
     * Estimates a heuristic score by taking into account the real score, the
     * number of empty cells and the clustering score of the board.
     * 
     * @param actualScore
     * @param numberOfEmptyCells
     * @param clusteringScore
     * @return 
     */
    private static int heuristicScore(int actualScore, int numberOfEmptyCells,int clusteringScore, int monotonicityScore) {
        //int score = (int) (actualScore+Math.log(actualScore)*numberOfEmptyCells -clusteringScore);
    	return (int) (monotonicityScore+actualScore/2);
    	//return (int) (actualScore+Math.log(actualScore)*numberOfEmptyCells + monotonicityScore);
    	//return Math.max(score, Math.min(actualScore, 1));
    }
    
    /**
     * Calculates a heuristic variance-like score that measures how clustered the
     * board is.
     * 
     * @param boardArray
     * @return 
     */
    private static int calculateClusteringScore(int[][] boardArray) {
        int clusteringScore=0;
        
        int[] neighbors = {-1,0,1};
        
        for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray.length;++j) {
                if(boardArray[i][j]==0) {
                    continue; //ignore empty cells
                }
                
                //clusteringScore-=boardArray[i][j];
                
                //for every pixel find the distance from each neightbors
                int numOfNeighbors=0;
                int sum=0;
                for(int k : neighbors) {
                    int x=i+k;
                    if(x<0 || x>=boardArray.length) {
                        continue;
                    }
                    for(int l : neighbors) {
                        int y = j+l;
                        if(y<0 || y>=boardArray.length) {
                            continue;
                        }
                        
                        if(boardArray[x][y]>0) {
                            ++numOfNeighbors;
                            sum+=Math.abs(boardArray[i][j]-boardArray[x][y]);
                        }
                        
                    }
                }
                
                clusteringScore+=sum/numOfNeighbors;
            }
        }
        
        return clusteringScore;
    }
    
    private static int  stability(int[][] boardArray){
    	//int[][] order = {{0,0,0,0},{0,0,0,0},{0,0,1,2},{6,5,4,3}}; //we assume that size=4
    	int[][] order = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{4,3,2,1}};
    	int sum=0;
    	for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray.length;++j) {
            	sum+=order[i][j]*boardArray[i][j];
            }
    	}
    	return sum;
    }

    private static int monotonicity(int[][] boardArray){
    	return 0;
    }
}
