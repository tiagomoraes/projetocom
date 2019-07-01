package components;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private String message; // conteudo da mensagem
	private String sender; // ip de sender
	private String receiver; // ip de receiver
	private int ps, pr; // ps indica indice em sender e pr indice em receiver
	private int status; // 0 = pending; 1 = sent; 2 = arrived; 3 = read; 4 = erased;
	
	
	public Message() {
		this.status = 0;
		this.message = "";
	}
	
	public Message(String sender, String receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.status = 0;
	}
	
	public Message(String sender, String receiver, String message, int Status) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.status = Status;
	}
	
	public Message(String sender, String receiver, String message, int Status, int Ps, int Pr) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.status = Status;
		this.ps = Ps;
		this.pr = Pr;
	}


	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getReceiver() {
		return receiver;
	}
	
	public void setReceiver (String receiver) {
		this.receiver = receiver;
	}
	
	public int getPs() {
		return ps;
	}
	
	public void setPs (int ps) {
		this.ps = ps;
	}
	
	public void setPr (int pr) {
		this.pr = pr;
	}
	
	public int getPr () {
		return pr;
	}
}

