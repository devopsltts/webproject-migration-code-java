package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;

public class IndexedPropertyDescriptor
  extends PropertyDescriptor
{
  private Reference<? extends Class<?>> indexedPropertyTypeRef;
  private final MethodRef indexedReadMethodRef = new MethodRef();
  private final MethodRef indexedWriteMethodRef = new MethodRef();
  private String indexedReadMethodName;
  private String indexedWriteMethodName;
  
  public IndexedPropertyDescriptor(String paramString, Class<?> paramClass)
    throws IntrospectionException
  {
    this(paramString, paramClass, "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString), "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString));
  }
  
  public IndexedPropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3, String paramString4, String paramString5)
    throws IntrospectionException
  {
    super(paramString1, paramClass, paramString2, paramString3);
    this.indexedReadMethodName = paramString4;
    if ((paramString4 != null) && (getIndexedReadMethod() == null)) {
      throw new IntrospectionException("Method not found: " + paramString4);
    }
    this.indexedWriteMethodName = paramString5;
    if ((paramString5 != null) && (getIndexedWriteMethod() == null)) {
      throw new IntrospectionException("Method not found: " + paramString5);
    }
    findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
  }
  
  public IndexedPropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4)
    throws IntrospectionException
  {
    super(paramString, paramMethod1, paramMethod2);
    setIndexedReadMethod0(paramMethod3);
    setIndexedWriteMethod0(paramMethod4);
    setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
  }
  
  IndexedPropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4)
    throws IntrospectionException
  {
    super(paramClass, paramString, paramMethod1, paramMethod2);
    setIndexedReadMethod0(paramMethod3);
    setIndexedWriteMethod0(paramMethod4);
    setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
  }
  
  public synchronized Method getIndexedReadMethod()
  {
    Method localMethod = this.indexedReadMethodRef.get();
    if (localMethod == null)
    {
      Class localClass = getClass0();
      if ((localClass == null) || ((this.indexedReadMethodName == null) && (!this.indexedReadMethodRef.isSet()))) {
        return null;
      }
      String str = "get" + getBaseName();
      if (this.indexedReadMethodName == null)
      {
        localObject = getIndexedPropertyType0();
        if ((localObject == Boolean.TYPE) || (localObject == null)) {
          this.indexedReadMethodName = ("is" + getBaseName());
        } else {
          this.indexedReadMethodName = str;
        }
      }
      Object localObject = { Integer.TYPE };
      localMethod = Introspector.findMethod(localClass, this.indexedReadMethodName, 1, (Class[])localObject);
      if ((localMethod == null) && (!this.indexedReadMethodName.equals(str)))
      {
        this.indexedReadMethodName = str;
        localMethod = Introspector.findMethod(localClass, this.indexedReadMethodName, 1, (Class[])localObject);
      }
      setIndexedReadMethod0(localMethod);
    }
    return localMethod;
  }
  
  public synchronized void setIndexedReadMethod(Method paramMethod)
    throws IntrospectionException
  {
    setIndexedPropertyType(findIndexedPropertyType(paramMethod, this.indexedWriteMethodRef.get()));
    setIndexedReadMethod0(paramMethod);
  }
  
  private void setIndexedReadMethod0(Method paramMethod)
  {
    this.indexedReadMethodRef.set(paramMethod);
    if (paramMethod == null)
    {
      this.indexedReadMethodName = null;
      return;
    }
    setClass0(paramMethod.getDeclaringClass());
    this.indexedReadMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public synchronized Method getIndexedWriteMethod()
  {
    Method localMethod = this.indexedWriteMethodRef.get();
    if (localMethod == null)
    {
      Class localClass1 = getClass0();
      if ((localClass1 == null) || ((this.indexedWriteMethodName == null) && (!this.indexedWriteMethodRef.isSet()))) {
        return null;
      }
      Class localClass2 = getIndexedPropertyType0();
      if (localClass2 == null) {
        try
        {
          localClass2 = findIndexedPropertyType(getIndexedReadMethod(), null);
          setIndexedPropertyType(localClass2);
        }
        catch (IntrospectionException localIntrospectionException)
        {
          Class localClass3 = getPropertyType();
          if (localClass3.isArray()) {
            localClass2 = localClass3.getComponentType();
          }
        }
      }
      if (this.indexedWriteMethodName == null) {
        this.indexedWriteMethodName = ("set" + getBaseName());
      }
      Class[] arrayOfClass = { Integer.TYPE, localClass2 == null ? null : localClass2 };
      localMethod = Introspector.findMethod(localClass1, this.indexedWriteMethodName, 2, arrayOfClass);
      if ((localMethod != null) && (!localMethod.getReturnType().equals(Void.TYPE))) {
        localMethod = null;
      }
      setIndexedWriteMethod0(localMethod);
    }
    return localMethod;
  }
  
  public synchronized void setIndexedWriteMethod(Method paramMethod)
    throws IntrospectionException
  {
    Class localClass = findIndexedPropertyType(getIndexedReadMethod(), paramMethod);
    setIndexedPropertyType(localClass);
    setIndexedWriteMethod0(paramMethod);
  }
  
  private void setIndexedWriteMethod0(Method paramMethod)
  {
    this.indexedWriteMethodRef.set(paramMethod);
    if (paramMethod == null)
    {
      this.indexedWriteMethodName = null;
      return;
    }
    setClass0(paramMethod.getDeclaringClass());
    this.indexedWriteMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public synchronized Class<?> getIndexedPropertyType()
  {
    Class localClass = getIndexedPropertyType0();
    if (localClass == null) {
      try
      {
        localClass = findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
        setIndexedPropertyType(localClass);
      }
      catch (IntrospectionException localIntrospectionException) {}
    }
    return localClass;
  }
  
  private void setIndexedPropertyType(Class<?> paramClass)
  {
    this.indexedPropertyTypeRef = getWeakReference(paramClass);
  }
  
  private Class<?> getIndexedPropertyType0()
  {
    return this.indexedPropertyTypeRef != null ? (Class)this.indexedPropertyTypeRef.get() : null;
  }
  
  private Class<?> findIndexedPropertyType(Method paramMethod1, Method paramMethod2)
    throws IntrospectionException
  {
    Class localClass = null;
    if (paramMethod1 != null)
    {
      localObject = getParameterTypes(getClass0(), paramMethod1);
      if (localObject.length != 1) {
        throw new IntrospectionException("bad indexed read method arg count");
      }
      if (localObject[0] != Integer.TYPE) {
        throw new IntrospectionException("non int index to indexed read method");
      }
      localClass = getReturnType(getClass0(), paramMethod1);
      if (localClass == Void.TYPE) {
        throw new IntrospectionException("indexed read method returns void");
      }
    }
    if (paramMethod2 != null)
    {
      localObject = getParameterTypes(getClass0(), paramMethod2);
      if (localObject.length != 2) {
        throw new IntrospectionException("bad indexed write method arg count");
      }
      if (localObject[0] != Integer.TYPE) {
        throw new IntrospectionException("non int index to indexed write method");
      }
      if ((localClass == null) || (localObject[1].isAssignableFrom(localClass))) {
        localClass = localObject[1];
      } else if (!localClass.isAssignableFrom(localObject[1])) {
        throw new IntrospectionException("type mismatch between indexed read and indexed write methods: " + getName());
      }
    }
    Object localObject = getPropertyType();
    if ((localObject != null) && ((!((Class)localObject).isArray()) || (((Class)localObject).getComponentType() != localClass))) {
      throw new IntrospectionException("type mismatch between indexed and non-indexed methods: " + getName());
    }
    return localClass;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof IndexedPropertyDescriptor)))
    {
      IndexedPropertyDescriptor localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramObject;
      Method localMethod1 = localIndexedPropertyDescriptor.getIndexedReadMethod();
      Method localMethod2 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
      if (!compareMethods(getIndexedReadMethod(), localMethod1)) {
        return false;
      }
      if (!compareMethods(getIndexedWriteMethod(), localMethod2)) {
        return false;
      }
      if (getIndexedPropertyType() != localIndexedPropertyDescriptor.getIndexedPropertyType()) {
        return false;
      }
      return super.equals(paramObject);
    }
    return false;
  }
  
  IndexedPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2)
  {
    super(paramPropertyDescriptor1, paramPropertyDescriptor2);
    IndexedPropertyDescriptor localIndexedPropertyDescriptor;
    Method localMethod3;
    if ((paramPropertyDescriptor1 instanceof IndexedPropertyDescriptor))
    {
      localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor1;
      try
      {
        Method localMethod1 = localIndexedPropertyDescriptor.getIndexedReadMethod();
        if (localMethod1 != null) {
          setIndexedReadMethod(localMethod1);
        }
        localMethod3 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
        if (localMethod3 != null) {
          setIndexedWriteMethod(localMethod3);
        }
      }
      catch (IntrospectionException localIntrospectionException1)
      {
        throw new AssertionError(localIntrospectionException1);
      }
    }
    if ((paramPropertyDescriptor2 instanceof IndexedPropertyDescriptor))
    {
      localIndexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor2;
      try
      {
        Method localMethod2 = localIndexedPropertyDescriptor.getIndexedReadMethod();
        if ((localMethod2 != null) && (localMethod2.getDeclaringClass() == getClass0())) {
          setIndexedReadMethod(localMethod2);
        }
        localMethod3 = localIndexedPropertyDescriptor.getIndexedWriteMethod();
        if ((localMethod3 != null) && (localMethod3.getDeclaringClass() == getClass0())) {
          setIndexedWriteMethod(localMethod3);
        }
      }
      catch (IntrospectionException localIntrospectionException2)
      {
        throw new AssertionError(localIntrospectionException2);
      }
    }
  }
  
  IndexedPropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor)
  {
    super(paramIndexedPropertyDescriptor);
    this.indexedReadMethodRef.set(paramIndexedPropertyDescriptor.indexedReadMethodRef.get());
    this.indexedWriteMethodRef.set(paramIndexedPropertyDescriptor.indexedWriteMethodRef.get());
    this.indexedPropertyTypeRef = paramIndexedPropertyDescriptor.indexedPropertyTypeRef;
    this.indexedWriteMethodName = paramIndexedPropertyDescriptor.indexedWriteMethodName;
    this.indexedReadMethodName = paramIndexedPropertyDescriptor.indexedReadMethodName;
  }
  
  void updateGenericsFor(Class<?> paramClass)
  {
    super.updateGenericsFor(paramClass);
    try
    {
      setIndexedPropertyType(findIndexedPropertyType(this.indexedReadMethodRef.get(), this.indexedWriteMethodRef.get()));
    }
    catch (IntrospectionException localIntrospectionException)
    {
      setIndexedPropertyType(null);
    }
  }
  
  public int hashCode()
  {
    int i = super.hashCode();
    i = 37 * i + (this.indexedWriteMethodName == null ? 0 : this.indexedWriteMethodName.hashCode());
    i = 37 * i + (this.indexedReadMethodName == null ? 0 : this.indexedReadMethodName.hashCode());
    i = 37 * i + (getIndexedPropertyType() == null ? 0 : getIndexedPropertyType().hashCode());
    return i;
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    super.appendTo(paramStringBuilder);
    appendTo(paramStringBuilder, "indexedPropertyType", this.indexedPropertyTypeRef);
    appendTo(paramStringBuilder, "indexedReadMethod", this.indexedReadMethodRef.get());
    appendTo(paramStringBuilder, "indexedWriteMethod", this.indexedWriteMethodRef.get());
  }
}
