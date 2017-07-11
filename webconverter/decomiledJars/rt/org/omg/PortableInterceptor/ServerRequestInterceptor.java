package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.IDLEntity;

public abstract interface ServerRequestInterceptor
  extends ServerRequestInterceptorOperations, Interceptor, IDLEntity
{}
