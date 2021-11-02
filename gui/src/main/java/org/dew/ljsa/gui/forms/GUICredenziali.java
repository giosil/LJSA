package org.dew.ljsa.gui.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
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
import javax.swing.table.DefaultTableCellRenderer;

import org.dew.ljsa.ICredenziale;
import org.dew.ljsa.gui.AppUtil;
import org.dew.ljsa.gui.DecodifiableFactory;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.rpc.DataNormalizer;
import org.dew.swingup.rpc.IRPCClient;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class GUICredenziali extends AEntityEditor implements ICredenziale
{
  private static final long serialVersionUID = -987659684450873386L;
  
  protected ATableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords = new ArrayList<Map<String, Object>>();
  protected Map<String, Object> oLastRecordReaded;
  
  public
  GUICredenziali()
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
  }
  
  protected
  Container buildGUIFilter()
  {
    FormPanel fp = new FormPanel("Ricerca");
    fp.addTab("Filtro");
    fp.addRow();
    fp.addComponent(sID_SERVIZIO,    "Servizio", DecodifiableFactory.buildDCServizio());
    fp.addTextField(sID_CREDENZIALE, "Credenziale", 50);
    fp.addTextField(sEMAIL,          "Email", 100);
    fp.build();
    
    return fp;
  }
  
  protected
  Container buildGUIDetail()
  {
    FormPanel fp = new FormPanel("Dettaglio");
    fp.addTab("Attributi");
    fp.addRow();
    fp.addComponent(sID_SERVIZIO,     "Servizio", DecodifiableFactory.buildDCServizio());
    fp.addTextField(sID_CREDENZIALE,  "Credenziale", 50);
    fp.addRow();
    fp.addPasswordField(sCREDENZIALI, "Password");
    fp.addTextField(sEMAIL,           "Email", 100);
    fp.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sID_SERVIZIO);
    oMandatoryFields.add(sID_CREDENZIALE);
    
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
    String[] asCOLUMNS   = {"Servizio",   "Credenziale",   "Email"};
    String[] asSYMBOLICS = {sID_SERVIZIO, sID_CREDENZIALE, sEMAIL};
    
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
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
    oRecords = WUtil.toListOfMapObject(oRPCClient.execute("CREDENZIALI.find", parameters, true));
    
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
    
    FormPanel fpDetail = (FormPanel) getDetailContainer();
    fpDetail.reset();
    
    if(oRecords.size() == 1) {
      ResourcesMgr.getStatusBar().setText("1 credenziale trovata.");
    }
    else {
      ResourcesMgr.getStatusBar().setText(oRecords.size() + " credenziali trovate.");
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
    
    String sIdServizio    = (String) oRecord.get(sID_SERVIZIO);
    String sIdCredenziale = (String) oRecord.get(sID_CREDENZIALE);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdCredenziale);
    Map<String, Object> mapRead = WUtil.toMapObject(oRPCClient.execute("CREDENZIALI.read", parameters));
    
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
    fpDetail.requestFocus(sID_SERVIZIO);
    
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
    fpDetail.setEnabled(sID_CREDENZIALE, false);
    
    fpDetail.getComponent(sEMAIL).requestFocus();
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
      boolean exists = WUtil.toBoolean(oRPCClient.execute("CREDENZIALI.exists", parameters), false);
      if(exists) {
        GUIMessage.showWarning("Credenziale gi\340 presente in archivio.");
        return false;
      }
      
      Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("CREDENZIALI.insert", parameters));
      
      fpDetail.setValues(mapResult);
      oRecords.add(mapResult);
      iRowToSelect = oRecords.size() - 1;
    }
    else {
      Map<String, Object> mapResult = WUtil.toMapObject(oRPCClient.execute("CREDENZIALI.update", parameters));
      
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
    String sIdServizio    = (String) oRecord.get(sID_SERVIZIO);
    String sIdCredenziale = (String) oRecord.get(sID_CREDENZIALE);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    List<String> parameters = new ArrayList<String>();
    parameters.add(sIdServizio);
    parameters.add(sIdCredenziale);
    oRPCClient.execute("CREDENZIALI.delete", parameters);
    
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
  }
  
  protected
  void onChangeEditorStatus(int iStatus)
  {
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
    return WUtil.toBoolean(record.get(sATTIVO), true);
  }
  
  protected
  void doToggle()
      throws Exception
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0) return;
    
    Map<String, Object> record = oRecords.get(iRow);
    String  sIdServizio    = (String) record.get(sID_SERVIZIO);
    String  sIdCredenziale = (String) record.get(sID_CREDENZIALE);
    boolean boAttivo       = WUtil.toBoolean(record.get(sATTIVO), true);
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(sIdServizio);
    parameters.add(sIdCredenziale);
    parameters.add(!boAttivo);
    
    Boolean result = WUtil.toBooleanObj(oRPCClient.execute("CREDENZIALI.setEnabled", parameters), true);
    
    record.put(sATTIVO, result);
    oLastRecordReaded.put(sATTIVO, result);
  }
}

