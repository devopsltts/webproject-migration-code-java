package java.sql;

public abstract interface SQLType
{
  public abstract String getName();
  
  public abstract String getVendor();
  
  public abstract Integer getVendorTypeNumber();
}
