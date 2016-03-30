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
			
			System.out.println("�ڣ�"+sleepTime+"���߳̿�ʼ����");
			boolean avaiFlag = available.tryAcquire();
			
			long endTime = System.currentTimeMillis();
			System.out.println("�ڣ�"+sleepTime+"���̻߳�����:"+avaiFlag+",��ʱ��"+(endTime-startTime)/1000.0);
			Thread.sleep(sleepTime*1000);
			
			System.out.println("�ڣ�"+sleepTime+"���߳��������");
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
