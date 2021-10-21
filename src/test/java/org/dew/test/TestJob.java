package org.dew.test;

import java.sql.Timestamp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class TestJob implements Job {

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    
    Trigger trigger = context.getTrigger();
    
    TriggerKey triggerKey = trigger.getKey();
    
    String triggerName = triggerKey.getName() + "," + triggerKey.getGroup();
    
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
    System.out.println(timestamp + " TestJob.execute " + triggerName);
    
  }
  
}
