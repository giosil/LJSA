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

import org.apache.log4j.Logger;

import org.util.WMap;
import org.util.WUtil;

import org.dew.ljsa.ICommon;
import org.dew.ljsa.ILog;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.LJSAClient;
import org.dew.ljsa.Schedulazione;

import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DataUtil;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class WSSchedulazioni implements ISchedulazione
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public
  List<Map<String, Object>> find(Map<String, Object> mapFilter, List<String> listServices)
      throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SCHEDULAZIONE",      sID_SCHEDULAZIONE);
    qb.put("ID_SERVIZIO",           sID_SERVIZIO);
    qb.put("ID_ATTIVITA",           sID_ATTIVITA);
    qb.put("DESCRIZIONE",           sDESCRIZIONE);
    qb.put("SCHEDULAZIONE",         sSCHEDULAZIONE);
    qb.put("ID_CREDENZIALE_INS",    sID_CREDENZIALE_INS);
    qb.put("DATA_INSERIMENTO",      sDATA_INSERIMENTO);
    qb.put("ORA_INSERIMENTO",       sORA_INSERIMENTO);
    qb.put("ID_CREDENZIALE_AGG",    sID_CREDENZIALE_AGG);
    qb.put("DATA_AGGIORNAMENTO",    sDATA_AGGIORNAMENTO);
    qb.put("ORA_AGGIORNAMENTO",     sORA_AGGIORNAMENTO);
    qb.put("STATO",                 sSTATO);
    qb.put("INIZIOVALIDITA",        sINIZIO_VALIDITA + "_"); // Si escludono dal filtro diretto per
    qb.put("FINEVALIDITA",          sFINE_VALIDITA   + "_"); // implementare il  filtro  intervallo
    qb.put("ESECUZIONI_COMPLETATE", sESECUZIONI_COMPLETATE);
    qb.put("ESECUZIONI_INTERROTTE", sESECUZIONI_INTERROTTE);
    
    WMap wmFilter = new WMap(mapFilter);
    int inizioVal = wmFilter.getIntDate(sINIZIO_VALIDITA, 0);
    int fineVal   = wmFilter.getIntDate(sFINE_VALIDITA,   99991231);
    String sAddClause = "FINEVALIDITA >= " + inizioVal + " AND INIZIOVALIDITA <= " + fineVal;
    if(listServices != null && listServices.size() > 0) {
      sAddClause += " AND ID_SERVIZIO IN (" + DataUtil.buildInSet(listServices) + ")";
    }
    
    String sSQL = qb.select("LJSA_SCHEDULAZIONI", mapFilter, sAddClause);
    sSQL += " ORDER BY ID_SCHEDULAZIONE DESC";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
      while(rs.next()) {
        int iIdSchedulazione      = rs.getInt("ID_SCHEDULAZIONE");
        String sIdServizio        = rs.getString("ID_SERVIZIO");
        String sIdAttivita        = rs.getString("ID_ATTIVITA");
        String sDescrizione       = rs.getString("DESCRIZIONE");
        String sSchedulazione     = rs.getString("SCHEDULAZIONE");
        String sIdCredenzialeIns  = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento      = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento       = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg  = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento    = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento     = rs.getInt("ORA_AGGIORNAMENTO");
        String sStato             = rs.getString("STATO");
        int iInizioValidita       = rs.getInt("INIZIOVALIDITA");
        int iFineValidita         = rs.getInt("FINEVALIDITA");
        int iEsecuzioniCompletate = rs.getInt("ESECUZIONI_COMPLETATE");
        int iEsecuzioniInterrotte = rs.getInt("ESECUZIONI_INTERROTTE");
        
        WMap record = new WMap(true);
        record.put(sID_SCHEDULAZIONE,       iIdSchedulazione);
        record.put(sID_SERVIZIO,            sIdServizio);
        record.put(sID_ATTIVITA,            sIdAttivita);
        record.put(sDESCRIZIONE,            sDescrizione);
        record.put(sSCHEDULAZIONE,          sSchedulazione);
        record.put(sID_CREDENZIALE_INS,     sIdCredenzialeIns);
        record.putDate(sDATA_INSERIMENTO,   iDataInserimento);
        record.put(sORA_INSERIMENTO,        iOraInserimento);
        record.put(sID_CREDENZIALE_AGG,     sIdCredenzialeAgg);
        record.putDate(sDATA_AGGIORNAMENTO, iDataAggiornamento);
        record.put(sORA_AGGIORNAMENTO,      iOraAggiornamento);
        record.put(sSTATO,                  sStato);
        record.putDate(sINIZIO_VALIDITA,    iInizioValidita);
        record.putDate(sFINE_VALIDITA,      iFineValidita);
        record.put(sESECUZIONI_COMPLETATE,  iEsecuzioniCompletate);
        record.put(sESECUZIONI_INTERROTTE,  iEsecuzioniInterrotte);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.find(" + mapFilter + "," + listServices + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public
  Map<String, Object> read(int idSchedulazione)
      throws Exception
  {
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      return read(conn, idSchedulazione);
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.read(" + idSchedulazione + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
  }
  
  public
  String readStatus(int idSchedulazione)
      throws Exception
  {
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      return readStatus(conn, idSchedulazione);
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readStatus(" + idSchedulazione + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
  }
  
  public
  List<Object> readExecInfo(int idSchedulazione)
      throws Exception
  {
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      return readExecInfo(conn, idSchedulazione);
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readExecInfo(" + idSchedulazione + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
  }
  
  public
  Map<String, Object> readInfoAttivita(String idServizio, String idAttivita)
      throws Exception
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      mapResult.put(sCONFIGURAZIONE, readConfigurazione(conn, 0, idServizio, idAttivita));
      mapResult.put(sPARAMETRI,      readParametri(conn, 0, idServizio, idAttivita));
      mapResult.put(sNOTIFICA,       readNotifica(conn, 0, idServizio, idAttivita));
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readInfoAttivita(" + idServizio + "," + idAttivita + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return mapResult;
  }
  
  public
  List<List<Object>> readLogCalendar(String idServizio, String idAttivita)
      throws Exception
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    String sSQL = "SELECT L.DATA_INIZIO,MIN(L.STATO) MIN_STATO,MAX(L.STATO) MAX_STATO ";
    sSQL += "FROM LJSA_LOG L,LJSA_ATTIVITA A,LJSA_SCHEDULAZIONI S ";
    sSQL += "WHERE L.ID_SCHEDULAZIONE=S.ID_SCHEDULAZIONE ";
    sSQL += "AND S.ID_SERVIZIO=A.ID_SERVIZIO AND S.ID_ATTIVITA=A.ID_ATTIVITA ";
    sSQL += "AND A.ID_SERVIZIO=? AND A.ID_ATTIVITA=? ";
    sSQL += "GROUP BY L.DATA_INIZIO";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      rs = pstm.executeQuery();
      while(rs.next()) {
        int    dataInizio = rs.getInt("DATA_INIZIO");
        String minStato   = rs.getString("MIN_STATO");
        String maxStato   = rs.getString("MAX_STATO");
        
        boolean boTutte_C     = minStato.equals("C") && maxStato.equals("C");
        boolean boTutte_E     = minStato.equals("E") && maxStato.equals("E");
        boolean boAlmenoUna_S = minStato.equals("S") || maxStato.equals("S");
        boolean boAlmenoUna_C = minStato.equals("C") || maxStato.equals("C");
        
        int iStato = 0;
        if(boTutte_C) {
          iStato = 1;
        }
        else if(boTutte_E) {
          iStato = 2;
        }
        else if(boAlmenoUna_S) {
          iStato = 4;
        }
        else if(boAlmenoUna_C) {
          iStato = 3;
        }
        
        List<Object> vRecord = new ArrayList<Object>(2);
        vRecord.add(WUtil.toDate(dataInizio, 0));
        vRecord.add(iStato);
        
        listResult.add(vRecord);
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readLogCalendar(" + idServizio + "," + idAttivita + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  List<Map<String, Object>> readLog(String idServizio, String idAttivita, List<?> listDates)
      throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    if(listDates == null || listDates.size() == 0) {
      return listResult;
    }
    
    String sSQL = "SELECT ";
    sSQL += "L.ID_LOG,";
    sSQL += "L.ID_SCHEDULAZIONE,";
    sSQL += "L.DATA_INIZIO,";
    sSQL += "L.ORA_INIZIO,";
    sSQL += "L.DATA_FINE,";
    sSQL += "L.ORA_FINE,";
    sSQL += "L.RAPPORTO,";
    sSQL += "L.STATO ";
    sSQL += "FROM LJSA_LOG L,LJSA_ATTIVITA A,LJSA_SCHEDULAZIONI S ";
    sSQL += "WHERE L.ID_SCHEDULAZIONE=S.ID_SCHEDULAZIONE ";
    sSQL += "AND S.ID_SERVIZIO=A.ID_SERVIZIO AND S.ID_ATTIVITA=A.ID_ATTIVITA ";
    sSQL += "AND A.ID_SERVIZIO=? AND A.ID_ATTIVITA=? ";
    sSQL += "AND L.DATA_INIZIO IN (?";
    for(int i = 1; i < listResult.size(); i++) sSQL += ",?";
    sSQL += ") ";
    sSQL += "ORDER BY ID_LOG";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, idServizio);
      pstm.setString(2, idAttivita);
      for(int i = 0; i < listDates.size(); i++) {
        pstm.setInt(3 + i, WUtil.toIntDate(listDates.get(i), 0));
      }
      rs = pstm.executeQuery();
      while(rs.next()) {
        int iIdSchedulazione = rs.getInt("ID_SCHEDULAZIONE");
        int iIdLog           = rs.getInt("ID_LOG");
        int iDataInizio      = rs.getInt("DATA_INIZIO");
        int iOraInizio       = rs.getInt("ORA_INIZIO");
        int iDataFine        = rs.getInt("DATA_FINE");
        int iOraFine         = rs.getInt("ORA_FINE");
        String sRapporto     = rs.getString("RAPPORTO");
        String sStato        = rs.getString("STATO");
        
        WMap record = new WMap();
        record.put(ILog.sID_SCHEDULAZIONE, iIdSchedulazione);
        record.put(ILog.sID_LOG,           iIdLog);
        record.putDate(ILog.sDATA_INIZIO,  iDataInizio);
        record.put(ILog.sORA_INIZIO,       iOraInizio);
        record.putDate(ILog.sDATA_FINE,    iDataFine);
        record.put(ILog.sORA_FINE,         iOraFine);
        record.put(ILog.sRAPPORTO,         sRapporto);
        record.put(ILog.sSTATO,            sStato);
        
        listResult.add(record.toMapObject());
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readLog(" + idServizio + "," + idAttivita + "," + listDates + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  List<Map<String, Object>> readLog(int idSchedulazione, int maxRecords)
      throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    if(maxRecords < 1) return listResult;
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("ID_LOG");
    qb.add("DATA_INIZIO");
    qb.add("ORA_INIZIO");
    qb.add("DATA_FINE");
    qb.add("ORA_FINE");
    qb.add("RAPPORTO");
    qb.add("STATO");
    String sSQL = qb.select("LJSA_LOG");
    sSQL += "WHERE ID_SCHEDULAZIONE=? ";
    sSQL += "ORDER BY ID_LOG DESC";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, idSchedulazione);
      rs = pstm.executeQuery();
      while(rs.next()) {
        int iIdLog       = rs.getInt("ID_LOG");
        int iDataInizio  = rs.getInt("DATA_INIZIO");
        int iOraInizio   = rs.getInt("ORA_INIZIO");
        int iDataFine    = rs.getInt("DATA_FINE");
        int iOraFine     = rs.getInt("ORA_FINE");
        String sRapporto = rs.getString("RAPPORTO");
        String sStato    = rs.getString("STATO");
        
        WMap record = new WMap();
        record.put(ILog.sID_SCHEDULAZIONE, idSchedulazione);
        record.put(ILog.sID_LOG,           iIdLog);
        record.putDate(ILog.sDATA_INIZIO,  iDataInizio);
        record.put(ILog.sORA_INIZIO,       iOraInizio);
        record.putDate(ILog.sDATA_FINE,    iDataFine);
        record.put(ILog.sORA_FINE,         iOraFine);
        record.put(ILog.sRAPPORTO,         sRapporto);
        record.put(ILog.sSTATO,            sStato);
        
        listResult.add(record.toMapObject());
        
        if(listResult.size() >= maxRecords) break;
      }
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.readLog(" + idSchedulazione + "," + maxRecords + ")", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  List<Map<String, Object>> readLogFiles(int idLog)
      throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("TIPOLOGIA");
    qb.add("NOME_FILE");
    qb.add("URL_FILE");
    String sSQL = qb.select("LJSA_LOG_FILES");
    sSQL += "WHERE ID_LOG=?";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, idLog);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sTipologia = rs.getString("TIPOLOGIA");
        String sNomeFile  = rs.getString("NOME_FILE");
        String sURLFile   = rs.getString("URL_FILE");
        
        Map<String, Object> record = new HashMap<String, Object>();
        record.put(ILog.sFILES_TIPOLOGIA, sTipologia);
        record.put(ILog.sFILES_NOME_FILE, sNomeFile);
        record.put(ILog.sFILES_URL_FILE,  sURLFile);
        
        listResult.add(record);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  public
  Map<String, Object> insert(Map<String, Object> mapValues)
      throws Exception
  {
    if(mapValues == null || mapValues.isEmpty()) {
      throw new Exception("Invalid values");
    }
    
    String user = WUtil.toString(mapValues.get(ICommon.sUSER_LOG), null);
    if(user != null && user.length() > 0) {
      mapValues.put(sID_CREDENZIALE_INS, user);
      mapValues.put(sID_CREDENZIALE_AGG, user);
    }
    
    Schedulazione schedulazione = new Schedulazione(mapValues);
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(schedulazione.getIdServizio());
      
      int idSchedulazione = ljsaClient.addSchedulazione(schedulazione);
      
      Date currentDate = new Date();
      
      mapValues.put(sID_SCHEDULAZIONE,      idSchedulazione);
      mapValues.put(sESECUZIONI_COMPLETATE, 0);
      mapValues.put(sESECUZIONI_INTERROTTE, 0);
      mapValues.put(sDATA_INSERIMENTO,      currentDate);
      mapValues.put(sORA_INSERIMENTO,       WUtil.timeToInt(currentDate));
      mapValues.put(sDATA_AGGIORNAMENTO,    currentDate);
      mapValues.put(sORA_AGGIORNAMENTO,     WUtil.timeToInt(currentDate));
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.insert(" + mapValues + ")", ex);
      throw ex;
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
    
    String user = WUtil.toString(mapValues.get(ICommon.sUSER_LOG), null);
    if(user != null && user.length() > 0) {
      mapValues.put(sID_CREDENZIALE_AGG, user);
    }
    Schedulazione schedulazione = new Schedulazione(mapValues);
    
    Map<String, Object> mapResult = null;
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(schedulazione.getIdServizio());
      
      ljsaClient.updateSchedulazione(schedulazione);
      
      mapResult = read(conn, schedulazione.getIdSchedulazione());
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.update(" + mapValues + ")", ex);
      throw ex;
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return mapResult;
  }
  
  public
  boolean setEnabled(String idServizio, int idSchedulazione, boolean boEnabled, String user)
    throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      if(boEnabled) {
        ljsaClient.enableSchedulazione(idSchedulazione, user);
      }
      else {
        ljsaClient.disableSchedulazione(idSchedulazione, user);
      }
    }
    catch(Exception ex) {
      logger.debug("WSSchedulazioni.setEnabled(" + idServizio + "," + idSchedulazione + "," + boEnabled + "," + user + ")", ex);
      throw ex;
    }
    return boEnabled;
  }
  
  public
  boolean delete(String idServizio, int idSchedulazione, String user)
      throws Exception
  {
    try {
      LJSAClient ljsaClient = LJSAFactory.createLJSAClient(idServizio);
      
      return ljsaClient.removeSchedulazione(idSchedulazione);
    }
    catch(Exception ex) {
      logger.error("Exception in WSSchedulazioni.delete(" + idServizio + "," + idSchedulazione + "," + user + ")", ex);
      throw ex;
    }
  }
  
  protected static
  Map<String, Object> read(Connection conn, int idSchedulazione)
      throws Exception
  {
    WMap record = new WMap();
    
    QueryBuilder qb = new QueryBuilder();
    qb.add("L.ID_SERVIZIO");
    qb.add("S.DESCRIZIONE DESC_SERVIZIO");
    qb.add("L.ID_ATTIVITA");
    qb.add("A.DESCRIZIONE DESC_ATTIVITA");
    qb.add("L.DESCRIZIONE");
    qb.add("L.SCHEDULAZIONE");
    qb.add("L.ID_CREDENZIALE_INS");
    qb.add("L.DATA_INSERIMENTO");
    qb.add("L.ORA_INSERIMENTO");
    qb.add("L.ID_CREDENZIALE_AGG");
    qb.add("L.DATA_AGGIORNAMENTO");
    qb.add("L.ORA_AGGIORNAMENTO");
    qb.add("L.STATO");
    qb.add("L.INIZIOVALIDITA");
    qb.add("L.FINEVALIDITA");
    qb.add("L.ESECUZIONI_COMPLETATE");
    qb.add("L.ESECUZIONI_INTERROTTE");
    String sSQL = qb.select("LJSA_SCHEDULAZIONI L,LJSA_ATTIVITA A,LJSA_SERVIZI S");
    sSQL += "WHERE L.ID_SERVIZIO=S.ID_SERVIZIO AND L.ID_SERVIZIO=A.ID_SERVIZIO AND L.ID_ATTIVITA=A.ID_ATTIVITA ";
    sSQL += "AND L.ID_SCHEDULAZIONE=?";
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, idSchedulazione);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sIdServizio        = rs.getString("ID_SERVIZIO");
        String sDescServizio      = rs.getString("DESC_SERVIZIO");
        String sIdAttivita        = rs.getString("ID_ATTIVITA");
        String sDescAttivita      = rs.getString("DESC_ATTIVITA");
        String sDescrizione       = rs.getString("DESCRIZIONE");
        String sSchedulazione     = rs.getString("SCHEDULAZIONE");
        String sIdCredenzialeIns  = rs.getString("ID_CREDENZIALE_INS");
        int iDataInserimento      = rs.getInt("DATA_INSERIMENTO");
        int iOraInserimento       = rs.getInt("ORA_INSERIMENTO");
        String sIdCredenzialeAgg  = rs.getString("ID_CREDENZIALE_AGG");
        int iDataAggiornamento    = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento     = rs.getInt("ORA_AGGIORNAMENTO");
        String sStato             = rs.getString("STATO");
        int iInizioValidita       = rs.getInt("INIZIOVALIDITA");
        int iFineValidita         = rs.getInt("FINEVALIDITA");
        int iEsecuzioniCompletate = rs.getInt("ESECUZIONI_COMPLETATE");
        int iEsecuzioniInterrotte = rs.getInt("ESECUZIONI_INTERROTTE");
        
        record.put(sID_SCHEDULAZIONE,       idSchedulazione);
        record.putList(sID_SERVIZIO,        sIdServizio, sIdServizio, sDescServizio);
        record.putList(sID_ATTIVITA,        sIdAttivita, sIdAttivita, sDescAttivita);
        record.put(sDESCRIZIONE,            sDescrizione);
        record.put(sSCHEDULAZIONE,          sSchedulazione);
        record.put(sID_CREDENZIALE_INS,     sIdCredenzialeIns);
        record.putDate(sDATA_INSERIMENTO,   iDataInserimento);
        record.put(sORA_INSERIMENTO,        iOraInserimento);
        record.put(sID_CREDENZIALE_AGG,     sIdCredenzialeAgg);
        record.putDate(sDATA_AGGIORNAMENTO, iDataAggiornamento);
        record.put(sORA_AGGIORNAMENTO,      iOraAggiornamento);
        record.put(sSTATO,                  sStato);
        record.putDate(sINIZIO_VALIDITA,    iInizioValidita);
        record.putDate(sFINE_VALIDITA,      iFineValidita);
        record.put(sESECUZIONI_COMPLETATE,  iEsecuzioniCompletate);
        record.put(sESECUZIONI_INTERROTTE,  iEsecuzioniInterrotte);
        
        record.put(sPARAMETRI,      readParametri(conn,      idSchedulazione, sIdServizio, sIdAttivita));
        record.put(sCONFIGURAZIONE, readConfigurazione(conn, idSchedulazione, sIdServizio, sIdAttivita));
        record.put(sNOTIFICA,       readNotifica(conn,       idSchedulazione, sIdServizio, sIdAttivita));
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return record.toMapObject();
  }
  
  protected static
  String readStatus(Connection conn, int iIdSchedulazione)
      throws Exception
  {
    String result = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT STATO FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=?");
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      if(rs.next()) result = rs.getString("STATO");
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return result;
  }
  
  protected static
  List<Object> readExecInfo(Connection conn, int idSchedulazione)
      throws Exception
  {
    List<Object> listResult = new ArrayList<Object>();
    String sSQL = "SELECT STATO,ESECUZIONI_COMPLETATE,ESECUZIONI_INTERROTTE ";
    sSQL += "FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=?";
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, idSchedulazione);
      rs = pstm.executeQuery();
      if(rs.next()) {
        String stato = rs.getString("STATO");
        if(stato == null || stato.length() == 0) {
          stato = sSTATO_DISATTIVATA;
        }
        int iEseCompletate = rs.getInt("ESECUZIONI_COMPLETATE");
        int iEseInterrotte = rs.getInt("ESECUZIONI_INTERROTTE");
        
        listResult.add(stato);
        listResult.add(iEseCompletate);
        listResult.add(iEseInterrotte);
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  List<Map<String, Object>> readParametri(Connection conn, int idSchedulazione, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    Map<String, Map<String, Object>> mapParamsAttivita = new HashMap<String, Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT PARAMETRO,DESCRIZIONE,VALORI,PREDEFINITO ";
      sSQL += "FROM LJSA_ATTIVITA_PARAMETRI ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ";
      sSQL += "ORDER BY PARAMETRO";
      
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
        record.put(sPARAMETRI_PARAMETRO,   sParametro);
        record.put(sPARAMETRI_DESCRIZIONE, sDescrizione);
        record.put(sPARAMETRI_VALORI,      sValori);
        record.put(sPARAMETRI_PREDEFINITO, sPredefinito);
        record.put(sPARAMETRI_VALORE,      sPredefinito);
        record.put(sPARAMETRI_DA_ATTIVITA, true);
        record.put(sPARAMETRI_OVERWRITE,   false);
        
        mapParamsAttivita.put(sParametro, record);
        
        listResult.add(record);
      }
      
      if(idSchedulazione != 0) {
        ConnectionManager.close(rs, pstm);
        
        pstm = conn.prepareStatement("SELECT PARAMETRO,VALORE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=?");
        pstm.setInt(1, idSchedulazione);
        rs = pstm.executeQuery();
        while(rs.next()) {
          String sParametro = rs.getString("PARAMETRO");
          String sValore    = rs.getString("VALORE");
          
          Map<String, Object> record = mapParamsAttivita.get(sParametro);
          if(record != null) {
            record.put(sPARAMETRI_VALORE,      sValore != null ? sValore : "");
            record.put(sPARAMETRI_OVERWRITE,   true);
          }
          else {
            record = new HashMap<String, Object>();
            record.put(sPARAMETRI_PARAMETRO,   sParametro);
            record.put(sPARAMETRI_VALORE,      sValore != null ? sValore : "");
            record.put(sPARAMETRI_DA_ATTIVITA, false);
            record.put(sPARAMETRI_OVERWRITE,   false);
            
            listResult.add(record);
          }
        }
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  List<Map<String, Object>> readConfigurazione(Connection conn, int idSchedulazione, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    Map<String, Map<String, Object>> mapConfigAttivita = new HashMap<String, Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL =  "SELECT OPZIONE,DESCRIZIONE,VALORI,PREDEFINITO ";
      sSQL += "FROM LJSA_ATTIVITA_CONF ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ";
      sSQL += "ORDER BY OPZIONE";
      
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
        record.put(sCONFIGURAZIONE_OPZIONE,     sOpzione);
        record.put(sCONFIGURAZIONE_DESCRIZIONE, sDescrizione);
        record.put(sCONFIGURAZIONE_VALORI,      sValori);
        record.put(sCONFIGURAZIONE_PREDEFINITO, sPredefinito);
        record.put(sCONFIGURAZIONE_VALORE,      sPredefinito);
        record.put(sCONFIGURAZIONE_DA_ATTIVITA, true);
        record.put(sCONFIGURAZIONE_OVERWRITE,   false);
        
        mapConfigAttivita.put(sOpzione, record);
        
        listResult.add(record);
      }
      
      if(idSchedulazione != 0) {
        ConnectionManager.close(rs, pstm);
        
        pstm = conn.prepareStatement("SELECT OPZIONE,VALORE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=?");
        pstm.setInt(1, idSchedulazione);
        rs = pstm.executeQuery();
        while(rs.next()) {
          String sOpzione   = rs.getString("OPZIONE");
          String sValore    = rs.getString("VALORE");
          
          Map<String, Object> record = mapConfigAttivita.get(sOpzione);
          if(record != null) {
            record.put(sCONFIGURAZIONE_VALORE,      sValore != null ? sValore : "");
            record.put(sCONFIGURAZIONE_OVERWRITE,   true);
          }
          else {
            record = new HashMap<String, Object>();
            record.put(sCONFIGURAZIONE_OPZIONE,     sOpzione);
            record.put(sCONFIGURAZIONE_VALORE,      sValore != null ? sValore : "");
            record.put(sCONFIGURAZIONE_DA_ATTIVITA, false);
            record.put(sCONFIGURAZIONE_OVERWRITE,   false);
            
            listResult.add(record);
          }
        }
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
  
  protected static
  List<Map<String, Object>> readNotifica(Connection conn, int idSchedulazione, String idServizio, String idAttivita)
    throws Exception
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    Map<String, Map<String, Object>> mapNotifAttivita = new HashMap<String, Map<String, Object>>();
    
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      String sSQL = "SELECT EVENTO, DESTINAZIONE ";
      sSQL += "FROM LJSA_ATTIVITA_NOTIFICA ";
      sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? ";
      sSQL += "ORDER BY EVENTO,DESTINAZIONE ";
      
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
        record.put(sNOTIFICA_EVENTO,       evento);
        record.put(sNOTIFICA_DESTINAZIONE, destin);
        record.put(sNOTIFICA_DA_ATTIVITA,  true);
        record.put(sNOTIFICA_CANCELLATA,   false);
        
        listResult.add(record);
        
        mapNotifAttivita.put(evento + ":" + destin, record);
      }
      
      if(idSchedulazione != 0) {
        ConnectionManager.close(rs, pstm);
        
        pstm = conn.prepareStatement("SELECT EVENTO,DESTINAZIONE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=?");
        pstm.setInt(1, idSchedulazione);
        rs = pstm.executeQuery();
        while(rs.next()) {
          String evento = rs.getString("EVENTO");
          String destin = rs.getString("DESTINAZIONE");
          if(evento == null || evento.length() == 0) continue;
          if(destin == null || destin.length() == 0) continue;
          
          Map<String, Object> record = null;
          if(destin.startsWith("-")) {
            record = mapNotifAttivita.get(evento + ":" + destin.substring(1));
            if(record != null) {
              record.put(sNOTIFICA_CANCELLATA, true);
              continue;
            }
          }
          
          record = mapNotifAttivita.get(evento + ":" + destin);
          if(record != null) {
            record.put(sNOTIFICA_DA_ATTIVITA, Boolean.FALSE);
            continue;
          }
          
          record = new HashMap<String, Object>();
          record.put(sNOTIFICA_EVENTO,       evento);
          record.put(sNOTIFICA_DESTINAZIONE, destin);
          record.put(sNOTIFICA_DA_ATTIVITA,  false);
          record.put(sNOTIFICA_CANCELLATA,   false);
          
          listResult.add(record);
        }
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return listResult;
  }
}
