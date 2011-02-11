import java.util.*;
import java.lang.*;
import java.io.*;

public class TextUI {
	public static TextUI that; // static singleton. instead of 'this' use 'that'... hehe

	public static final String DEFAULT_GROUP_HOST = "localhost";
	public static final int DEFAULT_GROUP_PORT = GroupServer.SERVER_PORT;

	public static final String DEFAULT_FILE_HOST = "localhost";
	public static final int DEFAULT_FILE_PORT = FileServer.SERVER_PORT;


	public static final int CMD_EXIT = 0;
	public static final int CMD_HELP = 1;
	public static final int CMD_CUSER = 2;
	public static final int CMD_DUSER = 3;
	public static final int CMD_UPFILE = 4;
	public static final int CMD_DWNFILE = 5;
	public static final int CMD_DFILE = 6;
	public static final int CMD_LSTFILE = 7;
	public static final int CMD_LSTGRP = 8;
	public static final int CMD_LSTGRPMEM = 9;
	public static final int CMD_CGRP = 11;
	public static final int CMD_DGRP = 12;
	public static final int CMD_CUSERGRP = 13;
	public static final int CMD_DUSERGRP = 14;
	public static final int CMD_AUTH = 15;
	

	protected HashMap<String, Integer> commandList;
	protected GroupClient groupClient;
	protected FileClient fileClient;
	protected Scanner userInputScanner;
	protected UserToken loggedInToken;

	/* create a singleton instance of this class
	 *
	 * if 'that' hasn't been created (rather, is null) 
	 * initialize it and set it to the class being constructed
	 *
	 * if 'that' has been created, just return it
	 */
	public TextUI() {
		if(that == null) {
			commandList = new HashMap<String, Integer>();
			commandList.put("exit", CMD_EXIT);
			commandList.put("help", CMD_HELP);
			commandList.put("auth", CMD_AUTH);

			// group thread commands
			commandList.put("cuser", CMD_CUSER);
			commandList.put("duser", CMD_DUSER);
			commandList.put("lgroups", CMD_LSTGRP);
			commandList.put("lmembers", CMD_LSTGRPMEM);
			commandList.put("cgroup", CMD_CGRP);
			commandList.put("dgroup", CMD_DGRP);
			commandList.put("ausertogroup", CMD_CUSERGRP);
			commandList.put("duserfromgroup", CMD_DUSERGRP);

			// file thread commands
			commandList.put("uploadf", CMD_UPFILE);
			commandList.put("downloadf", CMD_DWNFILE);
			commandList.put("deletef", CMD_DFILE);
			commandList.put("lfiles", CMD_LSTFILE);

			groupClient = new GroupClient();
			fileClient = new FileClient();
			loggedInToken = null;

			that = this;
		}
	}

	/* main function.
	 *
	 * run when 'java TextUI' is run from command line
	 *
	 * continually loops on user input until 'exit' command is reached
	 *
	 * all other user input is passed to handleUserCommand
	 */
	public static void main(String[] args) {
		that = new TextUI();
		that.userInputScanner = new Scanner(System.in);

		that.printHello();

		String userCommand = "";
		while(userCommand != null && !userCommand.equals("exit")) {
			userCommand = that.getUserInput();

			that.handleUserCommand(userCommand);
		}
	}

	/* handle user input
	 *
	 * this method handles user input by looking up the user command
	 * in the commandList map.  
	 * 
	 * depending on the command, a different function will be called
	 */
	public void handleUserCommand(String userCmd) {
		//System.out.println("DEBUG: user input was- "+userCmd);

		String[] cmdLine = userCmd.split(" "); // grab the command
		String[] cmdArgs = new String[cmdLine.length-1]; /* grab the args
															remember to pass this variable to 
															functions that take arguments!! */
		int cmd = 0;
	
		try {
			cmd = commandList.get(cmdLine[0]);

			System.arraycopy(cmdLine, 1, cmdArgs, 0, cmdLine.length-1);
			switch(cmd) {

				/* create a user 
				 *
				 * param: user name (String)
				 */
				case CMD_CUSER:
					createUser(cmdArgs);
				break;

				/* user wants to create a group. 
				 *
				 * param: group name (String)
				 */
				case CMD_CGRP:
					createGroup(cmdArgs);
				break;
				
				/* user wants to list files
				 *
				 * param: none
				 */
				case CMD_LSTFILE:
					listFiles(cmdArgs);
				break;
				
				/* user wants to list groups
				 *
				 * param: none
				 *
				case CMD_LSTGRP:
					listGroups(cmdArgs);
				break;
				*/
				
				/* user wants to list group members
				 *
				 * param: group name (String)
				 */
				case CMD_LSTGRPMEM:
					listGroupMems(cmdArgs);
				break;
				
				/* user wants to upload a file. 
				 *
				 * param: source name, dest name, group name (Strings)
				 */
				case CMD_UPFILE:
					uploadFile(cmdArgs);
				break;
				
				/* user wants to download a file. 
				 *
				 * param: source name, dest name (Strings)
				 */
				case CMD_DWNFILE:
					downloadFile(cmdArgs);
				break;
				
				/* user wants to delete a file. 
				 *
				 * param: file name (String)
				 */
				case CMD_DFILE:
					deleteFile(cmdArgs);
				break;
				
				/* user wants to delete a group. 
				 *
				 * param: group name (String)
				 */
				case CMD_DGRP:
					deleteGroup(cmdArgs);
				break;

				/* user wants to authenticate w/group server (get token) 
				 *
				 * param: user name (String)
				 */
				case CMD_AUTH:
					authUser(cmdArgs);
				break;

				/* user wants out, just return */
				case CMD_EXIT: 
					printGoodbye();
				break;

				/* user wants help 
				 *
				 * optional-param: command in-depth help (String)
				 */
				case CMD_HELP: 
					printHelp(cmdArgs);
				break;
			}
		} catch(Exception e) {
			System.out.println("ERROR... Input not recognized.  Please try again.");
		}
	}

