package jdk.internal.org.objectweb.asm;

import java.io.IOException;
import java.io.InputStream;

public class ClassReader
{
  static final boolean SIGNATURES = true;
  static final boolean ANNOTATIONS = true;
  static final boolean FRAMES = true;
  static final boolean WRITER = true;
  static final boolean RESIZE = true;
  public static final int SKIP_CODE = 1;
  public static final int SKIP_DEBUG = 2;
  public static final int SKIP_FRAMES = 4;
  public static final int EXPAND_FRAMES = 8;
  public final byte[] b;
  private final int[] items;
  private final String[] strings;
  private final int maxStringLength;
  public final int header;
  
  public ClassReader(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public ClassReader(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.b = paramArrayOfByte;
    if (readShort(paramInt1 + 6) > 52) {
      throw new IllegalArgumentException();
    }
    this.items = new int[readUnsignedShort(paramInt1 + 8)];
    int i = this.items.length;
    this.strings = new String[i];
    int j = 0;
    int k = paramInt1 + 10;
    for (int m = 1; m < i; m++)
    {
      this.items[m] = (k + 1);
      int n;
      switch (paramArrayOfByte[k])
      {
      case 3: 
      case 4: 
      case 9: 
      case 10: 
      case 11: 
      case 12: 
      case 18: 
        n = 5;
        break;
      case 5: 
      case 6: 
        n = 9;
        m++;
        break;
      case 1: 
        n = 3 + readUnsignedShort(k + 1);
        if (n > j) {
          j = n;
        }
        break;
      case 15: 
        n = 4;
        break;
      case 2: 
      case 7: 
      case 8: 
      case 13: 
      case 14: 
      case 16: 
      case 17: 
      default: 
        n = 3;
      }
      k += n;
    }
    this.maxStringLength = j;
    this.header = k;
  }
  
  public int getAccess()
  {
    return readUnsignedShort(this.header);
  }
  
  public String getClassName()
  {
    return readClass(this.header + 2, new char[this.maxStringLength]);
  }
  
  public String getSuperName()
  {
    return readClass(this.header + 4, new char[this.maxStringLength]);
  }
  
  public String[] getInterfaces()
  {
    int i = this.header + 6;
    int j = readUnsignedShort(i);
    String[] arrayOfString = new String[j];
    if (j > 0)
    {
      char[] arrayOfChar = new char[this.maxStringLength];
      for (int k = 0; k < j; k++)
      {
        i += 2;
        arrayOfString[k] = readClass(i, arrayOfChar);
      }
    }
    return arrayOfString;
  }
  
  void copyPool(ClassWriter paramClassWriter)
  {
    char[] arrayOfChar = new char[this.maxStringLength];
    int i = this.items.length;
    Item[] arrayOfItem = new Item[i];
    for (int j = 1; j < i; j++)
    {
      int k = this.items[j];
      int m = this.b[(k - 1)];
      Item localItem = new Item(j);
      int n;
      switch (m)
      {
      case 9: 
      case 10: 
      case 11: 
        n = this.items[readUnsignedShort(k + 2)];
        localItem.set(m, readClass(k, arrayOfChar), readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar));
        break;
      case 3: 
        localItem.set(readInt(k));
        break;
      case 4: 
        localItem.set(Float.intBitsToFloat(readInt(k)));
        break;
      case 12: 
        localItem.set(m, readUTF8(k, arrayOfChar), readUTF8(k + 2, arrayOfChar), null);
        break;
      case 5: 
        localItem.set(readLong(k));
        j++;
        break;
      case 6: 
        localItem.set(Double.longBitsToDouble(readLong(k)));
        j++;
        break;
      case 1: 
        String str = this.strings[j];
        if (str == null)
        {
          k = this.items[j];
          str = this.strings[j] =  = readUTF(k + 2, readUnsignedShort(k), arrayOfChar);
        }
        localItem.set(m, str, null, null);
        break;
      case 15: 
        i1 = this.items[readUnsignedShort(k + 1)];
        n = this.items[readUnsignedShort(i1 + 2)];
        localItem.set(20 + readByte(k), readClass(i1, arrayOfChar), readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar));
        break;
      case 18: 
        if (paramClassWriter.bootstrapMethods == null) {
          copyBootstrapMethods(paramClassWriter, arrayOfItem, arrayOfChar);
        }
        n = this.items[readUnsignedShort(k + 2)];
        localItem.set(readUTF8(n, arrayOfChar), readUTF8(n + 2, arrayOfChar), readUnsignedShort(k));
        break;
      case 2: 
      case 7: 
      case 8: 
      case 13: 
      case 14: 
      case 16: 
      case 17: 
      default: 
        localItem.set(m, readUTF8(k, arrayOfChar), null, null);
      }
      int i1 = localItem.hashCode % arrayOfItem.length;
      localItem.next = arrayOfItem[i1];
      arrayOfItem[i1] = localItem;
    }
    j = this.items[1] - 1;
    paramClassWriter.pool.putByteArray(this.b, j, this.header - j);
    paramClassWriter.items = arrayOfItem;
    paramClassWriter.threshold = ((int)(0.75D * i));
    paramClassWriter.index = i;
  }
  
  private void copyBootstrapMethods(ClassWriter paramClassWriter, Item[] paramArrayOfItem, char[] paramArrayOfChar)
  {
    int i = getAttributes();
    int j = 0;
    for (int k = readUnsignedShort(i); k > 0; k--)
    {
      String str = readUTF8(i + 2, paramArrayOfChar);
      if ("BootstrapMethods".equals(str))
      {
        j = 1;
        break;
      }
      i += 6 + readInt(i + 4);
    }
    if (j == 0) {
      return;
    }
    k = readUnsignedShort(i + 8);
    int m = 0;
    int n = i + 10;
    while (m < k)
    {
      int i1 = n - i - 10;
      int i2 = readConst(readUnsignedShort(n), paramArrayOfChar).hashCode();
      for (int i3 = readUnsignedShort(n + 2); i3 > 0; i3--)
      {
        i2 ^= readConst(readUnsignedShort(n + 4), paramArrayOfChar).hashCode();
        n += 2;
      }
      n += 4;
      Item localItem = new Item(m);
      localItem.set(i1, i2 & 0x7FFFFFFF);
      int i4 = localItem.hashCode % paramArrayOfItem.length;
      localItem.next = paramArrayOfItem[i4];
      paramArrayOfItem[i4] = localItem;
      m++;
    }
    m = readInt(i + 4);
    ByteVector localByteVector = new ByteVector(m + 62);
    localByteVector.putByteArray(this.b, i + 10, m - 2);
    paramClassWriter.bootstrapMethodsCount = k;
    paramClassWriter.bootstrapMethods = localByteVector;
  }
  
  public ClassReader(InputStream paramInputStream)
    throws IOException
  {
    this(readClass(paramInputStream, false));
  }
  
  public ClassReader(String paramString)
    throws IOException
  {
    this(readClass(ClassLoader.getSystemResourceAsStream(paramString.replace('.', '/') + ".class"), true));
  }
  
  /* Error */
  private static byte[] readClass(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: new 308	java/io/IOException
    //   7: dup
    //   8: ldc 11
    //   10: invokespecial 629	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: invokevirtual 630	java/io/InputStream:available	()I
    //   18: newarray byte
    //   20: astore_2
    //   21: iconst_0
    //   22: istore_3
    //   23: aload_0
    //   24: aload_2
    //   25: iload_3
    //   26: aload_2
    //   27: arraylength
    //   28: iload_3
    //   29: isub
    //   30: invokevirtual 633	java/io/InputStream:read	([BII)I
    //   33: istore 4
    //   35: iload 4
    //   37: iconst_m1
    //   38: if_icmpne +40 -> 78
    //   41: iload_3
    //   42: aload_2
    //   43: arraylength
    //   44: if_icmpge +20 -> 64
    //   47: iload_3
    //   48: newarray byte
    //   50: astore 5
    //   52: aload_2
    //   53: iconst_0
    //   54: aload 5
    //   56: iconst_0
    //   57: iload_3
    //   58: invokestatic 655	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   61: aload 5
    //   63: astore_2
    //   64: aload_2
    //   65: astore 5
    //   67: iload_1
    //   68: ifeq +7 -> 75
    //   71: aload_0
    //   72: invokevirtual 632	java/io/InputStream:close	()V
    //   75: aload 5
    //   77: areturn
    //   78: iload_3
    //   79: iload 4
    //   81: iadd
    //   82: istore_3
    //   83: iload_3
    //   84: aload_2
    //   85: arraylength
    //   86: if_icmpne +60 -> 146
    //   89: aload_0
    //   90: invokevirtual 631	java/io/InputStream:read	()I
    //   93: istore 5
    //   95: iload 5
    //   97: ifge +17 -> 114
    //   100: aload_2
    //   101: astore 6
    //   103: iload_1
    //   104: ifeq +7 -> 111
    //   107: aload_0
    //   108: invokevirtual 632	java/io/InputStream:close	()V
    //   111: aload 6
    //   113: areturn
    //   114: aload_2
    //   115: arraylength
    //   116: sipush 1000
    //   119: iadd
    //   120: newarray byte
    //   122: astore 6
    //   124: aload_2
    //   125: iconst_0
    //   126: aload 6
    //   128: iconst_0
    //   129: iload_3
    //   130: invokestatic 655	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   133: aload 6
    //   135: iload_3
    //   136: iinc 3 1
    //   139: iload 5
    //   141: i2b
    //   142: bastore
    //   143: aload 6
    //   145: astore_2
    //   146: goto -123 -> 23
    //   149: astore 7
    //   151: iload_1
    //   152: ifeq +7 -> 159
    //   155: aload_0
    //   156: invokevirtual 632	java/io/InputStream:close	()V
    //   159: aload 7
    //   161: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	162	0	paramInputStream	InputStream
    //   0	162	1	paramBoolean	boolean
    //   20	126	2	localObject1	Object
    //   22	114	3	i	int
    //   33	49	4	j	int
    //   50	26	5	localObject2	Object
    //   93	47	5	k	int
    //   101	43	6	localObject3	Object
    //   149	11	7	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   14	67	149	finally
    //   78	103	149	finally
    //   114	151	149	finally
  }
  
  public void accept(ClassVisitor paramClassVisitor, int paramInt)
  {
    accept(paramClassVisitor, new Attribute[0], paramInt);
  }
  
  public void accept(ClassVisitor paramClassVisitor, Attribute[] paramArrayOfAttribute, int paramInt)
  {
    int i = this.header;
    char[] arrayOfChar = new char[this.maxStringLength];
    Context localContext = new Context();
    localContext.attrs = paramArrayOfAttribute;
    localContext.flags = paramInt;
    localContext.buffer = arrayOfChar;
    int j = readUnsignedShort(i);
    String str1 = readClass(i + 2, arrayOfChar);
    String str2 = readClass(i + 4, arrayOfChar);
    String[] arrayOfString = new String[readUnsignedShort(i + 6)];
    i += 8;
    for (int k = 0; k < arrayOfString.length; k++)
    {
      arrayOfString[k] = readClass(i, arrayOfChar);
      i += 2;
    }
    String str3 = null;
    String str4 = null;
    String str5 = null;
    String str6 = null;
    String str7 = null;
    String str8 = null;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    Object localObject1 = null;
    i = getAttributes();
    for (int i4 = readUnsignedShort(i); i4 > 0; i4--)
    {
      String str9 = readUTF8(i + 2, arrayOfChar);
      if ("SourceFile".equals(str9))
      {
        str4 = readUTF8(i + 8, arrayOfChar);
      }
      else if ("InnerClasses".equals(str9))
      {
        i3 = i + 8;
      }
      else
      {
        int i7;
        if ("EnclosingMethod".equals(str9))
        {
          str6 = readClass(i + 8, arrayOfChar);
          i7 = readUnsignedShort(i + 10);
          if (i7 != 0)
          {
            str7 = readUTF8(this.items[i7], arrayOfChar);
            str8 = readUTF8(this.items[i7] + 2, arrayOfChar);
          }
        }
        else if ("Signature".equals(str9))
        {
          str3 = readUTF8(i + 8, arrayOfChar);
        }
        else if ("RuntimeVisibleAnnotations".equals(str9))
        {
          m = i + 8;
        }
        else if ("RuntimeVisibleTypeAnnotations".equals(str9))
        {
          i1 = i + 8;
        }
        else if ("Deprecated".equals(str9))
        {
          j |= 0x20000;
        }
        else if ("Synthetic".equals(str9))
        {
          j |= 0x41000;
        }
        else if ("SourceDebugExtension".equals(str9))
        {
          i7 = readInt(i + 4);
          str5 = readUTF(i + 8, i7, new char[i7]);
        }
        else if ("RuntimeInvisibleAnnotations".equals(str9))
        {
          n = i + 8;
        }
        else if ("RuntimeInvisibleTypeAnnotations".equals(str9))
        {
          i2 = i + 8;
        }
        else
        {
          Object localObject2;
          if ("BootstrapMethods".equals(str9))
          {
            localObject2 = new int[readUnsignedShort(i + 8)];
            int i8 = 0;
            int i9 = i + 10;
            while (i8 < localObject2.length)
            {
              localObject2[i8] = i9;
              i9 += (2 + readUnsignedShort(i9 + 2) << 1);
              i8++;
            }
            localContext.bootstrapMethods = ((int[])localObject2);
          }
          else
          {
            localObject2 = readAttribute(paramArrayOfAttribute, str9, i + 8, readInt(i + 4), arrayOfChar, -1, null);
            if (localObject2 != null)
            {
              ((Attribute)localObject2).next = localObject1;
              localObject1 = localObject2;
            }
          }
        }
      }
      i += 6 + readInt(i + 4);
    }
    paramClassVisitor.visit(readInt(this.items[1] - 7), j, str1, str3, str2, arrayOfString);
    if (((paramInt & 0x2) == 0) && ((str4 != null) || (str5 != null))) {
      paramClassVisitor.visitSource(str4, str5);
    }
    if (str6 != null) {
      paramClassVisitor.visitOuterClass(str6, str7, str8);
    }
    int i6;
    if (m != 0)
    {
      i4 = readUnsignedShort(m);
      i6 = m + 2;
      while (i4 > 0)
      {
        i6 = readAnnotationValues(i6 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i6, arrayOfChar), true));
        i4--;
      }
    }
    if (n != 0)
    {
      i4 = readUnsignedShort(n);
      i6 = n + 2;
      while (i4 > 0)
      {
        i6 = readAnnotationValues(i6 + 2, arrayOfChar, true, paramClassVisitor.visitAnnotation(readUTF8(i6, arrayOfChar), false));
        i4--;
      }
    }
    if (i1 != 0)
    {
      i4 = readUnsignedShort(i1);
      i6 = i1 + 2;
      while (i4 > 0)
      {
        i6 = readAnnotationTarget(localContext, i6);
        i6 = readAnnotationValues(i6 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(localContext.typeRef, localContext.typePath, readUTF8(i6, arrayOfChar), true));
        i4--;
      }
    }
    if (i2 != 0)
    {
      i4 = readUnsignedShort(i2);
      i6 = i2 + 2;
      while (i4 > 0)
      {
        i6 = readAnnotationTarget(localContext, i6);
        i6 = readAnnotationValues(i6 + 2, arrayOfChar, true, paramClassVisitor.visitTypeAnnotation(localContext.typeRef, localContext.typePath, readUTF8(i6, arrayOfChar), false));
        i4--;
      }
    }
    while (localObject1 != null)
    {
      Attribute localAttribute = localObject1.next;
      localObject1.next = null;
      paramClassVisitor.visitAttribute(localObject1);
      localObject1 = localAttribute;
    }
    if (i3 != 0)
    {
      i5 = i3 + 2;
      for (i6 = readUnsignedShort(i3); i6 > 0; i6--)
      {
        paramClassVisitor.visitInnerClass(readClass(i5, arrayOfChar), readClass(i5 + 2, arrayOfChar), readUTF8(i5 + 4, arrayOfChar), readUnsignedShort(i5 + 6));
        i5 += 8;
      }
    }
    i = this.header + 10 + 2 * arrayOfString.length;
    for (int i5 = readUnsignedShort(i - 2); i5 > 0; i5--) {
      i = readField(paramClassVisitor, localContext, i);
    }
    i += 2;
    for (i5 = readUnsignedShort(i - 2); i5 > 0; i5--) {
      i = readMethod(paramClassVisitor, localContext, i);
    }
    paramClassVisitor.visitEnd();
  }
  
  private int readField(ClassVisitor paramClassVisitor, Context paramContext, int paramInt)
  {
    char[] arrayOfChar = paramContext.buffer;
    int i = readUnsignedShort(paramInt);
    String str1 = readUTF8(paramInt + 2, arrayOfChar);
    String str2 = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    String str3 = null;
    int j = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--)
    {
      String str4 = readUTF8(paramInt + 2, arrayOfChar);
      if ("ConstantValue".equals(str4))
      {
        int i3 = readUnsignedShort(paramInt + 8);
        localObject1 = i3 == 0 ? null : readConst(i3, arrayOfChar);
      }
      else if ("Signature".equals(str4))
      {
        str3 = readUTF8(paramInt + 8, arrayOfChar);
      }
      else if ("Deprecated".equals(str4))
      {
        i |= 0x20000;
      }
      else if ("Synthetic".equals(str4))
      {
        i |= 0x41000;
      }
      else if ("RuntimeVisibleAnnotations".equals(str4))
      {
        j = paramInt + 8;
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(str4))
      {
        m = paramInt + 8;
      }
      else if ("RuntimeInvisibleAnnotations".equals(str4))
      {
        k = paramInt + 8;
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(str4))
      {
        n = paramInt + 8;
      }
      else
      {
        Attribute localAttribute2 = readAttribute(paramContext.attrs, str4, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (localAttribute2 != null)
        {
          localAttribute2.next = localObject2;
          localObject2 = localAttribute2;
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    FieldVisitor localFieldVisitor = paramClassVisitor.visitField(i, str1, str2, str3, localObject1);
    if (localFieldVisitor == null) {
      return paramInt;
    }
    int i2;
    int i4;
    if (j != 0)
    {
      i2 = readUnsignedShort(j);
      i4 = j + 2;
      while (i2 > 0)
      {
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, localFieldVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), true));
        i2--;
      }
    }
    if (k != 0)
    {
      i2 = readUnsignedShort(k);
      i4 = k + 2;
      while (i2 > 0)
      {
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, localFieldVisitor.visitAnnotation(readUTF8(i4, arrayOfChar), false));
        i2--;
      }
    }
    if (m != 0)
    {
      i2 = readUnsignedShort(m);
      i4 = m + 2;
      while (i2 > 0)
      {
        i4 = readAnnotationTarget(paramContext, i4);
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, localFieldVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i4, arrayOfChar), true));
        i2--;
      }
    }
    if (n != 0)
    {
      i2 = readUnsignedShort(n);
      i4 = n + 2;
      while (i2 > 0)
      {
        i4 = readAnnotationTarget(paramContext, i4);
        i4 = readAnnotationValues(i4 + 2, arrayOfChar, true, localFieldVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i4, arrayOfChar), false));
        i2--;
      }
    }
    while (localObject2 != null)
    {
      Attribute localAttribute1 = localObject2.next;
      localObject2.next = null;
      localFieldVisitor.visitAttribute(localObject2);
      localObject2 = localAttribute1;
    }
    localFieldVisitor.visitEnd();
    return paramInt;
  }
  
  private int readMethod(ClassVisitor paramClassVisitor, Context paramContext, int paramInt)
  {
    char[] arrayOfChar = paramContext.buffer;
    paramContext.access = readUnsignedShort(paramInt);
    paramContext.name = readUTF8(paramInt + 2, arrayOfChar);
    paramContext.desc = readUTF8(paramInt + 4, arrayOfChar);
    paramInt += 6;
    int i = 0;
    int j = 0;
    String[] arrayOfString = null;
    String str = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    int i5 = 0;
    int i6 = paramInt;
    Object localObject1 = null;
    Object localObject2;
    for (int i7 = readUnsignedShort(paramInt); i7 > 0; i7--)
    {
      localObject2 = readUTF8(paramInt + 2, arrayOfChar);
      if ("Code".equals(localObject2))
      {
        if ((paramContext.flags & 0x1) == 0) {
          i = paramInt + 8;
        }
      }
      else if ("Exceptions".equals(localObject2))
      {
        arrayOfString = new String[readUnsignedShort(paramInt + 8)];
        j = paramInt + 10;
        for (int i10 = 0; i10 < arrayOfString.length; i10++)
        {
          arrayOfString[i10] = readClass(j, arrayOfChar);
          j += 2;
        }
      }
      else if ("Signature".equals(localObject2))
      {
        str = readUTF8(paramInt + 8, arrayOfChar);
      }
      else if ("Deprecated".equals(localObject2))
      {
        paramContext.access |= 0x20000;
      }
      else if ("RuntimeVisibleAnnotations".equals(localObject2))
      {
        m = paramInt + 8;
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(localObject2))
      {
        i1 = paramInt + 8;
      }
      else if ("AnnotationDefault".equals(localObject2))
      {
        i3 = paramInt + 8;
      }
      else if ("Synthetic".equals(localObject2))
      {
        paramContext.access |= 0x41000;
      }
      else if ("RuntimeInvisibleAnnotations".equals(localObject2))
      {
        n = paramInt + 8;
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(localObject2))
      {
        i2 = paramInt + 8;
      }
      else if ("RuntimeVisibleParameterAnnotations".equals(localObject2))
      {
        i4 = paramInt + 8;
      }
      else if ("RuntimeInvisibleParameterAnnotations".equals(localObject2))
      {
        i5 = paramInt + 8;
      }
      else if ("MethodParameters".equals(localObject2))
      {
        k = paramInt + 8;
      }
      else
      {
        Attribute localAttribute2 = readAttribute(paramContext.attrs, (String)localObject2, paramInt + 8, readInt(paramInt + 4), arrayOfChar, -1, null);
        if (localAttribute2 != null)
        {
          localAttribute2.next = localObject1;
          localObject1 = localAttribute2;
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    MethodVisitor localMethodVisitor = paramClassVisitor.visitMethod(paramContext.access, paramContext.name, paramContext.desc, str, arrayOfString);
    if (localMethodVisitor == null) {
      return paramInt;
    }
    int i11;
    if ((localMethodVisitor instanceof MethodWriter))
    {
      localObject2 = (MethodWriter)localMethodVisitor;
      if ((((MethodWriter)localObject2).cw.cr == this) && (str == ((MethodWriter)localObject2).signature))
      {
        i11 = 0;
        if (arrayOfString == null)
        {
          i11 = ((MethodWriter)localObject2).exceptionCount == 0 ? 1 : 0;
        }
        else if (arrayOfString.length == ((MethodWriter)localObject2).exceptionCount)
        {
          i11 = 1;
          for (int i12 = arrayOfString.length - 1; i12 >= 0; i12--)
          {
            j -= 2;
            if (localObject2.exceptions[i12] != readUnsignedShort(j))
            {
              i11 = 0;
              break;
            }
          }
        }
        if (i11 != 0)
        {
          ((MethodWriter)localObject2).classReaderOffset = i6;
          ((MethodWriter)localObject2).classReaderLength = (paramInt - i6);
          return paramInt;
        }
      }
    }
    if (k != 0)
    {
      int i8 = this.b[k] & 0xFF;
      i11 = k + 1;
      while (i8 > 0)
      {
        localMethodVisitor.visitParameter(readUTF8(i11, arrayOfChar), readUnsignedShort(i11 + 2));
        i8--;
        i11 += 4;
      }
    }
    if (i3 != 0)
    {
      AnnotationVisitor localAnnotationVisitor = localMethodVisitor.visitAnnotationDefault();
      readAnnotationValue(i3, arrayOfChar, null, localAnnotationVisitor);
      if (localAnnotationVisitor != null) {
        localAnnotationVisitor.visitEnd();
      }
    }
    int i9;
    if (m != 0)
    {
      i9 = readUnsignedShort(m);
      i11 = m + 2;
      while (i9 > 0)
      {
        i11 = readAnnotationValues(i11 + 2, arrayOfChar, true, localMethodVisitor.visitAnnotation(readUTF8(i11, arrayOfChar), true));
        i9--;
      }
    }
    if (n != 0)
    {
      i9 = readUnsignedShort(n);
      i11 = n + 2;
      while (i9 > 0)
      {
        i11 = readAnnotationValues(i11 + 2, arrayOfChar, true, localMethodVisitor.visitAnnotation(readUTF8(i11, arrayOfChar), false));
        i9--;
      }
    }
    if (i1 != 0)
    {
      i9 = readUnsignedShort(i1);
      i11 = i1 + 2;
      while (i9 > 0)
      {
        i11 = readAnnotationTarget(paramContext, i11);
        i11 = readAnnotationValues(i11 + 2, arrayOfChar, true, localMethodVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i11, arrayOfChar), true));
        i9--;
      }
    }
    if (i2 != 0)
    {
      i9 = readUnsignedShort(i2);
      i11 = i2 + 2;
      while (i9 > 0)
      {
        i11 = readAnnotationTarget(paramContext, i11);
        i11 = readAnnotationValues(i11 + 2, arrayOfChar, true, localMethodVisitor.visitTypeAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i11, arrayOfChar), false));
        i9--;
      }
    }
    if (i4 != 0) {
      readParameterAnnotations(localMethodVisitor, paramContext, i4, true);
    }
    if (i5 != 0) {
      readParameterAnnotations(localMethodVisitor, paramContext, i5, false);
    }
    while (localObject1 != null)
    {
      Attribute localAttribute1 = localObject1.next;
      localObject1.next = null;
      localMethodVisitor.visitAttribute(localObject1);
      localObject1 = localAttribute1;
    }
    if (i != 0)
    {
      localMethodVisitor.visitCode();
      readCode(localMethodVisitor, paramContext, i);
    }
    localMethodVisitor.visitEnd();
    return paramInt;
  }
  
  private void readCode(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt)
  {
    byte[] arrayOfByte = this.b;
    char[] arrayOfChar = paramContext.buffer;
    int i = readUnsignedShort(paramInt);
    int j = readUnsignedShort(paramInt + 2);
    int k = readInt(paramInt + 4);
    paramInt += 8;
    int m = paramInt;
    int n = paramInt + k;
    Label[] arrayOfLabel = paramContext.labels = new Label[k + 2];
    readLabel(k + 1, arrayOfLabel);
    while (paramInt < n)
    {
      i1 = paramInt - m;
      int i2 = arrayOfByte[paramInt] & 0xFF;
      int i3;
      switch (ClassWriter.TYPE[i2])
      {
      case 0: 
      case 4: 
        paramInt++;
        break;
      case 9: 
        readLabel(i1 + readShort(paramInt + 1), arrayOfLabel);
        paramInt += 3;
        break;
      case 10: 
        readLabel(i1 + readInt(paramInt + 1), arrayOfLabel);
        paramInt += 5;
        break;
      case 17: 
        i2 = arrayOfByte[(paramInt + 1)] & 0xFF;
        if (i2 == 132) {
          paramInt += 6;
        } else {
          paramInt += 4;
        }
        break;
      case 14: 
        paramInt = paramInt + 4 - (i1 & 0x3);
        readLabel(i1 + readInt(paramInt), arrayOfLabel);
        for (i3 = readInt(paramInt + 8) - readInt(paramInt + 4) + 1; i3 > 0; i3--)
        {
          readLabel(i1 + readInt(paramInt + 12), arrayOfLabel);
          paramInt += 4;
        }
        paramInt += 12;
        break;
      case 15: 
        paramInt = paramInt + 4 - (i1 & 0x3);
        readLabel(i1 + readInt(paramInt), arrayOfLabel);
        for (i3 = readInt(paramInt + 4); i3 > 0; i3--)
        {
          readLabel(i1 + readInt(paramInt + 12), arrayOfLabel);
          paramInt += 8;
        }
        paramInt += 8;
        break;
      case 1: 
      case 3: 
      case 11: 
        paramInt += 2;
        break;
      case 2: 
      case 5: 
      case 6: 
      case 12: 
      case 13: 
        paramInt += 3;
        break;
      case 7: 
      case 8: 
        paramInt += 5;
        break;
      case 16: 
      default: 
        paramInt += 4;
      }
    }
    for (int i1 = readUnsignedShort(paramInt); i1 > 0; i1--)
    {
      localObject1 = readLabel(readUnsignedShort(paramInt + 2), arrayOfLabel);
      Label localLabel1 = readLabel(readUnsignedShort(paramInt + 4), arrayOfLabel);
      Label localLabel2 = readLabel(readUnsignedShort(paramInt + 6), arrayOfLabel);
      String str1 = readUTF8(this.items[readUnsignedShort(paramInt + 8)], arrayOfChar);
      paramMethodVisitor.visitTryCatchBlock((Label)localObject1, localLabel1, localLabel2, str1);
      paramInt += 8;
    }
    paramInt += 2;
    int[] arrayOfInt1 = null;
    Object localObject1 = null;
    int i4 = 0;
    int i5 = 0;
    int i6 = -1;
    int i7 = -1;
    int i8 = 0;
    int i9 = 0;
    boolean bool1 = true;
    boolean bool2 = (paramContext.flags & 0x8) != 0;
    int i10 = 0;
    int i11 = 0;
    int i12 = 0;
    Context localContext = null;
    Object localObject2 = null;
    int i17;
    int i20;
    for (int i13 = readUnsignedShort(paramInt); i13 > 0; i13--)
    {
      String str2 = readUTF8(paramInt + 2, arrayOfChar);
      int i18;
      if ("LocalVariableTable".equals(str2))
      {
        if ((paramContext.flags & 0x2) == 0)
        {
          i8 = paramInt + 8;
          i17 = readUnsignedShort(paramInt + 8);
          i18 = paramInt;
          while (i17 > 0)
          {
            i20 = readUnsignedShort(i18 + 10);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).status |= 0x1;
            }
            i20 += readUnsignedShort(i18 + 12);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).status |= 0x1;
            }
            i18 += 10;
            i17--;
          }
        }
      }
      else if ("LocalVariableTypeTable".equals(str2))
      {
        i9 = paramInt + 8;
      }
      else if ("LineNumberTable".equals(str2))
      {
        if ((paramContext.flags & 0x2) == 0)
        {
          i17 = readUnsignedShort(paramInt + 8);
          i18 = paramInt;
          while (i17 > 0)
          {
            i20 = readUnsignedShort(i18 + 10);
            if (arrayOfLabel[i20] == null) {
              readLabel(i20, arrayOfLabel).status |= 0x1;
            }
            arrayOfLabel[i20].line = readUnsignedShort(i18 + 12);
            i18 += 4;
            i17--;
          }
        }
      }
      else if ("RuntimeVisibleTypeAnnotations".equals(str2))
      {
        arrayOfInt1 = readTypeAnnotations(paramMethodVisitor, paramContext, paramInt + 8, true);
        i6 = (arrayOfInt1.length == 0) || (readByte(arrayOfInt1[0]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[0] + 1);
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(str2))
      {
        localObject1 = readTypeAnnotations(paramMethodVisitor, paramContext, paramInt + 8, false);
        i7 = (localObject1.length == 0) || (readByte(localObject1[0]) < 67) ? -1 : readUnsignedShort(localObject1[0] + 1);
      }
      else if ("StackMapTable".equals(str2))
      {
        if ((paramContext.flags & 0x4) == 0)
        {
          i10 = paramInt + 10;
          i11 = readInt(paramInt + 4);
          i12 = readUnsignedShort(paramInt + 8);
        }
      }
      else if ("StackMap".equals(str2))
      {
        if ((paramContext.flags & 0x4) == 0)
        {
          bool1 = false;
          i10 = paramInt + 10;
          i11 = readInt(paramInt + 4);
          i12 = readUnsignedShort(paramInt + 8);
        }
      }
      else
      {
        for (i17 = 0; i17 < paramContext.attrs.length; i17++) {
          if (paramContext.attrs[i17].type.equals(str2))
          {
            Attribute localAttribute2 = paramContext.attrs[i17].read(this, paramInt + 8, readInt(paramInt + 4), arrayOfChar, m - 8, arrayOfLabel);
            if (localAttribute2 != null)
            {
              localAttribute2.next = localObject2;
              localObject2 = localAttribute2;
            }
          }
        }
      }
      paramInt += 6 + readInt(paramInt + 4);
    }
    paramInt += 2;
    if (i10 != 0)
    {
      localContext = paramContext;
      localContext.offset = -1;
      localContext.mode = 0;
      localContext.localCount = 0;
      localContext.localDiff = 0;
      localContext.stackCount = 0;
      localContext.local = new Object[j];
      localContext.stack = new Object[i];
      if (bool2) {
        getImplicitFrame(paramContext);
      }
      for (i13 = i10; i13 < i10 + i11 - 2; i13++) {
        if (arrayOfByte[i13] == 8)
        {
          int i15 = readUnsignedShort(i13 + 1);
          if ((i15 >= 0) && (i15 < k) && ((arrayOfByte[(m + i15)] & 0xFF) == 187)) {
            readLabel(i15, arrayOfLabel);
          }
        }
      }
    }
    paramInt = m;
    int i19;
    Object localObject3;
    int i21;
    int i23;
    while (paramInt < n)
    {
      i13 = paramInt - m;
      Label localLabel3 = arrayOfLabel[i13];
      if (localLabel3 != null)
      {
        paramMethodVisitor.visitLabel(localLabel3);
        if (((paramContext.flags & 0x2) == 0) && (localLabel3.line > 0)) {
          paramMethodVisitor.visitLineNumber(localLabel3.line, localLabel3);
        }
      }
      while ((localContext != null) && ((localContext.offset == i13) || (localContext.offset == -1)))
      {
        if (localContext.offset != -1) {
          if ((!bool1) || (bool2)) {
            paramMethodVisitor.visitFrame(-1, localContext.localCount, localContext.local, localContext.stackCount, localContext.stack);
          } else {
            paramMethodVisitor.visitFrame(localContext.mode, localContext.localDiff, localContext.local, localContext.stackCount, localContext.stack);
          }
        }
        if (i12 > 0)
        {
          i10 = readFrame(i10, bool1, bool2, localContext);
          i12--;
        }
        else
        {
          localContext = null;
        }
      }
      i17 = arrayOfByte[paramInt] & 0xFF;
      Object localObject4;
      int i24;
      Object localObject5;
      switch (ClassWriter.TYPE[i17])
      {
      case 0: 
        paramMethodVisitor.visitInsn(i17);
        paramInt++;
        break;
      case 4: 
        if (i17 > 54)
        {
          i17 -= 59;
          paramMethodVisitor.visitVarInsn(54 + (i17 >> 2), i17 & 0x3);
        }
        else
        {
          i17 -= 26;
          paramMethodVisitor.visitVarInsn(21 + (i17 >> 2), i17 & 0x3);
        }
        paramInt++;
        break;
      case 9: 
        paramMethodVisitor.visitJumpInsn(i17, arrayOfLabel[(i13 + readShort(paramInt + 1))]);
        paramInt += 3;
        break;
      case 10: 
        paramMethodVisitor.visitJumpInsn(i17 - 33, arrayOfLabel[(i13 + readInt(paramInt + 1))]);
        paramInt += 5;
        break;
      case 17: 
        i17 = arrayOfByte[(paramInt + 1)] & 0xFF;
        if (i17 == 132)
        {
          paramMethodVisitor.visitIincInsn(readUnsignedShort(paramInt + 2), readShort(paramInt + 4));
          paramInt += 6;
        }
        else
        {
          paramMethodVisitor.visitVarInsn(i17, readUnsignedShort(paramInt + 2));
          paramInt += 4;
        }
        break;
      case 14: 
        paramInt = paramInt + 4 - (i13 & 0x3);
        i19 = i13 + readInt(paramInt);
        i20 = readInt(paramInt + 4);
        int i22 = readInt(paramInt + 8);
        localObject4 = new Label[i22 - i20 + 1];
        paramInt += 12;
        for (i24 = 0; i24 < localObject4.length; i24++)
        {
          localObject4[i24] = arrayOfLabel[(i13 + readInt(paramInt))];
          paramInt += 4;
        }
        paramMethodVisitor.visitTableSwitchInsn(i20, i22, arrayOfLabel[i19], (Label[])localObject4);
        break;
      case 15: 
        paramInt = paramInt + 4 - (i13 & 0x3);
        i19 = i13 + readInt(paramInt);
        i20 = readInt(paramInt + 4);
        localObject3 = new int[i20];
        localObject4 = new Label[i20];
        paramInt += 8;
        for (i24 = 0; i24 < i20; i24++)
        {
          localObject3[i24] = readInt(paramInt);
          localObject4[i24] = arrayOfLabel[(i13 + readInt(paramInt + 4))];
          paramInt += 8;
        }
        paramMethodVisitor.visitLookupSwitchInsn(arrayOfLabel[i19], (int[])localObject3, (Label[])localObject4);
        break;
      case 3: 
        paramMethodVisitor.visitVarInsn(i17, arrayOfByte[(paramInt + 1)] & 0xFF);
        paramInt += 2;
        break;
      case 1: 
        paramMethodVisitor.visitIntInsn(i17, arrayOfByte[(paramInt + 1)]);
        paramInt += 2;
        break;
      case 2: 
        paramMethodVisitor.visitIntInsn(i17, readShort(paramInt + 1));
        paramInt += 3;
        break;
      case 11: 
        paramMethodVisitor.visitLdcInsn(readConst(arrayOfByte[(paramInt + 1)] & 0xFF, arrayOfChar));
        paramInt += 2;
        break;
      case 12: 
        paramMethodVisitor.visitLdcInsn(readConst(readUnsignedShort(paramInt + 1), arrayOfChar));
        paramInt += 3;
        break;
      case 6: 
      case 7: 
        i19 = this.items[readUnsignedShort(paramInt + 1)];
        i20 = arrayOfByte[(i19 - 1)] == 11 ? 1 : 0;
        localObject3 = readClass(i19, arrayOfChar);
        i19 = this.items[readUnsignedShort(i19 + 2)];
        localObject4 = readUTF8(i19, arrayOfChar);
        localObject5 = readUTF8(i19 + 2, arrayOfChar);
        if (i17 < 182) {
          paramMethodVisitor.visitFieldInsn(i17, (String)localObject3, (String)localObject4, (String)localObject5);
        } else {
          paramMethodVisitor.visitMethodInsn(i17, (String)localObject3, (String)localObject4, (String)localObject5, i20);
        }
        if (i17 == 185) {
          paramInt += 5;
        } else {
          paramInt += 3;
        }
        break;
      case 8: 
        i19 = this.items[readUnsignedShort(paramInt + 1)];
        i21 = paramContext.bootstrapMethods[readUnsignedShort(i19)];
        localObject3 = (Handle)readConst(readUnsignedShort(i21), arrayOfChar);
        i23 = readUnsignedShort(i21 + 2);
        localObject5 = new Object[i23];
        i21 += 4;
        for (int i25 = 0; i25 < i23; i25++)
        {
          localObject5[i25] = readConst(readUnsignedShort(i21), arrayOfChar);
          i21 += 2;
        }
        i19 = this.items[readUnsignedShort(i19 + 2)];
        String str3 = readUTF8(i19, arrayOfChar);
        String str4 = readUTF8(i19 + 2, arrayOfChar);
        paramMethodVisitor.visitInvokeDynamicInsn(str3, str4, (Handle)localObject3, (Object[])localObject5);
        paramInt += 5;
        break;
      case 5: 
        paramMethodVisitor.visitTypeInsn(i17, readClass(paramInt + 1, arrayOfChar));
        paramInt += 3;
        break;
      case 13: 
        paramMethodVisitor.visitIincInsn(arrayOfByte[(paramInt + 1)] & 0xFF, arrayOfByte[(paramInt + 2)]);
        paramInt += 3;
        break;
      case 16: 
      default: 
        paramMethodVisitor.visitMultiANewArrayInsn(readClass(paramInt + 1, arrayOfChar), arrayOfByte[(paramInt + 3)] & 0xFF);
        paramInt += 4;
      }
      while ((arrayOfInt1 != null) && (i4 < arrayOfInt1.length) && (i6 <= i13))
      {
        if (i6 == i13)
        {
          i19 = readAnnotationTarget(paramContext, arrayOfInt1[i4]);
          readAnnotationValues(i19 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i19, arrayOfChar), true));
        }
        i4++;
        i6 = (i4 >= arrayOfInt1.length) || (readByte(arrayOfInt1[i4]) < 67) ? -1 : readUnsignedShort(arrayOfInt1[i4] + 1);
      }
      while ((localObject1 != null) && (i5 < localObject1.length) && (i7 <= i13))
      {
        if (i7 == i13)
        {
          i19 = readAnnotationTarget(paramContext, localObject1[i5]);
          readAnnotationValues(i19 + 2, arrayOfChar, true, paramMethodVisitor.visitInsnAnnotation(paramContext.typeRef, paramContext.typePath, readUTF8(i19, arrayOfChar), false));
        }
        i5++;
        i7 = (i5 >= localObject1.length) || (readByte(localObject1[i5]) < 67) ? -1 : readUnsignedShort(localObject1[i5] + 1);
      }
    }
    if (arrayOfLabel[k] != null) {
      paramMethodVisitor.visitLabel(arrayOfLabel[k]);
    }
    int i16;
    if (((paramContext.flags & 0x2) == 0) && (i8 != 0))
    {
      int[] arrayOfInt2 = null;
      if (i9 != 0)
      {
        paramInt = i9 + 2;
        arrayOfInt2 = new int[readUnsignedShort(i9) * 3];
        i16 = arrayOfInt2.length;
        while (i16 > 0)
        {
          arrayOfInt2[(--i16)] = (paramInt + 6);
          arrayOfInt2[(--i16)] = readUnsignedShort(paramInt + 8);
          arrayOfInt2[(--i16)] = readUnsignedShort(paramInt);
          paramInt += 10;
        }
      }
      paramInt = i8 + 2;
      for (i16 = readUnsignedShort(i8); i16 > 0; i16--)
      {
        i17 = readUnsignedShort(paramInt);
        i19 = readUnsignedShort(paramInt + 2);
        i21 = readUnsignedShort(paramInt + 8);
        localObject3 = null;
        if (arrayOfInt2 != null) {
          for (i23 = 0; i23 < arrayOfInt2.length; i23 += 3) {
            if ((arrayOfInt2[i23] == i17) && (arrayOfInt2[(i23 + 1)] == i21))
            {
              localObject3 = readUTF8(arrayOfInt2[(i23 + 2)], arrayOfChar);
              break;
            }
          }
        }
        paramMethodVisitor.visitLocalVariable(readUTF8(paramInt + 4, arrayOfChar), readUTF8(paramInt + 6, arrayOfChar), (String)localObject3, arrayOfLabel[i17], arrayOfLabel[(i17 + i19)], i21);
        paramInt += 10;
      }
    }
    int i14;
    if (arrayOfInt1 != null) {
      for (i14 = 0; i14 < arrayOfInt1.length; i14++) {
        if (readByte(arrayOfInt1[i14]) >> 1 == 32)
        {
          i16 = readAnnotationTarget(paramContext, arrayOfInt1[i14]);
          i16 = readAnnotationValues(i16 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.typeRef, paramContext.typePath, paramContext.start, paramContext.end, paramContext.index, readUTF8(i16, arrayOfChar), true));
        }
      }
    }
    if (localObject1 != null) {
      for (i14 = 0; i14 < localObject1.length; i14++) {
        if (readByte(localObject1[i14]) >> 1 == 32)
        {
          i16 = readAnnotationTarget(paramContext, localObject1[i14]);
          i16 = readAnnotationValues(i16 + 2, arrayOfChar, true, paramMethodVisitor.visitLocalVariableAnnotation(paramContext.typeRef, paramContext.typePath, paramContext.start, paramContext.end, paramContext.index, readUTF8(i16, arrayOfChar), false));
        }
      }
    }
    while (localObject2 != null)
    {
      Attribute localAttribute1 = localObject2.next;
      localObject2.next = null;
      paramMethodVisitor.visitAttribute(localObject2);
      localObject2 = localAttribute1;
    }
    paramMethodVisitor.visitMaxs(i, j);
  }
  
  private int[] readTypeAnnotations(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt, boolean paramBoolean)
  {
    char[] arrayOfChar = paramContext.buffer;
    int[] arrayOfInt = new int[readUnsignedShort(paramInt)];
    paramInt += 2;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      arrayOfInt[i] = paramInt;
      int j = readInt(paramInt);
      switch (j >>> 24)
      {
      case 0: 
      case 1: 
      case 22: 
        paramInt += 2;
        break;
      case 19: 
      case 20: 
      case 21: 
        paramInt++;
        break;
      case 64: 
      case 65: 
        for (k = readUnsignedShort(paramInt + 1); k > 0; k--)
        {
          int m = readUnsignedShort(paramInt + 3);
          int n = readUnsignedShort(paramInt + 5);
          readLabel(m, paramContext.labels);
          readLabel(m + n, paramContext.labels);
          paramInt += 6;
        }
        paramInt += 3;
        break;
      case 71: 
      case 72: 
      case 73: 
      case 74: 
      case 75: 
        paramInt += 4;
        break;
      default: 
        paramInt += 3;
      }
      int k = readByte(paramInt);
      if (j >>> 24 == 66)
      {
        TypePath localTypePath = k == 0 ? null : new TypePath(this.b, paramInt);
        paramInt += 1 + 2 * k;
        paramInt = readAnnotationValues(paramInt + 2, arrayOfChar, true, paramMethodVisitor.visitTryCatchAnnotation(j, localTypePath, readUTF8(paramInt, arrayOfChar), paramBoolean));
      }
      else
      {
        paramInt = readAnnotationValues(paramInt + 3 + 2 * k, arrayOfChar, true, null);
      }
    }
    return arrayOfInt;
  }
  
  private int readAnnotationTarget(Context paramContext, int paramInt)
  {
    int i = readInt(paramInt);
    switch (i >>> 24)
    {
    case 0: 
    case 1: 
    case 22: 
      i &= 0xFFFF0000;
      paramInt += 2;
      break;
    case 19: 
    case 20: 
    case 21: 
      i &= 0xFF000000;
      paramInt++;
      break;
    case 64: 
    case 65: 
      i &= 0xFF000000;
      j = readUnsignedShort(paramInt + 1);
      paramContext.start = new Label[j];
      paramContext.end = new Label[j];
      paramContext.index = new int[j];
      paramInt += 3;
      for (int k = 0; k < j; k++)
      {
        int m = readUnsignedShort(paramInt);
        int n = readUnsignedShort(paramInt + 2);
        paramContext.start[k] = readLabel(m, paramContext.labels);
        paramContext.end[k] = readLabel(m + n, paramContext.labels);
        paramContext.index[k] = readUnsignedShort(paramInt + 4);
        paramInt += 6;
      }
      break;
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
      i &= 0xFF0000FF;
      paramInt += 4;
      break;
    default: 
      i &= (i >>> 24 < 67 ? 65280 : -16777216);
      paramInt += 3;
    }
    int j = readByte(paramInt);
    paramContext.typeRef = i;
    paramContext.typePath = (j == 0 ? null : new TypePath(this.b, paramInt));
    return paramInt + 1 + 2 * j;
  }
  
  private void readParameterAnnotations(MethodVisitor paramMethodVisitor, Context paramContext, int paramInt, boolean paramBoolean)
  {
    int j = this.b[(paramInt++)] & 0xFF;
    int k = Type.getArgumentTypes(paramContext.desc).length - j;
    AnnotationVisitor localAnnotationVisitor;
    for (int i = 0; i < k; i++)
    {
      localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(i, "Ljava/lang/Synthetic;", false);
      if (localAnnotationVisitor != null) {
        localAnnotationVisitor.visitEnd();
      }
    }
    char[] arrayOfChar = paramContext.buffer;
    while (i < j + k)
    {
      int m = readUnsignedShort(paramInt);
      paramInt += 2;
      while (m > 0)
      {
        localAnnotationVisitor = paramMethodVisitor.visitParameterAnnotation(i, readUTF8(paramInt, arrayOfChar), paramBoolean);
        paramInt = readAnnotationValues(paramInt + 2, arrayOfChar, true, localAnnotationVisitor);
        m--;
      }
      i++;
    }
  }
  
  private int readAnnotationValues(int paramInt, char[] paramArrayOfChar, boolean paramBoolean, AnnotationVisitor paramAnnotationVisitor)
  {
    int i = readUnsignedShort(paramInt);
    paramInt += 2;
    if (paramBoolean) {
      while (i > 0)
      {
        paramInt = readAnnotationValue(paramInt + 2, paramArrayOfChar, readUTF8(paramInt, paramArrayOfChar), paramAnnotationVisitor);
        i--;
      }
    }
    while (i > 0)
    {
      paramInt = readAnnotationValue(paramInt, paramArrayOfChar, null, paramAnnotationVisitor);
      i--;
    }
    if (paramAnnotationVisitor != null) {
      paramAnnotationVisitor.visitEnd();
    }
    return paramInt;
  }
  
  private int readAnnotationValue(int paramInt, char[] paramArrayOfChar, String paramString, AnnotationVisitor paramAnnotationVisitor)
  {
    if (paramAnnotationVisitor == null)
    {
      switch (this.b[paramInt] & 0xFF)
      {
      case 101: 
        return paramInt + 5;
      case 64: 
        return readAnnotationValues(paramInt + 3, paramArrayOfChar, true, null);
      case 91: 
        return readAnnotationValues(paramInt + 1, paramArrayOfChar, false, null);
      }
      return paramInt + 3;
    }
    switch (this.b[(paramInt++)] & 0xFF)
    {
    case 68: 
    case 70: 
    case 73: 
    case 74: 
      paramAnnotationVisitor.visit(paramString, readConst(readUnsignedShort(paramInt), paramArrayOfChar));
      paramInt += 2;
      break;
    case 66: 
      paramAnnotationVisitor.visit(paramString, Byte.valueOf((byte)readInt(this.items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 90: 
      paramAnnotationVisitor.visit(paramString, readInt(this.items[readUnsignedShort(paramInt)]) == 0 ? Boolean.FALSE : Boolean.TRUE);
      paramInt += 2;
      break;
    case 83: 
      paramAnnotationVisitor.visit(paramString, Short.valueOf((short)readInt(this.items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 67: 
      paramAnnotationVisitor.visit(paramString, Character.valueOf((char)readInt(this.items[readUnsignedShort(paramInt)])));
      paramInt += 2;
      break;
    case 115: 
      paramAnnotationVisitor.visit(paramString, readUTF8(paramInt, paramArrayOfChar));
      paramInt += 2;
      break;
    case 101: 
      paramAnnotationVisitor.visitEnum(paramString, readUTF8(paramInt, paramArrayOfChar), readUTF8(paramInt + 2, paramArrayOfChar));
      paramInt += 4;
      break;
    case 99: 
      paramAnnotationVisitor.visit(paramString, Type.getType(readUTF8(paramInt, paramArrayOfChar)));
      paramInt += 2;
      break;
    case 64: 
      paramInt = readAnnotationValues(paramInt + 2, paramArrayOfChar, true, paramAnnotationVisitor.visitAnnotation(paramString, readUTF8(paramInt, paramArrayOfChar)));
      break;
    case 91: 
      int j = readUnsignedShort(paramInt);
      paramInt += 2;
      if (j == 0) {
        return readAnnotationValues(paramInt - 2, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      int i;
      switch (this.b[(paramInt++)] & 0xFF)
      {
      case 66: 
        byte[] arrayOfByte = new byte[j];
        for (i = 0; i < j; i++)
        {
          arrayOfByte[i] = ((byte)readInt(this.items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfByte);
        paramInt--;
        break;
      case 90: 
        boolean[] arrayOfBoolean = new boolean[j];
        for (i = 0; i < j; i++)
        {
          arrayOfBoolean[i] = (readInt(this.items[readUnsignedShort(paramInt)]) != 0 ? 1 : false);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfBoolean);
        paramInt--;
        break;
      case 83: 
        short[] arrayOfShort = new short[j];
        for (i = 0; i < j; i++)
        {
          arrayOfShort[i] = ((short)readInt(this.items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfShort);
        paramInt--;
        break;
      case 67: 
        char[] arrayOfChar = new char[j];
        for (i = 0; i < j; i++)
        {
          arrayOfChar[i] = ((char)readInt(this.items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfChar);
        paramInt--;
        break;
      case 73: 
        int[] arrayOfInt = new int[j];
        for (i = 0; i < j; i++)
        {
          arrayOfInt[i] = readInt(this.items[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfInt);
        paramInt--;
        break;
      case 74: 
        long[] arrayOfLong = new long[j];
        for (i = 0; i < j; i++)
        {
          arrayOfLong[i] = readLong(this.items[readUnsignedShort(paramInt)]);
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfLong);
        paramInt--;
        break;
      case 70: 
        float[] arrayOfFloat = new float[j];
        for (i = 0; i < j; i++)
        {
          arrayOfFloat[i] = Float.intBitsToFloat(readInt(this.items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfFloat);
        paramInt--;
        break;
      case 68: 
        double[] arrayOfDouble = new double[j];
        for (i = 0; i < j; i++)
        {
          arrayOfDouble[i] = Double.longBitsToDouble(readLong(this.items[readUnsignedShort(paramInt)]));
          paramInt += 3;
        }
        paramAnnotationVisitor.visit(paramString, arrayOfDouble);
        paramInt--;
        break;
      case 69: 
      case 71: 
      case 72: 
      case 75: 
      case 76: 
      case 77: 
      case 78: 
      case 79: 
      case 80: 
      case 81: 
      case 82: 
      case 84: 
      case 85: 
      case 86: 
      case 87: 
      case 88: 
      case 89: 
      default: 
        paramInt = readAnnotationValues(paramInt - 3, paramArrayOfChar, false, paramAnnotationVisitor.visitArray(paramString));
      }
      break;
    }
    return paramInt;
  }
  
  private void getImplicitFrame(Context paramContext)
  {
    String str = paramContext.desc;
    Object[] arrayOfObject = paramContext.local;
    int i = 0;
    if ((paramContext.access & 0x8) == 0) {
      if ("<init>".equals(paramContext.name)) {
        arrayOfObject[(i++)] = Opcodes.UNINITIALIZED_THIS;
      } else {
        arrayOfObject[(i++)] = readClass(this.header + 2, paramContext.buffer);
      }
    }
    int j = 1;
    for (;;)
    {
      int k = j;
      switch (str.charAt(j++))
      {
      case 'B': 
      case 'C': 
      case 'I': 
      case 'S': 
      case 'Z': 
        arrayOfObject[(i++)] = Opcodes.INTEGER;
        break;
      case 'F': 
        arrayOfObject[(i++)] = Opcodes.FLOAT;
        break;
      case 'J': 
        arrayOfObject[(i++)] = Opcodes.LONG;
        break;
      case 'D': 
        arrayOfObject[(i++)] = Opcodes.DOUBLE;
        break;
      case '[': 
        while (str.charAt(j) == '[') {
          j++;
        }
        if (str.charAt(j) == 'L')
        {
          j++;
          while (str.charAt(j) != ';') {
            j++;
          }
        }
        arrayOfObject[(i++)] = str.substring(k, ++j);
        break;
      case 'L': 
        while (str.charAt(j) != ';') {
          j++;
        }
        arrayOfObject[(i++)] = str.substring(k + 1, j++);
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        break label371;
      }
    }
    label371:
    paramContext.localCount = i;
  }
  
  private int readFrame(int paramInt, boolean paramBoolean1, boolean paramBoolean2, Context paramContext)
  {
    char[] arrayOfChar = paramContext.buffer;
    Label[] arrayOfLabel = paramContext.labels;
    int i;
    if (paramBoolean1)
    {
      i = this.b[(paramInt++)] & 0xFF;
    }
    else
    {
      i = 255;
      paramContext.offset = -1;
    }
    paramContext.localDiff = 0;
    int j;
    if (i < 64)
    {
      j = i;
      paramContext.mode = 3;
      paramContext.stackCount = 0;
    }
    else if (i < 128)
    {
      j = i - 64;
      paramInt = readFrameType(paramContext.stack, 0, paramInt, arrayOfChar, arrayOfLabel);
      paramContext.mode = 4;
      paramContext.stackCount = 1;
    }
    else
    {
      j = readUnsignedShort(paramInt);
      paramInt += 2;
      if (i == 247)
      {
        paramInt = readFrameType(paramContext.stack, 0, paramInt, arrayOfChar, arrayOfLabel);
        paramContext.mode = 4;
        paramContext.stackCount = 1;
      }
      else if ((i >= 248) && (i < 251))
      {
        paramContext.mode = 2;
        paramContext.localDiff = (251 - i);
        paramContext.localCount -= paramContext.localDiff;
        paramContext.stackCount = 0;
      }
      else if (i == 251)
      {
        paramContext.mode = 3;
        paramContext.stackCount = 0;
      }
      else
      {
        int k;
        int m;
        if (i < 255)
        {
          k = paramBoolean2 ? paramContext.localCount : 0;
          for (m = i - 251; m > 0; m--) {
            paramInt = readFrameType(paramContext.local, k++, paramInt, arrayOfChar, arrayOfLabel);
          }
          paramContext.mode = 1;
          paramContext.localDiff = (i - 251);
          paramContext.localCount += paramContext.localDiff;
          paramContext.stackCount = 0;
        }
        else
        {
          paramContext.mode = 0;
          k = readUnsignedShort(paramInt);
          paramInt += 2;
          paramContext.localDiff = k;
          paramContext.localCount = k;
          m = 0;
          while (k > 0)
          {
            paramInt = readFrameType(paramContext.local, m++, paramInt, arrayOfChar, arrayOfLabel);
            k--;
          }
          k = readUnsignedShort(paramInt);
          paramInt += 2;
          paramContext.stackCount = k;
          m = 0;
          while (k > 0)
          {
            paramInt = readFrameType(paramContext.stack, m++, paramInt, arrayOfChar, arrayOfLabel);
            k--;
          }
        }
      }
    }
    paramContext.offset += j + 1;
    readLabel(paramContext.offset, arrayOfLabel);
    return paramInt;
  }
  
  private int readFrameType(Object[] paramArrayOfObject, int paramInt1, int paramInt2, char[] paramArrayOfChar, Label[] paramArrayOfLabel)
  {
    int i = this.b[(paramInt2++)] & 0xFF;
    switch (i)
    {
    case 0: 
      paramArrayOfObject[paramInt1] = Opcodes.TOP;
      break;
    case 1: 
      paramArrayOfObject[paramInt1] = Opcodes.INTEGER;
      break;
    case 2: 
      paramArrayOfObject[paramInt1] = Opcodes.FLOAT;
      break;
    case 3: 
      paramArrayOfObject[paramInt1] = Opcodes.DOUBLE;
      break;
    case 4: 
      paramArrayOfObject[paramInt1] = Opcodes.LONG;
      break;
    case 5: 
      paramArrayOfObject[paramInt1] = Opcodes.NULL;
      break;
    case 6: 
      paramArrayOfObject[paramInt1] = Opcodes.UNINITIALIZED_THIS;
      break;
    case 7: 
      paramArrayOfObject[paramInt1] = readClass(paramInt2, paramArrayOfChar);
      paramInt2 += 2;
      break;
    default: 
      paramArrayOfObject[paramInt1] = readLabel(readUnsignedShort(paramInt2), paramArrayOfLabel);
      paramInt2 += 2;
    }
    return paramInt2;
  }
  
  protected Label readLabel(int paramInt, Label[] paramArrayOfLabel)
  {
    if (paramArrayOfLabel[paramInt] == null) {
      paramArrayOfLabel[paramInt] = new Label();
    }
    return paramArrayOfLabel[paramInt];
  }
  
  private int getAttributes()
  {
    int i = this.header + 8 + readUnsignedShort(this.header + 6) * 2;
    int k;
    for (int j = readUnsignedShort(i); j > 0; j--)
    {
      for (k = readUnsignedShort(i + 8); k > 0; k--) {
        i += 6 + readInt(i + 12);
      }
      i += 8;
    }
    i += 2;
    for (j = readUnsignedShort(i); j > 0; j--)
    {
      for (k = readUnsignedShort(i + 8); k > 0; k--) {
        i += 6 + readInt(i + 12);
      }
      i += 8;
    }
    return i + 2;
  }
  
  private Attribute readAttribute(Attribute[] paramArrayOfAttribute, String paramString, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    for (int i = 0; i < paramArrayOfAttribute.length; i++) {
      if (paramArrayOfAttribute[i].type.equals(paramString)) {
        return paramArrayOfAttribute[i].read(this, paramInt1, paramInt2, paramArrayOfChar, paramInt3, paramArrayOfLabel);
      }
    }
    return new Attribute(paramString).read(this, paramInt1, paramInt2, null, -1, null);
  }
  
  public int getItemCount()
  {
    return this.items.length;
  }
  
  public int getItem(int paramInt)
  {
    return this.items[paramInt];
  }
  
  public int getMaxStringLength()
  {
    return this.maxStringLength;
  }
  
  public int readByte(int paramInt)
  {
    return this.b[paramInt] & 0xFF;
  }
  
  public int readUnsignedShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  public short readShort(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (short)((arrayOfByte[paramInt] & 0xFF) << 8 | arrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  public int readInt(int paramInt)
  {
    byte[] arrayOfByte = this.b;
    return (arrayOfByte[paramInt] & 0xFF) << 24 | (arrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (arrayOfByte[(paramInt + 2)] & 0xFF) << 8 | arrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  public long readLong(int paramInt)
  {
    long l1 = readInt(paramInt);
    long l2 = readInt(paramInt + 4) & 0xFFFFFFFF;
    return l1 << 32 | l2;
  }
  
  public String readUTF8(int paramInt, char[] paramArrayOfChar)
  {
    int i = readUnsignedShort(paramInt);
    if ((paramInt == 0) || (i == 0)) {
      return null;
    }
    String str = this.strings[i];
    if (str != null) {
      return str;
    }
    paramInt = this.items[i];
    return this.strings[i] =  = readUTF(paramInt + 2, readUnsignedShort(paramInt), paramArrayOfChar);
  }
  
  private String readUTF(int paramInt1, int paramInt2, char[] paramArrayOfChar)
  {
    int i = paramInt1 + paramInt2;
    byte[] arrayOfByte = this.b;
    int j = 0;
    int m = 0;
    int n = 0;
    while (paramInt1 < i)
    {
      int k = arrayOfByte[(paramInt1++)];
      switch (m)
      {
      case 0: 
        k &= 0xFF;
        if (k < 128)
        {
          paramArrayOfChar[(j++)] = ((char)k);
        }
        else if ((k < 224) && (k > 191))
        {
          n = (char)(k & 0x1F);
          m = 1;
        }
        else
        {
          n = (char)(k & 0xF);
          m = 2;
        }
        break;
      case 1: 
        paramArrayOfChar[(j++)] = ((char)(n << 6 | k & 0x3F));
        m = 0;
        break;
      case 2: 
        n = (char)(n << 6 | k & 0x3F);
        m = 1;
      }
    }
    return new String(paramArrayOfChar, 0, j);
  }
  
  public String readClass(int paramInt, char[] paramArrayOfChar)
  {
    return readUTF8(this.items[readUnsignedShort(paramInt)], paramArrayOfChar);
  }
  
  public Object readConst(int paramInt, char[] paramArrayOfChar)
  {
    int i = this.items[paramInt];
    switch (this.b[(i - 1)])
    {
    case 3: 
      return Integer.valueOf(readInt(i));
    case 4: 
      return Float.valueOf(Float.intBitsToFloat(readInt(i)));
    case 5: 
      return Long.valueOf(readLong(i));
    case 6: 
      return Double.valueOf(Double.longBitsToDouble(readLong(i)));
    case 7: 
      return Type.getObjectType(readUTF8(i, paramArrayOfChar));
    case 8: 
      return readUTF8(i, paramArrayOfChar);
    case 16: 
      return Type.getMethodType(readUTF8(i, paramArrayOfChar));
    }
    int j = readByte(i);
    int[] arrayOfInt = this.items;
    int k = arrayOfInt[readUnsignedShort(i + 1)];
    String str1 = readClass(k, paramArrayOfChar);
    k = arrayOfInt[readUnsignedShort(k + 2)];
    String str2 = readUTF8(k, paramArrayOfChar);
    String str3 = readUTF8(k + 2, paramArrayOfChar);
    return new Handle(j, str1, str2, str3);
  }
}
