import java.util.List;
import java.io.Serializable;
import java.security.interfaces.*;

/**
 * Used throughout the system for user-related communications
 *
 */
public class Token implements UserToken, Serializable
{
	String mIssuer; // identifies the group server that created the token
	String mSubject; // name of the subject of the token
	List<String> mGroups; // list of groups that this token's owner has access to
	String mSignature; // signature of token
	String mFSAddress; //target fileserver

	/**
	 * @return Constructed Token object
	 */
	public Token(String _groupServerName, String _userName, List<String> _userGroups) {

		mIssuer  = _groupServerName;
		mSubject = _userName;
		mGroups = _userGroups;
		mFSAddress = null;
		mSignature = null;
	}
	
	public Token(String _groupServerName, String _userName, List<String> _userGroups, String _fsaddress) {

		mIssuer  = _groupServerName;
		mSubject = _userName;
		mGroups = _userGroups;
		mFSAddress = _fsaddress;
		mSignature = null;
	}

    /**
     * @return The issuer of this token
     */
    public String getIssuer() {
		return mIssuer;
	}
	
	 /**
     * @return The intended server of this FS-specific token (defaults to null for regular tokens)
     */
    public String getFSAddress() {
		return mFSAddress;
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
		for(String s : mGroups) {
			System.out.println("'"+mSubject+"' group: "+s);
		}
		return mGroups;
	}

	public String getSignature() {
		return mSignature;
	}

	public String toString() {
		String ret = "TOKEN||";
		ret += mIssuer +"||";
		ret += mSubject +"||";

		// TODO sort groups 
		for(String grp : getGroups()) {
			ret +=  grp+"||";
		}

		return ret;
	}

	public void sign(RSAPrivateKey signerKey) {
		mSignature = MyCrypto.getSignature(toString(), signerKey);
	}

	public boolean verifySignature(RSAPublicKey signerKey) {
		return MyCrypto.verifySignature(toString(), mSignature, signerKey);
	}


}
