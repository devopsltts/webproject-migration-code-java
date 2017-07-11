package javax.print.attribute;

import java.io.Serializable;
import java.net.URI;

public abstract class URISyntax
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -7842661210486401678L;
  private URI uri;
  
  protected URISyntax(URI paramURI)
  {
    this.uri = verify(paramURI);
  }
  
  private static URI verify(URI paramURI)
  {
    if (paramURI == null) {
      throw new NullPointerException(" uri is null");
    }
    return paramURI;
  }
  
  public URI getURI()
  {
    return this.uri;
  }
  
  public int hashCode()
  {
    return this.uri.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof URISyntax)) && (this.uri.equals(((URISyntax)paramObject).uri));
  }
  
  public String toString()
  {
    return this.uri.toString();
  }
}
