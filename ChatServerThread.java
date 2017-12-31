package chat_v5;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread extends Thread{
	private Socket socket = null;
	private ChatServer server = null;
	private int ID = -1;//ID of the client//introduced in version 4
	private DataInputStream strIn = null;
	private DataOutputStream strOut = null;//introduced in version 4
	boolean done = false;
	
	public ChatServerThread(ChatServer _server, Socket _socket){
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort();
		System.out.println("Chat Server Thread Info:"+
		"SERVER: "+server+" SOCKET: "+socket+" ID: "+
				ID);
	}
	public int getID(){//introduced in version 4
		return ID;
	}
	public void run(){
		while(ID!=-1){
				//getInput();
			try{
					server.handle(ID, strIn.readUTF());//modified in version 4
				
			}
			catch (IOException ioe) {
				System.out.println("Exception INSIDE ChatServerThread run method "+ioe.getMessage());
			}
		}
	}
	public void getInput(){//deprecated in version 4
		done = false;
		while(!done){
			try{
				String line = strIn.readUTF();
				if(line.equalsIgnoreCase("bye")){
					done = true;
				}
				System.out.println("UserID: "+ID+" said "+ line);//step 4 IO
			}
			catch (IOException ioe) {
				//System.out.println("Exception INSIDE ChatServerThread getInput method "+ioe.getMessage());
				done = true;//error occurred done = true
			}
		}
	}
	public void open() throws IOException{//modified in version 4
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));//step 3 use the socket to get the incoming data
		strOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));//step 3 use the socket to geth the outgoing stream
	}
	public void close() throws IOException{
		if(strIn!=null){
			strIn.close();
		}
		if(strOut!=null){//introduced in version 4
			strOut.close();
		}
		if(socket!=null){
		 socket.close();
		}
	}
	
	public void send(String msg){//introduced in version 4
		//System.out.println("UserID: "+ID+" said "+ msg);//step 4 IO
		try {
			strOut.writeUTF(msg);
			strOut.flush();
		} catch (IOException e) {
			System.err.println("PROBLEM in send Method: "+e.getMessage());
			server.remove(ID);
			ID = -1;//set ID to -1 for the thread
		}
		
	}
	
}
