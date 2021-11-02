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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.rpc.DataNormalizer;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.CodeAndDescription;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUISchedulazioni extends AEntityEditor implements ISchedulazione
{
  private static final long serialVersionUID = -5324027859485115326L;
  
  protected SimpleTableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords = new ArrayList<Map<String, Object>>();
  protected Map<String, Object> oLastRecordReaded;
  protected int iLastTableIndex = 0;
  
  protected SimpleTableModelForSorter oTableModel_Log;
  protected JTable oTable_Log;
  protected List<Map<String, Object>> oRecords_Log = new ArrayList<Map<String, Object>>();
  
  protected ATableModelForSorter oTableModel_LogFiles;
  protected JTable oTable_LogFiles;
  protected List<Map<String, Object>> oRecords_LogFiles = new ArrayList<Map<String, Object>>();
  
  protected LJSADecodeListener oFiltroAttivitaDecodeListener;
  protected LJSADecodeListener oAttivitaDecodeListener;
  protected LJSADecodeListener oCredenzialeDecodeListener;
  
  protected ADecodifiableComponent oDCFiltroServizio;
  protected ADecodifiableComponent oDCServizio;
  protected ADecodifiableComponent oDCFiltroAttivita;
  protected ADecodifiableComponent oDCAttivita;
  protected ADecodifiableComponent oDCCredenziale;
  
  protected DPSchedConfigurazione oDPConfigurazione;
  protected DPSchedParametri      oDPParametri;
  protected DPSchedNotifica       oDPNotifica;
  
  protected JTabbedPane jtpResult;
  protected JButton btnSchedRefresh;
  protected JButton btnLogRefresh;
  protected JButton btnGoToHomeDownload;
  protected int iIdSchedulazioneReadLog = 0;
  protected int iMaxLogRecords = 50;
  protected boolean boDontRead = false;
  
  protected JButton btnCopyFrom;
  protected boolean boDontReadInfoAttivita = false;
  
  public
  GUISchedulazioni()
  {
    super();
  }
  
  public
  void onOpened()
  {
    super.onOpened();
    
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    fpFilter.setValue(sID_SERVIZIO, AppUtil.getDefaultServizio());
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    super.actionPerformed(e);
    
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    
    if(sActionCommand.equals("log_refresh")) {
      readLog(true);
    }
    else if(sActionCommand.equals("sched_refresh")) {
      refreshSchedulazione();
    }
    else if(sActionCommand.equals("goto_home_download")) {
      gotoHomeDownload();
    }
    else if(sActionCommand.equals("copyFrom")) {
      doCopyFrom();
    }
  }
  
  protected
  void doCopyFrom()
  {
    if(oLastRecordReaded == null || oLastRecordReaded.isEmpty()) {
      GUIMessage.showWarning("Informazioni non sufficienti per eseguire l'operazione");
      return;
    }
    Map<String, Object> mapCopy = new HashMap<String, Object>(oLastRecordReaded);
    mapCopy.remove(sID_SCHEDULAZIONE);
    mapCopy.remove(sSTATO);
    mapCopy.remove(sID_CREDENZIALE_INS);
    mapCopy.remove(sDATA_INSERIMENTO);
    mapCopy.remove(sORA_INSERIMENTO);
    mapCopy.remove(sESECUZIONI_COMPLETATE);
    mapCopy.remove(sESECUZIONI_INTERROTTE);
    Integer oIdSchedulazione = (Integer) oLastRecordReaded.get(sID_SCHEDULAZIONE);
    String sMsgConfirm = "La nuova schedulazione avr\340 le stesse impostazioni della " + oIdSchedulazione + ". Procedere?";
    boolean boConfirm  = GUIMessage.getConfirmation(sMsgConfirm);
    if(!boConfirm) return;
    try {
      // Si attiva la creazione di un nuovo record
      fireNew();
      // Si riportano i valori ottenuti dall'ultima lettura
      // Si disabilita la lettura delle informazioni dell'attivita' derivante
      // dall'impostazione dell'attivita' stessa.
      boDontReadInfoAttivita = true;
      FormPanel fpDetail = (FormPanel) getDetailContainer();
      fpDetail.setValues(mapCopy);
      // Si rimuovono i valori specifici
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          FormPanel fpDetail = (FormPanel) getDetailContainer();
          fpDetail.requestFocus(sDESCRIZIONE);
        }
      });
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la copia da", ex);
    }
  }
  
  public
  Object getCurrentSelection()
      throws Exception
  {
    return oLastRecordReaded;
  }
  
  protected
  Container buildGUIFilter()
  {
    oDCFiltroServizio = DecodifiableFactory.buildDCServizio();
    
    oDCFiltroAttivita = DecodifiableFactory.buildDCAttivita();
    oDCFiltroAttivita.addDecodeListener(oFiltroAttivitaDecodeListener = new LJSADecodeListener());
    oDCFiltroAttivita.setEnabled(false);
    
    oDCCredenziale = DecodifiableFactory.buildDCComboCredenziale();
    oDCCredenziale.addDecodeListener(oCredenzialeDecodeListener = new LJSADecodeListener());
    oDCCredenziale.setEnabled(false);
    
    oDCFiltroServizio.addDecodeListener(new IDecodeListener() {
      @Override
      public void reset() {
        oFiltroAttivitaDecodeListener.setIdServizio(null);
        oDCFiltroAttivita.reset();
        oDCFiltroAttivita.setEnabled(false);
        
        oCredenzialeDecodeListener.setIdServizio(null);
        oDCCredenziale.reset();
        oDCCredenziale.setEnabled(false);
      }
      
      @Override
      public void set() {
        oFiltroAttivitaDecodeListener.setIdServizio((String) oDCFiltroServizio.getKey());
        oDCFiltroAttivita.setEnabled(getFilterContainer().isEnabled());
        
        oCredenzialeDecodeListener.setIdServizio((String) oDCFiltroServizio.getKey());
        oDCCredenziale.setEnabled(getFilterContainer().isEnabled());
      }
      
      @Override
      public void beforeFind(List<Object> list) {
      }
    });
    
    Vector<CodeAndDescription> vStati = new Vector<CodeAndDescription>();
    vStati.add(new CodeAndDescription(null, ""));
    vStati.add(new CodeAndDescription("A", "(A) Attiva"));
    vStati.add(new CodeAndDescription("D", "(D) Disattivata"));
    vStati.add(new CodeAndDescription("E", "(E) In Esecuzione"));
    
    FormPanel fp = new FormPanel("Ricerca");
    fp.addTab("Schedulazione");
    fp.addRow();
    fp.addComponent(sID_SERVIZIO,   "Servizio", oDCFiltroServizio);
    fp.addComponent(sID_ATTIVITA,   "Attivit\340", oDCFiltroAttivita);
    fp.addRow();
    fp.addComponent(sID_CREDENZIALE_INS, "Cred. Ins.", oDCCredenziale);
    fp.addOptionsField(sSTATO, "Stato", vStati);
    fp.addRow();
    fp.addDateField(sINIZIO_VALIDITA, "Inizio val.", null);
    fp.addDateField(sFINE_VALIDITA,   "Fine val.", null);
    fp.build();
    
    return fp;
  }
  
  protected
  Container buildGUIDetail()
  {
    oDPConfigurazione = new DPSchedConfigurazione();
    oDPConfigurazione.setPreferredSize(new Dimension(0, 120));
    
    oDPParametri = new DPSchedParametri();
    oDPParametri.setPreferredSize(new Dimension(0, 120));
    
    oDPNotifica = new DPSchedNotifica();
    oDPNotifica.setPreferredSize(new Dimension(0, 120));
    
    oDCServizio = DecodifiableFactory.buildDCServizio();
    
    oDCAttivita = DecodifiableFactory.buildDCAttivita();
    oDCAttivita.addDecodeListener(oAttivitaDecodeListener = new LJSADecodeListener());
    oDCAttivita.setEnabled(false);
    
    oDCServizio.addDecodeListener(new IDecodeListener() {
      @Override
      public void reset() {
        oAttivitaDecodeListener.setIdServizio(null);
        oDCAttivita.reset();
        oDCAttivita.setEnabled(false);
      }
      
      @Override
      public void set() {
        oAttivitaDecodeListener.setIdServizio((String) oDCServizio.getKey());
        oDCAttivita.setEnabled(getDetailContainer().isEnabled());
      }
      
      @Override
      public void beforeFind(List<Object> list) {
      }
    });
    oDCAttivita.addDecodeListener(new IDecodeListener() {
      @Override
      public void reset() {
        FormPanel fpDetail = (FormPanel) getDetailContainer();
        if(fpDetail == null) return;
        fpDetail.setValue(sCONFIGURAZIONE, null);
        fpDetail.setValue(sPARAMETRI, null);
        fpDetail.setValue(sNOTIFICA, null);
      }
      
      @Override
      public void set() {
        readInfoAttivita();
      }
      
      @Override
      @SuppressWarnings("rawtypes")
      public void beforeFind(List list) {
      }
    });
    
    FormPanel fp = new FormPanel("Dettaglio");
    fp.addTab("Attributi");
    fp.addRow();
    fp.addBlankField();
    fp.addRow();
    fp.addComponent(sID_SERVIZIO,   "Servizio", oDCServizio);
    fp.addRow();
    fp.addComponent(sID_ATTIVITA,   "Attivit\340", oDCAttivita);
    fp.addRow();
    fp.addTextField(sDESCRIZIONE,   "Descrizione", 255);
    fp.addTextField(sSCHEDULAZIONE, "Schedulazione", 50);
    fp.addRow();
    fp.addDateField(sINIZIO_VALIDITA, "Inizio val.", null);
    fp.addDateField(sFINE_VALIDITA,   "Fine val.", null);
    fp.addTab("Configurazione");
    fp.addRow();
    fp.addDataPanel(sCONFIGURAZIONE, oDPConfigurazione);
    fp.addTab("Parametri");
    fp.addRow();
    fp.addDataPanel(sPARAMETRI, oDPParametri);
    fp.addTab("Notifica");
    fp.addRow();
    fp.addDataPanel(sNOTIFICA, oDPNotifica);
    fp.addHiddenField(sID_SCHEDULAZIONE);
    fp.addHiddenField(sSTATO);
    fp.addHiddenField(sID_CREDENZIALE_INS);
    fp.addHiddenField(sDATA_INSERIMENTO);
    fp.addHiddenField(sORA_INSERIMENTO);
    fp.addHiddenField(sESECUZIONI_COMPLETATE);
    fp.addHiddenField(sESECUZIONI_INTERROTTE);
    fp.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sID_SERVIZIO);
    oMandatoryFields.add(sID_ATTIVITA);
    oMandatoryFields.add(sDESCRIZIONE);
    fp.setMandatoryFields(oMandatoryFields);
    
    fp.setHelpText(sSCHEDULAZIONE, getHelpTextSchedulazione());
    
    return fp;
  }
  
  protected
  void readInfoAttivita()
  {
    if(boDontReadInfoAttivita) {
      boDontReadInfoAttivita = false;
      return;
    }
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    if(fpDetail == null || !fpDetail.isEnabled()) return;
    
    String sIdServizio = (String) fpDetail.getValue(sID_SERVIZIO);
    String sIdAttivita = (String) fpDetail.getValue(sID_ATTIVITA);
    if(sIdServizio == null || sIdAttivita == null) return;
    
    Map<String, Object> mapAttivita = null;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdAttivita);
    try {
      mapAttivita = WUtil.toMapObject(oRPCClient.execute("SCHEDULAZIONI.readInfoAttivita", parameters, true));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura dell'attivit\340", ex);
    }
    
    if(mapAttivita == null || mapAttivita.isEmpty()) {
      return;
    }
    
    fpDetail.setValues(mapAttivita);
    
    if(fpDetail.isBlank(sDESCRIZIONE)) {
      fpDetail.setValue(sDESCRIZIONE, sIdServizio + ":" + sIdAttivita);
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
    String sLogReport = (String) oRecord.get(ILog.sRAPPORTO);
    if(sLogReport != null && sLogReport.length() > 0) {
      ResourcesMgr.getGUIManager().showGUITextMessage(ResourcesMgr.mainFrame, sLogReport, "Rapporto");
    }
  }
  
  protected
  void readLog(boolean boRefresh)
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return;
    }
    Map<String, Object> oRecord = oRecords.get(iRow);
    
    int idSchedulazione = WUtil.toInt(oRecord.get(sID_SCHEDULAZIONE), 0);
    
    btnLogRefresh.setEnabled(true);
    btnGoToHomeDownload.setEnabled(oTable_Log.getSelectedRow() >= 0);
    
    if(!boRefresh) {
      if(idSchedulazione == iIdSchedulazioneReadLog) return;
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Integer> parameters = new ArrayList<Integer>();
    parameters.add(idSchedulazione);
    parameters.add(iMaxLogRecords); // Massimo 50 record
    try {
      oRecords_Log = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULAZIONI.readLog", parameters, true));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura dei log", ex);
      return;
    }
    
    iIdSchedulazioneReadLog = idSchedulazione;
    
    oTableModel_Log.setData(oRecords_Log);
    TableSorter.resetHeader(oTable_Log);
    
    oRecords_LogFiles = new ArrayList<Map<String, Object>>();
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
    
    btnGoToHomeDownload.setEnabled(oTable_Log.getSelectedRow() >= 0);
  }
  
  protected
  void refreshSchedulazione()
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return;
    }
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    
    readExecInfo(oRecord);
    
    oTableModel.notifyUpdates();
    
    boDontRead = true;
    oTable.setRowSelectionInterval(iRow, iRow);
  }
  
  protected
  void readExecInfo(Map<String, Object> oRecord)
  {
    int idSchedulazione = WUtil.toInt(oRecord.get(sID_SCHEDULAZIONE), 0);
    
    // Eventuale normalizzazione
    Object oIdServizio = oRecord.get(sID_SERVIZIO);
    if(oIdServizio instanceof List) {
      oRecord.put(sID_SERVIZIO, ((List<?>) oIdServizio).get(0));
    }
    Object oIdAttivita = oRecord.get(sID_ATTIVITA);
    if(oIdAttivita instanceof List) {
      oRecord.put(sID_ATTIVITA, ((List<?>) oIdAttivita).get(0));
    }
    
    List<Object> listInfo = null;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Integer> parameters = new ArrayList<Integer>();
    parameters.add(idSchedulazione);
    try {
      listInfo = WUtil.toList(oRPCClient.execute("SCHEDULAZIONI.readExecInfo", parameters, false), Object.class, null);
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante il controllo di esecuzione della schedulazione", ex);
      return;
    }
    
    if(listInfo == null || listInfo.size() < 3) return;
    
    oRecord.put(sSTATO,                 listInfo.get(0));
    oRecord.put(sESECUZIONI_COMPLETATE, listInfo.get(1));
    oRecord.put(sESECUZIONI_INTERROTTE, listInfo.get(2));
    
    // In questo modo si obbliga il refresh quando si passa al tab dei log
    iIdSchedulazioneReadLog = 0;
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
    List<Integer> vParameters = new ArrayList<Integer>();
    vParameters.add(idLog);
    try {
      oRecords_LogFiles = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULAZIONI.readLogFiles", vParameters, false));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura dei file di log", ex);
      return;
    }
    
    if(oRecords_LogFiles != null) {
      for(int i = 0; i < oRecords_LogFiles.size(); i++) {
        Map<String, Object> mapRecord = oRecords_LogFiles.get(i);
        String sDescTipologia = getDescTipologia(mapRecord.get(ILog.sFILES_TIPOLOGIA));
        mapRecord.put(ILog.sFILES_TIPOLOGIA, sDescTipologia);
      }
      btnGoToHomeDownload.setEnabled(oRecords_LogFiles.size() > 0);
    }
    else {
      btnGoToHomeDownload.setEnabled(false);
    }
    
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
  }
  
  protected
  void gotoHomeDownload()
  {
    if(oRecords_LogFiles == null || oRecords_LogFiles.size() == 0) {
      return;
    }
    
    Map<String, Object> mapFirstFile = oRecords_LogFiles.get(0);
    String sURLFile = (String) mapFirstFile.get(ILog.sFILES_URL_FILE);
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
  Container buildGUIBigDetail()
  {
    return null;
  }
  
  protected
  Container buildGUIOtherDetail()
  {
    return null;
  }
  
  protected
  Container buildGUIResult()
  {
    JPanel oLogPanel = new JPanel(new GridLayout(2, 1, 4, 4));
    oLogPanel.add(buildLogTable());
    oLogPanel.add(buildLogFilesTable());
    
    jtpResult = new JTabbedPane();
    jtpResult.add("Schedulazioni", buildSchedulazioniTable());
    jtpResult.add("Log (Max " + iMaxLogRecords + " records)", oLogPanel);
    jtpResult.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int iSelectedIndex = jtpResult.getSelectedIndex();
        boolean boView = iEditorStatus == iSTATUS_VIEW;
        if(iSelectedIndex == 1) {
          if(boView) {
            if(btnToggle != null) btnToggle.setEnabled(false);
            if(btnNew    != null) btnNew.setEnabled(false);
            if(btnOpen   != null) btnOpen.setEnabled(false);
          }
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              readLog(false);
            }
          });
        }
        else {
          btnLogRefresh.setEnabled(false);
          btnGoToHomeDownload.setEnabled(false);
          if(boView) {
            if(btnToggle != null) btnToggle.setEnabled(true);
            if(btnNew    != null) btnNew.setEnabled(true);
            if(btnOpen   != null) btnOpen.setEnabled(true);
          }
        }
      }
    });
    
    return jtpResult;
  }
  
  protected
  Container buildSchedulazioniTable()
  {
    String[] asCOLUMNS   = {"Id",              "Servizio",   "Attivita",   "Schedulazione", "Stato", "Descrizione", "Esec. Compl.",         "Esec. Int.",           "Id Cred. Ins.",     "Data Ins.",       "Ora Ins.",       "Id Cred. Agg.",     "Data Agg.",         "Ora Agg."};
    String[] asSYMBOLICS = {sID_SCHEDULAZIONE, sID_SERVIZIO, sID_ATTIVITA, sSCHEDULAZIONE,  sSTATO,  sDESCRIZIONE,  sESECUZIONI_COMPLETATE, sESECUZIONI_INTERROTTE, sID_CREDENZIALE_INS, sDATA_INSERIMENTO, sORA_INSERIMENTO, sID_CREDENZIALE_AGG, sDATA_AGGIORNAMENTO, sORA_AGGIORNAMENTO};
    
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    oTableModel.addTimeField(sORA_INSERIMENTO);
    oTableModel.addTimeField(sORA_AGGIORNAMENTO);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    oTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, focus, row, col);
        
        Map<String, Object> record = oRecords.get(row);
        String sStato = WUtil.toString(record.get(sSTATO), "");
        if(sStato.equalsIgnoreCase(sSTATO_DISATTIVATA)) {
          this.setForeground(Color.gray);
          this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
        }
        else if(sStato.equalsIgnoreCase(sSTATO_IN_ESECUZIONE)) {
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
    
    TableUtils.setMonospacedFont(oTable);
    
    oTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          try {
            fireSelect();
          }
          catch(Exception ex) {
            GUIMessage.showException(ex);
          }
        }
      }
    });
    
    JScrollPane oScrollPane = new JScrollPane(oTable);
    
    TableColumnResizer.setResizeColumnsListeners(oTable);
    TableSorter.setSorterListener(oTable);
    
    oTable.getSelectionModel().addListSelectionListener(this);
    
    btnSchedRefresh = GUIUtil.buildActionButton("|Aggiorna Schedulazione|" + IConstants.sICON_REFRESH, "sched_refresh");
    btnSchedRefresh.addActionListener(this);
    btnSchedRefresh.setEnabled(false);
    
    JPanel oEastPanel = new JPanel(new BorderLayout());
    oEastPanel.add(btnSchedRefresh, BorderLayout.NORTH);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oScrollPane, BorderLayout.CENTER);
    oResult.add(oEastPanel, BorderLayout.EAST);
    return oResult;
  }
  
  protected
  Container buildLogTable()
  {
    String[] asCOLUMNS   = {"Id Log",     "Data Inizio",     "Ora Inizio",     "Data Fine",     "Ora Fine",     "Stato",     "Rapporto"};
    String[] asSYMBOLICS = {ILog.sID_LOG, ILog.sDATA_INIZIO, ILog.sORA_INIZIO, ILog.sDATA_FINE, ILog.sORA_FINE, ILog.sSTATO, ILog.sRAPPORTO};
    
    oTableModel_Log = new SimpleTableModelForSorter(oRecords_Log, asCOLUMNS, asSYMBOLICS);
    oTableModel_Log.addTimeField(ILog.sORA_INIZIO);
    oTableModel_Log.addTimeField(ILog.sORA_FINE);
    
    oTable_Log = new JTable(oTableModel_Log);
    oTable_Log.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable_Log.setColumnSelectionAllowed(false);
    oTable_Log.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
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
    
    TableUtils.setMonospacedFont(oTable_Log);
    
    JScrollPane oScrollPane = new JScrollPane(oTable_Log);
    
    TableColumnResizer.setResizeColumnsListeners(oTable_Log);
    TableSorter.setSorterListener(oTable_Log);
    
    oScrollPane.setBorder(BorderFactory.createTitledBorder("Esecuzioni"));
    
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
    
    btnLogRefresh = GUIUtil.buildActionButton("|Aggiorna Log|" + IConstants.sICON_REFRESH, "log_refresh");
    btnLogRefresh.addActionListener(this);
    btnLogRefresh.setEnabled(false);
    
    JPanel oEastPanel = new JPanel(new BorderLayout());
    oEastPanel.add(btnLogRefresh, BorderLayout.NORTH);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oScrollPane, BorderLayout.CENTER);
    oResult.add(oEastPanel,  BorderLayout.EAST);
    return oResult;
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
    
    TableUtils.setMonospacedFont(oTable_LogFiles);
    
    TableUtils.setLinkField(oTable_LogFiles, 2);
    
    JScrollPane oScrollPane = new JScrollPane(oTable_LogFiles);
    
    TableColumnResizer.setResizeColumnsListeners(oTable_LogFiles);
    TableSorter.setSorterListener(oTable_LogFiles);
    
    oScrollPane.setBorder(BorderFactory.createTitledBorder("Files"));
    
    return oScrollPane;
  }
  
  protected
  void onChoiceMade()
  {
    setChoice(oLastRecordReaded);
  }
  
  protected
  void setFilterValues(Object oValues)
      throws Exception
  {
    if(oValues instanceof Map) {
      FormPanel fpFilter = (FormPanel) getFilterContainer();
      fpFilter.setValues(WUtil.toMapObject(oValues));
    }
  }
  
  protected
  void doFind()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    
    Map<String, Object> oFilterValues = fpFilter.getValues();
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(DataNormalizer.normalize(oFilterValues));
    parameters.add(AppUtil.vServiziAbilitati);
    oRecords = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULAZIONI.find", parameters, true));
    
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
    
    resetLogTables();
    jtpResult.setSelectedIndex(0);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    
    if(oRecords.size() == 1) {
      ResourcesMgr.getStatusBar().setText("1 schedulazione trovata.");
    }
    else {
      ResourcesMgr.getStatusBar().setText(oRecords.size() + " schedulazioni trovate.");
    }
  }
  
  protected
  void doReset()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    fpFilter.reset();
    fpFilter.setValue(sINIZIO_VALIDITA, new Date());
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    
    resetLogTables();
    
    oRecords = new ArrayList<Map<String, Object>>();
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
  }
  
  protected
  boolean onSelection()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return false;
    }
    
    if(boDontRead) {
      boDontRead = false;
      return true;
    }
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    iLastTableIndex = iRow;
    
    int idSchedulazione = WUtil.toInt(oRecord.get(sID_SCHEDULAZIONE), 0);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Integer> parameters = new ArrayList<Integer>();
    parameters.add(idSchedulazione);
    Map<String, Object> mapRead = WUtil.toMapObject(oRPCClient.execute("SCHEDULAZIONI.read", parameters));
    
    oLastRecordReaded = new HashMap<String, Object>(mapRead);
    
    resetLogTables();
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    fpDetail.setValues(mapRead);
    
    // Nella consultazione puo' far comodo restare nel pannello selezionato.
    // Per questo motivo si commenta la seguente riga.
    // fpDetail.selectFirstTab();
    
    return true;
  }
  
  protected
  void resetLogTables()
  {
    iIdSchedulazioneReadLog = 0;
    oRecords_Log = new ArrayList<Map<String, Object>>();
    oTableModel_Log.setData(oRecords_Log);
    TableSorter.resetHeader(oTable_Log);
    
    oRecords_LogFiles = new ArrayList<Map<String, Object>>();
    oTableModel_LogFiles.setData(oRecords_LogFiles);
    TableSorter.resetHeader(oTable_LogFiles);
    
    btnLogRefresh.setEnabled(false);
    btnGoToHomeDownload.setEnabled(false);
  }
  
  protected
  void doNew()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    fpDetail.setValue(sSCHEDULAZIONE, "now");
    if(!fpFilter.isBlank(sID_SERVIZIO)) {
      fpDetail.setValue(sID_SERVIZIO, fpFilter.getContent(sID_SERVIZIO));
      fpDetail.requestFocus(sID_ATTIVITA);
    }
    else {
      fpDetail.requestFocus(sID_SERVIZIO);
    }
    
    oTable.clearSelection();
    oTable.setEnabled(false);
  }
  
  protected
  boolean checkBeforeOpen()
      throws Exception
  {
    if(isElementEnabled()) {
      FormPanel fpDetail = (FormPanel) getDetailContainer();
      String sSchedulazione = (String) fpDetail.getValue(sSCHEDULAZIONE);
      if(sSchedulazione != null && sSchedulazione.equalsIgnoreCase("now")) {
        GUIMessage.showWarning("Per modificare tale schedulazione occorre disattivarla.");
        return false;
      }
    }
    
    return true;
  }
  
  protected
  void doOpen()
      throws Exception
  {
    oTable.setEnabled(false);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    fpDetail.setEnabled(sID_SERVIZIO, false);
    fpDetail.setEnabled(sID_ATTIVITA, false);
    
    fpDetail.getComponent(sDESCRIZIONE).requestFocus();
  }
  
  protected
  boolean doSave(boolean boNew)
      throws Exception
  {
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    String sCheckMandatory = fpDetail.getStringCheckMandatories();
    if(sCheckMandatory.length() > 0) {
      GUIMessage.showWarning("Occorre valorizzare i seguenti campi:\n" + sCheckMandatory);
      return false;
    }
    
    int iRowToSelect = 0;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    Map<String, Object> oDetailValues = fpDetail.getValues();
    AppUtil.putUserLog(oDetailValues);
    
    // Si ottiene lo stato attuale della schedulazione prima dell'aggiornamento.
    // In tal modo se una schedulazione e' disattivata rimane tale anche in aggiornamento.
    if(!boNew) {
      readExecInfo(oDetailValues);
      String sStato = WUtil.toString(oDetailValues.get(sSTATO), "");
      if(sStato != null && sStato.equals(sSTATO_IN_ESECUZIONE)) {
        GUIMessage.showWarning("Schedulazione non modificabile poich\351 \350 in esecuzione.");
        return false;
      }
    }
    
    List<Object> vParameters = new ArrayList<Object>();
    vParameters.add(DataNormalizer.normalize(oDetailValues));
    
    try {
      if(boNew) {
        Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("SCHEDULAZIONI.insert", vParameters, true));
        
        readExecInfo(mapResult);
        
        fpDetail.setValues(mapResult);
        oRecords.add(0, mapResult);
        iRowToSelect = 0;
        boDontRead = false;
      }
      else {
        Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("SCHEDULAZIONI.update", vParameters, true));
        
        fpDetail.reset();
        fpDetail.setValues(mapResult);
        
        readExecInfo(mapResult);
        
        oRecords.set(iLastTableIndex, mapResult);
        iRowToSelect = iLastTableIndex;
        
        // Non viene effettuata di nuovo la lettura, tuttavia occorre aggiornare
        // oLastRecordReaded poiche' viene utilizzato in doCancel().
        oLastRecordReaded = new HashMap<String, Object>(mapResult);
        boDontRead = true;
      }
    }
    catch(Exception ex) {
      String sEx = ex.toString();
      int iIndexOf = sEx.indexOf("LJSA-");
      if(iIndexOf > 0) {
        GUIMessage.showWarning(sEx.substring(iIndexOf));
        return false;
      }
      else {
        throw ex;
      }
    }
    
    oTable.setEnabled(true);
    TableSorter.resetHeader(oTable);
    oTableModel.notifyUpdates();
    
    oTable.setRowSelectionInterval(iRowToSelect, iRowToSelect);
    
    return true;
  }
  
  protected
  void doCancel()
      throws Exception
  {
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    int iRow = oTable.getSelectedRow();
    fpDetail.reset();
    if(iRow >= 0) {
      fpDetail.setValues(oLastRecordReaded);
    }
    
    oTable.setEnabled(true);
  }
  
  protected
  boolean checkBeforeDelete()
      throws Exception
  {
    if(isElementEnabled()) {
      GUIMessage.showWarning("Si possono eliminare solo schedulazioni disattivate.");
      return false;
    }
    
    return true;
  }
  
  protected
  void doDelete()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return;
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    String sIdServizio     = WUtil.toString(oRecord.get(sID_SERVIZIO), "");
    int    idSchedulazione = WUtil.toInt(oRecord.get(sID_SCHEDULAZIONE), 0);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(sIdServizio);
    parameters.add(idSchedulazione);
    parameters.add(AppUtil.getUserLog());
    oRPCClient.execute("SCHEDULAZIONI.delete", parameters);
    
    oRecords.remove(iRow);
    oTable.clearSelection();
    oTableModel.notifyUpdates();
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
  }
  
  
  protected
  void checkActions(List<JButton> listDefActions, boolean boAllowEditing)
  {
    removeButtonByActionCommand(listDefActions, sACTION_PRINT);
    
    btnGoToHomeDownload = GUIUtil.buildActionButton("Download|Vai alla pagina di download|FolderOutLarge.gif", "goto_home_download");
    btnGoToHomeDownload.addActionListener(this);
    btnGoToHomeDownload.setEnabled(false);
    listDefActions.add(btnGoToHomeDownload);
    
    btnCopyFrom = GUIUtil.buildActionButton("C&opia da|Copia dall'elemento selezionato|" + IConstants.sICON_COPY, "copyFrom");
    btnCopyFrom.addActionListener(this);
    btnCopyFrom.setEnabled(false);
    listDefActions.add(btnCopyFrom);
    
    // Mette come ultimo pulsante quello di uscita (chiudi).
    JButton btnExit = removeButtonByActionCommand(listDefActions, sACTION_EXIT);
    if(btnExit != null) listDefActions.add(btnExit);
    
    iMaxRowsActions++;
  }
  
  protected
  void onChangeEditorStatus(int iStatus)
  {
    switch(iStatus) {
    case iSTATUS_STARTUP:
      btnSchedRefresh.setEnabled(false);
      btnCopyFrom.setEnabled(false);
      break;
    case iSTATUS_VIEW:
      btnSchedRefresh.setEnabled(true);
      btnCopyFrom.setEnabled(oTable.getSelectedRow() >= 0);
      break;
    case iSTATUS_EDITING:
      btnSchedRefresh.setEnabled(false);
      btnCopyFrom.setEnabled(false);
      break;
    }
  }
  
  protected
  void doPrint()
      throws Exception
  {
  }
  
  protected
  boolean isElementEnabled()
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return true;
    
    Map<String, Object> record = oRecords.get(iRow);
    String sStato = WUtil.toString(record.get(sSTATO), null);
    if(sStato == null) return true;
    return !sStato.equalsIgnoreCase(sSTATO_DISATTIVATA);
  }
  
  protected
  void doToggle()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return;
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    String  sIdServizio      = WUtil.toString(oRecord.get(sID_SERVIZIO), "");
    Integer oIdSchedulazione = WUtil.toInteger(oRecord.get(sID_SCHEDULAZIONE), 0);
    String  sStato           = WUtil.toString(oRecord.get(sSTATO), "");
    boolean boNuovoStato = false;
    if(sStato != null) {
      boNuovoStato = sStato.equalsIgnoreCase(sSTATO_DISATTIVATA);
    }
    
    if(boNuovoStato && !checkTemporalValidity(oRecord)) {
      GUIMessage.showWarning("Schedulazione non attivabile poich\351 fuori validit\340.");
      return;
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> vParameters = new Vector<Object>();
    vParameters.add(sIdServizio);
    vParameters.add(oIdSchedulazione);
    vParameters.add(new Boolean(boNuovoStato));
    vParameters.add(AppUtil.getUserLog());
    oRPCClient.execute("SCHEDULAZIONI.setEnabled", vParameters, true);
    
    readExecInfo(oRecord);
    
    // Aggiornamento del dettaglio
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.setValue(sSTATO, oRecord.get(sSTATO));
    fpDetail.setValue(sESECUZIONI_COMPLETATE, oRecord.get(sESECUZIONI_COMPLETATE));
    fpDetail.setValue(sESECUZIONI_INTERROTTE, oRecord.get(sESECUZIONI_INTERROTTE));
    
    ListSelectionEvent lse = new ListSelectionEvent(this, iRow, iRow, false);
    oTable.valueChanged(lse);
  }
  
  protected static
  String getHelpTextSchedulazione()
  {
    String result = "L'espressione di schedulazione \350 cos\354 composta:\n\n";
    result += "secondi minuti ore giorni mesi giorni_settimana [anno]\n\n";
    result += "Esempi di schedulazione:\n\n";
    result += "now                 = Esegui subito\n";
    result += "now/10              = Esegui subito e ripeti ogni 10 secondi\n";
    result += "0/10 * * * * ?      = Ogni 10 sec a partire da 0 sec\n";
    result += "0 0/5 * * * ?       = Ogni  5 min a partire da 0 min\n";
    result += "0 0/15 20-22 * * ?  = Ogni 15 min, dalle 20 alle 22\n";
    result += "0 30 19 * * ?       = Alle 19.30 di ogni giorno\n";
    result += "0 0 13,20 * * ?     = Alle 13.00 e alle 20.00 di ogni giorno\n";
    result += "0 0 21 15,20 * ?    = Alle 21.00 dei giorni 15 e 20 di ogni mese\n";
    result += "0 45 21 15 3 ? 2008 = Il 15/03/2008 alle ore 21.45 (Impostare fine val.)\n";
    result += "0 0/30 7-19 ? * 2-7 = Ogni 30 min, dalle 7 alle 19, dal lun. al sab.\n";
    result += "0 15 10 ? * 6#3     = Alle 10.15, il terzo venerd\354 (6) di ogni mese\n";
    return result;
  }
  
  public static
  String getDescTipologia(Object oTipologia)
  {
    if(oTipologia == null) return "Output";
    String sTipologia = oTipologia.toString();
    if(sTipologia.equalsIgnoreCase("O")) {
      return "Output";
    }
    else if(sTipologia.equalsIgnoreCase("E")) {
      return "Errori";
    }
    else if(sTipologia.equalsIgnoreCase("I")) {
      return "Informazioni";
    }
    else if(sTipologia.equalsIgnoreCase("M")) {
      return "Messaggio";
    }
    else if(sTipologia.equalsIgnoreCase("R")) {
      return "Rapporto";
    }
    return "Output";
  }
  
  protected
  boolean checkTemporalValidity(Map<String, Object> mapRecord)
  {
    if(mapRecord == null) return false;
    int iCurrentDate    = WUtil.toIntDate(Calendar.getInstance(), 0);
    int iInizioValidita = WUtil.toIntDate(mapRecord.get(sINIZIO_VALIDITA), iCurrentDate);
    int iFineValidita   = WUtil.toIntDate(mapRecord.get(sFINE_VALIDITA),   99991231);
    return iCurrentDate >= iInizioValidita && iCurrentDate <= iFineValidita;
  }
}
