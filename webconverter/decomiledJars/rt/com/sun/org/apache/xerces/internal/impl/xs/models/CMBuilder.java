package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.xs.XSComplexTypeDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSModelGroupImpl;
import com.sun.org.apache.xerces.internal.impl.xs.XSParticleDecl;

public class CMBuilder
{
  private XSDeclarationPool fDeclPool = null;
  private static XSEmptyCM fEmptyCM = new XSEmptyCM();
  private int fLeafCount;
  private int fParticleCount;
  private CMNodeFactory fNodeFactory;
  
  public CMBuilder(CMNodeFactory paramCMNodeFactory)
  {
    this.fNodeFactory = paramCMNodeFactory;
  }
  
  public void setDeclPool(XSDeclarationPool paramXSDeclarationPool)
  {
    this.fDeclPool = paramXSDeclarationPool;
  }
  
  public XSCMValidator getContentModel(XSComplexTypeDecl paramXSComplexTypeDecl)
  {
    int i = paramXSComplexTypeDecl.getContentType();
    if ((i == 1) || (i == 0)) {
      return null;
    }
    XSParticleDecl localXSParticleDecl = (XSParticleDecl)paramXSComplexTypeDecl.getParticle();
    if (localXSParticleDecl == null) {
      return fEmptyCM;
    }
    Object localObject = null;
    if ((localXSParticleDecl.fType == 3) && (((XSModelGroupImpl)localXSParticleDecl.fValue).fCompositor == 103)) {
      localObject = createAllCM(localXSParticleDecl);
    } else {
      localObject = createDFACM(localXSParticleDecl);
    }
    this.fNodeFactory.resetNodeCount();
    if (localObject == null) {
      localObject = fEmptyCM;
    }
    return localObject;
  }
  
  XSCMValidator createAllCM(XSParticleDecl paramXSParticleDecl)
  {
    if (paramXSParticleDecl.fMaxOccurs == 0) {
      return null;
    }
    XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
    XSAllCM localXSAllCM = new XSAllCM(paramXSParticleDecl.fMinOccurs == 0, localXSModelGroupImpl.fParticleCount);
    for (int i = 0; i < localXSModelGroupImpl.fParticleCount; i++) {
      localXSAllCM.addElement((XSElementDecl)localXSModelGroupImpl.fParticles[i].fValue, localXSModelGroupImpl.fParticles[i].fMinOccurs == 0);
    }
    return localXSAllCM;
  }
  
  XSCMValidator createDFACM(XSParticleDecl paramXSParticleDecl)
  {
    this.fLeafCount = 0;
    this.fParticleCount = 0;
    CMNode localCMNode = useRepeatingLeafNodes(paramXSParticleDecl) ? buildCompactSyntaxTree(paramXSParticleDecl) : buildSyntaxTree(paramXSParticleDecl, true);
    if (localCMNode == null) {
      return null;
    }
    return new XSDFACM(localCMNode, this.fLeafCount);
  }
  
