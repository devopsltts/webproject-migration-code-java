package com.sun.jndi.toolkit.dir;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class ContextEnumerator
  implements NamingEnumeration<Binding>
{
  private static boolean debug = false;
  private NamingEnumeration<Binding> children = null;
  private Binding currentChild = null;
  private boolean currentReturned = false;
  private Context root;
  private ContextEnumerator currentChildEnum = null;
  private boolean currentChildExpanded = false;
  private boolean rootProcessed = false;
  private int scope = 2;
  private String contextName = "";
  
  public ContextEnumerator(Context paramContext)
    throws NamingException
  {
    this(paramContext, 2);
  }
  
  public ContextEnumerator(Context paramContext, int paramInt)
    throws NamingException
  {
    this(paramContext, paramInt, "", paramInt != 1);
  }
  
  protected ContextEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
    throws NamingException
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("null context passed");
    }
    this.root = paramContext;
    if (paramInt != 0) {
      this.children = getImmediateChildren(paramContext);
    }
    this.scope = paramInt;
    this.contextName = paramString;
    this.rootProcessed = (!paramBoolean);
    prepNextChild();
  }
  
  protected NamingEnumeration<Binding> getImmediateChildren(Context paramContext)
    throws NamingException
  {
    return paramContext.listBindings("");
  }
  
  protected ContextEnumerator newEnumerator(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
    throws NamingException
  {
    return new ContextEnumerator(paramContext, paramInt, paramString, paramBoolean);
  }
  
  public boolean hasMore()
    throws NamingException
  {
    return (!this.rootProcessed) || ((this.scope != 0) && (hasMoreDescendants()));
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
  
  public Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException(localNamingException.toString());
    }
  }
  
  public Binding next()
    throws NamingException
  {
    if (!this.rootProcessed)
    {
      this.rootProcessed = true;
      return new Binding("", this.root.getClass().getName(), this.root, true);
    }
    if ((this.scope != 0) && (hasMoreDescendants())) {
      return getNextDescendant();
    }
    throw new NoSuchElementException();
  }
  
  public void close()
    throws NamingException
  {
    this.root = null;
  }
  
  private boolean hasMoreChildren()
    throws NamingException
  {
    return (this.children != null) && (this.children.hasMore());
  }
  
  private Binding getNextChild()
    throws NamingException
  {
    Binding localBinding1 = (Binding)this.children.next();
    Binding localBinding2 = null;
    if ((localBinding1.isRelative()) && (!this.contextName.equals("")))
    {
      NameParser localNameParser = this.root.getNameParser("");
      Name localName = localNameParser.parse(this.contextName);
      localName.add(localBinding1.getName());
      if (debug) {
        System.out.println("ContextEnumerator: adding " + localName);
      }
      localBinding2 = new Binding(localName.toString(), localBinding1.getClassName(), localBinding1.getObject(), localBinding1.isRelative());
    }
    else
    {
      if (debug) {
        System.out.println("ContextEnumerator: using old binding");
      }
      localBinding2 = localBinding1;
    }
    return localBinding2;
  }
  
  private boolean hasMoreDescendants()
    throws NamingException
  {
    if (!this.currentReturned)
    {
      if (debug) {
        System.out.println("hasMoreDescendants returning " + (this.currentChild != null));
      }
      return this.currentChild != null;
    }
    if ((this.currentChildExpanded) && (this.currentChildEnum.hasMore()))
    {
      if (debug) {
        System.out.println("hasMoreDescendants returning true");
      }
      return true;
    }
    if (debug) {
      System.out.println("hasMoreDescendants returning hasMoreChildren");
    }
    return hasMoreChildren();
  }
  
  private Binding getNextDescendant()
    throws NamingException
  {
    if (!this.currentReturned)
    {
      if (debug) {
        System.out.println("getNextDescedant: simple case");
      }
      this.currentReturned = true;
      return this.currentChild;
    }
    if ((this.currentChildExpanded) && (this.currentChildEnum.hasMore()))
    {
      if (debug) {
        System.out.println("getNextDescedant: expanded case");
      }
      return this.currentChildEnum.next();
    }
    if (debug) {
      System.out.println("getNextDescedant: next case");
    }
    prepNextChild();
    return getNextDescendant();
  }
  
  private void prepNextChild()
    throws NamingException
  {
    if (hasMoreChildren())
    {
      try
      {
        this.currentChild = getNextChild();
        this.currentReturned = false;
      }
      catch (NamingException localNamingException)
      {
        if (debug) {
          System.out.println(localNamingException);
        }
        if (debug) {
          localNamingException.printStackTrace();
        }
      }
    }
    else
    {
      this.currentChild = null;
      return;
    }
    if ((this.scope == 2) && ((this.currentChild.getObject() instanceof Context)))
    {
      this.currentChildEnum = newEnumerator((Context)this.currentChild.getObject(), this.scope, this.currentChild.getName(), false);
      this.currentChildExpanded = true;
      if (debug) {
        System.out.println("prepNextChild: expanded");
      }
    }
    else
    {
      this.currentChildExpanded = false;
      this.currentChildEnum = null;
      if (debug) {
        System.out.println("prepNextChild: normal");
      }
    }
  }
}
