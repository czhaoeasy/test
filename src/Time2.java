import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class Time2 implements Job{
	
	public void execute(JobExecutionContext jobCtx)throws JobExecutionException {
		System.out.println(jobCtx.getTrigger().getName()+ " triggered. time2 is:" + (new Date()));
		}

}
