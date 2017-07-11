package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactoryFactory;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.rmi.CORBA.Tie;

public final class PresentationManagerImpl
  implements PresentationManager
{
  private Map classToClassData;
  private Map methodToDMM;
  private PresentationManager.StubFactoryFactory staticStubFactoryFactory;
  private PresentationManager.StubFactoryFactory dynamicStubFactoryFactory;
  private ORBUtilSystemException wrapper = null;
  private boolean useDynamicStubs;
  
  public PresentationManagerImpl(boolean paramBoolean)
  {
    this.useDynamicStubs = paramBoolean;
    this.wrapper = ORBUtilSystemException.get("rpc.presentation");
    this.classToClassData = new HashMap();
    this.methodToDMM = new HashMap();
  }
  
  public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller(Method paramMethod)
  {
    if (paramMethod == null) {
      return null;
    }
    Object localObject = (DynamicMethodMarshaller)this.methodToDMM.get(paramMethod);
    if (localObject == null)
    {
      localObject = new DynamicMethodMarshallerImpl(paramMethod);
      this.methodToDMM.put(paramMethod, localObject);
    }
    return localObject;
  }
  
  public synchronized PresentationManager.ClassData getClassData(Class paramClass)
  {
    Object localObject = (PresentationManager.ClassData)this.classToClassData.get(paramClass);
    if (localObject == null)
    {
      localObject = new ClassDataImpl(paramClass);
      this.classToClassData.put(paramClass, localObject);
    }
    return localObject;
  }
  
  public PresentationManager.StubFactoryFactory getStubFactoryFactory(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.dynamicStubFactoryFactory;
    }
    return this.staticStubFactoryFactory;
  }
  
  public void setStubFactoryFactory(boolean paramBoolean, PresentationManager.StubFactoryFactory paramStubFactoryFactory)
  {
    if (paramBoolean) {
      this.dynamicStubFactoryFactory = paramStubFactoryFactory;
    } else {
      this.staticStubFactoryFactory = paramStubFactoryFactory;
    }
  }
  
  public Tie getTie()
  {
    return this.dynamicStubFactoryFactory.getTie(null);
  }
  
  public boolean useDynamicStubs()
  {
    return this.useDynamicStubs;
  }
  
  private Set getRootSet(Class paramClass, NodeImpl paramNodeImpl, Graph paramGraph)
  {
    Set localSet = null;
    if (paramClass.isInterface())
    {
      paramGraph.add(paramNodeImpl);
      localSet = paramGraph.getRoots();
    }
    else
    {
      Class localClass = paramClass;
      HashSet localHashSet = new HashSet();
      while ((localClass != null) && (!localClass.equals(Object.class)))
      {
        NodeImpl localNodeImpl = new NodeImpl(localClass);
        paramGraph.add(localNodeImpl);
        localHashSet.add(localNodeImpl);
        localClass = localClass.getSuperclass();
      }
      paramGraph.getRoots();
      paramGraph.removeAll(localHashSet);
      localSet = paramGraph.getRoots();
    }
    return localSet;
  }
  
  private Class[] getInterfaces(Set paramSet)
  {
    Class[] arrayOfClass = new Class[paramSet.size()];
    Iterator localIterator = paramSet.iterator();
    int i = 0;
    while (localIterator.hasNext())
    {
      NodeImpl localNodeImpl = (NodeImpl)localIterator.next();
      arrayOfClass[(i++)] = localNodeImpl.getInterface();
    }
    return arrayOfClass;
  }
  
  private String[] makeTypeIds(NodeImpl paramNodeImpl, Graph paramGraph, Set paramSet)
  {
    HashSet localHashSet = new HashSet(paramGraph);
    localHashSet.removeAll(paramSet);
    ArrayList localArrayList = new ArrayList();
    if (paramSet.size() > 1) {
      localArrayList.add(paramNodeImpl.getTypeId());
    }
    addNodes(localArrayList, paramSet);
    addNodes(localArrayList, localHashSet);
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private void addNodes(List paramList, Set paramSet)
  {
    Iterator localIterator = paramSet.iterator();
    while (localIterator.hasNext())
    {
      NodeImpl localNodeImpl = (NodeImpl)localIterator.next();
      String str = localNodeImpl.getTypeId();
      paramList.add(str);
    }
  }
  
  private class ClassDataImpl
    implements PresentationManager.ClassData
  {
    private Class cls;
    private IDLNameTranslator nameTranslator;
    private String[] typeIds;
    private PresentationManager.StubFactory sfactory;
    private InvocationHandlerFactory ihfactory;
    private Map dictionary;
    
    public ClassDataImpl(Class paramClass)
    {
      this.cls = paramClass;
      GraphImpl localGraphImpl = new GraphImpl();
      PresentationManagerImpl.NodeImpl localNodeImpl = new PresentationManagerImpl.NodeImpl(paramClass);
      Set localSet = PresentationManagerImpl.this.getRootSet(paramClass, localNodeImpl, localGraphImpl);
      Class[] arrayOfClass = PresentationManagerImpl.this.getInterfaces(localSet);
      this.nameTranslator = IDLNameTranslatorImpl.get(arrayOfClass);
      this.typeIds = PresentationManagerImpl.this.makeTypeIds(localNodeImpl, localGraphImpl, localSet);
      this.ihfactory = new InvocationHandlerFactoryImpl(PresentationManagerImpl.this, this);
      this.dictionary = new HashMap();
    }
    
    public Class getMyClass()
    {
      return this.cls;
    }
    
    public IDLNameTranslator getIDLNameTranslator()
    {
      return this.nameTranslator;
    }
    
    public String[] getTypeIds()
    {
      return this.typeIds;
    }
    
    public InvocationHandlerFactory getInvocationHandlerFactory()
    {
      return this.ihfactory;
    }
    
    public Map getDictionary()
    {
      return this.dictionary;
    }
  }
  
  private static class NodeImpl
    implements Node
  {
    private Class interf;
    
    public Class getInterface()
    {
      return this.interf;
    }
    
    public NodeImpl(Class paramClass)
    {
      this.interf = paramClass;
    }
    
    public String getTypeId()
    {
      return "RMI:" + this.interf.getName() + ":0000000000000000";
    }
    
    public Set getChildren()
    {
      HashSet localHashSet = new HashSet();
      Class[] arrayOfClass = this.interf.getInterfaces();
      for (int i = 0; i < arrayOfClass.length; i++)
      {
        Class localClass = arrayOfClass[i];
        if ((Remote.class.isAssignableFrom(localClass)) && (!Remote.class.equals(localClass))) {
          localHashSet.add(new NodeImpl(localClass));
        }
      }
      return localHashSet;
    }
    
    public String toString()
    {
      return "NodeImpl[" + this.interf + "]";
    }
    
    public int hashCode()
    {
      return this.interf.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof NodeImpl)) {
        return false;
      }
      NodeImpl localNodeImpl = (NodeImpl)paramObject;
      return localNodeImpl.interf.equals(this.interf);
    }
  }
}
