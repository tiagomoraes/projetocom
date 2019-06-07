package view;

import java.awt.EventQueue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.border.LineBorder;

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeView window = new HomeView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HomeView() {
		// Initialize Local variables
		this.messageArr = new Vector<Message>();
		
		// Initialize jPanel
		initialize();
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
				sendMessage("name1", textInput.getText());
			}
		});
		btnSend.setBounds(315, 513, 81, 33);
		panel.add(btnSend);
		
	}
	
	private void sendMessage(String sender, String message) {
		Message msg = new Message(sender, message);
		this.messageArr.add(msg);
		// Code to send message TCP
		updateMessages();
	}
	
	private void updateMessages() {
		this.boxMessages.removeAll();
		
		for(Message m : messageArr) {
//			gridMessage = new JPanel();
//			boxMessages.add(gridMessage);
//			gridMessage.setLayout(new GridLayout(1, 0, 0, 0));
//			
//			txtMessage = new JLabel(m.getMessage());
//			gridMessage.add(txtMessage);
//			
//			statusMessage = new JLabel(String.valueOf(m.getStatus()));
//			gridMessage.add(statusMessage);
		}
		
		this.boxMessages.revalidate();
		this.boxMessages.repaint();
	}
}
