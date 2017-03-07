package client;

import java.sql.Timestamp;

public class ClientStatisticsPrinter extends Thread {

	private Integer sentCount;
	private Integer receivedCount; 

	public ClientStatisticsPrinter() {

		this.sentCount = 0;
		this.receivedCount = 0;

	}

	public void incrementSentCount() {

		synchronized (sentCount) {
			sentCount++;
		}

	}

	public void incrementReceivedCount() {

		synchronized (receivedCount) {
			receivedCount++;
		}

	}

	@Override
	public void run() {

		while (true) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			System.out.println("[" + timestamp + "] Total Sent Count: " + sentCount + ", Total Recieved: " + receivedCount);

			synchronized (sentCount) {
				sentCount = 0;
			}
			
			synchronized (receivedCount) {
				receivedCount = 0;
			}
		}
	}

}
