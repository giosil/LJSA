package org.dew.test;

import java.sql.Connection;
import java.sql.DriverManager;

import org.dew.ljsa.LJSAMap;
import org.dew.ljsa.LJTest;
import org.dew.ljsa.OutputSchedulazione;
import org.dew.ljsa.Schedulazione;

public 
class TestLJSAJob extends LJTest 
{
  public TestLJSAJob()
  {
    Class<?> this_class  = this.getClass();
    Class<?> super_class = this_class.getSuperclass();
    System.out.println("Test of " + super_class.getName());
  }
  
  public
  static void main(String[] args)
  {
    TestLJSAJob tester = new TestLJSAJob();
    try {
      tester.test();
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
  }
  
  public
  Schedulazione getTestSchedulazione()
  {
    Schedulazione schedulazione = new Schedulazione("TEST", "test", "now", "user");
    schedulazione.addParametro("sleep",     "2");
    schedulazione.addParametro("exception", "");
    return schedulazione;
  }
  
  public 
  OutputSchedulazione getTestOutputSchedulazione() 
  {
    OutputSchedulazione outputSchedulazione = new OutputSchedulazione(1);
    return outputSchedulazione;
  }
  
  public
  void test()
    throws Exception
  {
    long lBegin = System.currentTimeMillis();
    long lEnd = lBegin;
    
    Schedulazione schedulazione = getTestSchedulazione();
    OutputSchedulazione outputSchedulazione = getTestOutputSchedulazione();
    
    this._schedulazione = schedulazione;
    this._outputSchedulazione = outputSchedulazione;
    try {
        System.out.println("init...");
        init(schedulazione, outputSchedulazione);
        
        System.out.println("execute...");
        execute(schedulazione, outputSchedulazione);
    }
    catch (Exception ex) {
        ex.printStackTrace();
        
        System.out.println("exceptionOccurred...");
        exceptionOccurred(ex);
        throw ex;
    }
    finally {
        System.out.println("destroy...");
        destroy(schedulazione, outputSchedulazione);
    }
    
    System.out.println("Report: " + outputSchedulazione.getReport());
    System.out.println("Mail:");
    System.out.println("---------------------------------------------------------");
    System.out.println(outputSchedulazione.getSubject());
    System.out.println("---------------------------------------------------------");
    System.out.println(outputSchedulazione.getMessage());
    System.out.println("---------------------------------------------------------");
    
    lEnd = System.currentTimeMillis();
    long lDiff = (lEnd - lBegin) / 1000;
    
    System.out.println(lDiff + " seconds elapsed");
  }
  
  @Override
  protected 
  Connection getConnection(LJSAMap configurazione) 
    throws Exception 
  {
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url    = "jdbc:oracle:thin:@localhost:1521:orcl";
    String user   = "LJSA";
    String pass   = "LJSA";
    
    Class.forName(driver);
    
    Connection conn = DriverManager.getConnection(url, user, pass);
    
    conn.setAutoCommit(false);
    
    return conn;
  }
  
  @Override
  protected 
  Connection getConnection(String prefix, LJSAMap configurazione) 
    throws Exception 
  {
    String driver = "oracle.jdbc.driver.OracleDriver";
    String url    = "jdbc:oracle:thin:@localhost:1521:orcl";
    String user   = "LJSA";
    String pass   = "LJSA";
    
    Class.forName(driver);
    
    Connection conn = DriverManager.getConnection(url, user, pass);
    
    conn.setAutoCommit(false);
    
    return conn;
  }
}
