package javax.xml.ws.spi;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public abstract class Provider
{
  public static final String JAXWSPROVIDER_PROPERTY = "javax.xml.ws.spi.Provider";
  static final String DEFAULT_JAXWSPROVIDER = "com.sun.xml.internal.ws.spi.ProviderImpl";
  private static final Method loadMethod;
  private static final Method iteratorMethod;
  
  protected Provider() {}
  
  public static Provider provider()
  {
    try
    {
      Object localObject = getProviderUsingServiceLoader();
      if (localObject == null) {
        localObject = FactoryFinder.find("javax.xml.ws.spi.Provider", "com.sun.xml.internal.ws.spi.ProviderImpl");
      }
      if (!(localObject instanceof Provider))
      {
        Provider localProvider = Provider.class;
        String str = localProvider.getName().replace('.', '/') + ".class";
        ClassLoader localClassLoader = localProvider.getClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        URL localURL = localClassLoader.getResource(str);
        throw new LinkageError("ClassCastException: attempting to cast" + localObject.getClass().getClassLoader().getResource(str) + "to" + localURL.toString());
      }
      return (Provider)localObject;
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (Exception localException)
    {
      throw new WebServiceException("Unable to createEndpointReference Provider", localException);
    }
  }
  
  private static Provider getProviderUsingServiceLoader()
  {
    if (loadMethod != null)
    {
      Object localObject;
      try
      {
        localObject = loadMethod.invoke(null, new Object[] { Provider.class });
      }
      catch (Exception localException1)
      {
        throw new WebServiceException("Cannot invoke java.util.ServiceLoader#load()", localException1);
      }
      Iterator localIterator;
      try
      {
        localIterator = (Iterator)iteratorMethod.invoke(localObject, new Object[0]);
      }
      catch (Exception localException2)
      {
        throw new WebServiceException("Cannot invoke java.util.ServiceLoader#iterator()", localException2);
      }
      return localIterator.hasNext() ? (Provider)localIterator.next() : null;
    }
    return null;
  }
  
  public abstract ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass);
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class<? extends Service> paramClass, WebServiceFeature... paramVarArgs)
  {
    throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
  }
  
  public abstract Endpoint createEndpoint(String paramString, Object paramObject);
  
  public abstract Endpoint createAndPublishEndpoint(String paramString, Object paramObject);
  
  public abstract EndpointReference readEndpointReference(Source paramSource);
  
  public abstract <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, List<Element> paramList1, String paramString2, List<Element> paramList2);
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList1, String paramString2, List<Element> paramList2, List<Element> paramList3, Map<QName, String> paramMap)
  {
    throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
  }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs)
  {
    throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
  }
  
  public Endpoint createEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs)
  {
    throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
  }
  
  public Endpoint createEndpoint(String paramString, Class<?> paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs)
  {
    throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
  }
  
  static
  {
    Method localMethod1 = null;
    Method localMethod2 = null;
    try
    {
      Class localClass = Class.forName("java.util.ServiceLoader");
      localMethod1 = localClass.getMethod("load", new Class[] { Class.class });
      localMethod2 = localClass.getMethod("iterator", new Class[0]);
    }
    catch (ClassNotFoundException localClassNotFoundException) {}catch (NoSuchMethodException localNoSuchMethodException) {}
    loadMethod = localMethod1;
    iteratorMethod = localMethod2;
  }
}
