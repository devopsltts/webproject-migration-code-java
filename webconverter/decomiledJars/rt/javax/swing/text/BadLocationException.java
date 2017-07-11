package javax.swing.text;

public class BadLocationException
  extends Exception
{
  private int offs;
  
  public BadLocationException(String paramString, int paramInt)
  {
    super(paramString);
    this.offs = paramInt;
  }
  
  public int offsetRequested()
  {
    return this.offs;
  }
}
