import java.util.Date;
import java.util.concurrent.CountDownLatch;



public class SocketClinetThread extends Thread {
	private final int num = 1;
	private final long sleepTime = 100l;
	public void run() {
		CountDownLatch doneSignal = new CountDownLatch(num);

		long startTime = new Date().getTime();
		System.out.println("startTime:"+startTime);
		
		for(int i=0; i<num; i++){
			Thread t = new SocketWork(new String(i+""),doneSignal);
			t.start();
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("endTime:"+endTime);
		System.out.println("time£º"+((endTime-startTime)/1000.0));
		
	}

}
