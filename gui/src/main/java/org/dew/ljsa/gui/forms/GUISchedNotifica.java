package org.dew.ljsa.gui.forms;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dew.ljsa.ISchedulazione;

import org.dew.swingup.AJDialog;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.util.CodeAndDescription;
import org.dew.swingup.util.FormPanel;

import org.dew.util.WUtil;

public
class GUISchedNotifica extends AJDialog implements ISchedulazione
{
  private static final long serialVersionUID = 5505298169575448320L;
  
  protected FormPanel oFormPanel;
  protected String sDestinazionePrec;
  
  public
  GUISchedNotifica()
  {
    super("Notifica");
    this.setSize(500, 190);
  }
  
  public static
  Map<String, Object> showMe(Map<String, Object> mapValues)
  {
    GUISchedNotifica dialog = new GUISchedNotifica();
    
    if(mapValues != null) {
      dialog.setValues(mapValues);
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
  void setValues(Map<String, Object> mapValues)
  {
    sDestinazionePrec = (String) mapValues.get(sNOT_DESTINAZIONE);
    if(sDestinazionePrec == null) sDestinazionePrec = "";
    
    Boolean oCancellata = WUtil.toBooleanObj(mapValues.get(sNOT_CANCELLATA), null);
    if(oCancellata != null && oCancellata.booleanValue()) {
      String sDestinazione = (String) mapValues.get(sNOT_DESTINAZIONE);
      mapValues.put(sNOT_DESTINAZIONE, "-" + sDestinazione);
    }
    
    oFormPanel.setValues(mapValues);
    oFormPanel.requestFocus(sNOT_EVENTO);
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
    Vector<CodeAndDescription> vEventi = new Vector<CodeAndDescription>();
    vEventi.add(new CodeAndDescription("R", "R - Risultato elaborazione"));
    vEventi.add(new CodeAndDescription("E", "E - Eccezione verificatasi"));
    vEventi.add(new CodeAndDescription("T", "T - Timeout raggiunto"));
    
    oFormPanel = new FormPanel("Notifica");
    oFormPanel.addRow();
    oFormPanel.addOptionsField(sNOT_EVENTO, "Evento", vEventi);
    oFormPanel.addRow();
    oFormPanel.addBlankField();
    oFormPanel.addRow();
    oFormPanel.addTextField(sNOT_DESTINAZIONE, "Destinaz. (email)", 255);
    oFormPanel.addHiddenField(sNOT_DA_ATTIVITA);
    oFormPanel.addHiddenField(sNOT_CANCELLATA);
    
    Map<String, Object> mapDefaultValues = new HashMap<String, Object>();
    mapDefaultValues.put(sNOT_EVENTO, "R");
    oFormPanel.setDefaultValues(mapDefaultValues);
    
    oFormPanel.build();
    
    List<String> oMandatoryFields = new ArrayList<String>();
    oMandatoryFields.add(sNOT_EVENTO);
    oMandatoryFields.add(sNOT_DESTINAZIONE);
    oFormPanel.setMandatoryFields(oMandatoryFields);
    
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
    
    String sDestinazione = (String) oFormPanel.getValue(sNOT_DESTINAZIONE);
    if(sDestinazione != null && sDestinazione.startsWith("-")) {
      if(sDestinazione.length() < 2) {
        GUIMessage.showWarning("Destinazione non valida.");
        return false;
      }
      if(sDestinazione.length() > 1) {
        sDestinazione = sDestinazione.substring(1);
        oFormPanel.setValue(sNOT_DESTINAZIONE, sDestinazione);
      }
      oFormPanel.setValue(sNOT_CANCELLATA, Boolean.TRUE);
    }
    else {
      oFormPanel.setValue(sNOT_CANCELLATA, Boolean.FALSE);
    }
    
    if(sDestinazionePrec == null || !sDestinazionePrec.equals(sDestinazione)) {
      oFormPanel.setValue(sNOT_DA_ATTIVITA, Boolean.FALSE);
    }
    
    return true;
  }
}
