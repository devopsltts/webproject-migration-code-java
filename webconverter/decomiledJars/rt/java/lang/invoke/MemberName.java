package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import sun.invoke.util.BytecodeDescriptor;
import sun.invoke.util.VerifyAccess;

final class MemberName
  implements Member, Cloneable
{
  private Class<?> clazz;
  private String name;
  private Object type;
  private int flags;
  private Object resolution;
  private static final int MH_INVOKE_MODS = 273;
  static final int BRIDGE = 64;
  static final int VARARGS = 128;
  static final int SYNTHETIC = 4096;
  static final int ANNOTATION = 8192;
  static final int ENUM = 16384;
  static final String CONSTRUCTOR_NAME = "<init>";
  static final int RECOGNIZED_MODIFIERS = 65535;
  static final int IS_METHOD = 65536;
  static final int IS_CONSTRUCTOR = 131072;
  static final int IS_FIELD = 262144;
  static final int IS_TYPE = 524288;
  static final int CALLER_SENSITIVE = 1048576;
  static final int ALL_ACCESS = 7;
  static final int ALL_KINDS = 983040;
  static final int IS_INVOCABLE = 196608;
  static final int IS_FIELD_OR_METHOD = 327680;
  static final int SEARCH_ALL_SUPERS = 3145728;
  
  public Class<?> getDeclaringClass()
  {
    return this.clazz;
  }
  
  public ClassLoader getClassLoader()
  {
    return this.clazz.getClassLoader();
  }
  
  public String getName()
  {
    if (this.name == null)
    {
      expandFromVM();
      if (this.name == null) {
        return null;
      }
    }
    return this.name;
  }
  
  public MethodType getMethodOrFieldType()
  {
    if (isInvocable()) {
      return getMethodType();
    }
    if (isGetter()) {
      return MethodType.methodType(getFieldType());
    }
    if (isSetter()) {
      return MethodType.methodType(Void.TYPE, getFieldType());
    }
    throw new InternalError("not a method or field: " + this);
  }
  
  public MethodType getMethodType()
  {
    if (this.type == null)
    {
      expandFromVM();
      if (this.type == null) {
        return null;
      }
    }
    if (!isInvocable()) {
      throw MethodHandleStatics.newIllegalArgumentException("not invocable, no method type");
    }
    Object localObject1 = this.type;
    if ((localObject1 instanceof MethodType)) {
      return (MethodType)localObject1;
    }
    synchronized (this)
    {
      Object localObject2;
      Object localObject3;
      if ((this.type instanceof String))
      {
        localObject2 = (String)this.type;
        localObject3 = MethodType.fromMethodDescriptorString((String)localObject2, getClassLoader());
        this.type = localObject3;
      }
      else if ((this.type instanceof Object[]))
      {
        localObject2 = (Object[])this.type;
        localObject3 = (Class[])localObject2[1];
        Class localClass = (Class)localObject2[0];
        MethodType localMethodType = MethodType.methodType(localClass, (Class[])localObject3);
        this.type = localMethodType;
      }
      if ((!$assertionsDisabled) && (!(this.type instanceof MethodType))) {
        throw new AssertionError("bad method type " + this.type);
      }
    }
    return (MethodType)this.type;
  }
  
  public MethodType getInvocationType()
  {
    MethodType localMethodType = getMethodOrFieldType();
    if ((isConstructor()) && (getReferenceKind() == 8)) {
      return localMethodType.changeReturnType(this.clazz);
    }
    if (!isStatic()) {
      return localMethodType.insertParameterTypes(0, new Class[] { this.clazz });
    }
    return localMethodType;
  }
  
  public Class<?>[] getParameterTypes()
  {
    return getMethodType().parameterArray();
  }
  
  public Class<?> getReturnType()
  {
    return getMethodType().returnType();
  }
  
  public Class<?> getFieldType()
  {
    if (this.type == null)
    {
      expandFromVM();
      if (this.type == null) {
        return null;
      }
    }
    if (isInvocable()) {
      throw MethodHandleStatics.newIllegalArgumentException("not a field or nested class, no simple type");
    }
    Object localObject1 = this.type;
    if ((localObject1 instanceof Class)) {
      return (Class)localObject1;
    }
    synchronized (this)
    {
      if ((this.type instanceof String))
      {
        String str = (String)this.type;
        MethodType localMethodType = MethodType.fromMethodDescriptorString("()" + str, getClassLoader());
        Class localClass = localMethodType.returnType();
        this.type = localClass;
      }
      if ((!$assertionsDisabled) && (!(this.type instanceof Class))) {
        throw new AssertionError("bad field type " + this.type);
      }
    }
    return (Class)this.type;
  }
  
  public Object getType()
  {
    return isInvocable() ? getMethodType() : getFieldType();
  }
  
  public String getSignature()
  {
    if (this.type == null)
    {
      expandFromVM();
      if (this.type == null) {
        return null;
      }
    }
    if (isInvocable()) {
      return BytecodeDescriptor.unparse(getMethodType());
    }
    return BytecodeDescriptor.unparse(getFieldType());
  }
  
  public int getModifiers()
  {
    return this.flags & 0xFFFF;
  }
  
  public byte getReferenceKind()
  {
    return (byte)(this.flags >>> 24 & 0xF);
  }
  
  private boolean referenceKindIsConsistent()
  {
    byte b = getReferenceKind();
    if (b == 0) {
      return isType();
    }
    if (isField())
    {
      assert (staticIsConsistent());
      if ((!$assertionsDisabled) && (!MethodHandleNatives.refKindIsField(b))) {
        throw new AssertionError();
      }
    }
    else if (isConstructor())
    {
      if ((!$assertionsDisabled) && (b != 8) && (b != 7)) {
        throw new AssertionError();
      }
    }
    else if (isMethod())
    {
      assert (staticIsConsistent());
      assert (MethodHandleNatives.refKindIsMethod(b));
      if ((this.clazz.isInterface()) && (!$assertionsDisabled) && (b != 9) && (b != 6) && (b != 7) && ((b != 5) || (!isObjectPublicMethod()))) {
        throw new AssertionError();
      }
    }
    else if (!$assertionsDisabled)
    {
      throw new AssertionError();
    }
    return true;
  }
  
  private boolean isObjectPublicMethod()
  {
    if (this.clazz == Object.class) {
      return true;
    }
    MethodType localMethodType = getMethodType();
    if ((this.name.equals("toString")) && (localMethodType.returnType() == String.class) && (localMethodType.parameterCount() == 0)) {
      return true;
    }
    if ((this.name.equals("hashCode")) && (localMethodType.returnType() == Integer.TYPE) && (localMethodType.parameterCount() == 0)) {
      return true;
    }
    return (this.name.equals("equals")) && (localMethodType.returnType() == Boolean.TYPE) && (localMethodType.parameterCount() == 1) && (localMethodType.parameterType(0) == Object.class);
  }
  
  boolean referenceKindIsConsistentWith(int paramInt)
  {
    int i = getReferenceKind();
    if (i == paramInt) {
      return true;
    }
    switch (paramInt)
    {
    case 9: 
      assert ((i == 5) || (i == 7)) : this;
      return true;
    case 5: 
    case 8: 
      assert (i == 7) : this;
      return true;
    }
    if (!$assertionsDisabled) {
      throw new AssertionError(this + " != " + MethodHandleNatives.refKindName((byte)paramInt));
    }
    return true;
  }
  
  private boolean staticIsConsistent()
  {
    byte b = getReferenceKind();
    return (MethodHandleNatives.refKindIsStatic(b) == isStatic()) || (getModifiers() == 0);
  }
  
  private boolean vminfoIsConsistent()
  {
    byte b = getReferenceKind();
    assert (isResolved());
    Object localObject1 = MethodHandleNatives.getMemberVMInfo(this);
    assert ((localObject1 instanceof Object[]));
    long l = ((Long)((Object[])(Object[])localObject1)[0]).longValue();
    Object localObject2 = ((Object[])(Object[])localObject1)[1];
    if (MethodHandleNatives.refKindIsField(b))
    {
      assert (l >= 0L) : (l + ":" + this);
      if ((!$assertionsDisabled) && (!(localObject2 instanceof Class))) {
        throw new AssertionError();
      }
    }
    else
    {
      if (MethodHandleNatives.refKindDoesDispatch(b))
      {
        if ((!$assertionsDisabled) && (l < 0L)) {
          throw new AssertionError(l + ":" + this);
        }
      }
      else {
        assert (l < 0L) : l;
      }
      assert ((localObject2 instanceof MemberName)) : (localObject2 + " in " + this);
    }
    return true;
  }
  
  private MemberName changeReferenceKind(byte paramByte1, byte paramByte2)
  {
    assert (getReferenceKind() == paramByte2);
    assert (MethodHandleNatives.refKindIsValid(paramByte1));
    this.flags += (paramByte1 - paramByte2 << 24);
    return this;
  }
  
  private boolean testFlags(int paramInt1, int paramInt2)
  {
    return (this.flags & paramInt1) == paramInt2;
  }
  
  private boolean testAllFlags(int paramInt)
  {
    return testFlags(paramInt, paramInt);
  }
  
  private boolean testAnyFlags(int paramInt)
  {
    return !testFlags(paramInt, 0);
  }
  
  public boolean isMethodHandleInvoke()
  {
    if ((testFlags(280, 272)) && (this.clazz == MethodHandle.class)) {
      return isMethodHandleInvokeName(this.name);
    }
    return false;
  }
  
  public static boolean isMethodHandleInvokeName(String paramString)
  {
    switch (paramString)
    {
    case "invoke": 
    case "invokeBasic": 
    case "invokeExact": 
      return true;
    }
    return false;
  }
  
  public boolean isStatic()
  {
    return Modifier.isStatic(this.flags);
  }
  
  public boolean isPublic()
  {
    return Modifier.isPublic(this.flags);
  }
  
  public boolean isPrivate()
  {
    return Modifier.isPrivate(this.flags);
  }
  
  public boolean isProtected()
  {
    return Modifier.isProtected(this.flags);
  }
  
  public boolean isFinal()
  {
    return Modifier.isFinal(this.flags);
  }
  
  public boolean canBeStaticallyBound()
  {
    return Modifier.isFinal(this.flags | this.clazz.getModifiers());
  }
  
  public boolean isVolatile()
  {
    return Modifier.isVolatile(this.flags);
  }
  
  public boolean isAbstract()
  {
    return Modifier.isAbstract(this.flags);
  }
  
  public boolean isNative()
  {
    return Modifier.isNative(this.flags);
  }
  
  public boolean isBridge()
  {
    return testAllFlags(65600);
  }
  
  public boolean isVarargs()
  {
    return (testAllFlags(128)) && (isInvocable());
  }
  
  public boolean isSynthetic()
  {
    return testAllFlags(4096);
  }
  
  public boolean isInvocable()
  {
    return testAnyFlags(196608);
  }
  
  public boolean isFieldOrMethod()
  {
    return testAnyFlags(327680);
  }
  
  public boolean isMethod()
  {
    return testAllFlags(65536);
  }
  
  public boolean isConstructor()
  {
    return testAllFlags(131072);
  }
  
  public boolean isField()
  {
    return testAllFlags(262144);
  }
  
  public boolean isType()
  {
    return testAllFlags(524288);
  }
  
  public boolean isPackage()
  {
    return !testAnyFlags(7);
  }
  
  public boolean isCallerSensitive()
  {
    return testAllFlags(1048576);
  }
  
  public boolean isAccessibleFrom(Class<?> paramClass)
  {
    return VerifyAccess.isMemberAccessible(getDeclaringClass(), getDeclaringClass(), this.flags, paramClass, 15);
  }
  
  private void init(Class<?> paramClass, String paramString, Object paramObject, int paramInt)
  {
    this.clazz = paramClass;
    this.name = paramString;
    this.type = paramObject;
    this.flags = paramInt;
    assert (testAnyFlags(983040));
    assert (this.resolution == null);
  }
  
  private void expandFromVM()
  {
    if (this.type != null) {
      return;
    }
    if (!isResolved()) {
      return;
    }
    MethodHandleNatives.expand(this);
  }
  
  private static int flagsMods(int paramInt1, int paramInt2, byte paramByte)
  {
    assert ((paramInt1 & 0xFFFF) == 0);
    assert ((paramInt2 & 0xFFFF0000) == 0);
    assert ((paramByte & 0xFFFFFFF0) == 0);
    return paramInt1 | paramInt2 | paramByte << 24;
  }
  
  public MemberName(Method paramMethod)
  {
    this(paramMethod, false);
  }
  
  public MemberName(Method paramMethod, boolean paramBoolean)
  {
    paramMethod.getClass();
    MethodHandleNatives.init(this, paramMethod);
    if (this.clazz == null)
    {
      if ((paramMethod.getDeclaringClass() == MethodHandle.class) && (isMethodHandleInvokeName(paramMethod.getName())))
      {
        MethodType localMethodType = MethodType.methodType(paramMethod.getReturnType(), paramMethod.getParameterTypes());
        int i = flagsMods(65536, paramMethod.getModifiers(), (byte)5);
        init(MethodHandle.class, paramMethod.getName(), localMethodType, i);
        if (isMethodHandleInvoke()) {
          return;
        }
      }
      throw new LinkageError(paramMethod.toString());
    }
    assert ((isResolved()) && (this.clazz != null));
    this.name = paramMethod.getName();
    if (this.type == null) {
      this.type = new Object[] { paramMethod.getReturnType(), paramMethod.getParameterTypes() };
    }
    if (paramBoolean)
    {
      if (isAbstract()) {
        throw new AbstractMethodError(toString());
      }
      if (getReferenceKind() == 5) {
        changeReferenceKind((byte)7, (byte)5);
      } else if (getReferenceKind() == 9) {
        changeReferenceKind((byte)7, (byte)9);
      }
    }
  }
  
  public MemberName asSpecial()
  {
    switch (getReferenceKind())
    {
    case 7: 
      return this;
    case 5: 
      return clone().changeReferenceKind((byte)7, (byte)5);
    case 9: 
      return clone().changeReferenceKind((byte)7, (byte)9);
    case 8: 
      return clone().changeReferenceKind((byte)7, (byte)8);
    }
    throw new IllegalArgumentException(toString());
  }
  
  public MemberName asConstructor()
  {
    switch (getReferenceKind())
    {
    case 7: 
      return clone().changeReferenceKind((byte)8, (byte)7);
    case 8: 
      return this;
    }
    throw new IllegalArgumentException(toString());
  }
  
  public MemberName asNormalOriginal()
  {
    byte b1 = this.clazz.isInterface() ? 9 : 5;
    byte b2 = getReferenceKind();
    byte b3 = b2;
    MemberName localMemberName = this;
    switch (b2)
    {
    case 5: 
    case 7: 
    case 9: 
      b3 = b1;
    }
    if (b3 == b2) {
      return this;
    }
    localMemberName = clone().changeReferenceKind(b3, b2);
    assert (referenceKindIsConsistentWith(localMemberName.getReferenceKind()));
    return localMemberName;
  }
  
  public MemberName(Constructor<?> paramConstructor)
  {
    paramConstructor.getClass();
    MethodHandleNatives.init(this, paramConstructor);
    assert ((isResolved()) && (this.clazz != null));
    this.name = "<init>";
    if (this.type == null) {
      this.type = new Object[] { Void.TYPE, paramConstructor.getParameterTypes() };
    }
  }
  
  public MemberName(Field paramField)
  {
    this(paramField, false);
  }
  
  public MemberName(Field paramField, boolean paramBoolean)
  {
    paramField.getClass();
    MethodHandleNatives.init(this, paramField);
    assert ((isResolved()) && (this.clazz != null));
    this.name = paramField.getName();
    this.type = paramField.getType();
    byte b = getReferenceKind();
    if (!$assertionsDisabled) {
      if (b != (isStatic() ? 2 : 1)) {
        throw new AssertionError();
      }
    }
    if (paramBoolean) {
      changeReferenceKind((byte)(b + 2), b);
    }
  }
  
  public boolean isGetter()
  {
    return MethodHandleNatives.refKindIsGetter(getReferenceKind());
  }
  
  public boolean isSetter()
  {
    return MethodHandleNatives.refKindIsSetter(getReferenceKind());
  }
  
  public MemberName asSetter()
  {
    byte b1 = getReferenceKind();
    assert (MethodHandleNatives.refKindIsGetter(b1));
    byte b2 = (byte)(b1 + 2);
    return clone().changeReferenceKind(b2, b1);
  }
  
  public MemberName(Class<?> paramClass)
  {
    init(paramClass.getDeclaringClass(), paramClass.getSimpleName(), paramClass, flagsMods(524288, paramClass.getModifiers(), (byte)0));
    initResolved(true);
  }
  
  static MemberName makeMethodHandleInvoke(String paramString, MethodType paramMethodType)
  {
    return makeMethodHandleInvoke(paramString, paramMethodType, 4369);
  }
  
  static MemberName makeMethodHandleInvoke(String paramString, MethodType paramMethodType, int paramInt)
  {
    MemberName localMemberName = new MemberName(MethodHandle.class, paramString, paramMethodType, (byte)5);
    localMemberName.flags |= paramInt;
    assert (localMemberName.isMethodHandleInvoke()) : localMemberName;
    return localMemberName;
  }
  
  MemberName() {}
  
  protected MemberName clone()
  {
    try
    {
      return (MemberName)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw MethodHandleStatics.newInternalError(localCloneNotSupportedException);
    }
  }
  
  public MemberName getDefinition()
  {
    if (!isResolved()) {
      throw new IllegalStateException("must be resolved: " + this);
    }
    if (isType()) {
      return this;
    }
    MemberName localMemberName = clone();
    localMemberName.clazz = null;
    localMemberName.type = null;
    localMemberName.name = null;
    localMemberName.resolution = localMemberName;
    localMemberName.expandFromVM();
    assert (localMemberName.getName().equals(getName()));
    return localMemberName;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.clazz, Byte.valueOf(getReferenceKind()), this.name, getType() });
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof MemberName)) && (equals((MemberName)paramObject));
  }
  
  public boolean equals(MemberName paramMemberName)
  {
    if (this == paramMemberName) {
      return true;
    }
    if (paramMemberName == null) {
      return false;
    }
    return (this.clazz == paramMemberName.clazz) && (getReferenceKind() == paramMemberName.getReferenceKind()) && (Objects.equals(this.name, paramMemberName.name)) && (Objects.equals(getType(), paramMemberName.getType()));
  }
  
  public MemberName(Class<?> paramClass1, String paramString, Class<?> paramClass2, byte paramByte)
  {
    init(paramClass1, paramString, paramClass2, flagsMods(262144, 0, paramByte));
    initResolved(false);
  }
  
  public MemberName(Class<?> paramClass, String paramString, MethodType paramMethodType, byte paramByte)
  {
    int i = (paramString != null) && (paramString.equals("<init>")) ? 131072 : 65536;
    init(paramClass, paramString, paramMethodType, flagsMods(i, 0, paramByte));
    initResolved(false);
  }
  
  public MemberName(byte paramByte, Class<?> paramClass, String paramString, Object paramObject)
  {
    int i;
    if (MethodHandleNatives.refKindIsField(paramByte))
    {
      i = 262144;
      if (!(paramObject instanceof Class)) {
        throw MethodHandleStatics.newIllegalArgumentException("not a field type");
      }
    }
    else if (MethodHandleNatives.refKindIsMethod(paramByte))
    {
      i = 65536;
      if (!(paramObject instanceof MethodType)) {
        throw MethodHandleStatics.newIllegalArgumentException("not a method type");
      }
    }
    else if (paramByte == 8)
    {
      i = 131072;
      if ((!(paramObject instanceof MethodType)) || (!"<init>".equals(paramString))) {
        throw MethodHandleStatics.newIllegalArgumentException("not a constructor type or name");
      }
    }
    else
    {
      throw MethodHandleStatics.newIllegalArgumentException("bad reference kind " + paramByte);
    }
    init(paramClass, paramString, paramObject, flagsMods(i, 0, paramByte));
    initResolved(false);
  }
  
  public boolean hasReceiverTypeDispatch()
  {
    return MethodHandleNatives.refKindDoesDispatch(getReferenceKind());
  }
  
  public boolean isResolved()
  {
    return this.resolution == null;
  }
  
  private void initResolved(boolean paramBoolean)
  {
    assert (this.resolution == null);
    if (!paramBoolean) {
      this.resolution = this;
    }
    assert (isResolved() == paramBoolean);
  }
  
  void checkForTypeAlias()
  {
    Object localObject;
    if (isInvocable())
    {
      if ((this.type instanceof MethodType)) {
        localObject = (MethodType)this.type;
      } else {
        this.type = (localObject = getMethodType());
      }
      if (((MethodType)localObject).erase() == localObject) {
        return;
      }
      if (VerifyAccess.isTypeVisible((MethodType)localObject, this.clazz)) {
        return;
      }
      throw new LinkageError("bad method type alias: " + localObject + " not visible from " + this.clazz);
    }
    if ((this.type instanceof Class)) {
      localObject = (Class)this.type;
    } else {
      this.type = (localObject = getFieldType());
    }
    if (VerifyAccess.isTypeVisible((Class)localObject, this.clazz)) {
      return;
    }
    throw new LinkageError("bad field type alias: " + localObject + " not visible from " + this.clazz);
  }
  
  public String toString()
  {
    if (isType()) {
      return this.type.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (getDeclaringClass() != null)
    {
      localStringBuilder.append(getName(this.clazz));
      localStringBuilder.append('.');
    }
    String str = getName();
    localStringBuilder.append(str == null ? "*" : str);
    Object localObject = getType();
    if (!isInvocable())
    {
      localStringBuilder.append('/');
      localStringBuilder.append(localObject == null ? "*" : getName(localObject));
    }
    else
    {
      localStringBuilder.append(localObject == null ? "(*)*" : getName(localObject));
    }
    byte b = getReferenceKind();
    if (b != 0)
    {
      localStringBuilder.append('/');
      localStringBuilder.append(MethodHandleNatives.refKindName(b));
    }
    return localStringBuilder.toString();
  }
  
  private static String getName(Object paramObject)
  {
    if ((paramObject instanceof Class)) {
      return ((Class)paramObject).getName();
    }
    return String.valueOf(paramObject);
  }
  
  public IllegalAccessException makeAccessException(String paramString, Object paramObject)
  {
    paramString = paramString + ": " + toString();
    if (paramObject != null) {
      paramString = paramString + ", from " + paramObject;
    }
    return new IllegalAccessException(paramString);
  }
  
  private String message()
  {
    if (isResolved()) {
      return "no access";
    }
    if (isConstructor()) {
      return "no such constructor";
    }
    if (isMethod()) {
      return "no such method";
    }
    return "no such field";
  }
  
  public ReflectiveOperationException makeAccessException()
  {
    String str = message() + ": " + toString();
    Object localObject;
    if ((isResolved()) || ((!(this.resolution instanceof NoSuchMethodError)) && (!(this.resolution instanceof NoSuchFieldError)))) {
      localObject = new IllegalAccessException(str);
    } else if (isConstructor()) {
      localObject = new NoSuchMethodException(str);
    } else if (isMethod()) {
      localObject = new NoSuchMethodException(str);
    } else {
      localObject = new NoSuchFieldException(str);
    }
    if ((this.resolution instanceof Throwable)) {
      ((ReflectiveOperationException)localObject).initCause((Throwable)this.resolution);
    }
    return localObject;
  }
  
  static Factory getFactory()
  {
    return Factory.INSTANCE;
  }
  
  static class Factory
  {
    static Factory INSTANCE = new Factory();
    private static int ALLOWED_FLAGS = 983040;
    
    private Factory() {}
    
    List<MemberName> getMembers(Class<?> paramClass1, String paramString, Object paramObject, int paramInt, Class<?> paramClass2)
    {
      paramInt &= ALLOWED_FLAGS;
      String str = null;
      if (paramObject != null)
      {
        str = BytecodeDescriptor.unparse(paramObject);
        if (str.startsWith("(")) {
          paramInt &= 0xFFF3FFFF;
        } else {
          paramInt &= 0xFFF4FFFF;
        }
      }
      int i = paramObject == null ? 4 : paramString == null ? 10 : 1;
      MemberName[] arrayOfMemberName = newMemberBuffer(i);
      int j = 0;
      ArrayList localArrayList1 = null;
      int k = 0;
      for (;;)
      {
        k = MethodHandleNatives.getMembers(paramClass1, paramString, str, paramInt, paramClass2, j, arrayOfMemberName);
        if (k <= arrayOfMemberName.length)
        {
          if (k < 0) {
            k = 0;
          }
          j += k;
          break;
        }
        j += arrayOfMemberName.length;
        int m = k - arrayOfMemberName.length;
        if (localArrayList1 == null) {
          localArrayList1 = new ArrayList(1);
        }
        localArrayList1.add(arrayOfMemberName);
        int n = arrayOfMemberName.length;
        n = Math.max(n, m);
        n = Math.max(n, j / 4);
        arrayOfMemberName = newMemberBuffer(Math.min(8192, n));
      }
      ArrayList localArrayList2 = new ArrayList(j);
      Iterator localIterator;
      Object localObject;
      if (localArrayList1 != null)
      {
        localIterator = localArrayList1.iterator();
        while (localIterator.hasNext())
        {
          localObject = (MemberName[])localIterator.next();
          Collections.addAll(localArrayList2, (Object[])localObject);
        }
      }
      localArrayList2.addAll(Arrays.asList(arrayOfMemberName).subList(0, k));
      if ((paramObject != null) && (paramObject != str))
      {
        localIterator = localArrayList2.iterator();
        while (localIterator.hasNext())
        {
          localObject = (MemberName)localIterator.next();
          if (!paramObject.equals(((MemberName)localObject).getType())) {
            localIterator.remove();
          }
        }
      }
      return localArrayList2;
    }
    
    private MemberName resolve(byte paramByte, MemberName paramMemberName, Class<?> paramClass)
    {
      MemberName localMemberName = paramMemberName.clone();
      assert (paramByte == localMemberName.getReferenceKind());
      try
      {
        localMemberName = MethodHandleNatives.resolve(localMemberName, paramClass);
        localMemberName.checkForTypeAlias();
        localMemberName.resolution = null;
      }
      catch (LinkageError localLinkageError)
      {
        assert (!localMemberName.isResolved());
        localMemberName.resolution = localLinkageError;
        return localMemberName;
      }
      assert (localMemberName.referenceKindIsConsistent());
      localMemberName.initResolved(true);
      assert (localMemberName.vminfoIsConsistent());
      return localMemberName;
    }
    
    public <NoSuchMemberException extends ReflectiveOperationException> MemberName resolveOrFail(byte paramByte, MemberName paramMemberName, Class<?> paramClass, Class<NoSuchMemberException> paramClass1)
      throws IllegalAccessException, ReflectiveOperationException
    {
      MemberName localMemberName = resolve(paramByte, paramMemberName, paramClass);
      if (localMemberName.isResolved()) {
        return localMemberName;
      }
      ReflectiveOperationException localReflectiveOperationException = localMemberName.makeAccessException();
      if ((localReflectiveOperationException instanceof IllegalAccessException)) {
        throw ((IllegalAccessException)localReflectiveOperationException);
      }
      throw ((ReflectiveOperationException)paramClass1.cast(localReflectiveOperationException));
    }
    
    public MemberName resolveOrNull(byte paramByte, MemberName paramMemberName, Class<?> paramClass)
    {
      MemberName localMemberName = resolve(paramByte, paramMemberName, paramClass);
      if (localMemberName.isResolved()) {
        return localMemberName;
      }
      return null;
    }
    
    public List<MemberName> getMethods(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
    {
      return getMethods(paramClass1, paramBoolean, null, null, paramClass2);
    }
    
    public List<MemberName> getMethods(Class<?> paramClass1, boolean paramBoolean, String paramString, MethodType paramMethodType, Class<?> paramClass2)
    {
      int i = 0x10000 | (paramBoolean ? 3145728 : 0);
      return getMembers(paramClass1, paramString, paramMethodType, i, paramClass2);
    }
    
    public List<MemberName> getConstructors(Class<?> paramClass1, Class<?> paramClass2)
    {
      return getMembers(paramClass1, null, null, 131072, paramClass2);
    }
    
    public List<MemberName> getFields(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
    {
      return getFields(paramClass1, paramBoolean, null, null, paramClass2);
    }
    
    public List<MemberName> getFields(Class<?> paramClass1, boolean paramBoolean, String paramString, Class<?> paramClass2, Class<?> paramClass3)
    {
      int i = 0x40000 | (paramBoolean ? 3145728 : 0);
      return getMembers(paramClass1, paramString, paramClass2, i, paramClass3);
    }
    
    public List<MemberName> getNestedTypes(Class<?> paramClass1, boolean paramBoolean, Class<?> paramClass2)
    {
      int i = 0x80000 | (paramBoolean ? 3145728 : 0);
      return getMembers(paramClass1, null, null, i, paramClass2);
    }
    
    private static MemberName[] newMemberBuffer(int paramInt)
    {
      MemberName[] arrayOfMemberName = new MemberName[paramInt];
      for (int i = 0; i < paramInt; i++) {
        arrayOfMemberName[i] = new MemberName();
      }
      return arrayOfMemberName;
    }
  }
}
