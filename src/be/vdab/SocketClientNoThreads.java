package be.vdab;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClientNoThreads {
	private Socket socket;
	private PrintWriter out;
	private Scanner in;
	private boolean myTurn = true;
	private boolean win = false;
	private boolean lose = true;
	private int fromServer;
	private int player = 1;
	private char[][] pieces;
	
	public boolean isWin(){
		return win;
	}
	public boolean isLose(){
		return lose;
	}

	public SocketClientNoThreads(char[][] pieces) {
		this.pieces = pieces;
	}

	public void createSocket(String hostName, int portNumber) {
		try {
			socket = new Socket(hostName, portNumber);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
			

			initPlayField();
			play();
			

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			System.exit(1);
		}
	}

	public void play() {
		while(true) {
			if((fromServer = in.nextInt()) != 0) {
				if(fromServer != 1) {
					arrangeBoard(fromServer);
					if(win){
						draw();
						if(lose) {
							System.out.println("YOU LOSE!");
						} else {
							System.out.println("YOU WIN!");
						}
						break;
					}
				}
				draw();
				dropPiece();
				
			} else {
				player = 2;
				draw();
				System.out.print("Wait for other player...(first turn)");
				myTurn = true;
			}
		}
	}
	
	public void dropPiece(){
		int fromUser = 0;
		
		Scanner scanner = new Scanner(System.in);
		System.out.print(fromServer + ":  ");
		
		if(myTurn){
			System.out.print("Drop piece in line: ");
			while (!scanner.hasNextInt()) {
				System.out.print("Drop piece in line: (give a number) ");
				scanner.next();
			}
			fromUser = scanner.nextInt();
			out.println(fromUser*10);
			myTurn = false;
		} else {
			System.out.print("Wait for other player...");
			myTurn = true;
		}
	}

	public void clearScreen() {
		for (int i = 0; i < 15; i++) {
			System.out.println("\n");
		}
	}

	public void initPlayField() {
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				pieces[row][col] = ' ';
			}
		}
	}

	public void draw() {
		clearScreen();

		System.out.println("|1|2|3|4|5|6|7|");
		for(int i = 5; i >= 0; i--) {
			System.out.println("---------------");
			drawRow(i);
		}
		System.out.println("---------------\n");
		System.out.println("Player: " + player);
	}

	public void drawRow(int row) {
		for (int col = 0; col < 6; col++) {
			System.out.print("|" + pieces[row][col]);
		}
		System.out.print("|" + pieces[row][6] + "|");
		System.out.println();
	}

	public void arrangeBoard(int fromServer) {
		int col = ((fromServer % 100)/10) -1;
		int player = fromServer / 100;
		int row = 0;
		boolean placed = false;
		while (!placed) {
			if (pieces[row][col] != ' ') {
				row++;
			} else {
				pieces[row][col] = (player % 2) == 1 ? 'O' : 'X';
				placed = true;
			}
		}
		checkIfWin();
	}
	
	public void checkIfWin() {
		int winNum = 4;
		int foundPieces = 1;
		int row = 0;
		int col = 0;
		int whatPlayer = 0;
		boolean found = false;
		boolean stop = false;
		while(!stop) {
			
			//look for a piece to start-----------------------------
			
			while(!found){
				if(pieces[row][col] != ' '){
					if(pieces[row][col] == 'O') {
						whatPlayer = 1;
					} else {
						whatPlayer = 2;
					}
					found = true;
					break;
				} else if(row < 5){
					row++;
				}
				if(row == 5 && col != 6){
					row = 0;
					col++; 
				}
				if(row == 5 && col == 6) {
					stop = true;
					break;
				}
			}
			
			//check the next pieces to match 4 in a row--------------------
			
			int nextCol;
			int nextRow;
			
			while(found){
				boolean deadEnd = false;
				
				//check horizontally----------------------------------------
				
				if((col < 6) && (pieces[row][col + 1] == pieces[row][col])) {
					foundPieces++;
					nextCol = col + 1;
					while(!deadEnd) {
						if((nextCol < 6) && (pieces[row][nextCol + 1] == pieces[row][nextCol])) {
							nextCol++;
							foundPieces++;
						} else {
							foundPieces = 1;
							found = false;
							row++;
							break;
						}
						if(foundPieces == winNum) {
							if((whatPlayer == 1) && (player == 1)){lose = false;}
							if((whatPlayer == 2) && (player == 2)){lose = false;}
							found = false;
							stop = true;
							win = true;
							break;
						}
					}
					
					//check vertically---------------------------------------
					
				} else if((row < 5) && (pieces[row + 1][col] == pieces[row][col])){
					foundPieces++;
					nextRow = row + 1;
					while(!deadEnd){
						if((nextRow < 5) && (pieces[nextRow + 1][col] == pieces[nextRow][col])){
							foundPieces++;
							nextRow++;
						} else {
							foundPieces = 1;
							found = false;
							if(row < 5) {
								row++;
							} else {
								row = 0;
								col++;
							}
							break;
						}
						if(foundPieces == winNum) {
							if((whatPlayer == 1) && (player == 1)){lose = false;}
							if((whatPlayer == 2) && (player == 2)){lose = false;}
							found = false;
							stop = true;
							win = true;
							break;
						}
					}
					
					//check diagonally UP----------------------------------
					
				} else if(((row < 5) &&(col < 6)) && (pieces[row + 1][col + 1] == pieces[row][col])) {
					foundPieces++;
					nextRow = row + 1;
					nextCol = col + 1;
					while(!deadEnd){
						if(((nextRow < 5) &&(nextCol < 6)) && (pieces[nextRow + 1][nextCol + 1] == pieces[row][col])){
							foundPieces++;
							nextRow++;
							nextCol++;
						} else {
							foundPieces = 1;
							found = false;
							if(row < 5) {
								row++;
							} else {
								row = 0;
								col++;
							}
							break;
						}
						if(foundPieces == winNum) {
							if((whatPlayer == 1) && (player == 1)){lose = false;}
							if((whatPlayer == 2) && (player == 2)){lose = false;}
							found = false;
							stop = true;
							win = true;
							break;
						}
					}	
				
					//check diagonally DOWN----------------------------------
				
				} else if(((row > 0) &&(col < 6)) && (pieces[row - 1][col + 1] == pieces[row][col])) {
					foundPieces++;
					nextRow = row - 1;
					nextCol = col + 1;
					while(!deadEnd){
						if(((nextRow > 0) &&(nextCol < 6)) && (pieces[nextRow - 1][nextCol + 1] == pieces[row][col])){
							foundPieces++;
							nextRow--;
							nextCol++;
						} else {
							foundPieces = 1;
							found = false;
							if(row < 5) {
								row++;
							} else {
								row = 0;
								col++;
							}
							break;
						}
						if(foundPieces == winNum) {
							if((whatPlayer == 1) && (player == 1)){lose = false;}
							if((whatPlayer == 2) && (player == 2)){lose = false;}
							found = false;
							stop = true;
							win = true;
							break;
						}
					}
				} else {
						found = false;
						if(row < 5) {
							row++;
						} else {
							row = 0;
							col++;
						}
						break;
					}
			}
		}
	}

	public static void main(String[] args) {
		boolean newGame = true;
		//String hostNameArg = args[0];
		//int portNumberArg = Integer.parseInt(args[1]);
		String hostNameArg = "172.16.111.115";
		int portNumberArg = 8082;
		while(newGame) {
			char[][] pieces = new char[6][7];
			SocketClientNoThreads socketClient = new SocketClientNoThreads(pieces);
			socketClient.createSocket(hostNameArg, portNumberArg);
			Scanner scanner = new Scanner(System.in);
			newGame = false;
			System.out.print("New game? Y or N ...");
			if(scanner.next().equalsIgnoreCase("y")){
				newGame = true;
			}
		}
	}

}
