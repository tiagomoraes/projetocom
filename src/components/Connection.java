package components;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class Connection extends Thread {
	private ObjectInputStream MyInput;
	private ObjectOutputStream MyOutput;
	private String ip;
	private ArrayList<Connection> connectionVector;

	public Connection(Socket socket, ArrayList<Connection> connection) {
		super();
		try {
			this.MyInput = new ObjectInputStream(socket.getInputStream());
			this.MyOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void sendMessage(Message msg) throws IOException {
		this.MyOutput.writeObject(msg);
	}

	public void run() {
		while(true) {
			try {
				Message msg = (Message)this.MyInput.readObject();
				this.ip = msg.getSender();
				if(msg.getStatus() == 0) {
					msg.setStatus(1);
					this.MyOutput.writeObject(msg);//confirmou recebimento pelo servidor para sender
					//enviar pro reciver
					for(int i = 0; i < this.connectionVector.size();i++) {
						if(connectionVector.get(i).getIp().equals(msg.getReceiver())) {
							connectionVector.get(i).sendMessage(msg);
						}
					}
				}else if(msg.getStatus() == 2) {
					this.MyOutput.writeObject(msg);//ok
				}else if(msg.getStatus() == 3) {
					this.MyOutput.writeObject(msg);//ok
				}else if(msg.getStatus() == 4) {
					for(int i = 0; i < this.connectionVector.size();i++) {
						if(connectionVector.get(i).getIp().equals(msg.getReceiver())) {
							connectionVector.get(i).sendMessage(msg);
						}
					}
				}else {
					//erro?
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
