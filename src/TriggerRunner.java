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
			// �ٴ���һ��JobDetailʵ����ָ��SimpleJob
			//JobDetail jobDetail = new JobDetail("job1_1", "jGroup1", Time.class);
			JobDetail jobDetail = new JobDetail("job1_1", "jGroup1", Time.class);
			// ��ͨ��SimpleTrigger������ȹ�������������ÿ2������һ�Σ�������100��
			SimpleTrigger simpleTrigger = new SimpleTrigger("trigger1_1",
					"tgroup1");
			simpleTrigger.setStartTime(new Date());
			simpleTrigger.setRepeatInterval(2000);
			simpleTrigger.setRepeatCount(3);
			
			// �ٴ���һ��JobDetailʵ����ָ��SimpleJob
			JobDetail jobDetail2 = new JobDetail("job2_2", "jGroup2", Time2.class);
//			// ��ͨ��SimpleTrigger������ȹ�������������ÿ2������һ�Σ�������100��
//			SimpleTrigger simpleTrigger2 = new SimpleTrigger("trigger2_2",
//					"tgroup2");
//			simpleTrigger2.setStartTime(new Date());
//			simpleTrigger2.setRepeatInterval(3000);
//			simpleTrigger2.setRepeatCount(3);
			
			//��-1������CronTrigger��ָ���鼰����
			CronTrigger cronTrigger = new CronTrigger("trigger1_2", "tgroup1");
			//CronExpression cexp = new CronExpression("0/5 * * * * ?");//��-2������Cron���ʽ
			//cronTrigger.setCronExpression("15 19 14 10 9 ? 2013");//��-3������Cron���ʽ
			cronTrigger.setCronExpression("00 10 * * * ? 2015");
			
			
			// ��ͨ��SchedulerFactory��ȡһ��������ʵ��
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			System.out.println("size:"+schedulerFactory.getAllSchedulers().size());
			
			
			
			Date date1 = scheduler.scheduleJob(jobDetail, simpleTrigger);// �� ע�Ტ���е���
			System.out.println("date1:"+date1+new Date());
			scheduler.start();// �ݵ�������
			System.out.println(scheduler.getSchedulerName());
			//scheduler.pauseAll();
			Date date2 = scheduler.scheduleJob(jobDetail2, cronTrigger);// �� ע�Ტ���е���
			System.out.println("date2:"+date2);
			//scheduler.resumeAll();
			//scheduler.pauseAll();
			//scheduler.deleteJob("job2_2", "jGroup2");
			//scheduler.resumeAll();
			scheduler.pauseTrigger("trigger2_2","tgroup2");// ֹͣ������
			scheduler.unscheduleJob("trigger2_2","tgroup2");// ��
			
//			Scheduler scheduler2 = schedulerFactory.getScheduler();
//			scheduler2.scheduleJob(jobDetail2, simpleTrigger2);// �� ע�Ტ���е���
//			scheduler2.start();// �ݵ�������
//			scheduler2.pauseAll();
//			scheduler2.deleteJob("job2_2", "trigger2_2");
//			scheduler2.pauseAll();
			//scheduler2.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
