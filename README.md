# LJSA - Light Java Scheduler Application

A job scheduler configurable through RESTful web services.

## Build

- `git clone https://github.com/giosil/LJSA.git`
- `mvn clean install`

## Implement a Job

```java
import java.io.PrintStream;

import org.dew.ljsa.ALJSAJob;
import org.dew.ljsa.LJSAMap;
import org.dew.ljsa.OutputSchedulazione;
import org.dew.ljsa.Schedulazione;

public 
class ExampleLJSAJob extends ALJSAJob
{
  protected PrintStream psLog;
  
  @Override
  public
  void init(Schedulazione sched, OutputSchedulazione out)
    throws Exception
  {
    LJSAMap configurazione = sched.getConfigurazione();
    LJSAMap parametri      = sched.getParametri();
    
    psLog = new PrintStream(out.createReportFile("report.txt"), true);
  }
  
  @Override
  public 
  void execute(Schedulazione sched, OutputSchedulazione out)
    throws Exception 
  {
    psLog.println("Hello World.");
    
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
```

## Run LJSA GUI (org.dew.swingup.main.Main)

### Define class

![Classes](img/01_cls.png)

### Define activity

![Activities](img/02_act.png)

### Schedule activity

![Schedule](img/03_jobs.png)

### View Job logs

![Job Logs](img/04_jobs_log.png)

### Check Scheduler

![Scheduler](img/05_sched.png)

### Logs

![Logs](img/06_logs.png)

### File Manager

![File Manager](img/07_fm.png)

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
