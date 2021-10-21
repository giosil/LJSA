package org.dew.ljsa.gui;

import java.util.List;

import org.dew.swingup.components.IDecodeListener;

public
class LJSADecodeListener implements IDecodeListener
{
  String idServizio;
  
  public
  LJSADecodeListener()
  {
  }
  
  public
  LJSADecodeListener(String idServizio)
  {
    this.idServizio = idServizio;
  }
  
  public
  void setIdServizio(String idServizio)
  {
    this.idServizio = idServizio;
  }
  
  public
  String getIdServizio()
  {
    return idServizio;
  }
  
  public
  void reset()
  {
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public
  void beforeFind(List list)
  {
    if(idServizio != null && idServizio.length() > 0) {
      list.add(idServizio);
    }
  }
  
  public
  void set()
  {
  }
}
