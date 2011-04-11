import java.util.ArrayList;

import java.security.*;
import java.security.Security;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Envelope implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7726335089122193103L;
	private String msg;
	private int nonce;
	private ArrayList<Object> objContents = new ArrayList<Object>();
	
	public Envelope(String text)
	{
		msg = text;
	}
	
	public String getMessage()
	{
		return msg;
	}
	
	public int getNonce()
	{
		return nonce;
	}
	
	public void setNonce(int n)
	{
		nonce = n;
	}
	
	public ArrayList<Object> getObjContents()
	{
		return objContents;
	}
	
	public void addObject(Object object)
	{
		objContents.add(object);
	}

	public String toString() {
		String outStr = "Envelope||";
		outStr += serialVersionUID +"||";
		outStr += msg +"||";
		for(Object o : getObjContents()) {
			outStr +=  o+"||";
		}

		return outStr;
	}

	public void encrypt(SecretKey s) {
		msg = MyCrypto.encryptStringWithSecretKey(msg, s);
	}

	public void decrypt(SecretKey s) {
		msg = MyCrypto.decryptStringWithSecretKey(msg, s);
	}
}
