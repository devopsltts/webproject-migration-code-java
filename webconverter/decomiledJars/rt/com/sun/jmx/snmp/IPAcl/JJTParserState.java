package com.sun.jmx.snmp.IPAcl;

import java.util.Stack;

class JJTParserState
{
  private Stack<Node> nodes = new Stack();
  private Stack<Integer> marks = new Stack();
  private int sp = 0;
  private int mk = 0;
  private boolean node_created;
  
  JJTParserState() {}
  
  boolean nodeCreated()
  {
    return this.node_created;
  }
  
  void reset()
  {
    this.nodes.removeAllElements();
    this.marks.removeAllElements();
    this.sp = 0;
    this.mk = 0;
  }
  
  Node rootNode()
  {
    return (Node)this.nodes.elementAt(0);
  }
  
  void pushNode(Node paramNode)
  {
    this.nodes.push(paramNode);
    this.sp += 1;
  }
  
  Node popNode()
  {
    if (--this.sp < this.mk) {
      this.mk = ((Integer)this.marks.pop()).intValue();
    }
    return (Node)this.nodes.pop();
  }
  
  Node peekNode()
  {
    return (Node)this.nodes.peek();
  }
  
  int nodeArity()
  {
    return this.sp - this.mk;
  }
  
  void clearNodeScope(Node paramNode)
  {
    while (this.sp > this.mk) {
      popNode();
    }
    this.mk = ((Integer)this.marks.pop()).intValue();
  }
  
  void openNodeScope(Node paramNode)
  {
    this.marks.push(new Integer(this.mk));
    this.mk = this.sp;
    paramNode.jjtOpen();
  }
  
  void closeNodeScope(Node paramNode, int paramInt)
  {
    this.mk = ((Integer)this.marks.pop()).intValue();
    while (paramInt-- > 0)
    {
      Node localNode = popNode();
      localNode.jjtSetParent(paramNode);
      paramNode.jjtAddChild(localNode, paramInt);
    }
    paramNode.jjtClose();
    pushNode(paramNode);
    this.node_created = true;
  }
  
  void closeNodeScope(Node paramNode, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i = nodeArity();
      this.mk = ((Integer)this.marks.pop()).intValue();
      while (i-- > 0)
      {
        Node localNode = popNode();
        localNode.jjtSetParent(paramNode);
        paramNode.jjtAddChild(localNode, i);
      }
      paramNode.jjtClose();
      pushNode(paramNode);
      this.node_created = true;
    }
    else
    {
      this.mk = ((Integer)this.marks.pop()).intValue();
      this.node_created = false;
    }
  }
}
