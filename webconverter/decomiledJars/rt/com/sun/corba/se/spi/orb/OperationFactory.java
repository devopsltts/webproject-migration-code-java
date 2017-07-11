package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringTokenizer;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public abstract class OperationFactory
{
  private static Operation suffixActionImpl = new SuffixAction(null);
  private static Operation valueActionImpl = new ValueAction(null);
  private static Operation identityActionImpl = new IdentityAction(null);
  private static Operation booleanActionImpl = new BooleanAction(null);
  private static Operation integerActionImpl = new IntegerAction(null);
  private static Operation stringActionImpl = new StringAction(null);
  private static Operation classActionImpl = new ClassAction(null);
  private static Operation setFlagActionImpl = new SetFlagAction(null);
  private static Operation URLActionImpl = new URLAction(null);
  private static Operation convertIntegerToShortImpl = new ConvertIntegerToShort(null);
  
  private OperationFactory() {}
  
  private static String getString(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return (String)paramObject;
    }
    throw new Error("String expected");
  }
  
  private static Object[] getObjectArray(Object paramObject)
  {
    if ((paramObject instanceof Object[])) {
      return (Object[])paramObject;
    }
    throw new Error("Object[] expected");
  }
  
  private static StringPair getStringPair(Object paramObject)
  {
    if ((paramObject instanceof StringPair)) {
      return (StringPair)paramObject;
    }
    throw new Error("StringPair expected");
  }
  
  public static Operation maskErrorAction(Operation paramOperation)
  {
    return new MaskErrorAction(paramOperation);
  }
  
  public static Operation indexAction(int paramInt)
  {
    return new IndexAction(paramInt);
  }
  
  public static Operation identityAction()
  {
    return identityActionImpl;
  }
  
  public static Operation suffixAction()
  {
    return suffixActionImpl;
  }
  
  public static Operation valueAction()
  {
    return valueActionImpl;
  }
  
  public static Operation booleanAction()
  {
    return booleanActionImpl;
  }
  
  public static Operation integerAction()
  {
    return integerActionImpl;
  }
  
  public static Operation stringAction()
  {
    return stringActionImpl;
  }
  
  public static Operation classAction()
  {
    return classActionImpl;
  }
  
  public static Operation setFlagAction()
  {
    return setFlagActionImpl;
  }
  
  public static Operation URLAction()
  {
    return URLActionImpl;
  }
  
  public static Operation integerRangeAction(int paramInt1, int paramInt2)
  {
    return new IntegerRangeAction(paramInt1, paramInt2);
  }
  
  public static Operation listAction(String paramString, Operation paramOperation)
  {
    return new ListAction(paramString, paramOperation);
  }
  
  public static Operation sequenceAction(String paramString, Operation[] paramArrayOfOperation)
  {
    return new SequenceAction(paramString, paramArrayOfOperation);
  }
  
  public static Operation compose(Operation paramOperation1, Operation paramOperation2)
  {
    return new ComposeAction(paramOperation1, paramOperation2);
  }
  
  public static Operation mapAction(Operation paramOperation)
  {
    return new MapAction(paramOperation);
  }
  
  public static Operation mapSequenceAction(Operation[] paramArrayOfOperation)
  {
    return new MapSequenceAction(paramArrayOfOperation);
  }
  
  public static Operation convertIntegerToShort()
  {
    return convertIntegerToShortImpl;
  }
  
  private static class BooleanAction
    extends OperationFactory.OperationBase
  {
    private BooleanAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return new Boolean(OperationFactory.getString(paramObject));
    }
    
    public String toString()
    {
      return "booleanAction";
    }
  }
  
  private static class ClassAction
    extends OperationFactory.OperationBase
  {
    private ClassAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      String str = OperationFactory.getString(paramObject);
      try
      {
        Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str);
        return localClass;
      }
      catch (Exception localException)
      {
        ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
        throw localORBUtilSystemException.couldNotLoadClass(localException, str);
      }
    }
    
    public String toString()
    {
      return "classAction";
    }
  }
  
  private static class ComposeAction
    extends OperationFactory.OperationBase
  {
    private Operation op1;
    private Operation op2;
    
    ComposeAction(Operation paramOperation1, Operation paramOperation2)
    {
      super();
      this.op1 = paramOperation1;
      this.op2 = paramOperation2;
    }
    
    public Object operate(Object paramObject)
    {
      return this.op2.operate(this.op1.operate(paramObject));
    }
    
    public String toString()
    {
      return "composition(" + this.op1 + "," + this.op2 + ")";
    }
  }
  
  private static class ConvertIntegerToShort
    extends OperationFactory.OperationBase
  {
    private ConvertIntegerToShort()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      Integer localInteger = (Integer)paramObject;
      return new Short(localInteger.shortValue());
    }
    
    public String toString()
    {
      return "ConvertIntegerToShort";
    }
  }
  
  private static class IdentityAction
    extends OperationFactory.OperationBase
  {
    private IdentityAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return paramObject;
    }
    
    public String toString()
    {
      return "identityAction";
    }
  }
  
  private static class IndexAction
    extends OperationFactory.OperationBase
  {
    private int index;
    
    public IndexAction(int paramInt)
    {
      super();
      this.index = paramInt;
    }
    
    public Object operate(Object paramObject)
    {
      return OperationFactory.getObjectArray(paramObject)[this.index];
    }
    
    public String toString()
    {
      return "indexAction(" + this.index + ")";
    }
  }
  
  private static class IntegerAction
    extends OperationFactory.OperationBase
  {
    private IntegerAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return new Integer(OperationFactory.getString(paramObject));
    }
    
    public String toString()
    {
      return "integerAction";
    }
  }
  
  private static class IntegerRangeAction
    extends OperationFactory.OperationBase
  {
    private int min;
    private int max;
    
    IntegerRangeAction(int paramInt1, int paramInt2)
    {
      super();
      this.min = paramInt1;
      this.max = paramInt2;
    }
    
    public Object operate(Object paramObject)
    {
      int i = Integer.parseInt(OperationFactory.getString(paramObject));
      if ((i >= this.min) && (i <= this.max)) {
        return new Integer(i);
      }
      throw new IllegalArgumentException("Property value " + i + " is not in the range " + this.min + " to " + this.max);
    }
    
    public String toString()
    {
      return "integerRangeAction(" + this.min + "," + this.max + ")";
    }
  }
  
  private static class ListAction
    extends OperationFactory.OperationBase
  {
    private String sep;
    private Operation act;
    
    ListAction(String paramString, Operation paramOperation)
    {
      super();
      this.sep = paramString;
      this.act = paramOperation;
    }
    
    public Object operate(Object paramObject)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(OperationFactory.getString(paramObject), this.sep);
      int i = localStringTokenizer.countTokens();
      Object localObject1 = null;
      int j = 0;
      while (localStringTokenizer.hasMoreTokens())
      {
        String str = localStringTokenizer.nextToken();
        Object localObject2 = this.act.operate(str);
        if (localObject1 == null) {
          localObject1 = Array.newInstance(localObject2.getClass(), i);
        }
        Array.set(localObject1, j++, localObject2);
      }
      return localObject1;
    }
    
    public String toString()
    {
      return "listAction(separator=\"" + this.sep + "\",action=" + this.act + ")";
    }
  }
  
  private static class MapAction
    extends OperationFactory.OperationBase
  {
    Operation op;
    
    MapAction(Operation paramOperation)
    {
      super();
      this.op = paramOperation;
    }
    
    public Object operate(Object paramObject)
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
      for (int i = 0; i < arrayOfObject1.length; i++) {
        arrayOfObject2[i] = this.op.operate(arrayOfObject1[i]);
      }
      return arrayOfObject2;
    }
    
    public String toString()
    {
      return "mapAction(" + this.op + ")";
    }
  }
  
  private static class MapSequenceAction
    extends OperationFactory.OperationBase
  {
    private Operation[] op;
    
    public MapSequenceAction(Operation[] paramArrayOfOperation)
    {
      super();
      this.op = paramArrayOfOperation;
    }
    
    public Object operate(Object paramObject)
    {
      Object[] arrayOfObject1 = (Object[])paramObject;
      Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
      for (int i = 0; i < arrayOfObject1.length; i++) {
        arrayOfObject2[i] = this.op[i].operate(arrayOfObject1[i]);
      }
      return arrayOfObject2;
    }
    
    public String toString()
    {
      return "mapSequenceAction(" + Arrays.toString(this.op) + ")";
    }
  }
  
  private static class MaskErrorAction
    extends OperationFactory.OperationBase
  {
    private Operation op;
    
    public MaskErrorAction(Operation paramOperation)
    {
      super();
      this.op = paramOperation;
    }
    
    public Object operate(Object paramObject)
    {
      try
      {
        return this.op.operate(paramObject);
      }
      catch (Exception localException) {}
      return null;
    }
    
    public String toString()
    {
      return "maskErrorAction(" + this.op + ")";
    }
  }
  
  private static abstract class OperationBase
    implements Operation
  {
    private OperationBase() {}
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof OperationBase)) {
        return false;
      }
      OperationBase localOperationBase = (OperationBase)paramObject;
      return toString().equals(localOperationBase.toString());
    }
    
    public int hashCode()
    {
      return toString().hashCode();
    }
  }
  
  private static class SequenceAction
    extends OperationFactory.OperationBase
  {
    private String sep;
    private Operation[] actions;
    
    SequenceAction(String paramString, Operation[] paramArrayOfOperation)
    {
      super();
      this.sep = paramString;
      this.actions = paramArrayOfOperation;
    }
    
    public Object operate(Object paramObject)
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(OperationFactory.getString(paramObject), this.sep);
      int i = localStringTokenizer.countTokens();
      if (i != this.actions.length) {
        throw new Error("Number of tokens and number of actions do not match");
      }
      int j = 0;
      Object[] arrayOfObject = new Object[i];
      while (localStringTokenizer.hasMoreTokens())
      {
        Operation localOperation = this.actions[j];
        String str = localStringTokenizer.nextToken();
        arrayOfObject[(j++)] = localOperation.operate(str);
      }
      return arrayOfObject;
    }
    
    public String toString()
    {
      return "sequenceAction(separator=\"" + this.sep + "\",actions=" + Arrays.toString(this.actions) + ")";
    }
  }
  
  private static class SetFlagAction
    extends OperationFactory.OperationBase
  {
    private SetFlagAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return Boolean.TRUE;
    }
    
    public String toString()
    {
      return "setFlagAction";
    }
  }
  
  private static class StringAction
    extends OperationFactory.OperationBase
  {
    private StringAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return paramObject;
    }
    
    public String toString()
    {
      return "stringAction";
    }
  }
  
  private static class SuffixAction
    extends OperationFactory.OperationBase
  {
    private SuffixAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return OperationFactory.getStringPair(paramObject).getFirst();
    }
    
    public String toString()
    {
      return "suffixAction";
    }
  }
  
  private static class URLAction
    extends OperationFactory.OperationBase
  {
    private URLAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      String str = (String)paramObject;
      try
      {
        return new URL(str);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        ORBUtilSystemException localORBUtilSystemException = ORBUtilSystemException.get("orb.lifecycle");
        throw localORBUtilSystemException.badUrl(localMalformedURLException, str);
      }
    }
    
    public String toString()
    {
      return "URLAction";
    }
  }
  
  private static class ValueAction
    extends OperationFactory.OperationBase
  {
    private ValueAction()
    {
      super();
    }
    
    public Object operate(Object paramObject)
    {
      return OperationFactory.getStringPair(paramObject).getSecond();
    }
    
    public String toString()
    {
      return "valueAction";
    }
  }
}
