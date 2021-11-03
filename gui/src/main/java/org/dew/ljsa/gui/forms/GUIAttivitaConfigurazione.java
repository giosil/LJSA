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
class GUIAttivitaConfigurazione extends AJDialog implements IAttivita
{
  private static final long serialVersionUID = 5354245914123186314L;
  
  protected FormPanel oFormPanel;
	protected List<String> listHints;
	
	public
	GUIAttivitaConfigurazione()
	{
		super("Configurazione");
		this.setSize(500, 300);
	}
	
	public static
	Map<String, Object> showMe(Map<String, Object> mapValues)
	{
		GUIAttivitaConfigurazione dialog = new GUIAttivitaConfigurazione();
		
		if(mapValues != null) {
			dialog.setValues(mapValues, false);
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
		GUIAttivitaConfigurazione dialog = new GUIAttivitaConfigurazione();
		
		if(mapValues != null) {
			dialog.setValues(mapValues, boEditFlag);
		}
		
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(size.width/2 - dialog.getSize().width/2, size.height/2 - dialog.getSize().height/2);
		
		dialog.setVisible(true);
		
		if(dialog.isCancel()) return null;
		
		return dialog.getConfigurazione();
	}
	
	public
	void setValues(Map<String, Object> mapValues, boolean boEditFlag)
	{
		oFormPanel.setValues(mapValues);
		
		if(!boEditFlag) {
			oFormPanel.setEnabled(sCONF_OPZIONE, false);
			oFormPanel.setDefaultFocus(sCONF_DESCRIZIONE);
		}
	}
	
	public
	Map<String, Object> getConfigurazione()
	{
		return oFormPanel.getValues();
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
		oFormPanel.addNoteField(sCONF_PREDEFINITO, "Predefinito", 3, 1024);
		
		oFormPanel.build();
		
		List<String> oMandatoryFields = new ArrayList<String>();
		oMandatoryFields.add(sCONF_OPZIONE);
		oMandatoryFields.add(sCONF_DESCRIZIONE);
		oFormPanel.setMandatoryFields(oMandatoryFields);
		
		Component compOpzione = oFormPanel.getComponent(sCONF_OPZIONE);
		if(compOpzione instanceof JTextField) {
			buildHints();
			
			new CollectionAutoCompleter(compOpzione, listHints, true);
			
			Component compDescrizione = oFormPanel.getComponent(sCONF_DESCRIZIONE);
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
		listHints = new ArrayList<String>();
		
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
	
	protected
	void setDescriptionHint()
	{
		String sDescHint = null;
		
		String sChiave      = (String) oFormPanel.getValue(sCONF_OPZIONE);
		String sDescrizione = (String) oFormPanel.getValue(sCONF_DESCRIZIONE);
		
		if(sChiave == null || sChiave.length() == 0) {
			sDescHint = null;
		}
		else if(sChiave.equals("attachFiles")) {
			sDescHint = "Se S i file prodotti vengono inviati in allegato alla mail";
		}
		else if(sChiave.equals("attachErrorFiles")) {
			sDescHint = "Se S i file di errore prodotti vengono inviati in allegato alla mail";
		}
		else if(sChiave.equals("compressFiles")) {
			sDescHint = "Se S i file prodotti vengono compressi";
		}
		else if(sChiave.equals("excludeHolidays")) {
			sDescHint = "Se S vengono esclusi i giorni festivi";
		}
		else if(sChiave.equals("fileInfo")) {
			sDescHint = "Se S viene creato il file di informazioni predefinito";
		}
		else if(sChiave.equals("jdbc.driver")) {
			sDescHint = "Driver jdbc";
		}
		else if(sChiave.equals("jdbc.ds")) {
			sDescHint = "Data Source jdbc";
		}
		else if(sChiave.equals("jdbc.url")) {
			sDescHint = "URL jdbc";
		}
		else if(sChiave.equals("jdbc.user")) {
			sDescHint = "User jdbc";
		}
		else if(sChiave.equals("jdbc.password")) {
			sDescHint = "Passoword jdbc";
		}
		else if(sChiave.equals("language")) {
			sDescHint = "Linguaggio";
		}
		else if(sChiave.equals("mail.delete")) {
			sDescHint = "Se S le mail non vengono conservate sul server";
		}
		else if(sChiave.equals("mail.user")) {
			sDescHint = "Utente casella di posta elettronica";
		}
		else if(sChiave.equals("mail.password")) {
			sDescHint = "Password casella di posta elettronica";
		}
		else if(sChiave.equals("message")) {
			sDescHint = "Testo del messaggio di notifica";
		}
		else if(sChiave.equals("nolog")) {
			sDescHint = "Se S le elaborazioni NON vengono tracciate in archivio";
		}
		else if(sChiave.equals("report")) {
			sDescHint = "Template del report";
		}
		else if(sChiave.equals("single")) {
			sDescHint = "Se S si bloccano esecuzioni sovrapposte dello stesso job";
		}
		else if(sChiave.equals("stopOnTimeout")) {
			sDescHint = "Interrompe l'elaborazione a timeout raggiunto";
		}
		else if(sChiave.equals("subject")) {
			sDescHint = "Oggetto del messaggio di notifica";
		}
		else if(sChiave.equals("timeout")) {
			sDescHint = "Timeout di elaborazione espresso in minuti";
		}
		
		if(sDescHint != null) {
			if(sDescrizione == null || sDescrizione.length() == 0) {
				oFormPanel.setValue(sCONF_DESCRIZIONE, sDescHint);
			}
		}
	}
}
