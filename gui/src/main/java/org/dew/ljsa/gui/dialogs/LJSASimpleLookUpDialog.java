package org.dew.ljsa.gui.dialogs;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import org.dew.ljsa.gui.AppUtil;

import org.dew.swingup.impl.SimpleLookUpDialog;
import org.dew.util.WUtil;

public
class LJSASimpleLookUpDialog extends SimpleLookUpDialog
{
  private static final long serialVersionUID = -1984785686296791794L;
  
  public
  LJSASimpleLookUpDialog(String sTitle)
  {
    super(sTitle);
  }
  
  protected
  Color getColorOfAdditionalField(Object oValue)
  {
    if(oValue instanceof Integer) {
      int iValue = ((Integer) oValue).intValue();
      switch(iValue) {
      case 0: return Color.black;
      case 1: return Color.lightGray;
      case 2: return Color.red;
      case 3: return Color.magenta;
      }
    }
    return Color.black;
  }
  
  protected
  void onDoubleClick()
  {
    if(oLookUpFinder instanceof LUFServizi) {
      int iRow = oTableRecords.getSelectedRow();
      List<Object> listItem = WUtil.toList(oRecords.get(iRow), Object.class, null);
      if(listItem != null && listItem.size() > 2) {
        String sCode = (String) listItem.get(0);
        String sDesc = (String) listItem.get(2);
        if(sDesc == null || sDesc.length() == 0) sDesc = sCode;
        
        List<String> defaultServizio = new ArrayList<String>(3);
        defaultServizio.add(sCode);
        defaultServizio.add(sCode);
        defaultServizio.add(sDesc);
        
        AppUtil.defaultServizio = defaultServizio;
      }
    }
    super.onDoubleClick();
  }
}