	/* BEGIN HANDLED FUNCTIONS  ******************************************************************
	 *
	 * these functions are called after handling user input in handleUserInput
	 *
	 * this is where new functions should be added as the command list is expanded
	 */


	public void createGroup(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a group name");
		} else {
			String groupName = args[0];
		
			if(ensureGroupConnection()) {
				boolean result = groupClient.createGroup(groupName, loggedInToken);
				System.out.println("Group created? ["+result+"]");
			} else {
				System.out.println("Group not created, '"+groupName+"'.  Group Server not available");
			}

		}
	}
	
	public void deleteGroup(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a group name");
		} else {
			String groupName = args[0];
		
			if(ensureGroupConnection()) {
				boolean result = groupClient.deleteGroup(groupName, loggedInToken);
				System.out.println("Group deleted? ["+result+"]");
			} else {
				System.out.println("Group not deleted, '"+groupName+"'.  Group Server not available");
			}

		}
	}


	public void createUser(String... args) {
		if(args.length < 1) {
			System.out.println("You must supply a user name");
		} else {
			String userName = args[0];
			//System.out.println("DEBUG: cuser input was: "+userName);
			//
			// now use userName to create the user and get a token back
			UserToken userTk = groupClient.getToken(userName);

			if(ensureGroupConnection()) {
				boolean result = groupClient.createUser(userName, userTk);
				System.out.println("User created? ["+result+"]");
			} else {
				System.out.println("Unable to create user '"+userName+"'.  Group Server not available");
			}
		}
	}

	public void authUser(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a target server and a user name");
		} else {
			String userName = args[0];

			if(ensureGroupConnection()) {
				loggedInToken = groupClient.getToken(userName);
				System.out.println("Successfully authenticated to Group Server as '"+userName+"'");
			} else {
				System.out.println("Unable to get token for user '"+userName+"'.  Group Server not available");
			}
		}
	}
	
	public void listFiles(String... args) {
		if(ensureGroupConnection()) {
			List<String> result = fileClient.listFiles(loggedInToken);
			for (int i = 0; i < result.size(); i++)
			{
				System.out.println(result.get(i));
			}
		} else {
			System.out.println("Could not retrieve file list. File Server not available");
		}
	}
	
	public void listGroups(String... args) {
		if(ensureGroupConnection()) {
			List<String> result = fileClient.listFiles(loggedInToken);
			for (int i = 0; i < result.size(); i++)
			{
				System.out.println(result.get(i));
			}
		} else {
			System.out.println("Could not retrieve group list. Group Server not available");
		}
	}
	
	public void listGroupMems(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a group name");
		} else {
			String groupName = args[0];
		
			if(ensureGroupConnection()) {
				List<String> result = groupClient.listMembers(groupName, loggedInToken);
				for (int i = 0; i < result.size(); i++)
				{
					System.out.println(result.get(i));
				}
			} else {
				System.out.println("Could not retrieve member list for group '"+groupName+"'.  Group Server not available");
			}

		}
	}
	
	public void deleteFile(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a file name");
		} else {
			String fileName = args[0];
		
			if(ensureFileConnection()) {
				boolean result = fileClient.delete(fileName, loggedInToken);
				System.out.println("File deleted? [" + result + "]");
			} else {
				System.out.println("Could not delete file '"+fileName+"'.  File Server not available");
			}

		}
	}
	
	public void uploadFile(String... args) {
		if(args.length < 3) {
			System.out.println("you must supply a source, destination and group name");
		} else {
			String sourcePath = args[0];
			String destPath = args[1];
			String groupName = args[2];
			if(ensureFileConnection()) {
				boolean result = fileClient.upload(sourcePath, destPath, groupName, loggedInToken);
				System.out.println("File uploaded? [" + result + "]");
			} else {
				System.out.println("Could not upload file to'"+groupName+"'.  File Server not available");
			}

		}
	}
	
	public void downloadFile(String... args) {
		if(args.length < 2) {
			System.out.println("you must supply a source and destination");
		} else {
			String sourcePath = args[0];
			String destPath = args[1];
			if(ensureFileConnection()) {
				boolean result = fileClient.download(sourcePath, destPath, loggedInToken);
				System.out.println("File downloaded? [" + result + "]");
			} else {
				System.out.println("Could not download file from'"+destPath+"'.  File Server not available");
			}

		}
	}


	public void printHelp(String... args) {
		//System.out.println("DEBUG: args length was "+args.length);

		if(args.length < 1) {
			/* if no additional args, print out the main list of commands */
			System.out.println("Available commands are:");
			System.out.println("\thelp\t Print this help menu.");
			System.out.println("\texit\t Exit this application.");
			System.out.println("\tauth\t Get a token from the group server. Eg: auth alice"); // CMD_AUTH
			System.out.println("\tcuser\t Create a user. Eg: cuser alice"); // CMD_CUSER
			System.out.println("\tduser\t Delete a user. Eg: duser alice"); // CMD_DUSER
			System.out.println("\tlgroups\t List groups."); // CMD_LSTGRP
			System.out.println("\tlmembers\t List group members. Eg: lmembers group_one"); // CMD_LSTGRPMEM
			System.out.println("\tcgroup\t Create a group. Eg: cgroup group_one"); // CMD_CGRP
			System.out.println("\tdgroup\t Delete a group. Eg: dgroup group_one"); // CMD_DGRP
			System.out.println("\tausertogroup\t Add a user to a group. Eg: ausertogroup alice group_one"); // CMD_CUSERGRP
			System.out.println("\tduserfromgroup\t Delete a user from a group. Eg: duserfromgroup alice group_one"); // CMD_DUSERGRP
			System.out.println("\tuploadf\t Upload file. Eg: uploadf /path/to/file.txt \"File.Name.On.Server\""); // CMD_UPFILE
			System.out.println("\tdownloadf\t Download File. Eg: downloadf \"File.Name.On.Server\""); // CMD_DWNFILE
			System.out.println("\tdeletef\t Delete file. Eg: deletef \"File.Name.On.Server\""); // CMD_DFILE
			System.out.println("\tlfiles\t List files."); // CMD_LSTFILE
		} else if(args.length >= 1) {
			/* if there are additional arguments, print additional help */
			System.out.println("Help for command `"+args[0]+"`:");

			try {
				int cmd = commandList.get(args[0]);
				switch(cmd) {

					case CMD_AUTH:
						System.out.println("\tDescription:\t derpa");
						System.out.println("\tUsage:\t herpa");
					break;

					case CMD_EXIT:
						System.out.println("\tDescription:\t exits the application");
						System.out.println("\tUsage:\t exit");
					break;

					case CMD_HELP:
						System.out.println("\tDescription:\t print help menus, optionally with another command");
						System.out.println("\tUsage:\t help [another_command]");
					break;
				}
			} catch(Exception e) {
				System.out.println("\tNo help is available for the command, `"+args[0]+"`");
			}
		}

	}


	/* BEGIN UTILITY FUNCTIONS
	 *
	 * the functions below are for utility
	 */

	public boolean ensureGroupConnection() {
		if(!groupClient.isConnected()) {
			return groupClient.connect(DEFAULT_GROUP_HOST, DEFAULT_GROUP_PORT);
		} else {
			return true;
		}
	}

	public boolean ensureFileConnection() {
		if(!fileClient.isConnected()) {
			return fileClient.connect(DEFAULT_FILE_HOST, DEFAULT_FILE_PORT);
		} else {
			return true;
		}
	}

	public void printHello() {
		String prettyStuff = " ************ ";
		System.out.println(prettyStuff+"Welcome to CS1653-cab-jap-map Phase 2!! "+prettyStuff);
		System.out.println("\nType 'help' for a list of commands. Type 'exit' to quit application.\n");
	}

	public void printGoodbye() {
		System.out.println("Bye!\n");
	}


	public String getUserInput() {
		String ret = "";
		System.out.print("|> ");

		try {
			ret = userInputScanner.nextLine();
		} catch(Exception e) {
			System.out.println("bad input!! try again");
			return "";
		}

		return ret;
	}
}
