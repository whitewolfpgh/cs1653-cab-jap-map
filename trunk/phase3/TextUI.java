import java.io.*;
import java.lang.*;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.*;

import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;


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
	public static final int CMD_CONNECT = 16;
	public static final int CMD_KEYGEN = 17;
	public static final int CMD_GETFS = 18;
	public static final int CMD_GETKEYCHAIN = 19;
	public static final int CMD_LOGOUT = 20;
	

	protected HashMap<String, Integer> commandList;
	protected GroupClient groupClient;
	protected FileClient fileClient;
	protected Scanner userInputScanner;
	protected UserToken loggedInToken;
	protected UserToken loggedInFSToken;
	protected String loggedInFSAddress;
	protected String loggedInUserName;
	protected Hashtable<Integer, String> loggedInKeychain;
	protected Hashtable<String, Hashtable<Integer, String>> allKeychains;

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
			//commandList.put("auth", CMD_AUTH);
			commandList.put("getkeychain", CMD_GETKEYCHAIN);
			commandList.put("getfstoken", CMD_GETFS);
			commandList.put("connect", CMD_CONNECT);
			commandList.put("logout", CMD_LOGOUT);
			commandList.put("keygen", CMD_KEYGEN);

			// group thread commands
			commandList.put("cuser", CMD_CUSER);
			commandList.put("duser", CMD_DUSER);
			//commandList.put("lgroups", CMD_LSTGRP);
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
			loggedInFSToken = null;
			allKeychains = new Hashtable<String, Hashtable<Integer, String>>();

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

		//String[] cmdLine = userCmd.split(" "); // grab the command
		//String[] cmdArgs = new String[cmdLine.length-1]; 
															/* grab the args
															remember to pass this variable to 
															functions that take arguments!! */

		List<String> matches = new ArrayList<String>();
        Pattern rgx = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher rgxMatcher = rgx.matcher(userCmd);
        while (rgxMatcher.find()) {
            matches.add(rgxMatcher.group().replaceAll("\"", ""));
        }

        String[] cmdLine = new String[matches.size()];
        cmdLine = matches.toArray(cmdLine);
        String[] cmdArgs = new String[cmdLine.length-1];

		int cmd = 0;
	
		try {
			cmd = commandList.get(cmdLine[0]);

			System.arraycopy(cmdLine, 1, cmdArgs, 0, cmdLine.length-1);

			/* DEBUG !!!
			System.out.println("Command received: ["+cmdLine[0]+"] and args:");
			for(int i=0; i < cmdArgs.length; i++) {
				System.out.println("\targ["+i+"] => ["+cmdArgs[i]+"]");
			}
			*/

			switch(cmd) {

				/* create a user 
				 *
				 * param: user name (String)
				 */
				case CMD_CUSER:
					createUser(cmdArgs);
				break;

				case CMD_DUSER:
					deleteUser(cmdArgs);
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

				/* add user to group */
				case CMD_CUSERGRP:
					addUserToGroup(cmdArgs);
				break;

				/* delete user from group */
				case CMD_DUSERGRP:
					deleteUserFromGroup(cmdArgs);
				break;

				/* user wants to authenticate w/group server (get token) 
				 *
				 * param: user name (String)
				 */
				case CMD_AUTH:
					authUser(cmdArgs);
				break;
				
				/* user wants to authenticate w/group server (get token) 
				 *
				 * param: user name (String)
				 */
				case CMD_GETFS:
					getFSToken(cmdArgs);
				break;


				/* user wants to get a keychain for a group
				 *
				 * params: group name (String)
				 */
				case CMD_GETKEYCHAIN:
					getGSKeychain(cmdArgs);
				break;
				

				/* user wants to connect to group/file server
				 *
				 * param: target server, user name
				 */
				case CMD_CONNECT:
					connectToServer(cmdArgs);
				break;

				case CMD_LOGOUT:
					logoutOfServer(cmdArgs);
				break;

				/* user wants to generate pub/priv key pair
				 */
				case CMD_KEYGEN:
					keygenForUser(cmdArgs);
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
		/* it'd be nice to pass up broken pipe errors so that when the server dies,
		 * the client doesn't get spammed with exception output
		 *
		 * alternative is to remove stack trace prints in the clients
		 * } catch(java.net.SocketException se) {
			System.out.println("ERROR: Group Server unavailable!  Connection lost!");
			printGoodbye();
			System.exit(-1);*/
		} catch(Exception e) {
			System.out.println("ERROR... Input not recognized.  Please try again.");

			// DEBUG ONLY!!!
			//System.out.println("DEBUG: error occurred- "+e);
			e.printStackTrace();
		}
	}

	/* BEGIN HANDLED FUNCTIONS  ******************************************************************
	 *
	 * these functions are called after handling user input in handleUserInput
	 *
	 * this is where new functions should be added as the command list is expanded
	 */

	public void addUserToGroup(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a user name and group name");
		} else {
			String userName = args[0];
			String groupName = args[1];
			
			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't add user to group - bad token");
					return;
				}

				boolean result = groupClient.addUserToGroup(userName, groupName, loggedInToken);
				System.out.println("User ["+userName+"] added to group ["+groupName+"]? ["+result+"]");
			} else {
				System.out.println("User ["+userName+"] NOT added to group ["+groupName+"].  Group Server not available");
			}
		}
	}

	public void deleteUserFromGroup(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a user name and group name");
		} else {
			String userName = args[0];
			String groupName = args[1];
			
			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't delete user - bad token");
					return;
				}

				boolean result = groupClient.deleteUserFromGroup(userName, groupName, loggedInToken);
				System.out.println("User ["+userName+"] removed from group ["+groupName+"]? ["+result+"]");
			} else {
				System.out.println("User ["+userName+"] NOT removed from group ["+groupName+"].  Group Server not available");
			}
		}
	}

	public void createGroup(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a group name");
		} else {
			String groupName = args[0];
		
			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't create group - bad token");
					return;
				}

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
		
			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't delete group - bad token");
					return;
				}

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

			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't create user - bad token");
					return;
				}

				boolean result = groupClient.createUser(userName, loggedInToken);
				System.out.println("User created? ["+result+"]");
			} else {
				System.out.println("Unable to create user '"+userName+"'.  Group Server not available");
			}
		}
	}

	public void deleteUser(String... args) {
		if(args.length < 1) {
			System.out.println("You must supply a user name");
		} else {
			String userName = args[0];
			//System.out.println("DEBUG: duser input was: "+userName);

			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't delete user - bad token");
					return;
				}

				boolean result = groupClient.deleteUser(userName, loggedInToken);
				System.out.println("User deleted? ["+result+"]");
			} else {
				System.out.println("Unable to delete user '"+userName+"'.  Group Server not available");
			}
		}
	}

	public void authUser(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a target server and a user name");
		} else {
			String userName = args[0];

			if(groupClient.isConnected()) {
				//loggedInToken = groupClient.getToken(loggedInUserName);
				if(!updateGroupServerToken()) {
					System.out.println("Can't auth user - bad token");
					return;
				}

				System.out.println("Successfully authenticated to Group Server as '"+userName+"'");
			} else {
				System.out.println("Unable to get token for user '"+userName+"'.  Group Server not available");
			}
		}
	}
	
	public void getFSToken(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a target server and a user name");
		} else {
			String userName = args[0];
			String fsAddress = args[1];
			if(groupClient.isConnected()) {
				loggedInFSToken = groupClient.getFSToken(loggedInUserName, loggedInFSAddress);
				if(!updateGroupServerToken()) {
					System.out.println("Can't auth user - bad token");
					return;
				}

				System.out.println("Successfully authenticated to Group Server as '"+userName+"'");
			} else {
				System.out.println("Unable to get token for user '"+userName+"'.  Group Server not available");
			}
		}
	}
	
	public void logoutOfServer(String... args) {
		if(args.length < 1) {
			System.out.println("usage: logout [server_type]");
		} else {
			String connectType = args[0];

			if(connectType.equals("group")) {
				loggedInUserName = null;
				loggedInToken = null;
				loggedInKeychain = null;
				allKeychains = null;
				groupClient.disconnect();
			} else if(connectType.equals("file")) {
				loggedInFSAddress = null;
				loggedInFSToken = null;
				fileClient.disconnect();
			}
		}
	}

	/* connect to a server, name@localhost:1234
	 *
	 */
	public void connectToServer(String... args) {
		if(args.length < 2) {
			System.out.println("usage: connect [server_type] [user]@[host]:[port]");
		} else {
			String connectType = args[0];
			String connectInfo = args[1];

			//System.out.println("connect type: ["+connectType+"] server info: ["+connectInfo+"]");

			List<String> matches = new ArrayList<String>();
			Pattern rgx = Pattern.compile("(.*)@(.*)");
			Matcher rgxMatcher = rgx.matcher(connectInfo.trim());

			if(rgxMatcher.matches()) {
				//System.out.println("found matches: \n"+rgxMatcher);
				String userName = rgxMatcher.group(1);
				String hostAddress = rgxMatcher.group(2);
				String[] hostSplit = hostAddress.split(":");
				int port = -1;

				InetAddress addr = null;

				//System.out.println("connecting '"+userName+"' to "+addr);

				if(hostSplit != null && hostSplit.length > 1) {
					port = Integer.valueOf(hostSplit[1]);
					hostAddress = hostSplit[0];
					System.out.println("Using non-standard port ["+port+"]");
				}

				try {
					addr = InetAddress.getByName(hostAddress);
				} catch(java.net.UnknownHostException e) {
					System.out.println("Unknown host "+hostAddress);
					return;
				}

				// 
				try {
					if(hostAddress == null || hostAddress.equals("")) {
						System.out.println("Host name empty.  Could not connect.");
						return;
					}

					if(userName == null || userName.equals("")) {
						System.out.println("User name empty.  Could not connect.");
						return;
					}

					loggedInUserName = userName;
					if(connectType.equals("group")) {
						if(port < 1) {
							port = DEFAULT_GROUP_PORT;
						}

						System.out.println("Connecting '"+userName+"' to group server ["+hostAddress+"]:["+port+"]");
						groupClient.connect(hostAddress, port);

						Certificate groupServerCert = groupClient.getServerIdentity(userName);
						RSAPublicKey serverPubKey = MyCrypto.readPublicKeyString(groupServerCert.getPublicKey());

						/* TODO if this server isn't in known hosts, ask. otherwise, set the choice as YES */
						String choice = "";
						boolean groupServerKnown = false;
						//groupServerKnown = checkKnownServer("group", groupServerCert);

						if(groupServerKnown) {
							choice = "YES";
							//System.out.println("Found server in known hosts.  Continuing without user validation.");
						} else {
							System.out.print("\nGOT CERTIFICATE: \n"+groupServerCert+"\n\n do you wish to accept? (yes/no): ");

							Scanner s = new Scanner(System.in);
							choice = s.next();
							//System.out.println("you chose: ["+choice+"]");
						}


						if(choice.toUpperCase().equals("YES")) {
							// initiate authentication
							System.out.println("You answered '"+choice+"'. Proceeding with authentication.... This may take a minute....");

							// TODO: store server identity for future use
							if(!groupServerKnown) {
								//saveKnownServer("group", groupServerCert);
								//groupServerKnown = true;
								//saveGroupPubKey(groupServerCert.getPublicKey())
							}

							String challengeResponse = groupClient.getChallengeResponse(userName, getPrivateKey(userName));
							boolean authOK = false;

							if(challengeResponse != null && !challengeResponse.equals("")) {
								//System.out.println("Got challenge from server: "+challenge);

								//challengeResponse = MyCrypto.decryptString(challenge, getPrivateKey(userName));
								//System.out.println("Got challenge response from client: "+challengeResponse); // DEBUG
								System.out.println("Authenticating...");
								authOK = groupClient.authenticate(userName, challengeResponse);
							} else {
								System.out.println("[GROUP-CONNECT] challenge was null or empty");
							}

							if(authOK) {
								System.out.println("Successfully authenticated as '"+userName+"' to group server "+groupServerCert.getIssuer());
								loggedInUserName = userName;
								loggedInToken = groupClient.getToken(loggedInUserName);
							} else {
								System.out.println("AUTH FAILURE as '"+userName+"' to group server "+groupServerCert.getIssuer());
								loggedInToken = null;
								loggedInUserName = null;
							}
						} else {
							// no go. disconnect.
							groupClient.disconnect();
						}
					} else if(connectType.equals("file")) {
						if(port < 1) {
							port = DEFAULT_FILE_PORT;
						}

						System.out.println("Connecting '"+userName+"' to file server ["+hostAddress+"]:["+port+"]");
						fileClient.connect(hostAddress, port);
						loggedInFSAddress = hostAddress + port;
						
						Certificate fileServerCert = fileClient.getServerIdentity(userName);
						RSAPublicKey serverPubKey = MyCrypto.readPublicKeyString(fileServerCert.getPublicKey());

						System.out.print("\nGOT CERTIFICATE: \n"+fileServerCert+"\n\n do you wish to accept? (yes/no): ");

						Scanner s = new Scanner(System.in);
						String choice = s.next();

						//System.out.println("you chose: ["+choice+"]");

						if(choice.toUpperCase().equals("YES")) {
							// TODO initiate pub key exchange for encrypted connection
						} else {
							// no go. disconnect.
							fileClient.disconnect();
						}
					}
				} catch(Exception e) {
					System.out.println("Unable to connect to "+connectType+" server.");
				}
			} else {
				System.out.println("Unable to read user/server/port.\n"+rgxMatcher);
			}
		}
	}

	public void getGSKeychain(String... args) {
		if(args.length < 1) {
			System.out.println("usage: getkeychain groupName");
		} else {
			if(groupClient.isConnected()) {
				if(!updateGroupServerToken()) {
					System.out.println("Can't auth user - bad token");
					return;
				}

				String groupName = args[0];
				if(groupName != null && !groupName.equals("")) {
					loggedInKeychain = groupClient.getGroupKeychain(groupName, loggedInToken);
					Hashtable<Integer, String> keyChain = groupClient.getGroupKeychain(groupName, loggedInToken);
					System.out.println("Keychain is: "+keyChain.size());
					allKeychains.put(groupName, keyChain);
					System.out.println("Keychain loaded for group '"+groupName+"'");
					for(int i=1; i <= loggedInKeychain.size(); i++) {
						System.out.println("DEBUG || key_id["+i+"] keyHex["+loggedInKeychain.get(i)+"]");
					}
				}
			}
		}
	}

	public void keygenForUser(String... args) {
		if(args.length < 1) {
			System.out.println("usage: keygen userName");
		} else {
			String userName = args[0];
			if(userName != null && !userName.equals("")) {
				Hashtable<String, RSAKey> keyPair = MyCrypto.createKeyPair();

				RSAPublicKey pubKey = (RSAPublicKey)keyPair.get("public_key");
				RSAPrivateKey privKey = (RSAPrivateKey)keyPair.get("private_key");


				boolean publicWritten = writePublicKey(userName, pubKey);
				boolean privateWritten = writePrivateKey(userName, privKey);

				/* DEBUG
				System.out.println("pub key new: "+pubKey+"\n||------------------********------------------||\n priv key new: "+privKey);
				System.out.println("\n\n|| ---------||||||||||||||||||||||||||||||||----------- ||");
				System.out.println("\n|| ---------||||||||||| FROM FILE: |||||||||||||||||||||----------- ||");
				System.out.println("\n|| ---------||||||||||||||||||||||||||||||||----------- ||\n");

				System.out.println("pub key file: "+getPublicKey(userName)+"\n\n||------------------********------------------||\n\n priv key file: "+getPrivateKey(userName));
				//boolean publicWritten = MyCrypto.writePublicKeyFile(pubKey, "client_certs/"+userName+"_public.key");
				//boolean privateWritten = MyCrypto.writePrivateKeyFile(privateKey, "client_certs/"+userName+"_private.key");
				*/
				if(publicWritten == false || privateWritten == false) {
					System.out.println("Unable to generate key pair for user '"+userName+"'");
				} else {
					System.out.println("Key pair written to client_keys/ for user '"+userName+"'");
				}
			}
		}
	}

	public String getCertPath(String userName) {
		String keyDir = "client_keys/";
		File f = new File(keyDir);
		boolean dirCreated = false;

		if(!f.exists()) {
			dirCreated = f.mkdir();
			System.out.println("Creating group keychain directory at "+keyDir+"  ["+dirCreated+"]");
		}

        return keyDir+userName;
	}

    public String getPublicKeyString(String userName) {
        RSAPublicKey pubKey = getPublicKey(userName);
        String ret = pubKey.getModulus().toString(16)+"\n"+pubKey.getPublicExponent().toString(16);
        return ret;
    }

    public String getPrivateKeyString(String userName) {
        RSAPrivateKey privKey = getPrivateKey(userName);
        String ret = privKey.getModulus().toString(16)+"\n"+privKey.getPrivateExponent().toString(16);
        return ret;
    }

    public RSAPrivateKey getPrivateKey(String userName) {
        return MyCrypto.readPrivateKeyFile(getCertPath(userName)+"_private.key");
    }

    public boolean writePrivateKey(String userName, RSAPrivateKey userKey) {
        return MyCrypto.writePrivateKeyFile(userKey, getCertPath(userName)+"_private.key");
    }

    public RSAPublicKey getPublicKey(String userName) {
        return MyCrypto.readPublicKeyFile(getCertPath(userName)+"_public.key");
    }

    public boolean writePublicKey(String userName, RSAPublicKey userKey) {
        return MyCrypto.writePublicKeyFile(userKey, getCertPath(userName)+"_public.key");
    }

	public void listFiles(String... args) {
		if(fileClient.isConnected()) {
			if(!updateGroupServerToken()) {
				System.out.println("Can't files - bad token");
				return;
			}

			List<String> result = fileClient.listFiles(loggedInFSToken);
			for (int i = 0; i < result.size(); i++)
			{
				System.out.println(result.get(i));
			}
		} else {
			System.out.println("Could not retrieve file list. File Server not available");
		}
	}
	/*
	public void listGroups(String... args) {
		if(ensureGroupConnection()) {
			List<String> result = fileClient.listFiles(loggedInFSToken);
			for (int i = 0; i < result.size(); i++)
			{
				System.out.println(result.get(i));
			}
		} else {
			System.out.println("Could not retrieve group list. Group Server not available");
		}
	}
*/

	public boolean updateGroupServerToken() {
		try {
			if(loggedInUserName == null || loggedInToken == null || loggedInUserName.equals("")) {
				System.out.println("no user is logged in.  please connect to a group server.");
				return false;
			}

			loggedInToken = groupClient.getToken(loggedInUserName);
			return true;
		} catch(Exception e) {
			System.out.println("Error getting token for '"+loggedInUserName+"'");
			return false;
		}
	}

	
	public void listGroupMems(String... args) {
		if(args.length < 1) {
			System.out.println("you must supply a group name");
		} else {
			String groupName = args[0];
		
			if(groupClient.isConnected()) {
				if(!updateGroupServerToken()) {
					System.out.println("Can't list members - bad token");
					return;
				}

				List<String> result = groupClient.listMembers(groupName, loggedInToken);
				if(result != null && result.size() > 0) {
					for (int i = 0; i < result.size(); i++)
					{
						System.out.println(result.get(i));
					}
				} else {
					System.out.println("There are no members in that group.");
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
		
			if(fileClient.isConnected()) {
				if(!updateGroupServerToken()) {
					System.out.println("Can't delete file - bad token");
					return;
				}

				boolean result = fileClient.delete(fileName, loggedInFSToken);
				System.out.println("File deleted? [" + result + "]");
			} else {
				System.out.println("Could not delete file '"+fileName+"'.  File Server not available");
			}

		}
	}

	public String getLatestGroupKeyString(String groupName) {
		Hashtable<Integer, String> currKeychain = loggedInKeychain;
		try {
			currKeychain = allKeychains.get(groupName);
			if(currKeychain == null) {
				System.out.println("Null keychain... did you run 'getkeychain'?");
			}
		} catch(Exception e) {
			System.out.println("Empty keychain... did you run 'getkeychain'?");
		}
		//return currKeychain.get(currKeychain.size());
		return loggedInKeychain.get(loggedInKeychain.size());
	}

	public SecretKey getLatestGroupKey(String groupName) {
		return MyCrypto.readDESString(getLatestGroupKeyString(groupName));
	}

	public int getLatestGroupKeyId(String groupName) {
		Hashtable<Integer, String> currKeychain = allKeychains.get(groupName);
		return currKeychain.size();
	}

	public void uploadFile(String... args) {
		if(args.length < 3) {
			System.out.println("usage: uploadf [local_path] [file_name_on_server] [group_name]");
		} else {
			String sourcePath = args[0];
			String destPath = args[1];
			String groupName = args[2];
			if(fileClient.isConnected()) {
				if(!updateGroupServerToken()) {
					System.out.println("Can't upload file - bad token");
					return;
				}

				loggedInToken.getGroups();


				System.out.println("locking file "+sourcePath);
				boolean success = MyCrypto.encryptFile(getLatestGroupKey(groupName), sourcePath);
				boolean result;

				if(success) {
					System.out.println("File locked at "+sourcePath+".locked");
					//result = fileClient.upload(sourcePath+".locked", destPath, groupName, loggedInFSToken);
					result = fileClient.upload(sourcePath+".locked", destPath, groupName, loggedInToken);
				} else {
					System.out.println("COULDN'T LOCK FILE! Aborting.");
					result = false;
					return;
				}

				System.out.println("File uploaded? [" + result + "]");
			} else {
				System.out.println("Could not upload file to'"+groupName+"'.  File Server not available");
			}

		}
	}
	
	public void downloadFile(String... args) {
		if(args.length < 3) {
			System.out.println("usage: downloadf [file_name_on_server] [local_path] [group_name]");
		} else {
			String sourcePath = args[0];
			String destPath = args[1];
			String groupName = args[2];
			if(fileClient.isConnected()) {
				if(!updateGroupServerToken()) {
					System.out.println("Can't download file - bad token");
					return;
				}

				loggedInToken.getGroups();

				//boolean result = fileClient.download(sourcePath, destPath, loggedInFSToken);
				boolean result = fileClient.download(sourcePath, destPath, loggedInToken);
				System.out.println("File downloaded? [" + result + "]");

				boolean success = MyCrypto.decryptFile(getLatestGroupKey(groupName), destPath);
				System.out.println("File unlocked? [" + success + "]");
				if(success) {
					System.out.println("File unlocked at: "+destPath+".unlocked");
				} else {
					System.out.println("Could not unlock file at: "+destPath);
				}
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
			//System.out.println("\tauth\t Get a token from the group server. Eg: auth alice"); // CMD_AUTH
			System.out.println("\tcuser\t Create a user. Eg: cuser alice"); // CMD_CUSER
			System.out.println("\tduser\t Delete a user. Eg: duser alice"); // CMD_DUSER
			//System.out.println("\tlgroups\t List groups."); // CMD_LSTGRP
			System.out.println("\tlmembers\t List group members. Eg: lmembers group_one"); // CMD_LSTGRPMEM
			System.out.println("\tcgroup\t Create a group. Eg: cgroup group_one"); // CMD_CGRP
			System.out.println("\tdgroup\t Delete a group. Eg: dgroup group_one"); // CMD_DGRP
			System.out.println("\tausertogroup\t Add a user to a group. Eg: ausertogroup alice group_one"); // CMD_CUSERGRP
			System.out.println("\tduserfromgroup\t Delete a user from a group. Eg: duserfromgroup alice group_one"); // CMD_DUSERGRP
			System.out.println("\tuploadf\t Upload file. Eg: uploadf /path/to/file.txt \"File.Name.On.Server\" ADMIN"); // CMD_UPFILE
			System.out.println("\tdownloadf\t Download File. Eg: downloadf \"File.Name.On.Server\""); // CMD_DWNFILE
			System.out.println("\tdeletef\t Delete file. Eg: deletef \"File.Name.On.Server\""); // CMD_DFILE
			System.out.println("\tlfiles\t List files."); // CMD_LSTFILE
			System.out.println("\tconnect\t Connect to a server. Eg: connect group brack@localhost:1234"); // CMD_LSTFILE
			System.out.println("\tkeygen\t Generate public/private key pair for user. eg: keygen brack"); // CMD_LSTFILE
			System.out.println("\tgetkeychain\t Get the most recent keychain for a group. eg: keygen brack"); // CMD_LSTFILE

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

	/* deprecated
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
	*/

	public void printHello() {
		String prettyStuff = " ************ ";
		System.out.println(prettyStuff+"Welcome to CS1653-cab-jap-map Phase 3!! "+prettyStuff);
		System.out.println("\nType 'help' for a list of commands. Type 'exit' to quit application.\n");
	}

	public void printGoodbye() {
		logoutOfServer("group");
		logoutOfServer("file");
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
