package org.dew.ljsa;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Bean contenente le informazioni di un file creato da OutputSchedulazione.
 */
public
class FileInfo implements Serializable
{
  private static final long serialVersionUID = -3449631015966487402L;
  
  private String type;
  private String fileName;
  private File   file;
  private OutputStream fileOutputStream;
  
  public FileInfo()
  {
  }
  
  public FileInfo(String fileName)
  {
    this.fileName = fileName;
    this.type = "O"; // Output
  }
  
  public FileInfo(String fileName, String type)
  {
    this.fileName = fileName;
    this.type = type;
  }
  
  public FileInfo(String fileName, String type, String filePath)
  {
    this.fileName = fileName;
    this.type = type;
    this.file = new File(filePath);
  }
  
  public FileInfo(String fileName, String type, String filePath, OutputStream fileOutputStream)
  {
    this.fileName = fileName;
    this.type = type;
    this.file = new File(filePath);
    this.fileOutputStream = fileOutputStream;
  }
  
  public File getFile() {
    return file;
  }
  
  public void setFile(File file) {
    this.file = file;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public OutputStream getFileOutputStream() {
    return fileOutputStream;
  }
  
  public void setFileOutputStream(OutputStream fileOutputStream) {
    this.fileOutputStream = fileOutputStream;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  @Override
  public boolean equals(Object object) {
    if(object instanceof FileInfo) {
      String objFileName = ((FileInfo) object).getFileName();
      if(fileName == null) return objFileName == null;
      return fileName.equals(objFileName);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if(fileName == null) return 0;
    return fileName.hashCode();
  }
  
  @Override
  public String toString() {
    return fileName;
  }
}
