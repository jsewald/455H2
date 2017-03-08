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
	
	private SocketChannel channel;
	private SelectionKey key;
	private LinkedList<String> hashCodes;
	private int rate;
	private ClientStatisticsPrinter stats;
	
	public WriterThread(SocketChannel channel, SelectionKey key, int rate, LinkedList<String> hashCodes, ClientStatisticsPrinter stats) {
		
		this.channel = channel;
		this.key = key;
		this.rate = rate;
		this.hashCodes = hashCodes;
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
		
		while (channel.isConnected()) {
			
			try {
				Thread.sleep(1000/rate);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			byte[] b = new byte[8000];
			new Random().nextBytes(b);
			
			ByteBuffer buffer =	ByteBuffer.wrap(b);
			
			try {
				
				channel.write(buffer);
				stats.incrementSentCount();
				//System.out.println("Sent message");
				
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
