package javax.smartcardio;

import java.util.Iterator;
import java.util.List;

public abstract class CardTerminals
{
  protected CardTerminals() {}
  
  public List<CardTerminal> list()
    throws CardException
  {
    return list(State.ALL);
  }
  
  public abstract List<CardTerminal> list(State paramState)
    throws CardException;
  
  public CardTerminal getTerminal(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    try
    {
      Iterator localIterator = list().iterator();
      while (localIterator.hasNext())
      {
        CardTerminal localCardTerminal = (CardTerminal)localIterator.next();
        if (localCardTerminal.getName().equals(paramString)) {
          return localCardTerminal;
        }
      }
      return null;
    }
    catch (CardException localCardException) {}
    return null;
  }
  
  public void waitForChange()
    throws CardException
  {
    waitForChange(0L);
  }
  
  public abstract boolean waitForChange(long paramLong)
    throws CardException;
  
  public static enum State
  {
    ALL,  CARD_PRESENT,  CARD_ABSENT,  CARD_INSERTION,  CARD_REMOVAL;
    
    private State() {}
  }
}
