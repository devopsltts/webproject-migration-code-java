package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.NamedNodeMap;

public class DeferredElementImpl
  extends ElementImpl
  implements DeferredNode
{
  static final long serialVersionUID = -7670981133940934842L;
  protected transient int fNodeIndex;
  
  DeferredElementImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
  {
    super(paramDeferredDocumentImpl, null);
    this.fNodeIndex = paramInt;
    needsSyncChildren(true);
  }
  
  public final int getNodeIndex()
  {
    return this.fNodeIndex;
  }
  
  protected final void synchronizeData()
  {
    needsSyncData(false);
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
    boolean bool = localDeferredDocumentImpl.mutationEvents;
    localDeferredDocumentImpl.mutationEvents = false;
    this.name = localDeferredDocumentImpl.getNodeName(this.fNodeIndex);
    setupDefaultAttributes();
    int i = localDeferredDocumentImpl.getNodeExtra(this.fNodeIndex);
    if (i != -1)
    {
      NamedNodeMap localNamedNodeMap = getAttributes();
      do
      {
        NodeImpl localNodeImpl = (NodeImpl)localDeferredDocumentImpl.getNodeObject(i);
        localNamedNodeMap.setNamedItem(localNodeImpl);
        i = localDeferredDocumentImpl.getPrevSibling(i);
      } while (i != -1);
    }
    localDeferredDocumentImpl.mutationEvents = bool;
  }
  
  protected final void synchronizeChildren()
  {
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    localDeferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
  }
}
