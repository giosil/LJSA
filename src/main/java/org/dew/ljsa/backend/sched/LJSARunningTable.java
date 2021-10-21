package org.dew.ljsa.backend.sched;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dew.ljsa.Schedulazione;

/**
 * Table of running LJSA job. 
 */
public
class LJSARunningTable
{
  private static Set<String> hsTable = new HashSet<String>();
  
  public static synchronized
  void add(Schedulazione schedulazione)
  {
    if(schedulazione == null) return;
    hsTable.add(schedulazione.toString());
  }
  
  public static synchronized
  void remove(Schedulazione schedulazione)
  {
    if(schedulazione == null) return;
    hsTable.remove(schedulazione.toString());
  }
  
  public static synchronized
  boolean exist(Schedulazione schedulazione)
  {
    if(schedulazione == null) return false;
    return hsTable.contains(schedulazione.toString());
  }
  
  public static synchronized
  boolean exist(String sIdServizio, String sIdAttivita)
  {
    Iterator<String> iterator = hsTable.iterator();
    while(iterator.hasNext()) {
      String item = iterator.next();
      if(item.endsWith(":" + sIdServizio + ":" + sIdAttivita)) {
        return true;
      }
    }
    return false;
  }
  
  public static
  List<String> getListSchedulazioni()
  {
    List<String> listResult = new ArrayList<String>();
    Iterator<String> iterator = hsTable.iterator();
    while(iterator.hasNext()) {
      listResult.add(iterator.next());
    }
    return listResult;
  }
}
