package org.dew.ljsa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.Job;

import org.util.WMap;
import org.util.WUtil;

/**
 * Classe contenente le informazioni di un'attivita' schedulata.
 */
public
class Schedulazione implements ISchedulazione
{
  protected int    idSchedulazione;
  protected String idServizio;
  protected String idAttivita;
  protected String schedulazione;
  protected String descrizione;
  protected String stato = sSTATO_ATTIVA;
  
  protected Class<? extends Job> classeAttivita;
  
  protected String idCredenzialeIns;
  protected int    dataInserimento;
  protected int    oraInserimento;
  
  protected String idCredenzialeAgg;
  protected int    dataAggiornamento;
  protected int    oraAggiornamento;
  
  protected int    inizioValidita;
  protected int    fineValidita;
  
  protected int    esecuzioniCompletate;
  protected int    esecuzioniInterrotte;
  
  protected Map<String, Object> mapConfigurazione;
  protected Map<String, Object> mapParametri;
  protected List<Map<String, Object>> listNotifica;
  protected List<Map<String, Object>> listNotificaTemporanea;
  
  protected boolean boDontNotify = false;
  
  public
  Schedulazione()
  {
  }
  
  public
  Schedulazione(String sIdServizio, String sIdAttivita, String sSchedulazione, String sIdCredenziale)
  {
    setIdServizio(sIdServizio);
    setIdAttivita(sIdAttivita);
    setSchedulazione(sSchedulazione);
    setIdCredenzialeIns(sIdCredenziale);
    setIdCredenzialeAgg(sIdCredenziale);
  }
  
  public
  Schedulazione(String sIdServizio, String sIdAttivita, int iDataSchedulazione, int iOraSchedulazione, String sIdCredenziale)
  {
    setIdServizio(sIdServizio);
    setIdAttivita(sIdAttivita);
    setSchedulazione(iDataSchedulazione, iOraSchedulazione);
    setIdCredenzialeIns(sIdCredenziale);
    setIdCredenzialeAgg(sIdCredenziale);
  }
  
