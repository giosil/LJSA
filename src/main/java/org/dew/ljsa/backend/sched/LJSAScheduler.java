package org.dew.ljsa.backend.sched;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import org.util.WUtil;

import org.dew.ljsa.ILJSAErrors;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.LJSAClassLoader;
import org.dew.ljsa.LJSARemoteJobExecutor;
import org.dew.ljsa.Schedulazione;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.QueryBuilder;

/**
 * Quartz scheduler manager.
 */
public
class LJSAScheduler
{
  private static Logger logger = Logger.getLogger(LJSAScheduler.class);
  
  // Configuration
  private static boolean     _boSchedulerStarted = false;
  private static String      _sInitException;
  private static int         _iDataSchedulazione = 0;
  private static int         _iOraSchedulazione  = 0;
  private static boolean     _boSleepingMode = false;
  private static boolean     _boForceSleepingFlag = false;
  private static int         _iRefresh = 60;
  private static String      _sLJSAUser;
  private static String      _sLJSAPassword;
  private static String      _sLJSAService;
  private static Set<String> _hServicesExcluded;
  
  private static Scheduler scheduler;
  
  private static List<Schedulazione> _schedulazioni = new ArrayList<Schedulazione>();
  
  private static final String sVERSION = "2.0";
  
  // [Remote]
  static {
    _sLJSAUser     = BEConfig.getProperty(BEConfig.sLJSA_CONF_USER, "ljsadmin");
    _sLJSAPassword = BEConfig.getProperty(BEConfig.sLJSA_CONF_PWD,  "dew2006");
  }
  
  public static
  void init(boolean boForceSleepingFlag)
  {
    _boForceSleepingFlag = boForceSleepingFlag;
    try {
      _sInitException = null;
      if(scheduler != null) {
        if(!scheduler.isShutdown()) {
          scheduler.shutdown(true);
        }
        scheduler = null;
      }
      _sLJSAUser         = BEConfig.getProperty(BEConfig.sLJSA_CONF_USER, "ljsadmin");
      _sLJSAPassword     = BEConfig.getProperty(BEConfig.sLJSA_CONF_PWD,  "dew2006");
      _boSleepingMode    = BEConfig.getBooleanProperty(BEConfig.sLJSA_CONF_SLEEPING, false);
      _iRefresh          = BEConfig.getIntProperty(BEConfig.sLJSA_CONF_REFRESH, 60);
      _sLJSAService      = BEConfig.getProperty(BEConfig.sLJSA_CONF_SERVICE);
      _hServicesExcluded = BEConfig.getHashSetProperty(BEConfig.sLJSA_CONF_SER_EXCLUDED);
      if(_boForceSleepingFlag) _boSleepingMode = false;
      if(_boSleepingMode) return;
      Properties quartzcfg = BEConfig.loadProperties("quartz.cfg");
      SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory(quartzcfg);
      scheduler = schedFact.getScheduler();
      _boSchedulerStarted = false;
      scheduleLJSAJobs();
      updateSchedulatori();
      logger.debug("Scheduler initialized");
    }
    catch(Exception ex) {
      _sInitException = ex.toString();
      logger.error("Exception in LJSAScheduler.init(" + boForceSleepingFlag + ")", ex);
    }
  }
  
  public static
  Scheduler getScheduler()
  {
    return scheduler;
  }
  
  public static
  String getVersion()
  {
    return sVERSION;
  }
  
  public static
  boolean isSleeper()
  {
    return _boSleepingMode;
  }
  
  public static
  boolean isStarted()
  {
    return _boSchedulerStarted;
  }
  
  public static
  List<Schedulazione> getSchedulazioni()
  {
    return _schedulazioni;
  }
  
  public static
  Schedulazione getSchedulazione(int idSchedulazione)
  {
    if(_schedulazioni == null) return null;
    for(Schedulazione schedulazione : _schedulazioni) {
      if(schedulazione.getIdSchedulazione() == idSchedulazione) {
        return schedulazione;
      }
    }
    return null;
  }
  
  public static
  String getStatus()
  {
    if(_boSleepingMode)          return "D"; // Disable
    if(_sInitException != null)  return "E"; // Exceptions
    if(scheduler == null)        return "N"; // Not initialized
    try {
      if(scheduler.isShutdown()) return "S"; // Shutdown
    }
    catch(SchedulerException ex) {
      logger.error("Exception in LJSAScheduler.getStatus()", ex);
    }
    if(_boSchedulerStarted) return "R"; // Running
    return "A"; // Alt
  }
  
  public static
  String getStatusDescription()
  {
    if(_boSleepingMode) {
      return "Schedulatore in modalit\340 disattiva (sleeping mode).";
    }
    if(_sInitException != null) {
      return "Errore durante l'inizializzazione: " + _sInitException;
    }
    if(scheduler == null) {
      return "Schedulatore non inizializzato.";
    }
    try {
      if(scheduler.isShutdown()) {
        return "Schedulatore distrutto (shutdown).";
      }
    }
    catch(SchedulerException ex) {
      logger.error("Exception in LJSAScheduler.getStatusDescription()", ex);
    }
    if(_boSchedulerStarted) {
      return "Schedulatore avviato.";
    }
    return "Schedulatore fermo.";
  }
  
