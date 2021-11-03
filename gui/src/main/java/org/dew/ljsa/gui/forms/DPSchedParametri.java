package org.dew.ljsa.gui.forms;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;
import java.util.List;

import org.dew.swingup.util.*;
import org.dew.util.WUtil;
import org.dew.swingup.*;

import org.dew.ljsa.*;

public
class DPSchedParametri extends ADataPanel implements ISchedulazione, ActionListener, ListSelectionListener
{
  private static final long serialVersionUID = 4127555720297174464L;
  
  protected ATableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords;
  protected JScrollPane oScrollPane;
  
  protected JButton btnAdd;
  protected JButton btnRemove;
  
  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    oTable.setEnabled(boEnabled);
    btnAdd.setEnabled(boEnabled);
    btnRemove.setEnabled(false);
  }
  
  public
  void setData(Object oData)
  {
    oRecords = new ArrayList<Map<String, Object>>();
    if(oData instanceof List) {
      oRecords.addAll(WUtil.toListOfMapObject(oData));
    }
    oTableModel.setData(oRecords);
    TableSorter.resetHeader(oTable);
  }
  
  public
  Object getData()
  {
    return oRecords;
  }
  
  public
  void actionPerformed(ActionEvent e)
  {
    String sActionCommand = e.getActionCommand();
    if(sActionCommand == null) return;
    
    try {
      if(sActionCommand.equals("add")) {
        addItem();
      }
      else if(sActionCommand.equals("remove")) {
        removeItem();
      }
    }
    catch(Exception ex) {
      GUIMessage.showException(ex);
    }
  }
  
  public
  void valueChanged(ListSelectionEvent e)
  {
    if(e.getValueIsAdjusting()) return;
    
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      btnRemove.setEnabled(false);
      return;
    }
    
    btnRemove.setEnabled(true);
  }
  
  protected
  void addItem()
      throws Exception
  {
    Map<String, Object> mapResult = DlgSchedParametro.showMe(null);
    if(mapResult == null) return;
    
    String sParametro = WUtil.toString(mapResult.get(sPAR_PARAMETRO), null);
    
    if(exists(sParametro)) {
      GUIMessage.showWarning("Parametro " + sParametro + " gi\340 presente.");
      return;
    }
    
    oRecords.add(mapResult);
    oTableModel.notifyUpdates();
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JScrollBar verticalScrollBar = oScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
      }
    });
  }
  
  protected
  void removeItem()
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return;
    }
    
    Map<String, Object> oRecordToRemove = oRecords.get(iRow);
    Boolean oDaAttivita = WUtil.toBooleanObj(oRecordToRemove.get(sPAR_DA_ATTIVITA), null);
    if(oDaAttivita != null && oDaAttivita.booleanValue()) {
      GUIMessage.showWarning("Non \350 possibile rimuovere parametri di attivit\340. Sovrascriverne il valore.");
      return;
    }
    
    oRecords.remove(iRow);
    oTableModel.notifyUpdates();
  }
  
  protected
  void doOpen()
  {
    int iRow = oTable.getSelectedRow();
    if(iRow < 0 || iRow >= oRecords.size()) {
      return;
    }
    
    Map<String, Object> mapParamToOpen = oRecords.get(iRow);
    
    Map<String, Object> mapResult = DlgSchedParametro.showMe(mapParamToOpen);
    if(mapResult == null) return;
    
    oRecords.set(iRow, mapResult);
    oTableModel.notifyUpdates();
    
    oTable.setRowSelectionInterval(iRow, iRow);
  }
  
  protected
  boolean exists(String sItem)
  {
    if(oRecords == null || sItem == null) {
      return false;
    }
    
    for(int i = 0; i < oRecords.size(); i++) {
      Map<String, Object> mapRecord = oRecords.get(i);
      String sId = (String) mapRecord.get(sPAR_PARAMETRO);
      if(sItem.equalsIgnoreCase(sId)) {
        return true;
      }
    }
    
    return false;
  }
  
  protected
  Container buildGUI()
      throws Exception
  {
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(buildTablePanel(),   BorderLayout.CENTER);
    oResult.add(buildButtonsPanel(), BorderLayout.EAST);
    return oResult;
  }
  
  protected
  Container buildTablePanel()
  {
    String[] asCOLUMNS   = {"Parametro",    "Descrizione",    "Valore"};
    String[] asSYMBOLICS = {sPAR_PARAMETRO, sPAR_DESCRIZIONE, sPAR_VALORE};
    
    oRecords = new ArrayList<Map<String, Object>>();
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    TableUtils.setMonospacedFont(oTable);
    
    oScrollPane = new JScrollPane(oTable);
    TableColumnResizer.setResizeColumnsListeners(oTable);
    TableSorter.setSorterListener(oTable);
    
    oTable.getSelectionModel().addListSelectionListener(this);
    
    oTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() >= 2 && !e.isControlDown()) {
          try {
            doOpen();
          }
          catch(Exception ex) {
            GUIMessage.showException(ex);
          }
        }
      }
    });
    
    oTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 1L;
      public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int col) {
        super.getTableCellRendererComponent(table, value, selected, focus, row, col);
        
        Map<String, Object> oRecord = oRecords.get(row);
        
        boolean boDaAttivita = WUtil.toBoolean(oRecord.get(sPAR_DA_ATTIVITA), false);
        boolean boOverWrite = WUtil.toBoolean(oRecord.get(sPAR_OVERWRITE),    false);
        
        if(boDaAttivita) {
          if(boOverWrite) {
            this.setForeground(Color.black);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
          else {
            this.setForeground(Color.gray);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
        }
        else {
          this.setForeground(Color.black);
          this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        }
        
        return this;
      }
    });
    
    return oScrollPane;
  }
  
  protected
  Container buildButtonsPanel()
  {
    btnAdd = GUIUtil.buildActionButton(IConstants.sGUIDATA_PLUS, "add");
    btnAdd.addActionListener(this);
    
    btnRemove = GUIUtil.buildActionButton(IConstants.sGUIDATA_MINUS, "remove");
    btnRemove.addActionListener(this);
    btnRemove.setEnabled(false);
    
    JPanel oButtonsPanel = new JPanel(new GridLayout(3, 1));
    oButtonsPanel.add(btnAdd);
    oButtonsPanel.add(btnRemove);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oButtonsPanel, BorderLayout.NORTH);
    return oResult;
  }
}
