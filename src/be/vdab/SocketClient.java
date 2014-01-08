package be.vdab;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketClient {
	// private Board board;
	private Socket socket;
	private PrintWriter out;
	private Scanner in;
	private boolean change = false;
	private int fromServer;
	private int turn;
	private char[][] pieces = new char[6][7];

	public SocketClient() {
	}

	public void createSocket(String hostName, int portNumber) {
		try {
			socket = new Socket(hostName, portNumber);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());

			// board = new Board();
			initPlayField();
			listen();
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
	
	public void listen(){
		Thread listenT = new Thread(){
			public void run(){
				while(true) {
					//System.out.print("*");
					if((fromServer = in.nextInt()) != 0) {
						turn = fromServer%10;
						arrangeBoard(fromServer);
						draw();
						change = true;
					}
				}
			}
		};
		listenT.start();
	}

	public void play() {
		while(true) {
			System.out.println("test");
			int fromUser = 0;
			
			//draw();
			System.out.print(fromServer + "  /  " + turn);
			Scanner scanner = new Scanner(System.in);
			//if(fromServer%10 == 0){
				System.out.print("Drop piece in line: ");
				while (!scanner.hasNextInt()) {
					System.out.print("Drop piece in line: (give a number) ");
					scanner.next();
				}
				fromUser = scanner.nextInt();
				out.println(fromUser*10 + turn);
//			} else {
//				System.out.print("Wait for other player...");
//				scanner.next();
//			}
			
			if(change){
				change = false;
			}
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
//		Thread startDraw = new Thread() {
//			public void run() {
					clearScreen();
	
					int row = 5;
					System.out.println("|1|2|3|4|5|6|7|");
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					drawRow(row--);
					System.out.println("---------------");
					
//			}
//		};
//		startDraw.start();
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
				pieces[row][col] = player == 1 ? 'O' : 'X';
				placed = true;
			}
		}
	}

	public static void main(String[] args) {
		// String hostNameArg = args[0];
		// int portNumberArg = Integer.parseInt(args[1]);
		String hostNameArg = "192.168.0.163";
		int portNumberArg = 8082;
		SocketClient socketClient = new SocketClient();
		socketClient.createSocket(hostNameArg, portNumberArg);
	}

}