  private CMNode buildSyntaxTree(XSParticleDecl paramXSParticleDecl, boolean paramBoolean)
  {
    int i = paramXSParticleDecl.fMaxOccurs;
    int j = paramXSParticleDecl.fMinOccurs;
    int k = paramXSParticleDecl.fType;
    Object localObject = null;
    if ((k == 2) || (k == 1))
    {
      localObject = this.fNodeFactory.getCMLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
      localObject = expandContentModel((CMNode)localObject, j, i, paramBoolean);
    }
    else if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
      CMNode localCMNode = null;
      int m = 0;
      for (int n = 0; n < localXSModelGroupImpl.fParticleCount; n++)
      {
        localCMNode = buildSyntaxTree(localXSModelGroupImpl.fParticles[n], (paramBoolean) && (j == 1) && (i == 1) && ((localXSModelGroupImpl.fCompositor == 102) || (localXSModelGroupImpl.fParticleCount == 1)));
        if (localCMNode != null) {
          if (localObject == null)
          {
            localObject = localCMNode;
          }
          else
          {
            localObject = this.fNodeFactory.getCMBinOpNode(localXSModelGroupImpl.fCompositor, (CMNode)localObject, localCMNode);
            m = 1;
          }
        }
      }
      if (localObject != null)
      {
        if ((localXSModelGroupImpl.fCompositor == 101) && (m == 0) && (localXSModelGroupImpl.fParticleCount > 1)) {
          localObject = this.fNodeFactory.getCMUniOpNode(5, (CMNode)localObject);
        }
        localObject = expandContentModel((CMNode)localObject, j, i, false);
      }
    }
    return localObject;
  }
  
  private CMNode expandContentModel(CMNode paramCMNode, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    CMNode localCMNode = null;
    if ((paramInt1 == 1) && (paramInt2 == 1))
    {
      localCMNode = paramCMNode;
    }
    else if ((paramInt1 == 0) && (paramInt2 == 1))
    {
      localCMNode = this.fNodeFactory.getCMUniOpNode(5, paramCMNode);
    }
    else if ((paramInt1 == 0) && (paramInt2 == -1))
    {
      localCMNode = this.fNodeFactory.getCMUniOpNode(4, paramCMNode);
    }
    else if ((paramInt1 == 1) && (paramInt2 == -1))
    {
      localCMNode = this.fNodeFactory.getCMUniOpNode(6, paramCMNode);
    }
    else if (((paramBoolean) && (paramCMNode.type() == 1)) || (paramCMNode.type() == 2))
    {
      localCMNode = this.fNodeFactory.getCMUniOpNode(paramInt1 == 0 ? 4 : 6, paramCMNode);
      localCMNode.setUserData(new int[] { paramInt1, paramInt2 });
    }
    else if (paramInt2 == -1)
    {
      localCMNode = this.fNodeFactory.getCMUniOpNode(6, paramCMNode);
      localCMNode = this.fNodeFactory.getCMBinOpNode(102, multiNodes(paramCMNode, paramInt1 - 1, true), localCMNode);
    }
    else
    {
      if (paramInt1 > 0) {
        localCMNode = multiNodes(paramCMNode, paramInt1, false);
      }
      if (paramInt2 > paramInt1)
      {
        paramCMNode = this.fNodeFactory.getCMUniOpNode(5, paramCMNode);
        if (localCMNode == null) {
          localCMNode = multiNodes(paramCMNode, paramInt2 - paramInt1, false);
        } else {
          localCMNode = this.fNodeFactory.getCMBinOpNode(102, localCMNode, multiNodes(paramCMNode, paramInt2 - paramInt1, true));
        }
      }
    }
    return localCMNode;
  }
  
  private CMNode multiNodes(CMNode paramCMNode, int paramInt, boolean paramBoolean)
  {
    if (paramInt == 0) {
      return null;
    }
    if (paramInt == 1) {
      return paramBoolean ? copyNode(paramCMNode) : paramCMNode;
    }
    int i = paramInt / 2;
    return this.fNodeFactory.getCMBinOpNode(102, multiNodes(paramCMNode, i, paramBoolean), multiNodes(paramCMNode, paramInt - i, true));
  }
  
  private CMNode copyNode(CMNode paramCMNode)
  {
    int i = paramCMNode.type();
    Object localObject;
    if ((i == 101) || (i == 102))
    {
      localObject = (XSCMBinOp)paramCMNode;
      paramCMNode = this.fNodeFactory.getCMBinOpNode(i, copyNode(((XSCMBinOp)localObject).getLeft()), copyNode(((XSCMBinOp)localObject).getRight()));
    }
    else if ((i == 4) || (i == 6) || (i == 5))
    {
      localObject = (XSCMUniOp)paramCMNode;
      paramCMNode = this.fNodeFactory.getCMUniOpNode(i, copyNode(((XSCMUniOp)localObject).getChild()));
    }
    else if ((i == 1) || (i == 2))
    {
      localObject = (XSCMLeaf)paramCMNode;
      paramCMNode = this.fNodeFactory.getCMLeafNode(((XSCMLeaf)localObject).type(), ((XSCMLeaf)localObject).getLeaf(), ((XSCMLeaf)localObject).getParticleId(), this.fLeafCount++);
    }
    return paramCMNode;
  }
  
  private CMNode buildCompactSyntaxTree(XSParticleDecl paramXSParticleDecl)
  {
    int i = paramXSParticleDecl.fMaxOccurs;
    int j = paramXSParticleDecl.fMinOccurs;
    int k = paramXSParticleDecl.fType;
    Object localObject = null;
    if ((k == 2) || (k == 1)) {
      return buildCompactSyntaxTree2(paramXSParticleDecl, j, i);
    }
    if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
      if ((localXSModelGroupImpl.fParticleCount == 1) && ((j != 1) || (i != 1))) {
        return buildCompactSyntaxTree2(localXSModelGroupImpl.fParticles[0], j, i);
      }
      CMNode localCMNode = null;
      int m = 0;
      for (int n = 0; n < localXSModelGroupImpl.fParticleCount; n++)
      {
        localCMNode = buildCompactSyntaxTree(localXSModelGroupImpl.fParticles[n]);
        if (localCMNode != null)
        {
          m++;
          if (localObject == null) {
            localObject = localCMNode;
          } else {
            localObject = this.fNodeFactory.getCMBinOpNode(localXSModelGroupImpl.fCompositor, (CMNode)localObject, localCMNode);
          }
        }
      }
      if ((localObject != null) && (localXSModelGroupImpl.fCompositor == 101) && (m < localXSModelGroupImpl.fParticleCount)) {
        localObject = this.fNodeFactory.getCMUniOpNode(5, (CMNode)localObject);
      }
    }
    return localObject;
  }
  
  private CMNode buildCompactSyntaxTree2(XSParticleDecl paramXSParticleDecl, int paramInt1, int paramInt2)
  {
    CMNode localCMNode = null;
    if ((paramInt1 == 1) && (paramInt2 == 1))
    {
      localCMNode = this.fNodeFactory.getCMLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
    }
    else if ((paramInt1 == 0) && (paramInt2 == 1))
    {
      localCMNode = this.fNodeFactory.getCMLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
      localCMNode = this.fNodeFactory.getCMUniOpNode(5, localCMNode);
    }
    else if ((paramInt1 == 0) && (paramInt2 == -1))
    {
      localCMNode = this.fNodeFactory.getCMLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
      localCMNode = this.fNodeFactory.getCMUniOpNode(4, localCMNode);
    }
    else if ((paramInt1 == 1) && (paramInt2 == -1))
    {
      localCMNode = this.fNodeFactory.getCMLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
      localCMNode = this.fNodeFactory.getCMUniOpNode(6, localCMNode);
    }
    else
    {
      localCMNode = this.fNodeFactory.getCMRepeatingLeafNode(paramXSParticleDecl.fType, paramXSParticleDecl.fValue, paramInt1, paramInt2, this.fParticleCount++, this.fLeafCount++);
      if (paramInt1 == 0) {
        localCMNode = this.fNodeFactory.getCMUniOpNode(4, localCMNode);
      } else {
        localCMNode = this.fNodeFactory.getCMUniOpNode(6, localCMNode);
      }
    }
    return localCMNode;
  }
  
  private boolean useRepeatingLeafNodes(XSParticleDecl paramXSParticleDecl)
  {
    int i = paramXSParticleDecl.fMaxOccurs;
    int j = paramXSParticleDecl.fMinOccurs;
    int k = paramXSParticleDecl.fType;
    if (k == 3)
    {
      XSModelGroupImpl localXSModelGroupImpl = (XSModelGroupImpl)paramXSParticleDecl.fValue;
      if ((j != 1) || (i != 1))
      {
        if (localXSModelGroupImpl.fParticleCount == 1)
        {
          XSParticleDecl localXSParticleDecl = localXSModelGroupImpl.fParticles[0];
          int n = localXSParticleDecl.fType;
          return ((n == 1) || (n == 2)) && (localXSParticleDecl.fMinOccurs == 1) && (localXSParticleDecl.fMaxOccurs == 1);
        }
        return localXSModelGroupImpl.fParticleCount == 0;
      }
      for (int m = 0; m < localXSModelGroupImpl.fParticleCount; m++) {
        if (!useRepeatingLeafNodes(localXSModelGroupImpl.fParticles[m])) {
          return false;
        }
      }
    }
    return true;
  }
}
