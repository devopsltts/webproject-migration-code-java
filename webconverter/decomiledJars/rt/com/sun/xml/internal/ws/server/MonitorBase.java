package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.external.amx.AMXGlassfish;
import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.InheritedAttributes;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.org.glassfish.gmbal.ManagedObjectManager.RegistrationDebugLevel;
import com.sun.org.glassfish.gmbal.ManagedObjectManagerFactory;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion.Setting;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.Stub;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public abstract class MonitorBase
{
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
  private static ManagementAssertion.Setting clientMonitoring = ManagementAssertion.Setting.NOT_SET;
  private static ManagementAssertion.Setting endpointMonitoring = ManagementAssertion.Setting.NOT_SET;
  private static int typelibDebug = -1;
  private static String registrationDebug = "NONE";
  private static boolean runtimeDebug = false;
  private static int maxUniqueEndpointRootNameRetries = 100;
  private static final String monitorProperty = "com.sun.xml.internal.ws.monitoring.";
  
  public MonitorBase() {}
  
  @NotNull
  public ManagedObjectManager createManagedObjectManager(WSEndpoint paramWSEndpoint)
  {
    Object localObject = paramWSEndpoint.getServiceName().getLocalPart() + "-" + paramWSEndpoint.getPortName().getLocalPart();
    if (((String)localObject).equals("-")) {
      localObject = "provider";
    }
    String str1 = getContextPath(paramWSEndpoint);
    if (str1 != null) {
      localObject = str1 + "-" + (String)localObject;
    }
    ManagedServiceAssertion localManagedServiceAssertion = ManagedServiceAssertion.getAssertion(paramWSEndpoint);
    if (localManagedServiceAssertion != null)
    {
      String str2 = localManagedServiceAssertion.getId();
      if (str2 != null) {
        localObject = str2;
      }
      if (localManagedServiceAssertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
        return disabled("This endpoint", (String)localObject);
      }
    }
    if (endpointMonitoring.equals(ManagementAssertion.Setting.OFF)) {
      return disabled("Global endpoint", (String)localObject);
    }
    return createMOMLoop((String)localObject, 0);
  }
  
  private String getContextPath(WSEndpoint paramWSEndpoint)
  {
    try
    {
      Container localContainer = paramWSEndpoint.getContainer();
      Method localMethod1 = localContainer.getClass().getDeclaredMethod("getSPI", new Class[] { Class.class });
      localMethod1.setAccessible(true);
      Class localClass = Class.forName("javax.servlet.ServletContext");
      Object localObject = localMethod1.invoke(localContainer, new Object[] { localClass });
      if (localObject != null)
      {
        Method localMethod2 = localClass.getDeclaredMethod("getContextPath", new Class[0]);
        localMethod2.setAccessible(true);
        return (String)localMethod2.invoke(localObject, new Object[0]);
      }
      return null;
    }
    catch (Throwable localThrowable)
    {
      logger.log(Level.FINEST, "getContextPath", localThrowable);
    }
    return null;
  }
  
  @NotNull
  public ManagedObjectManager createManagedObjectManager(Stub paramStub)
  {
    EndpointAddress localEndpointAddress = paramStub.requestContext.getEndpointAddress();
    if (localEndpointAddress == null) {
      return ManagedObjectManagerFactory.createNOOP();
    }
    Object localObject = localEndpointAddress.toString();
    ManagedClientAssertion localManagedClientAssertion = ManagedClientAssertion.getAssertion(paramStub.getPortInfo());
    if (localManagedClientAssertion != null)
    {
      String str = localManagedClientAssertion.getId();
      if (str != null) {
        localObject = str;
      }
      if (localManagedClientAssertion.monitoringAttribute() == ManagementAssertion.Setting.OFF) {
        return disabled("This client", (String)localObject);
      }
      if ((localManagedClientAssertion.monitoringAttribute() == ManagementAssertion.Setting.ON) && (clientMonitoring != ManagementAssertion.Setting.OFF)) {
        return createMOMLoop((String)localObject, 0);
      }
    }
    if ((clientMonitoring == ManagementAssertion.Setting.NOT_SET) || (clientMonitoring == ManagementAssertion.Setting.OFF)) {
      return disabled("Global client", (String)localObject);
    }
    return createMOMLoop((String)localObject, 0);
  }
  
  @NotNull
  private ManagedObjectManager disabled(String paramString1, String paramString2)
  {
    String str = paramString1 + " monitoring disabled. " + paramString2 + " will not be monitored";
    logger.log(Level.CONFIG, str);
    return ManagedObjectManagerFactory.createNOOP();
  }
  
  @NotNull
  private ManagedObjectManager createMOMLoop(String paramString, int paramInt)
  {
    boolean bool = AMXGlassfish.getGlassfishVersion() != null;
    ManagedObjectManager localManagedObjectManager = createMOM(bool);
    localManagedObjectManager = initMOM(localManagedObjectManager);
    localManagedObjectManager = createRoot(localManagedObjectManager, paramString, paramInt);
    return localManagedObjectManager;
  }
  
  @NotNull
  private ManagedObjectManager createMOM(boolean paramBoolean)
  {
    try
    {
      return new RewritingMOM(paramBoolean ? ManagedObjectManagerFactory.createFederated(AMXGlassfish.DEFAULT.serverMon(AMXGlassfish.DEFAULT.dasName())) : ManagedObjectManagerFactory.createStandalone("com.sun.metro"));
    }
    catch (Throwable localThrowable)
    {
      if (paramBoolean)
      {
        logger.log(Level.CONFIG, "Problem while attempting to federate with GlassFish AMX monitoring.  Trying standalone.", localThrowable);
        return createMOM(false);
      }
      logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", localThrowable);
    }
    return ManagedObjectManagerFactory.createNOOP();
  }
  
  @NotNull
  private ManagedObjectManager initMOM(ManagedObjectManager paramManagedObjectManager)
  {
    try
    {
      if (typelibDebug != -1) {
        paramManagedObjectManager.setTypelibDebug(typelibDebug);
      }
      if (registrationDebug.equals("FINE")) {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.FINE);
      } else if (registrationDebug.equals("NORMAL")) {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NORMAL);
      } else {
        paramManagedObjectManager.setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel.NONE);
      }
      paramManagedObjectManager.setRuntimeDebug(runtimeDebug);
      paramManagedObjectManager.suppressDuplicateRootReport(true);
      paramManagedObjectManager.stripPrefix(new String[] { "com.sun.xml.internal.ws.server", "com.sun.xml.internal.ws.rx.rm.runtime.sequence" });
      paramManagedObjectManager.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(ManagedData.class));
      paramManagedObjectManager.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(Description.class));
      paramManagedObjectManager.addAnnotation(WebServiceFeature.class, DummyWebServiceFeature.class.getAnnotation(InheritedAttributes.class));
      paramManagedObjectManager.suspendJMXRegistration();
    }
    catch (Throwable localThrowable)
    {
      try
      {
        paramManagedObjectManager.close();
      }
      catch (IOException localIOException)
      {
        logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", localIOException);
      }
      logger.log(Level.WARNING, "Ignoring exception - starting up without monitoring", localThrowable);
      return ManagedObjectManagerFactory.createNOOP();
    }
    return paramManagedObjectManager;
  }
  
  private ManagedObjectManager createRoot(ManagedObjectManager paramManagedObjectManager, String paramString, int paramInt)
  {
    String str1 = paramString + (paramInt == 0 ? "" : new StringBuilder().append("-").append(String.valueOf(paramInt)).toString());
    try
    {
      GmbalMBean localGmbalMBean = paramManagedObjectManager.createRoot(this, str1);
      if (localGmbalMBean != null)
      {
        ObjectName localObjectName = paramManagedObjectManager.getObjectName(paramManagedObjectManager.getRoot());
        if (localObjectName != null) {
          logger.log(Level.INFO, "Metro monitoring rootname successfully set to: {0}", localObjectName);
        }
        return paramManagedObjectManager;
      }
      try
      {
        paramManagedObjectManager.close();
      }
      catch (IOException localIOException)
      {
        logger.log(Level.CONFIG, "Ignoring exception caught when closing unused ManagedObjectManager", localIOException);
      }
      String str2 = "Duplicate Metro monitoring rootname: " + str1 + " : ";
      if (paramInt > maxUniqueEndpointRootNameRetries)
      {
        str3 = str2 + "Giving up.";
        logger.log(Level.INFO, str3);
        return ManagedObjectManagerFactory.createNOOP();
      }
      String str3 = str2 + "Will try to make unique";
      logger.log(Level.CONFIG, str3);
      return createMOMLoop(paramString, ++paramInt);
    }
    catch (Throwable localThrowable)
    {
      logger.log(Level.WARNING, "Error while creating monitoring root with name: " + paramString, localThrowable);
    }
    return ManagedObjectManagerFactory.createNOOP();
  }
  
  private static ManagementAssertion.Setting propertyToSetting(String paramString)
  {
    String str = System.getProperty(paramString);
    if (str == null) {
      return ManagementAssertion.Setting.NOT_SET;
    }
    str = str.toLowerCase();
    if ((str.equals("false")) || (str.equals("off"))) {
      return ManagementAssertion.Setting.OFF;
    }
    if ((str.equals("true")) || (str.equals("on"))) {
      return ManagementAssertion.Setting.ON;
    }
    return ManagementAssertion.Setting.NOT_SET;
  }
  
  static
  {
    try
    {
      endpointMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.endpoint");
      clientMonitoring = propertyToSetting("com.sun.xml.internal.ws.monitoring.client");
      Integer localInteger = Integer.getInteger("com.sun.xml.internal.ws.monitoring.typelibDebug");
      if (localInteger != null) {
        typelibDebug = localInteger.intValue();
      }
      String str = System.getProperty("com.sun.xml.internal.ws.monitoring.registrationDebug");
      if (str != null) {
        registrationDebug = str.toUpperCase();
      }
      str = System.getProperty("com.sun.xml.internal.ws.monitoring.runtimeDebug");
      if ((str != null) && (str.toLowerCase().equals("true"))) {
        runtimeDebug = true;
      }
      localInteger = Integer.getInteger("com.sun.xml.internal.ws.monitoring.maxUniqueEndpointRootNameRetries");
      if (localInteger != null) {
        maxUniqueEndpointRootNameRetries = localInteger.intValue();
      }
    }
    catch (Exception localException)
    {
      logger.log(Level.WARNING, "Error while reading monitoring properties", localException);
    }
  }
}