  public
  Schedulazione(Map<String, Object> mapValues)
    throws Exception
  {
    WMap wm = new WMap(mapValues);
    
    setIdSchedulazione(wm.getInt(sID_SCHEDULAZIONE));
    setIdServizio(wm.getString(sID_SERVIZIO));
    setIdAttivita(wm.getString(sID_ATTIVITA));
    String sSchedulazione = wm.getString(sSCHEDULAZIONE);
    if(sSchedulazione == null) {
      if(wm.get(sDATA_SCHED) == null) {
        throw new Exception(sSCHEDULAZIONE + " is null");
      }
      int iDataSchedulazione = wm.getIntDate(sDATA_SCHED);
      int iOraSchedulazione  = wm.getIntTime(sORA_SCHE);
      setSchedulazione(iDataSchedulazione, iOraSchedulazione);
    }
    else {
      setSchedulazione(sSchedulazione);
    }
    setDescrizione(wm.getString(sDESCRIZIONE));
    setStato(wm.getString(sSTATO));
    
    String sIdCredenzialeIns = wm.getString(sID_CREDENZIALE_INS);
    String sIdCredenzialeAgg = wm.getString(sID_CREDENZIALE_AGG);
    if(sIdCredenzialeIns == null && sIdCredenzialeAgg == null) {
      throw new Exception(sID_CREDENZIALE_INS + " and " + sID_CREDENZIALE_AGG + " are null");
    }
    if(sIdCredenzialeIns == null) {
      sIdCredenzialeIns = sIdCredenzialeAgg;
    }
    if(sIdCredenzialeAgg == null) {
      sIdCredenzialeAgg = sIdCredenzialeIns;
    }
    
    setIdCredenzialeIns(sIdCredenzialeIns);
    setDataInserimento(wm.getIntDate(sDATA_INS));
    setOraInserimento(wm.getIntTime(sORA_INS));
    setIdCredenzialeAgg(sIdCredenzialeAgg);
    setDataAggiornamento(wm.getIntDate(sDATA_AGG));
    setOraAggiornamento(wm.getIntTime(sORA_AGG));
    setInizioValidita(wm.getIntDate(sINIZIO_VALIDITA));
    setFineValidita(wm.getIntDate(sFINE_VALIDITA));
    
    Object oConfigurazione = wm.get(sCONFIGURAZIONE);
    if(oConfigurazione == null) {
      setConfigurazione(new HashMap<String, Object>());
    }
    else if(oConfigurazione instanceof Map) {
      setConfigurazione(WUtil.toMapObject(oConfigurazione));
    }
    else if(oConfigurazione instanceof List) {
      List<?> listConf = (List<?>) oConfigurazione;
      Map<String, Object> mapConf = new HashMap<String, Object>();
      for(int i = 0; i < listConf.size(); i++) {
        Map<String, Object> map = WUtil.toMapObject(listConf.get(i));
        String sOpzione = WUtil.toString(map.get(sCONF_OPZIONE), null);
        Object oValore  = map.get(sCONF_VALORE);
        String sValore = null;
        if(oValore != null) {
          sValore = oValore.toString();
        }
        else {
          sValore = "";
        }
        boolean daAttivita = WUtil.toBoolean(map.get(sCONF_DA_ATTIVITA), false);
        if(daAttivita) {
          boolean overWrite = WUtil.toBoolean(map.get(sCONF_OVERWRITE), false);
          if(overWrite) {
            mapConf.put(sOpzione, sValore);
          }
        }
        else {
          mapConf.put(sOpzione, sValore);
        }
      }
      setConfigurazione(mapConf);
    }
    
    Object oParametri = wm.get(sPARAMETRI);
    if(oParametri == null) {
      setParametri(new HashMap<String, Object>());
    }
    else if(oParametri instanceof Map) {
      setParametri(WUtil.toMapObject(oParametri));
    }
    else if(oParametri instanceof List) {
      List<?> listParams = (List<?>) oParametri;
      Map<String, Object> mapParams = new HashMap<String, Object>();
      for(int i = 0; i < listParams.size(); i++) {
        Map<String, Object> map = WUtil.toMapObject(listParams.get(i));
        String sParametro = WUtil.toString(map.get(sPAR_PARAMETRO), null);
        Object oValore  = map.get(sPAR_VALORE);
        String sValore = null;
        if(oValore != null) {
          sValore = oValore.toString();
        }
        else {
          sValore = "";
        }
        Boolean oDaAttivita = (Boolean) map.get(sPAR_DA_ATTIVITA);
        if(oDaAttivita != null && oDaAttivita.booleanValue()) {
          Boolean oOverWrite = (Boolean) map.get(sPAR_OVERWRITE);
          if(oOverWrite != null && oOverWrite.booleanValue()) {
            mapParams.put(sParametro, sValore);
          }
        }
        else {
          mapParams.put(sParametro, sValore);
        }
      }
      setParametri(mapParams);
    }
    
    List<Map<String, Object>> listNotifica = wm.getListOfMapObject(sNOTIFICA);
    
    List<Map<String, Object>> vNotifica = new ArrayList<Map<String, Object>>();
    
    if(listNotifica != null) {
      for(int i = 0; i < listNotifica.size(); i++) {
        Map<String, Object> map = listNotifica.get(i);
        String sEvento       = WUtil.toString(map.get(sNOT_EVENTO),       null);
        String sDestinazione = WUtil.toString(map.get(sNOT_DESTINAZIONE), null);
        if(sDestinazione == null) continue;
        Boolean boDaAttivita = WUtil.toBoolean(map.get(sNOT_DA_ATTIVITA), false);
        Boolean boCancellata = WUtil.toBoolean(map.get(sNOT_CANCELLATA),  false);
        if(boDaAttivita) {
          if(boCancellata) {
            Map<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put(sNOT_EVENTO,       sEvento);
            mapItem.put(sNOT_DESTINAZIONE, "-" + sDestinazione);
            vNotifica.add(mapItem);
          }
        }
        else {
          if(boCancellata) {
            Map<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put(sNOT_EVENTO,       sEvento);
            mapItem.put(sNOT_DESTINAZIONE, "-" + sDestinazione);
            vNotifica.add(mapItem);
          }
          else {
            Map<String, Object> mapItem = new HashMap<String, Object>();
            mapItem.put(sNOT_EVENTO,       sEvento);
            mapItem.put(sNOT_DESTINAZIONE, sDestinazione);
            vNotifica.add(mapItem);
          }
        }
      }
    }
    
    setNotifica(vNotifica);
  }
  
  public String getIdAttivita() {
    return idAttivita;
  }
  
