package org.dew.ljsa.backend.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.util.WUtil;

/**
 * Gestore configurazione LJSA.
 */
public
class BEConfig
{
  public static Properties config = new Properties();
  
  private static boolean configFileLoaded = false;
  private static String resultLoading = "OK";
  
  public final static String sLJSA_CONF_SLEEPING     = "ljsa.sleeping";
  public final static String sLJSA_CONF_USER         = "ljsa.user";
  public final static String sLJSA_CONF_PWD          = "ljsa.password";
  public final static String sLJSA_CONF_RPC_AUTH     = "ljsa.rpc.auth";
  public final static String sLJSA_CONF_CONTEXT      = "ljsa.context";
  public final static String sLJSA_CONF_DOWNLOAD     = "ljsa.download";
  public final static String sLJSA_CONF_REFRESH      = "ljsa.refresh";
  public final static String sLJSA_CONF_FOLDER       = "ljsa.folder";
  public final static String sLJSA_CONF_REPORTS      = "ljsa.reports";
  public final static String sLJSA_CONF_CLASSES      = "ljsa.classes";
  public final static String sLJSA_CONF_HISTORY      = "ljsa.history";
  public final static String sLJSA_CONF_CLEAN_CRON   = "ljsa.clean.cron";
  public final static String sLJSA_CONF_SERVICE      = "ljsa.service";
  public final static String sLJSA_CONF_SER_EXCLUDED = "ljsa.services_excluded";
  public final static String sLJSA_CONF_JDBC_DRIVER  = "ljsa.jdbc.driver";
  public final static String sLJSA_CONF_JDBC_URL     = "ljsa.jdbc.url";
  public final static String sLJSA_CONF_JDBC_USER    = "ljsa.jdbc.user";
  public final static String sLJSA_CONF_JDBC_PWD     = "ljsa.jdbc.password";
  public final static String sLJSA_CONF_JDBC_IDLE    = "ljsa.jdbc.idle";
  public final static String sLJSA_CONF_MAIL_LOOKUP  = "ljsa.mail.lookup";
  public final static String sLJSA_CONF_MAIL_SMTP    = "ljsa.mail.smtp.host";
  public final static String sLJSA_CONF_MAIL_POP     = "ljsa.mail.pop.host";
  public final static String sLJSA_CONF_MAIL_FROM    = "ljsa.mail.from";
  public final static String sLJSA_CONF_MAIL_USER    = "ljsa.mail.user";
  public final static String sLJSA_CONF_MAIL_PWD     = "ljsa.mail.password";
  public final static String sLJSA_CONF_MAIL_SMTP_AU = "ljsa.mail.smtp.auth";
  // [Remote]
  public final static String sLJSA_CONF_REMOTE_URL   = "ljsa.remote.url";
  
  static {
    String sUserHome = System.getProperty("user.home");
    String sPathFile = sUserHome + File.separator + "cfg" + File.separator + "ljsa.cfg";
    try {
      InputStream in = (InputStream) new FileInputStream(sPathFile);
      config = new Properties();
      config.load(in);
      in.close();
      configFileLoaded = true;
      resultLoading = "File " + sPathFile + " loaded.";
    }
    catch(FileNotFoundException ex) {
      resultLoading = "File " + sPathFile + " not found.";
    }
    catch(IOException ioex) {
      resultLoading = "IOException during load " + sPathFile + ": " + ioex;
    }
    checkFolders();
  }
  
  public static
  void reloadConfig()
      throws Exception
  {
    Properties properties = new Properties();
    String configFilePath = System.getProperty("user.home") + File.separator + "cfg" + File.separator + "ljsa.cfg";
    InputStream in = null;
    try {
      in = new FileInputStream(configFilePath);
      properties.load(in);
    }
    finally {
      in.close();
    }
    config = properties;
  }
  
  public static
  boolean isConfigFileLoaded()
  {
    return configFileLoaded;
  }
  
  public static
  String getResultLoading()
  {
    return resultLoading;
  }
  
  public static
  String getProperty(String key)
  {
    return config.getProperty(key);
  }
  
  public static
  String getProperty(String key, String defaultValue)
  {
    return config.getProperty(key, defaultValue);
  }
  
  public static
  Date getDateProperty(String key, Date defaultValue)
  {
    return WUtil.toDate(config.getProperty(key), defaultValue);
  }
  
  public static
  List<String> getListProperty(String key)
  {
    return WUtil.toListOfString(config.getProperty(key)); 
  }
  
  public static
  boolean getBooleanProperty(String key, boolean defaultValue)
  {
    return WUtil.toBoolean(config.getProperty(key), defaultValue);
  }
  
  public static
  int getIntProperty(String key, int defaultValue)
  {
    return WUtil.toInt(config.getProperty(key), defaultValue);
  }
  
  public static
  double getDoubleProperty(String key, double defaultValue)
  {
    return WUtil.toDouble(config.getProperty(key), defaultValue);
  }
  
  @SuppressWarnings("unchecked")
  public static
  Set<String> getHashSetProperty(String key)
  {
    return WUtil.toSet(config.getProperty(key), null);
  }
  
  public static
  Properties loadProperties(String fileName)
    throws Exception
  {
    Properties result = new Properties();
    InputStream inputStream = null;
    try {
      inputStream = Thread.currentThread().getContextClassLoader().getResource(fileName).openStream();
      result.load(inputStream);
    }
    finally {
      if(inputStream != null) try { inputStream.close(); } catch(Exception ex) {}
    }
    return result;
  }
  
  public static
  String getMailUser()
  {
    return config.getProperty(BEConfig.sLJSA_CONF_MAIL_USER);
  }
  
