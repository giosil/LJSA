package org.dew.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLJSAGUIWeb extends TestCase {
  
  public TestLJSAGUIWeb(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestLJSAGUIWeb.class);
  }
  
  public void testApp() throws Exception {
  }
}
