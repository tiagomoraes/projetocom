package components;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) throws IOException {
		int port = 3001;
		ServerSocket tmpsocket = new ServerSocket(port);
		ArrayList<Connection> connectionVector = null;

		while (true) {
			int index = 0;
			Connection connection = new Connection(tmpsocket.accept(), connectionVector);
			connectionVector.add(index, connection);
			connection.start();
		}
	}
}
