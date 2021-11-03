package org.dew.ljsa.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.util.ADataPanel;
import org.dew.swingup.util.ATableModelForSorter;
import org.dew.swingup.util.CodeAndDescription;
import org.dew.swingup.util.GUIUtil;
import org.dew.swingup.util.SimpleTableModelForSorter;
import org.dew.swingup.util.TableColumnResizer;
import org.dew.swingup.util.TableSorter;
import org.dew.swingup.util.TableUtils;

import org.dew.util.WUtil;

public
class DPSchedNotifica extends ADataPanel implements ISchedulazione, ActionListener, ListSelectionListener
{
  private static final long serialVersionUID = 6656747784115539812L;
  
  protected ATableModelForSorter oTableModel;
  protected JTable oTable;
  protected List<Map<String, Object>> oRecords;
  protected JScrollPane oScrollPane;
  
  protected JButton btnAdd;
  protected JButton btnRemove;
  
  protected JButton btnCopy;
  protected JButton btnPaste;
  
  public
  void setEnabled(boolean boEnabled)
  {
    super.setEnabled(boEnabled);
    oTable.setEnabled(boEnabled);
    btnAdd.setEnabled(boEnabled);
    btnRemove.setEnabled(false);
    
    btnCopy.setEnabled(oRecords != null && oRecords.size() > 0);
    btnPaste.setEnabled(boEnabled);
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
    
    btnCopy.setEnabled(oRecords != null && oRecords.size() > 0);
    btnPaste.setEnabled(isEnabled());
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
      else if(sActionCommand.equals("copy")) {
        doCopy();
      }
      else if(sActionCommand.equals("paste")) {
        doPaste();
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
    Map<String, Object> mapResult = GUISchedNotifica.showMe(null);
    if(mapResult == null) return;
    
    CodeAndDescription oEvento = (CodeAndDescription) mapResult.get(sNOT_EVENTO);
    String sEvento = null;
    if(oEvento != null) {
      sEvento = (String) oEvento.getCode();
    }
    else {
      return;
    }
    String sDestinazione = (String) mapResult.get(sNOT_DESTINAZIONE);
    Boolean oDaAttivita  = WUtil.toBooleanObj(mapResult.get(sNOT_DA_ATTIVITA), Boolean.FALSE);
    Boolean oCancellata  = WUtil.toBooleanObj(mapResult.get(sNOT_CANCELLATA),  Boolean.FALSE);
    
    if(!oCancellata.booleanValue() && exists(sEvento, sDestinazione)) {
      GUIMessage.showWarning("Notifica " + sEvento + ", " + sDestinazione + " gi\340 presente.");
      return;
    }
    
    Map<String, Object> mapRecord = new HashMap<String, Object>();
    mapRecord.put(sNOT_EVENTO,       sEvento);
    mapRecord.put(sNOT_DESTINAZIONE, sDestinazione);
    mapRecord.put(sNOT_DA_ATTIVITA,  oDaAttivita);
    mapRecord.put(sNOT_CANCELLATA,   oCancellata);
    
    oRecords.add(mapRecord);
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
    Boolean oDaAttivita = WUtil.toBooleanObj(oRecordToRemove.get(sNOT_DA_ATTIVITA), null);
    if(oDaAttivita != null && oDaAttivita.booleanValue()) {
      GUIMessage.showWarning("Non \350 possibile rimuovere notifiche di attivit\340. Sovrascriverne il valore anteponendo il segno meno.");
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
    
    Map<String, Object> mapValues = oRecords.get(iRow);
    
    Map<String, Object> mapResult = GUISchedNotifica.showMe(mapValues);
    if(mapResult == null) return;
    
    CodeAndDescription oEvento = (CodeAndDescription) mapResult.get(sNOT_EVENTO);
    String sEvento = null;
    if(oEvento != null) {
      sEvento = (String) oEvento.getCode();
    }
    else {
      return;
    }
    String sDestinazione = (String) mapResult.get(sNOT_DESTINAZIONE);
    Boolean oDaAttivita  = WUtil.toBooleanObj(mapResult.get(sNOT_DA_ATTIVITA), null);
    if(oDaAttivita == null) {
      oDaAttivita = Boolean.FALSE;
    }
    Boolean oCancellata = WUtil.toBooleanObj(mapResult.get(sNOT_CANCELLATA), null);
    if(oCancellata == null) {
      oCancellata = Boolean.FALSE;
    }
    
    Map<String, Object> mapRecord = new HashMap<String, Object>();
    mapRecord.put(sNOT_EVENTO,       sEvento);
    mapRecord.put(sNOT_DESTINAZIONE, sDestinazione);
    mapRecord.put(sNOT_DA_ATTIVITA,  oDaAttivita);
    mapRecord.put(sNOT_CANCELLATA,   oCancellata);
    
    oRecords.set(iRow, mapRecord);
    oTableModel.notifyUpdates();
    
    oTable.setRowSelectionInterval(iRow, iRow);
  }
  
  protected
  boolean exists(String sEvento, String sDestinazione)
  {
    if(oRecords == null || sEvento == null || sDestinazione == null) {
      return false;
    }
    
    for(int i = 0; i < oRecords.size(); i++) {
      Map<String, Object> mapRecord = oRecords.get(i);
      String sE = (String) mapRecord.get(sNOT_EVENTO);
      String sD = (String) mapRecord.get(sNOT_DESTINAZIONE);
      if(sEvento.equals(sE) && sDestinazione.equals(sD)) {
        return true;
      }
    }
    
    return false;
  }
  
  protected
  void doCopy()
  {
    int iRowsCopied = 0;
    String sSelection = "";
    for(int i = 0; i < oRecords.size(); i++) {
      Map<String, Object> mapRecord = oRecords.get(i);
      String sE = (String) mapRecord.get(sNOT_EVENTO);
      String sD = (String) mapRecord.get(sNOT_DESTINAZIONE);
      sSelection += sE + "\t" + sD + "\n";
      iRowsCopied++;
    }
    
    StringSelection oStringSelection = new StringSelection(sSelection);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(oStringSelection, null);
    
    GUIMessage.showInformation(iRowsCopied + " voci di notifica copiate.");
  }
  
  protected
  void doPaste()
  {
    List<String> listTextLines = getClipboardTextLines();
    if(listTextLines.size() == 0) {
      GUIMessage.showWarning("Non vi sono elementi di notifica copiati.");
      return;
    }
    
    List<Map<String, Object>> oRecordToPaste = new ArrayList<Map<String, Object>>();
    for(int i = 0; i < listTextLines.size(); i++) {
      String sLine = (String) listTextLines.get(i);
      String sE = null;
      String sD = null;
      int iIndexOfSep = sLine.indexOf('\t');
      if(iIndexOfSep <= 0) {
        sE = "R";
        sD = sLine.trim();
      }
      else {
        if(iIndexOfSep >= sLine.length() - 1) {
          continue;
        }
        sE = sLine.substring(0, 1);
        if("RET".indexOf(sE) < 0) {
          sE = "R";
        }
        sD = sLine.substring(iIndexOfSep + 1).trim();
      }
      
      if(exists(sE, sD)) continue;
      
      Map<String, Object> mapRecord = new HashMap<String, Object>();
      mapRecord.put(sNOT_EVENTO,       sE);
      mapRecord.put(sNOT_DESTINAZIONE, sD);
      oRecordToPaste.add(mapRecord);
    }
    
    if(oRecordToPaste.size() == 0) {
      GUIMessage.showWarning("Non vi sono nuove voci di notifica da incollare.");
      return;
    }
    
    String sMessage = "Saranno incollate " + oRecordToPaste.size() + " voci di notifica. Continuare?\n";
    int iEnd = oRecordToPaste.size();
    boolean boCutted = false;
    if(iEnd > 5) {
      iEnd = 5;
      boCutted = true;
    }
    for(int i = 0; i < iEnd; i++) {
      Map<String, Object> mapRecord = oRecordToPaste.get(i);
      sMessage += mapRecord.get(sNOT_EVENTO);
      sMessage += " - " + mapRecord.get(sNOT_DESTINAZIONE) + "\n";
    }
    if(boCutted) {
      sMessage += " ...\n";
    }
    boolean boConfirm = GUIMessage.getConfirmation(sMessage);
    
    if(!boConfirm) return;
    
    oRecords.addAll(oRecordToPaste);
    oTableModel.notifyUpdates();
    
    GUIMessage.showInformation(oRecordToPaste.size() + " voci di notifica incollate.");
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
    String[] asCOLUMNS   = {"Evento",    "Destinazione"};
    String[] asSYMBOLICS = {sNOT_EVENTO, sNOT_DESTINAZIONE};
    
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
        boolean boDaAttivita = WUtil.toBoolean(oRecord.get(sNOT_DA_ATTIVITA), false);
        boolean boCancellata = WUtil.toBoolean(oRecord.get(sNOT_CANCELLATA),  false);
        
        if(boDaAttivita) {
          if(boCancellata) {
            this.setForeground(Color.red);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
          else {
            this.setForeground(Color.gray);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
        }
        else {
          if(boCancellata) {
            this.setForeground(Color.red);
            this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
          }
          else {
            this.setForeground(Color.black);
            this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
          }
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
    
    btnCopy = new JButton(ResourcesMgr.getImageIcon(IConstants.sICON_COPY));
    btnCopy.setToolTipText("Copia");
    btnCopy.setActionCommand("copy");
    btnCopy.setMargin(new Insets(1, 1, 1, 1));
    btnCopy.addActionListener(this);
    btnCopy.setEnabled(false);
    
    btnPaste = new JButton(ResourcesMgr.getImageIcon(IConstants.sICON_PASTE));
    btnPaste.setToolTipText("Incolla");
    btnPaste.setActionCommand("paste");
    btnPaste.setMargin(new Insets(1, 1, 1, 1));
    btnPaste.addActionListener(this);
    btnPaste.setEnabled(true);
    
    JPanel oClipboardPanel = new JPanel(new GridLayout(1, 3));
    oClipboardPanel.add(new JPanel());
    oClipboardPanel.add(btnCopy);
    oClipboardPanel.add(btnPaste);
    
    JPanel oButtonsPanel = new JPanel(new GridLayout(3, 1));
    oButtonsPanel.add(btnAdd);
    oButtonsPanel.add(btnRemove);
    oButtonsPanel.add(oClipboardPanel);
    
    JPanel oResult = new JPanel(new BorderLayout());
    oResult.add(oButtonsPanel, BorderLayout.NORTH);
    return oResult;
  }
  
  protected
  String getClipboardText()
  {
    String sResult = null;
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable transferable = clipboard.getContents(this);
    Object oData = null;
    try {
      oData = transferable.getTransferData(DataFlavor.stringFlavor);
      if(oData != null) sResult = oData.toString();
    }
    catch(Exception ex) {
    }
    return sResult;
  }
  
  protected
  List<String> getClipboardTextLines()
  {
    List<String> listResult = new ArrayList<String>();
    
    String sText = getClipboardText();
    if(sText == null || sText.trim().length() == 0) return listResult;
    
    StringTokenizer st = new StringTokenizer(sText, "\n");
    while(st.hasMoreTokens()) {
      listResult.add(st.nextToken());
    }
    
    return listResult;
  }
}
