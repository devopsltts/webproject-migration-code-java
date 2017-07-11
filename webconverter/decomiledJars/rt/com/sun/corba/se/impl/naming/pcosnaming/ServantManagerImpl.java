package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.spi.orb.ORB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import org.omg.CORBA.LocalObject;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;

public class ServantManagerImpl
  extends LocalObject
  implements ServantLocator
{
  private static final long serialVersionUID = 4028710359865748280L;
  private ORB orb;
  private NameService theNameService;
  private File logDir;
  private Hashtable contexts;
  private CounterDB counterDb;
  private int counter;
  private static final String objKeyPrefix = "NC";
  
  ServantManagerImpl(ORB paramORB, File paramFile, NameService paramNameService)
  {
    this.logDir = paramFile;
    this.orb = paramORB;
    this.counterDb = new CounterDB(paramFile);
    this.contexts = new Hashtable();
    this.theNameService = paramNameService;
  }
  
  public Servant preinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, CookieHolder paramCookieHolder)
    throws ForwardRequest
  {
    String str = new String(paramArrayOfByte);
    Object localObject = (Servant)this.contexts.get(str);
    if (localObject == null) {
      localObject = readInContext(str);
    }
    return localObject;
  }
  
  public void postinvoke(byte[] paramArrayOfByte, POA paramPOA, String paramString, Object paramObject, Servant paramServant) {}
  
  public NamingContextImpl readInContext(String paramString)
  {
    NamingContextImpl localNamingContextImpl = (NamingContextImpl)this.contexts.get(paramString);
    if (localNamingContextImpl != null) {
      return localNamingContextImpl;
    }
    File localFile = new File(this.logDir, paramString);
    if (localFile.exists()) {
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
        localNamingContextImpl = (NamingContextImpl)localObjectInputStream.readObject();
        localNamingContextImpl.setORB(this.orb);
        localNamingContextImpl.setServantManagerImpl(this);
        localNamingContextImpl.setRootNameService(this.theNameService);
        localObjectInputStream.close();
      }
      catch (Exception localException) {}
    }
    if (localNamingContextImpl != null) {
      this.contexts.put(paramString, localNamingContextImpl);
    }
    return localNamingContextImpl;
  }
  
  public NamingContextImpl addContext(String paramString, NamingContextImpl paramNamingContextImpl)
  {
    File localFile = new File(this.logDir, paramString);
    if (localFile.exists()) {
      paramNamingContextImpl = readInContext(paramString);
    } else {
      try
      {
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
        ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
        localObjectOutputStream.writeObject(paramNamingContextImpl);
        localObjectOutputStream.close();
      }
      catch (Exception localException1) {}
    }
    try
    {
      this.contexts.remove(paramString);
    }
    catch (Exception localException2) {}
    this.contexts.put(paramString, paramNamingContextImpl);
    return paramNamingContextImpl;
  }
  
  public void updateContext(String paramString, NamingContextImpl paramNamingContextImpl)
  {
    File localFile = new File(this.logDir, paramString);
    if (localFile.exists())
    {
      localFile.delete();
      localFile = new File(this.logDir, paramString);
    }
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
      localObjectOutputStream.writeObject(paramNamingContextImpl);
      localObjectOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public static String getRootObjectKey()
  {
    return "NC0";
  }
  
  public String getNewObjectKey()
  {
    return "NC" + this.counterDb.getNextCounter();
  }
}
