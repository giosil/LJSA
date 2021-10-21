package org.dew.ljsa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dew.ljsa.backend.util.BEConfig;

/**
 * Classe utilizzata per gestire l'output di un'attivita' schedulata.
 */
public
class OutputSchedulazione
{
  public final static String sSTATO_COMPLETA   = "C";
  public final static String sSTATO_INTERROTTA = "I";
  public final static String sSTATO_ERRORI     = "E";
  
  public static final char[] CRLF = {13, 10};
  
  protected int idLog;
  protected String status;
  protected String report;
  protected String message;
  protected String subject;
  protected PrintStream psLog;
  protected boolean boMessageFromConfiguration = false;
  
  protected List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
  
  /**
   * Se idLog == 0 non viene creata la cartella con i file di output.
   * In tal caso i metodi createXXXFile restituiscono un EmptyOutputStream.
   *
   * @param iIdLog int
   */
  public
  OutputSchedulazione(int idLog)
  {
    this.idLog = idLog;
  }
  
  public
  String getReport()
  {
    return report;
  }
  
  public
  void setReport(String report)
  {
    this.report = report;
  }
  
  public
  String getMessage()
  {
    return message;
  }
  
  public
  void setMessage(String message)
  {
    this.message = message;
  }
  
  public
  String getSubject()
  {
    return subject;
  }
  
  public
  void setSubject(String subject)
  {
    this.subject = subject;
  }
  
  public
  boolean isMessageFromConfiguration()
  {
    return boMessageFromConfiguration;
  }
  
  public
  void setMessageFromConfiguration(boolean boMessageFromConfiguration)
  {
    this.boMessageFromConfiguration = boMessageFromConfiguration;
  }
  
  public
  String getStatus()
  {
    if(status == null) status = sSTATO_COMPLETA;
    return status;
  }
  
  public
  int getIdLog()
  {
    return idLog;
  }
  
  public
  void setErrorStatus()
  {
    status = sSTATO_ERRORI;
  }
  
  public
  void setCompletedStatus()
  {
    status = sSTATO_COMPLETA;
  }
  
  public
  void setInterruptedStatus()
  {
    status = sSTATO_INTERROTTA;
  }
  
  public
  boolean isErrorStatus()
  {
    if(status == null) status = sSTATO_COMPLETA;
    return status.equals(sSTATO_ERRORI);
  }
  
  public
  boolean isInterruptedStatus()
  {
    if(status == null) status = sSTATO_COMPLETA;
    return status.equals(sSTATO_INTERROTTA);
  }
  
  public
  boolean hasFiles()
  {
    return listFileInfo.size() > 0;
  }
  
  public
  int getFilesCount()
  {
    return listFileInfo.size();
  }
  
  public
  String getURLDownload()
  {
    return BEConfig.getLJSADownload(idLog);
  }
  
  public
  void removeFile(int iIndex)
  {
    if(iIndex >= listFileInfo.size() || iIndex < 0) {
      return;
    }
    FileInfo fileInfo = listFileInfo.get(iIndex);
    OutputStream fos  = fileInfo.getFileOutputStream();
    try{ fos.close(); } catch(Exception ex) {}
    File file = fileInfo.getFile();
    file.delete();
    listFileInfo.remove(iIndex);
  }
  
  public
  void removeAllFiles()
  {
    for(int i = 0; i < listFileInfo.size(); i++) {
      FileInfo fileInfo = listFileInfo.get(i);
      OutputStream fos  = fileInfo.getFileOutputStream();
      try{ fos.close(); } catch(Exception ex) {}
      File file = fileInfo.getFile();
      file.delete();
    }
    listFileInfo.clear();
  }
  
  public
  void removeAllTemporaryFiles()
  {
    removeAllFiles("T");
  }
  
