import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class TriggerRunner {
	public static void main(String[] args) {
		try {
			// ①创建一个JobDetail实例，指定SimpleJob
			//JobDetail jobDetail = new JobDetail("job1_1", "jGroup1", Time.class);
			JobDetail jobDetail = new JobDetail("job1_1", "jGroup1", Time.class);
			// ②通过SimpleTrigger定义调度规则：马上启动，每2秒运行一次，共运行100次
			SimpleTrigger simpleTrigger = new SimpleTrigger("trigger1_1",
					"tgroup1");
			simpleTrigger.setStartTime(new Date());
			simpleTrigger.setRepeatInterval(2000);
			simpleTrigger.setRepeatCount(3);
			
			// ①创建一个JobDetail实例，指定SimpleJob
			JobDetail jobDetail2 = new JobDetail("job2_2", "jGroup2", Time2.class);
//			// ②通过SimpleTrigger定义调度规则：马上启动，每2秒运行一次，共运行100次
//			SimpleTrigger simpleTrigger2 = new SimpleTrigger("trigger2_2",
//					"tgroup2");
//			simpleTrigger2.setStartTime(new Date());
//			simpleTrigger2.setRepeatInterval(3000);
//			simpleTrigger2.setRepeatCount(3);
			
			//①-1：创建CronTrigger，指定组及名称
			CronTrigger cronTrigger = new CronTrigger("trigger1_2", "tgroup1");
			//CronExpression cexp = new CronExpression("0/5 * * * * ?");//①-2：定义Cron表达式
			//cronTrigger.setCronExpression("15 19 14 10 9 ? 2013");//①-3：设置Cron表达式
			cronTrigger.setCronExpression("00 10 * * * ? 2015");
			
			
			// ③通过SchedulerFactory获取一个调度器实例
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			System.out.println("size:"+schedulerFactory.getAllSchedulers().size());
			
			
			
			Date date1 = scheduler.scheduleJob(jobDetail, simpleTrigger);// ④ 注册并进行调度
			System.out.println("date1:"+date1+new Date());
			scheduler.start();// ⑤调度启动
			System.out.println(scheduler.getSchedulerName());
			//scheduler.pauseAll();
			Date date2 = scheduler.scheduleJob(jobDetail2, cronTrigger);// ④ 注册并进行调度
			System.out.println("date2:"+date2);
			//scheduler.resumeAll();
			//scheduler.pauseAll();
			//scheduler.deleteJob("job2_2", "jGroup2");
			//scheduler.resumeAll();
			scheduler.pauseTrigger("trigger2_2","tgroup2");// 停止触发器
			scheduler.unscheduleJob("trigger2_2","tgroup2");// 移
			
//			Scheduler scheduler2 = schedulerFactory.getScheduler();
//			scheduler2.scheduleJob(jobDetail2, simpleTrigger2);// ④ 注册并进行调度
//			scheduler2.start();// ⑤调度启动
//			scheduler2.pauseAll();
//			scheduler2.deleteJob("job2_2", "trigger2_2");
//			scheduler2.pauseAll();
			//scheduler2.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
