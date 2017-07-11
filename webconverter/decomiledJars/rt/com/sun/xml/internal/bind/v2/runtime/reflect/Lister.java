package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Patcher;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public abstract class Lister<BeanT, PropT, ItemT, PackT>
{
  private static final Map<Class, WeakReference<Lister>> arrayListerCache;
  static final Map<Class, Lister> primitiveArrayListers;
  public static final Lister ERROR = new Lister()
  {
    public ListIterator iterator(Object paramAnonymousObject, XMLSerializer paramAnonymousXMLSerializer)
    {
      return Lister.EMPTY_ITERATOR;
    }
    
    public Object startPacking(Object paramAnonymousObject, Accessor paramAnonymousAccessor)
    {
      return null;
    }
    
    public void addToPack(Object paramAnonymousObject1, Object paramAnonymousObject2) {}
    
    public void endPacking(Object paramAnonymousObject1, Object paramAnonymousObject2, Accessor paramAnonymousAccessor) {}
    
    public void reset(Object paramAnonymousObject, Accessor paramAnonymousAccessor) {}
  };
  private static final ListIterator EMPTY_ITERATOR = new ListIterator()
  {
    public boolean hasNext()
    {
      return false;
    }
    
    public Object next()
    {
      throw new IllegalStateException();
    }
  };
  private static final Class[] COLLECTION_IMPL_CLASSES = { ArrayList.class, LinkedList.class, HashSet.class, TreeSet.class, Stack.class };
  
  protected Lister() {}
  
  public abstract ListIterator<ItemT> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer);
  
  public abstract PackT startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException;
  
  public abstract void addToPack(PackT paramPackT, ItemT paramItemT)
    throws AccessorException;
  
  public abstract void endPacking(PackT paramPackT, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException;
  
  public abstract void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    throws AccessorException;
  
  public static <BeanT, PropT, ItemT, PackT> Lister<BeanT, PropT, ItemT, PackT> create(Type paramType, ID paramID, Adapter<Type, Class> paramAdapter)
  {
    Class localClass = (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramType);
    Object localObject1;
    Object localObject2;
    if (localClass.isArray())
    {
      localObject1 = localClass.getComponentType();
      localObject2 = getArrayLister((Class)localObject1);
    }
    else if (Collection.class.isAssignableFrom(localClass))
    {
      Type localType = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(paramType, Collection.class);
      if ((localType instanceof ParameterizedType)) {
        localObject1 = (Class)Utils.REFLECTION_NAVIGATOR.erasure(((ParameterizedType)localType).getActualTypeArguments()[0]);
      } else {
        localObject1 = Object.class;
      }
      localObject2 = new CollectionLister(getImplClass(localClass));
    }
    else
    {
      return null;
    }
    if (paramID == ID.IDREF) {
      localObject2 = new IDREFS((Lister)localObject2, (Class)localObject1);
    }
    if (paramAdapter != null) {
      localObject2 = new AdaptedLister((Lister)localObject2, (Class)paramAdapter.adapterType);
    }
    return localObject2;
  }
  
  private static Class getImplClass(Class<?> paramClass)
  {
    return ClassFactory.inferImplClass(paramClass, COLLECTION_IMPL_CLASSES);
  }
  
  private static Lister getArrayLister(Class paramClass)
  {
    Object localObject = null;
    if (paramClass.isPrimitive())
    {
      localObject = (Lister)primitiveArrayListers.get(paramClass);
    }
    else
    {
      WeakReference localWeakReference = (WeakReference)arrayListerCache.get(paramClass);
      if (localWeakReference != null) {
        localObject = (Lister)localWeakReference.get();
      }
      if (localObject == null)
      {
        localObject = new ArrayLister(paramClass);
        arrayListerCache.put(paramClass, new WeakReference(localObject));
      }
    }
    assert (localObject != null);
    return localObject;
  }
  
  public static <A, B, C, D> Lister<A, B, C, D> getErrorInstance()
  {
    return ERROR;
  }
  
  static
  {
    arrayListerCache = Collections.synchronizedMap(new WeakHashMap());
    primitiveArrayListers = new HashMap();
    PrimitiveArrayListerBoolean.register();
    PrimitiveArrayListerByte.register();
    PrimitiveArrayListerCharacter.register();
    PrimitiveArrayListerDouble.register();
    PrimitiveArrayListerFloat.register();
    PrimitiveArrayListerInteger.register();
    PrimitiveArrayListerLong.register();
    PrimitiveArrayListerShort.register();
  }
  
  private static final class ArrayLister<BeanT, ItemT>
    extends Lister<BeanT, ItemT[], ItemT, Lister.Pack<ItemT>>
  {
    private final Class<ItemT> itemType;
    
    public ArrayLister(Class<ItemT> paramClass)
    {
      this.itemType = paramClass;
    }
    
    public ListIterator<ItemT> iterator(final ItemT[] paramArrayOfItemT, XMLSerializer paramXMLSerializer)
    {
      new ListIterator()
      {
        int idx = 0;
        
        public boolean hasNext()
        {
          return this.idx < paramArrayOfItemT.length;
        }
        
        public ItemT next()
        {
          return paramArrayOfItemT[(this.idx++)];
        }
      };
    }
    
    public Lister.Pack startPacking(BeanT paramBeanT, Accessor<BeanT, ItemT[]> paramAccessor)
    {
      return new Lister.Pack(this.itemType);
    }
    
    public void addToPack(Lister.Pack<ItemT> paramPack, ItemT paramItemT)
    {
      paramPack.add(paramItemT);
    }
    
    public void endPacking(Lister.Pack<ItemT> paramPack, BeanT paramBeanT, Accessor<BeanT, ItemT[]> paramAccessor)
      throws AccessorException
    {
      paramAccessor.set(paramBeanT, paramPack.build());
    }
    
    public void reset(BeanT paramBeanT, Accessor<BeanT, ItemT[]> paramAccessor)
      throws AccessorException
    {
      paramAccessor.set(paramBeanT, (Object[])Array.newInstance(this.itemType, 0));
    }
  }
  
  public static final class CollectionLister<BeanT, T extends Collection>
    extends Lister<BeanT, T, Object, T>
  {
    private final Class<? extends T> implClass;
    
    public CollectionLister(Class<? extends T> paramClass)
    {
      this.implClass = paramClass;
    }
    
    public ListIterator iterator(T paramT, XMLSerializer paramXMLSerializer)
    {
      final Iterator localIterator = paramT.iterator();
      new ListIterator()
      {
        public boolean hasNext()
        {
          return localIterator.hasNext();
        }
        
        public Object next()
        {
          return localIterator.next();
        }
      };
    }
    
    public T startPacking(BeanT paramBeanT, Accessor<BeanT, T> paramAccessor)
      throws AccessorException
    {
      Collection localCollection = (Collection)paramAccessor.get(paramBeanT);
      if (localCollection == null)
      {
        localCollection = (Collection)ClassFactory.create(this.implClass);
        if (!paramAccessor.isAdapted()) {
          paramAccessor.set(paramBeanT, localCollection);
        }
      }
      localCollection.clear();
      return localCollection;
    }
    
    public void addToPack(T paramT, Object paramObject)
    {
      paramT.add(paramObject);
    }
    
    public void endPacking(T paramT, BeanT paramBeanT, Accessor<BeanT, T> paramAccessor)
      throws AccessorException
    {
      try
      {
        if (paramAccessor.isAdapted()) {
          paramAccessor.set(paramBeanT, paramT);
        }
      }
      catch (AccessorException localAccessorException)
      {
        if (paramAccessor.isAdapted()) {
          throw localAccessorException;
        }
      }
    }
    
    public void reset(BeanT paramBeanT, Accessor<BeanT, T> paramAccessor)
      throws AccessorException
    {
      Collection localCollection = (Collection)paramAccessor.get(paramBeanT);
      if (localCollection == null) {
        return;
      }
      localCollection.clear();
    }
  }
  
  private static final class IDREFS<BeanT, PropT>
    extends Lister<BeanT, PropT, String, IDREFS<BeanT, PropT>.Pack>
  {
    private final Lister<BeanT, PropT, Object, Object> core;
    private final Class itemType;
    
    public IDREFS(Lister paramLister, Class paramClass)
    {
      this.core = paramLister;
      this.itemType = paramClass;
    }
    
    public ListIterator<String> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer)
    {
      ListIterator localListIterator = this.core.iterator(paramPropT, paramXMLSerializer);
      return new Lister.IDREFSIterator(localListIterator, paramXMLSerializer, null);
    }
    
    public IDREFS<BeanT, PropT>.Pack startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
    {
      return new Pack(paramBeanT, paramAccessor);
    }
    
    public void addToPack(IDREFS<BeanT, PropT>.Pack paramIDREFS, String paramString)
    {
      paramIDREFS.add(paramString);
    }
    
    public void endPacking(IDREFS<BeanT, PropT>.Pack paramIDREFS, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) {}
    
    public void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor)
      throws AccessorException
    {
      this.core.reset(paramBeanT, paramAccessor);
    }
    
    private class Pack
      implements Patcher
    {
      private final BeanT bean;
      private final List<String> idrefs = new ArrayList();
      private final UnmarshallingContext context;
      private final Accessor<BeanT, PropT> acc;
      private final LocatorEx location;
      
      public Pack(Accessor<BeanT, PropT> paramAccessor)
      {
        this.bean = paramAccessor;
        Object localObject;
        this.acc = localObject;
        this.context = UnmarshallingContext.getInstance();
        this.location = new LocatorEx.Snapshot(this.context.getLocator());
        this.context.addPatcher(this);
      }
      
      public void add(String paramString)
      {
        this.idrefs.add(paramString);
      }
      
      public void run()
        throws SAXException
      {
        try
        {
          Object localObject1 = Lister.this.startPacking(this.bean, this.acc);
          Iterator localIterator = this.idrefs.iterator();
          while (localIterator.hasNext())
          {
            String str = (String)localIterator.next();
            Callable localCallable = this.context.getObjectFromId(str, Lister.IDREFS.this.itemType);
            Object localObject2;
            try
            {
              localObject2 = localCallable != null ? localCallable.call() : null;
            }
            catch (SAXException localSAXException)
            {
              throw localSAXException;
            }
            catch (Exception localException)
            {
              throw new SAXException2(localException);
            }
            if (localObject2 == null)
            {
              this.context.errorUnresolvedIDREF(this.bean, str, this.location);
            }
            else
            {
              TODO.prototype();
              Lister.this.addToPack(localObject1, localObject2);
            }
          }
          Lister.this.endPacking(localObject1, this.bean, this.acc);
        }
        catch (AccessorException localAccessorException)
        {
          this.context.handleError(localAccessorException);
        }
      }
    }
  }
  
  public static final class IDREFSIterator
    implements ListIterator<String>
  {
    private final ListIterator i;
    private final XMLSerializer context;
    private Object last;
    
    private IDREFSIterator(ListIterator paramListIterator, XMLSerializer paramXMLSerializer)
    {
      this.i = paramListIterator;
      this.context = paramXMLSerializer;
    }
    
    public boolean hasNext()
    {
      return this.i.hasNext();
    }
    
    public Object last()
    {
      return this.last;
    }
    
    public String next()
      throws SAXException, JAXBException
    {
      this.last = this.i.next();
      String str = this.context.grammar.getBeanInfo(this.last, true).getId(this.last, this.context);
      if (str == null) {
        this.context.errorMissingId(this.last);
      }
      return str;
    }
  }
  
  public static final class Pack<ItemT>
    extends ArrayList<ItemT>
  {
    private final Class<ItemT> itemType;
    
    public Pack(Class<ItemT> paramClass)
    {
      this.itemType = paramClass;
    }
    
    public ItemT[] build()
    {
      return super.toArray((Object[])Array.newInstance(this.itemType, size()));
    }
  }
}
