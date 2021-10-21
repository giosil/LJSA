package org.dew.ljsa.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.ljsa.IClasse;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.components.ILookUpFinder;
import org.dew.swingup.rpc.IRPCClient;

import org.dew.util.WUtil;

public
class LUFClassi implements ILookUpFinder, IClasse
{
  @SuppressWarnings("rawtypes")
  public
  List<List<Object>> find(String sEntity, List oFilter)
      throws Exception
  {
    Map<String, Object> mapFilter = new HashMap<String, Object>();
    
    mapFilter.put(sCLASSE, oFilter.get(0));
    if(oFilter.size() > 1) {
      mapFilter.put(sDESCRIZIONE, oFilter.get(1));
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(mapFilter);
    
    return WUtil.toListOfListObject(oRPCClient.execute("CLASSI.lookup", parameters));
  }
}
