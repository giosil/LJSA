package org.dew.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestLJSAJob extends TestCase {
  
  public TestLJSAJob(String testName) {
    super(testName);
  }
  
  public static Test suite() {
    return new TestSuite(TestLJSAJob.class);
  }
  
  public void testApp() throws Exception {
    
    WrapperLJSAJob wrapperLJSAJob = new WrapperLJSAJob();
    wrapperLJSAJob.start();
    
  }
  
}
