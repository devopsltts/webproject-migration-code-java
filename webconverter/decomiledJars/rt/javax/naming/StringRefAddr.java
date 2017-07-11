package javax.naming;

public class StringRefAddr
  extends RefAddr
{
  private String contents;
  private static final long serialVersionUID = -8913762495138505527L;
  
  public StringRefAddr(String paramString1, String paramString2)
  {
    super(paramString1);
    this.contents = paramString2;
  }
  
  public Object getContent()
  {
    return this.contents;
  }
}
