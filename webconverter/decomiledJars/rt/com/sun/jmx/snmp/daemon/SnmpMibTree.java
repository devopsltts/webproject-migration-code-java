package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibTree
{
  private SnmpMibAgent defaultAgent = null;
  private TreeNode root = new TreeNode(-1L, null, null, null);
  
  public SnmpMibTree() {}
  
  public void setDefaultAgent(SnmpMibAgent paramSnmpMibAgent)
  {
    this.defaultAgent = paramSnmpMibAgent;
    this.root.agent = paramSnmpMibAgent;
  }
  
  public SnmpMibAgent getDefaultAgent()
  {
    return this.defaultAgent;
  }
  
  public void register(SnmpMibAgent paramSnmpMibAgent)
  {
    this.root.registerNode(paramSnmpMibAgent);
  }
  
  public void register(SnmpMibAgent paramSnmpMibAgent, long[] paramArrayOfLong)
  {
    this.root.registerNode(paramArrayOfLong, 0, paramSnmpMibAgent);
  }
  
  public SnmpMibAgent getAgentMib(SnmpOid paramSnmpOid)
  {
    TreeNode localTreeNode = this.root.retrieveMatchingBranch(paramSnmpOid.longValue(), 0);
    if (localTreeNode == null) {
      return this.defaultAgent;
    }
    if (localTreeNode.getAgentMib() == null) {
      return this.defaultAgent;
    }
    return localTreeNode.getAgentMib();
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid)
  {
    for (int i = 0; i < paramArrayOfSnmpOid.length; i++)
    {
      long[] arrayOfLong = paramArrayOfSnmpOid[i].longValue();
      TreeNode localTreeNode = this.root.retrieveMatchingBranch(arrayOfLong, 0);
      if (localTreeNode != null) {
        localTreeNode.removeAgent(paramSnmpMibAgent);
      }
    }
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent)
  {
    this.root.removeAgentFully(paramSnmpMibAgent);
  }
  
  public void printTree()
  {
    this.root.printTree(">");
  }
  
  final class TreeNode
  {
    private Vector<TreeNode> children = new Vector();
    private Vector<SnmpMibAgent> agents = new Vector();
    private long nodeValue;
    private SnmpMibAgent agent;
    private TreeNode parent;
    
    void registerNode(SnmpMibAgent paramSnmpMibAgent)
    {
      long[] arrayOfLong = paramSnmpMibAgent.getRootOid();
      registerNode(arrayOfLong, 0, paramSnmpMibAgent);
    }
    
    TreeNode retrieveMatchingBranch(long[] paramArrayOfLong, int paramInt)
    {
      TreeNode localTreeNode1 = retrieveChild(paramArrayOfLong, paramInt);
      if (localTreeNode1 == null) {
        return this;
      }
      if (this.children.isEmpty()) {
        return localTreeNode1;
      }
      if (paramInt + 1 == paramArrayOfLong.length) {
        return localTreeNode1;
      }
      TreeNode localTreeNode2 = localTreeNode1.retrieveMatchingBranch(paramArrayOfLong, paramInt + 1);
      return localTreeNode2.agent == null ? this : localTreeNode2;
    }
    
    SnmpMibAgent getAgentMib()
    {
      return this.agent;
    }
    
    public void printTree(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (this.agents == null) {
        return;
      }
      Enumeration localEnumeration = this.agents.elements();
      Object localObject;
      while (localEnumeration.hasMoreElements())
      {
        localObject = (SnmpMibAgent)localEnumeration.nextElement();
        if (localObject == null) {
          localStringBuilder.append("empty ");
        } else {
          localStringBuilder.append(((SnmpMibAgent)localObject).getMibName()).append(" ");
        }
      }
      paramString = paramString + " ";
      if (this.children == null) {
        return;
      }
      localEnumeration = this.children.elements();
      while (localEnumeration.hasMoreElements())
      {
        localObject = (TreeNode)localEnumeration.nextElement();
        ((TreeNode)localObject).printTree(paramString);
      }
    }
    
    private TreeNode(long paramLong, SnmpMibAgent paramSnmpMibAgent, TreeNode paramTreeNode)
    {
      this.nodeValue = paramLong;
      this.parent = paramTreeNode;
      this.agents.addElement(paramSnmpMibAgent);
    }
    
    private void removeAgentFully(SnmpMibAgent paramSnmpMibAgent)
    {
      Vector localVector = new Vector();
      Enumeration localEnumeration = this.children.elements();
      while (localEnumeration.hasMoreElements())
      {
        TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
        localTreeNode.removeAgentFully(paramSnmpMibAgent);
        if (localTreeNode.agents.isEmpty()) {
          localVector.add(localTreeNode);
        }
      }
      localEnumeration = localVector.elements();
      while (localEnumeration.hasMoreElements()) {
        this.children.removeElement(localEnumeration.nextElement());
      }
      removeAgent(paramSnmpMibAgent);
    }
    
    private void removeAgent(SnmpMibAgent paramSnmpMibAgent)
    {
      if (!this.agents.contains(paramSnmpMibAgent)) {
        return;
      }
      this.agents.removeElement(paramSnmpMibAgent);
      if (!this.agents.isEmpty()) {
        this.agent = ((SnmpMibAgent)this.agents.firstElement());
      }
    }
    
    private void setAgent(SnmpMibAgent paramSnmpMibAgent)
    {
      this.agent = paramSnmpMibAgent;
    }
    
    private void registerNode(long[] paramArrayOfLong, int paramInt, SnmpMibAgent paramSnmpMibAgent)
    {
      if (paramInt >= paramArrayOfLong.length) {
        return;
      }
      TreeNode localTreeNode = retrieveChild(paramArrayOfLong, paramInt);
      if (localTreeNode == null)
      {
        long l = paramArrayOfLong[paramInt];
        localTreeNode = new TreeNode(SnmpMibTree.this, l, paramSnmpMibAgent, this);
        this.children.addElement(localTreeNode);
      }
      else if (!this.agents.contains(paramSnmpMibAgent))
      {
        this.agents.addElement(paramSnmpMibAgent);
      }
      if (paramInt == paramArrayOfLong.length - 1) {
        localTreeNode.setAgent(paramSnmpMibAgent);
      } else {
        localTreeNode.registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibAgent);
      }
    }
    
    private TreeNode retrieveChild(long[] paramArrayOfLong, int paramInt)
    {
      long l = paramArrayOfLong[paramInt];
      Enumeration localEnumeration = this.children.elements();
      while (localEnumeration.hasMoreElements())
      {
        TreeNode localTreeNode = (TreeNode)localEnumeration.nextElement();
        if (localTreeNode.match(l)) {
          return localTreeNode;
        }
      }
      return null;
    }
    
    private boolean match(long paramLong)
    {
      return this.nodeValue == paramLong;
    }
  }
}
