package com.sun.xml.internal.ws.org.objectweb.asm;

final class AnnotationWriter
  implements AnnotationVisitor
{
  private final ClassWriter cw;
  private int size;
  private final boolean named;
  private final ByteVector bv;
  private final ByteVector parent;
  private final int offset;
  AnnotationWriter next;
  AnnotationWriter prev;
  
  AnnotationWriter(ClassWriter paramClassWriter, boolean paramBoolean, ByteVector paramByteVector1, ByteVector paramByteVector2, int paramInt)
  {
    this.cw = paramClassWriter;
    this.named = paramBoolean;
    this.bv = paramByteVector1;
    this.parent = paramByteVector2;
    this.offset = paramInt;
  }
  
  public void visit(String paramString, Object paramObject)
  {
    this.size += 1;
    if (this.named) {
      this.bv.putShort(this.cw.newUTF8(paramString));
    }
    if ((paramObject instanceof String))
    {
      this.bv.put12(115, this.cw.newUTF8((String)paramObject));
    }
    else if ((paramObject instanceof Byte))
    {
      this.bv.put12(66, this.cw.newInteger(((Byte)paramObject).byteValue()).index);
    }
    else if ((paramObject instanceof Boolean))
    {
      int i = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      this.bv.put12(90, this.cw.newInteger(i).index);
    }
    else if ((paramObject instanceof Character))
    {
      this.bv.put12(67, this.cw.newInteger(((Character)paramObject).charValue()).index);
    }
    else if ((paramObject instanceof Short))
    {
      this.bv.put12(83, this.cw.newInteger(((Short)paramObject).shortValue()).index);
    }
    else if ((paramObject instanceof Type))
    {
      this.bv.put12(99, this.cw.newUTF8(((Type)paramObject).getDescriptor()));
    }
    else
    {
      Object localObject;
      int j;
      if ((paramObject instanceof byte[]))
      {
        localObject = (byte[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(66, this.cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(90, this.cw.newInteger(localObject[j] != 0 ? 1 : 0).index);
        }
      }
      else if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(83, this.cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof char[]))
      {
        localObject = (char[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(67, this.cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(73, this.cw.newInteger(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(74, this.cw.newLong(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(70, this.cw.newFloat(localObject[j]).index);
        }
      }
      else if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        this.bv.put12(91, localObject.length);
        for (j = 0; j < localObject.length; j++) {
          this.bv.put12(68, this.cw.newDouble(localObject[j]).index);
        }
      }
      else
      {
        localObject = this.cw.newConstItem(paramObject);
        this.bv.put12(".s.IFJDCS".charAt(((Item)localObject).type), ((Item)localObject).index);
      }
    }
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    this.size += 1;
    if (this.named) {
      this.bv.putShort(this.cw.newUTF8(paramString1));
    }
    this.bv.put12(101, this.cw.newUTF8(paramString2)).putShort(this.cw.newUTF8(paramString3));
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    this.size += 1;
    if (this.named) {
      this.bv.putShort(this.cw.newUTF8(paramString1));
    }
    this.bv.put12(64, this.cw.newUTF8(paramString2)).putShort(0);
    return new AnnotationWriter(this.cw, true, this.bv, this.bv, this.bv.length - 2);
  }
  
  public AnnotationVisitor visitArray(String paramString)
  {
    this.size += 1;
    if (this.named) {
      this.bv.putShort(this.cw.newUTF8(paramString));
    }
    this.bv.put12(91, 0);
    return new AnnotationWriter(this.cw, false, this.bv, this.bv, this.bv.length - 2);
  }
  
  public void visitEnd()
  {
    if (this.parent != null)
    {
      byte[] arrayOfByte = this.parent.data;
      arrayOfByte[this.offset] = ((byte)(this.size >>> 8));
      arrayOfByte[(this.offset + 1)] = ((byte)this.size);
    }
  }
  
  int getSize()
  {
    int i = 0;
    for (AnnotationWriter localAnnotationWriter = this; localAnnotationWriter != null; localAnnotationWriter = localAnnotationWriter.next) {
      i += localAnnotationWriter.bv.length;
    }
    return i;
  }
  
  void put(ByteVector paramByteVector)
  {
    int i = 0;
    int j = 2;
    Object localObject1 = this;
    Object localObject2 = null;
    while (localObject1 != null)
    {
      i++;
      j += ((AnnotationWriter)localObject1).bv.length;
      ((AnnotationWriter)localObject1).visitEnd();
      ((AnnotationWriter)localObject1).prev = localObject2;
      localObject2 = localObject1;
      localObject1 = ((AnnotationWriter)localObject1).next;
    }
    paramByteVector.putInt(j);
    paramByteVector.putShort(i);
    for (localObject1 = localObject2; localObject1 != null; localObject1 = ((AnnotationWriter)localObject1).prev) {
      paramByteVector.putByteArray(((AnnotationWriter)localObject1).bv.data, 0, ((AnnotationWriter)localObject1).bv.length);
    }
  }
  
  static void put(AnnotationWriter[] paramArrayOfAnnotationWriter, int paramInt, ByteVector paramByteVector)
  {
    int i = 1 + 2 * (paramArrayOfAnnotationWriter.length - paramInt);
    for (int j = paramInt; j < paramArrayOfAnnotationWriter.length; j++) {
      i += (paramArrayOfAnnotationWriter[j] == null ? 0 : paramArrayOfAnnotationWriter[j].getSize());
    }
    paramByteVector.putInt(i).putByte(paramArrayOfAnnotationWriter.length - paramInt);
    for (j = paramInt; j < paramArrayOfAnnotationWriter.length; j++)
    {
      Object localObject1 = paramArrayOfAnnotationWriter[j];
      Object localObject2 = null;
      int k = 0;
      while (localObject1 != null)
      {
        k++;
        ((AnnotationWriter)localObject1).visitEnd();
        ((AnnotationWriter)localObject1).prev = localObject2;
        localObject2 = localObject1;
        localObject1 = ((AnnotationWriter)localObject1).next;
      }
      paramByteVector.putShort(k);
      for (localObject1 = localObject2; localObject1 != null; localObject1 = ((AnnotationWriter)localObject1).prev) {
        paramByteVector.putByteArray(((AnnotationWriter)localObject1).bv.data, 0, ((AnnotationWriter)localObject1).bv.length);
      }
    }
  }
}
