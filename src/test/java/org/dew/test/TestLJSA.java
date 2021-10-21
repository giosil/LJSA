package org.dew.test;

import java.util.Properties;

import org.dew.ljsa.backend.util.BEConfig;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import org.quartz.impl.StdSchedulerFactory;

public 
class TestLJSA 
{  
  public static void main(String args[]) {
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
}
