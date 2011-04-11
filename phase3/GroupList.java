// Author: brack
// Comments:
// 		- still needs to be tested
//

/* This list represents the groups on the server */
import java.util.*;
import java.security.*;
import java.security.Security;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

	public class GroupList implements java.io.Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 7600343803563417981L; // last 2 digits changed from UserList
		private Hashtable<String, Group> groupList = new Hashtable<String, Group>(); // group name, group object
		
		public synchronized void addGroup(String groupName, String creator) {
			Group newGroup = new Group();
			newGroup.addCreator(creator);
			newGroup.loadKeychain(groupName);
			groupList.put(groupName, newGroup);
			addGroupMember(groupName, creator);
		}
	
		// note: make sure at a higher level admin/creator is the one deleting!!
		public synchronized void deleteGroup(String groupName) {
			groupList.remove(groupName);
		}
	
		// does group exist? if so, true; otherwise false
		public synchronized boolean checkGroup(String groupName) {
			if(groupList.containsKey(groupName)) { 
				return true; 
			}
			else { 
				return false; 
			}
		}

		/* add member token to a group */
		public synchronized boolean addGroupMember(String groupName, String member) {
			if(groupList.get(groupName).getMembers().contains(member)) {
				System.out.println("User can't be added. '"+member+"' already exists in group '"+groupName+"'");
				return false;
			}
		
			try {
				groupList.get(groupName).addMember(member);
				//addNewGroupKey(groupName);
				return true;
			} catch(Exception e) {
				System.out.println("Error occurred adding member to group.");
				return false;
			}
		}

		public synchronized void removeMember(String groupName, String userName) {
			Group grp = groupList.get(groupName);

			/* if user is group owner, delete the group */
			if(userName.equals(grp.getCreator())) {
				System.out.println("DEBUG || removeMember- deleting group ["+groupName+"] b/c owner ["+userName+"] is being removed from grp");
				deleteGroup(groupName);
			} else {
				System.out.println("DEBUG || removeMember- removed user ["+userName+"] from group ["+groupName+"]");
				grp.removeMember(userName);
				//addNewGroupKey(groupName);
			}
		}
		
		/* list the members of a group. 
		 * 	chose to identify members by token, but for compatibility with
		 * 	other components, the Group object has a utility function to list 
		 * 	members as a string array of user names 
		 */
		public synchronized ArrayList<String> getGroupMembers(String groupName) {
			return groupList.get(groupName).getMembers();
		}

		public synchronized boolean userIsMember(String groupName, String userName) {
			return groupList.get(groupName).userIsMember(userName);
		}
	
		/* return the creator of the group */
		public synchronized String getGroupOwner(String groupName) {
			return groupList.get(groupName).getCreator();
		}
		
		public synchronized void setCreator(String groupName, String creator) {
			groupList.get(groupName).addCreator(creator);
		}
		
		public synchronized void demoteCreator(String groupName, String creator) {
			groupList.get(groupName).removeCreator(creator);
		}

		public synchronized Hashtable<Integer, String> getGroupKeychain(String groupName) {
			return groupList.get(groupName).getKeychain();
		}

		public synchronized boolean addNewGroupKey(String groupName) {
			Group g = groupList.get(groupName);
			g.addGroupKey(groupName);

			int keyId = g.getLatestGroupKeyId();
			String keyHex = g.getLatestGroupKeyString();
			System.out.println("[GROUPLIST-CREATE-KEY] got key with id ["+keyId+"] and key: \n"+keyHex+"\n");

			if(keyId == 0 || keyHex == null || keyHex.equals("")) {
				return false;
			} else {
				return true;
			}
		}

		public synchronized SecretKey getCurrentGroupKey(String groupName) {
			return groupList.get(groupName).getLatestGroupKey();
		}

		public synchronized String getCurrentGroupKeyString(String groupName) {
			return groupList.get(groupName).getLatestGroupKeyString();
		}

		public synchronized int getCurrentGroupKeyId(String groupName) {
			return groupList.get(groupName).getLatestGroupKeyId();
		}

		

	class Group implements java.io.Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6699986336399821581L; // last 2 digits changed...
		private ArrayList<String> members; // member users in group
		private String creatorName; // id of the group creator/owner
		private String demotedCreatorName; // id of the OLD group creator/owner

		private Hashtable<Integer, String> keychain; // keychain for group, <key_id, key_hex>
		
		public Group() {
			members = new ArrayList<String>();
			creatorName = null;
			demotedCreatorName = null;
			keychain = null;
		}
		
		public ArrayList<String> getMembers() { return members; }
		public String getCreator() { return creatorName; }
		public Hashtable<Integer, String> getKeychain() { 
			System.out.println("keychain looks like: "+keychain.size());
			System.out.println("key is: "+keychain.get(keychain.size()));
			return keychain; 
		}
		
		public void addMember(String user) {
			members.add(user);
		}

		public void removeMember(String user) {
			if(!members.isEmpty()) {
				if(members.contains(user)) {
					members.remove(members.indexOf(user));
				}
			}
		}

		public boolean userIsMember(String userName) {
			if(members.isEmpty() || !members.contains(userName)) {
				return false;
			} else if(members.contains(userName)) {
				return true;
			}

			return false;
		}


		public void addCreator(String creator) {
			creatorName = creator;
		}

		public void removeCreator(String demotedCreator) {
			if(creatorName != null) {
				creatorName = null;
				demotedCreatorName = demotedCreator;
			}
		}

		public String generateGroupKey() {
			return MyCrypto.readDESKeyAsString(MyCrypto.generateDESKey());
		}

		public String getLatestGroupKeyString() {
			return keychain.get(keychain.size());
		}

		public SecretKey getLatestGroupKey() {
			return MyCrypto.readDESString(getLatestGroupKeyString());
		}

		public int getLatestGroupKeyId() {
			return keychain.size();
		}

		public void loadKeychain(String groupName) {
			if(!MyCrypto.groupKeychainExists(groupName)) {
				String newKey = generateGroupKey();
				boolean added = MyCrypto.addToGroupKeychain(groupName, 1, newKey, false);
				System.out.println("EASKDHJALSKDJHLSADH");

				if(added) {
					System.out.println("New group key created for '"+groupName+"'");
					keychain = new Hashtable<Integer, String>();
					keychain.put(1, newKey);
				} else {
					System.out.println("New group key _NOT_ created for '"+groupName+"'");
				}
			} else {
				keychain = MyCrypto.getGroupSharedKeychain(groupName);
				System.out.println("Loaded "+keychain.size()+" keys from disk for group '"+groupName+"'");
			}
		}

		public void addGroupKey(String groupName) {
			String newKey = generateGroupKey();
			int newId = keychain.size()+1;
			boolean added = MyCrypto.addToGroupKeychain(groupName, newId, newKey, true);

			if(added) {
				System.out.println("New group key created for '"+groupName+"'");
				keychain = new Hashtable<Integer, String>();
				keychain.put(newId, newKey);
			} else {
				System.out.println("New group key _NOT_ created for '"+groupName+"'");
			}
		}
	}
	
}	
