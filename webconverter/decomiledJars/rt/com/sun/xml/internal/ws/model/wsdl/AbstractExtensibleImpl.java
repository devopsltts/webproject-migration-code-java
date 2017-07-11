package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;

abstract class AbstractExtensibleImpl
  extends AbstractObjectImpl
  implements WSDLExtensible
{
  protected final Set<WSDLExtension> extensions = new HashSet();
  protected List<UnknownWSDLExtension> notUnderstoodExtensions = new ArrayList();
  
  protected AbstractExtensibleImpl(XMLStreamReader paramXMLStreamReader)
  {
    super(paramXMLStreamReader);
  }
  
  protected AbstractExtensibleImpl(String paramString, int paramInt)
  {
    super(paramString, paramInt);
  }
  
  public final Iterable<WSDLExtension> getExtensions()
  {
    return this.extensions;
  }
  
  public final <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> paramClass)
  {
    ArrayList localArrayList = new ArrayList(this.extensions.size());
    Iterator localIterator = this.extensions.iterator();
    while (localIterator.hasNext())
    {
      WSDLExtension localWSDLExtension = (WSDLExtension)localIterator.next();
      if (paramClass.isInstance(localWSDLExtension)) {
        localArrayList.add(paramClass.cast(localWSDLExtension));
      }
    }
    return localArrayList;
  }
  
  public <T extends WSDLExtension> T getExtension(Class<T> paramClass)
  {
    Iterator localIterator = this.extensions.iterator();
    while (localIterator.hasNext())
    {
      WSDLExtension localWSDLExtension = (WSDLExtension)localIterator.next();
      if (paramClass.isInstance(localWSDLExtension)) {
        return (WSDLExtension)paramClass.cast(localWSDLExtension);
      }
    }
    return null;
  }
  
  public void addExtension(WSDLExtension paramWSDLExtension)
  {
    if (paramWSDLExtension == null) {
      throw new IllegalArgumentException();
    }
    this.extensions.add(paramWSDLExtension);
  }
  
  public List<? extends UnknownWSDLExtension> getNotUnderstoodExtensions()
  {
    return this.notUnderstoodExtensions;
  }
  
  public void addNotUnderstoodExtension(QName paramQName, Locator paramLocator)
  {
    this.notUnderstoodExtensions.add(new UnknownWSDLExtension(paramQName, paramLocator));
  }
  
  public boolean areRequiredExtensionsUnderstood()
  {
    if (this.notUnderstoodExtensions.size() != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder("Unknown WSDL extensibility elements:");
      Iterator localIterator = this.notUnderstoodExtensions.iterator();
      while (localIterator.hasNext())
      {
        UnknownWSDLExtension localUnknownWSDLExtension = (UnknownWSDLExtension)localIterator.next();
        localStringBuilder.append('\n').append(localUnknownWSDLExtension.toString());
      }
      throw new WebServiceException(localStringBuilder.toString());
    }
    return true;
  }
  
  protected static class UnknownWSDLExtension
    implements WSDLExtension, WSDLObject
  {
    private final QName extnEl;
    private final Locator locator;
    
    public UnknownWSDLExtension(QName paramQName, Locator paramLocator)
    {
      this.extnEl = paramQName;
      this.locator = paramLocator;
    }
    
    public QName getName()
    {
      return this.extnEl;
    }
    
    @NotNull
    public Locator getLocation()
    {
      return this.locator;
    }
    
    public String toString()
    {
      return this.extnEl + " " + UtilMessages.UTIL_LOCATION(Integer.valueOf(this.locator.getLineNumber()), this.locator.getSystemId());
    }
  }
}
