import java.util.List;
import java.io.*;

/**
 * Used throughout the system for user-related communications
 *
 */
public class Certificate implements UserCertificate, Serializable {
	String mPublicKey; // public key of the creator
	String mIssuer; // identifies the group server that created the cert 
	String mSubject; // name of the subject of the cert 

	/**
	 * @return Constructed Certificate object
	 */
	public Certificate(String _creatorPublicKey, String _issuerName, String _userName) {

		mPublicKey = _creatorPublicKey;
		mIssuer  = _issuerName;
		mSubject = _userName;
	}

	/**
	 * @return the public key of the creator
	 */
	public String getPublicKey() {
		return mPublicKey;
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
	 * convert into a certificate
	 */
	private void readObject(ObjectInputStream inStream) 
			throws IOException, ClassNotFoundException {
		mPublicKey = (String)inStream.readObject();
		mIssuer = (String)inStream.readObject();
		mSubject = (String)inStream.readObject();
	}

	/**
	 * serialize
	 */
	private void writeObject(ObjectOutputStream outStream) throws IOException {
		outStream.writeObject(mPublicKey);
		outStream.writeObject(mIssuer);
		outStream.writeObject(mSubject);
	}

	public String toString() {
		String outStr = "Issuer: "+getIssuer()+"\n";
		outStr += "Subject: "+getSubject()+"\n";
		outStr += "PubKey: "+getPublicKey()+"\n";

		return outStr;
	}
}