  public
  void removeAllFiles(String fileTypeToRemove)
  {
    List<FileInfo> listInfoToRemove = new ArrayList<FileInfo>();
    for(FileInfo fileInfo : listFileInfo) {
      String fileType = fileInfo.getType();
      if(fileType != null && fileType.equalsIgnoreCase(fileTypeToRemove)) {
        OutputStream fos = fileInfo.getFileOutputStream();
        try{ fos.close(); } catch(Exception ex) {}
        File file = fileInfo.getFile();
        file.delete();
        listInfoToRemove.add(fileInfo);
      }
    }
    for(FileInfo fileInfo : listInfoToRemove) {
      listFileInfo.remove(fileInfo);
    }
  }
  
  public
  void removeAllOutputFiles()
  {
    removeAllFiles("O");
  }
  
  public
  int indexOf(String sFileName)
  {
    for(int i = 0; i < listFileInfo.size(); i++) {
      FileInfo fileInfo = listFileInfo.get(i);
      String   fileName = fileInfo.getFileName();
      if(fileName != null && fileName.equals(sFileName)) {
        return i;
      }
    }
    return -1;
  }
  
  public
  File getFile(int i)
  {
    if(i >= listFileInfo.size() || i < 0) return null;
    FileInfo fileInfo = listFileInfo.get(i);
    return fileInfo.getFile();
  }
  
  public
  File getFile(String sFileName)
  {
    for(FileInfo fileInfo : listFileInfo) {
      String fileName = fileInfo.getFileName();
      if(fileName != null && fileName.equals(sFileName)) {
        return fileInfo.getFile();
      }
    }
    return null;
  }
  
  public
  String getTypeFile(int i)
  {
    if(i >= listFileInfo.size() || i < 0) return null;
    FileInfo fileInfo = listFileInfo.get(i);
    return fileInfo.getType();
  }
  
  public
  String getTypeFile(String sFileName)
  {
    for(FileInfo fileInfo : listFileInfo) {
      String fileName = fileInfo.getFileName();
      if(fileName != null && fileName.equals(sFileName)) {
        return fileInfo.getType();
      }
    }
    return null;
  }
  
  public
  String getFileName(int i)
  {
    if(i >= listFileInfo.size() || i < 0) return null;
    FileInfo fileInfo = listFileInfo.get(i);
    return fileInfo.getFileName();
  }
  
  public
  OutputStream getFileOutputStream(int i)
  {
    if(i >= listFileInfo.size() || i < 0) return null;
    FileInfo fileInfo = listFileInfo.get(i);
    return fileInfo.getFileOutputStream();
  }
  
  public
  OutputStream getFileOutputStream(String sFileName)
  {
    for(FileInfo fileInfo : listFileInfo) {
      String fileName = fileInfo.getFileName();
      if(fileName != null && fileName.equals(sFileName)) {
        return fileInfo.getFileOutputStream();
      }
    }
    return null;
  }
  
  public
  File closeFile(int i)
  {
    if(i >= listFileInfo.size() || i < 0) return null;
    FileInfo fileInfo = listFileInfo.get(i);
    OutputStream fout = fileInfo.getFileOutputStream();
    try{ fout.close(); } catch(Exception ex) {}
    return fileInfo.getFile();
  }
  
  public
  File closeLastFile()
  {
    if(listFileInfo.size() == 0) return null;
    FileInfo fileInfo = listFileInfo.get(listFileInfo.size() - 1);
    OutputStream fout = fileInfo.getFileOutputStream();
    try{ fout.close(); } catch(Exception ex) {}
    return fileInfo.getFile();
  }
  
  public
  File[] closeLastFiles(int iCount)
  {
    if(listFileInfo.size() == 0) return new File[0];
    
    List<File> listResult = new ArrayList<File>();
    int iFilesToClose = listFileInfo.size() < iCount ? listFileInfo.size() : iCount;
    for(int i = listFileInfo.size() - iFilesToClose; i < listFileInfo.size(); i++) {
      FileInfo fileInfo = listFileInfo.get(i);
      OutputStream fout = fileInfo.getFileOutputStream();
      try{ fout.close(); } catch(Exception ex) {}
      listResult.add(fileInfo.getFile());
    }
    
    File[] asFiles = new File[listResult.size()];
    for(int i = 0; i < listResult.size(); i++) {
      asFiles[i] = listResult.get(i);
    }
    return asFiles;
  }
  
