package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

@Deprecated
public abstract interface DynValue
  extends Object, DynAny
{
  public abstract String current_member_name();
  
  public abstract TCKind current_member_kind();
  
  public abstract NameValuePair[] get_members();
  
  public abstract void set_members(NameValuePair[] paramArrayOfNameValuePair)
    throws InvalidSeq;
}