  public static
  String getMailPassword()
  {
    return config.getProperty(BEConfig.sLJSA_CONF_MAIL_PWD);
  }
  
  public static
  String getLJSAContext()
  {
    String result = config.getProperty(BEConfig.sLJSA_CONF_CONTEXT, "http://localhost:8080/LJSA");
    if(result.length() > 0 && result.charAt(result.length() - 1) == '/') {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }
  
  public static
  String getLJSADownload()
  {
    String result = config.getProperty(sLJSA_CONF_DOWNLOAD);
    if(result != null && result.length() > 0) return result;
    return getLJSAContext() + "/download";
  }
  
  public static
  String getLJSADownload(int idLog)
  {
    String result = config.getProperty(sLJSA_CONF_DOWNLOAD);
    if(result != null && result.length() > 0) return result + "/" + idLog;
    return getLJSAContext() + "/download/" + idLog;
  }
  
  public static
  String getLJSAWebServices()
  {
    return getLJSAContext() + "/ws";
  }
  
  public static
  String getLJSARpcAuth()
  {
    return config.getProperty(BEConfig.sLJSA_CONF_RPC_AUTH);
  }
  
  public static
  String getLJSAOutputFolder()
  {
    return System.getProperty("user.home") + File.separator + getProperty(sLJSA_CONF_FOLDER, "ljsa_out");
  }
  
  public static
  String getLJSAOutputFolder(int idLog)
  {
    return System.getProperty("user.home") + File.separator + getProperty(sLJSA_CONF_FOLDER, "ljsa_out") + File.separator + idLog;
  }
  
  public static
  String getLJSAOutputFolder(Object idLog)
  {
    return System.getProperty("user.home") + File.separator + getProperty(sLJSA_CONF_FOLDER, "ljsa_out") + File.separator + idLog;
  }
  
  public static
  String getLJSAReportsFolder()
  {
    return System.getProperty("user.home") + File.separator + getProperty(sLJSA_CONF_REPORTS, "ljsa_reports");
  }
  
  public static
  String getLJSAClassesFolder()
  {
    return System.getProperty("user.home") + File.separator + getProperty(sLJSA_CONF_CLASSES, "ljsa_classes");
  }
  
  public static
  int getLJSAHistory()
  {
    return WUtil.toInt(config.getProperty(sLJSA_CONF_HISTORY), 30);
  }
  
  public static
  String getLJSALogFolder()
  {
    return System.getProperty("user.home") + File.separator + "log";
  }
  
  public static
  void replaceOrAddEntryValue(String entry, String value)
      throws Exception
  {
    if(entry == null || entry.length() == 0) return;
    if(value == null) value = "";
    Map<String, Object> map = new HashMap<String, Object>();
    map.put(entry, value);
    replaceOrAddEntryValues(map);
  }
  
  public static
  void replaceOrAddEntryValues(Map<String, Object> map)
      throws Exception
  {
    if(map == null || map.isEmpty()) return;
    
    String sUserHome = System.getProperty("user.home");
    String sPathFile = sUserHome + File.separator + "cfg" + File.separator + "ljsa.cfg";
    File fileBak = new File(sPathFile + ".bak");
    File fileNew = new File(sPathFile + ".new");
    File file    = new File(sPathFile);
    if(fileBak.exists()) {
      // Si cancella il file bak
      if(!fileBak.delete()) {
        throw new Exception(fileBak.getAbsolutePath() + " not deleted.");
      }
    }
    // Si potrebbe riportare il valore nell'oggetto Properties config
    // e poi invocare il metodo store tuttavia questo porterebbe a perdere
    // i vari commenti che si potrebbero avere nel file.
    // Per questo motivo si legge il file normalmente.
    BufferedReader br = null;
    PrintStream ps = null;
    try {
      br = new BufferedReader(new FileReader(file));
      ps = new PrintStream(fileNew);
      
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        int iSep = sLine.indexOf('=');
        if(iSep > 0) {
          String sLineEntry = sLine.substring(0, iSep).trim();
          if(map.containsKey(sLineEntry)) {
            Object oValue = map.get(sLineEntry);
            String sValue = oValue != null ? oValue.toString() : "";
            ps.println(sLineEntry + " = " + sValue);
            config.setProperty(sLineEntry, sValue);
            map.remove(sLineEntry);
          }
          else {
            ps.println(sLine);
          }
        }
        else {
          ps.println(sLine);
        }
      }
      // Le rimanenti si appendono alla fine del file
      if(!map.isEmpty()) {
        ps.println();
        ps.println("# Added at " + WUtil.formatDateTime(Calendar.getInstance(), "-", true));
        ps.println();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while(it.hasNext()) {
          Map.Entry<String, Object> entry = it.next();
          String sKey   = entry.getKey().toString();
          Object oValue = entry.getValue();
          String sValue = oValue != null ? oValue.toString() : "";
          ps.println(sKey + " = " + sValue);
          config.setProperty(sKey, sValue);
        }
      }
    }
    finally {
      if(br != null) try{ br.close(); } catch(Exception ex) {}
      if(ps != null) try{ ps.close(); } catch(Exception ex) {}
    }
    // Il file originario si rinomina come .bak
    file.renameTo(fileBak);
    // Il nuovo file si rinomina come l'originario
    fileNew.renameTo(file);
  }
  
  protected static
  void checkFolders()
  {
    try {
      File folder;
      folder = new File(getLJSAOutputFolder());
      if(!folder.exists()) folder.mkdirs();
      folder = new File(getLJSAReportsFolder());
      if(!folder.exists()) folder.mkdirs();
      folder = new File(getLJSAClassesFolder());
      if(!folder.exists()) folder.mkdirs();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
