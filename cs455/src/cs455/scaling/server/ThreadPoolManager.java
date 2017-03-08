package cs455.scaling.server;

import java.util.ArrayList;
import java.util.LinkedList;

import cs455.scaling.tasks.Read;
import cs455.scaling.tasks.Write;

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
//			else if (taskToExecute instanceof Write) {
//				int swap = -1;
//				Runnable task = null;
//				for (int i = 0; i < taskQueue.size(); i++) {
//					task = taskQueue.get(i);
//					if (task instanceof Read) {
//						if (((Read) task).key.equals(((Write) taskToExecute).key)) {
//							swap = i;
//							break;
//						}
//					}
//				}
//				if (swap != -1 && task != null) {
//					taskQueue.set(swap, taskToExecute);
//					taskQueue.add(task);
//				}
//			}
			else {
				taskQueue.addFirst(taskToExecute);
			}

		}

	}
	
	public void waitToFinish () {
		synchronized (taskQueue) {
			while (!taskQueue.isEmpty()) {
				try {
					taskQueue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
