import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communication extends Thread {

	int time;
	int port;
	InetAddress group;
	long currentTime;
	String unikey;
	DatagramSocket socket;
	DatagramPacket packet;

	byte[] sendBuffer;

	long stopWatch;

	public Communication(String unikey, int time, long currentTime, DatagramSocket socket, DatagramPacket packet,
			int port, InetAddress group) {
		this.time = time;
		this.currentTime = currentTime;
		this.unikey = unikey;
		this.socket = socket;
		this.packet = packet;
		this.port = port;
		this.group = group;
		sendBuffer = new byte[256];
	}

	public void run() {
		while (true) {
			if (System.currentTimeMillis() - currentTime > time) {
				try {
					peerBroadcast();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void peerBroadcast() throws IOException {
		currentTime = System.currentTimeMillis();
		sendBuffer = this.unikey.getBytes("ISO-8859-1");
		packet = new DatagramPacket(sendBuffer, sendBuffer.length, group, port);
		socket.send(packet);
		sendBuffer = new byte[256];
	}

}
