package components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MessageQueue extends Thread {
	private ArrayList<Message> messageVector = new ArrayList<Message>();
	private ArrayList<Connection> connectionVector = new ArrayList<Connection>();
	private Semaphore semaphore;

	public MessageQueue(ArrayList<Message> messageVector, ArrayList<Connection> connectionVector,
			Semaphore semaphore) {
		super();
		this.messageVector = messageVector;
		this.connectionVector = connectionVector;
		this.semaphore = semaphore;
	}

	public void run() {
		while (true) {
			for (int i = 0; i < messageVector.size(); i++) {
				for (int j = 0; j < connectionVector.size(); j++) {
					try {
						this.semaphore.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if (connectionVector.get(j).isConnected()
							&& this.messageVector.get(i).getReceiver().equals(connectionVector.get(j).getIp())) {

						try {
							connectionVector.get(j).sendMessage(messageVector.get(i));
							messageVector.remove(messageVector.get(i));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				this.semaphore.release();
			}
		}
	}

}
