package org.dew.ljsa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.util.WMap;
import org.util.WUtil;

/**
 * Classe di utilita' per l'accesso ai parametri e alla configurazione di un'attivita' schedulata.
 */
public
class LJSAMap extends WMap
{
  private String exceptionPrefix = ILJSAErrors.sPARAMETER_MISSING;
  
  public
  LJSAMap()
  {
    super();
  }
  
  public
  LJSAMap(Map<String, Object> map)
  {
    super(map);
  }
  
  public
  void setExceptionPrefix(String exceptionPrefix)
  {
    this.exceptionPrefix = exceptionPrefix;
  }
  
  public
  String getExceptionPrefix()
  {
    return exceptionPrefix;
  }
  
  @Override
  public 
  Object get(Object key) 
  {
    Object result = map.get(key);
    if(result instanceof String) {
      String stringValue = (String) result;
      if(stringValue.startsWith("$date")) {
        return String.valueOf(dateExpToInt(stringValue));
      }
    }
    return result;
  }
  
  public 
  Object get(Object key, boolean isMandatory)
    throws Exception
  {
    Object result = map.get(key);
    if(result == null) {
      if(isMandatory) {
        throw new Exception(exceptionPrefix + key);
      }
      return null;
    }
    else if(result instanceof String) {
      String stringValue = (String) result;
      if(stringValue.startsWith("$date")) {
        return String.valueOf(dateExpToInt(stringValue));
      }
    }
    return result;
  }
  
  public
  List<String> getKeysStartsWith(String sStartKey)
  {
    List<String> listResult = new ArrayList<String>();
    Iterator<?> iterator = keySet().iterator();
    while(iterator.hasNext()) {
      String sKey = iterator.next().toString();
      if(sKey.startsWith(sStartKey)) {
        listResult.add(sKey);
      }
    }
    Collections.sort(listResult);
    return listResult;
  }
  
  public
  List<String> getKeysEndsWith(String sEndKey)
  {
    List<String> listResult = new ArrayList<String>();
    Iterator<?> iterator = keySet().iterator();
    while(iterator.hasNext()) {
      String sKey = iterator.next().toString();
      if(sKey.endsWith(sEndKey)) {
        listResult.add(sKey);
      }
    }
    Collections.sort(listResult);
    return listResult;
  }
  
  public
  String getString(Object key, boolean isMandatory)
    throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toString(value, null);
  }
  
  public
  int getInt(Object key, boolean isMandatory)
    throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toInt(value, 0);
  }
  
  public
  long getLong(Object key, boolean isMandatory)
    throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toLong(value, 0);
  }
  
  public
  double getDouble(Object key, boolean isMandatory)
    throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toDouble(value, 0.0d);
  }
  
  public
  boolean getBoolean(Object key, boolean isMandatory, boolean defaultValue)
    throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toBoolean(value, defaultValue);
  }
  
  public
  Date getDate(Object key, boolean isMandatory)
      throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toDate(value, null);
  }
  
  public
  java.sql.Date getSQLDate(Object key, boolean isMandatory)
      throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toSQLDate(value, null);
  }
  
  public
  Calendar getCalendar(Object key, boolean isMandatory)
      throws Exception
  {
    Object value = get(key, isMandatory);
    
    return WUtil.toCalendar(value, null);
  }
  
  public
  String buildInfoString()
  {
    List<String> listKeys = new ArrayList<String>();
    Iterator<?> iterator = keySet().iterator();
    while(iterator.hasNext()) {
      listKeys.add(iterator.next().toString());
    }
    Collections.sort(listKeys);
    
    StringBuilder sb = new StringBuilder();
    for(String key : listKeys) {
      Object value = get(key);
      String sConverted = null;
      if(value instanceof String) {
        String sValue = (String) value;
        if(sValue.startsWith("$date")) {
          sConverted = String.valueOf(LJSAMap.dateExpToInt(sValue));
        }
      }
      if(sConverted != null) {
        sb.append(key + " = " + value + " -> " + sConverted + "\n");
      }
      else {
        sb.append(key + " = " + value + "\n");
      }
    }
    return sb.toString();
  }
  
  public static
  int dateExpToInt(String sValue)
  {
    Calendar cal = Calendar.getInstance();
    
    int iOp = sValue.indexOf('+');
    if(iOp > 0) {
      char cLast = sValue.charAt(sValue.length() - 1);
      if(cLast == 'd' || cLast == 'D') {
        String sD = sValue.substring(iOp + 1, sValue.length() - 1).trim();
        int iD = 0;
        try{ iD = Integer.parseInt(sD); } catch(Exception ex) {};
        cal.add(Calendar.DATE, iD);
      }
      else if(cLast == 'w' || cLast == 'W') {
        String sW = sValue.substring(iOp + 1, sValue.length() - 1).trim();
        int iW = 0;
        try{ iW = Integer.parseInt(sW); } catch(Exception ex) {};
        cal.add(Calendar.DATE, iW * 7);
      }
      else if(cLast == 'm' || cLast == 'M') {
        String sM = sValue.substring(iOp + 1, sValue.length() - 1).trim();
        int iM = 0;
        try{ iM = Integer.parseInt(sM); } catch(Exception ex) {};
        cal.add(Calendar.MONTH, iM);
      }
      else if(cLast == 'y' || cLast == 'Y') {
        String sY = sValue.substring(iOp + 1, sValue.length() - 1).trim();
        int iY = 0;
        try{ iY = Integer.parseInt(sY); } catch(Exception ex) {};
        cal.add(Calendar.YEAR, iY);
      }
      else {
        String sD = sValue.substring(iOp + 1, sValue.length()).trim();
        int iD = 0;
        try{ iD = Integer.parseInt(sD); } catch(Exception ex) {};
        cal.add(Calendar.DATE, iD);
      }
    }
    else {
      iOp = sValue.indexOf('-');
      if(iOp > 0) {
        char cLast = sValue.charAt(sValue.length() - 1);
        if(cLast == 'd' || cLast == 'D') {
          String sD = sValue.substring(iOp + 1, sValue.length() - 1).trim();
          int iD = 0;
          try{ iD = Integer.parseInt(sD); } catch(Exception ex) {};
          cal.add(Calendar.DATE, -iD);
        }
        else if(cLast == 'w' || cLast == 'W') {
          String sW = sValue.substring(iOp + 1, sValue.length() - 1).trim();
          int iW = 0;
          try{ iW = Integer.parseInt(sW); } catch(Exception ex) {};
          cal.add(Calendar.DATE, -iW * 7);
        }
        else if(cLast == 'm' || cLast == 'M') {
          String sM = sValue.substring(iOp + 1, sValue.length() - 1).trim();
          int iM = 0;
          try{ iM = Integer.parseInt(sM); } catch(Exception ex) {};
          cal.add(Calendar.MONTH, -iM);
        }
        else if(cLast == 'y' || cLast == 'Y') {
          String sY = sValue.substring(iOp + 1, sValue.length() - 1).trim();
          int iY = 0;
          try{ iY = Integer.parseInt(sY); } catch(Exception ex) {};
          cal.add(Calendar.YEAR, -iY);
        }
        else {
          String sD = sValue.substring(iOp + 1, sValue.length()).trim();
          int iD = 0;
          try{ iD = Integer.parseInt(sD); } catch(Exception ex) {};
          cal.add(Calendar.DATE, -iD);
        }
      }
    }
    return WUtil.toInt(cal, 0);
  }
}
