package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;

public class ExceptionHandlerImpl
  implements ExceptionHandler
{
  private ExceptionRW[] rws;
  private final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.presentation");
  
  public ExceptionHandlerImpl(Class[] paramArrayOfClass)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfClass.length; j++)
    {
      Class localClass1 = paramArrayOfClass[j];
      if (!RemoteException.class.isAssignableFrom(localClass1)) {
        i++;
      }
    }
    this.rws = new ExceptionRW[i];
    j = 0;
    for (int k = 0; k < paramArrayOfClass.length; k++)
    {
      Class localClass2 = paramArrayOfClass[k];
      if (!RemoteException.class.isAssignableFrom(localClass2))
      {
        Object localObject = null;
        if (UserException.class.isAssignableFrom(localClass2)) {
          localObject = new ExceptionRWIDLImpl(localClass2);
        } else {
          localObject = new ExceptionRWRMIImpl(localClass2);
        }
        this.rws[(j++)] = localObject;
      }
    }
  }
  
  private int findDeclaredException(Class paramClass)
  {
    for (int i = 0; i < this.rws.length; i++)
    {
      Class localClass = this.rws[i].getExceptionClass();
      if (localClass.isAssignableFrom(paramClass)) {
        return i;
      }
    }
    return -1;
  }
  
  private int findDeclaredException(String paramString)
  {
    for (int i = 0; i < this.rws.length; i++)
    {
      if (this.rws[i] == null) {
        return -1;
      }
      String str = this.rws[i].getId();
      if (paramString.equals(str)) {
        return i;
      }
    }
    return -1;
  }
  
  public boolean isDeclaredException(Class paramClass)
  {
    return findDeclaredException(paramClass) >= 0;
  }
  
  public void writeException(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
  {
    int i = findDeclaredException(paramException.getClass());
    if (i < 0) {
      throw this.wrapper.writeUndeclaredException(paramException, paramException.getClass().getName());
    }
    this.rws[i].write(paramOutputStream, paramException);
  }
  
  public Exception readException(ApplicationException paramApplicationException)
  {
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramApplicationException.getInputStream();
    String str = paramApplicationException.getId();
    int i = findDeclaredException(str);
    if (i < 0)
    {
      str = localInputStream.read_string();
      UnexpectedException localUnexpectedException = new UnexpectedException(str);
      localUnexpectedException.initCause(paramApplicationException);
      return localUnexpectedException;
    }
    return this.rws[i].read(localInputStream);
  }
  
  public ExceptionRW getRMIExceptionRW(Class paramClass)
  {
    return new ExceptionRWRMIImpl(paramClass);
  }
  
  public static abstract interface ExceptionRW
  {
    public abstract Class getExceptionClass();
    
    public abstract String getId();
    
    public abstract void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException);
    
    public abstract Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream);
  }
  
  public abstract class ExceptionRWBase
    implements ExceptionHandlerImpl.ExceptionRW
  {
    private Class cls;
    private String id;
    
    public ExceptionRWBase(Class paramClass)
    {
      this.cls = paramClass;
    }
    
    public Class getExceptionClass()
    {
      return this.cls;
    }
    
    public String getId()
    {
      return this.id;
    }
    
    void setId(String paramString)
    {
      this.id = paramString;
    }
  }
  
  public class ExceptionRWIDLImpl
    extends ExceptionHandlerImpl.ExceptionRWBase
  {
    private Method readMethod;
    private Method writeMethod;
    
    public ExceptionRWIDLImpl(Class paramClass)
    {
      super(paramClass);
      String str = paramClass.getName() + "Helper";
      ClassLoader localClassLoader = paramClass.getClassLoader();
      Class localClass;
      try
      {
        localClass = Class.forName(str, true, localClassLoader);
        Method localMethod = localClass.getDeclaredMethod("id", (Class[])null);
        setId((String)localMethod.invoke(null, (Object[])null));
      }
      catch (Exception localException1)
      {
        throw ExceptionHandlerImpl.this.wrapper.badHelperIdMethod(localException1, str);
      }
      try
      {
        Class[] arrayOfClass1 = { org.omg.CORBA.portable.OutputStream.class, paramClass };
        this.writeMethod = localClass.getDeclaredMethod("write", arrayOfClass1);
      }
      catch (Exception localException2)
      {
        throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod(localException2, str);
      }
      try
      {
        Class[] arrayOfClass2 = { org.omg.CORBA.portable.InputStream.class };
        this.readMethod = localClass.getDeclaredMethod("read", arrayOfClass2);
      }
      catch (Exception localException3)
      {
        throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod(localException3, str);
      }
    }
    
    public void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
    {
      try
      {
        Object[] arrayOfObject = { paramOutputStream, paramException };
        this.writeMethod.invoke(null, arrayOfObject);
      }
      catch (Exception localException)
      {
        throw ExceptionHandlerImpl.this.wrapper.badHelperWriteMethod(localException, this.writeMethod.getDeclaringClass().getName());
      }
    }
    
    public Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
    {
      try
      {
        Object[] arrayOfObject = { paramInputStream };
        return (Exception)this.readMethod.invoke(null, arrayOfObject);
      }
      catch (Exception localException)
      {
        throw ExceptionHandlerImpl.this.wrapper.badHelperReadMethod(localException, this.readMethod.getDeclaringClass().getName());
      }
    }
  }
  
  public class ExceptionRWRMIImpl
    extends ExceptionHandlerImpl.ExceptionRWBase
  {
    public ExceptionRWRMIImpl(Class paramClass)
    {
      super(paramClass);
      setId(IDLNameTranslatorImpl.getExceptionId(paramClass));
    }
    
    public void write(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Exception paramException)
    {
      paramOutputStream.write_string(getId());
      paramOutputStream.write_value(paramException, getExceptionClass());
    }
    
    public Exception read(org.omg.CORBA_2_3.portable.InputStream paramInputStream)
    {
      paramInputStream.read_string();
      return (Exception)paramInputStream.read_value(getExceptionClass());
    }
  }
}
