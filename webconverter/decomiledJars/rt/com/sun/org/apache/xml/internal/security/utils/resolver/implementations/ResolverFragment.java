package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ResolverFragment
  extends ResourceResolverSpi
{
  private static Logger log = Logger.getLogger(ResolverFragment.class.getName());
  
  public ResolverFragment() {}
  
  public boolean engineIsThreadSafe()
  {
    return true;
  }
  
  public XMLSignatureInput engineResolveURI(ResourceResolverContext paramResourceResolverContext)
    throws ResourceResolverException
  {
    Document localDocument = paramResourceResolverContext.attr.getOwnerElement().getOwnerDocument();
    Object localObject1 = null;
    if (paramResourceResolverContext.uriToResolve.equals(""))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "ResolverFragment with empty URI (means complete document)");
      }
      localObject1 = localDocument;
    }
    else
    {
      localObject2 = paramResourceResolverContext.uriToResolve.substring(1);
      localObject1 = localDocument.getElementById((String)localObject2);
      Object localObject3;
      if (localObject1 == null)
      {
        localObject3 = new Object[] { localObject2 };
        throw new ResourceResolverException("signature.Verification.MissingID", (Object[])localObject3, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
      }
      if (paramResourceResolverContext.secureValidation)
      {
        localObject3 = paramResourceResolverContext.attr.getOwnerDocument().getDocumentElement();
        if (!XMLUtils.protectAgainstWrappingAttack((Node)localObject3, (String)localObject2))
        {
          Object[] arrayOfObject = { localObject2 };
          throw new ResourceResolverException("signature.Verification.MultipleIDs", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
        }
      }
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Try to catch an Element with ID " + (String)localObject2 + " and Element was " + localObject1);
      }
    }
    Object localObject2 = new XMLSignatureInput((Node)localObject1);
    ((XMLSignatureInput)localObject2).setExcludeComments(true);
    ((XMLSignatureInput)localObject2).setMIMEType("text/xml");
    if ((paramResourceResolverContext.baseUri != null) && (paramResourceResolverContext.baseUri.length() > 0)) {
      ((XMLSignatureInput)localObject2).setSourceURI(paramResourceResolverContext.baseUri.concat(paramResourceResolverContext.uriToResolve));
    } else {
      ((XMLSignatureInput)localObject2).setSourceURI(paramResourceResolverContext.uriToResolve);
    }
    return localObject2;
  }
  
  public boolean engineCanResolveURI(ResourceResolverContext paramResourceResolverContext)
  {
    if (paramResourceResolverContext.uriToResolve == null)
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "Quick fail for null uri");
      }
      return false;
    }
    if ((paramResourceResolverContext.uriToResolve.equals("")) || ((paramResourceResolverContext.uriToResolve.charAt(0) == '#') && (!paramResourceResolverContext.uriToResolve.startsWith("#xpointer("))))
    {
      if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "State I can resolve reference: \"" + paramResourceResolverContext.uriToResolve + "\"");
      }
      return true;
    }
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Do not seem to be able to resolve reference: \"" + paramResourceResolverContext.uriToResolve + "\"");
    }
    return false;
  }
}
