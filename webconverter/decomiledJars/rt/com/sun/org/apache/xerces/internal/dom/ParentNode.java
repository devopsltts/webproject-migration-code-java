package com.sun.org.apache.xerces.internal.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public abstract class ParentNode
  extends ChildNode
{
  static final long serialVersionUID = 2815829867152120872L;
  protected CoreDocumentImpl ownerDocument;
  protected ChildNode firstChild = null;
  protected transient NodeListCache fNodeListCache = null;
  
  protected ParentNode(CoreDocumentImpl paramCoreDocumentImpl)
  {
    super(paramCoreDocumentImpl);
    this.ownerDocument = paramCoreDocumentImpl;
  }
  
  public ParentNode() {}
  
  public Node cloneNode(boolean paramBoolean)
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    ParentNode localParentNode = (ParentNode)super.cloneNode(paramBoolean);
    localParentNode.ownerDocument = this.ownerDocument;
    localParentNode.firstChild = null;
    localParentNode.fNodeListCache = null;
    if (paramBoolean) {
      for (ChildNode localChildNode = this.firstChild; localChildNode != null; localChildNode = localChildNode.nextSibling) {
        localParentNode.appendChild(localChildNode.cloneNode(true));
      }
    }
    return localParentNode;
  }
  
  public Document getOwnerDocument()
  {
    return this.ownerDocument;
  }
  
  CoreDocumentImpl ownerDocument()
  {
    return this.ownerDocument;
  }
  
  void setOwnerDocument(CoreDocumentImpl paramCoreDocumentImpl)
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    for (ChildNode localChildNode = this.firstChild; localChildNode != null; localChildNode = localChildNode.nextSibling) {
      localChildNode.setOwnerDocument(paramCoreDocumentImpl);
    }
    super.setOwnerDocument(paramCoreDocumentImpl);
    this.ownerDocument = paramCoreDocumentImpl;
  }
  
  public boolean hasChildNodes()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return this.firstChild != null;
  }
  
  public NodeList getChildNodes()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return this;
  }
  
  public Node getFirstChild()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return this.firstChild;
  }
  
  public Node getLastChild()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    return lastChild();
  }
  
  final ChildNode lastChild()
  {
    return this.firstChild != null ? this.firstChild.previousSibling : null;
  }
  
  final void lastChild(ChildNode paramChildNode)
  {
    if (this.firstChild != null) {
      this.firstChild.previousSibling = paramChildNode;
    }
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    return internalInsertBefore(paramNode1, paramNode2, false);
  }
  
  Node internalInsertBefore(Node paramNode1, Node paramNode2, boolean paramBoolean)
    throws DOMException
  {
    boolean bool = this.ownerDocument.errorChecking;
    if (paramNode1.getNodeType() == 11)
    {
      if (bool) {
        for (Node localNode = paramNode1.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
          if (!this.ownerDocument.isKidOK(this, localNode)) {
            throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
          }
        }
      }
      while (paramNode1.hasChildNodes()) {
        insertBefore(paramNode1.getFirstChild(), paramNode2);
      }
      return paramNode1;
    }
    if (paramNode1 == paramNode2)
    {
      paramNode2 = paramNode2.getNextSibling();
      removeChild(paramNode1);
      insertBefore(paramNode1, paramNode2);
      return paramNode1;
    }
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    if (bool)
    {
      if (isReadOnly()) {
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
      }
      if ((paramNode1.getOwnerDocument() != this.ownerDocument) && (paramNode1 != this.ownerDocument)) {
        throw new DOMException((short)4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
      }
      if (!this.ownerDocument.isKidOK(this, paramNode1)) {
        throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
      }
      if ((paramNode2 != null) && (paramNode2.getParentNode() != this)) {
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
      }
      if (this.ownerDocument.ancestorChecking)
      {
        int i = 1;
        for (localObject = this; (i != 0) && (localObject != null); localObject = ((NodeImpl)localObject).parentNode()) {
          i = paramNode1 != localObject ? 1 : 0;
        }
        if (i == 0) {
          throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
        }
      }
    }
    this.ownerDocument.insertingNode(this, paramBoolean);
    ChildNode localChildNode1 = (ChildNode)paramNode1;
    Object localObject = localChildNode1.parentNode();
    if (localObject != null) {
      ((Node)localObject).removeChild(localChildNode1);
    }
    ChildNode localChildNode2 = (ChildNode)paramNode2;
    localChildNode1.ownerNode = this;
    localChildNode1.isOwned(true);
    if (this.firstChild == null)
    {
      this.firstChild = localChildNode1;
      localChildNode1.isFirstChild(true);
      localChildNode1.previousSibling = localChildNode1;
    }
    else
    {
      ChildNode localChildNode3;
      if (localChildNode2 == null)
      {
        localChildNode3 = this.firstChild.previousSibling;
        localChildNode3.nextSibling = localChildNode1;
        localChildNode1.previousSibling = localChildNode3;
        this.firstChild.previousSibling = localChildNode1;
      }
      else if (paramNode2 == this.firstChild)
      {
        this.firstChild.isFirstChild(false);
        localChildNode1.nextSibling = this.firstChild;
        localChildNode1.previousSibling = this.firstChild.previousSibling;
        this.firstChild.previousSibling = localChildNode1;
        this.firstChild = localChildNode1;
        localChildNode1.isFirstChild(true);
      }
      else
      {
        localChildNode3 = localChildNode2.previousSibling;
        localChildNode1.nextSibling = localChildNode2;
        localChildNode3.nextSibling = localChildNode1;
        localChildNode2.previousSibling = localChildNode1;
        localChildNode1.previousSibling = localChildNode3;
      }
    }
    changed();
    if (this.fNodeListCache != null)
    {
      if (this.fNodeListCache.fLength != -1) {
        this.fNodeListCache.fLength += 1;
      }
      if (this.fNodeListCache.fChildIndex != -1) {
        if (this.fNodeListCache.fChild == localChildNode2) {
          this.fNodeListCache.fChild = localChildNode1;
        } else {
          this.fNodeListCache.fChildIndex = -1;
        }
      }
    }
    this.ownerDocument.insertedNode(this, localChildNode1, paramBoolean);
    checkNormalizationAfterInsert(localChildNode1);
    return paramNode1;
  }
  
  public Node removeChild(Node paramNode)
    throws DOMException
  {
    return internalRemoveChild(paramNode, false);
  }
  
  Node internalRemoveChild(Node paramNode, boolean paramBoolean)
    throws DOMException
  {
    CoreDocumentImpl localCoreDocumentImpl = ownerDocument();
    if (localCoreDocumentImpl.errorChecking)
    {
      if (isReadOnly()) {
        throw new DOMException((short)7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
      }
      if ((paramNode != null) && (paramNode.getParentNode() != this)) {
        throw new DOMException((short)8, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null));
      }
    }
    ChildNode localChildNode1 = (ChildNode)paramNode;
    localCoreDocumentImpl.removingNode(this, localChildNode1, paramBoolean);
    if (this.fNodeListCache != null)
    {
      if (this.fNodeListCache.fLength != -1) {
        this.fNodeListCache.fLength -= 1;
      }
      if (this.fNodeListCache.fChildIndex != -1) {
        if (this.fNodeListCache.fChild == localChildNode1)
        {
          this.fNodeListCache.fChildIndex -= 1;
          this.fNodeListCache.fChild = localChildNode1.previousSibling();
        }
        else
        {
          this.fNodeListCache.fChildIndex = -1;
        }
      }
    }
    if (localChildNode1 == this.firstChild)
    {
      localChildNode1.isFirstChild(false);
      this.firstChild = localChildNode1.nextSibling;
      if (this.firstChild != null)
      {
        this.firstChild.isFirstChild(true);
        this.firstChild.previousSibling = localChildNode1.previousSibling;
      }
    }
    else
    {
      localChildNode2 = localChildNode1.previousSibling;
      ChildNode localChildNode3 = localChildNode1.nextSibling;
      localChildNode2.nextSibling = localChildNode3;
      if (localChildNode3 == null) {
        this.firstChild.previousSibling = localChildNode2;
      } else {
        localChildNode3.previousSibling = localChildNode2;
      }
    }
    ChildNode localChildNode2 = localChildNode1.previousSibling();
    localChildNode1.ownerNode = localCoreDocumentImpl;
    localChildNode1.isOwned(false);
    localChildNode1.nextSibling = null;
    localChildNode1.previousSibling = null;
    changed();
    localCoreDocumentImpl.removedNode(this, paramBoolean);
    checkNormalizationAfterRemove(localChildNode2);
    return localChildNode1;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2)
    throws DOMException
  {
    this.ownerDocument.replacingNode(this);
    internalInsertBefore(paramNode1, paramNode2, true);
    if (paramNode1 != paramNode2) {
      internalRemoveChild(paramNode2, true);
    }
    this.ownerDocument.replacedNode(this);
    return paramNode2;
  }
  
  public String getTextContent()
    throws DOMException
  {
    Node localNode1 = getFirstChild();
    if (localNode1 != null)
    {
      Node localNode2 = localNode1.getNextSibling();
      if (localNode2 == null) {
        return hasTextContent(localNode1) ? ((NodeImpl)localNode1).getTextContent() : "";
      }
      if (this.fBufferStr == null) {
        this.fBufferStr = new StringBuffer();
      } else {
        this.fBufferStr.setLength(0);
      }
      getTextContent(this.fBufferStr);
      return this.fBufferStr.toString();
    }
    return "";
  }
  
  void getTextContent(StringBuffer paramStringBuffer)
    throws DOMException
  {
    for (Node localNode = getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      if (hasTextContent(localNode)) {
        ((NodeImpl)localNode).getTextContent(paramStringBuffer);
      }
    }
  }
  
  final boolean hasTextContent(Node paramNode)
  {
    return (paramNode.getNodeType() != 8) && (paramNode.getNodeType() != 7) && ((paramNode.getNodeType() != 3) || (!((TextImpl)paramNode).isIgnorableWhitespace()));
  }
  
  public void setTextContent(String paramString)
    throws DOMException
  {
    Node localNode;
    while ((localNode = getFirstChild()) != null) {
      removeChild(localNode);
    }
    if ((paramString != null) && (paramString.length() != 0)) {
      appendChild(ownerDocument().createTextNode(paramString));
    }
  }
  
  private int nodeListGetLength()
  {
    if (this.fNodeListCache == null)
    {
      if (this.firstChild == null) {
        return 0;
      }
      if (this.firstChild == lastChild()) {
        return 1;
      }
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    }
    if (this.fNodeListCache.fLength == -1)
    {
      int i;
      ChildNode localChildNode;
      if ((this.fNodeListCache.fChildIndex != -1) && (this.fNodeListCache.fChild != null))
      {
        i = this.fNodeListCache.fChildIndex;
        localChildNode = this.fNodeListCache.fChild;
      }
      else
      {
        localChildNode = this.firstChild;
        i = 0;
      }
      while (localChildNode != null)
      {
        i++;
        localChildNode = localChildNode.nextSibling;
      }
      this.fNodeListCache.fLength = i;
    }
    return this.fNodeListCache.fLength;
  }
  
  public int getLength()
  {
    return nodeListGetLength();
  }
  
  private Node nodeListItem(int paramInt)
  {
    if (this.fNodeListCache == null)
    {
      if (this.firstChild == lastChild()) {
        return paramInt == 0 ? this.firstChild : null;
      }
      this.fNodeListCache = this.ownerDocument.getNodeListCache(this);
    }
    int i = this.fNodeListCache.fChildIndex;
    ChildNode localChildNode = this.fNodeListCache.fChild;
    int j = 1;
    if ((i != -1) && (localChildNode != null))
    {
      j = 0;
      if (i < paramInt) {
        while ((i < paramInt) && (localChildNode != null))
        {
          i++;
          localChildNode = localChildNode.nextSibling;
        }
      }
      if (i <= paramInt) {}
    }
    else
    {
      while ((i > paramInt) && (localChildNode != null))
      {
        i--;
        localChildNode = localChildNode.previousSibling();
        continue;
        if (paramInt < 0) {
          return null;
        }
        localChildNode = this.firstChild;
        for (i = 0; (i < paramInt) && (localChildNode != null); i++) {
          localChildNode = localChildNode.nextSibling;
        }
      }
    }
    if ((j == 0) && ((localChildNode == this.firstChild) || (localChildNode == lastChild())))
    {
      this.fNodeListCache.fChildIndex = -1;
      this.fNodeListCache.fChild = null;
      this.ownerDocument.freeNodeListCache(this.fNodeListCache);
    }
    else
    {
      this.fNodeListCache.fChildIndex = i;
      this.fNodeListCache.fChild = localChildNode;
    }
    return localChildNode;
  }
  
  public Node item(int paramInt)
  {
    return nodeListItem(paramInt);
  }
  
  protected final NodeList getChildNodesUnoptimized()
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    new NodeList()
    {
      public int getLength()
      {
        return ParentNode.this.nodeListGetLength();
      }
      
      public Node item(int paramAnonymousInt)
      {
        return ParentNode.this.nodeListItem(paramAnonymousInt);
      }
    };
  }
  
  public void normalize()
  {
    if (isNormalized()) {
      return;
    }
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    for (ChildNode localChildNode = this.firstChild; localChildNode != null; localChildNode = localChildNode.nextSibling) {
      localChildNode.normalize();
    }
    isNormalized(true);
  }
  
  public boolean isEqualNode(Node paramNode)
  {
    if (!super.isEqualNode(paramNode)) {
      return false;
    }
    Node localNode1 = getFirstChild();
    for (Node localNode2 = paramNode.getFirstChild(); (localNode1 != null) && (localNode2 != null); localNode2 = localNode2.getNextSibling())
    {
      if (!((NodeImpl)localNode1).isEqualNode(localNode2)) {
        return false;
      }
      localNode1 = localNode1.getNextSibling();
    }
    return localNode1 == localNode2;
  }
  
  public void setReadOnly(boolean paramBoolean1, boolean paramBoolean2)
  {
    super.setReadOnly(paramBoolean1, paramBoolean2);
    if (paramBoolean2)
    {
      if (needsSyncChildren()) {
        synchronizeChildren();
      }
      for (ChildNode localChildNode = this.firstChild; localChildNode != null; localChildNode = localChildNode.nextSibling) {
        if (localChildNode.getNodeType() != 5) {
          localChildNode.setReadOnly(paramBoolean1, true);
        }
      }
    }
  }
  
  protected void synchronizeChildren()
  {
    needsSyncChildren(false);
  }
  
  void checkNormalizationAfterInsert(ChildNode paramChildNode)
  {
    if (paramChildNode.getNodeType() == 3)
    {
      ChildNode localChildNode1 = paramChildNode.previousSibling();
      ChildNode localChildNode2 = paramChildNode.nextSibling;
      if (((localChildNode1 != null) && (localChildNode1.getNodeType() == 3)) || ((localChildNode2 != null) && (localChildNode2.getNodeType() == 3))) {
        isNormalized(false);
      }
    }
    else if (!paramChildNode.isNormalized())
    {
      isNormalized(false);
    }
  }
  
  void checkNormalizationAfterRemove(ChildNode paramChildNode)
  {
    if ((paramChildNode != null) && (paramChildNode.getNodeType() == 3))
    {
      ChildNode localChildNode = paramChildNode.nextSibling;
      if ((localChildNode != null) && (localChildNode.getNodeType() == 3)) {
        isNormalized(false);
      }
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (needsSyncChildren()) {
      synchronizeChildren();
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    needsSyncChildren(false);
  }
  
  protected class UserDataRecord
    implements Serializable
  {
    private static final long serialVersionUID = 3258126977134310455L;
    Object fData;
    UserDataHandler fHandler;
    
    UserDataRecord(Object paramObject, UserDataHandler paramUserDataHandler)
    {
      this.fData = paramObject;
      this.fHandler = paramUserDataHandler;
    }
  }
}
