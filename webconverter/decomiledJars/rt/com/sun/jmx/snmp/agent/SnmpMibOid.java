package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpMibOid
  extends SnmpMibNode
  implements Serializable
{
  private static final long serialVersionUID = 5012254771107446812L;
  private NonSyncVector<SnmpMibNode> children = new NonSyncVector(1);
  private int nbChildren = 0;
  
  public SnmpMibOid() {}
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      SnmpStatusException localSnmpStatusException = new SnmpStatusException(225);
      paramSnmpMibSubRequest.registerGetException(localSnmpVarBind, localSnmpStatusException);
    }
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      SnmpStatusException localSnmpStatusException = new SnmpStatusException(6);
      paramSnmpMibSubRequest.registerSetException(localSnmpVarBind, localSnmpStatusException);
    }
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt)
    throws SnmpStatusException
  {
    Enumeration localEnumeration = paramSnmpMibSubRequest.getElements();
    while (localEnumeration.hasMoreElements())
    {
      SnmpVarBind localSnmpVarBind = (SnmpVarBind)localEnumeration.nextElement();
      SnmpStatusException localSnmpStatusException = new SnmpStatusException(6);
      paramSnmpMibSubRequest.registerCheckException(localSnmpVarBind, localSnmpStatusException);
    }
  }
  
  void findHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree)
    throws SnmpStatusException
  {
    int i = paramArrayOfLong.length;
    Object localObject = null;
    if (paramSnmpRequestTree == null) {
      throw new SnmpStatusException(5);
    }
    if (paramInt > i) {
      throw new SnmpStatusException(225);
    }
    if (paramInt == i) {
      throw new SnmpStatusException(224);
    }
    SnmpMibNode localSnmpMibNode = getChild(paramArrayOfLong[paramInt]);
    if (localSnmpMibNode == null) {
      paramSnmpRequestTree.add(this, paramInt, paramSnmpVarBind);
    } else {
      localSnmpMibNode.findHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt + 1, paramSnmpRequestTree);
    }
  }
  
  long[] findNextHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt1, int paramInt2, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker)
    throws SnmpStatusException
  {
    int i = paramArrayOfLong.length;
    Object localObject1 = null;
    long[] arrayOfLong1 = null;
    if (paramSnmpRequestTree == null) {
      throw new SnmpStatusException(225);
    }
    Object localObject2 = paramSnmpRequestTree.getUserData();
    int j = paramSnmpRequestTree.getRequestPduVersion();
    if (paramInt1 >= i)
    {
      arrayOfLong2 = new long[1];
      arrayOfLong2[0] = getNextVarId(-1L, localObject2, j);
      arrayOfLong1 = findNextHandlingNode(paramSnmpVarBind, arrayOfLong2, 0, paramInt2, paramSnmpRequestTree, paramAcmChecker);
      return arrayOfLong1;
    }
    long[] arrayOfLong2 = new long[1];
    long l = paramArrayOfLong[paramInt1];
    for (;;)
    {
      try
      {
        SnmpMibNode localSnmpMibNode = getChild(l);
        if (localSnmpMibNode == null) {
          throw new SnmpStatusException(225);
        }
        paramAcmChecker.add(paramInt2, l);
        try
        {
          arrayOfLong1 = localSnmpMibNode.findNextHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt1 + 1, paramInt2 + 1, paramSnmpRequestTree, paramAcmChecker);
        }
        finally
        {
          paramAcmChecker.remove(paramInt2);
        }
        arrayOfLong1[paramInt2] = l;
        return arrayOfLong1;
      }
      catch (SnmpStatusException localSnmpStatusException)
      {
        l = getNextVarId(l, localObject2, j);
        arrayOfLong2[0] = l;
        paramInt1 = 1;
        paramArrayOfLong = arrayOfLong2;
      }
    }
  }
  
  public void getRootOid(Vector<Integer> paramVector)
  {
    if (this.nbChildren != 1) {
      return;
    }
    paramVector.addElement(Integer.valueOf(this.varList[0]));
    ((SnmpMibNode)this.children.firstElement()).getRootOid(paramVector);
  }
  
  public void registerNode(String paramString, SnmpMibNode paramSnmpMibNode)
    throws IllegalAccessException
  {
    SnmpOid localSnmpOid = new SnmpOid(paramString);
    registerNode(localSnmpOid.longValue(), 0, paramSnmpMibNode);
  }
  
  void registerNode(long[] paramArrayOfLong, int paramInt, SnmpMibNode paramSnmpMibNode)
    throws IllegalAccessException
  {
    if (paramInt >= paramArrayOfLong.length) {
      throw new IllegalAccessException();
    }
    long l = paramArrayOfLong[paramInt];
    int i = retrieveIndex(l);
    Object localObject;
    if (i == this.nbChildren)
    {
      this.nbChildren += 1;
      this.varList = new int[this.nbChildren];
      this.varList[0] = ((int)l);
      i = 0;
      if (paramInt + 1 == paramArrayOfLong.length)
      {
        this.children.insertElementAt(paramSnmpMibNode, i);
        return;
      }
      localObject = new SnmpMibOid();
      this.children.insertElementAt(localObject, i);
      ((SnmpMibOid)localObject).registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
      return;
    }
    if (i == -1)
    {
      localObject = new int[this.nbChildren + 1];
      localObject[this.nbChildren] = ((int)l);
      System.arraycopy(this.varList, 0, localObject, 0, this.nbChildren);
      this.varList = ((int[])localObject);
      this.nbChildren += 1;
      SnmpMibNode.sort(this.varList);
      int j = retrieveIndex(l);
      this.varList[j] = ((int)l);
      if (paramInt + 1 == paramArrayOfLong.length)
      {
        this.children.insertElementAt(paramSnmpMibNode, j);
        return;
      }
      SnmpMibOid localSnmpMibOid = new SnmpMibOid();
      this.children.insertElementAt(localSnmpMibOid, j);
      localSnmpMibOid.registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
    }
    else
    {
      localObject = (SnmpMibNode)this.children.elementAt(i);
      if (paramInt + 1 == paramArrayOfLong.length)
      {
        if (localObject == paramSnmpMibNode) {
          return;
        }
        if ((localObject != null) && (paramSnmpMibNode != null))
        {
          if ((paramSnmpMibNode instanceof SnmpMibGroup))
          {
            ((SnmpMibOid)localObject).exportChildren((SnmpMibOid)paramSnmpMibNode);
            this.children.setElementAt(paramSnmpMibNode, i);
            return;
          }
          if (((paramSnmpMibNode instanceof SnmpMibOid)) && ((localObject instanceof SnmpMibGroup)))
          {
            ((SnmpMibOid)paramSnmpMibNode).exportChildren((SnmpMibOid)localObject);
            return;
          }
          if ((paramSnmpMibNode instanceof SnmpMibOid))
          {
            ((SnmpMibOid)localObject).exportChildren((SnmpMibOid)paramSnmpMibNode);
            this.children.setElementAt(paramSnmpMibNode, i);
            return;
          }
        }
        this.children.setElementAt(paramSnmpMibNode, i);
      }
      else
      {
        if (localObject == null) {
          throw new IllegalAccessException();
        }
        ((SnmpMibOid)localObject).registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
      }
    }
  }
  
  void exportChildren(SnmpMibOid paramSnmpMibOid)
    throws IllegalAccessException
  {
    if (paramSnmpMibOid == null) {
      return;
    }
    long[] arrayOfLong = new long[1];
    for (int i = 0; i < this.nbChildren; i++)
    {
      SnmpMibNode localSnmpMibNode = (SnmpMibNode)this.children.elementAt(i);
      if (localSnmpMibNode != null)
      {
        arrayOfLong[0] = this.varList[i];
        paramSnmpMibOid.registerNode(arrayOfLong, 0, localSnmpMibNode);
      }
    }
  }
  
  SnmpMibNode getChild(long paramLong)
    throws SnmpStatusException
  {
    int i = getInsertAt(paramLong);
    if (i >= this.nbChildren) {
      throw new SnmpStatusException(225);
    }
    if (this.varList[i] != (int)paramLong) {
      throw new SnmpStatusException(225);
    }
    SnmpMibNode localSnmpMibNode = null;
    try
    {
      localSnmpMibNode = (SnmpMibNode)this.children.elementAtNonSync(i);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new SnmpStatusException(225);
    }
    if (localSnmpMibNode == null) {
      throw new SnmpStatusException(224);
    }
    return localSnmpMibNode;
  }
  
  private int retrieveIndex(long paramLong)
  {
    int i = 0;
    int j = (int)paramLong;
    if ((this.varList == null) || (this.varList.length < 1)) {
      return this.nbChildren;
    }
    int k = this.varList.length - 1;
    for (int m = i + (k - i) / 2; i <= k; m = i + (k - i) / 2)
    {
      int n = this.varList[m];
      if (j == n) {
        return m;
      }
      if (n < j) {
        i = m + 1;
      } else {
        k = m - 1;
      }
    }
    return -1;
  }
  
  private int getInsertAt(long paramLong)
  {
    int i = 0;
    int j = (int)paramLong;
    if (this.varList == null) {
      return -1;
    }
    int k = this.varList.length - 1;
    for (int n = i + (k - i) / 2; i <= k; n = i + (k - i) / 2)
    {
      int m = this.varList[n];
      if (j == m) {
        return n;
      }
      if (m < j) {
        i = n + 1;
      } else {
        k = n - 1;
      }
    }
    return n;
  }
  
  class NonSyncVector<E>
    extends Vector<E>
  {
    public NonSyncVector(int paramInt)
    {
      super();
    }
    
    final void addNonSyncElement(E paramE)
    {
      ensureCapacity(this.elementCount + 1);
      this.elementData[(this.elementCount++)] = paramE;
    }
    
    final E elementAtNonSync(int paramInt)
    {
      return this.elementData[paramInt];
    }
  }
}
