package javax.xml.stream.events;

public abstract interface Characters
  extends XMLEvent
{
  public abstract String getData();
  
  public abstract boolean isWhiteSpace();
  
  public abstract boolean isCData();
  
  public abstract boolean isIgnorableWhiteSpace();
}
