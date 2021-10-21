package org.dew.ljsa.backend.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import org.apache.commons.pool.PoolableObjectFactory;

public
class POFLJSAConnection implements PoolableObjectFactory<Connection>
{
  @Override
  public
  Connection makeObject()
    throws Exception
  {
    String sDriver   = BEConfig.getProperty(BEConfig.sLJSA_CONF_JDBC_DRIVER);
    if(sDriver != null) {
      Class.forName(sDriver);
    }
    String sURL      = BEConfig.getProperty(BEConfig.sLJSA_CONF_JDBC_URL);
    if(sURL == null) {
      throw new Exception(BEConfig.sLJSA_CONF_JDBC_URL + " undefined.");
    }
    String sUser     = BEConfig.getProperty(BEConfig.sLJSA_CONF_JDBC_USER);
    String sPassword = BEConfig.getProperty(BEConfig.sLJSA_CONF_JDBC_PWD);
    Connection conn  = DriverManager.getConnection(sURL, sUser, sPassword);
    conn.setAutoCommit(false);
    return conn;
  }
  
  @Override
  public
  void destroyObject(Connection object)
    throws Exception
  {
    object.close();
  }
  
  @Override
  public
  boolean validateObject(Connection object)
  {
    boolean result = false;
    try {
      if(object == null || object.isClosed()) return false;
      DatabaseMetaData metadata = object.getMetaData();
      result = metadata != null;
    }
    catch(Exception ex) {
      System.err.println("Exception in POFLJSAConnection.validateObject(" + object + "): " + ex);
    }
    return result;
  }
  
  @Override
  public
  void activateObject(Connection object)
  {
  }
  
  @Override
  public
  void passivateObject(Connection object)
  {
  }
}
