package org.omg.PortableInterceptor;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectReferenceTemplateHolder
  implements Streamable
{
  public ObjectReferenceTemplate value = null;
  
  public ObjectReferenceTemplateHolder() {}
  
  public ObjectReferenceTemplateHolder(ObjectReferenceTemplate paramObjectReferenceTemplate)
  {
    this.value = paramObjectReferenceTemplate;
  }
  
  public void _read(InputStream paramInputStream)
  {
    this.value = ObjectReferenceTemplateHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ObjectReferenceTemplateHelper.write(paramOutputStream, this.value);
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceTemplateHelper.type();
  }
}
