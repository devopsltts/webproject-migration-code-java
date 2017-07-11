package java.lang.invoke;

import java.util.Arrays;

abstract class DelegatingMethodHandle
  extends MethodHandle
{
  static final LambdaForm.NamedFunction NF_getTarget;
  
  protected DelegatingMethodHandle(MethodHandle paramMethodHandle)
  {
    this(paramMethodHandle.type(), paramMethodHandle);
  }
  
  protected DelegatingMethodHandle(MethodType paramMethodType, MethodHandle paramMethodHandle)
  {
    super(paramMethodType, chooseDelegatingForm(paramMethodHandle));
  }
  
  protected DelegatingMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    super(paramMethodType, paramLambdaForm);
  }
  
  protected abstract MethodHandle getTarget();
  
  abstract MethodHandle asTypeUncached(MethodType paramMethodType);
  
  MemberName internalMemberName()
  {
    return getTarget().internalMemberName();
  }
  
  boolean isInvokeSpecial()
  {
    return getTarget().isInvokeSpecial();
  }
  
  Class<?> internalCallerClass()
  {
    return getTarget().internalCallerClass();
  }
  
  MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    throw MethodHandleStatics.newIllegalArgumentException("do not use this");
  }
  
  String internalProperties()
  {
    return "\n& Class=" + getClass().getSimpleName() + "\n& Target=" + getTarget().debugString();
  }
  
  BoundMethodHandle rebind()
  {
    return getTarget().rebind();
  }
  
  private static LambdaForm chooseDelegatingForm(MethodHandle paramMethodHandle)
  {
    if ((paramMethodHandle instanceof SimpleMethodHandle)) {
      return paramMethodHandle.internalForm();
    }
    return makeReinvokerForm(paramMethodHandle, 8, DelegatingMethodHandle.class, NF_getTarget);
  }
  
  static LambdaForm makeReinvokerForm(MethodHandle paramMethodHandle, int paramInt, Object paramObject, LambdaForm.NamedFunction paramNamedFunction)
  {
    String str;
    switch (paramInt)
    {
    case 7: 
      str = "BMH.reinvoke";
      break;
    case 8: 
      str = "MH.delegate";
      break;
    default: 
      str = "MH.reinvoke";
    }
    return makeReinvokerForm(paramMethodHandle, paramInt, paramObject, str, true, paramNamedFunction, null);
  }
  
  static LambdaForm makeReinvokerForm(MethodHandle paramMethodHandle, int paramInt, Object paramObject, String paramString, boolean paramBoolean, LambdaForm.NamedFunction paramNamedFunction1, LambdaForm.NamedFunction paramNamedFunction2)
  {
    MethodType localMethodType = paramMethodHandle.type().basicType();
    int i = (paramInt < 0) || (localMethodType.parameterSlotCount() > 253) ? 1 : 0;
    int j = paramNamedFunction2 != null ? 1 : 0;
    if (i == 0)
    {
      localLambdaForm = localMethodType.form().cachedLambdaForm(paramInt);
      if (localLambdaForm != null) {
        return localLambdaForm;
      }
    }
    int k = 1 + localMethodType.parameterCount();
    int m = k;
    int n = j != 0 ? m++ : -1;
    int i1 = i != 0 ? -1 : m++;
    int i2 = m++;
    LambdaForm.Name[] arrayOfName = LambdaForm.arguments(m - k, localMethodType.invokerType());
    assert (arrayOfName.length == m);
    arrayOfName[0] = arrayOfName[0].withConstraint(paramObject);
    if (j != 0) {
      arrayOfName[n] = new LambdaForm.Name(paramNamedFunction2, new Object[] { arrayOfName[0] });
    }
    Object[] arrayOfObject;
    if (i != 0)
    {
      arrayOfObject = Arrays.copyOfRange(arrayOfName, 1, k, [Ljava.lang.Object.class);
      arrayOfName[i2] = new LambdaForm.Name(paramMethodHandle, arrayOfObject);
    }
    else
    {
      arrayOfName[i1] = new LambdaForm.Name(paramNamedFunction1, new Object[] { arrayOfName[0] });
      arrayOfObject = Arrays.copyOfRange(arrayOfName, 0, k, [Ljava.lang.Object.class);
      arrayOfObject[0] = arrayOfName[i1];
      arrayOfName[i2] = new LambdaForm.Name(localMethodType, arrayOfObject);
    }
    LambdaForm localLambdaForm = new LambdaForm(paramString, k, arrayOfName, paramBoolean);
    if (i == 0) {
      localLambdaForm = localMethodType.form().setCachedLambdaForm(paramInt, localLambdaForm);
    }
    return localLambdaForm;
  }
  
  static
  {
    try
    {
      NF_getTarget = new LambdaForm.NamedFunction(DelegatingMethodHandle.class.getDeclaredMethod("getTarget", new Class[0]));
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
  }
}
