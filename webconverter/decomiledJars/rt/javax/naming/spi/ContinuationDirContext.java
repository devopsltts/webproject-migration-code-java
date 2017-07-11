package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

class ContinuationDirContext
  extends ContinuationContext
  implements DirContext
{
  ContinuationDirContext(CannotProceedException paramCannotProceedException, Hashtable<?, ?> paramHashtable)
  {
    super(paramCannotProceedException, paramHashtable);
  }
  
  protected DirContextNamePair getTargetContext(Name paramName)
    throws NamingException
  {
    if (this.cpe.getResolvedObj() == null) {
      throw ((NamingException)this.cpe.fillInStackTrace());
    }
    Context localContext = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if (localContext == null) {
      throw ((NamingException)this.cpe.fillInStackTrace());
    }
    if ((localContext instanceof DirContext)) {
      return new DirContextNamePair((DirContext)localContext, paramName);
    }
    if ((localContext instanceof Resolver))
    {
      localObject = (Resolver)localContext;
      ResolveResult localResolveResult = ((Resolver)localObject).resolveToClass(paramName, DirContext.class);
      DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
      return new DirContextNamePair(localDirContext, localResolveResult.getRemainingName());
    }
    Object localObject = localContext.lookup(paramName);
    if ((localObject instanceof DirContext)) {
      return new DirContextNamePair((DirContext)localObject, new CompositeName());
    }
    throw ((NamingException)this.cpe.fillInStackTrace());
  }
  
  protected DirContextStringPair getTargetContext(String paramString)
    throws NamingException
  {
    if (this.cpe.getResolvedObj() == null) {
      throw ((NamingException)this.cpe.fillInStackTrace());
    }
    Context localContext = NamingManager.getContext(this.cpe.getResolvedObj(), this.cpe.getAltName(), this.cpe.getAltNameCtx(), this.env);
    if ((localContext instanceof DirContext)) {
      return new DirContextStringPair((DirContext)localContext, paramString);
    }
    if ((localContext instanceof Resolver))
    {
      localObject = (Resolver)localContext;
      ResolveResult localResolveResult = ((Resolver)localObject).resolveToClass(paramString, DirContext.class);
      DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
      Name localName = localResolveResult.getRemainingName();
      String str = localName != null ? localName.toString() : "";
      return new DirContextStringPair(localDirContext, str);
    }
    Object localObject = localContext.lookup(paramString);
    if ((localObject instanceof DirContext)) {
      return new DirContextStringPair((DirContext)localObject, "");
    }
    throw ((NamingException)this.cpe.fillInStackTrace());
  }
  
  public Attributes getAttributes(String paramString)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().getAttributes(localDirContextStringPair.getString());
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().getAttributes(localDirContextStringPair.getString(), paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().getAttributes(localDirContextNamePair.getName());
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().getAttributes(localDirContextNamePair.getName(), paramArrayOfString);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    localDirContextNamePair.getDirContext().modifyAttributes(localDirContextNamePair.getName(), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    localDirContextStringPair.getDirContext().modifyAttributes(localDirContextStringPair.getString(), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    localDirContextNamePair.getDirContext().modifyAttributes(localDirContextNamePair.getName(), paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    localDirContextStringPair.getDirContext().modifyAttributes(localDirContextStringPair.getString(), paramArrayOfModificationItem);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    localDirContextNamePair.getDirContext().bind(localDirContextNamePair.getName(), paramObject, paramAttributes);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    localDirContextStringPair.getDirContext().bind(localDirContextStringPair.getString(), paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    localDirContextNamePair.getDirContext().rebind(localDirContextNamePair.getName(), paramObject, paramAttributes);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    localDirContextStringPair.getDirContext().rebind(localDirContextStringPair.getString(), paramObject, paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().createSubcontext(localDirContextNamePair.getName(), paramAttributes);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().createSubcontext(localDirContextStringPair.getString(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().search(localDirContextNamePair.getName(), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().search(localDirContextStringPair.getString(), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().search(localDirContextNamePair.getName(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().search(localDirContextStringPair.getString(), paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().search(localDirContextNamePair.getName(), paramString, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString1);
    return localDirContextStringPair.getDirContext().search(localDirContextStringPair.getString(), paramString2, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().search(localDirContextNamePair.getName(), paramString, paramArrayOfObject, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString1);
    return localDirContextStringPair.getDirContext().search(localDirContextStringPair.getString(), paramString2, paramArrayOfObject, paramSearchControls);
  }
  
  public DirContext getSchema(String paramString)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().getSchema(localDirContextStringPair.getString());
  }
  
  public DirContext getSchema(Name paramName)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().getSchema(localDirContextNamePair.getName());
  }
  
  public DirContext getSchemaClassDefinition(String paramString)
    throws NamingException
  {
    DirContextStringPair localDirContextStringPair = getTargetContext(paramString);
    return localDirContextStringPair.getDirContext().getSchemaClassDefinition(localDirContextStringPair.getString());
  }
  
  public DirContext getSchemaClassDefinition(Name paramName)
    throws NamingException
  {
    DirContextNamePair localDirContextNamePair = getTargetContext(paramName);
    return localDirContextNamePair.getDirContext().getSchemaClassDefinition(localDirContextNamePair.getName());
  }
}
