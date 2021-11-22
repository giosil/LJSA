# LJSA - Light Java Scheduler Application

A job scheduler configurable through RESTful web services.

## Dependencies

**multi-rpc 2.0.0**

- `git clone https://github.com/giosil/multi-rpc.git` 
- `mvn clean install` - this will publish `multi-rpc-2.0.0.jar` in Maven local repository

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

## Database

![Schema](img/00_relational.png)

## Run LJSA GUI (org.dew.swingup.main.Main) from LJSA-gui (see gui folder) sub-project

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

## Launch LJSA GUI WebApp from LJSA-gui-web sub-project

### Dependencies

**wrapp**

- `git clone https://github.com/giosil/wrapp.git` 
- `mvn clean install` - this will produce `wrapp.war` in `target` directory

### Build and deploy web application with Wrapp

- Create if not exists `$HOME/cfg` directory
- Copy json files from `cfg` to `$HOME/cfg`
- Deploy `wrapp.war` in your application server
- `git clone https://github.com/giosil/LJSA.git` 
- `cd gui-web`
- `mvn clean install` - this will produce `wljsa.war` in `target` directory
- Launch `http://localhost:8080/wrapp`

### Define class

![Classes](img/11_cls.png)

### Define activity

![Activities](img/12_act.png)

### Schedule activity

![Schedule](img/13_jobs.png)

## Contributors

* [Giorgio Silvestris](https://github.com/giosil)