  public
  List<File> closeAllFiles()
  {
    if(psLog != null) try{ psLog.close(); } catch(Exception ex) {}
    
    List<File> listResult = new ArrayList<File>(listFileInfo.size());
    for(FileInfo fileInfo : listFileInfo) {
      OutputStream fout = fileInfo.getFileOutputStream();
      try{ fout.close(); } catch(Exception ex) {}
      listResult.add(fileInfo.getFile());
    }
    return listResult;
  }
  
  public
  List<File> closeAllFiles(String fileTypeToClose)
  {
    if(psLog != null) try{ psLog.close(); } catch(Exception ex) {}
    
    List<File> listResult = new ArrayList<File>();
    for(FileInfo fileInfo : listFileInfo) {
      String fileType = fileInfo.getType();
      if(fileType != null && fileType.equalsIgnoreCase(fileTypeToClose)) {
        OutputStream fout = fileInfo.getFileOutputStream();
        try{ fout.close(); } catch(Exception ex) {}
        listResult.add(fileInfo.getFile());
      }
    }
    return listResult;
  }
  
  public
  int getLastIndex()
  {
    return listFileInfo.size() - 1;
  }
  
  public
  List<File> getListOfFile()
  {
    List<File> listResult = new ArrayList<File>(listFileInfo.size());
    for(FileInfo fileInfo : listFileInfo) {
      listResult.add(fileInfo.getFile());
    }
    return listResult;
  }
  
  public
  List<File> getListOfErrorOrReportFile()
  {
    List<File> listResult = new ArrayList<File>();
    for(FileInfo fileInfo : listFileInfo) {
      String fileType = fileInfo.getType();
      if(fileType == null) continue;
      if(fileType.equals("E") || fileType.equals("R")) {
        listResult.add(fileInfo.getFile());
      }
    }
    return listResult;
  }
  
  public
  void compressAllFiles()
      throws Exception
  {
    if(listFileInfo.size() == 0) return;
    
    byte[] abBuffer = new byte[1024];
    
    for(FileInfo fileInfo : listFileInfo) {
      String fileName = fileInfo.getFileName();
      
      // Si chiude l'outputstream eventualmente aperto del file...
      OutputStream fos = fileInfo.getFileOutputStream();
      try{ fos.close(); } catch(Exception ex) {}
      
      File file = fileInfo.getFile();
      String zipFilePath = file.getPath() + ".zip";
      
      // Si costruisce l'archivio zip...
      FileOutputStream zipFos = new FileOutputStream(zipFilePath);
      ZipOutputStream zos = new ZipOutputStream(zipFos);
      
      // Si aggiunge il file all'archivio...
      zos.putNextEntry(new ZipEntry(fileName));
      FileInputStream fis = new FileInputStream(file.getPath());
      int iLenght;
      while((iLenght = fis.read(abBuffer)) > 0) {
        zos.write(abBuffer, 0, iLenght);
      }
      fis.close();
      zos.closeEntry();
      zos.close();
      // Si elimina il file...
      file.delete();
      // Si sostituiscono le informazioni del file originario...
      fileInfo.setFileName(fileName + ".zip");
      fileInfo.setFile(new File(zipFilePath));
      fileInfo.setFileOutputStream(zipFos);
    }
  }
  
