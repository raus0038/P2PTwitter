import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Profile {

	static boolean userAdded = false;
	
	static final Properties properties = new Properties();
	static String participantList;
	static String unikeyList;
	
	static String[] participants;
	
	static String fileName;
	
	static FileInputStream input = null;
	
	static ArrayList<String> currentTweets;
	static ArrayList<String> IP;
	static ArrayList<String> unikeys;
	static ArrayList<Long> lastActive;
	static ArrayList<String> pseudos;
	

	public static void load() {
		fileName = "participants.properties";

		try {
			
			// Load all details from participants file into ArrayLists
			
			input = new FileInputStream("participants.properties");
			properties.load(input);
			input.close();
			
			currentTweets = new ArrayList<String>();
			unikeys = new ArrayList<String>();
			IP = new ArrayList<String>();
			lastActive = new ArrayList<Long>();
			pseudos = new ArrayList<String>();
			
			participantList = properties.getProperty("participants");
			
			participants = participantList.split(",");
			
			for(int i = 0; i < participants.length; i++) {
				unikeys.add(properties.getProperty(participants[i] + ".unikey"));
				IP.add(properties.getProperty(participants[i] + ".ip"));
				pseudos.add(properties.getProperty(participants[i] + ".pseudo"));
				currentTweets.add("-1");
				lastActive.add(System.currentTimeMillis());
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
