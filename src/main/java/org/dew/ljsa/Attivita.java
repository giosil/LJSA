package org.dew.ljsa;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.util.WMap;

/**
 * Classe contenente le informazioni di un'attivita'.
 */
public
class Attivita implements IAttivita, Serializable
{
  private static final long serialVersionUID = -5383708740476953804L;
  
  private String idServizio;
  private String idAttivita;
  private String descrizione;
  private String classe;
  private boolean attivo = true;
  
  private List<Map<String, Object>> listParametri;
  private List<Map<String, Object>> listConfigurazione;
  private List<Map<String, Object>> listNotifica;
  
  private String idCredenzialeIns;
  private int    dataInserimento;
  private int    oraInserimento;
  private String idCredenzialeAgg;
  private int    dataAggiornamento;
  private int    oraAggiornamento;
  
  public
  Attivita()
  {
  }
  
  public
  Attivita(String idServizio, String idAttivita, String idCredenziale)
  {
    setIdServizio(idServizio);
    setIdAttivita(idAttivita);
    setIdCredenzialeIns(idCredenziale);
    setIdCredenzialeAgg(idCredenziale);
  }
  
  public
  Attivita(String idServizio, String idAttivita, String classe, String idCredenziale)
  {
    setIdServizio(idServizio);
    setIdAttivita(idAttivita);
    setClasse(classe);
    setIdCredenzialeIns(idCredenziale);
    setIdCredenzialeAgg(idCredenziale);
  }
  
  public Attivita(Map<String, Object> mapValues)
    throws Exception
  {
    WMap wm = new WMap(mapValues);
    
    setIdServizio(wm.getString(sID_SERVIZIO));
    setIdAttivita(wm.getString(sID_ATTIVITA));
    setDescrizione(wm.getString(sDESCRIZIONE));
    setClasse(wm.getString(sCLASSE));
    setAttivo(wm.getBoolean(sATTIVO, true));
    
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
    setDataInserimento(wm.getIntDate(sDATA_INSERIMENTO));
    setOraInserimento(wm.getIntTime(sORA_INSERIMENTO));
    setIdCredenzialeAgg(sIdCredenzialeAgg);
    setDataAggiornamento(wm.getIntDate(sDATA_AGGIORNAMENTO));
    setOraAggiornamento(wm.getIntTime(sORA_AGGIORNAMENTO));
    
    setConfigurazione(wm.getListOfMapObject(sCONFIGURAZIONE));
    setParametri(wm.getListOfMapObject(sPARAMETRI));
    setNotifica(wm.getListOfMapObject(sNOTIFICA));
  }
  
  public Attivita(Map<String, Object> mapValues, String classe)
    throws Exception
  {
    this(mapValues);
    
    if(classe != null && classe.length() > 0 && mapValues !=  null) {
      mapValues.put(IAttivita.sCLASSE, classe);
    }
  }
  
  public String getIdServizio() {
    return idServizio;
  }
  
  public void setIdServizio(String idServizio) {
    this.idServizio = idServizio;
  }
  
  public String getIdAttivita() {
    return idAttivita;
  }
  
