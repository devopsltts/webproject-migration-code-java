package org.omg.CORBA;

public abstract interface IRObjectOperations
{
  public abstract DefinitionKind def_kind();
  
  public abstract void destroy();
}
