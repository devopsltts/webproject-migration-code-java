package javax.xml.stream;

public abstract interface Location
{
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
  
  public abstract int getCharacterOffset();
  
  public abstract String getPublicId();
  
  public abstract String getSystemId();
}
