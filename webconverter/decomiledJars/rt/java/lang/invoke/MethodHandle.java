package java.lang.invoke;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import sun.misc.Unsafe;

public abstract class MethodHandle
{
  private final MethodType type;
  final LambdaForm form;
  MethodHandle asTypeCache;
  byte customizationCount;
  private static final long FORM_OFFSET;
  
  public MethodType type()
  {
    return this.type;
  }
  
  MethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm)
  {
    paramMethodType.getClass();
    paramLambdaForm.getClass();
    this.type = paramMethodType;
    this.form = paramLambdaForm.uncustomize();
    this.form.prepare();
  }
  
  @PolymorphicSignature
  public final native Object invokeExact(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  public final native Object invoke(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  final native Object invokeBasic(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToVirtual(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToStatic(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToSpecial(Object... paramVarArgs)
    throws Throwable;
  
  @PolymorphicSignature
  static native Object linkToInterface(Object... paramVarArgs)
    throws Throwable;
  
  public Object invokeWithArguments(Object... paramVarArgs)
    throws Throwable
  {
    MethodType localMethodType = MethodType.genericMethodType(paramVarArgs == null ? 0 : paramVarArgs.length);
    return localMethodType.invokers().spreadInvoker(0).invokeExact(asType(localMethodType), paramVarArgs);
  }
  
  public Object invokeWithArguments(List<?> paramList)
    throws Throwable
  {
    return invokeWithArguments(paramList.toArray());
  }
  
  public MethodHandle asType(MethodType paramMethodType)
  {
    if (paramMethodType == this.type) {
      return this;
    }
    MethodHandle localMethodHandle = asTypeCached(paramMethodType);
    if (localMethodHandle != null) {
      return localMethodHandle;
    }
    return asTypeUncached(paramMethodType);
  }
  
  private MethodHandle asTypeCached(MethodType paramMethodType)
  {
    MethodHandle localMethodHandle = this.asTypeCache;
    if ((localMethodHandle != null) && (paramMethodType == localMethodHandle.type)) {
      return localMethodHandle;
    }
    return null;
  }
  
  MethodHandle asTypeUncached(MethodType paramMethodType)
  {
    if (!this.type.isConvertibleTo(paramMethodType)) {
      throw new WrongMethodTypeException("cannot convert " + this + " to " + paramMethodType);
    }
    return this.asTypeCache = MethodHandleImpl.makePairwiseConvert(this, paramMethodType, true);
  }
  
  public MethodHandle asSpreader(Class<?> paramClass, int paramInt)
  {
    MethodType localMethodType1 = asSpreaderChecks(paramClass, paramInt);
    int i = type().parameterCount();
    int j = i - paramInt;
    MethodHandle localMethodHandle = asType(localMethodType1);
    BoundMethodHandle localBoundMethodHandle = localMethodHandle.rebind();
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().spreadArgumentsForm(1 + j, paramClass, paramInt);
    MethodType localMethodType2 = localMethodType1.replaceParameterTypes(j, i, new Class[] { paramClass });
    return localBoundMethodHandle.copyWith(localMethodType2, localLambdaForm);
  }
  
  private MethodType asSpreaderChecks(Class<?> paramClass, int paramInt)
  {
    spreadArrayChecks(paramClass, paramInt);
    int i = type().parameterCount();
    if ((i < paramInt) || (paramInt < 0)) {
      throw MethodHandleStatics.newIllegalArgumentException("bad spread array length");
    }
    Class localClass1 = paramClass.getComponentType();
    MethodType localMethodType1 = type();
    int j = 1;
    int k = 0;
    for (int m = i - paramInt; m < i; m++)
    {
      Class localClass2 = localMethodType1.parameterType(m);
      if (localClass2 != localClass1)
      {
        j = 0;
        if (!MethodType.canConvert(localClass1, localClass2))
        {
          k = 1;
          break;
        }
      }
    }
    if (j != 0) {
      return localMethodType1;
    }
    MethodType localMethodType2 = localMethodType1.asSpreaderType(paramClass, paramInt);
    if (k == 0) {
      return localMethodType2;
    }
    asType(localMethodType2);
    throw MethodHandleStatics.newInternalError("should not return", null);
  }
  
  private void spreadArrayChecks(Class<?> paramClass, int paramInt)
  {
    Class localClass = paramClass.getComponentType();
    if (localClass == null) {
      throw MethodHandleStatics.newIllegalArgumentException("not an array type", paramClass);
    }
    if ((paramInt & 0x7F) != paramInt)
    {
      if ((paramInt & 0xFF) != paramInt) {
        throw MethodHandleStatics.newIllegalArgumentException("array length is not legal", Integer.valueOf(paramInt));
      }
      assert (paramInt >= 128);
      if ((localClass == Long.TYPE) || (localClass == Double.TYPE)) {
        throw MethodHandleStatics.newIllegalArgumentException("array length is not legal for long[] or double[]", Integer.valueOf(paramInt));
      }
    }
  }
  
  public MethodHandle asCollector(Class<?> paramClass, int paramInt)
  {
    asCollectorChecks(paramClass, paramInt);
    int i = type().parameterCount() - 1;
    BoundMethodHandle localBoundMethodHandle = rebind();
    MethodType localMethodType = type().asCollectorType(paramClass, paramInt);
    MethodHandle localMethodHandle = MethodHandleImpl.varargsArray(paramClass, paramInt);
    LambdaForm localLambdaForm = localBoundMethodHandle.editor().collectArgumentArrayForm(1 + i, localMethodHandle);
    if (localLambdaForm != null) {
      return localBoundMethodHandle.copyWith(localMethodType, localLambdaForm);
    }
    localLambdaForm = localBoundMethodHandle.editor().collectArgumentsForm(1 + i, localMethodHandle.type().basicType());
    return localBoundMethodHandle.copyWithExtendL(localMethodType, localLambdaForm, localMethodHandle);
  }
  
  boolean asCollectorChecks(Class<?> paramClass, int paramInt)
  {
    spreadArrayChecks(paramClass, paramInt);
    int i = type().parameterCount();
    if (i != 0)
    {
      Class localClass = type().parameterType(i - 1);
      if (localClass == paramClass) {
        return true;
      }
      if (localClass.isAssignableFrom(paramClass)) {
        return false;
      }
    }
    throw MethodHandleStatics.newIllegalArgumentException("array type not assignable to trailing argument", this, paramClass);
  }
  
  public MethodHandle asVarargsCollector(Class<?> paramClass)
  {
    paramClass.getClass();
    boolean bool = asCollectorChecks(paramClass, 0);
    if ((isVarargsCollector()) && (bool)) {
      return this;
    }
    return MethodHandleImpl.makeVarargsCollector(this, paramClass);
  }
  
  public boolean isVarargsCollector()
  {
    return false;
  }
  
  public MethodHandle asFixedArity()
  {
    assert (!isVarargsCollector());
    return this;
  }
  
  public MethodHandle bindTo(Object paramObject)
  {
    paramObject = this.type.leadingReferenceParameter().cast(paramObject);
    return bindArgumentL(0, paramObject);
  }
  
  public String toString()
  {
    if (MethodHandleStatics.DEBUG_METHOD_HANDLE_NAMES) {
      return "MethodHandle" + debugString();
    }
    return standardString();
  }
  
  String standardString()
  {
    return "MethodHandle" + this.type;
  }
  
  String debugString()
  {
    return this.type + " : " + internalForm() + internalProperties();
  }
  
  BoundMethodHandle bindArgumentL(int paramInt, Object paramObject)
  {
    return rebind().bindArgumentL(paramInt, paramObject);
  }
  
  MethodHandle setVarargs(MemberName paramMemberName)
    throws IllegalAccessException
  {
    if (!paramMemberName.isVarargs()) {
      return this;
    }
    Class localClass = type().lastParameterType();
    if (localClass.isArray()) {
      return MethodHandleImpl.makeVarargsCollector(this, localClass);
    }
    throw paramMemberName.makeAccessException("cannot make variable arity", null);
  }
  
  MethodHandle viewAsType(MethodType paramMethodType, boolean paramBoolean)
  {
    assert (viewAsTypeChecks(paramMethodType, paramBoolean));
    BoundMethodHandle localBoundMethodHandle = rebind();
    assert (!(localBoundMethodHandle instanceof DirectMethodHandle));
    return localBoundMethodHandle.copyWith(paramMethodType, localBoundMethodHandle.form);
  }
  
  boolean viewAsTypeChecks(MethodType paramMethodType, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if ((!$assertionsDisabled) && (!type().isViewableAs(paramMethodType, true))) {
        throw new AssertionError(Arrays.asList(new Object[] { this, paramMethodType }));
      }
    }
    else if ((!$assertionsDisabled) && (!type().basicType().isViewableAs(paramMethodType.basicType(), true))) {
      throw new AssertionError(Arrays.asList(new Object[] { this, paramMethodType }));
    }
    return true;
  }
  
  LambdaForm internalForm()
  {
    return this.form;
  }
  
  MemberName internalMemberName()
  {
    return null;
  }
  
  Class<?> internalCallerClass()
  {
    return null;
  }
  
  MethodHandleImpl.Intrinsic intrinsicName()
  {
    return MethodHandleImpl.Intrinsic.NONE;
  }
  
  MethodHandle withInternalMemberName(MemberName paramMemberName, boolean paramBoolean)
  {
    if (paramMemberName != null) {
      return MethodHandleImpl.makeWrappedMember(this, paramMemberName, paramBoolean);
    }
    if (internalMemberName() == null) {
      return this;
    }
    BoundMethodHandle localBoundMethodHandle = rebind();
    assert (localBoundMethodHandle.internalMemberName() == null);
    return localBoundMethodHandle;
  }
  
  boolean isInvokeSpecial()
  {
    return false;
  }
  
  Object internalValues()
  {
    return null;
  }
  
  Object internalProperties()
  {
    return "";
  }
  
  abstract MethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm);
  
  abstract BoundMethodHandle rebind();
  
  void updateForm(LambdaForm paramLambdaForm)
  {
    assert ((paramLambdaForm.customized == null) || (paramLambdaForm.customized == this));
    if (this.form == paramLambdaForm) {
      return;
    }
    paramLambdaForm.prepare();
    MethodHandleStatics.UNSAFE.putObject(this, FORM_OFFSET, paramLambdaForm);
    MethodHandleStatics.UNSAFE.fullFence();
  }
  
  void customize()
  {
    if (this.form.customized == null)
    {
      LambdaForm localLambdaForm = this.form.customize(this);
      updateForm(localLambdaForm);
    }
    else
    {
      assert (this.form.customized == this);
    }
  }
  
  static
  {
    MethodHandleImpl.initStatics();
    try
    {
      FORM_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(MethodHandle.class.getDeclaredField("form"));
    }
    catch (ReflectiveOperationException localReflectiveOperationException)
    {
      throw MethodHandleStatics.newInternalError(localReflectiveOperationException);
    }
  }
  
  @Target({java.lang.annotation.ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface PolymorphicSignature {}
}
