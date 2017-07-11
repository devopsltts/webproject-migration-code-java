package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;

public class CNCtx
  implements Context
{
  private static final boolean debug = false;
  private static ORB _defaultOrb;
  ORB _orb;
  public NamingContext _nc;
  private NameComponent[] _name = null;
  Hashtable<String, Object> _env;
  static final CNNameParser parser = new CNNameParser();
  private static final String FED_PROP = "com.sun.jndi.cosnaming.federation";
  boolean federation = false;
  OrbReuseTracker orbTracker = null;
  int enumCount;
  boolean isCloseCalled = false;
  
  private static synchronized ORB getDefaultOrb()
  {
    if (_defaultOrb == null) {
      _defaultOrb = CorbaUtils.getOrb(null, -1, new Hashtable());
    }
    return _defaultOrb;
  }
  
  CNCtx(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramHashtable != null) {
      paramHashtable = (Hashtable)paramHashtable.clone();
    }
    this._env = paramHashtable;
    this.federation = "true".equals(paramHashtable != null ? paramHashtable.get("com.sun.jndi.cosnaming.federation") : null);
    initOrbAndRootContext(paramHashtable);
  }
  
  private CNCtx() {}
  
  public static ResolveResult createUsingURL(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    CNCtx localCNCtx = new CNCtx();
    if (paramHashtable != null) {
      paramHashtable = (Hashtable)paramHashtable.clone();
    }
    localCNCtx._env = paramHashtable;
    String str = localCNCtx.initUsingUrl(paramHashtable != null ? (ORB)paramHashtable.get("java.naming.corba.orb") : null, paramString, paramHashtable);
    return new ResolveResult(localCNCtx, parser.parse(str));
  }
  
  CNCtx(ORB paramORB, OrbReuseTracker paramOrbReuseTracker, NamingContext paramNamingContext, Hashtable<String, Object> paramHashtable, NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    if ((paramORB == null) || (paramNamingContext == null)) {
      throw new ConfigurationException("Must supply ORB or NamingContext");
    }
    if (paramORB != null) {
      this._orb = paramORB;
    } else {
      this._orb = getDefaultOrb();
    }
    this._nc = paramNamingContext;
    this._env = paramHashtable;
    this._name = paramArrayOfNameComponent;
    this.federation = "true".equals(paramHashtable != null ? paramHashtable.get("com.sun.jndi.cosnaming.federation") : null);
  }
  
  NameComponent[] makeFullName(NameComponent[] paramArrayOfNameComponent)
  {
    if ((this._name == null) || (this._name.length == 0)) {
      return paramArrayOfNameComponent;
    }
    NameComponent[] arrayOfNameComponent = new NameComponent[this._name.length + paramArrayOfNameComponent.length];
    System.arraycopy(this._name, 0, arrayOfNameComponent, 0, this._name.length);
    System.arraycopy(paramArrayOfNameComponent, 0, arrayOfNameComponent, this._name.length, paramArrayOfNameComponent.length);
    return arrayOfNameComponent;
  }
  
  public String getNameInNamespace()
    throws NamingException
  {
    if ((this._name == null) || (this._name.length == 0)) {
      return "";
    }
    return CNNameParser.cosNameToInsString(this._name);
  }
  
  private static boolean isCorbaUrl(String paramString)
  {
    return (paramString.startsWith("iiop://")) || (paramString.startsWith("iiopname://")) || (paramString.startsWith("corbaname:"));
  }
  
  private void initOrbAndRootContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    ORB localORB = null;
    String str1 = null;
    if ((localORB == null) && (paramHashtable != null)) {
      localORB = (ORB)paramHashtable.get("java.naming.corba.orb");
    }
    if (localORB == null) {
      localORB = getDefaultOrb();
    }
    String str2 = null;
    if (paramHashtable != null) {
      str2 = (String)paramHashtable.get("java.naming.provider.url");
    }
    if ((str2 != null) && (!isCorbaUrl(str2)))
    {
      str1 = getStringifiedIor(str2);
      setOrbAndRootContext(localORB, str1);
    }
    else if (str2 != null)
    {
      String str3 = initUsingUrl(localORB, str2, paramHashtable);
      if (str3.length() > 0)
      {
        this._name = CNNameParser.nameToCosName(parser.parse(str3));
        try
        {
          org.omg.CORBA.Object localObject = this._nc.resolve(this._name);
          this._nc = NamingContextHelper.narrow(localObject);
          if (this._nc == null) {
            throw new ConfigurationException(str3 + " does not name a NamingContext");
          }
        }
        catch (BAD_PARAM localBAD_PARAM)
        {
          throw new ConfigurationException(str3 + " does not name a NamingContext");
        }
        catch (Exception localException)
        {
          throw ExceptionMapper.mapException(localException, this, this._name);
        }
      }
    }
    else
    {
      setOrbAndRootContext(localORB, (String)null);
    }
  }
  
  private String initUsingUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if ((paramString.startsWith("iiop://")) || (paramString.startsWith("iiopname://"))) {
      return initUsingIiopUrl(paramORB, paramString, paramHashtable);
    }
    return initUsingCorbanameUrl(paramORB, paramString, paramHashtable);
  }
  
  private String initUsingIiopUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramORB == null) {
      paramORB = getDefaultOrb();
    }
    try
    {
      IiopUrl localIiopUrl = new IiopUrl(paramString);
      Object localObject = null;
      Iterator localIterator = localIiopUrl.getAddresses().iterator();
      while (localIterator.hasNext())
      {
        IiopUrl.Address localAddress = (IiopUrl.Address)localIterator.next();
        try
        {
          String str = "corbaloc:iiop:" + localAddress.host + ":" + localAddress.port + "/NameService";
          org.omg.CORBA.Object localObject1 = paramORB.string_to_object(str);
          setOrbAndRootContext(paramORB, localObject1);
          return localIiopUrl.getStringName();
        }
        catch (Exception localException)
        {
          setOrbAndRootContext(paramORB, (String)null);
          return localIiopUrl.getStringName();
        }
        catch (NamingException localNamingException)
        {
          localObject = localNamingException;
        }
      }
      if (localObject != null) {
        throw localObject;
      }
      throw new ConfigurationException("Problem with URL: " + paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new ConfigurationException(localMalformedURLException.getMessage());
    }
  }
  
  private String initUsingCorbanameUrl(ORB paramORB, String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramORB == null) {
      paramORB = getDefaultOrb();
    }
    try
    {
      CorbanameUrl localCorbanameUrl = new CorbanameUrl(paramString);
      String str1 = localCorbanameUrl.getLocation();
      String str2 = localCorbanameUrl.getStringName();
      setOrbAndRootContext(paramORB, str1);
      return localCorbanameUrl.getStringName();
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new ConfigurationException(localMalformedURLException.getMessage());
    }
  }
  
  private void setOrbAndRootContext(ORB paramORB, String paramString)
    throws NamingException
  {
    this._orb = paramORB;
    try
    {
      org.omg.CORBA.Object localObject;
      if (paramString != null) {
        localObject = this._orb.string_to_object(paramString);
      } else {
        localObject = this._orb.resolve_initial_references("NameService");
      }
      this._nc = NamingContextHelper.narrow(localObject);
      if (this._nc == null)
      {
        if (paramString != null) {
          throw new ConfigurationException("Cannot convert IOR to a NamingContext: " + paramString);
        }
        throw new ConfigurationException("ORB.resolve_initial_references(\"NameService\") does not return a NamingContext");
      }
    }
    catch (InvalidName localInvalidName)
    {
      localObject1 = new ConfigurationException("COS Name Service not registered with ORB under the name 'NameService'");
      ((NamingException)localObject1).setRootCause(localInvalidName);
      throw ((Throwable)localObject1);
    }
    catch (COMM_FAILURE localCOMM_FAILURE)
    {
      localObject1 = new CommunicationException("Cannot connect to ORB");
      ((NamingException)localObject1).setRootCause(localCOMM_FAILURE);
      throw ((Throwable)localObject1);
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      localObject1 = new ConfigurationException("Invalid URL or IOR: " + paramString);
      ((NamingException)localObject1).setRootCause(localBAD_PARAM);
      throw ((Throwable)localObject1);
    }
    catch (INV_OBJREF localINV_OBJREF)
    {
      Object localObject1 = new ConfigurationException("Invalid object reference: " + paramString);
      ((NamingException)localObject1).setRootCause(localINV_OBJREF);
      throw ((Throwable)localObject1);
    }
  }
  
  private void setOrbAndRootContext(ORB paramORB, org.omg.CORBA.Object paramObject)
    throws NamingException
  {
    this._orb = paramORB;
    try
    {
      this._nc = NamingContextHelper.narrow(paramObject);
      if (this._nc == null) {
        throw new ConfigurationException("Cannot convert object reference to NamingContext: " + paramObject);
      }
    }
    catch (COMM_FAILURE localCOMM_FAILURE)
    {
      CommunicationException localCommunicationException = new CommunicationException("Cannot connect to ORB");
      localCommunicationException.setRootCause(localCOMM_FAILURE);
      throw localCommunicationException;
    }
  }
  
  private String getStringifiedIor(String paramString)
    throws NamingException
  {
    if ((paramString.startsWith("IOR:")) || (paramString.startsWith("corbaloc:"))) {
      return paramString;
    }
    InputStream localInputStream = null;
    try
    {
      URL localURL = new URL(paramString);
      localInputStream = localURL.openStream();
      Object localObject1;
      if (localInputStream != null)
      {
        localObject1 = new BufferedReader(new InputStreamReader(localInputStream, "8859_1"));
        String str1;
        while ((str1 = ((BufferedReader)localObject1).readLine()) != null) {
          if (str1.startsWith("IOR:"))
          {
            String str2 = str1;
            ConfigurationException localConfigurationException1;
            return str2;
          }
        }
      }
      try
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException1)
      {
        localObject1 = new ConfigurationException("Invalid URL: " + paramString);
        ((NamingException)localObject1).setRootCause(localIOException1);
        throw ((Throwable)localObject1);
      }
      ConfigurationException localConfigurationException2;
      throw new ConfigurationException(paramString + " does not contain an IOR");
    }
    catch (IOException localIOException2)
    {
      localObject1 = new ConfigurationException("Invalid URL: " + paramString);
      ((NamingException)localObject1).setRootCause(localIOException2);
      throw ((Throwable)localObject1);
    }
    finally
    {
      try
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
      }
      catch (IOException localIOException4)
      {
        localConfigurationException2 = new ConfigurationException("Invalid URL: " + paramString);
        localConfigurationException2.setRootCause(localIOException4);
        throw localConfigurationException2;
      }
    }
  }
  
  /* Error */
  Object callResolve(NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 486	com/sun/jndi/cosnaming/CNCtx:_nc	Lorg/omg/CosNaming/NamingContext;
    //   4: aload_1
    //   5: invokeinterface 599 2 0
    //   10: astore_2
    //   11: aload_2
    //   12: invokestatic 584	org/omg/CosNaming/NamingContextHelper:narrow	(Lorg/omg/CORBA/Object;)Lorg/omg/CosNaming/NamingContext;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +29 -> 46
    //   20: new 250	com/sun/jndi/cosnaming/CNCtx
    //   23: dup
    //   24: aload_0
    //   25: getfield 484	com/sun/jndi/cosnaming/CNCtx:_orb	Lorg/omg/CORBA/ORB;
    //   28: aload_0
    //   29: getfield 481	com/sun/jndi/cosnaming/CNCtx:orbTracker	Lcom/sun/jndi/cosnaming/OrbReuseTracker;
    //   32: aload_3
    //   33: aload_0
    //   34: getfield 482	com/sun/jndi/cosnaming/CNCtx:_env	Ljava/util/Hashtable;
    //   37: aload_0
    //   38: aload_1
    //   39: invokevirtual 517	com/sun/jndi/cosnaming/CNCtx:makeFullName	([Lorg/omg/CosNaming/NameComponent;)[Lorg/omg/CosNaming/NameComponent;
    //   42: invokespecial 524	com/sun/jndi/cosnaming/CNCtx:<init>	(Lorg/omg/CORBA/ORB;Lcom/sun/jndi/cosnaming/OrbReuseTracker;Lorg/omg/CosNaming/NamingContext;Ljava/util/Hashtable;[Lorg/omg/CosNaming/NameComponent;)V
    //   45: areturn
    //   46: aload_2
    //   47: areturn
    //   48: astore_3
    //   49: aload_2
    //   50: areturn
    //   51: astore_2
    //   52: aload_2
    //   53: aload_0
    //   54: aload_1
    //   55: invokestatic 532	com/sun/jndi/cosnaming/ExceptionMapper:mapException	(Ljava/lang/Exception;Lcom/sun/jndi/cosnaming/CNCtx;[Lorg/omg/CosNaming/NameComponent;)Ljavax/naming/NamingException;
    //   58: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	CNCtx
    //   0	59	1	paramArrayOfNameComponent	NameComponent[]
    //   10	40	2	localObject	org.omg.CORBA.Object
    //   51	2	2	localException	Exception
    //   15	18	3	localNamingContext	NamingContext
    //   48	1	3	localSystemException	org.omg.CORBA.SystemException
    // Exception table:
    //   from	to	target	type
    //   11	45	48	org/omg/CORBA/SystemException
    //   46	47	48	org/omg/CORBA/SystemException
    //   0	45	51	java/lang/Exception
    //   46	47	51	java/lang/Exception
    //   48	50	51	java/lang/Exception
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    return lookup(new CompositeName(paramString));
  }
  
  /* Error */
  public Object lookup(Name paramName)
    throws NamingException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 486	com/sun/jndi/cosnaming/CNCtx:_nc	Lorg/omg/CosNaming/NamingContext;
    //   4: ifnonnull +13 -> 17
    //   7: new 278	javax/naming/ConfigurationException
    //   10: dup
    //   11: ldc 12
    //   13: invokespecial 568	javax/naming/ConfigurationException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_1
    //   18: invokeinterface 594 1 0
    //   23: ifne +5 -> 28
    //   26: aload_0
    //   27: areturn
    //   28: aload_1
    //   29: invokestatic 528	com/sun/jndi/cosnaming/CNNameParser:nameToCosName	(Ljavax/naming/Name;)[Lorg/omg/CosNaming/NameComponent;
    //   32: astore_2
    //   33: aload_0
    //   34: aload_2
    //   35: invokevirtual 507	com/sun/jndi/cosnaming/CNCtx:callResolve	([Lorg/omg/CosNaming/NameComponent;)Ljava/lang/Object;
    //   38: astore_3
    //   39: aload_3
    //   40: aload_1
    //   41: aload_0
    //   42: aload_0
    //   43: getfield 482	com/sun/jndi/cosnaming/CNCtx:_env	Ljava/util/Hashtable;
    //   46: invokestatic 579	javax/naming/spi/NamingManager:getObjectInstance	(Ljava/lang/Object;Ljavax/naming/Name;Ljavax/naming/Context;Ljava/util/Hashtable;)Ljava/lang/Object;
    //   49: areturn
    //   50: astore 4
    //   52: aload 4
    //   54: athrow
    //   55: astore 4
    //   57: new 283	javax/naming/NamingException
    //   60: dup
    //   61: ldc 34
    //   63: invokespecial 572	javax/naming/NamingException:<init>	(Ljava/lang/String;)V
    //   66: astore 5
    //   68: aload 5
    //   70: aload 4
    //   72: invokevirtual 573	javax/naming/NamingException:setRootCause	(Ljava/lang/Throwable;)V
    //   75: aload 5
    //   77: athrow
    //   78: astore_3
    //   79: aload_3
    //   80: invokestatic 510	com/sun/jndi/cosnaming/CNCtx:getContinuationContext	(Ljavax/naming/CannotProceedException;)Ljavax/naming/Context;
    //   83: astore 4
    //   85: aload 4
    //   87: aload_3
    //   88: invokevirtual 565	javax/naming/CannotProceedException:getRemainingName	()Ljavax/naming/Name;
    //   91: invokeinterface 590 2 0
    //   96: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	CNCtx
    //   0	97	1	paramName	Name
    //   32	3	2	arrayOfNameComponent	NameComponent[]
    //   38	2	3	localObject	Object
    //   78	10	3	localCannotProceedException	CannotProceedException
    //   50	3	4	localNamingException1	NamingException
    //   55	16	4	localException	Exception
    //   83	3	4	localContext	Context
    //   66	10	5	localNamingException2	NamingException
    // Exception table:
    //   from	to	target	type
    //   39	49	50	javax/naming/NamingException
    //   39	49	55	java/lang/Exception
    //   33	49	78	javax/naming/CannotProceedException
    //   50	78	78	javax/naming/CannotProceedException
  }
  
  private void callBindOrRebind(NameComponent[] paramArrayOfNameComponent, Name paramName, Object paramObject, boolean paramBoolean)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    try
    {
      paramObject = NamingManager.getStateToBind(paramObject, paramName, this, this._env);
      if ((paramObject instanceof CNCtx)) {
        paramObject = ((CNCtx)paramObject)._nc;
      }
      if ((paramObject instanceof NamingContext))
      {
        NamingContext localNamingContext = NamingContextHelper.narrow((org.omg.CORBA.Object)paramObject);
        if (paramBoolean) {
          this._nc.rebind_context(paramArrayOfNameComponent, localNamingContext);
        } else {
          this._nc.bind_context(paramArrayOfNameComponent, localNamingContext);
        }
      }
      else if ((paramObject instanceof org.omg.CORBA.Object))
      {
        if (paramBoolean) {
          this._nc.rebind(paramArrayOfNameComponent, (org.omg.CORBA.Object)paramObject);
        } else {
          this._nc.bind(paramArrayOfNameComponent, (org.omg.CORBA.Object)paramObject);
        }
      }
      else
      {
        throw new IllegalArgumentException("Only instances of org.omg.CORBA.Object can be bound");
      }
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      NotContextException localNotContextException = new NotContextException(paramName.toString());
      localNotContextException.setRootCause(localBAD_PARAM);
      throw localNotContextException;
    }
    catch (Exception localException)
    {
      throw ExceptionMapper.mapException(localException, this, paramArrayOfNameComponent);
    }
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (paramName.size() == 0) {
      throw new InvalidNameException("Name is empty");
    }
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try
    {
      callBindOrRebind(arrayOfNameComponent, paramName, paramObject, false);
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = getContinuationContext(localCannotProceedException);
      localContext.bind(localCannotProceedException.getRemainingName(), paramObject);
    }
  }
  
  private static Context getContinuationContext(CannotProceedException paramCannotProceedException)
    throws NamingException
  {
    try
    {
      return NamingManager.getContinuationContext(paramCannotProceedException);
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Object localObject = localCannotProceedException.getResolvedObj();
      if ((localObject instanceof Reference))
      {
        Reference localReference = (Reference)localObject;
        RefAddr localRefAddr = localReference.get("nns");
        if ((localRefAddr.getContent() instanceof Context))
        {
          NameNotFoundException localNameNotFoundException = new NameNotFoundException("No object reference bound for specified name");
          localNameNotFoundException.setRootCause(paramCannotProceedException.getRootCause());
          localNameNotFoundException.setRemainingName(paramCannotProceedException.getRemainingName());
          throw localNameNotFoundException;
        }
      }
      throw localCannotProceedException;
    }
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    bind(new CompositeName(paramString), paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (paramName.size() == 0) {
      throw new InvalidNameException("Name is empty");
    }
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try
    {
      callBindOrRebind(arrayOfNameComponent, paramName, paramObject, true);
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = getContinuationContext(localCannotProceedException);
      localContext.rebind(localCannotProceedException.getRemainingName(), paramObject);
    }
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    rebind(new CompositeName(paramString), paramObject);
  }
  
  private void callUnbind(NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    try
    {
      this._nc.unbind(paramArrayOfNameComponent);
    }
    catch (NotFound localNotFound)
    {
      if (!leafNotFound(localNotFound, paramArrayOfNameComponent[(paramArrayOfNameComponent.length - 1)])) {
        throw ExceptionMapper.mapException(localNotFound, this, paramArrayOfNameComponent);
      }
    }
    catch (Exception localException)
    {
      throw ExceptionMapper.mapException(localException, this, paramArrayOfNameComponent);
    }
  }
  
  private boolean leafNotFound(NotFound paramNotFound, NameComponent paramNameComponent)
  {
    NameComponent localNameComponent;
    return (paramNotFound.why.value() == 0) && (paramNotFound.rest_of_name.length == 1) && ((localNameComponent = paramNotFound.rest_of_name[0]).id.equals(paramNameComponent.id)) && ((localNameComponent.kind == paramNameComponent.kind) || ((localNameComponent.kind != null) && (localNameComponent.kind.equals(paramNameComponent.kind))));
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    unbind(new CompositeName(paramString));
  }
  
  public void unbind(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 0) {
      throw new InvalidNameException("Name is empty");
    }
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try
    {
      callUnbind(arrayOfNameComponent);
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = getContinuationContext(localCannotProceedException);
      localContext.unbind(localCannotProceedException.getRemainingName());
    }
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    rename(new CompositeName(paramString1), new CompositeName(paramString2));
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    if ((paramName1.size() == 0) || (paramName2.size() == 0)) {
      throw new InvalidNameException("One or both names empty");
    }
    Object localObject = lookup(paramName1);
    bind(paramName2, localObject);
    unbind(paramName1);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    return list(new CompositeName(paramString));
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    return listBindings(paramName);
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    return listBindings(new CompositeName(paramString));
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    if (paramName.size() > 0) {
      try
      {
        Object localObject = lookup(paramName);
        if ((localObject instanceof CNCtx)) {
          return new CNBindingEnumeration((CNCtx)localObject, true, this._env);
        }
        throw new NotContextException(paramName.toString());
      }
      catch (NamingException localNamingException)
      {
        throw localNamingException;
      }
      catch (BAD_PARAM localBAD_PARAM)
      {
        NotContextException localNotContextException = new NotContextException(paramName.toString());
        localNotContextException.setRootCause(localBAD_PARAM);
        throw localNotContextException;
      }
    }
    return new CNBindingEnumeration(this, false, this._env);
  }
  
  private void callDestroy(NamingContext paramNamingContext)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    try
    {
      paramNamingContext.destroy();
    }
    catch (Exception localException)
    {
      throw ExceptionMapper.mapException(localException, this, null);
    }
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    destroySubcontext(new CompositeName(paramString));
  }
  
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    NamingContext localNamingContext = this._nc;
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    if (paramName.size() > 0) {
      try
      {
        Context localContext = (Context)callResolve(arrayOfNameComponent);
        localObject = (CNCtx)localContext;
        localNamingContext = ((CNCtx)localObject)._nc;
        ((CNCtx)localObject).close();
      }
      catch (ClassCastException localClassCastException)
      {
        throw new NotContextException(paramName.toString());
      }
      catch (CannotProceedException localCannotProceedException)
      {
        Object localObject = getContinuationContext(localCannotProceedException);
        ((Context)localObject).destroySubcontext(localCannotProceedException.getRemainingName());
        return;
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        if (((localNameNotFoundException.getRootCause() instanceof NotFound)) && (leafNotFound((NotFound)localNameNotFoundException.getRootCause(), arrayOfNameComponent[(arrayOfNameComponent.length - 1)]))) {
          return;
        }
        throw localNameNotFoundException;
      }
      catch (NamingException localNamingException)
      {
        throw localNamingException;
      }
    }
    callDestroy(localNamingContext);
    callUnbind(arrayOfNameComponent);
  }
  
  private Context callBindNewContext(NameComponent[] paramArrayOfNameComponent)
    throws NamingException
  {
    if (this._nc == null) {
      throw new ConfigurationException("Context does not have a corresponding NamingContext");
    }
    try
    {
      NamingContext localNamingContext = this._nc.bind_new_context(paramArrayOfNameComponent);
      return new CNCtx(this._orb, this.orbTracker, localNamingContext, this._env, makeFullName(paramArrayOfNameComponent));
    }
    catch (Exception localException)
    {
      throw ExceptionMapper.mapException(localException, this, paramArrayOfNameComponent);
    }
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    return createSubcontext(new CompositeName(paramString));
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 0) {
      throw new InvalidNameException("Name is empty");
    }
    NameComponent[] arrayOfNameComponent = CNNameParser.nameToCosName(paramName);
    try
    {
      return callBindNewContext(arrayOfNameComponent);
    }
    catch (CannotProceedException localCannotProceedException)
    {
      Context localContext = getContinuationContext(localCannotProceedException);
      return localContext.createSubcontext(localCannotProceedException.getRemainingName());
    }
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    return lookupLink(new CompositeName(paramString));
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    return lookup(paramName);
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    return parser;
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    return parser;
  }
  
  public Hashtable<String, Object> getEnvironment()
    throws NamingException
  {
    if (this._env == null) {
      return new Hashtable(5, 0.75F);
    }
    return (Hashtable)this._env.clone();
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    return composeName(new CompositeName(paramString1), new CompositeName(paramString2)).toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    Name localName = (Name)paramName2.clone();
    return localName.addAll(paramName1);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    if (this._env == null) {
      this._env = new Hashtable(7, 0.75F);
    } else {
      this._env = ((Hashtable)this._env.clone());
    }
    return this._env.put(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if ((this._env != null) && (this._env.get(paramString) != null))
    {
      this._env = ((Hashtable)this._env.clone());
      return this._env.remove(paramString);
    }
    return null;
  }
  
  public synchronized void incEnumCount()
  {
    this.enumCount += 1;
  }
  
  public synchronized void decEnumCount()
    throws NamingException
  {
    this.enumCount -= 1;
    if ((this.enumCount == 0) && (this.isCloseCalled)) {
      close();
    }
  }
  
  public synchronized void close()
    throws NamingException
  {
    if (this.enumCount > 0)
    {
      this.isCloseCalled = true;
      return;
    }
  }
  
  protected void finalize()
  {
    try
    {
      close();
    }
    catch (NamingException localNamingException) {}
  }
}
