package org.dew.ljsa.backend.rpc;

import java.security.Principal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.dew.ljsa.ICredenziale;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DataUtil;
import org.dew.ljsa.backend.util.QueryBuilder;

import org.rpc.util.RPCContext;

import org.util.WMap;
import org.util.WUtil;

public
class WSCredenziali implements ICredenziale
{
  protected static Logger logger = Logger.getLogger(WSCredenziali.class);
  
  public static
  List<String> getServicesEnabled()
    throws Exception
  {
    Principal principal = RPCContext.getUserPrincipal();
    if(principal == null) {
      return new ArrayList<String>();
    }
    String username = principal.getName();
    if(username == null || username.length() == 0) {
      return new ArrayList<String>();
    }
    boolean isEmail = username.indexOf('@') > 0 && username.indexOf('.') >= 0;
    
    List<String> listResult = new ArrayList<String>();
    
    String sSQL = "SELECT ID_SERVIZIO FROM LJSA_CREDENZIALI ";
    if(isEmail) {
      sSQL += "WHERE EMAIL=? AND ATTIVO=? ";
    }
    else {
      sSQL += "WHERE ID_CREDENZIALE=? AND ATTIVO=? ";
    }
    sSQL += "ORDER BY ID_SERVIZIO";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, username);
      pstm.setString(2, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      while(rs.next()) {
        listResult.add(rs.getString("ID_SERVIZIO"));
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.getServicesEnabled()<" + username + ">", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public static
  List<String> authenticate(String username, String password)
    throws Exception
  {
    logger.debug("WSCredenziali.authenticate(" + username + ",*)...");
    
    if(username == null || username.length() == 0) {
      return new ArrayList<String>();
    }
    if(password == null || password.length() == 0) {
      return new ArrayList<String>();
    }
    boolean isEmail = username.indexOf('@') > 0 && username.indexOf('.') >= 0;
    
    List<String> listResult = new ArrayList<String>();
    
    String sSQL = "SELECT ID_SERVIZIO FROM LJSA_CREDENZIALI ";
    if(isEmail) {
      sSQL += "WHERE EMAIL=? AND CREDENZIALI=? AND ATTIVO=? ";
    }
    else {
      sSQL += "WHERE ID_CREDENZIALE=? AND CREDENZIALI=? AND ATTIVO=? ";
    }
    sSQL += "ORDER BY ID_SERVIZIO";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, username);
      pstm.setString(2, String.valueOf(password.hashCode()));
      pstm.setString(3, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      while(rs.next()) {
        listResult.add(rs.getString("ID_SERVIZIO"));
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.authenticate(" + username + ",*)", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  List<Map<String, Object>> find(Map<String, Object> mapFilter, List<String> listServices)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    normalize(mapFilter);
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO",    sID_SERVIZIO + "%");
    qb.put("ID_CREDENZIALE", "%" + sID_CREDENZIALE + "%");
    qb.put("EMAIL",          "%" + sEMAIL + "%");
    qb.put("ATTIVO",         sATTIVO);
    
    String sInClause = null;
    if(listServices != null && listServices.size() > 0) {
      String sInSet = DataUtil.buildInSet(listServices);
      sInClause = "ID_SERVIZIO IN (" + sInSet + ")";
    }
    String sSQL = qb.select("LJSA_CREDENZIALI", mapFilter, sInClause);
    sSQL += " ORDER BY ID_SERVIZIO,ID_CREDENZIALE";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sIdServizio    = rs.getString("ID_SERVIZIO");
        String sIdCredenziale = rs.getString("ID_CREDENZIALE");
        String sEmail         = rs.getString("EMAIL");
        String sAttivo        = rs.getString("ATTIVO");
        
        WMap record = new WMap();
        record.put(sID_SERVIZIO,    sIdServizio);
        record.put(sID_CREDENZIALE, sIdCredenziale);
        record.put(sEMAIL,          sEmail);
        record.putBoolean(sATTIVO,  sAttivo);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.find(" + mapFilter + "," + listServices + ")", ex);
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
    qb.put("ID_SERVIZIO",    sID_SERVIZIO + "%");
    qb.put("ID_CREDENZIALE", "%" + sID_CREDENZIALE + "%");
    qb.put("ATTIVO",         sATTIVO);
    
    String sAddClause = null;
    if(listServices != null && listServices.size() > 0) {
      String sInSet = DataUtil.buildInSet(listServices);
      sAddClause = "ID_SERVIZIO IN (" + sInSet + ")";
    }
    String sSQL = qb.select("LJSA_CREDENZIALI", mapFilter, sAddClause);
    sSQL += " ORDER BY ID_SERVIZIO,ID_CREDENZIALE";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      
      rs = stm.executeQuery(sSQL);
      
      while(rs.next()) {
        String sIdCredenziale = rs.getString("ID_CREDENZIALE");
        String sAttivo        = rs.getString("ATTIVO");
        
        // 0 = Normal (Attivo), 1 = Disabled (Disattivo)
        int marker = WUtil.toBoolean(sAttivo, false) ? 0 : 1;
        
        List<Object> record = new ArrayList<Object>();
        record.add(sIdCredenziale);
        record.add(sIdCredenziale);
        record.add(sIdCredenziale);
        record.add(marker);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.lookup(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    
    return listResult;
  }
  
  public
  Map<String, Object> read(String idServizio, String idCredenziale)
    throws Exception
  {
    WMap result = new WMap();
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("EMAIL");
    qb.add("ATTIVO");
    String sSQL = qb.select("LJSA_CREDENZIALI");
    sSQL += "WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idCredenziale);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sEmail  = rs.getString("EMAIL");
        String sAttivo = rs.getString("ATTIVO");
        
        result.put(sID_SERVIZIO,    idServizio);
        result.put(sID_CREDENZIALE, idCredenziale);
        result.put(sEMAIL,          sEmail);
        result.putBoolean(sATTIVO,  sAttivo);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.read(" + idServizio + "," + idCredenziale + ")", ex);
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
    
    String idServizio    = WUtil.toUpperString(mapValues.get(sID_SERVIZIO), "");
    String idCredenziale = WUtil.toString(mapValues.get(sID_CREDENZIALE),   "");
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT ID_SERVIZIO FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?");
      pstm.setString(1, idServizio);
      pstm.setString(2, idCredenziale);
      rs = pstm.executeQuery();
      boResult = rs.next();
    }
    catch(Exception ex) {
      logger.error("Exception in WSCredenziali.exists(" + mapValues + ")", ex);
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
    WMap wmValues = new WMap(mapValues);
    String sIdServizio    = wmValues.getUpperString(sID_SERVIZIO);
    String sIdCredenziale = wmValues.getString(sID_CREDENZIALE);
    String sCredenziali   = wmValues.getString(sCREDENZIALI);
    String sEmail         = wmValues.getLowerString(sEMAIL);
    int    hashCodeCred   = sCredenziali != null ? sCredenziali.hashCode() : 0;
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("ID_SERVIZIO");
    qb.add("ID_CREDENZIALE");
    qb.add("CREDENZIALI");
    qb.add("EMAIL");
    qb.add("ATTIVO");
    String sSQL = qb.insert("LJSA_CREDENZIALI", true);
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sIdServizio);
      pstm.setString(2, sIdCredenziale);
      pstm.setString(3, String.valueOf(hashCodeCred));
      pstm.setString(4, sEmail);
      pstm.setString(5, QueryBuilder.decodeBoolean(true));
      pstm.executeUpdate();
      
      ut.commit();
      
      // Value can change (upper/lowe)
      mapValues.put(sID_SERVIZIO, sIdServizio);
      mapValues.put(sEMAIL,       sEmail);
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSCredenziali.insert(" + mapValues + ")", ex);
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
    WMap wmValues = new WMap(mapValues);
    String sIdServizio    = wmValues.getUpperString(sID_SERVIZIO);
    String sIdCredenziale = wmValues.getString(sID_CREDENZIALE);
    String sCredenziali   = wmValues.getString(sCREDENZIALI);
    String sEmail         = wmValues.getLowerString(sEMAIL);
    int    hashCodeCred   = sCredenziali != null ? sCredenziali.hashCode() : 0;
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("EMAIL");
    if(sCredenziali != null && sCredenziali.length() > 0) {
      qb.add("CREDENZIALI");
    }
    String sSQL = qb.update("LJSA_CREDENZIALI", true);
    sSQL += "WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?";
    
    int p = 0;
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(++p, sEmail);
      if(sCredenziali != null && sCredenziali.length() > 0) {
        pstm.setString(++p, String.valueOf(hashCodeCred));
      }
      // WHERE
      pstm.setString(++p, sIdServizio);
      pstm.setString(++p, sIdCredenziale);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSCredenziali.update(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return mapValues;
  }
  
  public
  boolean delete(String idServizio, String idCredenziale)
    throws Exception
  {
    if(idServizio == null || idServizio.length() == 0) {
      return false;
    }
    if(idCredenziale == null || idCredenziale.length() == 0) {
      return false;
    }
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("DELETE FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?");
      pstm.setString(1, idServizio.toUpperCase());
      pstm.setString(2, idCredenziale);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSCredenziali.delete(" + idServizio + "," + idCredenziale + ")", ex);
      return false;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return true;
  }
  
  public
  boolean setEnabled(String idServizio, String idCredenziale, boolean boEnabled)
    throws Exception
  {
    if(idServizio == null || idServizio.length() == 0) {
      return true; // Enabled (Default)
    }
    if(idCredenziale == null || idCredenziale.length() == 0) {
      return true; // Enabled (Default)
    }
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("UPDATE LJSA_CREDENZIALI SET ATTIVO=? WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?");
      pstm.setString(1, QueryBuilder.decodeBoolean(boEnabled));
      pstm.setString(2, idServizio);
      pstm.setString(3, idCredenziale);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSCredenziali.setEnabled(" + idServizio + "," + idCredenziale + "," + boEnabled + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return boEnabled;
  }
  
  protected
  void normalize(Map<String, Object> mapValues)
  {
    if(mapValues == null) return;
    
    String idServizio = WUtil.toString(mapValues.get(sID_SERVIZIO), null);
    if(idServizio != null) {
      mapValues.put(sID_SERVIZIO, idServizio.trim().toUpperCase());
    }
    String email = WUtil.toString(mapValues.get(sEMAIL), null);
    if(email != null) {
      mapValues.put(sEMAIL, email.trim().toLowerCase());
    }
  }
}
