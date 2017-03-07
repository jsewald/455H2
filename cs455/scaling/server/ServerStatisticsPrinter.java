package server;

import java.sql.Timestamp;

public class ServerStatisticsPrinter extends Thread {

	private Integer clientCount;
	private Integer messageCount; 

	public ServerStatisticsPrinter() {

		this.messageCount = 0;
		this.clientCount = 0;

	}

	public void incrementClientCount() {

		synchronized (clientCount) {
			clientCount++;
		}

	}

	public void incrementMessageCount() {

		synchronized (messageCount) {
			messageCount++;
		}

	}

	@Override
	public void run() {

		while (true) {

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			int trupa = 0;
			synchronized (messageCount) {
				trupa = messageCount / 5;
			}
			System.out.println("[" + timestamp + "] Current Server Throughput: " + trupa + " messages/s, Active Client Connections: " + clientCount);

			synchronized (messageCount) {
				messageCount = 0;
			}
		}
	}

}
