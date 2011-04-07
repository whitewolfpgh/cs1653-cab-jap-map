/* This thread does all the work. It communicates with the client through Envelopes.
 * 
 */
import java.lang.Thread;
import java.net.Socket;
import java.io.*;
import java.util.*;
import java.security.interfaces.*;
import javax.crypto.*;


/* TODO: test the following code
 *	- GETCHALLENGE and AUTHENTICATE
 *	- envelope encryption / decryption
 * 	- token signing before sending to client
 * 	- token verification before using it
 *
 *
 * 	-brack
 */

public class GroupThread extends Thread 
{
	private final Socket socket;
	private GroupServer my_gs;
	private SecretKey sessionSharedKey;
	private boolean userAuthenticated;
	private String userAuthenticatedName;
	
	public GroupThread(Socket _socket, GroupServer _gs)
	{
		socket = _socket;
		my_gs = _gs;
	}
	
	public void run()
	{
		boolean proceed = true;

		try
		{
			//Announces connection and opens object streams
			System.out.println("*** New connection from " + socket.getInetAddress() + ":" + socket.getPort() + "***");
			final ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			
			do
			{
				Envelope message = (Envelope)input.readObject();
				System.out.println("Request received: " + message.getMessage());
				Envelope response = new Envelope("FAIL");

				if(sessionSharedKey != null && message != null && !sessionSharedKey.equals("")) {
					message.decrypt(sessionSharedKey);
					System.out.println("Decrypted Request: "+message.getMessage());
				}

				boolean secureCommand = true; // assume that everything should be secure

				if(message.getMessage().equals("IDENTIFY")) { // client wants server identity
					secureCommand = false;
					String userName = (String)message.getObjContents().get(0);
					if(userName == null) {
						response = new Envelope("FAIL");
						response.addObject(null);
						//output.writeObject(response);
					} else {
						Certificate serverCert = getServerCertificate(userName);
						
						response = new Envelope("OK");
						response.addObject(serverCert);
						//output.writeObject(response);
					}
				}
				else if(message.getMessage().equals("GETCHALLENGE")) { // client wants to get challenge
					secureCommand = false;
					String userName = (String)message.getObjContents().get(0);
					if(userName == null) {
						response = new Envelope("FAIL");
						response.addObject(null);
						//output.writeObject(response);
					} else {
						String challenge = getUserChallenge(userName); // challenge encrypted with user pub key
						String sessionKey = getSessionKeyForUser(userName); // session shared key encrypted with user pub key

						response = new Envelope("OK");
						response.addObject(challenge);
						response.addObject(sessionKey);
						//output.writeObject(response);
					}
				}
				else if(message.getMessage().equals("AUTHENTICATE")) { // client responds with challenge response
					userAuthenticated = false;
					userAuthenticatedName = null;
					String userName = (String)message.getObjContents().get(0);
					String challengeResponse = (String)message.getObjContents().get(1);
					if(userName == null || challengeResponse == null) {
						response = new Envelope("FAIL");
						response.addObject(null);
						//output.writeObject(response);
					} else {
						boolean challengeOK = verifyUserChallenge(userName, challengeResponse);

						if(challengeOK) {
							userAuthenticatedName = userName;
							userAuthenticated = true;
							response = new Envelope("OK");
							response.addObject(challengeOK);
							//response.encrypt(sessionSharedKey);
							//output.writeObject(response);
						} else {
							response = new Envelope("FAIL");
							response.addObject(null);
							//output.writeObject(response);
						}
					}
				}
				else if(message.getMessage().equals("GET"))//Client wants a token
				{
					String username = (String)message.getObjContents().get(0); //Get the username
					if(username == null || userAuthenticated != true || userAuthenticatedName == null 
						|| userAuthenticatedName.equals("") || !userAuthenticatedName.equals(username))
					{
						response = new Envelope("FAIL");
						response.addObject(null);
						//output.writeObject(response);
					}
					else
					{
						UserToken yourToken = createToken(username); //Create a token
						// verify the token's signature
						if(!validateToken(yourToken)) {
							System.out.println("New token had _BAD_ signature!!!");
							response = new Envelope("FAIL");
							response.addObject(null);
							//output.writeObject(response);
						} else {
							//Respond to the client. On error, the client will receive a null token
							System.out.println("New token had _GOOD_ signature!!!");
							response = new Envelope("OK");
							response.addObject(yourToken);
							//output.writeObject(response);
						}
					}
				}
				else if(message.getMessage().equals("CUSER")) //Client wants to create a user
				{
					if(message.getObjContents().size() < 2)
					{
						response = new Envelope("FAIL");
						System.out.println("NOT ENOUGH PARAMS");
					}
					else
					{
						response = new Envelope("FAIL");
						
						if(message.getObjContents().get(0) != null)
						{
							if(message.getObjContents().get(1) != null)
							{
								String username = (String)message.getObjContents().get(0); //Extract the username
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token
								
								System.out.println("DEBUG || gThread::CUSER got username ["+username+"] and requester ["+yourToken.getSubject()+"]");
								if(createUser(username, yourToken))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}
					
					//output.writeObject(response);
				}
				else if(message.getMessage().equals("DUSER")) //Client wants to delete a user
				{
					
					if(message.getObjContents().size() < 2)
					{
						response = new Envelope("FAIL");
					}
					else
					{
						response = new Envelope("FAIL");
						
						if(message.getObjContents().get(0) != null)
						{
							if(message.getObjContents().get(1) != null)
							{
								String username = (String)message.getObjContents().get(0); //Extract the username
								UserToken yourToken = (UserToken)message.getObjContents().get(1); //Extract the token
								
								System.out.println("DEBUG || gThread::DUSER got username ["+username+"] and requester ["+yourToken.getSubject()+"]");
								if(deleteUser(username, yourToken))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}
					
					//output.writeObject(response);
				}
				else if(message.getMessage().equals("CGROUP")) //Client wants to create a group
				{
				    /* TODO:  Write this handler */

					/* TODO: implement case insensitive groups, so that you can't have users create
					 * 			malicious imposter groups.  Not as big of a deal with most monospace chars, but
					 * 			on some systems the difference between a capital i and lowercase l look the same
					 *
					 * 			example: group_hello vs group_heIIo
					 *
					 *
					 * 		oh, also- same goes for users.
					 */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					}
					else {
						response = new Envelope("FAIL");
						
						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0); //Extract the group name
							UserToken creatorToken = (UserToken)message.getObjContents().get(1); //Extract the token
							boolean hasToken = (creatorToken != null) ? true : false;
							System.out.println("DEBUG || group name ["+groupName+"] creator Token present? ["+hasToken+"]");
							
							if(createGroup(groupName, creatorToken)) {
								response = new Envelope("OK"); //Success
							}
						}
					}
					
					//output.writeObject(response);
				}
				else if(message.getMessage().equals("DGROUP")) //Client wants to delete a group
				{
				    /* TODO:  Write this handler */

					
					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");
						
						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0); //Extract the group name
							UserToken requesterToken = (UserToken)message.getObjContents().get(1); //Extract the token
							
							if(deleteGroup(groupName, requesterToken)) {
								response = new Envelope("OK"); //Success
							}
						}
					}
					
					//output.writeObject(response);
				}
				else if(message.getMessage().equals("LMEMBERS")) //Client wants a list of members in a group
				{
					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0); // grab grp name
							UserToken requesterToken = (UserToken)message.getObjContents().get(1); // grab requester token
							if(!validateToken(requesterToken)) {
								System.out.println("Can't list members. Bad token.");
							} else {

								System.out.println("DEBUG || gThread::LMEMBERS got groupName ["+groupName+"] and requester ["+requesterToken.getSubject()+"]");
								// return the list of members
								try {
									/* XXX it's possible that this is the cuse for the incorrect list of members
									 * 		the server reports the right amount of members
									 */
									List<String> members = my_gs.groupList.getGroupMembers(groupName);
									// TODO if requester isn't owner, and isn't in the group, don't show anything!
									System.out.println("DEBUG || LMEMBERS: group member count for ["+groupName+"] = "+members.size());
									String memberString = "";
									for(String s : members) {
										memberString += s+"||";
									}

									//System.out.println("HERE'S WHAT THE MEMBER STRING IS: "+memberString);

									if(members.size() > 0) {
										response = new Envelope("OK");
										response.addObject(members);
										response.addObject(memberString);
										response.addObject(members.size());
									}
								} catch(Exception e) {
									System.out.println("DEBUG || LMEMBERS: exception- "+e);

									// DEBUG
									e.printStackTrace();
								}
							}
						}
					}

					//output.writeObject(response);

				}
				else if(message.getMessage().equals("AUSERTOGROUP")) //Client wants to add user to a group
				{
				    /* TODO:  Write this handler */

					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null
							&& message.getObjContents().get(2) != null) {
							String userName = (String)message.getObjContents().get(0);
							String groupName = (String)message.getObjContents().get(1);
							UserToken requesterToken = (UserToken)message.getObjContents().get(2);

							System.out.println("DEBUG || gThread::AUSERTOGROUP got groupName ["+groupName+"] userName ["+userName+"] and requester ["+requesterToken.getSubject()+"]");

							if(addGroupMember(groupName, userName, requesterToken)) {
								response = new Envelope("OK");
							}
						}
					}

					//output.writeObject(response);
				}
				else if(message.getMessage().equals("RUSERFROMGROUP")) //Client wants to remove user from a group
				{
				    /* TODO:  Write this handler */

					/* TODO: add logging on the server side for success & fails on commands */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null
							&& message.getObjContents().get(2) != null) {
							String userName = (String)message.getObjContents().get(0);
							String groupName = (String)message.getObjContents().get(1);
							UserToken requesterToken = (UserToken)message.getObjContents().get(2);

							System.out.println("DEBUG || gThread::RUSERFROMGROUP got groupName ["+groupName+"] userName ["+userName+"] and requester ["+requesterToken.getSubject()+"]");

							if(removeGroupMember(groupName, userName, requesterToken)) {
								response = new Envelope("OK");
							}
						}
					}

					//output.writeObject(response);
				}
				else if(message.getMessage().equals("DISCONNECT")) //Client wants to disconnect
				{
					socket.close(); //Close the socket
					proceed = false; //End this communication loop
				}
				else
				{
					response = new Envelope("FAIL"); //Server does not understand client request
					//output.writeObject(response);
				}

				if(response == null) {
					response = new Envelope("FAIL");
				}

				if(secureCommand && sessionSharedKey != null && !sessionSharedKey.equals("")) {
					response.encrypt(sessionSharedKey);
				}

				output.writeObject(response);
			}while(proceed);	
		}
		catch(Exception e)
		{
			System.err.println("Error with group server command: " + e.getMessage());
			e.printStackTrace(System.err);

			try {
				final ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				if(output != null) {
					Envelope response = new Envelope("FAIL");
					output.writeObject(response);
				}
			} catch(Exception e2) {
				// well... no graceful failure for the client. sorry.
			}
		}
	}

	private Certificate getServerCertificate(String userName) {
		Certificate cert = new Certificate(my_gs.getPublicKeyString(), my_gs.name, userName);
		return cert;
	}

	private RSAPublicKey getServerPublicKey() {
		return my_gs.getPublicKey();
	}

	private String getSessionKeyForUser(String userName) {
		RSAPublicKey userPubKey = my_gs.getUserPublicKey("certs/", userName);

		sessionSharedKey = MyCrypto.generateSecretKey();
		String plainSessionSecret = MyCrypto.generateSecretKeyString(sessionSharedKey);
		String eSessionSecret = MyCrypto.encryptString(plainSessionSecret, userPubKey);
		System.out.println("[GET-SESSION-KEY] unencrypted for user '"+userName+"': "+plainSessionSecret);

		return eSessionSecret;
	}

	private String getUserChallenge(String userName) {
		int nonce = MyCrypto.getPRNG().nextInt();
		//String challenge = userName +"||"+ (System.currentTimeMillis()/1000L); // TODO: make this stronger
		String challenge = ""+nonce;

		RSAPublicKey userPubKey = my_gs.getUserPublicKey("certs/", userName);

		String eChallenge = MyCrypto.encryptString(challenge, userPubKey);
		my_gs.storeChallenge(userName, challenge);

		System.out.println("[GET-CHALLENGE] sending to user '"+userName+"'");

		return eChallenge;
	}

	private boolean verifyUserChallenge(String userName, String challengeResponse) {
		if(userName == null || challengeResponse == null || userName.equals("") || challengeResponse.equals("")) {
			return false;
		}

		//System.out.println("[VERIFY-CHALLENGE] checking challenge for '"+userName+"': "+challengeResponse); // DEBUG
		String correctResponse = (String)my_gs.getChallenge(userName);

		//System.out.println("correct response is: "+correctResponse); // DEBUG

		if(challengeResponse.equals(correctResponse)) {
			System.out.println("CORRECT response from '"+userName+"'");
			return true;
		} else {
			System.out.println("WRONG response from '"+userName+"'");
			return false;
		}
	}

	private boolean validateToken(UserToken tok) {
		if(tok == null) {
			return false;
		}

		return tok.verifySignature(my_gs.getPublicKey());
	}

	
	//Method to create tokens
	private UserToken createToken(String username) 
	{
		//Check that user exists
		if(my_gs.userList.checkUser(username))
		{
			//Issue a new token with server's name, user's name, and user's groups
			UserToken yourToken = new Token(my_gs.name, username, my_gs.userList.getUserGroups(username));
			yourToken.sign(my_gs.getPrivateKey());
			return yourToken;
		}
		else
		{
			return null;
		}
	}

	//Method to create a group
	private boolean createGroup(String groupName, UserToken yourToken)
	{
		if(!validateToken(yourToken)) {
			System.out.println("Can't create group. Bad token.");
			return false;
		}

		String requester = yourToken.getSubject();

		//check if requester exists
		if(my_gs.userList.checkUser(requester))
		{
			//ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			
			//does group exist? if group doesn't exist, user can create group (assuming privs)
			//if(userList.getUser(requester).userCanCreateGroup())
			if(my_gs.groupList.checkGroup(groupName)) {
				return false; // group already exists
			}
			else {
				my_gs.groupList.addGroup(groupName, requester); // note: client might want to verify
																//  that the group was actually created
				//my_gs.userList.addGroup(requester, groupName); // TODO fix userList
				return true;
			}
			// else { return false; // requester can't create groups }
		}
		else
		{
			return false; //requester does not exist
		}
	}

	private boolean addGroupMember(String groupName, String memberName, UserToken requesterToken) {
		if(!validateToken(requesterToken)) {
			System.out.println("Can't add group member. Bad token!");
			return false;
		}
		
		String requester = requesterToken.getSubject();

		if(my_gs.userList.checkUser(memberName)  // user exists
			&& my_gs.groupList.checkGroup(groupName)) { // group exists

			if(my_gs.groupList.getGroupOwner(groupName).equals(requester)) { // requester is group owner

				boolean added = my_gs.groupList.addGroupMember(groupName, memberName);
				System.out.println("DEBUG || addGroupMember- user ["+memberName+"] added to group ["+groupName+"]? ["+added+"]");
				if(added) {
					//my_gs.userList.addGroup(memberName, groupName); // TODO fix userList
				}
				return added;
			} else {
				System.out.println("DEBUG || addGroupMember- requester ["+requester+"] isnt the owner of ["+groupName+"]");
				return false;
			}
		} else {
			System.out.println("DEBUG || addGroupMember- user ["+memberName+"] NOT added to group ["+groupName+"]");
			return false; // user or group doesn't exist
		}
	}

	private boolean removeGroupMember(String groupName, String memberName, UserToken requesterToken) {
		if(!validateToken(requesterToken)) {
			System.out.println("Can't remove group member. Bad token.");
			return false;
		}
		
		String requester = requesterToken.getSubject();

		if(my_gs.userList.checkUser(memberName)  // user exists
			&& my_gs.groupList.checkGroup(groupName) // group exists
			&& my_gs.groupList.getGroupOwner(groupName).equals(requester)) { // requester is group owner

			my_gs.groupList.removeMember(groupName, memberName);
			//my_gs.userList.removeGroup(memberName, groupName); // TODO fix userList
			// note: check user's group and make sure it was actually removed
			return true;
		} else {
			System.out.println("[REMOVE-MEMBER] user or group doesn't exist");
			return false; // user or group doesn't exist
		}
	}

	//Method to delete a group
	private boolean deleteGroup(String groupName, UserToken requesterToken)
	{
		if(!validateToken(requesterToken)) {
			System.out.println("Can't delete group. Bad token.");
			return false;
		}
		
		String requester = requesterToken.getSubject();
		
		//Does requester exist? does group exist?
		if(my_gs.userList.checkUser(requester) && my_gs.groupList.checkGroup(groupName))
		{
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);

			//requester needs to be an admin or the owner of the group
			if(temp.contains("ADMIN") || requester.equals(my_gs.groupList.getGroupOwner(groupName)))
			{
				my_gs.groupList.deleteGroup(groupName);
				// TODO need to go through the group's users and clear out membership
				return true;	
			}
			else
			{
				return false; //requester is not an administer nor group owner
			}
		}
		else
		{
			return false; //requester or group does not exist
		}
	}

	public boolean verifyUserKeyStored(String userName) {
		try {
			RSAPublicKey userPubKey = my_gs.getUserPublicKey("certs/", userName);
			if(userPubKey != null) {
				return true;
			}
		} catch(Exception e) {
			System.out.println("DEBUG || disk had no entry in certs for '"+userName+"'");
		}
		return false;
	}

	
	//Method to create a user
	private boolean createUser(String username, UserToken yourToken)
	{
		if(!validateToken(yourToken)) {
			System.out.println("Can't create user. Bad token.");
			return false;
		}
		
		String requester = yourToken.getSubject();
		
		//Check if requester exists
		if(my_gs.userList.checkUser(requester))
		{
			//Get the requester's groups
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			//requester needs to be an administrator
			if(temp.contains("ADMIN"))
			{
				//Does user already exist?
				if(my_gs.userList.checkUser(username)) {
					System.out.println("DEBUG || createUser- user ["+username+"] already exists");
					return false; //User already exists
				} else if(!verifyUserKeyStored(username)) {
					System.out.println("DEBUG || createUser- no pub key on file for user ["+username+"]");
					return false;
				} else {
					System.out.println("DEBUG || createUser- user ["+username+"] successfully created");
					my_gs.userList.addUser(username);
					return true;
				}
			}
			else
			{
				System.out.println("DEBUG || createUser- user ["+requester+"] not admin");
				return false; //requester not an administrator
			}
		}
		else
		{
			System.out.println("DEBUG || createUser- user ["+requester+"] doesn't exist");
			return false; //requester does not exist
		}
	}
	
	//Method to delete a user
	private boolean deleteUser(String username, UserToken yourToken)
	{
		if(!validateToken(yourToken)) {
			System.out.println("Can't delete user. Bad token.");
			return false;
		}
		
		String requester = yourToken.getSubject();
		
		//Does requester exist?
		if(my_gs.userList.checkUser(requester))
		{
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			//requester needs to be an administer
			if(temp.contains("ADMIN"))
			{
				//Does user exist?
				if(my_gs.userList.checkUser(username))
				{
					//User needs deleted from the groups they belong
					ArrayList<String> deleteFromGroups = new ArrayList<String>();
					
					//This will produce a hard copy of the list of groups this user belongs
					for(int index = 0; index < my_gs.userList.getUserGroups(username).size(); index++)
					{
						deleteFromGroups.add(my_gs.userList.getUserGroups(username).get(index));
					}
					
					//Delete the user from the groups
					//If user is the owner, removeMember will automatically delete group!
					for(int index = 0; index < deleteFromGroups.size(); index++)
					{
						my_gs.groupList.removeMember(username, deleteFromGroups.get(index));
					}

					/* removing the owner's group(s) can be handled in the groupList logic..
					 * also says above that removeMember will automatically delete group...
					
					//If groups are owned, they must be deleted
					ArrayList<String> deleteOwnedGroup = new ArrayList<String>();
					
					//Make a hard copy of the user's ownership list
					for(int index = 0; index < my_gs.userList.getUserOwnership(username).size(); index++)
					{
						deleteOwnedGroup.add(my_gs.userList.getUserOwnership(username).get(index));
					}
					
					//Delete owned groups
					for(int index = 0; index < deleteOwnedGroup.size(); index++)
					{
						//Use the delete group method. Token must be created for this action
						deleteGroup(deleteOwnedGroup.get(index), new Token(my_gs.name, username, deleteOwnedGroup));
					}
					*/
					
					//Delete the user from the user list
					my_gs.userList.deleteUser(username);
					
					return true;	
				}
				else
				{
					return false; //User does not exist
					
				}
			}
			else
			{
				return false; //requester is not an administer
			}
		}
		else
		{
			return false; //requester does not exist
		}
	}
	
}
