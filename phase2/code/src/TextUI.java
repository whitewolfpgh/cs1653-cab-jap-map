import java.util.*;
import java.lang.*;
import java.io.*;

public class TextUI {
	public static TextUI that; // static singleton. instead of 'this' use 'that'... hehe
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
	

	protected HashMap<String, Integer> commandList;
	protected GroupClient groupClient;
	protected FileClient fileClient;
	protected Scanner userInputScanner;

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
			commandList.put("cuser", CMD_CUSER);
			commandList.put("duser", CMD_DUSER);
			commandList.put("upfile", CMD_UPFILE);
			commandList.put("dwnfile", CMD_DWNFILE);
			commandList.put("dfile", CMD_DFILE);
			commandList.put("lstfile", CMD_LSTFILE);
			commandList.put("lstgrp", CMD_LSTGRP);
			commandList.put("lstgrpmem", CMD_LSTGRPMEM);
			commandList.put("cgrp", CMD_CGRP);
			commandList.put("dgrp", CMD_DGRP);
			commandList.put("cusergrp", CMD_CUSERGRP);
			commandList.put("dusergrp", CMD_DUSERGRP);

			groupClient = new GroupClient();
			fileClient = new FileClient();

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

				/* create a user */
				case CMD_CUSER:
					createUser(cmdArgs);
				break;

				/* user wants out, just return */
				case CMD_EXIT: 
					printGoodbye();
				break;

				/* user wants help */
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


	public void createUser(String... args) {
		if(args.length < 1) {
			System.out.println("You must supply a user name");
		} else {
			String userName = args[0];
			//System.out.println("DEBUG: cuser input was: "+userName);
			//
			// now use userName to create the user and get a token back
			UserToken userTk = groupClient.getToken(userName);

			boolean result = groupClient.createUser(userName, userTk);

			System.out.println("User created? ["+result+"]");
		}
	}

	public void printHelp(String... args) {
		//System.out.println("DEBUG: args length was "+args.length);

		if(args.length < 1) {
			/* if no additional args, print out the main list of commands */
			System.out.println("Available commands are:");
			System.out.println("\thelp\t Print this help menu.");
			System.out.println("\texit\t Exit this application.");
		} else if(args.length >= 1) {
			/* if there are additional arguments, print additional help */
			System.out.println("Help for command `"+args[0]+"`:");

			try {
				int cmd = commandList.get(args[0]);
				switch(cmd) {
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
