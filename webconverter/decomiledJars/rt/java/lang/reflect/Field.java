package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.FieldAccessor;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation.TypeAnnotationTarget;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.FieldRepository;
import sun.reflect.generics.scope.ClassScope;

public final class Field
  extends AccessibleObject
  implements Member
{
  private Class<?> clazz;
  private int slot;
  private String name;
  private Class<?> type;
  private int modifiers;
  private transient String signature;
  private transient FieldRepository genericInfo;
  private byte[] annotations;
  private FieldAccessor fieldAccessor;
  private FieldAccessor overrideFieldAccessor;
  private Field root;
  private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  private String getGenericSignature()
  {
    return this.signature;
  }
  
  private GenericsFactory getFactory()
  {
    Class localClass = getDeclaringClass();
    return CoreReflectionFactory.make(localClass, ClassScope.make(localClass));
  }
  
  private FieldRepository getGenericInfo()
  {
    if (this.genericInfo == null) {
      this.genericInfo = FieldRepository.make(getGenericSignature(), getFactory());
    }
    return this.genericInfo;
  }
  
  Field(Class<?> paramClass1, String paramString1, Class<?> paramClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte)
  {
    this.clazz = paramClass1;
    this.name = paramString1;
    this.type = paramClass2;
    this.modifiers = paramInt1;
    this.slot = paramInt2;
    this.signature = paramString2;
    this.annotations = paramArrayOfByte;
  }
  
  Field copy()
  {
    if (this.root != null) {
      throw new IllegalArgumentException("Can not copy a non-root Field");
    }
    Field localField = new Field(this.clazz, this.name, this.type, this.modifiers, this.slot, this.signature, this.annotations);
    localField.root = this;
    localField.fieldAccessor = this.fieldAccessor;
    localField.overrideFieldAccessor = this.overrideFieldAccessor;
    return localField;
  }
  
  public Class<?> getDeclaringClass()
  {
    return this.clazz;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public int getModifiers()
  {
    return this.modifiers;
  }
  
  public boolean isEnumConstant()
  {
    return (getModifiers() & 0x4000) != 0;
  }
  
  public boolean isSynthetic()
  {
    return Modifier.isSynthetic(getModifiers());
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
  public Type getGenericType()
  {
    if (getGenericSignature() != null) {
      return getGenericInfo().getGenericType();
    }
    return getType();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Field)))
    {
      Field localField = (Field)paramObject;
      return (getDeclaringClass() == localField.getDeclaringClass()) && (getName() == localField.getName()) && (getType() == localField.getType());
    }
    return false;
  }
  
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
  }
  
  public String toString()
  {
    int i = getModifiers();
    return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + getType().getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  public String toGenericString()
  {
    int i = getModifiers();
    Type localType = getGenericType();
    return (i == 0 ? "" : new StringBuilder().append(Modifier.toString(i)).append(" ").toString()) + localType.getTypeName() + " " + getDeclaringClass().getTypeName() + "." + getName();
  }
  
  @CallerSensitive
  public Object get(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).get(paramObject);
  }
  
  @CallerSensitive
  public boolean getBoolean(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getBoolean(paramObject);
  }
  
  @CallerSensitive
  public byte getByte(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getByte(paramObject);
  }
  
  @CallerSensitive
  public char getChar(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getChar(paramObject);
  }
  
  @CallerSensitive
  public short getShort(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getShort(paramObject);
  }
  
  @CallerSensitive
  public int getInt(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getInt(paramObject);
  }
  
  @CallerSensitive
  public long getLong(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getLong(paramObject);
  }
  
  @CallerSensitive
  public float getFloat(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getFloat(paramObject);
  }
  
  @CallerSensitive
  public double getDouble(Object paramObject)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    return getFieldAccessor(paramObject).getDouble(paramObject);
  }
  
  @CallerSensitive
  public void set(Object paramObject1, Object paramObject2)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject1, this.modifiers);
    }
    getFieldAccessor(paramObject1).set(paramObject1, paramObject2);
  }
  
  @CallerSensitive
  public void setBoolean(Object paramObject, boolean paramBoolean)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setBoolean(paramObject, paramBoolean);
  }
  
  @CallerSensitive
  public void setByte(Object paramObject, byte paramByte)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setByte(paramObject, paramByte);
  }
  
  @CallerSensitive
  public void setChar(Object paramObject, char paramChar)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setChar(paramObject, paramChar);
  }
  
  @CallerSensitive
  public void setShort(Object paramObject, short paramShort)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setShort(paramObject, paramShort);
  }
  
  @CallerSensitive
  public void setInt(Object paramObject, int paramInt)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setInt(paramObject, paramInt);
  }
  
  @CallerSensitive
  public void setLong(Object paramObject, long paramLong)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setLong(paramObject, paramLong);
  }
  
  @CallerSensitive
  public void setFloat(Object paramObject, float paramFloat)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setFloat(paramObject, paramFloat);
  }
  
  @CallerSensitive
  public void setDouble(Object paramObject, double paramDouble)
    throws IllegalArgumentException, IllegalAccessException
  {
    if ((!this.override) && (!Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)))
    {
      Class localClass = Reflection.getCallerClass();
      checkAccess(localClass, this.clazz, paramObject, this.modifiers);
    }
    getFieldAccessor(paramObject).setDouble(paramObject, paramDouble);
  }
  
  private FieldAccessor getFieldAccessor(Object paramObject)
    throws IllegalAccessException
  {
    boolean bool = this.override;
    FieldAccessor localFieldAccessor = bool ? this.overrideFieldAccessor : this.fieldAccessor;
    return localFieldAccessor != null ? localFieldAccessor : acquireFieldAccessor(bool);
  }
  
  private FieldAccessor acquireFieldAccessor(boolean paramBoolean)
  {
    FieldAccessor localFieldAccessor = null;
    if (this.root != null) {
      localFieldAccessor = this.root.getFieldAccessor(paramBoolean);
    }
    if (localFieldAccessor != null)
    {
      if (paramBoolean) {
        this.overrideFieldAccessor = localFieldAccessor;
      } else {
        this.fieldAccessor = localFieldAccessor;
      }
    }
    else
    {
      localFieldAccessor = reflectionFactory.newFieldAccessor(this, paramBoolean);
      setFieldAccessor(localFieldAccessor, paramBoolean);
    }
    return localFieldAccessor;
  }
  
  private FieldAccessor getFieldAccessor(boolean paramBoolean)
  {
    return paramBoolean ? this.overrideFieldAccessor : this.fieldAccessor;
  }
  
  private void setFieldAccessor(FieldAccessor paramFieldAccessor, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.overrideFieldAccessor = paramFieldAccessor;
    } else {
      this.fieldAccessor = paramFieldAccessor;
    }
    if (this.root != null) {
      this.root.setFieldAccessor(paramFieldAccessor, paramBoolean);
    }
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return (Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass)
  {
    Objects.requireNonNull(paramClass);
    return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return AnnotationParser.toArray(declaredAnnotations());
  }
  
  private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations()
  {
    if (this.declaredAnnotations == null)
    {
      Field localField = this.root;
      if (localField != null) {
        this.declaredAnnotations = localField.declaredAnnotations();
      } else {
        this.declaredAnnotations = AnnotationParser.parseAnnotations(this.annotations, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
      }
    }
    return this.declaredAnnotations;
  }
  
  private native byte[] getTypeAnnotationBytes0();
  
  public AnnotatedType getAnnotatedType()
  {
    return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getGenericType(), TypeAnnotation.TypeAnnotationTarget.FIELD);
  }
}
