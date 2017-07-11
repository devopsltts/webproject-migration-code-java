package javax.xml.soap;

public abstract interface Name
{
  public abstract String getLocalName();
  
  public abstract String getQualifiedName();
  
  public abstract String getPrefix();
  
  public abstract String getURI();
}
