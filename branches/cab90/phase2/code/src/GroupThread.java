/* This thread does all the work. It communicates with the client through Envelopes.
 * 
 */
import java.lang.Thread;
import java.net.Socket;
import java.io.*;
import java.util.*;

/* TODO: test the following code
 * 		- create group "CGROUP"
 * 		- delete group "DGROUP"
 * 		- list group members "LMEMBERS"
 * 		- add user to group "AUSERTOGROUP"
 * 		- rm user from group "DUSERFROMGROUP"
 *
 * 	-brack
 */

public class GroupThread extends Thread 
{
	private final Socket socket;
	private GroupServer my_gs;
	
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
				Envelope response;
				
				if(message.getMessage().equals("GET"))//Client wants a token
				{
					String username = (String)message.getObjContents().get(0); //Get the username
					if(username == null)
					{
						response = new Envelope("FAIL");
						response.addObject(null);
						output.writeObject(response);
					}
					else
					{
						// note: sanitize & validate input in the future -brack
						UserToken yourToken = createToken(username); //Create a token
						
						//Respond to the client. On error, the client will receive a null token
						response = new Envelope("OK");
						response.addObject(yourToken);
						output.writeObject(response);
					}
				}
				else if(message.getMessage().equals("CUSER")) //Client wants to create a user
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
								
								if(createUser(username, yourToken))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}
					
					output.writeObject(response);
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
								
								if(deleteUser(username, yourToken))
								{
									response = new Envelope("OK"); //Success
								}
							}
						}
					}
					
					output.writeObject(response);
				}
				else if(message.getMessage().equals("CGROUP")) //Client wants to create a group
				{
				    /* TODO:  Write this handler */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					}
					else {
						response = new Envelope("FAIL");
						
						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0); //Extract the group name
							UserToken creatorToken = (UserToken)message.getObjContents().get(1); //Extract the token
							
							if(createGroup(groupName, creatorToken)) {
								response = new Envelope("OK"); //Success
							}
						}
					}
					
					output.writeObject(response);
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
					
					output.writeObject(response);
				}
				else if(message.getMessage().equals("LMEMBERS")) //Client wants a list of members in a group
				{
				    /* TODO:  Write this handler */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0); // grab grp name
							UserToken requesterToken = (UserToken)message.getObjContents().get(1); // grab requester token

							// return the list of members
							ArrayList<String> members = my_gs.groupList.getGroupMembers(groupName);
							if(members.size() > 0) {
								response = new Envelope("OK");
								response.addObject(members);
							}
						}
					}

					output.writeObject(response);

				}
				else if(message.getMessage().equals("AUSERTOGROUP")) //Client wants to add user to a group
				{
				    /* TODO:  Write this handler */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0);
							UserToken memberToken = (UserToken)message.getObjContents().get(1);

							if(addGroupMember(groupName, memberToken)) {
								response = new Envelope("OK");
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("RUSERFROMGROUP")) //Client wants to remove user from a group
				{
				    /* TODO:  Write this handler */


					if(message.getObjContents().size() < 2) {
						response = new Envelope("FAIL");
					} else {
						response = new Envelope("FAIL");

						if(message.getObjContents().get(0) != null
							&& message.getObjContents().get(1) != null) {
							String groupName = (String)message.getObjContents().get(0);
							UserToken memberToken = (UserToken)message.getObjContents().get(1);

							if(removeGroupMember(groupName, memberToken)) {
								response = new Envelope("OK");
							}
						}
					}

					output.writeObject(response);
				}
				else if(message.getMessage().equals("DISCONNECT")) //Client wants to disconnect
				{
					socket.close(); //Close the socket
					proceed = false; //End this communication loop
				}
				else
				{
					response = new Envelope("FAIL"); //Server does not understand client request
					output.writeObject(response);
				}
			}while(proceed);	
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}
	
	//Method to create tokens
	private UserToken createToken(String username) 
	{
		//Check that user exists
		if(my_gs.userList.checkUser(username))
		{
			//Issue a new token with server's name, user's name, and user's groups
			UserToken yourToken = new Token(my_gs.name, username, my_gs.userList.getUserGroups(username));
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
				return true;
			}
			// else { return false; // requester can't create groups }
		}
		else
		{
			return false; //requester does not exist
		}
	}

	private boolean addGroupMember(String groupName, UserToken memberToken) {
		String member = memberToken.getSubject();

		if(my_gs.userList.checkUser(member) && my_gs.groupList.checkGroup(groupName)) {
			my_gs.groupList.addGroupMember(groupName, member);
			// note: check user's group and make sure it was actually added before returning true
			return true;
		} else {
			return false; // user or group doesn't exist
		}
	}

	private boolean removeGroupMember(String groupName, UserToken memberToken) {
		String member = memberToken.getSubject();

		if(my_gs.userList.checkUser(member) && my_gs.groupList.checkGroup(groupName)) {
			my_gs.groupList.removeMember(groupName, member);
			// note: check user's group and make sure it was actually removed
			return true;
		} else {
			return false; // user or group doesn't exist
		}
	}

	//Method to delete a group
	private boolean deleteGroup(String groupName, UserToken requesterToken)
	{
		String requester = requesterToken.getSubject();
		
		//Does requester exist? does group exist?
		if(my_gs.userList.checkUser(requester) && my_gs.groupList.checkGroup(groupName))
		{
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);

			//requester needs to be an admin or the owner of the group
			if(temp.contains("ADMIN") || requester.equals(my_gs.groupList.getGroupOwner(groupName)))
			{
				my_gs.groupList.deleteGroup(groupName);
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
	
	//Method to create a user
	private boolean createUser(String username, UserToken yourToken)
	{
		String requester = yourToken.getSubject();
		
		//Check if requester exists
		if(my_gs.userList.checkUser(requester))
		{
			//Get the user's groups
			ArrayList<String> temp = my_gs.userList.getUserGroups(requester);
			//requester needs to be an administrator
			if(temp.contains("ADMIN"))
			{
				//Does user already exist?
				if(my_gs.userList.checkUser(username))
				{
					return false; //User already exists
				}
				else
				{
					my_gs.userList.addUser(username);
					return true;
				}
			}
			else
			{
				return false; //requester not an administrator
			}
		}
		else
		{
			return false; //requester does not exist
		}
	}
	
	//Method to delete a user
	private boolean deleteUser(String username, UserToken yourToken)
	{
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
