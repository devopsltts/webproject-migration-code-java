package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.orbutil.HexOutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.IORHelper;
import sun.corba.OutputStreamFactory;

public class IORImpl
  extends IdentifiableContainerBase
  implements com.sun.corba.se.spi.ior.IOR
{
  private String typeId;
  private ORB factory = null;
  private boolean isCachedHashValue = false;
  private int cachedHashValue;
  IORSystemException wrapper;
  private IORTemplateList iortemps = null;
  
  public ORB getORB()
  {
    return this.factory;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof com.sun.corba.se.spi.ior.IOR)) {
      return false;
    }
    com.sun.corba.se.spi.ior.IOR localIOR = (com.sun.corba.se.spi.ior.IOR)paramObject;
    return (super.equals(paramObject)) && (this.typeId.equals(localIOR.getTypeId()));
  }
  
  public synchronized int hashCode()
  {
    if (!this.isCachedHashValue)
    {
      this.cachedHashValue = (super.hashCode() ^ this.typeId.hashCode());
      this.isCachedHashValue = true;
    }
    return this.cachedHashValue;
  }
  
  public IORImpl(ORB paramORB)
  {
    this(paramORB, "");
  }
  
  public IORImpl(ORB paramORB, String paramString)
  {
    this.factory = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
    this.typeId = paramString;
  }
  
  public IORImpl(ORB paramORB, String paramString, IORTemplate paramIORTemplate, ObjectId paramObjectId)
  {
    this(paramORB, paramString);
    this.iortemps = IORFactories.makeIORTemplateList();
    this.iortemps.add(paramIORTemplate);
    addTaggedProfiles(paramIORTemplate, paramObjectId);
    makeImmutable();
  }
  
  private void addTaggedProfiles(IORTemplate paramIORTemplate, ObjectId paramObjectId)
  {
    ObjectKeyTemplate localObjectKeyTemplate = paramIORTemplate.getObjectKeyTemplate();
    Iterator localIterator = paramIORTemplate.iterator();
    while (localIterator.hasNext())
    {
      TaggedProfileTemplate localTaggedProfileTemplate = (TaggedProfileTemplate)localIterator.next();
      TaggedProfile localTaggedProfile = localTaggedProfileTemplate.create(localObjectKeyTemplate, paramObjectId);
      add(localTaggedProfile);
    }
  }
  
  public IORImpl(ORB paramORB, String paramString, IORTemplateList paramIORTemplateList, ObjectId paramObjectId)
  {
    this(paramORB, paramString);
    this.iortemps = paramIORTemplateList;
    Iterator localIterator = paramIORTemplateList.iterator();
    while (localIterator.hasNext())
    {
      IORTemplate localIORTemplate = (IORTemplate)localIterator.next();
      addTaggedProfiles(localIORTemplate, paramObjectId);
    }
    makeImmutable();
  }
  
  public IORImpl(InputStream paramInputStream)
  {
    this((ORB)paramInputStream.orb(), paramInputStream.read_string());
    IdentifiableFactoryFinder localIdentifiableFactoryFinder = this.factory.getTaggedProfileFactoryFinder();
    EncapsulationUtility.readIdentifiableSequence(this, localIdentifiableFactoryFinder, paramInputStream);
    makeImmutable();
  }
  
  public String getTypeId()
  {
    return this.typeId;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_string(this.typeId);
    EncapsulationUtility.writeIdentifiableSequence(this, paramOutputStream);
  }
  
  public String stringify()
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
    localEncapsOutputStream.putEndian();
    write((OutputStream)localEncapsOutputStream);
    StringWriter localStringWriter = new StringWriter();
    try
    {
      localEncapsOutputStream.writeTo(new HexOutputStream(localStringWriter));
    }
    catch (IOException localIOException)
    {
      throw this.wrapper.stringifyWriteError(localIOException);
    }
    return "IOR:" + localStringWriter;
  }
  
  public synchronized void makeImmutable()
  {
    makeElementsImmutable();
    if (this.iortemps != null) {
      this.iortemps.makeImmutable();
    }
    super.makeImmutable();
  }
  
  public org.omg.IOP.IOR getIOPIOR()
  {
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.factory);
    write(localEncapsOutputStream);
    InputStream localInputStream = (InputStream)localEncapsOutputStream.create_input_stream();
    return IORHelper.read(localInputStream);
  }
  
  public boolean isNil()
  {
    return size() == 0;
  }
  
  public boolean isEquivalent(com.sun.corba.se.spi.ior.IOR paramIOR)
  {
    Iterator localIterator1 = iterator();
    Iterator localIterator2 = paramIOR.iterator();
    while ((localIterator1.hasNext()) && (localIterator2.hasNext()))
    {
      TaggedProfile localTaggedProfile1 = (TaggedProfile)localIterator1.next();
      TaggedProfile localTaggedProfile2 = (TaggedProfile)localIterator2.next();
      if (!localTaggedProfile1.isEquivalent(localTaggedProfile2)) {
        return false;
      }
    }
    return localIterator1.hasNext() == localIterator2.hasNext();
  }
  
  private void initializeIORTemplateList()
  {
    HashMap localHashMap = new HashMap();
    this.iortemps = IORFactories.makeIORTemplateList();
    Iterator localIterator = iterator();
    ObjectId localObjectId = null;
    while (localIterator.hasNext())
    {
      TaggedProfile localTaggedProfile = (TaggedProfile)localIterator.next();
      TaggedProfileTemplate localTaggedProfileTemplate = localTaggedProfile.getTaggedProfileTemplate();
      ObjectKeyTemplate localObjectKeyTemplate = localTaggedProfile.getObjectKeyTemplate();
      if (localObjectId == null) {
        localObjectId = localTaggedProfile.getObjectId();
      } else if (!localObjectId.equals(localTaggedProfile.getObjectId())) {
        throw this.wrapper.badOidInIorTemplateList();
      }
      IORTemplate localIORTemplate = (IORTemplate)localHashMap.get(localObjectKeyTemplate);
      if (localIORTemplate == null)
      {
        localIORTemplate = IORFactories.makeIORTemplate(localObjectKeyTemplate);
        localHashMap.put(localObjectKeyTemplate, localIORTemplate);
        this.iortemps.add(localIORTemplate);
      }
      localIORTemplate.add(localTaggedProfileTemplate);
    }
    this.iortemps.makeImmutable();
  }
  
  public synchronized IORTemplateList getIORTemplates()
  {
    if (this.iortemps == null) {
      initializeIORTemplateList();
    }
    return this.iortemps;
  }
  
  public IIOPProfile getProfile()
  {
    IIOPProfile localIIOPProfile = null;
    Iterator localIterator = iteratorById(0);
    if (localIterator.hasNext()) {
      localIIOPProfile = (IIOPProfile)localIterator.next();
    }
    if (localIIOPProfile != null) {
      return localIIOPProfile;
    }
    throw this.wrapper.iorMustHaveIiopProfile();
  }
}
