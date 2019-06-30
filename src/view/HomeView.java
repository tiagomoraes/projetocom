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

public class HomeView {

	private JFrame frame;
	private JTextField textInput;
	private Box boxMessages;

	// Local variables should be declared here
	private Vector<Message> messageArr;
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
	private int lastRead;
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
		this.sem = new Semaphore(1);
		this.messageArr = new Vector<Message>();
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
				if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
					sendMessage("-1", "-1", "-1");
				}
			}
		});
		frame.setBounds(100, 100, 440, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel = new JPanel();
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
					sendMessage("-1", "-1", "-1");
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
				if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
					sendMessage("-1", "-1", "-1");
				}
			}
		});
		scrollPane.setBounds(30, 12, 373, 476);
		panel.add(scrollPane);

		this.boxMessages = Box.createVerticalBox();
		boxMessages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
					sendMessage("-1", "-1", "-1");
				}
			}
		});
		scrollPane.setViewportView(boxMessages);

		textInput = new JTextField();
		textInput.setBounds(30, 513, 373, 33);
		panel.add(textInput);
		textInput.setColumns(10);
		textInput.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (tryTo.isAlive()) {
					moveOff = true;
				}
				int key = e.getKeyCode();
				System.out.println("teste key: " + lastRead);
				if (key == KeyEvent.VK_ENTER) {
					if (!textInput.getText().equals("")) {
						if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
							sendMessage("-1", "-1", "-1");
						}
						sendMessage(myip, auserip, textInput.getText());
					} else {
						if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
							sendMessage("-1", "-1", "-1");
						}
					}
				} else {
					// System.out.println("teste key");
					if (messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
						sendMessage("-1", "-1", "-1");
					}
				}
			}
		});

		// poder ser tocar no scroll ou em alguma parte do app, digitar alguma coisa la
		// na box etc.
		// ler msg le le tudo na tela q o outro enviou (só manda uma msg contendo o
		// indice no vetor da ultima msg enviada por sender)
		// ter um boolean ou alguma condição pra nao mandar msg redundante
		// action read message
		// private void readMessage() {
		// //send message with 3
		// }

		// pode ser um botao no lado de cada msg ou um double click
		// se for msg do outro usuario, só apaga localmente, se for sua manda uma msg
		// com 4 e o indice no seu vetor
		// action delete message
		// private void deleteMessage() {
		// //send message with 4
		// }
	}

	private void sendMessage(String sender, String receiver, String message) {
		Message msg = new Message(sender, receiver, message);
		// Code to send message TCP
		try {
			sem.acquire();
			if (msg.getSender().equals("-1")) {
				msg.setStatus(3);
			} else if (msg.getSender().equals("-2")) {
				msg.setStatus(4);
			} else if (!msg.getSender().equals("-1")) {
				msg.setPs(messageArr.size());
				msg.setPr(-1);
				messageArr.add(msg);
				//System.out.println("GOT THERE");
				// this.lastRead = messageArr.size();
			}
			if (tryTo.isAlive()) System.out.println("omegalul");
			if (!tryTo.isAlive()) {
				//System.out.println("GOT THERE");
				if (msg.getStatus() == 3) {

					msg.setSender(this.auserip);
					msg.setReceiver(this.myip);
					msg.setPs(messageArr.get(messageArr.size() - 1).getPs());
					msg.setPr(messageArr.get(messageArr.size() - 1).getPr());
					this.lastRead = messageArr.size();
					sendTCPMessage(msg);
				} else if (msg.getStatus() == 4) {
					// msg.setPs(messageArr.get(messageArr.size()-1).getPs()); ver como vai fazer
					// msg.setPr(messageArr.get(messageArr.size()-1).getPr()); ver como vai fazer
					updateMessages(msg);
				} else {
					for (int i = this.toSend; i < messageArr.size(); i++) {
						//System.out.println("GOT THERE");
						if (messageArr.get(i).getSender().equals(this.myip)) {
							sendTCPMessage(messageArr.get(i));
						}

					}

					// this.lastRead = messageArr.size();
				}
			}
			if (msg.getStatus() == 0)updateMessages(msg);
			sem.release();
		} catch (Exception e) {
			sem.release();
			//if (!tryTo.isAlive()) {
			tryTo = new connect();
			tryTo.start();
			//}
		}
		this.textInput.setText("");
	}

	private void sendTCPMessage(Message m) throws Exception {
		try {
			System.out.println(toSend);
			myOutput.writeObject(m);
			myOutput.flush();
			if (m.getStatus() == 0) this.toSend++;
			System.out.println(toSend);
		} catch (Exception e) {
			throw e;
		}
	}

	private void updateMessages(Message i) {
		//System.out.println("bug");
		boxMessages.removeAll();
		if (this.myip.equals(i.getSender())) {
			messageArr.get(i.getPs()).setStatus(i.getStatus());
			if (i.getStatus() == 3) {
				for (int j = i.getPr(); j >= 0 ; j--) {
					this.messageArr.get(j).setStatus(3);
				}
			} else if (i.getStatus() == 2) {
				messageArr.get(i.getPs()).setPr(i.getPr());
			} else if (i.getStatus() == 1) {
				//
			}

		} else if (this.myip.equals(i.getReceiver())) {
			messageArr.get(i.getPr()).setStatus(i.getStatus());
			if (i.getStatus() == 4) messageArr.get(i.getPr()).setMessage("----DELETED----");
		}
		for (Message m : messageArr) {
			// System.out.println(m.getMessage());
			// Display messages
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
				JLabel statusMessage = new JLabel(String.valueOf(m.getStatus()));
				gridMessage.add(statusMessage);
			}
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
			while (true) {
				try {
					while (tryTo.isAlive()) {
						Thread.sleep(100);
					}
					sem.acquire();
					if (moveOff && messageArr.size() != 0 && !tryTo.isAlive() && !messageArr.lastElement().getSender().equals(myip) && lastRead != messageArr.size()) {
						sendMessage("-1", "-1", "-1");
					}
					moveOff = false;
					System.out.println(toSend);
					if (messageArr.size() != toSend) {
						for (int i = toSend; i < messageArr.size(); i++) {
							System.out.println("kaka");
							if (messageArr.get(i).getSender().equals(myip)) {
								sendTCPMessage(messageArr.get(i));
							}
							toSend = i + 1;
						}
					}

					sem.release();

					Message m = (Message) myInput.readObject();

					sem.acquire();
					if (m.getStatus() == 1 && m.getSender().equals(myip)) {
						updateMessages(m);
					} else if (m.getStatus() == 1 && m.getReceiver().contains(myip)) {
						Message aux = new Message(m.getSender(), m.getReceiver(), "");
						aux.setPs(m.getPs());
						aux.setPr(messageArr.size());
						m.setPr(messageArr.size());
						messageArr.add(m);
						updateMessages(m);
						aux.setStatus(2);
						// lastRead = messageArr.size();
						// System.out.println("sent2");
						sendTCPMessage(aux);

					} else if (m.getStatus() == 2) {
						//System.out.println(m.getMessage() + ' ' + m.getSender() + ' ' + m.getReceiver());
						updateMessages(m);
					} else if (m.getStatus() == 3) {
						//lastRead = messageArr.size();
						updateMessages(m);
					} else if (m.getStatus() == 4) {
						updateMessages(m);
					}
					sem.release();
				} catch (Exception e) {
					sem.release();
					//if (!tryTo.isAlive()) {
					tryTo = new connect();
					tryTo.start();
					//}
					break;
				}
			}
			return;
		}
	}
}
