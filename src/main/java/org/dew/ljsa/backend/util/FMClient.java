package org.dew.ljsa.backend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.rpc.client.JsonRpcInvoker;
import org.soap.rpc.client.SoapRpcInvoker;
import org.xml.rpc.client.XmlRpcInvoker;

import org.rpc.client.AsyncCallback;
import org.rpc.client.RpcClientTransport;
import org.rpc.client.RpcInvoker;
import org.rpc.util.Base64Coder;

import org.util.WUtil;

/**
 * Client File Manager. 
 */
public 
class FMClient implements AsyncCallback 
{
  public static final String sUSER_HOME = "user.home";
  
  protected String url;
  protected String user;
  protected String pass;
  protected String prot = "json-rpc";
  protected PrintStream log;
  
  protected boolean processing = false;
  protected Object oResult = null;
  protected Exception oException = null;
  
  protected RpcInvoker rpcInvoker;

  public FMClient(String url, String user, String pass) throws Exception {
    this.url  = url;
    this.user = user;
    this.pass = pass;
    this.log  = new PrintStream(new EmptyOutputStream());
    
    init();
  }
  
  public FMClient(String url, String user, String pass, String protocol) throws Exception {
    this.url  = url;
    this.user = user;
    this.pass = pass;
    this.prot = protocol;
    this.log = new PrintStream(new EmptyOutputStream());
    
    init();
  }

  public FMClient(String url, String user, String pass, PrintStream log) throws Exception {
    this.url = url;
    this.user = user;
    this.pass = pass;
    this.log = log;
    if(this.log == null) this.log = new PrintStream(new EmptyOutputStream());
    
    init();
  }

  public FMClient(String url, String user, String pass, String protocol, PrintStream log) throws Exception {
    this.url  = url;
    this.user = user;
    this.pass = pass;
    this.prot = protocol;
    this.log = log;
    if(this.log == null) this.log = new PrintStream(new EmptyOutputStream());
    
    init();
  }

  public String getVersion() throws Exception {
    List<Object> parameters = new ArrayList<Object>();
    return WUtil.toString(rpcExecute("FM.getVersion", parameters, 0), "");
  }

  public List<FMEntry> ls() throws Exception {
    return ls(null, null);
  }

  public List<FMEntry> ls(String sDirectory) throws Exception {
    return ls(sDirectory, null);
  }

