package jdk.internal.org.objectweb.asm;

public class ClassWriter
  extends ClassVisitor
{
  public static final int COMPUTE_MAXS = 1;
  public static final int COMPUTE_FRAMES = 2;
  static final int ACC_SYNTHETIC_ATTRIBUTE = 262144;
  static final int TO_ACC_SYNTHETIC = 64;
  static final int NOARG_INSN = 0;
  static final int SBYTE_INSN = 1;
  static final int SHORT_INSN = 2;
  static final int VAR_INSN = 3;
  static final int IMPLVAR_INSN = 4;
  static final int TYPE_INSN = 5;
  static final int FIELDORMETH_INSN = 6;
  static final int ITFMETH_INSN = 7;
  static final int INDYMETH_INSN = 8;
  static final int LABEL_INSN = 9;
  static final int LABELW_INSN = 10;
  static final int LDC_INSN = 11;
  static final int LDCW_INSN = 12;
  static final int IINC_INSN = 13;
  static final int TABL_INSN = 14;
  static final int LOOK_INSN = 15;
  static final int MANA_INSN = 16;
  static final int WIDE_INSN = 17;
  static final byte[] TYPE;
  static final int CLASS = 7;
  static final int FIELD = 9;
  static final int METH = 10;
  static final int IMETH = 11;
  static final int STR = 8;
  static final int INT = 3;
  static final int FLOAT = 4;
  static final int LONG = 5;
  static final int DOUBLE = 6;
  static final int NAME_TYPE = 12;
  static final int UTF8 = 1;
  static final int MTYPE = 16;
  static final int HANDLE = 15;
  static final int INDY = 18;
  static final int HANDLE_BASE = 20;
  static final int TYPE_NORMAL = 30;
  static final int TYPE_UNINIT = 31;
  static final int TYPE_MERGED = 32;
  static final int BSM = 33;
  ClassReader cr;
  int version;
  int index = 1;
  final ByteVector pool = new ByteVector();
  Item[] items = new Item['Ā'];
  int threshold = (int)(0.75D * this.items.length);
  final Item key = new Item();
  final Item key2 = new Item();
  final Item key3 = new Item();
  final Item key4 = new Item();
  Item[] typeTable;
  private short typeCount;
  private int access;
  private int name;
  String thisName;
  private int signature;
  private int superName;
  private int interfaceCount;
  private int[] interfaces;
  private int sourceFile;
  private ByteVector sourceDebug;
  private int enclosingMethodOwner;
  private int enclosingMethod;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private AnnotationWriter tanns;
  private AnnotationWriter itanns;
  private Attribute attrs;
  private int innerClassesCount;
  private ByteVector innerClasses;
  int bootstrapMethodsCount;
  ByteVector bootstrapMethods;
  FieldWriter firstField;
  FieldWriter lastField;
  MethodWriter firstMethod;
  MethodWriter lastMethod;
  private boolean computeMaxs;
  private boolean computeFrames;
  boolean invalidFrames;
  
  public ClassWriter(int paramInt)
  {
    super(327680);
    this.computeMaxs = ((paramInt & 0x1) != 0);
    this.computeFrames = ((paramInt & 0x2) != 0);
  }
  
  public ClassWriter(ClassReader paramClassReader, int paramInt)
  {
    this(paramInt);
    paramClassReader.copyPool(this);
    this.cr = paramClassReader;
  }
  
  public final void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this.version = paramInt1;
    this.access = paramInt2;
    this.name = newClass(paramString1);
    this.thisName = paramString1;
    if (paramString2 != null) {
      this.signature = newUTF8(paramString2);
    }
    this.superName = (paramString3 == null ? 0 : newClass(paramString3));
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      this.interfaceCount = paramArrayOfString.length;
      this.interfaces = new int[this.interfaceCount];
      for (int i = 0; i < this.interfaceCount; i++) {
        this.interfaces[i] = newClass(paramArrayOfString[i]);
      }
    }
  }
  
  public final void visitSource(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      this.sourceFile = newUTF8(paramString1);
    }
    if (paramString2 != null) {
      this.sourceDebug = new ByteVector().encodeUTF8(paramString2, 0, Integer.MAX_VALUE);
    }
  }
  
  public final void visitOuterClass(String paramString1, String paramString2, String paramString3)
  {
    this.enclosingMethodOwner = newClass(paramString1);
    if ((paramString2 != null) && (paramString3 != null)) {
      this.enclosingMethod = newNameType(paramString2, paramString3);
    }
  }
  
  public final AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      localAnnotationWriter.next = this.anns;
      this.anns = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.next = this.ianns;
      this.ianns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this, true, localByteVector, localByteVector, localByteVector.length - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.next = this.tanns;
      this.tanns = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.next = this.itanns;
      this.itanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public final void visitAttribute(Attribute paramAttribute)
  {
    paramAttribute.next = this.attrs;
    this.attrs = paramAttribute;
  }
  
  public final void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt)
  {
    if (this.innerClasses == null) {
      this.innerClasses = new ByteVector();
    }
    Item localItem = newClassItem(paramString1);
    if (localItem.intVal == 0)
    {
      this.innerClassesCount += 1;
      this.innerClasses.putShort(localItem.index);
      this.innerClasses.putShort(paramString2 == null ? 0 : newClass(paramString2));
      this.innerClasses.putShort(paramString3 == null ? 0 : newUTF8(paramString3));
      this.innerClasses.putShort(paramInt);
      localItem.intVal = this.innerClassesCount;
    }
  }
  
  public final FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    return new FieldWriter(this, paramInt, paramString1, paramString2, paramString3, paramObject);
  }
  
  public final MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    return new MethodWriter(this, paramInt, paramString1, paramString2, paramString3, paramArrayOfString, this.computeMaxs, this.computeFrames);
  }
  
  public final void visitEnd() {}
  
  public byte[] toByteArray()
  {
    if (this.index > 65535) {
      throw new RuntimeException("Class file too large!");
    }
    int i = 24 + 2 * this.interfaceCount;
    int j = 0;
    for (FieldWriter localFieldWriter = this.firstField; localFieldWriter != null; localFieldWriter = (FieldWriter)localFieldWriter.fv)
    {
      j++;
      i += localFieldWriter.getSize();
    }
    int k = 0;
    for (MethodWriter localMethodWriter = this.firstMethod; localMethodWriter != null; localMethodWriter = (MethodWriter)localMethodWriter.mv)
    {
      k++;
      i += localMethodWriter.getSize();
    }
    int m = 0;
    if (this.bootstrapMethods != null)
    {
      m++;
      i += 8 + this.bootstrapMethods.length;
      newUTF8("BootstrapMethods");
    }
    if (this.signature != 0)
    {
      m++;
      i += 8;
      newUTF8("Signature");
    }
    if (this.sourceFile != 0)
    {
      m++;
      i += 8;
      newUTF8("SourceFile");
    }
    if (this.sourceDebug != null)
    {
      m++;
      i += this.sourceDebug.length + 6;
      newUTF8("SourceDebugExtension");
    }
    if (this.enclosingMethodOwner != 0)
    {
      m++;
      i += 10;
      newUTF8("EnclosingMethod");
    }
    if ((this.access & 0x20000) != 0)
    {
      m++;
      i += 6;
      newUTF8("Deprecated");
    }
    if (((this.access & 0x1000) != 0) && (((this.version & 0xFFFF) < 49) || ((this.access & 0x40000) != 0)))
    {
      m++;
      i += 6;
      newUTF8("Synthetic");
    }
    if (this.innerClasses != null)
    {
      m++;
      i += 8 + this.innerClasses.length;
      newUTF8("InnerClasses");
    }
    if (this.anns != null)
    {
      m++;
      i += 8 + this.anns.getSize();
      newUTF8("RuntimeVisibleAnnotations");
    }
    if (this.ianns != null)
    {
      m++;
      i += 8 + this.ianns.getSize();
      newUTF8("RuntimeInvisibleAnnotations");
    }
    if (this.tanns != null)
    {
      m++;
      i += 8 + this.tanns.getSize();
      newUTF8("RuntimeVisibleTypeAnnotations");
    }
    if (this.itanns != null)
    {
      m++;
      i += 8 + this.itanns.getSize();
      newUTF8("RuntimeInvisibleTypeAnnotations");
    }
    if (this.attrs != null)
    {
      m += this.attrs.getCount();
      i += this.attrs.getSize(this, null, 0, -1, -1);
    }
    i += this.pool.length;
    ByteVector localByteVector = new ByteVector(i);
    localByteVector.putInt(-889275714).putInt(this.version);
    localByteVector.putShort(this.index).putByteArray(this.pool.data, 0, this.pool.length);
    int n = 0x60000 | (this.access & 0x40000) / 64;
    localByteVector.putShort(this.access & (n ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.superName);
    localByteVector.putShort(this.interfaceCount);
    for (int i1 = 0; i1 < this.interfaceCount; i1++) {
      localByteVector.putShort(this.interfaces[i1]);
    }
    localByteVector.putShort(j);
    for (localFieldWriter = this.firstField; localFieldWriter != null; localFieldWriter = (FieldWriter)localFieldWriter.fv) {
      localFieldWriter.put(localByteVector);
    }
    localByteVector.putShort(k);
    for (localMethodWriter = this.firstMethod; localMethodWriter != null; localMethodWriter = (MethodWriter)localMethodWriter.mv) {
      localMethodWriter.put(localByteVector);
    }
    localByteVector.putShort(m);
    if (this.bootstrapMethods != null)
    {
      localByteVector.putShort(newUTF8("BootstrapMethods"));
      localByteVector.putInt(this.bootstrapMethods.length + 2).putShort(this.bootstrapMethodsCount);
      localByteVector.putByteArray(this.bootstrapMethods.data, 0, this.bootstrapMethods.length);
    }
    if (this.signature != 0) {
      localByteVector.putShort(newUTF8("Signature")).putInt(2).putShort(this.signature);
    }
    if (this.sourceFile != 0) {
      localByteVector.putShort(newUTF8("SourceFile")).putInt(2).putShort(this.sourceFile);
    }
    if (this.sourceDebug != null)
    {
      i1 = this.sourceDebug.length;
      localByteVector.putShort(newUTF8("SourceDebugExtension")).putInt(i1);
      localByteVector.putByteArray(this.sourceDebug.data, 0, i1);
    }
    if (this.enclosingMethodOwner != 0)
    {
      localByteVector.putShort(newUTF8("EnclosingMethod")).putInt(4);
      localByteVector.putShort(this.enclosingMethodOwner).putShort(this.enclosingMethod);
    }
    if ((this.access & 0x20000) != 0) {
      localByteVector.putShort(newUTF8("Deprecated")).putInt(0);
    }
    if (((this.access & 0x1000) != 0) && (((this.version & 0xFFFF) < 49) || ((this.access & 0x40000) != 0))) {
      localByteVector.putShort(newUTF8("Synthetic")).putInt(0);
    }
    if (this.innerClasses != null)
    {
      localByteVector.putShort(newUTF8("InnerClasses"));
      localByteVector.putInt(this.innerClasses.length + 2).putShort(this.innerClassesCount);
      localByteVector.putByteArray(this.innerClasses.data, 0, this.innerClasses.length);
    }
    if (this.anns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(localByteVector);
    }
    if (this.ianns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(localByteVector);
    }
    if (this.tanns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeVisibleTypeAnnotations"));
      this.tanns.put(localByteVector);
    }
    if (this.itanns != null)
    {
      localByteVector.putShort(newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.itanns.put(localByteVector);
    }
    if (this.attrs != null) {
      this.attrs.put(this, null, 0, -1, -1, localByteVector);
    }
    if (this.invalidFrames)
    {
      this.anns = null;
      this.ianns = null;
      this.attrs = null;
      this.innerClassesCount = 0;
      this.innerClasses = null;
      this.bootstrapMethodsCount = 0;
      this.bootstrapMethods = null;
      this.firstField = null;
      this.lastField = null;
      this.firstMethod = null;
      this.lastMethod = null;
      this.computeMaxs = false;
      this.computeFrames = true;
      this.invalidFrames = false;
      new ClassReader(localByteVector.data).accept(this, 4);
      return toByteArray();
    }
    return localByteVector.data;
  }
  
  Item newConstItem(Object paramObject)
  {
    int i;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Byte))
    {
      i = ((Byte)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Character))
    {
      i = ((Character)paramObject).charValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Short))
    {
      i = ((Short)paramObject).intValue();
      return newInteger(i);
    }
    if ((paramObject instanceof Boolean))
    {
      i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      return newInteger(i);
    }
    if ((paramObject instanceof Float))
    {
      float f = ((Float)paramObject).floatValue();
      return newFloat(f);
    }
    if ((paramObject instanceof Long))
    {
      long l = ((Long)paramObject).longValue();
      return newLong(l);
    }
    if ((paramObject instanceof Double))
    {
      double d = ((Double)paramObject).doubleValue();
      return newDouble(d);
    }
    if ((paramObject instanceof String)) {
      return newString((String)paramObject);
    }
    Object localObject;
    if ((paramObject instanceof Type))
    {
      localObject = (Type)paramObject;
      int j = ((Type)localObject).getSort();
      if (j == 10) {
        return newClassItem(((Type)localObject).getInternalName());
      }
      if (j == 11) {
        return newMethodTypeItem(((Type)localObject).getDescriptor());
      }
      return newClassItem(((Type)localObject).getDescriptor());
    }
    if ((paramObject instanceof Handle))
    {
      localObject = (Handle)paramObject;
      return newHandleItem(((Handle)localObject).tag, ((Handle)localObject).owner, ((Handle)localObject).name, ((Handle)localObject).desc);
    }
    throw new IllegalArgumentException("value " + paramObject);
  }
  
  public int newConst(Object paramObject)
  {
    return newConstItem(paramObject).index;
  }
  
  public int newUTF8(String paramString)
  {
    this.key.set(1, paramString, null, null);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(1).putUTF8(paramString);
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem.index;
  }
  
  Item newClassItem(String paramString)
  {
    this.key2.set(7, paramString, null, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      this.pool.put12(7, newUTF8(paramString));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newClass(String paramString)
  {
    return newClassItem(paramString).index;
  }
  
  Item newMethodTypeItem(String paramString)
  {
    this.key2.set(16, paramString, null, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      this.pool.put12(16, newUTF8(paramString));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newMethodType(String paramString)
  {
    return newMethodTypeItem(paramString).index;
  }
  
  Item newHandleItem(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.key4.set(20 + paramInt, paramString1, paramString2, paramString3);
    Item localItem = get(this.key4);
    if (localItem == null)
    {
      if (paramInt <= 4) {
        put112(15, paramInt, newField(paramString1, paramString2, paramString3));
      } else {
        put112(15, paramInt, newMethod(paramString1, paramString2, paramString3, paramInt == 9));
      }
      localItem = new Item(this.index++, this.key4);
      put(localItem);
    }
    return localItem;
  }
  
  public int newHandle(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    return newHandleItem(paramInt, paramString1, paramString2, paramString3).index;
  }
  
  Item newInvokeDynamicItem(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    ByteVector localByteVector = this.bootstrapMethods;
    if (localByteVector == null) {
      localByteVector = this.bootstrapMethods = new ByteVector();
    }
    int i = localByteVector.length;
    int j = paramHandle.hashCode();
    localByteVector.putShort(newHandle(paramHandle.tag, paramHandle.owner, paramHandle.name, paramHandle.desc));
    int k = paramVarArgs.length;
    localByteVector.putShort(k);
    for (int m = 0; m < k; m++)
    {
      Object localObject = paramVarArgs[m];
      j ^= localObject.hashCode();
      localByteVector.putShort(newConst(localObject));
    }
    byte[] arrayOfByte = localByteVector.data;
    int n = 2 + k << 1;
    j &= 0x7FFFFFFF;
    Item localItem = this.items[(j % this.items.length)];
    int i1;
    label246:
    while (localItem != null) {
      if ((localItem.type != 33) || (localItem.hashCode != j))
      {
        localItem = localItem.next;
      }
      else
      {
        i1 = localItem.intVal;
        for (int i2 = 0;; i2++)
        {
          if (i2 >= n) {
            break label246;
          }
          if (arrayOfByte[(i + i2)] != arrayOfByte[(i1 + i2)])
          {
            localItem = localItem.next;
            break;
          }
        }
      }
    }
    if (localItem != null)
    {
      i1 = localItem.index;
      localByteVector.length = i;
    }
    else
    {
      i1 = this.bootstrapMethodsCount++;
      localItem = new Item(i1);
      localItem.set(i, j);
      put(localItem);
    }
    this.key3.set(paramString1, paramString2, i1);
    localItem = get(this.key3);
    if (localItem == null)
    {
      put122(18, i1, newNameType(paramString1, paramString2));
      localItem = new Item(this.index++, this.key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newInvokeDynamic(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    return newInvokeDynamicItem(paramString1, paramString2, paramHandle, paramVarArgs).index;
  }
  
  Item newFieldItem(String paramString1, String paramString2, String paramString3)
  {
    this.key3.set(9, paramString1, paramString2, paramString3);
    Item localItem = get(this.key3);
    if (localItem == null)
    {
      put122(9, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(this.index++, this.key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newField(String paramString1, String paramString2, String paramString3)
  {
    return newFieldItem(paramString1, paramString2, paramString3).index;
  }
  
  Item newMethodItem(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    int i = paramBoolean ? 11 : 10;
    this.key3.set(i, paramString1, paramString2, paramString3);
    Item localItem = get(this.key3);
    if (localItem == null)
    {
      put122(i, newClass(paramString1), newNameType(paramString2, paramString3));
      localItem = new Item(this.index++, this.key3);
      put(localItem);
    }
    return localItem;
  }
  
  public int newMethod(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return newMethodItem(paramString1, paramString2, paramString3, paramBoolean).index;
  }
  
  Item newInteger(int paramInt)
  {
    this.key.set(paramInt);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(3).putInt(paramInt);
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem;
  }
  
  Item newFloat(float paramFloat)
  {
    this.key.set(paramFloat);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(4).putInt(this.key.intVal);
      localItem = new Item(this.index++, this.key);
      put(localItem);
    }
    return localItem;
  }
  
  Item newLong(long paramLong)
  {
    this.key.set(paramLong);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(5).putLong(paramLong);
      localItem = new Item(this.index, this.key);
      this.index += 2;
      put(localItem);
    }
    return localItem;
  }
  
  Item newDouble(double paramDouble)
  {
    this.key.set(paramDouble);
    Item localItem = get(this.key);
    if (localItem == null)
    {
      this.pool.putByte(6).putLong(this.key.longVal);
      localItem = new Item(this.index, this.key);
      this.index += 2;
      put(localItem);
    }
    return localItem;
  }
  
  private Item newString(String paramString)
  {
    this.key2.set(8, paramString, null, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      this.pool.put12(8, newUTF8(paramString));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }
  
  public int newNameType(String paramString1, String paramString2)
  {
    return newNameTypeItem(paramString1, paramString2).index;
  }
  
  Item newNameTypeItem(String paramString1, String paramString2)
  {
    this.key2.set(12, paramString1, paramString2, null);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      put122(12, newUTF8(paramString1), newUTF8(paramString2));
      localItem = new Item(this.index++, this.key2);
      put(localItem);
    }
    return localItem;
  }
  
  int addType(String paramString)
  {
    this.key.set(30, paramString, null, null);
    Item localItem = get(this.key);
    if (localItem == null) {
      localItem = addType(this.key);
    }
    return localItem.index;
  }
  
  int addUninitializedType(String paramString, int paramInt)
  {
    this.key.type = 31;
    this.key.intVal = paramInt;
    this.key.strVal1 = paramString;
    this.key.hashCode = (0x7FFFFFFF & 31 + paramString.hashCode() + paramInt);
    Item localItem = get(this.key);
    if (localItem == null) {
      localItem = addType(this.key);
    }
    return localItem.index;
  }
  
  private Item addType(Item paramItem)
  {
    this.typeCount = ((short)(this.typeCount + 1));
    Item localItem = new Item(this.typeCount, this.key);
    put(localItem);
    if (this.typeTable == null) {
      this.typeTable = new Item[16];
    }
    if (this.typeCount == this.typeTable.length)
    {
      Item[] arrayOfItem = new Item[2 * this.typeTable.length];
      System.arraycopy(this.typeTable, 0, arrayOfItem, 0, this.typeTable.length);
      this.typeTable = arrayOfItem;
    }
    this.typeTable[this.typeCount] = localItem;
    return localItem;
  }
  
  int getMergedType(int paramInt1, int paramInt2)
  {
    this.key2.type = 32;
    this.key2.longVal = (paramInt1 | paramInt2 << 32);
    this.key2.hashCode = (0x7FFFFFFF & 32 + paramInt1 + paramInt2);
    Item localItem = get(this.key2);
    if (localItem == null)
    {
      String str1 = this.typeTable[paramInt1].strVal1;
      String str2 = this.typeTable[paramInt2].strVal1;
      this.key2.intVal = addType(getCommonSuperClass(str1, str2));
      localItem = new Item(0, this.key2);
      put(localItem);
    }
    return localItem.intVal;
  }
  
  protected String getCommonSuperClass(String paramString1, String paramString2)
  {
    ClassLoader localClassLoader = getClass().getClassLoader();
    Class localClass1;
    Class localClass2;
    try
    {
      localClass1 = Class.forName(paramString1.replace('/', '.'), false, localClassLoader);
      localClass2 = Class.forName(paramString2.replace('/', '.'), false, localClassLoader);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException.toString());
    }
    if (localClass1.isAssignableFrom(localClass2)) {
      return paramString1;
    }
    if (localClass2.isAssignableFrom(localClass1)) {
      return paramString2;
    }
    if ((localClass1.isInterface()) || (localClass2.isInterface())) {
      return "java/lang/Object";
    }
    do
    {
      localClass1 = localClass1.getSuperclass();
    } while (!localClass1.isAssignableFrom(localClass2));
    return localClass1.getName().replace('.', '/');
  }
  
  private Item get(Item paramItem)
  {
    for (Item localItem = this.items[(paramItem.hashCode % this.items.length)]; (localItem != null) && ((localItem.type != paramItem.type) || (!paramItem.isEqualTo(localItem))); localItem = localItem.next) {}
    return localItem;
  }
  
  private void put(Item paramItem)
  {
    if (this.index + this.typeCount > this.threshold)
    {
      i = this.items.length;
      int j = i * 2 + 1;
      Item[] arrayOfItem = new Item[j];
      for (int k = i - 1; k >= 0; k--)
      {
        Item localItem;
        for (Object localObject = this.items[k]; localObject != null; localObject = localItem)
        {
          int m = ((Item)localObject).hashCode % arrayOfItem.length;
          localItem = ((Item)localObject).next;
          ((Item)localObject).next = arrayOfItem[m];
          arrayOfItem[m] = localObject;
        }
      }
      this.items = arrayOfItem;
      this.threshold = ((int)(j * 0.75D));
    }
    int i = paramItem.hashCode % this.items.length;
    paramItem.next = this.items[i];
    this.items[i] = paramItem;
  }
  
  private void put122(int paramInt1, int paramInt2, int paramInt3)
  {
    this.pool.put12(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  private void put112(int paramInt1, int paramInt2, int paramInt3)
  {
    this.pool.put11(paramInt1, paramInt2).putShort(paramInt3);
  }
  
  static
  {
    byte[] arrayOfByte = new byte['Ü'];
    String str = "AAAAAAAAAAAAAAAABCLMMDDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAADDDDDEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANAAAAAAAAAAAAAAAAAAAAJJJJJJJJJJJJJJJJDOPAAAAAAGGGGGGGHIFBFAAFFAARQJJKKJJJJJJJJJJJJJJJJJJ";
    for (int i = 0; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = ((byte)(str.charAt(i) - 'A'));
    }
    TYPE = arrayOfByte;
  }
}
