package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase
  implements IdentifiableFactory
{
  private int id;
  
  public int getId()
  {
    return this.id;
  }
  
  public EncapsulationFactoryBase(int paramInt)
  {
    this.id = paramInt;
  }
  
  public final Identifiable create(InputStream paramInputStream)
  {
    InputStream localInputStream = EncapsulationUtility.getEncapsulationStream(paramInputStream);
    return readContents(localInputStream);
  }
  
  protected abstract Identifiable readContents(InputStream paramInputStream);
}
