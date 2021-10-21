package org.dew.ljsa.gui.forms;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.dew.ljsa.IClasse;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.rpc.DataNormalizer;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.CollectionAutoCompleter;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUIClassi extends AEntityEditor implements IClasse
{
  private static final long serialVersionUID = -997868234934127133L;
  
  protected ATableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords = new ArrayList<Map<String, Object>>();
  protected Map<String, Object> oLastRecordReaded;
  
  protected List<String> listPackages;
  
  protected CollectionAutoCompleter collAutoComp;
  
  public
  GUIClassi()
  {
    super();
  }
  
  public
  Object getCurrentSelection()
      throws Exception
  {
    return oLastRecordReaded;
  }
  
  protected
  void beforeBuildGUI()
  {
    listPackages = loadPackages();
  }
  
  protected
  Container buildGUIFilter()
  {
    FormPanel fp = new FormPanel("Ricerca");
    fp.addTab("Classe");
    fp.addRow();
    Component jtfClasse = fp.addTextField(sCLASSE, "Classe", 255);
    fp.addRow();
    fp.addTextField(sDESCRIZIONE, "Descrizione", 255);
    
    fp.build();
    
    CollectionAutoCompleter autoCompleter = new CollectionAutoCompleter(jtfClasse, listPackages);
    autoCompleter.setEnabledAutoCompletion(true);
    
    return fp;
  }
  
  protected
  Container buildGUIDetail()
  {
    DPClasseAttivita oDPClasseAttivita = new DPClasseAttivita();
    oDPClasseAttivita.setPreferredSize(new Dimension(0, 90));
    
    FormPanel fp = new FormPanel("Dettaglio");
    fp.addTab("Attributi");
    fp.addRow();
    fp.addBlankField();
    fp.addRow();
    Component jtfClasse = fp.addTextField(sCLASSE, "Classe", 255);
    fp.addRow();
    fp.addTextField(sDESCRIZIONE, "Descrizione", 255);
    
    fp.addTab("Attivita");
    fp.addRow();
    fp.addDataPanel(sATTIVITA, oDPClasseAttivita);
    
    fp.build();
    
    collAutoComp = new CollectionAutoCompleter(jtfClasse, listPackages);
    collAutoComp.setEnabledAutoCompletion(false);
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sCLASSE);
    oMandatoryFields.add(sDESCRIZIONE);
    
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
    String[] asCOLUMNS   = {"Classe", "Descrizione"};
    String[] asSYMBOLICS = {sCLASSE,  sDESCRIZIONE};
    
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
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
    oRecords = WUtil.toListOfMapObject(oRPCClient.execute("CLASSI.find", parameters, true));
    
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    
    if(oRecords.size() == 1) {
      ResourcesMgr.getStatusBar().setText("1 classe trovata.");
    }
    else {
      ResourcesMgr.getStatusBar().setText(oRecords.size() + " classi trovate.");
    }
  }
  
  protected
  void doReset()
      throws Exception
  {
    FormPanel fpFilter = (FormPanel) getFilterContainer();
    fpFilter.reset();
    
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
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return false;
    }
    
    Map<String, Object> oRecord = oRecords.get(iRow);
    
    String sClasse = (String) oRecord.get(sCLASSE);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sClasse);
    Map<String, Object> mapRead = WUtil.toMapObject(oRPCClient.execute("CLASSI.read", parameters));
    
    oLastRecordReaded = new HashMap<String, Object>(mapRead);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    fpDetail.setValues(mapRead);
    fpDetail.selectFirstTab();
    
    return true;
  }
  
  protected
  void doNew()
      throws Exception
  {
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    fpDetail.requestFocus(sCLASSE);
    
    oTable.clearSelection();
    oTable.setEnabled(false);
  }
  
  protected
  void doOpen()
      throws Exception
  {
    oTable.setEnabled(false);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    
    fpDetail.setEnabled(sCLASSE, false);
    
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
    
    Map<String, Object> mapDetailValues = fpDetail.getValues();
    
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(DataNormalizer.normalize(mapDetailValues));
    
    if(boNew) {
      boolean exists = WUtil.toBoolean(oRPCClient.execute("CLASSI.exists", parameters), false);
      if(exists) {
        GUIMessage.showWarning("Classe gi\340 presente in archivio.");
        return false;
      }
      
      String sClasse = (String) mapDetailValues.get(sCLASSE);
      if(sClasse != null) {
        int iLastDot = sClasse.lastIndexOf('.');
        if(iLastDot > 0) {
          String sPackage = sClasse.substring(0, iLastDot);
          if(listPackages != null && !listPackages.contains(sPackage)) {
            listPackages.add(sPackage);
          }
        }
      }
      
      Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("CLASSI.insert", parameters));
      
      // Evita il difetto consistente nell'attribuzione del focus al campo con
      // l'autocompletamento reso poi disabilitato ma ancora evidenziato.
      collAutoComp.setEnabledAutoCompletion(false);
      
      fpDetail.setValues(mapResult);
      oRecords.add(mapResult);
      iRowToSelect = oRecords.size() - 1;
    }
    else {
      Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("CLASSI.update", parameters));
      
      // Evita il difetto consistente nell'attribuzione del focus al campo con
      // l'autocompletamento reso poi disabilitato ma ancora evidenziato.
      collAutoComp.setEnabledAutoCompletion(false);
      
      fpDetail.setValues(mapResult);
      int iRow = oTable.getSelectedRow();
      oRecords.set(iRow, mapResult);
      iRowToSelect = iRow;
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
    String sClasse = (String) oRecord.get(sCLASSE);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sClasse);
    oRPCClient.execute("CLASSI.delete", parameters);
    
    oRecords.remove(iRow);
    oTable.clearSelection();
    oTableModel.notifyUpdates();
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
  }
  
  protected
  void checkActions(List<JButton> listDefActions, boolean boAllowEditing)
  {
    removeButtonByActionCommand(listDefActions, sACTION_TOGGLE);
    removeButtonByActionCommand(listDefActions, sACTION_PRINT);
  }
  
  protected
  void onChangeEditorStatus(int iStatus)
  {
    collAutoComp.setEnabledAutoCompletion(iStatus == iSTATUS_EDITING);
  }
  
  protected
  void doPrint()
      throws Exception
  {
  }
  
  protected
  boolean isElementEnabled()
  {
    return true;
  }
  
  protected
  void doToggle()
      throws Exception
  {
  }
  
  public static
  List<String> loadPackages()
  {
    List<String> listResult = null;
    try{
      IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
      listResult = WUtil.toListOfString(oRPCClient.execute("CLASSI.getPackages", new ArrayList<Object>()));
    }
    catch(Exception ex) {
      GUIMessage.showException("Errore durante il caricamento dei package", ex);
    }
    if(listResult == null) listResult = new ArrayList<String>();
    return listResult;
  }
}
