package java.io;

public class OptionalDataException
  extends ObjectStreamException
{
  private static final long serialVersionUID = -8011121865681257820L;
  public int length;
  public boolean eof;
  
  OptionalDataException(int paramInt)
  {
    this.eof = false;
    this.length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean)
  {
    this.length = 0;
    this.eof = paramBoolean;
  }
}