  public static
  boolean loadSchedulazioni()
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    boolean boResult = false;
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      // Se la schedulazione di un determinato job da' luogo ad una eccezione
      // la schedulazione viene disabilitata. Per questo motivo si riporta
      // il caricamento delle schedulazioni in una transazione.
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      boResult = loadSchedulazioni(conn);
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in LJSAScheduler.loadSchedulazioni()", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return boResult;
  }
  
  public static
  boolean update()
    throws Exception
  {
    boolean boResult = false;
    BEConfig.reloadConfig();
    if(_boForceSleepingFlag && !_boSleepingMode) {
      boResult = loadSchedulazioni();
    }
    else {
      boolean boNewSleepingMode = BEConfig.getBooleanProperty(BEConfig.sLJSA_CONF_SLEEPING, false);
      if(_boSleepingMode != boNewSleepingMode) {
        if(checkRunningJobs()) {
          throw new Exception(ILJSAErrors.sTHERE_ARE_RUNNING_JOBS);
        }
        init(false);
        if(!boNewSleepingMode) {
          start();
        }
        boResult = true;
      }
      else if(!_boSleepingMode) {
        boResult = loadSchedulazioni();
      }
    }
    return boResult;
  }
  
  public static
  void updateLogSchedulatore()
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      // Aggiornamento del log
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      updateLogSchedulatore(conn);
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in LJSAScheduler.updateLogSchedulatore()", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
  }
  
  public static
  boolean start()
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    if(_sInitException != null) {
      throw new Exception(_sInitException);
    }
    if(scheduler == null) {
      throw new Exception("Scheduler not initialized");
    }
    if(_boSchedulerStarted) return false;
    if(scheduler.isShutdown()) return false;
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      Date oNow = new Date();
      _iDataSchedulazione = WUtil.toIntDate(oNow, 0);
      _iOraSchedulazione  = WUtil.toIntTime(oNow, 0);
      insertLogSchedulatore(conn, _iDataSchedulazione, _iOraSchedulazione);
      scheduler.start();
      loadSchedulazioni(conn);
      _boSchedulerStarted = true;
      logger.debug("Scheduler avviato in " + _iDataSchedulazione + ", " + _iOraSchedulazione);
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in LJSAScheduler.start()", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public static
  boolean schedule(Connection conn, int idSchedulazione)
    throws Exception
  {
    Schedulazione schedulazione = LJSAScheduler.readSchedulazione(conn, idSchedulazione);
    
    return schedule(schedulazione);
  }
  
  public static
  boolean schedule(Connection conn, int idSchedulazione, boolean boReplaceIfAlreadyScheduled)
    throws Exception
  {
    Schedulazione schedulazione = LJSAScheduler.readSchedulazione(conn, idSchedulazione);
    
    return schedule(schedulazione, boReplaceIfAlreadyScheduled);
  }
  
  public static
  boolean schedule(Schedulazione schedulazione)
    throws Exception
  {
    return schedule(schedulazione, false);
  }
  
  public static
  boolean schedule(Schedulazione schedulazione, boolean boReplaceIfAlreadyScheduled)
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    
    String sIdServizio = schedulazione.getIdServizio();
    checkService(sIdServizio);
    
    if(schedulazione.isExpired()) {
      unschedule(schedulazione);
      throw new Exception(ILJSAErrors.sSCHED_EXPIRED);
    }
    if(!schedulazione.isEnabled()) {
      unschedule(schedulazione);
      throw new Exception(ILJSAErrors.sSCHED_NOT_ENABLED);
    }
    
    int index = _schedulazioni.indexOf(schedulazione);
    if(index >= 0) {
      Schedulazione s = (Schedulazione) _schedulazioni.get(index);
      if(boReplaceIfAlreadyScheduled || s.isUpdated(schedulazione)) {
        if(isRunning(s)) {
          logger.debug(s + " is running. Try to interrupt...");
          scheduler.interrupt(new JobKey(s.getJobName(), s.getJobGroup()));
          logger.debug(s + " interrupt invoked.");
        }
        // Si rimuove la schedulazione vecchia
        scheduler.unscheduleJob(new TriggerKey(s.getTriggerName(), s.getTriggerGroup()));
        scheduler.deleteJob(new JobKey(s.getJobName(), s.getJobGroup()));
        logger.debug(s + " unscheduled");
        // Si schedula la schedulazione modificata
        try{
          scheduler.addJob(buildJobDetail(schedulazione), true);
          scheduler.scheduleJob(buildTrigger(schedulazione));
        }
        catch(java.text.ParseException pex) {
          throw new Exception(ILJSAErrors.sINVALID_SCHEDULATION);
        }
        // Si sostituisce la vecchia schedulazione con quella modificata
        _schedulazioni.set(index, schedulazione);
        logger.debug(schedulazione + " scheduled (update)");
        return true;
      }
    }
    else {
      try{
        scheduler.addJob(buildJobDetail(schedulazione), true);
        scheduler.scheduleJob(buildTrigger(schedulazione));
      }
      catch(java.text.ParseException pex) {
        throw new Exception(ILJSAErrors.sINVALID_SCHEDULATION);
      }
      _schedulazioni.add(schedulazione);
      logger.debug(schedulazione + " scheduled");
      return true;
    }
    return false;
  }
  
  public static
  int unscheduleAllExpired()
    throws Exception
  {
    int iResult = 0;
    for(int i = 0; i < _schedulazioni.size(); i++) {
      Schedulazione sched = (Schedulazione) _schedulazioni.get(i);
      String sIdServizio  = sched.getIdServizio();
      if(sIdServizio != null && sIdServizio.length() > 0) {
        if(_sLJSAService != null && _sLJSAService.length() > 0 && !_sLJSAService.equals(sIdServizio)) {
          continue;
        }
        if(_hServicesExcluded != null && _hServicesExcluded.contains(sIdServizio)) {
          continue;
        }
      }
      if(sched.isExpired()) {
        unschedule(sched, false);
        iResult++;
      }
    }
    return iResult;
  }
  
  public static
  boolean unschedule(Schedulazione schedulazione)
    throws Exception
  {
    return unschedule(schedulazione, true);
  }
  
  public static
  boolean unschedule(Schedulazione schedulazione, boolean boInterrupt)
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    int index = _schedulazioni.indexOf(schedulazione);
    if(index < 0) {
      // [Remote]
      Class<? extends Job> classe = schedulazione.getClasseAttivita();
      if(classe != null && classe.equals(LJSARemoteJobExecutor.class)) {
        String sResult = LJSARemoteJobExecutor.invokeInterrupt(schedulazione.getIdSchedulazione());
        logger.debug(schedulazione + " invokeInterrupt(" + schedulazione.getIdSchedulazione() + ") -> " + sResult);
        return true;
      }
      return false;
    }
    
    Schedulazione sched = (Schedulazione) _schedulazioni.get(index);
    if(isRunning(sched)) {
      if(boInterrupt) {
        logger.debug(sched + " is running. Try to interrupt...");
        scheduler.interrupt(new JobKey(sched.getJobName(), sched.getJobGroup()));
        logger.debug(sched + " interrupt invoked.");
      }
      else {
        logger.debug(sched + " is running. It can't unschedule...");
        return false;
      }
    }
    scheduler.unscheduleJob(new TriggerKey(sched.getTriggerName(), sched.getTriggerGroup()));
    scheduler.deleteJob(new JobKey(sched.getJobName(), sched.getJobGroup()));
    _schedulazioni.remove(sched);
    logger.debug(sched + " unscheduled");
    return true;
  }
  
  public static
  void remove(Schedulazione schedulazione)
  {
    if(scheduler == null) {
      if(_schedulazioni != null) {
        _schedulazioni.remove(schedulazione);
      }
      logger.debug(schedulazione + " removed (scheduler is null)");
      return;
    }
    try {
      scheduler.unscheduleJob(new TriggerKey(schedulazione.getTriggerName(), schedulazione.getTriggerGroup()));
      scheduler.deleteJob(new JobKey(schedulazione.getJobName(), schedulazione.getJobGroup()));
    }
    catch(Exception ex) {
      logger.error("Exception in LJSAScheduler.remove(" + schedulazione + ")", ex);
    }
    _schedulazioni.remove(schedulazione);
    logger.debug(schedulazione + " removed");
  }
  
  public static
  boolean interrupt(Schedulazione schedulazione)
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    int iIndex = _schedulazioni.indexOf(schedulazione);
    if(iIndex < 0) {
      // [Remote]
      Class<? extends Job> classe = schedulazione.getClasseAttivita();
      if(classe != null && classe.equals(LJSARemoteJobExecutor.class)) {
        String result = LJSARemoteJobExecutor.invokeInterrupt(schedulazione.getIdSchedulazione());
        logger.debug(schedulazione + " invokeInterrupt(" + schedulazione.getIdSchedulazione() + ") -> " + result);
        return true;
      }
      return false;
    }
    Schedulazione sched = (Schedulazione) _schedulazioni.get(iIndex);
    if(isRunning(sched)) {
      logger.debug(sched + " is running. Try to interrupt...");
      scheduler.interrupt(new JobKey(sched.getJobName(), sched.getJobGroup()));
      logger.debug(sched + " interrupt invoked.");
    }
    return false;
  }
  
  public static
  boolean stop()
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    if(!_boSchedulerStarted) return false;
    if(scheduler.isShutdown()) return false;
    scheduler.pauseAll();
    _boSchedulerStarted = false;
    logger.debug("Scheduler fermato (pause)");
    return true;
  }
  
  public static
  boolean setSleepingMode(boolean boSleepingMode)
    throws Exception
  {
    boolean boResult = false;
    if(boSleepingMode) {
      boResult = shutdown();
      BEConfig.replaceOrAddEntryValue(BEConfig.sLJSA_CONF_SLEEPING, "true");
      _boSleepingMode = true;
      logger.debug("Istanza di LJSA messa in sleeping mode.");
    }
    else if(!_boSchedulerStarted && _boSleepingMode) {
      BEConfig.replaceOrAddEntryValue(BEConfig.sLJSA_CONF_SLEEPING, "false");
      _boSleepingMode = false;
      BEConfig.reloadConfig();
      LJSAScheduler.init(false);
      boResult = LJSAScheduler.start();
      logger.debug("Istanza di LJSA passata da sleeping mode a running con persistenza.");
    }
    return boResult;
  }
  
  public static
  boolean shutdown()
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    if(scheduler == null) return false;
    if(scheduler.isShutdown()) return false;
    scheduler.shutdown(false);
    scheduler = null;
    _boSchedulerStarted = false;
    logger.debug("Scheduler distrutto (shutdown)");
    return true;
  }
  
  public static
  boolean autenticathe(String sUserName, String sPassword)
    throws Exception
  {
    if(sUserName == null || !sUserName.equals(_sLJSAUser)) {
      return false;
    }
    if(sPassword == null || !sPassword.equals(_sLJSAPassword)) {
      return false;
    }
    return true;
  }
  
  public static
  void checkAuthorization(String sUserName, String sPassword)
    throws Exception
  {
    if(sUserName == null || !sUserName.equals(_sLJSAUser)) {
      throw new Exception(ILJSAErrors.sNOT_AUTHORIZED);
    }
    if(sPassword == null || !sPassword.equals(_sLJSAPassword)) {
      throw new Exception(ILJSAErrors.sNOT_AUTHORIZED);
    }
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
  }
  
  public static
  void checkAuthorization(String sUserName, String sPassword, boolean boCheckSleepingMode)
    throws Exception
  {
    if(sUserName == null || !sUserName.equals(_sLJSAUser)) {
      throw new Exception(ILJSAErrors.sNOT_AUTHORIZED);
    }
    if(sPassword == null || !sPassword.equals(_sLJSAPassword)) {
      throw new Exception(ILJSAErrors.sNOT_AUTHORIZED);
    }
    if(boCheckSleepingMode && _boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
  }
  
  public static
  void checkService(String sIdServizio)
    throws Exception
  {
    if(sIdServizio == null || sIdServizio.length() == 0) return;
    if(_sLJSAService != null && _sLJSAService.length() > 0) {
      if(!_sLJSAService.equals(sIdServizio)) {
        throw new Exception(ILJSAErrors.sSERVICE_NOT_MANAGED);
      }
    }
    if(_hServicesExcluded != null && _hServicesExcluded.contains(sIdServizio)) {
      throw new Exception(ILJSAErrors.sSERVICE_NOT_MANAGED);
    }
  }
  
  public static
  boolean checkRunningJobs()
    throws Exception
  {
    List<JobExecutionContext> listCurrentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
    for(JobExecutionContext jobExecutionContext : listCurrentlyExecutingJobs) {
      JobDetail jobDetail  = jobExecutionContext.getJobDetail();
      String sJobName      = jobDetail.getKey().getName();
      // idSchedulazione + ":" + idServizio + ":" + idAttivita;
      if(sJobName != null && sJobName.indexOf(':') > 0) {
        return true;
      }
    }
    return false;
  }
  
  public static
  boolean checkRunningJobs(String sIdServizio, String sIdAttivita)
    throws Exception
  {
    List<JobExecutionContext> listCurrentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
    for(JobExecutionContext jobExecutionContext : listCurrentlyExecutingJobs) {
      JobDetail jobDetail  = jobExecutionContext.getJobDetail();
      String sJobName      = jobDetail.getKey().getName();
      // idSchedulazione + ":" + idServizio + ":" + idAttivita;
      if(sJobName != null && sJobName.indexOf(":" + sIdServizio + ":" + sIdAttivita) > 0) {
        return true;
      }
    }
    return false;
  }
  
  public static
  int countInternalJobs()
    throws Exception
  {
    int iResult = 0;
    if(scheduler == null) return iResult;
    List<String> asTriggerGroupNames = scheduler.getTriggerGroupNames();
    if(asTriggerGroupNames == null) return iResult;
    for(int i = 0; i < asTriggerGroupNames.size(); i++) {
      String sTriggerGroupName = asTriggerGroupNames.get(i);
      Set<TriggerKey> setTriggerKey = scheduler.getTriggerKeys(org.quartz.impl.matchers.GroupMatcher.groupEquals(sTriggerGroupName));
      if(setTriggerKey == null) continue;
      Iterator<TriggerKey> iterator = setTriggerKey.iterator();
      while(iterator.hasNext()) {
        TriggerKey triggerKey = iterator.next();
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if(trigger != null) iResult++;
      }
    }
    return iResult;
  }
  
  public static
  List<List<Object>> internal_JobTable()
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    List<Object> listHeader = new ArrayList<Object>(4);
    listHeader.add("Group");
    listHeader.add("Name");
    listHeader.add("Type");
    listHeader.add("Info");
    listResult.add(listHeader);
    if(scheduler == null) return listResult;
    
    List<String> asTriggerGroupNames = scheduler.getTriggerGroupNames();
    if(asTriggerGroupNames == null) return listResult;
    
    Collections.sort(asTriggerGroupNames);
    for(int i = 0; i < asTriggerGroupNames.size(); i++) {
      String sTriggerGroupName = asTriggerGroupNames.get(i);
      Set<TriggerKey> setTriggerKey = scheduler.getTriggerKeys(org.quartz.impl.matchers.GroupMatcher.groupEquals(sTriggerGroupName));
      if(setTriggerKey == null) continue;
      Iterator<TriggerKey> iterator = setTriggerKey.iterator();
      while(iterator.hasNext()) {
        TriggerKey triggerKey = iterator.next();
        
        String  sTriggerName = triggerKey.getName();
        Trigger trigger      = scheduler.getTrigger(triggerKey);
        String  sTriggerType = getTriggerType(trigger);
        String  sTriggerInfo = getTriggerInfo(trigger);
        
        List<Object> record = new ArrayList<Object>(4);
        record.add(sTriggerGroupName);
        record.add(sTriggerName);
        record.add(sTriggerType);
        record.add(sTriggerInfo);
        
        listResult.add(record);
      }
    }
    return listResult;
  }
  
  public static
  List<List<Object>> internal_CurrentlyExecutingJobs()
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    List<Object> listHeader = new ArrayList<Object>(4);
    listHeader.add("Group");
    listHeader.add("Name");
    listHeader.add("Type");
    listHeader.add("Info");
    listResult.add(listHeader);
    if(scheduler == null) return listResult;
    
    List<JobExecutionContext> listCurrentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
    for(JobExecutionContext jobExecutionContext : listCurrentlyExecutingJobs) {
      Trigger trigger = jobExecutionContext.getTrigger();
      
      List<Object> record = new ArrayList<Object>(4);
      record.add(trigger.getKey().getGroup());
      record.add(trigger.getKey().getName());
      record.add(getTriggerType(trigger));
      record.add(getTriggerInfo(trigger));
      
      listResult.add(record);
    }
    return listResult;
  }
  
  public static
  boolean isRunning(Schedulazione schedulazione)
    throws Exception
  {
    if(scheduler == null) return false;
    String sSchedJobName  = schedulazione.getJobName();
    String sSchedJobGroup = schedulazione.getJobGroup();
    List<JobExecutionContext> listCurrentlyExecutingJobs = scheduler.getCurrentlyExecutingJobs();
    for(JobExecutionContext jobExecutionContext : listCurrentlyExecutingJobs) {
      JobDetail jobDetail  = jobExecutionContext.getJobDetail();
      String sJobGroupName = jobDetail.getKey().getGroup();
      String sJobName = jobDetail.getKey().getName();
      if(sJobName.equals(sSchedJobName) && sJobGroupName.equals(sSchedJobGroup)) {
        return true;
      }
    }
    return false;
  }
  
  public static
  String getIdSchedulatore()
  {
    String result = "LJSA";
    if(_sLJSAService != null && _sLJSAService.length() > 0) {
      result += "-" + _sLJSAService;
    }
    return result;
  }
  
  private static
  String getTriggerType(Trigger trigger)
  {
    if(trigger instanceof SimpleTrigger) return "Simple";
    if(trigger instanceof CronTrigger)   return "Cron";
    return "Unknow";
  }
  
  private static
  String getTriggerInfo(Trigger trigger)
  {
    if(trigger instanceof SimpleTrigger) {
      int  repeatCount    = ((SimpleTrigger) trigger).getRepeatCount();
      long repeatInterval = ((SimpleTrigger) trigger).getRepeatInterval();
      return "repeatCount = " + repeatCount + ",repeatInterval = " + repeatInterval;
    }
    else if(trigger instanceof CronTrigger) {
      return "cronExpression = " + ((CronTrigger) trigger).getCronExpression();
    }
    return "";
  }
  
  private static
  void updateSchedulatori()
    throws Exception
  {
    Connection conn = null;
    PreparedStatement pstm = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("UPDATE LJSA_SCHEDULATORI SET URL_SERVIZIO=?,ATTIVO=? WHERE ID_SCHEDULATORE=?");
      pstm.setString(1, BEConfig.getLJSAWebServices());
      pstm.setString(2, QueryBuilder.decodeBoolean(true));
      pstm.setString(3, getIdSchedulatore());
      int iRows = pstm.executeUpdate();
      if(iRows == 0) {
        pstm.close();
        
        pstm = conn.prepareStatement("INSERT INTO LJSA_SCHEDULATORI(ID_SCHEDULATORE,URL_SERVIZIO,ATTIVO) VALUES(?,?,?)");
        pstm.setString(1, getIdSchedulatore());
        pstm.setString(2, BEConfig.getLJSAWebServices());
        pstm.setString(3, QueryBuilder.decodeBoolean(true));
        pstm.executeUpdate();
      }
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in LJSAScheduler.updateSchedulatori()", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
  }
  
  private static
  void insertLogSchedulatore(Connection conn, int iDataSchedulazione, int iOraSchedulazione)
    throws Exception
  {
    String sSQL_D = "DELETE FROM LJSA_LOG_SCHEDULATORE WHERE ID_SCHEDULATORE=? AND DATA_SCHEDULAZIONE=? AND ORA_SCHEDULAZIONE=?";
    String sSQL_I = "INSERT INTO LJSA_LOG_SCHEDULATORE(ID_SCHEDULATORE,DATA_SCHEDULAZIONE,ORA_SCHEDULAZIONE,DATA_AGGIORNAMENTO,ORA_AGGIORNAMENTO) VALUES (?,?,?,?,?)";
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL_D);
      pstm.setString(1, getIdSchedulatore());
      pstm.setInt(2, iDataSchedulazione);
      pstm.setInt(3, iOraSchedulazione);
      pstm.executeUpdate();
      
      ConnectionManager.close(pstm);
      pstm = conn.prepareStatement(sSQL_I);
      pstm.setString(1, getIdSchedulatore());
      pstm.setInt(2, iDataSchedulazione);
      pstm.setInt(3, iOraSchedulazione);
      pstm.setInt(4, iDataSchedulazione);
      pstm.setInt(5, iOraSchedulazione);
      pstm.executeUpdate();
    }
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  private static
  JobDetail buildJobDetail(Schedulazione schedulazione)
    throws Exception
  {
    JobBuilder jobBuilder = JobBuilder.newJob(schedulazione.getClasseAttivita())
        .withIdentity(schedulazione.getJobName(), schedulazione.getJobGroup())
        .storeDurably(true);
    JobDetail jobDetail = jobBuilder.build();
    JobDataMap jdm = jobDetail.getJobDataMap();
    jdm.put("#schedulazione", schedulazione);
    return jobDetail;
  }
  
  private static
  Trigger buildTrigger(Schedulazione schedulazione)
    throws Exception
  {
    Trigger trigger = null;
    String sSchedulazione = schedulazione.getSchedulazione();
    if(sSchedulazione != null && sSchedulazione.trim().toUpperCase().startsWith("NOW")) {
      int iSlash = sSchedulazione.indexOf('/');
      if(iSlash > 0) {
        int iRepeatInterval = 60000;
        String sRepeatInterval = null;
        if(iSlash < sSchedulazione.length() - 1) {
          sRepeatInterval = sSchedulazione.substring(iSlash + 1).trim();
          iRepeatInterval = Integer.parseInt(sRepeatInterval) * 1000;
        }
        trigger = TriggerBuilder.newTrigger()
            .forJob(new JobKey(schedulazione.getJobName(), schedulazione.getJobGroup()))
            .withIdentity(schedulazione.getTriggerName(), schedulazione.getTriggerGroup())
            .startNow()
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMilliseconds(iRepeatInterval))
            .build();
      }
      else {
        trigger = TriggerBuilder.newTrigger()
            .forJob(new JobKey(schedulazione.getJobName(), schedulazione.getJobGroup()))
            .withIdentity(schedulazione.getTriggerName(), schedulazione.getTriggerGroup())
            .startNow()
            .build();
      }
    }
    else {
      trigger = TriggerBuilder.newTrigger()
          .forJob(new JobKey(schedulazione.getJobName(), schedulazione.getJobGroup()))
          .withIdentity(schedulazione.getTriggerName(), schedulazione.getTriggerGroup())
          .withSchedule(CronScheduleBuilder.cronSchedule(sSchedulazione))
          .build();
    }
    return trigger;
  }
  
  private static
  void scheduleLJSAJobs()
    throws Exception
  {
    String sCleanCron = BEConfig.getProperty(BEConfig.sLJSA_CONF_CLEAN_CRON,  "0 0 6 * * ?");
    if(sCleanCron != null && sCleanCron.length() < 8) sCleanCron = "0 0 6 * * ?";
    
    JobDetail jobDetail;
    Trigger trigger;
    
    // Schedulazione del job che aggiorna lo schedulatore
    if(_iRefresh > 0) {
      jobDetail = JobBuilder.newJob(JobUpdate.class)
          .withIdentity("Update", "LJSA")
          .storeDurably(true)
          .build();
      scheduler.addJob(jobDetail, true);
      
      trigger = TriggerBuilder.newTrigger()
          .forJob(new JobKey("Update", "LJSA"))
          .withIdentity("Update", "LJSA")
          .startNow()
          .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInMilliseconds(_iRefresh * 60 * 1000))
          .build();
      scheduler.scheduleJob(trigger);
    }
    
    // Schedulazione del job di retention log
    jobDetail = JobBuilder.newJob(JobClean.class)
        .withIdentity("Clean", "LJSA")
        .storeDurably(true)
        .build();
    scheduler.addJob(jobDetail, true);
    
    trigger = TriggerBuilder.newTrigger()
        .forJob(new JobKey("Clean", "LJSA"))
        .withIdentity("Clean", "LJSA")
        .withSchedule(CronScheduleBuilder.cronSchedule(sCleanCron))
        .build();
    scheduler.scheduleJob(trigger);
  }
  
  public static
  Schedulazione readSchedulazione(Connection conn, int iIdSchedulazione)
    throws Exception
  {
    Schedulazione schedulazione = null;
    String sSQL = "SELECT ";
    sSQL += "S.ID_SCHEDULAZIONE,";
    sSQL += "S.ID_SERVIZIO,";
    sSQL += "S.ID_ATTIVITA,";
    sSQL += "S.DESCRIZIONE,";
    sSQL += "S.SCHEDULAZIONE,";
    sSQL += "S.ID_CREDENZIALE_INS,";
    sSQL += "S.DATA_INSERIMENTO,";
    sSQL += "S.ORA_INSERIMENTO,";
    sSQL += "S.ID_CREDENZIALE_AGG,";
    sSQL += "S.DATA_AGGIORNAMENTO,";
    sSQL += "S.ORA_AGGIORNAMENTO,";
    sSQL += "S.STATO,";
    sSQL += "S.INIZIOVALIDITA,";
    sSQL += "S.FINEVALIDITA,";
    sSQL += "A.CLASSE ";
    sSQL += "FROM LJSA_SCHEDULAZIONI S,LJSA_ATTIVITA A ";
    sSQL += "WHERE S.ID_SERVIZIO=A.ID_SERVIZIO AND S.ID_ATTIVITA=A.ID_ATTIVITA ";
    sSQL += "AND S.ID_SCHEDULAZIONE=?";
    LJSAClassLoader ljsaClassLoader = new LJSAClassLoader();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sIdServizio       = rs.getString("ID_SERVIZIO");
        String sIdAttivita       = rs.getString("ID_ATTIVITA");
        String sDescrizione      = rs.getString("DESCRIZIONE");
        String sSchedulazione    = rs.getString("SCHEDULAZIONE");
        String sIdCredenzialeIns = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento     = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento      = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento   = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento    = rs.getInt("ORA_AGGIORNAMENTO");
        String sStato            = rs.getString("STATO");
        int iInizioValidita      = rs.getInt("INIZIOVALIDITA");
        int iFineValidita        = rs.getInt("FINEVALIDITA");
        String sClasse           = rs.getString("CLASSE");
        Class<? extends Job> classe = null;
        // [Remote]
        String sRemoteObject = null;
        if(sClasse != null && (sClasse.startsWith("java:") || sClasse.startsWith("/"))) {
          classe = LJSARemoteJobExecutor.class;
          sRemoteObject = sClasse;
        }
        else {
          try {
            classe = ljsaClassLoader.loadJobClass(sClasse);
          }
          catch(ClassNotFoundException cnfex) {
            throw new Exception(ILJSAErrors.sINVALID_CLASS + sClasse + " (not found)");
          }
          if(classe == null) {
            throw new Exception(ILJSAErrors.sINVALID_CLASS + sClasse);
          }
        }
        schedulazione = new Schedulazione();
        schedulazione.setIdSchedulazione(iIdSchedulazione);
        schedulazione.setIdServizio(sIdServizio);
        schedulazione.setIdAttivita(sIdAttivita);
        schedulazione.setClasseAttivita(classe);
        schedulazione.setDescrizione(sDescrizione);
        schedulazione.setSchedulazione(sSchedulazione);
        schedulazione.setIdCredenzialeIns(sIdCredenzialeIns);
        schedulazione.setDataInserimento(iDataInserimento);
        schedulazione.setOraInserimento(iOraInserimento);
        schedulazione.setIdCredenzialeAgg(sIdCredenzialeAgg);
        schedulazione.setDataAggiornamento(iDataAggiornamento);
        schedulazione.setOraAggiornamento(iOraAggiornamento);
        schedulazione.setStato(sStato);
        schedulazione.setInizioValidita(iInizioValidita);
        schedulazione.setFineValidita(iFineValidita);
        Map<String, Object> mapParametri = readParametri(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        schedulazione.setParametri(mapParametri);
        Map<String, Object> mapConfig = readConfigurazione(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        // [Remote]
        if(sRemoteObject != null && sRemoteObject.length() > 0) {
          if(mapConfig == null) mapConfig = new HashMap<String, Object>();
          mapConfig.put("remote.object", sClasse);
        }
        schedulazione.setConfigurazione(mapConfig);
        List<Map<String, Object>> listNotifica = readNotifica(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        schedulazione.setNotifica(listNotifica);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return schedulazione;
  }
  
  protected static
  List<Schedulazione> readSchedulazioni(Connection conn)
    throws Exception
  {
    List<Schedulazione> listResult = new ArrayList<Schedulazione>();
    
    String sSQL = "SELECT ";
    sSQL += "S.ID_SCHEDULAZIONE,";
    sSQL += "S.ID_SERVIZIO,";
    sSQL += "S.ID_ATTIVITA,";
    sSQL += "S.DESCRIZIONE,";
    sSQL += "S.SCHEDULAZIONE,";
    sSQL += "S.ID_CREDENZIALE_INS,";
    sSQL += "S.DATA_INSERIMENTO,";
    sSQL += "S.ORA_INSERIMENTO,";
    sSQL += "S.ID_CREDENZIALE_AGG,";
    sSQL += "S.DATA_AGGIORNAMENTO,";
    sSQL += "S.ORA_AGGIORNAMENTO,";
    sSQL += "S.STATO,";
    sSQL += "S.INIZIOVALIDITA,";
    sSQL += "S.FINEVALIDITA,";
    sSQL += "A.CLASSE ";
    sSQL += "FROM LJSA_SCHEDULAZIONI S,LJSA_ATTIVITA A ";
    sSQL += "WHERE S.ID_SERVIZIO=A.ID_SERVIZIO AND S.ID_ATTIVITA=A.ID_ATTIVITA ";
    sSQL += "AND S.FINEVALIDITA>=? AND A.ATTIVO=? AND S.STATO<>?";
    if(_sLJSAService != null && _sLJSAService.length() > 0) {
      sSQL += " AND S.ID_SERVIZIO=?";
    }
    LJSAClassLoader ljsaClassLoader = new LJSAClassLoader();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1,    WUtil.toIntDate(new Date(), 0));
      pstm.setString(2, QueryBuilder.decodeBoolean(true));
      pstm.setString(3, ISchedulazione.sSTATO_DISATTIVATA);
      if(_sLJSAService != null && _sLJSAService.length() > 0) {
        pstm.setString(4, _sLJSAService);
      }
      rs = pstm.executeQuery();
      while(rs.next()) {
        int iIdSchedulazione     = rs.getInt("ID_SCHEDULAZIONE");
        String sIdServizio       = rs.getString("ID_SERVIZIO");
        if(_hServicesExcluded != null && _hServicesExcluded.contains(sIdServizio)) {
          continue;
        }
        String sIdAttivita       = rs.getString("ID_ATTIVITA");
        String sDescrizione      = rs.getString("DESCRIZIONE");
        String sSchedulazione    = rs.getString("SCHEDULAZIONE");
        String sIdCredenzialeIns = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento     = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento      = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento   = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento    = rs.getInt("ORA_AGGIORNAMENTO");
        String sStato            = rs.getString("STATO");
        int iInizioValidita      = rs.getInt("INIZIOVALIDITA");
        int iFineValidita        = rs.getInt("FINEVALIDITA");
        String sClasse           = rs.getString("CLASSE");
        Class<? extends Job> classe = null;
        // [Remote]
        String sRemoteObject = null;
        if(sClasse != null && (sClasse.startsWith("java:") || sClasse.startsWith("/"))) {
          classe = LJSARemoteJobExecutor.class;
          sRemoteObject = sClasse;
        }
        else {
          try {
            classe = ljsaClassLoader.loadJobClass(sClasse);
          }
          catch(ClassNotFoundException cnfex) {
            cnfex.printStackTrace();
          }
          if(classe == null) continue;
        }
        
        Schedulazione schedulazione = new Schedulazione();
        schedulazione.setIdSchedulazione(iIdSchedulazione);
        schedulazione.setIdServizio(sIdServizio);
        schedulazione.setIdAttivita(sIdAttivita);
        schedulazione.setClasseAttivita(classe);
        schedulazione.setDescrizione(sDescrizione);
        schedulazione.setSchedulazione(sSchedulazione);
        schedulazione.setIdCredenzialeIns(sIdCredenzialeIns);
        schedulazione.setDataInserimento(iDataInserimento);
        schedulazione.setOraInserimento(iOraInserimento);
        schedulazione.setIdCredenzialeAgg(sIdCredenzialeAgg);
        schedulazione.setDataAggiornamento(iDataAggiornamento);
        schedulazione.setOraAggiornamento(iOraAggiornamento);
        schedulazione.setStato(sStato);
        schedulazione.setInizioValidita(iInizioValidita);
        schedulazione.setFineValidita(iFineValidita);
        Map<String, Object> mapParametri = readParametri(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        schedulazione.setParametri(mapParametri);
        Map<String, Object> mapConfig = readConfigurazione(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        // [Remote]
        if(sRemoteObject != null && sRemoteObject.length() > 0) {
          if(mapConfig == null) mapConfig = new HashMap<String, Object>();
          mapConfig.put("remote.object", sClasse);
        }
        schedulazione.setConfigurazione(mapConfig);
        List<Map<String, Object>> listNotifica = readNotifica(conn, iIdSchedulazione, sIdServizio, sIdAttivita);
        schedulazione.setNotifica(listNotifica);
        
        listResult.add(schedulazione);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  Map<String, Object> readParametri(Connection conn, int iIdSchedulazione, String sIdServizio, String sIdAttivita)
    throws Exception
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT PARAMETRO,PREDEFINITO FROM LJSA_ATTIVITA_PARAMETRI WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sIdServizio);
      pstm.setString(2, sIdAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sParametro   = rs.getString("PARAMETRO");
        String sPredefinito = rs.getString("PREDEFINITO");
        if(sParametro != null && sPredefinito != null) {
          mapResult.put(sParametro, sPredefinito);
        }
      }
      
      ConnectionManager.close(rs, pstm);
      sSQL = "SELECT PARAMETRO,VALORE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sParametro = rs.getString("PARAMETRO");
        String sValore    = rs.getString("VALORE");
        mapResult.put(sParametro, sValore);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return mapResult;
  }
  
  protected static
  Map<String, Object> readConfigurazione(Connection conn, int iIdSchedulazione, String sIdServizio, String sIdAttivita)
    throws Exception
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT OPZIONE,PREDEFINITO FROM LJSA_ATTIVITA_CONF WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sIdServizio);
      pstm.setString(2, sIdAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sOpzione     = rs.getString("OPZIONE");
        String sPredefinito = rs.getString("PREDEFINITO");
        if(sOpzione != null && sPredefinito != null) {
          mapResult.put(sOpzione, sPredefinito);
        }
      }
      
      ConnectionManager.close(rs, pstm);
      sSQL = "SELECT OPZIONE,VALORE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sOpzione = rs.getString("OPZIONE");
        String sValore  = rs.getString("VALORE");
        mapResult.put(sOpzione, sValore);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return mapResult;
  }
  
  protected static
  List<Map<String, Object>> readNotifica(Connection conn, int iIdSchedulazione, String sIdServizio, String sIdAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    Set<String> setCheckUnique = new HashSet<String>();
    
    String sSQL = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      sSQL = "SELECT EVENTO,DESTINAZIONE FROM LJSA_ATTIVITA_NOTIFICA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sIdServizio);
      pstm.setString(2, sIdAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sEvento       = rs.getString("EVENTO");
        String sDestinazione = rs.getString("DESTINAZIONE");
        String sKey = sEvento + ":" + sDestinazione;
        if(sEvento != null && sDestinazione != null) {
          if(!setCheckUnique.contains(sKey)) {
            Map<String, Object> mapRecord = new HashMap<String, Object>();
            mapRecord.put(ISchedulazione.sNOT_EVENTO,       sEvento);
            mapRecord.put(ISchedulazione.sNOT_DESTINAZIONE, sDestinazione);
            listResult.add(mapRecord);
            setCheckUnique.add(sKey);
          }
        }
      }
      
      ConnectionManager.close(rs, pstm);
      sSQL = "SELECT EVENTO,DESTINAZIONE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sEvento       = rs.getString("EVENTO");
        String sDestinazione = rs.getString("DESTINAZIONE");
        String sKey = sEvento + ":" + sDestinazione;
        Map<String, Object> mapRecord = new HashMap<String, Object>();
        if(sEvento != null && sDestinazione != null) {
          if(sDestinazione.equals("-alls")) {
            listResult.clear();
            break;
          }
          else if(sDestinazione.startsWith("-") && sDestinazione.length() > 1) {
            mapRecord.put(ISchedulazione.sNOT_EVENTO,       sEvento);
            mapRecord.put(ISchedulazione.sNOT_DESTINAZIONE, sDestinazione.substring(1));
            listResult.remove(mapRecord);
            setCheckUnique.remove(sKey);
          }
          else {
            if(!setCheckUnique.contains(sKey)) {
              mapRecord.put(ISchedulazione.sNOT_EVENTO,       sEvento);
              mapRecord.put(ISchedulazione.sNOT_DESTINAZIONE, sDestinazione);
              listResult.add(mapRecord);
              setCheckUnique.add(sKey);
            }
          }
        }
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  public static
  boolean loadSchedulazioni(Connection conn)
    throws Exception
  {
    if(_boSleepingMode) {
      throw new Exception(ILJSAErrors.sSLEEPING_MODE);
    }
    boolean boAtLastOneRemoved = false;
    boolean boAtLastOneUpdated = false;
    // Lettura delle schedulazioni
    List<Schedulazione> schedulazioni = readSchedulazioni(conn);
    // Rimozione delle schedulazioni non piu' valide
    List<Schedulazione> schedulazioniToRemove = new ArrayList<Schedulazione>();
    for(int i = 0; i < _schedulazioni.size(); i++) {
      Schedulazione schedulazione = _schedulazioni.get(i);
      if(!schedulazioni.contains(schedulazione)) {
        schedulazioniToRemove.add(schedulazione);
      }
    }
    for(Schedulazione schedulazione : schedulazioniToRemove) {
      if(unschedule(schedulazione)) {
        boAtLastOneRemoved = true;
      }
    }
    // Aggiunta delle schedulazioni attive e non scadute
    for(Schedulazione schedulazione : schedulazioni) {
      try {
        if(schedule(schedulazione)) {
          boAtLastOneUpdated = true;
        }
      }
      catch(Throwable th) {
        setEnabled(conn, schedulazione, false);
      }
    }
    return boAtLastOneRemoved || boAtLastOneUpdated;
  }
  
  private static
  void updateLogSchedulatore(Connection conn)
    throws Exception
  {
    String sSQL = "UPDATE LJSA_LOG_SCHEDULATORE SET DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? ";
    sSQL += "WHERE ID_SCHEDULATORE=? AND DATA_SCHEDULAZIONE=? AND ORA_SCHEDULAZIONE=?";
    Date oNow = new Date();
    int iCurrentDate = WUtil.toIntDate(oNow, 0);
    int iCurrentTime = WUtil.toIntTime(oNow, 0);
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setInt(1,    iCurrentDate);
      pstm.setInt(2,    iCurrentTime);
      // WHERE
      pstm.setString(3, getIdSchedulatore());
      pstm.setInt(4,    _iDataSchedulazione);
      pstm.setInt(5,    _iOraSchedulazione);
      pstm.executeUpdate();
    }
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  private static
  void setEnabled(Connection conn, Schedulazione schedulazione, boolean boEnabled)
    throws Exception
  {
    Date oNow = new Date();
    int iCurrentDate = WUtil.toIntDate(oNow, 0);
    int iCurrentTime = WUtil.toIntTime(oNow, 0);
    String sSQL = "UPDATE LJSA_SCHEDULAZIONI SET STATO=?,ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? ";
    sSQL += "WHERE ID_SCHEDULAZIONE=?";
    String sStato = boEnabled ? ISchedulazione.sSTATO_ATTIVA : ISchedulazione.sSTATO_DISATTIVATA;
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sStato);
      pstm.setString(2, schedulazione.getIdCredenzialeAgg());
      pstm.setInt(3, iCurrentDate);
      pstm.setInt(4, iCurrentTime);
      // WHERE
      pstm.setInt(5, schedulazione.getIdSchedulazione());
      pstm.executeUpdate();
    }
    finally {
      ConnectionManager.close(pstm);
    }
    schedulazione.setDataAggiornamento(iCurrentDate);
    schedulazione.setOraAggiornamento(iCurrentTime);
    schedulazione.setStato(sStato);
  }
}