  public void setIdAttivita(String idAttivita) {
    this.idAttivita = idAttivita;
  }
  
  public Class<? extends Job> getClasseAttivita() {
    return classeAttivita;
  }
  
  public void setClasseAttivita(Class<? extends Job> classeAttivita) {
    this.classeAttivita = classeAttivita;
  }
  
  public String getIdCredenzialeIns() {
    return idCredenzialeIns;
  }
  
  public void setIdCredenzialeIns(String idCredenzialeIns) {
    this.idCredenzialeIns = idCredenzialeIns;
  }
  
  public int getDataInserimento() {
    return dataInserimento;
  }
  
  public void setDataInserimento(int dataInserimento) {
    this.dataInserimento = dataInserimento;
  }
  
  public int getOraInserimento() {
    return oraInserimento;
  }
  
  public void setOraInserimento(int oraInserimento) {
    this.oraInserimento = oraInserimento;
  }
  
  public String getIdCredenzialeAgg() {
    return idCredenzialeAgg;
  }
  
  public void setIdCredenzialeAgg(String idCredenzialeAgg) {
    this.idCredenzialeAgg = idCredenzialeAgg;
  }
  
  public int getDataAggiornamento() {
    return dataAggiornamento;
  }
  
  public void setDataAggiornamento(int dataAggiornamento) {
    this.dataAggiornamento = dataAggiornamento;
  }
  
  public int getOraAggiornamento() {
    return oraAggiornamento;
  }
  
  public void setOraAggiornamento(int oraAggiornamento) {
    this.oraAggiornamento = oraAggiornamento;
  }
  
  public String getStato() {
    return stato;
  }
  
  public void setStato(String stato) {
    if(stato == null) {
      this.stato = sSTATO_ATTIVA;
    }
    else {
      this.stato = stato;
    }
    if(stato != null && stato.equalsIgnoreCase(sSTATO_IN_ESECUZIONE)) {
      clearNotificaTemporanea();
    }
  }
  
  public int getInizioValidita() {
    return inizioValidita;
  }
  
  public void setInizioValidita(Date dateInizioValidita) {
    if(dateInizioValidita == null) {
      this.inizioValidita = WUtil.toIntDate(new Date(), 0);
    }
    else {
      this.inizioValidita = WUtil.toIntDate(dateInizioValidita, 0);
    }
  }
  
  public void setInizioValidita(int inizioValidita) {
    if(inizioValidita == 0) {
      this.inizioValidita = WUtil.toIntDate(new Date(), 0);
    }
    else {
      this.inizioValidita = inizioValidita;
    }
  }
  
  public int getFineValidita() {
    return fineValidita;
  }
  
  public void setFineValidita(Date dateFineValidita) {
    if(dateFineValidita == null) {
      this.fineValidita = getDefFineValidita();
    }
    else {
      this.fineValidita = WUtil.toIntDate(dateFineValidita, 0);
    }
  }
  
  public void setFineValidita(int fineValidita) {
    if(fineValidita == 0) {
      this.fineValidita = getDefFineValidita();
    }
    else {
      if(fineValidita == 99991231 && isOneShot()) {
        this.fineValidita = WUtil.toIntDate(new Date(), 0);
      }
      else {
        this.fineValidita = fineValidita;
      }
    }
  }
  
  public int getEsecuzioniCompletate() {
    return esecuzioniCompletate;
  }
  
  public void setEsecuzioniCompletate(int esecuzioniCompletate) {
    this.esecuzioniCompletate = esecuzioniCompletate;
  }
  
  public int getEsecuzioniInterrotte() {
    return esecuzioniInterrotte;
  }
  
  public void setEsecuzioniInterrotte(int esecuzioniInterrotte) {
    this.esecuzioniInterrotte = esecuzioniInterrotte;
  }
  
  public String getIdServizio() {
    return idServizio;
  }
  
  public void setIdServizio(String idServizio) {
    this.idServizio = idServizio;
  }
  
  public String getDescrizione() {
    if(descrizione == null) {
      descrizione = idServizio + ":" + idAttivita;
    }
    return descrizione;
  }
  
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  public String getSchedulazione() {
    return schedulazione;
  }
  
  public boolean isOneShot() {
    if(schedulazione == null) return false;
    return schedulazione.equalsIgnoreCase("now");
  }
  
