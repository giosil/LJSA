package org.dew.ljsa;

public
interface ISchedulazione
{
  public final static String sID_SCHEDULAZIONE      = "id_schedulazione";
  public final static String sID_SERVIZIO           = "id_servizio";
  public final static String sID_ATTIVITA           = "id_attivita";
  public final static String sSCHEDULAZIONE         = "schedulazione";
  public final static String sDESCRIZIONE           = "descrizione";
  public final static String sID_CREDENZIALE_INS    = "id_credenziale_ins";
  public final static String sDATA_INS              = "data_inserimento";
  public final static String sORA_INS               = "ora_inserimento";
  public final static String sID_CREDENZIALE_AGG    = "id_credenziale_agg";
  public final static String sDATA_AGG              = "data_aggiornamento";
  public final static String sORA_AGG               = "ora_aggiornamento";
  public final static String sSTATO                 = "stato";
  public final static String sINIZIO_VALIDITA       = "inizio_validita";
  public final static String sFINE_VALIDITA         = "fine_validita";
  public final static String sDATA_SCHED            = "data_schedulazione";
  public final static String sORA_SCHE              = "ora_schedulazione";
  public final static String sATTIVO                = "a";
  
  public final static String sSTATO_ATTIVA          = "A";
  public final static String sSTATO_DISATTIVATA     = "D";
  public final static String sSTATO_IN_ESECUZIONE   = "E";
  
  public final static String sPARAMETRI             = "parametri";
  public final static String sPAR_PARAMETRO         = "parametro";
  public final static String sPAR_VALORE            = "valore";
  public final static String sPAR_DA_ATTIVITA       = "da_attivita";
  public final static String sPAR_OVERWRITE         = "overwrite";
  public final static String sPAR_DESCRIZIONE       = IAttivita.sPAR_DESCRIZIONE;
  public final static String sPAR_VALORI            = IAttivita.sPAR_VALORI;
  public final static String sPAR_PREDEFINITO       = IAttivita.sPAR_PREDEFINITO;
  
  public final static String sCONFIGURAZIONE        = "configurazione";
  public final static String sCONF_OPZIONE          = "opzione";
  public final static String sCONF_VALORE           = "valore";
  public final static String sCONF_DA_ATTIVITA      = "da_attivita";
  public final static String sCONF_OVERWRITE        = "overwrite";
  public final static String sCONF_DESCRIZIONE      = IAttivita.sCONF_DESCRIZIONE;
  public final static String sCONF_VALORI           = IAttivita.sCONF_VALORI;
  public final static String sCONF_PREDEFINITO      = IAttivita.sCONF_PREDEFINITO;
  public final static String sCONF_TIMEOUT          = "timeout";
  public final static String sCONF_STOP_ON_TIMEOUT  = "stopOnTimeout";
  public final static String sCONF_COMPRESS_FILES   = "compressFiles";
  public final static String sCONF_ATTACH_FILES     = "attachFiles";
  public final static String sCONF_ATTACH_ERR_FILES = "attachErrorFiles";
  public final static String sCONF_FILE_INFO        = "fileInfo";
  public final static String sCONF_NO_LOG           = "nolog";

  public final static String sNOTIFICA              = "notifica";
  public final static String sNOT_EVENTO            = "evento";
  public final static String sNOT_DESTINAZIONE      = "destinazione";
  public final static String sNOT_DA_ATTIVITA       = "da_attivita";
  public final static String sNOT_CANCELLATA        = "cancellata";

  public final static String sESEC_COMPLETATE       = "esecuzioni_completate";
  public final static String sESEC_INTERROTTE       = "esecuzioni_interrotte";
}
