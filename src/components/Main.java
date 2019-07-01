package components;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {
	public static void main(String[] args) throws IOException {
		int port = 3001;
		@SuppressWarnings("resource")
		ServerSocket tmpsocket = new ServerSocket(port);
		ArrayList<Connection> connectionVector = new ArrayList<Connection>();
		Semaphore semaphore = new Semaphore(1);
		boolean hasAlreadyConnected;

		while (true) {
			hasAlreadyConnected = false;
			Socket sock = tmpsocket.accept();
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < connectionVector.size(); i++) {
				if (sock.getInetAddress().toString().replaceAll("/", "").equals(connectionVector.get(i).getIp())) {
					hasAlreadyConnected = true;
					connectionVector.set(i, new Connection(sock, connectionVector.get(i).getMessageVector(),
							connectionVector, semaphore));
					connectionVector.get(i).sendMessagesOnReconnect();
					connectionVector.get(i).start();
				}
			}

			if (!hasAlreadyConnected) {
				Connection connection = new Connection(sock, connectionVector, semaphore);
				connectionVector.add(connection);
				connection.start();
			}
			semaphore.release();

		}
	}
}
