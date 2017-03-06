package client;

import java.awt.List;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;

import server.Server;
import tasks.Connect;
import tasks.Read;

public class Client {

	private InetSocketAddress serverAddress;
	private int messagingRate;
	private Selector selector;
	private SelectionKey key;
	private SocketChannel channel;
	private LinkedList<String> hashCodes;
	private Integer sentCount;
	private Integer recievedCount;

	public Client(InetSocketAddress serverAddress, int messagingRate) {

		this.messagingRate = messagingRate;
		hashCodes = new LinkedList<String>();
		sentCount = 0;
		recievedCount = 0;

		try {

			channel = SocketChannel.open();
			channel.configureBlocking(false);
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_CONNECT);
			channel.connect(serverAddress);

		} catch (IOException e) {

			System.out.println("Error opening channel");
			e.printStackTrace();

		}

	}

	private void connectToServer() throws IOException {

		while (true) {	

			selector.select();	
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();	

			while (keys.hasNext()) {

				SelectionKey key = keys.next();
				keys.remove(); 

				if (!key.isValid()) {
					continue;
				}
				if	(key.isConnectable()) {	// Connect
					channel.finishConnect();
					key.interestOps(SelectionKey.OP_READ);
					startSending();
					System.out.println("Established Connection");
				}
				else if	(key.isReadable()) { // Read
					read(key);
				}	

			}

		}

	}

	private void read(SelectionKey key) {

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
		
		// Check hashcode
		byte[] temp = new byte[read];
		System.arraycopy(buffer.array(), 0, temp, 0, read);
		String hash = new String(temp);
		
		synchronized (hashCodes) {
			for (String s: hashCodes) {
				if (hash.equals(s)) {
					hashCodes.remove(s);
					synchronized (recievedCount) {
						recievedCount++;
					}
					break;
				}
			}
		}
		
		
	}

	private void startSending() {

		Thread sender = new Thread(new WriterThread(channel, key, messagingRate, hashCodes, sentCount));
		sender.start();

	}

	public static void main(String[] args) throws IOException {

		System.out.println("CLIENT");
		System.out.println("Host: " + args[0]);
		System.out.println("Port: " + Integer.parseInt(args[1]));
		System.out.println("Rate: " + Integer.parseInt(args[2]));

		Client thisClient = new Client(new InetSocketAddress(args[0], Integer.parseInt(args[1])), Integer.parseInt(args[2]));
		thisClient.connectToServer();

	}

}
