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
import org.dew.util.WUtil;

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
		Boolean oOverWrite = WUtil.toBooleanObj(mapValues.get(sCONF_OVERWRITE), false);
		if(oOverWrite != null) {
			boOverWrite = oOverWrite.booleanValue();
		}
		else {
			boOverWrite = false;
		}
		
		oFormPanel.setValues(mapValues);
		
		oFormPanel.setEnabled(sCONF_DESCRIZIONE, false);
		oFormPanel.setEnabled(sCONF_VALORI, false);
		if(!boEditFlag) {
			oFormPanel.setEnabled(sCONF_OPZIONE, false);
			oFormPanel.setDefaultFocus(sCONF_VALORE);
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
		oFormPanel.setVisible(sCONF_DESCRIZIONE, false);
		oFormPanel.setVisible(sCONF_VALORI,      false);
		this.setSize(400, 200);
	}
	
	protected
	Container buildGUI()
		throws Exception
	{
		oFormPanel = new FormPanel("Opzione di configurazione");
		oFormPanel.addRow();
		oFormPanel.addTextField(sCONF_OPZIONE, "Opzione", 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(sCONF_DESCRIZIONE, "Descrizione", 3, 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(sCONF_VALORI, "Valori", 3, 255);
		oFormPanel.addRow();
		oFormPanel.addNoteField(sCONF_VALORE, "Valore", 3, 1024);
		oFormPanel.addHiddenField(sCONF_PREDEFINITO);
		oFormPanel.addHiddenField(sCONF_DA_ATTIVITA);
		oFormPanel.addHiddenField(sCONF_OVERWRITE);
		
		oFormPanel.build();
		
		oFormPanel.setEnabled(sCONF_DESCRIZIONE, false);
		oFormPanel.setEnabled(sCONF_VALORI, false);
		
		List<String> oMandatoryFields = new ArrayList<String>();
		oMandatoryFields.add(sCONF_OPZIONE);
		oFormPanel.setMandatoryFields(oMandatoryFields);
		
		Component compOpzione = oFormPanel.getComponent(sCONF_OPZIONE);
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
			Boolean oDaAttivita = WUtil.toBooleanObj(oFormPanel.getValue(sCONF_DA_ATTIVITA), false);
			if(oDaAttivita != null && oDaAttivita.booleanValue()) {
				String sPredefinito = (String) oFormPanel.getValue(IAttivita.sCONF_PREDEFINITO);
				if(sPredefinito == null) {
					oFormPanel.setValue(sCONF_PREDEFINITO, "");
					sPredefinito = "";
				}
				String sValore = (String) oFormPanel.getValue(sCONF_VALORE);
				oFormPanel.setValue(sPAR_OVERWRITE, new Boolean(!sPredefinito.equals(sValore)));
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
