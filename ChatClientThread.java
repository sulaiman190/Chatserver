package chat_v5;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread{
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream strIn = null;
	private boolean done = true;
	
	public ChatClientThread(ChatClient _client, Socket _socket){
		client = _client;
		socket = _socket;
		open();
		start();//automatically calls run.. you could say this.start();
	}
	
	public void run(){
		done=false;
		while(!done){
			try{
				client.handle(strIn.readUTF());
			}
			catch(IOException e){
				System.err.println("Error listening for input "+e.getMessage());
				close();
				client.stop();
				done=true;
			}
		}
	}
	public void open() {//introduced in version 4
		try {
			strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error getting input stream "+e);
		}
	}
	
	public void close(){
		try{
			if(strIn!=null){
				strIn.close();
			}
			if(socket!=null){
			 socket.close();
			}
		}catch(IOException e){
			System.err.println("Error closing stream" + e.getMessage());
		}
	}
}
