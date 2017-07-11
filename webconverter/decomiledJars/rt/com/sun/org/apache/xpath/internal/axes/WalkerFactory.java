package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import com.sun.org.apache.xpath.internal.compiler.OpMap;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.patterns.ContextMatchStepPattern;
import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import java.io.PrintStream;
import javax.xml.transform.TransformerException;

public class WalkerFactory
{
  static final boolean DEBUG_PATTERN_CREATION = false;
  static final boolean DEBUG_WALKER_CREATION = false;
  static final boolean DEBUG_ITERATOR_CREATION = false;
  public static final int BITS_COUNT = 255;
  public static final int BITS_RESERVED = 3840;
  public static final int BIT_PREDICATE = 4096;
  public static final int BIT_ANCESTOR = 8192;
  public static final int BIT_ANCESTOR_OR_SELF = 16384;
  public static final int BIT_ATTRIBUTE = 32768;
  public static final int BIT_CHILD = 65536;
  public static final int BIT_DESCENDANT = 131072;
  public static final int BIT_DESCENDANT_OR_SELF = 262144;
  public static final int BIT_FOLLOWING = 524288;
  public static final int BIT_FOLLOWING_SIBLING = 1048576;
  public static final int BIT_NAMESPACE = 2097152;
  public static final int BIT_PARENT = 4194304;
  public static final int BIT_PRECEDING = 8388608;
  public static final int BIT_PRECEDING_SIBLING = 16777216;
  public static final int BIT_SELF = 33554432;
  public static final int BIT_FILTER = 67108864;
  public static final int BIT_ROOT = 134217728;
  public static final int BITMASK_TRAVERSES_OUTSIDE_SUBTREE = 234381312;
  public static final int BIT_BACKWARDS_SELF = 268435456;
  public static final int BIT_ANY_DESCENDANT_FROM_ROOT = 536870912;
  public static final int BIT_NODETEST_ANY = 1073741824;
  public static final int BIT_MATCH_PATTERN = Integer.MIN_VALUE;
  
  public WalkerFactory() {}
  
  static AxesWalker loadOneWalker(WalkingIterator paramWalkingIterator, Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    AxesWalker localAxesWalker = null;
    int i = paramCompiler.getOp(paramInt);
    if (i != -1)
    {
      localAxesWalker = createDefaultWalker(paramCompiler, i, paramWalkingIterator, 0);
      localAxesWalker.init(paramCompiler, paramInt, i);
    }
    return localAxesWalker;
  }
  
