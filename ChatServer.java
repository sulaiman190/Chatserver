package chat_v5;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * version 5
 * server will remain open for additional clients to connect after a client says bye
 * server can handle multiple clients simultaneously
 * output from all the clients will be printed locally on the server's console
 * and sent to all connected clients
 * Private Messaging //pm:12345-blahblhablahblahblahblahblah
 * 
 */
public class ChatServer implements Runnable{
	private ServerSocket server = null;
	private Thread thread = null;
	private ChatServerThread [] clients = new ChatServerThread[50];
	private int clientCount = 0;
	
	public ChatServer(int port){//same as version 2, and 3
		try {
			System.out.println("Will start server");
			server = new ServerSocket(port);//step 1	
			start();//start method of the server
		} catch (IOException e) {
			System.err.println("Tried to use our server.. but..."+e.getMessage());
		}
	}
	public void start(){//same as version 2, and 3
		if(thread == null){
			thread = new Thread(this);
			thread.start();//start will call the thread's run method
		}
	}
	@Override
	public void run() {//same as version 3
		while(thread!=null){
			System.out.println("Waiting for a client");
			try {
				addThread(server.accept());//add a new ChatServerThread.....
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void remove(int ID){//introduced in version 4
		int pos = findClient(ID);//find the id via the ChatServerThread ID inside the clients array 
		if(pos >= 0){
			ChatServerThread toTerminate = clients[pos];//get the client to terminate
			System.out.println("Remove the client with ID: "+ID+" at index "+pos);
			if(pos < clientCount-1){
				for(int i=pos+1; i<clientCount; i++){
					clients[i-1]=clients[i];//shift everyone that is right of the client to the left
				}
				clientCount--;
			}
			try {
				toTerminate.close();//close the streams for the client to become disconnected
			} catch (IOException e) {
				System.err.println("Inside Remove: Problem closing ChatServerThread "+e.getMessage());
			}
		}
	}
	
	public synchronized void handle(int ID, String input){//introduced in version 4
		System.out.println("SERVERLOG- USER: "+ ID + " said: "+input);
		String privMsg = "pm:";//pm:12345-
		String delimIDStart = ":";
		String delimMsgStart= "-";
		if(input.startsWith(privMsg)){
			int indexIDStart = input.indexOf(delimIDStart)+1;//get the starting index of the id using the delimeter
			int indexIDEnd = input.indexOf(delimMsgStart);
			int indexMsgStart = input.indexOf(delimMsgStart)+1;
			int ID_SendTo = Integer.parseInt(input.substring(indexIDStart,indexIDEnd));
			String msg = input.substring(indexMsgStart);//the rest is the message
			int indexOfID_SendTo = findClient(ID_SendTo);
			if(indexOfID_SendTo != -1){//user found so we send private
				clients[indexOfID_SendTo].send("USER: "+ ID + " said: "+msg);
			}
		}
		else{
			for(int i=0; i<clientCount; i++){
				clients[i].send("USER: "+ ID + " said: "+input);
			}
		}
	}
	
	
	
	
	
	private int findClient(int ID){//introduced in version 4
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID() == ID){//if clients ID matches target
				return i;//match found return index location
			}
		}
		return -1;//no match found
	}
	//add a connected client to a new thread via the ChatServerThread
	public synchronized void addThread(Socket socket){//introduced in version 3, modified in v4
		if(clientCount < clients.length){
			System.out.println("client accepted on socket");
			clients[clientCount] = new ChatServerThread(this, socket);
			try{
				clients[clientCount].open();//open the stream for the ChatServerThread to handle the client
				clients[clientCount].start();//start running the ChatServerThread to handle the client
				clientCount++;
			}
			catch (IOException ioe) {
				System.out.println("Exception INSIDE addThread to open and start handling the client");
			}
		}
		else{
			System.err.println("Cliente refused: max num of clients, "+clients.length+" reached");
		}
	}
	

	public static void main(String[] args) {
		//ChatServer myServer = new ChatServer(8080);
		ChatServer myServer = null;
		if(args.length != 1){
			System.out.println("You need just a port address to run the server");
		}
		else{
			myServer = new ChatServer(Integer.parseInt(args[0]));
		}

	}
	
	
	
}
