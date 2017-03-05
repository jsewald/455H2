package tasks;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Connect implements Runnable {
	
	private SelectionKey key;
	private SocketChannel channel;

	public Connect (SelectionKey key, SocketChannel channel) {
		
		this.key = key;
		this.channel = channel;

	}

	@Override
	public void run() {
		
		try {
			
			channel.finishConnect();
			
		} catch (IOException e) {
			
			System.out.println("Exception in CONNECT");
			e.printStackTrace();
			
		}	
		
		key.interestOps(SelectionKey.OP_WRITE);	

	}

}
