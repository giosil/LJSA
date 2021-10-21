package org.dew.ljsa.backend.rpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.dew.ljsa.IServizio;

import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DataUtil;
import org.dew.ljsa.backend.util.QueryBuilder;

import org.util.WMap;
import org.util.WUtil;

public
class WSServizi implements IServizio
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public
  List<Map<String, Object>> find(Map<String, Object> mapFilter, List<String> listServices)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    // Effettua l'upper-case dell'identificativo con l'eccezione dei servizi di posta elettronica.
    normalize(mapFilter);
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO", sID_SERVIZIO + "%");
    qb.put("DESCRIZIONE", "%" + sDESCRIZIONE + "%");
    qb.put("ATTIVO",      sATTIVO);
    
    String sInClause = null;
    if(listServices != null && listServices.size() > 0) {
      String sInSet = DataUtil.buildInSet(listServices);
      sInClause = "ID_SERVIZIO IN (" + sInSet + ")";
    }
    String sSQL = qb.select("LJSA_SERVIZI", mapFilter, sInClause);
    sSQL += " ORDER BY ID_SERVIZIO";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sIdServizio  = rs.getString("ID_SERVIZIO");
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sAttivo      = rs.getString("ATTIVO");
        
        WMap record = new WMap();
        record.put(sID_SERVIZIO,   sIdServizio);
        record.put(sDESCRIZIONE,   sDescrizione);
        record.putBoolean(sATTIVO, sAttivo);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.find(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  List<List<Object>> lookup(Map<String, Object> mapFilter, List<String> listServices)
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    normalize(mapFilter);
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO", sID_SERVIZIO + "%");
    qb.put("DESCRIZIONE", "%" + sDESCRIZIONE + "%");
    qb.put("ATTIVO",      sATTIVO);
    
    String sAddClause = null;
    if(listServices != null && listServices.size() > 0) {
      String sInSet = DataUtil.buildInSet(listServices);
      sAddClause = "ID_SERVIZIO IN (" + sInSet + ")";
    }
    String sSQL = qb.select("LJSA_SERVIZI", mapFilter, sAddClause);
    sSQL += " ORDER BY ID_SERVIZIO";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      
      rs = stm.executeQuery(sSQL);
      
      while(rs.next()) {
        String sIdServizio  = rs.getString("ID_SERVIZIO");
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sAttivo      = rs.getString("ATTIVO");
        
        // 0 = Normal (Attivo), 1 = Disabled (Disattivo)
        int marker = WUtil.toBoolean(sAttivo, false) ? 0 : 1;
        
        List<Object> record = new ArrayList<Object>();
        record.add(sIdServizio);
        record.add(sIdServizio);
        record.add(sDescrizione);
        record.add(marker);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.lookup(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    
    return listResult;
  }
  
  public
  Map<String, Object> read(String idServizio)
    throws Exception
  {
    WMap result = new WMap();
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("DESCRIZIONE");
    qb.add("ATTIVO");
    String sSQL = qb.select("LJSA_SERVIZI");
    sSQL += "WHERE ID_SERVIZIO=?";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sAttivo      = rs.getString("ATTIVO");
        
        result.put(sID_SERVIZIO,   idServizio);
        result.put(sDESCRIZIONE,   sDescrizione);
        result.putBoolean(sATTIVO, sAttivo);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.read(" + idServizio + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    
    return result.toMapObject();
  }
  
  public
  boolean exists(Map<String, Object> mapValues)
    throws Exception
  {
    boolean boResult = false;
    
    String idServizio = WUtil.toUpperString(mapValues.get(sID_SERVIZIO), "");
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT ID_SERVIZIO FROM LJSA_SERVIZI WHERE ID_SERVIZIO=?");
      pstm.setString(1, idServizio);
      rs = pstm.executeQuery();
      boResult = rs.next();
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.exists(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return boResult;
  }
  
  public
  Map<String, Object> insert(Map<String, Object> mapValues)
    throws Exception
  {
    QueryBuilder qb = new QueryBuilder();
    qb.add("ID_SERVIZIO");
    qb.add("DESCRIZIONE");
    qb.add("ATTIVO");
    String sSQL = qb.insert("LJSA_SERVIZI", true);
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      WMap dmValues = new WMap(mapValues);
      String sIdServizio  = dmValues.getUpperString(sID_SERVIZIO);
      String sDescrizione = dmValues.getString(sDESCRIZIONE);
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sIdServizio);
      pstm.setString(2, sDescrizione);
      pstm.setString(3, QueryBuilder.decodeBoolean(true));
      pstm.executeUpdate();
      
      ut.commit();
      
      // Value can change (upper)
      mapValues.put(sID_SERVIZIO, sIdServizio);
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSServizi.insert(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return mapValues;
  }
  
  public
  Map<String, Object> update(Map<String, Object> mapValues)
    throws Exception
  {
    QueryBuilder qb = new QueryBuilder();
    qb.add("DESCRIZIONE"); 
    String sSQL = qb.update("LJSA_SERVIZI", true);
    sSQL += "WHERE ID_SERVIZIO=?";
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      WMap dmValues = new WMap(mapValues);
      String sIdServizio  = dmValues.getUpperString(sID_SERVIZIO);
      String sDescrizione = dmValues.getString(sDESCRIZIONE);
      
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, sDescrizione);
      // WHERE
      pstm.setString(2, sIdServizio);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSServizi.update(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return mapValues;
  }
  
  public
  boolean delete(String idServizio)
    throws Exception
  {
    if(idServizio == null || idServizio.length() == 0) {
      return false;
    }
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      int countCredenziali = countCredenziali(conn, idServizio);
      if(countCredenziali > 0) return false;
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("DELETE FROM LJSA_SERVIZI WHERE ID_SERVIZIO=?");
      pstm.setString(1, idServizio.toUpperCase());
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSServizi.delete(" + idServizio + ")", ex);
      return false;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return true;
  }
  
  public
  boolean setEnabled(String idServizio, boolean boEnabled)
    throws Exception
  {
    if(idServizio == null || idServizio.length() == 0) {
      return true; // Enabled (Default)
    }
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("UPDATE LJSA_SERVIZI SET ATTIVO=? WHERE ID_SERVIZIO=?");
      pstm.setString(1, QueryBuilder.decodeBoolean(boEnabled));
      pstm.setString(2, idServizio);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSServizi.setEnabled(" + idServizio + "," + boEnabled + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return boEnabled;
  }
  
  public
  List<String> getServicesByUserName(String idCredenziale)
    throws Exception
  {
    List<String> result = new ArrayList<String>();
    
    String sSQL = "SELECT C.ID_SERVIZIO ";
    sSQL += "FROM LJSA_CREDENZIALI C,LJSA_SERVIZI S ";
    sSQL += "WHERE C.ID_SERVIZIO=S.ID_SERVIZIO AND C.ID_CREDENZIALE=?";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idCredenziale);
      rs = pstm.executeQuery();
      while(rs.next()) {
        result.add(rs.getString(1));
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.getServicesByUserName(" + idCredenziale + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return result;
  }
  
  protected
  int countCredenziali(Connection conn, String idServizio)
    throws Exception
  {
    if(idServizio == null || idServizio.length() == 0) {
      return 0;
    }
    int result = 0;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT COUNT(*) FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=?");
      pstm.setString(1, idServizio.toUpperCase());
      rs = pstm.executeQuery();
      if(rs.next()) result = rs.getInt(1);
    }
    catch(Exception ex) {
      logger.error("Exception in WSServizi.countCredenziali(conn," + idServizio + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return result;
  }

  protected
  void normalize(Map<String, Object> mapValues)
  {
    if(mapValues == null) return;
    
    String idServizio = WUtil.toString(mapValues.get(sID_SERVIZIO), null);
    if(idServizio != null) {
      mapValues.put(sID_SERVIZIO, idServizio.trim().toUpperCase());
    }
  }
}

