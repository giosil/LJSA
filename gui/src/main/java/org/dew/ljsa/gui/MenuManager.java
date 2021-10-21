package org.dew.ljsa.gui;

import java.util.HashMap;
import java.util.Map;

import org.dew.ljsa.gui.forms.GUIAttivita;
import org.dew.ljsa.gui.forms.GUIClassi;
import org.dew.ljsa.gui.forms.GUICredenziali;
import org.dew.ljsa.gui.forms.GUILogSchedulazioni;
import org.dew.ljsa.gui.forms.GUISchedulatore;
import org.dew.ljsa.gui.forms.GUISchedulazioni;
import org.dew.ljsa.gui.forms.GUIServizi;

import org.dew.swingup.ASimpleMenuManager;
import org.dew.swingup.AWorkPanel;
import org.dew.swingup.ResourcesMgr;

import org.dew.swingup.editors.AEntityEditor;
import org.dew.swingup.editors.EntityInternalFrame;
import org.dew.swingup.editors.IEntityMgr;

public
class MenuManager extends ASimpleMenuManager
{
  public final static String sICON_SERVIZI           = "OpenProjectLarge.gif";
  public final static String sICON_CREDENZIALI       = "DocumentLockLarge.gif";
  public final static String sICON_CLASSI            = "ObjectLarge.gif";
  public final static String sICON_ATTIVITA          = "HelpIndexLarge.gif";
  public final static String sICON_SCHEDULAZIONI     = "HourglassLarge.gif";
  public final static String sICON_SCHEDULATORE      = "ExecuteProjectLarge.gif";
  public final static String sICON_LOG_SCHEDULAZIONI = "BookLarge.gif";
  
  public
  void enable(String userRole)
  {
    Map<String, Object> enablings = new HashMap<String, Object>();
    enablings.put("ljsa.servizi",       true);
    enablings.put("ljsa.credenziali",   true);
    enablings.put("ljsa.classi",        true);
    enablings.put("ljsa.attivita",      true);
    enablings.put("ljsa.schedulazioni", true);
    enablings.put("ljsa.log",           true);
    enablings.put("ljsa.schedulatore",  true);
    
    super.setEnabled(enablings);
  }
  
  protected
  void onClick(String idItem)
  {
    AWorkPanel workPanel = ResourcesMgr.getWorkPanel();
    
    if(idItem.equals("ljsa.servizi")) {
      AEntityEditor entityEditor = new GUIServizi();
      IEntityMgr entityMgr = new EntityInternalFrame();
      entityMgr.init(entityEditor, "Gestione Servizi", sICON_SERVIZI);
      workPanel.show(entityMgr);
    }
    else if(idItem.equals("ljsa.credenziali")) {
      AEntityEditor entityEditor = new GUICredenziali();
      IEntityMgr entityMgr = new EntityInternalFrame();
      entityMgr.init(entityEditor, "Gestione Credenziali", sICON_CREDENZIALI);
      workPanel.show(entityMgr);
    }
    else if(idItem.equals("ljsa.classi")) {
      AEntityEditor entityEditor = new GUIClassi();
      IEntityMgr entityMgr = new EntityInternalFrame();
      entityMgr.init(entityEditor, "Gestione Classi", sICON_CLASSI);
      workPanel.show(entityMgr);
    }
    else if(idItem.equals("ljsa.attivita")) {
      AEntityEditor entityEditor = new GUIAttivita();
      IEntityMgr entityMgr = new EntityInternalFrame();
      entityMgr.init(entityEditor, "Gestione Attivit\340", sICON_ATTIVITA);
      workPanel.show(entityMgr);
    }
    else if(idItem.equals("ljsa.schedulazioni")) {
      AEntityEditor entityEditor = new GUISchedulazioni();
      IEntityMgr entityMgr = new EntityInternalFrame();
      entityMgr.init(entityEditor, "Gestione Schedulazioni", sICON_SCHEDULAZIONI);
      workPanel.show(entityMgr);
    }
    else if(idItem.equals("ljsa.log")) {
      workPanel.show(new GUILogSchedulazioni(), "Log schedulazioni LJSA", sICON_LOG_SCHEDULAZIONI);
    }
    else if(idItem.equals("ljsa.schedulatore")) {
      if(!workPanel.selectTab("Schedulatore LJSA")) {
        workPanel.show(new GUISchedulatore(), "Schedulatore LJSA", sICON_SCHEDULATORE);
      }
    }
  }
  
  protected
  void initMenu()
  {
    addMenu("ljsa", "&LJSA", "Schedulatore LJSA", false);
  }
  
  protected
  void initItems()
  {
    // Gap tra le voci del menu laterale.
    iGapItems = 0;
    
    addMenuItem("ljsa",           // Id Menu
        "servizi",                // Id Item
        "Se&rvizi",               // Testo
        "Gestione schedulazioni", // Descrizione
        sICON_SERVIZI,            // Small Icon
        sICON_SERVIZI,            // Large Icon
        false);                   // Enabled
    
    addMenuItem("ljsa",           // Id Menu
        "credenziali",            // Id Item
        "Creden&ziali",           // Testo
        "Gestione credenziali",   // Descrizione
        sICON_CREDENZIALI,        // Small Icon
        sICON_CREDENZIALI,        // Large Icon
        false);                   // Enabled
    
    addSeparator("ljsa");
    
    addMenuItem("ljsa",           // Id Menu
        "classi",                 // Id Item
        "&Classi",                // Testo
        "Gestione classi",        // Descrizione
        sICON_CLASSI,             // Small Icon
        sICON_CLASSI,             // Large Icon
        false);                   // Enabled
    
    addMenuItem("ljsa",           // Id Menu
        "attivita",               // Id Item
        "&Attivit\340",           // Testo
        "Gestione attivit\340",   // Descrizione
        sICON_ATTIVITA,           // Small Icon
        sICON_ATTIVITA,           // Large Icon
        false);                   // Enabled
    
    addMenuItem("ljsa",           // Id Menu
        "schedulazioni",          // Id Item
        "&Schedulazioni",         // Testo
        "Gestione schedulazioni", // Descrizione
        sICON_SCHEDULAZIONI,      // Small Icon
        sICON_SCHEDULAZIONI,      // Large Icon
        false);                   // Enabled
    
    addMenuItem("ljsa",           // Id Menu
        "log",                    // Id Item
        "&Log Schedulazioni",     // Testo
        "Consultazione log",      // Descrizione
        sICON_LOG_SCHEDULAZIONI,  // Small Icon
        sICON_LOG_SCHEDULAZIONI,  // Large Icon
        false);                   // Enabled
    
    addSeparator("ljsa");
    
    addMenuItem("ljsa",           // Id Menu
        "schedulatore",           // Id Item
        "Sche&dulatore",          // Testo
        "Gestione schedulatori",  // Descrizione
        sICON_SCHEDULATORE,       // Small Icon
        sICON_SCHEDULATORE,       // Large Icon
        false);                   // Enabled
  }
}
