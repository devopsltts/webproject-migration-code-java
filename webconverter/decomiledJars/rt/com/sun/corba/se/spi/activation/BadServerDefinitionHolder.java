package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class BadServerDefinitionHolder
  implements Streamable
{
  public BadServerDefinition value = null;
  
  public BadServerDefinitionHolder() {}
  
  public BadServerDefinitionHolder(BadServerDefinition paramBadServerDefinition)
  {
    this.value = paramBadServerDefinition;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = BadServerDefinitionHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    BadServerDefinitionHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return BadServerDefinitionHelper.type();
  }
}
