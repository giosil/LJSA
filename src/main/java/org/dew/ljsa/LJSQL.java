package org.dew.ljsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;
import org.util.WUtil;

/**
 * Implementazione di ALJSAJob che esegue un comando SQL.
 *
 *  <pre>
 *  Configurazione:
 *  jdbc.ds       = data source
 *  jdbc.driver   = driver jdbc
 *  jdbc.url      = url jdbc
 *  jdbc.user     = user db
 *  jdbc.password = password db
 *
 *  Parametri:
 *  sql    = query sql
 *  type   = type file (html, csv, xls, rtf, xml)
 *  title  = title of report
 *  name   = name of file
 *  header = flag header (default is true)
 *  </pre>
 */
public
class LJSQL extends ALJSAJob
{
  public static final int iTYPE_HTML = 0;
  public static final int iTYPE_CSV  = 1;
  public static final int iTYPE_XML  = 2;
  public static final int iTYPE_TXT  = 3;
  
  public
  void execute(Schedulazione sched, OutputSchedulazione out)
      throws Exception
  {
    LJSAMap configurazione = sched.getConfigurazione();
    LJSAMap parametri      = sched.getParametri();
    
    String sFileName = configurazione.getString("name", "output");
    
    int iType = getTypeReport(parametri);
    String sTitle = parametri.getString("title");
    List<String> listTotalsCols = parametri.getList("totals", String.class, null);
    boolean boHeader = parametri.getBoolean("header", false, true);
    
    String sSQL = getSQL(parametri);
    
    if(!isSelect(sSQL)) {
      boolean boCanUpdate = parametri.getBoolean("canUpdate");
      if(!boCanUpdate) {
        throw new Exception("SQL command rejected.");
      }
    }
    
    Connection conn = null;
    Statement stm = null;
    ResultSet rs = null;
    try {
      conn = getConnection(configurazione);
      stm = conn.createStatement();
      
      if(!stm.execute(sSQL)) {
        conn.commit();
        // Is an insert, update, delete...
        PrintStream ps = new PrintStream(out.createOutputFile(sFileName + ".txt"), true);
        ps.println(sSQL);
        ps.println();
        ps.println(stm.getUpdateCount() + " records updated.");
        out.setReport("SQL command executed.");
        return;
      }
      rs = stm.getResultSet();
      PrintStream ps;
      switch(iType) {
      case iTYPE_HTML:
        ps = new PrintStream(out.createOutputFile(sFileName + ".html"), true);
        exportToHTML(rs, ps, sTitle, listTotalsCols, boHeader);
        break;
      case iTYPE_CSV:
        ps = new PrintStream(out.createOutputFile(sFileName + ".csv"), true);
        exportToCSV(rs, ps, sTitle, listTotalsCols, boHeader);
        break;
      case iTYPE_XML:
        ps = new PrintStream(out.createOutputFile(sFileName + ".xml"), true);
        exportToXML(rs, ps, sTitle, listTotalsCols);
        break;
      case iTYPE_TXT:
        ps = new PrintStream(out.createOutputFile(sFileName + ".txt"), true);
        exportToTXT(rs, ps, sTitle, listTotalsCols, boHeader);
        break;
      default:
        ps = new PrintStream(out.createOutputFile(sFileName + ".html"), true);
        exportToHTML(rs, ps, sTitle, listTotalsCols, boHeader);
        break;
      }
      
      String sMessage = out.getMessage();
      if(sMessage == null) {
        sMessage = sched + " completed successfully.\\n\\n";
        sMessage += sSQL;
        out.setMessage(sMessage);
      }
    }
    catch(Exception ex) {
      out.removeFile(0);
      
      PrintStream outerr = new PrintStream(out.createErrorFile(), true);
      outerr.println("Exception:\n");
      outerr.println(ex.toString() + "\n\n");
      outerr.println(sSQL);
      out.setErrorStatus();
      out.setReport(ex.toString());
      return;
    }
    finally {
      ConnectionManager.close(rs, stm, conn);
    }
    out.setReport("SQL query executed.");
  }
  
