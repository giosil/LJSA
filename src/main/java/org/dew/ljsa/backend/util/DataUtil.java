package org.dew.ljsa.backend.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.util.WUtil;

public
class DataUtil
{
  public static
  String buildInSet(List<?> items)
  {
    if(items == null || items.size() == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for(Object item : items) {
      if(item instanceof String) {
        sb.append(",'" + ((String) item).replace("'", "''") + "'");
      }
      else {
        sb.append("," + item);
      }
    }
    return sb.substring(1);
  }
  
  public static
  String buildInSet(List<?> items, String key)
  {
    if(items == null || items.size() == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for(Object item : items) {
      if(item instanceof String) {
        sb.append(",'" + ((String) item).replace("'", "''") + "'");
      }
      else if(item instanceof Map) {
        Object value = ((Map<?, ?>) item).get(key);
        if(value == null) continue;
        if(value instanceof String) {
          sb.append(",'" + value.toString().replace("'", "''") + "'");
        }
        else {
          sb.append("," + value);
        }
      }
      else {
        sb.append("," + item);
      }
    }
    return sb.substring(1);
  }
  
  public static
  boolean isHoliday(int date)
  {
    if(date == 0) return false;
    
    int iYear  = date / 10000;
    int iMonth = (date % 10000) / 100;
    int iDay   = (date % 10000) % 100;
    
    Calendar calendar = new GregorianCalendar(iYear, iMonth-1, iDay);
    
    int iDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    if(iDayOfWeek == Calendar.SUNDAY) return true;
    
    calendar = computeEaster(iYear); // Pasqua
    calendar.add(Calendar.DATE, 1);  // Lunedi' dell'Angelo (Pasquetta)
    int iDayAfterEaster = WUtil.toIntDate(calendar, 0);
    if(date == iDayAfterEaster) return true;
    
    List<Integer> holidays = new ArrayList<Integer>();
    holidays.add(99990101); // Capodanno
    holidays.add(99990106); // Epifania
    holidays.add(99990425); // Festa della Liberazione
    holidays.add(99990501); // Festa dei Lavoratori
    holidays.add(99990602); // Festa della Repubblica
    holidays.add(99990815); // Ferragosto
    holidays.add(99991101); // Tutti i santi
    holidays.add(99991208); // Immacolata
    holidays.add(99991225); // Natale
    holidays.add(99991226); // Santo Stefano
    
    return holidays.contains(9999 * 10000 + iMonth * 100 + iDay);
  }
  
  public static
  boolean isTodayHoliday()
  {
    return isHoliday(WUtil.toIntDate(Calendar.getInstance(), 0));
  }
  
  /*
   * Compute the day of the year that Easter falls on. Step names E1 E2 etc.,
   * are direct references to Knuth, Vol 1, p 155. @exception
   * IllegalArgumentexception If the year is before 1582 (since the algorithm
   * only works on the Gregorian calendar).
   */
  public static
  Calendar computeEaster(int year) 
  {
    if(year <= 1582) {
      throw new IllegalArgumentException("Algorithm invalid before April 1583");
    }
    int golden, century, x, z, d, epact, n;
    
    golden = (year % 19) + 1; /* E1: metonic cycle */
    century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
    x = (3 * century / 4) - 12; /* E3: leap year correction */
    z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
    d = (5 * year / 4) - x - 10;
    epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
    if((epact == 25 && golden > 11) || epact == 24)
      epact++;
    n = 44 - epact;
    n += 30 *(n < 21 ? 1 : 0); /* E6: */
    n += 7 -((d + n) % 7);
    if(n > 31) /* E7: */
      return new GregorianCalendar(year, 4 - 1, n - 31); /* April */
    else
      return new GregorianCalendar(year, 3 - 1, n); /* March */
  }
  
  public static
  String getExtension(String sFile)
  {
    String sResult = "";
    int iDot = sFile.lastIndexOf('.');
    if(iDot >= 0 && iDot < sFile.length() - 1) {
      sResult = sFile.substring(iDot + 1).toLowerCase();
    }
    return sResult;
  }
  
  public static
  String getFolder(String sFilePath)
  {
    int iLength = sFilePath.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if(c == '/' || c == '\\') {
        return sFilePath.substring(0, iIndex);
      }
    }
    return "";
  }
  
  public static
  String getFileName(String sFilePath)
  {
    int iLength = sFilePath.length();
    for(int i = 1; i <= iLength; i++) {
      int iIndex = iLength - i;
      char c = sFilePath.charAt(iIndex);
      if(c == '/' || c == '\\') {
        if(iIndex == iLength - 1) return "";
        return sFilePath.substring(iIndex + 1);
      }
    }
    return sFilePath;
  }
  
  public static
  int getCurrentDate()
  {
    return WUtil.toIntDate(Calendar.getInstance(), 0);
  }
}
