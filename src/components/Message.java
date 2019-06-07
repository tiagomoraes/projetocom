package components;

public class Message {
	private String sender;
	private String message;
	private int status; // 0 = pending; 1 = sent; 2 = arrived; 3 = read;
	
	
	public Message() {
		this.status = 0;
		this.message = "";
	}
	
	public Message(String sender, String message) {
		this.sender = sender;
		this.message = message;
		this.status = 0;
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
}
