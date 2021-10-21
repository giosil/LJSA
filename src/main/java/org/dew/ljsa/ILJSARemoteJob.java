package org.dew.ljsa;

import java.util.Map;

/**
 * Interfaccia Job remoto. 
 */
public 
interface ILJSARemoteJob 
{
  public void fireInterrupt();
  
  public void fireExecute(Map<String, Object> mapSchedulazione);
}
