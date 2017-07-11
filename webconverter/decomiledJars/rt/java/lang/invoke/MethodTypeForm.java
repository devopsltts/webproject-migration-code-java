package java.lang.invoke;

import java.lang.ref.SoftReference;
import sun.invoke.util.Wrapper;

final class MethodTypeForm
{
  final int[] argToSlotTable;
  final int[] slotToArgTable;
  final long argCounts;
  final long primCounts;
  final MethodType erasedType;
  final MethodType basicType;
  @Stable
  final SoftReference<MethodHandle>[] methodHandles;
  static final int MH_BASIC_INV = 0;
  static final int MH_NF_INV = 1;
  static final int MH_UNINIT_CS = 2;
  static final int MH_LIMIT = 3;
  @Stable
  final SoftReference<LambdaForm>[] lambdaForms;
  static final int LF_INVVIRTUAL = 0;
  static final int LF_INVSTATIC = 1;
  static final int LF_INVSPECIAL = 2;
  static final int LF_NEWINVSPECIAL = 3;
  static final int LF_INVINTERFACE = 4;
  static final int LF_INVSTATIC_INIT = 5;
  static final int LF_INTERPRET = 6;
  static final int LF_REBIND = 7;
  static final int LF_DELEGATE = 8;
  static final int LF_DELEGATE_BLOCK_INLINING = 9;
  static final int LF_EX_LINKER = 10;
  static final int LF_EX_INVOKER = 11;
  static final int LF_GEN_LINKER = 12;
  static final int LF_GEN_INVOKER = 13;
  static final int LF_CS_LINKER = 14;
  static final int LF_MH_LINKER = 15;
  static final int LF_GWC = 16;
  static final int LF_GWT = 17;
  static final int LF_LIMIT = 18;
  public static final int NO_CHANGE = 0;
  public static final int ERASE = 1;
  public static final int WRAP = 2;
  public static final int UNWRAP = 3;
  public static final int INTS = 4;
  public static final int LONGS = 5;
  public static final int RAW_RETURN = 6;
  
  public MethodType erasedType()
  {
    return this.erasedType;
  }
  
  public MethodType basicType()
  {
    return this.basicType;
  }
  
  private boolean assertIsBasicType()
  {
    assert (this.erasedType == this.basicType) : ("erasedType: " + this.erasedType + " != basicType: " + this.basicType);
    return true;
  }
  
  public MethodHandle cachedMethodHandle(int paramInt)
  {
    assert (assertIsBasicType());
    SoftReference localSoftReference = this.methodHandles[paramInt];
    return localSoftReference != null ? (MethodHandle)localSoftReference.get() : null;
  }
  
  public synchronized MethodHandle setCachedMethodHandle(int paramInt, MethodHandle paramMethodHandle)
  {
    SoftReference localSoftReference = this.methodHandles[paramInt];
    if (localSoftReference != null)
    {
      MethodHandle localMethodHandle = (MethodHandle)localSoftReference.get();
      if (localMethodHandle != null) {
        return localMethodHandle;
      }
    }
    this.methodHandles[paramInt] = new SoftReference(paramMethodHandle);
    return paramMethodHandle;
  }
  
  public LambdaForm cachedLambdaForm(int paramInt)
  {
    assert (assertIsBasicType());
    SoftReference localSoftReference = this.lambdaForms[paramInt];
    return localSoftReference != null ? (LambdaForm)localSoftReference.get() : null;
  }
  
  public synchronized LambdaForm setCachedLambdaForm(int paramInt, LambdaForm paramLambdaForm)
  {
    SoftReference localSoftReference = this.lambdaForms[paramInt];
    if (localSoftReference != null)
    {
      LambdaForm localLambdaForm = (LambdaForm)localSoftReference.get();
      if (localLambdaForm != null) {
        return localLambdaForm;
      }
    }
    this.lambdaForms[paramInt] = new SoftReference(paramLambdaForm);
    return paramLambdaForm;
  }
  
