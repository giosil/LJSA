package org.dew.ljsa.backend.web;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.util.WUtil;

import org.dew.ljsa.backend.sched.LJSAScheduler;

import org.dew.ljsa.backend.util.ConnectionManager;
import org.dew.ljsa.backend.util.QueryBuilder;

public
class WebResources
{
  public static final String sMSG_INVALID_CRED = "Credenziali non valide";
  
  protected static Map<String, String> mapMIME = new HashMap<String, String>();
  
  static {
    mapMIME.put("txt",   "text/plain");
    mapMIME.put("dat",   "text/plain");
    mapMIME.put("csv",   "text/plain");
    mapMIME.put("html",  "text/html");
    mapMIME.put("htm",   "text/html");
    mapMIME.put("xml",   "text/xml");
    mapMIME.put("log",   "text/plain");
    mapMIME.put("rtf",   "application/rtf");
    mapMIME.put("doc",   "application/msword");
    mapMIME.put("docx",  "application/msword");
    mapMIME.put("xls",   "application/x-msexcel");
    mapMIME.put("xlsx",  "application/x-msexcel");
    mapMIME.put("pdf",   "application/pdf");
    mapMIME.put("gif",   "image/gif");
    mapMIME.put("bmp",   "image/bmp");
    mapMIME.put("jpg",   "image/jpeg");
    mapMIME.put("jpeg",  "image/jpeg");
    mapMIME.put("tif",   "image/tiff");
    mapMIME.put("tiff",  "image/tiff");
    mapMIME.put("png",   "image/png");
    mapMIME.put("mpg",   "video/mpeg");
    mapMIME.put("mpeg",  "video/mpeg");
    mapMIME.put("mpeg4", "video/mpeg");
    mapMIME.put("mov",   "video/quicktime");
    mapMIME.put("flv",   "video/x-flv");
    mapMIME.put("mp3",   "audio/mp3");
    mapMIME.put("wav",   "audio/wav");
    mapMIME.put("wma",   "audio/wma");
    mapMIME.put("tar",   "application/x-tar");
    mapMIME.put("zip",   "application/x-zip-compressed");
  }
  
  public static
  String getContentType(String sExt)
  {
    if(sExt == null || sExt.length() == 0) {
      return "text/plain";
    }
    String sResult = mapMIME.get(sExt);
    if(sResult != null) return sResult;
    return "text/plain";
  }
  
  public static
  String getContentType(File file)
  {
    if(file == null) return "N/A";
    String fileName  = file.getName();
    String extension = null;
    int dot = fileName.lastIndexOf('.');
    if(dot >= 0 && dot < fileName.length() - 1) {
      extension = fileName.substring(dot + 1);
    }
    return WebResources.getContentType(extension);
  }
  
  public static
  boolean checkAuthorization(HttpServletRequest request, HttpServletResponse response, Writer out)
    throws ServletException, IOException
  {
    return checkAuthorization(request, response, out, 0, 0);
  }
  
  public static
  boolean checkAuthorization(HttpServletRequest request, HttpServletResponse response, Writer out, int iLevel)
    throws ServletException, IOException
  {
    return checkAuthorization(request, response, out, iLevel, 0);
  }
  
  public static
  boolean checkAuthorization(HttpServletRequest request, HttpServletResponse response, int level, int idLog)
    throws ServletException, IOException
  {
    return checkAuthorization(request, response, null, level, idLog);
  }
  
  public static
  boolean checkAuthorization(HttpServletRequest request, HttpServletResponse response, Writer out, int level, int idLog)
    throws ServletException, IOException
  {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    
    if(username != null && password != null) {
      boolean boCheckLJSAUser = checkLJSAUser(username, password);
      if(boCheckLJSAUser) {
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("username",   username);
        httpSession.setAttribute("isLJSAUser", "1");
        return true;
      }
      
      if(idLog == 0) {
        response.setContentType("text/html");
        if(out == null) out = response.getWriter();
        printLoginPage(out, WebResources.sMSG_INVALID_CRED, level);
        return false;
      }
      else {
        if(checkServiceUser(idLog, username, password)) {
          HttpSession httpSession = request.getSession(true);
          httpSession.setAttribute("username", username);
          return true;
        }
        else {
          response.setContentType("text/html");
          if(out == null) out = response.getWriter();
          printLoginPage(out, WebResources.sMSG_INVALID_CRED, level);
          return false;
        }
      }
    }
    else {
      HttpSession httpSession = request.getSession(false);
      if(httpSession == null || httpSession.getAttribute("username") == null) {
        response.setContentType("text/html");
        if(out == null) out = response.getWriter();
        printLoginPage(out, null, level);
        return false;
      }
      
      if(idLog == 0 && httpSession != null) {
        boolean isLJSAUser = WUtil.toBoolean(httpSession.getAttribute("isLJSAUser"), false);
        if(!isLJSAUser) {
          response.setContentType("text/html");
          if(out == null) out = response.getWriter();
          printLoginPage(out, null, level);
          return false;
        }
      }
    }
    return true;
  }
  
  public static
  void printHeader(Writer out)
    throws IOException
  {
    printHeader(out, 0);
  }
  
