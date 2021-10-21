package org.dew.ljsa;

import java.util.List;

import org.apache.log4j.Logger;

import org.dew.ljsa.backend.sched.LJSAScheduler;
import org.dew.ljsa.backend.util.MailManager;

/**
 * Classe utilizzata per gestire il timeout di una schedulazione.
 */
public
class TimeoutGuard extends Thread
{
  private transient Logger logger = Logger.getLogger(getClass());
  
  protected int timeout = 0;
  protected List<String> listNotifica;
  protected String subject;
  protected String message;
  protected Schedulazione schedulazione;
  
  public
  TimeoutGuard(Schedulazione schedulazione)
  {
    this.schedulazione = schedulazione;
    timeout = schedulazione.getTimeout();
    listNotifica = schedulazione.getNotificaTimeout();
    
    subject = "timeout " + schedulazione + "\n";
    message = "Timeout reached for " + schedulazione + " schedulation.\n";
  }
  
  public
  void run()
  {
    if(timeout <= 0) return;
    
    if(listNotifica == null || listNotifica.size() == 0) return;
    
    int iMinutes = 0;
    while(true) {
      try {
        Thread.sleep(60000);
        iMinutes++;
        if(iMinutes >= timeout) {
          notifyTimeout();
          break;
        }
      }
      catch(InterruptedException ex) {
        break;
      }
    }
  }
  
  public
  void notifyTimeout()
  {
    logger.warn("Timeout " + schedulazione);
    try {
      MailManager.send(listNotifica, subject, message);
    }
    catch(Exception ex) {
      logger.error("Exception during send notification timeout " + schedulazione, ex);
    }
    if(schedulazione.getFlagStopOnTimeout()) {
      try {
        LJSAScheduler.interrupt(schedulazione);
      }
      catch(Exception ex) {
        logger.error("Exception during interrupt " + schedulazione, ex);
      }
    }
  }
}
