package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.TypeAnnotation.TypeAnnotationTarget;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ConstructorScope;

public final class Constructor<T>
  extends Executable
{
  private Class<T> clazz;
  private int slot;
  private Class<?>[] parameterTypes;
  private Class<?>[] exceptionTypes;
  private int modifiers;
  private transient String signature;
  private transient ConstructorRepository genericInfo;
  private byte[] annotations;
  private byte[] parameterAnnotations;
  private volatile ConstructorAccessor constructorAccessor;
  private Constructor<T> root;
  
  private GenericsFactory getFactory()
  {
    return CoreReflectionFactory.make(this, ConstructorScope.make(this));
  }
  
  ConstructorRepository getGenericInfo()
  {
    if (this.genericInfo == null) {
      this.genericInfo = ConstructorRepository.make(getSignature(), getFactory());
    }
    return this.genericInfo;
  }
  
  Executable getRoot()
  {
    return this.root;
  }
  
  Constructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    this.clazz = paramClass;
    this.parameterTypes = paramArrayOfClass1;
    this.exceptionTypes = paramArrayOfClass2;
    this.modifiers = paramInt1;
    this.slot = paramInt2;
    this.signature = paramString;
    this.annotations = paramArrayOfByte1;
    this.parameterAnnotations = paramArrayOfByte2;
  }
  
  Constructor<T> copy()
  {
    if (this.root != null) {
      throw new IllegalArgumentException("Can not copy a non-root Constructor");
    }
    Constructor localConstructor = new Constructor(this.clazz, this.parameterTypes, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations);
    localConstructor.root = this;
    localConstructor.constructorAccessor = this.constructorAccessor;
    return localConstructor;
  }
  
  boolean hasGenericInformation()
  {
    return getSignature() != null;
  }
  
  byte[] getAnnotationBytes()
  {
    return this.annotations;
  }
  
  public Class<T> getDeclaringClass()
  {
    return this.clazz;
  }
  
  public String getName()
  {
    return getDeclaringClass().getName();
  }
  
  public int getModifiers()
  {
    return this.modifiers;
  }
  
  public TypeVariable<Constructor<T>>[] getTypeParameters()
  {
    if (getSignature() != null) {
      return (TypeVariable[])getGenericInfo().getTypeParameters();
    }
    return (TypeVariable[])new TypeVariable[0];
  }
  
  public Class<?>[] getParameterTypes()
  {
    return (Class[])this.parameterTypes.clone();
  }
  
  public int getParameterCount()
  {
    return this.parameterTypes.length;
  }
  
  public Type[] getGenericParameterTypes()
  {
    return super.getGenericParameterTypes();
  }
  
  public Class<?>[] getExceptionTypes()
  {
    return (Class[])this.exceptionTypes.clone();
  }
  
  public Type[] getGenericExceptionTypes()
  {
    return super.getGenericExceptionTypes();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Constructor)))
    {
      Constructor localConstructor = (Constructor)paramObject;
      if (getDeclaringClass() == localConstructor.getDeclaringClass()) {
        return equalParamTypes(this.parameterTypes, localConstructor.parameterTypes);
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode();
  }
  
  public String toString()
  {
    return sharedToString(Modifier.constructorModifiers(), false, this.parameterTypes, this.exceptionTypes);
  }
  
  void specificToStringHeader(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append(getDeclaringClass().getTypeName());
  }
  
  public String toGenericString()
  {
    return sharedToGenericString(Modifier.constructorModifiers(), false);
  }
  
  void specificToGenericStringHeader(StringBuilder paramStringBuilder)
  {
    specificToStringHeader(paramStringBuilder);
  }
  
  @CallerSensitive
  public T newInstance(Object... paramVarArgs)
    throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      localObject1 = Reflection.getCallerClass();
      checkAccess((Class)localObject1, this.clazz, null, this.modifiers);
    }
    if ((this.clazz.getModifiers() & 0x4000) != 0) {
      throw new IllegalArgumentException("Cannot reflectively create enum objects");
    }
    Object localObject1 = this.constructorAccessor;
    if (localObject1 == null) {
      localObject1 = acquireConstructorAccessor();
    }
    Object localObject2 = ((ConstructorAccessor)localObject1).newInstance(paramVarArgs);
    return localObject2;
  }
  
  public boolean isVarArgs()
  {
    return super.isVarArgs();
  }
  
  public boolean isSynthetic()
  {
    return super.isSynthetic();
  }
  
  private ConstructorAccessor acquireConstructorAccessor()
  {
    ConstructorAccessor localConstructorAccessor = null;
    if (this.root != null) {
      localConstructorAccessor = this.root.getConstructorAccessor();
    }
    if (localConstructorAccessor != null)
    {
      this.constructorAccessor = localConstructorAccessor;
    }
    else
    {
      localConstructorAccessor = reflectionFactory.newConstructorAccessor(this);
      setConstructorAccessor(localConstructorAccessor);
    }
    return localConstructorAccessor;
  }
  
  ConstructorAccessor getConstructorAccessor()
  {
    return this.constructorAccessor;
  }
  
  void setConstructorAccessor(ConstructorAccessor paramConstructorAccessor)
  {
    this.constructorAccessor = paramConstructorAccessor;
    if (this.root != null) {
      this.root.setConstructorAccessor(paramConstructorAccessor);
    }
  }
  
  int getSlot()
  {
    return this.slot;
  }
  
  String getSignature()
  {
    return this.signature;
  }
  
  byte[] getRawAnnotations()
  {
    return this.annotations;
  }
  
  byte[] getRawParameterAnnotations()
  {
    return this.parameterAnnotations;
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    return super.getAnnotation(paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return super.getDeclaredAnnotations();
  }
  
  public Annotation[][] getParameterAnnotations()
  {
    return sharedGetParameterAnnotations(this.parameterTypes, this.parameterAnnotations);
  }
  
  void handleParameterNumberMismatch(int paramInt1, int paramInt2)
  {
    Class localClass = getDeclaringClass();
    if ((localClass.isEnum()) || (localClass.isAnonymousClass()) || (localClass.isLocalClass())) {
      return;
    }
    if ((!localClass.isMemberClass()) || ((localClass.isMemberClass()) && ((localClass.getModifiers() & 0x8) == 0) && (paramInt1 + 1 != paramInt2))) {
      throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
    }
  }
  
  public AnnotatedType getAnnotatedReturnType()
  {
    return getAnnotatedReturnType0(getDeclaringClass());
  }
  
  public AnnotatedType getAnnotatedReceiverType()
  {
    if (getDeclaringClass().getEnclosingClass() == null) {
      return super.getAnnotatedReceiverType();
    }
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass().getEnclosingClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
  }
}
