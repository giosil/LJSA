package org.dew.ljsa.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.rpc.util.Base64Coder;

import org.dew.swingup.ISessionManager;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.User;
import org.dew.swingup.rpc.IRPCClient;

import org.dew.util.WUtil;

public
class SessionManager implements ISessionManager
{
  private User user;
  
  @Override
  public
  boolean isActive()
  {
    return user != null;
  }
  
  @Override
  public
  String getUserMessage()
  {
    return null;
  }
  
  @Override
  public
  User getUser()
  {
    return user;
  }
  
  @Override
  public
  void login(String sIdService, String sUserName, String sPassword, String sIdClient)
      throws Exception
  {
    user = null;
    AppUtil.clear();
    
    Map<String, Object> headers = new HashMap<String, Object>();
    headers.put("Authorization", "Basic " + Base64Coder.encodeString(sUserName + ":" + sPassword));
    
    String sWS_URL = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_URL);
    String sWS_BAK = ResourcesMgr.config.getProperty(ResourcesMgr.sAPP_RPC_BAK);
    
    IRPCClient rpcClient = ResourcesMgr.createIRPCClient();
    rpcClient.init(sWS_URL, sWS_BAK);
    rpcClient.setHeaders(headers);
    List<String> services = null;
    try {
      services = WUtil.toListOfString(rpcClient.execute("CREDENZIALI.getServicesEnabled", Collections.EMPTY_LIST));
    }
    catch(Exception ex) {
      String message = ex.getMessage();
      System.err.println("Exception: " + ex);
      if(message != null && message.length() > 0) {
        if(message.indexOf("401") >= 0 || message.indexOf("403") >= 0) {
          return;
        }
      }
      throw new Exception("Servizio non disponibile");
    }
    
    if(services == null || services.size() == 0) {
      return;
    }
    
    ResourcesMgr.setDefaultRPCClient(rpcClient);
    
    user = new User();
    user.setUserName(sUserName);
    user.setPassword(sPassword);
    user.setRole("admin");
    user.setDatePassword(new Date());
    user.setDateLastAccess(new Date());
    user.setCurrentIdClient(sIdClient);
    
    AppUtil.setServiziAbilitati(services);
  }
  
  @Override
  public
  void login(String sIdService, byte[] abSignature, String sIdClient)
      throws Exception
  {
    user = null;
    AppUtil.clear();
  }
  
  @Override
  public
  void logout()
  {
    user = null;
    AppUtil.clear();
  }
  
  @Override
  public
  void changePassword(String sNewPassword)
      throws Exception
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public
  List<String> getClients(String sIdServices)
      throws Exception
  {
    List<String> listResult = new ArrayList<String>(1);
    listResult.add("DEFAULT");
    return listResult;
  }
}
