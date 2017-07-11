package javax.management.remote.rmi;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.internal.ClientCommunicatorAdmin;
import com.sun.jmx.remote.internal.ClientListenerInfo;
import com.sun.jmx.remote.internal.ClientNotifForwarder;
import com.sun.jmx.remote.internal.IIOPHelper;
import com.sun.jmx.remote.internal.ProxyRef;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.MarshalException;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.remote.JMXAddressable;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.NotificationResult;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.security.auth.Subject;
import sun.reflect.misc.ReflectUtil;
import sun.rmi.server.UnicastRef2;
import sun.rmi.transport.LiveRef;

public class RMIConnector
  implements JMXConnector, Serializable, JMXAddressable
{
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.rmi", "RMIConnector");
  private static final long serialVersionUID = 817323035842634473L;
  private static final String rmiServerImplStubClassName = RMIServer.class.getName() + "Impl_Stub";
  private static final Class<?> rmiServerImplStubClass;
  private static final String rmiConnectionImplStubClassName = RMIConnection.class.getName() + "Impl_Stub";
  private static final Class<?> rmiConnectionImplStubClass;
  private static final String pRefClassName = "com.sun.jmx.remote.internal.PRef";
  private static final Constructor<?> proxyRefConstructor;
  private static final String iiopConnectionStubClassName = "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub";
  private static final String proxyStubClassName = "com.sun.jmx.remote.protocol.iiop.ProxyStub";
  private static final String ProxyInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.ProxyInputStream";
  private static final String pInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.PInputStream";
  private static final Class<?> proxyStubClass;
  private static final byte[] base64ToInt = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
  private final RMIServer rmiServer;
  private final JMXServiceURL jmxServiceURL;
  private transient Map<String, Object> env;
  private transient ClassLoader defaultClassLoader;
  private transient RMIConnection connection;
  private transient String connectionId;
  private transient long clientNotifSeqNo = 0L;
  private transient WeakHashMap<Subject, WeakReference<MBeanServerConnection>> rmbscMap;
  private transient WeakReference<MBeanServerConnection> nullSubjectConnRef = null;
  private transient RMINotifClient rmiNotifClient;
  private transient long clientNotifCounter = 0L;
  private transient boolean connected;
  private transient boolean terminated;
  private transient Exception closeException;
  private transient NotificationBroadcasterSupport connectionBroadcaster;
  private transient ClientCommunicatorAdmin communicatorAdmin;
  private static volatile WeakReference<Object> orb = null;
  
  private RMIConnector(RMIServer paramRMIServer, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
  {
    if ((paramRMIServer == null) && (paramJMXServiceURL == null)) {
      throw new IllegalArgumentException("rmiServer and jmxServiceURL both null");
    }
    initTransients();
    this.rmiServer = paramRMIServer;
    this.jmxServiceURL = paramJMXServiceURL;
    if (paramMap == null)
    {
      this.env = Collections.emptyMap();
    }
    else
    {
      EnvHelp.checkAttributes(paramMap);
      this.env = Collections.unmodifiableMap(paramMap);
    }
  }
  
  public RMIConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap)
  {
    this(null, paramJMXServiceURL, paramMap);
  }
  
  public RMIConnector(RMIServer paramRMIServer, Map<String, ?> paramMap)
  {
    this(paramRMIServer, null, paramMap);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
    localStringBuilder.append(":");
    if (this.rmiServer != null) {
      localStringBuilder.append(" rmiServer=").append(this.rmiServer.toString());
    }
    if (this.jmxServiceURL != null)
    {
      if (this.rmiServer != null) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(" jmxServiceURL=").append(this.jmxServiceURL.toString());
    }
    return localStringBuilder.toString();
  }
  
  public JMXServiceURL getAddress()
  {
    return this.jmxServiceURL;
  }
  
  public void connect()
    throws IOException
  {
    connect(null);
  }
  
  public synchronized void connect(Map<String, ?> paramMap)
    throws IOException
  {
    boolean bool1 = logger.traceOn();
    String str1 = bool1 ? "[" + toString() + "]" : null;
    if (this.terminated)
    {
      logger.trace("connect", str1 + " already closed.");
      throw new IOException("Connector closed");
    }
    if (this.connected)
    {
      logger.trace("connect", str1 + " already connected.");
      return;
    }
    try
    {
      if (bool1) {
        logger.trace("connect", str1 + " connecting...");
      }
      HashMap localHashMap = new HashMap(this.env == null ? Collections.emptyMap() : this.env);
      if (paramMap != null)
      {
        EnvHelp.checkAttributes(paramMap);
        localHashMap.putAll(paramMap);
      }
      if (bool1) {
        logger.trace("connect", str1 + " finding stub...");
      }
      localObject1 = this.rmiServer != null ? this.rmiServer : findRMIServer(this.jmxServiceURL, localHashMap);
      String str2 = (String)localHashMap.get("jmx.remote.x.check.stub");
      boolean bool2 = EnvHelp.computeBooleanFromString(str2);
      if (bool2) {
        checkStub((Remote)localObject1, rmiServerImplStubClass);
      }
      if (bool1) {
        logger.trace("connect", str1 + " connecting stub...");
      }
      localObject1 = connectStub((RMIServer)localObject1, localHashMap);
      str1 = bool1 ? "[" + toString() + "]" : null;
      if (bool1) {
        logger.trace("connect", str1 + " getting connection...");
      }
      Object localObject2 = localHashMap.get("jmx.remote.credentials");
      try
      {
        this.connection = getConnection((RMIServer)localObject1, localObject2, bool2);
      }
      catch (RemoteException localRemoteException)
      {
        if (this.jmxServiceURL != null)
        {
          String str3 = this.jmxServiceURL.getProtocol();
          localObject3 = this.jmxServiceURL.getURLPath();
          if (("rmi".equals(str3)) && (((String)localObject3).startsWith("/jndi/iiop:")))
          {
            MalformedURLException localMalformedURLException = new MalformedURLException("Protocol is rmi but JNDI scheme is iiop: " + this.jmxServiceURL);
            localMalformedURLException.initCause(localRemoteException);
            throw localMalformedURLException;
          }
        }
        throw localRemoteException;
      }
      if (bool1) {
        logger.trace("connect", str1 + " getting class loader...");
      }
      this.defaultClassLoader = EnvHelp.resolveClientClassLoader(localHashMap);
      localHashMap.put("jmx.remote.default.class.loader", this.defaultClassLoader);
      this.rmiNotifClient = new RMINotifClient(this.defaultClassLoader, localHashMap);
      this.env = localHashMap;
      long l = EnvHelp.getConnectionCheckPeriod(localHashMap);
      this.communicatorAdmin = new RMIClientCommunicatorAdmin(l);
      this.connected = true;
      this.connectionId = getConnectionId();
      Object localObject3 = new JMXConnectionNotification("jmx.remote.connection.opened", this, this.connectionId, this.clientNotifSeqNo++, "Successful connection", null);
      sendNotification((Notification)localObject3);
      if (bool1) {
        logger.trace("connect", str1 + " done...");
      }
    }
    catch (IOException localIOException)
    {
      if (bool1) {
        logger.trace("connect", str1 + " failed to connect: " + localIOException);
      }
      throw localIOException;
    }
    catch (RuntimeException localRuntimeException)
    {
      if (bool1) {
        logger.trace("connect", str1 + " failed to connect: " + localRuntimeException);
      }
      throw localRuntimeException;
    }
    catch (NamingException localNamingException)
    {
      Object localObject1 = "Failed to retrieve RMIServer stub: " + localNamingException;
      if (bool1) {
        logger.trace("connect", str1 + " " + (String)localObject1);
      }
      throw ((IOException)EnvHelp.initCause(new IOException((String)localObject1), localNamingException));
    }
  }
  
  public synchronized String getConnectionId()
    throws IOException
  {
    if ((this.terminated) || (!this.connected))
    {
      if (logger.traceOn()) {
        logger.trace("getConnectionId", "[" + toString() + "] not connected.");
      }
      throw new IOException("Not connected");
    }
    return this.connection.getConnectionId();
  }
  
  public synchronized MBeanServerConnection getMBeanServerConnection()
    throws IOException
  {
    return getMBeanServerConnection(null);
  }
  
  public synchronized MBeanServerConnection getMBeanServerConnection(Subject paramSubject)
    throws IOException
  {
    if (this.terminated)
    {
      if (logger.traceOn()) {
        logger.trace("getMBeanServerConnection", "[" + toString() + "] already closed.");
      }
      throw new IOException("Connection closed");
    }
    if (!this.connected)
    {
      if (logger.traceOn()) {
        logger.trace("getMBeanServerConnection", "[" + toString() + "] is not connected.");
      }
      throw new IOException("Not connected");
    }
    return getConnectionWithSubject(paramSubject);
  }
  
  public void addConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    if (paramNotificationListener == null) {
      throw new NullPointerException("listener");
    }
    this.connectionBroadcaster.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void removeConnectionNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    if (paramNotificationListener == null) {
      throw new NullPointerException("listener");
    }
    this.connectionBroadcaster.removeNotificationListener(paramNotificationListener);
  }
  
  public void removeConnectionNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
    throws ListenerNotFoundException
  {
    if (paramNotificationListener == null) {
      throw new NullPointerException("listener");
    }
    this.connectionBroadcaster.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  private void sendNotification(Notification paramNotification)
  {
    this.connectionBroadcaster.sendNotification(paramNotification);
  }
  
  public synchronized void close()
    throws IOException
  {
    close(false);
  }
  
  private synchronized void close(boolean paramBoolean)
    throws IOException
  {
    boolean bool1 = logger.traceOn();
    boolean bool2 = logger.debugOn();
    String str1 = bool1 ? "[" + toString() + "]" : null;
    if (!paramBoolean) {
      if (this.terminated)
      {
        if (this.closeException == null) {
          if (bool1) {
            logger.trace("close", str1 + " already closed.");
          }
        }
      }
      else {
        this.terminated = true;
      }
    }
    if ((this.closeException != null) && (bool1) && (bool1))
    {
      logger.trace("close", str1 + " had failed: " + this.closeException);
      logger.trace("close", str1 + " attempting to close again.");
    }
    String str2 = null;
    if (this.connected) {
      str2 = this.connectionId;
    }
    this.closeException = null;
    if (bool1) {
      logger.trace("close", str1 + " closing.");
    }
    if (this.communicatorAdmin != null) {
      this.communicatorAdmin.terminate();
    }
    if (this.rmiNotifClient != null) {
      try
      {
        this.rmiNotifClient.terminate();
        if (bool1) {
          logger.trace("close", str1 + " RMI Notification client terminated.");
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        this.closeException = localRuntimeException;
        if (bool1) {
          logger.trace("close", str1 + " Failed to terminate RMI Notification client: " + localRuntimeException);
        }
        if (bool2) {
          logger.debug("close", localRuntimeException);
        }
      }
    }
    if (this.connection != null) {
      try
      {
        this.connection.close();
        if (bool1) {
          logger.trace("close", str1 + " closed.");
        }
      }
      catch (NoSuchObjectException localNoSuchObjectException) {}catch (IOException localIOException)
      {
        this.closeException = localIOException;
        if (bool1) {
          logger.trace("close", str1 + " Failed to close RMIServer: " + localIOException);
        }
        if (bool2) {
          logger.debug("close", localIOException);
        }
      }
    }
    this.rmbscMap.clear();
    Object localObject;
    if (str2 != null)
    {
      localObject = new JMXConnectionNotification("jmx.remote.connection.closed", this, str2, this.clientNotifSeqNo++, "Client has been closed", null);
      sendNotification((Notification)localObject);
    }
    if (this.closeException != null)
    {
      if (bool1) {
        logger.trace("close", str1 + " failed to close: " + this.closeException);
      }
      if ((this.closeException instanceof IOException)) {
        throw ((IOException)this.closeException);
      }
      if ((this.closeException instanceof RuntimeException)) {
        throw ((RuntimeException)this.closeException);
      }
      localObject = new IOException("Failed to close: " + this.closeException);
      throw ((IOException)EnvHelp.initCause((Throwable)localObject, this.closeException));
    }
  }
  
  private Integer addListenerWithSubject(ObjectName paramObjectName, MarshalledObject<NotificationFilter> paramMarshalledObject, Subject paramSubject, boolean paramBoolean)
    throws InstanceNotFoundException, IOException
  {
    boolean bool = logger.debugOn();
    if (bool) {
      logger.debug("addListenerWithSubject", "(ObjectName,MarshalledObject,Subject)");
    }
    ObjectName[] arrayOfObjectName = { paramObjectName };
    MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { paramMarshalledObject });
    Subject[] arrayOfSubject = { paramSubject };
    Integer[] arrayOfInteger = addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, paramBoolean);
    if (bool) {
      logger.debug("addListenerWithSubject", "listenerID=" + arrayOfInteger[0]);
    }
    return arrayOfInteger[0];
  }
  
  private Integer[] addListenersWithSubjects(ObjectName[] paramArrayOfObjectName, MarshalledObject<NotificationFilter>[] paramArrayOfMarshalledObject, Subject[] paramArrayOfSubject, boolean paramBoolean)
    throws InstanceNotFoundException, IOException
  {
    boolean bool = logger.debugOn();
    if (bool) {
      logger.debug("addListenersWithSubjects", "(ObjectName[],MarshalledObject[],Subject[])");
    }
    ClassLoader localClassLoader = pushDefaultClassLoader();
    Integer[] arrayOfInteger = null;
    try
    {
      arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
    }
    catch (NoSuchObjectException localNoSuchObjectException)
    {
      if (paramBoolean)
      {
        this.communicatorAdmin.gotIOException(localNoSuchObjectException);
        arrayOfInteger = this.connection.addNotificationListeners(paramArrayOfObjectName, paramArrayOfMarshalledObject, paramArrayOfSubject);
      }
      else
      {
        throw localNoSuchObjectException;
      }
    }
    catch (IOException localIOException)
    {
      this.communicatorAdmin.gotIOException(localIOException);
    }
    finally
    {
      popDefaultClassLoader(localClassLoader);
    }
    if (bool) {
      logger.debug("addListenersWithSubjects", "registered " + (arrayOfInteger == null ? 0 : arrayOfInteger.length) + " listener(s)");
    }
    return arrayOfInteger;
  }
  
  static RMIServer connectStub(RMIServer paramRMIServer, Map<String, ?> paramMap)
    throws IOException
  {
    if (IIOPHelper.isStub(paramRMIServer)) {
      try
      {
        IIOPHelper.getOrb(paramRMIServer);
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        IIOPHelper.connect(paramRMIServer, resolveOrb(paramMap));
      }
    }
    return paramRMIServer;
  }
  
  static Object resolveOrb(Map<String, ?> paramMap)
    throws IOException
  {
    if (paramMap != null)
    {
      localObject1 = paramMap.get("java.naming.corba.orb");
      if ((localObject1 != null) && (!IIOPHelper.isOrb(localObject1))) {
        throw new IllegalArgumentException("java.naming.corba.orb must be an instance of org.omg.CORBA.ORB.");
      }
      if (localObject1 != null) {
        return localObject1;
      }
    }
    Object localObject1 = orb == null ? null : orb.get();
    if (localObject1 != null) {
      return localObject1;
    }
    Object localObject2 = IIOPHelper.createOrb((String[])null, (Properties)null);
    orb = new WeakReference(localObject2);
    return localObject2;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    if ((this.rmiServer == null) && (this.jmxServiceURL == null)) {
      throw new InvalidObjectException("rmiServer and jmxServiceURL both null");
    }
    initTransients();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if ((this.rmiServer == null) && (this.jmxServiceURL == null)) {
      throw new InvalidObjectException("rmiServer and jmxServiceURL both null.");
    }
    connectStub(this.rmiServer, this.env);
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void initTransients()
  {
    this.rmbscMap = new WeakHashMap();
    this.connected = false;
    this.terminated = false;
    this.connectionBroadcaster = new NotificationBroadcasterSupport();
  }
  
  private static void checkStub(Remote paramRemote, Class<?> paramClass)
  {
    if (paramRemote.getClass() != paramClass)
    {
      if (!Proxy.isProxyClass(paramRemote.getClass())) {
        throw new SecurityException("Expecting a " + paramClass.getName() + " stub!");
      }
      localObject = Proxy.getInvocationHandler(paramRemote);
      if (localObject.getClass() != RemoteObjectInvocationHandler.class) {
        throw new SecurityException("Expecting a dynamic proxy instance with a " + RemoteObjectInvocationHandler.class.getName() + " invocation handler!");
      }
      paramRemote = (Remote)localObject;
    }
    Object localObject = ((RemoteObject)paramRemote).getRef();
    if (localObject.getClass() != UnicastRef2.class) {
      throw new SecurityException("Expecting a " + UnicastRef2.class.getName() + " remote reference in stub!");
    }
    LiveRef localLiveRef = ((UnicastRef2)localObject).getLiveRef();
    RMIClientSocketFactory localRMIClientSocketFactory = localLiveRef.getClientSocketFactory();
    if ((localRMIClientSocketFactory == null) || (localRMIClientSocketFactory.getClass() != SslRMIClientSocketFactory.class)) {
      throw new SecurityException("Expecting a " + SslRMIClientSocketFactory.class.getName() + " RMI client socket factory in stub!");
    }
  }
  
  private RMIServer findRMIServer(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap)
    throws NamingException, IOException
  {
    boolean bool = RMIConnectorServer.isIiopURL(paramJMXServiceURL, true);
    if (bool) {
      paramMap.put("java.naming.corba.orb", resolveOrb(paramMap));
    }
    String str1 = paramJMXServiceURL.getURLPath();
    int i = str1.indexOf(';');
    if (i < 0) {
      i = str1.length();
    }
    if (str1.startsWith("/jndi/")) {
      return findRMIServerJNDI(str1.substring(6, i), paramMap, bool);
    }
    if (str1.startsWith("/stub/")) {
      return findRMIServerJRMP(str1.substring(6, i), paramMap, bool);
    }
    if (str1.startsWith("/ior/"))
    {
      if (!IIOPHelper.isAvailable()) {
        throw new IOException("iiop protocol not available");
      }
      return findRMIServerIIOP(str1.substring(5, i), paramMap, bool);
    }
    String str2 = "URL path must begin with /jndi/ or /stub/ or /ior/: " + str1;
    throw new MalformedURLException(str2);
  }
  
  private RMIServer findRMIServerJNDI(String paramString, Map<String, ?> paramMap, boolean paramBoolean)
    throws NamingException
  {
    InitialContext localInitialContext = new InitialContext(EnvHelp.mapToHashtable(paramMap));
    Object localObject = localInitialContext.lookup(paramString);
    localInitialContext.close();
    if (paramBoolean) {
      return narrowIIOPServer(localObject);
    }
    return narrowJRMPServer(localObject);
  }
  
  private static RMIServer narrowJRMPServer(Object paramObject)
  {
    return (RMIServer)paramObject;
  }
  
  private static RMIServer narrowIIOPServer(Object paramObject)
  {
    try
    {
      return (RMIServer)IIOPHelper.narrow(paramObject, RMIServer.class);
    }
    catch (ClassCastException localClassCastException)
    {
      if (logger.traceOn()) {
        logger.trace("narrowIIOPServer", "Failed to narrow objref=" + paramObject + ": " + localClassCastException);
      }
      if (logger.debugOn()) {
        logger.debug("narrowIIOPServer", localClassCastException);
      }
    }
    return null;
  }
  
  private RMIServer findRMIServerIIOP(String paramString, Map<String, ?> paramMap, boolean paramBoolean)
  {
    Object localObject1 = paramMap.get("java.naming.corba.orb");
    Object localObject2 = IIOPHelper.stringToObject(localObject1, paramString);
    return (RMIServer)IIOPHelper.narrow(localObject2, RMIServer.class);
  }
  
  private RMIServer findRMIServerJRMP(String paramString, Map<String, ?> paramMap, boolean paramBoolean)
    throws IOException
  {
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = base64ToByteArray(paramString);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new MalformedURLException("Bad BASE64 encoding: " + localIllegalArgumentException.getMessage());
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    ClassLoader localClassLoader = EnvHelp.resolveClientClassLoader(paramMap);
    ObjectInputStreamWithLoader localObjectInputStreamWithLoader = localClassLoader == null ? new ObjectInputStream(localByteArrayInputStream) : new ObjectInputStreamWithLoader(localByteArrayInputStream, localClassLoader);
    Object localObject;
    try
    {
      localObject = localObjectInputStreamWithLoader.readObject();
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new MalformedURLException("Class not found: " + localClassNotFoundException);
    }
    return (RMIServer)localObject;
  }
  
  private MBeanServerConnection getConnectionWithSubject(Subject paramSubject)
  {
    Object localObject = null;
    if (paramSubject == null)
    {
      if ((this.nullSubjectConnRef == null) || ((localObject = (MBeanServerConnection)this.nullSubjectConnRef.get()) == null))
      {
        localObject = new RemoteMBeanServerConnection(null);
        this.nullSubjectConnRef = new WeakReference(localObject);
      }
    }
    else
    {
      WeakReference localWeakReference = (WeakReference)this.rmbscMap.get(paramSubject);
      if ((localWeakReference == null) || ((localObject = (MBeanServerConnection)localWeakReference.get()) == null))
      {
        localObject = new RemoteMBeanServerConnection(paramSubject);
        this.rmbscMap.put(paramSubject, new WeakReference(localObject));
      }
    }
    return localObject;
  }
  
  private static RMIConnection shadowJrmpStub(RemoteObject paramRemoteObject)
    throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException
  {
    RemoteRef localRemoteRef1 = paramRemoteObject.getRef();
    RemoteRef localRemoteRef2 = (RemoteRef)proxyRefConstructor.newInstance(new Object[] { localRemoteRef1 });
    Constructor localConstructor = rmiConnectionImplStubClass.getConstructor(new Class[] { RemoteRef.class });
    Object[] arrayOfObject = { localRemoteRef2 };
    RMIConnection localRMIConnection = (RMIConnection)localConstructor.newInstance(arrayOfObject);
    return localRMIConnection;
  }
  
  private static RMIConnection shadowIiopStub(Object paramObject)
    throws InstantiationException, IllegalAccessException
  {
    Object localObject = null;
    try
    {
      localObject = AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Object run()
          throws Exception
        {
          return RMIConnector.proxyStubClass.newInstance();
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new InternalError();
    }
    IIOPHelper.setDelegate(localObject, IIOPHelper.getDelegate(paramObject));
    return (RMIConnection)localObject;
  }
  
  private static RMIConnection getConnection(RMIServer paramRMIServer, Object paramObject, boolean paramBoolean)
    throws IOException
  {
    RMIConnection localRMIConnection = paramRMIServer.newClient(paramObject);
    if (paramBoolean) {
      checkStub(localRMIConnection, rmiConnectionImplStubClass);
    }
    try
    {
      if (localRMIConnection.getClass() == rmiConnectionImplStubClass) {
        return shadowJrmpStub((RemoteObject)localRMIConnection);
      }
      if (localRMIConnection.getClass().getName().equals("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub")) {
        return shadowIiopStub(localRMIConnection);
      }
      logger.trace("getConnection", "Did not wrap " + localRMIConnection.getClass() + " to foil " + "stack search for classes: class loading semantics " + "may be incorrect");
    }
    catch (Exception localException)
    {
      logger.error("getConnection", "Could not wrap " + localRMIConnection.getClass() + " to foil " + "stack search for classes: class loading semantics " + "may be incorrect: " + localException);
      logger.debug("getConnection", localException);
    }
    return localRMIConnection;
  }
  
  private static byte[] base64ToByteArray(String paramString)
  {
    int i = paramString.length();
    int j = i / 4;
    if (4 * j != i) {
      throw new IllegalArgumentException("String length must be a multiple of four.");
    }
    int k = 0;
    int m = j;
    if (i != 0)
    {
      if (paramString.charAt(i - 1) == '=')
      {
        k++;
        m--;
      }
      if (paramString.charAt(i - 2) == '=') {
        k++;
      }
    }
    byte[] arrayOfByte = new byte[3 * j - k];
    int n = 0;
    int i1 = 0;
    int i3;
    int i4;
    for (int i2 = 0; i2 < m; i2++)
    {
      i3 = base64toInt(paramString.charAt(n++));
      i4 = base64toInt(paramString.charAt(n++));
      int i5 = base64toInt(paramString.charAt(n++));
      int i6 = base64toInt(paramString.charAt(n++));
      arrayOfByte[(i1++)] = ((byte)(i3 << 2 | i4 >> 4));
      arrayOfByte[(i1++)] = ((byte)(i4 << 4 | i5 >> 2));
      arrayOfByte[(i1++)] = ((byte)(i5 << 6 | i6));
    }
    if (k != 0)
    {
      i2 = base64toInt(paramString.charAt(n++));
      i3 = base64toInt(paramString.charAt(n++));
      arrayOfByte[(i1++)] = ((byte)(i2 << 2 | i3 >> 4));
      if (k == 1)
      {
        i4 = base64toInt(paramString.charAt(n++));
        arrayOfByte[(i1++)] = ((byte)(i3 << 4 | i4 >> 2));
      }
    }
    return arrayOfByte;
  }
  
  private static int base64toInt(char paramChar)
  {
    int i;
    if (paramChar >= base64ToInt.length) {
      i = -1;
    } else {
      i = base64ToInt[paramChar];
    }
    if (i < 0) {
      throw new IllegalArgumentException("Illegal character " + paramChar);
    }
    return i;
  }
  
  private ClassLoader pushDefaultClassLoader()
  {
    final Thread localThread = Thread.currentThread();
    ClassLoader localClassLoader = localThread.getContextClassLoader();
    if (this.defaultClassLoader != null) {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          localThread.setContextClassLoader(RMIConnector.this.defaultClassLoader);
          return null;
        }
      });
    }
    return localClassLoader;
  }
  
  private void popDefaultClassLoader(final ClassLoader paramClassLoader)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        Thread.currentThread().setContextClassLoader(paramClassLoader);
        return null;
      }
    });
  }
  
  private static String objects(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null) {
      return "null";
    }
    return Arrays.asList(paramArrayOfObject).toString();
  }
  
  private static String strings(String[] paramArrayOfString)
  {
    return objects(paramArrayOfString);
  }
  
  static
  {
    byte[] arrayOfByte = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\000.\000\027\n\000\005\000\r\t\000\004\000\016\013\000\017\000\020\007\000\021\007\000\022\001\000\006<init>\001\000\036(Ljava/rmi/server/RemoteRef;)V\001\000\004Code\001\000\006invoke\001\000S(Ljava/rmi/Remote;Ljava/lang/reflect/Method;[Ljava/lang/Object;J)Ljava/lang/Object;\001\000\nExceptions\007\000\023\f\000\006\000\007\f\000\024\000\025\007\000\026\f\000\t\000\n\001\000 com/sun/jmx/remote/internal/PRef\001\000$com/sun/jmx/remote/internal/ProxyRef\001\000\023java/lang/Exception\001\000\003ref\001\000\033Ljava/rmi/server/RemoteRef;\001\000\031java/rmi/server/RemoteRef\000!\000\004\000\005\000\000\000\000\000\002\000\001\000\006\000\007\000\001\000\b\000\000\000\022\000\002\000\002\000\000\000\006*+·\000\001±\000\000\000\000\000\001\000\t\000\n\000\002\000\b\000\000\000\033\000\006\000\006\000\000\000\017*´\000\002+,-\026\004¹\000\003\006\000°\000\000\000\000\000\013\000\000\000\004\000\001\000\f\000\000");
    Object localObject1 = new PrivilegedExceptionAction()
    {
      public Constructor<?> run()
        throws Exception
      {
        RMIConnector localRMIConnector = RMIConnector.class;
        ClassLoader localClassLoader = localRMIConnector.getClassLoader();
        ProtectionDomain localProtectionDomain = localRMIConnector.getProtectionDomain();
        String[] arrayOfString = { ProxyRef.class.getName() };
        NoCallStackClassLoader localNoCallStackClassLoader = new NoCallStackClassLoader("com.sun.jmx.remote.internal.PRef", this.val$pRefByteCode, arrayOfString, localClassLoader, localProtectionDomain);
        Class localClass = localNoCallStackClassLoader.loadClass("com.sun.jmx.remote.internal.PRef");
        return localClass.getConstructor(new Class[] { RemoteRef.class });
      }
    };
    try
    {
      localObject2 = Class.forName(rmiServerImplStubClassName);
    }
    catch (Exception localException1)
    {
      logger.error("<clinit>", "Failed to instantiate " + rmiServerImplStubClassName + ": " + localException1);
      logger.debug("<clinit>", localException1);
      localObject2 = null;
    }
    rmiServerImplStubClass = (Class)localObject2;
    try
    {
      localObject3 = Class.forName(rmiConnectionImplStubClassName);
      localObject4 = (Constructor)AccessController.doPrivileged((PrivilegedExceptionAction)localObject1);
    }
    catch (Exception localException2)
    {
      logger.error("<clinit>", "Failed to initialize proxy reference constructor for " + rmiConnectionImplStubClassName + ": " + localException2);
      logger.debug("<clinit>", localException2);
      localObject3 = null;
      localObject4 = null;
    }
    rmiConnectionImplStubClass = (Class)localObject3;
    proxyRefConstructor = (Constructor)localObject4;
    localObject1 = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\0003\000+\n\000\f\000\030\007\000\031\n\000\f\000\032\n\000\002\000\033\007\000\034\n\000\005\000\035\n\000\005\000\036\n\000\005\000\037\n\000\002\000 \n\000\f\000!\007\000\"\007\000#\001\000\006<init>\001\000\003()V\001\000\004Code\001\000\007_invoke\001\000K(Lorg/omg/CORBA/portable/OutputStream;)Lorg/omg/CORBA/portable/InputStream;\001\000\rStackMapTable\007\000\034\001\000\nExceptions\007\000$\001\000\r_releaseReply\001\000'(Lorg/omg/CORBA/portable/InputStream;)V\f\000\r\000\016\001\000-com/sun/jmx/remote/protocol/iiop/PInputStream\f\000\020\000\021\f\000\r\000\027\001\000+org/omg/CORBA/portable/ApplicationException\f\000%\000&\f\000'\000(\f\000\r\000)\f\000*\000&\f\000\026\000\027\001\000*com/sun/jmx/remote/protocol/iiop/ProxyStub\001\000<org/omg/stub/javax/management/remote/rmi/_RMIConnection_Stub\001\000)org/omg/CORBA/portable/RemarshalException\001\000\016getInputStream\001\000&()Lorg/omg/CORBA/portable/InputStream;\001\000\005getId\001\000\024()Ljava/lang/String;\001\0009(Ljava/lang/String;Lorg/omg/CORBA/portable/InputStream;)V\001\000\025getProxiedInputStream\000!\000\013\000\f\000\000\000\000\000\003\000\001\000\r\000\016\000\001\000\017\000\000\000\021\000\001\000\001\000\000\000\005*·\000\001±\000\000\000\000\000\001\000\020\000\021\000\002\000\017\000\000\000G\000\004\000\004\000\000\000'»\000\002Y*+·\000\003·\000\004°M»\000\002Y,¶\000\006·\000\004N»\000\005Y,¶\000\007-·\000\b¿\000\001\000\000\000\f\000\r\000\005\000\001\000\022\000\000\000\006\000\001M\007\000\023\000\024\000\000\000\006\000\002\000\005\000\025\000\001\000\026\000\027\000\001\000\017\000\000\000'\000\002\000\002\000\000\000\022+Æ\000\013+À\000\002¶\000\tL*+·\000\n±\000\000\000\001\000\022\000\000\000\003\000\001\f\000\000");
    Object localObject2 = NoCallStackClassLoader.stringToBytes("Êþº¾\000\000\0003\000\036\n\000\007\000\017\t\000\006\000\020\n\000\021\000\022\n\000\006\000\023\n\000\024\000\025\007\000\026\007\000\027\001\000\006<init>\001\000'(Lorg/omg/CORBA/portable/InputStream;)V\001\000\004Code\001\000\bread_any\001\000\025()Lorg/omg/CORBA/Any;\001\000\nread_value\001\000)(Ljava/lang/Class;)Ljava/io/Serializable;\f\000\b\000\t\f\000\030\000\031\007\000\032\f\000\013\000\f\f\000\033\000\034\007\000\035\f\000\r\000\016\001\000-com/sun/jmx/remote/protocol/iiop/PInputStream\001\0001com/sun/jmx/remote/protocol/iiop/ProxyInputStream\001\000\002in\001\000$Lorg/omg/CORBA/portable/InputStream;\001\000\"org/omg/CORBA/portable/InputStream\001\000\006narrow\001\000*()Lorg/omg/CORBA_2_3/portable/InputStream;\001\000&org/omg/CORBA_2_3/portable/InputStream\000!\000\006\000\007\000\000\000\000\000\003\000\001\000\b\000\t\000\001\000\n\000\000\000\022\000\002\000\002\000\000\000\006*+·\000\001±\000\000\000\000\000\001\000\013\000\f\000\001\000\n\000\000\000\024\000\001\000\001\000\000\000\b*´\000\002¶\000\003°\000\000\000\000\000\001\000\r\000\016\000\001\000\n\000\000\000\025\000\002\000\002\000\000\000\t*¶\000\004+¶\000\005°\000\000\000\000\000\000");
    Object localObject3 = { "com.sun.jmx.remote.protocol.iiop.ProxyStub", "com.sun.jmx.remote.protocol.iiop.PInputStream" };
    Object localObject4 = { localObject1, localObject2 };
    final String[] arrayOfString = { "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.protocol.iiop.ProxyInputStream" };
    if (IIOPHelper.isAvailable())
    {
      PrivilegedExceptionAction local2 = new PrivilegedExceptionAction()
      {
        public Class<?> run()
          throws Exception
        {
          RMIConnector localRMIConnector = RMIConnector.class;
          ClassLoader localClassLoader = localRMIConnector.getClassLoader();
          ProtectionDomain localProtectionDomain = localRMIConnector.getProtectionDomain();
          NoCallStackClassLoader localNoCallStackClassLoader = new NoCallStackClassLoader(this.val$classNames, this.val$byteCodes, arrayOfString, localClassLoader, localProtectionDomain);
          return localNoCallStackClassLoader.loadClass("com.sun.jmx.remote.protocol.iiop.ProxyStub");
        }
      };
      Class localClass;
      try
      {
        localClass = (Class)AccessController.doPrivileged(local2);
      }
      catch (Exception localException3)
      {
        logger.error("<clinit>", "Unexpected exception making shadow IIOP stub class: " + localException3);
        logger.debug("<clinit>", localException3);
        localClass = null;
      }
      proxyStubClass = localClass;
    }
    else
    {
      proxyStubClass = null;
    }
  }
  
  private static final class ObjectInputStreamWithLoader
    extends ObjectInputStream
  {
    private final ClassLoader loader;
    
    ObjectInputStreamWithLoader(InputStream paramInputStream, ClassLoader paramClassLoader)
      throws IOException
    {
      super();
      this.loader = paramClassLoader;
    }
    
    protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass)
      throws IOException, ClassNotFoundException
    {
      String str = paramObjectStreamClass.getName();
      ReflectUtil.checkPackageAccess(str);
      return Class.forName(str, false, this.loader);
    }
  }
  
  private class RMIClientCommunicatorAdmin
    extends ClientCommunicatorAdmin
  {
    public RMIClientCommunicatorAdmin(long paramLong)
    {
      super();
    }
    
    public void gotIOException(IOException paramIOException)
      throws IOException
    {
      if ((paramIOException instanceof NoSuchObjectException))
      {
        super.gotIOException(paramIOException);
        return;
      }
      try
      {
        RMIConnector.this.connection.getDefaultDomain(null);
      }
      catch (IOException localIOException)
      {
        int i = 0;
        synchronized (this)
        {
          if (!RMIConnector.this.terminated)
          {
            RMIConnector.this.terminated = true;
            i = 1;
          }
        }
        if (i != 0)
        {
          ??? = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.access$1108(RMIConnector.this), "Failed to communicate with the server: " + paramIOException.toString(), paramIOException);
          RMIConnector.this.sendNotification((Notification)???);
          try
          {
            RMIConnector.this.close(true);
          }
          catch (Exception localException) {}
        }
      }
      if ((paramIOException instanceof ServerException))
      {
        Throwable localThrowable = ((ServerException)paramIOException).detail;
        if ((localThrowable instanceof IOException)) {
          throw ((IOException)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
      }
      throw paramIOException;
    }
    
    public void reconnectNotificationListeners(ClientListenerInfo[] paramArrayOfClientListenerInfo)
      throws IOException
    {
      int i = paramArrayOfClientListenerInfo.length;
      ClientListenerInfo[] arrayOfClientListenerInfo1 = new ClientListenerInfo[i];
      Subject[] arrayOfSubject = new Subject[i];
      ObjectName[] arrayOfObjectName = new ObjectName[i];
      NotificationListener[] arrayOfNotificationListener = new NotificationListener[i];
      NotificationFilter[] arrayOfNotificationFilter = new NotificationFilter[i];
      MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[i]);
      Object[] arrayOfObject = new Object[i];
      for (int j = 0; j < i; j++)
      {
        arrayOfSubject[j] = paramArrayOfClientListenerInfo[j].getDelegationSubject();
        arrayOfObjectName[j] = paramArrayOfClientListenerInfo[j].getObjectName();
        arrayOfNotificationListener[j] = paramArrayOfClientListenerInfo[j].getListener();
        arrayOfNotificationFilter[j] = paramArrayOfClientListenerInfo[j].getNotificationFilter();
        arrayOfMarshalledObject[j] = new MarshalledObject(arrayOfNotificationFilter[j]);
        arrayOfObject[j] = paramArrayOfClientListenerInfo[j].getHandback();
      }
      try
      {
        Integer[] arrayOfInteger = RMIConnector.this.addListenersWithSubjects(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject, false);
        for (j = 0; j < i; j++) {
          arrayOfClientListenerInfo1[j] = new ClientListenerInfo(arrayOfInteger[j], arrayOfObjectName[j], arrayOfNotificationListener[j], arrayOfNotificationFilter[j], arrayOfObject[j], arrayOfSubject[j]);
        }
        RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo1);
        return;
      }
      catch (InstanceNotFoundException localInstanceNotFoundException1)
      {
        int k = 0;
        for (j = 0; j < i; j++) {
          try
          {
            Integer localInteger = RMIConnector.this.addListenerWithSubject(arrayOfObjectName[j], new MarshalledObject(arrayOfNotificationFilter[j]), arrayOfSubject[j], false);
            arrayOfClientListenerInfo1[(k++)] = new ClientListenerInfo(localInteger, arrayOfObjectName[j], arrayOfNotificationListener[j], arrayOfNotificationFilter[j], arrayOfObject[j], arrayOfSubject[j]);
          }
          catch (InstanceNotFoundException localInstanceNotFoundException2)
          {
            RMIConnector.logger.warning("reconnectNotificationListeners", "Can't reconnect listener for " + arrayOfObjectName[j]);
          }
        }
        if (k != i)
        {
          ClientListenerInfo[] arrayOfClientListenerInfo2 = arrayOfClientListenerInfo1;
          arrayOfClientListenerInfo1 = new ClientListenerInfo[k];
          System.arraycopy(arrayOfClientListenerInfo2, 0, arrayOfClientListenerInfo1, 0, k);
        }
        RMIConnector.this.rmiNotifClient.postReconnection(arrayOfClientListenerInfo1);
      }
    }
    
    protected void checkConnection()
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("RMIClientCommunicatorAdmin-checkConnection", "Calling the method getDefaultDomain.");
      }
      RMIConnector.this.connection.getDefaultDomain(null);
    }
    
    protected void doStart()
      throws IOException
    {
      try
      {
        localRMIServer = RMIConnector.this.rmiServer != null ? RMIConnector.this.rmiServer : RMIConnector.this.findRMIServer(RMIConnector.this.jmxServiceURL, RMIConnector.this.env);
      }
      catch (NamingException localNamingException)
      {
        throw new IOException("Failed to get a RMI stub: " + localNamingException);
      }
      RMIServer localRMIServer = RMIConnector.connectStub(localRMIServer, RMIConnector.this.env);
      Object localObject = RMIConnector.this.env.get("jmx.remote.credentials");
      RMIConnector.this.connection = localRMIServer.newClient(localObject);
      ClientListenerInfo[] arrayOfClientListenerInfo = RMIConnector.this.rmiNotifClient.preReconnection();
      reconnectNotificationListeners(arrayOfClientListenerInfo);
      RMIConnector.this.connectionId = RMIConnector.this.getConnectionId();
      JMXConnectionNotification localJMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.opened", this, RMIConnector.this.connectionId, RMIConnector.access$1108(RMIConnector.this), "Reconnected to server", null);
      RMIConnector.this.sendNotification(localJMXConnectionNotification);
    }
    
    protected void doStop()
    {
      try
      {
        RMIConnector.this.close();
      }
      catch (IOException localIOException)
      {
        RMIConnector.logger.warning("RMIClientCommunicatorAdmin-doStop", "Failed to call the method close():" + localIOException);
        RMIConnector.logger.debug("RMIClientCommunicatorAdmin-doStop", localIOException);
      }
    }
  }
  
  private class RMINotifClient
    extends ClientNotifForwarder
  {
    public RMINotifClient(Map<String, ?> paramMap)
    {
      super(localMap);
    }
    
    protected NotificationResult fetchNotifs(long paramLong1, int paramInt, long paramLong2)
      throws IOException, ClassNotFoundException
    {
      int i = 0;
      for (;;)
      {
        try
        {
          return RMIConnector.this.connection.fetchNotifications(paramLong1, paramInt, paramLong2);
        }
        catch (IOException localIOException1)
        {
          rethrowDeserializationException(localIOException1);
          try
          {
            RMIConnector.this.communicatorAdmin.gotIOException(localIOException1);
          }
          catch (IOException localIOException2)
          {
            int j = 0;
            synchronized (this)
            {
              if (RMIConnector.this.terminated) {
                throw localIOException1;
              }
              if (i != 0) {
                j = 1;
              }
            }
            if (j != 0)
            {
              ??? = new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.access$1108(RMIConnector.this), "Failed to communicate with the server: " + localIOException1.toString(), localIOException1);
              RMIConnector.this.sendNotification((Notification)???);
              try
              {
                RMIConnector.this.close(true);
              }
              catch (Exception localException) {}
              throw localIOException1;
            }
            i = 1;
          }
        }
      }
    }
    
    private void rethrowDeserializationException(IOException paramIOException)
      throws ClassNotFoundException, IOException
    {
      if ((paramIOException instanceof UnmarshalException)) {
        throw paramIOException;
      }
      if ((paramIOException instanceof MarshalException))
      {
        MarshalException localMarshalException = (MarshalException)paramIOException;
        if ((localMarshalException.detail instanceof NotSerializableException)) {
          throw ((NotSerializableException)localMarshalException.detail);
        }
      }
    }
    
    protected Integer addListenerForMBeanRemovedNotif()
      throws IOException, InstanceNotFoundException
    {
      NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
      localNotificationFilterSupport.enableType("JMX.mbean.unregistered");
      MarshalledObject localMarshalledObject = new MarshalledObject(localNotificationFilterSupport);
      ObjectName[] arrayOfObjectName = { MBeanServerDelegate.DELEGATE_NAME };
      MarshalledObject[] arrayOfMarshalledObject = (MarshalledObject[])Util.cast(new MarshalledObject[] { localMarshalledObject });
      Subject[] arrayOfSubject = { null };
      Integer[] arrayOfInteger;
      try
      {
        arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        arrayOfInteger = RMIConnector.this.connection.addNotificationListeners(arrayOfObjectName, arrayOfMarshalledObject, arrayOfSubject);
      }
      return arrayOfInteger[0];
    }
    
    protected void removeListenerForMBeanRemovedNotif(Integer paramInteger)
      throws IOException, InstanceNotFoundException, ListenerNotFoundException
    {
      try
      {
        RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { paramInteger }, null);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { paramInteger }, null);
      }
    }
    
    protected void lostNotifs(String paramString, long paramLong)
    {
      JMXConnectionNotification localJMXConnectionNotification = new JMXConnectionNotification("jmx.remote.connection.notifs.lost", RMIConnector.this, RMIConnector.this.connectionId, RMIConnector.access$1408(RMIConnector.this), paramString, Long.valueOf(paramLong));
      RMIConnector.this.sendNotification(localJMXConnectionNotification);
    }
  }
  
  private class RemoteMBeanServerConnection
    implements MBeanServerConnection
  {
    private Subject delegationSubject;
    
    public RemoteMBeanServerConnection()
    {
      this(null);
    }
    
    public RemoteMBeanServerConnection(Subject paramSubject)
    {
      this.delegationSubject = paramSubject;
    }
    
    public ObjectInstance createMBean(String paramString, ObjectName paramObjectName)
      throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("createMBean(String,ObjectName)", "className=" + paramString + ", name=" + paramObjectName);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        ObjectInstance localObjectInstance1 = RMIConnector.this.connection.createMBean(paramString, paramObjectName, this.delegationSubject);
        return localObjectInstance1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        ObjectInstance localObjectInstance2 = RMIConnector.this.connection.createMBean(paramString, paramObjectName, this.delegationSubject);
        return localObjectInstance2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2)
      throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName)", "className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ")");
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        ObjectInstance localObjectInstance1 = RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, this.delegationSubject);
        return localObjectInstance1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        ObjectInstance localObjectInstance2 = RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, this.delegationSubject);
        return localObjectInstance2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString)
      throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("createMBean(String,ObjectName,Object[],String[])", "className=" + paramString + ", name=" + paramObjectName + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        ObjectInstance localObjectInstance1 = RMIConnector.this.connection.createMBean(paramString, paramObjectName, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObjectInstance1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        ObjectInstance localObjectInstance2 = RMIConnector.this.connection.createMBean(paramString, paramObjectName, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObjectInstance2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString)
      throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "className=" + paramString + ", name=" + paramObjectName1 + ", loaderName=" + paramObjectName2 + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        ObjectInstance localObjectInstance1 = RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObjectInstance1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        ObjectInstance localObjectInstance2 = RMIConnector.this.connection.createMBean(paramString, paramObjectName1, paramObjectName2, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObjectInstance2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void unregisterMBean(ObjectName paramObjectName)
      throws InstanceNotFoundException, MBeanRegistrationException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("unregisterMBean", "name=" + paramObjectName);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.unregisterMBean(paramObjectName, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.unregisterMBean(paramObjectName, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public ObjectInstance getObjectInstance(ObjectName paramObjectName)
      throws InstanceNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getObjectInstance", "name=" + paramObjectName);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        ObjectInstance localObjectInstance1 = RMIConnector.this.connection.getObjectInstance(paramObjectName, this.delegationSubject);
        return localObjectInstance1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        ObjectInstance localObjectInstance2 = RMIConnector.this.connection.getObjectInstance(paramObjectName, this.delegationSubject);
        return localObjectInstance2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp)
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("queryMBeans", "name=" + paramObjectName + ", query=" + paramQueryExp);
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramQueryExp);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        Set localSet1 = RMIConnector.this.connection.queryMBeans(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localSet1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        Set localSet2 = RMIConnector.this.connection.queryMBeans(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localSet2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp)
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("queryNames", "name=" + paramObjectName + ", query=" + paramQueryExp);
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramQueryExp);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        Set localSet1 = RMIConnector.this.connection.queryNames(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localSet1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        Set localSet2 = RMIConnector.this.connection.queryNames(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localSet2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public boolean isRegistered(ObjectName paramObjectName)
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("isRegistered", "name=" + paramObjectName);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        boolean bool1 = RMIConnector.this.connection.isRegistered(paramObjectName, this.delegationSubject);
        return bool1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        boolean bool2 = RMIConnector.this.connection.isRegistered(paramObjectName, this.delegationSubject);
        return bool2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public Integer getMBeanCount()
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getMBeanCount", "");
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        Integer localInteger1 = RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
        return localInteger1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        Integer localInteger2 = RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
        return localInteger2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public Object getAttribute(ObjectName paramObjectName, String paramString)
      throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getAttribute", "name=" + paramObjectName + ", attribute=" + paramString);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        Object localObject1 = RMIConnector.this.connection.getAttribute(paramObjectName, paramString, this.delegationSubject);
        return localObject1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        Object localObject2 = RMIConnector.this.connection.getAttribute(paramObjectName, paramString, this.delegationSubject);
        return localObject2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString)
      throws InstanceNotFoundException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getAttributes", "name=" + paramObjectName + ", attributes=" + RMIConnector.strings(paramArrayOfString));
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        AttributeList localAttributeList1 = RMIConnector.this.connection.getAttributes(paramObjectName, paramArrayOfString, this.delegationSubject);
        return localAttributeList1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        AttributeList localAttributeList2 = RMIConnector.this.connection.getAttributes(paramObjectName, paramArrayOfString, this.delegationSubject);
        return localAttributeList2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute)
      throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("setAttribute", "name=" + paramObjectName + ", attribute=" + paramAttribute);
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramAttribute);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.setAttribute(paramObjectName, localMarshalledObject, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.setAttribute(paramObjectName, localMarshalledObject, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList)
      throws InstanceNotFoundException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("setAttributes", "name=" + paramObjectName + ", attributes=" + paramAttributeList);
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramAttributeList);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        AttributeList localAttributeList1 = RMIConnector.this.connection.setAttributes(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localAttributeList1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        AttributeList localAttributeList2 = RMIConnector.this.connection.setAttributes(paramObjectName, localMarshalledObject, this.delegationSubject);
        return localAttributeList2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
      throws InstanceNotFoundException, MBeanException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("invoke", "name=" + paramObjectName + ", operationName=" + paramString + ", params=" + RMIConnector.objects(paramArrayOfObject) + ", signature=" + RMIConnector.strings(paramArrayOfString));
      }
      MarshalledObject localMarshalledObject = new MarshalledObject(paramArrayOfObject);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        Object localObject1 = RMIConnector.this.connection.invoke(paramObjectName, paramString, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObject1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        Object localObject2 = RMIConnector.this.connection.invoke(paramObjectName, paramString, localMarshalledObject, paramArrayOfString, this.delegationSubject);
        return localObject2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public String getDefaultDomain()
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getDefaultDomain", "");
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        String str1 = RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
        return str1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        String str2 = RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
        return str2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public String[] getDomains()
      throws IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getDomains", "");
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        String[] arrayOfString1 = RMIConnector.this.connection.getDomains(this.delegationSubject);
        return arrayOfString1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        String[] arrayOfString2 = RMIConnector.this.connection.getDomains(this.delegationSubject);
        return arrayOfString2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public MBeanInfo getMBeanInfo(ObjectName paramObjectName)
      throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("getMBeanInfo", "name=" + paramObjectName);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        MBeanInfo localMBeanInfo1 = RMIConnector.this.connection.getMBeanInfo(paramObjectName, this.delegationSubject);
        return localMBeanInfo1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        MBeanInfo localMBeanInfo2 = RMIConnector.this.connection.getMBeanInfo(paramObjectName, this.delegationSubject);
        return localMBeanInfo2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public boolean isInstanceOf(ObjectName paramObjectName, String paramString)
      throws InstanceNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("isInstanceOf", "name=" + paramObjectName + ", className=" + paramString);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        boolean bool1 = RMIConnector.this.connection.isInstanceOf(paramObjectName, paramString, this.delegationSubject);
        return bool1;
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        boolean bool2 = RMIConnector.this.connection.isInstanceOf(paramObjectName, paramString, this.delegationSubject);
        return bool2;
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
      throws InstanceNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2 + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
      }
      MarshalledObject localMarshalledObject1 = new MarshalledObject(paramNotificationFilter);
      MarshalledObject localMarshalledObject2 = new MarshalledObject(paramObject);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.addNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.addNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2)
      throws InstanceNotFoundException, ListenerNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject)
      throws InstanceNotFoundException, ListenerNotFoundException, IOException
    {
      if (RMIConnector.logger.debugOn()) {
        RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + paramObjectName1 + ", listener=" + paramObjectName2 + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
      }
      MarshalledObject localMarshalledObject1 = new MarshalledObject(paramNotificationFilter);
      MarshalledObject localMarshalledObject2 = new MarshalledObject(paramObject);
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.removeNotificationListener(paramObjectName1, paramObjectName2, localMarshalledObject1, localMarshalledObject2, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
      throws InstanceNotFoundException, IOException
    {
      boolean bool = RMIConnector.logger.debugOn();
      if (bool) {
        RMIConnector.logger.debug("addNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + paramObjectName + ", listener=" + paramNotificationListener + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
      }
      Integer localInteger = RMIConnector.this.addListenerWithSubject(paramObjectName, new MarshalledObject(paramNotificationFilter), this.delegationSubject, true);
      RMIConnector.this.rmiNotifClient.addNotificationListener(localInteger, paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject, this.delegationSubject);
    }
    
    public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener)
      throws InstanceNotFoundException, ListenerNotFoundException, IOException
    {
      boolean bool = RMIConnector.logger.debugOn();
      if (bool) {
        RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener)", "name=" + paramObjectName + ", listener=" + paramNotificationListener);
      }
      Integer[] arrayOfInteger = RMIConnector.this.rmiNotifClient.removeNotificationListener(paramObjectName, paramNotificationListener);
      if (bool) {
        RMIConnector.logger.debug("removeNotificationListener", "listenerIDs=" + RMIConnector.objects(arrayOfInteger));
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.removeNotificationListeners(paramObjectName, arrayOfInteger, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.removeNotificationListeners(paramObjectName, arrayOfInteger, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
    
    public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
      throws InstanceNotFoundException, ListenerNotFoundException, IOException
    {
      boolean bool = RMIConnector.logger.debugOn();
      if (bool) {
        RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + paramObjectName + ", listener=" + paramNotificationListener + ", filter=" + paramNotificationFilter + ", handback=" + paramObject);
      }
      Integer localInteger = RMIConnector.this.rmiNotifClient.removeNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject);
      if (bool) {
        RMIConnector.logger.debug("removeNotificationListener", "listenerID=" + localInteger);
      }
      ClassLoader localClassLoader = RMIConnector.this.pushDefaultClassLoader();
      try
      {
        RMIConnector.this.connection.removeNotificationListeners(paramObjectName, new Integer[] { localInteger }, this.delegationSubject);
      }
      catch (IOException localIOException)
      {
        RMIConnector.this.communicatorAdmin.gotIOException(localIOException);
        RMIConnector.this.connection.removeNotificationListeners(paramObjectName, new Integer[] { localInteger }, this.delegationSubject);
      }
      finally
      {
        RMIConnector.this.popDefaultClassLoader(localClassLoader);
      }
    }
  }
}
