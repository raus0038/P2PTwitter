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
	int messageTimer;
	long peerActiveTimer;
	String unikey;
	String tweet;
	InetAddress address;
	DatagramSocket socket = null;
	DatagramPacket packet;
	byte[] sendBuffer = new byte[256];
	Random timeGenerator;
	boolean inputEntered;
	String data;

	Communication peerBroadcast;

	public P2PTClient(String unikey) throws IOException {

		peerActiveTimer = System.currentTimeMillis();
		messageTimer = 0;
		timeGenerator = new Random();
		data = null;

		try {

			this.unikey = unikey;
			address = InetAddress.getLocalHost();
			port = 7014;
			socket = new DatagramSocket();
			sendBuffer = new byte[256];

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);
		messageTimer = timeGenerator.nextInt((3000 - 1000) + 1) + 1000;
		long previousTime = System.currentTimeMillis();

		System.out.print("Status: ");
		try {
			while (true) {

				peerActiveTimer = System.currentTimeMillis();

				if (bufferedReader.ready()) {

					data = bufferedReader.readLine();
					
					previousTime = System.currentTimeMillis();

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
						System.out.println("Status:");
					}

				} else {
					
					if(System.currentTimeMillis() - previousTime > messageTimer) {
						printTweets();
						System.out.println("Status:");
						previousTime = System.currentTimeMillis();
						messageTimer = timeGenerator.nextInt((3000 - 1000) + 1) + 1000;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}

	}

	private void sendTweet(String tweet) throws IOException {

		try {
			sendBuffer = tweet.getBytes("ISO-8859-1");

			for (int i = 0; i < Profile.IP.size(); i++) {
				packet = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(Profile.IP.get(i)),
						7014);
				socket.send(packet);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	private void printTweets() {
		System.out.println("### P2P Tweets ###");

		for (int i = 0; i < Profile.unikeys.size(); i++) {
			if (!Profile.currentTweets.get(i).equalsIgnoreCase("-1")) {
				if (!Profile.unikeys.get(i).equalsIgnoreCase(unikey)) {
					if (peerActiveTimer - Profile.lastActive.get(i) < 10000) {
						System.out.println("# " + Profile.pseudos.get(i) + " (" + Profile.unikeys.get(i) + "): "
								+ Profile.currentTweets.get(i));
					} else if (peerActiveTimer - Profile.lastActive.get(i) < 20000) {
						System.out.println(
								"# [" + Profile.pseudos.get(i) + " (" + Profile.unikeys.get(i) + "): " + "idle]");
					} else {
						continue;
					}
				} else {
					System.out.println("# " + Profile.pseudos.get(i) + " (myself): " + Profile.currentTweets.get(i));
				}
			} else {
				if (!Profile.unikeys.get(i).equalsIgnoreCase(unikey)) {
					if (peerActiveTimer - Profile.lastActive.get(i) < 10000) {
						System.out.println("# [" + Profile.pseudos.get(i) + " (" + Profile.unikeys.get(i)
								+ "): not yet initialized]");
					} else if (peerActiveTimer - Profile.lastActive.get(i) < 20000) {
						System.out.println(
								"# [" + Profile.pseudos.get(i) + " (" + Profile.unikeys.get(i) + "): " + "idle]");
					} else {
						continue;
					}
				} else {
					System.out.println("# [" + Profile.pseudos.get(i) + " (myself): not yet initialized]");
				}
			}
		}

		System.out.println("### End Tweets ###");
	}

}
