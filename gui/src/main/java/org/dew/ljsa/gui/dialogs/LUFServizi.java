package org.dew.ljsa.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dew.ljsa.IServizio;

import org.dew.ljsa.gui.DataManager;

import org.dew.swingup.ResourcesMgr;
import org.dew.swingup.components.ILookUpFinder;
import org.dew.swingup.rpc.IRPCClient;

import org.dew.util.WUtil;

public
class LUFServizi implements ILookUpFinder, IServizio
{
  public
  List<List<Object>> find(String sEntity, List<Object> oFilter)
      throws Exception
  {
    Map<String, Object> mapFilter = new HashMap<String, Object>();
    
    mapFilter.put(sID_SERVIZIO, oFilter.get(0));
    if(oFilter.size() > 1) {
      mapFilter.put(sDESCRIZIONE, oFilter.get(1));
    }
    
    IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
    
    List<Object> parameters = new ArrayList<Object>();
    parameters.add(mapFilter);
    parameters.add(DataManager.vServiziAbilitati);
    
    List<List<Object>> listResult = WUtil.toListOfListObject(oRPCClient.execute("SERVIZI.lookup", parameters));
    
    if(listResult != null && listResult.size() == 1) {
      List<Object> item0 = listResult.get(0);
      if(item0 != null && item0.size() > 2) {
        String sCode = (String) item0.get(0);
        String sDesc = (String) item0.get(2);
        if(sDesc == null || sDesc.length() == 0) {
          sDesc = sCode;
        }
        
        List<String> defaultServizio = new ArrayList<String>(3);
        defaultServizio.add(sCode);
        defaultServizio.add(sCode);
        defaultServizio.add(sDesc);
        
        DataManager.defaultServizio = defaultServizio;
      }
    }
    
    return listResult;
  }
}
