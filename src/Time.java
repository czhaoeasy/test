import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class Time implements Job{
	//test 4 5 7
	public void execute(JobExecutionContext jobCtx)throws JobExecutionException {
		System.out.println(jobCtx.getTrigger().getName()+ " triggered. time is:" + (new Date()));
		}

}
