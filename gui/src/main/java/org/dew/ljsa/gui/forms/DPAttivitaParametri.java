package org.dew.ljsa.gui.forms;

import java.awt.BorderLayout;
import java.awt.Container;
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

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.gui.dialogs.DlgAttivitaParametro;
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
class DPAttivitaParametri extends ADataPanel implements IAttivita, ActionListener, ListSelectionListener
{
  private static final long serialVersionUID = 8576328432609710872L;
  
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
    if(e.getValueIsAdjusting()) {
      return;
    }
    
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
    Map<String, Object> mapResult = DlgAttivitaParametro.showMe(null);
    if(mapResult == null) return;
    
    String sParametro = (String) mapResult.get(sPAR_PARAMETRO);
    
    if(exist(sParametro)) {
      GUIMessage.showWarning("Parametro " + sParametro + " gi\340 presente.");
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
    
    Map<String, Object> mapResult = DlgAttivitaParametro.showMe(mapParamToOpen);
    if(mapResult == null) return;
    
    oRecords.set(iRow, mapResult);
    oTableModel.notifyUpdates();
    
    oTable.setRowSelectionInterval(iRow, iRow);
  }
  
  protected
  boolean exist(String sItem)
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
    String[] asCOLUMNS   = {"Parametro",    "Descrizione",    "Valori",    "Predefinito"};
    String[] asSYMBOLICS = {sPAR_PARAMETRO, sPAR_DESCRIZIONE, sPAR_VALORI, sPAR_PREDEFINITO};
    
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
