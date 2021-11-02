package org.dew.ljsa.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dew.ljsa.ICommon;
import org.dew.ljsa.IServizio;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.User;

import org.dew.util.WUtil;

public
class AppUtil
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
        List<String> servizio = new ArrayList<String>(3);
        servizio.add(idServizio); // id
        servizio.add(idServizio); // code
        servizio.add(idServizio); // description
        
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
