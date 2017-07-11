package javax.naming.ldap;

import com.sun.naming.internal.VersionHelper;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;

public class StartTlsRequest
  implements ExtendedRequest
{
  public static final String OID = "1.3.6.1.4.1.1466.20037";
  private static final long serialVersionUID = 4441679576360753397L;
  
  public StartTlsRequest() {}
  
  public String getID()
  {
    return "1.3.6.1.4.1.1466.20037";
  }
  
  public byte[] getEncodedValue()
  {
    return null;
  }
  
  public ExtendedResponse createExtendedResponse(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NamingException
  {
    if ((paramString != null) && (!paramString.equals("1.3.6.1.4.1.1466.20037"))) {
      throw new ConfigurationException("Start TLS received the following response instead of 1.3.6.1.4.1.1466.20037: " + paramString);
    }
    StartTlsResponse localStartTlsResponse = null;
    ServiceLoader localServiceLoader = ServiceLoader.load(StartTlsResponse.class, getContextClassLoader());
    Iterator localIterator = localServiceLoader.iterator();
    while ((localStartTlsResponse == null) && (privilegedHasNext(localIterator))) {
      localStartTlsResponse = (StartTlsResponse)localIterator.next();
    }
    if (localStartTlsResponse != null) {
      return localStartTlsResponse;
    }
    try
    {
      VersionHelper localVersionHelper = VersionHelper.getVersionHelper();
      Class localClass = localVersionHelper.loadClass("com.sun.jndi.ldap.ext.StartTlsResponseImpl");
      localStartTlsResponse = (StartTlsResponse)localClass.newInstance();
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw wrapException(localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw wrapException(localInstantiationException);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw wrapException(localClassNotFoundException);
    }
    return localStartTlsResponse;
  }
  
  private ConfigurationException wrapException(Exception paramException)
  {
    ConfigurationException localConfigurationException = new ConfigurationException("Cannot load implementation of javax.naming.ldap.StartTlsResponse");
    localConfigurationException.setRootCause(paramException);
    return localConfigurationException;
  }
  
  private final ClassLoader getContextClassLoader()
  {
    (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return Thread.currentThread().getContextClassLoader();
      }
    });
  }
  
  private static final boolean privilegedHasNext(Iterator<StartTlsResponse> paramIterator)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(this.val$iter.hasNext());
      }
    });
    return localBoolean.booleanValue();
  }
}
