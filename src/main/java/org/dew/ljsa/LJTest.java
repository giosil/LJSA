package org.dew.ljsa;

import java.io.PrintStream;

import org.json.JSON;

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
      PrintStream ps = new PrintStream(out.createOutputFile("test.json"), true);
      ps.println(JSON.stringify(sched));
      
      out.setReport("Test completed.");
    }
  }
}
