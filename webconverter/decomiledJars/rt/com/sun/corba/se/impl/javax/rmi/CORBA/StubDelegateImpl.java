package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.StubDelegate;
import org.omg.CORBA.ORB;

public class StubDelegateImpl
  implements StubDelegate
{
  static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
  private StubIORImpl ior = null;
  
  public StubIORImpl getIOR()
  {
    return this.ior;
  }
  
  public StubDelegateImpl() {}
  
  private void init(Stub paramStub)
  {
    if (this.ior == null) {
      this.ior = new StubIORImpl(paramStub);
    }
  }
  
  public int hashCode(Stub paramStub)
  {
    init(paramStub);
    return this.ior.hashCode();
  }
  
  public boolean equals(Stub paramStub, Object paramObject)
  {
    if (paramStub == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Stub)) {
      return false;
    }
    Stub localStub = (Stub)paramObject;
    if (localStub.hashCode() != paramStub.hashCode()) {
      return false;
    }
    return paramStub.toString().equals(localStub.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StubDelegateImpl)) {
      return false;
    }
    StubDelegateImpl localStubDelegateImpl = (StubDelegateImpl)paramObject;
    if (this.ior == null) {
      return this.ior == localStubDelegateImpl.ior;
    }
    return this.ior.equals(localStubDelegateImpl.ior);
  }
  
  public int hashCode()
  {
    if (this.ior == null) {
      return 0;
    }
    return this.ior.hashCode();
  }
  
  public String toString(Stub paramStub)
  {
    if (this.ior == null) {
      return null;
    }
    return this.ior.toString();
  }
  
  public void connect(Stub paramStub, ORB paramORB)
    throws RemoteException
  {
    this.ior = StubConnectImpl.connect(this.ior, paramStub, paramStub, paramORB);
  }
  
  public void readObject(Stub paramStub, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (this.ior == null) {
      this.ior = new StubIORImpl();
    }
    this.ior.doRead(paramObjectInputStream);
  }
  
  public void writeObject(Stub paramStub, ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    init(paramStub);
    this.ior.doWrite(paramObjectOutputStream);
  }
}
