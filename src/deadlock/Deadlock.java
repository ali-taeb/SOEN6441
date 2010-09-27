package deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
	
	public static Lock lock = new ReentrantLock();
	public static Object obj1 = new Object();
	public static Object obj2 = new Object();
	   
	public static void main(String[] a) {
		Thread t1 = new Thread1();
	    Thread t2 = new Thread2();
	    t1.start();
	    t2.start();
	}
	

	private static class Thread1 extends Thread {
		public void run() {
			lock.lock();
			try{
				System.out.println("Thread 1: Holding lock 1...");
				try { 
					Thread.sleep(10); 
				} catch (InterruptedException e) {
					//
				}
	            	
				System.out.println("Thread 1: Waiting for lock 2...");
	            synchronized (obj2) {
	            	System.out.println("Thread 1: Holding lock 1 & 2...");
	            }
			} finally{
				lock.unlock();
			}
	    }
	}
	   
	private static class Thread2 extends Thread {
	      public void run() {
	    	  lock.lock();
	         try{
	            System.out.println("Thread 2: Holding lock 2...");
	            
	            try { 
	            	Thread.sleep(10); 
	            } catch (InterruptedException e) {
	            	//
	            }
	            
	            System.out.println("Thread 2: Waiting for lock 1...");
	            synchronized (obj1) {
	               System.out.println("Thread 2: Holding lock 2 & 1...");
	            }
	         } finally{
	        	 lock.unlock();
	         }
	      }
	   }
	}