  public List<FMEntry> ls(String sDirectory, String sFilter) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    if (sFilter == null) sFilter = "";
    List<FMEntry> listResult = new ArrayList<FMEntry>();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sFilter));
    List<Map<String, Object>> listFiles = WUtil.toListOfMapObject(rpcExecute("FM.ls", parameters, 0));
    if (listFiles != null && listFiles.size() > 0) {
      for (int i = 0; i < listFiles.size(); i++) {
        FMEntry fmEntry = new FMEntry(listFiles.get(i));
        listResult.add(fmEntry);
      }
    }
    return listResult;
  }

  public boolean exist(String sDirectory, String sFileName) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    if (sFileName == null) sFileName = "";
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sFileName));
    return WUtil.toBoolean(rpcExecute("FM.exist", parameters, 0), false);
  }

  public int check(String sFile) throws Exception {
    if (sFile == null || sFile.length() == 0) return 0;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    return WUtil.toInt(rpcExecute("FM.check", parameters, 0), 0);
  }

  public FMEntry info(String sFile) throws Exception {
    if (sFile == null || sFile.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    Map<String, Object> mapResult = WUtil.toMapObject(rpcExecute("FM.info", parameters, 0));
    if (mapResult == null || mapResult.isEmpty()) return null;
    return new FMEntry(mapResult);
  }

  public String execute(String sDirectory, String sCommandLine) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    if (sCommandLine == null || sCommandLine.length() == 0) return "";
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sCommandLine));
    parameters.add(Collections.EMPTY_LIST);
    return WUtil.toString(rpcExecute("FM.execute", parameters, 30000), "");
  }

  public String execute(String sDirectory, String sCommandLine, List<String> listTextToType) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    if (sCommandLine == null || sCommandLine.length() == 0) return "";
    if (listTextToType == null) listTextToType = new ArrayList<String>();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sCommandLine));
    parameters.add(listTextToType);
    return WUtil.toString(rpcExecute("FM.execute", parameters, 30000), "");
  }

  public List<String> getProcesses() throws Exception {
    List<String> listResult = new ArrayList<String>();
    List<Object> parameters = new ArrayList<Object>();
    List<String> listProcesses = WUtil.toListOfString(rpcExecute("FM.getProcesses", parameters, 0));
    if (listProcesses != null && listProcesses.size() > 0) {
      for (int i = 0; i < listProcesses.size(); i++) {
        listResult.add(listProcesses.get(i));
      }
    }
    return listResult;
  }

  public boolean kill(String sKeyProcess) throws Exception {
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sKeyProcess));
    return WUtil.toBoolean(rpcExecute("FM.kill", parameters, 0), false);
  }

  public Map<String, Object> env() throws Exception {
    List<Object> parameters = new ArrayList<Object>();
    return WUtil.toMapObject(rpcExecute("FM.env", parameters, 0));
  }

  public String env(String sKey) throws Exception {
    if (sKey == null || sKey.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(sKey);
    return WUtil.toString(rpcExecute("FM.env", parameters, 0), "");
  }

  public String env(String sKey, String sValue) throws Exception {
    if (sKey == null || sKey.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(sKey);
    parameters.add(sValue);
    return WUtil.toString(rpcExecute("FM.env", parameters, 0), "");
  }

  public boolean delete(String sFile) throws Exception {
    if (sFile == null || sFile.length() == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    return WUtil.toBoolean(rpcExecute("FM.delete", parameters, 0), false);
  }

  public boolean mkdir(String sDirectory, String sSubDirectoryName) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    if (sSubDirectoryName == null || sSubDirectoryName.length() == 0) return true;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sSubDirectoryName));
    return WUtil.toBoolean(rpcExecute("FM.mkdir", parameters, 0), false);
  }

  public boolean mkdirs(String sPath) throws Exception {
    if (sPath == null || sPath.length() == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sPath));
    return WUtil.toBoolean(rpcExecute("FM.mkdirs", parameters, 0), false);
  }

  public boolean touch(String sFile) throws Exception {
    if (sFile == null || sFile.length() == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    return WUtil.toBoolean(rpcExecute("FM.touch", parameters, 0), false);
  }

  public boolean rename(String sFile, String sNewName) throws Exception {
    if (sFile    == null || sFile.length()    == 0) return false;
    if (sNewName == null || sNewName.length() == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(encrypt(sNewName));
    return WUtil.toBoolean(rpcExecute("FM.rename", parameters, 0), false);
  }

  public boolean move(String sFile, String sDirectory) throws Exception {
    if (sFile == null || sFile.length() == 0) return false;
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(encrypt(sDirectory));
    return WUtil.toBoolean(rpcExecute("FM.move", parameters, 0), false);
  }

  public boolean copy(String sFile, String sDirectory) throws Exception {
    if (sFile == null || sFile.length() == 0) return false;
    if (sDirectory == null || sDirectory.length() == 0) {
      sDirectory = sUSER_HOME;
    }
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(encrypt(sDirectory));
    return WUtil.toBoolean(rpcExecute("FM.copy", parameters, 0), false);
  }

  public String getTextContent(String sFile) throws Exception {
    if (sFile == null || sFile.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    return WUtil.toString(rpcExecute("FM.getTextContent", parameters, 0), "");
  }

  public String head(String sFile, int iRows) throws Exception {
    if (sFile == null || sFile.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(iRows);
    return WUtil.toString(rpcExecute("FM.head", parameters, 0), "");
  }

  public String tail(String sFile, int iRows) throws Exception {
    if (sFile == null || sFile.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(iRows);
    return (String) rpcExecute("FM.tail", parameters, 0);
  }

  public List<Map<String, Object>> find(String sFile, String sText, int iMaxResults) throws Exception {
    if (sFile == null || sFile.length() == 0) return new ArrayList<Map<String, Object>>();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(sText);
    parameters.add(iMaxResults);
    return WUtil.toListOfMapObject(rpcExecute("FM.find", parameters, 0));
  }

  public boolean download(String sFile, String sLocalFilePath) throws Exception {
    FMEntry fmEntry = info(sFile);
    return download(fmEntry, sLocalFilePath, 100 * 1024);
  }

  public boolean download(String sFile, String sLocalFilePath, int iBlock) throws Exception {
    FMEntry fmEntry = info(sFile);
    return download(fmEntry, sLocalFilePath, iBlock);
  }

  public boolean download(FMEntry fmEntry, String sLocalFilePath, int iBlock) throws Exception {
    if (fmEntry == null || sLocalFilePath == null || sLocalFilePath.length() == 0) {
      return false;
    }
    File file = new File(sLocalFilePath);
    long lLength = fmEntry.getLength();
    int iParts = 1;
    int iRemainder = 0;
    if (lLength >= 0) {
      iParts = (int) (lLength / iBlock);
      iRemainder = (int) (lLength % iBlock);
    }
    if (iRemainder > 0) iParts++;
    log.println("download " + fmEntry.getPath() + "...");
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      List<Object> parameters = new ArrayList<Object>();
      parameters.add(encrypt(fmEntry.getPath()));
      parameters.add(1);
      parameters.add(iBlock);
      for (int iPart = 1; iPart <= iParts; iPart++) {
        parameters.set(1, iPart);
        byte[] arrayOfByte = WUtil.toArrayOfByte(rpcExecute("FM.getContent", parameters, 0), true);
        fos.write(arrayOfByte);
        int iPercentage = iPart * 100 / iParts;
        log.println(iPercentage + "%");
      }
      log.println("completed.");
    } 
    finally {
      if (fos != null) try { fos.close(); } catch (Exception ex) {}
    }
    return true;
  }

  public String startUpload(String sRemoteDirectory, String sFileName, boolean boMakeDirs) throws Exception {
    if (sRemoteDirectory == null || sRemoteDirectory.length() == 0) return null;
    if (sFileName == null || sFileName.length() == 0) return null;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sRemoteDirectory));
    parameters.add(encrypt(sFileName));
    parameters.add(boMakeDirs);
    return WUtil.toString(rpcExecute("FM.startUpload", parameters, 0), "");
  }

  public boolean appendContent(String sFile, byte[] arrayOfByte) throws Exception {
    if (sFile == null || sFile.length() == 0) return false;
    if (arrayOfByte == null || arrayOfByte.length == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sFile));
    parameters.add(arrayOfByte);
    return WUtil.toBoolean(rpcExecute("FM.appendContent", parameters, 0), false);
  }

  public boolean appendContent(String sDirectory, String sFile, byte[] arrayOfByte) throws Exception {
    if (sDirectory == null || sDirectory.length() == 0) return false;
    if (sFile == null || sFile.length() == 0) return false;
    if (arrayOfByte == null || arrayOfByte.length == 0) return false;
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sDirectory));
    parameters.add(encrypt(sFile));
    parameters.add(arrayOfByte);
    return WUtil.toBoolean(rpcExecute("FM.appendContent", parameters, 0), false);
  }

  public boolean endUpload(String sTmpFile, String sMD5) throws Exception {
    if (sTmpFile == null || sTmpFile.length() == 0) return false;
    if (sMD5 == null) sMD5 = "";
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sTmpFile));
    parameters.add(sMD5);
    return WUtil.toBoolean(rpcExecute("FM.endUpload", parameters, 0), false);
  }

  public boolean upload(String sRemoteDirectory, String sLocalFilePath) throws Exception {
    return upload(sRemoteDirectory, sLocalFilePath, 50 * 1024);
  }

  public boolean upload(String sRemoteDirectory, String sLocalFilePath, int iBlock) throws Exception {
    if (sRemoteDirectory == null || sRemoteDirectory.length() == 0) {
      sRemoteDirectory = sUSER_HOME;
    }
    if (sLocalFilePath == null || sLocalFilePath.length() == 0) {
      return false;
    }
    File file = new File(sLocalFilePath);
    if(!file.exists()) return false;
    if(file.isDirectory()) {
      return uploadDirectory(sRemoteDirectory, sLocalFilePath, iBlock);
    }
    return uploadFile(sRemoteDirectory, sLocalFilePath, iBlock, false);
  }
  
  protected boolean uploadDirectory(String sRemoteDirectory, String sLocalDirectory, int iBlock) throws Exception {
    boolean boResult = true;
    File fLocalDirectory = new File(sLocalDirectory);
    String sParent = fLocalDirectory.getParent();
    char cRemoteSeparator = getSeparator(sRemoteDirectory);
    List<String> listFiles = getFiles(null, fLocalDirectory);
    if(listFiles == null || listFiles.size() == 0) return false;
    Collections.sort(listFiles);
    for(int i = 0; i < listFiles.size(); i++) {
      String sFile     = listFiles.get(i);
      String sFolder   = getFolder(sFile);
      String sFilePath = sParent + File.separator + sFile;
      String sRemDir   = sRemoteDirectory + cRemoteSeparator + sFolder.replace('\\', cRemoteSeparator).replace('/', cRemoteSeparator);
      boResult = boResult && uploadFile(sRemDir, sFilePath, iBlock, true);
    }
    return boResult;
  }
  
  protected boolean uploadFile(String sRemoteDirectory, String sLocalFilePath, int iBlock, boolean boMakeDirs) throws Exception {
    log.println("upload " + sLocalFilePath + "...");
    Boolean oResult  = null;
    String sFileName = getFileName(sLocalFilePath);
    // Start Upload
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(encrypt(sRemoteDirectory));
    parameters.add(encrypt(sFileName));
    parameters.add(boMakeDirs);
    String sTmpFile = WUtil.toString(rpcExecute("FM.startUpload", parameters, 0), "");
    // Upload content
    parameters.clear();
    parameters.add(encrypt(sTmpFile));
    parameters.add(new byte[0]);
    MessageDigest md = MessageDigest.getInstance("MD5");
    FileInputStream is = null;
    try {
      is = new FileInputStream(sLocalFilePath);
      int iAvailable   = is.available();
      int iBufferLegth = iBlock;
      int iBytesReaded = 0;
      int iTransferred = 0;
      int iPercentage  = 0;
      byte[] abBuffer  = new byte[iBufferLegth];
      while ((iBytesReaded = is.read(abBuffer)) > 0) {
        md.update(abBuffer, 0, iBytesReaded);
        if (iBytesReaded < iBufferLegth) {
          byte[] abReductBuffer = new byte[iBytesReaded];
          System.arraycopy(abBuffer, 0, abReductBuffer, 0, iBytesReaded);
          parameters.set(1, abReductBuffer);
        } else {
          parameters.set(1, abBuffer);
        }
        rpcExecute("FM.appendContent", parameters, 0);
        iTransferred += iBytesReaded;
        iPercentage = (iTransferred * 100) / iAvailable;
        log.println(iPercentage + "%");
      }
      if(iAvailable == 0) {
        parameters.set(1, new byte[0]);
        rpcExecute("FM.appendContent", parameters, 0);
        log.println("100%");
      }
      // End upload
      String sMD5 = String.valueOf(Base64Coder.encode(md.digest()));
      parameters.set(1, sMD5 != null ? sMD5.trim() : "");
      oResult = WUtil.toBooleanObj(rpcExecute("FM.endUpload", parameters, 0), null);
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
    }
    if(oResult != null && oResult.booleanValue()) {
      log.println("completed.");
      return true;
    }
    return false;
  }
  
  protected void init() throws Exception {
    if(url == null || url.length() < 7) {
      url = "http://localhost:8080/LJSA/ws";
    }
    char p = 'j';
    if(prot != null && prot.length() > 0) {
      p = prot.toLowerCase().charAt(0);
    }
    if(p == 'j') {
      rpcInvoker = new JsonRpcInvoker(url);
    }
    else if(p == 'x') {
      rpcInvoker = new XmlRpcInvoker(url);
    }
    else if(p == 's') {
      rpcInvoker = new SoapRpcInvoker(url);
    }
    else {
      rpcInvoker = new JsonRpcInvoker(url);
    }
    if(user != null && user.length() > 0) {
      RpcClientTransport transport = rpcInvoker.getTransport();
      if(transport != null) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Authorization", "Basic " + Base64Coder.encodeString(user + ":" + pass));
        transport.setHeaders(headers);
      }
    }
  }

  protected Object rpcExecute(String method, List<Object> parameters, int timeOut)
      throws Exception 
  {
    if(timeOut > 0) {
        int elapsed = 0;
        oResult    = null;
        oException = null;
        processing = true;
        rpcInvoker.invokeAsync(method, parameters, this);
        while(processing) {
          Thread.sleep(100);
          elapsed += 100;
          if(elapsed > timeOut) throw new Exception("RPC call timed out");
        }
        if(oException != null) throw oException;
        return oResult;
    }
    try {
      return rpcInvoker.invoke(method, parameters);
    }
    catch(Throwable th) {
      throw new Exception(th);
    }
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public void handleResult(String sMethod, Collection colArgs, Object result) {
    this.oResult    = result;
    this.processing = false;
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public void handleError(String sMethod, Collection colArgs, Throwable error) {
    this.oException = new Exception(error);
    this.processing = false;
  }
  
  public static String getFileName(String sFilePath) {
    if (sFilePath == null)
      return null;
    int iLength = sFilePath.length();
    for (int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if (c == '/' || c == '\\') {
        return sFilePath.substring(iIndex + 1);
      }
    }
    return sFilePath;
  }

  public static String getFolder(String sFilePath) {
    int iLength = sFilePath.length();
    for (int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if (c == '/' || c == '\\') {
        return sFilePath.substring(0, iIndex);
      }
    }
    return "";
  }

  public static char getSeparator(String sFilePath) {
    int iLength = sFilePath.length();
    for (int i = 0; i < iLength; i++) {
      char c = sFilePath.charAt(i);
      if (c == '/' || c == '\\')
        return c;
    }
    return '/';
  }

  public static boolean isPath(String sText) {
    if (sText == null)
      return false;
    int iLength = sText.length();
    for (int i = 0; i < iLength; i++) {
      char c = sText.charAt(i);
      if (c == '/' || c == '\\')
        return true;
    }
    return false;
  }

  public static List<String> getFiles(String sPrefix, File fLocalDirectory) {
    File[] afFiles = fLocalDirectory.listFiles();
    String sDirName = fLocalDirectory.getName();
    List<String> listResult = new ArrayList<String>();
    if (afFiles != null && afFiles.length > 0) {
      for (int i = 0; i < afFiles.length; i++) {
        File file = afFiles[i];
        if (file.isDirectory()) {
          if (sPrefix != null && sPrefix.length() > 0) {
            listResult.addAll(getFiles(sPrefix + File.separator + sDirName, file));
          } 
          else {
            listResult.addAll(getFiles(sDirName, file));
          }
          continue;
        }
        String sFile = null;
        if (sPrefix != null && sPrefix.length() > 0) {
          sFile = sPrefix + File.separator + sDirName + File.separator + file.getName();
        } 
        else {
          sFile = sDirName + File.separator + file.getName();
        }
        listResult.add(sFile);
      }
    }
    return listResult;
  }

  public static String encrypt(String sText) {
    if (sText == null)
      return null;
    // La chiave può contenere caratteri che appartengono all'insieme [32
    // (spazio) - 95 (_)]
    String sKey = "@X<:S=?'B;F)<=B>D@?=:D';@=B<?C;)@:'/=?A-X0=;(?1<X!";
    int k = 0;
    StringBuffer sb = new StringBuffer(sText.length());
    for (int i = 0; i < sText.length(); i++) {
      if (k >= sKey.length() - 1) {
        k = 0;
      } else {
        k++;
      }
      int c = sText.charAt(i);
      int d = sKey.charAt(k);
      int r = c;
      if (c >= 32 && c <= 126) {
        r = r - d;
        if (r < 32) {
          r = 127 + r - 32;
        }
      }
      sb.append((char) r);
    }
    return sb.toString();
  }
  
  public static class EmptyOutputStream extends OutputStream {
    public void write(int iByte) throws java.io.IOException {
    }
  }
}
