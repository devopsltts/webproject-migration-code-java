package com.sun.corba.se.impl.util;

class StubEntry
{
  org.omg.CORBA.Object stub;
  boolean mostDerived;
  
  StubEntry(org.omg.CORBA.Object paramObject, boolean paramBoolean)
  {
    this.stub = paramObject;
    this.mostDerived = paramBoolean;
  }
}
