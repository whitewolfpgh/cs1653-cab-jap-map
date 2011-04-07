
import java.util.List;

/**
 * A simple interface to the cert data structure that will be
 * returned by a group server.  
 */
public interface UserCertificate
{
	/**
	 * Return the public key of the issuer
	 *
	 */
	public String getPublicKey();

    /**
     * This method should return a string describing the issuer of
     * this cert.  This string identifies the group server that
     * created this cert.  For instance, if "Alice" requests a cert 
     * from the group server "Server1", this method will return the
     * string "Server1".
     *
     * @return The issuer of this certificate
     *
     */
    public String getIssuer();


    /**
     * This method should return a string indicating the name of the
     * subject of the cert.  For instance, if "Alice" requests a
     * cert from the group server "Server1", this method will return
     * the string "Alice".
     *
     * @return The subject of this certificate
     *
     */
    public String getSubject();

}   //-- end interface UserCertificate
