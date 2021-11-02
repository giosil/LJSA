package org.dew.ljsa;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import org.dew.ljsa.backend.util.BEConfig;

import org.quartz.Job;

/**
 * LJSA implementation of ClassLoader.
 */
public
class LJSAClassLoader extends ClassLoader
{
  @SuppressWarnings("unchecked")
  public
  Class<? extends Job> loadJobClass(String name) 
    throws ClassNotFoundException
  {
    Class<?> result = loadClass(name);
    
    if(result.isInstance(Job.class)) {
      return (Class<? extends Job>) result;
    }
    
    return null;
  }
  
  public
  Class<?> findClass(String name)
  {
    Class<?> result = null;
    String sFilePath = BEConfig.getLJSAClassesFolder() + File.separator + name.replace('.', File.separatorChar) + ".class";
    File file = new File(sFilePath);
    if(!file.exists() || !file.isFile()) {
      
      try {
        result = Class.forName(name);
      }
      catch(Exception ex) {
        ex.printStackTrace();
        return null;
      }
      
      return result;
    }
    byte[] b = loadClassData(sFilePath);
    if(b == null) return null;
    return defineClass(name, b, 0, b.length);
  }
  
  protected
  URL findResource(String name)
  {
    String filePath = BEConfig.getLJSAClassesFolder() + File.separator + name;
    File file = new File(filePath);
    if(file.exists() && file.isFile()) {
      URL url = null;
      try {
        url = file.toURI().toURL();
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return url;
    }
    return null;
  }
  
  private
  byte[] loadClassData(String filePath)
  {
    byte[] result = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filePath);
      int available = fis.available();
      result = new byte[available];
      fis.read(result);
    }
    catch(Exception ex) {
      ex.printStackTrace();
      return null;
    }
    finally {
      if(fis != null) try{ fis.close(); } catch(Exception ex) {}
    }
    return result;
  }
}
