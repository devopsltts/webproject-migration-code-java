package java.lang.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import sun.invoke.empty.Empty;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.VerifyType;
import sun.invoke.util.Wrapper;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

abstract class MethodHandleImpl
{
  private static final int MAX_ARITY = ((Integer)arrayOfObject[0]).intValue();
  private static final Function<MethodHandle, LambdaForm> PRODUCE_BLOCK_INLINING_FORM = new Function()
  {
    public LambdaForm apply(MethodHandle paramAnonymousMethodHandle)
    {
      return DelegatingMethodHandle.makeReinvokerForm(paramAnonymousMethodHandle, 9, MethodHandleImpl.CountingWrapper.class, "reinvoker.dontInline", false, DelegatingMethodHandle.NF_getTarget, MethodHandleImpl.CountingWrapper.NF_maybeStopCounting);
    }
  };
  private static final Function<MethodHandle, LambdaForm> PRODUCE_REINVOKER_FORM = new Function()
  {
    public LambdaForm apply(MethodHandle paramAnonymousMethodHandle)
    {
      return DelegatingMethodHandle.makeReinvokerForm(paramAnonymousMethodHandle, 8, DelegatingMethodHandle.class, DelegatingMethodHandle.NF_getTarget);
    }
  };
  static MethodHandle[] FAKE_METHOD_HANDLE_INVOKE = new MethodHandle[2];
  private static final Object[] NO_ARGS_ARRAY = new Object[0];
  private static final int FILL_ARRAYS_COUNT = 11;
  private static final int LEFT_ARGS = 10;
  private static final MethodHandle[] FILL_ARRAY_TO_RIGHT = new MethodHandle[MAX_ARITY + 1];
  private static final ClassValue<MethodHandle[]> TYPED_COLLECTORS = new ClassValue()
  {
    protected MethodHandle[] computeValue(Class<?> paramAnonymousClass)
    {
      return new MethodHandle['Ā'];
    }
  };
  static final int MAX_JVM_ARITY = 255;
  
  MethodHandleImpl() {}
  
  static void initStatics()
  {
    MemberName.Factory.INSTANCE.getClass();
  }
  
  static MethodHandle makeArrayElementAccessor(Class<?> paramClass, boolean paramBoolean)
  {
    if (paramClass == [Ljava.lang.Object.class) {
      return paramBoolean ? ArrayAccessor.OBJECT_ARRAY_SETTER : ArrayAccessor.OBJECT_ARRAY_GETTER;
    }
    if (!paramClass.isArray()) {
      throw MethodHandleStatics.newIllegalArgumentException("not an array: " + paramClass);
    }
    MethodHandle[] arrayOfMethodHandle = (MethodHandle[])ArrayAccessor.TYPED_ACCESSORS.get(paramClass);
    int i = paramBoolean ? 1 : 0;
    MethodHandle localMethodHandle = arrayOfMethodHandle[i];
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    localMethodHandle = ArrayAccessor.getAccessor(paramClass, paramBoolean);
    MethodType localMethodType = ArrayAccessor.correctType(paramClass, paramBoolean);
    if (localMethodHandle.type() != localMethodType)
    {
      assert (localMethodHandle.type().parameterType(0) == [Ljava.lang.Object.class);
      if (!$assertionsDisabled) {
        if ((paramBoolean ? localMethodHandle.type().parameterType(2) : localMethodHandle.type().returnType()) != Object.class) {
          throw new AssertionError();
        }
      }
      assert ((paramBoolean) || (localMethodType.parameterType(0).getComponentType() == localMethodType.returnType()));
      localMethodHandle = localMethodHandle.viewAsType(localMethodType, false);
    }
    localMethodHandle = makeIntrinsic(localMethodHandle, paramBoolean ? Intrinsic.ARRAY_STORE : Intrinsic.ARRAY_LOAD);
    synchronized (arrayOfMethodHandle)
    {
      if (arrayOfMethodHandle[i] == null) {
        arrayOfMethodHandle[i] = localMethodHandle;
      } else {
        localMethodHandle = arrayOfMethodHandle[i];
      }
    }
    return localMethodHandle;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2)
  {
    MethodType localMethodType = paramMethodHandle.type();
    if (paramMethodType == localMethodType) {
      return paramMethodHandle;
    }
    return makePairwiseConvertByEditor(paramMethodHandle, paramMethodType, paramBoolean1, paramBoolean2);
  }
  
  private static int countNonNull(Object[] paramArrayOfObject)
  {
    int i = 0;
    for (Object localObject : paramArrayOfObject) {
      if (localObject != null) {
        i++;
      }
    }
    return i;
  }
  
  static MethodHandle makePairwiseConvertByEditor(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object[] arrayOfObject = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = countNonNull(arrayOfObject);
    if (i == 0) {
      return paramMethodHandle.viewAsType(paramMethodType, paramBoolean1);
    }
    MethodType localMethodType1 = paramMethodType.basicType();
    MethodType localMethodType2 = paramMethodHandle.type().basicType();
    BoundMethodHandle localBoundMethodHandle = paramMethodHandle.rebind();
    Object localObject2;
    Object localObject3;
    Object localObject4;
    for (int j = 0; j < arrayOfObject.length - 1; j++)
    {
      localObject2 = arrayOfObject[j];
      if (localObject2 != null)
      {
        if ((localObject2 instanceof Class)) {
          localObject3 = Lazy.MH_castReference.bindTo(localObject2);
        } else {
          localObject3 = (MethodHandle)localObject2;
        }
        localObject4 = localMethodType1.parameterType(j);
        i--;
        if (i == 0) {
          localMethodType2 = paramMethodType;
        } else {
          localMethodType2 = localMethodType2.changeParameterType(j, (Class)localObject4);
        }
        LambdaForm localLambdaForm = localBoundMethodHandle.editor().filterArgumentForm(1 + j, LambdaForm.BasicType.basicType((Class)localObject4));
        localBoundMethodHandle = localBoundMethodHandle.copyWithExtendL(localMethodType2, localLambdaForm, localObject3);
        localBoundMethodHandle = localBoundMethodHandle.rebind();
      }
    }
    Object localObject1 = arrayOfObject[(arrayOfObject.length - 1)];
    if (localObject1 != null)
    {
      if ((localObject1 instanceof Class))
      {
        if (localObject1 == Void.TYPE) {
          localObject2 = null;
        } else {
          localObject2 = Lazy.MH_castReference.bindTo(localObject1);
        }
      }
      else {
        localObject2 = (MethodHandle)localObject1;
      }
      localObject3 = localMethodType1.returnType();
      if (!$assertionsDisabled)
      {
        i--;
        if (i != 0) {
          throw new AssertionError();
        }
      }
      localMethodType2 = paramMethodType;
      if (localObject2 != null)
      {
        localBoundMethodHandle = localBoundMethodHandle.rebind();
        localObject4 = localBoundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType((Class)localObject3), false);
        localBoundMethodHandle = localBoundMethodHandle.copyWithExtendL(localMethodType2, (LambdaForm)localObject4, localObject2);
      }
      else
      {
        localObject4 = localBoundMethodHandle.editor().filterReturnForm(LambdaForm.BasicType.basicType((Class)localObject3), true);
        localBoundMethodHandle = localBoundMethodHandle.copyWith(localMethodType2, (LambdaForm)localObject4);
      }
    }
    assert (i == 0);
    assert (localBoundMethodHandle.type().equals(paramMethodType));
    return localBoundMethodHandle;
  }
  
  static MethodHandle makePairwiseConvertIndirect(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean1, boolean paramBoolean2)
  {
    assert (paramMethodHandle.type().parameterCount() == paramMethodType.parameterCount());
    Object[] arrayOfObject1 = computeValueConversions(paramMethodType, paramMethodHandle.type(), paramBoolean1, paramBoolean2);
    int i = paramMethodType.parameterCount();
    int j = countNonNull(arrayOfObject1);
    int k = arrayOfObject1[i] != null ? 1 : 0;
    int m = paramMethodType.returnType() == Void.TYPE ? 1 : 0;
    if ((k != 0) && (m != 0))
    {
      j--;
      k = 0;
    }
    int n = 1 + i;
    int i1 = n + j + 1;
    int i2 = k == 0 ? -1 : i1 - 1;
    int i3 = (k == 0 ? i1 : i2) - 1;
    int i4 = m != 0 ? -1 : i1 - 1;
    MethodType localMethodType = paramMethodType.basicType().invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(i1 - n, localMethodType);
    Object[] arrayOfObject2 = new Object[0 + i];
    int i5 = n;
    Object localObject3;
    for (int i6 = 0; i6 < i; i6++)
    {
      localObject2 = arrayOfObject1[i6];
      if (localObject2 == null)
      {
        arrayOfObject2[(0 + i6)] = arrayOfName[(1 + i6)];
      }
      else
      {
        Object localObject4;
        if ((localObject2 instanceof Class))
        {
          localObject4 = (Class)localObject2;
          localObject3 = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { localObject4, arrayOfName[(1 + i6)] });
        }
        else
        {
          localObject4 = (MethodHandle)localObject2;
          localObject3 = new LambdaForm.Name((MethodHandle)localObject4, new Object[] { arrayOfName[(1 + i6)] });
        }
        assert (arrayOfName[i5] == null);
        arrayOfName[(i5++)] = localObject3;
        assert (arrayOfObject2[(0 + i6)] == null);
        arrayOfObject2[(0 + i6)] = localObject3;
      }
    }
    assert (i5 == i3);
    arrayOfName[i3] = new LambdaForm.Name(paramMethodHandle, arrayOfObject2);
    Object localObject1 = arrayOfObject1[i];
    if (k == 0)
    {
      if ((!$assertionsDisabled) && (i3 != arrayOfName.length - 1)) {
        throw new AssertionError();
      }
    }
    else
    {
      if (localObject1 == Void.TYPE)
      {
        localObject2 = new LambdaForm.Name(LambdaForm.constantZero(LambdaForm.BasicType.basicType(paramMethodType.returnType())), new Object[0]);
      }
      else if ((localObject1 instanceof Class))
      {
        localObject3 = (Class)localObject1;
        localObject2 = new LambdaForm.Name(Lazy.MH_castReference, new Object[] { localObject3, arrayOfName[i3] });
      }
      else
      {
        localObject3 = (MethodHandle)localObject1;
        if (((MethodHandle)localObject3).type().parameterCount() == 0) {
          localObject2 = new LambdaForm.Name((MethodHandle)localObject3, new Object[0]);
        } else {
          localObject2 = new LambdaForm.Name((MethodHandle)localObject3, new Object[] { arrayOfName[i3] });
        }
      }
      assert (arrayOfName[i2] == null);
      arrayOfName[i2] = localObject2;
      assert (i2 == arrayOfName.length - 1);
    }
    Object localObject2 = new LambdaForm("convert", localMethodType.parameterCount(), arrayOfName, i4);
    return SimpleMethodHandle.make(paramMethodType, (LambdaForm)localObject2);
  }
  
