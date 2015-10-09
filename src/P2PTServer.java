import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class P2PTServer implements Runnable {

	MulticastSocket socket;
	DatagramPacket packet;
	byte[] buffer;
	InetAddress address;
	String key;
	String unikey;
	InetAddress group;

	public P2PTServer(String unikey) throws IOException {
		try {
			this.unikey = unikey;
			socket = new MulticastSocket(7014);
			buffer = new byte[256];
			packet = new DatagramPacket(buffer, buffer.length);
			try {
				group = InetAddress.getByName("224.0.0.255");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			socket.joinGroup(group);
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		while (true) {
			try {
				
			
				socket.receive(packet);
			
			
				key = new String(buffer, 0, packet.getLength());
				
				if(key.length() == 8) {
					Profile.addParticipant(key, packet.getAddress());
				}
				
				
				else if (key.length()  > 8) {
					String[] message = key.split(":");
					
					String unikey = message[0];
					String tweet = message[1];
					
					for(int i = 0; i < Profile.unikeys.size(); i++) {
						if(Profile.unikeys.get(i).equalsIgnoreCase(unikey)) {
							Profile.currentTweets.set(i, tweet);
						}
					}
					
					
				}

				
				
				
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
