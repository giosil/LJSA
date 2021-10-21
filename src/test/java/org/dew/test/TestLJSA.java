package org.dew.test;

import org.dew.ljsa.backend.sched.LJSAScheduler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLJSA extends TestCase {
  
  public TestLJSA(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestLJSA.class);
  }
  
  public void testApp() throws Exception {
    System.out.println("LJSAScheduler.getVersion() -> " + LJSAScheduler.getVersion());
  }
  
}
