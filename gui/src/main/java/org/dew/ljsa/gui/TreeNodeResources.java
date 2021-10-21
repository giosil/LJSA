package org.dew.ljsa.gui;

import javax.swing.tree.DefaultMutableTreeNode;

public
class TreeNodeResources extends DefaultMutableTreeNode
{
  private static final long serialVersionUID = -5974064884798858679L;
  
  protected String idNode;
  
  public
  TreeNodeResources(Object userObject, String idNode)
  {
    super(userObject);
    
    this.idNode = idNode;
  }
  
  public
  String getIdNode()
  {
    return idNode;
  }
}
