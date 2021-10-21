package org.dew.ljsa.backend.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.dew.ljsa.backend.util.LJSA;
import org.dew.ljsa.backend.ws.WSFM;
import org.dew.ljsa.backend.ws.WSLJSA;

public
class WebServices extends org.rpc.server.RpcServlet
{
  private static final long serialVersionUID = 1513591189001762011L;
  
  @Override
  public
  void init(ServletConfig config)
    throws ServletException
  {
    super.init(config);
    LJSA.init();
  }
  
  @Override
  public
  void init()
    throws ServletException
  {
    rpcExecutor      = new org.rpc.server.MultiRpcExecutor();
    restAudit        = null;
    restTracer       = null;
    
    legacy           = false;
    createRpcContex  = false;
    checkSession     = false;
    checkSessionREST = false;
    restful          = true;
    basicAuth        = false;
    
    addWebService(new WSLJSA(), "LJSA", "API Services");
    addWebService(new WSFM(),   "FM",   "File manager");
  }
  
  @Override
  public
  void destroy()
  {
    super.destroy();
    LJSA.destroy();
  }
}
