package org.dew.ljsa.web;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "WebModule", loadOnStartup = 1, urlPatterns = { "/module" })
public 
class WebModule extends HttpServlet
{
  private static final long serialVersionUID = 1565017307434951263L;
  
  private static final String URL_WRAPP_REFRESH = "http://localhost:8080/wrapp/api/refresh?module=wljsa";
  
  protected String refreshResult;
  
  public
  void init()
    throws ServletException
  {
    System.out.println("org.dew.ljsa.web.WebModule.init()...");
    
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(URL_WRAPP_REFRESH).openConnection();
      connection.setConnectTimeout(2000);
      connection.setReadTimeout(2000);
      
      refreshResult = "HTTP " + connection.getResponseCode();
    }
    catch(Exception ex) {
      refreshResult = ex.toString();
    }
    
    System.out.println("org.dew.ljsa.web.WebModule.init() " + refreshResult);
  }
  
  @Override
  protected 
  void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    request.setAttribute("message", refreshResult);
    
    RequestDispatcher requestDispatcher = request.getRequestDispatcher("index.jsp");
    requestDispatcher.forward(request, response);
  }
}
