package com.sun.jmx.mbeanserver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.management.NotCompliantMBeanException;

class MBeanAnalyzer<M>
{
  private Map<String, List<M>> opMap = Util.newInsertionOrderMap();
  private Map<String, AttrMethods<M>> attrMap = Util.newInsertionOrderMap();
  
  void visit(MBeanVisitor<M> paramMBeanVisitor)
  {
    Iterator localIterator = this.attrMap.entrySet().iterator();
    Map.Entry localEntry;
    Object localObject1;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      localObject1 = (String)localEntry.getKey();
      localObject2 = (AttrMethods)localEntry.getValue();
      paramMBeanVisitor.visitAttribute((String)localObject1, ((AttrMethods)localObject2).getter, ((AttrMethods)localObject2).setter);
    }
    localIterator = this.opMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localEntry = (Map.Entry)localIterator.next();
      localObject1 = ((List)localEntry.getValue()).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = ((Iterator)localObject1).next();
        paramMBeanVisitor.visitOperation((String)localEntry.getKey(), localObject2);
      }
    }
  }
  
  static <M> MBeanAnalyzer<M> analyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector)
    throws NotCompliantMBeanException
  {
    return new MBeanAnalyzer(paramClass, paramMBeanIntrospector);
  }
  
  private MBeanAnalyzer(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector)
    throws NotCompliantMBeanException
  {
    if (!paramClass.isInterface()) {
      throw new NotCompliantMBeanException("Not an interface: " + paramClass.getName());
    }
    if ((!Modifier.isPublic(paramClass.getModifiers())) && (!Introspector.ALLOW_NONPUBLIC_MBEAN)) {
      throw new NotCompliantMBeanException("Interface is not public: " + paramClass.getName());
    }
    try
    {
      initMaps(paramClass, paramMBeanIntrospector);
    }
    catch (Exception localException)
    {
      throw Introspector.throwException(paramClass, localException);
    }
  }
  
  private void initMaps(Class<?> paramClass, MBeanIntrospector<M> paramMBeanIntrospector)
    throws Exception
  {
    List localList1 = paramMBeanIntrospector.getMethods(paramClass);
    List localList2 = eliminateCovariantMethods(localList1);
    Iterator localIterator = localList2.iterator();
    Object localObject1;
    Object localObject2;
    while (localIterator.hasNext())
    {
      localObject1 = (Method)localIterator.next();
      localObject2 = ((Method)localObject1).getName();
      int i = ((Method)localObject1).getParameterTypes().length;
      Object localObject3 = paramMBeanIntrospector.mFrom((Method)localObject1);
      String str2 = "";
      if (((String)localObject2).startsWith("get")) {
        str2 = ((String)localObject2).substring(3);
      } else if ((((String)localObject2).startsWith("is")) && (((Method)localObject1).getReturnType() == Boolean.TYPE)) {
        str2 = ((String)localObject2).substring(2);
      }
      Object localObject4;
      String str3;
      if ((str2.length() != 0) && (i == 0) && (((Method)localObject1).getReturnType() != Void.TYPE))
      {
        localObject4 = (AttrMethods)this.attrMap.get(str2);
        if (localObject4 == null)
        {
          localObject4 = new AttrMethods(null);
        }
        else if (((AttrMethods)localObject4).getter != null)
        {
          str3 = "Attribute " + str2 + " has more than one getter";
          throw new NotCompliantMBeanException(str3);
        }
        ((AttrMethods)localObject4).getter = localObject3;
        this.attrMap.put(str2, localObject4);
      }
      else if ((((String)localObject2).startsWith("set")) && (((String)localObject2).length() > 3) && (i == 1) && (((Method)localObject1).getReturnType() == Void.TYPE))
      {
        str2 = ((String)localObject2).substring(3);
        localObject4 = (AttrMethods)this.attrMap.get(str2);
        if (localObject4 == null)
        {
          localObject4 = new AttrMethods(null);
        }
        else if (((AttrMethods)localObject4).setter != null)
        {
          str3 = "Attribute " + str2 + " has more than one setter";
          throw new NotCompliantMBeanException(str3);
        }
        ((AttrMethods)localObject4).setter = localObject3;
        this.attrMap.put(str2, localObject4);
      }
      else
      {
        localObject4 = (List)this.opMap.get(localObject2);
        if (localObject4 == null) {
          localObject4 = Util.newList();
        }
        ((List)localObject4).add(localObject3);
        this.opMap.put(localObject2, localObject4);
      }
    }
    localIterator = this.attrMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = (AttrMethods)((Map.Entry)localObject1).getValue();
      if (!paramMBeanIntrospector.consistent(((AttrMethods)localObject2).getter, ((AttrMethods)localObject2).setter))
      {
        String str1 = "Getter and setter for " + (String)((Map.Entry)localObject1).getKey() + " have inconsistent types";
        throw new NotCompliantMBeanException(str1);
      }
    }
  }
  
  static List<Method> eliminateCovariantMethods(List<Method> paramList)
  {
    int i = paramList.size();
    Method[] arrayOfMethod = (Method[])paramList.toArray(new Method[i]);
    Arrays.sort(arrayOfMethod, MethodOrder.instance);
    Set localSet = Util.newSet();
    for (int j = 1; j < i; j++)
    {
      Method localMethod1 = arrayOfMethod[(j - 1)];
      Method localMethod2 = arrayOfMethod[j];
      if ((localMethod1.getName().equals(localMethod2.getName())) && (Arrays.equals(localMethod1.getParameterTypes(), localMethod2.getParameterTypes())) && (!localSet.add(localMethod1))) {
        throw new RuntimeException("Internal error: duplicate Method");
      }
    }
    List localList = Util.newList(paramList);
    localList.removeAll(localSet);
    return localList;
  }
  
  private static class AttrMethods<M>
  {
    M getter;
    M setter;
    
    private AttrMethods() {}
  }
  
  static abstract interface MBeanVisitor<M>
  {
    public abstract void visitAttribute(String paramString, M paramM1, M paramM2);
    
    public abstract void visitOperation(String paramString, M paramM);
  }
  
  private static class MethodOrder
    implements Comparator<Method>
  {
    public static final MethodOrder instance = new MethodOrder();
    
    private MethodOrder() {}
    
    public int compare(Method paramMethod1, Method paramMethod2)
    {
      int i = paramMethod1.getName().compareTo(paramMethod2.getName());
      if (i != 0) {
        return i;
      }
      Class[] arrayOfClass1 = paramMethod1.getParameterTypes();
      Class[] arrayOfClass2 = paramMethod2.getParameterTypes();
      if (arrayOfClass1.length != arrayOfClass2.length) {
        return arrayOfClass1.length - arrayOfClass2.length;
      }
      if (!Arrays.equals(arrayOfClass1, arrayOfClass2)) {
        return Arrays.toString(arrayOfClass1).compareTo(Arrays.toString(arrayOfClass2));
      }
      Class localClass1 = paramMethod1.getReturnType();
      Class localClass2 = paramMethod2.getReturnType();
      if (localClass1 == localClass2) {
        return 0;
      }
      if (localClass1.isAssignableFrom(localClass2)) {
        return -1;
      }
      return 1;
    }
  }
}