  @ForceInline
  static <T, U> T castReference(Class<? extends T> paramClass, U paramU)
  {
    if ((paramU != null) && (!paramClass.isInstance(paramU))) {
      throw newClassCastException(paramClass, paramU);
    }
    return paramU;
  }
  
  private static ClassCastException newClassCastException(Class<?> paramClass, Object paramObject)
  {
    return new ClassCastException("Cannot cast " + paramObject.getClass().getName() + " to " + paramClass.getName());
  }
  
  static Object[] computeValueConversions(MethodType paramMethodType1, MethodType paramMethodType2, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = paramMethodType1.parameterCount();
    Object[] arrayOfObject = new Object[i + 1];
    for (int j = 0; j <= i; j++)
    {
      int k = j == i ? 1 : 0;
      Class localClass1 = k != 0 ? paramMethodType2.returnType() : paramMethodType1.parameterType(j);
      Class localClass2 = k != 0 ? paramMethodType1.returnType() : paramMethodType2.parameterType(j);
      if (!VerifyType.isNullConversion(localClass1, localClass2, paramBoolean1)) {
        arrayOfObject[j] = valueConversion(localClass1, localClass2, paramBoolean1, paramBoolean2);
      }
    }
    return arrayOfObject;
  }
  
  static MethodHandle makePairwiseConvert(MethodHandle paramMethodHandle, MethodType paramMethodType, boolean paramBoolean)
  {
    return makePairwiseConvert(paramMethodHandle, paramMethodType, paramBoolean, false);
  }
  
  static Object valueConversion(Class<?> paramClass1, Class<?> paramClass2, boolean paramBoolean1, boolean paramBoolean2)
  {
    assert (!VerifyType.isNullConversion(paramClass1, paramClass2, paramBoolean1));
    if (paramClass2 == Void.TYPE) {
      return paramClass2;
    }
    MethodHandle localMethodHandle;
    Wrapper localWrapper;
    if (paramClass1.isPrimitive())
    {
      if (paramClass1 == Void.TYPE) {
        return Void.TYPE;
      }
      if (paramClass2.isPrimitive())
      {
        localMethodHandle = ValueConversions.convertPrimitive(paramClass1, paramClass2);
      }
      else
      {
        localWrapper = Wrapper.forPrimitiveType(paramClass1);
        localMethodHandle = ValueConversions.boxExact(localWrapper);
        assert (localMethodHandle.type().parameterType(0) == localWrapper.primitiveType());
        assert (localMethodHandle.type().returnType() == localWrapper.wrapperType());
        if (!VerifyType.isNullConversion(localWrapper.wrapperType(), paramClass2, paramBoolean1))
        {
          MethodType localMethodType = MethodType.methodType(paramClass2, paramClass1);
          if (paramBoolean1) {
            localMethodHandle = localMethodHandle.asType(localMethodType);
          } else {
            localMethodHandle = makePairwiseConvert(localMethodHandle, localMethodType, false);
          }
        }
      }
    }
    else if (paramClass2.isPrimitive())
    {
      localWrapper = Wrapper.forPrimitiveType(paramClass2);
      if ((paramBoolean2) || (paramClass1 == localWrapper.wrapperType())) {
        localMethodHandle = ValueConversions.unboxExact(localWrapper, paramBoolean1);
      } else {
        localMethodHandle = paramBoolean1 ? ValueConversions.unboxWiden(localWrapper) : ValueConversions.unboxCast(localWrapper);
      }
    }
    else
    {
      return paramClass2;
    }
    if ((!$assertionsDisabled) && (localMethodHandle.type().parameterCount() > 1)) {
      throw new AssertionError("pc" + Arrays.asList(new Object[] { paramClass1.getSimpleName(), paramClass2.getSimpleName(), localMethodHandle }));
    }
    return localMethodHandle;
  }
  
