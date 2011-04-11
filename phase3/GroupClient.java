/* Implements the GroupClient Interface */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.ObjectInputStream;
import java.security.*;
import java.security.Security;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class GroupClient extends Client implements GroupClientInterface {
	
	//set to 1 because initial message is n-1 and server expects 0
	private int sessionID;
	private int nonce;
	
/*
	public Certificate getServerIdentity(String userName) {
		try {
			Certificate cert = null;
			Envelope message = null, response = null;

			message = new Envelope("IDENTIFY");
			message.addObject(userName);
			output.writeObject(message);

			response = (Envelope)input.readObject();

			if(response.getMessage().equals("OK")) {
				ArrayList<Object> temp = null;
				temp = response.getObjContents();

				if(temp.size() == 1) {
					cert = (Certificate)temp.get(0);
					return cert;
				}
			}

			return null;
		} catch(Exception e) {
			System.err.println("Error: "+e.getMessage());

			e.printStackTrace(System.err);
			return null;
		}
	}
	*/

	public String getChallengeResponse(String userName, RSAPrivateKey privKey) {
		try {
			String challengeResponse = null;
			Envelope message = null, response = null;

			message = new Envelope("GETCHALLENGE");
			message.addObject(userName);
			message.setNonce(nonce);
			output.writeObject(message);

			response = (Envelope)input.readObject();
			
			//read in sessionID from server
			System.out.println("Getting sessionID from server...");
			System.out.println(response.getNonce());
			sessionID = response.getNonce();
			//This is not getting any value from response!!
			System.out.println(sessionID);
			nonce = sessionID;

			if(response.getMessage().equals("OK")) {
				ArrayList<Object> temp = null;
				temp = response.getObjContents();

				if(temp.size() == 2) {
					String challenge = (String)temp.get(0);
					String keyStr = (String)temp.get(1);

					try {
						if(challenge != null && !challenge.equals("") && keyStr != null && !keyStr.equals("")) {
							challengeResponse = MyCrypto.decryptString(challenge, privKey);

							String decryptedSharedKey = MyCrypto.decryptString(keyStr, privKey);
							System.out.println("CLIENT SHARED KEY IS "+decryptedSharedKey);
							sessionSharedKey = MyCrypto.readSecretKeyString(decryptedSharedKey);
						}
					} catch(Exception e) {
						System.out.println("ERROR GETTING CHALLENGE RESPONSE "+e);
						e.printStackTrace();
						sessionSharedKey = null;
						challengeResponse = null;
					}
						
					return challengeResponse;
				}
			}

			return null;
		} catch(Exception e) {
			System.err.println("[GET-CHALLENGE] error getting challenge: "+e.getMessage());

			e.printStackTrace(System.err); // DEBUG
			return null;
		}
	}

	public boolean authenticate(String userName, String challengeResponse) {
		try {
			boolean authOK = false;
			Envelope message = null, response = null;

			message = new Envelope("AUTHENTICATE");
			message.addObject(userName);
			message.addObject(challengeResponse);
			message.encrypt(sessionSharedKey);
			message.setNonce(nonce-1);	
			output.writeObject(message);

			response = (Envelope)input.readObject();

			//System.out.println("AUTH RESPONSE encrypted: "+response.getMessage());
			response.decrypt(sessionSharedKey);
			//System.out.println("AUTH RESPONSE decrypted: "+response.getMessage());

			if(response.getMessage().equals("OK")) {
				ArrayList<Object> temp = null;
				temp = response.getObjContents();

				if(temp.size() == 1) {
					authOK = (Boolean)temp.get(0);
					return authOK;
				}
			}

			return false;
		} catch(Exception e) {
			System.err.println("[AUTHENTICATE] error authenticating: "+e.getMessage());

			e.printStackTrace(System.err); // DEBUG
			return false;
		}
	}


	 public UserToken getToken(String username)
	 {

		if(username == null || username.equals("")) {
			return null;
		}

		try
		{
			UserToken token = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("GET");
			message.addObject(username); //Add user name string
			message.encrypt(sessionSharedKey);
			message.setNonce(nonce-1);	
			output.writeObject(message);
		
			//Get the response from the server
			response = (Envelope)input.readObject();
			response.decrypt(sessionSharedKey);
			
			//Successful response
			if(response.getMessage().equals("OK"))
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = response.getObjContents();
				
				if(temp.size() == 1)
				{
					token = (UserToken)temp.get(0);
					return token;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }
	 
	  public UserToken getFSToken(String username, String fsaddress)
	 {

		if(username == null || username.equals("")) {
			return null;
		}

		try
		{
			UserToken token = null;
			Envelope message = null, response = null;
		 		 	
			//Tell the server to return a token.
			message = new Envelope("GETFS");
			message.addObject(username); //Add user name string
			message.addObject(fsaddress);
			message.encrypt(sessionSharedKey);
			message.setNonce(nonce-1);	
			output.writeObject(message);
		
			//Get the response from the server
			response = (Envelope)input.readObject();
			response.decrypt(sessionSharedKey);
			
			//Successful response
			if(response.getMessage().equals("OK"))
			{
				//If there is a token in the Envelope, return it 
				ArrayList<Object> temp = null;
				temp = response.getObjContents();
				
				if(temp.size() == 1)
				{
					token = (UserToken)temp.get(0);
					return token;
				}
			}
			
			return null;
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
		
	 }
	 
	 public boolean createUser(String username, UserToken token)
	 {
		 try
			{
				//System.out.println("WTF || createUser got "+username);//+" and req: "+token.getSubject());
				Envelope message = null, response = null;
				//Tell the server to create a user
				message = new Envelope("CUSER");
				message.addObject(username); //Add user name string
				message.addObject(token); //Add the requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message);
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteUser(String username, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
			 
				//Tell the server to delete a user
				message = new Envelope("DUSER");
				message.addObject(username); //Add user name
				message.addObject(token);  //Add requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message);
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean createGroup(String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to create a group
				message = new Envelope("CGROUP");
				message.addObject(groupname); //Add the group name string
				message.addObject(token); //Add the requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message); 
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteGroup(String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to delete a group
				message = new Envelope("DGROUP");
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message); 
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 @SuppressWarnings("unchecked")
	public List<String> listMembers(String group, UserToken token)
	 {
		 try
		 {
			 Envelope message = null, response = null;
			 //Tell the server to return the member list
			 message = new Envelope("LMEMBERS");
			 message.addObject(group); //Add group name string
			 message.addObject(token); //Add requester's token
			 message.encrypt(sessionSharedKey);
			 message.setNonce(nonce-1);	
			 output.writeObject(message); 
			 
			 response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
			 
			 //If server indicates success, return the member list
			 if(response.getMessage().equals("OK"))
			 { 
				//List<String> members = new ArrayList<String>();

				/* this is BS - I don't know what the hell is going on with this list in transit between
					the group server and the client, but the group server has the correct information, and when
					I pass the member list differently, it works just fine.  for some reason though the client
					never updates.  that said, parse a string instead and be happy for now */
				//members = (List<String>)response.getObjContents().get(0); //This cast creates compiler warnings. Sorry.
				String memberStr = (String)response.getObjContents().get(1);
				int serverCount = (Integer)response.getObjContents().get(2);

				String[] wtfMembers = memberStr.split("\\|\\|");
				/*for(String s : wtfMembers) {
					System.out.println("WTF: "+s);
				}*/

				ArrayList<String> members = new ArrayList(Arrays.asList(wtfMembers));

				System.out.println("DEBUG || server count: "+serverCount+" client member count: "+members.size());

				return members;
			 }
				
			 return null;
			 
		 }
		 catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return null;
			}
	 }
	 
	 public boolean addUserToGroup(String username, String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to add a user to the group
				message = new Envelope("AUSERTOGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message); 
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }
	 
	 public boolean deleteUserFromGroup(String username, String groupname, UserToken token)
	 {
		 try
			{
				Envelope message = null, response = null;
				//Tell the server to remove a user from the group
				message = new Envelope("RUSERFROMGROUP");
				message.addObject(username); //Add user name string
				message.addObject(groupname); //Add group name string
				message.addObject(token); //Add requester's token
				message.encrypt(sessionSharedKey);
				message.setNonce(nonce-1);	
				output.writeObject(message);
			
				response = (Envelope)input.readObject();
				response.decrypt(sessionSharedKey);
				//If server indicates success, return true
				if(response.getMessage().equals("OK"))
				{
					return true;
				}
				
				return false;
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
				return false;
			}
	 }

}
