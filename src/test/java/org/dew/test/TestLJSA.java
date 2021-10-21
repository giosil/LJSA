package org.dew.test;

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
  }
}
