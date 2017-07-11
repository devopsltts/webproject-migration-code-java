package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;

public class ObjectReferenceTemplateImpl
  extends ObjectReferenceProducerBase
  implements ObjectReferenceTemplate, StreamableValue
{
  private transient IORTemplate iorTemplate;
  public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0";
  
  public ObjectReferenceTemplateImpl(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    super((ORB)paramInputStream.orb());
    _read(paramInputStream);
  }
  
  public ObjectReferenceTemplateImpl(ORB paramORB, IORTemplate paramIORTemplate)
  {
    super(paramORB);
    this.iorTemplate = paramIORTemplate;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectReferenceTemplateImpl)) {
      return false;
    }
    ObjectReferenceTemplateImpl localObjectReferenceTemplateImpl = (ObjectReferenceTemplateImpl)paramObject;
    return (this.iorTemplate != null) && (this.iorTemplate.equals(localObjectReferenceTemplateImpl.iorTemplate));
  }
  
  public int hashCode()
  {
    return this.iorTemplate.hashCode();
  }
  
  public String[] _truncatable_ids()
  {
    return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" };
  }
  
  public TypeCode _type()
  {
    return ObjectReferenceTemplateHelper.type();
  }
  
  public void _read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream;
    this.iorTemplate = IORFactories.makeIORTemplate(localInputStream);
    this.orb = ((ORB)localInputStream.orb());
  }
  
  public void _write(org.omg.CORBA.portable.OutputStream paramOutputStream)
  {
    org.omg.CORBA_2_3.portable.OutputStream localOutputStream = (org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream;
    this.iorTemplate.write(localOutputStream);
  }
  
  public String server_id()
  {
    int i = this.iorTemplate.getObjectKeyTemplate().getServerId();
    return Integer.toString(i);
  }
  
  public String orb_id()
  {
    return this.iorTemplate.getObjectKeyTemplate().getORBId();
  }
  
  public String[] adapter_name()
  {
    ObjectAdapterId localObjectAdapterId = this.iorTemplate.getObjectKeyTemplate().getObjectAdapterId();
    return localObjectAdapterId.getAdapterName();
  }
  
  public IORFactory getIORFactory()
  {
    return this.iorTemplate;
  }
  
  public IORTemplateList getIORTemplateList()
  {
    IORTemplateList localIORTemplateList = IORFactories.makeIORTemplateList();
    localIORTemplateList.add(this.iorTemplate);
    localIORTemplateList.makeImmutable();
    return localIORTemplateList;
  }
}
