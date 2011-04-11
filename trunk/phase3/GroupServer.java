/* Group server. Server loads the users from UserList.bin.
 * If user list does not exists, it creates a new list and makes the user the server administrator.
 * On exit, the server saves the user list to file. 
 */

/*
 * TODO: This file will need to be modified to save state related to
 *       groups that are created in the system
 *
 */

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.*;

public class GroupServer extends Server {

	public static final int SERVER_PORT = 8765;
	public static final String CERT_PATH = "gs_user_keystore/";
	public UserList userList;
	public GroupList groupList;
	public Hashtable<String, String> challenges;
    
	public GroupServer() {
		super(SERVER_PORT, "ALPHA");
		initKeystore();
	}
	
	public GroupServer(int _port) {
		super(_port, "ALPHA");
		initKeystore();
	}

    private void initKeystore() {
        File f = new File(CERT_PATH);
        boolean dirCreated = false;

        if(!f.exists()) {
            dirCreated = f.mkdir();
            System.out.println("Creating group server's user keystore directory at "+CERT_PATH+"  ["+dirCreated+"]");
        }
    }


	public void storeChallenge(String userName, String challenge) {
		if(userName == null || challenge == null || userName.equals("") || challenge.equals("")) {
			System.out.println("[STORE-CHALLENGE] couldn't put challenge for user '"+userName+"'");
		} else {
			if(challenges == null) {
				challenges = new Hashtable<String, String>();
			}

			challenges.put(userName, challenge);
		}
	}

	public String getChallenge(String userName) {
		if(userName == null || userName.equals("")) {
			System.out.println("[GET-CHALLENGE] couldn't get challenge for user '"+userName+"'");
			return null;
		} else {
			return challenges.get(userName);
		}
	}
	
	public void start() {
		// Overwrote server.start() because if no user file exists, initial admin account needs to be created
		System.out.println("Starting Group Server...");
		
		String userFile = "UserList.bin";
		String groupFile = "GroupList.bin";
		Scanner console = new Scanner(System.in);
		ObjectInputStream userStream;
		ObjectInputStream groupStream;
		
		//This runs a thread that saves the lists on program exit
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ShutDownListener(this));
		
		//Open user & group file to get user & group list
		try
		{
			FileInputStream fis = new FileInputStream(userFile);
			userStream = new ObjectInputStream(fis);
			userList = (UserList)userStream.readObject();

			FileInputStream fisGroup = new FileInputStream(groupFile);
			groupStream = new ObjectInputStream(fisGroup);
			groupList = (GroupList)groupStream.readObject();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("UserList/GroupList File Does Not Exist. Creating UserList & GroupList...");
			System.out.println("No users currently exist. Your account will be the administrator.");
			System.out.print("Enter your username: ");
			String username = console.next();

			/* don't let them create a blank user!! */
			if(username == null && username.equals("")) {
				System.out.println("EMPTY USER NAME!");
				System.exit(-1);
			}

			/* create a new group list, create ADMIN group. also assigns user as owner and member */
			groupList = new GroupList();
			groupList.addGroup("ADMIN", username);

			//Create a new list, add current user to the ADMIN group. They now own the ADMIN group.
			userList = new UserList();
			userList.addUser(username);
			userList.addGroup(username, "ADMIN");
			userList.addOwnership(username, "ADMIN");

			System.out.println("Group ADMIN created, added user '"+username+"'");
		}
		catch(IOException e)
		{
			System.out.println("Error reading from UserList/GroupList file");
			System.exit(-1);
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("Error reading from UserList/GroupList file");
			System.exit(-1);
		}
		
		//Autosave Daemon. Saves lists every 5 minutes
		AutoSave aSave = new AutoSave(this);
		aSave.setDaemon(true);
		aSave.start();

		//This block listens for connections and creates threads on new connections
		try
		{
			
			final ServerSocket serverSock = new ServerSocket(port);
			
			Socket sock = null;
			GroupThread thread = null;

			System.out.println("Group Server started successfully. Waiting for connections.");
			
			while(true)
			{
				sock = serverSock.accept();
				thread = new GroupThread(sock, this);
				thread.start();
			}
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}


	}
	
}

//This thread saves the user list
class ShutDownListener extends Thread
{
	public GroupServer my_gs;
	
	public ShutDownListener (GroupServer _gs) {
		my_gs = _gs;
	}
	
	public void run()
	{
		System.out.println("GroupServer: Shutting down server");
		ObjectOutputStream outStream;
		try
		{
			outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
			outStream.writeObject(my_gs.userList);
		}
		catch(Exception e)
		{
			System.err.println("Error in shut down writing UserList.bin: " + e.getMessage());
			e.printStackTrace(System.err);
		}

		try
		{
			outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
			outStream.writeObject(my_gs.groupList);
		}
		catch(Exception e)
		{
			System.err.println("Error in shut down writing GroupList.bin: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
}

class AutoSave extends Thread
{
	public GroupServer my_gs;
	
	public AutoSave (GroupServer _gs) {
		my_gs = _gs;
	}
	
	public void run()
	{
		do
		{
			try
			{
				Thread.sleep(300000); //Save group and user lists every 5 minutes
				System.out.println("Autosave group and user lists...");
				ObjectOutputStream outStream;
				try
				{
					outStream = new ObjectOutputStream(new FileOutputStream("UserList.bin"));
					outStream.writeObject(my_gs.userList);
				}
				catch(Exception e)
				{
					System.err.println("Error auto-saving UserList.bin: " + e.getMessage());
					e.printStackTrace(System.err);
				}

				try
				{
					outStream = new ObjectOutputStream(new FileOutputStream("GroupList.bin"));
					outStream.writeObject(my_gs.groupList);
				}
				catch(Exception e)
				{
					System.err.println("Error auto-saving GroupList.bin: " + e.getMessage());
					e.printStackTrace(System.err);
				}
			}
			catch(Exception e)
			{
				System.out.println("GroupServer: Autosave Interrupted");
			}
		}while(true);
	}
}
