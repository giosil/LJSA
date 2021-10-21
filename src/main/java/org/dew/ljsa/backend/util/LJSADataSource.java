package org.dew.ljsa.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashSet;

import javax.transaction.UserTransaction;

import org.apache.commons.pool.impl.StackObjectPool;

public
class LJSADataSource
{
  protected static HashSet<Integer> setOfHashConnections;
  protected static StackObjectPool<Connection> pool;
  
  static {
    init();
  }
  
  public static
  void init()
  {
    setOfHashConnections = new HashSet<Integer>();
    int idle = BEConfig.getIntProperty(BEConfig.sLJSA_CONF_JDBC_IDLE, 1);
    pool = new StackObjectPool<Connection>(new POFLJSAConnection(), idle);
  }
  
  public static
  UserTransaction getUserTransaction(Connection conn)
    throws Exception
  {
    return new SimpleUserTransaction(conn);
  }
  
  public static
  Connection getDefaultConnection()
    throws Exception
  {
    Connection conn = pool.borrowObject();
    setOfHashConnections.add(conn.hashCode());
    return conn;
  }
  
  public static
  Connection getConnection(String sName)
    throws Exception
  {
    String driver = BEConfig.getProperty(sName + ".jdbc.driver");
    if(driver != null && driver.length() > 0) {
      Class.forName(driver);
    }
    String url = BEConfig.getProperty(sName + ".jdbc.url");
    if(url == null || url.length() == 0) {
      throw new Exception(sName + ".jdbc.url undefined.");
    }
    String user = BEConfig.getProperty(sName + ".jdbc.user");
    String pass = BEConfig.getProperty(sName + ".jdbc.password");
    Connection conn = DriverManager.getConnection(url, user, pass);
    conn.setAutoCommit(false);
    return conn;
  }
  
  public static
  void closeConnection(Connection conn)
  {
    if(conn == null) return;
    try{
      int hashCode = conn.hashCode();
      if(setOfHashConnections.contains(hashCode)) {
        pool.returnObject(conn);
        setOfHashConnections.remove(hashCode);
      }
      else {
        conn.close();
      }
    }
    catch(Exception ex) {
      System.err.println("Exception in LJSADataSource.closeConnection: " + ex);
    }
  }
  
  public static
  void closeLJSAConnections()
  {
    try {
      pool.close();
    }
    catch(Exception ex) {
      System.err.println("Exception in LJSADataSource.closeLJSAConnections: " + ex);
    }
  }
}
