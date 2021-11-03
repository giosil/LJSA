package org.dew.ljsa.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.dew.ljsa.ISchedulazione;

import org.dew.swingup.GUIMessage;
import org.dew.swingup.IConstants;
import org.dew.swingup.util.ADataPanel;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class DPSchedConfigurazione extends ADataPanel implements ISchedulazione, ActionListener, ListSelectionListener
{
  private static final long serialVersionUID = -6979954681689942104L;
  
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
    Map<String, Object> mapResult = GUISchedConfigurazione.showMe(null);
    if(mapResult == null) return;
    
    String sOpzione = WUtil.toString(mapResult.get(sCONF_OPZIONE), null);
    if(exists(sOpzione)) {
      GUIMessage.showWarning("Opzione " + sOpzione + " gi\340 presente.");
      return;
    }
    
    oRecords.add(mapResult);
    oTableModel.notifyUpdates();
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JScrollBar vs = oScrollPane.getVerticalScrollBar();
        vs.setValue(vs.getMaximum());
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
    Boolean oDaAttivita = (Boolean) oRecordToRemove.get(sCONF_DA_ATTIVITA);
    if(oDaAttivita != null && oDaAttivita.booleanValue()) {
      GUIMessage.showWarning("Non \350 possibile rimuovere configurazioni di attivit\340. Sovrascriverne il valore.");
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
    
    Map<String, Object> mapConfToOpen = oRecords.get(iRow);
    
    Map<String, Object> mapResult = GUISchedConfigurazione.showMe(mapConfToOpen);
    if(mapResult == null) {
      return;
    }
    
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
      if(sItem.equals(mapRecord.get(sCONF_OPZIONE))) {
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
    String[] asCOLUMNS   = {"Opzione",     "Descrizione",     "Valore"};
    String[] asSYMBOLICS = {sCONF_OPZIONE, sCONF_DESCRIZIONE, sCONF_VALORE};
    
    oRecords = new ArrayList<Map<String, Object>>();
    oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
    
    oTable = new JTable(oTableModel);
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    oTable.setColumnSelectionAllowed(false);
    oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        boolean boDaAttivita = WUtil.toBoolean(oRecord.get(sCONF_DA_ATTIVITA), false);
        boolean boOverWrite  = WUtil.toBoolean(oRecord.get(sCONF_OVERWRITE), false);
        
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
    
    TableUtils.setMonospacedFont(oTable);
    
    oScrollPane = new JScrollPane(oTable);
    TableColumnResizer.setResizeColumnsListeners(oTable);
    TableSorter.setSorterListener(oTable);
    
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
