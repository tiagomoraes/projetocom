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
	private ArrayList<Message> statusFour;
	private ArrayList<Connection> connectionVector;
	private Semaphore semaphore;

	public Connection(String ip, ArrayList<Connection> connectionVector, Semaphore semaphore) {
		super();
		this.messageVector = new ArrayList<Message>();
		this.semaphore = semaphore;
		this.connectionVector = connectionVector;
		this.ip = ip;
	}

	public Connection(Socket socket, ArrayList<Connection> connectionVector, Semaphore semaphore) {
		super();
		try {
			this.messageVector = new ArrayList<Message>();
			this.socket = socket;
			this.MyOutput = new ObjectOutputStream(socket.getOutputStream());
			this.MyInput = new ObjectInputStream(socket.getInputStream());
			this.ip = socket.getInetAddress().toString().replaceAll("/", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.semaphore = semaphore;
		this.connectionVector = connectionVector;
	}

	public Connection(Socket socket, ArrayList<Message> messageVector, ArrayList<Connection> connectionVector,
			Semaphore semaphore) {
		super();
		try {
			this.messageVector = messageVector;
			this.socket = socket;
			this.MyOutput = new ObjectOutputStream(socket.getOutputStream());
			this.MyInput = new ObjectInputStream(socket.getInputStream());
			this.ip = socket.getInetAddress().toString().replaceAll("/", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.semaphore = semaphore;
		this.connectionVector = connectionVector;
	}

	public String getIp() {
		return this.ip;
	}

	public ArrayList<Message> getMessageVector() {
		return this.messageVector;
	}

	public void sendMessagesOnReconnect() throws IOException {
		for (int i = 0; i < messageVector.size(); i++) {
			if (isConnected()) {
				this.MyOutput.writeObject(this.messageVector.get(i));
				this.messageVector.remove(i);
				i--;
				this.MyOutput.flush();
			}
		}
	}

	public void sendMessage(Message msg) throws IOException {

		if (isConnected()) {
			this.MyOutput.writeObject(msg);
			this.MyOutput.flush();
		} else {
			this.messageVector.add(msg);
		}

	}

	public boolean isConnected() {
		if (this.socket != null) {
			return !this.socket.isClosed();
		} else {
			return false;
		}
	}

	public void run() {

		boolean reciverExist;
		while (true) {
			reciverExist = false;
			try {
				if (this.isConnected()) {
					System.out.println("waiting for msg");
					Message msg = (Message) this.MyInput.readObject();
					System.out.println("got msg");
					System.out.printf("recived msg with%nstatus: %d%nmessage: %s%n%n", msg.getStatus(),
							msg.getMessage());
					if (msg.getStatus() == 0) { // pending
						System.out.println("got msg with status 0");
						msg.setStatus(1); // sent
						this.MyOutput.writeObject(msg);// confirmou recebimento pelo servidor para sender
						this.MyOutput.flush();
// enviar pro reciver
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						reciverExist = false;
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getReceiver())) {
								reciverExist = true;
								System.out.println("sent it to reciver = " + msg.getReceiver() + '\n');
								connectionVector.get(i).sendMessage(msg);
								this.MyOutput.flush();
							}
						}
						if (!reciverExist) {
							System.out.println("reciver do not exist :(");
							Connection reciverConnection = new Connection(msg.getReceiver(), this.connectionVector,
									this.semaphore);
							connectionVector.add(reciverConnection);
							reciverConnection.sendMessage(msg);
						}
						semaphore.release();
					} else if (msg.getStatus() == 2) {
						System.out.println("got message with status 2" + '\n' + " message: " + msg.getMessage() + '\n');
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getSender())) {
								System.out.println("found connection to send msg with status 2" + '\n');
								connectionVector.get(i).sendMessage(msg);
							}
						}
						semaphore.release();
					} else if (msg.getStatus() == 3) {
						System.out.println("got stauts 3");
						try {
							semaphore.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						for (int i = 0; i < this.connectionVector.size(); i++) {
							if (connectionVector.get(i).getIp().equals(msg.getSender())) {
								System.out.println("sent status 3 to: " + msg.getSender());
								connectionVector.get(i).sendMessage(msg);
							}
						}
						semaphore.release();
						System.out.println("out of 'for' to send msg with status 3");
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
						System.out.println("eh vey, deu ruim");
// erro?
					}
				} else {
					return;
				}

			} catch (Exception e) {
				try {
					System.out.println("fechou " + this.getIp() + '\n');
					this.MyInput.close();
					this.MyOutput.close();
					this.socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
		}
	}
}
