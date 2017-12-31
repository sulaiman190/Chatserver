package chat_v5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatGUI extends JFrame implements ActionListener,KeyListener, Runnable{

	private Socket socket = null;
	private final String serverName = "localhost"; 
	private final int serverPortNumber = 8080; //needs to ma
	
	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;
	
	private ChatClientThread client = null;
	private boolean done = true;//until connected you are "done"
	private String line = "";
	
	private JTextArea displayText = new JTextArea(16,50);
	private JTextField input = new JTextField(1);
	private JButton btnConnect = new JButton("Connect");
	private JButton btnSend = new JButton("Send");
	private JButton btnQuit = new JButton("Disconnect");
	private JButton btnPrivate = new JButton("Private");
	private JButton btnEncrypt = new JButton("Encrypt");
	private JPanel mainJP = new JPanel();//everything goes in here
	private JPanel displayJP = new JPanel();//textarea.. 
	private JPanel btnsJP = new JPanel();//put this on the bottom
	private JPanel east = new JPanel();
	private JPanel west = new JPanel();
	private JPanel north = new JPanel();
	public ChatGUI(){
		
		mainJP.setLayout(new BorderLayout());
		
		
		displayJP.setLayout(new GridLayout(2,1));
		displayJP.add(displayText); 
		displayJP.add(input);//added input below textarea to jpanel
		
		btnsJP.setLayout(new GridLayout(1,4));
		
		
		btnConnect.addActionListener(this);
		btnPrivate.addActionListener(this);	
		btnSend.addActionListener(this);
		btnQuit.addActionListener(this);
		btnEncrypt.addActionListener(this);

		
		Box box = Box.createHorizontalBox();
		box.add(btnPrivate);box.add(btnSend);box.add(btnQuit);box.add(btnEncrypt);box.add(btnConnect);
		btnsJP.add(box);
		/*btnsJP.add(btnPrivate);
		btnsJP.add(btnConnect);
		btnsJP.add(btnSend);
		btnsJP.add(btnQuit); */
		east.setBackground(Color.red);
		west.setBackground(Color.red);
		north.setBackground(Color.red);
		east.setBackground(Color.red);
		east.setBackground(Color.red);
		btnsJP.setBackground(Color.red);
		
		mainJP.add(displayJP, BorderLayout.CENTER);//add to center
		mainJP.add(btnsJP, BorderLayout.SOUTH);//add to bottom
		mainJP.add(east, BorderLayout.EAST);
		mainJP.add(west, BorderLayout.WEST);
		mainJP.add(north, BorderLayout.NORTH);
		
		input.setEditable(false);
		displayText.setEditable(false);
		
		
		add(mainJP);
		btnQuit.setEnabled(false);
		
		btnPrivate.setEnabled(false);
		btnSend.setEnabled(false);
		btnPrivate.setEnabled(false);
		btnEncrypt.setEnabled(false);
		addKeyListener(this);
		
	}
	
	@Override
	public void run() {
		while(!done){
			try {
				line = strIn.readUTF();
				//displayMessage(line);
				displayMsg(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	private void displayMsg(String msg){
		displayText.append(msg +"\n");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//do something when connect
		//connect(serverName, serverPortNumber);
		if(e.getSource() == btnConnect)
			connect(serverName, serverPortNumber);
		
		//do something when send
		if(e.getSource()== btnSend)
			send();
		
		if(e.getSource() == btnQuit)
			disconnect();
		if(e.getSource() == btnPrivate)
			privatemsg();
		if(e.getSource() == btnEncrypt)
			privatemsg();
		
	
		
	}
	public void connect(String serverName, int portNum){
		try {
			done=false;
			socket = new Socket(serverName, portNum);
			System.out.println("Client just got connected");
			//display("hooray that we got connected");... 
			open();
			input.setText("Connected");
			send();
			btnSend.enable();
			btnConnect.enable();
			btnQuit.enable();
			btnPrivate.enable();
			btnSend.setEnabled(true);
			btnConnect.setEnabled(false);
			btnPrivate.setEnabled(true);
			btnQuit.setEnabled(true);
			input.setEditable(true);
			//enable our buttons.... 
			//either.... use .setEnabled... and have separate buttons and use
			//getSource from within actionPerformed
			//or....... use .setText ... and have 1 button whose face changes and use
			//getActionCommand from within actionPerformed
			//or... still use getSource... and compare against the boolean done
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			done=true;
			//displayMessage("OOPSY"+ e.getMessage()+"... or something nicer");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			done=true;
			//displayMessage("OOPSY"+ e.getMessage()+"... or something nicer");
		}
		finally{
			//displayMessage("the connection attempt completed... success/failure..");
			//onto the next task.. blah blha
		}
		
	}
	

	
	public void send(){
		try {
		//get the message from the input textfield getText(); 
		//...store it in a String
		// make sure to 
			input.requestFocus();
			
			String msgs = input.getText();
			strOut.writeUTF(msgs);
			strOut.flush();
			input.setText("");
			btnSend.setEnabled(true);
			btnConnect.setEnabled(false);
			btnPrivate.setEnabled(true);
			btnQuit.setEnabled(true);
			input.setEditable(true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//displayMessage("BLAH");
		}
		
	}
	
	public void privatemsg(){
		try {
		//get the message from the input textfield getText(); 
		//...store it in a String
		// make sure to 
			input.requestFocus();
			String msgs = input.getText();
			strOut.writeUTF(msgs);
			strOut.flush();
			input.setText("");
			btnConnect.setEnabled(false);
			btnPrivate.setEnabled(true);
			btnSend.setEnabled(true);
			btnQuit.setEnabled(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}	
	public void disconnect(){
		done=true;
		input.setText("chat");
		send();
		btnSend.setEnabled(false);
		btnConnect.setEnabled(true);
		btnPrivate.setEnabled(false);
		btnQuit.setEnabled(false);
		input.setEditable(false);
		///either setEnabled(false/true)... for your buttons
		//or...setText("Connect")... for your buttons...
		//if ... stream... is !=null
		//stream.close();
		//do that for in... and for out... do that for the socket...
	}
	public void open(){
		try {
			strOut = new DataOutputStream(socket.getOutputStream());
			strIn = new DataInputStream(socket.getInputStream());
			new Thread(this).start();//to be able to listen in
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//strIn =...
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		 if (e.getKeyCode() == KeyEvent.VK_ENTER) 
		    	System.out.println("texting");
		    	send();
			
	 
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater( new Runnable(){
			public void run() {
				ChatGUI chatclient = new ChatGUI();
				chatclient.pack();
				chatclient.setVisible(true);
				chatclient.setSize(500,500);
				chatclient.setTitle("Chat Server");
				
				
			}
			
			
		}
				
				);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

		
	}

