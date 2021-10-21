package org.dew.ljsa.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.dew.ljsa.ICommon;
import org.dew.ljsa.IServizio;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.User;

import org.dew.util.WUtil;

public
class DataManager
{
  public static List<String> vServiziAbilitati = new ArrayList<String>();
  public static List<String> defaultServizio;
  
  public static
  void clear()
  {
    vServiziAbilitati = new ArrayList<String>();
    defaultServizio   = null;
  }
  
  public static
  void setServiziAbilitati(List<String> serviziAbilitati)
  {
    if(serviziAbilitati == null || serviziAbilitati.size() == 0) {
      vServiziAbilitati = new ArrayList<String>();
      defaultServizio   = null;
      return;
    }
    vServiziAbilitati = new ArrayList<String>();
    vServiziAbilitati.addAll(serviziAbilitati);
    
    if(serviziAbilitati.size() == 0) {
      String idServizio = serviziAbilitati.get(0);
      if(idServizio != null && idServizio.length() > 0) {
        List<String> servizio = new ArrayList<String>();
        servizio.add(idServizio);
        servizio.add(idServizio);
        servizio.add(idServizio);
        
        defaultServizio = servizio;
      }
    }
  }
  
  public static
  void addServizioAbilitato(Map<String, Object> map)
  {
    if(map == null) return;
    
    String idServizio = WUtil.toString(map.get(IServizio.sID_SERVIZIO), null);
    if(idServizio == null || idServizio.length() == 0) {
      return;
    }
    
    if(vServiziAbilitati == null) vServiziAbilitati = new ArrayList<String>();
    if(vServiziAbilitati.contains(idServizio)) return;
    
    vServiziAbilitati.add(idServizio);
  }
  
  public static
  List<String> getDefaultServizio()
  {
    if(defaultServizio != null) return defaultServizio;
    return defaultServizio;
  }
  
  public static
  void putUserLog(Map<String, Object> mapValues)
  {
    if(mapValues == null) return;
    
    User user = ResourcesMgr.getSessionManager().getUser();
    if(user == null) return;
    
    mapValues.put(ICommon.sUSER_LOG, user.getUserName());
  }
  
  public static
  String getUserLog()
  {
    User user = ResourcesMgr.getSessionManager().getUser();
    if(user == null) return null;
    return user.getUserName();
  }
  
  public static
  void downloadFile(String sURL)
  {
    URL url = null;
    try {
      url = new URL(sURL);
    }
    catch(Exception ex) {
      GUIMessage.showWarning("URL non corretta: " + sURL);
      return;
    }
    
    downloadFile(url);
  }
  
  public static
  String getFileName(String sURL)
  {
    String sResult = "";
    int iSep = sURL.lastIndexOf('/');
    if(iSep >= 0 && iSep < sURL.length() - 1) {
      sResult = sURL.substring(iSep + 1);
    }
    else {
      iSep = sURL.lastIndexOf('\\');
      if(iSep >= 0 && iSep < sURL.length() - 1) {
        sResult = sURL.substring(iSep + 1);
      }
      else {
        sResult = sURL;
      }
    }
    return sResult;
  }
  
  public static
  String getFolder(String sFilePath)
  {
    int iLength = sFilePath.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iLength - i);
      if(c == '/' || c == '\\') {
        return sFilePath.substring(0, iIndex);
      }
    }
    return "";
  }
  
  public static
  void downloadFile(URL url)
  {
    if(url == null) return;
    
    String sFileName = getFileName(url.toString());
    if(!GUIMessage.getConfirmation("Si \350 scelto di scaricare il file " + sFileName + ". Procedere?")) {
      return;
    }
    
    String sDefDir = ResourcesMgr.dat.getProperty("download.directory");
    
    JFileChooser oFileChooser = new JFileChooser();
    File oDefSelectedFile = new File(sFileName);
    oFileChooser.setSelectedFile(oDefSelectedFile);
    if(sDefDir != null && sDefDir.length() > 0) {
      oFileChooser.setCurrentDirectory(new File(sDefDir));
    }
    int iResult = oFileChooser.showSaveDialog(ResourcesMgr.mainFrame);
    
    if(iResult != JFileChooser.APPROVE_OPTION) {
      return;
    }
    
    String sFilePath = oFileChooser.getSelectedFile().getAbsolutePath();
    ResourcesMgr.dat.setProperty("download.directory", getFolder(sFilePath));
    ResourcesMgr.saveDat();
    
    ResourcesMgr.setVisibleWaitPleaseWindow(true);
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    try {
      bis = new BufferedInputStream(url.openStream());
      fos = new FileOutputStream(sFilePath, false);
      int iBytesReaded = 0;
      byte[] abBuffer = new byte[1024];
      while((iBytesReaded = bis.read(abBuffer)) > 0) {
        fos.write(abBuffer, 0, iBytesReaded);
      }
      GUIMessage.showInformation("Download del file " + sFileName + " completato.");
    }
    catch(Exception ex) {
      ResourcesMgr.setVisibleWaitPleaseWindow(false);
      GUIMessage.showException("Errore durante il download del file " + sFileName, ex);
    }
    finally {
      ResourcesMgr.setVisibleWaitPleaseWindow(false);
      if(bis != null) try{ bis.close(); } catch(Exception ex) {};
      if(fos != null) try{ fos.close(); } catch(Exception ex) {};
    }
  }
  
  public static
  void downloadFile(String sURL, String sFilePath)
      throws Exception
  {
    if(sURL == null || sURL.length() == 0) return;
    
    BufferedInputStream bis = null;
    FileOutputStream fos = null;
    try {
      URL url = new URL(sURL);
      bis = new BufferedInputStream(url.openStream());
      fos = new FileOutputStream(sFilePath, false);
      int iBytesReaded = 0;
      byte[] abBuffer = new byte[1024];
      while((iBytesReaded = bis.read(abBuffer)) > 0) {
        fos.write(abBuffer, 0, iBytesReaded);
      }
    }
    finally {
      if(bis != null) try{ bis.close(); } catch(Exception ex) {};
      if(fos != null) try{ fos.close(); } catch(Exception ex) {};
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <T>
  T expect(Object object, Class<T> expectedClass)
  {
    if(object == null || expectedClass == null) {
      return null;
    }
    if(expectedClass.isInstance(object)) {
      return (T) object;
    }
    return null;
  }
}