  public static
  void printHeader(Writer out, int level)
    throws IOException
  {
    String styleSheetHRef = "css/ljsa.css";
    for(int i = 1; i <= level; i++) {
      styleSheetHRef = "../" + styleSheetHRef;
    }
    String version = LJSAScheduler.getVersion();
    
    out.write("<!DOCTYPE html>\n");
    out.write("<html>\n");
    out.write("<head>\n");
    out.write("<title>LJSA " + version + " - Light Java Scheduler Application</title>\n");
    out.write("<link rel=\"stylesheet\" href=\"" + styleSheetHRef + "\">\n");
    out.write("</head>\n");
    out.write("<body>\n");
    out.write("<h3>LJSA " + version + " - Light Java Scheduler Application</h3>\n");
    out.write("<hr />\n");
  }
  
  public static
  void printFooter(Writer out)
    throws IOException
  {
    printFooter(out, 0);
  }
  
  public static
  void printFooter(Writer out, int level)
    throws IOException
  {
    out.write("<br />\n");
    out.write("<hr />\n");
    out.write("<div class=\"footer\"></div>\n");
    out.write("</body>\n");
    out.write("</html>\n");
  }
  
  public static
  void printLoginPage(Writer out, String message)
    throws IOException
  {
    printHeader(out);
    printLoginForm(out, message);
    printFooter(out);
  }
  
  public static
  void printLoginForm(Writer out, String message)
    throws IOException
  {
    printLoginForm(out, message, 0);
  }
  
  public static
  void printLoginPage(Writer out, String message, int level)
    throws IOException
  {
    printHeader(out, level);
    printLoginForm(out, message, level);
    printFooter(out, level);
  }
  
  public static
  void printLoginForm(Writer out, String message, int level)
    throws IOException
  {
    out.write("<form method=\"post\" name=\"login_form\" class=\"login_form\">\n");
    out.write("<label for=\"username\">UserName:</label> <input type=\"text\" name=\"username\" size=\"20\"><br />\n");
    out.write("<label for=\"password\">Password:</label> <input type=\"password\" name=\"password\" size=\"20\"><br />\n");
    out.write("<input type=\"submit\" value=\"Accedi\"> <input type=\"reset\" value=\"Annulla\"><br />\n");
    out.write("</form>\n");
    if(message != null && message.trim().length() > 0) {
      out.write("<p class=\"err_message\">" + WUtil.toHTMLText(message, "") + "</p>\n");
    }
  }
  
  public static
  void printMessagePage(Writer out, String message)
    throws IOException
  {
    printMessagePage(out, message, 0);
  }
  
  public static
  void printMessagePage(Writer out, String message, int level)
    throws IOException
  {
    printHeader(out, level);
    out.write("<h4 class=\"info_message\">" + WUtil.toHTMLText(message, "") + "</h4>\n");
    printFooter(out, level);
  }
  
  public static
  boolean checkLJSAUser(String username, String password)
  {
    try {
      LJSAScheduler.checkAuthorization(username, password, false);
    }
    catch(Exception ex) {
      return false;
    }
    return true;
  }
  
  public static
  boolean checkServiceUser(int idLog, String username, String password)
    throws ServletException, IOException
  {
    boolean result = false;
    
    String idServizio = null;
    String sSQL = null;
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      
      sSQL = "SELECT S.ID_SERVIZIO FROM LJSA_SCHEDULAZIONI S,LJSA_LOG L WHERE S.ID_SCHEDULAZIONE=L.ID_SCHEDULAZIONE AND L.ID_LOG=?";
      pstm = conn.prepareStatement(sSQL);
      pstm.setInt(1, idLog);
      rs = pstm.executeQuery();
      if(rs.next()) idServizio = rs.getString("ID_SERVIZIO");
      if(idServizio == null || idServizio.length() == 0) return true;
      result = checkCredential(conn, idServizio, username, password);
    }
    catch(Exception ex) {
      throw new ServletException(ex);
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return result;
  }
  
  protected static
  String getDimensione(File file)
  {
    if(file == null) return "N/A";
    if(file.isDirectory()) {
      return "&nbsp;";
    }
    long lLength = file.length();
    if(lLength < 1024) {
      return lLength + " bytes";
    }
    else {
      long lKB = lLength / 1024l;
      long lDecimal = (lLength % 1024l) * 10 / 1024l;
      if(lDecimal > 0) {
        return lKB + "." + lDecimal + " KB";
      }
      return lKB + " KB";
    }
  }
  
  protected static
  String getUltimaMofifica(File file)
  {
    if(file == null) return "N/A";
    long lLastModified = file.lastModified();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new java.util.Date(lLastModified));
    return WUtil.formatDateTime(cal, "-", false);
  }
  
  public static
  boolean checkCredential(Connection conn, String idServizio, String username, String password)
    throws Exception
  {
    if(username == null || username.length() == 0) return false;
    if(password == null || password.length() == 0) return false;
    boolean boResult = false;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      pstm = conn.prepareStatement("SELECT * FROM LJSA_CREDENZIALI WHERE ID_SERVIZIO=? AND ID_CREDENZIALE=? AND CREDENZIALI=? AND ATTIVO=?");
      pstm.setString(1, idServizio);
      pstm.setString(2, username);
      pstm.setString(3, String.valueOf(password.hashCode()));
      pstm.setString(4, QueryBuilder.decodeBoolean(true));
      rs = pstm.executeQuery();
      boResult = rs.next();
    }
    finally {
      ConnectionManager.close(rs, pstm);
    }
    return boResult;
  }
}