package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.logging.ActivationSystemException;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerAlreadyInstalled;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerAlreadyUninstalled;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation._RepositoryImplBase;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketOrChannelAcceptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

public class RepositoryImpl
  extends _RepositoryImplBase
  implements Serializable
{
  private static final long serialVersionUID = 8458417785209341858L;
  private transient boolean debug = false;
  static final int illegalServerId = -1;
  private transient RepositoryDB db = null;
  transient ORB orb = null;
  transient ActivationSystemException wrapper;
  
  RepositoryImpl(ORB paramORB, File paramFile, boolean paramBoolean)
  {
    this.debug = paramBoolean;
    this.orb = paramORB;
    this.wrapper = ActivationSystemException.get(paramORB, "orbd.repository");
    File localFile = new File(paramFile, "servers.db");
    if (!localFile.exists())
    {
      this.db = new RepositoryDB(localFile);
      this.db.flush();
    }
    else
    {
      try
      {
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
        this.db = ((RepositoryDB)localObjectInputStream.readObject());
        localObjectInputStream.close();
      }
      catch (Exception localException)
      {
        throw this.wrapper.cannotReadRepositoryDb(localException);
      }
    }
    paramORB.connect(this);
  }
  
  private String printServerDef(ServerDef paramServerDef)
  {
    return "ServerDef[applicationName=" + paramServerDef.applicationName + " serverName=" + paramServerDef.serverName + " serverClassPath=" + paramServerDef.serverClassPath + " serverArgs=" + paramServerDef.serverArgs + " serverVmArgs=" + paramServerDef.serverVmArgs + "]";
  }
  
  public int registerServer(ServerDef paramServerDef, int paramInt)
    throws ServerAlreadyRegistered
  {
    DBServerDef localDBServerDef = null;
    synchronized (this.db)
    {
      Enumeration localEnumeration = this.db.serverTable.elements();
      while (localEnumeration.hasMoreElements())
      {
        localDBServerDef = (DBServerDef)localEnumeration.nextElement();
        if (paramServerDef.applicationName.equals(localDBServerDef.applicationName))
        {
          if (this.debug) {
            System.out.println("RepositoryImpl: registerServer called to register ServerDef " + printServerDef(paramServerDef) + " with " + (paramInt == -1 ? "a new server Id" : new StringBuilder().append("server Id ").append(paramInt).toString()) + " FAILED because it is already registered.");
          }
          throw new ServerAlreadyRegistered(localDBServerDef.id);
        }
      }
      int i;
      if (paramInt == -1) {
        i = this.db.incrementServerIdCounter();
      } else {
        i = paramInt;
      }
      localDBServerDef = new DBServerDef(paramServerDef, i);
      this.db.serverTable.put(new Integer(i), localDBServerDef);
      this.db.flush();
      if (this.debug) {
        if (paramInt == -1) {
          System.out.println("RepositoryImpl: registerServer called to register ServerDef " + printServerDef(paramServerDef) + " with new serverId " + i);
        } else {
          System.out.println("RepositoryImpl: registerServer called to register ServerDef " + printServerDef(paramServerDef) + " with assigned serverId " + i);
        }
      }
      return i;
    }
  }
  
  public int registerServer(ServerDef paramServerDef)
    throws ServerAlreadyRegistered, BadServerDefinition
  {
    LegacyServerSocketEndPointInfo localLegacyServerSocketEndPointInfo = this.orb.getLegacyServerSocketManager().legacyGetEndpoint("BOOT_NAMING");
    int i = ((SocketOrChannelAcceptor)localLegacyServerSocketEndPointInfo).getServerSocket().getLocalPort();
    ServerTableEntry localServerTableEntry = new ServerTableEntry(this.wrapper, -1, paramServerDef, i, "", true, this.debug);
    switch (localServerTableEntry.verify())
    {
    case 0: 
      break;
    case 1: 
      throw new BadServerDefinition("main class not found.");
    case 2: 
      throw new BadServerDefinition("no main method found.");
    case 3: 
      throw new BadServerDefinition("server application error.");
    default: 
      throw new BadServerDefinition("unknown Exception.");
    }
    return registerServer(paramServerDef, -1);
  }
  
  public void unregisterServer(int paramInt)
    throws ServerNotRegistered
  {
    DBServerDef localDBServerDef = null;
    Integer localInteger = new Integer(paramInt);
    synchronized (this.db)
    {
      localDBServerDef = (DBServerDef)this.db.serverTable.get(localInteger);
      if (localDBServerDef == null)
      {
        if (this.debug) {
          System.out.println("RepositoryImpl: unregisterServer for serverId " + paramInt + " called: server not registered");
        }
        throw new ServerNotRegistered();
      }
      this.db.serverTable.remove(localInteger);
      this.db.flush();
    }
    if (this.debug) {
      System.out.println("RepositoryImpl: unregisterServer for serverId " + paramInt + " called");
    }
  }
  
  private DBServerDef getDBServerDef(int paramInt)
    throws ServerNotRegistered
  {
    Integer localInteger = new Integer(paramInt);
    DBServerDef localDBServerDef = (DBServerDef)this.db.serverTable.get(localInteger);
    if (localDBServerDef == null) {
      throw new ServerNotRegistered(paramInt);
    }
    return localDBServerDef;
  }
  
  public ServerDef getServer(int paramInt)
    throws ServerNotRegistered
  {
    DBServerDef localDBServerDef = getDBServerDef(paramInt);
    ServerDef localServerDef = new ServerDef(localDBServerDef.applicationName, localDBServerDef.name, localDBServerDef.classPath, localDBServerDef.args, localDBServerDef.vmArgs);
    if (this.debug) {
      System.out.println("RepositoryImpl: getServer for serverId " + paramInt + " returns " + printServerDef(localServerDef));
    }
    return localServerDef;
  }
  
  public boolean isInstalled(int paramInt)
    throws ServerNotRegistered
  {
    DBServerDef localDBServerDef = getDBServerDef(paramInt);
    return localDBServerDef.isInstalled;
  }
  
  public void install(int paramInt)
    throws ServerNotRegistered, ServerAlreadyInstalled
  {
    DBServerDef localDBServerDef = getDBServerDef(paramInt);
    if (localDBServerDef.isInstalled) {
      throw new ServerAlreadyInstalled(paramInt);
    }
    localDBServerDef.isInstalled = true;
    this.db.flush();
  }
  
  public void uninstall(int paramInt)
    throws ServerNotRegistered, ServerAlreadyUninstalled
  {
    DBServerDef localDBServerDef = getDBServerDef(paramInt);
    if (!localDBServerDef.isInstalled) {
      throw new ServerAlreadyUninstalled(paramInt);
    }
    localDBServerDef.isInstalled = false;
    this.db.flush();
  }
  
  public int[] listRegisteredServers()
  {
    synchronized (this.db)
    {
      int i = 0;
      int[] arrayOfInt = new int[this.db.serverTable.size()];
      Enumeration localEnumeration = this.db.serverTable.elements();
      Object localObject1;
      while (localEnumeration.hasMoreElements())
      {
        localObject1 = (DBServerDef)localEnumeration.nextElement();
        arrayOfInt[(i++)] = ((DBServerDef)localObject1).id;
      }
      if (this.debug)
      {
        localObject1 = new StringBuffer();
        for (int j = 0; j < arrayOfInt.length; j++)
        {
          ((StringBuffer)localObject1).append(' ');
          ((StringBuffer)localObject1).append(arrayOfInt[j]);
        }
        System.out.println("RepositoryImpl: listRegisteredServers returns" + ((StringBuffer)localObject1).toString());
      }
      return arrayOfInt;
    }
  }
  
  public int getServerID(String paramString)
    throws ServerNotRegistered
  {
    synchronized (this.db)
    {
      int i = -1;
      Enumeration localEnumeration = this.db.serverTable.keys();
      while (localEnumeration.hasMoreElements())
      {
        Integer localInteger = (Integer)localEnumeration.nextElement();
        DBServerDef localDBServerDef = (DBServerDef)this.db.serverTable.get(localInteger);
        if (localDBServerDef.applicationName.equals(paramString))
        {
          i = localInteger.intValue();
          break;
        }
      }
      if (this.debug) {
        System.out.println("RepositoryImpl: getServerID for " + paramString + " is " + i);
      }
      if (i == -1) {
        throw new ServerNotRegistered();
      }
      return i;
    }
  }
  
  public String[] getApplicationNames()
  {
    synchronized (this.db)
    {
      Vector localVector = new Vector();
      Object localObject1 = this.db.serverTable.keys();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        Integer localInteger = (Integer)((Enumeration)localObject1).nextElement();
        DBServerDef localDBServerDef = (DBServerDef)this.db.serverTable.get(localInteger);
        if (!localDBServerDef.applicationName.equals("")) {
          localVector.addElement(localDBServerDef.applicationName);
        }
      }
      localObject1 = new String[localVector.size()];
      for (int i = 0; i < localVector.size(); i++) {
        localObject1[i] = ((String)localVector.elementAt(i));
      }
      if (this.debug)
      {
        StringBuffer localStringBuffer = new StringBuffer();
        for (int j = 0; j < localObject1.length; j++)
        {
          localStringBuffer.append(' ');
          localStringBuffer.append(localObject1[j]);
        }
        System.out.println("RepositoryImpl: getApplicationNames returns " + localStringBuffer.toString());
      }
      return localObject1;
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    boolean bool = false;
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-debug")) {
        bool = true;
      }
    }
    try
    {
      Properties localProperties = new Properties();
      localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      ORB localORB = (ORB)ORB.init(paramArrayOfString, localProperties);
      String str = System.getProperty("com.sun.CORBA.activation.db", "db");
      RepositoryImpl localRepositoryImpl = new RepositoryImpl(localORB, new File(str), bool);
      localORB.run();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  class DBServerDef
    implements Serializable
  {
    String applicationName;
    String name;
    String classPath;
    String args;
    String vmArgs;
    boolean isInstalled;
    int id;
    
    public String toString()
    {
      return "DBServerDef(applicationName=" + this.applicationName + ", name=" + this.name + ", classPath=" + this.classPath + ", args=" + this.args + ", vmArgs=" + this.vmArgs + ", id=" + this.id + ", isInstalled=" + this.isInstalled + ")";
    }
    
    DBServerDef(ServerDef paramServerDef, int paramInt)
    {
      this.applicationName = paramServerDef.applicationName;
      this.name = paramServerDef.serverName;
      this.classPath = paramServerDef.serverClassPath;
      this.args = paramServerDef.serverArgs;
      this.vmArgs = paramServerDef.serverVmArgs;
      this.id = paramInt;
      this.isInstalled = false;
    }
  }
  
  class RepositoryDB
    implements Serializable
  {
    File db;
    Hashtable serverTable;
    Integer serverIdCounter;
    
    RepositoryDB(File paramFile)
    {
      this.db = paramFile;
      this.serverTable = new Hashtable(255);
      this.serverIdCounter = new Integer(256);
    }
    
    int incrementServerIdCounter()
    {
      int i = this.serverIdCounter.intValue();
      this.serverIdCounter = new Integer(++i);
      return i;
    }
    
    void flush()
    {
      try
      {
        this.db.delete();
        FileOutputStream localFileOutputStream = new FileOutputStream(this.db);
        ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
        localObjectOutputStream.writeObject(this);
        localObjectOutputStream.flush();
        localObjectOutputStream.close();
      }
      catch (Exception localException)
      {
        throw RepositoryImpl.this.wrapper.cannotWriteRepositoryDb(localException);
      }
    }
  }
}
