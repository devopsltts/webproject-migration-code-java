package sun.util.locale;

public class StringTokenIterator
{
  private String text;
  private String dlms;
  private char delimiterChar;
  private String token;
  private int start;
  private int end;
  private boolean done;
  
  public StringTokenIterator(String paramString1, String paramString2)
  {
    this.text = paramString1;
    if (paramString2.length() == 1) {
      this.delimiterChar = paramString2.charAt(0);
    } else {
      this.dlms = paramString2;
    }
    setStart(0);
  }
  
  public String first()
  {
    setStart(0);
    return this.token;
  }
  
  public String current()
  {
    return this.token;
  }
  
  public int currentStart()
  {
    return this.start;
  }
  
  public int currentEnd()
  {
    return this.end;
  }
  
  public boolean isDone()
  {
    return this.done;
  }
  
  public String next()
  {
    if (hasNext())
    {
      this.start = (this.end + 1);
      this.end = nextDelimiter(this.start);
      this.token = this.text.substring(this.start, this.end);
    }
    else
    {
      this.start = this.end;
      this.token = null;
      this.done = true;
    }
    return this.token;
  }
  
  public boolean hasNext()
  {
    return this.end < this.text.length();
  }
  
  public StringTokenIterator setStart(int paramInt)
  {
    if (paramInt > this.text.length()) {
      throw new IndexOutOfBoundsException();
    }
    this.start = paramInt;
    this.end = nextDelimiter(this.start);
    this.token = this.text.substring(this.start, this.end);
    this.done = false;
    return this;
  }
  
  public StringTokenIterator setText(String paramString)
  {
    this.text = paramString;
    setStart(0);
    return this;
  }
  
  private int nextDelimiter(int paramInt)
  {
    int i = this.text.length();
    int j;
    if (this.dlms == null)
    {
      for (j = paramInt; j < i; j++) {
        if (this.text.charAt(j) == this.delimiterChar) {
          return j;
        }
      }
    }
    else
    {
      j = this.dlms.length();
      for (int k = paramInt; k < i; k++)
      {
        int m = this.text.charAt(k);
        for (int n = 0; n < j; n++) {
          if (m == this.dlms.charAt(n)) {
            return k;
          }
        }
      }
    }
    return i;
  }
}