  public
  File compressFile(String sFileName, String sZipFileName)
      throws Exception
  {
    if(listFileInfo.size() == 0) return null;
    if(sFileName == null || sZipFileName == null) return null;
    if(sFileName != null && sFileName.equals(sZipFileName)) return null;
    FileInfo fileInfoFound = null;
    for(int i = 0; i < listFileInfo.size(); i++) {
      fileInfoFound = listFileInfo.get(i);
      String fileName = fileInfoFound.getFileName();
      if(fileName != null && fileName.equals(sFileName)) {
        break;
      }
    }
    if(fileInfoFound == null) return null;
    // Si chiude l'outputstream eventualmente aperto del file...
    OutputStream fos = fileInfoFound.getFileOutputStream();
    try{ fos.close(); } catch(Exception ex) {};
    
    // Si costruisce l'archivio zip...
    OutputStream os = createFile(fileInfoFound.getType(), sZipFileName);
    FileInfo fileInfo = listFileInfo.get(listFileInfo.size() - 1);
    ZipOutputStream zos = new ZipOutputStream(os);
    
    // Si aggiunge il file all'archivio...
    byte[] abBuffer = new byte[1024];
    zos.putNextEntry(new ZipEntry(sFileName));
    FileInputStream fis = new FileInputStream(fileInfoFound.getFile());
    int iLenght;
    while((iLenght = fis.read(abBuffer)) > 0) {
      zos.write(abBuffer, 0, iLenght);
    }
    fis.close();
    zos.closeEntry();
    zos.close();
    
    return fileInfo.getFile();
  }
  
  public static
  OutputStream createEmptyOutputStream()
  {
    return new EmptyOutputStream();
  }
  
  public
  OutputStream createInfoFile()
    throws Exception
  {
    return createFile("I", "info.txt");
  }
  
  public
  OutputStream createInfoFile(String sFileName)
    throws Exception
  {
    return createFile("I", sFileName);
  }
  
  public
  OutputStream createOutputFile()
    throws Exception
  {
    return createFile("O", "output.txt");
  }
  
  public
  OutputStream createOutputFile(String sFileName)
    throws Exception
  {
    return createFile("O", sFileName);
  }
  
  public
  OutputStream createErrorFile()
    throws Exception
  {
    return createFile("E", "errors.txt");
  }
  
  public
  OutputStream createErrorFile(String sFileName)
    throws Exception
  {
    return createFile("E", sFileName);
  }
  
  public
  OutputStream createReportFile()
    throws Exception
  {
    return createFile("R", "report.txt");
  }
  
  public
  OutputStream createReportFile(String sFileName)
    throws Exception
  {
    return createFile("R", sFileName);
  }
  
  public
  OutputStream createMessageFile()
    throws Exception
  {
    return createFile("M", "message.tmp");
  }
  
  public
  OutputStream createMessageFile(String sFileName)
    throws Exception
  {
    return createFile("M", sFileName);
  }
  
  public
  OutputStream createTemporaryFile()
    throws Exception
  {
    return createFile("T", System.currentTimeMillis() + ".tmp");
  }
  
  public
  OutputStream createTemporaryFile(String sFileName)
    throws Exception
  {
    return createFile("T", sFileName);
  }
  
  public
  OutputStream createFile(String sType, String sFileName)
    throws Exception
  {
    if(idLog == 0) {
      return new EmptyOutputStream();
    }
    
    // Controlla che non sia stato gia' creato...
    OutputStream fos = getFileOutputStream(sFileName);
    if(fos != null) return fos;
    
    String folderPath = BEConfig.getLJSAOutputFolder() + File.separator + idLog;
    File folder = new File(folderPath);
    if(!folder.exists()) folder.mkdirs();
    String filePath = folderPath + File.separator + sFileName;
    
    fos = new FileOutputStream(filePath, false);
    
    listFileInfo.add(new FileInfo(sFileName, sType, filePath, fos));
    
    return fos;
  }
  
  public
  PrintStream getLogPrintStream(String sFileName)
    throws Exception
  {
    if(psLog != null) return psLog;
    
    String logFolderPath = BEConfig.getLJSALogFolder();
    File folder = new File(logFolderPath);
    if(!folder.exists()) folder.mkdirs();
    String filePath = logFolderPath + File.separator + sFileName;
    FileOutputStream fos = new FileOutputStream(filePath, true);
    psLog = new PrintStream(fos, true);
    return psLog;
  }
  
  public static
  class EmptyOutputStream extends OutputStream
  {
    @Override
    public void write(int b) throws IOException {
    }
  }
}
