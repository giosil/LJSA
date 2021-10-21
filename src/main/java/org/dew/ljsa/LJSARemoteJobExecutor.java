package org.dew.ljsa;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import org.json.JSON;

import org.rpc.util.Base64Coder;

import org.dew.ljsa.backend.sched.LJSARunningTable;
import org.dew.ljsa.backend.sched.LJSAScheduler;
import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.DataUtil;

// [Remote]
public
class LJSARemoteJobExecutor implements Job, InterruptableJob
{
  // Tale flag e' stato reso pubblico per permettere la sua verifica anche
  // all'esterno della classe che estende ALJSAJob.
  public boolean boLJSAJobInterrupted = false;
  
  protected int _idSchedulazione = 0;
  
  public
  void execute(JobExecutionContext jec)
      throws JobExecutionException
  {
    JobDataMap jobDataMap = jec.getJobDetail().getJobDataMap();
    
    Schedulazione schedulazione = (Schedulazione) jobDataMap.get("#schedulazione");
    Logger logger = Logger.getLogger(LJSARemoteJobExecutor.class);
    logger.debug(schedulazione + " begin remote job");
    
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
    // Aggiunta in LJSARunningTable per il controllo delle schedulazioni single.
    LJSARunningTable.add(schedulazione);
    
    boolean boDontNotify = configurazione.getBoolean("dontNotify");
    schedulazione.setDontNotify(boDontNotify);
    
    schedulazione.setStato(ISchedulazione.sSTATO_IN_ESECUZIONE);
    
    // Verifica del flag di interruzione
    if(boLJSAJobInterrupted) {
      // Rimozione da LJSARunningTable
      LJSARunningTable.remove(schedulazione);
      logger.debug(schedulazione + " end for interruption");
      return;
    }
    
    try {
      String result = invokeExecute(schedulazione);
      
      logger.debug(schedulazione + " invokeExecute -> " + result);
      
      _idSchedulazione = schedulazione.getIdSchedulazione();
    }
    catch(Exception ex) {
      logger.debug(schedulazione + " invokeExecute -> " + ex);
    }
    finally {
      // Rimozione da LJSARunningTable
      LJSARunningTable.remove(schedulazione);
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
  
  public
  void interrupt()
      throws UnableToInterruptJobException
  {
    boLJSAJobInterrupted = true;
    if(_idSchedulazione != 0) {
      invokeInterrupt(_idSchedulazione);
    }
  }
  
  public static
  String invokeInterrupt(int iIdSchedulazione)
  {
    if(iIdSchedulazione == 0) {
      return "iIdSchedulazione = " + iIdSchedulazione;
    }
    try {
      return http("GET", "/" + iIdSchedulazione, null);
    }
    catch(Exception ex) {
      return ex.toString();
    }
  }
  
  public static
  String invokeExecute(Schedulazione schedulazione)
      throws Exception
  {
    int idSchedulazione = schedulazione.getIdSchedulazione();
    
    return http("POST", "/" + idSchedulazione, JSON.stringify(schedulazione.toMap()));
  }
  
  protected static
  String http(String method, String path, String data)
      throws Exception
  {
    String rurl = BEConfig.getProperty(BEConfig.sLJSA_CONF_REMOTE_URL);
    String user = BEConfig.getProperty(BEConfig.sLJSA_CONF_USER);
    String pass = BEConfig.getProperty(BEConfig.sLJSA_CONF_PWD);
    
    if(rurl == null || rurl.length() < 7) {
      rurl = BEConfig.getProperty(BEConfig.sLJSA_CONF_CONTEXT);
      
      int iLastSep = rurl.lastIndexOf('/');
      if(iLastSep > 0) {
        rurl = rurl.substring(0, iLastSep) + "/cfbackend/LJSAExecutor";
      }
    }
    
    if(path == null) path = "";
    if(path.length() > 0 && !path.startsWith("/")) path = "/" + path;
    
    HttpURLConnection connection = (HttpURLConnection) new URL(rurl + path).openConnection();
    
    if(method == null || method.length() < 2) method = "GET";
    connection.setRequestMethod(method.toUpperCase());
    if(user != null && user.length() > 0) {
      connection.addRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(user + ":" + pass));
    }
    if("POST".equalsIgnoreCase(method)) {
      if(data == null) data = "{}";
    }
    if(data != null) {
      if(!"GET".equalsIgnoreCase(method)) {
        connection.addRequestProperty("Content-Type", "application/json");
      }
      connection.setDoOutput(true);
    }
    connection.setConnectTimeout(300 * 1000);
    connection.setReadTimeout(300 * 1000);
    
    int statusCode = 0;
    boolean error  = false;
    OutputStream out = null;
    try {
      if(data != null) {
        out = connection.getOutputStream();
        out.write(data.getBytes("UTF-8"));
        out.flush();
        out.close();
      }
      statusCode = connection.getResponseCode();
      error = statusCode >= 400;
    }
    catch(Exception ex) {
      throw ex;
    }
    finally {
      if(out != null) try{ out.close(); } catch(Exception ex) {}
    }
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      BufferedInputStream  bin = new BufferedInputStream(error ? connection.getErrorStream() : connection.getInputStream());
      byte[] buff = new byte[1024];
      int n;
      while((n = bin.read(buff)) > 0) baos.write(buff, 0, n);
      baos.flush();
      baos.close();
    }
    finally {
      if(connection != null) try{ connection.disconnect(); } catch(Exception ex) {}
    }
    
    byte[] abResponse = new String(baos.toByteArray(), "UTF-8").getBytes();
    
    String result = new String(abResponse);
    if(error) {
      if(result.length() == 0 || result.equalsIgnoreCase("error")) {
        result += " (HTTP " + statusCode + ")";
      }
    }
    if(error) {
      throw new Exception(result);
    }
    return result;
  }
}
