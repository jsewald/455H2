package server;

import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPoolManager {

	private LinkedList<Runnable> taskQueue;
	private ArrayList<Thread> workers;

	public ThreadPoolManager(int threadPoolSize) {

		taskQueue = new LinkedList<Runnable>();
		workers = new ArrayList<Thread>();

		for (int i = 0; i < threadPoolSize; i++) {	 // Adds (threadPoolSize) threads to the thread pool

			workers.add(new Thread(new Worker(taskQueue)));

		}

		for (int i = 0; i < workers.size(); i++) { 	// Starts all Worker threads in the thread pool

			workers.get(i).start();

		}	

	}

	public void execute (Runnable taskToExecute) {

		synchronized(taskQueue) {

			if (taskQueue.isEmpty()) {

				taskQueue.addLast(taskToExecute); // Add task to taskQueue
				taskQueue.notifyAll();	// Notify all that taskQueue is no longer empty

			}

		}

	}

	//	public synchronized void stop() {
	//		
	//		for (int i = 0; i < workers.size(); i++) {
	//			
	//			workers.get(i).stopThread();
	//			
	//		}
	//		
	//	}

}
