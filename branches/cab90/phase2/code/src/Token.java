import java.util.List;

/**
 * Used throughout the system for user-related communications
 *
 */
public class Token implements UserToken
{
	String mIssuer; // identifies the group server that created the token
	String mSubject; // name of the subject of the token
	List<String> mGroups; // list of groups that this token's owner has access to

	/**
	 * @return Constructed Token object
	 */
	public Token(String _groupServerName, String _userName, List<String> _userGroups) {

		mIssuer  = _groupServerName;
		mSubject = _userName;
		mGroups = _userGroups;
	}

    /**
     * @return The issuer of this token
     */
    public String getIssuer() {
		return mIssuer;
	}


    /**
     * @return The subject of this token
     */
    public String getSubject() {
		return mSubject;
	}


    /**
     * @return The list of group memberships encoded in this token
     */
    public List<String> getGroups() {
		return mGroups;
	}

}
