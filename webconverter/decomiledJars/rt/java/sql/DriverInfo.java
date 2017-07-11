package java.sql;

class DriverInfo
{
  final Driver driver;
  DriverAction da;
  
  DriverInfo(Driver paramDriver, DriverAction paramDriverAction)
  {
    this.driver = paramDriver;
    this.da = paramDriverAction;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof DriverInfo)) && (this.driver == ((DriverInfo)paramObject).driver);
  }
  
  public int hashCode()
  {
    return this.driver.hashCode();
  }
  
  public String toString()
  {
    return "driver[className=" + this.driver + "]";
  }
  
  DriverAction action()
  {
    return this.da;
  }
}
