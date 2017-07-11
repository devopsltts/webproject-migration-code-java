package sun.net.www.protocol.http;

import java.io.IOException;
import java.lang.reflect.Constructor;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class Negotiator
{
  public Negotiator() {}
  
  static Negotiator getNegotiator(HttpCallerInfo paramHttpCallerInfo)
  {
    Constructor localConstructor;
    try
    {
      Class localClass = Class.forName("sun.net.www.protocol.http.spnego.NegotiatorImpl", true, null);
      localConstructor = localClass.getConstructor(new Class[] { HttpCallerInfo.class });
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      finest(localClassNotFoundException);
      return null;
    }
    catch (ReflectiveOperationException localReflectiveOperationException1)
    {
      throw new AssertionError(localReflectiveOperationException1);
    }
    try
    {
      return (Negotiator)localConstructor.newInstance(new Object[] { paramHttpCallerInfo });
    }
    catch (ReflectiveOperationException localReflectiveOperationException2)
    {
      finest(localReflectiveOperationException2);
      Throwable localThrowable = localReflectiveOperationException2.getCause();
      if ((localThrowable != null) && ((localThrowable instanceof Exception))) {
        finest((Exception)localThrowable);
      }
    }
    return null;
  }
  
  public abstract byte[] firstToken()
    throws IOException;
  
  public abstract byte[] nextToken(byte[] paramArrayOfByte)
    throws IOException;
  
  private static void finest(Exception paramException)
  {
    PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
    if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
      localPlatformLogger.finest("NegotiateAuthentication: " + paramException);
    }
  }
}