  protected MethodTypeForm(MethodType paramMethodType)
  {
    this.erasedType = paramMethodType;
    Class[] arrayOfClass1 = paramMethodType.ptypes();
    MethodTypeForm localMethodTypeForm1 = arrayOfClass1.length;
    int i = localMethodTypeForm1;
    int j = 1;
    int k = 1;
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    Class[] arrayOfClass2 = arrayOfClass1;
    Class[] arrayOfClass3 = arrayOfClass2;
    Object localObject;
    for (int i3 = 0; i3 < arrayOfClass2.length; i3++)
    {
      localClass2 = arrayOfClass2[i3];
      if (localClass2 != Object.class)
      {
        m++;
        localObject = Wrapper.forPrimitiveType(localClass2);
        if (((Wrapper)localObject).isDoubleWord()) {
          n++;
        }
        if ((((Wrapper)localObject).isSubwordOrInt()) && (localClass2 != Integer.TYPE))
        {
          if (arrayOfClass3 == arrayOfClass2) {
            arrayOfClass3 = (Class[])arrayOfClass3.clone();
          }
          arrayOfClass3[i3] = Integer.TYPE;
        }
      }
    }
    i += n;
    Class localClass1 = paramMethodType.returnType();
    Class localClass2 = localClass1;
    if (localClass1 != Object.class)
    {
      i1++;
      localObject = Wrapper.forPrimitiveType(localClass1);
      if (((Wrapper)localObject).isDoubleWord()) {
        i2++;
      }
      if ((((Wrapper)localObject).isSubwordOrInt()) && (localClass1 != Integer.TYPE)) {
        localClass2 = Integer.TYPE;
      }
      if (localClass1 == Void.TYPE) {
        j = k = 0;
      } else {
        k += i2;
      }
    }
    if ((arrayOfClass2 == arrayOfClass3) && (localClass2 == localClass1))
    {
      this.basicType = paramMethodType;
    }
    else
    {
      this.basicType = MethodType.makeImpl(localClass2, arrayOfClass3, true);
      localObject = this.basicType.form();
      assert (this != localObject);
      this.primCounts = ((MethodTypeForm)localObject).primCounts;
      this.argCounts = ((MethodTypeForm)localObject).argCounts;
      this.argToSlotTable = ((MethodTypeForm)localObject).argToSlotTable;
      this.slotToArgTable = ((MethodTypeForm)localObject).slotToArgTable;
      this.methodHandles = null;
      this.lambdaForms = null;
      return;
    }
    int i5;
    if (n != 0)
    {
      int i4 = localMethodTypeForm1 + n;
      arrayOfInt2 = new int[i4 + 1];
      arrayOfInt1 = new int[1 + localMethodTypeForm1];
      arrayOfInt1[0] = i4;
      for (i5 = 0; i5 < arrayOfClass2.length; i5++)
      {
        Class localClass3 = arrayOfClass2[i5];
        Wrapper localWrapper = Wrapper.forBasicType(localClass3);
        if (localWrapper.isDoubleWord()) {
          i4--;
        }
        i4--;
        arrayOfInt2[i4] = (i5 + 1);
        arrayOfInt1[(1 + i5)] = i4;
      }
      assert (i4 == 0);
    }
    else
    {
      MethodTypeForm localMethodTypeForm2;
      if (m != 0)
      {
        assert (localMethodTypeForm1 == i);
        localMethodTypeForm2 = MethodType.genericMethodType(localMethodTypeForm1).form();
        assert (this != localMethodTypeForm2);
        arrayOfInt2 = localMethodTypeForm2.slotToArgTable;
        arrayOfInt1 = localMethodTypeForm2.argToSlotTable;
      }
      else
      {
        localMethodTypeForm2 = localMethodTypeForm1;
        arrayOfInt2 = new int[localMethodTypeForm2 + 1];
        arrayOfInt1 = new int[1 + localMethodTypeForm1];
        arrayOfInt1[0] = localMethodTypeForm2;
        for (i5 = 0; i5 < localMethodTypeForm1; i5++)
        {
          localMethodTypeForm2--;
          arrayOfInt2[localMethodTypeForm2] = (i5 + 1);
          arrayOfInt1[(1 + i5)] = localMethodTypeForm2;
        }
      }
    }
    this.primCounts = pack(i2, i1, n, m);
    this.argCounts = pack(k, j, i, localMethodTypeForm1);
    this.argToSlotTable = arrayOfInt1;
    this.slotToArgTable = arrayOfInt2;
    if (i >= 256) {
      throw MethodHandleStatics.newIllegalArgumentException("too many arguments");
    }
    assert (this.basicType == paramMethodType);
    this.lambdaForms = new SoftReference[18];
    this.methodHandles = new SoftReference[3];
  }
  
  private static long pack(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    assert (((paramInt1 | paramInt2 | paramInt3 | paramInt4) & 0xFFFF0000) == 0);
    long l1 = paramInt1 << 16 | paramInt2;
    long l2 = paramInt3 << 16 | paramInt4;
    return l1 << 32 | l2;
  }
  
  private static char unpack(long paramLong, int paramInt)
  {
    assert (paramInt <= 3);
    return (char)(int)(paramLong >> (3 - paramInt) * 16);
  }
  
