package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyAccess;
import sun.invoke.util.Wrapper;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class MethodHandles
{
  private static final MemberName.Factory IMPL_NAMES;
  private static final Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");
  private static final MethodHandle[] IDENTITY_MHS = new MethodHandle[Wrapper.values().length];
  private static final MethodHandle[] ZERO_MHS = new MethodHandle[Wrapper.values().length];
  
  private MethodHandles() {}
  
  @CallerSensitive
  public static Lookup lookup()
  {
    return new Lookup(Reflection.getCallerClass());
  }
  
  public static Lookup publicLookup()
  {
    return Lookup.PUBLIC_LOOKUP;
  }
  
  public static <T extends Member> T reflectAs(Class<T> paramClass, MethodHandle paramMethodHandle)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(ACCESS_PERMISSION);
    }
    Lookup localLookup = Lookup.IMPL_LOOKUP;
    return localLookup.revealDirect(paramMethodHandle).reflectAs(paramClass, localLookup);
  }
  
  public static MethodHandle arrayElementGetter(Class<?> paramClass)
    throws IllegalArgumentException
  {
    return MethodHandleImpl.makeArrayElementAccessor(paramClass, false);
  }
  
  public static MethodHandle arrayElementSetter(Class<?> paramClass)
    throws IllegalArgumentException
  {
    return MethodHandleImpl.makeArrayElementAccessor(paramClass, true);
  }
  
  public static MethodHandle spreadInvoker(MethodType paramMethodType, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > paramMethodType.parameterCount())) {
      throw MethodHandleStatics.newIllegalArgumentException("bad argument count", Integer.valueOf(paramInt));
    }
    paramMethodType = paramMethodType.asSpreaderType([Ljava.lang.Object.class, paramMethodType.parameterCount() - paramInt);
    return paramMethodType.invokers().spreadInvoker(paramInt);
  }
  
  public static MethodHandle exactInvoker(MethodType paramMethodType)
  {
    return paramMethodType.invokers().exactInvoker();
  }
  
  public static MethodHandle invoker(MethodType paramMethodType)
  {
    return paramMethodType.invokers().genericInvoker();
  }
  
  static MethodHandle basicInvoker(MethodType paramMethodType)
  {
    return paramMethodType.invokers().basicInvoker();
  }
  
  public static MethodHandle explicitCastArguments(MethodHandle paramMethodHandle, MethodType paramMethodType)
  {
    explicitCastArgumentsChecks(paramMethodHandle, paramMethodType);
    MethodType localMethodType = paramMethodHandle.type();
    if (localMethodType == paramMethodType) {
      return paramMethodHandle;
    }
    if (localMethodType.explicitCastEquivalentToAsType(paramMethodType)) {
      return paramMethodHandle.asFixedArity().asType(paramMethodType);
    }
    return MethodHandleImpl.makePairwiseConvert(paramMethodHandle, paramMethodType, false);
  }
  
  private static void explicitCastArgumentsChecks(MethodHandle paramMethodHandle, MethodType paramMethodType)
  {
    if (paramMethodHandle.type().parameterCount() != paramMethodType.parameterCount()) {
      throw new WrongMethodTypeException("cannot explicitly cast " + paramMethodHandle + " to " + paramMethodType);
    }
  }
  
  public static MethodHandle permuteArguments(MethodHandle paramMethodHandle, MethodType paramMethodType, int... paramVarArgs)
  {
    paramVarArgs = (int[])paramVarArgs.clone();
    MethodType localMethodType = paramMethodHandle.type();
    permuteArgumentChecks(paramVarArgs, paramMethodType, localMethodType);
    int[] arrayOfInt = paramVarArgs;
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle.rebind();
    LambdaForm localLambdaForm = localBoundMethodHandle.form;
    int i = paramMethodType.parameterCount();
    int j;
    while ((j = findFirstDupOrDrop(paramVarArgs, i)) != 0)
    {
      int k;
      int m;
      int i1;
      if (j > 0)
      {
        k = j;
        m = k;
        int n = paramVarArgs[k];
        i1 = 0;
        while ((i2 = paramVarArgs[(--m)]) != n) {
          if (n > i2) {
            i1 = 1;
          }
        }
        if (i1 == 0)
        {
          k = m;
          m = j;
        }
        localLambdaForm = localLambdaForm.editor().dupArgumentForm(1 + k, 1 + m);
        assert (paramVarArgs[k] == paramVarArgs[m]);
        localMethodType = localMethodType.dropParameterTypes(m, m + 1);
        int i2 = m + 1;
        System.arraycopy(paramVarArgs, i2, paramVarArgs, m, paramVarArgs.length - i2);
        paramVarArgs = Arrays.copyOf(paramVarArgs, paramVarArgs.length - 1);
      }
      else
      {
        k = j ^ 0xFFFFFFFF;
        for (m = 0; (m < paramVarArgs.length) && (paramVarArgs[m] < k); m++) {}
        Class localClass = paramMethodType.parameterType(k);
        localLambdaForm = localLambdaForm.editor().addArgumentForm(1 + m, LambdaForm.BasicType.basicType(localClass));
        localMethodType = localMethodType.insertParameterTypes(m, new Class[] { localClass });
        i1 = m + 1;
        paramVarArgs = Arrays.copyOf(paramVarArgs, paramVarArgs.length + 1);
        System.arraycopy(paramVarArgs, m, paramVarArgs, i1, paramVarArgs.length - i1);
        paramVarArgs[m] = k;
      }
      if ((!$assertionsDisabled) && (!permuteArgumentChecks(paramVarArgs, paramMethodType, localMethodType))) {
        throw new AssertionError();
      }
    }
    assert (paramVarArgs.length == i);
    localLambdaForm = localLambdaForm.editor().permuteArgumentsForm(1, paramVarArgs);
    if ((paramMethodType == localBoundMethodHandle.type()) && (localLambdaForm == localBoundMethodHandle.internalForm())) {
      return localBoundMethodHandle;
    }
    return localBoundMethodHandle.copyWith(paramMethodType, localLambdaForm);
  }
  
  private static int findFirstDupOrDrop(int[] paramArrayOfInt, int paramInt)
  {
    if (paramInt < 63)
    {
      long l1 = 0L;
      for (int j = 0; j < paramArrayOfInt.length; j++)
      {
        int m = paramArrayOfInt[j];
        if (m >= paramInt) {
          return paramArrayOfInt.length;
        }
        long l3 = 1L << m;
        if ((l1 & l3) != 0L) {
          return j;
        }
        l1 |= l3;
      }
      if (l1 == (1L << paramInt) - 1L)
      {
        assert (Long.numberOfTrailingZeros(Long.lowestOneBit(l1 ^ 0xFFFFFFFFFFFFFFFF)) == paramInt);
        return 0;
      }
      long l2 = Long.lowestOneBit(l1 ^ 0xFFFFFFFFFFFFFFFF);
      int n = Long.numberOfTrailingZeros(l2);
      assert (n <= paramInt);
      if (n == paramInt) {
        return 0;
      }
      return n ^ 0xFFFFFFFF;
    }
    BitSet localBitSet = new BitSet(paramInt);
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      int k = paramArrayOfInt[i];
      if (k >= paramInt) {
        return paramArrayOfInt.length;
      }
      if (localBitSet.get(k)) {
        return i;
      }
      localBitSet.set(k);
    }
    i = localBitSet.nextClearBit(0);
    assert (i <= paramInt);
    if (i == paramInt) {
      return 0;
    }
    return i ^ 0xFFFFFFFF;
  }
  
  private static boolean permuteArgumentChecks(int[] paramArrayOfInt, MethodType paramMethodType1, MethodType paramMethodType2)
  {
    if (paramMethodType1.returnType() != paramMethodType2.returnType()) {
      throw MethodHandleStatics.newIllegalArgumentException("return types do not match", paramMethodType2, paramMethodType1);
    }
    if (paramArrayOfInt.length == paramMethodType2.parameterCount())
    {
      int i = paramMethodType1.parameterCount();
      int j = 0;
      for (int k = 0; k < paramArrayOfInt.length; k++)
      {
        int m = paramArrayOfInt[k];
        if ((m < 0) || (m >= i))
        {
          j = 1;
          break;
        }
        Class localClass1 = paramMethodType1.parameterType(m);
        Class localClass2 = paramMethodType2.parameterType(k);
        if (localClass1 != localClass2) {
          throw MethodHandleStatics.newIllegalArgumentException("parameter types do not match after reorder", paramMethodType2, paramMethodType1);
        }
      }
      if (j == 0) {
        return true;
      }
    }
    throw MethodHandleStatics.newIllegalArgumentException("bad reorder array: " + Arrays.toString(paramArrayOfInt));
  }
  
  public static MethodHandle constant(Class<?> paramClass, Object paramObject)
  {
    if (paramClass.isPrimitive())
    {
      if (paramClass == Void.TYPE) {
        throw MethodHandleStatics.newIllegalArgumentException("void type");
      }
      Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
      paramObject = localWrapper.convert(paramObject, paramClass);
      if (localWrapper.zero().equals(paramObject)) {
        return zero(localWrapper, paramClass);
      }
      return insertArguments(identity(paramClass), 0, new Object[] { paramObject });
    }
    if (paramObject == null) {
      return zero(Wrapper.OBJECT, paramClass);
    }
    return identity(paramClass).bindTo(paramObject);
  }
  
  public static MethodHandle identity(Class<?> paramClass)
  {
    Wrapper localWrapper = paramClass.isPrimitive() ? Wrapper.forPrimitiveType(paramClass) : Wrapper.OBJECT;
    int i = localWrapper.ordinal();
    MethodHandle localMethodHandle = IDENTITY_MHS[i];
    if (localMethodHandle == null) {
      localMethodHandle = setCachedMethodHandle(IDENTITY_MHS, i, makeIdentity(localWrapper.primitiveType()));
    }
    if (localMethodHandle.type().returnType() == paramClass) {
      return localMethodHandle;
    }
    assert (localWrapper == Wrapper.OBJECT);
    return makeIdentity(paramClass);
  }
  
  private static MethodHandle makeIdentity(Class<?> paramClass)
  {
    MethodType localMethodType = MethodType.methodType(paramClass, paramClass);
    LambdaForm localLambdaForm = LambdaForm.identityForm(LambdaForm.BasicType.basicType(paramClass));
    return MethodHandleImpl.makeIntrinsic(localMethodType, localLambdaForm, MethodHandleImpl.Intrinsic.IDENTITY);
  }
  
  private static MethodHandle zero(Wrapper paramWrapper, Class<?> paramClass)
  {
    int i = paramWrapper.ordinal();
    MethodHandle localMethodHandle = ZERO_MHS[i];
    if (localMethodHandle == null) {
      localMethodHandle = setCachedMethodHandle(ZERO_MHS, i, makeZero(paramWrapper.primitiveType()));
    }
    if (localMethodHandle.type().returnType() == paramClass) {
      return localMethodHandle;
    }
    assert (paramWrapper == Wrapper.OBJECT);
    return makeZero(paramClass);
  }
  
  private static MethodHandle makeZero(Class<?> paramClass)
  {
    MethodType localMethodType = MethodType.methodType(paramClass);
    LambdaForm localLambdaForm = LambdaForm.zeroForm(LambdaForm.BasicType.basicType(paramClass));
    return MethodHandleImpl.makeIntrinsic(localMethodType, localLambdaForm, MethodHandleImpl.Intrinsic.ZERO);
  }
  
  private static synchronized MethodHandle setCachedMethodHandle(MethodHandle[] paramArrayOfMethodHandle, int paramInt, MethodHandle paramMethodHandle)
  {
    MethodHandle localMethodHandle = paramArrayOfMethodHandle[paramInt];
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    return paramArrayOfMethodHandle[paramInt] =  = paramMethodHandle;
  }
  
  public static MethodHandle insertArguments(MethodHandle paramMethodHandle, int paramInt, Object... paramVarArgs)
  {
    int i = paramVarArgs.length;
    Class[] arrayOfClass = insertArgumentsChecks(paramMethodHandle, i, paramInt);
    if (i == 0) {
      return paramMethodHandle;
    }
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle.rebind();
    for (int j = 0; j < i; j++)
    {
      Object localObject = paramVarArgs[j];
      Class localClass = arrayOfClass[(paramInt + j)];
      if (localClass.isPrimitive())
      {
        localBoundMethodHandle = insertArgumentPrimitive(localBoundMethodHandle, paramInt, localClass, localObject);
      }
      else
      {
        localObject = localClass.cast(localObject);
        localBoundMethodHandle = localBoundMethodHandle.bindArgumentL(paramInt, localObject);
      }
    }
    return localBoundMethodHandle;
  }
  
  private static BoundMethodHandle insertArgumentPrimitive(BoundMethodHandle paramBoundMethodHandle, int paramInt, Class<?> paramClass, Object paramObject)
  {
    Wrapper localWrapper = Wrapper.forPrimitiveType(paramClass);
    paramObject = localWrapper.convert(paramObject, paramClass);
    switch (1.$SwitchMap$sun$invoke$util$Wrapper[localWrapper.ordinal()])
    {
    case 1: 
      return paramBoundMethodHandle.bindArgumentI(paramInt, ((Integer)paramObject).intValue());
    case 2: 
      return paramBoundMethodHandle.bindArgumentJ(paramInt, ((Long)paramObject).longValue());
    case 3: 
      return paramBoundMethodHandle.bindArgumentF(paramInt, ((Float)paramObject).floatValue());
    case 4: 
      return paramBoundMethodHandle.bindArgumentD(paramInt, ((Double)paramObject).doubleValue());
    }
    return paramBoundMethodHandle.bindArgumentI(paramInt, ValueConversions.widenSubword(paramObject));
  }
  
  private static Class<?>[] insertArgumentsChecks(MethodHandle paramMethodHandle, int paramInt1, int paramInt2)
    throws RuntimeException
  {
    MethodType localMethodType = paramMethodHandle.type();
    int i = localMethodType.parameterCount();
    int j = i - paramInt1;
    if (j < 0) {
      throw MethodHandleStatics.newIllegalArgumentException("too many values to insert");
    }
    if ((paramInt2 < 0) || (paramInt2 > j)) {
      throw MethodHandleStatics.newIllegalArgumentException("no argument type to append");
    }
    return localMethodType.ptypes();
  }
  
  public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, List<Class<?>> paramList)
  {
    MethodType localMethodType1 = paramMethodHandle.type();
    int i = dropArgumentChecks(localMethodType1, paramInt, paramList);
    MethodType localMethodType2 = localMethodType1.insertParameterTypes(paramInt, paramList);
    if (i == 0) {
      return paramMethodHandle;
    }
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle.rebind();
    LambdaForm localLambdaForm = localBoundMethodHandle.form;
    int j = 1 + paramInt;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      localLambdaForm = localLambdaForm.editor().addArgumentForm(j++, LambdaForm.BasicType.basicType(localClass));
    }
    localBoundMethodHandle = localBoundMethodHandle.copyWith(localMethodType2, localLambdaForm);
    return localBoundMethodHandle;
  }
  
  private static int dropArgumentChecks(MethodType paramMethodType, int paramInt, List<Class<?>> paramList)
  {
    int i = paramList.size();
    MethodType.checkSlotCount(i);
    int j = paramMethodType.parameterCount();
    int k = j + i;
    if ((paramInt < 0) || (paramInt > j)) {
      throw MethodHandleStatics.newIllegalArgumentException("no argument type to remove" + Arrays.asList(new Object[] { paramMethodType, Integer.valueOf(paramInt), paramList, Integer.valueOf(k), Integer.valueOf(j) }));
    }
    return i;
  }
  
  public static MethodHandle dropArguments(MethodHandle paramMethodHandle, int paramInt, Class<?>... paramVarArgs)
  {
    return dropArguments(paramMethodHandle, paramInt, Arrays.asList(paramVarArgs));
  }
  
  public static MethodHandle filterArguments(MethodHandle paramMethodHandle, int paramInt, MethodHandle... paramVarArgs)
  {
    filterArgumentsCheckArity(paramMethodHandle, paramInt, paramVarArgs);
    MethodHandle localMethodHandle1 = paramMethodHandle;
    int i = paramInt - 1;
    for (MethodHandle localMethodHandle2 : paramVarArgs)
    {
      i++;
      if (localMethodHandle2 != null) {
        localMethodHandle1 = filterArgument(localMethodHandle1, i, localMethodHandle2);
      }
    }
    return localMethodHandle1;
  }
  
  static MethodHandle filterArgument(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
  {
    filterArgumentChecks(paramMethodHandle1, paramInt, paramMethodHandle2);
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle1.rebind();
    Class localClass = localMethodType2.parameterType(0);
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().filterArgumentForm(1 + paramInt, LambdaForm.BasicType.basicType(localClass));
    MethodType localMethodType3 = localMethodType1.changeParameterType(paramInt, localClass);
    localBoundMethodHandle = localBoundMethodHandle.copyWithExtendL(localMethodType3, localLambdaForm, paramMethodHandle2);
    return localBoundMethodHandle;
  }
  
  private static void filterArgumentsCheckArity(MethodHandle paramMethodHandle, int paramInt, MethodHandle[] paramArrayOfMethodHandle)
  {
    MethodType localMethodType = paramMethodHandle.type();
    int i = localMethodType.parameterCount();
    if (paramInt + paramArrayOfMethodHandle.length > i) {
      throw MethodHandleStatics.newIllegalArgumentException("too many filters");
    }
  }
  
  private static void filterArgumentChecks(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
    throws RuntimeException
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    if ((localMethodType2.parameterCount() != 1) || (localMethodType2.returnType() != localMethodType1.parameterType(paramInt))) {
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", localMethodType1, localMethodType2);
    }
  }
  
  public static MethodHandle collectArguments(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
  {
    MethodType localMethodType1 = collectArgumentsChecks(paramMethodHandle1, paramInt, paramMethodHandle2);
    MethodType localMethodType2 = paramMethodHandle2.type();
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle1.rebind();
    if ((localMethodType2.returnType().isArray()) && (paramMethodHandle2.intrinsicName() == MethodHandleImpl.Intrinsic.NEW_ARRAY))
    {
      localLambdaForm = localBoundMethodHandle.editor().collectArgumentArrayForm(1 + paramInt, paramMethodHandle2);
      if (localLambdaForm != null) {
        return localBoundMethodHandle.copyWith(localMethodType1, localLambdaForm);
      }
    }
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().collectArgumentsForm(1 + paramInt, localMethodType2.basicType());
    return localBoundMethodHandle.copyWithExtendL(localMethodType1, localLambdaForm, paramMethodHandle2);
  }
  
  private static MethodType collectArgumentsChecks(MethodHandle paramMethodHandle1, int paramInt, MethodHandle paramMethodHandle2)
    throws RuntimeException
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    Class localClass = localMethodType2.returnType();
    List localList = localMethodType2.parameterList();
    if (localClass == Void.TYPE) {
      return localMethodType1.insertParameterTypes(paramInt, localList);
    }
    if (localClass != localMethodType1.parameterType(paramInt)) {
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", localMethodType1, localMethodType2);
    }
    return localMethodType1.dropParameterTypes(paramInt, paramInt + 1).insertParameterTypes(paramInt, localList);
  }
  
  public static MethodHandle filterReturnValue(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    filterReturnValueChecks(localMethodType1, localMethodType2);
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle1.rebind();
    LambdaForm.BasicType localBasicType = LambdaForm.BasicType.basicType(localMethodType2.returnType());
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().filterReturnForm(localBasicType, false);
    MethodType localMethodType3 = localMethodType1.changeReturnType(localMethodType2.returnType());
    localBoundMethodHandle = localBoundMethodHandle.copyWithExtendL(localMethodType3, localLambdaForm, paramMethodHandle2);
    return localBoundMethodHandle;
  }
  
  private static void filterReturnValueChecks(MethodType paramMethodType1, MethodType paramMethodType2)
    throws RuntimeException
  {
    Class localClass = paramMethodType1.returnType();
    int i = paramMethodType2.parameterCount();
    if (i == 0 ? localClass != Void.TYPE : localClass != paramMethodType2.parameterType(0)) {
      throw MethodHandleStatics.newIllegalArgumentException("target and filter types do not match", paramMethodType1, paramMethodType2);
    }
  }
  
  public static MethodHandle foldArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
  {
    int i = 0;
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    Class localClass = foldArgumentChecks(i, localMethodType1, localMethodType2);
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle1.rebind();
    boolean bool = localClass == Void.TYPE;
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().foldArgumentsForm(1 + i, bool, localMethodType2.basicType());
    MethodType localMethodType3 = localMethodType1;
    if (!bool) {
      localMethodType3 = localMethodType3.dropParameterTypes(i, i + 1);
    }
    localBoundMethodHandle = localBoundMethodHandle.copyWithExtendL(localMethodType3, localLambdaForm, paramMethodHandle2);
    return localBoundMethodHandle;
  }
  
  private static Class<?> foldArgumentChecks(int paramInt, MethodType paramMethodType1, MethodType paramMethodType2)
  {
    int i = paramMethodType2.parameterCount();
    Class localClass = paramMethodType2.returnType();
    int j = localClass == Void.TYPE ? 0 : 1;
    int k = paramInt + j;
    int m = paramMethodType1.parameterCount() >= k + i ? 1 : 0;
    if ((m != 0) && (!paramMethodType2.parameterList().equals(paramMethodType1.parameterList().subList(k, k + i)))) {
      m = 0;
    }
    if ((m != 0) && (j != 0) && (paramMethodType2.returnType() != paramMethodType1.parameterType(0))) {
      m = 0;
    }
    if (m == 0) {
      throw misMatchedTypes("target and combiner types", paramMethodType1, paramMethodType2);
    }
    return localClass;
  }
  
  public static MethodHandle guardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    MethodType localMethodType3 = paramMethodHandle3.type();
    if (!localMethodType2.equals(localMethodType3)) {
      throw misMatchedTypes("target and fallback types", localMethodType2, localMethodType3);
    }
    if (localMethodType1.returnType() != Boolean.TYPE) {
      throw MethodHandleStatics.newIllegalArgumentException("guard type is not a predicate " + localMethodType1);
    }
    List localList1 = localMethodType2.parameterList();
    List localList2 = localMethodType1.parameterList();
    if (!localList1.equals(localList2))
    {
      int i = localList2.size();
      int j = localList1.size();
      if ((i >= j) || (!localList1.subList(0, i).equals(localList2))) {
        throw misMatchedTypes("target and test types", localMethodType2, localMethodType1);
      }
      paramMethodHandle1 = dropArguments(paramMethodHandle1, i, localList1.subList(i, j));
      localMethodType1 = paramMethodHandle1.type();
    }
    return MethodHandleImpl.makeGuardWithTest(paramMethodHandle1, paramMethodHandle2, paramMethodHandle3);
  }
  
  static RuntimeException misMatchedTypes(String paramString, MethodType paramMethodType1, MethodType paramMethodType2)
  {
    return MethodHandleStatics.newIllegalArgumentException(paramString + " must match: " + paramMethodType1 + " != " + paramMethodType2);
  }
  
  public static MethodHandle catchException(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    if ((localMethodType2.parameterCount() < 1) || (!localMethodType2.parameterType(0).isAssignableFrom(paramClass))) {
      throw MethodHandleStatics.newIllegalArgumentException("handler does not accept exception type " + paramClass);
    }
    if (localMethodType2.returnType() != localMethodType1.returnType()) {
      throw misMatchedTypes("target and handler return types", localMethodType1, localMethodType2);
    }
    List localList1 = localMethodType1.parameterList();
    List localList2 = localMethodType2.parameterList();
    localList2 = localList2.subList(1, localList2.size());
    if (!localList1.equals(localList2))
    {
      int i = localList2.size();
      int j = localList1.size();
      if ((i >= j) || (!localList1.subList(0, i).equals(localList2))) {
        throw misMatchedTypes("target and handler types", localMethodType1, localMethodType2);
      }
      paramMethodHandle2 = dropArguments(paramMethodHandle2, 1 + i, localList1.subList(i, j));
      localMethodType2 = paramMethodHandle2.type();
    }
    return MethodHandleImpl.makeGuardWithCatch(paramMethodHandle1, paramClass, paramMethodHandle2);
  }
  
  public static MethodHandle throwException(Class<?> paramClass, Class<? extends Throwable> paramClass1)
  {
    if (!Throwable.class.isAssignableFrom(paramClass1)) {
      throw new ClassCastException(paramClass1.getName());
    }
    return MethodHandleImpl.throwException(MethodType.methodType(paramClass, paramClass1));
  }
  
  static
  {
    IMPL_NAMES = MemberName.getFactory();
    MethodHandleImpl.initStatics();
  }
  
  public static final class Lookup
  {
    private final Class<?> lookupClass;
    private final int allowedModes;
    public static final int PUBLIC = 1;
    public static final int PRIVATE = 2;
    public static final int PROTECTED = 4;
    public static final int PACKAGE = 8;
    private static final int ALL_MODES = 15;
    private static final int TRUSTED = -1;
    static final Lookup PUBLIC_LOOKUP = new Lookup(Object.class, 1);
    static final Lookup IMPL_LOOKUP = new Lookup(Object.class, -1);
    private static final boolean ALLOW_NESTMATE_ACCESS = false;
    static ConcurrentHashMap<MemberName, DirectMethodHandle> LOOKASIDE_TABLE = new ConcurrentHashMap();
    
    private static int fixmods(int paramInt)
    {
      paramInt &= 0x7;
      return paramInt != 0 ? paramInt : 8;
    }
    
    public Class<?> lookupClass()
    {
      return this.lookupClass;
    }
    
    private Class<?> lookupClassOrNull()
    {
      return this.allowedModes == -1 ? null : this.lookupClass;
    }
    
    public int lookupModes()
    {
      return this.allowedModes & 0xF;
    }
    
    Lookup(Class<?> paramClass)
    {
      this(paramClass, 15);
      checkUnprivilegedlookupClass(paramClass, 15);
    }
    
    private Lookup(Class<?> paramClass, int paramInt)
    {
      this.lookupClass = paramClass;
      this.allowedModes = paramInt;
    }
    
    public Lookup in(Class<?> paramClass)
    {
      paramClass.getClass();
      if (this.allowedModes == -1) {
        return new Lookup(paramClass, 15);
      }
      if (paramClass == this.lookupClass) {
        return this;
      }
      int i = this.allowedModes & 0xB;
      if (((i & 0x8) != 0) && (!VerifyAccess.isSamePackage(this.lookupClass, paramClass))) {
        i &= 0xFFFFFFF5;
      }
      if (((i & 0x2) != 0) && (!VerifyAccess.isSamePackageMember(this.lookupClass, paramClass))) {
        i &= 0xFFFFFFFD;
      }
      if (((i & 0x1) != 0) && (!VerifyAccess.isClassAccessible(paramClass, this.lookupClass, this.allowedModes))) {
        i = 0;
      }
      checkUnprivilegedlookupClass(paramClass, i);
      return new Lookup(paramClass, i);
    }
    
    private static void checkUnprivilegedlookupClass(Class<?> paramClass, int paramInt)
    {
      String str = paramClass.getName();
      if (str.startsWith("java.lang.invoke.")) {
        throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + paramClass);
      }
      if ((paramInt == 15) && (paramClass.getClassLoader() == null) && ((str.startsWith("java.")) || ((str.startsWith("sun.")) && (!str.startsWith("sun.invoke."))))) {
        throw MethodHandleStatics.newIllegalArgumentException("illegal lookupClass: " + paramClass);
      }
    }
    
    public String toString()
    {
      String str = this.lookupClass.getName();
      switch (this.allowedModes)
      {
      case 0: 
        return str + "/noaccess";
      case 1: 
        return str + "/public";
      case 9: 
        return str + "/package";
      case 11: 
        return str + "/private";
      case 15: 
        return str;
      case -1: 
        return "/trusted";
      }
      str = str + "/" + Integer.toHexString(this.allowedModes);
      if (!$assertionsDisabled) {
        throw new AssertionError(str);
      }
      return str;
    }
    
    public MethodHandle findStatic(Class<?> paramClass, String paramString, MethodType paramMethodType)
      throws NoSuchMethodException, IllegalAccessException
    {
      MemberName localMemberName = resolveOrFail((byte)6, paramClass, paramString, paramMethodType);
      return getDirectMethod((byte)6, paramClass, localMemberName, findBoundCallerClass(localMemberName));
    }
    
    public MethodHandle findVirtual(Class<?> paramClass, String paramString, MethodType paramMethodType)
      throws NoSuchMethodException, IllegalAccessException
    {
      if (paramClass == MethodHandle.class)
      {
        MethodHandle localMethodHandle = findVirtualForMH(paramString, paramMethodType);
        if (localMethodHandle != null) {
          return localMethodHandle;
        }
      }
      byte b = paramClass.isInterface() ? 9 : 5;
      MemberName localMemberName = resolveOrFail(b, paramClass, paramString, paramMethodType);
      return getDirectMethod(b, paramClass, localMemberName, findBoundCallerClass(localMemberName));
    }
    
    private MethodHandle findVirtualForMH(String paramString, MethodType paramMethodType)
    {
      if ("invoke".equals(paramString)) {
        return MethodHandles.invoker(paramMethodType);
      }
      if ("invokeExact".equals(paramString)) {
        return MethodHandles.exactInvoker(paramMethodType);
      }
      if ("invokeBasic".equals(paramString)) {
        return MethodHandles.basicInvoker(paramMethodType);
      }
      assert (!MemberName.isMethodHandleInvokeName(paramString));
      return null;
    }
    
    public MethodHandle findConstructor(Class<?> paramClass, MethodType paramMethodType)
      throws NoSuchMethodException, IllegalAccessException
    {
      String str = "<init>";
      MemberName localMemberName = resolveOrFail((byte)8, paramClass, str, paramMethodType);
      return getDirectConstructor(paramClass, localMemberName);
    }
    
    public MethodHandle findSpecial(Class<?> paramClass1, String paramString, MethodType paramMethodType, Class<?> paramClass2)
      throws NoSuchMethodException, IllegalAccessException
    {
      checkSpecialCaller(paramClass2);
      Lookup localLookup = in(paramClass2);
      MemberName localMemberName = localLookup.resolveOrFail((byte)7, paramClass1, paramString, paramMethodType);
      return localLookup.getDirectMethod((byte)7, paramClass1, localMemberName, findBoundCallerClass(localMemberName));
    }
    
    public MethodHandle findGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
      throws NoSuchFieldException, IllegalAccessException
    {
      MemberName localMemberName = resolveOrFail((byte)1, paramClass1, paramString, paramClass2);
      return getDirectField((byte)1, paramClass1, localMemberName);
    }
    
    public MethodHandle findSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
      throws NoSuchFieldException, IllegalAccessException
    {
      MemberName localMemberName = resolveOrFail((byte)3, paramClass1, paramString, paramClass2);
      return getDirectField((byte)3, paramClass1, localMemberName);
    }
    
    public MethodHandle findStaticGetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
      throws NoSuchFieldException, IllegalAccessException
    {
      MemberName localMemberName = resolveOrFail((byte)2, paramClass1, paramString, paramClass2);
      return getDirectField((byte)2, paramClass1, localMemberName);
    }
    
    public MethodHandle findStaticSetter(Class<?> paramClass1, String paramString, Class<?> paramClass2)
      throws NoSuchFieldException, IllegalAccessException
    {
      MemberName localMemberName = resolveOrFail((byte)4, paramClass1, paramString, paramClass2);
      return getDirectField((byte)4, paramClass1, localMemberName);
    }
    
    public MethodHandle bind(Object paramObject, String paramString, MethodType paramMethodType)
      throws NoSuchMethodException, IllegalAccessException
    {
      Class localClass = paramObject.getClass();
      MemberName localMemberName = resolveOrFail((byte)7, localClass, paramString, paramMethodType);
      MethodHandle localMethodHandle = getDirectMethodNoRestrict((byte)7, localClass, localMemberName, findBoundCallerClass(localMemberName));
      return localMethodHandle.bindArgumentL(0, paramObject).setVarargs(localMemberName);
    }
    
    public MethodHandle unreflect(Method paramMethod)
      throws IllegalAccessException
    {
      if (paramMethod.getDeclaringClass() == MethodHandle.class)
      {
        localObject = unreflectForMH(paramMethod);
        if (localObject != null) {
          return localObject;
        }
      }
      Object localObject = new MemberName(paramMethod);
      byte b = ((MemberName)localObject).getReferenceKind();
      if (b == 7) {
        b = 5;
      }
      assert (((MemberName)localObject).isMethod());
      Lookup localLookup = paramMethod.isAccessible() ? IMPL_LOOKUP : this;
      return localLookup.getDirectMethodNoSecurityManager(b, ((MemberName)localObject).getDeclaringClass(), (MemberName)localObject, findBoundCallerClass((MemberName)localObject));
    }
    
    private MethodHandle unreflectForMH(Method paramMethod)
    {
      if (MemberName.isMethodHandleInvokeName(paramMethod.getName())) {
        return MethodHandleImpl.fakeMethodHandleInvoke(new MemberName(paramMethod));
      }
      return null;
    }
    
    public MethodHandle unreflectSpecial(Method paramMethod, Class<?> paramClass)
      throws IllegalAccessException
    {
      checkSpecialCaller(paramClass);
      Lookup localLookup = in(paramClass);
      MemberName localMemberName = new MemberName(paramMethod, true);
      assert (localMemberName.isMethod());
      return localLookup.getDirectMethodNoSecurityManager((byte)7, localMemberName.getDeclaringClass(), localMemberName, findBoundCallerClass(localMemberName));
    }
    
    public MethodHandle unreflectConstructor(Constructor<?> paramConstructor)
      throws IllegalAccessException
    {
      MemberName localMemberName = new MemberName(paramConstructor);
      assert (localMemberName.isConstructor());
      Lookup localLookup = paramConstructor.isAccessible() ? IMPL_LOOKUP : this;
      return localLookup.getDirectConstructorNoSecurityManager(localMemberName.getDeclaringClass(), localMemberName);
    }
    
    public MethodHandle unreflectGetter(Field paramField)
      throws IllegalAccessException
    {
      return unreflectField(paramField, false);
    }
    
    private MethodHandle unreflectField(Field paramField, boolean paramBoolean)
      throws IllegalAccessException
    {
      MemberName localMemberName = new MemberName(paramField, paramBoolean);
      assert (paramBoolean ? MethodHandleNatives.refKindIsSetter(localMemberName.getReferenceKind()) : MethodHandleNatives.refKindIsGetter(localMemberName.getReferenceKind()));
      Lookup localLookup = paramField.isAccessible() ? IMPL_LOOKUP : this;
      return localLookup.getDirectFieldNoSecurityManager(localMemberName.getReferenceKind(), paramField.getDeclaringClass(), localMemberName);
    }
    
    public MethodHandle unreflectSetter(Field paramField)
      throws IllegalAccessException
    {
      return unreflectField(paramField, true);
    }
    
    public MethodHandleInfo revealDirect(MethodHandle paramMethodHandle)
    {
      MemberName localMemberName = paramMethodHandle.internalMemberName();
      if ((localMemberName == null) || ((!localMemberName.isResolved()) && (!localMemberName.isMethodHandleInvoke()))) {
        throw MethodHandleStatics.newIllegalArgumentException("not a direct method handle");
      }
      Class localClass1 = localMemberName.getDeclaringClass();
      byte b = localMemberName.getReferenceKind();
      assert (MethodHandleNatives.refKindIsValid(b));
      if ((b == 7) && (!paramMethodHandle.isInvokeSpecial())) {
        b = 5;
      }
      if ((b == 5) && (localClass1.isInterface())) {
        b = 9;
      }
      try
      {
        checkAccess(b, localClass1, localMemberName);
        checkSecurityManager(localClass1, localMemberName);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new IllegalArgumentException(localIllegalAccessException);
      }
      if ((this.allowedModes != -1) && (localMemberName.isCallerSensitive()))
      {
        Class localClass2 = paramMethodHandle.internalCallerClass();
        if ((!hasPrivateAccess()) || (localClass2 != lookupClass())) {
          throw new IllegalArgumentException("method handle is caller sensitive: " + localClass2);
        }
      }
      return new InfoFromMemberName(this, localMemberName, b);
    }
    
    MemberName resolveOrFail(byte paramByte, Class<?> paramClass1, String paramString, Class<?> paramClass2)
      throws NoSuchFieldException, IllegalAccessException
    {
      checkSymbolicClass(paramClass1);
      paramString.getClass();
      paramClass2.getClass();
      return MethodHandles.IMPL_NAMES.resolveOrFail(paramByte, new MemberName(paramClass1, paramString, paramClass2, paramByte), lookupClassOrNull(), NoSuchFieldException.class);
    }
    
    MemberName resolveOrFail(byte paramByte, Class<?> paramClass, String paramString, MethodType paramMethodType)
      throws NoSuchMethodException, IllegalAccessException
    {
      checkSymbolicClass(paramClass);
      paramString.getClass();
      paramMethodType.getClass();
      checkMethodName(paramByte, paramString);
      return MethodHandles.IMPL_NAMES.resolveOrFail(paramByte, new MemberName(paramClass, paramString, paramMethodType, paramByte), lookupClassOrNull(), NoSuchMethodException.class);
    }
    
    MemberName resolveOrFail(byte paramByte, MemberName paramMemberName)
      throws ReflectiveOperationException
    {
      checkSymbolicClass(paramMemberName.getDeclaringClass());
      paramMemberName.getName().getClass();
      paramMemberName.getType().getClass();
      return MethodHandles.IMPL_NAMES.resolveOrFail(paramByte, paramMemberName, lookupClassOrNull(), ReflectiveOperationException.class);
    }
    
    void checkSymbolicClass(Class<?> paramClass)
      throws IllegalAccessException
    {
      paramClass.getClass();
      Class localClass = lookupClassOrNull();
      if ((localClass != null) && (!VerifyAccess.isClassAccessible(paramClass, localClass, this.allowedModes))) {
        throw new MemberName(paramClass).makeAccessException("symbolic reference class is not public", this);
      }
    }
    
    void checkMethodName(byte paramByte, String paramString)
      throws NoSuchMethodException
    {
      if ((paramString.startsWith("<")) && (paramByte != 8)) {
        throw new NoSuchMethodException("illegal method name: " + paramString);
      }
    }
    
    Class<?> findBoundCallerClass(MemberName paramMemberName)
      throws IllegalAccessException
    {
      Class localClass = null;
      if (MethodHandleNatives.isCallerSensitive(paramMemberName)) {
        if (hasPrivateAccess()) {
          localClass = this.lookupClass;
        } else {
          throw new IllegalAccessException("Attempt to lookup caller-sensitive method using restricted lookup object");
        }
      }
      return localClass;
    }
    
    private boolean hasPrivateAccess()
    {
      return (this.allowedModes & 0x2) != 0;
    }
    
    void checkSecurityManager(Class<?> paramClass, MemberName paramMemberName)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager == null) {
        return;
      }
      if (this.allowedModes == -1) {
        return;
      }
      boolean bool = hasPrivateAccess();
      if ((!bool) || (!VerifyAccess.classLoaderIsAncestor(this.lookupClass, paramClass))) {
        ReflectUtil.checkPackageAccess(paramClass);
      }
      if (paramMemberName.isPublic()) {
        return;
      }
      if (!bool) {
        localSecurityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
      }
      Class localClass = paramMemberName.getDeclaringClass();
      if ((!bool) && (localClass != paramClass)) {
        ReflectUtil.checkPackageAccess(localClass);
      }
    }
    
    void checkMethod(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      int i = paramByte == 6 ? 1 : 0;
      String str;
      if (paramMemberName.isConstructor())
      {
        str = "expected a method, not a constructor";
      }
      else if (!paramMemberName.isMethod())
      {
        str = "expected a method";
      }
      else if (i != paramMemberName.isStatic())
      {
        str = i != 0 ? "expected a static method" : "expected a non-static method";
      }
      else
      {
        checkAccess(paramByte, paramClass, paramMemberName);
        return;
      }
      throw paramMemberName.makeAccessException(str, this);
    }
    
    void checkField(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      int i = !MethodHandleNatives.refKindHasReceiver(paramByte) ? 1 : 0;
      String str;
      if (i != paramMemberName.isStatic())
      {
        str = i != 0 ? "expected a static field" : "expected a non-static field";
      }
      else
      {
        checkAccess(paramByte, paramClass, paramMemberName);
        return;
      }
      throw paramMemberName.makeAccessException(str, this);
    }
    
    void checkAccess(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      assert ((paramMemberName.referenceKindIsConsistentWith(paramByte)) && (MethodHandleNatives.refKindIsValid(paramByte)) && (MethodHandleNatives.refKindIsField(paramByte) == paramMemberName.isField()));
      int i = this.allowedModes;
      if (i == -1) {
        return;
      }
      int j = paramMemberName.getModifiers();
      if ((Modifier.isProtected(j)) && (paramByte == 5) && (paramMemberName.getDeclaringClass() == Object.class) && (paramMemberName.getName().equals("clone")) && (paramClass.isArray())) {
        j ^= 0x5;
      }
      if ((Modifier.isProtected(j)) && (paramByte == 8)) {
        j ^= 0x4;
      }
      if ((Modifier.isFinal(j)) && (MethodHandleNatives.refKindIsSetter(paramByte))) {
        throw paramMemberName.makeAccessException("unexpected set of a final field", this);
      }
      if ((Modifier.isPublic(j)) && (Modifier.isPublic(paramClass.getModifiers())) && (i != 0)) {
        return;
      }
      int k = fixmods(j);
      if ((k & i) != 0)
      {
        if (!VerifyAccess.isMemberAccessible(paramClass, paramMemberName.getDeclaringClass(), j, lookupClass(), i)) {}
      }
      else if (((k & 0x4) != 0) && ((i & 0x8) != 0) && (VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), lookupClass()))) {
        return;
      }
      throw paramMemberName.makeAccessException(accessFailedMessage(paramClass, paramMemberName), this);
    }
    
    String accessFailedMessage(Class<?> paramClass, MemberName paramMemberName)
    {
      Class localClass = paramMemberName.getDeclaringClass();
      int i = paramMemberName.getModifiers();
      int j = (Modifier.isPublic(localClass.getModifiers())) && ((localClass == paramClass) || (Modifier.isPublic(paramClass.getModifiers()))) ? 1 : 0;
      if ((j == 0) && ((this.allowedModes & 0x8) != 0)) {
        j = (VerifyAccess.isClassAccessible(localClass, lookupClass(), 15)) && ((localClass == paramClass) || (VerifyAccess.isClassAccessible(paramClass, lookupClass(), 15))) ? 1 : 0;
      }
      if (j == 0) {
        return "class is not public";
      }
      if (Modifier.isPublic(i)) {
        return "access to public member failed";
      }
      if (Modifier.isPrivate(i)) {
        return "member is private";
      }
      if (Modifier.isProtected(i)) {
        return "member is protected";
      }
      return "member is private to package";
    }
    
    private void checkSpecialCaller(Class<?> paramClass)
      throws IllegalAccessException
    {
      int i = this.allowedModes;
      if (i == -1) {
        return;
      }
      if ((!hasPrivateAccess()) || (paramClass != lookupClass())) {
        throw new MemberName(paramClass).makeAccessException("no private access for invokespecial", this);
      }
    }
    
    private boolean restrictProtectedReceiver(MemberName paramMemberName)
    {
      return (paramMemberName.isProtected()) && (!paramMemberName.isStatic()) && (this.allowedModes != -1) && (paramMemberName.getDeclaringClass() != lookupClass()) && (!VerifyAccess.isSamePackage(paramMemberName.getDeclaringClass(), lookupClass()));
    }
    
    private MethodHandle restrictReceiver(MemberName paramMemberName, DirectMethodHandle paramDirectMethodHandle, Class<?> paramClass)
      throws IllegalAccessException
    {
      assert (!paramMemberName.isStatic());
      if (!paramMemberName.getDeclaringClass().isAssignableFrom(paramClass)) {
        throw paramMemberName.makeAccessException("caller class must be a subclass below the method", paramClass);
      }
      MethodType localMethodType1 = paramDirectMethodHandle.type();
      if (localMethodType1.parameterType(0) == paramClass) {
        return paramDirectMethodHandle;
      }
      MethodType localMethodType2 = localMethodType1.changeParameterType(0, paramClass);
      assert (!paramDirectMethodHandle.isVarargsCollector());
      assert (paramDirectMethodHandle.viewAsTypeChecks(localMethodType2, true));
      return paramDirectMethodHandle.copyWith(localMethodType2, paramDirectMethodHandle.form);
    }
    
    private MethodHandle getDirectMethod(byte paramByte, Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2)
      throws IllegalAccessException
    {
      return getDirectMethodCommon(paramByte, paramClass1, paramMemberName, true, true, paramClass2);
    }
    
    private MethodHandle getDirectMethodNoRestrict(byte paramByte, Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2)
      throws IllegalAccessException
    {
      return getDirectMethodCommon(paramByte, paramClass1, paramMemberName, true, false, paramClass2);
    }
    
    private MethodHandle getDirectMethodNoSecurityManager(byte paramByte, Class<?> paramClass1, MemberName paramMemberName, Class<?> paramClass2)
      throws IllegalAccessException
    {
      return getDirectMethodCommon(paramByte, paramClass1, paramMemberName, false, true, paramClass2);
    }
    
    private MethodHandle getDirectMethodCommon(byte paramByte, Class<?> paramClass1, MemberName paramMemberName, boolean paramBoolean1, boolean paramBoolean2, Class<?> paramClass2)
      throws IllegalAccessException
    {
      checkMethod(paramByte, paramClass1, paramMemberName);
      if (paramBoolean1) {
        checkSecurityManager(paramClass1, paramMemberName);
      }
      assert (!paramMemberName.isMethodHandleInvoke());
      if ((paramByte == 7) && (paramClass1 != lookupClass()) && (!paramClass1.isInterface()) && (paramClass1 != lookupClass().getSuperclass()) && (paramClass1.isAssignableFrom(lookupClass())))
      {
        assert (!paramMemberName.getName().equals("<init>"));
        localObject1 = lookupClass();
        do
        {
          localObject1 = ((Class)localObject1).getSuperclass();
          localObject2 = new MemberName((Class)localObject1, paramMemberName.getName(), paramMemberName.getMethodType(), (byte)7);
          localObject2 = MethodHandles.IMPL_NAMES.resolveOrNull(paramByte, (MemberName)localObject2, lookupClassOrNull());
        } while ((localObject2 == null) && (paramClass1 != localObject1));
        if (localObject2 == null) {
          throw new InternalError(paramMemberName.toString());
        }
        paramMemberName = (MemberName)localObject2;
        paramClass1 = (Class<?>)localObject1;
        checkMethod(paramByte, paramClass1, paramMemberName);
      }
      Object localObject1 = DirectMethodHandle.make(paramByte, paramClass1, paramMemberName);
      Object localObject2 = localObject1;
      if ((paramBoolean2) && ((paramByte == 7) || ((MethodHandleNatives.refKindHasReceiver(paramByte)) && (restrictProtectedReceiver(paramMemberName))))) {
        localObject2 = restrictReceiver(paramMemberName, (DirectMethodHandle)localObject1, lookupClass());
      }
      localObject2 = maybeBindCaller(paramMemberName, (MethodHandle)localObject2, paramClass2);
      localObject2 = ((MethodHandle)localObject2).setVarargs(paramMemberName);
      return localObject2;
    }
    
    private MethodHandle maybeBindCaller(MemberName paramMemberName, MethodHandle paramMethodHandle, Class<?> paramClass)
      throws IllegalAccessException
    {
      if ((this.allowedModes == -1) || (!MethodHandleNatives.isCallerSensitive(paramMemberName))) {
        return paramMethodHandle;
      }
      Object localObject = this.lookupClass;
      if (!hasPrivateAccess()) {
        localObject = paramClass;
      }
      MethodHandle localMethodHandle = MethodHandleImpl.bindCaller(paramMethodHandle, (Class)localObject);
      return localMethodHandle;
    }
    
    private MethodHandle getDirectField(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      return getDirectFieldCommon(paramByte, paramClass, paramMemberName, true);
    }
    
    private MethodHandle getDirectFieldNoSecurityManager(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      return getDirectFieldCommon(paramByte, paramClass, paramMemberName, false);
    }
    
    private MethodHandle getDirectFieldCommon(byte paramByte, Class<?> paramClass, MemberName paramMemberName, boolean paramBoolean)
      throws IllegalAccessException
    {
      checkField(paramByte, paramClass, paramMemberName);
      if (paramBoolean) {
        checkSecurityManager(paramClass, paramMemberName);
      }
      DirectMethodHandle localDirectMethodHandle = DirectMethodHandle.make(paramClass, paramMemberName);
      int i = (MethodHandleNatives.refKindHasReceiver(paramByte)) && (restrictProtectedReceiver(paramMemberName)) ? 1 : 0;
      if (i != 0) {
        return restrictReceiver(paramMemberName, localDirectMethodHandle, lookupClass());
      }
      return localDirectMethodHandle;
    }
    
    private MethodHandle getDirectConstructor(Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      return getDirectConstructorCommon(paramClass, paramMemberName, true);
    }
    
    private MethodHandle getDirectConstructorNoSecurityManager(Class<?> paramClass, MemberName paramMemberName)
      throws IllegalAccessException
    {
      return getDirectConstructorCommon(paramClass, paramMemberName, false);
    }
    
    private MethodHandle getDirectConstructorCommon(Class<?> paramClass, MemberName paramMemberName, boolean paramBoolean)
      throws IllegalAccessException
    {
      assert (paramMemberName.isConstructor());
      checkAccess((byte)8, paramClass, paramMemberName);
      if (paramBoolean) {
        checkSecurityManager(paramClass, paramMemberName);
      }
      assert (!MethodHandleNatives.isCallerSensitive(paramMemberName));
      return DirectMethodHandle.make(paramMemberName).setVarargs(paramMemberName);
    }
    
    MethodHandle linkMethodHandleConstant(byte paramByte, Class<?> paramClass, String paramString, Object paramObject)
      throws ReflectiveOperationException
    {
      if ((!(paramObject instanceof Class)) && (!(paramObject instanceof MethodType))) {
        throw new InternalError("unresolved MemberName");
      }
      MemberName localMemberName1 = new MemberName(paramByte, paramClass, paramString, paramObject);
      MethodHandle localMethodHandle = (MethodHandle)LOOKASIDE_TABLE.get(localMemberName1);
      if (localMethodHandle != null)
      {
        checkSymbolicClass(paramClass);
        return localMethodHandle;
      }
      if ((paramClass == MethodHandle.class) && (paramByte == 5))
      {
        localMethodHandle = findVirtualForMH(localMemberName1.getName(), localMemberName1.getMethodType());
        if (localMethodHandle != null) {
          return localMethodHandle;
        }
      }
      MemberName localMemberName2 = resolveOrFail(paramByte, localMemberName1);
      localMethodHandle = getDirectMethodForConstant(paramByte, paramClass, localMemberName2);
      if (((localMethodHandle instanceof DirectMethodHandle)) && (canBeCached(paramByte, paramClass, localMemberName2)))
      {
        MemberName localMemberName3 = localMethodHandle.internalMemberName();
        if (localMemberName3 != null) {
          localMemberName3 = localMemberName3.asNormalOriginal();
        }
        if (localMemberName1.equals(localMemberName3)) {
          LOOKASIDE_TABLE.put(localMemberName3, (DirectMethodHandle)localMethodHandle);
        }
      }
      return localMethodHandle;
    }
    
    private boolean canBeCached(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
    {
      if (paramByte == 7) {
        return false;
      }
      if ((!Modifier.isPublic(paramClass.getModifiers())) || (!Modifier.isPublic(paramMemberName.getDeclaringClass().getModifiers())) || (!paramMemberName.isPublic()) || (paramMemberName.isCallerSensitive())) {
        return false;
      }
      ClassLoader localClassLoader = paramClass.getClassLoader();
      Object localObject;
      if (!VM.isSystemDomainLoader(localClassLoader))
      {
        localObject = ClassLoader.getSystemClassLoader();
        int i = 0;
        while (localObject != null)
        {
          if (localClassLoader == localObject)
          {
            i = 1;
            break;
          }
          localObject = ((ClassLoader)localObject).getParent();
        }
        if (i == 0) {
          return false;
        }
      }
      try
      {
        localObject = MethodHandles.publicLookup().resolveOrFail(paramByte, new MemberName(paramByte, paramClass, paramMemberName.getName(), paramMemberName.getType()));
        checkSecurityManager(paramClass, (MemberName)localObject);
      }
      catch (ReflectiveOperationException|SecurityException localReflectiveOperationException)
      {
        return false;
      }
      return true;
    }
    
    private MethodHandle getDirectMethodForConstant(byte paramByte, Class<?> paramClass, MemberName paramMemberName)
      throws ReflectiveOperationException
    {
      if (MethodHandleNatives.refKindIsField(paramByte)) {
        return getDirectFieldNoSecurityManager(paramByte, paramClass, paramMemberName);
      }
      if (MethodHandleNatives.refKindIsMethod(paramByte)) {
        return getDirectMethodNoSecurityManager(paramByte, paramClass, paramMemberName, this.lookupClass);
      }
      if (paramByte == 8) {
        return getDirectConstructorNoSecurityManager(paramClass, paramMemberName);
      }
      throw MethodHandleStatics.newIllegalArgumentException("bad MethodHandle constant #" + paramMemberName);
    }
    
    static
    {
      MethodHandles.IMPL_NAMES.getClass();
    }
  }
}
