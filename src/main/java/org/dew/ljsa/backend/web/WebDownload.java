package org.dew.ljsa.backend.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.util.WUtil;

import org.dew.ljsa.backend.util.BEConfig;
import org.dew.ljsa.backend.util.ConnectionManager;

public
class WebDownload extends HttpServlet
{
  private static final long serialVersionUID = -1331044709175445162L;
  
  @Override
  public
  void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    doGet(request, response);
  }
  
  @Override
  public
  void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String pathInfo = request.getPathInfo();
    if(pathInfo == null || pathInfo.length() == 0) {
      response.sendError(404); // Not Found
      return;
    }
    
    int    idLog    = getIdLog(pathInfo);
    String fileName = getFileName(pathInfo);
    
    boolean noSubPath = false;
    if(!pathInfo.endsWith(idLog + "/")) {
      noSubPath = true;
    }
    
    int level = 1;
    if(noSubPath) {
      if(fileName != null && fileName.length() > 0) {
        level = 2;
      }
    }
    else {
      level = 2;
    }
    
    if(!WebResources.checkAuthorization(request, response, level, idLog)) return;
    
    boolean boFilesFromList = fileName != null && fileName.equals("*");
    if(boFilesFromList) fileName = null;
    
    if(fileName != null && fileName.length() > 0) {
      File file = getFile(idLog, fileName);
      
      if(file == null) {
        response.sendError(404); // Not Found
        return;
      }
      
      response.setContentLength((int) file.length());
      response.setContentType(WebResources.getContentType(file));
      
      OutputStream out = response.getOutputStream();
      FileInputStream fis = new FileInputStream(file);
      int iBytesReaded = 0;
      byte[] abBuffer = new byte[1024];
      while((iBytesReaded = fis.read(abBuffer)) > 0) {
        out.write(abBuffer, 0, iBytesReaded);
      }
      fis.close();
    }
    else {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      
      WebResources.printHeader(out, level);
      
      out.println("<br />");
      out.println("<table align=\"center\" class=\"table_files\">");
      out.println("<tr>");
      out.println("<th>Nome File</th>");
      out.println("<th>Tipologia</th>");
      out.println("<th>Dimensione</th>");
      out.println("<th>Content Type</th>");
      out.println("<th>Ult. Modifica</th>");
      out.println("</tr>");
      List<List<Object>> listFiles = null;
      if(boFilesFromList) {
        listFiles = getListFiles(idLog);
      }
      else {
        listFiles = getFiles(idLog);
      }
      for(int i = 0; i < listFiles.size(); i++) {
        List<Object> record = listFiles.get(i);
        if((i % 2) == 0) {
          out.println("<tr class=\"even_row\">");
        }
        else {
          out.println("<tr class=\"odd_row\">");
        }
        String sNomeFile = WUtil.toString(record.get(1), null);
        File file = getFile(idLog, sNomeFile);
        if(file != null) {
          if(noSubPath && !boFilesFromList) {
            out.println("<td><a href=\"" + idLog + "/" + sNomeFile + "\">" + sNomeFile + "</a></td>");
          }
          else {
            out.println("<td><a href=\"" + sNomeFile+ "\">" + sNomeFile + "</a></td>");
          }
        }
        else {
          out.println("<td>" + sNomeFile + "</td>");
        }
        out.println("<td>" + getDescTipologia(record.get(0))      + "</td>");
        out.println("<td>" + WebResources.getDimensione(file)     + "</td>");
        out.println("<td>" + WebResources.getContentType(file)    + "</td>");
        out.println("<td>" + WebResources.getUltimaMofifica(file) + "</td>");
        out.println("</tr>");
      }
      out.println("</table>");
      
      WebResources.printFooter(out, level);
    }
  }
  
  protected static
  int getIdLog(String pathInfo)
    throws ServletException
  {
    int result = 0;
    if(pathInfo == null) {
      throw new ServletException("Missing Log Identifier");
    }
    if(pathInfo.charAt(0) == '/') pathInfo = pathInfo.substring(1);
    int sep = pathInfo.indexOf('/');
    try {
      if(sep < 0) {
        result = WUtil.toInt(pathInfo, 0);
      }
      else {
        result = WUtil.toInt(pathInfo.substring(0, sep), 0);
      }
    }
    catch(Exception ex) {
      throw new ServletException("Invalid Log Identifier");
    }
    return result;
  }
  
  protected static
  String getFileName(String pathInfo)
  {
    if(pathInfo == null) return null;
    if(pathInfo.charAt(0) == '/') pathInfo = pathInfo.substring(1);
    int iBegin = pathInfo.indexOf('/');
    if(iBegin < 0) {
      return null;
    }
    else if(iBegin == pathInfo.length() - 1) {
      return null;
    }
    return pathInfo.substring(iBegin + 1);
  }
  
  protected static
  File getFile(int idLog, String fileName)
  {
    String outFolder = BEConfig.getLJSAOutputFolder(idLog);
    String filePath  = outFolder + File.separator + fileName;
    File file = new File(filePath);
    if(!file.exists()) {
      return null;
    }
    if(!file.isFile()) {
      return null;
    }
    return file;
  }
  
  protected
  String getDescTipologia(Object tipologia)
  {
    String sTipologia = WUtil.toString(tipologia, null);
    if(tipologia == null) {
      return "Output";
    }
    if(sTipologia.equalsIgnoreCase("O")) {
      return "Output";
    }
    else if(sTipologia.equalsIgnoreCase("E")) {
      return "Errori";
    }
    else if(sTipologia.equalsIgnoreCase("I")) {
      return "Informazioni";
    }
    else if(sTipologia.equalsIgnoreCase("M")) {
      return "Messaggio";
    }
    else if(sTipologia.equalsIgnoreCase("R")) {
      return "Rapporto";
    }
    else if(sTipologia.equalsIgnoreCase("T")) {
      return "Temporaneo";
    }
    return "Output";
  }
  
  protected
  List<List<Object>> getFiles(int idLog)
    throws ServletException
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    Connection conn = null;
    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      conn = ConnectionManager.getDefaultConnection();
      pstm = conn.prepareStatement("SELECT TIPOLOGIA,NOME_FILE,URL_FILE FROM LJSA_LOG_FILES WHERE ID_LOG=?");
      pstm.setInt(1, idLog);
      rs = pstm.executeQuery();
      while(rs.next()) {
        String sTipologia = rs.getString("TIPOLOGIA");
        String sNomeFile  = rs.getString("NOME_FILE");
        String sURL       = rs.getString("URL_FILE");
        
        List<Object> record = new ArrayList<Object>(3);
        record.add(sTipologia);
        record.add(sNomeFile);
        record.add(sURL);
        
        listResult.add(record);
      }
    }
    catch(Exception ex) {
      throw new ServletException(ex);
    }
    finally {
      ConnectionManager.close(rs, pstm, conn);
    }
    return listResult;
  }
  
  protected
  List<List<Object>> getListFiles(int idLog)
    throws ServletException
  {
    List<List<Object>> listResult = new ArrayList<List<Object>>();
    
    String sOutputFolder = BEConfig.getLJSAOutputFolder(idLog);
    String sURLDownload  = BEConfig.getLJSADownload(idLog);
    
    File fOutputFolder = new File(sOutputFolder);
    if(!fOutputFolder.exists()) {
      return listResult;
    }
    if(!fOutputFolder.isDirectory()) {
      return listResult;
    }
    File[] files = fOutputFolder.listFiles();
    for(File file : files) {
      if(!file.isFile()) continue;
      String fileName = file.getName();
      
      List<Object> record = new ArrayList<Object>(3);
      record.add("O");
      record.add(fileName);
      record.add(sURLDownload + "/" + fileName);
      
      listResult.add(record);
    }
    return listResult;
  }
}
