package javax.smartcardio;

public class CardNotPresentException
  extends CardException
{
  private static final long serialVersionUID = 1346879911706545215L;
  
  public CardNotPresentException(String paramString)
  {
    super(paramString);
  }
  
  public CardNotPresentException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public CardNotPresentException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
