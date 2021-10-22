package org.dew.test;

import java.io.PrintStream;

import org.dew.ljsa.ALJSAJob;
import org.dew.ljsa.LJSAMap;
import org.dew.ljsa.OutputSchedulazione;
import org.dew.ljsa.Schedulazione;

public 
class ExampleLJSAJob extends ALJSAJob
{
  protected PrintStream psLog;
  
  protected String greeting;
  protected String name;
  
  public
  void init(Schedulazione sched, OutputSchedulazione out)
    throws Exception
  {
    LJSAMap configurazione = sched.getConfigurazione();
    LJSAMap parametri      = sched.getParametri();
    
    psLog = new PrintStream(out.createReportFile("report.txt"), true);
    
    greeting = configurazione.getString("greeting", "Hello");
    name     = parametri.getString("name", "World");
    
    psLog.println("greeting = " + greeting);
    psLog.println("name     = " + name);
  }
  
  public 
  void execute(Schedulazione sched, OutputSchedulazione out)
    throws Exception 
  {
    psLog.println(greeting + " " + name);
    
    out.setReport("Job completed.");
  }
  
  public
  void destroy(Schedulazione sched, OutputSchedulazione out)
    throws Exception
  {
  }
  
  public
  void exceptionOccurred(Throwable throwable)
    throws Exception
  {
    throwable.printStackTrace(psLog);
    psLog.println("Job aborted.");
  }
}
