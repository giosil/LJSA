package org.dew.ljsa.backend.util;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

import org.util.WUtil;

public 
class FMEntry implements Serializable, Comparable<FMEntry>
{
  private static final long serialVersionUID = -7431720625068199789L;
  
  private String path;
  private String name;
  private String type;
  private String ext;
  private long length;
  private Date lastModified;
  private Date sysDateTime;
  private int countSubdir;
  private int countFiles;

  public FMEntry() {
  }

  public FMEntry(Map<String, Object> map) {
    if (map == null) return;
    path         = WUtil.toString(map.get("p"), null);
    name         = WUtil.toString(map.get("n"), null);
    type         = WUtil.toString(map.get("t"), null);
    lastModified = WUtil.toDate(map.get("d"),   null);
    length       = WUtil.toLong(map.get("l"),   0l);
    countSubdir  = WUtil.toInt(map.get("cd"),   0);
    countFiles   = WUtil.toInt(map.get("cf"),   0);
    sysDateTime  = WUtil.toDate(map.get("dt"),  null);
    ext          = extractExtension();
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
    this.ext  = extractExtension();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.ext  = extractExtension();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getLength() {
    return length;
  }

  public void setLength(long length) {
    this.length = length;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public boolean isDirectory() {
    return type != null && type.equalsIgnoreCase("d");
  }

  public boolean isFile() {
    return type == null || type.equalsIgnoreCase("f");
  }

  public Date getSysDateTime() {
    return sysDateTime;
  }

  public void setSysDateTime(Date sysDateTime) {
    this.sysDateTime = sysDateTime;
  }

  public int getCountSubdir() {
    return countSubdir;
  }

  public void setCountSubdir(int countSubdir) {
    this.countSubdir = countSubdir;
  }

  public int getCountFiles() {
    return countFiles;
  }

  public void setCountFiles(int countFiles) {
    this.countFiles = countFiles;
  }

  public String getExtension() {
    return ext;
  }

  public void setExtension(String ext) {
    if (ext != null && ext.length() > 0) {
      this.ext = ext.toLowerCase();
    } 
    else {
      this.ext = extractExtension();
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof FMEntry) {
      FMEntry fmEntry = (FMEntry) object;
      String objPath = fmEntry.getPath();
      if (objPath == null && path == null) return true;
      return objPath != null && objPath.equals(path);
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (path == null) return 0;
    return path.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(FMEntry object) {
    if(object == null) return 1;
    String objPath = object.getPath();
    return path.compareTo(objPath);
  }

  protected String extractExtension() {
    String fileName = null;
    if (name != null) {
      fileName = name;
    } 
    else if (path != null) {
      fileName = path;
    } 
    else {
      return "";
    }
    String result = "";
    int dot = fileName.lastIndexOf('.');
    if (dot >= 0 && dot < fileName.length() - 1) {
      result = fileName.substring(dot + 1).toLowerCase();
    }
    return result;
  }
}