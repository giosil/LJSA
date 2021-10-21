package org.dew.ljsa.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.tree.DefaultMutableTreeNode;

import org.dew.util.WUtil;

public
class TreeResources
{
  public static final String sDESCRIPTION = "@d";
  public static final String sINDEX       = "@i";
  public static final String sREFERENCE   = "@r";
  
  public static
  DefaultMutableTreeNode parse(Map<String, Object> mapResources, String sRoot)
  {
    Map<String, DefaultMutableTreeNode> mapNodes = new HashMap<String, DefaultMutableTreeNode>();
    
    List<DefaultMutableTreeNode> listNodes = new ArrayList<DefaultMutableTreeNode>();
    
    TreeNodeResources oRoot = new TreeNodeResources(sRoot, "");
    listNodes.add(oRoot);
    
    List<String> listKeys = getSortedKeys(mapResources);
    for(int i = 0; i < listKeys.size(); i++) {
      String sKey = listKeys.get(i);
      
      StringTokenizer st = new StringTokenizer(sKey, ".");
      DefaultMutableTreeNode oPrevNode = oRoot;
      String sIdNode = null;
      while(st.hasMoreTokens()) {
        String sToken = st.nextToken().trim();
        
        if(sToken.equals(sDESCRIPTION)) continue;
        if(sToken.equals(sINDEX)) continue;
        if(sToken.equals(sREFERENCE)) continue;
        if(sIdNode == null) {
          sIdNode = sToken;
        }
        else {
          sIdNode = sIdNode + "." + sToken;
        }
        
        DefaultMutableTreeNode oCurrNode = null;
        oCurrNode = mapNodes.get(sIdNode);
        if(oCurrNode == null) {
          String sDesc = WUtil.toString(mapResources.get(sIdNode + "." + sDESCRIPTION), sToken);
          String sReference = WUtil.toString(mapResources.get(sIdNode + "." + sREFERENCE), null);
          if(sReference != null && sReference.length() > 0) {
            sDesc += " -> " + sReference;
          }
          else {
            Object oValue = mapResources.get(sIdNode);
            if(oValue != null) {
              sDesc += " = " + oValue;
            }
          }
          oCurrNode = new TreeNodeResources(sDesc, sIdNode);
          mapNodes.put(sIdNode, oCurrNode);
          
          listNodes.add(oCurrNode);
          if(oPrevNode != null) {
            oPrevNode.add(oCurrNode);
          }
        }
        
        oPrevNode = oCurrNode;
      }
    }
    
    listKeys = getKeysWithoutDot(mapResources);
    for(int i = 0; i < listKeys.size(); i++) {
      String sKey   = (String) listKeys.get(i);
      Object oValue = mapResources.get(sKey);
      oRoot.add(new TreeNodeResources(sKey + " = " + oValue, sKey));
    }
    
    return oRoot;
  }
  
  protected static
  List<String> getKeysWithoutDot(Map<String, Object> map)
  {
    List<String> listResult = new ArrayList<String>();
    Iterator<String> iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      if(key.indexOf('.') < 0) listResult.add(key);
    }
    Collections.sort(listResult);
    return listResult;
  }
  
  protected static
  List<String> getSortedKeys(Map<String, Object> map)
  {
    List<String> listResult = new ArrayList<String>();
    
    Iterator<String> iterator = map.keySet().iterator();
    while(iterator.hasNext()) {
      String key = iterator.next();
      int iLastPoint = key.lastIndexOf('.');
      if(iLastPoint < 0) continue;
      
      int iBegin = key.lastIndexOf('.', iLastPoint - 1);
      String sKeyIndex = key.substring(0, iLastPoint + 1) + sINDEX;
      String sIndex = (String) map.get(sKeyIndex);
      if(sIndex == null) {
        sIndex = "000";
      }
      else {
        int iLength = sIndex.length();
        for(int i = 0; i < 3-iLength; i++) {
          sIndex = "0" + sIndex;
        }
      }
      String s1 = key.substring(0, iBegin + 1);
      String s2 = key.substring(iBegin + 1);
      String sKeyToAdd = s1 + "'" + sIndex + "'" + s2;
      listResult.add(sKeyToAdd);
    }
    
    Collections.sort(listResult);
    
    for(int i = 0; i < listResult.size(); i++) {
      String sKey = (String) listResult.get(i);
      int iBegin = sKey.indexOf('\'');
      int iEnd   = sKey.indexOf('\'', iBegin + 1);
      String sKeyToSet = sKey.substring(0, iBegin) + sKey.substring(iEnd + 1);
      listResult.set(i, sKeyToSet);
    }
    return listResult;
  }
}
