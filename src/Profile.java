import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Properties;

public class Profile {

	static boolean userAdded = false;
	
	static final Properties properties = new Properties();
	static String participantList;
	static String[] participants;
	
	static String fileName;
	
	static FileOutputStream output = null;
	static FileInputStream input = null;
	static File propFile = null;
	
	static ArrayList<String> currentTweets;
	static ArrayList<String> IP;
	static ArrayList<String> unikeys;
	

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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getProperties(String unikey) {

	}

	public synchronized static void addParticipant(String key, InetAddress address) throws IOException {

		participantList = properties.getProperty("participants");
		String peer;

		if (participantList != null) {
			
			participants = participantList.split(",");

			peer = "peer" + (participants.length + 1);
			for (String s : participants) {
				if (s.equalsIgnoreCase(key)) {
					return;
				}
			}
			
			
			
			properties.setProperty("participants", participantList + peer + ",");
		}
		else {
			peer = "peer1";
			properties.setProperty("participants", peer + ",");
		}
		
		properties.setProperty(peer + ".ip", address.toString().replace("/", ""));
		properties.setProperty(peer + ".unikey" , key);
		
		
		properties.store(output, null);
		
		IP.add(address.getHostAddress());
		unikeys.add(key);
		currentTweets.add("Not Initialized");
	

	}

	public static ArrayList<String> getAddresses() {
		return IP;
	}

}
