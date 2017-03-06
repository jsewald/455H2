package tasks;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import server.ThreadPoolManager;

public class Read implements Runnable {
	
	private SelectionKey key;
	private SocketChannel channel;
	ThreadPoolManager pool;
	
	public Read (ThreadPoolManager pool, SelectionKey key, SocketChannel channel) {
		
		this.key = key;
		this.channel = channel;
		this.pool = pool;
		
	}
	
	public String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		
		 MessageDigest digest = MessageDigest.getInstance("SHA1");
		 byte[] hash = digest.digest(data);
		 BigInteger hashInt = new BigInteger(1, hash);
		 return hashInt.toString(16);
		 
		}

	@Override
	public void run() {
		
		if (!key.isValid() || !channel.isConnected()) {
			return;
		}
		
		ByteBuffer buffer =	ByteBuffer.allocate(8000);
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
		
		String writeSHA1 = "";
		
		try {
			
			writeSHA1 = SHA1FromBytes(buffer.array());
			
		} catch (NoSuchAlgorithmException e) {
			
			System.out.println("Error getting SHA1 from bytes");
			e.printStackTrace();
			
		}
		
		pool.execute(new Write(key, channel, writeSHA1));
		
	}
	
}
