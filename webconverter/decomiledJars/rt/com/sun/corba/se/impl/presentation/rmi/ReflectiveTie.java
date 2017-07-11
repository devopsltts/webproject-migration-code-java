package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public final class ReflectiveTie
  extends Servant
  implements Tie
{
  private Remote target = null;
  private PresentationManager pm;
  private PresentationManager.ClassData classData = null;
  private ORBUtilSystemException wrapper = null;
  
  public ReflectiveTie(PresentationManager paramPresentationManager, ORBUtilSystemException paramORBUtilSystemException)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new DynamicAccessPermission("access"));
    }
    this.pm = paramPresentationManager;
    this.wrapper = paramORBUtilSystemException;
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte)
  {
    return this.classData.getTypeIds();
  }
  
  public void setTarget(Remote paramRemote)
  {
    this.target = paramRemote;
    if (paramRemote == null)
    {
      this.classData = null;
    }
    else
    {
      Class localClass = paramRemote.getClass();
      this.classData = this.pm.getClassData(localClass);
    }
  }
  
  public Remote getTarget()
  {
    return this.target;
  }
  
  public org.omg.CORBA.Object thisObject()
  {
    return _this_object();
  }
  
  public void deactivate()
  {
    try
    {
      _poa().deactivate_object(_poa().servant_to_id(this));
    }
    catch (WrongPolicy localWrongPolicy) {}catch (ObjectNotActive localObjectNotActive) {}catch (ServantNotActive localServantNotActive) {}
  }
  
  public org.omg.CORBA.ORB orb()
  {
    return _orb();
  }
  
  public void orb(org.omg.CORBA.ORB paramORB)
  {
    try
    {
      com.sun.corba.se.spi.orb.ORB localORB = (com.sun.corba.se.spi.orb.ORB)paramORB;
      ((org.omg.CORBA_2_3.ORB)paramORB).set_delegate(this);
    }
    catch (ClassCastException localClassCastException)
    {
      throw this.wrapper.badOrbForServant(localClassCastException);
    }
  }
  
  public org.omg.CORBA.portable.OutputStream _invoke(String paramString, org.omg.CORBA.portable.InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    Method localMethod = null;
    DynamicMethodMarshaller localDynamicMethodMarshaller = null;
    try
    {
      org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream;
      localMethod = this.classData.getIDLNameTranslator().getMethod(paramString);
      if (localMethod == null) {
        throw this.wrapper.methodNotFoundInTie(paramString, this.target.getClass().getName());
      }
      localDynamicMethodMarshaller = this.pm.getDynamicMethodMarshaller(localMethod);
      localObject1 = localDynamicMethodMarshaller.readArguments(localInputStream);
      localObject2 = localMethod.invoke(this.target, (Object[])localObject1);
      org.omg.CORBA_2_3.portable.OutputStream localOutputStream = (org.omg.CORBA_2_3.portable.OutputStream)paramResponseHandler.createReply();
      localDynamicMethodMarshaller.writeResult(localOutputStream, localObject2);
      return localOutputStream;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw this.wrapper.invocationErrorInReflectiveTie(localIllegalAccessException, localMethod.getName(), localMethod.getDeclaringClass().getName());
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw this.wrapper.invocationErrorInReflectiveTie(localIllegalArgumentException, localMethod.getName(), localMethod.getDeclaringClass().getName());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject2;
      Object localObject1 = localInvocationTargetException.getCause();
      if ((localObject1 instanceof SystemException)) {
        throw ((SystemException)localObject1);
      }
      if (((localObject1 instanceof Exception)) && (localDynamicMethodMarshaller.isDeclaredException((Throwable)localObject1)))
      {
        localObject2 = (org.omg.CORBA_2_3.portable.OutputStream)paramResponseHandler.createExceptionReply();
        localDynamicMethodMarshaller.writeException((org.omg.CORBA_2_3.portable.OutputStream)localObject2, (Exception)localObject1);
        return localObject2;
      }
      throw new UnknownException((Throwable)localObject1);
    }
  }
}
