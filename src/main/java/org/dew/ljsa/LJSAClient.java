package org.dew.ljsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.rpc.client.JsonRpcClient;
import org.rpc.client.RpcClient;
import org.soap.rpc.client.SoapRpcClient;
import org.util.WUtil;
import org.xml.rpc.client.XmlRpcClient;

/**
 * Classe di accesso ai servizi LJSA.
 */
public
class LJSAClient
{
  protected RpcClient rpcClient;
  protected String url;
  protected String user;
  protected String pass;
  
  public
  LJSAClient(String url, String user, String pass)
    throws Exception
  {
    this.url  = url;
    this.user = user;
    this.pass = pass;
    
    rpcClient = new JsonRpcClient(url);
  }
  
  public
  LJSAClient(String url, String user, String pass, String prot)
    throws Exception
  {
    this.url  = url;
    this.user = user;
    this.pass = pass;
    
    if(prot == null || prot.length() == 0) {
      prot = "json-rpc";
    }
    else {
      prot = prot.trim().toLowerCase();
    }
    
    if(prot.startsWith("x")) {
      rpcClient = new XmlRpcClient(url);
    }
    else if(prot.startsWith("s")) {
      rpcClient = new SoapRpcClient(url);
    }
    else {
      rpcClient = new JsonRpcClient(url);
    }
  }
  
  public
  boolean check()
    throws Exception
  {
    Object result = execute("check", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  String getVersion()
    throws Exception
  {
    Object result = execute("getVersion", user, pass);
    
    return WUtil.toString(result, null);
  }
  
  public
  String getEmail(String idServizio, String idCredenziale)
    throws Exception
  {
    Object result = execute("getEmail", idServizio, idCredenziale, user, pass);
    
    return WUtil.toString(result, null);
  }
  
  public
  boolean pingDataSource(String dataSource)
    throws Exception
  {
    Object result = execute("pingDataSource", user, pass, dataSource);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  Map<String, Object> getConfiguration()
    throws Exception
  {
    Object result = execute("getConfiguration", user, pass);
    
    return WUtil.toMapObject(result);
  }
  
  public
  Map<String, Object> readInfo()
    throws Exception
  {
    Object result = execute("readInfo", user, pass);
    
    return WUtil.toMapObject(result);
  }
  
  public
  List<Attivita> findAttivita(Attivita attivita)
    throws Exception
  {
    Map<String, Object> mapAttivita = attivita != null ? attivita.toMap() : new HashMap<String, Object>();
    
    Object result = execute("findAttivita", mapAttivita, user, pass);
    
    return toListOfAttivita(result);
  }
  
  public
  boolean addAttivita(Attivita attivita)
    throws Exception
  {
    Object result = execute("addAttivita", attivita.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean updateAttivita(Attivita attivita)
    throws Exception
  {
    Object result = execute("updateAttivita", attivita.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean enableAttivita(Attivita attivita)
    throws Exception
  {
    Object result = execute("enableAttivita", attivita.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean disableAttivita(Attivita attivita)
    throws Exception
  {
    Object result = execute("disableAttivita", attivita.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean removeAttivita(Attivita attivita)
    throws Exception
  {
    Object result = execute("removeAttivita", attivita.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  int addSchedulazione(Schedulazione schedulazione)
    throws Exception
  {
    Object result = execute("addSchedulazione", schedulazione.toMap(), user, pass);
    
    return WUtil.toInt(result, 0);
  }
  
  public
  boolean updateSchedulazione(Schedulazione schedulazione)
    throws Exception
  {
    Object result = execute("updateSchedulazione", schedulazione.toMap(), user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  List<Map<String, Object>> getSchedulazioni()
    throws Exception
  {
    Object result = execute("getSchedulazioni", user, pass);
    
    return WUtil.toListOfMapObject(result);
  }
  
  public
  List<Map<String, Object>> getInfoSchedulazioni()
    throws Exception
  {
    Object result = execute("getInfoSchedulazioni", user, pass);
    
    return WUtil.toListOfMapObject(result);
  }
  
  public
  boolean start()
    throws Exception
  {
    Object result = execute("start", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean stop()
    throws Exception
  {
    Object result = execute("stop", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean update()
    throws Exception
  {
    Object result = execute("update", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean isStarted()
    throws Exception
  {
    Object result = execute("isStarted", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean isRunning()
    throws Exception
  {
    Object result = execute("isRunning", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean wakeUp()
    throws Exception
  {
    Object result = execute("wakeUp", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean setSleepingMode(boolean flag)
    throws Exception
  {
    Object result = execute("setSleepingMode", user, pass, flag);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean layDown()
    throws Exception
  {
    Object result = execute("layDown", user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean enableSchedulazione(int idSchedulazione, String idCredenziale)
    throws Exception
  {
    Object result = execute("enableSchedulazione", idSchedulazione, idCredenziale, user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean disableSchedulazione(int idSchedulazione, String idCredenziale)
    throws Exception
  {
    Object result = execute("disableSchedulazione", idSchedulazione, idCredenziale, user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  public
  boolean removeSchedulazione(int idSchedulazione)
    throws Exception
  {
    Object result = execute("removeSchedulazione", idSchedulazione, user, pass);
    
    return WUtil.toBoolean(result, false);
  }
  
  protected
  Object execute(String method, Object... parameters)
    throws Exception
  {
    return rpcClient.execute("LJSA." + method, Arrays.asList(parameters));
  }
  
  protected
  List<Attivita> toListOfAttivita(Object object)
    throws Exception
  {
    List<Attivita> listResult = new ArrayList<Attivita>();
    
    List<Map<String, Object>> items = WUtil.toListOfMapObject(object);
    if(items == null) return listResult;
    
    for(Map<String, Object> item : items) {
      listResult.add(new Attivita(item));
    }
    return listResult;
  }
}
