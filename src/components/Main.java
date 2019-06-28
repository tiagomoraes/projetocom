package components;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Main {
	public static void main(String[] args) throws IOException {
		int port = 3001;
		@SuppressWarnings("resource")
		ServerSocket tmpsocket = new ServerSocket(port);
		ArrayList <Message> messageVector = new ArrayList<Message>();
		ArrayList<Connection> connectionVector = new ArrayList<Connection>();
		Semaphore semaphore = new Semaphore(1);
		//MessageQueue messageQueue = new MessageQueue(messageVector,connectionVector,semaphore);
		//messageQueue.start();
		boolean hasAlreadyConnected;

		while (true) {
			hasAlreadyConnected = false;
			Connection connection = new Connection(tmpsocket.accept(), messageVector, connectionVector, semaphore);
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int x = 0; x < connectionVector.size(); x++) {
				if (connection.getIp().equals(connectionVector.get(x).getIp())) {
					hasAlreadyConnected = true;
					connectionVector.get(x).reconnect(tmpsocket.accept());
					for (int i = 0; i < messageVector.size(); i++) {
						for (int j = 0; j < connectionVector.size(); j++) {
							try {
								semaphore.acquire();
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							if (connectionVector.get(j).isConnected()
									&& messageVector.get(i).getReceiver().equals(connectionVector.get(j).getIp())) {

								try {
									connectionVector.get(j).sendMessage(messageVector.get(i));
									messageVector.remove(messageVector.get(i));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						semaphore.release();
					}
				}
			}

			if (!hasAlreadyConnected) {
				connectionVector.add(connection);
			}
			semaphore.release();
			connection.start();
			System.out.println(connectionVector.size());

		}
	}
}
