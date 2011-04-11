// Author: brack
// Comments:
// 		- still needs to be tested
//

/* This list represents the groups on the server */
import java.util.*;

	public class GroupList implements java.io.Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 7600343803563417981L; // last 2 digits changed from UserList
		private Hashtable<String, Group> groupList = new Hashtable<String, Group>(); // group name, group object
		
		public synchronized void addGroup(String groupName, String creator) {
			Group newGroup = new Group();
			newGroup.addCreator(creator);
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
		public synchronized void addGroupMember(String groupName, String member) {
			groupList.get(groupName).addMember(member);
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
		

	class Group implements java.io.Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6699986336399821581L; // last 2 digits changed...
		private ArrayList<String> members; // member users in group
		private String creatorName; // id of the group creator/owner
		private String demotedCreatorName; // id of the OLD group creator/owner
		
		public Group() {
			members = new ArrayList<String>();
			creatorName = null;
			demotedCreatorName = null;
		}
		
		public ArrayList<String> getMembers() { return members; }
		public String getCreator() { return creatorName; }
		
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

		public void addCreator(String creator) {
			creatorName = creator;
		}

		public void removeCreator(String demotedCreator) {
			if(creatorName != null) {
				creatorName = null;
				demotedCreatorName = demotedCreator;
			}
		}
		
	}
	
}	
