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

	/**
	 * Launch the application.
	 */
	//arg0 = myip
	//arg1 = myname;
	//arg2 = serverip;
	//arg3 = auserip;
	//arg4 = ausername;
	//arg5 = myport;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeView window = new HomeView(args[0], args[1], args[2] , args[3], args[4],Integer.parseInt(args[5]));
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
		this.sem = new Semaphore(1);
		this.messageArr = new Vector<Message>();

		// Initialize jPanel
		initialize();
		this.frame.setVisible(true);
		
		// connect to server
		try {
			mysocket = new Socket(serverip, port);
			myInput = new ObjectInputStream(mysocket.getInputStream());
			myOutput = new ObjectOutputStream(mysocket.getOutputStream());
			System.out.println("meme");
			new listen().start();
		} catch (Exception e){
			e.printStackTrace();
		}
		

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 440, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 12, 373, 476);
		panel.add(scrollPane);

		this.boxMessages = Box.createVerticalBox();
		scrollPane.setViewportView(boxMessages);

		textInput = new JTextField();
		textInput.setBounds(30, 513, 280, 33);
		panel.add(textInput);
		textInput.setColumns(10);

		// When button "Send" is clicked, append an item to the list and refresh content
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sendMessage(myip, auserip,textInput.getText());
			}
		});
		btnSend.setBounds(315, 513, 81, 33);
		panel.add(btnSend);

	}

	private void sendMessage(String sender, String receiver,String message) {
		Message msg = new Message(sender, receiver,message);

		//this.textInput.removeAll() fazer remover o texto do input text

		// Code to send message TCP
		try {
			sem.acquire();
			msg.setId(messageArr.size());
			messageArr.add(msg);
			sendTCPMessage(msg);
			updateMessages(msg);
			sem.release();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendTCPMessage(Message m) {
		try {
			myOutput.writeObject(m);
			myOutput.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateMessages(Message i) {
		boxMessages.removeAll();
		messageArr.get(i.getId()).setStatus(i.getStatus());
		if (i.getStatus() == 4) {
			System.out.println("kkkk21");
			messageArr.get(i.getId()).setMessage("------------");
		}
		for(Message m : messageArr) {
			// Display messages
			JPanel gridMessage = new JPanel();
			boxMessages.add(gridMessage);
			gridMessage.setLayout(new GridLayout(1, 0, 0, 0));

			JLabel txtMessage = new JLabel(m.getMessage());
			gridMessage.add(txtMessage);
			
			if (m.getSender().equals(myip)) {
				JLabel statusMessage = new JLabel(String.valueOf(m.getStatus()));
				gridMessage.add(statusMessage);
			}
		}

		boxMessages.revalidate();
		boxMessages.repaint();
	}

	// action read message
	// private void readMessage() {
	// //send message with 3
	// }

	// action delete message
	// private void deleteMessage() {
	// //send message with 4
	// }

	class listen extends Thread {
		public listen () {
			super();
		}
		public void run() {
			while (true) {
				try {
					Message m = (Message) myInput.readObject();

					sem.acquire();
					if (m.getStatus() == 1 && !m.getReceiver().contains(myip)) {
						updateMessages(m);
					} else if (m.getStatus() == 1 && m.getReceiver().contains(myip)) {
						//System.out.println("kkkkkkkk");
						messageArr.add(m);
						updateMessages(m);
						Message aux = new Message(m.getSender(), m.getReceiver(), "");
						aux.setStatus(2);
						aux.setId(m.getId());
						sendTCPMessage(aux);
					} else if (m.getStatus() == 2) {
						updateMessages(m);
					} else if (m.getStatus() == 3) {
						updateMessages(m);
					} else if (m.getStatus() == 4) {
						updateMessages(m);
					}
					sem.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}