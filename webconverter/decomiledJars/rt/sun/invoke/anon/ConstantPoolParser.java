package sun.invoke.anon;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ConstantPoolParser
{
  final byte[] classFile;
  final byte[] tags;
  final char[] firstHeader;
  int endOffset;
  char[] secondHeader;
  private char[] charArray = new char[80];
  
  public ConstantPoolParser(byte[] paramArrayOfByte)
    throws InvalidConstantPoolFormatException
  {
    this.classFile = paramArrayOfByte;
    this.firstHeader = parseHeader(paramArrayOfByte);
    this.tags = new byte[this.firstHeader[4]];
  }
  
  public ConstantPoolParser(Class<?> paramClass)
    throws IOException, InvalidConstantPoolFormatException
  {
    this(AnonymousClassLoader.readClassFile(paramClass));
  }
  
  public ConstantPoolPatch createPatch()
  {
    return new ConstantPoolPatch(this);
  }
  
  public byte getTag(int paramInt)
  {
    getEndOffset();
    return this.tags[paramInt];
  }
  
  public int getLength()
  {
    return this.firstHeader[4];
  }
  
  public int getStartOffset()
  {
    return this.firstHeader.length * 2;
  }
  
  public int getEndOffset()
  {
    if (this.endOffset == 0) {
      throw new IllegalStateException("class file has not yet been parsed");
    }
    return this.endOffset;
  }
  
  public int getThisClassIndex()
  {
    getEndOffset();
    return this.secondHeader[1];
  }
  
  public int getTailLength()
  {
    return this.classFile.length - getEndOffset();
  }
  
  public void writeHead(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(this.classFile, 0, getEndOffset());
  }
  
  void writePatchedHead(OutputStream paramOutputStream, Object[] paramArrayOfObject)
  {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public void writeTail(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(this.classFile, getEndOffset(), getTailLength());
  }
  
  private static char[] parseHeader(byte[] paramArrayOfByte)
    throws InvalidConstantPoolFormatException
  {
    char[] arrayOfChar = new char[5];
    ByteBuffer localByteBuffer = ByteBuffer.wrap(paramArrayOfByte);
    for (int i = 0; i < arrayOfChar.length; i++) {
      arrayOfChar[i] = ((char)getUnsignedShort(localByteBuffer));
    }
    i = arrayOfChar[0] << '\020' | arrayOfChar[1] << '\000';
    if (i != -889275714) {
      throw new InvalidConstantPoolFormatException("invalid magic number " + i);
    }
    int j = arrayOfChar[4];
    if (j < 1) {
      throw new InvalidConstantPoolFormatException("constant pool length < 1");
    }
    return arrayOfChar;
  }
  
  public void parse(ConstantPoolVisitor paramConstantPoolVisitor)
    throws InvalidConstantPoolFormatException
  {
    ByteBuffer localByteBuffer = ByteBuffer.wrap(this.classFile);
    localByteBuffer.position(getStartOffset());
    Object[] arrayOfObject = new Object[getLength()];
    try
    {
      parseConstantPool(localByteBuffer, arrayOfObject, paramConstantPoolVisitor);
    }
    catch (BufferUnderflowException localBufferUnderflowException)
    {
      throw new InvalidConstantPoolFormatException(localBufferUnderflowException);
    }
    if (this.endOffset == 0)
    {
      this.endOffset = localByteBuffer.position();
      this.secondHeader = new char[4];
      for (int i = 0; i < this.secondHeader.length; i++) {
        this.secondHeader[i] = ((char)getUnsignedShort(localByteBuffer));
      }
    }
    resolveConstantPool(arrayOfObject, paramConstantPoolVisitor);
  }
  
  private char[] getCharArray(int paramInt)
  {
    if (paramInt <= this.charArray.length) {
      return this.charArray;
    }
    return this.charArray = new char[paramInt];
  }
  
  private void parseConstantPool(ByteBuffer paramByteBuffer, Object[] paramArrayOfObject, ConstantPoolVisitor paramConstantPoolVisitor)
    throws InvalidConstantPoolFormatException
  {
    int i = 1;
    while (i < this.tags.length)
    {
      byte b = (byte)getUnsignedByte(paramByteBuffer);
      assert ((this.tags[i] == 0) || (this.tags[i] == b));
      this.tags[i] = b;
      switch (b)
      {
      case 1: 
        int j = getUnsignedShort(paramByteBuffer);
        String str = getUTF8(paramByteBuffer, j, getCharArray(j));
        paramConstantPoolVisitor.visitUTF8(i, (byte)1, str);
        this.tags[i] = b;
        paramArrayOfObject[(i++)] = str;
        break;
      case 3: 
        paramConstantPoolVisitor.visitConstantValue(i, b, Integer.valueOf(paramByteBuffer.getInt()));
        i++;
        break;
      case 4: 
        paramConstantPoolVisitor.visitConstantValue(i, b, Float.valueOf(paramByteBuffer.getFloat()));
        i++;
        break;
      case 5: 
        paramConstantPoolVisitor.visitConstantValue(i, b, Long.valueOf(paramByteBuffer.getLong()));
        i += 2;
        break;
      case 6: 
        paramConstantPoolVisitor.visitConstantValue(i, b, Double.valueOf(paramByteBuffer.getDouble()));
        i += 2;
        break;
      case 7: 
      case 8: 
        this.tags[i] = b;
        paramArrayOfObject[(i++)] = { getUnsignedShort(paramByteBuffer) };
        break;
      case 9: 
      case 10: 
      case 11: 
      case 12: 
        this.tags[i] = b;
        paramArrayOfObject[(i++)] = { getUnsignedShort(paramByteBuffer), getUnsignedShort(paramByteBuffer) };
        break;
      case 2: 
      default: 
        throw new AssertionError("invalid constant " + b);
      }
    }
  }
  
  private void resolveConstantPool(Object[] paramArrayOfObject, ConstantPoolVisitor paramConstantPoolVisitor)
  {
    int i = 1;
    int m;
    for (int j = paramArrayOfObject.length - 1; i <= j; j = m)
    {
      int k = j;
      m = i - 1;
      for (int n = i; n <= j; n++)
      {
        Object localObject1 = paramArrayOfObject[n];
        if ((localObject1 instanceof int[]))
        {
          int[] arrayOfInt = (int[])localObject1;
          byte b = this.tags[n];
          Object localObject2;
          Object localObject3;
          switch (b)
          {
          case 8: 
            String str = (String)paramArrayOfObject[arrayOfInt[0]];
            paramConstantPoolVisitor.visitConstantString(n, b, str, arrayOfInt[0]);
            paramArrayOfObject[n] = null;
            break;
          case 7: 
            localObject2 = (String)paramArrayOfObject[arrayOfInt[0]];
            localObject2 = ((String)localObject2).replace('/', '.');
            paramConstantPoolVisitor.visitConstantString(n, b, (String)localObject2, arrayOfInt[0]);
            paramArrayOfObject[n] = localObject2;
            break;
          case 12: 
            localObject2 = (String)paramArrayOfObject[arrayOfInt[0]];
            localObject3 = (String)paramArrayOfObject[arrayOfInt[1]];
            paramConstantPoolVisitor.visitDescriptor(n, b, (String)localObject2, (String)localObject3, arrayOfInt[0], arrayOfInt[1]);
            paramArrayOfObject[n] = { localObject2, localObject3 };
            break;
          case 9: 
          case 10: 
          case 11: 
            localObject2 = paramArrayOfObject[arrayOfInt[0]];
            localObject3 = paramArrayOfObject[arrayOfInt[1]];
            if ((!(localObject2 instanceof String)) || (!(localObject3 instanceof String[])))
            {
              if (k > n) {
                k = n;
              }
              if (m < n) {
                m = n;
              }
            }
            else
            {
              String[] arrayOfString = (String[])localObject3;
              paramConstantPoolVisitor.visitMemberRef(n, b, (String)localObject2, arrayOfString[0], arrayOfString[1], arrayOfInt[0], arrayOfInt[1]);
              paramArrayOfObject[n] = null;
            }
            break;
          }
        }
      }
      i = k;
    }
  }
  
  private static int getUnsignedByte(ByteBuffer paramByteBuffer)
  {
    return paramByteBuffer.get() & 0xFF;
  }
  
  private static int getUnsignedShort(ByteBuffer paramByteBuffer)
  {
    int i = getUnsignedByte(paramByteBuffer);
    int j = getUnsignedByte(paramByteBuffer);
    return (i << 8) + (j << 0);
  }
  
  private static String getUTF8(ByteBuffer paramByteBuffer, int paramInt, char[] paramArrayOfChar)
    throws InvalidConstantPoolFormatException
  {
    int i = paramByteBuffer.position() + paramInt;
    int j = 0;
    while (paramByteBuffer.position() < i)
    {
      int k = paramByteBuffer.get() & 0xFF;
      if (k > 127)
      {
        paramByteBuffer.position(paramByteBuffer.position() - 1);
        return getUTF8Extended(paramByteBuffer, i, paramArrayOfChar, j);
      }
      paramArrayOfChar[(j++)] = ((char)k);
    }
    return new String(paramArrayOfChar, 0, j);
  }
  
  private static String getUTF8Extended(ByteBuffer paramByteBuffer, int paramInt1, char[] paramArrayOfChar, int paramInt2)
    throws InvalidConstantPoolFormatException
  {
    while (paramByteBuffer.position() < paramInt1)
    {
      int i = paramByteBuffer.get() & 0xFF;
      int j;
      switch (i >> 4)
      {
      case 0: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      case 7: 
        paramArrayOfChar[(paramInt2++)] = ((char)i);
        break;
      case 12: 
      case 13: 
        j = paramByteBuffer.get();
        if ((j & 0xC0) != 128) {
          throw new InvalidConstantPoolFormatException("malformed input around byte " + paramByteBuffer.position());
        }
        paramArrayOfChar[(paramInt2++)] = ((char)((i & 0x1F) << 6 | j & 0x3F));
        break;
      case 14: 
        j = paramByteBuffer.get();
        int k = paramByteBuffer.get();
        if (((j & 0xC0) != 128) || ((k & 0xC0) != 128)) {
          throw new InvalidConstantPoolFormatException("malformed input around byte " + paramByteBuffer.position());
        }
        paramArrayOfChar[(paramInt2++)] = ((char)((i & 0xF) << 12 | (j & 0x3F) << 6 | (k & 0x3F) << 0));
        break;
      case 8: 
      case 9: 
      case 10: 
      case 11: 
      default: 
        throw new InvalidConstantPoolFormatException("malformed input around byte " + paramByteBuffer.position());
      }
    }
    return new String(paramArrayOfChar, 0, paramInt2);
  }
}
