package java.lang.instrument;

import java.security.ProtectionDomain;

public abstract interface ClassFileTransformer
{
  public abstract byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte)
    throws IllegalClassFormatException;
}