  public void setDontNotify(boolean boDontNotify) {
    this.boDontNotify = boDontNotify;
  }
  
  public boolean isDontNotify() {
    return boDontNotify;
  }
  
  /**
   * Imposta una schedulazione periodica con intervallo specificato dal parametro <i>minuti</i>.
   *
   * @param minuti intervallo espresso in minuti
   */
  public
  void setSchedulazione(int minuti)
  {
    schedulazione = "0 0/" + minuti + " * * * ?";
  }
  
  /**
   * Imposta la schedulazione alla data (YYYYMMGG) e all'ora (HHMM) specificata.
   *
   * @param date int YYYYMMGG
   * @param time int HHMM
   */
  public
  void setSchedulazione(int date, int time)
  {
    if(date < 19000101) {
      date = WUtil.toIntDate(new Date(), 0);
    }
    
    int YYYY = date / 10000;
    int MM   = (date % 10000) / 100;
    int DD   = (date % 10000) % 100;
    int HH   = 0;
    int MI   = 0;
    int SS   = 0;
    
    if(time > 9999) {
      HH = time / 10000;
      MI = (time % 10000) / 100;
      SS = (time % 10000) % 100;
    }
    else {
      HH = time / 100;
      MI = time % 100;
    }
    
    inizioValidita = WUtil.toIntDate(new Date(), 0);
    fineValidita   = date;
    
    schedulazione = "";
    schedulazione += SS + " ";
    schedulazione += MI + " ";
    schedulazione += HH + " ";
    schedulazione += DD + " ";
    schedulazione += MM + " ? ";
    schedulazione += YYYY;
  }
  
  public
  void setSchedulazione(String schedulazione)
  {
    this.schedulazione = schedulazione;
    
    if(inizioValidita == 0) inizioValidita = getDefInizioValidita();
    if(fineValidita   == 0) fineValidita   = getDefFineValidita();
  }
  
  public
  int getIdSchedulazione()
  {
    return idSchedulazione;
  }
  
  public
  void setIdSchedulazione(int idSchedulazione)
  {
    this.idSchedulazione = idSchedulazione;
  }
  
  public
  LJSAMap getConfigurazione()
  {
    LJSAMap ljsaMap = null;
    if(mapConfigurazione == null) {
      ljsaMap = new LJSAMap();
    }
    else {
      ljsaMap = new LJSAMap(mapConfigurazione);
    }
    ljsaMap.setExceptionPrefix(ILJSAErrors.sOPTION_MISSING);
    return ljsaMap;
  }
  
  public
  Iterator<Map.Entry<String, Object>> iteratorConfigurazione()
  {
    if(mapConfigurazione == null) {
      mapConfigurazione = new HashMap<String, Object>();
    }
    return mapConfigurazione.entrySet().iterator();
  }
  
  public
  boolean hasConfigurazione() 
  {
    if(mapConfigurazione == null) return false;
    return !mapConfigurazione.isEmpty();
  }
  
  public
  void setConfigurazione(Map<String, Object> mapConfigurazione)
  {
    this.mapConfigurazione = mapConfigurazione;
  }
  
  public
  void addConfigurazione(String key, Object value)
  {
    if(mapConfigurazione == null) {
      mapConfigurazione = new HashMap<String, Object>();
    }
    mapConfigurazione.put(key, value);
  }
  
  public
  LJSAMap getParametri()
  {
    LJSAMap ljsaMap = null;
    if(mapParametri == null) {
      ljsaMap = new LJSAMap();
    }
    else {
      ljsaMap = new LJSAMap(mapParametri);
    }
    ljsaMap.setExceptionPrefix(ILJSAErrors.sPARAMETER_MISSING);
    return ljsaMap;
  }
  
  public
  Iterator<Map.Entry<String, Object>> iteratorParametri()
  {
    if(mapParametri == null) {
      mapParametri = new HashMap<String, Object>();
    }
    return mapParametri.entrySet().iterator();
  }
  
  public
  boolean hasParametri() 
  {
    if(mapParametri == null) return false;
    return !mapParametri.isEmpty();
  }
  
  public
  void setParametri(Map<String, Object> mapParametri)
  {
    this.mapParametri = mapParametri;
  }
  
  public
  void addParametro(String key, Object value)
  {
    if(mapParametri == null) {
      mapParametri = new HashMap<String, Object>();
    }
    mapParametri.put(key, value);
  }
  
