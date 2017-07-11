package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl
  implements NodeList
{
  protected NodeImpl rootNode;
  protected String tagName;
  protected int changes = 0;
  protected Vector nodes;
  protected String nsName;
  protected boolean enableNS = false;
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString)
  {
    this.rootNode = paramNodeImpl;
    this.tagName = paramString;
    this.nodes = new Vector();
  }
  
  public DeepNodeListImpl(NodeImpl paramNodeImpl, String paramString1, String paramString2)
  {
    this(paramNodeImpl, paramString2);
    this.nsName = ((paramString1 != null) && (!paramString1.equals("")) ? paramString1 : null);
    this.enableNS = true;
  }
  
  public int getLength()
  {
    item(Integer.MAX_VALUE);
    return this.nodes.size();
  }
  
  public Node item(int paramInt)
  {
    if (this.rootNode.changes() != this.changes)
    {
      this.nodes = new Vector();
      this.changes = this.rootNode.changes();
    }
    if (paramInt < this.nodes.size()) {
      return (Node)this.nodes.elementAt(paramInt);
    }
    Object localObject;
    if (this.nodes.size() == 0) {
      localObject = this.rootNode;
    } else {
      localObject = (NodeImpl)this.nodes.lastElement();
    }
    while ((localObject != null) && (paramInt >= this.nodes.size()))
    {
      localObject = nextMatchingElementAfter((Node)localObject);
      if (localObject != null) {
        this.nodes.addElement(localObject);
      }
    }
    return localObject;
  }
  
  protected Node nextMatchingElementAfter(Node paramNode)
  {
    while (paramNode != null)
    {
      if (paramNode.hasChildNodes())
      {
        paramNode = paramNode.getFirstChild();
      }
      else
      {
        Node localNode;
        if ((paramNode != this.rootNode) && (null != (localNode = paramNode.getNextSibling())))
        {
          paramNode = localNode;
        }
        else
        {
          localNode = null;
          while (paramNode != this.rootNode)
          {
            localNode = paramNode.getNextSibling();
            if (localNode != null) {
              break;
            }
            paramNode = paramNode.getParentNode();
          }
          paramNode = localNode;
        }
      }
      if ((paramNode != this.rootNode) && (paramNode != null) && (paramNode.getNodeType() == 1)) {
        if (!this.enableNS)
        {
          if ((this.tagName.equals("*")) || (((ElementImpl)paramNode).getTagName().equals(this.tagName))) {
            return paramNode;
          }
        }
        else
        {
          ElementImpl localElementImpl;
          if (this.tagName.equals("*"))
          {
            if ((this.nsName != null) && (this.nsName.equals("*"))) {
              return paramNode;
            }
            localElementImpl = (ElementImpl)paramNode;
            if (((this.nsName == null) && (localElementImpl.getNamespaceURI() == null)) || ((this.nsName != null) && (this.nsName.equals(localElementImpl.getNamespaceURI())))) {
              return paramNode;
            }
          }
          else
          {
            localElementImpl = (ElementImpl)paramNode;
            if ((localElementImpl.getLocalName() != null) && (localElementImpl.getLocalName().equals(this.tagName)))
            {
              if ((this.nsName != null) && (this.nsName.equals("*"))) {
                return paramNode;
              }
              if (((this.nsName == null) && (localElementImpl.getNamespaceURI() == null)) || ((this.nsName != null) && (this.nsName.equals(localElementImpl.getNamespaceURI())))) {
                return paramNode;
              }
            }
          }
        }
      }
    }
    return null;
  }
}
