package thread;

import java.util.concurrent.Semaphore;

public class SemaphoreMain {
	
	private static final int MAX_AVAILABLE = 2;

	
	public static void main(String[] args) throws InterruptedException {
		
		Semaphore available = new Semaphore(MAX_AVAILABLE, true);
		Semaphore available2 = new Semaphore(1, true);
		for (int i = 0; i < 10; i++){
			Thread.sleep(2000l);
			
			if(i>=4){
				Thread t = new Thread(new SemaphoreDemo(i, available2));
				t.start();
			} else {
				Thread t = new Thread(new SemaphoreDemo(i, available));
				t.start();
			}
		}
	}
}
