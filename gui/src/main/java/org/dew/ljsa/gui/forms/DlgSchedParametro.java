package org.dew.ljsa.gui.forms;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextField;

import org.dew.swingup.AJDialog;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.util.CollectionAutoCompleter;
import org.dew.swingup.util.FormPanel;
import org.dew.swingup.util.StringToObject;

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.ISchedulazione;

public
class DlgSchedParametro extends AJDialog implements ISchedulazione
{
  private static final long serialVersionUID = 1025760532655783957L;
  
  protected FormPanel oFormPanel;
  protected boolean boOverWrite = false;
  protected List<String> listHints;
  
  public
  DlgSchedParametro()
  {
    super("Parametro");
    this.setSize(500, 320);
  }
  
  public static
  Map<String, Object> showMe(Map<String, Object> mapValues)
  {
    DlgSchedParametro dialog = new DlgSchedParametro();
    
    if(mapValues != null) {
      dialog.setValues(mapValues, false);
    }
    else {
      dialog.hideDescrizioneValori();
    }
    
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    
    if(dialog.isCancel()) {
      return null;
    }
    
    return dialog.getParametro();
  }
  
  public static
  Map<String, Object> showMe(Map<String, Object> mapValues, boolean boEditFlag)
  {
    DlgSchedParametro dialog = new DlgSchedParametro();
    
    if(mapValues != null) {
      dialog.setValues(mapValues, boEditFlag);
    }
    else {
      dialog.hideDescrizioneValori();
    }
    
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    
    if(dialog.isCancel()) {
      return null;
    }
    
    return dialog.getParametro();
  }
  
  public
  void setValues(Map<String, Object> mapValues, boolean boEditFlag)
  {
    Boolean oOverWrite = (Boolean) mapValues.get(sPAR_OVERWRITE);
    if(oOverWrite != null) {
      boOverWrite = oOverWrite.booleanValue();
    }
    else {
      boOverWrite = false;
    }
    
    oFormPanel.setValues(mapValues);
    
    if(!boEditFlag) {
      oFormPanel.setEnabled(sPAR_PARAMETRO, false);
      oFormPanel.setDefaultFocus(sPAR_VALORE);
    }
  }
  
  public
  void hideDescrizioneValori()
  {
    oFormPanel.setVisible(IAttivita.sPAR_DESCRIZIONE, false);
    oFormPanel.setVisible(IAttivita.sPAR_VALORI, false);
    this.setSize(400, 200);
  }
  
  public
  Map<String, Object> getParametro()
  {
    return oFormPanel.getValues();
  }
  
  protected
  Container buildGUI()
      throws Exception
  {
    oFormPanel = new FormPanel("Parametro");
    oFormPanel.addRow();
    oFormPanel.addTextField(sPAR_PARAMETRO, "Parametro", 255);
    oFormPanel.addRow();
    oFormPanel.addNoteField(IAttivita.sPAR_DESCRIZIONE, "Descrizione", 3, 255);
    oFormPanel.addRow();
    oFormPanel.addNoteField(IAttivita.sPAR_VALORI, "Valori", 3, 255);
    oFormPanel.addRow();
    oFormPanel.addNoteField(sPAR_VALORE, "Valore", 3, 1024);
    oFormPanel.addHiddenField(IAttivita.sPAR_PREDEFINITO);
    oFormPanel.addHiddenField(sPAR_DA_ATTIVITA);
    oFormPanel.addHiddenField(sPAR_OVERWRITE);
    
    oFormPanel.build();
    
    oFormPanel.setEnabled(IAttivita.sPAR_DESCRIZIONE, false);
    oFormPanel.setEnabled(IAttivita.sPAR_VALORI, false);
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sPAR_PARAMETRO);
    oFormPanel.setMandatoryFields(oMandatoryFields);
    
    Component compParametro = oFormPanel.getComponent(sPAR_PARAMETRO);
    if(compParametro instanceof JTextField) {
      buildHints();
      
      new CollectionAutoCompleter(compParametro, listHints, true);
    }
    
    return oFormPanel;
  }
  
  public
  void onOpened()
  {
  }
  
  public
  void onActivated()
  {
  }
  
  public
  boolean doCancel()
  {
    return true;
  }
  
  public
  boolean doOk()
  {
    String sCheckMandatory = oFormPanel.getStringCheckMandatories();
    if(sCheckMandatory.length() > 0) {
      GUIMessage.showWarning("Occorre valorizzare i seguenti campi:\n" + sCheckMandatory);
      return false;
    }
    
    if(!boOverWrite) {
      Boolean oDaAttivita = (Boolean) oFormPanel.getValue(sPAR_DA_ATTIVITA);
      if(oDaAttivita != null && oDaAttivita.booleanValue()) {
        String sPredefinito = (String) oFormPanel.getValue(IAttivita.sPAR_PREDEFINITO);
        if(sPredefinito == null) {
          oFormPanel.setValue(IAttivita.sPAR_PREDEFINITO, "");
          sPredefinito = "";
        }
        String sValore = (String) oFormPanel.getValue(sPAR_VALORE);
        oFormPanel.setValue(sPAR_OVERWRITE, new Boolean(!sPredefinito.equals(sValore)));
      }
    }
    
    return true;
  }
  
  protected static
  Vector<String> getValori(Map<String, Object> mapValues)
  {
    String sValori = (String) mapValues.get(IAttivita.sPAR_VALORI);
    
    if(sValori == null) {
      return null;
    }
    
    List<?> listValues = null;
    try {
      listValues = (List<?>) StringToObject.parse("[" + sValori + "]");
    }
    catch(Exception ex) {
    }
    
    if(listValues == null || listValues.size() == 0) {
      return null;
    }
    
    Vector<String> vResult = new Vector<String>();
    for(int i = 0; i < listValues.size(); i++) {
      Object object = listValues.get(i);
      if(object != null) {
        vResult.add(object.toString());
      }
    }
    
    return vResult;
  }
  
  protected
  void buildHints()
  {
    listHints = new ArrayList<String>(15);
    
    listHints.add("command");
    listHints.add("exception");
    listHints.add("fromDate");
    listHints.add("name");
    listHints.add("sleep");
    listHints.add("sql");
    listHints.add("sql.1");
    listHints.add("sql.2");
    listHints.add("sql.3");
    listHints.add("sql.4");
    listHints.add("table");
    listHints.add("text");
    listHints.add("title");
    listHints.add("toDate");
    listHints.add("type");
  }
}
