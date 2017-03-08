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
	private ClientStatisticsPrinter stats;

	public Client(InetSocketAddress serverAddress, int messagingRate) {

		this.messagingRate = messagingRate;
		hashCodes = new LinkedList<String>();
		stats = new ClientStatisticsPrinter();

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
					stats.start();
					channel.finishConnect();
					System.out.println("Connected to server");
					key.interestOps(SelectionKey.OP_READ);
					startSending();
					//System.out.println("Established Connection");
				}
				else if	(key.isReadable()) { // Read
					read(key);
				}	

			}

		}

	}

	private void read(SelectionKey key) {

		//System.out.println("Reading");
		ByteBuffer buffer =	ByteBuffer.allocate(40);
		int read = 0;	
		
		try	{
			
			
			while (buffer.hasRemaining() &&	read !=	-1) {	
				
				read = channel.read(buffer);
				//System.out.println("Read hash");
				
			}	
			
		} catch (IOException e) {
			
			//System.out.println("Abnormal termination in READ");
			return;	
		}
		
		if	(read	==	-1)	{
			
			//System.out.println("Connection terminated by client");
			return;	
			
		}
		
		// Check hashcode
		byte[] temp = new byte[read];
		System.arraycopy(buffer.array(), 0, temp, 0, read);
		String hash = new String(temp);
		//System.out.println("Got here");
		
		synchronized (hashCodes) {
			boolean found = false;
			for (String s: hashCodes) {
				//System.out.println("Hashcode from Server: " + hash);
				//System.out.println("On Client, compare to: " + s);
				if (hash.equals(s)) {
					//System.out.println("MATCH");
					found = true;
					hashCodes.remove(s);
					stats.incrementReceivedCount();
					break;
				}
			}
//			if (!found) {
//				System.out.println("Not found...");
//			}
		}
		
		
	}

	private void startSending() {

		Thread sender = new Thread(new WriterThread(channel, key, messagingRate, hashCodes, stats));
		sender.start();

	}

	public static void main(String[] args) throws IOException {

//		System.out.println("CLIENT");
//		System.out.println("Host: " + args[0]);
//		System.out.println("Port: " + Integer.parseInt(args[1]));
//		System.out.println("Rate: " + Integer.parseInt(args[2]));

		Client thisClient = new Client(new InetSocketAddress(args[0], Integer.parseInt(args[1])), Integer.parseInt(args[2]));
		thisClient.connectToServer();

	}

}
