package com.sun.jndi.cosnaming;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;

final class CNBindingEnumeration
  implements NamingEnumeration<javax.naming.Binding>
{
  private static final int DEFAULT_BATCHSIZE = 100;
  private BindingListHolder _bindingList;
  private BindingIterator _bindingIter;
  private int counter;
  private int batchsize = 100;
  private CNCtx _ctx;
  private Hashtable<?, ?> _env;
  private boolean more = false;
  private boolean isLookedUpCtx = false;
  
  CNBindingEnumeration(CNCtx paramCNCtx, boolean paramBoolean, Hashtable<?, ?> paramHashtable)
  {
    String str = paramHashtable != null ? (String)paramHashtable.get("java.naming.batchsize") : null;
    if (str != null) {
      try
      {
        this.batchsize = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException("Batch size not numeric: " + str);
      }
    }
    this._ctx = paramCNCtx;
    this._ctx.incEnumCount();
    this.isLookedUpCtx = paramBoolean;
    this._env = paramHashtable;
    this._bindingList = new BindingListHolder();
    BindingIteratorHolder localBindingIteratorHolder = new BindingIteratorHolder();
    this._ctx._nc.list(0, this._bindingList, localBindingIteratorHolder);
    this._bindingIter = localBindingIteratorHolder.value;
    if (this._bindingIter != null) {
      this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
    } else {
      this.more = false;
    }
    this.counter = 0;
  }
  
  public javax.naming.Binding next()
    throws NamingException
  {
    if ((this.more) && (this.counter >= this._bindingList.value.length)) {
      getMore();
    }
    if ((this.more) && (this.counter < this._bindingList.value.length))
    {
      org.omg.CosNaming.Binding localBinding = this._bindingList.value[this.counter];
      this.counter += 1;
      return mapBinding(localBinding);
    }
    throw new NoSuchElementException();
  }
  
  public boolean hasMore()
    throws NamingException
  {
    return (this.counter < this._bindingList.value.length) || (getMore());
  }
  
  public boolean hasMoreElements()
  {
    try
    {
      return hasMore();
    }
    catch (NamingException localNamingException) {}
    return false;
  }
  
  public javax.naming.Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException();
    }
  }
  
  public void close()
    throws NamingException
  {
    this.more = false;
    if (this._bindingIter != null)
    {
      this._bindingIter.destroy();
      this._bindingIter = null;
    }
    if (this._ctx != null)
    {
      this._ctx.decEnumCount();
      if (this.isLookedUpCtx) {
        this._ctx.close();
      }
      this._ctx = null;
    }
  }
  
  protected void finalize()
  {
    try
    {
      close();
    }
    catch (NamingException localNamingException) {}
  }
  
  private boolean getMore()
    throws NamingException
  {
    try
    {
      this.more = this._bindingIter.next_n(this.batchsize, this._bindingList);
      this.counter = 0;
    }
    catch (Exception localException)
    {
      this.more = false;
      NamingException localNamingException = new NamingException("Problem getting binding list");
      localNamingException.setRootCause(localException);
      throw localNamingException;
    }
    return this.more;
  }
  
  private javax.naming.Binding mapBinding(org.omg.CosNaming.Binding paramBinding)
    throws NamingException
  {
    Object localObject1 = this._ctx.callResolve(paramBinding.binding_name);
    Name localName = CNNameParser.cosNameToName(paramBinding.binding_name);
    try
    {
      localObject1 = NamingManager.getObjectInstance(localObject1, localName, this._ctx, this._env);
    }
    catch (NamingException localNamingException)
    {
      throw localNamingException;
    }
    catch (Exception localException)
    {
      localObject2 = new NamingException("problem generating object using object factory");
      ((NamingException)localObject2).setRootCause(localException);
      throw ((Throwable)localObject2);
    }
    String str1 = localName.toString();
    Object localObject2 = new javax.naming.Binding(str1, localObject1);
    NameComponent[] arrayOfNameComponent = this._ctx.makeFullName(paramBinding.binding_name);
    String str2 = CNNameParser.cosNameToInsString(arrayOfNameComponent);
    ((javax.naming.Binding)localObject2).setNameInNamespace(str2);
    return localObject2;
  }
}
