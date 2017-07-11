package java.time.chrono;

import java.time.DateTimeException;

public enum ThaiBuddhistEra
  implements Era
{
  BEFORE_BE,  BE;
  
  private ThaiBuddhistEra() {}
  
  public static ThaiBuddhistEra of(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return BEFORE_BE;
    case 1: 
      return BE;
    }
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue()
  {
    return ordinal();
  }
}
