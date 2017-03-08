package tasks;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import server.ServerStatisticsPrinter;
import server.ThreadPoolManager;

public class Read implements Runnable {
	
	public SelectionKey key;
	private SocketChannel channel;
	ThreadPoolManager pool;
	ServerStatisticsPrinter stats;
	
	public Read (ThreadPoolManager pool, SelectionKey key, SocketChannel channel, ServerStatisticsPrinter stats) {
		
		this.key = key;
		this.channel = channel;
		this.pool = pool;
		this.stats = stats;
		
	}
	
	public String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		
		 MessageDigest digest = MessageDigest.getInstance("SHA1");
		 byte[] hash = digest.digest(data);
		 BigInteger hashInt = new BigInteger(1, hash);
		 String ret = hashInt.toString(16);
		 while (ret.length() < 40) {
			 ret += "=";
		 }
		 return ret;
	
		}

	@Override
	public void run() {
		
		
		
		//System.out.println(Thread.currentThread().getName() + " executing read");
		
		ByteBuffer buffer;
		synchronized (channel) {
		if (!key.isValid() || !channel.isConnected()) {
			return;
		}
		
		buffer =	ByteBuffer.allocate(8000);
		int read = 0;	
		
		try	{
			
			while (buffer.hasRemaining() &&	read !=	-1) {	
				
				read = channel.read(buffer);
				
				//System.out.println("Read message");
				
			}	
			
		} catch (IOException e) {
			
			//System.out.println("Abnormal termination in READ");
			return;	
			
		}
		
		
		if	(read	==	-1)	{
			
			//System.out.println("Connection terminated by client");
			return;	
			
		}
		stats.incrementMessageCount();
		}
		
		String writeSHA1 = "";
		
		try {
			
			writeSHA1 = SHA1FromBytes(buffer.array());
			//System.out.println("Hash on server: " + writeSHA1);
			
		} catch (NoSuchAlgorithmException e) {
			
			System.out.println("Error getting SHA1 from bytes");
			e.printStackTrace();
			
		}
		
		
		
		//synchronized (key) {
		pool.execute(new Write(key, channel, writeSHA1));
		//}
		//new Write(key, channel, writeSHA1).run();
	}
	
	
}
