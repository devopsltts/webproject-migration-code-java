package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ServerManagerImplBase
  extends ObjectImpl
  implements ServerManager, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0" };
  
  public _ServerManagerImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    Object localObject1;
    Object localObject2;
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        int i = ServerIdHelper.read(paramInputStream);
        localObject1 = ServerHelper.read(paramInputStream);
        active(i, (Server)localObject1);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered1);
      }
    case 1: 
      try
      {
        int j = ServerIdHelper.read(paramInputStream);
        localObject1 = ORBidHelper.read(paramInputStream);
        localObject2 = EndpointInfoListHelper.read(paramInputStream);
        registerEndpoints(j, (String)localObject1, (EndPointInfo[])localObject2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered2);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint1);
      }
      catch (ORBAlreadyRegistered localORBAlreadyRegistered)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ORBAlreadyRegisteredHelper.write(localOutputStream, localORBAlreadyRegistered);
      }
    case 2: 
      int[] arrayOfInt = null;
      arrayOfInt = getActiveServers();
      localOutputStream = paramResponseHandler.createReply();
      ServerIdsHelper.write(localOutputStream, arrayOfInt);
      break;
    case 3: 
      try
      {
        int k = ServerIdHelper.read(paramInputStream);
        activate(k);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerAlreadyActive localServerAlreadyActive)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyActiveHelper.write(localOutputStream, localServerAlreadyActive);
      }
      catch (ServerNotRegistered localServerNotRegistered3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered3);
      }
      catch (ServerHeldDown localServerHeldDown1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown1);
      }
    case 4: 
      try
      {
        int m = ServerIdHelper.read(paramInputStream);
        shutdown(m);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotActive localServerNotActive)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotActiveHelper.write(localOutputStream, localServerNotActive);
      }
      catch (ServerNotRegistered localServerNotRegistered4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered4);
      }
    case 5: 
      try
      {
        int n = ServerIdHelper.read(paramInputStream);
        install(n);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered5);
      }
      catch (ServerHeldDown localServerHeldDown2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown2);
      }
      catch (ServerAlreadyInstalled localServerAlreadyInstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyInstalledHelper.write(localOutputStream, localServerAlreadyInstalled);
      }
    case 6: 
      try
      {
        int i1 = ServerIdHelper.read(paramInputStream);
        localObject1 = null;
        localObject1 = getORBNames(i1);
        localOutputStream = paramResponseHandler.createReply();
        ORBidListHelper.write(localOutputStream, (String[])localObject1);
      }
      catch (ServerNotRegistered localServerNotRegistered6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered6);
      }
    case 7: 
      try
      {
        int i2 = ServerIdHelper.read(paramInputStream);
        uninstall(i2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered7)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered7);
      }
      catch (ServerHeldDown localServerHeldDown3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown3);
      }
      catch (ServerAlreadyUninstalled localServerAlreadyUninstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyUninstalledHelper.write(localOutputStream, localServerAlreadyUninstalled);
      }
    case 8: 
      try
      {
        int i3 = ServerIdHelper.read(paramInputStream);
        localObject1 = paramInputStream.read_string();
        localObject2 = null;
        localObject2 = locateServer(i3, (String)localObject1);
        localOutputStream = paramResponseHandler.createReply();
        ServerLocationHelper.write(localOutputStream, (ServerLocation)localObject2);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint2);
      }
      catch (ServerNotRegistered localServerNotRegistered8)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered8);
      }
      catch (ServerHeldDown localServerHeldDown4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown4);
      }
    case 9: 
      try
      {
        int i4 = ServerIdHelper.read(paramInputStream);
        localObject1 = ORBidHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = locateServerForORB(i4, (String)localObject1);
        localOutputStream = paramResponseHandler.createReply();
        ServerLocationPerORBHelper.write(localOutputStream, (ServerLocationPerORB)localObject2);
      }
      catch (InvalidORBid localInvalidORBid)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidORBidHelper.write(localOutputStream, localInvalidORBid);
      }
      catch (ServerNotRegistered localServerNotRegistered9)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered9);
      }
      catch (ServerHeldDown localServerHeldDown5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown5);
      }
    case 10: 
      try
      {
        String str1 = paramInputStream.read_string();
        int i5 = 0;
        i5 = getEndpoint(str1);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(i5);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint3);
      }
    case 11: 
      try
      {
        ServerLocationPerORB localServerLocationPerORB = ServerLocationPerORBHelper.read(paramInputStream);
        String str2 = paramInputStream.read_string();
        int i6 = 0;
        i6 = getServerPortForType(localServerLocationPerORB, str2);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(i6);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint4);
      }
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  static
  {
    _methods.put("active", new Integer(0));
    _methods.put("registerEndpoints", new Integer(1));
    _methods.put("getActiveServers", new Integer(2));
    _methods.put("activate", new Integer(3));
    _methods.put("shutdown", new Integer(4));
    _methods.put("install", new Integer(5));
    _methods.put("getORBNames", new Integer(6));
    _methods.put("uninstall", new Integer(7));
    _methods.put("locateServer", new Integer(8));
    _methods.put("locateServerForORB", new Integer(9));
    _methods.put("getEndpoint", new Integer(10));
    _methods.put("getServerPortForType", new Integer(11));
  }
}
