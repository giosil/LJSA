package org.dew.ljsa.backend.web;

import java.security.Principal;

import java.util.List;

import javax.servlet.ServletException;

import org.dew.ljsa.backend.rpc.WSAttivita;
import org.dew.ljsa.backend.rpc.WSClassi;
import org.dew.ljsa.backend.rpc.WSCredenziali;
import org.dew.ljsa.backend.rpc.WSSchedulatore;
import org.dew.ljsa.backend.rpc.WSSchedulazioni;
import org.dew.ljsa.backend.rpc.WSServizi;

import org.rpc.util.SimplePrincipal;

public
class WebRpc extends org.rpc.server.RpcServlet
{
  private static final long serialVersionUID = 2361290876955563328L;
  
  @Override
  public
  void init()
    throws ServletException
  {
    rpcExecutor      = new org.rpc.server.MultiRpcExecutor();
    restAudit        = null;
    restTracer       = null;
    
    legacy           = false;
    createRpcContex  = true;
    checkSession     = false;
    checkSessionREST = false;
    restful          = true;
    basicAuth        = true;
    
    addWebService(new WSServizi(),       "SERVIZI",       "Gestione servizi");
    addWebService(new WSCredenziali(),   "CREDENZIALI",   "Gestione credenziali");
    addWebService(new WSClassi(),        "CLASSI",        "Gestione classi");
    addWebService(new WSAttivita(),      "ATTIVITA",      "Gestione attivit\340");
    addWebService(new WSSchedulazioni(), "SCHEDULAZIONI", "Gestione schedulazioni");
    addWebService(new WSSchedulatore(),  "SCHEDULATORE",  "Gestione schedulatore");
  }
  
  @Override
  protected
  Principal authenticate(String username, String password)
  {
    List<String> services = null;
    try {
      services = WSCredenziali.authenticate(username, password);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    if(services != null && services.size() > 0) {
      return new SimplePrincipal(username);
    }
    return null;
  }
}
