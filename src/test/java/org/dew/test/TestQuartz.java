package org.dew.test;

import java.sql.Timestamp;
import java.util.Properties;

import org.dew.ljsa.backend.util.BEConfig;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class TestQuartz {
  
  public static void main(String[] args) {
    try {
      System.out.println("Load config...");
      Properties config = BEConfig.loadProperties("quartz.cfg");
      
      System.out.println("Create SchedulerFactory instance...");
      SchedulerFactory schedulerFactory = new StdSchedulerFactory(config);
      
      System.out.println("Create Scheduler instance...");
      Scheduler scheduler = schedulerFactory.getScheduler();
      
      JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
          .withIdentity("TestJob", "Group")
          .storeDurably(true)
          .build();
      
      scheduler.addJob(jobDetail, true);
      
      Trigger triggerS = TriggerBuilder.newTrigger()
          .forJob(new JobKey("TestJob", "Group"))
          .withIdentity("Simple", "Group")
          .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMilliseconds(2 * 1000))
          .build();
      
      scheduler.scheduleJob(triggerS);
      
      /*
        Cron expression examples:
       
        0/10 * * * * ?      = Ogni 10 sec a partire da 0 sec
        0 0/5 * * * ?       = Ogni  5 min a partire da 0 min
        0 0/15 20-22 * * ?  = Ogni 15 min, dalle 20 alle 22
        0 30 19 * * ?       = Alle 19.30 di ogni giorno
        0 0 13,20 * * ?     = Alle 13.00 e alle 20.00 di ogni giorno
        0 0 21 15,20 * ?    = Alle 21.00 dei giorni 15 e 20 di ogni mese
        0 45 21 15 3 ? 2008 = Il 15/03/2008 alle ore 21.45
        0 0/30 7-19 ? * 2-7 = Ogni 30 min, dalle 7 alle 19, dal lun. al sab.
        0 15 10 ? * 6#3     = Alle 10.15, il terzo venerdi' (6) di ogni mese
        
       */
      
      Trigger triggerC = TriggerBuilder.newTrigger()
          .forJob(new JobKey("TestJob", "Group"))
          .withIdentity("Cron", "Group")
          .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
          .build();
      
      scheduler.scheduleJob(triggerC);
      
      System.out.println("Scheduler.start()...");
      scheduler.start();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  class TestJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      
      Trigger trigger = context.getTrigger();
      
      TriggerKey triggerKey = trigger.getKey();
      String triggerName = triggerKey.getName() + "," + triggerKey.getGroup();
      
      System.out.println(new Timestamp(System.currentTimeMillis()) + " TestJob.execute " + triggerName);
      
    }
    
  }
}
