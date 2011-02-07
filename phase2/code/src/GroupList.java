/* This list represents the groups on the server */
/* TODO: This is currently a rough mirror of UserList.java. It needs to be updated to be relevant to Groups. */
import java.util.*;


	public class GroupList implements java.io.Serializable {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 7600343803563417992L;
		private Hashtable<String, Group> list = new Hashtable<String, Group>();
		
		public synchronized void addGroup(String groupname)
		{
			Group newGroup = new Group();
			list.put(groupname, newGroup);
		}
		
		public synchronized void deleteGroup(String groupname)
		{
			list.remove(groupname);
		}
		
		public synchronized boolean checkUser(String groupname)
		{
			if(list.containsKey(groupname))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		public synchronized ArrayList<String> getUserGroups(String username)
		{
			return list.get(username).getGroups();
		}
		
		public synchronized ArrayList<String> getUserOwnership(String username)
		{
			return list.get(username).getOwnership();
		}
		
		public synchronized void addMember(String user, String groupname)
		{
			list.get(user).addMember(groupname);
		}
		
		public synchronized void removeMember(String user, String groupname)
		{
			list.get(user).removeMember(groupname);
		}
		
		public synchronized void addOwnership(String user, String groupname)
		{
			list.get(user).addOwnership(groupname);
		}
		
		public synchronized void removeOwnership(String user, String groupname)
		{
			list.get(user).removeOwnership(groupname);
		}
		
	
	class Group implements java.io.Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6699986336399821598L;
		private ArrayList<String> members;
		private ArrayList<String> ownership;
		
		public Group()
		{
			members = new ArrayList<String>();
			ownership = new ArrayList<String>();
		}
		
		public ArrayList<String> getMembers()
		{
			return members;
		}
		
		public ArrayList<String> getOwnership()
		{
			return ownership;
		}
		
		public void addMember(String user)
		{
			members.add(user);
		}
		
		public void removeMember(String user)
		{
			if(!members.isEmpty())
			{
				if(members.contains(user))
				{
					members.remove(members.indexOf(user));
				}
			}
		}
		
		public void addOwnership(String user)
		{
			ownership.add(user);
		}
		
		public void removeOwnership(String user)
		{
			if(!ownership.isEmpty())
			{
				if(ownership.contains(user))
				{
					ownership.remove(ownership.indexOf(user));
				}
			}
		}
		
	}
	
}	
