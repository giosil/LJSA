package org.dew.ljsa.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.ljsa.IAttivita;
import org.dew.ljsa.gui.DataManager;
import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.components.ILookUpFinder;
import org.dew.swingup.rpc.IRPCClient;

import org.dew.util.WUtil;

public
class LUFAttivita implements ILookUpFinder, IAttivita
{
  public
  List<List<Object>> find(String sEntity, List<Object> oFilter)
      throws Exception
  {
    Map<String, Object> mapFilter = new HashMap<String, Object>();
    
    mapFilter.put(sID_ATTIVITA, oFilter.get(0));
    if(oFilter.size() > 1) {
      mapFilter.put(sDESCRIZIONE, oFilter.get(1));
    }
    if(oFilter.size() > 2) {
      mapFilter.put(sID_SERVIZIO, oFilter.get(2));
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(mapFilter);
    parameters.add(DataManager.vServiziAbilitati);
    
    return WUtil.toListOfListObject(oRPCClient.execute("ATTIVITA.lookup", parameters));
  }
}
