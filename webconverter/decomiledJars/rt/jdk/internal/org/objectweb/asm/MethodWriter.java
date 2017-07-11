package jdk.internal.org.objectweb.asm;

class MethodWriter
  extends MethodVisitor
{
  static final int ACC_CONSTRUCTOR = 524288;
  static final int SAME_FRAME = 0;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  static final int RESERVED = 128;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  static final int CHOP_FRAME = 248;
  static final int SAME_FRAME_EXTENDED = 251;
  static final int APPEND_FRAME = 252;
  static final int FULL_FRAME = 255;
  private static final int FRAMES = 0;
  private static final int MAXS = 1;
  private static final int NOTHING = 2;
  final ClassWriter cw;
  private int access;
  private final int name;
  private final int desc;
  private final String descriptor;
  String signature;
  int classReaderOffset;
  int classReaderLength;
  int exceptionCount;
  int[] exceptions;
  private ByteVector annd;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private AnnotationWriter tanns;
  private AnnotationWriter itanns;
  private AnnotationWriter[] panns;
  private AnnotationWriter[] ipanns;
  private int synthetics;
  private Attribute attrs;
  private ByteVector code = new ByteVector();
  private int maxStack;
  private int maxLocals;
  private int currentLocals;
  private int frameCount;
  private ByteVector stackMap;
  private int previousFrameOffset;
  private int[] previousFrame;
  private int[] frame;
  private int handlerCount;
  private Handler firstHandler;
  private Handler lastHandler;
  private int methodParametersCount;
  private ByteVector methodParameters;
  private int localVarCount;
  private ByteVector localVar;
  private int localVarTypeCount;
  private ByteVector localVarType;
  private int lineNumberCount;
  private ByteVector lineNumber;
  private int lastCodeOffset;
  private AnnotationWriter ctanns;
  private AnnotationWriter ictanns;
  private Attribute cattrs;
  private boolean resize;
  private int subroutines;
  private final int compute;
  private Label labels;
  private Label previousBlock;
  private Label currentBlock;
  private int stackSize;
  private int maxStackSize;
  
  MethodWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(327680);
    if (paramClassWriter.firstMethod == null) {
      paramClassWriter.firstMethod = this;
    } else {
      paramClassWriter.lastMethod.mv = this;
    }
    paramClassWriter.lastMethod = this;
    this.cw = paramClassWriter;
    this.access = paramInt;
    if ("<init>".equals(paramString1)) {
      this.access |= 0x80000;
    }
    this.name = paramClassWriter.newUTF8(paramString1);
    this.desc = paramClassWriter.newUTF8(paramString2);
    this.descriptor = paramString2;
    this.signature = paramString3;
    int i;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      this.exceptionCount = paramArrayOfString.length;
      this.exceptions = new int[this.exceptionCount];
      for (i = 0; i < this.exceptionCount; i++) {
        this.exceptions[i] = paramClassWriter.newClass(paramArrayOfString[i]);
      }
    }
    this.compute = (paramBoolean1 ? 1 : paramBoolean2 ? 0 : 2);
    if ((paramBoolean1) || (paramBoolean2))
    {
      i = Type.getArgumentsAndReturnSizes(this.descriptor) >> 2;
      if ((paramInt & 0x8) != 0) {
        i--;
      }
      this.maxLocals = i;
      this.currentLocals = i;
      this.labels = new Label();
      this.labels.status |= 0x8;
      visitLabel(this.labels);
    }
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (this.methodParameters == null) {
      this.methodParameters = new ByteVector();
    }
    this.methodParametersCount += 1;
    this.methodParameters.putShort(paramString == null ? 0 : this.cw.newUTF8(paramString)).putShort(paramInt);
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    this.annd = new ByteVector();
    return new AnnotationWriter(this.cw, false, this.annd, null, 0);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, 2);
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
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, localByteVector.length - 2);
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
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    if ("Ljava/lang/Synthetic;".equals(paramString))
    {
      this.synthetics = Math.max(this.synthetics, paramInt + 1);
      return new AnnotationWriter(this.cw, false, localByteVector, null, 0);
    }
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      if (this.panns == null) {
        this.panns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
      }
      localAnnotationWriter.next = this.panns[paramInt];
      this.panns[paramInt] = localAnnotationWriter;
    }
    else
    {
      if (this.ipanns == null) {
        this.ipanns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length];
      }
      localAnnotationWriter.next = this.ipanns[paramInt];
      this.ipanns[paramInt] = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (paramAttribute.isCodeAttribute())
    {
      paramAttribute.next = this.cattrs;
      this.cattrs = paramAttribute;
    }
    else
    {
      paramAttribute.next = this.attrs;
      this.attrs = paramAttribute;
    }
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (this.compute == 0) {
      return;
    }
    int i;
    int j;
    if (paramInt1 == -1)
    {
      if (this.previousFrame == null) {
        visitImplicitFirstFrame();
      }
      this.currentLocals = paramInt2;
      i = startFrame(this.code.length, paramInt2, paramInt3);
      for (j = 0; j < paramInt2; j++) {
        if ((paramArrayOfObject1[j] instanceof String)) {
          this.frame[(i++)] = (0x1700000 | this.cw.addType((String)paramArrayOfObject1[j]));
        } else if ((paramArrayOfObject1[j] instanceof Integer)) {
          this.frame[(i++)] = ((Integer)paramArrayOfObject1[j]).intValue();
        } else {
          this.frame[(i++)] = (0x1800000 | this.cw.addUninitializedType("", ((Label)paramArrayOfObject1[j]).position));
        }
      }
      for (j = 0; j < paramInt3; j++) {
        if ((paramArrayOfObject2[j] instanceof String)) {
          this.frame[(i++)] = (0x1700000 | this.cw.addType((String)paramArrayOfObject2[j]));
        } else if ((paramArrayOfObject2[j] instanceof Integer)) {
          this.frame[(i++)] = ((Integer)paramArrayOfObject2[j]).intValue();
        } else {
          this.frame[(i++)] = (0x1800000 | this.cw.addUninitializedType("", ((Label)paramArrayOfObject2[j]).position));
        }
      }
      endFrame();
    }
    else
    {
      if (this.stackMap == null)
      {
        this.stackMap = new ByteVector();
        i = this.code.length;
      }
      else
      {
        i = this.code.length - this.previousFrameOffset - 1;
        if (i < 0)
        {
          if (paramInt1 == 3) {
            return;
          }
          throw new IllegalStateException();
        }
      }
      switch (paramInt1)
      {
      case 0: 
        this.currentLocals = paramInt2;
        this.stackMap.putByte(255).putShort(i).putShort(paramInt2);
        for (j = 0; j < paramInt2; j++) {
          writeFrameType(paramArrayOfObject1[j]);
        }
        this.stackMap.putShort(paramInt3);
        for (j = 0; j < paramInt3; j++) {
          writeFrameType(paramArrayOfObject2[j]);
        }
        break;
      case 1: 
        this.currentLocals += paramInt2;
        this.stackMap.putByte(251 + paramInt2).putShort(i);
        for (j = 0; j < paramInt2; j++) {
          writeFrameType(paramArrayOfObject1[j]);
        }
        break;
      case 2: 
        this.currentLocals -= paramInt2;
        this.stackMap.putByte(251 - paramInt2).putShort(i);
        break;
      case 3: 
        if (i < 64) {
          this.stackMap.putByte(i);
        } else {
          this.stackMap.putByte(251).putShort(i);
        }
        break;
      case 4: 
        if (i < 64) {
          this.stackMap.putByte(64 + i);
        } else {
          this.stackMap.putByte(247).putShort(i);
        }
        writeFrameType(paramArrayOfObject2[0]);
      }
      this.previousFrameOffset = this.code.length;
      this.frameCount += 1;
    }
    this.maxStack = Math.max(this.maxStack, paramInt3);
    this.maxLocals = Math.max(this.maxLocals, this.currentLocals);
  }
  
  public void visitInsn(int paramInt)
  {
    this.lastCodeOffset = this.code.length;
    this.code.putByte(paramInt);
    if (this.currentBlock != null)
    {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
      }
      else
      {
        int i = this.stackSize + Frame.SIZE[paramInt];
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
      if (((paramInt >= 172) && (paramInt <= 177)) || (paramInt == 191)) {
        noSuccessor();
      }
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    this.lastCodeOffset = this.code.length;
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 != 188)
      {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
    }
    if (paramInt1 == 17) {
      this.code.put12(paramInt1, paramInt2);
    } else {
      this.code.put11(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    this.lastCodeOffset = this.code.length;
    int i;
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 == 169)
      {
        this.currentBlock.status |= 0x100;
        this.currentBlock.inputStackTop = this.stackSize;
        noSuccessor();
      }
      else
      {
        i = this.stackSize + Frame.SIZE[paramInt1];
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
    }
    if (this.compute != 2)
    {
      if ((paramInt1 == 22) || (paramInt1 == 24) || (paramInt1 == 55) || (paramInt1 == 57)) {
        i = paramInt2 + 2;
      } else {
        i = paramInt2 + 1;
      }
      if (i > this.maxLocals) {
        this.maxLocals = i;
      }
    }
    if ((paramInt2 < 4) && (paramInt1 != 169))
    {
      if (paramInt1 < 54) {
        i = 26 + (paramInt1 - 21 << 2) + paramInt2;
      } else {
        i = 59 + (paramInt1 - 54 << 2) + paramInt2;
      }
      this.code.putByte(i);
    }
    else if (paramInt2 >= 256)
    {
      this.code.putByte(196).put12(paramInt1, paramInt2);
    }
    else
    {
      this.code.put11(paramInt1, paramInt2);
    }
    if ((paramInt1 >= 54) && (this.compute == 0) && (this.handlerCount > 0)) {
      visitLabel(new Label());
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newClassItem(paramString);
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt, this.code.length, this.cw, localItem);
      }
      else if (paramInt == 187)
      {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
    }
    this.code.put12(paramInt, localItem.index);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newFieldItem(paramString1, paramString2, paramString3);
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, localItem);
      }
      else
      {
        int j = paramString3.charAt(0);
        int i;
        switch (paramInt)
        {
        case 178: 
          i = this.stackSize + ((j == 68) || (j == 74) ? 2 : 1);
          break;
        case 179: 
          i = this.stackSize + ((j == 68) || (j == 74) ? -2 : -1);
          break;
        case 180: 
          i = this.stackSize + ((j == 68) || (j == 74) ? 1 : 0);
          break;
        default: 
          i = this.stackSize + ((j == 68) || (j == 74) ? -3 : -2);
        }
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
    }
    this.code.put12(paramInt, localItem.index);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newMethodItem(paramString1, paramString2, paramString3, paramBoolean);
    int i = localItem.intVal;
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, localItem);
      }
      else
      {
        if (i == 0)
        {
          i = Type.getArgumentsAndReturnSizes(paramString3);
          localItem.intVal = i;
        }
        int j;
        if (paramInt == 184) {
          j = this.stackSize - (i >> 2) + (i & 0x3) + 1;
        } else {
          j = this.stackSize - (i >> 2) + (i & 0x3);
        }
        if (j > this.maxStackSize) {
          this.maxStackSize = j;
        }
        this.stackSize = j;
      }
    }
    if (paramInt == 185)
    {
      if (i == 0)
      {
        i = Type.getArgumentsAndReturnSizes(paramString3);
        localItem.intVal = i;
      }
      this.code.put12(185, localItem.index).put11(i >> 2, 0);
    }
    else
    {
      this.code.put12(paramInt, localItem.index);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newInvokeDynamicItem(paramString1, paramString2, paramHandle, paramVarArgs);
    int i = localItem.intVal;
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(186, 0, this.cw, localItem);
      }
      else
      {
        if (i == 0)
        {
          i = Type.getArgumentsAndReturnSizes(paramString2);
          localItem.intVal = i;
        }
        int j = this.stackSize - (i >> 2) + (i & 0x3) + 1;
        if (j > this.maxStackSize) {
          this.maxStackSize = j;
        }
        this.stackSize = j;
      }
    }
    this.code.put12(186, localItem.index);
    this.code.putShort(0);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    this.lastCodeOffset = this.code.length;
    Label localLabel = null;
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
        paramLabel.getFirst().status |= 0x10;
        addSuccessor(0, paramLabel);
        if (paramInt != 167) {
          localLabel = new Label();
        }
      }
      else if (paramInt == 168)
      {
        if ((paramLabel.status & 0x200) == 0)
        {
          paramLabel.status |= 0x200;
          this.subroutines += 1;
        }
        this.currentBlock.status |= 0x80;
        addSuccessor(this.stackSize + 1, paramLabel);
        localLabel = new Label();
      }
      else
      {
        this.stackSize += Frame.SIZE[paramInt];
        addSuccessor(this.stackSize, paramLabel);
      }
    }
    if (((paramLabel.status & 0x2) != 0) && (paramLabel.position - this.code.length < 32768))
    {
      if (paramInt == 167)
      {
        this.code.putByte(200);
      }
      else if (paramInt == 168)
      {
        this.code.putByte(201);
      }
      else
      {
        if (localLabel != null) {
          localLabel.status |= 0x10;
        }
        this.code.putByte(paramInt <= 166 ? (paramInt + 1 ^ 0x1) - 1 : paramInt ^ 0x1);
        this.code.putShort(8);
        this.code.putByte(200);
      }
      paramLabel.put(this, this.code, this.code.length - 1, true);
    }
    else
    {
      this.code.putByte(paramInt);
      paramLabel.put(this, this.code, this.code.length - 1, false);
    }
    if (this.currentBlock != null)
    {
      if (localLabel != null) {
        visitLabel(localLabel);
      }
      if (paramInt == 167) {
        noSuccessor();
      }
    }
  }
  
  public void visitLabel(Label paramLabel)
  {
    this.resize |= paramLabel.resolve(this, this.code.length, this.code.data);
    if ((paramLabel.status & 0x1) != 0) {
      return;
    }
    if (this.compute == 0)
    {
      if (this.currentBlock != null)
      {
        if (paramLabel.position == this.currentBlock.position)
        {
          this.currentBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.currentBlock.frame;
          return;
        }
        addSuccessor(0, paramLabel);
      }
      this.currentBlock = paramLabel;
      if (paramLabel.frame == null)
      {
        paramLabel.frame = new Frame();
        paramLabel.frame.owner = paramLabel;
      }
      if (this.previousBlock != null)
      {
        if (paramLabel.position == this.previousBlock.position)
        {
          this.previousBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.previousBlock.frame;
          this.currentBlock = this.previousBlock;
          return;
        }
        this.previousBlock.successor = paramLabel;
      }
      this.previousBlock = paramLabel;
    }
    else if (this.compute == 1)
    {
      if (this.currentBlock != null)
      {
        this.currentBlock.outputStackMax = this.maxStackSize;
        addSuccessor(this.stackSize, paramLabel);
      }
      this.currentBlock = paramLabel;
      this.stackSize = 0;
      this.maxStackSize = 0;
      if (this.previousBlock != null) {
        this.previousBlock.successor = paramLabel;
      }
      this.previousBlock = paramLabel;
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newConstItem(paramObject);
    if (this.currentBlock != null) {
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(18, 0, this.cw, localItem);
      }
      else
      {
        if ((localItem.type == 5) || (localItem.type == 6)) {
          i = this.stackSize + 2;
        } else {
          i = this.stackSize + 1;
        }
        if (i > this.maxStackSize) {
          this.maxStackSize = i;
        }
        this.stackSize = i;
      }
    }
    int i = localItem.index;
    if ((localItem.type == 5) || (localItem.type == 6)) {
      this.code.put12(20, i);
    } else if (i >= 256) {
      this.code.put12(19, i);
    } else {
      this.code.put11(18, i);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    this.lastCodeOffset = this.code.length;
    if ((this.currentBlock != null) && (this.compute == 0)) {
      this.currentBlock.frame.execute(132, paramInt1, null, null);
    }
    if (this.compute != 2)
    {
      int i = paramInt1 + 1;
      if (i > this.maxLocals) {
        this.maxLocals = i;
      }
    }
    if ((paramInt1 > 255) || (paramInt2 > 127) || (paramInt2 < -128)) {
      this.code.putByte(196).put12(132, paramInt1).putShort(paramInt2);
    } else {
      this.code.putByte(132).put11(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    this.lastCodeOffset = this.code.length;
    int i = this.code.length;
    this.code.putByte(170);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramInt1).putInt(paramInt2);
    for (int j = 0; j < paramVarArgs.length; j++) {
      paramVarArgs[j].put(this, this.code, i, true);
    }
    visitSwitchInsn(paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    this.lastCodeOffset = this.code.length;
    int i = this.code.length;
    this.code.putByte(171);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramArrayOfLabel.length);
    for (int j = 0; j < paramArrayOfLabel.length; j++)
    {
      this.code.putInt(paramArrayOfInt[j]);
      paramArrayOfLabel[j].put(this, this.code, i, true);
    }
    visitSwitchInsn(paramLabel, paramArrayOfLabel);
  }
  
  private void visitSwitchInsn(Label paramLabel, Label[] paramArrayOfLabel)
  {
    if (this.currentBlock != null)
    {
      int i;
      if (this.compute == 0)
      {
        this.currentBlock.frame.execute(171, 0, null, null);
        addSuccessor(0, paramLabel);
        paramLabel.getFirst().status |= 0x10;
        for (i = 0; i < paramArrayOfLabel.length; i++)
        {
          addSuccessor(0, paramArrayOfLabel[i]);
          paramArrayOfLabel[i].getFirst().status |= 0x10;
        }
      }
      else
      {
        this.stackSize -= 1;
        addSuccessor(this.stackSize, paramLabel);
        for (i = 0; i < paramArrayOfLabel.length; i++) {
          addSuccessor(this.stackSize, paramArrayOfLabel[i]);
        }
      }
      noSuccessor();
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    this.lastCodeOffset = this.code.length;
    Item localItem = this.cw.newClassItem(paramString);
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(197, paramInt, this.cw, localItem);
      } else {
        this.stackSize += 1 - paramInt;
      }
    }
    this.code.put12(197, localItem.index).putByte(paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    paramInt = paramInt & 0xFF0000FF | this.lastCodeOffset << 8;
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, localByteVector.length - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.next = this.ctanns;
      this.ctanns = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.next = this.ictanns;
      this.ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    this.handlerCount += 1;
    Handler localHandler = new Handler();
    localHandler.start = paramLabel1;
    localHandler.end = paramLabel2;
    localHandler.handler = paramLabel3;
    localHandler.desc = paramString;
    localHandler.type = (paramString != null ? this.cw.newClass(paramString) : 0);
    if (this.lastHandler == null) {
      this.firstHandler = localHandler;
    } else {
      this.lastHandler.next = localHandler;
    }
    this.lastHandler = localHandler;
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, localByteVector.length - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.next = this.ctanns;
      this.ctanns = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.next = this.ictanns;
      this.ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    if (paramString3 != null)
    {
      if (this.localVarType == null) {
        this.localVarType = new ByteVector();
      }
      this.localVarTypeCount += 1;
      this.localVarType.putShort(paramLabel1.position).putShort(paramLabel2.position - paramLabel1.position).putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString3)).putShort(paramInt);
    }
    if (this.localVar == null) {
      this.localVar = new ByteVector();
    }
    this.localVarCount += 1;
    this.localVar.putShort(paramLabel1.position).putShort(paramLabel2.position - paramLabel1.position).putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString2)).putShort(paramInt);
    if (this.compute != 2)
    {
      int i = paramString2.charAt(0);
      int j = paramInt + ((i == 74) || (i == 68) ? 2 : 1);
      if (j > this.maxLocals) {
        this.maxLocals = j;
      }
    }
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putByte(paramInt >>> 24).putShort(paramArrayOfLabel1.length);
    for (int i = 0; i < paramArrayOfLabel1.length; i++) {
      localByteVector.putShort(paramArrayOfLabel1[i].position).putShort(paramArrayOfLabel2[i].position - paramArrayOfLabel1[i].position).putShort(paramArrayOfInt[i]);
    }
    if (paramTypePath == null)
    {
      localByteVector.putByte(0);
    }
    else
    {
      i = paramTypePath.b[paramTypePath.offset] * 2 + 1;
      localByteVector.putByteArray(paramTypePath.b, paramTypePath.offset, i);
    }
    localByteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(this.cw, true, localByteVector, localByteVector, localByteVector.length - 2);
    if (paramBoolean)
    {
      localAnnotationWriter.next = this.ctanns;
      this.ctanns = localAnnotationWriter;
    }
    else
    {
      localAnnotationWriter.next = this.ictanns;
      this.ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    if (this.lineNumber == null) {
      this.lineNumber = new ByteVector();
    }
    this.lineNumberCount += 1;
    this.lineNumber.putShort(paramLabel.position);
    this.lineNumber.putShort(paramInt);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (this.resize) {
      resizeInstructions();
    }
    Handler localHandler;
    Object localObject1;
    Object localObject2;
    Object localObject4;
    Object localObject6;
    if (this.compute == 0)
    {
      for (localHandler = this.firstHandler; localHandler != null; localHandler = localHandler.next)
      {
        localObject1 = localHandler.start.getFirst();
        localObject2 = localHandler.handler.getFirst();
        Label localLabel1 = localHandler.end.getFirst();
        localObject4 = localHandler.desc == null ? "java/lang/Throwable" : localHandler.desc;
        int m = 0x1700000 | this.cw.addType((String)localObject4);
        localObject2.status |= 0x10;
        while (localObject1 != localLabel1)
        {
          Edge localEdge1 = new Edge();
          localEdge1.info = m;
          localEdge1.successor = ((Label)localObject2);
          localEdge1.next = ((Label)localObject1).successors;
          ((Label)localObject1).successors = localEdge1;
          localObject1 = ((Label)localObject1).successor;
        }
      }
      localObject1 = this.labels.frame;
      localObject2 = Type.getArgumentTypes(this.descriptor);
      ((Frame)localObject1).initInputFrame(this.cw, this.access, (Type[])localObject2, this.maxLocals);
      visitFrame((Frame)localObject1);
      int j = 0;
      localObject4 = this.labels;
      int i4;
      while (localObject4 != null)
      {
        localObject5 = localObject4;
        localObject4 = ((Label)localObject4).next;
        ((Label)localObject5).next = null;
        localObject1 = ((Label)localObject5).frame;
        if ((((Label)localObject5).status & 0x10) != 0) {
          localObject5.status |= 0x20;
        }
        localObject5.status |= 0x40;
        int i1 = ((Frame)localObject1).inputStack.length + ((Label)localObject5).outputStackMax;
        if (i1 > j) {
          j = i1;
        }
        for (Edge localEdge2 = ((Label)localObject5).successors; localEdge2 != null; localEdge2 = localEdge2.next)
        {
          Label localLabel2 = localEdge2.successor.getFirst();
          i4 = ((Frame)localObject1).merge(this.cw, localLabel2.frame, localEdge2.info);
          if ((i4 != 0) && (localLabel2.next == null))
          {
            localLabel2.next = ((Label)localObject4);
            localObject4 = localLabel2;
          }
        }
      }
      for (Object localObject5 = this.labels; localObject5 != null; localObject5 = ((Label)localObject5).successor)
      {
        localObject1 = ((Label)localObject5).frame;
        if ((((Label)localObject5).status & 0x20) != 0) {
          visitFrame((Frame)localObject1);
        }
        if ((((Label)localObject5).status & 0x40) == 0)
        {
          localObject6 = ((Label)localObject5).successor;
          int i2 = ((Label)localObject5).position;
          int i3 = (localObject6 == null ? this.code.length : ((Label)localObject6).position) - 1;
          if (i3 >= i2)
          {
            j = Math.max(j, 1);
            for (i4 = i2; i4 < i3; i4++) {
              this.code.data[i4] = 0;
            }
            this.code.data[i3] = -65;
            int i5 = startFrame(i2, 0, 1);
            this.frame[i5] = (0x1700000 | this.cw.addType("java/lang/Throwable"));
            endFrame();
            this.firstHandler = Handler.remove(this.firstHandler, (Label)localObject5, (Label)localObject6);
          }
        }
      }
      localHandler = this.firstHandler;
      this.handlerCount = 0;
      while (localHandler != null)
      {
        this.handlerCount += 1;
        localHandler = localHandler.next;
      }
      this.maxStack = j;
    }
    else if (this.compute == 1)
    {
      Object localObject3;
      for (localHandler = this.firstHandler; localHandler != null; localHandler = localHandler.next)
      {
        localObject1 = localHandler.start;
        localObject2 = localHandler.handler;
        localObject3 = localHandler.end;
        while (localObject1 != localObject3)
        {
          localObject4 = new Edge();
          ((Edge)localObject4).info = Integer.MAX_VALUE;
          ((Edge)localObject4).successor = ((Label)localObject2);
          if ((((Label)localObject1).status & 0x80) == 0)
          {
            ((Edge)localObject4).next = ((Label)localObject1).successors;
            ((Label)localObject1).successors = ((Edge)localObject4);
          }
          else
          {
            ((Edge)localObject4).next = ((Label)localObject1).successors.next.next;
            ((Label)localObject1).successors.next.next = ((Edge)localObject4);
          }
          localObject1 = ((Label)localObject1).successor;
        }
      }
      if (this.subroutines > 0)
      {
        i = 0;
        this.labels.visitSubroutine(null, 1L, this.subroutines);
        for (localObject2 = this.labels; localObject2 != null; localObject2 = ((Label)localObject2).successor) {
          if ((((Label)localObject2).status & 0x80) != 0)
          {
            localObject3 = ((Label)localObject2).successors.next.successor;
            if ((((Label)localObject3).status & 0x400) == 0)
            {
              i++;
              ((Label)localObject3).visitSubroutine(null, i / 32L << 32 | 1L << i % 32, this.subroutines);
            }
          }
        }
        for (localObject2 = this.labels; localObject2 != null; localObject2 = ((Label)localObject2).successor) {
          if ((((Label)localObject2).status & 0x80) != 0)
          {
            for (localObject3 = this.labels; localObject3 != null; localObject3 = ((Label)localObject3).successor) {
              localObject3.status &= 0xF7FF;
            }
            localObject4 = ((Label)localObject2).successors.next.successor;
            ((Label)localObject4).visitSubroutine((Label)localObject2, 0L, this.subroutines);
          }
        }
      }
      int i = 0;
      localObject2 = this.labels;
      while (localObject2 != null)
      {
        localObject3 = localObject2;
        localObject2 = ((Label)localObject2).next;
        int k = ((Label)localObject3).inputStackTop;
        int n = k + ((Label)localObject3).outputStackMax;
        if (n > i) {
          i = n;
        }
        localObject6 = ((Label)localObject3).successors;
        if ((((Label)localObject3).status & 0x80) != 0) {}
        for (localObject6 = ((Edge)localObject6).next; localObject6 != null; localObject6 = ((Edge)localObject6).next)
        {
          localObject3 = ((Edge)localObject6).successor;
          if ((((Label)localObject3).status & 0x8) == 0)
          {
            ((Label)localObject3).inputStackTop = (((Edge)localObject6).info == Integer.MAX_VALUE ? 1 : k + ((Edge)localObject6).info);
            localObject3.status |= 0x8;
            ((Label)localObject3).next = ((Label)localObject2);
            localObject2 = localObject3;
          }
        }
      }
      this.maxStack = Math.max(paramInt1, i);
    }
    else
    {
      this.maxStack = paramInt1;
      this.maxLocals = paramInt2;
    }
  }
  
  public void visitEnd() {}
  
  private void addSuccessor(int paramInt, Label paramLabel)
  {
    Edge localEdge = new Edge();
    localEdge.info = paramInt;
    localEdge.successor = paramLabel;
    localEdge.next = this.currentBlock.successors;
    this.currentBlock.successors = localEdge;
  }
  
  private void noSuccessor()
  {
    if (this.compute == 0)
    {
      Label localLabel = new Label();
      localLabel.frame = new Frame();
      localLabel.frame.owner = localLabel;
      localLabel.resolve(this, this.code.length, this.code.data);
      this.previousBlock.successor = localLabel;
      this.previousBlock = localLabel;
    }
    else
    {
      this.currentBlock.outputStackMax = this.maxStackSize;
    }
    this.currentBlock = null;
  }
  
  private void visitFrame(Frame paramFrame)
  {
    int k = 0;
    int m = 0;
    int n = 0;
    int[] arrayOfInt1 = paramFrame.inputLocals;
    int[] arrayOfInt2 = paramFrame.inputStack;
    int j;
    for (int i = 0; i < arrayOfInt1.length; i++)
    {
      j = arrayOfInt1[i];
      if (j == 16777216)
      {
        k++;
      }
      else
      {
        m += k + 1;
        k = 0;
      }
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    for (i = 0; i < arrayOfInt2.length; i++)
    {
      j = arrayOfInt2[i];
      n++;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    int i1 = startFrame(paramFrame.owner.position, m, n);
    i = 0;
    while (m > 0)
    {
      j = arrayOfInt1[i];
      this.frame[(i1++)] = j;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
      i++;
      m--;
    }
    for (i = 0; i < arrayOfInt2.length; i++)
    {
      j = arrayOfInt2[i];
      this.frame[(i1++)] = j;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    endFrame();
  }
  
  private void visitImplicitFirstFrame()
  {
    int i = startFrame(0, this.descriptor.length() + 1, 0);
    if ((this.access & 0x8) == 0) {
      if ((this.access & 0x80000) == 0) {
        this.frame[(i++)] = (0x1700000 | this.cw.addType(this.cw.thisName));
      } else {
        this.frame[(i++)] = 6;
      }
    }
    int j = 1;
    for (;;)
    {
      int k = j;
      switch (this.descriptor.charAt(j++))
      {
      case 'B': 
      case 'C': 
      case 'I': 
      case 'S': 
      case 'Z': 
        this.frame[(i++)] = 1;
        break;
      case 'F': 
        this.frame[(i++)] = 2;
        break;
      case 'J': 
        this.frame[(i++)] = 4;
        break;
      case 'D': 
        this.frame[(i++)] = 3;
        break;
      case '[': 
        while (this.descriptor.charAt(j) == '[') {
          j++;
        }
        if (this.descriptor.charAt(j) == 'L')
        {
          j++;
          while (this.descriptor.charAt(j) != ';') {
            j++;
          }
        }
        this.frame[(i++)] = (0x1700000 | this.cw.addType(this.descriptor.substring(k, ++j)));
        break;
      case 'L': 
        while (this.descriptor.charAt(j) != ';') {
          j++;
        }
        this.frame[(i++)] = (0x1700000 | this.cw.addType(this.descriptor.substring(k + 1, j++)));
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
        break label409;
      }
    }
    label409:
    this.frame[1] = (i - 3);
    endFrame();
  }
  
  private int startFrame(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 3 + paramInt2 + paramInt3;
    if ((this.frame == null) || (this.frame.length < i)) {
      this.frame = new int[i];
    }
    this.frame[0] = paramInt1;
    this.frame[1] = paramInt2;
    this.frame[2] = paramInt3;
    return 3;
  }
  
  private void endFrame()
  {
    if (this.previousFrame != null)
    {
      if (this.stackMap == null) {
        this.stackMap = new ByteVector();
      }
      writeFrame();
      this.frameCount += 1;
    }
    this.previousFrame = this.frame;
    this.frame = null;
  }
  
  private void writeFrame()
  {
    int i = this.frame[1];
    int j = this.frame[2];
    if ((this.cw.version & 0xFFFF) < 50)
    {
      this.stackMap.putShort(this.frame[0]).putShort(i);
      writeFrameTypes(3, 3 + i);
      this.stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
      return;
    }
    int k = this.previousFrame[1];
    int m = 255;
    int n = 0;
    int i1;
    if (this.frameCount == 0) {
      i1 = this.frame[0];
    } else {
      i1 = this.frame[0] - this.previousFrame[0] - 1;
    }
    if (j == 0)
    {
      n = i - k;
      switch (n)
      {
      case -3: 
      case -2: 
      case -1: 
        m = 248;
        k = i;
        break;
      case 0: 
        m = i1 < 64 ? 0 : 251;
        break;
      case 1: 
      case 2: 
      case 3: 
        m = 252;
      }
    }
    else if ((i == k) && (j == 1))
    {
      m = i1 < 63 ? 64 : 247;
    }
    if (m != 255)
    {
      int i2 = 3;
      for (int i3 = 0; i3 < k; i3++)
      {
        if (this.frame[i2] != this.previousFrame[i2])
        {
          m = 255;
          break;
        }
        i2++;
      }
    }
    switch (m)
    {
    case 0: 
      this.stackMap.putByte(i1);
      break;
    case 64: 
      this.stackMap.putByte(64 + i1);
      writeFrameTypes(3 + i, 4 + i);
      break;
    case 247: 
      this.stackMap.putByte(247).putShort(i1);
      writeFrameTypes(3 + i, 4 + i);
      break;
    case 251: 
      this.stackMap.putByte(251).putShort(i1);
      break;
    case 248: 
      this.stackMap.putByte(251 + n).putShort(i1);
      break;
    case 252: 
      this.stackMap.putByte(251 + n).putShort(i1);
      writeFrameTypes(3 + k, 3 + i);
      break;
    default: 
      this.stackMap.putByte(255).putShort(i1).putShort(i);
      writeFrameTypes(3, 3 + i);
      this.stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
    }
  }
  
  private void writeFrameTypes(int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = this.frame[i];
      int k = j & 0xF0000000;
      if (k == 0)
      {
        int m = j & 0xFFFFF;
        switch (j & 0xFF00000)
        {
        case 24117248: 
          this.stackMap.putByte(7).putShort(this.cw.newClass(this.cw.typeTable[m].strVal1));
          break;
        case 25165824: 
          this.stackMap.putByte(8).putShort(this.cw.typeTable[m].intVal);
          break;
        default: 
          this.stackMap.putByte(m);
        }
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        k >>= 28;
        while (k-- > 0) {
          localStringBuilder.append('[');
        }
        if ((j & 0xFF00000) == 24117248)
        {
          localStringBuilder.append('L');
          localStringBuilder.append(this.cw.typeTable[(j & 0xFFFFF)].strVal1);
          localStringBuilder.append(';');
        }
        else
        {
          switch (j & 0xF)
          {
          case 1: 
            localStringBuilder.append('I');
            break;
          case 2: 
            localStringBuilder.append('F');
            break;
          case 3: 
            localStringBuilder.append('D');
            break;
          case 9: 
            localStringBuilder.append('Z');
            break;
          case 10: 
            localStringBuilder.append('B');
            break;
          case 11: 
            localStringBuilder.append('C');
            break;
          case 12: 
            localStringBuilder.append('S');
            break;
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          default: 
            localStringBuilder.append('J');
          }
        }
        this.stackMap.putByte(7).putShort(this.cw.newClass(localStringBuilder.toString()));
      }
    }
  }
  
  private void writeFrameType(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      this.stackMap.putByte(7).putShort(this.cw.newClass((String)paramObject));
    } else if ((paramObject instanceof Integer)) {
      this.stackMap.putByte(((Integer)paramObject).intValue());
    } else {
      this.stackMap.putByte(8).putShort(((Label)paramObject).position);
    }
  }
  
  final int getSize()
  {
    if (this.classReaderOffset != 0) {
      return 6 + this.classReaderLength;
    }
    int i = 8;
    int j;
    if (this.code.length > 0)
    {
      if (this.code.length > 65536) {
        throw new RuntimeException("Method code too large!");
      }
      this.cw.newUTF8("Code");
      i += 18 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null)
      {
        this.cw.newUTF8("LocalVariableTable");
        i += 8 + this.localVar.length;
      }
      if (this.localVarType != null)
      {
        this.cw.newUTF8("LocalVariableTypeTable");
        i += 8 + this.localVarType.length;
      }
      if (this.lineNumber != null)
      {
        this.cw.newUTF8("LineNumberTable");
        i += 8 + this.lineNumber.length;
      }
      if (this.stackMap != null)
      {
        j = (this.cw.version & 0xFFFF) >= 50 ? 1 : 0;
        this.cw.newUTF8(j != 0 ? "StackMapTable" : "StackMap");
        i += 8 + this.stackMap.length;
      }
      if (this.ctanns != null)
      {
        this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
        i += 8 + this.ctanns.getSize();
      }
      if (this.ictanns != null)
      {
        this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
        i += 8 + this.ictanns.getSize();
      }
      if (this.cattrs != null) {
        i += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals);
      }
    }
    if (this.exceptionCount > 0)
    {
      this.cw.newUTF8("Exceptions");
      i += 8 + 2 * this.exceptionCount;
    }
    if (((this.access & 0x1000) != 0) && (((this.cw.version & 0xFFFF) < 49) || ((this.access & 0x40000) != 0)))
    {
      this.cw.newUTF8("Synthetic");
      i += 6;
    }
    if ((this.access & 0x20000) != 0)
    {
      this.cw.newUTF8("Deprecated");
      i += 6;
    }
    if (this.signature != null)
    {
      this.cw.newUTF8("Signature");
      this.cw.newUTF8(this.signature);
      i += 8;
    }
    if (this.methodParameters != null)
    {
      this.cw.newUTF8("MethodParameters");
      i += 7 + this.methodParameters.length;
    }
    if (this.annd != null)
    {
      this.cw.newUTF8("AnnotationDefault");
      i += 6 + this.annd.length;
    }
    if (this.anns != null)
    {
      this.cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + this.anns.getSize();
    }
    if (this.ianns != null)
    {
      this.cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + this.ianns.getSize();
    }
    if (this.tanns != null)
    {
      this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
      i += 8 + this.tanns.getSize();
    }
    if (this.itanns != null)
    {
      this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
      i += 8 + this.itanns.getSize();
    }
    if (this.panns != null)
    {
      this.cw.newUTF8("RuntimeVisibleParameterAnnotations");
      i += 7 + 2 * (this.panns.length - this.synthetics);
      for (j = this.panns.length - 1; j >= this.synthetics; j--) {
        i += (this.panns[j] == null ? 0 : this.panns[j].getSize());
      }
    }
    if (this.ipanns != null)
    {
      this.cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      i += 7 + 2 * (this.ipanns.length - this.synthetics);
      for (j = this.ipanns.length - 1; j >= this.synthetics; j--) {
        i += (this.ipanns[j] == null ? 0 : this.ipanns[j].getSize());
      }
    }
    if (this.attrs != null) {
      i += this.attrs.getSize(this.cw, null, 0, -1, -1);
    }
    return i;
  }
  
  final void put(ByteVector paramByteVector)
  {
    int i = 0xE0000 | (this.access & 0x40000) / 64;
    paramByteVector.putShort(this.access & (i ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.desc);
    if (this.classReaderOffset != 0)
    {
      paramByteVector.putByteArray(this.cw.cr.b, this.classReaderOffset, this.classReaderLength);
      return;
    }
    int j = 0;
    if (this.code.length > 0) {
      j++;
    }
    if (this.exceptionCount > 0) {
      j++;
    }
    if (((this.access & 0x1000) != 0) && (((this.cw.version & 0xFFFF) < 49) || ((this.access & 0x40000) != 0))) {
      j++;
    }
    if ((this.access & 0x20000) != 0) {
      j++;
    }
    if (this.signature != null) {
      j++;
    }
    if (this.methodParameters != null) {
      j++;
    }
    if (this.annd != null) {
      j++;
    }
    if (this.anns != null) {
      j++;
    }
    if (this.ianns != null) {
      j++;
    }
    if (this.tanns != null) {
      j++;
    }
    if (this.itanns != null) {
      j++;
    }
    if (this.panns != null) {
      j++;
    }
    if (this.ipanns != null) {
      j++;
    }
    if (this.attrs != null) {
      j += this.attrs.getCount();
    }
    paramByteVector.putShort(j);
    int k;
    if (this.code.length > 0)
    {
      k = 12 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null) {
        k += 8 + this.localVar.length;
      }
      if (this.localVarType != null) {
        k += 8 + this.localVarType.length;
      }
      if (this.lineNumber != null) {
        k += 8 + this.lineNumber.length;
      }
      if (this.stackMap != null) {
        k += 8 + this.stackMap.length;
      }
      if (this.ctanns != null) {
        k += 8 + this.ctanns.getSize();
      }
      if (this.ictanns != null) {
        k += 8 + this.ictanns.getSize();
      }
      if (this.cattrs != null) {
        k += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals);
      }
      paramByteVector.putShort(this.cw.newUTF8("Code")).putInt(k);
      paramByteVector.putShort(this.maxStack).putShort(this.maxLocals);
      paramByteVector.putInt(this.code.length).putByteArray(this.code.data, 0, this.code.length);
      paramByteVector.putShort(this.handlerCount);
      if (this.handlerCount > 0) {
        for (Handler localHandler = this.firstHandler; localHandler != null; localHandler = localHandler.next) {
          paramByteVector.putShort(localHandler.start.position).putShort(localHandler.end.position).putShort(localHandler.handler.position).putShort(localHandler.type);
        }
      }
      j = 0;
      if (this.localVar != null) {
        j++;
      }
      if (this.localVarType != null) {
        j++;
      }
      if (this.lineNumber != null) {
        j++;
      }
      if (this.stackMap != null) {
        j++;
      }
      if (this.ctanns != null) {
        j++;
      }
      if (this.ictanns != null) {
        j++;
      }
      if (this.cattrs != null) {
        j += this.cattrs.getCount();
      }
      paramByteVector.putShort(j);
      if (this.localVar != null)
      {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTable"));
        paramByteVector.putInt(this.localVar.length + 2).putShort(this.localVarCount);
        paramByteVector.putByteArray(this.localVar.data, 0, this.localVar.length);
      }
      if (this.localVarType != null)
      {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTypeTable"));
        paramByteVector.putInt(this.localVarType.length + 2).putShort(this.localVarTypeCount);
        paramByteVector.putByteArray(this.localVarType.data, 0, this.localVarType.length);
      }
      if (this.lineNumber != null)
      {
        paramByteVector.putShort(this.cw.newUTF8("LineNumberTable"));
        paramByteVector.putInt(this.lineNumber.length + 2).putShort(this.lineNumberCount);
        paramByteVector.putByteArray(this.lineNumber.data, 0, this.lineNumber.length);
      }
      if (this.stackMap != null)
      {
        int m = (this.cw.version & 0xFFFF) >= 50 ? 1 : 0;
        paramByteVector.putShort(this.cw.newUTF8(m != 0 ? "StackMapTable" : "StackMap"));
        paramByteVector.putInt(this.stackMap.length + 2).putShort(this.frameCount);
        paramByteVector.putByteArray(this.stackMap.data, 0, this.stackMap.length);
      }
      if (this.ctanns != null)
      {
        paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
        this.ctanns.put(paramByteVector);
      }
      if (this.ictanns != null)
      {
        paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
        this.ictanns.put(paramByteVector);
      }
      if (this.cattrs != null) {
        this.cattrs.put(this.cw, this.code.data, this.code.length, this.maxLocals, this.maxStack, paramByteVector);
      }
    }
    if (this.exceptionCount > 0)
    {
      paramByteVector.putShort(this.cw.newUTF8("Exceptions")).putInt(2 * this.exceptionCount + 2);
      paramByteVector.putShort(this.exceptionCount);
      for (k = 0; k < this.exceptionCount; k++) {
        paramByteVector.putShort(this.exceptions[k]);
      }
    }
    if (((this.access & 0x1000) != 0) && (((this.cw.version & 0xFFFF) < 49) || ((this.access & 0x40000) != 0))) {
      paramByteVector.putShort(this.cw.newUTF8("Synthetic")).putInt(0);
    }
    if ((this.access & 0x20000) != 0) {
      paramByteVector.putShort(this.cw.newUTF8("Deprecated")).putInt(0);
    }
    if (this.signature != null) {
      paramByteVector.putShort(this.cw.newUTF8("Signature")).putInt(2).putShort(this.cw.newUTF8(this.signature));
    }
    if (this.methodParameters != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("MethodParameters"));
      paramByteVector.putInt(this.methodParameters.length + 1).putByte(this.methodParametersCount);
      paramByteVector.putByteArray(this.methodParameters.data, 0, this.methodParameters.length);
    }
    if (this.annd != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("AnnotationDefault"));
      paramByteVector.putInt(this.annd.length);
      paramByteVector.putByteArray(this.annd.data, 0, this.annd.length);
    }
    if (this.anns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(paramByteVector);
    }
    if (this.ianns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(paramByteVector);
    }
    if (this.tanns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
      this.tanns.put(paramByteVector);
    }
    if (this.itanns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.itanns.put(paramByteVector);
    }
    if (this.panns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      AnnotationWriter.put(this.panns, this.synthetics, paramByteVector);
    }
    if (this.ipanns != null)
    {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      AnnotationWriter.put(this.ipanns, this.synthetics, paramByteVector);
    }
    if (this.attrs != null) {
      this.attrs.put(this.cw, null, 0, -1, -1, paramByteVector);
    }
  }
  
  private void resizeInstructions()
  {
    byte[] arrayOfByte = this.code.data;
    Object localObject1 = new int[0];
    Object localObject2 = new int[0];
    boolean[] arrayOfBoolean = new boolean[this.code.length];
    int i2 = 3;
    int i4;
    int k;
    int i1;
    Object localObject5;
    do
    {
      if (i2 == 3) {
        i2 = 2;
      }
      i = 0;
      while (i < arrayOfByte.length)
      {
        int i3 = arrayOfByte[i] & 0xFF;
        i4 = 0;
        switch (ClassWriter.TYPE[i3])
        {
        case 0: 
        case 4: 
          i++;
          break;
        case 9: 
          if (i3 > 201)
          {
            i3 = i3 < 218 ? i3 - 49 : i3 - 20;
            k = i + readUnsignedShort(arrayOfByte, i + 1);
          }
          else
          {
            k = i + readShort(arrayOfByte, i + 1);
          }
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
          if (((i1 < 32768) || (i1 > 32767)) && (arrayOfBoolean[i] == 0))
          {
            if ((i3 == 167) || (i3 == 168)) {
              i4 = 2;
            } else {
              i4 = 5;
            }
            arrayOfBoolean[i] = true;
          }
          i += 3;
          break;
        case 10: 
          i += 5;
          break;
        case 14: 
          if (i2 == 1)
          {
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, i);
            i4 = -(i1 & 0x3);
          }
          else if (arrayOfBoolean[i] == 0)
          {
            i4 = i & 0x3;
            arrayOfBoolean[i] = true;
          }
          i = i + 4 - (i & 0x3);
          i += 4 * (readInt(arrayOfByte, i + 8) - readInt(arrayOfByte, i + 4) + 1) + 12;
          break;
        case 15: 
          if (i2 == 1)
          {
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, i);
            i4 = -(i1 & 0x3);
          }
          else if (arrayOfBoolean[i] == 0)
          {
            i4 = i & 0x3;
            arrayOfBoolean[i] = true;
          }
          i = i + 4 - (i & 0x3);
          i += 8 * readInt(arrayOfByte, i + 4) + 8;
          break;
        case 17: 
          i3 = arrayOfByte[(i + 1)] & 0xFF;
          if (i3 == 132) {
            i += 6;
          } else {
            i += 4;
          }
          break;
        case 1: 
        case 3: 
        case 11: 
          i += 2;
          break;
        case 2: 
        case 5: 
        case 6: 
        case 12: 
        case 13: 
          i += 3;
          break;
        case 7: 
        case 8: 
          i += 5;
          break;
        case 16: 
        default: 
          i += 4;
        }
        if (i4 != 0)
        {
          localObject4 = new int[localObject1.length + 1];
          localObject5 = new int[localObject2.length + 1];
          System.arraycopy(localObject1, 0, localObject4, 0, localObject1.length);
          System.arraycopy(localObject2, 0, localObject5, 0, localObject2.length);
          localObject4[localObject1.length] = i;
          localObject5[localObject2.length] = i4;
          localObject1 = localObject4;
          localObject2 = localObject5;
          if (i4 > 0) {
            i2 = 3;
          }
        }
      }
      if (i2 < 3) {
        i2--;
      }
    } while (i2 != 0);
    ByteVector localByteVector = new ByteVector(this.code.length);
    int i = 0;
    while (i < this.code.length)
    {
      i4 = arrayOfByte[i] & 0xFF;
      int j;
      int n;
      switch (ClassWriter.TYPE[i4])
      {
      case 0: 
      case 4: 
        localByteVector.putByte(i4);
        i++;
        break;
      case 9: 
        if (i4 > 201)
        {
          i4 = i4 < 218 ? i4 - 49 : i4 - 20;
          k = i + readUnsignedShort(arrayOfByte, i + 1);
        }
        else
        {
          k = i + readShort(arrayOfByte, i + 1);
        }
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
        if (arrayOfBoolean[i] != 0)
        {
          if (i4 == 167)
          {
            localByteVector.putByte(200);
          }
          else if (i4 == 168)
          {
            localByteVector.putByte(201);
          }
          else
          {
            localByteVector.putByte(i4 <= 166 ? (i4 + 1 ^ 0x1) - 1 : i4 ^ 0x1);
            localByteVector.putShort(8);
            localByteVector.putByte(200);
            i1 -= 3;
          }
          localByteVector.putInt(i1);
        }
        else
        {
          localByteVector.putByte(i4);
          localByteVector.putShort(i1);
        }
        i += 3;
        break;
      case 10: 
        k = i + readInt(arrayOfByte, i + 1);
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
        localByteVector.putByte(i4);
        localByteVector.putInt(i1);
        i += 5;
        break;
      case 14: 
        j = i;
        i = i + 4 - (j & 0x3);
        localByteVector.putByte(170);
        localByteVector.putByteArray(null, 0, (4 - localByteVector.length % 4) % 4);
        k = j + readInt(arrayOfByte, i);
        i += 4;
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
        localByteVector.putInt(i1);
        n = readInt(arrayOfByte, i);
        i += 4;
        localByteVector.putInt(n);
        n = readInt(arrayOfByte, i) - n + 1;
        i += 4;
        localByteVector.putInt(readInt(arrayOfByte, i - 4));
      case 15: 
      case 17: 
      case 1: 
      case 3: 
      case 11: 
      case 2: 
      case 5: 
      case 6: 
      case 12: 
      case 13: 
      case 7: 
      case 8: 
      case 16: 
      default: 
        while (n > 0)
        {
          k = j + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
          localByteVector.putInt(i1);
          n--;
          continue;
          j = i;
          i = i + 4 - (j & 0x3);
          localByteVector.putByte(171);
          localByteVector.putByteArray(null, 0, (4 - localByteVector.length % 4) % 4);
          k = j + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
          localByteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          localByteVector.putInt(n);
          while (n > 0)
          {
            localByteVector.putInt(readInt(arrayOfByte, i));
            i += 4;
            k = j + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
            localByteVector.putInt(i1);
            n--;
            continue;
            i4 = arrayOfByte[(i + 1)] & 0xFF;
            if (i4 == 132)
            {
              localByteVector.putByteArray(arrayOfByte, i, 6);
              i += 6;
            }
            else
            {
              localByteVector.putByteArray(arrayOfByte, i, 4);
              i += 4;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 2);
              i += 2;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 3);
              i += 3;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 5);
              i += 5;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 4);
              i += 4;
            }
          }
        }
      }
    }
    if (this.compute == 0)
    {
      for (localObject3 = this.labels; localObject3 != null; localObject3 = ((Label)localObject3).successor)
      {
        i = ((Label)localObject3).position - 3;
        if ((i >= 0) && (arrayOfBoolean[i] != 0)) {
          localObject3.status |= 0x10;
        }
        getNewOffset((int[])localObject1, (int[])localObject2, (Label)localObject3);
      }
      for (m = 0; m < this.cw.typeTable.length; m++)
      {
        localObject4 = this.cw.typeTable[m];
        if ((localObject4 != null) && (((Item)localObject4).type == 31)) {
          ((Item)localObject4).intVal = getNewOffset((int[])localObject1, (int[])localObject2, 0, ((Item)localObject4).intVal);
        }
      }
    }
    else if (this.frameCount > 0)
    {
      this.cw.invalidFrames = true;
    }
    for (Object localObject3 = this.firstHandler; localObject3 != null; localObject3 = ((Handler)localObject3).next)
    {
      getNewOffset((int[])localObject1, (int[])localObject2, ((Handler)localObject3).start);
      getNewOffset((int[])localObject1, (int[])localObject2, ((Handler)localObject3).end);
      getNewOffset((int[])localObject1, (int[])localObject2, ((Handler)localObject3).handler);
    }
    for (int m = 0; m < 2; m++)
    {
      localObject4 = m == 0 ? this.localVar : this.localVarType;
      if (localObject4 != null)
      {
        arrayOfByte = ((ByteVector)localObject4).data;
        for (i = 0; i < ((ByteVector)localObject4).length; i += 10)
        {
          k = readUnsignedShort(arrayOfByte, i);
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, k);
          writeShort(arrayOfByte, i, i1);
          k += readUnsignedShort(arrayOfByte, i + 2);
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, k) - i1;
          writeShort(arrayOfByte, i + 2, i1);
        }
      }
    }
    if (this.lineNumber != null)
    {
      arrayOfByte = this.lineNumber.data;
      for (i = 0; i < this.lineNumber.length; i += 4) {
        writeShort(arrayOfByte, i, getNewOffset((int[])localObject1, (int[])localObject2, 0, readUnsignedShort(arrayOfByte, i)));
      }
    }
    for (Object localObject4 = this.cattrs; localObject4 != null; localObject4 = ((Attribute)localObject4).next)
    {
      localObject5 = ((Attribute)localObject4).getLabels();
      if (localObject5 != null) {
        for (m = localObject5.length - 1; m >= 0; m--) {
          getNewOffset((int[])localObject1, (int[])localObject2, localObject5[m]);
        }
      }
    }
    this.code = localByteVector;
  }
  
  static int readUnsignedShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  static short readShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  static int readInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  static void writeShort(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >>> 8));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)paramInt2);
  }
  
  static int getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < paramArrayOfInt1.length; j++) {
      if ((paramInt1 < paramArrayOfInt1[j]) && (paramArrayOfInt1[j] <= paramInt2)) {
        i += paramArrayOfInt2[j];
      } else if ((paramInt2 < paramArrayOfInt1[j]) && (paramArrayOfInt1[j] <= paramInt1)) {
        i -= paramArrayOfInt2[j];
      }
    }
    return i;
  }
  
  static void getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, Label paramLabel)
  {
    if ((paramLabel.status & 0x4) == 0)
    {
      paramLabel.position = getNewOffset(paramArrayOfInt1, paramArrayOfInt2, 0, paramLabel.position);
      paramLabel.status |= 0x4;
    }
  }
}
