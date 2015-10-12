import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

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
	long peerActiveTimer;
	int messageTimer;
	Random timeGenerator;

	Communication peerBroadcast;

	public P2PTClient(String unikey) throws IOException {

		peerDiscoveryTimer = 5000;
		peerActiveTimer = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		messageTimer = 0;
		timeGenerator = new Random();

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
			packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, port);

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
		System.out.print("Status: ");
		try {
			while (true) {

				peerActiveTimer = System.currentTimeMillis();

				if (bufferedReader.ready()) {

					String data = bufferedReader.readLine();
					
					String processedData = data.replace(":", "\\:");
					
					

					if (data.equalsIgnoreCase("")) {
						System.out.println("Status is empty. Retry.");
					} else if (data.length() > 140) {
						System.out.println("Status is too long, 140 characters max. Retry.");
					} else {
						tweet = unikey + ":" + processedData;
						sendTweet(tweet);

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						printTweets();
					}
					
				}
				else {
					messageTimer =  timeGenerator.nextInt((3000 - 1000) + 1) + 1000;
					try {
						Thread.sleep(messageTimer);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					printTweets();
				}
				System.out.println("Status:");
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void sendTweet(String tweet) throws IOException {

		try {
			sendBuffer = tweet.getBytes("ISO-8859-1");

			packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, port);
			socket.send(packet);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void printTweets() {
		System.out.println("### P2P Tweets ###");

		for (int i = 0; i < Profile.unikeys.size(); i++) {
			if (!Profile.currentTweets.get(i).equalsIgnoreCase("-1")) {
				if (!Profile.unikeys.get(i).equalsIgnoreCase(unikey)) {
					if (peerActiveTimer - Profile.lastActive.get(i) < 10) {
						System.out.println(
								"# " + Profile.unikeys.get(i) + " : " + Profile.currentTweets.get(i));
					} else if (peerActiveTimer - Profile.lastActive.get(i) < 20) {
						System.out.println("# [" + Profile.unikeys.get(i) + " : " + "idle]");
					} else {
						continue;
					}
				} else {
					System.out.println(
							"# " + Profile.unikeys.get(i) + " : " + Profile.currentTweets.get(i));
				}
			} else {
				System.out.println("# [" + Profile.unikeys.get(i) + " : " + "not yet initialized]");
			}
		}

		System.out.println("### End Tweets ###");
	}

}
