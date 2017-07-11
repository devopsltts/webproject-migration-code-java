package org.omg.CORBA;

public final class WrongTransaction
  extends UserException
{
  public WrongTransaction()
  {
    super(WrongTransactionHelper.id());
  }
  
  public WrongTransaction(String paramString)
  {
    super(WrongTransactionHelper.id() + "  " + paramString);
  }
}
