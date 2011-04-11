import java.net.Socket;
import java.io.*;

import java.security.interfaces.*;
import java.util.Hashtable;

public abstract class Server {

	public static String DEFAULT_CERT_PATH = "server_keys/";
	protected int port;
	public String name;
	public Hashtable<String, RSAKey> keyPair;
	abstract void start();
	
	public Server(int _SERVER_PORT, String _serverName) {
		port = _SERVER_PORT;
		name = _serverName; 

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

	/** load a key pair from disk, or create and save a new pair
	 *
	 *
	 */
	private Hashtable<String, RSAKey> loadKeyPair() {
		Hashtable<String, RSAKey> keys = new Hashtable<String, RSAKey>();
		RSAPublicKey pubKey = null;
		RSAPrivateKey privKey = null;

		boolean loadedFromDisk = false;
		try {
			pubKey = MyCrypto.readPublicKeyFile(DEFAULT_CERT_PATH+name+"_public.key");
			privKey = MyCrypto.readPrivateKeyFile(DEFAULT_CERT_PATH+name+"_private.key");
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

			File f = new File(DEFAULT_CERT_PATH);
			boolean dirCreated = false;

			if(f.exists()) {
				dirCreated = true;
			} else {
				dirCreated = f.mkdir();
				System.out.println("Creating server key directory at "+DEFAULT_CERT_PATH);
			}

			if(dirCreated) {
				boolean pubWritten = MyCrypto.writePublicKeyFile(pubKey, DEFAULT_CERT_PATH+name+"_public.key");
				boolean privWritten = MyCrypto.writePrivateKeyFile(privKey, DEFAULT_CERT_PATH+name+"_private.key");
				if(pubWritten && privWritten) {
					System.out.println("Generated server public and private keys in "+DEFAULT_CERT_PATH);
					return keys;
				} else {
					return null;
				}
			} else {
				System.out.println("UNABLE TO ACCESS/CREATE DIRECTORY AT "+DEFAULT_CERT_PATH);
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
