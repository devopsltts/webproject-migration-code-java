package javax.sql.rowset.spi;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;

class ProviderImpl
  extends SyncProvider
{
  private String className = null;
  private String vendorName = null;
  private String ver = null;
  private int index;
  
  ProviderImpl() {}
  
  public void setClassname(String paramString)
  {
    this.className = paramString;
  }
  
  public String getClassname()
  {
    return this.className;
  }
  
  public void setVendor(String paramString)
  {
    this.vendorName = paramString;
  }
  
  public String getVendor()
  {
    return this.vendorName;
  }
  
  public void setVersion(String paramString)
  {
    this.ver = paramString;
  }
  
  public String getVersion()
  {
    return this.ver;
  }
  
  public void setIndex(int paramInt)
  {
    this.index = paramInt;
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public int getDataSourceLock()
    throws SyncProviderException
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(this.className).getDataSourceLock();
    }
    catch (SyncFactoryException localSyncFactoryException)
    {
      throw new SyncProviderException(localSyncFactoryException.getMessage());
    }
    return i;
  }
  
  public int getProviderGrade()
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(this.className).getProviderGrade();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return i;
  }
  
  public String getProviderID()
  {
    return this.className;
  }
  
  public RowSetReader getRowSetReader()
  {
    RowSetReader localRowSetReader = null;
    try
    {
      localRowSetReader = SyncFactory.getInstance(this.className).getRowSetReader();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return localRowSetReader;
  }
  
  public RowSetWriter getRowSetWriter()
  {
    RowSetWriter localRowSetWriter = null;
    try
    {
      localRowSetWriter = SyncFactory.getInstance(this.className).getRowSetWriter();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return localRowSetWriter;
  }
  
  public void setDataSourceLock(int paramInt)
    throws SyncProviderException
  {
    try
    {
      SyncFactory.getInstance(this.className).setDataSourceLock(paramInt);
    }
    catch (SyncFactoryException localSyncFactoryException)
    {
      throw new SyncProviderException(localSyncFactoryException.getMessage());
    }
  }
  
  public int supportsUpdatableView()
  {
    int i = 0;
    try
    {
      i = SyncFactory.getInstance(this.className).supportsUpdatableView();
    }
    catch (SyncFactoryException localSyncFactoryException) {}
    return i;
  }
}