  public void setIdAttivita(String idAttivita) {
    this.idAttivita = idAttivita;
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
  
  public String getClasse() {
    return classe;
  }
  
  public void setClasse(String classe) {
    this.classe = classe;
  }
  
  public boolean getAttivo() {
    return attivo;
  }
  
  public void setAttivo(boolean attivo) {
    this.attivo = attivo;
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
  
  public void setOraInserimento(int iOraInserimento) {
    this.oraInserimento = iOraInserimento;
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
  
  public List<Map<String, Object>> getParametri() {
    if(listParametri == null) {
      listParametri = new ArrayList<Map<String, Object>>();
    }
    return listParametri;
  }
  
  public void setParametri(List<Map<String, Object>> listParametri) {
    this.listParametri = listParametri;
  }
  
  public List<Map<String, Object>> getConfigurazione() {
    if(listConfigurazione == null) {
      listConfigurazione = new ArrayList<Map<String, Object>>();
    }
    return listConfigurazione;
  }
  
  public void setConfigurazione(List<Map<String, Object>> listConfigurazione) {
    this.listConfigurazione = listConfigurazione;
  }
  
  public List<Map<String, Object>> getNotifica() {
    if(listNotifica == null) {
      listNotifica = new ArrayList<Map<String, Object>>();
    }
    return listNotifica;
  }
  
  public void setNotifica(List<Map<String, Object>> listNotifica) {
    this.listNotifica = listNotifica;
  }
  
  public
  void addParametro(String sParametro, String sDescrizione, String sValori, String sPredefinito)
  {
    if(listParametri == null) {
      listParametri = new ArrayList<Map<String, Object>>();
    }
    if(sParametro == null || sParametro.length() == 0) sParametro = "par";
    if(sDescrizione == null) sDescrizione = sParametro;
    
    Map<String, Object> mapParametro = new HashMap<String, Object>();
    mapParametro.put(sPARAMETRI_PARAMETRO,   sParametro);
    mapParametro.put(sPARAMETRI_DESCRIZIONE, sDescrizione);
    mapParametro.put(sPARAMETRI_VALORI,      sValori);
    mapParametro.put(sPARAMETRI_PREDEFINITO, sPredefinito);
    
    listParametri.add(mapParametro);
  }
  
  public
  void addConfigurazione(String sOpzione, String sDescrizione, String sValori, String sPredefinito)
  {
    if(listConfigurazione == null) {
      listConfigurazione = new ArrayList<Map<String, Object>>();
    }
    if(sOpzione == null || sOpzione.length() == 0) sOpzione = "con";
    if(sDescrizione == null) sDescrizione = sOpzione;
    
    Map<String, Object> mapConfigurazione = new HashMap<String, Object>();
    mapConfigurazione.put(sCONFIGURAZIONE_OPZIONE,     sOpzione);
    mapConfigurazione.put(sCONFIGURAZIONE_DESCRIZIONE, sDescrizione);
    mapConfigurazione.put(sCONFIGURAZIONE_VALORI,      sValori);
    mapConfigurazione.put(sCONFIGURAZIONE_PREDEFINITO, sPredefinito);
    
    listConfigurazione.add(mapConfigurazione);
  }
  
  public 
  void addNotifica(String sEvento, String sDestinazione)
  {
    if(listNotifica == null) {
      listNotifica = new ArrayList<Map<String, Object>>();
    }
    if(sDestinazione == null || sDestinazione.length() == 0) {
      return;
    }
    Map<String, Object> mapNotifica = new HashMap<String, Object>();
    if(sEvento != null && sEvento.length() > 0) {
      mapNotifica.put(sNOTIFICA_EVENTO, sEvento);
    }
    else {
      mapNotifica.put(sNOTIFICA_EVENTO, "R");
    }
    mapNotifica.put(sNOTIFICA_DESTINAZIONE, sDestinazione);
    listNotifica.add(mapNotifica);
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
  Map<String, Object> toMap()
  {
    Map<String, Object> map = new HashMap<String, Object>();
    
    map.put(sID_SERVIZIO,        idServizio);
    map.put(sID_ATTIVITA,        idAttivita);
    map.put(sDESCRIZIONE,        getDescrizione());
    map.put(sCLASSE,             classe);
    map.put(sATTIVO,             attivo);
    
    map.put(sID_CREDENZIALE_INS, idCredenzialeIns);
    map.put(sDATA_INSERIMENTO,   dataInserimento);
    map.put(sORA_INSERIMENTO,    oraInserimento);
    map.put(sID_CREDENZIALE_AGG, idCredenzialeAgg);
    map.put(sDATA_AGGIORNAMENTO, dataAggiornamento);
    map.put(sORA_AGGIORNAMENTO,  oraAggiornamento);
    
    if(listConfigurazione == null) {
      map.put(sCONFIGURAZIONE,   new ArrayList<Map<String, Object>>());
    }
    else {
      map.put(sCONFIGURAZIONE,   listConfigurazione);
    }
    if(listParametri == null) {
      map.put(sPARAMETRI,        new ArrayList<Map<String, Object>>());
    }
    else {
      map.put(sPARAMETRI,        listParametri);
    }
    if(listNotifica == null) {
      map.put(sNOTIFICA,         new ArrayList<Map<String, Object>>());
    }
    else {
      map.put(sNOTIFICA,         listNotifica);
    }
    
    return map;
  }
  
  @Override
  public 
  boolean equals(Object object) 
  {
    if(object instanceof Attivita) {
      return toString().equals(object.toString());
    }
    return false;
  }
  
  @Override
  public 
  int hashCode() 
  {
    return toString().hashCode();
  }
  
  @Override
  public 
  String toString() 
  {
    return idServizio + ":" + idAttivita;
  }
}
