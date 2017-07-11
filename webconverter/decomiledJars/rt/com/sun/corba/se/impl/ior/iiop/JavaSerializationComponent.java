package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import org.omg.CORBA_2_3.portable.OutputStream;

public class JavaSerializationComponent
  extends TaggedComponentBase
{
  private byte version;
  private static JavaSerializationComponent singleton;
  
  public static JavaSerializationComponent singleton()
  {
    if (singleton == null) {
      synchronized (JavaSerializationComponent.class)
      {
        singleton = new JavaSerializationComponent((byte)1);
      }
    }
    return singleton;
  }
  
  public JavaSerializationComponent(byte paramByte)
  {
    this.version = paramByte;
  }
  
  public byte javaSerializationVersion()
  {
    return this.version;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    paramOutputStream.write_octet(this.version);
  }
  
  public int getId()
  {
    return 1398099458;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof JavaSerializationComponent)) {
      return false;
    }
    JavaSerializationComponent localJavaSerializationComponent = (JavaSerializationComponent)paramObject;
    return this.version == localJavaSerializationComponent.version;
  }
  
  public int hashCode()
  {
    return this.version;
  }
}
