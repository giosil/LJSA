package org.dew.ljsa.backend.sched;

import org.apache.log4j.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job di servizio che aggiorna il log dello schedulatore.
 */
public
class JobUpdate implements Job
{
	protected transient Logger logger = Logger.getLogger(getClass());
	
	public
	void execute(JobExecutionContext jec)
		throws JobExecutionException
	{
		try {
			LJSAScheduler.unscheduleAllExpired();
			LJSAScheduler.updateLogSchedulatore();
		}
		catch(Exception ex) {
		  logger.error("Errore in JobUpdate.execute", ex);
			throw new JobExecutionException(ex, false);
		}
	}
}
