package org.dew.ljsa;

public
interface ILJSAErrors
{
  public static final String sCUSTOM                 = "LJSA-000#";
  public static final String sNOT_AUTHORIZED         = "LJSA-001#Utente LJSA non autorizzato.";
  public static final String sINVALID_CREDENTIAL     = "LJSA-002#Credenziale inesistente o non valida nel servizio specificato.";
  public static final String sINVALID_CLASS          = "LJSA-003#Classe attivit\340 non valida: ";
  public static final String sSLEEPING_MODE          = "LJSA-004#LJSA \350 in modalit\340 disattiva (sleeping mode).";
  public static final String sOPTION_MISSING         = "LJSA-005#Opzione mancante: ";
  public static final String sPARAMETER_MISSING      = "LJSA-006#Parametro mancante: ";
  public static final String sINTERRUPTED_JOB        = "LJSA-007#Job interrotto.";
  public static final String sINVALID_SCHEDULATION   = "LJSA-008#Espressione di schedulazione non corretta.";
  public static final String sJOB_CANT_ENABLED       = "LJSA-009#Schedulazione non attivabile.";
  public static final String sACTIVITY_UNREMOVABLE   = "LJSA-010#L'attivit\340 non pu\362 essere rimossa.";
  public static final String sDB_NOT_AVAILABLE       = "LJSA-011#Database non disponibile.";
  public static final String sSCHED_EXPIRED          = "LJSA-012#Schedulazione scaduta (verificare la fine validit\340).";
  public static final String sSCHED_NOT_ENABLED      = "LJSA-013#Schedulazione disattivata.";
  public static final String sTHERE_ARE_RUNNING_JOBS = "LJSA-014#Operazione non consentita con job in esecuzione.";
  public static final String sSERVICE_NOT_MANAGED    = "LJSA-015#Operazione riferita ad un servizio non gestito.";
  public static final String sALREADY_RUNNING        = "LJSA-016#L'istanza di LJSA \350 gi\340 in running.";
}
