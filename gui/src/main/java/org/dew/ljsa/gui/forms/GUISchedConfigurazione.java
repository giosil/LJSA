package org.dew.ljsa.gui.forms;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.ISchedulazione;

import org.dew.swingup.AJDialog;
import org.dew.swingup.GUIMessage;
import org.dew.swingup.util.CollectionAutoCompleter;
import org.dew.swingup.util.FormPanel;

public
class GUISchedConfigurazione extends AJDialog implements ISchedulazione
{
  private static final long serialVersionUID = 58297944650534861L;
 
  protected FormPanel oFormPanel;
	protected boolean boOverWrite = false;
	protected List<String> listHints;
	
	public
	GUISchedConfigurazione()
	{
		super("Configurazione");
		this.setSize(500, 300);
	}
	
	public static
	Map<String, Object> showMe(Map<String, Object> mapValues)
	{
		GUISchedConfigurazione dialog = new GUISchedConfigurazione();
		
		if(mapValues != null) {
			dialog.setValues(mapValues, false);
		}
		else {
			dialog.hideDescrizioneValori();
		}
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
		
		dialog.setVisible(true);
		
		if(dialog.isCancel()) return null;
		
		return dialog.getConfigurazione();
	}
	
	public static
	Map<String, Object> showMe(Map<String, Object> mapValues, boolean boEditFlag)
	{
		GUISchedConfigurazione dialog = new GUISchedConfigurazione();
		
		if(mapValues != null) {
			dialog.setValues(mapValues, boEditFlag);
		}
		else {
			dialog.hideDescrizioneValori();
		}
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width/2 - dialog.getSize().width/2,
			screenSize.height/2 - dialog.getSize().height/2);
		
		dialog.setVisible(true);
		
		if(dialog.isCancel()) return null;
		
		return dialog.getConfigurazione();
	}
	
	public
	void setValues(Map<String, Object> mapValues, boolean boEditFlag)
	{
		Boolean oOverWrite = (Boolean) mapValues.get(sCONFIGURAZIONE_OVERWRITE);
		if(oOverWrite != null) {
			boOverWrite = oOverWrite.booleanValue();
		}
		else {
			boOverWrite = false;
		}
		
		oFormPanel.setValues(mapValues);
		
		oFormPanel.setEnabled(IAttivita.sCONFIGURAZIONE_DESCRIZIONE, false);
		oFormPanel.setEnabled(IAttivita.sCONFIGURAZIONE_VALORI, false);
		if(!boEditFlag) {
			oFormPanel.setEnabled(sCONFIGURAZIONE_OPZIONE, false);
			oFormPanel.setDefaultFocus(sCONFIGURAZIONE_VALORE);
		}
	}
	
	public
	Map<String, Object> getConfigurazione()
	{
		return oFormPanel.getValues();
	}
	
	public
	void hideDescrizioneValori()
	{
		oFormPanel.setVisible(IAttivita.sCONFIGURAZIONE_DESCRIZIONE, false);
		oFormPanel.setVisible(IAttivita.sCONFIGURAZIONE_VALORI, false);
		this.setSize(400, 200);
	}
	
	protected
	Container buildGUI()
		throws Exception
	{
		oFormPanel = new FormPanel("Opzione di configurazione");
		oFormPanel.addRow();
		oFormPanel.addTextField(sCONFIGURAZIONE_OPZIONE, "Opzione", 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(IAttivita.sCONFIGURAZIONE_DESCRIZIONE, "Descrizione", 3, 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(IAttivita.sCONFIGURAZIONE_VALORI, "Valori", 3, 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(sCONFIGURAZIONE_VALORE, "Valore", 3, 1024);
		oFormPanel.addHiddenField(IAttivita.sCONFIGURAZIONE_PREDEFINITO);
		oFormPanel.addHiddenField(sCONFIGURAZIONE_DA_ATTIVITA);
		oFormPanel.addHiddenField(sCONFIGURAZIONE_OVERWRITE);
		
		oFormPanel.build();
		
		oFormPanel.setEnabled(IAttivita.sCONFIGURAZIONE_DESCRIZIONE, false);
		oFormPanel.setEnabled(IAttivita.sCONFIGURAZIONE_VALORI, false);
		
		List<String> oMandatoryFields = new ArrayList<String>();
		oMandatoryFields.add(sCONFIGURAZIONE_OPZIONE);
		oFormPanel.setMandatoryFields(oMandatoryFields);
		
		Component compOpzione = oFormPanel.getComponent(sCONFIGURAZIONE_OPZIONE);
		if(compOpzione instanceof JTextField) {
			buildHints();
			
			new CollectionAutoCompleter(compOpzione, listHints, true);
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
			Boolean oDaAttivita = (Boolean) oFormPanel.getValue(sCONFIGURAZIONE_DA_ATTIVITA);
			if(oDaAttivita != null && oDaAttivita.booleanValue()) {
				String sPredefinito = (String) oFormPanel.getValue(IAttivita.sCONFIGURAZIONE_PREDEFINITO);
				if(sPredefinito == null) {
					oFormPanel.setValue(IAttivita.sCONFIGURAZIONE_PREDEFINITO, "");
					sPredefinito = "";
				}
				String sValore = (String) oFormPanel.getValue(sCONFIGURAZIONE_VALORE);
				oFormPanel.setValue(sPARAMETRI_OVERWRITE, new Boolean(!sPredefinito.equals(sValore)));
			}
		}
		
		return true;
	}
	
	protected
	void buildHints()
	{
		listHints = new ArrayList<String>(21);
		
		listHints.add("attachFiles");
		listHints.add("attachErrorFiles");
		listHints.add("compressFiles");
		listHints.add("excludeHolidays");
		listHints.add("fileInfo");
		listHints.add("jdbc.driver");
		listHints.add("jdbc.ds");
		listHints.add("jdbc.password");
		listHints.add("jdbc.url");
		listHints.add("jdbc.user");
		listHints.add("language");
		listHints.add("mail.delete");
		listHints.add("mail.password");
		listHints.add("mail.user");
		listHints.add("message");
		listHints.add("nolog");
		listHints.add("report");
		listHints.add("single");
		listHints.add("stopOnTimeout");
		listHints.add("subject");
		listHints.add("timeout");
	}
}
