package org.dew.ljsa;

import java.io.PrintStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.dew.ljsa.backend.sched.LJSARunningTable;
import org.dew.ljsa.backend.sched.LJSAScheduler;
import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DataUtil;
import org.dew.ljsa.backend.util.MailManager;
import org.dew.ljsa.backend.util.QueryBuilder;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.JobExecutionContextImpl;
import org.quartz.spi.TriggerFiredBundle;

import org.util.WUtil;

/**
 * Classe astratta da estendere per l'esecuzione di un job LJSA.<br />
 * Esempio:
 *  <pre>
 *  public class LJHello extends ALJSAJob
 *  {
 *    public void execute(Schedulazione sched, OutputSchedulazione out)
 *       throws Exception
 *    {
 *      LJSAMap parametri = sched.getParametri();
 *
 *      String sName = parametri.getString("name");
 *
 *      PrintStream output = new PrintStream(out.createOutputFile(), true);
 *
 *      output.println("Hello " + sName);
 *    }
 *  }
 *  </pre>
 *
 * Oltre al metodo <i>execute</i> e' consigliata l'implementazione dei seguenti metodi:
 *
 * <pre>
 * protected
 * void init(Schedulazione sched, OutputSchedulazione out)
 *   throws Exception
 * {
 * }
 *
 * protected
 * void destroy(Schedulazione sched, OutputSchedulazione out)
 *   throws Exception
 * {
 * }
 *
 * protected
 * void exceptionOccurred(Throwable throwable)
 *   throws Exception
 * {
 * }
 * </pre>
 */
public abstract
class ALJSAJob implements Job, InterruptableJob, ILJSARemoteJob
{
  protected Schedulazione       _schedulazione;
  protected OutputSchedulazione _outputSchedulazione;
  public boolean boLJSAInitCompleted  = false;
  public boolean boLJSAJobInterrupted = false;
  
  /**
   * Metodo da implementare per l'esecuzione di un job nello schedulatore LJSA.
   *
   * @param sched Schedulazione
   * @param out OutputSchedulazione
   * @throws Exception
   */
  public abstract
  void execute(Schedulazione sched, OutputSchedulazione out)
      throws Exception;
  
  /**
   * Metodo facoltativo da implementare per l'inizializzazione.
   *
   * @param sched Schedulazione
   * @param out OutputSchedulazione
   * @throws Exception
   */
  protected
  void init(Schedulazione sched, OutputSchedulazione out)
      throws Exception
  {
  }
  
  /**
   * Metodo facoltativo da implementare per la finalizzazione.
   *
   * @param sched Schedulazione
   * @param out OutputSchedulazione
   * @throws Exception
   */
  protected
  void destroy(Schedulazione sched, OutputSchedulazione out)
      throws Exception
  {
  }
  
  /**
   * Metodo facoltativo da implementare per la gestione delle eccezioni.
   *
   * @param throwable Throwable
   * @throws Exception
   */
  protected
  void exceptionOccurred(Throwable throwable)
      throws Exception
  {
  }
  // methods to implement -----------------------------------------------------
  
