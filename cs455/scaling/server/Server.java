package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import tasks.Connect;
import tasks.Read;
import tasks.Write;

public class Server {

	private ThreadPoolManager pool;
	private InetSocketAddress listeningAddressPort;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private ServerStatisticsPrinter stats;

	public Server(InetSocketAddress listeningAddressPort, int threadPoolSize) throws Throwable {

		pool = new ThreadPoolManager(threadPoolSize);
		this.listeningAddressPort = listeningAddressPort;
		stats = new ServerStatisticsPrinter();
		
	}

	private	void startServer() throws IOException {

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		serverSocketChannel.bind(listeningAddressPort);
		stats.start();

		while (true) {	
			
			selector.select();	
			Iterator<SelectionKey> keys = selector.selectedKeys().iterator();	
			
			while (keys.hasNext()) {
				
				SelectionKey key = keys.next();
				keys.remove();
				
				if (!key.isValid()) {
					continue;
				}
				else if	(key.isAcceptable()) {	// Accept
					accept(key);
				}
				else if	(key.isReadable()) { // Read
					SocketChannel channel = (SocketChannel) key.channel();
					pool.execute(new Read(pool, key, channel));
					stats.incrementMessageCount();
				}
//				else if	(key.isWritable()) { // Write
//					byte[] temp = null; // Temporary, FIX THIS
//					pool.execute(new Write(key, channel, temp));
//				}	
				
			}
			
		}
		
	}

	private	void accept(SelectionKey key) throws IOException {
		ServerSocketChannel servSocket = (ServerSocketChannel)key.channel();
		SocketChannel channel = null;
		while (channel == null) {
			channel = servSocket.accept();
		}
		//System.out.println("Accepting incoming connection");
		stats.incrementClientCount();
		channel.configureBlocking(false);
		channel.register(selector,	SelectionKey.OP_READ);	
	}	

//	private	void read(SelectionKey key)	throws	IOException {	
//		SocketChannel channel	=	(SocketChannel)	key.channel();	
//		ByteBuffer buffer	=	ByteBuffer.allocate(buffSize);
//		int read	=	0;	
//		try	{
//			while	(buffer.hasRemaining()	&&	read	!=	-1)	{	
//				read	=	channel.read(buffer);	
//			}	
//		}	catch	(IOException e)	{ /*	Abnormal	termination	*/	
//			server.disconnect(key);	return;	
//		}
//		if	(read	==	-1)	{
//			/*	Connection	was	terminated	by	the	client.	*/	
//			server.disconnect(key);
//			return;	
//		}
//		key.interestOps(SelectionKey.OP_WRITE);	
//	}	


//	private	void	write(SelectionKey key)	throws	IOException {	
//		SocketChannel channel	=	(SocketChannel)	key.channel();	
//		//You	have	your	data	stored	in	�data�,	(type:	byte[])
//		ByteBuffer buffer	=	ByteBuffer.wrap(data);	
//		channel.write(buffer);	
//		key.interestOps(SelectionKey.OP_READ);	
//	}	



//	private	void	connect(SelectionKey key)	throws	IOException {	
//		SocketChannel channel	=	(SocketChannel)	key.channel();	
//		channel.finishConnect();	
//		key.interestOps(SelectionKey.OP_WRITE);	
//	}	


	public static void main(String[] args) throws NumberFormatException, Throwable {

		Server thisServer = new Server(new InetSocketAddress("localhost", Integer.parseInt(args[0])), Integer.parseInt(args[1]));
		thisServer.startServer();

	}

}
