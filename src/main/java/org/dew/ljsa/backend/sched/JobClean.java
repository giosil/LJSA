package org.dew.ljsa.backend.sched;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.util.WUtil;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;

/**
 * Job di Log retention.
 */
public
class JobClean implements Job
{
  public static final String sFILE_LAST_ID_DELETED = "last_id_deleted.txt";
  public static final String sFILE_LOG             = "jobclean.log";
  public static final String sFILE_UNDELETEDFILES  = "undeleted_files.txt";
  
  protected PrintStream psLog;
  protected PrintStream psUndeletedFiles;
  
  public
  void execute(JobExecutionContext jec)
    throws JobExecutionException
  {
    String sLogFolder = BEConfig.getLJSALogFolder();
    try {
      psLog = new PrintStream(new FileOutputStream(sLogFolder + File.separator + sFILE_LOG, false));
    }
    catch(Exception ex) { psLog = System.out; }
    try {
      psUndeletedFiles = new PrintStream(new FileOutputStream(sLogFolder + File.separator + sFILE_UNDELETEDFILES, true));
    }
    catch(Exception ex) { psUndeletedFiles = System.out; }
    
    psLog.println("JobClean.execute started at " +WUtil.formatDateTime(Calendar.getInstance(), "-", true) + "...");
    Connection conn = null;
    Integer oIdLog = null;
    try {
      Integer oLastIdDeleted = loadLastIdDeleted();
      
      conn = ConnectionManager.getDefaultConnection();
      
      // Cancellazione dei log dall'archivio
      List<Integer> listIdLog = getListIdLog(conn, oLastIdDeleted);
      for(int i = 0; i < listIdLog.size(); i++) {
        oIdLog = listIdLog.get(i);
        String sFolder = BEConfig.getLJSAOutputFolder(oIdLog);
        psLog.print("   check " + sFolder + "... ");
        File fFolder = new File(sFolder);
        if(!fFolder.exists()) {
          psLog.println("don't exists!");
          deleteLog(conn, oIdLog.intValue());
          continue;
        }
        psLog.println("exits");
        if(deleteFiles(fFolder)) {
          oLastIdDeleted = oIdLog;
        }
        deleteLog(conn, oIdLog.intValue());
        conn.commit();
      }
      
      saveLastIdDeleted(oLastIdDeleted);
      
      // Cancellazione file direttamente dalla cartella
      int iLastIdDeleted = oLastIdDeleted != null ? oLastIdDeleted.intValue() : 0;
      String sFolder = BEConfig.getLJSAOutputFolder();
      psLog.println("   check list of " + sFolder + "... ");
      File fFolder = new File(sFolder);
      if(fFolder.exists()) {
        String[] asFiles = fFolder.list();
        for(int i = 0; i < asFiles.length; i++) {
          String sFile = asFiles[i];
          int iValue = 0;
          try{ iValue = Integer.parseInt(sFile); } catch(Exception ex) {}
          if(iValue != 0 && iValue < iLastIdDeleted) {
            deleteFiles(new File(sFolder + File.separator + sFile));
          }
        }
      }
    }
    catch(Exception ex) {
      psLog.println("Exception in JobClean.execute (oIdLog = " + oIdLog + "): " + ex);
      psLog.close();
      psUndeletedFiles.close();
      throw new JobExecutionException(ex, false);
    }
    finally {
      if(conn != null) ConnectionManager.closeConnection(conn);
    }
    psLog.println("JobClean.execute ended at " +WUtil.formatDateTime(Calendar.getInstance(), "-", true));
    psLog.close();
    psUndeletedFiles.close();
  }
  
  protected
  List<Integer> getListIdLog(Connection conn, Integer oLastIdDeleted)
      throws Exception
  {
    psLog.println("   getListIdLog(" + oLastIdDeleted + ")...");
    List<Integer> listResult = new ArrayList<Integer>();
    
    int iHistory = BEConfig.getLJSAHistory();
    
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DATE, -iHistory);
    int iEndDate = WUtil.toIntDate(cal, 0);
    cal.add(Calendar.DATE, -3);
    int iBeginDate = WUtil.toIntDate(cal, 0);
    
    String sSQL = "SELECT L.ID_LOG,S.ID_SERVIZIO,S.ID_ATTIVITA,L.DATA_INIZIO,L.ORA_INIZIO ";
    sSQL += "FROM LJSA_LOG L,LJSA_SCHEDULAZIONI S ";
    sSQL += "WHERE L.ID_SCHEDULAZIONE = S.ID_SCHEDULAZIONE";
    sSQL += " AND L.DATA_FINE >= " + iBeginDate;
    sSQL += " AND L.DATA_FINE < "  + iEndDate;
    if(oLastIdDeleted != null) sSQL += " AND L.ID_LOG > " + oLastIdDeleted;
    String sLJSAService = BEConfig.getProperty(BEConfig.sLJSA_CONF_SERVICE);
    if(sLJSAService != null && sLJSAService.length() > 0) {
      sSQL += " AND S.ID_SERVIZIO = '" + sLJSAService.replace("'", "''") + "'";
    }
    sSQL += " ORDER BY ID_LOG";
    
