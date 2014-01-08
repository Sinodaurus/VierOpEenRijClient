package be.vdab;

import java.io.Serializable;

public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	private char[][] pieces = new char[6][7];
	
	public void init(){
		for(int row = 0; row < 6; row++){
			for(int col = 0; col < 7; col++){
				pieces[row][col] = ' ';
			}
		}
	}
	
	public void addPiece(int col, int player){
		int row = 0;
		boolean placed = false;
		while(!placed) {
			if(pieces[row][col] != ' '){
				row++;
			} else {
				pieces[row][col] = player == 1 ? 'O' : 'X';
				placed = true;
			}
		}
	}
	
	public char[][] getBoard(){
		return pieces;
	}
}
