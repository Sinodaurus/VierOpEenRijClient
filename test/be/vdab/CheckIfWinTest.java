package be.vdab;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class CheckIfWinTest {
	private SocketClientNoThreads socketClient;
	
	@Before
	public void setUp(){
		char[][] pieces = {{' ',' ',' ',' ',' ',' ',' '},{' ',' ',' ',' ',' ',' ',' '},{'O',' ',' ',' ',' ',' ',' '}
		,{'O',' ',' ',' ',' ',' ',' '},{'O',' ',' ',' ',' ',' ',' '},{'O',' ',' ',' ',' ',' ',' '}};
		socketClient = new SocketClientNoThreads(pieces);
	}

	@Test
	public void test() {
		socketClient.checkIfWin();
		Assert.assertTrue(socketClient.isWin());
	}

}
