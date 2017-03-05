package client;

import java.net.InetSocketAddress;

import server.Server;

public class Client {

	public static void main(String[] args) {
		
		System.out.println("CLIENT");
		System.out.println("Host: " + args[1]);
		System.out.println("Port: " + Integer.parseInt(args[2]));
		System.out.println("Rate: " + Integer.parseInt(args[3]));

		//Client thisClient = new Client(new InetSocketAddress(args[1], Integer.parseInt(args[2])), Integer.parseInt(args[3]));
		//thisServer.startServer();

	}

}
