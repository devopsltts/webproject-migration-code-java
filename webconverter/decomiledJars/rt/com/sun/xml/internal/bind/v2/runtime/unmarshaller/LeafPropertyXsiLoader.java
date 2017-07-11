package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor.CompositeTransducedAccessorImpl;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class LeafPropertyXsiLoader
  extends Loader
{
  private final Loader defaultLoader;
  private final TransducedAccessor xacc;
  private final Accessor acc;
  
  public LeafPropertyXsiLoader(Loader paramLoader, TransducedAccessor paramTransducedAccessor, Accessor paramAccessor)
  {
    this.defaultLoader = paramLoader;
    this.expectText = true;
    this.xacc = paramTransducedAccessor;
    this.acc = paramAccessor;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    Loader localLoader = selectLoader(paramState, paramTagName);
    paramState.setLoader(localLoader);
    localLoader.startElement(paramState, paramTagName);
  }
  
  protected Loader selectLoader(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    JaxBeanInfo localJaxBeanInfo = null;
    Attributes localAttributes = paramTagName.atts;
    int i = localAttributes.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
    if (i >= 0)
    {
      String str = localAttributes.getValue(i);
      QName localQName = DatatypeConverterImpl._parseQName(str, localUnmarshallingContext);
      if (localQName == null) {
        return this.defaultLoader;
      }
      localJaxBeanInfo = localUnmarshallingContext.getJAXBContext().getGlobalType(localQName);
      if (localJaxBeanInfo == null) {
        return this.defaultLoader;
      }
      ClassBeanInfoImpl localClassBeanInfoImpl;
      try
      {
        localClassBeanInfoImpl = (ClassBeanInfoImpl)localJaxBeanInfo;
      }
      catch (ClassCastException localClassCastException)
      {
        return this.defaultLoader;
      }
      if (null == localClassBeanInfoImpl.getTransducer()) {
        return this.defaultLoader;
      }
      return new LeafPropertyLoader(new TransducedAccessor.CompositeTransducedAccessorImpl(paramState.getContext().getJAXBContext(), localClassBeanInfoImpl.getTransducer(), this.acc));
    }
    return this.defaultLoader;
  }
  
  public Collection<QName> getExpectedChildElements()
  {
    return this.defaultLoader.getExpectedChildElements();
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    return this.defaultLoader.getExpectedAttributes();
  }
}
