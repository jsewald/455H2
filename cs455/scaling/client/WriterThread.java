package client;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;

import tasks.Write;

public class WriterThread implements Runnable {
	
	SocketChannel channel;
	SelectionKey key;
	LinkedList<String> hashCodes;
	Integer sentCount;
	
	public WriterThread(SocketChannel channel, SelectionKey key, int rate, LinkedList<String> hashCodes, Integer sentCount) {
		
		this.channel = channel;
		this.key = key;
		this.hashCodes = hashCodes;
		this.sentCount = sentCount;
		
	}
	
	public String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
		
		 MessageDigest digest = MessageDigest.getInstance("SHA1");
		 byte[] hash = digest.digest(data);
		 BigInteger hashInt = new BigInteger(1, hash);
		 return hashInt.toString(16);
		 
	}

	@Override
	public void run() {
		
		while (channel.isConnected()) {
			
			byte[] b = new byte[8000];
			new Random().nextBytes(b);
			
			ByteBuffer buffer =	ByteBuffer.wrap(b);
			
			try {
				
				channel.write(buffer);
				synchronized (sentCount) {
					sentCount++;
				}
				
			} catch (IOException e) {
				
				System.out.println("Exception in WRITE");
				e.printStackTrace();
				
			}
			
			String SHA1 = "";
			
			try {
				
				SHA1 = SHA1FromBytes(b);
				
			} catch (NoSuchAlgorithmException e) {
				
				System.out.println("Error getting SHA1 hash code");
				e.printStackTrace();
				
			}
			
			synchronized(hashCodes) {
				hashCodes.add(SHA1);
			}
			
		}
		
	}
	
	

}
