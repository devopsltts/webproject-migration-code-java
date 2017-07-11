package java.lang.invoke;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

class Invokers
{
  private final MethodType targetType;
  @Stable
  private final MethodHandle[] invokers = new MethodHandle[3];
  static final int INV_EXACT = 0;
  static final int INV_GENERIC = 1;
  static final int INV_BASIC = 2;
  static final int INV_LIMIT = 3;
  private static final int MH_LINKER_ARG_APPENDED = 1;
  private static final LambdaForm.NamedFunction NF_checkExactType;
  private static final LambdaForm.NamedFunction NF_checkGenericType;
  private static final LambdaForm.NamedFunction NF_getCallSiteTarget;
  private static final LambdaForm.NamedFunction NF_checkCustomized;
  
  Invokers(MethodType paramMethodType)
  {
    this.targetType = paramMethodType;
  }
  
  MethodHandle exactInvoker()
  {
    MethodHandle localMethodHandle = cachedInvoker(0);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    localMethodHandle = makeExactOrGeneralInvoker(true);
    return setCachedInvoker(0, localMethodHandle);
  }
  
  MethodHandle genericInvoker()
  {
    MethodHandle localMethodHandle = cachedInvoker(1);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    localMethodHandle = makeExactOrGeneralInvoker(false);
    return setCachedInvoker(1, localMethodHandle);
  }
  
  MethodHandle basicInvoker()
  {
    Object localObject = cachedInvoker(2);
    if (localObject != null) {
      return localObject;
    }
    MethodType localMethodType = this.targetType.basicType();
    if (localMethodType != this.targetType) {
      return setCachedInvoker(2, localMethodType.invokers().basicInvoker());
    }
    localObject = localMethodType.form().cachedMethodHandle(0);
    if (localObject == null)
    {
      MemberName localMemberName = invokeBasicMethod(localMethodType);
      localObject = DirectMethodHandle.make(localMemberName);
      assert (checkInvoker((MethodHandle)localObject));
      localObject = localMethodType.form().setCachedMethodHandle(0, (MethodHandle)localObject);
    }
    return setCachedInvoker(2, (MethodHandle)localObject);
  }
  
  private MethodHandle cachedInvoker(int paramInt)
  {
    return this.invokers[paramInt];
  }
  
  private synchronized MethodHandle setCachedInvoker(int paramInt, MethodHandle paramMethodHandle)
  {
    MethodHandle localMethodHandle = this.invokers[paramInt];
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    return this.invokers[paramInt] =  = paramMethodHandle;
  }
  
  private MethodHandle makeExactOrGeneralInvoker(boolean paramBoolean)
  {
    MethodType localMethodType1 = this.targetType;
    MethodType localMethodType2 = localMethodType1.invokerType();
    int i = paramBoolean ? 11 : 13;
    LambdaForm localLambdaForm = invokeHandleForm(localMethodType1, false, i);
    Object localObject = BoundMethodHandle.bindSingle(localMethodType2, localLambdaForm, localMethodType1);
    String str = paramBoolean ? "invokeExact" : "invoke";
    localObject = ((MethodHandle)localObject).withInternalMemberName(MemberName.makeMethodHandleInvoke(str, localMethodType1), false);
    assert (checkInvoker((MethodHandle)localObject));
    maybeCompileToBytecode((MethodHandle)localObject);
    return localObject;
  }
  
  private void maybeCompileToBytecode(MethodHandle paramMethodHandle)
  {
    if ((this.targetType == this.targetType.erase()) && (this.targetType.parameterCount() < 10)) {
      paramMethodHandle.form.compileToBytecode();
    }
  }
  
  static MemberName invokeBasicMethod(MethodType paramMethodType)
  {
    assert (paramMethodType == paramMethodType.basicType());
    try
    {
      return MethodHandles.Lookup.IMPL_LOOKUP.resolveOrFail((byte)5, MethodHandle.class, "invokeBasic", paramMethodType);
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError("JVM cannot find invoker for " + paramMethodType, localReflectiveOperationException);
    }
  }
  
