package org.dew.ljsa.gui;

import org.dew.ljsa.gui.dialogs.LJSASimpleLookUpDialog;
import org.dew.ljsa.gui.dialogs.LUFAttivita;
import org.dew.ljsa.gui.dialogs.LUFClassi;
import org.dew.ljsa.gui.dialogs.LUFCredenziali;
import org.dew.ljsa.gui.dialogs.LUFServizi;

import org.dew.swingup.components.ADecodifiableComponent;
import org.dew.swingup.components.JComboDecodifiable;
import org.dew.swingup.components.JTextDecodifiable;

import org.dew.swingup.impl.SimpleLookUpDialog;

public 
class DecodifiableFactory 
{
  public static
  ADecodifiableComponent buildDCServizio()
  {
    JTextDecodifiable result = new JTextDecodifiable("Servizio");
    result.setLookUpFinder(new LUFServizi());
    result.setLookUpDialog(new LJSASimpleLookUpDialog("Ricerca Servizio"));
    return result;
  }
  
  public static
  ADecodifiableComponent buildDCClasse()
  {
    JTextDecodifiable result = new JTextDecodifiable("Classi", 1);
    result.setLookUpFinder(new LUFClassi());
    result.setLookUpDialog(new SimpleLookUpDialog("Ricerca Classe"));
    result.setCodeIfKeyIsNull(true);
    return result;
  }
  
  public static
  ADecodifiableComponent buildDCAttivita()
  {
    JTextDecodifiable result = new JTextDecodifiable("Attivita");
    result.setLookUpFinder(new LUFAttivita());
    result.setLookUpDialog(new SimpleLookUpDialog("Ricerca Attivit\340"));
    return result;
  }
  
  public static
  ADecodifiableComponent buildDCComboCredenziale()
  {
    ADecodifiableComponent result = new JComboDecodifiable("Credenziale");
    result.setLookUpFinder(new LUFCredenziali());
    return result;
  }
}