  protected
  void exportToHTML(ResultSet rs, PrintStream ps, String sTitle, List<String> listTotalsCols, boolean boHeader)
      throws Exception
  {
    boolean boTotals = listTotalsCols != null && listTotalsCols.size() > 0;
    List<Number> listTotals = null;
    if(boTotals) {
      listTotals = new ArrayList<Number>();
      for(int i = 0; i < listTotalsCols.size(); i++) {
        listTotals.add(null);
      }
    }
    
    ps.println("<!DOCTYPE html>\n");
    ps.println("<html>");
    ps.println("<head>");
    ps.println("<title>LJSQL Result</title>");
    ps.println("</head>");
    ps.println("<body>");
    if(sTitle != null && sTitle.length() > 0) {
      ps.println("<h3 style=\"text-align: center;\">" + sTitle + "</h3>");
      ps.println("<hr>");
    }
    ps.println("<table style=\"text-align: center;border: 1px solid black;border-collapse: collapse;\">");
    
    StringBuffer sbRow = new StringBuffer();
    
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    if(boHeader) {
      ps.println("<tr>");
      for(int i = 0; i < columnCount; i++) {
        if(boLJSAJobInterrupted) return;
        sbRow.append("<th bgcolor=\"#dddddd\">" + rsmd.getColumnName(i + 1) + "</th>");
      }
      ps.print(sbRow);
      ps.println("</tr>");
    }
    int iRow = 0;
    while(rs.next()) {
      if(boLJSAJobInterrupted) {
        return;
      }
      iRow++;
      ps.println("<tr>");
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        int iColType = rsmd.getColumnType(i + 1);
        String value = rs.getString(i + 1);
        if(value == null) value = "&nbsp;";
        String sAlign = null;
        if(iColType == java.sql.Types.VARCHAR || iColType == java.sql.Types.CHAR || iColType == java.sql.Types.CLOB || iColType == java.sql.Types.BLOB) {
          sAlign = "align=\"left\"";
        }
        else {
          sAlign = "align=\"right\"";
        }
        if((iRow % 2) == 0) {
          sbRow.append("<td bgcolor=\"#eeeeee\" " + sAlign + ">" + value + "</td>");
        }
        else {
          sbRow.append("<td " + sAlign + ">" + value + "</td>");
        }
        if(boTotals) {
          add(i, value, listTotalsCols, listTotals);
        }
      }
      ps.print(sbRow);
      ps.println("</tr>");
    }
    if(boTotals) {
      ps.println("<tr>");
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String sTotal = getTotal(i, listTotalsCols, listTotals);
        if(sTotal == null) {
          sbRow.append("<td bgcolor=\"#FFFFAA\">&nbsp;</td>");
        }
        else {
          sbRow.append("<td bgcolor=\"#FFFFAA\">" + sTotal + "</td>");
        }
      }
      ps.print(sbRow);
      ps.println("</tr>");
    }
    ps.println("</table>");
    ps.println("</body>");
    ps.println("</html>");
  }
  
  protected
  void exportToCSV(ResultSet rs, PrintStream ps, String sTitle, List<String> listTotalsCols, boolean boHeader)
      throws Exception
  {
    boolean boTotals = listTotalsCols != null && listTotalsCols.size() > 0;
    List<Number> listTotals = null;
    if(boTotals) {
      listTotals = new ArrayList<Number>();
      for(int i = 0; i < listTotalsCols.size(); i++) {
        listTotals.add(null);
      }
    }
    
    StringBuffer sbRow = new StringBuffer();
    
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    if(boHeader) {
      for(int i = 0; i < columnCount; i++) {
        if(boLJSAJobInterrupted) return;
        sbRow.append(rsmd.getColumnName(i + 1) + ";");
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
    
    while(rs.next()) {
      if(boLJSAJobInterrupted) return;
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String value = rs.getString(i + 1);
        if(value == null) value = "";
        sbRow.append(value + ";");
        if(boTotals) {
          add(i, value, listTotalsCols, listTotals);
        }
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
    if(boTotals) {
      ps.print(OutputSchedulazione.CRLF);
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String sTotal = getTotal(i, listTotalsCols, listTotals);
        if(sTotal == null) {
          sbRow.append(";");
        }
        else {
          sbRow.append(sTotal + ";");
        }
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
  }
  
  protected
  void exportToXML(ResultSet rs, PrintStream ps, String sTitle, List<String> listTotalsCols)
      throws Exception
  {
    boolean boTotals = listTotalsCols != null && listTotalsCols.size() > 0;
    List<Number> listTotals = null;
    if(boTotals) {
      listTotals = new ArrayList<Number>();
      for(int i = 0; i < listTotalsCols.size(); i++) {
        listTotals.add(null);
      }
    }
    
    ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    
    String sXMLRoot = "<result";
    if(sTitle != null) {
      sXMLRoot += " title=\"" + sTitle + "\">";
    }
    else {
      sXMLRoot += ">";
    }
    ps.println(sXMLRoot);
    
    StringBuffer sbRow = new StringBuffer();
    
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    String[] asColumnName = new String[columnCount];
    for(int i = 0; i < columnCount; i++) {
      if(boLJSAJobInterrupted) return;
      asColumnName[i] = rsmd.getColumnName(i + 1).toLowerCase();
    }
    while(rs.next()) {
      if(boLJSAJobInterrupted) return;
      sbRow.delete(0, sbRow.length());
      ps.println("\t<record>");
      for(int i = 0; i < columnCount; i++) {
        sbRow.append("\t\t<" + asColumnName[i] + ">");
        String value = rs.getString(i + 1);
        if(value == null) value = "";
        sbRow.append(value);
        sbRow.append("</" + asColumnName[i] + ">\n");
        
        if(boTotals) {
          add(i, value, listTotalsCols, listTotals);
        }
      }
      ps.println(sbRow);
      ps.println("\t</record>");
    }
    if(boTotals) {
      ps.println("\t<totals>");
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String sTotal = getTotal(i, listTotalsCols, listTotals);
        if(sTotal != null) {
          sbRow.append("\t\t<" + asColumnName[i] + ">");
          sbRow.append(sTotal);
          sbRow.append("</" + asColumnName[i] + ">\n");
        }
      }
      ps.println(sbRow);
      ps.println("\t</totals>");
    }
    ps.println("</result>");
  }
  
  protected
  void exportToTXT(ResultSet rs, PrintStream ps, String sTitle, List<String> listTotalsCols, boolean boHeader)
      throws Exception
  {
    boolean boTotals = listTotalsCols != null && listTotalsCols.size() > 0;
    List<Number> listTotals = null;
    if(boTotals) {
      listTotals = new ArrayList<Number>();
      for(int i = 0; i < listTotalsCols.size(); i++) {
        listTotals.add(null);
      }
    }
    
    StringBuilder sbRow = new StringBuilder();
    
    if(sTitle != null) {
      ps.print(sTitle);
      ps.print(OutputSchedulazione.CRLF);
    }
    
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    if(boHeader) {
      for(int i = 0; i < columnCount; i++) {
        if(boLJSAJobInterrupted) return;
        sbRow.append(rsmd.getColumnName(i + 1) + '\t');
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
    while(rs.next()) {
      if(boLJSAJobInterrupted) return;
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String value = rs.getString(i + 1);
        if(value == null) value = "";
        sbRow.append(value + '\t');
        
        if(boTotals) {
          add(i, value, listTotalsCols, listTotals);
        }
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
    
    if(boTotals) {
      ps.print(OutputSchedulazione.CRLF);
      sbRow.delete(0, sbRow.length());
      for(int i = 0; i < columnCount; i++) {
        String sTotal = getTotal(i, listTotalsCols, listTotals);
        if(sTotal == null) {
          sbRow.append('\t');
        }
        else {
          sbRow.append(sTotal + '\t');
        }
      }
      ps.print(sbRow);
      ps.print(OutputSchedulazione.CRLF);
    }
  }
  
  protected
  String getSQL(LJSAMap parametri)
      throws Exception
  {
    List<String> listKeys = parametri.getKeysStartsWith("sql");
    if(listKeys.size() == 0) {
      throw new Exception(ILJSAErrors.sCUSTOM + "Comando sql non specificato.");
    }
    
    String sSQL = "";
    for(int i = 0; i < listKeys.size(); i++) {
      String sKey = (String) listKeys.get(i);
      sSQL += parametri.getString(sKey) + " ";
    }
    
    if(sSQL.endsWith(".sql")) {
      sSQL = loadSQL(sSQL);
    }
    
    sSQL = substParameter(sSQL, "$date", getCurrentDate());
    sSQL = substParameter(sSQL, "$time", getCurrentTime());
    
    sSQL = removeNullRegions(sSQL, parametri);
    
    @SuppressWarnings("unchecked")
    Iterator<Map.Entry<String, Object>> oItEntries = parametri.entrySet().iterator();
    while(oItEntries.hasNext()) {
      Map.Entry<String, Object> entry = oItEntries.next();
      String sKey = (String) entry.getKey();
      String sValue = null;
      Object oValue = entry.getValue();
      if(oValue == null) {
        sValue = "NULL";
      }
      else {
        sValue = oValue.toString();
        if(sValue.startsWith("$date")) {
          int iValue = LJSAMap.dateExpToInt(sValue);
          sValue = String.valueOf(iValue);
        }
      }
      sSQL = substParameter(sSQL, "[" + sKey + "]", sValue);
    }
    
    return correctWhereClause(sSQL);
  }
  
  protected static
  int getTypeReport(LJSAMap parametri)
  {
    String sTypeFile = parametri.getString("type");
    if(sTypeFile == null) return iTYPE_HTML;
    if(sTypeFile.equalsIgnoreCase("HTM") || sTypeFile.equalsIgnoreCase("HTML")) {
      return iTYPE_HTML;
    }
    else if(sTypeFile.equalsIgnoreCase("CSV")) {
      return iTYPE_CSV;
    }
    else if(sTypeFile.equalsIgnoreCase("XML")) {
      return iTYPE_XML;
    }
    else if(sTypeFile.equalsIgnoreCase("TXT") || sTypeFile.equalsIgnoreCase("TEXT")) {
      return iTYPE_TXT;
    }
    return iTYPE_HTML;
  }
  
  protected static
  boolean isSelect(String sSQL)
    throws Exception
  {
    return sSQL.trim().toUpperCase().startsWith("SELECT");
  }
  
  protected static
  String substParameter(String sText, String sPar, String sValue)
  {
    if(sValue == null) sValue = "";
    int iParLen  = sPar.length();
    int iTextLen = sText.length();
    int iIndexOf = sText.indexOf(sPar);
    while(iIndexOf >= 0) {
      String sLeft = sText.substring(0, iIndexOf);
      String sParValue = sValue;
      String sRight = null;
      if(iIndexOf + iParLen >= iTextLen) {
        sRight = "";
      }
      else {
        sRight = sText.substring(iIndexOf + iParLen);
      }
      if(isTextDelimit(sLeft, sRight)) {
        sParValue = doubleQuotes(sValue);
      }
      sText = sLeft + sParValue + sRight;
      
      iIndexOf = sText.indexOf(sPar);
    }
    return sText;
  }
  
  protected static
  boolean isTextDelimit(String sLeft, String sRight)
  {
    if(sLeft.length() > 0) {
      char cLast = sLeft.charAt(sLeft.length() - 1);
      if(cLast == '\'' || cLast == '%' || cLast == '_') {
        return true;
      }
    }
    if(sRight.length() > 0) {
      char cFirst = sRight.charAt(0);
      if(cFirst == '\'' || cFirst == '%' || cFirst == '_') {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Rimuove le regioni delimitate da { e } in cui vi sono parametri nulli.
   *
   * @param sSQL String
   * @param parametri LJSAMap
   * @return String
   */
  protected static
  String removeNullRegions(String sSQL, LJSAMap parametri)
  {
    int iTest = sSQL.indexOf('{');
    if(iTest < 0) return sSQL;
    
    int iSQLLength = sSQL.length();
    StringBuffer sbResult = new StringBuffer(iSQLLength);
    boolean boDoNotCopy = false;
    for(int i = 0; i < iSQLLength; i++) {
      char c = sSQL.charAt(i);
      if(c == '{') {
        int iEndRegion  = sSQL.indexOf('}', i);
        if(iEndRegion > 0) {
          boolean boAtLeastOneNull = false;
          for(int j = i; j < iEndRegion; j++) {
            char cp = sSQL.charAt(j);
            if(cp == '[') {
              int iBeginNextPar = sSQL.indexOf('[', j + 1);
              int iEndPar = sSQL.indexOf(']', j);
              if(iBeginNextPar > 0 && iEndPar > iBeginNextPar) {
                continue;
              }
              if(iEndPar > 0) {
                String sParName = sSQL.substring(j + 1, iEndPar);
                Object oValue = parametri.get(sParName);
                if(oValue == null || oValue.toString().length() == 0) {
                  boAtLeastOneNull = true;
                  break;
                }
              } // end if(iEndPar > 0)
            } // end if(cp == '[')
          } // end for(int j
          boDoNotCopy = boAtLeastOneNull;
        } // end if(iEndRegion > 0)
      }
      if(c == '}') {
        boDoNotCopy = false;
      }
      if(!boDoNotCopy && c != '{' && c != '}') {
        sbResult.append(c);
      }
    }
    return sbResult.toString();
  }
  
  /**
   * Corregge eventuali errori derivanti dalla sostituizione dei parametri
   * nel comando SQL.
   * Puo' succedere ad esempio che la clausola WHERE rimanga vuota. In tal
   * caso viene eliminata del tutto.
   * Se dopo la WHERE si ha un AND oppure un OR esso viene eliminato.
   *
   * @param sSQL String
   * @return String
   */
  protected static
  String correctWhereClause(String sSQL)
  {
    String sUSQL = sSQL.toUpperCase() + " ";
    int iWhere = sUSQL.indexOf(" WHERE ");
    if(iWhere < 0) return sSQL;
    int iBeginWord = -1;
    int iEndWord = -1;
    for(int i = iWhere + 7; i < sUSQL.length(); i++) {
      char c = sUSQL.charAt(i);
      if(Character.isLetter(c)) {
        if(iBeginWord < 0) {
          iBeginWord = i;
        }
      }
      else {
        if(iBeginWord > 0) {
          iEndWord = i;
          break;
        }
      }
    }
    String sWord = null;
    if(iBeginWord >= 0) {
      if(iEndWord < 0) iEndWord = sUSQL.length() - 1;
      sWord = sUSQL.substring(iBeginWord, iEndWord);
    }
    if(sWord == null) {
      return sSQL.substring(0, iWhere);
    }
    if(sWord.equals("AND") || sWord.equals("OR")) {
      return sSQL.substring(0, iWhere + 7) + sSQL.substring(iEndWord + 1);
    }
    return sSQL;
  }
  
  protected static
  String getCurrentDate()
  {
    Calendar cal = new GregorianCalendar();
    int iDate = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
    return String.valueOf(iDate);
  }
  
  protected static
  String getYesterday()
  {
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DATE, -1);
    int iDate = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
    return String.valueOf(iDate);
  }
  
  protected static
  String getCurrentTime()
  {
    Calendar cal = new GregorianCalendar();
    int iTime = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE);
    return String.valueOf(iTime);
  }
  
  protected static
  String doubleQuotes(String text)
  {
    StringBuffer result = new StringBuffer(text.length());
    char c;
    for(int i = 0; i < text.length(); i++) {
      c = text.charAt(i);
      if(c == '\'') result.append('\'');
      result.append(c);
    }
    return result.toString();
  }
  
  protected static
  void add(int iCol, String value, List<String> listTotalsCols, List<Number> listTotals)
  {
    int iIndex = -1;
    for(int i = 0; i < listTotalsCols.size(); i++) {
      if(listTotalsCols.get(i).equals(String.valueOf(iCol + 1))) {
        iIndex = i;
        break;
      }
    }
    
    if(iIndex < 0 || iIndex >= listTotals.size()) return;
    
    value = value.replace(',', '.');
    Number nValue = null;
    if(value.trim().length() == 0) {
      try{ nValue = 0; } catch(Exception ex) {}
    }
    else if(value.indexOf('.') > 0) {
      try{ nValue = WUtil.toDouble(nValue, 0); } catch(Exception ex) {}
    }
    else {
      try{ nValue = WUtil.toInteger(nValue, 0); } catch(Exception ex) {}
    }
    if(nValue == null) return;
    
    Number nTotal = (Number) listTotals.get(iIndex);
    if(nTotal == null) {
      nTotal = nValue;
    }
    else {
      if(nTotal instanceof Double || nValue instanceof Double) {
        nTotal = nTotal.doubleValue() + nValue.doubleValue();
      }
      else {
        nTotal = nTotal.intValue() + nValue.intValue();
      }
    }
    listTotals.set(iIndex, nTotal);
  }
  
  protected static
  String getTotal(int iCol, List<String> listTotalsCols, List<Number> listTotals)
  {
    int iIndex = -1;
    for(int i = 0; i < listTotalsCols.size(); i++) {
      if(listTotalsCols.get(i).equals(String.valueOf(iCol + 1))) {
        iIndex = i;
        break;
      }
    }
    if(iIndex < 0 || iIndex >= listTotals.size()) return null;
    
    Number nTotal = listTotals.get(iIndex);
    if(nTotal == null) return null;
    
    if(nTotal instanceof Double) {
      DecimalFormat df = new DecimalFormat("0.00");
      return df.format(nTotal.doubleValue());
    }
    
    return nTotal.toString();
  }
  
  protected static
  String loadSQL(String sSQLFile)
      throws Exception
  {
    String filePath = BEConfig.getLJSAReportsFolder() + File.separator + sSQLFile;
    StringBuilder sb = new StringBuilder();
    InputStream is = null;
    BufferedReader br = null;
    try {
      is = new FileInputStream(filePath);
      br = new BufferedReader(new InputStreamReader(is));
      String sLine = null;
      while((sLine = br.readLine()) != null) {
        if(sLine.trim().length() == 0) continue;
        if(sLine.startsWith("--")) continue;
        sb.append(sLine);
        sb.append(' ');
      }
    }
    finally {
      if(is != null) try{ is.close(); } catch(Exception ex) {}
      if(br != null) try{ br.close(); } catch(Exception ex) {}
    }
    return sb.toString();
  }
}
