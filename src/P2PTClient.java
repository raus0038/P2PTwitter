import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class P2PTClient implements Runnable {

	int a;
	int port;
	String unikey;
	InetAddress address;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] sendBuffer = new byte[256];
	String tweet;
	InetAddress group;
	int peerDiscoveryTimer;
	long startTime;
	
	Communication peerBroadcast;

	public P2PTClient(String unikey) throws IOException {
		
		peerDiscoveryTimer = 5000;
		startTime = System.currentTimeMillis();
		
	
		
		try {
			try {
				group = InetAddress.getByName("224.0.0.255");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			this.unikey = unikey;
			address = InetAddress.getLocalHost();
			port = 7014;
			socket = new DatagramSocket();
			sendBuffer = this.unikey.getBytes();
			packet = new DatagramPacket(sendBuffer, sendBuffer.length, group,
					port);
			
			peerBroadcast = new Communication(unikey, peerDiscoveryTimer, startTime, socket, packet, port, group);

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);
		
		peerBroadcast.start();

		try {
			socket.send(packet);
			sendBuffer = new byte[256];
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendBuffer = new byte[256];

		try {
			while (true) {

				System.out.print("Status: ");

				String data = bufferedReader.readLine();

				if (data.equalsIgnoreCase("")) {
					System.out.println("Status is empty. Retry.");
				} else if (data.length() > 140) {
					System.out
							.println("Status is too long, 140 characters max. Retry.");
				} else {
					tweet = unikey + ":" + data;
					sendTweet(tweet);

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					System.out.println("### P2P Tweets ###");

					for (int i = 0; i < Profile.unikeys.size(); i++) {
						System.out.println("# " + Profile.unikeys.get(i)
								+ " : " + Profile.currentTweets.get(i));
					}

					System.out.println("### End Tweets ###");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendTweet(String tweet) throws IOException {

		try {
			sendBuffer = tweet.getBytes("ISO-8859-1");

			packet = new DatagramPacket(sendBuffer, sendBuffer.length, group,
					port);
			socket.send(packet);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}