  // org.dew.ljsa.ILJSARemoteJob ----------------------------------------------
  public
  void fireInterrupt()
  {
    try {
      interrupt();
    }
    catch(Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
  
  public
  void fireExecute(Map<String, Object> mapSchedulazione)
  {
    if(mapSchedulazione == null || mapSchedulazione.isEmpty()) {
      throw new RuntimeException("Missing schedulation data");
    }
    
    try {
      Schedulazione schedulazione = new Schedulazione(mapSchedulazione);
      
      JobBuilder jobBuilder = JobBuilder.newJob(schedulazione.getClasseAttivita()).withIdentity(schedulazione.getJobName(), schedulazione.getJobGroup());
      JobDetail jobDetail = jobBuilder.build();
      JobDataMap jdm = jobDetail.getJobDataMap();
      jdm.put("#schedulazione", schedulazione);
      
      TriggerFiredBundle triggerFiredBundle = new TriggerFiredBundle(jobDetail, null, null, false, null, null, null, null);
      
      JobExecutionContext jobExecutionContext = new JobExecutionContextImpl(LJSAScheduler.getScheduler(), triggerFiredBundle, this);
      
      execute(jobExecutionContext);
    }
    catch(Exception ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
  
  // org.quartz.InterruptableJob ----------------------------------------------
  public
  void interrupt()
      throws UnableToInterruptJobException
  {
    boLJSAJobInterrupted = true;
    if(_outputSchedulazione == null) {
      return;
    }
    try {
      _schedulazione.setStato(ISchedulazione.sSTATO_DISATTIVATA);
      _schedulazione.clearNotificaTemporanea();
      _outputSchedulazione.setInterruptedStatus();
      _outputSchedulazione.setReport("Schedulazione interrotta.");
      updateLog(_schedulazione, _outputSchedulazione);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  // org.quartz.InterruptableJob ----------------------------------------------
  
  // org.quartz.Job ----------------------------------------------------------
  public
  void execute(JobExecutionContext jec)
      throws JobExecutionException
  {
    JobDataMap jobDataMap = jec.getJobDetail().getJobDataMap();
    
    Schedulazione schedulazione = (Schedulazione) jobDataMap.get("#schedulazione");
    Logger logger = Logger.getLogger(ALJSAJob.class);
    logger.debug(schedulazione + " begin");
    
    if(schedulazione.isExpired()) {
      LJSAScheduler.remove(schedulazione);
      logger.debug(schedulazione + " end because schedulation is expired");
      return;
    }
    if(!schedulazione.isEnabled()) {
      LJSAScheduler.remove(schedulazione);
      logger.debug(schedulazione + " end because schedulation is not enabled");
      return;
    }
    if(!schedulazione.isValid()) {
      logger.debug(schedulazione + " end because schedulation is not valid");
      return;
    }
    LJSAMap configurazione = schedulazione.getConfigurazione();
    boolean boExcludeHolidays = configurazione.getBoolean("excludeHolidays");
    if(boExcludeHolidays && DataUtil.isTodayHoliday()) {
      logger.debug(schedulazione + " end for holiday unaccepted");
      return;
    }
    
    boolean boSingle = configurazione.getBoolean("single");
    if(boSingle && LJSARunningTable.exist(schedulazione)) {
      logger.debug(schedulazione + " end. It is single and exist in LJSARunningTable.");
      return;
    }
    
    LJSARunningTable.add(schedulazione);
    
    boolean boDontNotify = configurazione.getBoolean("dontNotify");
    schedulazione.setDontNotify(boDontNotify);
    
    schedulazione.setStato(ISchedulazione.sSTATO_IN_ESECUZIONE);
    _schedulazione = schedulazione;
    
    if(boLJSAJobInterrupted) {
      LJSARunningTable.remove(schedulazione);
      logger.debug(schedulazione + " end for interruption");
      return;
    }
    
    boLJSAInitCompleted  = false;
    boLJSAJobInterrupted = false;
    
    int iIdLog = 0;
    try {
      iIdLog = insertLog(schedulazione);
      logger.debug(schedulazione + " id_log = " + iIdLog);
    }
    catch(Exception ex) {
      LJSARunningTable.remove(schedulazione);
      logger.error(schedulazione + " exception in insertLog", ex);
      throw new JobExecutionException(ex, false);
    }
    
    OutputSchedulazione outputSchedulazione = new OutputSchedulazione(iIdLog);
    _outputSchedulazione = outputSchedulazione;
    
    String sMessage = configurazione.getString("message");
    if(sMessage != null && sMessage.length() > 0) {
      outputSchedulazione.setMessage(sMessage);
      outputSchedulazione.setMessageFromConfiguration(true);
    }
    String sSubject = configurazione.getString("subject");
    if(sSubject != null && sSubject.length() > 0) {
      outputSchedulazione.setSubject(sSubject);
    }
    
    TimeoutGuard timeoutGuard = null;
    int timeout = schedulazione.getTimeout();
    if(timeout > 0) {
      timeoutGuard = new TimeoutGuard(schedulazione);
      timeoutGuard.start();
    }
    try {
      // Invocazione del metodo init
      logger.debug(schedulazione + " " + iIdLog + " init");
      init(schedulazione, outputSchedulazione);
      
      boLJSAInitCompleted = true;
      
      // Verifica del flag di interruzione
      if(boLJSAJobInterrupted) {
        logger.debug(schedulazione + " " + iIdLog + " end for interruption");
        return;
      }
      
      // Invocazione del metodo execute
      logger.debug(schedulazione + " " + iIdLog + " execute");
      execute(schedulazione, outputSchedulazione);
      
      // Verifica del flag di interruzione
      if(boLJSAJobInterrupted) {
        logger.debug(schedulazione + " " + iIdLog + " end for interruption");
        return;
      }
    }
    catch(Throwable th) {
      outputSchedulazione.setErrorStatus();
      if(boLJSAInitCompleted) {
        outputSchedulazione.setReport(th.toString());
      }
      else {
        outputSchedulazione.setReport("[init] " + th.toString());
      }
      
      // Invocazione del metodo exceptionOccurred
      try {
        logger.debug(schedulazione + " " + iIdLog + " exceptionOccurred(" + th + ")");
        exceptionOccurred(th);
      }
      catch(Throwable theo) {
        theo.printStackTrace();
      }
      
      // Verifica del flag di interruzione
      if(boLJSAJobInterrupted) {
        logger.debug(schedulazione + " " + iIdLog + " end for interruption");
        return;
      }
    }
    finally {
      // Interruzione del thread di timeout
      if(timeoutGuard != null) timeoutGuard.interrupt();
      
      // Invocazione del metodo destroy
      try {
        logger.debug(schedulazione + " " + iIdLog + " destroy");
        destroy(schedulazione, outputSchedulazione);
      }
      catch(Throwable th) {
        outputSchedulazione.setReport("[destroy] " + th.toString());
      }
      
      // Rimozione da LJSARunningTable
      LJSARunningTable.remove(schedulazione);
      
      // Eventuale creazione del file info predefinito
      if(schedulazione.getFlagFileInfo() && !schedulazione.getFlagNoLog()) {
        logger.debug(schedulazione + " " + iIdLog + " createFileInfo");
        createFileInfo(schedulazione, outputSchedulazione);
      }
      
      // Chiusura di tutti i file creati
      outputSchedulazione.closeAllFiles();
      logger.debug(schedulazione + " " + iIdLog + " closeAllFiles");
      
      // Eliminazione dei file temporanei
      outputSchedulazione.removeAllTemporaryFiles();
      logger.debug(schedulazione + " " + iIdLog + " removeAllTemporaryFiles");
      
      // Verifica del flag di interruzione
      if(boLJSAJobInterrupted) {
        logger.debug(schedulazione + " " + iIdLog + " end for interruption");
        return;
      }
      
      // Eventuale compressione dei file
      if(schedulazione.getFlagCompressFiles()) {
        try{
          outputSchedulazione.compressAllFiles();
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      
      // Disattivazione della schedulazione in caso di schedulazione one-shot
      if(schedulazione.isOneShot()) {
        schedulazione.setStato(ISchedulazione.sSTATO_DISATTIVATA);
        LJSAScheduler.remove(schedulazione);
      }
      else {
        schedulazione.setStato(ISchedulazione.sSTATO_ATTIVA);
      }
    }
    
    // Aggiorna LJSA_LOG, LJSA_LOG_FILES e LJSA_SCHEDULAZIONI
    try {
      updateLog(schedulazione, outputSchedulazione);
      logger.debug(schedulazione + " " + iIdLog + " updateLog");
    }
    catch(Exception ex) {
      logger.error(schedulazione + " " + iIdLog + " exception in updateLog", ex);
      // Non si interrompe il job. Si procede con la notifica.
      // throw new JobExecutionException(ex, false);
    }
    
    // Invia i messaggi di notifica
    if(!schedulazione.isDontNotify()) {
      logger.debug(schedulazione + " " + iIdLog + " notify");
      notify(schedulazione, outputSchedulazione);
    }
    else {
      logger.debug(schedulazione + " " + iIdLog + " don't notify");
    }
    
    logger.debug(schedulazione + " " + iIdLog + " end");
  }
  // org.quartz.Job ----------------------------------------------------------
  
  protected
  void notifyErrors(List<String> listDestination, String sSubject, String sMessage, List<?> listAttachments)
      throws Exception
  {
    List<String> listMailAddress = getListOfMailAddress(listDestination);
    
    MailManager.send(listMailAddress, sSubject, sMessage, listAttachments, 1);
  }
  
  protected
  void notifyResult(List<String> listDestination, String sSubject, String sMessage, List<?> listAttachments)
      throws Exception
  {
    List<String> listMailAddress = getListOfMailAddress(listDestination);
    
    if(listMailAddress == null || listMailAddress.size() == 0) {
      return;
    }
    
    String mailFrom = null;
    if(_schedulazione != null) {
      LJSAMap parametri = _schedulazione.getParametri();
      mailFrom = parametri.getString(BEConfig.sLJSA_CONF_MAIL_FROM);
    }
    
    if(mailFrom != null && mailFrom.length() > 0) {
      MailManager.send(listMailAddress, mailFrom, sSubject, sMessage, listAttachments);
    }
    else {
      MailManager.send(listMailAddress, sSubject, sMessage, listAttachments);
    }
  }
  
  protected
  void createFileInfo(Schedulazione sched, OutputSchedulazione out)
  {
    try {
      PrintStream psInfo = new PrintStream(out.createInfoFile(), true);
      psInfo.println("Schedulazione:");
      psInfo.println();
      psInfo.println("IdSchedulazione    = " + sched.getIdSchedulazione());
      psInfo.println("IdServizio         = " + sched.getIdServizio());
      psInfo.println("IdAttivita         = " + sched.getIdAttivita());
      psInfo.println("Descrizione        = " + sched.getDescrizione());
      psInfo.println("Schedulazione      = " + sched.getSchedulazione());
      psInfo.println("InizioValidita     = " + sched.getInizioValidita());
      psInfo.println("FineValidita       = " + sched.getFineValidita());
      psInfo.println("IdCredenzialeIns   = " + sched.getIdCredenzialeIns());
      psInfo.println("Data Inserimento   = " + sched.getDataInserimento());
      psInfo.println("Ora Inserimento    = " + sched.getOraInserimento());
      psInfo.println("IdCredenzialeAgg   = " + sched.getIdCredenzialeAgg());
      psInfo.println("Data Aggiornamento = " + sched.getDataAggiornamento());
      psInfo.println("Ora Aggiornamento  = " + sched.getOraAggiornamento());
      psInfo.println();
      psInfo.println("Configurazione:");
      psInfo.println();
      psInfo.println(sched.getConfigurazione().buildInfoString());
      psInfo.println();
      psInfo.println("Parametri:");
      psInfo.println();
      psInfo.println(sched.getParametri().buildInfoString());
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  protected
  String getEmail(String idServizio, String idCredenziale)
  {
    String result = null;
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT EMAIL FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?");
      pstm.setString(1, idServizio);
      pstm.setString(2, idCredenziale);
      rs = pstm.executeQuery();
      if(rs.next()) result = rs.getString("EMAIL");
    }
    catch(Exception ex) {
      System.err.println("Exception in ALJSAJob.getEmail(" + idServizio + "," + idCredenziale + "): " + ex);
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    if(result == null || result.length() < 5) {
      return null;
    }
    result = result.trim().toLowerCase();
    if(result.indexOf('@') <= 0 || result.indexOf('.') < 0) {
      return null;
    }
    return result;
  }
  
  private static
  int insertLog(Schedulazione schedulazione)
      throws Exception
  {
    int idLog = 0;
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      String sSQL = "UPDATE LJSA_SCHEDULAZIONI SET STATO=? WHERE ID_SCHEDULAZIONE=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, ISchedulazione.sSTATO_IN_ESECUZIONE);
      pstm.setInt(2, schedulazione.getIdSchedulazione());
      pstm.executeUpdate();
      
      if(schedulazione.getFlagNoLog()) {
        ut.commit();
        return 0;
      }
      
      pstm.close();
      
      idLog = ConnectionManager.nextVal(conn, "LJSA_SEQ_LOG");
      
      sSQL = "INSERT INTO LJSA_LOG(ID_LOG,ID_SCHEDULAZIONE,DATA_INIZIO,ORA_INIZIO,STATO) VALUES(?,?,?,?,?)";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1,    idLog);
      pstm.setInt(2,    schedulazione.getIdSchedulazione());
      pstm.setInt(3,    iCurrentDate);
      pstm.setInt(4,    iCurrentTime);
      pstm.setString(5, QueryBuilder.decodeBoolean(true));
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return idLog;
  }
  
  private static
  void updateLog(Schedulazione schedulazione, OutputSchedulazione outputSchedulazione)
      throws Exception
  {
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    String sURLDownload = BEConfig.getLJSADownload();
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      String sSQL = "UPDATE LJSA_SCHEDULAZIONI SET STATO=?,";
      if(outputSchedulazione.isErrorStatus()) {
        sSQL += "ESECUZIONI_INTERROTTE=ESECUZIONI_INTERROTTE+1 ";
      }
      else if(outputSchedulazione.isInterruptedStatus()) {
        sSQL += "ESECUZIONI_INTERROTTE=ESECUZIONI_INTERROTTE+1 ";
      }
      else {
        sSQL += "ESECUZIONI_COMPLETATE=ESECUZIONI_COMPLETATE+1 ";
      }
      sSQL += "WHERE ID_SCHEDULAZIONE=?";
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, schedulazione.getStato());
      // WHERE
      pstm.setInt(2, schedulazione.getIdSchedulazione());
      pstm.executeUpdate();
      
      if(schedulazione.getFlagNoLog()) {
        ut.commit();
        return;
      }
      
      int    idLog   = outputSchedulazione.getIdLog();
      String sStatus = outputSchedulazione.getStatus();
      String sReport = outputSchedulazione.getReport();
      if(sReport != null && sReport.length() > 255) {
        sReport = sReport.substring(0, 255);
      }
      
      pstm.close();
      
      sSQL = "UPDATE LJSA_LOG SET DATA_FINE=?,ORA_FINE=?,STATO=?,RAPPORTO=? WHERE ID_LOG=?";
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setInt(1,    iCurrentDate);
      pstm.setInt(2,    iCurrentTime);
      pstm.setString(3, sStatus);
      pstm.setString(4, sReport);
      // WHERE
      pstm.setInt(5,    idLog);
      pstm.executeUpdate();
      
      if(outputSchedulazione.hasFiles()) {
        pstm.close();
        
        sSQL = "INSERT INTO LJSA_LOG_FILES ";
        sSQL += "(ID_LOG, TIPOLOGIA, NOME_FILE, URL_FILE) ";
        sSQL += "VALUES (?, ?, ?, ?)";
        pstm = conn.prepareStatement(sSQL);
        for(int i = 0; i < outputSchedulazione.getFilesCount(); i++) {
          String sType     = outputSchedulazione.getTypeFile(i);
          String sFileName = outputSchedulazione.getFileName(i);
          String sURLFile  = sURLDownload + "/" + idLog + "/" + sFileName;
          pstm.setInt(1,    idLog);
          pstm.setString(2, sType);
          pstm.setString(3, sFileName);
          pstm.setString(4, sURLFile);
          pstm.executeUpdate();
        }
      }
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
  }
  
  protected
  void notify(Schedulazione schedulazione, OutputSchedulazione outputSchedulazione)
  {
    int iIdLog = outputSchedulazione.getIdLog();
    
    if(outputSchedulazione.isErrorStatus()) {
      List<String> listNotificaErrori = schedulazione.getNotificaErrori();
      if(listNotificaErrori.size() == 0) return;
      String sSubject = outputSchedulazione.getSubject();
      if(sSubject == null || sSubject.length() == 0) {
        sSubject = "exception in " + schedulazione;
      }
      String sMessage = outputSchedulazione.getMessage();
      boolean boMessageFromConfiguration = outputSchedulazione.isMessageFromConfiguration();
      boolean boAttachFiles = schedulazione.getFlagAttachFiles() ||
          schedulazione.getFlagAttachErrorFiles();
      if(sMessage == null || boMessageFromConfiguration) {
        sMessage =  "Exception in " +  schedulazione + ".\n\n";
        sMessage += "Parameters:\n";
        sMessage += schedulazione.getParametri().buildInfoString() + "\n";
        sMessage += "Download files from " + outputSchedulazione.getURLDownload() + "\n\n";
      }
      try {
        if(boAttachFiles) {
          notifyErrors(listNotificaErrori, sSubject, sMessage, outputSchedulazione.getListOfErrorOrReportFile());
        }
        else {
          notifyErrors(listNotificaErrori, sSubject, sMessage, null);
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    else {
      // Notifica risultato
      List<String> listNotificaRisultato = schedulazione.getNotificaRisultato();
      if(listNotificaRisultato.size() == 0) return;
      
      List<String> listOfWrongNotificationItems = getListOfWrongNotificationItems(listNotificaRisultato);
      if(listOfWrongNotificationItems != null && listOfWrongNotificationItems.size() > 0) {
        List<String> listNotificaErrori = schedulazione.getNotificaErrori();
        String sSubjectErr = "wrong notification destinations";
        String sMessageErr = schedulazione  + " completed successfully.\n";
        sMessageErr += "Wrong notification destinations:\n";
        for(int i = 0; i < listOfWrongNotificationItems.size(); i++) {
          sMessageErr += "- " + listOfWrongNotificationItems.get(i) + "\n";
        }
        sMessageErr += "\n";
        try {
          notifyErrors(listNotificaErrori, sSubjectErr, sMessageErr, null);
        }
        catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      
      String sSubject = outputSchedulazione.getSubject();
      if(sSubject == null || sSubject.length() == 0) {
        sSubject = schedulazione + " completed successfully.";
      }
      String sMessage = outputSchedulazione.getMessage();
      boolean boAttachFiles = schedulazione.getFlagAttachFiles();
      if(sMessage == null) {
        sMessage = schedulazione + " completed successfully.\n\n";
        sMessage += "Parameters:\n";
        sMessage += schedulazione.getParametri().buildInfoString() + "\n";
        if(iIdLog != 0) {
          if(boAttachFiles) {
            sMessage += "The files are attached. You can also download files from " + outputSchedulazione.getURLDownload();
          }
          else {
            sMessage += "Download files from " + outputSchedulazione.getURLDownload();
          }
        }
      }
      try {
        if(boAttachFiles) {
          notifyResult(listNotificaRisultato, sSubject, sMessage, outputSchedulazione.getListOfFile());
        }
        else {
          notifyResult(listNotificaRisultato, sSubject, sMessage, null);
        }
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    schedulazione.clearNotificaTemporanea();
  }
  
  protected
  Connection getConnection(String sPrefix, LJSAMap configurazione)
      throws Exception
  {
    Connection conn = null;
    String dataSource = configurazione.getString(sPrefix + ".jdbc.ds");
    if(dataSource != null && dataSource.length() > 0) {
      conn = ConnectionManager.getConnection(dataSource);
    }
    else {
      String sDriver   = configurazione.getString(sPrefix + ".jdbc.driver",   "oracle.jdbc.driver.OracleDriver");
      String sURL      = configurazione.getString(sPrefix + ".jdbc.url",      true);
      String sUser     = configurazione.getString(sPrefix + ".jdbc.user",     true);
      String sPassword = configurazione.getString(sPrefix + ".jdbc.password", true);
      Class.forName(sDriver);
      conn = DriverManager.getConnection(sURL, sUser, sPassword);
    }
    return conn;
  }
  
  protected
  Connection getConnection(LJSAMap configurazione)
      throws Exception
  {
    Connection conn = null;
    String dataSource = configurazione.getString("jdbc.ds");
    if(dataSource != null && dataSource.length() > 0) {
      conn = ConnectionManager.getConnection(dataSource);
    }
    else {
      String sDriver   = configurazione.getString("jdbc.driver",  "oracle.jdbc.driver.OracleDriver");
      String sURL      = configurazione.getString("jdbc.url",      true);
      String sUser     = configurazione.getString("jdbc.user",     true);
      String sPassword = configurazione.getString("jdbc.password", true);
      Class.forName(sDriver);
      conn = DriverManager.getConnection(sURL, sUser, sPassword);
    }
    return conn;
  }
  
  protected
  Connection getConnection(String sDataSource)
      throws Exception
  {
    return ConnectionManager.getConnection(sDataSource);
  }
  
  protected
  void closeConnection(Connection conn)
  {
    if(conn != null) try{ conn.close(); } catch(Exception ex) {}
  }
  
  protected
  List<String> getListOfMailAddress(List<String> listDestination)
  {
    List<String> listResult = new ArrayList<String>();
    if(listDestination == null) return listResult;
    for(int i = 0; i < listDestination.size(); i++) {
      String destination = listDestination.get(i);
      if(destination.indexOf("://") < 0) {
        if(destination.indexOf("@") > 0 && destination.indexOf('.') >= 0) {
          listResult.add(destination);
        }
      }
    }
    return listResult;
  }
  
  /**
   * Viene restituita una lista degli elementi di notifica giudicati errati.
   *
   * @param listDestination Lista di notifica
   * @return Lista elementi errati
   */
  protected
  List<String> getListOfWrongNotificationItems(List<String> listDestination)
  {
    List<String> listResult = new ArrayList<String>();
    if(listDestination == null) return listResult;
    for(int i = 0; i < listDestination.size(); i++) {
      String destination = listDestination.get(i);
      if(destination.indexOf("://") < 0) {
        int iAt = destination.indexOf("@");
        if(iAt > 0) {
          int iDot = destination.lastIndexOf('.');
          if(destination.length() < 5) {
            listResult.add(destination);
          }
          else if(destination.indexOf(' ') > 0) {
            listResult.add(destination);
          }
          else if(iDot < iAt) {
            listResult.add(destination);
          }
        }
      }
    }
    return listResult;
  }
  
  protected static
  Properties getLJSAConfig()
      throws Exception
  {
    return BEConfig.config;
  }
  
  protected
  void printOnLJSALog(String sText)
      throws Exception
  {
    Logger logger = Logger.getLogger(ALJSAJob.class);
    if(logger != null && _schedulazione != null) {
      logger.debug("[" + _schedulazione + "] " + sText);
    }
  }
}
