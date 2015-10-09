import java.io.IOException;


public class P2PTwitter {

	public static void main(String args[]) throws IOException {
		Thread server = new Thread(new P2PTServer(args[0]));
		Thread client = new Thread(new P2PTClient(args[0]));
		Profile.load();
		server.start();
		client.start();
		
	}
}
