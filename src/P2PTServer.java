import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class P2PTServer implements Runnable {

	DatagramSocket socket;
	DatagramPacket packet;
	byte[] buffer;
	InetAddress address;
	String key;
	String unikey;

	public P2PTServer(String unikey) throws IOException {
		try {
			this.unikey = unikey;
			socket = new DatagramSocket(7014);
			buffer = new byte[256];
			packet = new DatagramPacket(buffer, buffer.length);

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {
			try {
				
				
				/*
				 * Receive a datagram packet, retrieve the string contained within the packet, 
				 * split it using the first colon as an anchor point, decode any following colons in the message and 
				 * update the current tweet and active time of the sender
				 */
				
				socket.receive(packet);

				key = new String(buffer, 0, packet.getLength());

				String[] message = key.split(":", 2);

				String unikey = message[0];
				String tweet = message[1];

				tweet = tweet.replace("\\:", ":");

				for (int i = 0; i < Profile.unikeys.size(); i++) {
					if (Profile.unikeys.get(i).equalsIgnoreCase(unikey)) {
						Profile.currentTweets.set(i, tweet);
						Profile.lastActive.set(i, System.currentTimeMillis());
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} 
		}

	}

}
