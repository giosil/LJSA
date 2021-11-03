package org.dew.ljsa.gui.forms;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import org.dew.ljsa.IAttivita;

import org.dew.swingup.AJDialog;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.util.CollectionAutoCompleter;
import org.dew.swingup.util.FormPanel;

public
class GUIAttivitaParametro extends AJDialog implements IAttivita
{
  private static final long serialVersionUID = 1411586237456326021L;
  
  protected FormPanel oFormPanel;
  protected List<String> listHints;
  
  public
  GUIAttivitaParametro()
  {
    super("Parametro");
    this.setSize(550, 320);
  }
  
  public static
  Map<String, Object> showMe(Map<String, Object> mapValues)
  {
    GUIAttivitaParametro dialog = new GUIAttivitaParametro();
    
    if(mapValues != null) {
      dialog.setValues(mapValues, false);
    }
    
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    
    if(dialog.isCancel()) return null;
    
    return dialog.getParametro();
  }
  
  public static
  Map<String, Object> showMe(Map<String, Object> mapValues, boolean boEditFlag)
  {
    GUIAttivitaParametro dialog = new GUIAttivitaParametro();
    
    if(mapValues != null) {
      dialog.setValues(mapValues, boEditFlag);
    }
    
    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
    
    dialog.setVisible(true);
    
    if(dialog.isCancel()) return null;
    
    return dialog.getParametro();
  }
  
  public
  void setValues(Map<String, Object> mapValues, boolean boEditFlag)
  {
    oFormPanel.setValues(mapValues);
    
    if(!boEditFlag) {
      oFormPanel.setEnabled(sPAR_PARAMETRO, false);
      oFormPanel.setDefaultFocus(sPAR_DESCRIZIONE);
    }
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
    oFormPanel.addNoteField(sPAR_DESCRIZIONE, "Descrizione", 3, 255);
    oFormPanel.addRow();
    oFormPanel.addNoteField(sPAR_VALORI, "Valori", 3, 255);
    oFormPanel.addRow();
    oFormPanel.addNoteField(sPAR_PREDEFINITO, "Predefinito", 3, 1024);
    
    oFormPanel.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sPAR_PARAMETRO);
    oMandatoryFields.add(sPAR_DESCRIZIONE);
    oFormPanel.setMandatoryFields(oMandatoryFields);
    
    Component compParametro = oFormPanel.getComponent(sPAR_PARAMETRO);
    if(compParametro instanceof JTextField) {
      buildHints();
      
      new CollectionAutoCompleter(compParametro, listHints, true);
      
      Component compDescrizione = oFormPanel.getComponent(sPAR_DESCRIZIONE);
      compDescrizione.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          setDescriptionHint();
        }
      });
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
    
    return true;
  }
  
  protected
  void buildHints()
  {
    listHints = new ArrayList<String>(16);
    
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
    listHints.add("sql.5");
    listHints.add("table");
    listHints.add("text");
    listHints.add("title");
    listHints.add("toDate");
    listHints.add("type");
  }
  
  protected
  void setDescriptionHint()
  {
    String sDescHint = null;
    
    String sChiave      = (String) oFormPanel.getValue(sPAR_PARAMETRO);
    String sDescrizione = (String) oFormPanel.getValue(sPAR_DESCRIZIONE);
    
    if(sChiave == null || sChiave.length() == 0) {
      sDescHint = null;
    }
    if(sChiave.equals("command")) {
      sDescHint = "Comando sistema operativo";
    }
    else if(sChiave.equals("exception")) {
      sDescHint = "[LJTest] Simulazione di eccezione";
    }
    else if(sChiave.equals("fromDate")) {
      sDescHint = "Dalla data (YYYYMMGG)";
    }
    else if(sChiave.equals("name")) {
      sDescHint = "Nome";
    }
    else if(sChiave.equals("sleep")) {
      sDescHint = "[LJTest] Simulazione di elaborazione prolungata (in secondi)";
    }
    else if(sChiave.equals("sql")) {
      sDescHint = "Comando sql";
    }
    else if(sChiave.startsWith("sql.")) {
      sDescHint = sChiave;
    }
    else if(sChiave.equals("table")) {
      sDescHint = "Nome tabella";
    }
    else if(sChiave.equals("text")) {
      sDescHint = "Testo";
    }
    else if(sChiave.equals("title")) {
      sDescHint = "Titolo report";
    }
    else if(sChiave.equals("toDate")) {
      sDescHint = "Alla data (YYYYMMGG)";
    }
    else if(sChiave.equals("type")) {
      sDescHint = "Tipo report";
    }
    
    if(sDescHint != null) {
      if(sDescrizione == null || sDescrizione.length() == 0) {
        oFormPanel.setValue(sPAR_DESCRIZIONE, sDescHint);
      }
    }
  }
}
