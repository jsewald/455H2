package tasks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Write implements Runnable {
	
	private SelectionKey key;
	private SocketChannel channel;
	private byte[] data;

	public Write (SelectionKey key, SocketChannel channel, String data) {

		this.key = key;
		this.channel = channel;
		this.data = data.getBytes();
		
	}

	@Override
	public void run() {
		
		ByteBuffer buffer =	ByteBuffer.wrap(data);
		
		try {
			
			channel.write(buffer);
			
		} catch (IOException e) {
			
			System.out.println("Exception in WRITE");
			e.printStackTrace();
			
		}	
		
		key.interestOps(SelectionKey.OP_READ);	

	}

}
