package thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo implements Runnable{

	private int sleepTime;
	
	private Semaphore available;
	
	public SemaphoreDemo(int sleepTime, Semaphore available){
		this.sleepTime = sleepTime;
		this.available = available;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			long startTime = System.currentTimeMillis();
			
			System.out.println("第："+sleepTime+"个线程开始运行");
			boolean avaiFlag = available.tryAcquire();
			
			long endTime = System.currentTimeMillis();
			System.out.println("第："+sleepTime+"个线程获得许可:"+avaiFlag+",用时："+(endTime-startTime)/1000.0);
			Thread.sleep(sleepTime*1000);
			
			System.out.println("第："+sleepTime+"个线程运行完毕");
			if(avaiFlag){
				available.release();
				available.release();
				available.release();
				available.release();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
