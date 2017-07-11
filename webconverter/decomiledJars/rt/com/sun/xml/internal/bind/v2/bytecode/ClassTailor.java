package com.sun.xml.internal.bind.v2.bytecode;

import com.sun.xml.internal.bind.Util;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassTailor
{
  private static final Logger logger = Util.getClassLogger();
  
  private ClassTailor() {}
  
  public static String toVMClassName(Class paramClass)
  {
    assert (!paramClass.isPrimitive());
    if (paramClass.isArray()) {
      return toVMTypeName(paramClass);
    }
    return paramClass.getName().replace('.', '/');
  }
  
  public static String toVMTypeName(Class paramClass)
  {
    if (paramClass.isArray()) {
      return '[' + toVMTypeName(paramClass.getComponentType());
    }
    if (paramClass.isPrimitive())
    {
      if (paramClass == Boolean.TYPE) {
        return "Z";
      }
      if (paramClass == Character.TYPE) {
        return "C";
      }
      if (paramClass == Byte.TYPE) {
        return "B";
      }
      if (paramClass == Double.TYPE) {
        return "D";
      }
      if (paramClass == Float.TYPE) {
        return "F";
      }
      if (paramClass == Integer.TYPE) {
        return "I";
      }
      if (paramClass == Long.TYPE) {
        return "J";
      }
      if (paramClass == Short.TYPE) {
        return "S";
      }
      throw new IllegalArgumentException(paramClass.getName());
    }
    return 'L' + paramClass.getName().replace('.', '/') + ';';
  }
  
  public static byte[] tailor(Class paramClass, String paramString, String... paramVarArgs)
  {
    String str = toVMClassName(paramClass);
    return tailor(SecureLoader.getClassClassLoader(paramClass).getResourceAsStream(str + ".class"), str, paramString, paramVarArgs);
  }
  
  public static byte[] tailor(InputStream paramInputStream, String paramString1, String paramString2, String... paramVarArgs)
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(1024);
      DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
      long l = localDataInputStream.readLong();
      localDataOutputStream.writeLong(l);
      int i = localDataInputStream.readShort();
      localDataOutputStream.writeShort(i);
      int k;
      for (int j = 0; j < i; j++)
      {
        k = localDataInputStream.readByte();
        localDataOutputStream.writeByte(k);
        switch (k)
        {
        case 0: 
          break;
        case 1: 
          String str = localDataInputStream.readUTF();
          if (str.equals(paramString1)) {
            str = paramString2;
          } else {
            for (int m = 0; m < paramVarArgs.length; m += 2) {
              if (str.equals(paramVarArgs[m]))
              {
                str = paramVarArgs[(m + 1)];
                break;
              }
            }
          }
          localDataOutputStream.writeUTF(str);
          break;
        case 3: 
        case 4: 
          localDataOutputStream.writeInt(localDataInputStream.readInt());
          break;
        case 5: 
        case 6: 
          j++;
          localDataOutputStream.writeLong(localDataInputStream.readLong());
          break;
        case 7: 
        case 8: 
          localDataOutputStream.writeShort(localDataInputStream.readShort());
          break;
        case 9: 
        case 10: 
        case 11: 
        case 12: 
          localDataOutputStream.writeInt(localDataInputStream.readInt());
          break;
        case 2: 
        default: 
          throw new IllegalArgumentException("Unknown constant type " + k);
        }
      }
      byte[] arrayOfByte = new byte['Ȁ'];
      while ((k = localDataInputStream.read(arrayOfByte)) > 0) {
        localDataOutputStream.write(arrayOfByte, 0, k);
      }
      localDataInputStream.close();
      localDataOutputStream.close();
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException)
    {
      logger.log(Level.WARNING, "failed to tailor", localIOException);
    }
    return null;
  }
}