  private boolean checkInvoker(MethodHandle paramMethodHandle)
  {
    if ((!$assertionsDisabled) && (!this.targetType.invokerType().equals(paramMethodHandle.type()))) {
      throw new AssertionError(Arrays.asList(new Object[] { this.targetType, this.targetType.invokerType(), paramMethodHandle }));
    }
    assert ((paramMethodHandle.internalMemberName() == null) || (paramMethodHandle.internalMemberName().getMethodType().equals(this.targetType)));
    assert (!paramMethodHandle.isVarargsCollector());
    return true;
  }
  
  MethodHandle spreadInvoker(int paramInt)
  {
    int i = this.targetType.parameterCount() - paramInt;
    MethodType localMethodType1 = this.targetType;
    Class localClass = impliedRestargType(localMethodType1, paramInt);
    if (localMethodType1.parameterSlotCount() <= 253) {
      return genericInvoker().asSpreader(localClass, i);
    }
    MethodType localMethodType2 = localMethodType1.replaceParameterTypes(paramInt, localMethodType1.parameterCount(), new Class[] { localClass });
    MethodHandle localMethodHandle1 = MethodHandles.invoker(localMethodType2);
    MethodHandle localMethodHandle2 = MethodHandles.insertArguments(Lazy.MH_asSpreader, 1, new Object[] { localClass, Integer.valueOf(i) });
    return MethodHandles.filterArgument(localMethodHandle1, 0, localMethodHandle2);
  }
  
