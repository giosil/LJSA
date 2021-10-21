package org.dew.ljsa.backend.rpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.util.WMap;
import org.util.WUtil;

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.IClasse;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class WSClassi implements IClasse
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public
  List<List<Object>> lookup(Map<String, Object> mapFilter)
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("CLASSE",      sCLASSE);
    qb.put("DESCRIZIONE", sDESCRIZIONE);
    String sSQL = qb.select("LJSA_CLASSI", mapFilter);
    sSQL += " ORDER BY CLASSE";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sClasse      = rs.getString("CLASSE");
        String sDescrizione = rs.getString("DESCRIZIONE");
        
        List<Object> record = new ArrayList<Object>(3);
        record.add(sClasse);
        record.add(sClasse);
        record.add(sDescrizione);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.lookUp(" + mapFilter + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  List<Map<String, Object>> find(Map<String, Object> mapFilter)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("CLASSE",      sCLASSE);
    qb.put("DESCRIZIONE", sDESCRIZIONE);
    String sSQL = qb.select("LJSA_CLASSI", mapFilter);
    sSQL += " ORDER BY CLASSE";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm  = conn.createStatement();
      rs   = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sClasse      = rs.getString("CLASSE");
        String sDescrizione = rs.getString("DESCRIZIONE");
        
        Map<String, Object> record = new HashMap<String, Object>();
        record.put(sCLASSE,      sClasse);
        record.put(sDESCRIZIONE, sDescrizione);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.find(" + mapFilter + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  List<String> getPackages()
    throws Exception
  {
    List<String> listResult = new ArrayList<String>();
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery("SELECT CLASSE FROM LJSA_CLASSI ORDER BY CLASSE");
      while(rs.next()) {
        String classe = rs.getString("CLASSE");
        int lastDot = classe.lastIndexOf('.');
        if(lastDot < 0) continue;
        
        String packageName = classe.substring(0, lastDot);
        if(!listResult.contains(packageName)) {
          listResult.add(packageName);
        }
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.getPackages()", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  Map<String, Object> read(String classe)
    throws Exception
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT DESCRIZIONE FROM LJSA_CLASSI WHERE CLASSE=?");
      pstm.setString(1, classe);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sDescrizione = rs.getString("DESCRIZIONE");
        
        mapResult.put(sCLASSE,      classe);
        mapResult.put(sDESCRIZIONE, sDescrizione);
        mapResult.put(sATTIVITA,    readAttivita(conn, classe));
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.read(" + classe + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return mapResult;
  }
  
  public
  boolean exists(Map<String, Object> mapValues)
    throws Exception
  {
    boolean result = false;
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT CLASSE FROM LJSA_CLASSI WHERE CLASSE=?");
      pstm.setString(1, WUtil.toString(mapValues.get(sCLASSE), null));
      rs = pstm.executeQuery();
      result = rs.next();
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.exists(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return result;
  }
  
  public
  Map<String, Object> insert(Map<String, Object> mapValues)
    throws Exception
  {
    QueryBuilder qb = new QueryBuilder();
    qb.add("CLASSE");
    qb.add("DESCRIZIONE");
    String sSQL = qb.insert("LJSA_CLASSI", true);
    
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      WMap   wmValues = new WMap(mapValues);
      String sClasse      = wmValues.getString(sCLASSE);
      String sDescrizione = wmValues.getString(sDESCRIZIONE);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, sClasse);
      pstm.setString(2, sDescrizione);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSClassi.insert(" + mapValues + ")", ex);
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
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      WMap   wmValues = new WMap(mapValues);
      String sClasse      = wmValues.getString(sCLASSE);
      String sDescrizione = wmValues.getString(sDESCRIZIONE);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("UPDATE LJSA_CLASSI SET DESCRIZIONE=? WHERE CLASSE=?");
      // SET
      pstm.setString(1, sDescrizione);
      // WHERE
      pstm.setString(2, sClasse);
      pstm.executeUpdate();
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSClassi.update(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return mapValues;
  }
  
  public
  boolean delete(String classe)
    throws Exception
  {
    boolean result = false;
    Connection conn = null;
    UserTransaction ut = null;
    PreparedStatement pstm = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      pstm = conn.prepareStatement("DELETE FROM LJSA_CLASSI WHERE CLASSE=?");
      pstm.setString(1, classe);
      int upd = pstm.executeUpdate();
      
      ut.commit();
      
      result = upd > 0;
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSClassi.delete(" + classe + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(pstm, conn);
    }
    return result;
  }
  
  protected
  List<Map<String, Object>> readAttivita(Connection conn, String classe)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    String sSQL = "SELECT ID_SERVIZIO,ID_ATTIVITA,DESCRIZIONE,ATTIVO ";
    sSQL += "FROM LJSA_ATTIVITA ";
    sSQL += "WHERE CLASSE=? ";
    sSQL += "ORDER BY ID_SERVIZIO, ID_ATTIVITA ";
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, classe);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sIdServizio  = rs.getString("ID_SERVIZIO");
        String sIdAttivita  = rs.getString("ID_ATTIVITA");
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sAttivo      = rs.getString("ATTIVO");
        
        WMap record = new WMap();
        record.put(IAttivita.sID_SERVIZIO, sIdServizio);
        record.put(IAttivita.sID_ATTIVITA, sIdAttivita);
        record.put(IAttivita.sDESCRIZIONE, sDescrizione);
        record.putBoolean(IAttivita.sATTIVO, sAttivo);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSClassi.readAttivita(conn," + classe + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
}
