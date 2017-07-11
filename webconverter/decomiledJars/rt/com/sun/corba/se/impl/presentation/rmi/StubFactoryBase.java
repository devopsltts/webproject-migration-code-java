package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.StubFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;

public abstract class StubFactoryBase
  implements PresentationManager.StubFactory
{
  private String[] typeIds = null;
  protected final PresentationManager.ClassData classData;
  
  protected StubFactoryBase(PresentationManager.ClassData paramClassData)
  {
    this.classData = paramClassData;
  }
  
  public synchronized String[] getTypeIds()
  {
    if (this.typeIds == null) {
      if (this.classData == null)
      {
        org.omg.CORBA.Object localObject = makeStub();
        this.typeIds = StubAdapter.getTypeIds(localObject);
      }
      else
      {
        this.typeIds = this.classData.getTypeIds();
      }
    }
    return this.typeIds;
  }
}
