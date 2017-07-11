package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;

public abstract class SpecialMethod
{
  static SpecialMethod[] methods = { new IsA(), new GetInterface(), new NonExistent(), new NotExistent() };
  
  public SpecialMethod() {}
  
  public abstract boolean isNonExistentMethod();
  
  public abstract String getName();
  
  public abstract CorbaMessageMediator invoke(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter);
  
  public static final SpecialMethod getSpecialMethod(String paramString)
  {
    for (int i = 0; i < methods.length; i++) {
      if (methods[i].getName().equals(paramString)) {
        return methods[i];
      }
    }
    return null;
  }
}
