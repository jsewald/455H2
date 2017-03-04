package server;

import java.util.LinkedList;

public class Worker extends Thread {

	private LinkedList<Runnable> taskQueue;
	
	public Worker (LinkedList<Runnable> taskQueue) {
		
		this.taskQueue = new LinkedList<Runnable>();
		this.taskQueue = taskQueue;
		
	}
	
	public void run() {
		
		while (true) {
			
			Runnable task = null;
			
			synchronized(taskQueue) {
				
				while (taskQueue.isEmpty()) { 
					
					try {
						taskQueue.wait(); 	// If taskQueue is empty, Worker thread waits
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				task = (Runnable)taskQueue.removeFirst();	// Once notified that taskQueue is not empty, set task to first in LinkedList
				
			}
			
			task.run(); // Execute task on Worker thread
			
		}
		
	}
	
}
