import java.util.Hashtable;
import java.security.*;
import java.security.Security;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import java.io.*;
import java.math.BigInteger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class MyCrypto
{

	static Provider PROVIDER_BC = null;
	static SecureRandom PRNG = null;
	static SecureRandom sessionID = null;

	public static Provider getProvider() {
		if(PROVIDER_BC == null) {
			PROVIDER_BC = new BouncyCastleProvider();
			Security.insertProviderAt(PROVIDER_BC, 1);
		}

		return PROVIDER_BC;
	}

	public static SecureRandom getPRNG() {
		try {
			if(PRNG == null) {
				getProvider();
				PRNG = SecureRandom.getInstance("SHA1PRNG");
				PRNG.nextInt();
			}
		} catch(Exception e) {
			System.out.println("[GET-PRNG] unable to create PRNG... ");
			e.printStackTrace(); // DEBUG
		}

		return PRNG;
	}

	public static SecureRandom getSessionID() {
		try 
		{
				getProvider();
				sessionID = SecureRandom.getInstance("SHA1PRNG");
				sessionID.nextInt();
		} catch(Exception e) {
			System.out.println("[GET-SESSIONID] unable to create SessionID... ");
			e.printStackTrace(); // DEBUG
		}

		System.out.println("Generating new sessionID...");
		return sessionID;
	}
	
	public static void ourHMAC(){
		
		String mykey = "secret";
		String test = "test";
		try {
		    Mac mac = Mac.getInstance("HmacSHA1");
		    SecretKeySpec secret = new SecretKeySpec(mykey.getBytes(),"HmacSHA1");
		    mac.init(secret);
		    byte[] digest = mac.doFinal(test.getBytes());
		    //String enc = new String(digest);
		    //System.out.println(enc); 
			for (byte b : digest) {
				System.out.format("%02x", b);
			}
			System.out.println();
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}
		

	}
	
	public static Hashtable<String, RSAKey> createKeyPair() {
		Hashtable<String, RSAKey> ret = new Hashtable<String, RSAKey>();
		// using an RSA-2048 key generator, generate a pair of keys
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", getProvider());
			keyGen.initialize(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)); // 65537

			KeyPair kp = keyGen.generateKeyPair();
			RSAPrivateKey privKey = (RSAPrivateKey)kp.getPrivate();
			RSAPublicKey pubKey = (RSAPublicKey)kp.getPublic();

			if(privKey != null && pubKey != null) {
				ret.put("public_key", pubKey);
				ret.put("private_key", privKey);
			} else {
				throw new Exception("bad public/private key pair.");
			}
		} catch(Exception e) {
			System.out.println("Couldn't create key pair... "+e);
			e.printStackTrace(); // DEBUG
		}

		return ret;
	}

	public static String encryptString(String inString, RSAPublicKey pubKey) {
		try {
			//System.out.println("Encrypting string "+inString);
			Cipher eCipher = Cipher.getInstance("RSA", getProvider());
			eCipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] eCipherBytes = eCipher.doFinal(inString.getBytes());
			String eCipherHex = hexify(eCipherBytes);
			/*System.out.println("************** ENCRYPTED STRING ***************\n"+eCipherHex);
			System.out.println("***********************************************\n");*/

			return eCipherHex;
		} catch(Exception e) {
			System.out.println("Couldn't create key pair... "+e);
			e.printStackTrace(); // DEBUG
		}

		return null;
	}

	public static String decryptString(String inString, RSAPrivateKey privKey) {
		try {
			//System.out.println("Decrypting string "+inString);
			// decrypt using private key and output plaintext
			Cipher dCipher = Cipher.getInstance("RSA", getProvider());
			dCipher.init(Cipher.DECRYPT_MODE, privKey);
			byte[] dCipherBytes = dCipher.doFinal(unHexify(inString));
			String dPlaintext = new String(dCipherBytes);
			/*System.out.println("************** DECRYPTED STRING ***************\n"+dPlaintext);
			System.out.println("***********************************************\n");*/

			return dPlaintext;
		} catch(Exception e) {
			System.out.println("Couldn't decrypt string ... "+e);
			e.printStackTrace(); // DEBUG
		}

		return null;
	}

	public static String getSignature(String inString, RSAPrivateKey privKey) {
		try {
			/*RSAPrivateKeySpec privSpec = new RSAPrivateKeySpec(privKey.getModulus(), privKey.getPrivateExponent());
			KeyFactory privKF = KeyFactory.getInstance("RSA", getProvider());
			PrivateKey privPK = privKF.generatePrivate(privSpec);*/

			// sign using priv key
			Signature sig = Signature.getInstance("MD5withRSA", getProvider());
			sig.initSign(privKey);
			sig.update(inString.getBytes());

			byte[] sigBytes = sig.sign();
			String sigHex = hexify(sigBytes);
			/* DEBUG
			System.out.println("************** SIGNATURE FOR STRING ***************\n"+sigHex);
			System.out.println("***********************************************\n");
			*/

			return sigHex;
		} catch(Exception e) {
			System.out.println("Couldn't create signature... "+e);
			e.printStackTrace(); // DEBUG
		}

		return null;
	}

	public static boolean verifySignature(String inString, String signature, RSAPublicKey pubKey) {
		try {
			/*RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent());
			KeyFactory pubKF = KeyFactory.getInstance("RSA", getProvider());
			PublicKey pubPK = pubKF.generatePublic(pubSpec);*/

			// verify using pub key
			Signature verifier = Signature.getInstance("MD5withRSA", getProvider());
			verifier.initVerify(pubKey);
			verifier.update(inString.getBytes());
			//verifier.update(unHexify(inString));

			boolean verified = verifier.verify(unHexify(signature));
			/* DEBUG
			if(verified) {
				System.out.println("************** SIGNATURE VERIFIED FOR STRING ***************\n");
			} else {
				System.out.println("************** SIGNATURE _NOT_ VERIFIED FOR STRING ***************\n");
			}
			*/

			return verified;
		} catch(Exception e) {
			System.out.println("Couldn't verify signature... "+e);
			e.printStackTrace(); // DEBUG
		}

		return false;
	}

	public static SecretKey generateSecretKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");//, getProvider());
			keyGen.init(128);

			// create a secret key spec from the generated key
			SecretKey secretKey = keyGen.generateKey();
			//byte[] secretBytes = secret.getEncoded();
			//SecretKeySpec secretSpec = new SecretKeySpec(secretBytes, "AES");

			return secretKey;
		} catch(Exception e) {
			System.out.println("Couldn't generate secret key..."+e);
			return null;
		}
	}

	public static String generateSecretKeyString(SecretKey secretKey) {
		try {
			return hexify(secretKey.getEncoded());
		} catch(Exception e) {
			System.out.println("Couldn't generate string from secret key... "+e);
			return null;
		}
	}

	public static SecretKey readSecretKeyString(String key) {
		try {
			SecretKeySpec secretSpec = new SecretKeySpec(unHexify(key), "AES");
			SecretKeyFactory secretFactory = SecretKeyFactory.getInstance("AES");//, getProvider());
			SecretKey retSecretKey = secretFactory.generateSecret(secretSpec);
			return retSecretKey;
		} catch(Exception e) {
			System.out.println("Couldn't read secret key from string... "+e);
			return null;
		}
	}


	/** encrypt using secret key
	 * 
	 * given plaintext and a secret key, return hex ciphertext
	 *
	 */
	public static String encryptStringWithSecretKey(String plaintext, SecretKey secretKey) {
		try {
			// get AES cipher
			Cipher cipher = Cipher.getInstance("AES");//, getProvider());

			// encrypt the input string using the cipher in encrypt mode
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
			String encryptedString = hexify(encryptedBytes);
			/* DEBUG
			System.out.println("************** ENCRYPTED STRING ***************\n"+encryptedString);
			System.out.println("***********************************************\n");
			*/

			return encryptedString;
		} catch(Exception e) {
			System.out.println("Couldn't encrypt string using secret key... "+e);
			return null;
		}
	}

	/** decrypt using secret key
	 *
	 * given hex ciphertext and secret key, return plaintext
	 *
	 */
	public static String decryptStringWithSecretKey(String ciphertext, SecretKey secretKey) {
		try {
			// get AES cipher
			Cipher cipher = Cipher.getInstance("AES");//, getProvider());

			// now decrypt the encrypted bytes using the cipher in decrypt mode
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedBytes = cipher.doFinal(unHexify(ciphertext));
			String decryptedString = new String(decryptedBytes);
			/* DEBUG
			System.out.println("************** DECRYPTED STRING ***************\n"+decryptedString);
			System.out.println("***********************************************\n");
			*/

			return decryptedString;
		} catch(Exception e) {
			System.out.println("Couldn't decrypt string using secret key... "+e);
			return null;
		}
	}


    // turn a byte array into a hex string
    public static String hexify(byte bytes[]) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);

        for(int i=0; i < bytes.length; i++) {
            if(((int)bytes[i] & 0xff) < 0x10) {
                sb.append("0");
            }

            sb.append(Long.toString((int)bytes[i] & 0xff, 16));
        }

        return sb.toString();
    } 
	// turns a hex string into a byte array
	public static byte[] unHexify(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}

		return data;
	}

	// TODO: compare keys
	//

	public static RSAPublicKey readPublicKeyString(String key) {
		try {
			String[] parts = key.split("\n");

			if(parts.length < 2) {
				return null;
			} else {
				BigInteger keyModulus = new BigInteger(parts[0], 16);
				BigInteger keyExponent = new BigInteger(parts[1], 16);

				RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(keyModulus, keyExponent);

				KeyFactory keyFactory = KeyFactory.getInstance("RSA", getProvider());
				RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(publicSpec);
				return publicKey;
			}
		} catch(Exception e) {
			System.out.println("Couldn't generate key from string. "+e);
			return null;
		}
	}

	public static RSAPrivateKey readPrivateKeyString(String key) {
		try {
			String[] parts = key.split("\n");

			if(parts.length < 2) {
				return null;
			} else {
				BigInteger keyModulus = new BigInteger(parts[0], 16);
				BigInteger keyExponent = new BigInteger(parts[1], 16);

				RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(keyModulus, keyExponent);

				KeyFactory keyFactory = KeyFactory.getInstance("RSA", getProvider());
				RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateSpec);
				return privateKey;
			}
		} catch(Exception e) {
			System.out.println("Couldn't generate key from string. "+e);
			return null;
		}
	}


	public static RSAPublicKey readPublicKeyFile(String fileName) {
		try {
			BufferedReader keyIn = new BufferedReader(new FileReader(fileName));
			BigInteger keyModulus = new BigInteger(keyIn.readLine(), 16);
			BigInteger keyExponent = new BigInteger(keyIn.readLine(), 16);

			keyIn.close();

			RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(keyModulus, keyExponent);

			KeyFactory keyFactory = null;
			RSAPublicKey publicKey = null;

			keyFactory = KeyFactory.getInstance("RSA", getProvider());
			publicKey = (RSAPublicKey)keyFactory.generatePublic(publicSpec);

			//System.out.println("read public key from ["+fileName+"] got modulus ["+keyModulus+"] exponent ["+keyExponent+"]");

			return publicKey;
		} catch(Exception e) {
			System.out.println("couldn't generate public key from file. "+e);
			return null;
		}
	}

	public static RSAPrivateKey readPrivateKeyFile(String fileName) {
		try {
			BufferedReader keyIn = new BufferedReader(new FileReader(fileName));
			BigInteger keyModulus = new BigInteger(keyIn.readLine(), 16);
			BigInteger keyExponent = new BigInteger(keyIn.readLine(), 16);

			keyIn.close();

			RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(keyModulus, keyExponent);

			KeyFactory keyFactory = null;
			RSAPrivateKey privateKey = null;

			keyFactory = KeyFactory.getInstance("RSA", getProvider());
			privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateSpec);

			//System.out.println("read private key from ["+fileName+"] got modulus ["+keyModulus+"] exponent ["+keyExponent+"]");

			return privateKey;
		} catch(Exception e) {
			System.out.println("couldn't generate key. "+e);
			return null;
		}
	}

	public static boolean writePublicKeyFile(RSAPublicKey publicKey, String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(publicKey.getModulus().toString(16).toUpperCase());
			fw.write("\n");
			fw.write(publicKey.getPublicExponent().toString(16).toUpperCase());
			fw.close();

			File f = new File(fileName);
			if(f.exists()) {
				return true;
			} else {
				System.out.println("file ["+fileName+"] doesnt exist... failure.");
				return false;
			}
		} catch(Exception e) {
			System.out.println("unable to write public key file: "+e);
			return false;
		}
	}

	public static boolean writePrivateKeyFile(RSAPrivateKey privateKey, String fileName) {
		try {
			FileWriter fw = new FileWriter(fileName);
			fw.write(privateKey.getModulus().toString(16).toUpperCase());
			fw.write("\n");
			fw.write(privateKey.getPrivateExponent().toString(16).toUpperCase());
			fw.close();

			File f = new File(fileName);
			if(f.exists()) {
				return true;
			} else {
				System.out.println("file ["+fileName+"] doesnt exist... failure.");
				return false;
			}
		} catch(Exception e) {
			System.out.println("unable to write private key file: "+e);
			return false;
		}
	}
}