  public int parameterCount()
  {
    return unpack(this.argCounts, 3);
  }
  
  public int parameterSlotCount()
  {
    return unpack(this.argCounts, 2);
  }
  
  public int returnCount()
  {
    return unpack(this.argCounts, 1);
  }
  
  public int returnSlotCount()
  {
    return unpack(this.argCounts, 0);
  }
  
  public int primitiveParameterCount()
  {
    return unpack(this.primCounts, 3);
  }
  
  public int longPrimitiveParameterCount()
  {
    return unpack(this.primCounts, 2);
  }
  
  public int primitiveReturnCount()
  {
    return unpack(this.primCounts, 1);
  }
  
  public int longPrimitiveReturnCount()
  {
    return unpack(this.primCounts, 0);
  }
  
  public boolean hasPrimitives()
  {
    return this.primCounts != 0L;
  }
  
  public boolean hasNonVoidPrimitives()
  {
    if (this.primCounts == 0L) {
      return false;
    }
    if (primitiveParameterCount() != 0) {
      return true;
    }
    return (primitiveReturnCount() != 0) && (returnCount() != 0);
  }
  
  public boolean hasLongPrimitives()
  {
    return (longPrimitiveParameterCount() | longPrimitiveReturnCount()) != 0;
  }
  
  public int parameterToArgSlot(int paramInt)
  {
    return this.argToSlotTable[(1 + paramInt)];
  }
  
  public int argSlotToParameter(int paramInt)
  {
    return this.slotToArgTable[paramInt] - 1;
  }
  
  static MethodTypeForm findForm(MethodType paramMethodType)
  {
    MethodType localMethodType = canonicalize(paramMethodType, 1, 1);
    if (localMethodType == null) {
      return new MethodTypeForm(paramMethodType);
    }
    return localMethodType.form();
  }
  
  public static MethodType canonicalize(MethodType paramMethodType, int paramInt1, int paramInt2)
  {
    Class[] arrayOfClass1 = paramMethodType.ptypes();
    Class[] arrayOfClass2 = canonicalizeAll(arrayOfClass1, paramInt2);
    Class localClass1 = paramMethodType.returnType();
    Class localClass2 = canonicalize(localClass1, paramInt1);
    if ((arrayOfClass2 == null) && (localClass2 == null)) {
      return null;
    }
    if (localClass2 == null) {
      localClass2 = localClass1;
    }
    if (arrayOfClass2 == null) {
      arrayOfClass2 = arrayOfClass1;
    }
    return MethodType.makeImpl(localClass2, arrayOfClass2, true);
  }
  
  static Class<?> canonicalize(Class<?> paramClass, int paramInt)
  {
    if (paramClass != Object.class) {
      if (!paramClass.isPrimitive()) {
        switch (paramInt)
        {
        case 3: 
          Class localClass = Wrapper.asPrimitiveType(paramClass);
          if (localClass != paramClass) {
            return localClass;
          }
          break;
        case 1: 
        case 6: 
          return Object.class;
        }
      } else if (paramClass == Void.TYPE) {
        switch (paramInt)
        {
        case 6: 
          return Integer.TYPE;
        case 2: 
          return Void.class;
        }
      } else {
        switch (paramInt)
        {
        case 2: 
          return Wrapper.asWrapperType(paramClass);
        case 4: 
          if ((paramClass == Integer.TYPE) || (paramClass == Long.TYPE)) {
            return null;
          }
          if (paramClass == Double.TYPE) {
            return Long.TYPE;
          }
          return Integer.TYPE;
        case 5: 
          if (paramClass == Long.TYPE) {
            return null;
          }
          return Long.TYPE;
        case 6: 
          if ((paramClass == Integer.TYPE) || (paramClass == Long.TYPE) || (paramClass == Float.TYPE) || (paramClass == Double.TYPE)) {
            return null;
          }
          return Integer.TYPE;
        }
      }
    }
    return null;
  }
  
  static Class<?>[] canonicalizeAll(Class<?>[] paramArrayOfClass, int paramInt)
  {
    Class[] arrayOfClass = null;
    int i = paramArrayOfClass.length;
    for (int j = 0; j < i; j++)
    {
      Class localClass = canonicalize(paramArrayOfClass[j], paramInt);
      if (localClass == Void.TYPE) {
        localClass = null;
      }
      if (localClass != null)
      {
        if (arrayOfClass == null) {
          arrayOfClass = (Class[])paramArrayOfClass.clone();
        }
        arrayOfClass[j] = localClass;
      }
    }
    return arrayOfClass;
  }
  
  public String toString()
  {
    return "Form" + this.erasedType;
  }
}
