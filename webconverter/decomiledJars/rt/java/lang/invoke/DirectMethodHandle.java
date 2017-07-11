package java.lang.invoke;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;

class DirectMethodHandle
  extends MethodHandle
{
  final MemberName member;
  private static final MemberName.Factory IMPL_NAMES = MemberName.getFactory();
  private static byte AF_GETFIELD = 0;
  private static byte AF_PUTFIELD = 1;
  private static byte AF_GETSTATIC = 2;
  private static byte AF_PUTSTATIC = 3;
  private static byte AF_GETSTATIC_INIT = 4;
  private static byte AF_PUTSTATIC_INIT = 5;
  private static byte AF_LIMIT = 6;
  private static int FT_LAST_WRAPPER = Wrapper.values().length - 1;
  private static int FT_UNCHECKED_REF = Wrapper.OBJECT.ordinal();
  private static int FT_CHECKED_REF = FT_LAST_WRAPPER + 1;
  private static int FT_LIMIT = FT_LAST_WRAPPER + 2;
  private static final LambdaForm[] ACCESSOR_FORMS = new LambdaForm[afIndex(AF_LIMIT, false, 0)];
  
  private DirectMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName)
  {
    super(paramMethodType, paramLambdaForm);
    if (!paramMemberName.isResolved()) {
      throw new InternalError();
    }
    if ((paramMemberName.getDeclaringClass().isInterface()) && (paramMemberName.isMethod()) && (!paramMemberName.isAbstract()))
    {
      MemberName localMemberName = new MemberName(Object.class, paramMemberName.getName(), paramMemberName.getMethodType(), paramMemberName.getReferenceKind());
      localMemberName = MemberName.getFactory().resolveOrNull(localMemberName.getReferenceKind(), localMemberName, null);
      if ((localMemberName != null) && (localMemberName.isPublic()))
      {
        assert (paramMemberName.getReferenceKind() == localMemberName.getReferenceKind());
        paramMemberName = localMemberName;
      }
    }
    this.member = paramMemberName;
  }
  
  static DirectMethodHandle make(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
  {
    MethodType localMethodType = paramMemberName.getMethodOrFieldType();
    if (!paramMemberName.isStatic())
    {
      if ((!paramMemberName.getDeclaringClass().isAssignableFrom(paramClass)) || (paramMemberName.isConstructor())) {
        throw new InternalError(paramMemberName.toString());
      }
      localMethodType = localMethodType.insertParameterTypes(0, new Class[] { paramClass });
    }
    if (!paramMemberName.isField())
    {
      if (paramByte == 7)
      {
        paramMemberName = paramMemberName.asSpecial();
        localLambdaForm = preparedLambdaForm(paramMemberName);
        return new Special(localMethodType, localLambdaForm, paramMemberName, null);
      }
      localLambdaForm = preparedLambdaForm(paramMemberName);
      return new DirectMethodHandle(localMethodType, localLambdaForm, paramMemberName);
    }
    LambdaForm localLambdaForm = preparedFieldLambdaForm(paramMemberName);
    if (paramMemberName.isStatic())
    {
      l = MethodHandleNatives.staticFieldOffset(paramMemberName);
      Object localObject = MethodHandleNatives.staticFieldBase(paramMemberName);
      return new StaticAccessor(localMethodType, localLambdaForm, paramMemberName, localObject, l, null);
    }
    long l = MethodHandleNatives.objectFieldOffset(paramMemberName);
    assert (l == (int)l);
    return new Accessor(localMethodType, localLambdaForm, paramMemberName, (int)l, null);
  }
  
  static DirectMethodHandle make(Class<?> paramClass, MemberName paramMemberName)
  {
    byte b = paramMemberName.getReferenceKind();
    if (b == 7) {
      b = 5;
    }
    return make(b, paramClass, paramMemberName);
  }
  
  static DirectMethodHandle make(MemberName paramMemberName)
  {
    if (paramMemberName.isConstructor()) {
      return makeAllocator(paramMemberName);
    }
    return make(paramMemberName.getDeclaringClass(), paramMemberName);
  }
  
  static DirectMethodHandle make(Method paramMethod)
  {
    return make(paramMethod.getDeclaringClass(), new MemberName(paramMethod));
  }
  
  static DirectMethodHandle make(Field paramField)
  {
    return make(paramField.getDeclaringClass(), new MemberName(paramField));
  }
  
  private static DirectMethodHandle makeAllocator(MemberName paramMemberName)
  {
    assert ((paramMemberName.isConstructor()) && (paramMemberName.getName().equals("<init>")));
    Class localClass = paramMemberName.getDeclaringClass();
    paramMemberName = paramMemberName.asConstructor();
    assert ((paramMemberName.isConstructor()) && (paramMemberName.getReferenceKind() == 8)) : paramMemberName;
    MethodType localMethodType = paramMemberName.getMethodType().changeReturnType(localClass);
    LambdaForm localLambdaForm = preparedLambdaForm(paramMemberName);
    MemberName localMemberName = paramMemberName.asSpecial();
    assert (localMemberName.getMethodType().returnType() == Void.TYPE);
    return new Constructor(localMethodType, localLambdaForm, paramMemberName, localMemberName, localClass, null);
  }
  
  BoundMethodHandle rebind()
  {
    return BoundMethodHandle.makeReinvoker(this);
  }
  
  MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    assert (getClass() == DirectMethodHandle.class);
    return new DirectMethodHandle(paramMethodType, paramLambdaForm, this.member);
  }
  
  String internalProperties()
  {
    return "\n& DMH.MN=" + internalMemberName();
  }
  
  @ForceInline
  MemberName internalMemberName()
  {
    return this.member;
  }
  
  private static LambdaForm preparedLambdaForm(MemberName paramMemberName)
  {
    assert (paramMemberName.isInvocable()) : paramMemberName;
    MethodType localMethodType = paramMemberName.getInvocationType().basicType();
    assert ((!paramMemberName.isMethodHandleInvoke()) || ("invokeBasic".equals(paramMemberName.getName()))) : paramMemberName;
    int i;
    switch (paramMemberName.getReferenceKind())
    {
    case 5: 
      i = 0;
      break;
    case 6: 
      i = 1;
      break;
    case 7: 
      i = 2;
      break;
    case 9: 
      i = 4;
      break;
    case 8: 
      i = 3;
      break;
    default: 
      throw new InternalError(paramMemberName.toString());
    }
    if ((i == 1) && (shouldBeInitialized(paramMemberName)))
    {
      preparedLambdaForm(localMethodType, i);
      i = 5;
    }
    LambdaForm localLambdaForm = preparedLambdaForm(localMethodType, i);
    maybeCompile(localLambdaForm, paramMemberName);
    if ((!$assertionsDisabled) && (!localLambdaForm.methodType().dropParameterTypes(0, 1).equals(paramMemberName.getInvocationType().basicType()))) {
      throw new AssertionError(Arrays.asList(new Object[] { paramMemberName, paramMemberName.getInvocationType().basicType(), localLambdaForm, localLambdaForm.methodType() }));
    }
    return localLambdaForm;
  }
  
  private static LambdaForm preparedLambdaForm(MethodType paramMethodType, int paramInt)
  {
    LambdaForm localLambdaForm = paramMethodType.form().cachedLambdaForm(paramInt);
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    localLambdaForm = makePreparedLambdaForm(paramMethodType, paramInt);
    return paramMethodType.form().setCachedLambdaForm(paramInt, localLambdaForm);
  }
  
  private static LambdaForm makePreparedLambdaForm(MethodType paramMethodType, int paramInt)
  {
    int i = paramInt == 5 ? 1 : 0;
    int j = paramInt == 3 ? 1 : 0;
    String str1;
    switch (paramInt)
    {
    case 0: 
      str1 = "linkToVirtual";
      str2 = "DMH.invokeVirtual";
      break;
    case 1: 
      str1 = "linkToStatic";
      str2 = "DMH.invokeStatic";
      break;
    case 5: 
      str1 = "linkToStatic";
      str2 = "DMH.invokeStaticInit";
      break;
    case 2: 
      str1 = "linkToSpecial";
      str2 = "DMH.invokeSpecial";
      break;
    case 4: 
      str1 = "linkToInterface";
      str2 = "DMH.invokeInterface";
      break;
    case 3: 
      str1 = "linkToSpecial";
      str2 = "DMH.newInvokeSpecial";
      break;
    default: 
      throw new InternalError("which=" + paramInt);
    }
    MethodType localMethodType = paramMethodType.appendParameterTypes(new Class[] { MemberName.class });
    if (j != 0) {
      localMethodType = localMethodType.insertParameterTypes(0, new Class[] { Object.class }).changeReturnType(Void.TYPE);
    }
    MemberName localMemberName = new MemberName(MethodHandle.class, str1, localMethodType, (byte)6);
    try
    {
      localMemberName = IMPL_NAMES.resolveOrFail((byte)6, localMemberName, null, NoSuchMethodException.class);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
    int k = 1 + paramMethodType.parameterCount();
    int m = k;
    int n = j != 0 ? m++ : -1;
    int i1 = m++;
    int i2 = m++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(m - k, paramMethodType.invokerType());
    assert (arrayOfName.length == m);
    if (j != 0)
    {
      arrayOfName[n] = new LambdaForm.Name(Lazy.NF_allocateInstance, new Object[] { arrayOfName[0] });
      arrayOfName[i1] = new LambdaForm.Name(Lazy.NF_constructorMethod, new Object[] { arrayOfName[0] });
    }
    else if (i != 0)
    {
      arrayOfName[i1] = new LambdaForm.Name(Lazy.NF_internalMemberNameEnsureInit, new Object[] { arrayOfName[0] });
    }
    else
    {
      arrayOfName[i1] = new LambdaForm.Name(Lazy.NF_internalMemberName, new Object[] { arrayOfName[0] });
    }
    assert (findDirectMethodHandle(arrayOfName[i1]) == arrayOfName[0]);
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 1, i1 + 1, [Ljava.lang.Object.class);
    assert (arrayOfObject[(arrayOfObject.length - 1)] == arrayOfName[i1]);
    int i3 = -2;
    if (j != 0)
    {
      assert (arrayOfObject[(arrayOfObject.length - 2)] == arrayOfName[n]);
      System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, arrayOfObject.length - 2);
      arrayOfObject[0] = arrayOfName[n];
      i3 = n;
    }
    arrayOfName[i2] = new LambdaForm.Name(localMemberName, arrayOfObject);
    String str2 = str2 + "_" + LambdaForm.shortenSignature(LambdaForm.basicTypeSignature(paramMethodType));
    LambdaForm localLambdaForm = new LambdaForm(str2, k, arrayOfName, i3);
    localLambdaForm.compileToBytecode();
    return localLambdaForm;
  }
  
  static Object findDirectMethodHandle(LambdaForm.Name paramName)
  {
    if ((paramName.function == Lazy.NF_internalMemberName) || (paramName.function == Lazy.NF_internalMemberNameEnsureInit) || (paramName.function == Lazy.NF_constructorMethod))
    {
      assert (paramName.arguments.length == 1);
      return paramName.arguments[0];
    }
    return null;
  }
  
  private static void maybeCompile(LambdaForm paramLambdaForm, MemberName paramMemberName)
  {
    if (VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), MethodHandle.class)) {
      paramLambdaForm.compileToBytecode();
    }
  }
  
  @ForceInline
  static Object internalMemberName(Object paramObject)
  {
    return ((DirectMethodHandle)paramObject).member;
  }
  
  static Object internalMemberNameEnsureInit(Object paramObject)
  {
    DirectMethodHandle localDirectMethodHandle = (DirectMethodHandle)paramObject;
    localDirectMethodHandle.ensureInitialized();
    return localDirectMethodHandle.member;
  }
  
  static boolean shouldBeInitialized(MemberName paramMemberName)
  {
    switch (paramMemberName.getReferenceKind())
    {
    case 2: 
    case 4: 
    case 6: 
    case 8: 
      break;
    case 3: 
    case 5: 
    case 7: 
    default: 
      return false;
    }
    Class localClass = paramMemberName.getDeclaringClass();
    if ((localClass == ValueConversions.class) || (localClass == MethodHandleImpl.class) || (localClass == Invokers.class)) {
      return false;
    }
    if ((VerifyAccess.isSamePackage(MethodHandle.class, localClass)) || (VerifyAccess.isSamePackage(ValueConversions.class, localClass)))
    {
      if (MethodHandleStatics.UNSAFE.shouldBeInitialized(localClass)) {
        MethodHandleStatics.UNSAFE.ensureClassInitialized(localClass);
      }
      return false;
    }
    return MethodHandleStatics.UNSAFE.shouldBeInitialized(localClass);
  }
  
  private void ensureInitialized()
  {
    if (checkInitialized(this.member)) {
      if (this.member.isField()) {
        updateForm(preparedFieldLambdaForm(this.member));
      } else {
        updateForm(preparedLambdaForm(this.member));
      }
    }
  }
  
  private static boolean checkInitialized(MemberName paramMemberName)
  {
    Class localClass = paramMemberName.getDeclaringClass();
    WeakReference localWeakReference = (WeakReference)EnsureInitialized.INSTANCE.get(localClass);
    if (localWeakReference == null) {
      return true;
    }
    Thread localThread = (Thread)localWeakReference.get();
    if (localThread == Thread.currentThread())
    {
      if (MethodHandleStatics.UNSAFE.shouldBeInitialized(localClass)) {
        return false;
      }
    }
    else {
      MethodHandleStatics.UNSAFE.ensureClassInitialized(localClass);
    }
    assert (!MethodHandleStatics.UNSAFE.shouldBeInitialized(localClass));
    EnsureInitialized.INSTANCE.remove(localClass);
    return true;
  }
  
  static void ensureInitialized(Object paramObject)
  {
    ((DirectMethodHandle)paramObject).ensureInitialized();
  }
  
  static Object constructorMethod(Object paramObject)
  {
    Constructor localConstructor = (Constructor)paramObject;
    return localConstructor.initMethod;
  }
  
  static Object allocateInstance(Object paramObject)
    throws InstantiationException
  {
    Constructor localConstructor = (Constructor)paramObject;
    return MethodHandleStatics.UNSAFE.allocateInstance(localConstructor.instanceClass);
  }
  
  @ForceInline
  static long fieldOffset(Object paramObject)
  {
    return ((Accessor)paramObject).fieldOffset;
  }
  
  @ForceInline
  static Object checkBase(Object paramObject)
  {
    paramObject.getClass();
    return paramObject;
  }
  
  @ForceInline
  static Object nullCheck(Object paramObject)
  {
    paramObject.getClass();
    return paramObject;
  }
  
  @ForceInline
  static Object staticBase(Object paramObject)
  {
    return ((StaticAccessor)paramObject).staticBase;
  }
  
  @ForceInline
  static long staticOffset(Object paramObject)
  {
    return ((StaticAccessor)paramObject).staticOffset;
  }
  
  @ForceInline
  static Object checkCast(Object paramObject1, Object paramObject2)
  {
    return ((DirectMethodHandle)paramObject1).checkCast(paramObject2);
  }
  
  Object checkCast(Object paramObject)
  {
    return this.member.getReturnType().cast(paramObject);
  }
  
  private static int afIndex(byte paramByte, boolean paramBoolean, int paramInt)
  {
    return paramByte * FT_LIMIT * 2 + (paramBoolean ? FT_LIMIT : 0) + paramInt;
  }
  
  private static int ftypeKind(Class<?> paramClass)
  {
    if (paramClass.isPrimitive()) {
      return Wrapper.forPrimitiveType(paramClass).ordinal();
    }
    if (VerifyType.isNullReferenceConversion(Object.class, paramClass)) {
      return FT_UNCHECKED_REF;
    }
    return FT_CHECKED_REF;
  }
  
  private static LambdaForm preparedFieldLambdaForm(MemberName paramMemberName)
  {
    Class localClass = paramMemberName.getFieldType();
    boolean bool = paramMemberName.isVolatile();
    int i;
    switch (paramMemberName.getReferenceKind())
    {
    case 1: 
      i = AF_GETFIELD;
      break;
    case 3: 
      i = AF_PUTFIELD;
      break;
    case 2: 
      i = AF_GETSTATIC;
      break;
    case 4: 
      i = AF_PUTSTATIC;
      break;
    default: 
      throw new InternalError(paramMemberName.toString());
    }
    byte b;
    if (shouldBeInitialized(paramMemberName))
    {
      preparedFieldLambdaForm(i, bool, localClass);
      assert (AF_GETSTATIC_INIT - AF_GETSTATIC == AF_PUTSTATIC_INIT - AF_PUTSTATIC);
      b = (byte)(i + (AF_GETSTATIC_INIT - AF_GETSTATIC));
    }
    LambdaForm localLambdaForm = preparedFieldLambdaForm(b, bool, localClass);
    maybeCompile(localLambdaForm, paramMemberName);
    if ((!$assertionsDisabled) && (!localLambdaForm.methodType().dropParameterTypes(0, 1).equals(paramMemberName.getInvocationType().basicType()))) {
      throw new AssertionError(Arrays.asList(new Object[] { paramMemberName, paramMemberName.getInvocationType().basicType(), localLambdaForm, localLambdaForm.methodType() }));
    }
    return localLambdaForm;
  }
  
  private static LambdaForm preparedFieldLambdaForm(byte paramByte, boolean paramBoolean, Class<?> paramClass)
  {
    int i = afIndex(paramByte, paramBoolean, ftypeKind(paramClass));
    LambdaForm localLambdaForm = ACCESSOR_FORMS[i];
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    localLambdaForm = makePreparedFieldLambdaForm(paramByte, paramBoolean, ftypeKind(paramClass));
    ACCESSOR_FORMS[i] = localLambdaForm;
    return localLambdaForm;
  }
  
  private static LambdaForm makePreparedFieldLambdaForm(byte paramByte, boolean paramBoolean, int paramInt)
  {
    int i = (paramByte & 0x1) == (AF_GETFIELD & 0x1) ? 1 : 0;
    int j = paramByte >= AF_GETSTATIC ? 1 : 0;
    int k = paramByte >= AF_GETSTATIC_INIT ? 1 : 0;
    int m = paramInt == FT_CHECKED_REF ? 1 : 0;
    Wrapper localWrapper = m != 0 ? Wrapper.OBJECT : Wrapper.values()[paramInt];
    Class localClass = localWrapper.primitiveType();
    if (!$assertionsDisabled) {
      if (ftypeKind(m != 0 ? String.class : localClass) != paramInt) {
        throw new AssertionError();
      }
    }
    String str1 = localWrapper.primitiveSimpleName();
    String str2 = Character.toUpperCase(str1.charAt(0)) + str1.substring(1);
    if (paramBoolean) {
      str2 = str2 + "Volatile";
    }
    String str3 = i != 0 ? "get" : "put";
    String str4 = str3 + str2;
    MethodType localMethodType1;
    if (i != 0) {
      localMethodType1 = MethodType.methodType(localClass, Object.class, new Class[] { Long.TYPE });
    } else {
      localMethodType1 = MethodType.methodType(Void.TYPE, Object.class, new Class[] { Long.TYPE, localClass });
    }
    MemberName localMemberName = new MemberName(Unsafe.class, str4, localMethodType1, (byte)5);
    try
    {
      localMemberName = IMPL_NAMES.resolveOrFail((byte)5, localMemberName, null, NoSuchMethodException.class);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
    if (i != 0) {
      localMethodType2 = MethodType.methodType(localClass);
    } else {
      localMethodType2 = MethodType.methodType(Void.TYPE, localClass);
    }
    MethodType localMethodType2 = localMethodType2.basicType();
    if (j == 0) {
      localMethodType2 = localMethodType2.insertParameterTypes(0, new Class[] { Object.class });
    }
    int n = 1 + localMethodType2.parameterCount();
    int i1 = j != 0 ? -1 : 1;
    int i2 = i != 0 ? -1 : n - 1;
    int i3 = n;
    int i4 = j != 0 ? i3++ : -1;
    int i5 = i3++;
    int i6 = i1 >= 0 ? i3++ : -1;
    int i7 = k != 0 ? i3++ : -1;
    int i8 = (m != 0) && (i == 0) ? i3++ : -1;
    int i9 = i3++;
    int i10 = (m != 0) && (i != 0) ? i3++ : -1;
    int i11 = i3 - 1;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(i3 - n, localMethodType2.invokerType());
    if (k != 0) {
      arrayOfName[i7] = new LambdaForm.Name(Lazy.NF_ensureInitialized, new Object[] { arrayOfName[0] });
    }
    if ((m != 0) && (i == 0)) {
      arrayOfName[i8] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arrayOfName[0], arrayOfName[i2] });
    }
    Object[] arrayOfObject = new Object[1 + localMethodType1.parameterCount()];
    if (!$assertionsDisabled) {
      if (arrayOfObject.length != (i != 0 ? 3 : 4)) {
        throw new AssertionError();
      }
    }
    arrayOfObject[0] = MethodHandleStatics.UNSAFE;
    if (j != 0)
    {
      void tmp680_677 = new LambdaForm.Name(Lazy.NF_staticBase, new Object[] { arrayOfName[0] });
      arrayOfName[i4] = tmp680_677;
      arrayOfObject[1] = tmp680_677;
      void tmp711_708 = new LambdaForm.Name(Lazy.NF_staticOffset, new Object[] { arrayOfName[0] });
      arrayOfName[i5] = tmp711_708;
      arrayOfObject[2] = tmp711_708;
    }
    else
    {
      void tmp746_743 = new LambdaForm.Name(Lazy.NF_checkBase, new Object[] { arrayOfName[i1] });
      arrayOfName[i6] = tmp746_743;
      arrayOfObject[1] = tmp746_743;
      void tmp777_774 = new LambdaForm.Name(Lazy.NF_fieldOffset, new Object[] { arrayOfName[0] });
      arrayOfName[i5] = tmp777_774;
      arrayOfObject[2] = tmp777_774;
    }
    if (i == 0) {
      arrayOfObject[3] = (m != 0 ? arrayOfName[i8] : arrayOfName[i2]);
    }
    Object localObject2;
    for (localObject2 : arrayOfObject) {
      assert (localObject2 != null);
    }
    arrayOfName[i9] = new LambdaForm.Name(localMemberName, arrayOfObject);
    if ((m != 0) && (i != 0)) {
      arrayOfName[i10] = new LambdaForm.Name(Lazy.NF_checkCast, new Object[] { arrayOfName[0], arrayOfName[i9] });
    }
    for (localObject2 : arrayOfName) {
      assert (localObject2 != null);
    }
    ??? = j != 0 ? "Static" : "Field";
    String str5 = str4 + (String)???;
    if (m != 0) {
      str5 = str5 + "Cast";
    }
    if (k != 0) {
      str5 = str5 + "Init";
    }
    return new LambdaForm(str5, n, arrayOfName, i11);
  }
  
  static class Accessor
    extends DirectMethodHandle
  {
    final Class<?> fieldType;
    final int fieldOffset;
    
    private Accessor(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName, int paramInt)
    {
      super(paramLambdaForm, paramMemberName, null);
      this.fieldType = paramMemberName.getFieldType();
      this.fieldOffset = paramInt;
    }
    
    Object checkCast(Object paramObject)
    {
      return this.fieldType.cast(paramObject);
    }
    
    MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
    {
      return new Accessor(paramMethodType, paramLambdaForm, this.member, this.fieldOffset);
    }
  }
  
  static class Constructor
    extends DirectMethodHandle
  {
    final MemberName initMethod;
    final Class<?> instanceClass;
    
    private Constructor(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName1, MemberName paramMemberName2, Class<?> paramClass)
    {
      super(paramLambdaForm, paramMemberName1, null);
      this.initMethod = paramMemberName2;
      this.instanceClass = paramClass;
      assert (paramMemberName2.isResolved());
    }
    
    MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
    {
      return new Constructor(paramMethodType, paramLambdaForm, this.member, this.initMethod, this.instanceClass);
    }
  }
  
  private static class EnsureInitialized
    extends ClassValue<WeakReference<Thread>>
  {
    static final EnsureInitialized INSTANCE = new EnsureInitialized();
    
    private EnsureInitialized() {}
    
    protected WeakReference<Thread> computeValue(Class<?> paramClass)
    {
      MethodHandleStatics.UNSAFE.ensureClassInitialized(paramClass);
      if (MethodHandleStatics.UNSAFE.shouldBeInitialized(paramClass)) {
        return new WeakReference(Thread.currentThread());
      }
      return null;
    }
  }
  
  private static class Lazy
  {
    static final LambdaForm.NamedFunction NF_internalMemberName;
    static final LambdaForm.NamedFunction NF_internalMemberNameEnsureInit;
    static final LambdaForm.NamedFunction NF_ensureInitialized;
    static final LambdaForm.NamedFunction NF_fieldOffset;
    static final LambdaForm.NamedFunction NF_checkBase;
    static final LambdaForm.NamedFunction NF_staticBase;
    static final LambdaForm.NamedFunction NF_staticOffset;
    static final LambdaForm.NamedFunction NF_checkCast;
    static final LambdaForm.NamedFunction NF_allocateInstance;
    static final LambdaForm.NamedFunction NF_constructorMethod;
    
    private Lazy() {}
    
    static
    {
      try
      {
        LambdaForm.NamedFunction[] tmp21_18 = new LambdaForm.NamedFunction[10];
        void tmp46_43 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberName", new Class[] { Object.class }));
        NF_internalMemberName = tmp46_43;
        tmp21_18[0] = tmp46_43;
        LambdaForm.NamedFunction[] tmp51_21 = tmp21_18;
        void tmp76_73 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("internalMemberNameEnsureInit", new Class[] { Object.class }));
        NF_internalMemberNameEnsureInit = tmp76_73;
        tmp51_21[1] = tmp76_73;
        LambdaForm.NamedFunction[] tmp81_51 = tmp51_21;
        void tmp106_103 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("ensureInitialized", new Class[] { Object.class }));
        NF_ensureInitialized = tmp106_103;
        tmp81_51[2] = tmp106_103;
        LambdaForm.NamedFunction[] tmp111_81 = tmp81_51;
        void tmp136_133 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("fieldOffset", new Class[] { Object.class }));
        NF_fieldOffset = tmp136_133;
        tmp111_81[3] = tmp136_133;
        LambdaForm.NamedFunction[] tmp141_111 = tmp111_81;
        void tmp166_163 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkBase", new Class[] { Object.class }));
        NF_checkBase = tmp166_163;
        tmp141_111[4] = tmp166_163;
        LambdaForm.NamedFunction[] tmp171_141 = tmp141_111;
        void tmp196_193 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticBase", new Class[] { Object.class }));
        NF_staticBase = tmp196_193;
        tmp171_141[5] = tmp196_193;
        LambdaForm.NamedFunction[] tmp201_171 = tmp171_141;
        void tmp227_224 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("staticOffset", new Class[] { Object.class }));
        NF_staticOffset = tmp227_224;
        tmp201_171[6] = tmp227_224;
        LambdaForm.NamedFunction[] tmp232_201 = tmp201_171;
        void tmp263_260 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("checkCast", new Class[] { Object.class, Object.class }));
        NF_checkCast = tmp263_260;
        tmp232_201[7] = tmp263_260;
        LambdaForm.NamedFunction[] tmp268_232 = tmp232_201;
        void tmp294_291 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("allocateInstance", new Class[] { Object.class }));
        NF_allocateInstance = tmp294_291;
        tmp268_232[8] = tmp294_291;
        LambdaForm.NamedFunction[] tmp299_268 = tmp268_232;
        void tmp325_322 = new LambdaForm.NamedFunction(DirectMethodHandle.class.getDeclaredMethod("constructorMethod", new Class[] { Object.class }));
        NF_constructorMethod = tmp325_322;
        tmp299_268[9] = tmp325_322;
        LambdaForm.NamedFunction[] arrayOfNamedFunction1 = tmp299_268;
        for (LambdaForm.NamedFunction localNamedFunction : arrayOfNamedFunction1)
        {
          assert (InvokerBytecodeGenerator.isStaticallyInvocable(localNamedFunction.member)) : localNamedFunction;
          localNamedFunction.resolve();
        }
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
  }
  
  static class Special
    extends DirectMethodHandle
  {
    private Special(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName)
    {
      super(paramLambdaForm, paramMemberName, null);
    }
    
    boolean isInvokeSpecial()
    {
      return true;
    }
    
    MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
    {
      return new Special(paramMethodType, paramLambdaForm, this.member);
    }
  }
  
  static class StaticAccessor
    extends DirectMethodHandle
  {
    private final Class<?> fieldType;
    private final Object staticBase;
    private final long staticOffset;
    
    private StaticAccessor(MethodType paramMethodType, LambdaForm paramLambdaForm, MemberName paramMemberName, Object paramObject, long paramLong)
    {
      super(paramLambdaForm, paramMemberName, null);
      this.fieldType = paramMemberName.getFieldType();
      this.staticBase = paramObject;
      this.staticOffset = paramLong;
    }
    
    Object checkCast(Object paramObject)
    {
      return this.fieldType.cast(paramObject);
    }
    
    MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
    {
      return new StaticAccessor(paramMethodType, paramLambdaForm, this.member, this.staticBase, this.staticOffset);
    }
  }
}
