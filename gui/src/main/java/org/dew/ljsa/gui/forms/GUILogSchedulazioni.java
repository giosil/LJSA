package org.dew.ljsa.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.dew.ljsa.ILog;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.gui.AppUtil;
import org.dew.ljsa.gui.DecodifiableFactory;
import org.dew.ljsa.gui.LJSADecodeListener;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.IConstants;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.User;
import org.dew.swingup.components.ADecodifiableComponent;
import org.dew.swingup.components.IDecodeListener;
import org.dew.swingup.components.JBigCalendar;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUILogSchedulazioni extends JPanel implements ActionListener, ISchedulazione
{
  private static final long serialVersionUID = 5723776219976573965L;
  
  protected ADecodifiableComponent oDCFiltroServizio;
  protected ADecodifiableComponent oDCFiltroAttivita;
  protected LJSADecodeListener oFiltroAttivitaDecodeListener;
  protected FormPanel fpFilter;
  protected JBigCalendar jbigCalendar;
  
  protected JButton btnShow;
  protected JButton btnReset;
  protected JButton btnNextMonth;
  protected JButton btnPrevMonth;
  
  protected SimpleTableModelForSorter oTableModel_Log;
  protected JTable oTable_Log;
  protected List<Map<String, Object>> oRecords_Log = new ArrayList<Map<String, Object>>();
  
  protected ATableModelForSorter oTableModel_LogFiles;
  protected JTable oTable_LogFiles;
  protected List<Map<String, Object>> oRecords_LogFiles = new ArrayList<Map<String, Object>>();
  
  public
  GUILogSchedulazioni()
  {
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUILogSchedulazioni", ex);
    }
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    
    try {
      this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      
      if(sActionCommand.equals("show")) {
        doShow();
      }
      else if(sActionCommand.equals("reset")) {
        doReset();
      }
      else if(sActionCommand.equals("prev")) {
        jbigCalendar.prevMonth();
      }
      else if(sActionCommand.equals("next")) {
        jbigCalendar.nextMonth();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    finally {
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  
  protected
  void doShow()
      throws Exception
  {
    String sCheckMandatory = fpFilter.getStringCheckMandatories();
    if(sCheckMandatory.length() > 0) {
      GUIMessage.showWarning("Occorre valorizzare i seguenti campi:\n" + sCheckMandatory);
      return;
    }
    
    Map<String, Object> oFilterValues = fpFilter.getValues();
    String sIdServizio = (String) oFilterValues.get(sID_SERVIZIO);
    String sIdAttivita = (String) oFilterValues.get(sID_ATTIVITA);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdAttivita);
    
    List<List<Object>> oResult = WUtil.toListOfListObject(oRPCClient.execute("SCHEDULAZIONI.readLogCalendar", parameters, true));
    
    Calendar cal = new GregorianCalendar();
    int iStartYear = cal.get(Calendar.YEAR);
    int iStartMonth = cal.get(Calendar.MONTH);
    if(iStartMonth == 0) {
      iStartMonth = 12;
      iStartYear--;
    }
    
    jbigCalendar.setStartMonth(iStartMonth, iStartYear);
    jbigCalendar.setEnabled(true);
    jbigCalendar.clear();
    for(int i = 0; i < oResult.size(); i++) {
      List<Object> oRecord = oResult.get(i);
      Date date  = WUtil.toDate(oRecord.get(0), null);
      int  stato = WUtil.toInt(oRecord.get(1), 0);
      jbigCalendar.setBGColor(date, getColor(stato));
    }
    btnPrevMonth.setEnabled(true);
    btnNextMonth.setEnabled(true);
    
    if(oResult.size() == 1) {
      ResourcesMgr.getStatusBar().setText("1 giornata con log di esecuzione trovata.");
    }
    else {
      ResourcesMgr.getStatusBar().setText(oResult.size() + " giornate con log di esecuzione trovate.");
    }
  }
  
  protected
  void readLog(List<Date> listOfDate)
      throws Exception
  {
    if(listOfDate == null || listOfDate.size() == 0) {
      oRecords_Log = new ArrayList<Map<String, Object>>();
      oTableModel_Log.setData(oRecords_Log);
      TableSorter.resetHeader(oTable_Log);
      
      oRecords_LogFiles = new ArrayList<Map<String, Object>>();
      oTableModel_LogFiles.setData(oRecords_LogFiles);
      TableSorter.resetHeader(oTable_LogFiles);
      return;
    }
    
    Map<String, Object> oFilterValues = fpFilter.getValues();
    String sIdServizio = (String) oFilterValues.get(sID_SERVIZIO);
    String sIdAttivita = (String) oFilterValues.get(sID_ATTIVITA);
    if(sIdServizio == null || sIdServizio.length() == 0) {
      return;
    }
    if(sIdAttivita == null || sIdAttivita.length() == 0) {
      return;
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(sIdServizio);
    parameters.add(sIdAttivita);
    parameters.add(listOfDate);
    
    oRecords_Log = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULAZIONI.readLog", parameters, true));
    
    oTableModel_Log.setData(oRecords_Log);
    TableSorter.resetHeader(oTable_Log);
    
    oRecords_LogFiles = new ArrayList<Map<String, Object>>();
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
  }
  
  protected
  void readLogFiles()
  {
    int iRow = oTable_Log.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords_Log.size()) {
      return;
    }
    Map<String, Object> oRecord_Log = oRecords_Log.get(iRow);
    
    int idLog = WUtil.toInt(oRecord_Log.get(ILog.sID_LOG), 0);
    if(idLog == 0) return;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(idLog);
    try {
      oRecords_LogFiles = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULAZIONI.readLogFiles", parameters, false));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura dei file di log", ex);
      return;
    }
    
    if(oRecords_LogFiles != null) {
      for(int i = 0; i < oRecords_LogFiles.size(); i++) {
        Map<String, Object> mapRecord = oRecords_LogFiles.get(i);
        String sDescTipologia = GUISchedulazioni.getDescTipologia(mapRecord.get(ILog.sFILES_TIPOLOGIA));
        mapRecord.put(ILog.sFILES_TIPOLOGIA, sDescTipologia);
      }
    }
    
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
  }
  
  protected
  void doReset()
      throws Exception
  {
    oRecords_Log = new ArrayList<Map<String, Object>>();
    oTableModel_Log.setData(oRecords_Log);
    TableSorter.resetHeader(oTable_Log);
    
    oRecords_LogFiles = new ArrayList<Map<String, Object>>();
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
    
    jbigCalendar.clear();
    jbigCalendar.setEnabled(false);
    btnPrevMonth.setEnabled(false);
    btnNextMonth.setEnabled(false);
    
    fpFilter.reset();
    fpFilter.requestFocus();
  }
  
  protected
  Container buildFilterContainer()
  {
    oDCFiltroServizio = DecodifiableFactory.buildDCServizio();
    
    oDCFiltroAttivita = DecodifiableFactory.buildDCAttivita();
    oDCFiltroAttivita.addDecodeListener(oFiltroAttivitaDecodeListener = new LJSADecodeListener());
    oDCFiltroAttivita.setEnabled(false);
    
    oDCFiltroServizio.addDecodeListener(new IDecodeListener() {
      @Override
      public void reset() {
        oFiltroAttivitaDecodeListener.setIdServizio(null);
        oDCFiltroAttivita.reset();
        oDCFiltroAttivita.setEnabled(false);
      }
      
      @Override
      public void set() {
        oFiltroAttivitaDecodeListener.setIdServizio((String) oDCFiltroServizio.getKey());
        oDCFiltroAttivita.setEnabled(true);
      }
      
      @Override
      public void beforeFind(List<Object> list) {
      }
    });
    
    fpFilter = new FormPanel("Filtro");
    fpFilter.addRow();
    fpFilter.addComponent(sID_SERVIZIO, "Servizio", oDCFiltroServizio);
    fpFilter.addRow();
    fpFilter.addComponent(sID_ATTIVITA, "Attivit\340", oDCFiltroAttivita);
    fpFilter.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sID_SERVIZIO);
    oMandatoryFields.add(sID_ATTIVITA);
    
    fpFilter.setMandatoryFields(oMandatoryFields);
    
    fpFilter.setValue(sID_SERVIZIO, AppUtil.getDefaultServizio());
    
    return fpFilter;
  }
  
  protected
  Container buildButtonsContainer()
  {
    btnShow = GUIUtil.buildActionButton("&Mostra|Mostra |ListLarge.gif", "show");
    btnShow.addActionListener(this);
    
    btnReset = GUIUtil.buildActionButton(IConstants.sGUIDATA_RESET, "reset");
    btnReset.addActionListener(this);
    
    JPanel oButtonsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    oButtonsPanel.add(btnShow);
    oButtonsPanel.add(btnReset);
    
    JPanel oActionsPanel = new JPanel(new BorderLayout(4, 4));
    oActionsPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 8));
    oActionsPanel.add(oButtonsPanel, BorderLayout.NORTH);
    
    return oActionsPanel;
  }
  
  protected
  Container buildCalendarContainer()
  {
    Calendar cal = new GregorianCalendar();
    int iStartYear = cal.get(Calendar.YEAR);
    int iStartMonth = cal.get(Calendar.MONTH);
    if(iStartMonth == 0) {
      iStartMonth = 12;
      iStartYear--;
    }
    
    jbigCalendar = new JBigCalendar(2, iStartMonth, iStartYear);
    jbigCalendar.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        Date[] dates = jbigCalendar.getSelectedDays_Date();
        try {
          readLog(Arrays.asList(dates));
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante la lettura dei log", ex);
        }
      }
    });
    jbigCalendar.setEnabled(false);
    
    btnPrevMonth = GUIUtil.buildActionButton("&Precedente|Mese precedente|FingerLeftLarge.gif", "prev");
    btnPrevMonth.addActionListener(this);
    btnPrevMonth.setEnabled(false);
    
    btnNextMonth = GUIUtil.buildActionButton("&Successivo|Mese successivo|FingerRightLarge.gif", "next");
    btnNextMonth.addActionListener(this);
    btnNextMonth.setEnabled(false);
    
    JPanel oPrevAndNext = new JPanel(new GridLayout(1, 2, 2, 2));
    oPrevAndNext.add(btnPrevMonth);
    oPrevAndNext.add(btnNextMonth);
    
    JPanel oNorth = new JPanel(new BorderLayout());
    oNorth.add(oPrevAndNext, BorderLayout.EAST);
    
    JPanel oResult = new JPanel(new BorderLayout(4, 4));
    oResult.setBorder(BorderFactory.createTitledBorder("Calendario"));
    oResult.add(oNorth, BorderLayout.NORTH);
    oResult.add(jbigCalendar, BorderLayout.CENTER);
    
    return oResult;
  }
  
  protected
  Container buildLogTable()
  {
    String[] asCOLUMNS   = {"Id Log",     "Id Sched.",           "Rapporto",      "Data Inizio",     "Ora Inizio",     "Data Fine",    "Ora Fine",      "Stato"};
    String[] asSYMBOLICS = {ILog.sID_LOG, ILog.sID_SCHEDULAZIONE, ILog.sRAPPORTO, ILog.sDATA_INIZIO, ILog.sORA_INIZIO, ILog.sDATA_FINE, ILog.sORA_FINE, ILog.sSTATO};
    
    oTableModel_Log = new SimpleTableModelForSorter(oRecords_Log, asCOLUMNS, asSYMBOLICS);
    oTableModel_Log.addTimeField(ILog.sORA_INIZIO);
    oTableModel_Log.addTimeField(ILog.sORA_FINE);
    
    oTable_Log = new JTable(oTableModel_Log);
    oTable_Log.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable_Log.setColumnSelectionAllowed(false);
    oTable_Log.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    TableUtils.setMonospacedFont(oTable_Log);
    
    TableUtils.setLinkField(oTable_Log, 1, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String sActionCommand = e.getActionCommand();
        if(sActionCommand == null || sActionCommand.length() == 0) return;
        int iIdSchedulazione = Integer.parseInt(sActionCommand);
        try {
          viewSchedulazione(iIdSchedulazione);
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante la lettura della schedulazione", ex);
        }
      }
    });
    
    oTable_Log.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, focus, row, col);
        
        Map<String, Object> oRecord = oRecords_Log.get(row);
        
        String sStato = WUtil.toString(oRecord.get(ILog.sSTATO), "");
        
        if(sStato.equalsIgnoreCase("E")) {
          this.setForeground(Color.red);
          this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        }
        else
          if(sStato.equalsIgnoreCase("S")) {
            this.setForeground(Color.blue);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
          else {
            this.setForeground(Color.black);
            this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
          }
        
        return this;
      }
    });
    oTable_Log.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          try {
            viewLogReport();
          }
          catch(Exception ex) {
            GUIMessage.showException(ex);
          }
        }
      }
    });
    oTable_Log.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if(e.getValueIsAdjusting()) return;
        oTable_Log.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              readLogFiles();
            }
          });
        }
        finally {
          oTable_Log.setCursor(Cursor.getDefaultCursor());
        }
      }
    });
    
    JScrollPane oScrollPane = new JScrollPane(oTable_Log);
    
    TableColumnResizer.setResizeColumnsListeners(oTable_Log);
    TableSorter.setSorterListener(oTable_Log);
    
    oScrollPane.setBorder(BorderFactory.createTitledBorder("Esecuzioni"));
    
    return oScrollPane;
  }
  
  protected
  Container buildLogFilesTable()
  {
    String[] asCOLUMNS   = {"Tipologia",           "Nome Files",          "URL download"};
    String[] asSYMBOLICS = {ILog.sFILES_TIPOLOGIA, ILog.sFILES_NOME_FILE, ILog.sFILES_URL_FILE};
    
    oTableModel_LogFiles = new SimpleTableModelForSorter(oRecords_LogFiles, asCOLUMNS, asSYMBOLICS);
    
    oTable_LogFiles = new JTable(oTableModel_LogFiles);
    oTable_LogFiles.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable_LogFiles.setColumnSelectionAllowed(false);
    oTable_LogFiles.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    oTable_LogFiles.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          gotoHomeDownload();
        }
      }
    });
    
    TableUtils.setMonospacedFont(oTable_LogFiles);
    
    TableUtils.setLinkField(oTable_LogFiles, 2);
    
    JScrollPane oScrollPane = new JScrollPane(oTable_LogFiles);
    
    TableColumnResizer.setResizeColumnsListeners(oTable_LogFiles);
    TableSorter.setSorterListener(oTable_LogFiles);
    
    oScrollPane.setBorder(BorderFactory.createTitledBorder("Files"));
    
    return oScrollPane;
  }
  
  protected
  void gotoHomeDownload()
  {
    if(oRecords_LogFiles == null || oRecords_LogFiles.size() == 0) {
      return;
    }
    
    Map<String, Object> mapFirstFile = oRecords_LogFiles.get(0);
    String sURLFile = WUtil.toString(mapFirstFile.get(ILog.sFILES_URL_FILE), null);
    if(sURLFile == null || sURLFile.length() == 0) {
      return;
    }
    
    int iLastSlash = sURLFile.lastIndexOf('/');
    String sURLHome = null;
    if(iLastSlash < sURLFile.length() - 1) {
      sURLHome = sURLFile.substring(0, iLastSlash + 1);
    }
    else {
      sURLHome = sURLFile;
    }
    
    User user = ResourcesMgr.getSessionManager().getUser();
    
    Map<String, Object> mapResources = WUtil.toMapObject(user.getResources());
    String sAltHost = null;
    if(mapResources != null) {
      sAltHost = (String) mapResources.get("ljsa.host");
    }
    if(sAltHost != null && sAltHost.length() > 0) {
      int iSepCtx = sURLHome.indexOf('/', 7);
      if(iSepCtx > 0) {
        sURLHome = "http://" + sAltHost + sURLHome.substring(iSepCtx);
      }
    }
    
    try {
      ResourcesMgr.openBrowser(sURLHome);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'apertura della pagina di download", ex);
    }
  }
  
  protected
  void viewLogReport()
      throws Exception
  {
    int iRow = oTable_Log.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords_Log.size()) {
      return;
    }
    Map<String, Object> oRecord = oRecords_Log.get(iRow);
    String sLogReport = WUtil.toString(oRecord.get(ILog.sRAPPORTO), "");
    if(sLogReport != null && sLogReport.length() > 0) {
      jbigCalendar.setEnabled(false);
      ResourcesMgr.getGUIManager().showGUITextMessage(ResourcesMgr.mainFrame, sLogReport, "Rapporto");
      jbigCalendar.setEnabled(true);
    }
  }
  
  protected
  void viewSchedulazione(int idSchedulazione)
      throws Exception
  {
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Integer> parameters = new Vector<Integer>();
    parameters.add(idSchedulazione);
    
    Map<String, Object> mapSchedulazione = WUtil.toMapObject(oRPCClient.execute("SCHEDULAZIONI.read", parameters, false));
    if(mapSchedulazione == null || mapSchedulazione.isEmpty()) {
      return;
    }
    Date   oDataIns = WUtil.toDate(mapSchedulazione.get(sDATA_INS), null);
    String sDataIns = ResourcesMgr.getDefaultDateFormat().format(oDataIns);
    String sOraIns  = WUtil.formatTime(mapSchedulazione.get(sORA_INS), false, false);
    Date   oDataAgg = WUtil.toDate(mapSchedulazione.get(sDATA_AGG), null);
    String sDataAgg = ResourcesMgr.getDefaultDateFormat().format(oDataAgg);
    String sOraAgg  = WUtil.formatTime(mapSchedulazione.get(sORA_AGG), false, false);
    String sReport  = "Dati schedulazione:\n\n";
    sReport += "Descrizione   : " + mapSchedulazione.get(sDESCRIZIONE) + "\n";
    sReport += "Schedulazione : " + mapSchedulazione.get(sSCHEDULAZIONE) + "\n";
    sReport += "Credenz. Ins. : " + mapSchedulazione.get(sID_CREDENZIALE_INS) + "\n";
    sReport += "Data di  Ins. : " + sDataIns + "\n";
    sReport += "Ora  di  Ins. : " + sOraIns + "\n";
    sReport += "Credenz. Agg. : " + mapSchedulazione.get(sID_CREDENZIALE_AGG) + "\n";
    sReport += "Data di  Agg. : " + sDataAgg + "\n";
    sReport += "Ora  di  Agg. : " + sOraAgg + "\n";
    
    List<Map<String, Object>> listConfigurazione = WUtil.toListOfMapObject(mapSchedulazione.get(sCONFIGURAZIONE));
    if(listConfigurazione != null && listConfigurazione.size() > 0) {
      int iMaxLength = 0;
      for(int i = 0; i < listConfigurazione.size(); i++) {
        Map<String, Object> map = listConfigurazione.get(i);
        String sOpzione = (String) map.get(sCONF_OPZIONE);
        if(sOpzione.length() > iMaxLength) {
          iMaxLength = sOpzione.length();
        }
      }
      sReport += "\nConfigurazione:\n\n";
      for(int i = 0; i < listConfigurazione.size(); i++) {
        Map<String, Object> map = listConfigurazione.get(i);
        Boolean oDaAttivita = WUtil.toBooleanObj(map.get(sCONF_DA_ATTIVITA), false);
        Boolean oOverWrite  = WUtil.toBooleanObj(map.get(sCONF_OVERWRITE),   false);
        String sNota = "";
        if(oDaAttivita != null && oDaAttivita.booleanValue()) {
          if(oOverWrite != null && oOverWrite.booleanValue()) {
            sNota = " (**)";
          }
          else {
            sNota = " (*)";
          }
        }
        String sOpzione = WUtil.rpad((String) map.get(sCONF_OPZIONE), ' ', iMaxLength);
        Object oVal = map.get(sCONF_VALORE);
        if(oVal == null) oVal = "";
        sReport += sOpzione + " = " + oVal + sNota + "\n";
      }
    }
    List<Map<String, Object>> listParametri = WUtil.toListOfMapObject(mapSchedulazione.get(sPARAMETRI));
    if(listParametri != null && listParametri.size() > 0) {
      int iMaxLength = 0;
      for(int i = 0; i < listParametri.size(); i++) {
        Map<String, Object> map = listParametri.get(i);
        String sParametro = (String) map.get(sPAR_PARAMETRO);
        if(sParametro.length() > iMaxLength) {
          iMaxLength = sParametro.length();
        }
      }
      sReport += "\nParametri:\n\n";
      for(int i = 0; i < listParametri.size(); i++) {
        Map<String, Object> map = listParametri.get(i);
        Boolean oDaAttivita = WUtil.toBooleanObj(map.get(sPAR_DA_ATTIVITA), null);
        Boolean oOverWrite  = WUtil.toBooleanObj(map.get(sPAR_OVERWRITE), null);
        String sNota = "";
        if(oDaAttivita != null && oDaAttivita.booleanValue()) {
          if(oOverWrite != null && oOverWrite.booleanValue()) {
            sNota = " (**)";
          }
          else {
            sNota = " (*)";
          }
        }
        String sParametro = WUtil.rpad((String) map.get(sPAR_PARAMETRO), ' ', iMaxLength);
        Object oVal = map.get(sPAR_VALORE);
        if(oVal == null) oVal = "";
        sReport += sParametro + " = " + oVal + sNota + "\n";
      }
    }
    List<Map<String, Object>> listNotifica = WUtil.toListOfMapObject(mapSchedulazione.get(sNOTIFICA));
    if(listParametri != null && listParametri.size() > 0) {
      sReport += "\nNotifica:\n\n";
      for(int i = 0; i < listNotifica.size(); i++) {
        Map<String, Object> map = listNotifica.get(i);
        Boolean oCancellata = WUtil.toBooleanObj(map.get(sNOT_CANCELLATA), null);
        if(oCancellata != null && oCancellata.booleanValue()) {
          continue;
        }
        Boolean oDaAttivita = WUtil.toBooleanObj(map.get(sNOT_DA_ATTIVITA), null);
        String sDaAttivita = "";
        if(oDaAttivita != null && oDaAttivita.booleanValue()) {
          sDaAttivita = " (*)";
        }
        sReport += map.get(sNOT_EVENTO) + " -> " + map.get(sNOT_DESTINAZIONE) + sDaAttivita + "\n";
      }
    }
    sReport += "\n";
    sReport += " (*) Valore predefinito.\n";
    sReport += "(**) Presente nell'attivit\340 e ridefinito.\n";
    
    jbigCalendar.setEnabled(false);
    ResourcesMgr.getGUIManager().showGUITextMessage(ResourcesMgr.mainFrame, sReport, "Schedulazione " + idSchedulazione);
    jbigCalendar.setEnabled(true);
  }
  
  protected
  Color getColor(int stato)
  {
    switch(stato) {
    case 0: return Color.gray;
    case 1: return Color.green;
    case 2: return Color.red;
    case 3: return Color.yellow;
    case 4: return Color.cyan;
    }
    return Color.gray;
  }
  
  protected
  void init()
      throws Exception
  {
    this.setLayout(new BorderLayout());
    
    JPanel oNorthPanel = new JPanel(new BorderLayout(4, 4));
    oNorthPanel.add(buildFilterContainer(),  BorderLayout.CENTER);
    oNorthPanel.add(buildButtonsContainer(), BorderLayout.EAST);
    
    JPanel oSouthPanel = new JPanel(new GridLayout(1, 2, 4, 4));
    oSouthPanel.add(buildLogTable());
    oSouthPanel.add(buildLogFilesTable());
    oSouthPanel.setPreferredSize(new Dimension(0, 150));
    
    this.add(oNorthPanel,              BorderLayout.NORTH);
    this.add(buildCalendarContainer(), BorderLayout.CENTER);
    this.add(oSouthPanel,              BorderLayout.SOUTH);
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        fpFilter.requestFocus();
      }
    });
  }
}
