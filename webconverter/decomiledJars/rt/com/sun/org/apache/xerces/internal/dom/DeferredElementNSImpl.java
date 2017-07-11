package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.NamedNodeMap;

public class DeferredElementNSImpl
  extends ElementNSImpl
  implements DeferredNode
{
  static final long serialVersionUID = -5001885145370927385L;
  protected transient int fNodeIndex;
  
  DeferredElementNSImpl(DeferredDocumentImpl paramDeferredDocumentImpl, int paramInt)
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
    int i = this.name.indexOf(':');
    if (i < 0) {
      this.localName = this.name;
    } else {
      this.localName = this.name.substring(i + 1);
    }
    this.namespaceURI = localDeferredDocumentImpl.getNodeURI(this.fNodeIndex);
    this.type = ((XSTypeDefinition)localDeferredDocumentImpl.getTypeInfo(this.fNodeIndex));
    setupDefaultAttributes();
    int j = localDeferredDocumentImpl.getNodeExtra(this.fNodeIndex);
    if (j != -1)
    {
      NamedNodeMap localNamedNodeMap = getAttributes();
      int k = 0;
      do
      {
        AttrImpl localAttrImpl = (AttrImpl)localDeferredDocumentImpl.getNodeObject(j);
        if ((!localAttrImpl.getSpecified()) && ((k != 0) || ((localAttrImpl.getNamespaceURI() != null) && (localAttrImpl.getNamespaceURI() != NamespaceContext.XMLNS_URI) && (localAttrImpl.getName().indexOf(':') < 0))))
        {
          k = 1;
          localNamedNodeMap.setNamedItemNS(localAttrImpl);
        }
        else
        {
          localNamedNodeMap.setNamedItem(localAttrImpl);
        }
        j = localDeferredDocumentImpl.getPrevSibling(j);
      } while (j != -1);
    }
    localDeferredDocumentImpl.mutationEvents = bool;
  }
  
  protected final void synchronizeChildren()
  {
    DeferredDocumentImpl localDeferredDocumentImpl = (DeferredDocumentImpl)ownerDocument();
    localDeferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
  }
}
