package javax.management.relation;

import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RoleUnresolvedList
  extends ArrayList<Object>
{
  private transient boolean typeSafe;
  private transient boolean tainted;
  private static final long serialVersionUID = 4054902803091433324L;
  
  public RoleUnresolvedList() {}
  
  public RoleUnresolvedList(int paramInt)
  {
    super(paramInt);
  }
  
  public RoleUnresolvedList(List<RoleUnresolved> paramList)
    throws IllegalArgumentException
  {
    if (paramList == null) {
      throw new IllegalArgumentException("Null parameter");
    }
    checkTypeSafe(paramList);
    super.addAll(paramList);
  }
  
  public List<RoleUnresolved> asList()
  {
    if (!this.typeSafe)
    {
      if (this.tainted) {
        checkTypeSafe(this);
      }
      this.typeSafe = true;
    }
    return (List)Util.cast(this);
  }
  
  public void add(RoleUnresolved paramRoleUnresolved)
    throws IllegalArgumentException
  {
    if (paramRoleUnresolved == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    super.add(paramRoleUnresolved);
  }
  
  public void add(int paramInt, RoleUnresolved paramRoleUnresolved)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRoleUnresolved == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    super.add(paramInt, paramRoleUnresolved);
  }
  
  public void set(int paramInt, RoleUnresolved paramRoleUnresolved)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRoleUnresolved == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    super.set(paramInt, paramRoleUnresolved);
  }
  
  public boolean addAll(RoleUnresolvedList paramRoleUnresolvedList)
    throws IndexOutOfBoundsException
  {
    if (paramRoleUnresolvedList == null) {
      return true;
    }
    return super.addAll(paramRoleUnresolvedList);
  }
  
  public boolean addAll(int paramInt, RoleUnresolvedList paramRoleUnresolvedList)
    throws IllegalArgumentException, IndexOutOfBoundsException
  {
    if (paramRoleUnresolvedList == null)
    {
      String str = "Invalid parameter";
      throw new IllegalArgumentException(str);
    }
    return super.addAll(paramInt, paramRoleUnresolvedList);
  }
  
  public boolean add(Object paramObject)
  {
    if (!this.tainted) {
      this.tainted = isTainted(paramObject);
    }
    if (this.typeSafe) {
      checkTypeSafe(paramObject);
    }
    return super.add(paramObject);
  }
  
  public void add(int paramInt, Object paramObject)
  {
    if (!this.tainted) {
      this.tainted = isTainted(paramObject);
    }
    if (this.typeSafe) {
      checkTypeSafe(paramObject);
    }
    super.add(paramInt, paramObject);
  }
  
  public boolean addAll(Collection<?> paramCollection)
  {
    if (!this.tainted) {
      this.tainted = isTainted(paramCollection);
    }
    if (this.typeSafe) {
      checkTypeSafe(paramCollection);
    }
    return super.addAll(paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<?> paramCollection)
  {
    if (!this.tainted) {
      this.tainted = isTainted(paramCollection);
    }
    if (this.typeSafe) {
      checkTypeSafe(paramCollection);
    }
    return super.addAll(paramInt, paramCollection);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    if (!this.tainted) {
      this.tainted = isTainted(paramObject);
    }
    if (this.typeSafe) {
      checkTypeSafe(paramObject);
    }
    return super.set(paramInt, paramObject);
  }
  
  private static void checkTypeSafe(Object paramObject)
  {
    try
    {
      paramObject = (RoleUnresolved)paramObject;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  private static void checkTypeSafe(Collection<?> paramCollection)
  {
    try
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        RoleUnresolved localRoleUnresolved = (RoleUnresolved)localObject;
      }
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  private static boolean isTainted(Object paramObject)
  {
    try
    {
      checkTypeSafe(paramObject);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return true;
    }
    return false;
  }
  
  private static boolean isTainted(Collection<?> paramCollection)
  {
    try
    {
      checkTypeSafe(paramCollection);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return true;
    }
    return false;
  }
}
