package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import org.w3c.dom.Node;

public class DTMChildIterNodeList
  extends DTMNodeListBase
{
  private int m_firstChild;
  private DTM m_parentDTM;
  
  private DTMChildIterNodeList() {}
  
  public DTMChildIterNodeList(DTM paramDTM, int paramInt)
  {
    this.m_parentDTM = paramDTM;
    this.m_firstChild = paramDTM.getFirstChild(paramInt);
  }
  
  public Node item(int paramInt)
  {
    for (int i = this.m_firstChild;; i = this.m_parentDTM.getNextSibling(i))
    {
      paramInt--;
      if ((paramInt < 0) || (i == -1)) {
        break;
      }
    }
    if (i == -1) {
      return null;
    }
    return this.m_parentDTM.getNode(i);
  }
  
  public int getLength()
  {
    int i = 0;
    for (int j = this.m_firstChild; j != -1; j = this.m_parentDTM.getNextSibling(j)) {
      i++;
    }
    return i;
  }
}