  static AxesWalker loadWalkers(WalkingIterator paramWalkingIterator, Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int j = analyze(paramCompiler, paramInt1, paramInt2);
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1)))
    {
      AxesWalker localAxesWalker = createDefaultWalker(paramCompiler, paramInt1, paramWalkingIterator, j);
      localAxesWalker.init(paramCompiler, paramInt1, i);
      localAxesWalker.exprSetParent(paramWalkingIterator);
      if (null == localObject1)
      {
        localObject1 = localAxesWalker;
      }
      else
      {
        ((AxesWalker)localObject2).setNextWalker(localAxesWalker);
        localAxesWalker.setPrevWalker((AxesWalker)localObject2);
      }
      localObject2 = localAxesWalker;
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0) {
        break;
      }
    }
    return localObject1;
  }
  
  public static boolean isSet(int paramInt1, int paramInt2)
  {
    return 0 != (paramInt1 & paramInt2);
  }
  
  public static void diagnoseIterator(String paramString, int paramInt, Compiler paramCompiler)
  {
    System.out.println(paramCompiler.toString() + ", " + paramString + ", " + Integer.toBinaryString(paramInt) + ", " + getAnalysisString(paramInt));
  }
  
  public static DTMIterator newDTMIterator(Compiler paramCompiler, int paramInt, boolean paramBoolean)
    throws TransformerException
  {
    int i = OpMap.getFirstChildPos(paramInt);
    int j = analyze(paramCompiler, i, 0);
    boolean bool = isOneStep(j);
    Object localObject;
    if ((bool) && (walksSelfOnly(j)) && (isWild(j)) && (!hasPredicate(j))) {
      localObject = new SelfIteratorNoPredicate(paramCompiler, paramInt, j);
    } else if ((walksChildrenOnly(j)) && (bool))
    {
      if ((isWild(j)) && (!hasPredicate(j))) {
        localObject = new ChildIterator(paramCompiler, paramInt, j);
      } else {
        localObject = new ChildTestIterator(paramCompiler, paramInt, j);
      }
    }
    else if ((bool) && (walksAttributes(j))) {
      localObject = new AttributeIterator(paramCompiler, paramInt, j);
    } else if ((bool) && (!walksFilteredList(j)))
    {
      if ((!walksNamespaces(j)) && ((walksInDocOrder(j)) || (isSet(j, 4194304)))) {
        localObject = new OneStepIteratorForward(paramCompiler, paramInt, j);
      } else {
        localObject = new OneStepIterator(paramCompiler, paramInt, j);
      }
    }
    else if (isOptimizableForDescendantIterator(paramCompiler, i, 0)) {
      localObject = new DescendantIterator(paramCompiler, paramInt, j);
    } else if (isNaturalDocOrder(paramCompiler, i, 0, j)) {
      localObject = new WalkingIterator(paramCompiler, paramInt, j, true);
    } else {
      localObject = new WalkingIteratorSorted(paramCompiler, paramInt, j, true);
    }
    if ((localObject instanceof LocPathIterator)) {
      ((LocPathIterator)localObject).setIsTopLevel(paramBoolean);
    }
    return localObject;
  }
  
  public static int getAxisFromStep(Compiler paramCompiler, int paramInt)
    throws TransformerException
  {
    int i = paramCompiler.getOp(paramInt);
    switch (i)
    {
    case 43: 
      return 6;
    case 44: 
      return 7;
    case 46: 
      return 11;
    case 47: 
      return 12;
    case 45: 
      return 10;
    case 49: 
      return 9;
    case 37: 
      return 0;
    case 38: 
      return 1;
    case 39: 
      return 2;
    case 50: 
      return 19;
    case 40: 
      return 3;
    case 42: 
      return 5;
    case 41: 
      return 4;
    case 48: 
      return 13;
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      return 20;
    }
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
  }
  
  public static int getAnalysisBitFromAxes(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return 8192;
    case 1: 
      return 16384;
    case 2: 
      return 32768;
    case 3: 
      return 65536;
    case 4: 
      return 131072;
    case 5: 
      return 262144;
    case 6: 
      return 524288;
    case 7: 
      return 1048576;
    case 8: 
    case 9: 
      return 2097152;
    case 10: 
      return 4194304;
    case 11: 
      return 8388608;
    case 12: 
      return 16777216;
    case 13: 
      return 33554432;
    case 14: 
      return 262144;
    case 16: 
    case 17: 
    case 18: 
      return 536870912;
    case 19: 
      return 134217728;
    case 20: 
      return 67108864;
    }
    return 67108864;
  }
  
  static boolean functionProximateOrContainsProximate(Compiler paramCompiler, int paramInt)
  {
    int i = paramInt + paramCompiler.getOp(paramInt + 1) - 1;
    paramInt = OpMap.getFirstChildPos(paramInt);
    int j = paramCompiler.getOp(paramInt);
    switch (j)
    {
    case 1: 
    case 2: 
      return true;
    }
    paramInt++;
    int k = 0;
    int m = paramInt;
    while (m < i)
    {
      int n = m + 2;
      int i1 = paramCompiler.getOp(n);
      boolean bool = isProximateInnerExpr(paramCompiler, n);
      if (bool) {
        return true;
      }
      m = paramCompiler.getNextOpPos(m);
      k++;
    }
    return false;
  }
  
  static boolean isProximateInnerExpr(Compiler paramCompiler, int paramInt)
  {
    int i = paramCompiler.getOp(paramInt);
    int j = paramInt + 2;
    boolean bool;
    switch (i)
    {
    case 26: 
      if (isProximateInnerExpr(paramCompiler, j)) {
        return true;
      }
      break;
    case 21: 
    case 22: 
    case 27: 
    case 28: 
      break;
    case 25: 
      bool = functionProximateOrContainsProximate(paramCompiler, paramInt);
      if (bool) {
        return true;
      }
      break;
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
      int k = OpMap.getFirstChildPos(i);
      int m = paramCompiler.getNextOpPos(k);
      bool = isProximateInnerExpr(paramCompiler, k);
      if (bool) {
        return true;
      }
      bool = isProximateInnerExpr(paramCompiler, m);
      if (bool) {
        return true;
      }
      break;
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    case 20: 
    case 23: 
    case 24: 
    default: 
      return true;
    }
    return false;
  }
  
  public static boolean mightBeProximate(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    boolean bool1 = false;
    int i;
    switch (paramInt2)
    {
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      i = paramCompiler.getArgLength(paramInt1);
      break;
    default: 
      i = paramCompiler.getArgLengthOfStep(paramInt1);
    }
    int j = paramCompiler.getFirstPredicateOpPos(paramInt1);
    int k = 0;
    while (29 == paramCompiler.getOp(j))
    {
      k++;
      int m = j + 2;
      int n = paramCompiler.getOp(m);
      boolean bool2;
      switch (n)
      {
      case 22: 
        return true;
      case 28: 
        break;
      case 19: 
      case 27: 
        return true;
      case 25: 
        bool2 = functionProximateOrContainsProximate(paramCompiler, m);
        if (bool2) {
          return true;
        }
        break;
      case 5: 
      case 6: 
      case 7: 
      case 8: 
      case 9: 
        int i1 = OpMap.getFirstChildPos(m);
        int i2 = paramCompiler.getNextOpPos(i1);
        bool2 = isProximateInnerExpr(paramCompiler, i1);
        if (bool2) {
          return true;
        }
        bool2 = isProximateInnerExpr(paramCompiler, i2);
        if (bool2) {
          return true;
        }
        break;
      case 10: 
      case 11: 
      case 12: 
      case 13: 
      case 14: 
      case 15: 
      case 16: 
      case 17: 
      case 18: 
      case 20: 
      case 21: 
      case 23: 
      case 24: 
      case 26: 
      default: 
        return true;
      }
      j = paramCompiler.getNextOpPos(j);
    }
    return bool1;
  }
  
  private static boolean isOptimizableForDescendantIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 1033;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1)))
    {
      if ((i1 != 1033) && (i1 != 35)) {
        return false;
      }
      j++;
      if (j > 3) {
        return false;
      }
      boolean bool = mightBeProximate(paramCompiler, paramInt1, i);
      if (bool) {
        return false;
      }
      switch (i)
      {
      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 37: 
      case 38: 
      case 39: 
      case 43: 
      case 44: 
      case 45: 
      case 46: 
      case 47: 
      case 49: 
      case 51: 
      case 52: 
      case 53: 
        return false;
      case 50: 
        if (1 != j) {
          return false;
        }
        break;
      case 40: 
        if ((n == 0) && ((k == 0) || (m == 0))) {
          return false;
        }
        break;
      case 42: 
        n = 1;
      case 41: 
        if (3 == j) {
          return false;
        }
        k = 1;
        break;
      case 48: 
        if (1 != j) {
          return false;
        }
        m = 1;
        break;
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      case 30: 
      case 31: 
      case 32: 
      case 33: 
      case 34: 
      case 35: 
      case 36: 
      default: 
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      }
      i1 = paramCompiler.getStepTestType(paramInt1);
      int i2 = paramCompiler.getNextStepPos(paramInt1);
      if (i2 < 0) {
        break;
      }
      if ((-1 != paramCompiler.getOp(i2)) && (paramCompiler.countPredicates(paramInt1) > 0)) {
        return false;
      }
      paramInt1 = i2;
    }
    return true;
  }
  
  private static int analyze(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    int j = 0;
    int k = 0;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1)))
    {
      j++;
      boolean bool = analyzePredicate(paramCompiler, paramInt1, i);
      if (bool) {
        k |= 0x1000;
      }
      switch (i)
      {
      case 22: 
      case 23: 
      case 24: 
      case 25: 
        k |= 0x4000000;
        break;
      case 50: 
        k |= 0x8000000;
        break;
      case 37: 
        k |= 0x2000;
        break;
      case 38: 
        k |= 0x4000;
        break;
      case 39: 
        k |= 0x8000;
        break;
      case 49: 
        k |= 0x200000;
        break;
      case 40: 
        k |= 0x10000;
        break;
      case 41: 
        k |= 0x20000;
        break;
      case 42: 
        if ((2 == j) && (134217728 == k)) {
          k |= 0x20000000;
        }
        k |= 0x40000;
        break;
      case 43: 
        k |= 0x80000;
        break;
      case 44: 
        k |= 0x100000;
        break;
      case 46: 
        k |= 0x800000;
        break;
      case 47: 
        k |= 0x1000000;
        break;
      case 45: 
        k |= 0x400000;
        break;
      case 48: 
        k |= 0x2000000;
        break;
      case 51: 
        k |= 0x80008000;
        break;
      case 52: 
        k |= 0x80002000;
        break;
      case 53: 
        k |= 0x80400000;
        break;
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      case 30: 
      case 31: 
      case 32: 
      case 33: 
      case 34: 
      case 35: 
      case 36: 
      default: 
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      }
      if (1033 == paramCompiler.getOp(paramInt1 + 3)) {
        k |= 0x40000000;
      }
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0) {
        break;
      }
    }
    k |= j & 0xFF;
    return k;
  }
  
  public static boolean isDownwardAxisOfMany(int paramInt)
  {
    return (5 == paramInt) || (4 == paramInt) || (6 == paramInt) || (11 == paramInt);
  }
  
  static StepPattern loadSteps(MatchPatternIterator paramMatchPatternIterator, Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    StepPattern localStepPattern1 = null;
    StepPattern localStepPattern2 = null;
    StepPattern localStepPattern3 = null;
    int j = analyze(paramCompiler, paramInt1, paramInt2);
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1)))
    {
      localStepPattern1 = createDefaultStepPattern(paramCompiler, paramInt1, paramMatchPatternIterator, j, localStepPattern2, localStepPattern3);
      if (null == localStepPattern2) {
        localStepPattern2 = localStepPattern1;
      } else {
        localStepPattern1.setRelativePathPattern(localStepPattern3);
      }
      localStepPattern3 = localStepPattern1;
      paramInt1 = paramCompiler.getNextStepPos(paramInt1);
      if (paramInt1 < 0) {
        break;
      }
    }
    int k = 13;
    int m = 13;
    Object localObject1 = localStepPattern1;
    for (Object localObject2 = localStepPattern1; null != localObject2; localObject2 = ((StepPattern)localObject2).getRelativePathPattern())
    {
      int n = ((StepPattern)localObject2).getAxis();
      ((StepPattern)localObject2).setAxis(k);
      int i1 = ((StepPattern)localObject2).getWhatToShow();
      if ((i1 == 2) || (i1 == 4096))
      {
        int i2 = i1 == 2 ? 2 : 9;
        if (isDownwardAxisOfMany(k))
        {
          StepPattern localStepPattern4 = new StepPattern(i1, ((StepPattern)localObject2).getNamespace(), ((StepPattern)localObject2).getLocalName(), i2, 0);
          XNumber localXNumber2 = ((StepPattern)localObject2).getStaticScore();
          ((StepPattern)localObject2).setNamespace(null);
          ((StepPattern)localObject2).setLocalName("*");
          localStepPattern4.setPredicates(((StepPattern)localObject2).getPredicates());
          ((StepPattern)localObject2).setPredicates(null);
          ((StepPattern)localObject2).setWhatToShow(1);
          StepPattern localStepPattern5 = ((StepPattern)localObject2).getRelativePathPattern();
          ((StepPattern)localObject2).setRelativePathPattern(localStepPattern4);
          localStepPattern4.setRelativePathPattern(localStepPattern5);
          localStepPattern4.setStaticScore(localXNumber2);
          if (11 == ((StepPattern)localObject2).getAxis()) {
            ((StepPattern)localObject2).setAxis(15);
          } else if (4 == ((StepPattern)localObject2).getAxis()) {
            ((StepPattern)localObject2).setAxis(5);
          }
          localObject2 = localStepPattern4;
        }
        else if (3 == ((StepPattern)localObject2).getAxis())
        {
          ((StepPattern)localObject2).setAxis(2);
        }
      }
      k = n;
      localObject1 = localObject2;
    }
    if (k < 16)
    {
      localObject2 = new ContextMatchStepPattern(k, m);
      XNumber localXNumber1 = ((StepPattern)localObject1).getStaticScore();
      ((StepPattern)localObject1).setRelativePathPattern((StepPattern)localObject2);
      ((StepPattern)localObject1).setStaticScore(localXNumber1);
      ((StepPattern)localObject2).setStaticScore(localXNumber1);
    }
    return localStepPattern1;
  }
  
  private static StepPattern createDefaultStepPattern(Compiler paramCompiler, int paramInt1, MatchPatternIterator paramMatchPatternIterator, int paramInt2, StepPattern paramStepPattern1, StepPattern paramStepPattern2)
    throws TransformerException
  {
    int i = paramCompiler.getOp(paramInt1);
    int j = 0;
    int k = 1;
    int m = paramCompiler.getWhatToShow(paramInt1);
    Object localObject = null;
    int n;
    int i1;
    switch (i)
    {
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      k = 0;
      Expression localExpression;
      switch (i)
      {
      case 22: 
      case 23: 
      case 24: 
      case 25: 
        localExpression = paramCompiler.compile(paramInt1);
        break;
      default: 
        localExpression = paramCompiler.compile(paramInt1 + 2);
      }
      n = 20;
      i1 = 20;
      localObject = new FunctionPattern(localExpression, n, i1);
      j = 1;
      break;
    case 50: 
      m = 1280;
      n = 19;
      i1 = 19;
      localObject = new StepPattern(1280, n, i1);
      break;
    case 39: 
      m = 2;
      n = 10;
      i1 = 2;
      break;
    case 49: 
      m = 4096;
      n = 10;
      i1 = 9;
      break;
    case 37: 
      n = 4;
      i1 = 0;
      break;
    case 40: 
      n = 10;
      i1 = 3;
      break;
    case 38: 
      n = 5;
      i1 = 1;
      break;
    case 48: 
      n = 13;
      i1 = 13;
      break;
    case 45: 
      n = 3;
      i1 = 10;
      break;
    case 47: 
      n = 7;
      i1 = 12;
      break;
    case 46: 
      n = 6;
      i1 = 11;
      break;
    case 44: 
      n = 12;
      i1 = 7;
      break;
    case 43: 
      n = 11;
      i1 = 6;
      break;
    case 42: 
      n = 1;
      i1 = 5;
      break;
    case 41: 
      n = 0;
      i1 = 4;
      break;
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    default: 
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
    }
    if (null == localObject)
    {
      m = paramCompiler.getWhatToShow(paramInt1);
      localObject = new StepPattern(m, paramCompiler.getStepNS(paramInt1), paramCompiler.getStepLocalName(paramInt1), n, i1);
    }
    int i2 = paramCompiler.getFirstPredicateOpPos(paramInt1);
    ((StepPattern)localObject).setPredicates(paramCompiler.getCompiledPredicates(i2));
    return localObject;
  }
  
  static boolean analyzePredicate(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    int i;
    switch (paramInt2)
    {
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      i = paramCompiler.getArgLength(paramInt1);
      break;
    default: 
      i = paramCompiler.getArgLengthOfStep(paramInt1);
    }
    int j = paramCompiler.getFirstPredicateOpPos(paramInt1);
    int k = paramCompiler.countPredicates(j);
    return k > 0;
  }
  
  private static AxesWalker createDefaultWalker(Compiler paramCompiler, int paramInt1, WalkingIterator paramWalkingIterator, int paramInt2)
  {
    Object localObject = null;
    int i = paramCompiler.getOp(paramInt1);
    int j = 0;
    int k = paramInt2 & 0xFF;
    int m = 1;
    switch (i)
    {
    case 22: 
    case 23: 
    case 24: 
    case 25: 
      m = 0;
      localObject = new FilterExprWalker(paramWalkingIterator);
      j = 1;
      break;
    case 50: 
      localObject = new AxesWalker(paramWalkingIterator, 19);
      break;
    case 37: 
      m = 0;
      localObject = new ReverseAxesWalker(paramWalkingIterator, 0);
      break;
    case 38: 
      m = 0;
      localObject = new ReverseAxesWalker(paramWalkingIterator, 1);
      break;
    case 39: 
      localObject = new AxesWalker(paramWalkingIterator, 2);
      break;
    case 49: 
      localObject = new AxesWalker(paramWalkingIterator, 9);
      break;
    case 40: 
      localObject = new AxesWalker(paramWalkingIterator, 3);
      break;
    case 41: 
      m = 0;
      localObject = new AxesWalker(paramWalkingIterator, 4);
      break;
    case 42: 
      m = 0;
      localObject = new AxesWalker(paramWalkingIterator, 5);
      break;
    case 43: 
      m = 0;
      localObject = new AxesWalker(paramWalkingIterator, 6);
      break;
    case 44: 
      m = 0;
      localObject = new AxesWalker(paramWalkingIterator, 7);
      break;
    case 46: 
      m = 0;
      localObject = new ReverseAxesWalker(paramWalkingIterator, 11);
      break;
    case 47: 
      m = 0;
      localObject = new ReverseAxesWalker(paramWalkingIterator, 12);
      break;
    case 45: 
      m = 0;
      localObject = new ReverseAxesWalker(paramWalkingIterator, 10);
      break;
    case 48: 
      localObject = new AxesWalker(paramWalkingIterator, 13);
      break;
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    default: 
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
    }
    if (j != 0)
    {
      ((AxesWalker)localObject).initNodeTest(-1);
    }
    else
    {
      int n = paramCompiler.getWhatToShow(paramInt1);
      if ((0 == (n & 0x1043)) || (n == -1)) {
        ((AxesWalker)localObject).initNodeTest(n);
      } else {
        ((AxesWalker)localObject).initNodeTest(n, paramCompiler.getStepNS(paramInt1), paramCompiler.getStepLocalName(paramInt1));
      }
    }
    return localObject;
  }
  
  public static String getAnalysisString(int paramInt)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("count: ").append(getStepCount(paramInt)).append(' ');
    if ((paramInt & 0x40000000) != 0) {
      localStringBuffer.append("NTANY|");
    }
    if ((paramInt & 0x1000) != 0) {
      localStringBuffer.append("PRED|");
    }
    if ((paramInt & 0x2000) != 0) {
      localStringBuffer.append("ANC|");
    }
    if ((paramInt & 0x4000) != 0) {
      localStringBuffer.append("ANCOS|");
    }
    if ((paramInt & 0x8000) != 0) {
      localStringBuffer.append("ATTR|");
    }
    if ((paramInt & 0x10000) != 0) {
      localStringBuffer.append("CH|");
    }
    if ((paramInt & 0x20000) != 0) {
      localStringBuffer.append("DESC|");
    }
    if ((paramInt & 0x40000) != 0) {
      localStringBuffer.append("DESCOS|");
    }
    if ((paramInt & 0x80000) != 0) {
      localStringBuffer.append("FOL|");
    }
    if ((paramInt & 0x100000) != 0) {
      localStringBuffer.append("FOLS|");
    }
    if ((paramInt & 0x200000) != 0) {
      localStringBuffer.append("NS|");
    }
    if ((paramInt & 0x400000) != 0) {
      localStringBuffer.append("P|");
    }
    if ((paramInt & 0x800000) != 0) {
      localStringBuffer.append("PREC|");
    }
    if ((paramInt & 0x1000000) != 0) {
      localStringBuffer.append("PRECS|");
    }
    if ((paramInt & 0x2000000) != 0) {
      localStringBuffer.append(".|");
    }
    if ((paramInt & 0x4000000) != 0) {
      localStringBuffer.append("FLT|");
    }
    if ((paramInt & 0x8000000) != 0) {
      localStringBuffer.append("R|");
    }
    return localStringBuffer.toString();
  }
  
  public static boolean hasPredicate(int paramInt)
  {
    return 0 != (paramInt & 0x1000);
  }
  
  public static boolean isWild(int paramInt)
  {
    return 0 != (paramInt & 0x40000000);
  }
  
  public static boolean walksAncestors(int paramInt)
  {
    return isSet(paramInt, 24576);
  }
  
  public static boolean walksAttributes(int paramInt)
  {
    return 0 != (paramInt & 0x8000);
  }
  
  public static boolean walksNamespaces(int paramInt)
  {
    return 0 != (paramInt & 0x200000);
  }
  
  public static boolean walksChildren(int paramInt)
  {
    return 0 != (paramInt & 0x10000);
  }
  
  public static boolean walksDescendants(int paramInt)
  {
    return isSet(paramInt, 393216);
  }
  
  public static boolean walksSubtree(int paramInt)
  {
    return isSet(paramInt, 458752);
  }
  
  public static boolean walksSubtreeOnlyMaybeAbsolute(int paramInt)
  {
    return (walksSubtree(paramInt)) && (!walksExtraNodes(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt));
  }
  
  public static boolean walksSubtreeOnly(int paramInt)
  {
    return (walksSubtreeOnlyMaybeAbsolute(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean walksFilteredList(int paramInt)
  {
    return isSet(paramInt, 67108864);
  }
  
  public static boolean walksSubtreeOnlyFromRootOrContext(int paramInt)
  {
    return (walksSubtree(paramInt)) && (!walksExtraNodes(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isSet(paramInt, 67108864));
  }
  
  public static boolean walksInDocOrder(int paramInt)
  {
    return ((walksSubtreeOnlyMaybeAbsolute(paramInt)) || (walksExtraNodesOnly(paramInt)) || (walksFollowingOnlyMaybeAbsolute(paramInt))) && (!isSet(paramInt, 67108864));
  }
  
  public static boolean walksFollowingOnlyMaybeAbsolute(int paramInt)
  {
    return (isSet(paramInt, 35127296)) && (!walksSubtree(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt));
  }
  
  public static boolean walksUp(int paramInt)
  {
    return isSet(paramInt, 4218880);
  }
  
  public static boolean walksSideways(int paramInt)
  {
    return isSet(paramInt, 26738688);
  }
  
  public static boolean walksExtraNodes(int paramInt)
  {
    return isSet(paramInt, 2129920);
  }
  
  public static boolean walksExtraNodesOnly(int paramInt)
  {
    return (walksExtraNodes(paramInt)) && (!isSet(paramInt, 33554432)) && (!walksSubtree(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean isAbsolute(int paramInt)
  {
    return isSet(paramInt, 201326592);
  }
  
  public static boolean walksChildrenOnly(int paramInt)
  {
    return (walksChildren(paramInt)) && (!isSet(paramInt, 33554432)) && (!walksExtraNodes(paramInt)) && (!walksDescendants(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && ((!isAbsolute(paramInt)) || (isSet(paramInt, 134217728)));
  }
  
  public static boolean walksChildrenAndExtraAndSelfOnly(int paramInt)
  {
    return (walksChildren(paramInt)) && (!walksDescendants(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && ((!isAbsolute(paramInt)) || (isSet(paramInt, 134217728)));
  }
  
  public static boolean walksDescendantsAndExtraAndSelfOnly(int paramInt)
  {
    return (!walksChildren(paramInt)) && (walksDescendants(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && ((!isAbsolute(paramInt)) || (isSet(paramInt, 134217728)));
  }
  
  public static boolean walksSelfOnly(int paramInt)
  {
    return (isSet(paramInt, 33554432)) && (!walksSubtree(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean walksUpOnly(int paramInt)
  {
    return (!walksSubtree(paramInt)) && (walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean walksDownOnly(int paramInt)
  {
    return (walksSubtree(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean walksDownExtraOnly(int paramInt)
  {
    return (walksSubtree(paramInt)) && (walksExtraNodes(paramInt)) && (!walksUp(paramInt)) && (!walksSideways(paramInt)) && (!isAbsolute(paramInt));
  }
  
  public static boolean canSkipSubtrees(int paramInt)
  {
    return isSet(paramInt, 65536) | walksSideways(paramInt);
  }
  
  public static boolean canCrissCross(int paramInt)
  {
    if (walksSelfOnly(paramInt)) {
      return false;
    }
    if ((walksDownOnly(paramInt)) && (!canSkipSubtrees(paramInt))) {
      return false;
    }
    if (walksChildrenAndExtraAndSelfOnly(paramInt)) {
      return false;
    }
    if (walksDescendantsAndExtraAndSelfOnly(paramInt)) {
      return false;
    }
    if (walksUpOnly(paramInt)) {
      return false;
    }
    if (walksExtraNodesOnly(paramInt)) {
      return false;
    }
    return (walksSubtree(paramInt)) && ((walksSideways(paramInt)) || (walksUp(paramInt)) || (canSkipSubtrees(paramInt)));
  }
  
  public static boolean isNaturalDocOrder(int paramInt)
  {
    if ((canCrissCross(paramInt)) || (isSet(paramInt, 2097152)) || (walksFilteredList(paramInt))) {
      return false;
    }
    return walksInDocOrder(paramInt);
  }
  
  private static boolean isNaturalDocOrder(Compiler paramCompiler, int paramInt1, int paramInt2, int paramInt3)
    throws TransformerException
  {
    if (canCrissCross(paramInt3)) {
      return false;
    }
    if (isSet(paramInt3, 2097152)) {
      return false;
    }
    if ((isSet(paramInt3, 1572864)) && (isSet(paramInt3, 25165824))) {
      return false;
    }
    int j = 0;
    int k = 0;
    int m = 0;
    int i;
    while (-1 != (i = paramCompiler.getOp(paramInt1)))
    {
      j++;
      switch (i)
      {
      case 39: 
      case 51: 
        if (k != 0) {
          return false;
        }
        String str = paramCompiler.getStepLocalName(paramInt1);
        if (str.equals("*")) {
          k = 1;
        }
        break;
      case 22: 
      case 23: 
      case 24: 
      case 25: 
      case 37: 
      case 38: 
      case 41: 
      case 42: 
      case 43: 
      case 44: 
      case 45: 
      case 46: 
      case 47: 
      case 49: 
      case 52: 
      case 53: 
        if (m > 0) {
          return false;
        }
        m++;
      case 40: 
      case 48: 
      case 50: 
        if (k != 0) {
          return false;
        }
        break;
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      case 30: 
      case 31: 
      case 32: 
      case 33: 
      case 34: 
      case 35: 
      case 36: 
      default: 
        throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NULL_ERROR_HANDLER", new Object[] { Integer.toString(i) }));
      }
      int n = paramCompiler.getNextStepPos(paramInt1);
      if (n < 0) {
        break;
      }
      paramInt1 = n;
    }
    return true;
  }
  
  public static boolean isOneStep(int paramInt)
  {
    return (paramInt & 0xFF) == 1;
  }
  
  public static int getStepCount(int paramInt)
  {
    return paramInt & 0xFF;
  }
}
