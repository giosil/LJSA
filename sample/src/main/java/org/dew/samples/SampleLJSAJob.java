package org.dew.samples;

import java.io.PrintStream;

import org.dew.ljsa.ALJSAJob;
import org.dew.ljsa.LJSAMap;
import org.dew.ljsa.OutputSchedulazione;
import org.dew.ljsa.Schedulazione;

public 
class SampleLJSAJob extends ALJSAJob
{
  protected PrintStream psLog;
  
  protected String greeting;
  protected String name;
  
  @Override
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
  
  @Override
  public 
  void execute(Schedulazione sched, OutputSchedulazione out)
    throws Exception 
  {
    psLog.println(greeting + " " + name);
    
    out.setReport("Job completed.");
  }
  
  @Override
  public
  void destroy(Schedulazione sched, OutputSchedulazione out)
    throws Exception
  {
  }
  
  @Override
  public
  void exceptionOccurred(Throwable throwable)
    throws Exception
  {
    throwable.printStackTrace(psLog);
    psLog.println("Job aborted.");
  }
}
