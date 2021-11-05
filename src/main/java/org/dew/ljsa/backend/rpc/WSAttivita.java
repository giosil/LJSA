package org.dew.ljsa.backend.rpc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.util.WMap;
import org.util.WUtil;

import org.dew.ljsa.Attivita;
import org.dew.ljsa.IAttivita;
import org.dew.ljsa.ICommon;
import org.dew.ljsa.ILJSAErrors;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.LJSAClient;

import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DataUtil;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class WSAttivita implements IAttivita
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public
  List<List<Object>> lookup(Map<String, Object> mapFilter, List<String> listServices)
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO", sID_SERVIZIO);
    qb.put("ID_ATTIVITA", sID_ATTIVITA);
    qb.put("DESCRIZIONE", sDESCRIZIONE);
    String addClause = null;
    if(listServices != null && listServices.size() > 0) {
      addClause = "ID_SERVIZIO IN (" + DataUtil.buildInSet(listServices) + ") AND ATTIVO='" + QueryBuilder.decodeBoolean(true) + "'";
    }
    else {
      addClause = "ATTIVO='" + QueryBuilder.decodeBoolean(true) + "'";
    }
    
    String sSQL = qb.select("LJSA_ATTIVITA", mapFilter, addClause);
    sSQL += " ORDER BY ID_SERVIZIO,ID_ATTIVITA";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sIdAttivita  = rs.getString("ID_ATTIVITA");
        String sDescrizione = rs.getString("DESCRIZIONE");
        
        List<Object> record = new ArrayList<Object>(3);
        record.add(sIdAttivita);
        record.add(sIdAttivita);
        record.add(sDescrizione);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.lookUp(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  List<List<Object>> lookup(String idServizio, String idAttivita)
    throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    if(idServizio == null || idServizio.length() == 0) {
      return listResult;
    }
    
    String sSQL = "SELECT ID_ATTIVITA,DESCRIZIONE ";
    sSQL += "FROM LJSA_ATTIVITA ";
    sSQL += "WHERE ID_SERVIZIO=? AND ATTIVO=? ";
    if(idAttivita != null && idAttivita.length() > 0) {
      sSQL += "AND ID_ATTIVITA LIKE ? ";
    }
    sSQL += "ORDER BY ID_ATTIVITA";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio.trim().toUpperCase());
      pstm.setString(2, QueryBuilder.decodeBoolean(true));
      if(idAttivita != null && idAttivita.length() > 0) {
        pstm.setString(3, "%" + idAttivita.trim().toUpperCase() + "%");
      }
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sIdAttivita  = rs.getString("ID_ATTIVITA");
        String sDescrizione = rs.getString("DESCRIZIONE");
        
        List<Object> record = new ArrayList<Object>(3);
        record.add(sIdAttivita);
        record.add(sIdAttivita);
        record.add(sDescrizione);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.lookUp(" + idServizio + ")", ex);
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
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO",        sID_SERVIZIO);
    qb.put("ID_ATTIVITA",        sID_ATTIVITA);
    qb.put("DESCRIZIONE",        sDESCRIZIONE);
    qb.put("CLASSE",             sCLASSE);
    qb.put("ATTIVO",             sATTIVO);
    qb.put("ID_CREDENZIALE_INS", sID_CREDENZIALE_INS);
    qb.put("DATA_INSERIMENTO",   sDATA_INS);
    qb.put("ORA_INSERIMENTO",    sORA_INS);
    qb.put("ID_CREDENZIALE_AGG", sID_CREDENZIALE_AGG);
    qb.put("DATA_AGGIORNAMENTO", sDATA_AGG);
    qb.put("ORA_AGGIORNAMENTO",  sORA_AGG);
    
    String sInClause = null;
    if(listServices != null && listServices.size() > 0) {
      sInClause = "ID_SERVIZIO IN (" + DataUtil.buildInSet(listServices) + ")";
    }
    
    String sSQL = qb.select("LJSA_ATTIVITA", mapFilter, sInClause);
    sSQL += " ORDER BY ID_SERVIZIO,ID_ATTIVITA";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm  = conn.createStatement();
      rs   = stm.executeQuery(sSQL);
      while(rs.next()) {
        String sIdServizio       = rs.getString("ID_SERVIZIO");
        String sIdAttivita       = rs.getString("ID_ATTIVITA");
        String sDescrizione      = rs.getString("DESCRIZIONE");
        String sClasse           = rs.getString("CLASSE");
        String sAttivo           = rs.getString("ATTIVO");
        String sIdCredenzialeIns = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento     = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento      = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento   = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento    = rs.getInt("ORA_AGGIORNAMENTO");
        
        WMap record = new WMap(true);
        record.put(sID_SERVIZIO,            sIdServizio);
        record.put(sID_ATTIVITA,            sIdAttivita);
        record.put(sDESCRIZIONE,            sDescrizione);
        record.put(sCLASSE,                 sClasse);
        record.putBoolean(sATTIVO,          sAttivo);
        record.put(sID_CREDENZIALE_INS,     sIdCredenzialeIns);
        record.putDate(sDATA_INS,   iDataInserimento);
        record.put(sORA_INS,        iOraInserimento);
        record.put(sID_CREDENZIALE_AGG,     sIdCredenzialeAgg);
        record.putDate(sDATA_AGG, iDataAggiornamento);
        record.put(sORA_AGG,      iOraAggiornamento);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.find(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  Map<String, Object> read(String idServizio, String idAttivita)
    throws Exception
  {
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      return read(conn, idServizio, idAttivita);
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.read(" + idServizio + "," + idAttivita + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
  }
  
  public
  int countSchedulazioni(String idServizio, String idAttivita)
    throws Exception
  {
    int result = 0;
    String sSQL = "SELECT COUNT(*) FROM LJSA_SCHEDULAZIONI WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? AND STATO<>?";
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      pstm.setString(3, ISchedulazione.sSTATO_DISATTIVATA);
      rs = pstm.executeQuery();
      if(rs.next()) result = rs.getInt(1);
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.countSchedulazioni" + idServizio + "," + idAttivita + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return result;
  }
  
  public
  boolean exists(Map<String, Object> mapFilter)
    throws Exception
  {
    boolean result = false;
    WMap wmFilter = new WMap(mapFilter);
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT ID_ATTIVITA FROM LJSA_ATTIVITA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?");
      pstm.setString(1, wmFilter.getString(sID_SERVIZIO));
      pstm.setString(2, wmFilter.getString(sID_ATTIVITA));
      rs = pstm.executeQuery();
      result = rs.next();
    }
    catch(Exception ex) {
      logger.error("Exception in WSAttivita.exists(" + mapFilter + ")", ex);
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
    if(mapValues == null || mapValues.isEmpty()) {
      throw new Exception("Invalid values");
    }
    
    String user = WUtil.toString(mapValues.get(ICommon.sUSER_LOG), "");
    mapValues.put(sID_CREDENZIALE_INS, user);
    mapValues.put(sID_CREDENZIALE_AGG, user);
    
    Attivita attivita = new Attivita(mapValues);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(attivita);
      
      if(!ljsaClient.addAttivita(attivita)) {
        throw new Exception(ILJSAErrors.sCUSTOM + "Attivit\340 esistente");
      }
      
      Date currentDate = new Date();
      mapValues.put(sDATA_INS,   currentDate);
      mapValues.put(sORA_INS,    WUtil.timeToInt(currentDate));
      mapValues.put(sDATA_AGG, currentDate);
      mapValues.put(sORA_AGG,  WUtil.timeToInt(currentDate));
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in insert(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return mapValues;
  }
  
  public
  Map<String, Object> update(Map<String, Object> mapValues)
    throws Exception
  {
    if(mapValues == null || mapValues.isEmpty()) {
      throw new Exception("Invalid values");
    }
    
    String user = WUtil.toString(mapValues.get(ICommon.sUSER_LOG), "");
    mapValues.put(IAttivita.sID_CREDENZIALE_AGG, user);
    
    Attivita attivita = new Attivita(mapValues);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(attivita);
      ljsaClient.updateAttivita(attivita);
      
      Date currentDate = new Date();
      mapValues.put(sDATA_AGG, currentDate);
      mapValues.put(sORA_AGG,  WUtil.timeToInt(currentDate));
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSAttivita.update(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return mapValues;
  }
  
  public
  boolean delete(String idServizio, String idAttivita, String user)
    throws Exception
  {
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      Attivita attivita = new Attivita(idServizio, idAttivita, user);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      ljsaClient.removeAttivita(attivita);
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSAttivita.delete(" + idServizio + "," + idAttivita + "," + user + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return true;
  }
  
  public
  boolean setEnabled(String idServizio, String idAttivita, boolean boEnabled, String user)
    throws Exception
  {
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      Attivita attivita = new Attivita(idServizio, idAttivita, user);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      if(boEnabled) {
        ljsaClient.enableAttivita(attivita);
      }
      else {
        ljsaClient.disableAttivita(attivita);
      }
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSAttivita.setEnabled(" + idServizio + "," + idAttivita + "," + boEnabled + "," + user + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return boEnabled;
  }
  
  protected
  Map<String, Object> read(Connection conn, String idServizio, String idAttivita)
    throws Exception
  {
    WMap result = new WMap();
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("S.DESCRIZIONE DESC_SERVIZIO");
    qb.add("A.DESCRIZIONE");
    qb.add("A.CLASSE");
    qb.add("A.ATTIVO");
    qb.add("A.ID_CREDENZIALE_INS");
    qb.add("A.DATA_INSERIMENTO");
    qb.add("A.ORA_INSERIMENTO");
    qb.add("A.ID_CREDENZIALE_AGG");
    qb.add("A.DATA_AGGIORNAMENTO");
    qb.add("A.ORA_AGGIORNAMENTO");
    
    String sSQL = qb.select("LJSA_ATTIVITA A,LJSA_SERVIZI S");
    sSQL += "WHERE A.ID_SERVIZIO=S.ID_SERVIZIO ";
    sSQL += "AND A.ID_SERVIZIO=? AND A.ID_ATTIVITA=?";
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sDescServizio     = rs.getString("DESC_SERVIZIO");
        String sDescrizione      = rs.getString("DESCRIZIONE");
        String sClasse           = rs.getString("CLASSE");
        String sAttivo           = rs.getString("ATTIVO");
        String sIdCredenzialeIns = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento     = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento      = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento   = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento    = rs.getInt("ORA_AGGIORNAMENTO");
        
        result.putList(sID_SERVIZIO,        idServizio, idServizio, sDescServizio);
        result.put(sID_ATTIVITA,            idAttivita);
        result.put(sDESCRIZIONE,            sDescrizione);
        result.put(sCLASSE,                 sClasse);
        result.putBoolean(sATTIVO,          sAttivo);
        result.put(sID_CREDENZIALE_INS,     sIdCredenzialeIns);
        result.putDate(sDATA_INS,   iDataInserimento);
        result.put(sORA_INS,        iOraInserimento);
        result.put(sID_CREDENZIALE_AGG,     sIdCredenzialeAgg);
        result.putDate(sDATA_AGG, iDataAggiornamento);
        result.put(sORA_AGG,      iOraAggiornamento);
        
        result.put(sCONFIGURAZIONE,  readConfigurazione(conn, idServizio, idAttivita));
        result.put(sPARAMETRI,       readParametri(conn, idServizio, idAttivita));
        result.put(sNOTIFICA,        readNotifica(conn, idServizio, idAttivita));
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return result.toMapObject();
  }
  
  protected static
  List<Map<String, Object>> readConfigurazione(Connection conn, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT OPZIONE,DESCRIZIONE,VALORI,PREDEFINITO FROM LJSA_ATTIVITA_CONF ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ORDER BY OPZIONE";
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sOpzione     = rs.getString("OPZIONE");
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sValori      = rs.getString("VALORI");
        String sPredefinito = rs.getString("PREDEFINITO");
        
        Map<String, Object> record = new HashMap<String, Object>();
        record.put(sCONF_OPZIONE,     sOpzione);
        record.put(sCONF_DESCRIZIONE, sDescrizione);
        record.put(sCONF_VALORI,      sValori);
        record.put(sCONF_PREDEFINITO, sPredefinito);
        
        listResult.add(record);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  List<Map<String, Object>> readParametri(Connection conn, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT PARAMETRO,DESCRIZIONE,VALORI,PREDEFINITO FROM LJSA_ATTIVITA_PARAMETRI ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ORDER BY PARAMETRO";
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sParametro   = rs.getString("PARAMETRO");
        String sDescrizione = rs.getString("DESCRIZIONE");
        String sValori      = rs.getString("VALORI");
        String sPredefinito = rs.getString("PREDEFINITO");
        
        Map<String, Object> record = new HashMap<String, Object>();
        record.put(sPAR_PARAMETRO,   sParametro);
        record.put(sPAR_DESCRIZIONE, sDescrizione);
        record.put(sPAR_VALORI,      sValori);
        record.put(sPAR_PREDEFINITO, sPredefinito);
        
        listResult.add(record);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  List<Map<String, Object>> readNotifica(Connection conn, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT EVENTO,DESTINAZIONE FROM LJSA_ATTIVITA_NOTIFICA ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ORDER BY EVENTO,DESTINAZIONE";
      
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String evento = rs.getString("EVENTO");
        String destin = rs.getString("DESTINAZIONE");
        if(evento == null || evento.length() == 0) continue;
        if(destin == null || destin.length() == 0) continue;
        
        Map<String, Object> record = new HashMap<String, Object>();
        record.put(ISchedulazione.sNOT_EVENTO,       evento);
        record.put(ISchedulazione.sNOT_DESTINAZIONE, destin);
        record.put(ISchedulazione.sATTIVO,           true);
        
        listResult.add(record);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
}
