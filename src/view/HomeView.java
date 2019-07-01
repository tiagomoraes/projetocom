package view;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.EventQueue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.border.LineBorder;

//import com.sun.java.swing.plaf.windows.resources.windows;

// Custom imports
import components.Message;
import javax.swing.JScrollPane;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Scrollbar;

public class HomeView {

	private JFrame frame;
	private JTextField textInput;
	private Box boxMessages;

	// Local variables should be declared here
	private Vector<Message> messageArr;
	private Vector<Message> delToSend;
	private ObjectInputStream myInput;
	private ObjectOutputStream myOutput;
	private Socket mysocket;
	private String myname;
	private String myip;
	private String auserip;
	private String ausername;
	private String serverip;
	private Semaphore sem;
	private int port;
	private int lastRead, firstToRead;
	private int toSend;
	private connect tryTo;
	private boolean moveOff;

	/**
	 * Launch the application.
	 */
	// arg0 = myip
	// arg1 = myname;
	// arg2 = serverip;
	// arg3 = auserip;
	// arg4 = ausername;
	// arg5 = myport;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeView window = new HomeView(args[0], args[1], args[2], args[3], args[4],
							Integer.parseInt(args[5]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HomeView(String myip, String myname, String serverip, String auserip, String ausername, int myport) {

		// Initialize Local variables
		this.myname = myname;
		this.myip = myip;
		this.serverip = serverip;
		this.auserip = auserip;
		this.ausername = ausername;
		this.port = myport;
		this.lastRead = 0;
		this.firstToRead = 0;
		this.sem = new Semaphore(1);
		this.messageArr = new Vector<Message>();
		this.delToSend = new Vector<Message>();
		this.toSend = 0;
		this.moveOff = false;

		// Initialize jPanel
		initialize();
		this.frame.setVisible(true);

		// connect to server
		this.tryTo = new connect();
		tryTo.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
					sendMessage(new Message(auserip, myip, "", 3));
				}
			}
		});
		frame.setBounds(100, 100, 607, 697);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel = new JPanel();
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
					sendMessage(new Message(auserip, myip, "", 3));
				}
			}
		});
		panel.setBackground(Color.WHITE);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
					sendMessage(new Message(auserip, myip, "", 3));
				}
			}
		});
		scrollPane.setBounds(38, 84, 525, 489);
		panel.add(scrollPane);

		this.boxMessages = Box.createVerticalBox();
		boxMessages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
					sendMessage(new Message(auserip, myip, "", 3));
				}
			}
		});
		scrollPane.setViewportView(boxMessages);

		textInput = new JTextField();
		textInput.setBounds(111, 601, 373, 33);
		panel.add(textInput);
		textInput.setColumns(10);

		textInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					if (!textInput.getText().equals("")) {
						if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
							sendMessage(new Message(auserip, myip, "", 3));
						}
						sendMessage(new Message(myip, auserip, textInput.getText()));
					} else {
						if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
							sendMessage(new Message(auserip, myip, "", 3));
						}
					}
				} else {
					if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
						sendMessage(new Message(auserip, myip, "", 3));
					}
				}
			}
		});

	}

	private void sendMessage(Message msg) {
		try {
			sem.acquire();
			if (msg.getStatus() == 0) {
				trySend(msg);
				updateMessages(msg);
				this.textInput.setText("");
			} else if (msg.getStatus() == 3) {
				sendReadStatus(msg);
				//	this.textInput.setText("");
			} else if (msg.getStatus() == 4) {
				sendDelStatus(msg);
				//	this.textInput.setText("");
			}
			sem.release();
		} catch (Exception e) {

			sem.release();

			if (!tryTo.isAlive()) {
				tryTo = new connect();
				tryTo.start();
			}
		}
		System.out.println(this.lastRead + " " + this.firstToRead + " " + this.toSend + " " + delToSend.size()); // for
		// debuging
		//	this.textInput.setText("");
	}

	public void trySend(Message msg) throws Exception {
		msg.setPs(messageArr.size());
		msg.setPr(-1);
		messageArr.add(msg);
		this.firstToRead++;
		if (!tryTo.isAlive()) {
			for (int i = this.toSend; i < messageArr.size(); i++) {
				if (messageArr.get(i).getSender().equals(this.myip)) {
					try {
						sendTCPMessage(messageArr.get(i));
					} catch (Exception e) {

						throw e;
					}
				}
				this.toSend = i + 1;
			}
		}
	}

	public void sendConfirmationStatus(String sender, String receiver, String message, Message m) throws Exception {
		Message aux = new Message(sender, receiver, message);
		aux.setPs(m.getPs());
		aux.setPr(messageArr.size());
		m.setPr(messageArr.size());
		messageArr.add(m);
		updateMessages(m);
		aux.setStatus(2);
		try {
			sendTCPMessage(aux);
		} catch (Exception e) {
			throw e;
		}
	}

	public void sendReadStatus(Message msg) throws Exception {
		System.out.println("debug");
		msg.setPs(messageArr.get(messageArr.size() - 1).getPs());
		msg.setPr(messageArr.get(messageArr.size() - 1).getPr());
		this.firstToRead = messageArr.size();
		try {
			System.out.println("debu2");
			sendTCPMessage(msg);
		} catch (Exception e) {
			throw e;
		}
	}

	public void sendDelStatus(Message msg) throws Exception {
		msg.setStatus(4);
		updateMessages(msg);
		if (this.myip == msg.getSender()) {
			try {
				sendTCPMessage(msg);
			} catch (Exception e) {
				this.delToSend.add(msg);
				throw e;
			}
		}

	}

	private void sendTCPMessage(Message m) throws Exception {
		try {
			myOutput.writeObject(m);
			myOutput.flush();
		} catch (Exception e) {
			throw e;
		}
	}

	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void updateMessages(Message i) {
		boxMessages.removeAll();
		//System.out.println(i.getReceiver() +  " " + myip);
		if (this.myip.equals(i.getSender())) {
			if (i.getStatus() == 3) {
				for (int j = i.getPs(); j >= this.lastRead; j--) {
					this.messageArr.get(j).setStatus(3);
				}
				this.lastRead = i.getPs() + 1;
			} else if (i.getStatus() == 2) {
				messageArr.get(i.getPs()).setStatus(i.getStatus());
				messageArr.get(i.getPs()).setPr(i.getPr());
			} else if (i.getStatus() == 1) {
				messageArr.get(i.getPs()).setStatus(i.getStatus());
			} else if (i.getStatus() == 4) {
				messageArr.get(i.getPs()).setStatus(i.getStatus());
				messageArr.get(i.getPs()).setMessage("---Mensagem Removida---");
			}
		} else if (this.myip.equals(i.getReceiver())) {
			if (i.getStatus() == 4) {
				if (i.getPr() == -1) {
					for (Message m : messageArr) {
						if (m.getPs() == i.getPs()) {
							m.setMessage("---Mensagem Removida---");
							m.setStatus(i.getStatus());
						}
					}
				} else {
					messageArr.get(i.getPr()).setStatus(i.getStatus());
					messageArr.get(i.getPr()).setMessage("---Mensagem Removida---");
				}
			} else
				messageArr.get(i.getPr()).setStatus(i.getStatus());

		}
		//System.out.println("debugup" + i.getStatus());
		for (Message m : messageArr) {
			// Display messages
			//System.out.println("HERE");
			JPanel gridMessage = new JPanel();

			boxMessages.add(gridMessage);
			gridMessage.setLayout(new GridLayout(1, 0, 0, 0));
			JLabel txtMessage = new JLabel();

			if (m.getSender().equals(this.myip)) {
				txtMessage = new JLabel(this.myname + ": " + m.getMessage());
			} else if (m.getSender().equals(this.auserip)) {
				txtMessage = new JLabel(this.ausername + ": " + m.getMessage());
			}
			gridMessage.add(txtMessage);

			if (m.getSender().equals(myip)) {
				//JLabel statusMessage = new JLabel(String.valueOf(m.getStatus()));
				if (m.getStatus() == 0) {
					ImageIcon icon = createImageIcon("status0.png");
					JLabel statusMessage = new JLabel(icon);
					gridMessage.add(statusMessage);
				} else if (m.getStatus() == 1) {
					ImageIcon icon = createImageIcon("status1.png");
					JLabel statusMessage = new JLabel(icon);
					gridMessage.add(statusMessage);
				} else if (m.getStatus() == 2) {
					ImageIcon icon = createImageIcon("status2.png");
					JLabel statusMessage = new JLabel(icon);
					gridMessage.add(statusMessage);
				} else if (m.getStatus() == 3) {
					ImageIcon icon = createImageIcon("status3.png");
					JLabel statusMessage = new JLabel(icon);
					gridMessage.add(statusMessage);
				} else if (m.getStatus() == 4) {
					ImageIcon icon = createImageIcon("status4.png");
					JLabel statusMessage = new JLabel(icon);
					gridMessage.add(statusMessage);
				}
				//gridMessage.add(statusMessage);
			}
			ImageIcon garbage = createImageIcon("garbage.png");
			JButton btnNewButton = new JButton(garbage);
			btnNewButton.setMargin(new Insets(0, 0, 0, 0));
			btnNewButton.setBorder(null);
			btnNewButton.setBackground(new Color(255, 255, 255, 0));
			btnNewButton.addMouseListener(new MouseAdapter() {
				private int id = (myip.equals(m.getSender())) ? m.getPs() : m.getPr();

				@Override
				public void mouseClicked(MouseEvent e) {
					if (tryTo.isAlive()) {
						moveOff = true;
					}
					if (messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
						sendMessage(new Message(auserip, myip, "", 3));
					}
					if (!messageArr.get(id).getMessage().equals("---Mensagem Removida---")) {
						sendMessage(new Message(messageArr.get(id).getSender(), messageArr.get(id).getReceiver(), "", 4,
								messageArr.get(id).getPs(), messageArr.get(id).getPr()));
					}
				}
			});

			gridMessage.add(btnNewButton);
		}

		boxMessages.revalidate();
		boxMessages.repaint();

	}

	class connect extends Thread {
		public connect() {
			super();
		}

		public void run() {
			while (true) {
				try {
					sem.acquire();
					mysocket = new Socket(serverip, port);
					myInput = new ObjectInputStream(mysocket.getInputStream());
					myOutput = new ObjectOutputStream(mysocket.getOutputStream());
					sem.release();
					new listen().start();
					break;
				} catch (Exception e) {
					try {
						System.out.println("me");
						sem.release();
						Thread.sleep(5000);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
			return;
		}
	}

	class listen extends Thread {
		public listen() {
			super();
		}

		public void run() {
			try {
				System.out.println("meme estranho");
				while (tryTo.isAlive()) {
					Thread.sleep(100);
				}
				sem.acquire();
				if (moveOff && messageArr.size() != 0 && !tryTo.isAlive() && firstToRead != messageArr.size()) {
					sendReadStatus(new Message(myip, auserip, "", 3));
				}
				moveOff = false;
				if (messageArr.size() != toSend) {
					for (int i = toSend; i < messageArr.size(); i++) {
						if (messageArr.get(i).getSender().equals(myip)) {
							if (delToSend.size() > 0) {
								Message aux = new Message(messageArr.get(i).getSender(),
										messageArr.get(i).getReceiver(), "", 4, messageArr.get(i).getPs(),
										messageArr.get(i).getPr());
								if (delToSend.contains(aux)) {
									messageArr.get(i).setMessage("---Mensagem Removida---");
									delToSend.remove(aux);
								}
							}
							sendTCPMessage(messageArr.get(i));
						}
						toSend = i + 1;
					}
				}
				if (delToSend.size() != 0) {
					for (Message m : delToSend) {
						sendDelStatus(m);
					}
					delToSend.removeAllElements();
				}
				sem.release();
			} catch (Exception e) {
				sem.release();
				if (!tryTo.isAlive()) {
					tryTo = new connect();
					tryTo.start();
				}
				return;
			}
			while (true) {
				try {
					Message m = (Message) myInput.readObject();
					System.out.println(m.getMessage());
					//System.out.println(m.getStatus());
					sem.acquire();
					if (m.getStatus() == 1 && m.getSender().equals(myip)) {
						updateMessages(m);
					} else if (m.getStatus() == 1 && m.getReceiver().equals(myip)) {
						sendConfirmationStatus(m.getSender(), m.getReceiver(), "", m);
					} else if (m.getStatus() == 2) {
						updateMessages(m);
					} else if (m.getStatus() == 3) {
						System.out.println("");
						updateMessages(m);
					} else if (m.getStatus() == 4) {
						updateMessages(m);
					}
					sem.release();
				} catch (Exception e) {
					sem.release();
					if (!tryTo.isAlive()) {
						tryTo = new connect();
						tryTo.start();
					}

					return;
				}
			}
		}
	}
}