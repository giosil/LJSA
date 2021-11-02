package org.dew.ljsa.gui.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.gui.AppUtil;
import org.dew.ljsa.gui.DecodifiableFactory;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.IConstants;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.components.JTextDecodifiable;
import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.rpc.DataNormalizer;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.CollectionAutoCompleter;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUIAttivita extends AEntityEditor implements IAttivita
{
  private static final long serialVersionUID = 8740665797637957692L;
  
  protected SimpleTableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords = new ArrayList<Map<String, Object>>();
  protected Map<String, Object> oLastRecordReaded;
  
  protected boolean boLockServizio = false;
  protected boolean boMultiSelection = false;
  protected Object oFiltroServizio = null;
  
  protected DPAttivitaConfigurazione oDPConfigurazione;
  protected DPAttivitaParametri      oDPParametri;
  protected DPAttivitaNotifica       oDPNotifica;
  
  protected CollectionAutoCompleter collAutoComp;
  
  protected JButton btnCopyFrom;
  
  public
  GUIAttivita()
  {
    super();
  }
  
  public
  GUIAttivita(boolean boLockServizio, boolean boMultiSelection)
  {
    super();
    
    this.boLockServizio = boLockServizio;
    this.boMultiSelection = boMultiSelection;
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
    try {
      if(sActionCommand.equals("copyFrom")) {
        doCopyFrom();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  protected
  void doCopyFrom()
  {
    if(oLastRecordReaded == null || oLastRecordReaded.isEmpty()) {
      GUIMessage.showWarning("Informazioni non sufficienti per eseguire l'operazione");
      return;
    }
    String sIdAttivita = (String) oLastRecordReaded.get(sID_ATTIVITA);
    String sMsgConfirm = "La nuova attivit\340 avr\340 le stesse impostazioni di " + sIdAttivita + ". Procedere?";
    boolean boConfirm  = GUIMessage.getConfirmation(sMsgConfirm);
    if(!boConfirm) return;
    try {
      // Si attiva la creazione di un nuovo record
      fireNew();
      // Si riportano i valori ottenuti dall'ultima lettura
      FormPanel fpDetail = (FormPanel) getDetailContainer();
      fpDetail.setValues(oLastRecordReaded);
      // Si rimuovono i valori specifici
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          FormPanel fpDetail = (FormPanel) getDetailContainer();
          fpDetail.setValue(sID_ATTIVITA, null);
          fpDetail.requestFocus(sID_ATTIVITA);
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
    FormPanel fp = new FormPanel("Ricerca");
    fp.addTab("Attivit\340");
    fp.addRow();
    fp.addComponent(sID_SERVIZIO, "Servizio", DecodifiableFactory.buildDCServizio());
    fp.addRow();
    fp.addTextField(sID_ATTIVITA, "Codice", 50);
    fp.addTextField(sDESCRIZIONE, "Descrizione", 255);
    
    fp.build();
    
    return fp;
  }
  
  protected
  Container buildGUIDetail()
  {
    oDPConfigurazione = new DPAttivitaConfigurazione();
    oDPConfigurazione.setPreferredSize(new Dimension(0, 120));
    
    oDPParametri = new DPAttivitaParametri();
    oDPParametri.setPreferredSize(new Dimension(0, 120));
    
    oDPNotifica = new DPAttivitaNotifica();
    oDPNotifica.setPreferredSize(new Dimension(0, 120));
    
    JTextDecodifiable jtdClasse = (JTextDecodifiable) DecodifiableFactory.buildDCClasse();
    collAutoComp = new CollectionAutoCompleter(jtdClasse.getJTextField(0), GUIClassi.loadPackages());
    collAutoComp.setEnabledAutoCompletion(false);
    
    FormPanel fp = new FormPanel("Dettaglio");
    fp.addTab("Attributi");
    fp.addRow();
    fp.addBlankField();
    fp.addRow();
    fp.addComponent(sID_SERVIZIO, "Servizio", DecodifiableFactory.buildDCServizio());
    fp.addRow();
    fp.addTextField(sID_ATTIVITA, "Codice", 50);
    fp.addTextField(sDESCRIZIONE, "Descrizione", 255);
    fp.addRow();
    fp.addComponent(sCLASSE, "Classe", jtdClasse);
    fp.addTab("Configurazione");
    fp.addRow();
    fp.addDataPanel(sCONFIGURAZIONE, oDPConfigurazione);
    fp.addTab("Parametri");
    fp.addRow();
    fp.addDataPanel(sPARAMETRI, oDPParametri);
    fp.addTab("Notifica");
    fp.addRow();
    fp.addDataPanel(sNOTIFICA, oDPNotifica);
    
    fp.addHiddenField(sID_CREDENZIALE_INS);
    fp.addHiddenField(sDATA_INSERIMENTO);
    fp.addHiddenField(sORA_INSERIMENTO);
    
    fp.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sID_SERVIZIO);
    oMandatoryFields.add(sID_ATTIVITA);
    oMandatoryFields.add(sDESCRIZIONE);
    oMandatoryFields.add(sCLASSE);
    
    fp.setMandatoryFields(oMandatoryFields);
    
    return fp;
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
    String[] asCOLUMNS   = {"Servizio",   "Codice",     "Descrizione", "Classe", "Id Cred. Ins.",     "Data Ins.",      "Ora Ins.",        "Id Cred. Agg.",     "Data Agg.",         "Ora Agg."};
    String[] asSYMBOLICS = {sID_SERVIZIO, sID_ATTIVITA, sDESCRIZIONE,  sCLASSE,  sID_CREDENZIALE_INS, sDATA_INSERIMENTO, sORA_INSERIMENTO, sID_CREDENZIALE_AGG, sDATA_AGGIORNAMENTO, sORA_AGGIORNAMENTO};
    
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    oTableModel.addTimeField(sORA_INSERIMENTO);
    oTableModel.addTimeField(sORA_AGGIORNAMENTO);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    if(boMultiSelection) {
      oTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    else {
      oTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }
    oTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, focus, row, col);
        
        Map<String, Object> record = oRecords.get(row);
        boolean boAttivo = WUtil.toBoolean(record.get(sATTIVO), true);
        if(boAttivo) {
          this.setForeground(Color.black);
          this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        }
        else {
          this.setForeground(Color.gray);
          this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
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
    
    return oScrollPane;
  }
  
  protected
  void onChoiceMade()
  {
    if(boMultiSelection) {
      int[] aiSelectedRows = oTable.getSelectedRows();
      List<Map<String, Object>> oSelectedRecords = new ArrayList<Map<String, Object>>();
      for(int i = 0; i < aiSelectedRows.length; i++) {
        oSelectedRecords.add(oRecords.get(aiSelectedRows[i]));
      }
      setChoice(oSelectedRecords);
    }
    else {
      setChoice(oLastRecordReaded);
    }
  }
  
  protected
  void setFilterValues(Object oValues)
      throws Exception
  {
    if(oValues instanceof Map) {
      Map<String, Object> mapValues = WUtil.toMapObject(oValues);
      
      FormPanel fpFilter = (FormPanel) getFilterContainer();
      fpFilter.setValues(mapValues);
      
      oFiltroServizio = mapValues.get(sID_SERVIZIO);
    }
  }
  
  protected
  void doFind()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    Map<String, Object> oFilterValues = fpFilter.getValues();
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<Object> vParameters = new ArrayList<Object>();
    vParameters.add(DataNormalizer.normalize(oFilterValues));
    vParameters.add(AppUtil.vServiziAbilitati);
    oRecords = WUtil.toListOfMapObject(oRPCClient.execute("ATTIVITA.find", vParameters, true));
    
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
    
    fpDetail.reset();
    
    if(oRecords.size() == 1) {
      ResourcesMgr.getStatusBar().setText("1 attivit\340 trovata.");
    }
    else {
      ResourcesMgr.getStatusBar().setText(oRecords.size() + " attivit\340 trovate.");
    }
  }
  
  protected
  void doReset()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    fpFilter.reset();
    
    if(boLockServizio) {
      fpFilter.setValue(sID_SERVIZIO, oFiltroServizio);
      fpFilter.getComponent(sID_ATTIVITA).requestFocus();
    }
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    
    oRecords = new ArrayList<Map<String, Object>>();
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
  }
  
  protected
  boolean onSelection()
      throws Exception
  {
    Map<String, Object> oRecord = null;
    
    if(boMultiSelection) {
      int[] aiRows = oTable.getSelectedRows();
      if(aiRows.length == 0) return false;
      oRecord = oRecords.get(aiRows[aiRows.length - 1]);
    }
    else {
      int iRow = oTable.getSelectedRow();
      if(iRow < 0 || iRow >= oRecords.size()) {
        return false;
      }
      oRecord = oRecords.get(iRow);
    }
    
    String sIdServizio = (String) oRecord.get(sID_SERVIZIO);
    String sIdAttivita = (String) oRecord.get(sID_ATTIVITA);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new Vector<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdAttivita);
    Map<String, Object> mapRead = WUtil.toMapObject(oRPCClient.execute("ATTIVITA.read", parameters));
    
    oLastRecordReaded = new HashMap<String, Object>(mapRead);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    fpDetail.setValues(mapRead);
    // Nella consultazione puo' far comodo lasciare il pannello selezionato
    // fpDetail.selectFirstTab();
    
    return true;
  }
  
  protected
  void doNew()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    oDPConfigurazione.setDefaultData();
    if(!fpFilter.isBlank(sID_SERVIZIO)) {
      fpDetail.setValue(sID_SERVIZIO, fpFilter.getContent(sID_SERVIZIO));
      fpDetail.requestFocus(sID_ATTIVITA);
    }
    else {
      fpDetail.requestFocus(sID_SERVIZIO);
    }
    collAutoComp.setEnabledAutoCompletion(true);
    
    oTable.clearSelection();
    oTable.setEnabled(false);
  }
  
  protected
  void doOpen()
      throws Exception
  {
    oTable.setEnabled(false);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    fpDetail.setEnabled(sID_SERVIZIO, false);
    fpDetail.setEnabled(sID_ATTIVITA, false);
    
    collAutoComp.setEnabledAutoCompletion(true);
    
    fpDetail.getComponent(sDESCRIZIONE).requestFocus();
  }
  
  protected
  boolean doSave(boolean boNew)
      throws Exception
  {
    collAutoComp.setEnabledAutoCompletion(false);
    
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
    Vector<Object> vParameters = new Vector<Object>();
    vParameters.add(DataNormalizer.normalize(oDetailValues));
    
    try {
      if(boNew) {
        boolean exists = (Boolean) oRPCClient.execute("ATTIVITA.exists", vParameters);
        if(exists) {
          GUIMessage.showWarning("Attivit\340 gi\340 presente in archivio.");
          return false;
        }
        
        Map<String, Object> htResult = WUtil.toMapObject(oRPCClient.execute("ATTIVITA.insert", vParameters));
        
        fpDetail.setValues(htResult);
        oRecords.add(htResult);
        iRowToSelect = oRecords.size() - 1;
      }
      else {
        Map<String, Object> htResult = WUtil.toMapObject(oRPCClient.execute("ATTIVITA.update", vParameters));
        
        htResult.put(sATTIVO, oLastRecordReaded.get(sATTIVO));
        
        fpDetail.setValues(htResult);
        int iRow = oTable.getSelectedRow();
        oRecords.set(iRow, htResult);
        iRowToSelect = iRow;
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
  void doDelete()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return;
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    String sIdServizio = (String) oRecord.get(sID_SERVIZIO);
    String sIdAttivita = (String) oRecord.get(sID_ATTIVITA);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdAttivita);
    
    // Controllo
    int result = WUtil.toInt(oRPCClient.execute("ATTIVITA.countSchedulazioni", parameters), 0);
    if(result > 0) {
      GUIMessage.showWarning("Attivit\340 non eliminabile. Vi sono " + result + " schedulazioni non disattivate.");
      return;
    }
    
    // Cancellazione
    parameters.add(AppUtil.getUserLog());
    oRPCClient.execute("ATTIVITA.delete", parameters);
    
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
    
    btnCopyFrom = GUIUtil.buildActionButton("C&opia da|Copia dall'elemento selezionato|" + IConstants.sICON_COPY, "copyFrom");
    btnCopyFrom.addActionListener(this);
    btnCopyFrom.setEnabled(false);
    listDefActions.add(btnCopyFrom);
    
    // Mette come ultimo pulsante quello di uscita (chiudi).
    JButton btnExit = removeButtonByActionCommand(listDefActions, sACTION_EXIT);
    if(btnExit != null) {
      listDefActions.add(btnExit);
    }
  }
  
  protected
  void onChangeEditorStatus(int iStatus)
  {
    if(boLockServizio) {
      FormPanel fpFilter = (FormPanel) getFilterContainer();
      if(iStatus == iSTATUS_STARTUP) {
        if(fpFilter != null) {
          fpFilter.getComponent(sID_ATTIVITA).requestFocus();
          fpFilter.setEnabled(sID_SERVIZIO, false);
        }
      }
      else {
        if(fpFilter != null) {
          fpFilter.setEnabled(sID_SERVIZIO, false);
        }
      }
    }
    if(iStatus == iSTATUS_VIEW) {
      btnCopyFrom.setEnabled(oTable.getSelectedRow() >= 0);
    }
    else {
      btnCopyFrom.setEnabled(false);
    }
    // Si disabilita sempre l'autocompletion.
    // Lo si abilita in doNew e doOpen per evitare si
    // produca l'effetto visivo di comparsa del popup
    // dovuto al reset o all'impostazione del campo
    collAutoComp.setEnabledAutoCompletion(false);
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
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    
    return WUtil.toBoolean(oRecord.get(sATTIVO), true);
  }
  
  protected
  void doToggle()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return;
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    String sIdServizio = (String) oRecord.get(sID_SERVIZIO);
    String sIdAttivita = (String) oRecord.get(sID_ATTIVITA);
    Boolean oAttivo = (Boolean) oRecord.get(sATTIVO);
    boolean boNuovoStato = false;
    if(oAttivo != null) {
      boNuovoStato = !oAttivo.booleanValue();
    }
    
    Boolean oResult = null;
    try {
      IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
      List<Object> parameters = new ArrayList<Object>();
      parameters.add(sIdServizio);
      parameters.add(sIdAttivita);
      parameters.add(new Boolean(boNuovoStato));
      parameters.add(AppUtil.getUserLog());
      oResult = WUtil.toBooleanObj(oRPCClient.execute("ATTIVITA.setEnabled", parameters), false);
    }
    catch(Exception ex) {
      String sEx = ex.toString();
      int iIndexOf = sEx.indexOf("LJSA-");
      if(iIndexOf > 0) {
        GUIMessage.showWarning(sEx.substring(iIndexOf));
        return;
      }
      else {
        throw ex;
      }
    }
    
    oRecord.put(sATTIVO, oResult);
    oLastRecordReaded.put(sATTIVO, oResult);
  }
}