  public
  List<Map<String, Object>> getNotifica()
  {
    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
    if(listNotifica != null) {
      listResult.addAll(listNotifica);
    }
    if(listNotificaTemporanea != null) {
      listResult.addAll(listNotificaTemporanea);
    }
    return listResult;
  }
  
  public
  void setNotifica(List<Map<String, Object>> listNotifica)
  {
    this.listNotifica = listNotifica;
  }
  
  public
  void addNotifica(String sEvento, String sDestinazione)
  {
    if(sDestinazione == null || sDestinazione.length() == 0) {
      return;
    }
    
    Map<String, Object> mapNotifica = new HashMap<String, Object>();
    if(sEvento != null) {
      mapNotifica.put(sNOT_EVENTO, sEvento);
    }
    else {
      mapNotifica.put(sNOT_EVENTO, "R");
    }
    mapNotifica.put(sNOT_DESTINAZIONE, sDestinazione);
    
    if(stato != null && stato.equals(sSTATO_IN_ESECUZIONE)) {
      if(listNotificaTemporanea == null) {
        listNotificaTemporanea = new ArrayList<Map<String, Object>>();
      }
      listNotificaTemporanea.add(mapNotifica);
    }
    else {
      if(listNotifica == null) {
        listNotifica = new ArrayList<Map<String, Object>>();
      }
      listNotifica.add(mapNotifica);
    }
  }
  
  public
  void addNotificaRisultato(String sDestinazione)
  {
    addNotifica("R", sDestinazione);
  }
  
  public
  void addNotificaErrore(String sDestinazione)
  {
    addNotifica("E", sDestinazione);
  }
  
  public
  void addNotificaTimeout(String sDestinazione)
  {
    addNotifica("T", sDestinazione);
  }
  
  public
  void clearNotificaTemporanea()
  {
    if(listNotificaTemporanea != null) {
      listNotificaTemporanea.clear();
    }
    listNotificaTemporanea = null;
  }
  
  public
  List<String> getNotificaRisultato()
  {
    return getNotifica("R");
  }
  
  public
  List<String> getNotificaErrori()
  {
    return getNotifica("E");
  }
  
  public
  List<String> getNotificaTimeout()
  {
    return getNotifica("T");
  }
  
  public
  List<String> getNotifica(String sEvento)
  {
    List<String> listResult = new ArrayList<String>();
    
    if(listNotifica != null) {
      for(int i = 0; i < listNotifica.size(); i++) {
        Map<String, Object> mapNotifica = listNotifica.get(i);
        String sE = WUtil.toString(mapNotifica.get(sNOT_EVENTO), null);
        if(sE != null && sE.equals(sEvento)) {
          Object oDestinazione = mapNotifica.get(sNOT_DESTINAZIONE);
          if(oDestinazione != null) {
            listResult.add(oDestinazione.toString());
          }
        }
      }
    }
    
    if(listNotificaTemporanea != null) {
      for(int i = 0; i < listNotificaTemporanea.size(); i++) {
        Map<String, Object> mapNotifica = listNotificaTemporanea.get(i);
        String sE = (String) mapNotifica.get(sNOT_EVENTO);
        if(sE != null && sE.equals(sEvento)) {
          Object oDestinazione = mapNotifica.get(sNOT_DESTINAZIONE);
          if(oDestinazione != null) {
            listResult.add(oDestinazione.toString());
          }
        }
      }
    }
    
    return listResult;
  }
  
  public
  int getTimeout()
  {
    if(mapConfigurazione == null) return 0;
    return WUtil.toInt(mapConfigurazione.get(sCONF_TIMEOUT), 0);
  }
  
