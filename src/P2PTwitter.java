/*
 * 
 * @author Rhys Austin
 * @date 20/10/2015
 * 
 * 
 * P2PTwitter Program, send messages between participants using a peer to peer style setup
 * 
 */


import java.io.IOException;




public class P2PTwitter {

	public static void main(String args[]) throws IOException {
		//Load details from participants file
		Profile.load();
		
		Thread server = new Thread(new P2PTServer(args[0]));
		Thread client = new Thread(new P2PTClient(args[0]));
		server.start();
		client.start();
		
	}
}
