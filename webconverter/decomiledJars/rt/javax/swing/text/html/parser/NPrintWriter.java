package javax.swing.text.html.parser;

import java.io.PrintWriter;

class NPrintWriter
  extends PrintWriter
{
  private int numLines = 5;
  private int numPrinted = 0;
  
  public NPrintWriter(int paramInt)
  {
    super(System.out);
    this.numLines = paramInt;
  }
  
  public void println(char[] paramArrayOfChar)
  {
    if (this.numPrinted >= this.numLines) {
      return;
    }
    Object localObject = null;
    for (int i = 0; i < paramArrayOfChar.length; i++)
    {
      if (paramArrayOfChar[i] == '\n') {
        this.numPrinted += 1;
      }
      if (this.numPrinted == this.numLines) {
        System.arraycopy(paramArrayOfChar, 0, localObject, 0, i);
      }
    }
    if (localObject != null) {
      super.print(localObject);
    }
    if (this.numPrinted == this.numLines) {
      return;
    }
    super.println(paramArrayOfChar);
    this.numPrinted += 1;
  }
}
