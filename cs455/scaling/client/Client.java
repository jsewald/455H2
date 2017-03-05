package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import server.Server;
import tasks.Connect;

public class Client {
	
	private InetSocketAddress serverAddress;
	private int messagingRate;
	private Selector selector;
	private SelectionKey key;
	private SocketChannel channel;
	
	public Client(InetSocketAddress serverAddress, int messagingRate) {
		
		this.messagingRate = messagingRate;
		this.serverAddress = serverAddress;
		
	}
	
	private void connectToServer() {

		try {
			
			selector = Selector.open();
			//channel.bind(serverAddress);
			key = channel.register(selector, SelectionKey.OP_CONNECT);
			
		} catch (IOException e) {
			
			System.out.println("Could not open connection to Server socket");
			e.printStackTrace();
			
		}
		
		if	(key.isConnectable()) {	// Connect
			Connect connect = new Connect(key, channel);
			connect.run();
		}
		
	}
	
	private void startSending() {
		
		
		
	}

	public static void main(String[] args) {
		
		System.out.println("CLIENT");
		System.out.println("Host: " + args[0]);
		System.out.println("Port: " + Integer.parseInt(args[1]));
		System.out.println("Rate: " + Integer.parseInt(args[2]));

		Client thisClient = new Client(new InetSocketAddress(args[0], Integer.parseInt(args[1])), Integer.parseInt(args[2]));
		thisClient.connectToServer();

	}

}
