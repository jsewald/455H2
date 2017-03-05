package tasks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Read implements Runnable {
	
	private SelectionKey key;
	private SocketChannel channel;
	
	public Read (SelectionKey key, SocketChannel channel) {
		
		this.key = key;
		this.channel = channel;
		
	}

	@Override
	public void run() {
		
		ByteBuffer buffer =	ByteBuffer.allocate(256);
		int read = 0;	
		
		try	{
			
			while (buffer.hasRemaining() &&	read !=	-1) {	
				
				read = channel.read(buffer);	
				
			}	
			
		} catch (IOException e) {
			
			System.out.println("Abnormal termination in READ");
			return;	
			
		}
		
		if	(read	==	-1)	{
			
			System.out.println("Connection terminated by client");
			return;	
			
		}
		
		key.interestOps(SelectionKey.OP_WRITE);
		
	}
	
}
