package org.dew.ljsa.backend.rpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import org.dew.ljsa.Attivita;
import org.dew.ljsa.LJSAClient;
import org.dew.ljsa.Schedulazione;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class LJSAFactory
{
  protected static String sURL;
  protected static Map<String, String> mapServicesURL;
  
  protected static Logger logger = Logger.getLogger(LJSAFactory.class);
  
  static {
    loadConfig();
  }
  
  public static
  String getURL()
  {
    return sURL;
  }
  
  public static
  String getURL(String idServizio)
  {
    if(idServizio == null || idServizio.length() == 0) {
      return sURL;
    }
    if(mapServicesURL == null) return null;
    return mapServicesURL.get(idServizio);
  }
  
  public static
  LJSAClient createLJSAClient(Schedulazione schedulazione)
      throws Exception
  {
    if(schedulazione == null) {
      return createLJSAClient((String) null);
    }
    return createLJSAClient(schedulazione.getIdServizio());
  }
  
  public static
  LJSAClient createLJSAClient(Attivita attivita)
      throws Exception
  {
    if(attivita == null) {
      return createLJSAClient((String) null);
    }
    return createLJSAClient(attivita.getIdServizio());
  }
  
  public static
  LJSAClient createLJSAClient(String idServizio)
      throws Exception
  {
    String sLJSAUser     = BEConfig.getProperty("ljsa.user",     "ljsadmin");
    String sLJSAPassword = BEConfig.getProperty("ljsa.password", "dew2006");
    if(idServizio == null || idServizio.length() == 0) {
      return new LJSAClient(sURL, sLJSAUser, sLJSAPassword);
    }
    
    sLJSAUser     = BEConfig.getProperty(idServizio + ".ljsa.user",     sLJSAUser);
    sLJSAPassword = BEConfig.getProperty(idServizio + ".ljsa.password", sLJSAPassword);
    
    String sLJSAURL = mapServicesURL != null ? (String) mapServicesURL.get(idServizio) : sURL;
    if(sLJSAURL == null || sLJSAURL.length() == 0) {
      sLJSAURL = sURL;
    }
    
    return new LJSAClient(sLJSAURL, sLJSAUser, sLJSAPassword);
  }
  
  public static
  void loadConfig()
  {
    mapServicesURL = new HashMap<String, String>();
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT ID_SCHEDULATORE,URL_SERVIZIO FROM LJSA_SCHEDULATORI WHERE ATTIVO=?");
      pstm.setString(1, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      while(rs.next()) {
        String idSchedulatore = rs.getString("ID_SCHEDULATORE");
        String sURLServizio   = rs.getString("URL_SERVIZIO");
        int iSep = idSchedulatore.indexOf('-');
        if(iSep > 0) {
          String idServizio = idSchedulatore.substring(iSep + 1);
          mapServicesURL.put(idServizio, sURLServizio);
        }
        else {
          sURL = sURLServizio;
        }
      }
    }
    catch(Exception ex) {
      logger.error("Exception in LJSAFactory.loadConfig()", ex);
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
  }
}
