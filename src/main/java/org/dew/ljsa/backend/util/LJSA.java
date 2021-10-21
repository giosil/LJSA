package org.dew.ljsa.backend.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;

import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.log4j.PropertyConfigurator;

import org.dew.ljsa.backend.sched.LJSAScheduler;
import org.dew.ljsa.backend.web.WebResources;

public
class LJSA
{
  private static Logger logger = Logger.getLogger(LJSA.class.getName());
  
  public static Properties log4jCfg;
  public static String checkInit;
  public static String checkConfig;
  public static String checkScheduler;
  
  public static
  boolean init()
  {
    logger.info("[LJSA] ver. " + LJSAScheduler.getVersion() + " init...");
    try {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logger.cfg");
      if(is != null) {
        log4jCfg = new Properties();
        log4jCfg.load(is);
        changeLogFilePath(log4jCfg);
        PropertyConfigurator.configure(log4jCfg);
      }
      else {
        checkInit = "Resource logger.cfg not found";
      }
    }
    catch(IOException ex) {
      logger.severe("[LJSA] Log setting: " + ex);
      checkInit = ex.toString();
      return false;
    }
    checkInit = "OK";
    
    if(BEConfig.isConfigFileLoaded()) {
      checkConfig = "OK (" + BEConfig.getResultLoading() + ")";
    }
    else {
      checkConfig = BEConfig.getResultLoading();
    }
    logger.info("[LJSA] Load configuration: " + checkConfig);
    
    checkScheduler = "OK";
    logger.info("[LJSA] LJSAScheduler.init(false)...");
    LJSAScheduler.init(false);
    try {
      logger.info("[LJSA] LJSAScheduler.start()...");
      LJSAScheduler.start();
    }
    catch(Exception ex) {
      logger.severe("[LJSA] LJSAScheduler.start(): " + ex);
      checkScheduler = ex.toString();
      return false;
    }
    return true;
  }
  
  public static
  boolean destroy()
  {
    logger.info("[LJSA] destroy...");
    try {
      logger.info("[LJSA] LJSAScheduler.shutdown()...");
      LJSAScheduler.shutdown();
      logger.info("[LJSA] LJSADataSource.closeLJSAConnections()...");
      LJSADataSource.closeLJSAConnections();
    }
    catch(Exception ex) {
      logger.severe("[LJSA] destroy: " + ex);
      return false;
    }
    return true;
  }
  
  // [Remote]
  public static
  boolean checkUser(String sUserName, String sPassword)
  {
    if(sUserName == null || sPassword == null) return false;
    
    boolean result = WebResources.checkLJSAUser(sUserName, sPassword);
    if(result) return result;
    
    String idServizio = "*";
    int iSep = sUserName.indexOf("/");
    if(iSep >= 0) {
      idServizio = sUserName.substring(0,iSep);
      sUserName  = sUserName.substring(iSep+1);
    }
    Connection conn = null;
    try {
      conn   = ConnectionManager.getDefaultConnection();
      
      result = WebResources.checkCredential(conn, idServizio, sUserName, sPassword);
    }
    catch(Exception ex) {
      logger.severe("[LJSA] checkUser(" + sUserName + ",*): " + ex);
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return result;
  }
  
  protected static
  void changeLogFilePath(Properties properties)
  {
    if(properties == null) return;
    
    String logFilePath = System.getProperty("user.home") + File.separator + "log" + File.separator;
    
    Iterator<Object> iterator = properties.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next().toString();
      String val = properties.getProperty(key);
      if(key.endsWith(".File") && val != null) {
        if(!val.startsWith(".") && !val.startsWith("/")) {
          properties.put(key, logFilePath + val);
        }
      }
    }
  }
}
