package org.dew.ljsa.backend.ws;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;

import org.quartz.Job;

import org.util.WMap;
import org.util.WUtil;

import org.dew.ljsa.Attivita;
import org.dew.ljsa.IAttivita;
import org.dew.ljsa.ILJSAErrors;
import org.dew.ljsa.ISchedulatore;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.LJSAClassLoader;
import org.dew.ljsa.LJSAMap;
import org.dew.ljsa.LJSARemoteJobExecutor;
import org.dew.ljsa.Schedulazione;

import org.dew.ljsa.backend.sched.LJSAScheduler;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.DB;
import org.dew.ljsa.backend.util.QueryBuilder;

/**
 * LJSA Services.
 */
public 
class WSLJSA 
{
  protected transient Logger logger = Logger.getLogger(getClass());
  
  public 
  boolean check(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    return true;
  }
  
  public 
  String getVersion(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    return LJSAScheduler.getVersion();
  }
  
  public 
  String getEmail(String idServizio, String idCredenziale, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    String result = null;
    Connection conn = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      result = getEmail(conn, idServizio, idCredenziale);
    } 
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.getEmail(" + idServizio + "," + idCredenziale + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    return result;
  }
  
  public 
  Map<String, Object> getConfiguration(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    if(BEConfig.config == null) {
      return new HashMap<String, Object>();
    }
    Map<String, Object> mapResult = new HashMap<String, Object>();
    Iterator<Map.Entry<Object, Object>> iterator = BEConfig.config.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<Object, Object> entry = iterator.next();
      String key = WUtil.toString(entry.getKey(),   "");
      String val = WUtil.toString(entry.getValue(), "");
      if(key.indexOf("password") >= 0) val = "********";
      mapResult.put(key, val);
    }
    return mapResult;
  }
  
  public 
  boolean pingDataSource(String user, String pass, String dataSource) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    boolean result = false;
    Connection conn = null;
    try {
      conn = ConnectionManager.getConnection(dataSource);
      result = !conn.isClosed();
    } 
    catch(Throwable th) {
      logger.error("Exception in WSLJSA.pingDataSource(" + user + ",*," + dataSource + ")", th);
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return result;
  }
  
  public 
  Map<String, Object> readInfo(String user, String pass) 
    throws Exception 
  {
    WMap result = new WMap();
    
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    String sSQL = "SELECT DATA_SCHEDULAZIONE,ORA_SCHEDULAZIONE,DATA_AGGIORNAMENTO,ORA_AGGIORNAMENTO ";
    sSQL += "FROM LJSA_LOG_SCHEDULATORE ";
    sSQL += "WHERE ID_SCHEDULATORE=? ";
    sSQL += "ORDER BY DATA_SCHEDULAZIONE DESC,ORA_SCHEDULAZIONE DESC";
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, LJSAScheduler.getIdSchedulatore());
      rs = pstm.executeQuery();
      if(rs.next()) {
        int iDataSchedulazione = rs.getInt("DATA_SCHEDULAZIONE");
        int iOraSchedulazione  = rs.getInt("ORA_SCHEDULAZIONE");
        int iDataAggiornamento = rs.getInt("DATA_AGGIORNAMENTO");
        int iOraAggiornamento  = rs.getInt("ORA_AGGIORNAMENTO");
        
        result.putDate(ISchedulatore.sINFO_DATA_SCHEDULAZIONE, iDataSchedulazione);
        result.put(ISchedulatore.sINFO_ORA_SCHEDULAZIONE,      iOraSchedulazione);
        result.putDate(ISchedulatore.sINFO_DATA_AGGIORNAMENTO, iDataAggiornamento);
        result.put(ISchedulatore.sINFO_ORA_AGGIORNAMENTO,      iOraAggiornamento);
      }
      
      List<Schedulazione> listSchedulazioni = LJSAScheduler.getSchedulazioni();
      if(listSchedulazioni != null) {
        result.put(ISchedulatore.sINFO_NUMERO_SCHEDULAZIONI, listSchedulazioni.size());
      } 
      else {
        result.put(ISchedulatore.sINFO_NUMERO_SCHEDULAZIONI, 0);
      }
      result.put(ISchedulatore.sINFO_ID_SCHEDULATORE,   LJSAScheduler.getIdSchedulatore());
      result.put(ISchedulatore.sINFO_VERSION,           LJSAScheduler.getVersion());
      result.put(ISchedulatore.sINFO_STATO,             LJSAScheduler.getStatus());
      result.put(ISchedulatore.sINFO_DESCRIZIONE_STATO, LJSAScheduler.getStatusDescription());
      result.put(ISchedulatore.sINFO_URL_DOWNLOAD,      BEConfig.getLJSADownload());
    } 
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.readInfo(" + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return result.toMapObject();
  }
  
  public 
  List<Map<String, Object>> findAttivita(Map<String, Object> mapFilter, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    QueryBuilder qb = new QueryBuilder();
    qb.put("ID_SERVIZIO",        IAttivita.sID_SERVIZIO);
    qb.put("ID_ATTIVITA",        IAttivita.sID_ATTIVITA);
    qb.put("DESCRIZIONE",        IAttivita.sDESCRIZIONE);
    qb.put("CLASSE",             IAttivita.sCLASSE);
    qb.put("ATTIVO",             IAttivita.sATTIVO);
    qb.put("ID_CREDENZIALE_INS", IAttivita.sID_CREDENZIALE_INS);
    qb.put("DATA_INSERIMENTO",   IAttivita.sDATA_INS);
    qb.put("ORA_INSERIMENTO",    IAttivita.sORA_INS);
    qb.put("ID_CREDENZIALE_AGG", IAttivita.sID_CREDENZIALE_AGG);
    qb.put("DATA_AGGIORNAMENTO", IAttivita.sDATA_AGG);
    qb.put("ORA_AGGIORNAMENTO",  IAttivita.sORA_AGG);
    String sSQL = qb.select("LJSA_ATTIVITA", mapFilter);
    sSQL += " ORDER BY ID_SERVIZIO,ID_ATTIVITA";
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      stm = conn.createStatement();
      rs = stm.executeQuery(sSQL);
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
        
        WMap record = new WMap();
        record.put(IAttivita.sID_SERVIZIO,            sIdServizio);
        record.put(IAttivita.sID_ATTIVITA,            sIdAttivita);
        record.put(IAttivita.sDESCRIZIONE,            sDescrizione);
        record.put(IAttivita.sCLASSE,                 sClasse);
        record.putBoolean(IAttivita.sATTIVO,          sAttivo);
        record.put(IAttivita.sID_CREDENZIALE_INS,     sIdCredenzialeIns);
        record.putDate(IAttivita.sDATA_INS,   iDataInserimento);
        record.put(IAttivita.sORA_INS,        iOraInserimento);
        record.put(IAttivita.sID_CREDENZIALE_AGG,     sIdCredenzialeAgg);
        record.putDate(IAttivita.sDATA_AGG, iDataAggiornamento);
        record.put(IAttivita.sORA_AGG,      iOraAggiornamento);
        
        listResult.add(record.toMapObject());
      }
    } 
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.findAttivita(" + mapFilter + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    return listResult;
  }
  
  public 
  boolean addAttivita(Map<String, Object> mapAttivita, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Attivita attivita = new Attivita(mapAttivita);
    String   idServizio = attivita.getIdServizio();
    String   idCredenziale = attivita.getIdCredenzialeIns();
    
    LJSAScheduler.checkService(idServizio);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(existAttivita(conn, attivita)) {
        return false;
      }
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      checkClasseAttivita(attivita);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      insertAttivita(conn, attivita);
      insertDettaglioAttivita(conn, attivita);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.addAttivita", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public 
  boolean removeAttivita(Map<String, Object> mapAttivita, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    if(mapAttivita == null || mapAttivita.isEmpty()) return false;
    
    boolean boResult = true;
    
    Attivita attivita  = new Attivita(mapAttivita, "*");
    String   idServizio = attivita.getIdServizio();
    String   idCredenziale = attivita.getIdCredenzialeIns();
    
    LJSAScheduler.checkService(idServizio);
    
    if(LJSAScheduler.checkRunningJobs(idServizio, idCredenziale)) {
      throw new Exception(ILJSAErrors.sTHERE_ARE_RUNNING_JOBS);
    }
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      int count = countSchedulazioniNonDisattive(conn, attivita);
      if(count > 0) {
        throw new Exception(ILJSAErrors.sACTIVITY_UNREMOVABLE);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      deleteSchedulazioni(conn, attivita);
      deleteDettaglioAttivita(conn, attivita);
      deleteAttivita(conn, attivita);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.removeAttivita(" + mapAttivita + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return boResult;
  }
  
  public 
  boolean updateAttivita(Map<String, Object> mapAttivita, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Attivita attivita = new Attivita(mapAttivita);
    String idServizio = attivita.getIdServizio();
    String idAttivita = attivita.getIdAttivita();
    String idCredenziale = attivita.getIdCredenzialeAgg();
    
    LJSAScheduler.checkService(idServizio);
    
    if(LJSAScheduler.checkRunningJobs(idServizio, idAttivita)) {
      throw new Exception(ILJSAErrors.sTHERE_ARE_RUNNING_JOBS);
    }
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(!existAttivita(conn, attivita)) {
        return false;
      }
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      checkClasseAttivita(attivita);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      updateAttivita(conn, attivita);
      deleteDettaglioAttivita(conn, attivita);
      insertDettaglioAttivita(conn, attivita);
      updateSchedulazioni(conn, attivita);
      
      LJSAScheduler.loadSchedulazioni(conn);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.updateAttivita(" + mapAttivita + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public 
  boolean enableAttivita(Map<String, Object> mapAttivita, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Attivita attivita = new Attivita(mapAttivita);
    String idServizio = attivita.getIdServizio();
    String idCredenziale = attivita.getIdCredenzialeAgg();
    
    LJSAScheduler.checkService(idServizio);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(!existAttivita(conn, attivita)) {
        return false;
      }
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      setEnabledAttivita(conn, attivita, true);
      updateSchedulazioni(conn, attivita);
      
      LJSAScheduler.loadSchedulazioni(conn);
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.enableAttivita(" + mapAttivita + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public 
  boolean disableAttivita(Map<String, Object> mapAttivita, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Attivita attivita = new Attivita(mapAttivita);
    String idServizio = attivita.getIdServizio();
    String idAttivita = attivita.getIdAttivita();
    String idCredenziale = attivita.getIdCredenzialeAgg();
    
    LJSAScheduler.checkService(idServizio);
    
    if(LJSAScheduler.checkRunningJobs(idServizio, idAttivita)) {
      throw new Exception(ILJSAErrors.sTHERE_ARE_RUNNING_JOBS);
    }
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(!existAttivita(conn, attivita)) {
        return false;
      }
      if(!existCredenziale(conn, idAttivita, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      setEnabledAttivita(conn, attivita, false);
      updateSchedulazioni(conn, attivita);
      
      LJSAScheduler.loadSchedulazioni(conn);
      
      ut.commit();
    }
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.disableAttivita(" + mapAttivita + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public 
  int addSchedulazione(Map<String, Object> mapSchedulazione, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    int idSchedulazione = 0;
    
    Schedulazione schedulazione = new Schedulazione(mapSchedulazione);
    String idServizio = schedulazione.getIdServizio();
    String idCredenziale = schedulazione.getIdCredenzialeIns();
    
    LJSAScheduler.checkService(idServizio);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      if(!schedulazione.getFlagNoLog()) {
        // Inserisce la mail della credenziale nella lista di notifica del risultato.
        String email = getEmail(conn, idServizio, idCredenziale);
        if(email != null && email.length() > 0) {
          List<String> listNotificaRisultato = schedulazione.getNotificaRisultato();
          if(canAddEmail(listNotificaRisultato, email)) {
            schedulazione.addNotificaRisultato(email);
          }
          List<String> listNotificaErrori = schedulazione.getNotificaErrori();
          if(canAddEmail(listNotificaErrori, email)) {
            schedulazione.addNotificaErrore(email);
          }
        }
      }
      
      setClasseAttivita(conn, schedulazione);
      boolean boToSchedule = schedulazione.isValid();
      if(boToSchedule) {
        schedulazione.setStato(ISchedulazione.sSTATO_ATTIVA);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      idSchedulazione = insertSchedulazione(conn, schedulazione);
      
      insertDettaglioSchedulazione(conn, schedulazione);
      
      if(boToSchedule) {
        LJSAScheduler.schedule(conn, idSchedulazione, true);
      }
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.addSchedulazione(" + mapSchedulazione + "," + user + ",*)", ex);
      throw ex;
    }
    finally {
      ConnectionManager.close(conn);
    }
    return idSchedulazione;
  }
  
  public 
  boolean updateSchedulazione(Map<String, Object> mapSchedulazione, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Schedulazione schedulazione = new Schedulazione(mapSchedulazione);
    String idServizio = schedulazione.getIdServizio();
    String idCredenziale = schedulazione.getIdCredenzialeAgg();
    
    LJSAScheduler.checkService(idServizio);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      setClasseAttivita(conn, schedulazione);
      boolean boToSchedule = schedulazione.isValid();
      if(boToSchedule) {
        schedulazione.setStato(ISchedulazione.sSTATO_ATTIVA);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      updateSchedulazione(conn, schedulazione);
      deleteDettaglioSchedulazione(conn, schedulazione);
      insertDettaglioSchedulazione(conn, schedulazione);
      
      int idSchedulazione = schedulazione.getIdSchedulazione();
      if(boToSchedule) {
        LJSAScheduler.schedule(conn, idSchedulazione, true);
      } 
      else {
        LJSAScheduler.unschedule(schedulazione);
      }
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.updateSchedulazione(" + mapSchedulazione + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    
    return true;
  }
  
  public 
  boolean start(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    logger.fatal("WSLJSA.start(" + user + ",*)...");
    
    boolean result = false;
    try {
      result = LJSAScheduler.start();
    } 
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.start(" + user + ",*)", ex);
      throw ex;
    }
    return result;
  }
  
  public 
  boolean stop(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    logger.fatal("WSLJSA.stop(" + user + ",*)...");
    
    boolean result = false;
    try {
      result = LJSAScheduler.stop();
    }
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.stop(" + user + ",*)", ex);
      throw ex;
    }
    return result;
  }
  
  public 
  boolean update(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    logger.fatal("WSLJSA.update(" + user + ",*)...");
    
    boolean result = false;
    try {
      result = LJSAScheduler.update();
    }
    catch(Exception ex) {
      logger.error("Exception in WSLJSA.update(" + user + ",*)", ex);
      throw ex;
    }
    return result;
  }
  
  public 
  boolean enableSchedulazione(int idSchedulazione, String idCredenziale, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    boolean result = false;
    
    Schedulazione schedulazione = null;
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      schedulazione = LJSAScheduler.readSchedulazione(conn, idSchedulazione);
      if(schedulazione == null) return false;
      
      schedulazione.setIdCredenzialeAgg(idCredenziale);
      String idServizio = schedulazione.getIdServizio();
      
      LJSAScheduler.checkService(idServizio);
      
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      if(!schedulazione.checkTemporalValidity()) {
        throw new Exception(ILJSAErrors.sJOB_CANT_ENABLED);
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      setEnabled(conn, schedulazione, true);
      
      result = LJSAScheduler.schedule(schedulazione, true);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.enableSchedulazione(" + idSchedulazione + "," + idCredenziale + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return result;
  }
  
  public 
  boolean disableSchedulazione(int idSchedulazione, String idCredenziale, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    boolean result = false;
    
    Schedulazione schedulazione = null;
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      schedulazione = LJSAScheduler.readSchedulazione(conn, idSchedulazione);
      if(schedulazione == null) return false;
      
      schedulazione.setIdCredenzialeAgg(idCredenziale);
      String idServizio = schedulazione.getIdServizio();
      LJSAScheduler.checkService(idServizio);
      if(!existCredenziale(conn, idServizio, idCredenziale)) {
        throw new Exception(ILJSAErrors.sINVALID_CREDENTIAL);
      }
      
      result = LJSAScheduler.unschedule(schedulazione);
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      setEnabled(conn, schedulazione, false);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.disableSchedulazione(" + idSchedulazione + "," + idCredenziale + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return result;
  }
  
  public 
  boolean removeSchedulazione(int idSchedulazione, String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    Connection conn = null;
    UserTransaction ut = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      String stato = DB.readString(conn, "SELECT STATO FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=?", idSchedulazione);
      if(stato == null || stato.length() == 0) {
        return false;
      }
      if(!stato.equals(ISchedulazione.sSTATO_DISATTIVATA)) {
        return false;
      }
      
      ut = ConnectionManager.getUserTransaction(conn);
      ut.begin();
      
      deleteSchedulazione(conn, idSchedulazione);
      
      ut.commit();
    } 
    catch(Exception ex) {
      ConnectionManager.rollback(ut);
      logger.error("Exception in WSLJSA.removeSchedulazione(" + idSchedulazione + "," + user + ",*)", ex);
      throw ex;
    } 
    finally {
      ConnectionManager.close(conn);
    }
    return true;
  }
  
  public 
  List<Map<String, Object>> getInfoSchedulazioni(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    
    List<Schedulazione> listSchedulazioni = LJSAScheduler.getSchedulazioni();
    for(Schedulazione schedulazione : listSchedulazioni) {
      listResult.add(schedulazione.toMap());
    }
    
    return listResult;
  }
  
  public 
  List<Integer> getSchedulazioni(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass);
    
    List<Integer> listResult = new ArrayList<Integer>();
    
    List<Schedulazione> listSchedulazioni = LJSAScheduler.getSchedulazioni();
    for(Schedulazione schedulazione : listSchedulazioni) {
      listResult.add(schedulazione.getIdSchedulazione());
    }
    
    return listResult;
  }
  
  public 
  boolean isStarted(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    return LJSAScheduler.isStarted();
  }
  
  public 
  boolean isRunning(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    boolean boIsStarted = LJSAScheduler.isStarted();
    boolean boIsSleeper = LJSAScheduler.isSleeper();
    int countInternalJobs = LJSAScheduler.countInternalJobs();
    
    return (boIsStarted || !boIsSleeper) && countInternalJobs > 0;
  }
  
  /**
   * Tale metodo permette di "risvegliare" l'istanza di LJSA se e' in sleeping mode. 
   * Il cambiamento di stato non e' persistente: in altre parole se l'istanza di LJSA 
   * viene riavviata essa recupera il flag sleeping dal file di configurazione non modificato.
   *
   * @param user Utente amministratore
   * @param passPassword amministratore
   * @return true|false
   * @throws Exception
   */
  public 
  boolean wakeUp(String user, String pass) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, false);
    
    boolean boIsStarted = LJSAScheduler.isStarted();
    boolean boIsSleeper = LJSAScheduler.isSleeper();
    if(boIsStarted || !boIsSleeper) {
      throw new Exception(ILJSAErrors.sALREADY_RUNNING);
    }
    
    BEConfig.reloadConfig();
    LJSAScheduler.init(true);
    LJSAScheduler.start();
    
    logger.debug("Wake up LJSA (from sleeping mode to running).");
    return LJSAScheduler.isStarted();
  }
  
  /**
   * Tale metodo permette di "addormentare" l'istanza di LJSA. Il cambiamento di stato 
   * e' persistente: in altre parole se l'istanza di LJSA viene riavviata essa recupera 
   * il flag sleeping dal file di configurazione modificato.
   *
   * @param user Utente amministratore
   * @param pass Password amministratore
   * @return true|false
   * @throws Exception
   */
  public boolean layDown(String user, String pass) throws Exception {
    LJSAScheduler.checkAuthorization(user, pass);
    LJSAScheduler.setSleepingMode(true);
    
    logger.debug("Lay down LJSA (sleeping mode).");
    return !LJSAScheduler.isStarted();
  }
  
  /**
   * Imposta il flag sleeping mode con persistenza. Se false = viene eseguito
   * lo shutdown dello schedulatore. Se true  (ed era false) = viene eseguito
   * l'init e lo start dello schedulatore.
   *
   * @param user Utente amministratore
   * @param pass Password amministratore
   * @param flag valore di SleepingMode
   * @return
   * @throws Exception
   */
  public 
  boolean setSleepingMode(String user, String pass, boolean flag) 
    throws Exception 
  {
    LJSAScheduler.checkAuthorization(user, pass, flag);
    
    return LJSAScheduler.setSleepingMode(flag);
  }
  
  protected static 
  int insertSchedulazione(Connection conn, Schedulazione schedulazione) 
    throws Exception 
  {
    int idSchedulazione = ConnectionManager.nextVal(conn, "LJSA_SEQ_SCHEDULAZIONI");
    
    String sSQL = "INSERT INTO LJSA_SCHEDULAZIONI (";
    sSQL += "ID_SCHEDULAZIONE,";
    sSQL += "ID_SERVIZIO,";
    sSQL += "ID_ATTIVITA,";
    sSQL += "DESCRIZIONE,";
    sSQL += "SCHEDULAZIONE,";
    sSQL += "ID_CREDENZIALE_INS,";
    sSQL += "DATA_INSERIMENTO,";
    sSQL += "ORA_INSERIMENTO,";
    sSQL += "ID_CREDENZIALE_AGG,";
    sSQL += "DATA_AGGIORNAMENTO,";
    sSQL += "ORA_AGGIORNAMENTO,";
    sSQL += "STATO,";
    sSQL += "INIZIOVALIDITA,";
    sSQL += "FINEVALIDITA,";
    sSQL += "ESECUZIONI_COMPLETATE,";
    sSQL += "ESECUZIONI_INTERROTTE) ";
    sSQL += "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1,     idSchedulazione);
      pstm.setString(2,  schedulazione.getIdServizio());
      pstm.setString(3,  schedulazione.getIdAttivita());
      pstm.setString(4,  schedulazione.getDescrizione());
      pstm.setString(5,  schedulazione.getSchedulazione());
      pstm.setString(6,  schedulazione.getIdCredenzialeIns());
      pstm.setInt(7,     iCurrentDate);
      pstm.setInt(8,     iCurrentTime);
      pstm.setString(9,  schedulazione.getIdCredenzialeIns());
      pstm.setInt(10,    iCurrentDate);
      pstm.setInt(11,    iCurrentTime);
      pstm.setString(12, schedulazione.getStato());
      pstm.setInt(13,    schedulazione.getInizioValidita());
      pstm.setInt(14,    schedulazione.getFineValidita());
      pstm.setInt(15,    0);
      pstm.setInt(16,    0);
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
    schedulazione.setIdSchedulazione(idSchedulazione);
    schedulazione.setDataInserimento(iCurrentDate);
    schedulazione.setOraInserimento(iCurrentTime);
    schedulazione.setIdCredenzialeAgg(schedulazione.getIdCredenzialeIns());
    schedulazione.setDataAggiornamento(iCurrentDate);
    schedulazione.setOraAggiornamento(iCurrentTime);
    return idSchedulazione;
  }
  
  protected static 
  void insertDettaglioSchedulazione(Connection conn, Schedulazione schedulazione) 
    throws Exception 
  {
    int idSchedulazione = schedulazione.getIdSchedulazione();
    
    String sSQL;
    PreparedStatement pstm = null;
    try {
      
      if(schedulazione.hasParametri()) {
        sSQL = "INSERT INTO LJSA_SCHEDULAZIONI_PARAMETRI(ID_SCHEDULAZIONE,PARAMETRO,VALORE) VALUES(?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        Iterator<Map.Entry<String, Object>> iterator = schedulazione.iteratorParametri();
        while(iterator.hasNext()) {
          Map.Entry<String, Object> entry = iterator.next();
          String key = entry.getKey();
          Object val = entry.getValue();
          
          pstm.setInt(1,    idSchedulazione);
          pstm.setString(2, key);
          pstm.setString(3, WUtil.toString(val, ""));
          pstm.executeUpdate();
        }
      }
      
      LJSAMap mapConfigurazione = schedulazione.getConfigurazione();
      if(!mapConfigurazione.isEmpty()) {
        ConnectionManager.close(pstm);
        
        sSQL = "INSERT INTO LJSA_SCHEDULAZIONI_CONF(ID_SCHEDULAZIONE,OPZIONE,VALORE) VALUES(?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        Iterator<Map.Entry<String, Object>> iterator = schedulazione.iteratorConfigurazione();
        while(iterator.hasNext()) {
          Map.Entry<String, Object> entry = iterator.next();
          String key = entry.getKey();
          Object val = entry.getValue();
          
          pstm.setInt(1,    idSchedulazione);
          pstm.setString(2, key);
          pstm.setString(3, WUtil.toString(val, ""));
          pstm.executeUpdate();
        }
      }
      
      List<Map<String, Object>> listNotifica = schedulazione.getNotifica();
      if(listNotifica != null && !listNotifica.isEmpty()) {
        ConnectionManager.close(pstm);
        
        sSQL = "INSERT INTO LJSA_SCHEDULAZIONI_NOTIFICA(ID_SCHEDULAZIONE,EVENTO,DESTINAZIONE) VALUES(?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        for(int i = 0; i < listNotifica.size(); i++) {
          Map<String, Object> mapRecord = listNotifica.get(i);
          
          String evento = WUtil.toString(mapRecord.get(ISchedulazione.sNOT_EVENTO), "R");
          String destinazione = WUtil.toString(mapRecord.get(ISchedulazione.sNOT_DESTINAZIONE), null);
          if(destinazione == null || destinazione.length() == 0) {
            continue;
          }
          
          pstm.setInt(1,    idSchedulazione);
          pstm.setString(2, evento);
          pstm.setString(3, destinazione);
          pstm.executeUpdate();
        }
      }
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void deleteSchedulazione(Connection conn, int idSchedulazione) 
    throws Exception 
  {
    deleteLogs(conn, idSchedulazione);
    
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=?",      idSchedulazione);
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=?", idSchedulazione);
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=?",  idSchedulazione);
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=?",           idSchedulazione);
  }
  
  protected static 
  void deleteSchedulazioni(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String sSQL = "SELECT ID_SCHEDULAZIONE FROM LJSA_SCHEDULAZIONI WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
    ResultSet rs = null;
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, attivita.getIdServizio());
      pstm.setString(2, attivita.getIdAttivita());
      rs = pstm.executeQuery();
      while(rs.next()) {
        int idSchedulazione = rs.getInt("ID_SCHEDULAZIONE");
        deleteSchedulazione(conn, idSchedulazione);
      }
    } 
    finally {
      ConnectionManager.close(rs, pstm);
    }
  }
  
  protected static 
  void deleteLogs(Connection conn, int idSchedulazione) 
    throws Exception 
  {
    List<Integer> listOfIdLog = new ArrayList<Integer>();
    
    ResultSet rs = null;
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement("SELECT ID_LOG FROM LJSA_LOG WHERE ID_SCHEDULAZIONE=?");
      pstm.setInt(1, idSchedulazione);
      rs = pstm.executeQuery();
      while (rs.next()) {
        listOfIdLog.add(rs.getInt("ID_LOG"));
      }
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    
    if(listOfIdLog.size() == 0) return;
    
    PreparedStatement pstmDF = null;
    PreparedStatement pstmDL = null;
    try {
      pstmDF = conn.prepareStatement("DELETE FROM LJSA_LOG_FILES WHERE ID_LOG=?");
      pstmDL = conn.prepareStatement("DELETE FROM LJSA_LOG WHERE ID_LOG=?");
      for(int idLog : listOfIdLog) {
        pstmDF.setInt(1, idLog);
        pstmDF.executeUpdate();
        
        pstmDL.setInt(1, idLog);
        pstmDL.executeUpdate();
        
        String folderPath = BEConfig.getLJSAOutputFolder(idLog);
        File folder = new File(folderPath);
        if (!folder.exists()) continue;
        
        deleteFiles(folder);
      }
    } 
    finally {
      ConnectionManager.close(pstmDF, pstmDL);
    }
  }
  
  protected static 
  void deleteDettaglioSchedulazione(Connection conn, Schedulazione schedulazione) 
    throws Exception 
  {
    if(schedulazione == null) return;
    
    int idSchedulazione = schedulazione.getIdSchedulazione();
    
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=?",      idSchedulazione);
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=?", idSchedulazione);
    DB.execUpd(conn, "DELETE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=?",  idSchedulazione);
  }
  
  protected static 
  boolean existAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    boolean result = false;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT ID_ATTIVITA FROM LJSA_ATTIVITA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?");
      pstm.setString(1, attivita.getIdServizio());
      pstm.setString(2, attivita.getIdAttivita());
      rs = pstm.executeQuery();
      result = rs.next();
    } 
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return result;
  }
  
  protected static 
  void insertAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String sSQL = "INSERT INTO LJSA_ATTIVITA (";
    sSQL += "ID_SERVIZIO,";        //  (1)
    sSQL += "ID_ATTIVITA,";
    sSQL += "DESCRIZIONE,";
    sSQL += "CLASSE,";
    sSQL += "ATTIVO,";             //  (5)
    sSQL += "ID_CREDENZIALE_INS,"; //  (6)
    sSQL += "DATA_INSERIMENTO,";
    sSQL += "ORA_INSERIMENTO,";
    sSQL += "ID_CREDENZIALE_AGG,"; //  (9)
    sSQL += "DATA_AGGIORNAMENTO,"; // (10)
    sSQL += "ORA_AGGIORNAMENTO) "; // (11)
    sSQL += "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      pstm.setString(1, attivita.getIdServizio());
      pstm.setString(2, attivita.getIdAttivita());
      pstm.setString(3, attivita.getDescrizione());
      pstm.setString(4, attivita.getClasse());
      pstm.setString(5, attivita.getAttivo() ? "S" : "N");
      pstm.setString(6, attivita.getIdCredenzialeIns());
      pstm.setInt(7,    iCurrentDate);
      pstm.setInt(8,    iCurrentTime);
      pstm.setString(9, attivita.getIdCredenzialeIns());
      pstm.setInt(10,   iCurrentDate);
      pstm.setInt(11,   iCurrentTime);
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void insertDettaglioAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String idServizio = attivita.getIdServizio();
    String idAttivita = attivita.getIdAttivita();
    
    PreparedStatement pstm = null;
    try {
      List<Map<String, Object>> listConfigurazione = attivita.getConfigurazione();
      if(listConfigurazione != null && !listConfigurazione.isEmpty()) {
        String sSQL = "INSERT INTO LJSA_ATTIVITA_CONF(ID_SERVIZIO,ID_ATTIVITA,OPZIONE,DESCRIZIONE,VALORI,PREDEFINITO) VALUES(?,?,?,?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        for(Map<String, Object> mapRecord : listConfigurazione) {
          String sOpzione     = WUtil.toString(mapRecord.get(IAttivita.sCONF_OPZIONE),     null);
          String sDescrizione = WUtil.toString(mapRecord.get(IAttivita.sCONF_DESCRIZIONE), null);
          String sValori      = WUtil.toString(mapRecord.get(IAttivita.sCONF_VALORI),      null);
          String sPredefinito = WUtil.toString(mapRecord.get(IAttivita.sCONF_PREDEFINITO), null);
          
          if(sOpzione == null || sOpzione.length() == 0) continue;
          if(sDescrizione == null || sDescrizione.length() == 0) sDescrizione = sOpzione;
          
          pstm.setString(1, idServizio);
          pstm.setString(2, idAttivita);
          pstm.setString(3, sOpzione);
          pstm.setString(4, sDescrizione);
          pstm.setString(5, sValori);
          pstm.setString(6, sPredefinito);
          pstm.executeUpdate();
        }
      }
      
      List<Map<String, Object>> listParametri = attivita.getParametri();
      if(listParametri != null && !listParametri.isEmpty()) {
        ConnectionManager.close(pstm);
        
        String sSQL = "INSERT INTO LJSA_ATTIVITA_PARAMETRI(ID_SERVIZIO,ID_ATTIVITA,PARAMETRO,DESCRIZIONE,VALORI,PREDEFINITO) VALUES(?,?,?,?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        for(Map<String, Object> mapRecord : listParametri) {
          String sParametro   = WUtil.toString(mapRecord.get(IAttivita.sPAR_PARAMETRO),   null);
          String sDescrizione = WUtil.toString(mapRecord.get(IAttivita.sPAR_DESCRIZIONE), null);
          String sValori      = WUtil.toString(mapRecord.get(IAttivita.sPAR_VALORI),      null);
          String sPredefinito = WUtil.toString(mapRecord.get(IAttivita.sPAR_PREDEFINITO), null);
          
          if(sParametro   == null || sParametro.length()   == 0) continue;
          if(sDescrizione == null || sDescrizione.length() == 0) sDescrizione = sParametro;
          
          pstm.setString(1, idServizio);
          pstm.setString(2, idAttivita);
          pstm.setString(3, sParametro);
          pstm.setString(4, sDescrizione);
          pstm.setString(5, sValori);
          pstm.setString(6, sPredefinito);
          pstm.executeUpdate();
        }
      }
      
      List<Map<String, Object>> listNotifica = attivita.getNotifica();
      if(listNotifica != null && !listNotifica.isEmpty()) {
        ConnectionManager.close(pstm);
        
        String sSQL = "INSERT INTO LJSA_ATTIVITA_NOTIFICA(ID_SERVIZIO,ID_ATTIVITA,EVENTO,DESTINAZIONE) VALUES(?,?,?,?)";
        pstm = conn.prepareStatement(sSQL);
        for(Map<String, Object> mapRecord : listNotifica) {
          
          String evento = WUtil.toString(mapRecord.get(IAttivita.sNOT_EVENTO),       null);
          String destin = WUtil.toString(mapRecord.get(IAttivita.sNOT_DESTINAZIONE), null);
          
          if(evento == null || evento.length() == 0) evento = "R";
          if(destin == null || destin.length() == 0) continue;
          
          pstm.setString(1, idServizio);
          pstm.setString(2, idAttivita);
          pstm.setString(3, evento);
          pstm.setString(4, destin);
          pstm.executeUpdate();
        }
      }
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void setEnabledAttivita(Connection conn, Attivita attivita, boolean boEnabled) 
    throws Exception 
  {
    String sSQL = "UPDATE LJSA_ATTIVITA SET ATTIVO=?,ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? ";
    sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, QueryBuilder.decodeBoolean(boEnabled));
      pstm.setString(2, attivita.getIdCredenzialeAgg());
      pstm.setInt(3,    iCurrentDate);
      pstm.setInt(4,    iCurrentTime);
      // WHERE
      pstm.setString(5, attivita.getIdServizio());
      pstm.setString(6, attivita.getIdAttivita());
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void updateAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String sSQL = "UPDATE LJSA_ATTIVITA ";
    sSQL += "SET DESCRIZIONE=?,CLASSE=?,ATTIVO=?,ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? ";
    sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, attivita.getDescrizione());
      pstm.setString(2, attivita.getClasse());
      pstm.setString(3, attivita.getAttivo() ? "S" : "N");
      pstm.setString(4, attivita.getIdCredenzialeAgg());
      pstm.setInt(5,    iCurrentDate);
      pstm.setInt(6,    iCurrentTime);
      // WHERE
      pstm.setString(7, attivita.getIdServizio());
      pstm.setString(8, attivita.getIdAttivita());
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void updateSchedulazione(Connection conn, Schedulazione schedulazione) 
    throws Exception 
  {
    String sSQL = "UPDATE LJSA_SCHEDULAZIONI ";
    sSQL += "SET DESCRIZIONE=?,SCHEDULAZIONE=?,STATO=?,INIZIOVALIDITA=?,FINEVALIDITA=?,ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO = ? ";
    sSQL += "WHERE ID_SCHEDULAZIONE=?"; // (9)
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, schedulazione.getDescrizione());
      pstm.setString(2, schedulazione.getSchedulazione());
      pstm.setString(3, schedulazione.getStato());
      pstm.setInt(4,    schedulazione.getInizioValidita());
      pstm.setInt(5,    schedulazione.getFineValidita());
      pstm.setString(6, schedulazione.getIdCredenzialeAgg());
      pstm.setInt(7,    iCurrentDate);
      pstm.setInt(8,    iCurrentTime);
      // WHERE
      pstm.setInt(9, schedulazione.getIdSchedulazione());
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  void updateSchedulazioni(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String sSQL = "UPDATE LJSA_SCHEDULAZIONI ";
    sSQL += "SET ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? ";
    sSQL += "WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?";
    
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, attivita.getIdCredenzialeAgg());
      pstm.setInt(2,    iCurrentDate);
      pstm.setInt(3,    iCurrentTime);
      // WHERE
      pstm.setString(4, attivita.getIdServizio());
      pstm.setString(5, attivita.getIdAttivita());
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
  }
  
  protected static 
  int countSchedulazioniNonDisattive(Connection conn, Attivita attivita) 
    throws Exception 
  {
    return DB.readInt(conn, "SELECT COUNT(*) FROM LJSA_SCHEDULAZIONI WHERE ID_SERVIZIO=? AND ID_ATTIVITA=? AND STATO<>?", 
        attivita.getIdServizio(), attivita.getIdAttivita(), ISchedulazione.sSTATO_DISATTIVATA);
  }
  
  protected static 
  void deleteAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    DB.execUpd(conn, "DELETE FROM LJSA_ATTIVITA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?", 
        attivita.getIdServizio(), attivita.getIdAttivita());
  }
  
  protected static 
  void deleteDettaglioAttivita(Connection conn, Attivita attivita) 
    throws Exception 
  {
    String idServizio = attivita.getIdServizio();
    String idAttivita = attivita.getIdAttivita();
    
    DB.execUpd(conn, "DELETE FROM LJSA_ATTIVITA_CONF WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?",      idServizio, idAttivita);
    DB.execUpd(conn, "DELETE FROM LJSA_ATTIVITA_PARAMETRI WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?", idServizio, idAttivita);
    DB.execUpd(conn, "DELETE FROM LJSA_ATTIVITA_NOTIFICA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?",  idServizio, idAttivita);
  }
  
  protected static 
  void setEnabled(Connection conn, Schedulazione schedulazione, boolean boEnabled) 
    throws Exception 
  {
    Date now = new Date();
    int iCurrentDate = WUtil.toIntDate(now, 0);
    int iCurrentTime = WUtil.toIntTime(now, 0);
    
    String sSQL = "UPDATE LJSA_SCHEDULAZIONI SET STATO=?,ID_CREDENZIALE_AGG=?,DATA_AGGIORNAMENTO=?,ORA_AGGIORNAMENTO=? WHERE ID_SCHEDULAZIONE=?";
    
    String sStato = boEnabled ? ISchedulazione.sSTATO_ATTIVA : ISchedulazione.sSTATO_DISATTIVATA;
    
    PreparedStatement pstm = null;
    try {
      pstm = conn.prepareStatement(sSQL);
      // SET
      pstm.setString(1, sStato);
      pstm.setString(2, schedulazione.getIdCredenzialeAgg());
      pstm.setInt(3, iCurrentDate);
      pstm.setInt(4, iCurrentTime);
      // WHERE
      pstm.setInt(5, schedulazione.getIdSchedulazione());
      pstm.executeUpdate();
    } 
    finally {
      ConnectionManager.close(pstm);
    }
    schedulazione.setDataAggiornamento(iCurrentDate);
    schedulazione.setOraAggiornamento(iCurrentTime);
    schedulazione.setStato(sStato);
  }
  
  protected static 
  void setClasseAttivita(Connection conn, Schedulazione schedulazione) 
    throws Exception 
  {
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT CLASSE FROM LJSA_ATTIVITA WHERE ID_SERVIZIO=? AND ID_ATTIVITA=?");
      pstm.setString(1, schedulazione.getIdServizio());
      pstm.setString(2, schedulazione.getIdAttivita());
      rs = pstm.executeQuery();
      if(rs.next()) {
        String sClasse = rs.getString("CLASSE");
        Class<? extends Job> classe = null;
        // [Remote]
        if(sClasse != null && (sClasse.startsWith("java:") || sClasse.startsWith("/"))) {
          classe = LJSARemoteJobExecutor.class;
          schedulazione.addConfigurazione("remote.object", sClasse);
        }
        else {
          LJSAClassLoader ljsaClassLoader = new LJSAClassLoader();
          try {
            classe = ljsaClassLoader.loadJobClass(sClasse);
          } 
          catch(ClassNotFoundException cnfex) {
          }
          if(classe == null) {
            throw new Exception(ILJSAErrors.sINVALID_CLASS + " " + sClasse);
          }
        }
        schedulazione.setClasseAttivita(classe);
      } 
      else {
        throw new Exception("Activity " + schedulazione.getIdServizio() + " " + schedulazione.getIdAttivita() + " not found.");
      }
    } 
    finally {
      ConnectionManager.close(rs, pstm);
    }
  }
  
  protected static 
  boolean existCredenziale(Connection conn, String idServizio, String idCredenziale) 
    throws Exception 
  {
    boolean result = false;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT EMAIL FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=? AND ATTIVO=?");
      pstm.setString(1, idServizio);
      pstm.setString(2, idCredenziale);
      pstm.setString(3, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      result = rs.next();
    } 
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return result;
  }
  
  protected static 
  String getEmail(Connection conn, String idServizio, String idCredenziale) 
    throws Exception 
  {
    String result = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT EMAIL FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=?");
      pstm.setString(1, idServizio);
      pstm.setString(2, idCredenziale);
      rs = pstm.executeQuery();
      if(rs.next()) result = rs.getString("EMAIL");
    } 
    finally {
      ConnectionManager.close(rs, pstm);
    }
    if(result == null || result.length() < 5) {
      return null;
    }
    result = result.trim().toLowerCase();
    if(result.indexOf('@') <= 0 || result.indexOf('.') < 0) {
      return null;
    }
    return result;
  }
  
  protected static 
  boolean canAddEmail(List<String> listNotifica, String email) 
  {
    if(listNotifica == null) {
      return false;
    }
    if(listNotifica.contains(email)) {
      return false;
    } 
    else if(listNotifica.contains("-" + email)) {
      return false;
    } 
    else if(listNotifica.contains("-my")) {
      return false;
    }
    return true;
  }
  
  protected static 
  void checkClasseAttivita(Attivita attivita) 
    throws Exception 
  {
    String sClasse = attivita.getClasse();
    if(sClasse == null || sClasse.trim().length() == 0) {
      throw new Exception(ILJSAErrors.sINVALID_CLASS + sClasse);
    }
    // [Remote]
    if(sClasse.startsWith("java:") || sClasse.startsWith("/")) return;
    LJSAClassLoader ljsaClassLoader = new LJSAClassLoader();
    boolean boClassNotFound = ljsaClassLoader.loadClass(sClasse) == null;
    if(boClassNotFound) {
      throw new Exception(ILJSAErrors.sINVALID_CLASS + sClasse);
    }
  }
  
  protected static 
  boolean deleteFiles(File fileOfFolder) 
  {
    boolean result = true;
    if(fileOfFolder.isFile()) {
      return fileOfFolder.delete();
    } 
    else if(fileOfFolder.isDirectory()) {
      File files[] = fileOfFolder.listFiles();
      for(int i = 0; i < files.length; i++) {
        File file = files[i];
        if(file.isDirectory()) {
          if(!deleteFiles(file)) result = false;
        } 
        else {
          if(!file.delete()) result = false;
        }
      }
      if(!fileOfFolder.delete()) result = false;
    }
    return result;
  }
}