  public
  boolean getFlagCompressFiles()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_COMPRESS_FILES), false);
  }
  
  public
  boolean getFlagAttachFiles()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_ATTACH_FILES), false);
  }
  
  public
  boolean getFlagAttachErrorFiles()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_ATTACH_ERR_FILES), false);
  }
  
  public
  boolean getFlagFileInfo()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_FILE_INFO), false);
  }
  
  public
  boolean getFlagStopOnTimeout()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_STOP_ON_TIMEOUT), false);
  }
  
  public
  boolean getFlagNoLog()
  {
    if(mapConfigurazione == null) return false;
    return WUtil.toBoolean(mapConfigurazione.get(sCONF_NO_LOG), false);
  }
  
  public
  boolean isUpdated(Schedulazione schedulazione)
  {
    int iId = schedulazione.getIdSchedulazione();
    int iDataAggiornamento = schedulazione.getDataAggiornamento();
    int iOraAggiornamento = schedulazione.getOraAggiornamento();
    
    if(idSchedulazione != iId) return false;
    
    if(dataAggiornamento < iDataAggiornamento) {
      return true;
    }
    else if(dataAggiornamento == iDataAggiornamento) {
      if(oraAggiornamento < iOraAggiornamento) {
        return true;
      }
    }
    
    return false;
  }
  
  public
  boolean isExpired()
  {
    int currentDate = WUtil.toInt(new Date(), 0);
    if(currentDate > fineValidita) {
      return true;
    }
    return false;
  }
  
  public
  boolean isEnabled()
  {
    if(stato != null && stato.equalsIgnoreCase(sSTATO_DISATTIVATA)) {
      return false;
    }
    return true;
  }
  
  public
  boolean isValid()
  {
    int currentDate = WUtil.toInt(new Date(), 0);
    if(currentDate < inizioValidita || currentDate > fineValidita) {
      return false;
    }
    if(stato != null && stato.equalsIgnoreCase(sSTATO_DISATTIVATA)) {
      return false;
    }
    return true;
  }
  
  public
  boolean checkTemporalValidity()
  {
    int currentDate = WUtil.toInt(new Date(), 0);
    if(currentDate < inizioValidita || currentDate > fineValidita) {
      return false;
    }
    return true;
  }
  
  public
  String getJobName()
  {
    return idSchedulazione + ":" + idServizio + ":" + idAttivita;
  }
  
  public
  String getJobGroup()
  {
    return "LJSA";
  }
  
  public
  String getTriggerName()
  {
    return idSchedulazione + ":" + idServizio + ":" + idAttivita;
  }
  
  public
  String getTriggerGroup()
  {
    return "LJSA";
  }
  
  protected
  int getDefInizioValidita()
  {
    return WUtil.toIntDate(new Date(), 0);
  }
  
  protected
  int getDefFineValidita()
  {
    if(schedulazione == null || schedulazione.length() == 0) {
      return WUtil.toIntDate(new Date(), 0);
    }
    else if(schedulazione.equalsIgnoreCase("now")) {
      return WUtil.toIntDate(new Date(), 0);
    }
    return 99991231;
  }
  
  public
  Map<String, Object> toMap()
  {
    Map<String, Object> map = new HashMap<String, Object>();
    
    map.put(sID_SCHEDULAZIONE,   idSchedulazione);
    map.put(sID_SERVIZIO,        idServizio);
    map.put(sID_ATTIVITA,        idAttivita);
    map.put(sSCHEDULAZIONE,      schedulazione);
    map.put(sDESCRIZIONE,        getDescrizione());
    map.put(sSTATO,              getStato());
    map.put(sID_CREDENZIALE_INS, idCredenzialeIns);
    map.put(sDATA_INS,   dataInserimento);
    map.put(sORA_INS,    oraInserimento);
    map.put(sID_CREDENZIALE_AGG, idCredenzialeAgg);
    map.put(sDATA_AGG, dataAggiornamento);
    map.put(sORA_AGG,  oraAggiornamento);
    
    if(inizioValidita == 0) inizioValidita = getDefInizioValidita();
    if(fineValidita   == 0) fineValidita   = getDefFineValidita();
    map.put(sINIZIO_VALIDITA,    inizioValidita);
    map.put(sFINE_VALIDITA,      fineValidita);
    
    map.put(sCONFIGURAZIONE, mapConfigurazione);
    map.put(sPARAMETRI,      mapParametri);
    
    if(listNotifica == null) {
      map.put(sNOTIFICA, new ArrayList<Map<String, Object>>());
    }
    else {
      map.put(sNOTIFICA, listNotifica);
    }
    
    return map;
  }
  
  @Override
  public
  boolean equals(Object object)
  {
    if(object instanceof Schedulazione) {
      return idSchedulazione == ((Schedulazione) object).getIdSchedulazione();
    }
    return false;
  }
  
  @Override
  public
  int hashCode()
  {
    return idSchedulazione;
  }
  
  @Override
  public
  String toString()
  {
    return idSchedulazione + ":" + idServizio + ":" + idAttivita;
  }
}