  static MethodHandle makeVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass)
  {
    MethodType localMethodType = paramMethodHandle.type();
    int i = localMethodType.parameterCount() - 1;
    if (localMethodType.parameterType(i) != paramClass) {
      paramMethodHandle = paramMethodHandle.asType(localMethodType.changeParameterType(i, paramClass));
    }
    paramMethodHandle = paramMethodHandle.asFixedArity();
    return new AsVarargsCollector(paramMethodHandle, paramClass);
  }
  
  static MethodHandle makeSpreadArguments(MethodHandle paramMethodHandle, Class<?> paramClass, int paramInt1, int paramInt2)
  {
    MethodType localMethodType1 = paramMethodHandle.type();
    for (int i = 0; i < paramInt2; i++)
    {
      localObject = VerifyType.spreadArgElementType(paramClass, i);
      if (localObject == null) {
        localObject = Object.class;
      }
      localMethodType1 = localMethodType1.changeParameterType(paramInt1 + i, (Class)localObject);
    }
    paramMethodHandle = paramMethodHandle.asType(localMethodType1);
    MethodType localMethodType2 = localMethodType1.replaceParameterTypes(paramInt1, paramInt1 + paramInt2, new Class[] { paramClass });
    Object localObject = localMethodType2.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(paramInt2 + 2, (MethodType)localObject);
    int j = ((MethodType)localObject).parameterCount();
    int[] arrayOfInt = new int[localMethodType1.parameterCount()];
    int k = 0;
    for (int m = 1; k < localMethodType1.parameterCount() + 1; m++)
    {
      Class localClass = ((MethodType)localObject).parameterType(k);
      if (k == paramInt1)
      {
        MethodHandle localMethodHandle = MethodHandles.arrayElementGetter(paramClass);
        LambdaForm.Name localName = arrayOfName1[m];
        arrayOfName1[(j++)] = new LambdaForm.Name(Lazy.NF_checkSpreadArgument, new Object[] { localName, Integer.valueOf(paramInt2) });
        for (int i1 = 0; i1 < paramInt2; i1++)
        {
          arrayOfInt[k] = j;
          arrayOfName1[(j++)] = new LambdaForm.Name(localMethodHandle, new Object[] { localName, Integer.valueOf(i1) });
          k++;
        }
      }
      else if (k < arrayOfInt.length)
      {
        arrayOfInt[k] = m;
      }
      k++;
    }
    assert (j == arrayOfName1.length - 1);
    LambdaForm.Name[] arrayOfName2 = new LambdaForm.Name[localMethodType1.parameterCount()];
    for (m = 0; m < localMethodType1.parameterCount(); m++)
    {
      int n = arrayOfInt[m];
      arrayOfName2[m] = arrayOfName1[n];
    }
    arrayOfName1[(arrayOfName1.length - 1)] = new LambdaForm.Name(paramMethodHandle, (Object[])arrayOfName2);
    LambdaForm localLambdaForm = new LambdaForm("spread", ((MethodType)localObject).parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(localMethodType2, localLambdaForm);
  }
  
  static void checkSpreadArgument(Object paramObject, int paramInt)
  {
    if (paramObject == null)
    {
      if (paramInt != 0) {}
    }
    else
    {
      int i;
      if ((paramObject instanceof Object[]))
      {
        i = ((Object[])paramObject).length;
        if (i == paramInt) {
          return;
        }
      }
      else
      {
        i = Array.getLength(paramObject);
        if (i == paramInt) {
          return;
        }
      }
    }
    throw MethodHandleStatics.newIllegalArgumentException("array is not of length " + paramInt);
  }
  
  static MethodHandle makeCollectArguments(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt, boolean paramBoolean)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    MethodType localMethodType2 = paramMethodHandle2.type();
    int i = localMethodType2.parameterCount();
    Class localClass = localMethodType2.returnType();
    int j = localClass == Void.TYPE ? 0 : 1;
    MethodType localMethodType3 = localMethodType1.dropParameterTypes(paramInt, paramInt + j);
    if (!paramBoolean) {
      localMethodType3 = localMethodType3.insertParameterTypes(paramInt, localMethodType2.parameterList());
    }
    MethodType localMethodType4 = localMethodType3.invokerType();
    LambdaForm.Name[] arrayOfName1 = LambdaForm.arguments(2, localMethodType4);
    int k = arrayOfName1.length - 2;
    int m = arrayOfName1.length - 1;
    LambdaForm.Name[] arrayOfName2 = (LambdaForm.Name[])Arrays.copyOfRange(arrayOfName1, 1 + paramInt, 1 + paramInt + i);
    arrayOfName1[k] = new LambdaForm.Name(paramMethodHandle2, (Object[])arrayOfName2);
    LambdaForm.Name[] arrayOfName3 = new LambdaForm.Name[localMethodType1.parameterCount()];
    int n = 1;
    int i1 = 0;
    int i2 = paramInt;
    System.arraycopy(arrayOfName1, n, arrayOfName3, i1, i2);
    n += i2;
    i1 += i2;
    if (localClass != Void.TYPE) {
      arrayOfName3[(i1++)] = arrayOfName1[k];
    }
    i2 = i;
    if (paramBoolean)
    {
      System.arraycopy(arrayOfName1, n, arrayOfName3, i1, i2);
      i1 += i2;
    }
    n += i2;
    i2 = arrayOfName3.length - i1;
    System.arraycopy(arrayOfName1, n, arrayOfName3, i1, i2);
    assert (n + i2 == k);
    arrayOfName1[m] = new LambdaForm.Name(paramMethodHandle1, (Object[])arrayOfName3);
    LambdaForm localLambdaForm = new LambdaForm("collect", localMethodType4.parameterCount(), arrayOfName1);
    return SimpleMethodHandle.make(localMethodType3, localLambdaForm);
  }
  
  @LambdaForm.Hidden
  static MethodHandle selectAlternative(boolean paramBoolean, MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2)
  {
    if (paramBoolean) {
      return paramMethodHandle1;
    }
    return paramMethodHandle2;
  }
  
  @LambdaForm.Hidden
  static boolean profileBoolean(boolean paramBoolean, int[] paramArrayOfInt)
  {
    int i = paramBoolean ? 1 : 0;
    try
    {
      paramArrayOfInt[i] = Math.addExact(paramArrayOfInt[i], 1);
    }
    catch (ArithmeticException localArithmeticException)
    {
      paramArrayOfInt[i] /= 2;
    }
    return paramBoolean;
  }
  
  static MethodHandle makeGuardWithTest(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, MethodHandle paramMethodHandle3)
  {
    MethodType localMethodType1 = paramMethodHandle2.type();
    assert ((paramMethodHandle1.type().equals(localMethodType1.changeReturnType(Boolean.TYPE))) && (paramMethodHandle3.type().equals(localMethodType1)));
    MethodType localMethodType2 = localMethodType1.basicType();
    LambdaForm localLambdaForm = makeGuardWithTestForm(localMethodType2);
    BoundMethodHandle localBoundMethodHandle;
    try
    {
      if (MethodHandleStatics.PROFILE_GWT)
      {
        int[] arrayOfInt = new int[2];
        localBoundMethodHandle = BoundMethodHandle.speciesData_LLLL().constructor().invokeBasic(localMethodType1, localLambdaForm, paramMethodHandle1, profile(paramMethodHandle2), profile(paramMethodHandle3), arrayOfInt);
      }
      else
      {
        localBoundMethodHandle = BoundMethodHandle.speciesData_LLL().constructor().invokeBasic(localMethodType1, localLambdaForm, paramMethodHandle1, profile(paramMethodHandle2), profile(paramMethodHandle3));
      }
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
    assert (localBoundMethodHandle.type() == localMethodType1);
    return localBoundMethodHandle;
  }
  
  static MethodHandle profile(MethodHandle paramMethodHandle)
  {
    if (MethodHandleStatics.DONT_INLINE_THRESHOLD >= 0) {
      return makeBlockInlningWrapper(paramMethodHandle);
    }
    return paramMethodHandle;
  }
  
  static MethodHandle makeBlockInlningWrapper(MethodHandle paramMethodHandle)
  {
    LambdaForm localLambdaForm = (LambdaForm)PRODUCE_BLOCK_INLINING_FORM.apply(paramMethodHandle);
    return new CountingWrapper(paramMethodHandle, localLambdaForm, PRODUCE_BLOCK_INLINING_FORM, PRODUCE_REINVOKER_FORM, MethodHandleStatics.DONT_INLINE_THRESHOLD, null);
  }
  
  static LambdaForm makeGuardWithTestForm(MethodType paramMethodType)
  {
    LambdaForm localLambdaForm = paramMethodType.form().cachedLambdaForm(17);
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = MethodHandleStatics.PROFILE_GWT ? j++ : -1;
    int i2 = j++;
    int i3 = i1 != -1 ? j++ : -1;
    int i4 = j - 1;
    int i5 = j++;
    int i6 = j++;
    assert (i6 == i5 + 1);
    MethodType localMethodType1 = paramMethodType.invokerType();
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, localMethodType1);
    BoundMethodHandle.SpeciesData localSpeciesData = i1 != -1 ? BoundMethodHandle.speciesData_LLLL() : BoundMethodHandle.speciesData_LLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(localSpeciesData);
    arrayOfName[k] = new LambdaForm.Name(localSpeciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(localSpeciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(localSpeciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    if (i1 != -1) {
      arrayOfName[i1] = new LambdaForm.Name(localSpeciesData.getterFunction(3), new Object[] { arrayOfName[0] });
    }
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, i, [Ljava.lang.Object.class);
    MethodType localMethodType2 = paramMethodType.changeReturnType(Boolean.TYPE).basicType();
    arrayOfObject[0] = arrayOfName[k];
    arrayOfName[i2] = new LambdaForm.Name(localMethodType2, arrayOfObject);
    if (i3 != -1) {
      arrayOfName[i3] = new LambdaForm.Name(Lazy.NF_profileBoolean, new Object[] { arrayOfName[i2], arrayOfName[i1] });
    }
    arrayOfName[i5] = new LambdaForm.Name(Lazy.MH_selectAlternative, new Object[] { arrayOfName[i4], arrayOfName[m], arrayOfName[n] });
    arrayOfObject[0] = arrayOfName[i5];
    arrayOfName[i6] = new LambdaForm.Name(paramMethodType, arrayOfObject);
    localLambdaForm = new LambdaForm("guard", localMethodType1.parameterCount(), arrayOfName, true);
    return paramMethodType.form().setCachedLambdaForm(17, localLambdaForm);
  }
  
  private static LambdaForm makeGuardWithCatchForm(MethodType paramMethodType)
  {
    MethodType localMethodType1 = paramMethodType.invokerType();
    LambdaForm localLambdaForm = paramMethodType.form().cachedLambdaForm(16);
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    int i = 1 + paramMethodType.parameterCount();
    int j = i;
    int k = j++;
    int m = j++;
    int n = j++;
    int i1 = j++;
    int i2 = j++;
    int i3 = j++;
    int i4 = j++;
    int i5 = j++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(j - i, localMethodType1);
    BoundMethodHandle.SpeciesData localSpeciesData = BoundMethodHandle.speciesData_LLLLL();
    arrayOfName[0] = arrayOfName[0].withConstraint(localSpeciesData);
    arrayOfName[k] = new LambdaForm.Name(localSpeciesData.getterFunction(0), new Object[] { arrayOfName[0] });
    arrayOfName[m] = new LambdaForm.Name(localSpeciesData.getterFunction(1), new Object[] { arrayOfName[0] });
    arrayOfName[n] = new LambdaForm.Name(localSpeciesData.getterFunction(2), new Object[] { arrayOfName[0] });
    arrayOfName[i1] = new LambdaForm.Name(localSpeciesData.getterFunction(3), new Object[] { arrayOfName[0] });
    arrayOfName[i2] = new LambdaForm.Name(localSpeciesData.getterFunction(4), new Object[] { arrayOfName[0] });
    MethodType localMethodType2 = paramMethodType.changeReturnType(Object.class);
    MethodHandle localMethodHandle1 = MethodHandles.basicInvoker(localMethodType2);
    Object[] arrayOfObject1 = new Object[localMethodHandle1.type().parameterCount()];
    arrayOfObject1[0] = arrayOfName[i1];
    System.arraycopy(arrayOfName, 1, arrayOfObject1, 1, i - 1);
    arrayOfName[i3] = new LambdaForm.Name(makeIntrinsic(localMethodHandle1, Intrinsic.GUARD_WITH_CATCH), arrayOfObject1);
    Object[] arrayOfObject2 = { arrayOfName[k], arrayOfName[m], arrayOfName[n], arrayOfName[i3] };
    arrayOfName[i4] = new LambdaForm.Name(Lazy.NF_guardWithCatch, arrayOfObject2);
    MethodHandle localMethodHandle2 = MethodHandles.basicInvoker(MethodType.methodType(paramMethodType.rtype(), Object.class));
    Object[] arrayOfObject3 = { arrayOfName[i2], arrayOfName[i4] };
    arrayOfName[i5] = new LambdaForm.Name(localMethodHandle2, arrayOfObject3);
    localLambdaForm = new LambdaForm("guardWithCatch", localMethodType1.parameterCount(), arrayOfName);
    return paramMethodType.form().setCachedLambdaForm(16, localLambdaForm);
  }
  
  static MethodHandle makeGuardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2)
  {
    MethodType localMethodType1 = paramMethodHandle1.type();
    LambdaForm localLambdaForm = makeGuardWithCatchForm(localMethodType1.basicType());
    MethodType localMethodType2 = localMethodType1.changeReturnType([Ljava.lang.Object.class);
    MethodHandle localMethodHandle1 = varargsArray(localMethodType1.parameterCount()).asType(localMethodType2);
    Class localClass = localMethodType1.returnType();
    MethodHandle localMethodHandle2;
    if (localClass.isPrimitive())
    {
      if (localClass == Void.TYPE)
      {
        localMethodHandle2 = ValueConversions.ignore();
      }
      else
      {
        localObject = Wrapper.forPrimitiveType(localMethodType1.returnType());
        localMethodHandle2 = ValueConversions.unboxExact((Wrapper)localObject);
      }
    }
    else {
      localMethodHandle2 = MethodHandles.identity(Object.class);
    }
    Object localObject = BoundMethodHandle.speciesData_LLLLL();
    BoundMethodHandle localBoundMethodHandle;
    try
    {
      localBoundMethodHandle = ((BoundMethodHandle.SpeciesData)localObject).constructor().invokeBasic(localMethodType1, localLambdaForm, paramMethodHandle1, paramClass, paramMethodHandle2, localMethodHandle1, localMethodHandle2);
    }
    catch (Throwable localThrowable)
    {
      throw MethodHandleStatics.uncaughtException(localThrowable);
    }
    assert (localBoundMethodHandle.type() == localMethodType1);
    return localBoundMethodHandle;
  }
  
  @LambdaForm.Hidden
  static Object guardWithCatch(MethodHandle paramMethodHandle1, Class<? extends Throwable> paramClass, MethodHandle paramMethodHandle2, Object... paramVarArgs)
    throws Throwable
  {
    try
    {
      return paramMethodHandle1.asFixedArity().invokeWithArguments(paramVarArgs);
    }
    catch (Throwable localThrowable)
    {
      if (!paramClass.isInstance(localThrowable)) {
        throw localThrowable;
      }
      return paramMethodHandle2.asFixedArity().invokeWithArguments(prepend(localThrowable, paramVarArgs));
    }
  }
  
  @LambdaForm.Hidden
  private static Object[] prepend(Object paramObject, Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = new Object[paramArrayOfObject.length + 1];
    arrayOfObject[0] = paramObject;
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 1, paramArrayOfObject.length);
    return arrayOfObject;
  }
  
  static MethodHandle throwException(MethodType paramMethodType)
  {
    assert (Throwable.class.isAssignableFrom(paramMethodType.parameterType(0)));
    int i = paramMethodType.parameterCount();
    if (i > 1)
    {
      MethodHandle localMethodHandle = throwException(paramMethodType.dropParameterTypes(1, i));
      localMethodHandle = MethodHandles.dropArguments(localMethodHandle, 1, paramMethodType.parameterList().subList(1, i));
      return localMethodHandle;
    }
    return makePairwiseConvert(Lazy.NF_throwException.resolvedHandle(), paramMethodType, false, true);
  }
  
  static <T extends Throwable> Empty throwException(T paramT)
    throws Throwable
  {
    throw paramT;
  }
  
  static MethodHandle fakeMethodHandleInvoke(MemberName paramMemberName)
  {
    assert (paramMemberName.isMethodHandleInvoke());
    Object localObject = paramMemberName.getName();
    int j = -1;
    switch (((String)localObject).hashCode())
    {
    case -1183693704: 
      if (((String)localObject).equals("invoke")) {
        j = 0;
      }
      break;
    case 941760871: 
      if (((String)localObject).equals("invokeExact")) {
        j = 1;
      }
      break;
    }
    int i;
    switch (j)
    {
    case 0: 
      i = 0;
      break;
    case 1: 
      i = 1;
      break;
    default: 
      throw new InternalError(paramMemberName.getName());
    }
    localObject = FAKE_METHOD_HANDLE_INVOKE[i];
    if (localObject != null) {
      return localObject;
    }
    MethodType localMethodType = MethodType.methodType(Object.class, UnsupportedOperationException.class, new Class[] { MethodHandle.class, [Ljava.lang.Object.class });
    localObject = throwException(localMethodType);
    localObject = ((MethodHandle)localObject).bindTo(new UnsupportedOperationException("cannot reflectively invoke MethodHandle"));
    if (!paramMemberName.getInvocationType().equals(((MethodHandle)localObject).type())) {
      throw new InternalError(paramMemberName.toString());
    }
    localObject = ((MethodHandle)localObject).withInternalMemberName(paramMemberName, false);
    localObject = ((MethodHandle)localObject).asVarargsCollector([Ljava.lang.Object.class);
    assert (paramMemberName.isVarargs());
    FAKE_METHOD_HANDLE_INVOKE[i] = localObject;
    return localObject;
  }
  
  static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass)
  {
    return BindCaller.bindCaller(paramMethodHandle, paramClass);
  }
  
  static MethodHandle makeWrappedMember(MethodHandle paramMethodHandle, MemberName paramMemberName, boolean paramBoolean)
  {
    if ((paramMemberName.equals(paramMethodHandle.internalMemberName())) && (paramBoolean == paramMethodHandle.isInvokeSpecial())) {
      return paramMethodHandle;
    }
    return new WrappedMember(paramMethodHandle, paramMethodHandle.type(), paramMemberName, paramBoolean, null, null);
  }
  
  static MethodHandle makeIntrinsic(MethodHandle paramMethodHandle, Intrinsic paramIntrinsic)
  {
    if (paramIntrinsic == paramMethodHandle.intrinsicName()) {
      return paramMethodHandle;
    }
    return new IntrinsicMethodHandle(paramMethodHandle, paramIntrinsic);
  }
  
  static MethodHandle makeIntrinsic(MethodType paramMethodType, LambdaForm paramLambdaForm, Intrinsic paramIntrinsic)
  {
    return new IntrinsicMethodHandle(SimpleMethodHandle.make(paramMethodType, paramLambdaForm), paramIntrinsic);
  }
  
  private static MethodHandle findCollector(String paramString, int paramInt, Class<?> paramClass, Class<?>... paramVarArgs)
  {
    MethodType localMethodType = MethodType.genericMethodType(paramInt).changeReturnType(paramClass).insertParameterTypes(0, paramVarArgs);
    try
    {
      return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MethodHandleImpl.class, paramString, localMethodType);
    }
    catch (ReflectiveOperationException localReflectiveOperationException) {}
    return null;
  }
  
  private static Object[] makeArray(Object... paramVarArgs)
  {
    return paramVarArgs;
  }
  
  private static Object[] array()
  {
    return NO_ARGS_ARRAY;
  }
  
  private static Object[] array(Object paramObject)
  {
    return makeArray(new Object[] { paramObject });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2)
  {
    return makeArray(new Object[] { paramObject1, paramObject2 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
  }
  
  private static Object[] array(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    return makeArray(new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
  }
  
  private static MethodHandle[] makeArrays()
  {
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      MethodHandle localMethodHandle = findCollector("array", localArrayList.size(), [Ljava.lang.Object.class, new Class[0]);
      if (localMethodHandle == null) {
        break;
      }
      localMethodHandle = makeIntrinsic(localMethodHandle, Intrinsic.NEW_ARRAY);
      localArrayList.add(localMethodHandle);
    }
    assert (localArrayList.size() == 11);
    return (MethodHandle[])localArrayList.toArray(new MethodHandle[MAX_ARITY + 1]);
  }
  
  private static Object[] fillNewArray(Integer paramInteger, Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = new Object[paramInteger.intValue()];
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject);
    return arrayOfObject;
  }
  
  private static Object[] fillNewTypedArray(Object[] paramArrayOfObject1, Integer paramInteger, Object[] paramArrayOfObject2)
  {
    Object[] arrayOfObject = Arrays.copyOf(paramArrayOfObject1, paramInteger.intValue());
    assert (arrayOfObject.getClass() != [Ljava.lang.Object.class);
    fillWithArguments(arrayOfObject, 0, paramArrayOfObject2);
    return arrayOfObject;
  }
  
  private static void fillWithArguments(Object[] paramArrayOfObject1, int paramInt, Object... paramVarArgs)
  {
    System.arraycopy(paramVarArgs, 0, paramArrayOfObject1, paramInt, paramVarArgs.length);
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9 });
    return paramArrayOfObject;
  }
  
  private static Object[] fillArray(Integer paramInteger, Object[] paramArrayOfObject, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    fillWithArguments(paramArrayOfObject, paramInteger.intValue(), new Object[] { paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10 });
    return paramArrayOfObject;
  }
  
  private static MethodHandle[] makeFillArrays()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(null);
    for (;;)
    {
      MethodHandle localMethodHandle = findCollector("fillArray", localArrayList.size(), [Ljava.lang.Object.class, new Class[] { Integer.class, [Ljava.lang.Object.class });
      if (localMethodHandle == null) {
        break;
      }
      localArrayList.add(localMethodHandle);
    }
    assert (localArrayList.size() == 11);
    return (MethodHandle[])localArrayList.toArray(new MethodHandle[0]);
  }
  
  private static Object copyAsPrimitiveArray(Wrapper paramWrapper, Object... paramVarArgs)
  {
    Object localObject = paramWrapper.makeArray(paramVarArgs.length);
    paramWrapper.copyArrayUnboxing(paramVarArgs, 0, localObject, 0, paramVarArgs.length);
    return localObject;
  }
  
  static MethodHandle varargsArray(int paramInt)
  {
    MethodHandle localMethodHandle = Lazy.ARRAYS[paramInt];
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    localMethodHandle = findCollector("array", paramInt, [Ljava.lang.Object.class, new Class[0]);
    if (localMethodHandle != null) {
      localMethodHandle = makeIntrinsic(localMethodHandle, Intrinsic.NEW_ARRAY);
    }
    if (localMethodHandle != null) {
      return Lazy.ARRAYS[paramInt] =  = localMethodHandle;
    }
    localMethodHandle = buildVarargsArray(Lazy.MH_fillNewArray, Lazy.MH_arrayIdentity, paramInt);
    assert (assertCorrectArity(localMethodHandle, paramInt));
    localMethodHandle = makeIntrinsic(localMethodHandle, Intrinsic.NEW_ARRAY);
    return Lazy.ARRAYS[paramInt] =  = localMethodHandle;
  }
  
  private static boolean assertCorrectArity(MethodHandle paramMethodHandle, int paramInt)
  {
    assert (paramMethodHandle.type().parameterCount() == paramInt) : ("arity != " + paramInt + ": " + paramMethodHandle);
    return true;
  }
  
  static <T> T[] identity(T[] paramArrayOfT)
  {
    return paramArrayOfT;
  }
  
  private static MethodHandle buildVarargsArray(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, int paramInt)
  {
    int i = Math.min(paramInt, 10);
    int j = paramInt - i;
    MethodHandle localMethodHandle1 = paramMethodHandle1.bindTo(Integer.valueOf(paramInt));
    localMethodHandle1 = localMethodHandle1.asCollector([Ljava.lang.Object.class, i);
    Object localObject = paramMethodHandle2;
    if (j > 0)
    {
      MethodHandle localMethodHandle2 = fillToRight(10 + j);
      if (localObject == Lazy.MH_arrayIdentity) {
        localObject = localMethodHandle2;
      } else {
        localObject = MethodHandles.collectArguments((MethodHandle)localObject, 0, localMethodHandle2);
      }
    }
    if (localObject == Lazy.MH_arrayIdentity) {
      localObject = localMethodHandle1;
    } else {
      localObject = MethodHandles.collectArguments((MethodHandle)localObject, 0, localMethodHandle1);
    }
    return localObject;
  }
  
  private static MethodHandle fillToRight(int paramInt)
  {
    MethodHandle localMethodHandle = FILL_ARRAY_TO_RIGHT[paramInt];
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    localMethodHandle = buildFiller(paramInt);
    assert (assertCorrectArity(localMethodHandle, paramInt - 10 + 1));
    return FILL_ARRAY_TO_RIGHT[paramInt] =  = localMethodHandle;
  }
  
  private static MethodHandle buildFiller(int paramInt)
  {
    if (paramInt <= 10) {
      return Lazy.MH_arrayIdentity;
    }
    int i = paramInt % 10;
    int j = paramInt - i;
    if (i == 0)
    {
      j = paramInt - (i = 10);
      if (FILL_ARRAY_TO_RIGHT[j] == null) {
        for (int k = 0; k < j; k += 10) {
          if (k > 10) {
            fillToRight(k);
          }
        }
      }
    }
    if (j < 10) {
      i = paramInt - (j = 10);
    }
    assert (i > 0);
    MethodHandle localMethodHandle1 = fillToRight(j);
    MethodHandle localMethodHandle2 = Lazy.FILL_ARRAYS[i].bindTo(Integer.valueOf(j));
    assert (localMethodHandle1.type().parameterCount() == 1 + j - 10);
    assert (localMethodHandle2.type().parameterCount() == 1 + i);
    if (j == 10) {
      return localMethodHandle2;
    }
    return MethodHandles.collectArguments(localMethodHandle2, 0, localMethodHandle1);
  }
  
  static MethodHandle varargsArray(Class<?> paramClass, int paramInt)
  {
    Class localClass = paramClass.getComponentType();
    if (localClass == null) {
      throw new IllegalArgumentException("not an array: " + paramClass);
    }
    if (paramInt >= 126)
    {
      int i = paramInt;
      if ((i <= 254) && (localClass.isPrimitive())) {
        i *= Wrapper.forPrimitiveType(localClass).stackSlots();
      }
      if (i > 254) {
        throw new IllegalArgumentException("too many arguments: " + paramClass.getSimpleName() + ", length " + paramInt);
      }
    }
    if (localClass == Object.class) {
      return varargsArray(paramInt);
    }
    MethodHandle[] arrayOfMethodHandle = (MethodHandle[])TYPED_COLLECTORS.get(localClass);
    MethodHandle localMethodHandle1 = paramInt < arrayOfMethodHandle.length ? arrayOfMethodHandle[paramInt] : null;
    if (localMethodHandle1 != null) {
      return localMethodHandle1;
    }
    Object localObject1;
    if (paramInt == 0)
    {
      localObject1 = Array.newInstance(paramClass.getComponentType(), 0);
      localMethodHandle1 = MethodHandles.constant(paramClass, localObject1);
    }
    else
    {
      Object localObject2;
      if (localClass.isPrimitive())
      {
        localObject1 = Lazy.MH_fillNewArray;
        localObject2 = buildArrayProducer(paramClass);
        localMethodHandle1 = buildVarargsArray((MethodHandle)localObject1, (MethodHandle)localObject2, paramInt);
      }
      else
      {
        localObject1 = paramClass.asSubclass([Ljava.lang.Object.class);
        localObject2 = Arrays.copyOf(NO_ARGS_ARRAY, 0, (Class)localObject1);
        MethodHandle localMethodHandle2 = Lazy.MH_fillNewTypedArray.bindTo(localObject2);
        MethodHandle localMethodHandle3 = Lazy.MH_arrayIdentity;
        localMethodHandle1 = buildVarargsArray(localMethodHandle2, localMethodHandle3, paramInt);
      }
    }
    localMethodHandle1 = localMethodHandle1.asType(MethodType.methodType(paramClass, Collections.nCopies(paramInt, localClass)));
    localMethodHandle1 = makeIntrinsic(localMethodHandle1, Intrinsic.NEW_ARRAY);
    assert (assertCorrectArity(localMethodHandle1, paramInt));
    if (paramInt < arrayOfMethodHandle.length) {
      arrayOfMethodHandle[paramInt] = localMethodHandle1;
    }
    return localMethodHandle1;
  }
  
  private static MethodHandle buildArrayProducer(Class<?> paramClass)
  {
    Class localClass = paramClass.getComponentType();
    assert (localClass.isPrimitive());
    return Lazy.MH_copyAsPrimitiveArray.bindTo(Wrapper.forPrimitiveType(localClass));
  }
  
  static void assertSame(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != paramObject2)
    {
      String str = String.format("mh1 != mh2: mh1 = %s (form: %s); mh2 = %s (form: %s)", new Object[] { paramObject1, ((MethodHandle)paramObject1).form, paramObject2, ((MethodHandle)paramObject2).form });
      throw MethodHandleStatics.newInternalError(str);
    }
  }
  
  static
  {
    Object[] arrayOfObject = { Integer.valueOf(255) };
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        this.val$values[0] = Integer.getInteger(MethodHandleImpl.class.getName() + ".MAX_ARITY", 255);
        return null;
      }
    });
  }
  
  static final class ArrayAccessor
  {
    static final int GETTER_INDEX = 0;
    static final int SETTER_INDEX = 1;
    static final int INDEX_LIMIT = 2;
    static final ClassValue<MethodHandle[]> TYPED_ACCESSORS;
    static final MethodHandle OBJECT_ARRAY_GETTER;
    static final MethodHandle OBJECT_ARRAY_SETTER;
    
    ArrayAccessor() {}
    
    static int getElementI(int[] paramArrayOfInt, int paramInt)
    {
      return paramArrayOfInt[paramInt];
    }
    
    static long getElementJ(long[] paramArrayOfLong, int paramInt)
    {
      return paramArrayOfLong[paramInt];
    }
    
    static float getElementF(float[] paramArrayOfFloat, int paramInt)
    {
      return paramArrayOfFloat[paramInt];
    }
    
    static double getElementD(double[] paramArrayOfDouble, int paramInt)
    {
      return paramArrayOfDouble[paramInt];
    }
    
    static boolean getElementZ(boolean[] paramArrayOfBoolean, int paramInt)
    {
      return paramArrayOfBoolean[paramInt];
    }
    
    static byte getElementB(byte[] paramArrayOfByte, int paramInt)
    {
      return paramArrayOfByte[paramInt];
    }
    
    static short getElementS(short[] paramArrayOfShort, int paramInt)
    {
      return paramArrayOfShort[paramInt];
    }
    
    static char getElementC(char[] paramArrayOfChar, int paramInt)
    {
      return paramArrayOfChar[paramInt];
    }
    
    static Object getElementL(Object[] paramArrayOfObject, int paramInt)
    {
      return paramArrayOfObject[paramInt];
    }
    
    static void setElementI(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      paramArrayOfInt[paramInt1] = paramInt2;
    }
    
    static void setElementJ(long[] paramArrayOfLong, int paramInt, long paramLong)
    {
      paramArrayOfLong[paramInt] = paramLong;
    }
    
    static void setElementF(float[] paramArrayOfFloat, int paramInt, float paramFloat)
    {
      paramArrayOfFloat[paramInt] = paramFloat;
    }
    
    static void setElementD(double[] paramArrayOfDouble, int paramInt, double paramDouble)
    {
      paramArrayOfDouble[paramInt] = paramDouble;
    }
    
    static void setElementZ(boolean[] paramArrayOfBoolean, int paramInt, boolean paramBoolean)
    {
      paramArrayOfBoolean[paramInt] = paramBoolean;
    }
    
    static void setElementB(byte[] paramArrayOfByte, int paramInt, byte paramByte)
    {
      paramArrayOfByte[paramInt] = paramByte;
    }
    
    static void setElementS(short[] paramArrayOfShort, int paramInt, short paramShort)
    {
      paramArrayOfShort[paramInt] = paramShort;
    }
    
    static void setElementC(char[] paramArrayOfChar, int paramInt, char paramChar)
    {
      paramArrayOfChar[paramInt] = paramChar;
    }
    
    static void setElementL(Object[] paramArrayOfObject, int paramInt, Object paramObject)
    {
      paramArrayOfObject[paramInt] = paramObject;
    }
    
    static String name(Class<?> paramClass, boolean paramBoolean)
    {
      Class localClass = paramClass.getComponentType();
      if (localClass == null) {
        throw MethodHandleStatics.newIllegalArgumentException("not an array", paramClass);
      }
      return (!paramBoolean ? "getElement" : "setElement") + Wrapper.basicTypeChar(localClass);
    }
    
    static MethodType type(Class<?> paramClass, boolean paramBoolean)
    {
      Object localObject1 = paramClass.getComponentType();
      Object localObject2 = paramClass;
      if (!((Class)localObject1).isPrimitive())
      {
        localObject2 = [Ljava.lang.Object.class;
        localObject1 = Object.class;
      }
      return !paramBoolean ? MethodType.methodType((Class)localObject1, (Class)localObject2, new Class[] { Integer.TYPE }) : MethodType.methodType(Void.TYPE, (Class)localObject2, new Class[] { Integer.TYPE, localObject1 });
    }
    
    static MethodType correctType(Class<?> paramClass, boolean paramBoolean)
    {
      Class localClass = paramClass.getComponentType();
      return !paramBoolean ? MethodType.methodType(localClass, paramClass, new Class[] { Integer.TYPE }) : MethodType.methodType(Void.TYPE, paramClass, new Class[] { Integer.TYPE, localClass });
    }
    
    static MethodHandle getAccessor(Class<?> paramClass, boolean paramBoolean)
    {
      String str = name(paramClass, paramBoolean);
      MethodType localMethodType = type(paramClass, paramBoolean);
      try
      {
        return MethodHandles.Lookup.IMPL_LOOKUP.findStatic(ArrayAccessor.class, str, localMethodType);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
      }
    }
    
    static
    {
      TYPED_ACCESSORS = new ClassValue()
      {
        protected MethodHandle[] computeValue(Class<?> paramAnonymousClass)
        {
          return new MethodHandle[2];
        }
      };
      MethodHandle[] arrayOfMethodHandle = (MethodHandle[])TYPED_ACCESSORS.get([Ljava.lang.Object.class);
      MethodHandle tmp52_49 = MethodHandleImpl.makeIntrinsic(getAccessor([Ljava.lang.Object.class, false), MethodHandleImpl.Intrinsic.ARRAY_LOAD);
      OBJECT_ARRAY_GETTER = tmp52_49;
      arrayOfMethodHandle[0] = tmp52_49;
      MethodHandle tmp71_68 = MethodHandleImpl.makeIntrinsic(getAccessor([Ljava.lang.Object.class, true), MethodHandleImpl.Intrinsic.ARRAY_STORE);
      OBJECT_ARRAY_SETTER = tmp71_68;
      arrayOfMethodHandle[1] = tmp71_68;
      assert (InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_GETTER.internalMemberName()));
      assert (InvokerBytecodeGenerator.isStaticallyInvocable(OBJECT_ARRAY_SETTER.internalMemberName()));
    }
  }
  
  private static final class AsVarargsCollector
    extends DelegatingMethodHandle
  {
    private final MethodHandle target;
    private final Class<?> arrayType;
    @Stable
    private MethodHandle asCollectorCache;
    
    AsVarargsCollector(MethodHandle paramMethodHandle, Class<?> paramClass)
    {
      this(paramMethodHandle.type(), paramMethodHandle, paramClass);
    }
    
    AsVarargsCollector(MethodType paramMethodType, MethodHandle paramMethodHandle, Class<?> paramClass)
    {
      super(paramMethodHandle);
      this.target = paramMethodHandle;
      this.arrayType = paramClass;
      this.asCollectorCache = paramMethodHandle.asCollector(paramClass, 0);
    }
    
    public boolean isVarargsCollector()
    {
      return true;
    }
    
    protected MethodHandle getTarget()
    {
      return this.target;
    }
    
    public MethodHandle asFixedArity()
    {
      return this.target;
    }
    
    MethodHandle setVarargs(MemberName paramMemberName)
    {
      if (paramMemberName.isVarargs()) {
        return this;
      }
      return asFixedArity();
    }
    
    public MethodHandle asTypeUncached(MethodType paramMethodType)
    {
      MethodType localMethodType = type();
      int i = localMethodType.parameterCount() - 1;
      int j = paramMethodType.parameterCount();
      if ((j == i + 1) && (localMethodType.parameterType(i).isAssignableFrom(paramMethodType.parameterType(i)))) {
        return this.asTypeCache = asFixedArity().asType(paramMethodType);
      }
      MethodHandle localMethodHandle1 = this.asCollectorCache;
      if ((localMethodHandle1 != null) && (localMethodHandle1.type().parameterCount() == j)) {
        return this.asTypeCache = localMethodHandle1.asType(paramMethodType);
      }
      int k = j - i;
      MethodHandle localMethodHandle2;
      try
      {
        localMethodHandle2 = asFixedArity().asCollector(this.arrayType, k);
        if ((!$assertionsDisabled) && (localMethodHandle2.type().parameterCount() != j)) {
          throw new AssertionError("newArity=" + j + " but collector=" + localMethodHandle2);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new WrongMethodTypeException("cannot build collector", localIllegalArgumentException);
      }
      this.asCollectorCache = localMethodHandle2;
      return this.asTypeCache = localMethodHandle2.asType(paramMethodType);
    }
    
    boolean viewAsTypeChecks(MethodType paramMethodType, boolean paramBoolean)
    {
      super.viewAsTypeChecks(paramMethodType, true);
      if (paramBoolean) {
        return true;
      }
      if ((!$assertionsDisabled) && (!type().lastParameterType().getComponentType().isAssignableFrom(paramMethodType.lastParameterType().getComponentType()))) {
        throw new AssertionError(Arrays.asList(new Object[] { this, paramMethodType }));
      }
      return true;
    }
  }
  
  private static class BindCaller
  {
    private static ClassValue<MethodHandle> CV_makeInjectedInvoker;
    private static final MethodHandle MH_checkCallerClass;
    private static final byte[] T_BYTES = (byte[])localObject[0];
    
    private BindCaller() {}
    
    static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass)
    {
      if ((paramClass == null) || (paramClass.isArray()) || (paramClass.isPrimitive()) || (paramClass.getName().startsWith("java.")) || (paramClass.getName().startsWith("sun."))) {
        throw new InternalError();
      }
      MethodHandle localMethodHandle1 = prepareForInvoker(paramMethodHandle);
      MethodHandle localMethodHandle2 = (MethodHandle)CV_makeInjectedInvoker.get(paramClass);
      return restoreToType(localMethodHandle2.bindTo(localMethodHandle1), paramMethodHandle, paramClass);
    }
    
    private static MethodHandle makeInjectedInvoker(Class<?> paramClass)
    {
      Class localClass = MethodHandleStatics.UNSAFE.defineAnonymousClass(paramClass, T_BYTES, null);
      if (paramClass.getClassLoader() != localClass.getClassLoader()) {
        throw new InternalError(paramClass.getName() + " (CL)");
      }
      try
      {
        if (paramClass.getProtectionDomain() != localClass.getProtectionDomain()) {
          throw new InternalError(paramClass.getName() + " (PD)");
        }
      }
      catch (SecurityException localSecurityException) {}
      try
      {
        MethodHandle localMethodHandle1 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(localClass, "init", MethodType.methodType(Void.TYPE));
        localMethodHandle1.invokeExact();
      }
      catch (Throwable localThrowable1)
      {
        throw MethodHandleStatics.uncaughtException(localThrowable1);
      }
      MethodHandle localMethodHandle2;
      try
      {
        MethodType localMethodType = MethodType.methodType(Object.class, MethodHandle.class, new Class[] { [Ljava.lang.Object.class });
        localMethodHandle2 = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(localClass, "invoke_V", localMethodType);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.uncaughtException(localReflectiveOperationException);
      }
      try
      {
        MethodHandle localMethodHandle3 = prepareForInvoker(MH_checkCallerClass);
        Object localObject = localMethodHandle2.invokeExact(localMethodHandle3, new Object[] { paramClass, localClass });
      }
      catch (Throwable localThrowable2)
      {
        throw new InternalError(localThrowable2);
      }
      return localMethodHandle2;
    }
    
    private static MethodHandle prepareForInvoker(MethodHandle paramMethodHandle)
    {
      paramMethodHandle = paramMethodHandle.asFixedArity();
      MethodType localMethodType = paramMethodHandle.type();
      int i = localMethodType.parameterCount();
      MethodHandle localMethodHandle = paramMethodHandle.asType(localMethodType.generic());
      localMethodHandle.internalForm().compileToBytecode();
      localMethodHandle = localMethodHandle.asSpreader([Ljava.lang.Object.class, i);
      localMethodHandle.internalForm().compileToBytecode();
      return localMethodHandle;
    }
    
    private static MethodHandle restoreToType(MethodHandle paramMethodHandle1, MethodHandle paramMethodHandle2, Class<?> paramClass)
    {
      MethodType localMethodType = paramMethodHandle2.type();
      Object localObject = paramMethodHandle1.asCollector([Ljava.lang.Object.class, localMethodType.parameterCount());
      MemberName localMemberName = paramMethodHandle2.internalMemberName();
      localObject = ((MethodHandle)localObject).asType(localMethodType);
      localObject = new MethodHandleImpl.WrappedMember((MethodHandle)localObject, localMethodType, localMemberName, paramMethodHandle2.isInvokeSpecial(), paramClass, null);
      return localObject;
    }
    
    @CallerSensitive
    private static boolean checkCallerClass(Class<?> paramClass1, Class<?> paramClass2)
    {
      Class localClass = Reflection.getCallerClass();
      if ((localClass != paramClass1) && (localClass != paramClass2)) {
        throw new InternalError("found " + localClass.getName() + ", expected " + paramClass1.getName() + (paramClass1 == paramClass2 ? "" : new StringBuilder().append(", or else ").append(paramClass2.getName()).toString()));
      }
      return true;
    }
    
    static
    {
      CV_makeInjectedInvoker = new ClassValue()
      {
        protected MethodHandle computeValue(Class<?> paramAnonymousClass)
        {
          return MethodHandleImpl.BindCaller.makeInjectedInvoker(paramAnonymousClass);
        }
      };
      Object localObject = BindCaller.class;
      assert (checkCallerClass((Class)localObject, (Class)localObject));
      try
      {
        MH_checkCallerClass = MethodHandles.Lookup.IMPL_LOOKUP.findStatic((Class)localObject, "checkCallerClass", MethodType.methodType(Boolean.TYPE, Class.class, new Class[] { Class.class }));
        if ((!$assertionsDisabled) && (!MH_checkCallerClass.invokeExact((Class)localObject, (Class)localObject))) {
          throw new AssertionError();
        }
      }
      catch (Throwable localThrowable)
      {
        throw new InternalError(localThrowable);
      }
      localObject = new Object[] { null };
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          try
          {
            MethodHandleImpl.BindCaller.T localT = MethodHandleImpl.BindCaller.T.class;
            String str1 = localT.getName();
            String str2 = str1.substring(str1.lastIndexOf('.') + 1) + ".class";
            URLConnection localURLConnection = localT.getResource(str2).openConnection();
            int i = localURLConnection.getContentLength();
            byte[] arrayOfByte = new byte[i];
            InputStream localInputStream = localURLConnection.getInputStream();
            Object localObject1 = null;
            try
            {
              int j = localInputStream.read(arrayOfByte);
              if (j != i) {
                throw new IOException(str2);
              }
            }
            catch (Throwable localThrowable2)
            {
              localObject1 = localThrowable2;
              throw localThrowable2;
            }
            finally
            {
              if (localInputStream != null) {
                if (localObject1 != null) {
                  try
                  {
                    localInputStream.close();
                  }
                  catch (Throwable localThrowable3)
                  {
                    localObject1.addSuppressed(localThrowable3);
                  }
                } else {
                  localInputStream.close();
                }
              }
            }
            this.val$values[0] = arrayOfByte;
          }
          catch (IOException localIOException)
          {
            throw new InternalError(localIOException);
          }
          return null;
        }
      });
    }
    
    private static class T
    {
      private T() {}
      
      static void init() {}
      
      static Object invoke_V(MethodHandle paramMethodHandle, Object[] paramArrayOfObject)
        throws Throwable
      {
        return paramMethodHandle.invokeExact(paramArrayOfObject);
      }
    }
  }
  
  static class CountingWrapper
    extends DelegatingMethodHandle
  {
    private final MethodHandle target;
    private int count;
    private Function<MethodHandle, LambdaForm> countingFormProducer;
    private Function<MethodHandle, LambdaForm> nonCountingFormProducer;
    private volatile boolean isCounting;
    static final LambdaForm.NamedFunction NF_maybeStopCounting;
    
    private CountingWrapper(MethodHandle paramMethodHandle, LambdaForm paramLambdaForm, Function<MethodHandle, LambdaForm> paramFunction1, Function<MethodHandle, LambdaForm> paramFunction2, int paramInt)
    {
      super(paramLambdaForm);
      this.target = paramMethodHandle;
      this.count = paramInt;
      this.countingFormProducer = paramFunction1;
      this.nonCountingFormProducer = paramFunction2;
      this.isCounting = (paramInt > 0);
    }
    
    @LambdaForm.Hidden
    protected MethodHandle getTarget()
    {
      return this.target;
    }
    
    public MethodHandle asTypeUncached(MethodType paramMethodType)
    {
      MethodHandle localMethodHandle = this.target.asType(paramMethodType);
      Object localObject;
      if (this.isCounting)
      {
        LambdaForm localLambdaForm = (LambdaForm)this.countingFormProducer.apply(localMethodHandle);
        localObject = new CountingWrapper(localMethodHandle, localLambdaForm, this.countingFormProducer, this.nonCountingFormProducer, MethodHandleStatics.DONT_INLINE_THRESHOLD);
      }
      else
      {
        localObject = localMethodHandle;
      }
      return this.asTypeCache = localObject;
    }
    
    boolean countDown()
    {
      if (this.count <= 0)
      {
        if (this.isCounting)
        {
          this.isCounting = false;
          return true;
        }
        return false;
      }
      this.count -= 1;
      return false;
    }
    
    @LambdaForm.Hidden
    static void maybeStopCounting(Object paramObject)
    {
      CountingWrapper localCountingWrapper = (CountingWrapper)paramObject;
      if (localCountingWrapper.countDown())
      {
        LambdaForm localLambdaForm = (LambdaForm)localCountingWrapper.nonCountingFormProducer.apply(localCountingWrapper.target);
        localLambdaForm.compileToBytecode();
        localCountingWrapper.updateForm(localLambdaForm);
      }
    }
    
    static
    {
      CountingWrapper localCountingWrapper = CountingWrapper.class;
      try
      {
        NF_maybeStopCounting = new LambdaForm.NamedFunction(localCountingWrapper.getDeclaredMethod("maybeStopCounting", new Class[] { Object.class }));
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
  }
  
  static enum Intrinsic
  {
    SELECT_ALTERNATIVE,  GUARD_WITH_CATCH,  NEW_ARRAY,  ARRAY_LOAD,  ARRAY_STORE,  IDENTITY,  ZERO,  NONE;
    
    private Intrinsic() {}
  }
  
  private static final class IntrinsicMethodHandle
    extends DelegatingMethodHandle
  {
    private final MethodHandle target;
    private final MethodHandleImpl.Intrinsic intrinsicName;
    
    IntrinsicMethodHandle(MethodHandle paramMethodHandle, MethodHandleImpl.Intrinsic paramIntrinsic)
    {
      super(paramMethodHandle);
      this.target = paramMethodHandle;
      this.intrinsicName = paramIntrinsic;
    }
    
    protected MethodHandle getTarget()
    {
      return this.target;
    }
    
    MethodHandleImpl.Intrinsic intrinsicName()
    {
      return this.intrinsicName;
    }
    
    public MethodHandle asTypeUncached(MethodType paramMethodType)
    {
      return this.asTypeCache = this.target.asType(paramMethodType);
    }
    
    String internalProperties()
    {
      return super.internalProperties() + "\n& Intrinsic=" + this.intrinsicName;
    }
    
    public MethodHandle asCollector(Class<?> paramClass, int paramInt)
    {
      if (this.intrinsicName == MethodHandleImpl.Intrinsic.IDENTITY)
      {
        MethodType localMethodType = type().asCollectorType(paramClass, paramInt);
        MethodHandle localMethodHandle = MethodHandleImpl.varargsArray(paramClass, paramInt);
        return localMethodHandle.asType(localMethodType);
      }
      return super.asCollector(paramClass, paramInt);
    }
  }
  
  static class Lazy
  {
    private static final Class<?> MHI = MethodHandleImpl.class;
    private static final MethodHandle[] ARRAYS = MethodHandleImpl.access$000();
    private static final MethodHandle[] FILL_ARRAYS = MethodHandleImpl.access$100();
    static final LambdaForm.NamedFunction NF_checkSpreadArgument;
    static final LambdaForm.NamedFunction NF_guardWithCatch;
    static final LambdaForm.NamedFunction NF_throwException;
    static final LambdaForm.NamedFunction NF_profileBoolean;
    static final MethodHandle MH_castReference;
    static final MethodHandle MH_selectAlternative;
    static final MethodHandle MH_copyAsPrimitiveArray;
    static final MethodHandle MH_fillNewTypedArray;
    static final MethodHandle MH_fillNewArray;
    static final MethodHandle MH_arrayIdentity;
    
    Lazy() {}
    
    static
    {
      try
      {
        NF_checkSpreadArgument = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("checkSpreadArgument", new Class[] { Object.class, Integer.TYPE }));
        NF_guardWithCatch = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("guardWithCatch", new Class[] { MethodHandle.class, Class.class, MethodHandle.class, [Ljava.lang.Object.class }));
        NF_throwException = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("throwException", new Class[] { Throwable.class }));
        NF_profileBoolean = new LambdaForm.NamedFunction(MHI.getDeclaredMethod("profileBoolean", new Class[] { Boolean.TYPE, [I.class }));
        NF_checkSpreadArgument.resolve();
        NF_guardWithCatch.resolve();
        NF_throwException.resolve();
        NF_profileBoolean.resolve();
        MH_castReference = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "castReference", MethodType.methodType(Object.class, Class.class, new Class[] { Object.class }));
        MH_copyAsPrimitiveArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "copyAsPrimitiveArray", MethodType.methodType(Object.class, Wrapper.class, new Class[] { [Ljava.lang.Object.class }));
        MH_arrayIdentity = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "identity", MethodType.methodType([Ljava.lang.Object.class, [Ljava.lang.Object.class));
        MH_fillNewArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewArray", MethodType.methodType([Ljava.lang.Object.class, Integer.class, new Class[] { [Ljava.lang.Object.class }));
        MH_fillNewTypedArray = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "fillNewTypedArray", MethodType.methodType([Ljava.lang.Object.class, [Ljava.lang.Object.class, new Class[] { Integer.class, [Ljava.lang.Object.class }));
        MH_selectAlternative = MethodHandleImpl.makeIntrinsic(MethodHandles.Lookup.IMPL_LOOKUP.findStatic(MHI, "selectAlternative", MethodType.methodType(MethodHandle.class, Boolean.TYPE, new Class[] { MethodHandle.class, MethodHandle.class })), MethodHandleImpl.Intrinsic.SELECT_ALTERNATIVE);
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
  }
  
  private static final class WrappedMember
    extends DelegatingMethodHandle
  {
    private final MethodHandle target;
    private final MemberName member;
    private final Class<?> callerClass;
    private final boolean isInvokeSpecial;
    
    private WrappedMember(MethodHandle paramMethodHandle, MethodType paramMethodType, MemberName paramMemberName, boolean paramBoolean, Class<?> paramClass)
    {
      super(paramMethodHandle);
      this.target = paramMethodHandle;
      this.member = paramMemberName;
      this.callerClass = paramClass;
      this.isInvokeSpecial = paramBoolean;
    }
    
    MemberName internalMemberName()
    {
      return this.member;
    }
    
    Class<?> internalCallerClass()
    {
      return this.callerClass;
    }
    
    boolean isInvokeSpecial()
    {
      return this.isInvokeSpecial;
    }
    
    protected MethodHandle getTarget()
    {
      return this.target;
    }
    
    public MethodHandle asTypeUncached(MethodType paramMethodType)
    {
      return this.asTypeCache = this.target.asType(paramMethodType);
    }
  }
}
