package org.dew.ljsa.backend.rpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.dew.ljsa.ISchedulatore;
import org.dew.ljsa.LJSAClient;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class WSSchedulatore implements ISchedulatore
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public
  List<String> getServicesByIstances()
      throws Exception
  {
    List<String> listResult = new ArrayList<String>();
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT ID_SCHEDULATORE FROM LJSA_SCHEDULATORI WHERE ATTIVO=? ORDER BY ID_SCHEDULATORE");
      pstm.setString(1, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      while(rs.next()) {
        String idSchedulatore = rs.getString("ID_SCHEDULATORE");
        int sep = idSchedulatore.indexOf('-');
        if(sep > 0) {
          listResult.add(idSchedulatore.substring(sep + 1));
        }
        else {
          listResult.add("");
        }
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.getServicesByIstances()", ex);
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  Map<String, Object> readInfo(String idServizio)
      throws Exception
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    
    try {
      // Si ricarica la configurazione del client LJSA...
      LJSAFactory.loadConfig();
      
      if(idServizio != null && idServizio.length() > 0) {
        String sURL = LJSAFactory.getURL(idServizio);
        if(sURL != null && sURL.length() > 0) {
          mapResult.put(sINFO_ID_SCHEDULATORE, "LJSA-" + idServizio);
          mapResult.put(sINFO_URL_SERVIZIO,    sURL);
        }
        else {
          mapResult.put(sINFO_ID_SCHEDULATORE, "LJSA");
          mapResult.put(sINFO_URL_SERVIZIO,    LJSAFactory.getURL());
        }
      }
      else {
        mapResult.put(sINFO_ID_SCHEDULATORE, "LJSA");
        mapResult.put(sINFO_URL_SERVIZIO,    LJSAFactory.getURL());
      }
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      mapResult.putAll(ljsaClient.readInfo());
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.readInfo(" + idServizio + ")", ex);
      mapResult.put(sINFO_STATO,             "X");
      mapResult.put(sINFO_DESCRIZIONE_STATO, "Server unreachable");
    }
    return mapResult;
  }
  
  public
  boolean pingDataSource(String idServizio, String dataSource)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.pingDataSource(dataSource);
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.pingDataSource(" + idServizio + "," + dataSource + ")", ex);
      throw ex;
    }
  }
  
  public
  List<Map<String, Object>> getInfoSchedulazioni(String idServizio)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.getInfoSchedulazioni();
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.getInfoSchedulazioni(" + idServizio + ")", ex);
      throw ex;
    }
  }
  
  public
  Map<String, Object> getConfiguration(String idServizio)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.getConfiguration();
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.getConfiguration(" + idServizio + ")", ex);
      throw ex;
    }
  }
  
  public
  boolean start(String idServizio)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.start();
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.start(" + idServizio + ")", ex);
      throw ex;
    }
  }
  
  public
  boolean stop(String idServizio)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.stop();
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.stop(" + idServizio + ")", ex);
      throw ex;
    }
  }
  
  public
  boolean update(String idServizio)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.update();
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulatore.update(" + idServizio + ")", ex);
      throw ex;
    }
  }
}
