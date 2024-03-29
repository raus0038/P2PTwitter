import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Properties;

public class Profile {

	static boolean userAdded = false;
	
	static final Properties properties = new Properties();
	static String participantList;
	static String unikeyList;
	
	static String[] participants;
	
	static String fileName;
	
	static FileOutputStream output = null;
	static FileInputStream input = null;
	static File propFile = null;
	
	static ArrayList<String> currentTweets;
	static ArrayList<String> IP;
	static ArrayList<String> unikeys;
	static ArrayList<Long> lastActive;
	
	

	public static void load() {
		fileName = "participants.properties";

		try {
			propFile = new File(fileName);
			input = new FileInputStream(propFile);
			output = new FileOutputStream(fileName);
			properties.load(input);
			input.close();
			
			currentTweets = new ArrayList<String>();
			unikeys = new ArrayList<String>();
			IP = new ArrayList<String>();
			lastActive = new ArrayList<Long>();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public synchronized static void addParticipant(String key, InetAddress address) throws IOException {

		participantList = properties.getProperty("participants");
		
		String peer = "peer";

		if (participantList != null) {
			
			int i = 1;
			
			participants = participantList.split(",");

			peer = "peer" + i;
	
			while (properties.getProperty(peer + ".unikey") != null) {
				unikeyList = properties.getProperty(peer + ".unikey");
				if (key.equalsIgnoreCase(unikeyList)) {
					return;
				}
				++i;
				peer = "peer" + i;
			}
			
			peer = "peer" + (participants.length + 1);
	
			properties.setProperty("participants", participantList + peer + ",");
		}
		else {
			peer = "peer1";
			properties.setProperty("participants", peer + ",");
		}
		
		properties.setProperty(peer + ".ip", address.toString().replace("/", ""));
		properties.setProperty(peer + ".unikey" , key);
		
		PrintWriter clearFile = new PrintWriter("participants.properties");
		clearFile.close();
		
		
		properties.store(output, null);
		
		IP.add(address.getHostAddress());
		unikeys.add(key);
		currentTweets.add("-1");
		lastActive.add((long) System.currentTimeMillis());
	

	}

	public static ArrayList<String> getAddresses() {
		return IP;
	}

}
