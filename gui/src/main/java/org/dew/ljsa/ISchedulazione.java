package org.dew.ljsa;

public
interface ISchedulazione
{
  public final static String sID_SCHEDULAZIONE           = "id_schedulazione";
  public final static String sID_SERVIZIO                = "id_servizio";
  public final static String sID_ATTIVITA                = "id_attivita";
  public final static String sSCHEDULAZIONE              = "schedulazione";
  public final static String sDESCRIZIONE                = "descrizione";
  public final static String sID_CREDENZIALE_INS         = "id_credenziale_ins";
  public final static String sDATA_INSERIMENTO           = "data_inserimento";
  public final static String sORA_INSERIMENTO            = "ora_inserimento";
  public final static String sID_CREDENZIALE_AGG         = "id_credenziale_agg";
  public final static String sDATA_AGGIORNAMENTO         = "data_aggiornamento";
  public final static String sORA_AGGIORNAMENTO          = "ora_aggiornamento";
  public final static String sSTATO                      = "stato";
  public final static String sINIZIO_VALIDITA            = "inizio_validita";
  public final static String sFINE_VALIDITA              = "fine_validita";
  public final static String sDATA_SCHEDULAZIONE         = "data_schedulazione";
  public final static String sORA_SCHEDULAZIONE          = "ora_schedulazione";
  
  public final static String sCONFIGURAZIONE             = "configurazione";
  public final static String sPARAMETRI                  = "parametri";
  public final static String sNOTIFICA                   = "notifica";
  
  public final static String sNOTIFICA_EVENTO            = "evento";
  public final static String sNOTIFICA_DESTINAZIONE      = "destinazione";
  public final static String sNOTIFICA_DA_ATTIVITA       = "da_attivita";
  public final static String sNOTIFICA_CANCELLATA        = "cancellata";
  
  public final static String sSTATO_ATTIVA               = "A";
  public final static String sSTATO_DISATTIVATA          = "D";
  public final static String sSTATO_IN_ESECUZIONE        = "E";
  
  public final static String sPARAMETRI_PARAMETRO        = "parametro";
  public final static String sPARAMETRI_VALORE           = "valore";
  public final static String sPARAMETRI_DA_ATTIVITA      = "da_attivita";
  public final static String sPARAMETRI_OVERWRITE        = "overwrite";
  
  public final static String sPARAMETRI_DESCRIZIONE      = IAttivita.sPARAMETRI_DESCRIZIONE;
  public final static String sPARAMETRI_VALORI           = IAttivita.sPARAMETRI_VALORI;
  public final static String sPARAMETRI_PREDEFINITO      = IAttivita.sPARAMETRI_PREDEFINITO;
  
  public final static String sCONFIGURAZIONE_OPZIONE     = "opzione";
  public final static String sCONFIGURAZIONE_VALORE      = "valore";
  public final static String sCONFIGURAZIONE_DA_ATTIVITA = "da_attivita";
  public final static String sCONFIGURAZIONE_OVERWRITE   = "overwrite";
  
  public final static String sCONFIGURAZIONE_DESCRIZIONE = IAttivita.sCONFIGURAZIONE_DESCRIZIONE;
  public final static String sCONFIGURAZIONE_VALORI      = IAttivita.sCONFIGURAZIONE_VALORI;
  public final static String sCONFIGURAZIONE_PREDEFINITO = IAttivita.sCONFIGURAZIONE_PREDEFINITO;
  
  public final static String sESECUZIONI_COMPLETATE      = "esecuzioni_completate";
  public final static String sESECUZIONI_INTERROTTE      = "esecuzioni_interrotte";
  
  public final static String sCONF_TIMEOUT               = "timeout";
  public final static String sCONF_STOP_ON_TIMEOUT       = "stopOnTimeout";
  public final static String sCONF_COMPRESS_FILES        = "compressFiles";
  public final static String sCONF_ATTACH_FILES          = "attachFiles";
  public final static String sCONF_ATTACH_ERROR_FILES    = "attachErrorFiles";
  public final static String sCONF_FILE_INFO             = "fileInfo";
  public final static String sCONF_NO_LOG                = "nolog";
  
  public final static String sFLAG_ATTIVO                = "a";
}
