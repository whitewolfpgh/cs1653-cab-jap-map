import java.net.Socket;
import java.net.InetAddress;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import java.security.*;
import java.security.Security;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public abstract class Client {

	/* protected keyword is like private but subclasses have access
	 * Socket and input/output streams
	 */
	protected Socket sock;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;

	protected SecretKey sessionSharedKey;

    public Certificate getServerIdentity(String userName) {
        try {
            Certificate cert = null;
            Envelope message = null, response = null;

            message = new Envelope("IDENTIFY");
            message.addObject(userName);
            output.writeObject(message);

            response = (Envelope)input.readObject();

            if(response.getMessage().equals("OK")) {
                ArrayList<Object> temp = null;
                temp = response.getObjContents();

                if(temp.size() == 1) {
                    cert = (Certificate)temp.get(0);
                    return cert;
                }
            }

            return null;
        } catch(Exception e) {
            System.err.println("Error: "+e.getMessage());

            e.printStackTrace(System.err);
            return null;
        }
    }

	public boolean connect(final String server, final int port) {
		//System.out.println("attempting to connect");

		/* connect to the server, set up the server i/o and return true
		 * if there's an exception, print it out and return false
		 *
		 * -brack
		 */
		try {
			InetAddress addr = InetAddress.getByName(server);
			sock = new Socket(addr, port);

			output = new ObjectOutputStream(sock.getOutputStream());
			input = new ObjectInputStream(sock.getInputStream());

			System.out.println("Connected to host ["+server+"] on port ["+port+"]");
		} catch(Exception e) {
			System.err.println("Error connecting to host ["+server+"]:["+port+"] -- "+e);
			e.printStackTrace(System.err);

			return false;
		}

		return true;

	}

	public boolean isConnected() {
		if (sock == null || !sock.isConnected()) {
			return false;
		}
		else {
			return true;
		}
	}

	public void disconnect()	 {
		if (isConnected()) {
			try
			{
				Envelope message = new Envelope("DISCONNECT");
				output.writeObject(message);
			}
			catch(Exception e)
			{
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}
}
