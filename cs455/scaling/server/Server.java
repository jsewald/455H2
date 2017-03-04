package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

	private ThreadPoolManager pool;
	private InetSocketAddress listeningAddressPort;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private SelectionKey key;

	public Server(InetSocketAddress listeningAddressPort, int threadPoolSize) throws Throwable {

		pool = new ThreadPoolManager(threadPoolSize);
		this.listeningAddressPort = listeningAddressPort;

	}

	private	void startServer() throws IOException {

		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		serverSocketChannel.bind(listeningAddressPort);

		while (true) {	
			//			wait	for	events	
			selector.select();	
			//			wake	up	to	work	on	selected	keys
			Iterator keys = selector.selectedKeys().iterator();	
			while	(keys.hasNext())	{	
				//more	housekeeping
				if	(key.isAcceptable ())	{	
					this.accept(key);	
				}
				if	(key.isReadable ())	{	
					this.read(key);	
				}
				if	(key.isWritable ())	{	
					this.write(key);	
				}	
			}
		}
	}

	private	void accept(SelectionKey key) throws IOException {
		ServerSocketChannel servSocket = (ServerSocketChannel)key.channel();	
		SocketChannel channel = servSocket.accept();
		System.out.println("Accepting incoming	connection	");	
		channel.configureBlocking(false);
		channel.register(selector,	SelectionKey.OP_READ);	
	}	

	private	void read(SelectionKey key)	throws	IOException {	
		SocketChannel channel	=	(SocketChannel)	key.channel();	
		ByteBuffer buffer	=	ByteBuffer.allocate(buffSize);
		int read	=	0;	
		try	{
			while	(buffer.hasRemaining()	&&	read	!=	-1)	{	
				read	=	channel.read(buffer);	
			}	
		}	catch	(IOException e)	{ /*	Abnormal	termination	*/	
			server.disconnect(key);	return;	
		}
		if	(read	==	-1)	{
			/*	Connection	was	terminated	by	the	client.	*/	
			server.disconnect(key);
			return;	
		}
		key.interestOps(SelectionKey.OP_WRITE);	
	}	


	private	void	write(SelectionKey key)	throws	IOException {	
		SocketChannel channel	=	(SocketChannel)	key.channel();	
		//You	have	your	data	stored	in	‘data’,	(type:	byte[])
		ByteBuffer buffer	=	ByteBuffer.wrap(data);	
		channel.write(buffer);	
		key.interestOps(SelectionKey.OP_READ);	
	}	



	private	void	connect(SelectionKey key)	throws	IOException {	
		SocketChannel channel	=	(SocketChannel)	key.channel();	
		channel.finishConnect();	
		key.interestOps(SelectionKey.OP_WRITE);	
	}	


	public static void main(String[] args) throws NumberFormatException, Throwable {

		Server thisServer = new Server(new InetSocketAddress("localhost", Integer.parseInt(args[1])), Integer.parseInt(args[2]));
		thisServer.startServer();

	}

}
