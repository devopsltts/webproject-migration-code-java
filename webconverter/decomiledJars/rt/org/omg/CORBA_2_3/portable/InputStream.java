package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;

public abstract class InputStream
  extends org.omg.CORBA.portable.InputStream
{
  private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";
  private static final boolean allowSubclass = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Boolean run()
    {
      String str = System.getProperty("jdk.corba.allowInputStreamSubclass");
      return Boolean.valueOf(str != null);
    }
  })).booleanValue();
  
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (!allowSubclass)) {
      localSecurityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
    }
    return null;
  }
  
  private InputStream(Void paramVoid) {}
  
  public InputStream()
  {
    this(checkPermission());
  }
  
  public Serializable read_value()
  {
    throw new NO_IMPLEMENT();
  }
  
  public Serializable read_value(Class paramClass)
  {
    throw new NO_IMPLEMENT();
  }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper)
  {
    throw new NO_IMPLEMENT();
  }
  
  public Serializable read_value(String paramString)
  {
    throw new NO_IMPLEMENT();
  }
  
  public Serializable read_value(Serializable paramSerializable)
  {
    throw new NO_IMPLEMENT();
  }
  
  public Object read_abstract_interface()
  {
    throw new NO_IMPLEMENT();
  }
  
  public Object read_abstract_interface(Class paramClass)
  {
    throw new NO_IMPLEMENT();
  }
}
