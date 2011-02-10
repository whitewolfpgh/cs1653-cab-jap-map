import java.net.Socket;
import java.net.InetAddress;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public abstract class Client {

	/* protected keyword is like private but subclasses have access
	 * Socket and input/output streams
	 */
	protected Socket sock;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;

	public boolean connect(final String server, final int port) {
		System.out.println("attempting to connect");

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