  private static Class<?> impliedRestargType(MethodType paramMethodType, int paramInt)
  {
    if (paramMethodType.isGeneric()) {
      return [Ljava.lang.Object.class;
    }
    int i = paramMethodType.parameterCount();
    if (paramInt >= i) {
      return [Ljava.lang.Object.class;
    }
    Class localClass = paramMethodType.parameterType(paramInt);
    for (int j = paramInt + 1; j < i; j++) {
      if (localClass != paramMethodType.parameterType(j)) {
        throw MethodHandleStatics.newIllegalArgumentException("need homogeneous rest arguments", paramMethodType);
      }
    }
    if (localClass == Object.class) {
      return [Ljava.lang.Object.class;
    }
    return Array.newInstance(localClass, 0).getClass();
  }
  
  public String toString()
  {
    return "Invokers" + this.targetType;
  }
  
  static MemberName methodHandleInvokeLinkerMethod(String paramString, MethodType paramMethodType, Object[] paramArrayOfObject)
  {
    Object localObject = paramString;
    int j = -1;
    switch (((String)localObject).hashCode())
    {
    case 941760871: 
      if (((String)localObject).equals("invokeExact")) {
        j = 0;
      }
      break;
    case -1183693704: 
      if (((String)localObject).equals("invoke")) {
        j = 1;
      }
      break;
    }
    int i;
    switch (j)
    {
    case 0: 
      i = 10;
      break;
    case 1: 
      i = 12;
      break;
    default: 
      throw new InternalError("not invoker: " + paramString);
    }
    if (paramMethodType.parameterSlotCount() <= 253)
    {
      localObject = invokeHandleForm(paramMethodType, false, i);
      paramArrayOfObject[0] = paramMethodType;
    }
    else
    {
      localObject = invokeHandleForm(paramMethodType, true, i);
    }
    return ((LambdaForm)localObject).vmentry;
  }
  
  private static LambdaForm invokeHandleForm(MethodType paramMethodType, boolean paramBoolean, int paramInt)
  {
    int i;
    if (!paramBoolean)
    {
      paramMethodType = paramMethodType.basicType();
      i = 1;
    }
    else
    {
      i = 0;
    }
    int j;
    int k;
    String str;
    switch (paramInt)
    {
    case 10: 
      j = 1;
      k = 0;
      str = "invokeExact_MT";
      break;
    case 11: 
      j = 0;
      k = 0;
      str = "exactInvoker";
      break;
    case 12: 
      j = 1;
      k = 1;
      str = "invoke_MT";
      break;
    case 13: 
      j = 0;
      k = 1;
      str = "invoker";
      break;
    default: 
      throw new InternalError();
    }
    if (i != 0)
    {
      localLambdaForm = paramMethodType.form().cachedLambdaForm(paramInt);
      if (localLambdaForm != null) {
        return localLambdaForm;
      }
    }
    int m = 0 + (j != 0 ? 0 : 1);
    int n = m + 1;
    int i1 = n + paramMethodType.parameterCount();
    int i2 = i1 + ((j != 0) && (!paramBoolean) ? 1 : 0);
    int i3 = i1;
    int i4 = paramBoolean ? -1 : i3++;
    int i5 = i3++;
    int i6 = MethodHandleStatics.CUSTOMIZE_THRESHOLD >= 0 ? i3++ : -1;
    int i7 = i3++;
    MethodType localMethodType = paramMethodType.invokerType();
    if (j != 0)
    {
      if (!paramBoolean) {
        localMethodType = localMethodType.appendParameterTypes(new Class[] { MemberName.class });
      }
    }
    else {
      localMethodType = localMethodType.invokerType();
    }
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(i3 - i2, localMethodType);
    if ((!$assertionsDisabled) && (arrayOfName.length != i3)) {
      throw new AssertionError(Arrays.asList(new Serializable[] { paramMethodType, Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt), Integer.valueOf(i3), Integer.valueOf(arrayOfName.length) }));
    }
    if (i4 >= i2)
    {
      assert (arrayOfName[i4] == null);
      localObject1 = BoundMethodHandle.speciesData_L();
      arrayOfName[0] = arrayOfName[0].withConstraint(localObject1);
      localObject2 = ((BoundMethodHandle.SpeciesData)localObject1).getterFunction(0);
      arrayOfName[i4] = new LambdaForm.Name((LambdaForm.NamedFunction)localObject2, new Object[] { arrayOfName[0] });
    }
    Object localObject1 = paramMethodType.basicType();
    Object localObject2 = Arrays.copyOfRange(arrayOfName, m, i1, [Ljava.lang.Object.class);
    LambdaForm.Name localName = paramBoolean ? paramMethodType : arrayOfName[i4];
    if (k == 0)
    {
      arrayOfName[i5] = new LambdaForm.Name(NF_checkExactType, new Object[] { arrayOfName[m], localName });
    }
    else
    {
      arrayOfName[i5] = new LambdaForm.Name(NF_checkGenericType, new Object[] { arrayOfName[m], localName });
      localObject2[0] = arrayOfName[i5];
    }
    if (i6 != -1) {
      arrayOfName[i6] = new LambdaForm.Name(NF_checkCustomized, new Object[] { localObject2[0] });
    }
    arrayOfName[i7] = new LambdaForm.Name((MethodType)localObject1, (Object[])localObject2);
    LambdaForm localLambdaForm = new LambdaForm(str, i2, arrayOfName);
    if (j != 0) {
      localLambdaForm.compileToBytecode();
    }
    if (i != 0) {
      localLambdaForm = paramMethodType.form().setCachedLambdaForm(paramInt, localLambdaForm);
    }
    return localLambdaForm;
  }
  
  static WrongMethodTypeException newWrongMethodTypeException(MethodType paramMethodType1, MethodType paramMethodType2)
  {
    return new WrongMethodTypeException("expected " + paramMethodType2 + " but found " + paramMethodType1);
  }
  
  @ForceInline
  static void checkExactType(Object paramObject1, Object paramObject2)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramObject1;
    MethodType localMethodType1 = (MethodType)paramObject2;
    MethodType localMethodType2 = localMethodHandle.type();
    if (localMethodType2 != localMethodType1) {
      throw newWrongMethodTypeException(localMethodType1, localMethodType2);
    }
  }
  
  @ForceInline
  static Object checkGenericType(Object paramObject1, Object paramObject2)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramObject1;
    MethodType localMethodType = (MethodType)paramObject2;
    return localMethodHandle.asType(localMethodType);
  }
  
  static MemberName linkToCallSiteMethod(MethodType paramMethodType)
  {
    LambdaForm localLambdaForm = callSiteForm(paramMethodType, false);
    return localLambdaForm.vmentry;
  }
  
  static MemberName linkToTargetMethod(MethodType paramMethodType)
  {
    LambdaForm localLambdaForm = callSiteForm(paramMethodType, true);
    return localLambdaForm.vmentry;
  }
  
  private static LambdaForm callSiteForm(MethodType paramMethodType, boolean paramBoolean)
  {
    paramMethodType = paramMethodType.basicType();
    int i = paramBoolean ? 15 : 14;
    LambdaForm localLambdaForm = paramMethodType.form().cachedLambdaForm(i);
    if (localLambdaForm != null) {
      return localLambdaForm;
    }
    int j = 0 + paramMethodType.parameterCount();
    int k = j + 1;
    int m = j;
    int n = m++;
    int i1 = paramBoolean ? -1 : n;
    int i2 = paramBoolean ? n : m++;
    int i3 = m++;
    MethodType localMethodType = paramMethodType.appendParameterTypes(new Class[] { paramBoolean ? MethodHandle.class : CallSite.class });
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(m - k, localMethodType);
    assert (arrayOfName.length == m);
    assert (arrayOfName[n] != null);
    if (!paramBoolean) {
      arrayOfName[i2] = new LambdaForm.Name(NF_getCallSiteTarget, new Object[] { arrayOfName[i1] });
    }
    Object[] arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, j + 1, [Ljava.lang.Object.class);
    System.arraycopy(arrayOfObject, 0, arrayOfObject, 1, arrayOfObject.length - 1);
    arrayOfObject[0] = arrayOfName[i2];
    arrayOfName[i3] = new LambdaForm.Name(paramMethodType, arrayOfObject);
    localLambdaForm = new LambdaForm(paramBoolean ? "linkToTargetMethod" : "linkToCallSite", k, arrayOfName);
    localLambdaForm.compileToBytecode();
    localLambdaForm = paramMethodType.form().setCachedLambdaForm(i, localLambdaForm);
    return localLambdaForm;
  }
  
  @ForceInline
  static Object getCallSiteTarget(Object paramObject)
  {
    return ((CallSite)paramObject).getTarget();
  }
  
  @ForceInline
  static void checkCustomized(Object paramObject)
  {
    MethodHandle localMethodHandle = (MethodHandle)paramObject;
    if (localMethodHandle.form.customized == null) {
      maybeCustomize(localMethodHandle);
    }
  }
  
  @DontInline
  static void maybeCustomize(MethodHandle paramMethodHandle)
  {
    int i = paramMethodHandle.customizationCount;
    if (i >= MethodHandleStatics.CUSTOMIZE_THRESHOLD) {
      paramMethodHandle.customize();
    } else {
      paramMethodHandle.customizationCount = ((byte)(i + 1));
    }
  }
  
  static
  {
    try
    {
      LambdaForm.NamedFunction[] tmp20_17 = new LambdaForm.NamedFunction[4];
      void tmp50_47 = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkExactType", new Class[] { Object.class, Object.class }));
      NF_checkExactType = tmp50_47;
      tmp20_17[0] = tmp50_47;
      LambdaForm.NamedFunction[] tmp55_20 = tmp20_17;
      void tmp85_82 = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkGenericType", new Class[] { Object.class, Object.class }));
      NF_checkGenericType = tmp85_82;
      tmp55_20[1] = tmp85_82;
      LambdaForm.NamedFunction[] tmp90_55 = tmp55_20;
      void tmp115_112 = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("getCallSiteTarget", new Class[] { Object.class }));
      NF_getCallSiteTarget = tmp115_112;
      tmp90_55[2] = tmp115_112;
      LambdaForm.NamedFunction[] tmp120_90 = tmp90_55;
      void tmp145_142 = new LambdaForm.NamedFunction(Invokers.class.getDeclaredMethod("checkCustomized", new Class[] { Object.class }));
      NF_checkCustomized = tmp145_142;
      tmp120_90[3] = tmp145_142;
      LambdaForm.NamedFunction[] arrayOfNamedFunction1 = tmp120_90;
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
  
  private static class Lazy
  {
    private static final MethodHandle MH_asSpreader;
    
    private Lazy() {}
    
    static
    {
      try
      {
        MH_asSpreader = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(MethodHandle.class, "asSpreader", MethodType.methodType(MethodHandle.class, Class.class, new Class[] { Integer.TYPE }));
      }
      catch (ReflectiveOperationException localReflectiveOperationException)
      {
        throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
      }
    }
  }
}
