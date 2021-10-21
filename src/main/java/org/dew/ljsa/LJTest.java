package org.dew.ljsa;

import java.io.PrintStream;

import java.util.List;
import java.util.Map;

/**
 * Implementazione test di ALJSAJob.
 */
public
class LJTest extends ALJSAJob
{
  public
  void execute(Schedulazione sched, OutputSchedulazione out)
    throws Exception
  {
    LJSAMap config = sched.getConfigurazione();
    LJSAMap params = sched.getParametri();
    
    String exception = params.getString("exception");
    int    sleep     = params.getInt("sleep");
    
    for(int i = 1; i < sleep; i++) {
      Thread.sleep(1000);
      if(boLJSAJobInterrupted) return;
    }
    
    if(exception != null && exception.length() > 0) {
      PrintStream ps = new PrintStream(out.createErrorFile(), true);
      ps.println("Exception:");
      ps.println(exception);
      
      out.setReport(exception);
      out.setErrorStatus();
      return;
    }
    else {
      PrintStream ps = new PrintStream(out.createOutputFile(), true);
      ps.println("Schedulazione:");
      ps.println();
      ps.println("Id Schedulazione = " + sched.getIdSchedulazione());
      ps.println("Id Servizio      = " + sched.getIdServizio());
      ps.println("Id Attivita      = " + sched.getIdAttivita());
      ps.println("Descrizione      = " + sched.getDescrizione());
      ps.println("Schedulazione    = " + sched.getSchedulazione());
      ps.println();
      ps.println("Configurazione:");
      ps.println();
      ps.println(config.buildInfoString());
      ps.println();
      ps.println("Parametri:");
      ps.println();
      ps.println(params.buildInfoString());
      ps.println();
      ps.println("Notifica:");
      ps.println();
      List<Map<String, Object>> listNotifica = sched.getNotifica();
      for(Map<String, Object> item : listNotifica) {
        Object event = item.get(ISchedulazione.sNOTIFICA_EVENTO);
        Object dest  = item.get(ISchedulazione.sNOTIFICA_DESTINAZIONE);
        ps.println(event + " -> " + dest);
      }
      
      out.setReport("Test completed.");
    }
  }
}
