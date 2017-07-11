package sun.awt.geom;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class AreaOp
{
  public static final int CTAG_LEFT = 0;
  public static final int CTAG_RIGHT = 1;
  public static final int ETAG_IGNORE = 0;
  public static final int ETAG_ENTER = 1;
  public static final int ETAG_EXIT = -1;
  public static final int RSTAG_INSIDE = 1;
  public static final int RSTAG_OUTSIDE = -1;
  private static Comparator YXTopComparator = new Comparator()
  {
    public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
    {
      Curve localCurve1 = ((Edge)paramAnonymousObject1).getCurve();
      Curve localCurve2 = ((Edge)paramAnonymousObject2).getCurve();
      double d1;
      double d2;
      if (((d1 = localCurve1.getYTop()) == (d2 = localCurve2.getYTop())) && ((d1 = localCurve1.getXTop()) == (d2 = localCurve2.getXTop()))) {
        return 0;
      }
      if (d1 < d2) {
        return -1;
      }
      return 1;
    }
  };
  private static CurveLink[] EmptyLinkList = new CurveLink[2];
  private static ChainEnd[] EmptyChainList = new ChainEnd[2];
  
  private AreaOp() {}
  
  public abstract void newRow();
  
  public abstract int classify(Edge paramEdge);
  
  public abstract int getState();
  
  public Vector calculate(Vector paramVector1, Vector paramVector2)
  {
    Vector localVector = new Vector();
    addEdges(localVector, paramVector1, 0);
    addEdges(localVector, paramVector2, 1);
    localVector = pruneEdges(localVector);
    return localVector;
  }
  
  private static void addEdges(Vector paramVector1, Vector paramVector2, int paramInt)
  {
    Enumeration localEnumeration = paramVector2.elements();
    while (localEnumeration.hasMoreElements())
    {
      Curve localCurve = (Curve)localEnumeration.nextElement();
      if (localCurve.getOrder() > 0) {
        paramVector1.add(new Edge(localCurve, paramInt));
      }
    }
  }
  
  private Vector pruneEdges(Vector paramVector)
  {
    int i = paramVector.size();
    if (i < 2) {
      return paramVector;
    }
    Edge[] arrayOfEdge = (Edge[])paramVector.toArray(new Edge[i]);
    Arrays.sort(arrayOfEdge, YXTopComparator);
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    double[] arrayOfDouble = new double[2];
    Vector localVector1 = new Vector();
    Vector localVector2 = new Vector();
    Vector localVector3 = new Vector();
    while (j < i)
    {
      double d1 = arrayOfDouble[0];
      Object localObject1;
      for (m = n = k - 1; m >= j; m--)
      {
        localObject1 = arrayOfEdge[m];
        if (((Edge)localObject1).getCurve().getYBot() > d1)
        {
          if (n > m) {
            arrayOfEdge[n] = localObject1;
          }
          n--;
        }
      }
      j = n + 1;
      if (j >= k)
      {
        if (k >= i) {
          break;
        }
        d1 = arrayOfEdge[k].getCurve().getYTop();
        if (d1 > arrayOfDouble[0]) {
          finalizeSubCurves(localVector1, localVector2);
        }
        arrayOfDouble[0] = d1;
      }
      while (k < i)
      {
        localObject1 = arrayOfEdge[k];
        if (((Edge)localObject1).getCurve().getYTop() > d1) {
          break;
        }
        k++;
      }
      arrayOfDouble[1] = arrayOfEdge[j].getCurve().getYBot();
      if (k < i)
      {
        d1 = arrayOfEdge[k].getCurve().getYTop();
        if (arrayOfDouble[1] > d1) {
          arrayOfDouble[1] = d1;
        }
      }
      int i1 = 1;
      for (m = j; m < k; m++)
      {
        localObject1 = arrayOfEdge[m];
        ((Edge)localObject1).setEquivalence(0);
        for (n = m; n > j; n--)
        {
          Edge localEdge = arrayOfEdge[(n - 1)];
          int i2 = ((Edge)localObject1).compareTo(localEdge, arrayOfDouble);
          if (arrayOfDouble[1] <= arrayOfDouble[0]) {
            throw new InternalError("backstepping to " + arrayOfDouble[1] + " from " + arrayOfDouble[0]);
          }
          if (i2 >= 0)
          {
            if (i2 != 0) {
              break;
            }
            int i3 = localEdge.getEquivalence();
            if (i3 == 0)
            {
              i3 = i1++;
              localEdge.setEquivalence(i3);
            }
            ((Edge)localObject1).setEquivalence(i3);
            break;
          }
          arrayOfEdge[n] = localEdge;
        }
        arrayOfEdge[n] = localObject1;
      }
      newRow();
      double d2 = arrayOfDouble[0];
      double d3 = arrayOfDouble[1];
      int i4;
      for (m = j; m < k; m++)
      {
        localObject1 = arrayOfEdge[m];
        int i5 = ((Edge)localObject1).getEquivalence();
        if (i5 != 0)
        {
          int i6 = getState();
          i4 = i6 == 1 ? -1 : 1;
          Object localObject4 = null;
          Object localObject5 = localObject1;
          double d4 = d3;
          do
          {
            classify((Edge)localObject1);
            if ((localObject4 == null) && (((Edge)localObject1).isActiveFor(d2, i4))) {
              localObject4 = localObject1;
            }
            d1 = ((Edge)localObject1).getCurve().getYBot();
            if (d1 > d4)
            {
              localObject5 = localObject1;
              d4 = d1;
            }
            m++;
          } while ((m < k) && ((localObject1 = arrayOfEdge[m]).getEquivalence() == i5));
          m--;
          if (getState() == i6) {
            i4 = 0;
          } else {
            localObject1 = localObject4 != null ? localObject4 : localObject5;
          }
        }
        else
        {
          i4 = classify((Edge)localObject1);
        }
        if (i4 != 0)
        {
          ((Edge)localObject1).record(d3, i4);
          localVector3.add(new CurveLink(((Edge)localObject1).getCurve(), d2, d3, i4));
        }
      }
      if (getState() != -1)
      {
        System.out.println("Still inside at end of active edge list!");
        System.out.println("num curves = " + (k - j));
        System.out.println("num links = " + localVector3.size());
        System.out.println("y top = " + arrayOfDouble[0]);
        if (k < i) {
          System.out.println("y top of next curve = " + arrayOfEdge[k].getCurve().getYTop());
        } else {
          System.out.println("no more curves");
        }
        for (m = j; m < k; m++)
        {
          localObject1 = arrayOfEdge[m];
          System.out.println(localObject1);
          i4 = ((Edge)localObject1).getEquivalence();
          if (i4 != 0) {
            System.out.println("  was equal to " + i4 + "...");
          }
        }
      }
      resolveLinks(localVector1, localVector2, localVector3);
      localVector3.clear();
      arrayOfDouble[0] = d3;
    }
    finalizeSubCurves(localVector1, localVector2);
    Vector localVector4 = new Vector();
    Enumeration localEnumeration = localVector1.elements();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject2 = (CurveLink)localEnumeration.nextElement();
      localVector4.add(((CurveLink)localObject2).getMoveto());
      Object localObject3 = localObject2;
      while ((localObject3 = ((CurveLink)localObject3).getNext()) != null) {
        if (!((CurveLink)localObject2).absorb((CurveLink)localObject3))
        {
          localVector4.add(((CurveLink)localObject2).getSubCurve());
          localObject2 = localObject3;
        }
      }
      localVector4.add(((CurveLink)localObject2).getSubCurve());
    }
    return localVector4;
  }
  
  public static void finalizeSubCurves(Vector paramVector1, Vector paramVector2)
  {
    int i = paramVector2.size();
    if (i == 0) {
      return;
    }
    if ((i & 0x1) != 0) {
      throw new InternalError("Odd number of chains!");
    }
    ChainEnd[] arrayOfChainEnd = new ChainEnd[i];
    paramVector2.toArray(arrayOfChainEnd);
    for (int j = 1; j < i; j += 2)
    {
      ChainEnd localChainEnd1 = arrayOfChainEnd[(j - 1)];
      ChainEnd localChainEnd2 = arrayOfChainEnd[j];
      CurveLink localCurveLink = localChainEnd1.linkTo(localChainEnd2);
      if (localCurveLink != null) {
        paramVector1.add(localCurveLink);
      }
    }
    paramVector2.clear();
  }
  
  public static void resolveLinks(Vector paramVector1, Vector paramVector2, Vector paramVector3)
  {
    int i = paramVector3.size();
    CurveLink[] arrayOfCurveLink;
    if (i == 0)
    {
      arrayOfCurveLink = EmptyLinkList;
    }
    else
    {
      if ((i & 0x1) != 0) {
        throw new InternalError("Odd number of new curves!");
      }
      arrayOfCurveLink = new CurveLink[i + 2];
      paramVector3.toArray(arrayOfCurveLink);
    }
    int j = paramVector2.size();
    ChainEnd[] arrayOfChainEnd;
    if (j == 0)
    {
      arrayOfChainEnd = EmptyChainList;
    }
    else
    {
      if ((j & 0x1) != 0) {
        throw new InternalError("Odd number of chains!");
      }
      arrayOfChainEnd = new ChainEnd[j + 2];
      paramVector2.toArray(arrayOfChainEnd);
    }
    int k = 0;
    int m = 0;
    paramVector2.clear();
    Object localObject1 = arrayOfChainEnd[0];
    ChainEnd localChainEnd1 = arrayOfChainEnd[1];
    Object localObject2 = arrayOfCurveLink[0];
    for (CurveLink localCurveLink = arrayOfCurveLink[1]; (localObject1 != null) || (localObject2 != null); localCurveLink = arrayOfCurveLink[(m + 1)])
    {
      int n = localObject2 == null ? 1 : 0;
      int i1 = localObject1 == null ? 1 : 0;
      if ((n == 0) && (i1 == 0))
      {
        n = ((k & 0x1) == 0) && (((ChainEnd)localObject1).getX() == localChainEnd1.getX()) ? 1 : 0;
        i1 = ((m & 0x1) == 0) && (((CurveLink)localObject2).getX() == localCurveLink.getX()) ? 1 : 0;
        if ((n == 0) && (i1 == 0))
        {
          double d1 = ((ChainEnd)localObject1).getX();
          double d2 = ((CurveLink)localObject2).getX();
          n = (localChainEnd1 != null) && (d1 < d2) && (obstructs(localChainEnd1.getX(), d2, k)) ? 1 : 0;
          i1 = (localCurveLink != null) && (d2 < d1) && (obstructs(localCurveLink.getX(), d1, m)) ? 1 : 0;
        }
      }
      Object localObject3;
      if (n != 0)
      {
        localObject3 = ((ChainEnd)localObject1).linkTo(localChainEnd1);
        if (localObject3 != null) {
          paramVector1.add(localObject3);
        }
        k += 2;
        localObject1 = arrayOfChainEnd[k];
        localChainEnd1 = arrayOfChainEnd[(k + 1)];
      }
      if (i1 != 0)
      {
        localObject3 = new ChainEnd((CurveLink)localObject2, null);
        ChainEnd localChainEnd2 = new ChainEnd(localCurveLink, (ChainEnd)localObject3);
        ((ChainEnd)localObject3).setOtherEnd(localChainEnd2);
        paramVector2.add(localObject3);
        paramVector2.add(localChainEnd2);
        m += 2;
        localObject2 = arrayOfCurveLink[m];
        localCurveLink = arrayOfCurveLink[(m + 1)];
      }
      if ((n == 0) && (i1 == 0))
      {
        ((ChainEnd)localObject1).addLink((CurveLink)localObject2);
        paramVector2.add(localObject1);
        k++;
        localObject1 = localChainEnd1;
        localChainEnd1 = arrayOfChainEnd[(k + 1)];
        m++;
        localObject2 = localCurveLink;
      }
    }
    if ((paramVector2.size() & 0x1) != 0) {
      System.out.println("Odd number of chains!");
    }
  }
  
  public static boolean obstructs(double paramDouble1, double paramDouble2, int paramInt)
  {
    return paramDouble1 <= paramDouble2;
  }
  
  public static class AddOp
    extends AreaOp.CAGOp
  {
    public AddOp() {}
    
    public boolean newClassification(boolean paramBoolean1, boolean paramBoolean2)
    {
      return (paramBoolean1) || (paramBoolean2);
    }
  }
  
  public static abstract class CAGOp
    extends AreaOp
  {
    boolean inLeft;
    boolean inRight;
    boolean inResult;
    
    public CAGOp()
    {
      super();
    }
    
    public void newRow()
    {
      this.inLeft = false;
      this.inRight = false;
      this.inResult = false;
    }
    
    public int classify(Edge paramEdge)
    {
      if (paramEdge.getCurveTag() == 0) {
        this.inLeft = (!this.inLeft);
      } else {
        this.inRight = (!this.inRight);
      }
      boolean bool = newClassification(this.inLeft, this.inRight);
      if (this.inResult == bool) {
        return 0;
      }
      this.inResult = bool;
      return bool ? 1 : -1;
    }
    
    public int getState()
    {
      return this.inResult ? 1 : -1;
    }
    
    public abstract boolean newClassification(boolean paramBoolean1, boolean paramBoolean2);
  }
  
  public static class EOWindOp
    extends AreaOp
  {
    private boolean inside;
    
    public EOWindOp()
    {
      super();
    }
    
    public void newRow()
    {
      this.inside = false;
    }
    
    public int classify(Edge paramEdge)
    {
      boolean bool = !this.inside;
      this.inside = bool;
      return bool ? 1 : -1;
    }
    
    public int getState()
    {
      return this.inside ? 1 : -1;
    }
  }
  
  public static class IntOp
    extends AreaOp.CAGOp
  {
    public IntOp() {}
    
    public boolean newClassification(boolean paramBoolean1, boolean paramBoolean2)
    {
      return (paramBoolean1) && (paramBoolean2);
    }
  }
  
  public static class NZWindOp
    extends AreaOp
  {
    private int count;
    
    public NZWindOp()
    {
      super();
    }
    
    public void newRow()
    {
      this.count = 0;
    }
    
    public int classify(Edge paramEdge)
    {
      int i = this.count;
      int j = i == 0 ? 1 : 0;
      i += paramEdge.getCurve().getDirection();
      this.count = i;
      return i == 0 ? -1 : j;
    }
    
    public int getState()
    {
      return this.count == 0 ? -1 : 1;
    }
  }
  
  public static class SubOp
    extends AreaOp.CAGOp
  {
    public SubOp() {}
    
    public boolean newClassification(boolean paramBoolean1, boolean paramBoolean2)
    {
      return (paramBoolean1) && (!paramBoolean2);
    }
  }
  
  public static class XorOp
    extends AreaOp.CAGOp
  {
    public XorOp() {}
    
    public boolean newClassification(boolean paramBoolean1, boolean paramBoolean2)
    {
      return paramBoolean1 != paramBoolean2;
    }
  }
}
