package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedProfileHolder
  implements Streamable
{
  public TaggedProfile value = null;
  
  public TaggedProfileHolder() {}
  
  public TaggedProfileHolder(TaggedProfile paramTaggedProfile)
  {
    this.value = paramTaggedProfile;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = TaggedProfileHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    TaggedProfileHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return TaggedProfileHelper.type();
  }
}
