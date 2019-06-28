package components;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Connection extends Thread {
	private Socket socket;
	private ObjectInputStream MyInput;
	private ObjectOutputStream MyOutput;
	private String ip;
	private ArrayList<Message> messageVector;
	private ArrayList<Connection> connectionVector;
	private Semaphore semaphore;

	public Connection(Socket socket, ArrayList<Message> messageVector, ArrayList<Connection> connectionVector,
			Semaphore semaphore) {
		super();
		try {
			this.socket = socket;
			this.MyOutput = new ObjectOutputStream(socket.getOutputStream());
			this.MyInput = new ObjectInputStream(socket.getInputStream());
			this.ip = socket.getInetAddress().toString().replaceAll("/", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.semaphore = semaphore;
		this.messageVector = messageVector;
		this.connectionVector = connectionVector;
	}

	public String getIp() {
		return this.ip;
	}

	public void sendMessage(Message msg) throws IOException {
		if (isConnected()) {
			this.MyOutput.writeObject(msg);
			this.MyOutput.flush();
		} else {
			System.out.println("add msg no messageVector");
			this.messageVector.add(msg);
		}
	}

	public void reconnect(Socket socket) throws IOException {
		this.socket = socket;
		this.MyOutput = new ObjectOutputStream(socket.getOutputStream());
		this.MyInput = new ObjectInputStream(socket.getInputStream());
	}

	public boolean isConnected() {
		return !socket.isClosed();
	}

	public void run() {
		while (true) {
			try {
				if (this.isConnected()) {
					Message msg = (Message) this.MyInput.readObject();
					if (msg.getStatus() == 0) { // pending
						msg.setStatus(1); // sent
						this.MyOutput.writeObject(msg);// confirmou recebimento pelo servidor para sender
						this.MyOutput.flush();
						// enviar pro reciver
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("status é 0");
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getReceiver())) {
								System.out.println("encontrado ip certo");
								connectionVector.get(i).sendMessage(msg);
								this.MyOutput.flush();
							}
						}
						semaphore.release();
					} else if (msg.getStatus() == 2) {
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getSender())) {
								connectionVector.get(i).sendMessage(msg);
								this.MyOutput.flush();
							}
						}
						semaphore.release();
					} else if (msg.getStatus() == 3) {
						this.MyOutput.writeObject(msg);// OK
						this.MyOutput.flush();
					} else if (msg.getStatus() == 4) {
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getReceiver())) {
								connectionVector.get(i).sendMessage(msg);
								this.MyOutput.flush();
							}
						}
						semaphore.release();
					} else {
						// erro?
					}
				} else {
					return;
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				try {
					this.MyInput.close();
					this.MyOutput.close();
					this.socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	}
}
