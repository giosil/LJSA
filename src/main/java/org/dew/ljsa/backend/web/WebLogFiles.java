package org.dew.ljsa.backend.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dew.ljsa.backend.util.BEConfig;

public
class WebLogFiles extends HttpServlet
{
  private static final long serialVersionUID = 1993064186943080331L;
  
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
    String fileName = null;
    
    int level = 1;
    if(pathInfo != null && pathInfo.length() > 0) {
      fileName = getFileName(pathInfo);
    }
    else {
      level = 0;
    }
    
    if(!WebResources.checkAuthorization(request, response, level, 0)) {
      return;
    }
    
    if(fileName != null && fileName.length() > 0) {
      File file = getFile(fileName);
      if(file == null) {
        response.sendError(404); // Not Found
        return;
      }
      String sContentType = WebResources.getContentType(file);
      
      response.setContentLength((int) file.length());
      response.setContentType(sContentType);
      
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
      out.println("<th>Dimensione</th>");
      out.println("<th>Content Type</th>");
      out.println("<th>Ult. Modifica</th>");
      out.println("</tr>");
      
      List<String> listFiles = getListFiles();
      for(int i = 0; i < listFiles.size(); i++) {
        fileName = listFiles.get(i);
        if((i % 2) == 0) {
          out.println("<tr class=\"even_row\">");
        }
        else {
          out.println("<tr class=\"odd_row\">");
        }
        File file = getFile(fileName);
        if(file != null) {
          if(level == 0) {
            out.println("<td><a href=\"log/" + fileName + "\">" + fileName + "</a></td>");
          }
          else {
            out.println("<td><a href=\"" + fileName + "\">" + fileName + "</a></td>");
          }
        }
        else {
          out.println("<td>" + fileName + "</td>");
        }
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
  String getFileName(String pathInfo)
  {
    String result = null;
    if(pathInfo == null) return result;
    if(pathInfo.charAt(0) == '/') {
      pathInfo = pathInfo.substring(1);
    }
    int iBegin = pathInfo.indexOf('/');
    if(iBegin < 0) {
      result = pathInfo;
    }
    else {
      result = pathInfo.substring(0, iBegin);
    }
    return result;
  }
  
  protected static
  File getFile(String fileName)
  {
    File file = new File(BEConfig.getLJSALogFolder() + File.separator + fileName);
    if(!file.exists()) {
      return null;
    }
    if(!file.isFile()) {
      return null;
    }
    return file;
  }
  
  protected static
  List<String> getListFiles()
    throws ServletException
  {
    List<String> listResult = new ArrayList<String>();
    String logFolderPath = BEConfig.getLJSALogFolder();
    File logFolder = new File(logFolderPath);
    if(!logFolder.exists()) {
      return listResult;
    }
    if(!logFolder.isDirectory()) {
      return listResult;
    }
    File[] afFiles = logFolder.listFiles();
    for(int i = 0; i < afFiles.length; i++) {
      File file = afFiles[i];
      if(!file.isFile()) continue;
      listResult.add(file.getName());
    }
    return listResult;
  }
}
