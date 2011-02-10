
public class ServerMaster {

	public static void main(String[] args) {

		GroupServer groupServerInstance = new GroupServer();
		FileServer fileServerInstance = new FileServer();


		groupServerInstance.start();
		fileServerInstance.start();
	}
}
