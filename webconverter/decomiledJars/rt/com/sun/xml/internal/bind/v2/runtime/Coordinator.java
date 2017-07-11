package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.v2.ClassFactory;
import java.util.HashMap;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class Coordinator
  implements ErrorHandler, ValidationEventHandler
{
  private final HashMap<Class<? extends XmlAdapter>, XmlAdapter> adapters = new HashMap();
  private static final ThreadLocal<Coordinator> activeTable = new ThreadLocal();
  private Coordinator old;
  
  public Coordinator() {}
  
  public final XmlAdapter putAdapter(Class<? extends XmlAdapter> paramClass, XmlAdapter paramXmlAdapter)
  {
    if (paramXmlAdapter == null) {
      return (XmlAdapter)this.adapters.remove(paramClass);
    }
    return (XmlAdapter)this.adapters.put(paramClass, paramXmlAdapter);
  }
  
  public final <T extends XmlAdapter> T getAdapter(Class<T> paramClass)
  {
    XmlAdapter localXmlAdapter = (XmlAdapter)paramClass.cast(this.adapters.get(paramClass));
    if (localXmlAdapter == null)
    {
      localXmlAdapter = (XmlAdapter)ClassFactory.create(paramClass);
      putAdapter(paramClass, localXmlAdapter);
    }
    return localXmlAdapter;
  }
  
  public <T extends XmlAdapter> boolean containsAdapter(Class<T> paramClass)
  {
    return this.adapters.containsKey(paramClass);
  }
  
  protected final void pushCoordinator()
  {
    this.old = ((Coordinator)activeTable.get());
    activeTable.set(this);
  }
  
  protected final void popCoordinator()
  {
    if (this.old != null) {
      activeTable.set(this.old);
    } else {
      activeTable.remove();
    }
    this.old = null;
  }
  
  public static Coordinator _getInstance()
  {
    return (Coordinator)activeTable.get();
  }
  
  protected abstract ValidationEventLocator getLocation();
  
  public final void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(1, paramSAXParseException);
  }
  
  public final void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(0, paramSAXParseException);
  }
  
  public final void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    propagateEvent(2, paramSAXParseException);
  }
  
  private void propagateEvent(int paramInt, SAXParseException paramSAXParseException)
    throws SAXException
  {
    ValidationEventImpl localValidationEventImpl = new ValidationEventImpl(paramInt, paramSAXParseException.getMessage(), getLocation());
    Exception localException = paramSAXParseException.getException();
    if (localException != null) {
      localValidationEventImpl.setLinkedException(localException);
    } else {
      localValidationEventImpl.setLinkedException(paramSAXParseException);
    }
    boolean bool = handleEvent(localValidationEventImpl);
    if (!bool) {
      throw paramSAXParseException;
    }
  }
}