    Set<String> setOfervicesExcluded = BEConfig.getHashSetProperty(BEConfig.sLJSA_CONF_SER_EXCLUDED);
    if(setOfervicesExcluded == null) setOfervicesExcluded = new HashSet<String>();
    
    ResultSet rs  = null;
    Statement stm = null;
    try {
      stm = conn.createStatement();
      psLog.println("      " + sSQL);
      rs  = stm.executeQuery(sSQL);
      while(rs.next()) {
        int iIdLog         = rs.getInt("ID_LOG");
        String sIdServizio = rs.getString("ID_SERVIZIO");
        String sIdAttivita = rs.getString("ID_ATTIVITA");
        int iDataInizio    = rs.getInt("DATA_INIZIO");
        int iOraInizio     = rs.getInt("ORA_INIZIO");
        psLog.print("      " + iIdLog + "," + sIdServizio + "," + sIdAttivita + "," + iDataInizio + "," + iOraInizio);
        if(setOfervicesExcluded.contains(sIdServizio)) {
          psLog.println(" excluded");
          continue;
        }
        psLog.println("");
        listResult.add(iIdLog);
      }
    }
    finally {
      ConnectionManager.close(rs, stm);
    }
    Object oFirst = null;
    Object oLast  = null;
    if(listResult.size() > 0) {
      int iLast = listResult.size() - 1;
      oFirst = listResult.get(0);
      oLast  = listResult.get(iLast);
      psLog.println("   getListIdLog(" + oLastIdDeleted + ") -> " + listResult.size() + " [0]=" + oFirst + ",[" + iLast + "]=" + oLast);
    }
    else {
      psLog.println("   getListIdLog(" + oLastIdDeleted + ") -> " + listResult.size());
    }
    return listResult;
  }
  
  protected
  void deleteLog(Connection conn, int iIdLog)
      throws Exception
  {
    String sSQL;
    
    psLog.println("   deleteLog(" + iIdLog + ")...");
    PreparedStatement pstmDel = null;
    try {
      psLog.println("      getIdSchedulazioneToDelete(" + iIdLog + ")...");
      int iIdSchedulazione = getIdSchedulazioneToDelete(conn, iIdLog);
      psLog.println("      iIdSchedulazione = " + iIdSchedulazione);
      
      sSQL = "DELETE FROM LJSA_LOG_FILES WHERE ID_LOG=?";
      pstmDel = conn.prepareStatement(sSQL);
      pstmDel.setInt(1, iIdLog);
      psLog.println("      DELETE FROM LJSA_LOG_FILES WHERE ID_LOG=" + iIdLog + " ...");
      pstmDel.executeUpdate();
      
      pstmDel.close();
      sSQL = "DELETE FROM LJSA_LOG WHERE ID_LOG=?";
      pstmDel = conn.prepareStatement(sSQL);
      pstmDel.setInt(1, iIdLog);
      psLog.println("      DELETE FROM LJSA_LOG WHERE ID_LOG=" + iIdLog + " ...");
      pstmDel.executeUpdate();
      
      if(iIdSchedulazione != 0) {
        pstmDel.close();
        sSQL = "DELETE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=?";
        pstmDel = conn.prepareStatement(sSQL);
        pstmDel.setInt(1, iIdSchedulazione);
        psLog.println("      DELETE FROM LJSA_SCHEDULAZIONI_CONF WHERE ID_SCHEDULAZIONE=" + iIdSchedulazione + " ...");
        pstmDel.executeUpdate();
        
        pstmDel.close();
        sSQL = "DELETE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=?";
        pstmDel = conn.prepareStatement(sSQL);
        pstmDel.setInt(1, iIdSchedulazione);
        psLog.println("      DELETE FROM LJSA_SCHEDULAZIONI_PARAMETRI WHERE ID_SCHEDULAZIONE=" + iIdSchedulazione + " ...");
        pstmDel.executeUpdate();
        
        pstmDel.close();
        sSQL = "DELETE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=?";
        pstmDel = conn.prepareStatement(sSQL);
        pstmDel.setInt(1, iIdSchedulazione);
        psLog.println("      DELETE FROM LJSA_SCHEDULAZIONI_NOTIFICA WHERE ID_SCHEDULAZIONE=" + iIdSchedulazione + " ...");
        pstmDel.executeUpdate();
        
        pstmDel.close();
        sSQL = "DELETE FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=?";
        pstmDel = conn.prepareStatement(sSQL);
        pstmDel.setInt(1, iIdSchedulazione);
        psLog.println("      DELETE FROM LJSA_SCHEDULAZIONI WHERE ID_SCHEDULAZIONE=" + iIdSchedulazione + " ...");
        pstmDel.executeUpdate();
      }
    }
    finally {
      if(pstmDel != null) try{ pstmDel.close(); } catch(Exception ex) {}
    }
  }
  
  protected
  int getIdSchedulazioneToDelete(Connection conn, int iIdLog)
      throws Exception
  {
    int iIdSchedulazione = 0;
    ResultSet rs = null;
    PreparedStatement pstm = null;
    try {
      String sSQL = "SELECT S.ID_SCHEDULAZIONE,S.STATO ";
      sSQL += "FROM LJSA_LOG L,LJSA_SCHEDULAZIONI S ";
      sSQL += "WHERE L.ID_SCHEDULAZIONE=S.ID_SCHEDULAZIONE AND L.ID_LOG=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, iIdLog);
      rs = pstm.executeQuery();
      
      String sStato = null;
      if(rs.next()) {
        iIdSchedulazione = rs.getInt("ID_SCHEDULAZIONE");
        sStato = rs.getString("STATO");
      }
      
      // Se la schedulazione non e' disattiva non si elimina...
      if(sStato == null || !sStato.equals("D")) {
        return 0;
      }
      
      int iCountLog = 0;
      
      rs.close();
      pstm.close();
      pstm = conn.prepareStatement("SELECT COUNT(*) FROM LJSA_LOG WHERE ID_SCHEDULAZIONE=?");
      pstm.setInt(1, iIdSchedulazione);
      rs = pstm.executeQuery();
      if(rs.next()) iCountLog = rs.getInt(1);
      
      // Se vi sono piu' log per la stessa schedulazione non si elimina...
      if(iCountLog > 1) return 0;
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    
    return iIdSchedulazione;
  }
  
  protected
  boolean deleteFiles(File file)
  {
    psLog.println("   deleteFiles(" + file.getAbsolutePath() + ")...");
    boolean boResult = true;
    
    if(file.isFile()) {
      boResult = file.delete();
      if(!boResult) psUndeletedFiles.println(WUtil.formatDateTime(Calendar.getInstance(), "-", true) + " " + file.getAbsolutePath());
      psLog.println("   deleteFiles(" + file.getAbsolutePath() + ") -> " + boResult);
      return boResult;
    }
    else {
      File files[] = file.listFiles();
      for(int i = 0; i < files.length; i++) {
        File f = files[i];
        if(f.isDirectory()) {
          boResult = boResult && deleteFiles(f);
        }
        else {
          boolean boDelFile = f.delete();
          if(!boDelFile) psUndeletedFiles.println(WUtil.formatDateTime(Calendar.getInstance(), "-", true) + " " + f.getAbsolutePath());
          psLog.println("      delete " + f.getAbsolutePath() + " -> " + boDelFile);
          boResult = boResult && boDelFile;
        }
      }
      boResult = boResult && file.delete();
    }
    if(!boResult) psUndeletedFiles.println(WUtil.formatDateTime(Calendar.getInstance(), "-", true) + " " + file.getAbsolutePath());
    psLog.println("   deleteFiles(" + file.getAbsolutePath() + ") -> " + boResult);
    return boResult;
  }
  
  protected
  Integer loadLastIdDeleted()
  {
    psLog.println("   loadLastIdDeleted()...");
    String sUserHome = System.getProperty("user.home");
    String sPathFile = sUserHome + File.separator + "cfg" + File.separator + sFILE_LAST_ID_DELETED;
    File fileLastIdDeleted = new File(sPathFile);
    if(!fileLastIdDeleted.exists()) {
      return null;
    }
    String sLine = null;
    FileInputStream fis = null;
    BufferedReader br = null;
    try {
      fis = new FileInputStream(sPathFile);
      br = new BufferedReader(new InputStreamReader(fis));
      while((sLine = br.readLine()) != null) {
        if(sLine.length() == 0) continue;
        break;
      }
    }
    catch(Exception ex) {
      System.err.println("Exception in JobClean.loadLastIdDeleted: " + ex);
      return null;
    }
    finally {
      if(fis != null) try{ fis.close(); } catch(Exception ex) {};
    }
    if(sLine == null) return null;
    Integer oResult = null;
    try{
      oResult = new Integer(sLine.trim());
    }
    catch(Exception ex) {
      System.err.println("Exception in JobClean.loadLastIdDeleted: " + ex);
      return null;
    }
    psLog.println("   loadLastIdDeleted() -> " + oResult);
    return oResult;
  }
  
  protected
  void saveLastIdDeleted(Integer oLastIdDeleted)
  {
    psLog.println("   saveLastIdDeleted(" + oLastIdDeleted + ")...");
    if(oLastIdDeleted == null) return;
    String sUserHome = System.getProperty("user.home");
    String sPathFile = sUserHome + File.separator + "cfg" + File.separator + sFILE_LAST_ID_DELETED;
    FileOutputStream fos = null;
    PrintStream ps = null;
    try {
      fos = new FileOutputStream(sPathFile);
      ps = new PrintStream(fos);
      ps.println(oLastIdDeleted.intValue());
    }
    catch(Exception ex) {
      System.err.println("Exception in JobClean.saveLastIdDeleted: " + ex);
    }
    finally {
      if(fos != null) try{ fos.close(); } catch(Exception ex) {};
    }
  }
}
