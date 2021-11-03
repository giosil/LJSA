package org.dew.ljsa.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.dew.ljsa.ISchedulatore;
import org.dew.ljsa.ISchedulazione;
import org.dew.ljsa.gui.TreeResources;

import org.dew.swingup.AWorkPanel;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.IConstants;
import org.dew.swingup.IWorkObject;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.fm.FMUtils;
import org.dew.swingup.fm.GUIFileManager;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.CodeAndDescription;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUISchedulatore extends JPanel implements ActionListener, IWorkObject, ISchedulatore
{
  private static final long serialVersionUID = -7273799614647960247L;
  
  protected JButton btnStart;
  protected JButton btnStop;
  protected JButton btnUpdate;
  protected JButton btnPing;
  protected JButton btnFileMgr;
  
  protected JTable oTable;
  protected SimpleTableModelForSorter oTableModel;
  protected List<Map<String, Object>> oRecords;
  
  protected JTable oTable_Sched;
  protected SimpleTableModelForSorter oTableModel_Sched;
  protected List<Map<String, Object>> oRecords_Sched;
  
  protected JTree oTree_Conf;
  
  protected JTabbedPane jTabbedPane;
  
  protected JLabel jlName;
  protected JLabel jlURLService;
  protected JLabel jlDescStato;
  protected String sURLService;
  protected String sLastIdServizio;
  
  // L'array di chiavi serve per seguire il seguente ordine
  protected String[] asKeys = {sINFO_ID_SCHEDULATORE, sINFO_URL_SERVIZIO, sINFO_VERSION, sINFO_STATO, sINFO_DESCRIZIONE_STATO, sINFO_NUMERO_SCHEDULAZIONI,
      sINFO_DATA_SCHEDULAZIONE, sINFO_ORA_SCHEDULAZIONE, sINFO_DATA_AGGIORNAMENTO, sINFO_ORA_AGGIORNAMENTO};
  
  protected boolean boIsSchedulerStarted = false;
  protected boolean boIsSchedulerOnLine  = false;
  protected Map<String, Object> mapConfigurazione = null;
  protected String sDataSourceToPing = null;
  protected JComboBox<CodeAndDescription> jcbSchedulatori;
  
  public
  GUISchedulatore()
  {
    try {
      init();
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante l'inizializzazione di GUISchedulatore", ex);
    }
  }
  
  // java.awt.event.ActionListener --------------------------------------------
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    
    try {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      
      if(sActionCommand.equals("filemgr")) {
        doOpenFileMgr();
      }
      else if(sActionCommand.equals("ping")) {
        doPing();
      }
      else if(sActionCommand.equals("start")) {
        doStart();
      }
      else if(sActionCommand.equals("stop")) {
        doStop();
      }
      else if(sActionCommand.equals("update")) {
        doUpdate();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  // --------------------------------------------------------------------------
  
  // org.dew.swingup.IWorkObject ---------------------------------------------
  public boolean onClosing() {
    return true;
  }
  
  public void onActivated() {
  }
  
  public void onOpened() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        readInfo();
      }
    });
  }
  // --------------------------------------------------------------------------
  
  protected
  void doOpenFileMgr()
      throws Exception
  {
    if(sURLService == null || sURLService.length() == 0) return;
    String sVersion = FMUtils.getVersion(sURLService);
    if(sVersion == null || sVersion.length() == 0) {
      GUIMessage.showWarning("Il server non supporta la gestione remota dei file.");
      return;
    }
    AWorkPanel oWorkPanel = ResourcesMgr.getWorkPanel();
    oWorkPanel.show(new GUIFileManager(sURLService), GUIFileManager.getHost(sURLService) + " (ver. " + sVersion + ")", IConstants.sICON_CLIENT);
  }
  
  protected
  void doPing()
      throws Exception
  {
    if(sDataSourceToPing == null) {
      GUIMessage.showWarning("Data source non specificato.");
      btnPing.setEnabled(false);
      return;
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(getIdServizio());
    parameters.add(sDataSourceToPing);
    Boolean oResult = (Boolean) oRPCClient.execute("SCHEDULATORE.pingDataSource", parameters, true);
    if(oResult != null && oResult.booleanValue()) {
      GUIMessage.showInformation("Connessione \"" + sDataSourceToPing + "\" verificata con successo.");
    }
    else {
      GUIMessage.showWarning("Tentativo di connessione a \"" + sDataSourceToPing + "\" fallito.");
    }
  }
  
  protected
  void doStart()
      throws Exception
  {
    if(!GUIMessage.getConfirmation("Sei sicuro di voler avviare lo schedulatore?")) return;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(getIdServizio());
    Boolean oResult = WUtil.toBooleanObj(oRPCClient.execute("SCHEDULATORE.start", parameters, true), false);
    if(oResult != null && oResult.booleanValue()) {
      GUIMessage.showInformation("Schedulatore avviato.");
    }
    else {
      GUIMessage.showWarning("Non \350 stato possibile avviare lo schedulatore.");
    }
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        readInfo();
      }
    });
  }
  
  protected
  void doStop()
      throws Exception
  {
    if(!GUIMessage.getConfirmation("Sei sicuro di voler fermare lo schedulatore?")) return;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(getIdServizio());
    Boolean oResult = WUtil.toBooleanObj(oRPCClient.execute("SCHEDULATORE.stop", parameters, true), false);
    if(oResult != null && oResult.booleanValue()) {
      GUIMessage.showInformation("Schedulatore fermato.");
    }
    else {
      GUIMessage.showWarning("Non \350 stato possibile fermare lo schedulatore.");
    }
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        readInfo();
      }
    });
  }
  
  protected
  void doUpdate()
      throws Exception
  {
    if(!GUIMessage.getConfirmation("Sei sicuro di voler aggiornare lo schedulatore?")) return;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(getIdServizio());
    Boolean oResult = WUtil.toBooleanObj(oRPCClient.execute("SCHEDULATORE.update", parameters, true), false);
    if(oResult != null && oResult.booleanValue()) {
      GUIMessage.showInformation("Aggiornamento avvenuto con successo.");
    }
    else {
      GUIMessage.showInformation("Aggiornamento avvenuto senza alterazione delle schedulazioni.");
    }
    
    jTabbedPane.setSelectedIndex(0);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        readInfo();
      }
    });
  }
  
  protected
  void readInfo()
  {
    sLastIdServizio = getIdServizio();
    
    Map<String, Object> mapInfo = null;
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sLastIdServizio);
    try {
      mapInfo = WUtil.toMapObject(oRPCClient.execute("SCHEDULATORE.readInfo", parameters, true));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura delle informazioni", ex);
      return;
    }
    
    if(mapInfo == null || mapInfo.isEmpty()) {
      oRecords.clear();
      oTableModel.notifyUpdates();
      return;
    }
    
    sURLService = (String) mapInfo.get(ISchedulatore.sINFO_URL_SERVIZIO);
    if(sURLService != null) {
      jlURLService.setText("<html><a href=\"\">" + sURLService + "</a></html>");
    }
    else {
      jlURLService.setText("");
    }
    
    String sStato     = (String) mapInfo.get(sINFO_STATO);
    boIsSchedulerOnLine = true;
    if(sStato != null && sStato.equalsIgnoreCase("X")) {
      boIsSchedulerOnLine = false;
    }
    
    boIsSchedulerStarted = false;
    if(sStato != null && sStato.equalsIgnoreCase("R")) {
      boIsSchedulerStarted = true;
    }
    
    String sDescStato = (String) mapInfo.get(sINFO_DESCRIZIONE_STATO);
    if(sDescStato != null) {
      jlDescStato.setText(sDescStato);
      if(boIsSchedulerOnLine) {
        if(boIsSchedulerStarted) {
          jlDescStato.setIcon(ResourcesMgr.getImageIcon("GreenCircleSmall.gif"));
        }
        else {
          jlDescStato.setIcon(ResourcesMgr.getImageIcon("RedCircleSmall.gif"));
        }
      }
      else {
        jlDescStato.setIcon(ResourcesMgr.getImageIcon("RedCircleSmall.gif"));
      }
    }
    else {
      jlDescStato.setText("Stato schedulatore non disponibile");
      jlDescStato.setIcon(ResourcesMgr.getImageIcon("RedCircleSmall.gif"));
    }
    
    oRecords.clear();
    for(int i = 0; i < asKeys.length; i++) {
      String sKey   = asKeys[i];
      Object oValue = mapInfo.get(sKey);
      if(sKey.equals(sINFO_ORA_SCHEDULAZIONE) || sKey.equals(sINFO_ORA_AGGIORNAMENTO)) {
        oValue = WUtil.formatTime(oValue, false, false);
      }
      
      Map<String, Object> mapRecord = new HashMap<String, Object>();
      mapRecord.put("k", sKey);
      mapRecord.put("v", oValue);
      
      oRecords.add(mapRecord);
    }
    
    updateGUI();
  }
  
  protected
  void readSchedulazioni()
  {
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> vParameters = new ArrayList<String>();
    vParameters.add(getIdServizio());
    try {
      oRecords_Sched = WUtil.toListOfMapObject(oRPCClient.execute("SCHEDULATORE.getInfoSchedulazioni", vParameters, true));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura delle schedulazioni", ex);
      return;
    }
    
    oTableModel_Sched.setData(oRecords_Sched);
  }
  
  protected
  void readConfigurazione()
  {
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(getIdServizio());
    try {
      mapConfigurazione = WUtil.toMapObject(oRPCClient.execute("SCHEDULATORE.getConfiguration", parameters, true));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante la lettura della configurazione", ex);
      return;
    }
    
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      DefaultMutableTreeNode dmtn = TreeResources.parse(mapConfigurazione, "Configurazione");
      oTree_Conf.setModel(new DefaultTreeModel(dmtn));
    }
    catch(Throwable ex) {
      GUIMessage.showException("Errore durante la costruzione della struttura", ex);
    }
    finally {
      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }
  
  protected
  Vector<CodeAndDescription> loadIstanze()
  {
    Vector<CodeAndDescription> vResult = new Vector<CodeAndDescription>();
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    try {
      List<String> listIstanze = WUtil.toListOfString(oRPCClient.execute("SCHEDULATORE.getServicesByIstances", Collections.EMPTY_LIST));
      if(listIstanze == null) return vResult;
      
      for(int i = 0; i < listIstanze.size(); i++) {
        String sIstanza = listIstanze.get(i);
        if(sIstanza != null && sIstanza.length() > 0) {
          vResult.add(new CodeAndDescription(sIstanza, "Istanza LJSA dedicata al servizio " + sIstanza));
        }
        else {
          vResult.add(0, new CodeAndDescription("", "Istanza LJSA principale"));
        }
      }
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante il caricamento delle istanze", ex);
    }
    return vResult;
  }
  
  protected
  void updateGUI()
  {
    btnFileMgr.setEnabled(boIsSchedulerOnLine);
    btnStart.setEnabled(boIsSchedulerOnLine && !boIsSchedulerStarted);
    btnStop.setEnabled(boIsSchedulerOnLine && boIsSchedulerStarted);
    btnUpdate.setEnabled(boIsSchedulerOnLine && boIsSchedulerStarted);
    
    oTableModel.notifyUpdates();
  }
  
  protected
  String getIdServizio()
  {
    if(jcbSchedulatori == null) return "";
    CodeAndDescription cd = (CodeAndDescription) jcbSchedulatori.getSelectedItem();
    if(cd == null) return "";
    String sIdServizio = (String) cd.getCode();
    if(sIdServizio == null) return "";
    return sIdServizio;
  }
  
  protected
  Container buildResultContainer()
  {
    oTree_Conf = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("Configurazione")));
    oTree_Conf.setShowsRootHandles(true);
    oTree_Conf.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        if(mapConfigurazione == null) return;
        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) oTree_Conf.getLastSelectedPathComponent();
        if(dmtn == null) return;
        Object oUserObject = dmtn.getUserObject();
        if(oUserObject == null) return;
        String sUserObject = oUserObject.toString();
        if(sUserObject.equalsIgnoreCase("jdbc")) {
          Object oParent = dmtn.getParent();
          String sParent = oParent != null ? oParent.toString() : null;
          if(sParent != null && sParent.length() > 0) {
            sDataSourceToPing = sParent;
            btnPing.setEnabled(true);
          }
          else {
            sDataSourceToPing = null;
            btnPing.setEnabled(false);
          }
        }
        else {
          String sJdbcURL = (String) mapConfigurazione.get(sUserObject + ".jdbc.url");
          if(sJdbcURL != null && sJdbcURL.length() > 0) {
            sDataSourceToPing = sUserObject;
            btnPing.setEnabled(true);
          }
          else {
            sDataSourceToPing = null;
            btnPing.setEnabled(false);
          }
        }
      }
    });
    
    jTabbedPane = new JTabbedPane();
    
    jTabbedPane.add("Informazioni",   buildTableInfo());
    jTabbedPane.add("Schedulazioni",  buildTableSchedulazioni());
    jTabbedPane.add("Configurazione", new JScrollPane(oTree_Conf));
    
    jTabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        mapConfigurazione = null;
        sDataSourceToPing = null;
        btnPing.setEnabled(false);
        
        int iSelectedIndex = jTabbedPane.getSelectedIndex();
        if(iSelectedIndex == 1) {
          if(boIsSchedulerOnLine) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                readSchedulazioni();
              }
            });
          }
        }
        else if(iSelectedIndex == 2) {
          if(boIsSchedulerOnLine) {
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                readConfigurazione();
              }
            });
          }
        }
      }
    });
    
    return jTabbedPane;
  }
  
  protected
  Container buildTableInfo()
  {
    String[] asCOLUMNS   = {"Chiave", "Valore"};
    String[] asSYMBOLICS = {"k",      "v"};
    
    oRecords = new ArrayList<Map<String, Object>>();
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    TableUtils.setMonospacedFont(oTable);
    
    JScrollPane oScrollPane = new JScrollPane(oTable);
    TableColumnResizer.setResizeColumnsListeners(oTable);
    TableSorter.setSorterListener(oTable);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oScrollPane, BorderLayout.CENTER);
    
    oResult.setBorder(BorderFactory.createTitledBorder("Informazioni"));
    
    return oResult;
  }
  
  protected
  Container buildTableSchedulazioni()
  {
    String[] asCOLUMNS   = {"Id",                            "Servizio",                   "Attivita",                  "Schedulazione",               "Stato",               "Descrizione"};
    String[] asSYMBOLICS = {ISchedulazione.sID_SCHEDULAZIONE, ISchedulazione.sID_SERVIZIO, ISchedulazione.sID_ATTIVITA, ISchedulazione.sSCHEDULAZIONE, ISchedulazione.sSTATO, ISchedulazione.sDESCRIZIONE};
    
    oTableModel_Sched = new SimpleTableModelForSorter(oRecords_Sched, asCOLUMNS, asSYMBOLICS);
    
    oTable_Sched = new JTable(oTableModel_Sched);
    oTable_Sched.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable_Sched.setColumnSelectionAllowed(false);
    oTable_Sched.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
    oTable_Sched.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, focus, row, col);
        
        Map<String, Object> oRecord = oRecords.get(row);
        
        String sStato = WUtil.toString(oRecord.get(ISchedulazione.sSTATO), "");
        
        if(sStato.equalsIgnoreCase(ISchedulazione.sSTATO_DISATTIVATA)) {
          this.setForeground(Color.gray);
          this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
        }
        else if(sStato.equalsIgnoreCase(ISchedulazione.sSTATO_IN_ESECUZIONE)) {
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
    
    TableUtils.setMonospacedFont(oTable_Sched);
    
    JScrollPane oScrollPane = new JScrollPane(oTable_Sched);
    
    TableColumnResizer.setResizeColumnsListeners(oTable_Sched);
    TableSorter.setSorterListener(oTable_Sched);
    
    return oScrollPane;
  }
  
  protected
  JPanel buildHeaderPanel()
  {
    JPanel oResult = new JPanel(new GridLayout(4, 1, 4, 4));
    
    jcbSchedulatori = new JComboBox<CodeAndDescription>(loadIstanze());
    jcbSchedulatori.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String sIdServizio = getIdServizio();
        if(sIdServizio != null && sIdServizio.equals(sLastIdServizio)) {
          return;
        }
        jTabbedPane.setSelectedIndex(0);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            readInfo();
          }
        });
      }
    });
    oResult.add(GUIUtil.buildLabelledComponent(jcbSchedulatori, "Istanza:", 50));
    
    jlName = new JLabel("LJSA - Light Java Scheduler Application");
    jlName.setFont(GUIUtil.modifyFont(jlName.getFont(), 2));
    oResult.add(jlName);
    
    jlURLService = new JLabel("");
    jlURLService.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    jlURLService.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(sURLService == null || sURLService.length() == 0) {
          return;
        }
        try {
          ResourcesMgr.openBrowser(sURLService);
        }
        catch(Exception ex) {
          GUIMessage.showException("Errore durante l'apertura del browser", ex);
        }
      }
    });
    oResult.add(jlURLService);
    
    jlDescStato = new JLabel("Stato schedulatore non disponibile");
    jlDescStato.setIcon(ResourcesMgr.getImageIcon("RedCircleSmall.gif"));
    oResult.add(jlDescStato);
    
    oResult.setBorder(BorderFactory.createTitledBorder("Schedulatore"));
    
    return oResult;
  }
  
  protected
  JPanel buildButtonsPanel()
  {
    JPanel oPanel = new JPanel(new GridLayout(1, 5, 4, 4));
    
    btnFileMgr = GUIUtil.buildActionButton("&File Mgr|File manager|" + IConstants.sICON_CONNECT, "filemgr");
    btnFileMgr.addActionListener(this);
    btnFileMgr.setEnabled(false);
    oPanel.add(btnFileMgr);
    
    btnPing = GUIUtil.buildActionButton("&Ping DS|Verifica Connessione Data Source|" + IConstants.sICON_CONNECT, "ping");
    btnPing.addActionListener(this);
    btnPing.setEnabled(false);
    oPanel.add(btnPing);
    
    btnStart = GUIUtil.buildActionButton("&Avvia|Avvia lo schedulatore|GreenCircleSmall.gif", "start");
    btnStart.addActionListener(this);
    btnStart.setEnabled(false);
    oPanel.add(btnStart);
    
    btnStop = GUIUtil.buildActionButton("&Ferma|Ferma lo schedulatore|RedCircleSmall.gif", "stop");
    btnStop.addActionListener(this);
    btnStop.setEnabled(false);
    oPanel.add(btnStop);
    
    btnUpdate = GUIUtil.buildActionButton("A&ggiorna|Aggiorna lo schedulatore|" + IConstants.sICON_REFRESH, "update");
    btnUpdate.addActionListener(this);
    btnUpdate.setEnabled(false);
    oPanel.add(btnUpdate);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oPanel, BorderLayout.EAST);
    oResult.setBorder(BorderFactory.createTitledBorder("Comandi"));
    
    return oResult;
  }
  
  protected
  void init()
      throws Exception
  {
    this.setLayout(new BorderLayout());
    
    this.add(buildHeaderPanel(),     BorderLayout.NORTH);
    this.add(buildResultContainer(), BorderLayout.CENTER);
    this.add(buildButtonsPanel(),    BorderLayout.SOUTH);
  }
}
