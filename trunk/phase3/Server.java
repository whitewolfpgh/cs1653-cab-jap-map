import java.net.Socket;

import java.security.interfaces.*;
import java.util.Hashtable;

public abstract class Server {

	public static String DEFAULT_CERT_PATH = "certs/";
	protected int port;
	public String name;
	public Hashtable<String, RSAKey> keyPair;
	abstract void start();
	
	public Server(int _SERVER_PORT, String _serverName) {
		port = _SERVER_PORT;
		name = _serverName; 
		/* TODO make this more sophisticated so that it will return from disk if already generated
		 */
		//keyPair = MyCrypto.createKeyPair();
		try {
			keyPair = loadKeyPair();

			if(keyPair == null || keyPair.size() < 1) {
				System.out.println("Bad keypair for server '"+name+"' aborting...");
				System.exit(1);
			}
		} catch(Exception e) {
			System.out.println("Error generating server key pair for server '"+name+"' aborting... "+e);
			e.printStackTrace(); // DEBUG
			System.exit(1);
		}
	}

	private Hashtable<String, RSAKey> loadKeyPair() {
		Hashtable<String, RSAKey> keys = new Hashtable<String, RSAKey>();
		RSAPublicKey pubKey = null;
		RSAPrivateKey privKey = null;

		boolean loadedFromDisk = false;
		try {
			pubKey = MyCrypto.readPublicKeyFile("certs/"+name+"_public.key");
			privKey = MyCrypto.readPrivateKeyFile("certs/"+name+"_private.key");
			if(pubKey != null && privKey != null) {
				loadedFromDisk = true;
			}
		} catch(Exception e) {
			System.out.println("Server couldn't generate key pair from disk... "+e);
			//e.printStackTrace(); // DEBUG
		}

		if(loadedFromDisk) {
			System.out.println("Loaded key pair from disk for server '"+name+"'");
			keys.put("public_key", pubKey);
			keys.put("private_key", privKey);
			return keys;
		} else {
			System.out.println("Generating new key pair for server '"+name+"'");
			keys = MyCrypto.createKeyPair();

			pubKey = (RSAPublicKey)keys.get("public_key");
			privKey = (RSAPrivateKey)keys.get("private_key");
			if(pubKey == null || privKey == null) {
				return null;
			}

			boolean pubWritten = MyCrypto.writePublicKeyFile(pubKey, "certs/"+name+"_public.key");
			boolean privWritten = MyCrypto.writePrivateKeyFile(privKey, "certs/"+name+"_private.key");
			if(pubWritten && privWritten) {
				return keys;
			} else {
				return null;
			}
		}
	}
	
		
	public int getPort() {
		return port;
	}
	
	public String getName() {
		return name;
	}

	public RSAPublicKey getPublicKey() {
		return (RSAPublicKey)keyPair.get("public_key");
	}

	public String getPublicKeyString() {
		RSAPublicKey pubKey = getPublicKey();
		String ret = pubKey.getModulus().toString(16)+"\n"+pubKey.getPublicExponent().toString(16);
		return ret;
	}

	public RSAPrivateKey getPrivateKey() {
		return (RSAPrivateKey)keyPair.get("private_key");
	}

	public String getPrivateKeyString() {
		RSAPrivateKey privKey = getPrivateKey();
		String ret = privKey.getModulus().toString(16)+"\n"+privKey.getPrivateExponent().toString(16);
		return ret;
	}

	public RSAPublicKey getUserPublicKey(String path, String userName) {
		return MyCrypto.readPublicKeyFile(path+userName+"_public.key");
	}

	public boolean writeUserPublicKey(String path, String userName, RSAPublicKey userKey) {
		return MyCrypto.writePublicKeyFile(userKey, path+userName+"_public.key");
	}

	public String getSignature(String inString) {
		return MyCrypto.getSignature(inString, getPrivateKey());
	}

	public boolean verifyOwnSignature(String inString, String signature) {
		return MyCrypto.verifySignature(inString, signature, getPublicKey());
	}

	public boolean verifySignature(String inString, String signature, RSAPublicKey pubKey) {
		return MyCrypto.verifySignature(inString, signature, pubKey);
	}
}
