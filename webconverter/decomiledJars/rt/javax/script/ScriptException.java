package javax.script;

public class ScriptException
  extends Exception
{
  private static final long serialVersionUID = 8265071037049225001L;
  private String fileName;
  private int lineNumber;
  private int columnNumber;
  
  public ScriptException(String paramString)
  {
    super(paramString);
    this.fileName = null;
    this.lineNumber = -1;
    this.columnNumber = -1;
  }
  
  public ScriptException(Exception paramException)
  {
    super(paramException);
    this.fileName = null;
    this.lineNumber = -1;
    this.columnNumber = -1;
  }
  
  public ScriptException(String paramString1, String paramString2, int paramInt)
  {
    super(paramString1);
    this.fileName = paramString2;
    this.lineNumber = paramInt;
    this.columnNumber = -1;
  }
  
  public ScriptException(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    super(paramString1);
    this.fileName = paramString2;
    this.lineNumber = paramInt1;
    this.columnNumber = paramInt2;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if (this.fileName != null)
    {
      str = str + " in " + this.fileName;
      if (this.lineNumber != -1) {
        str = str + " at line number " + this.lineNumber;
      }
      if (this.columnNumber != -1) {
        str = str + " at column number " + this.columnNumber;
      }
    }
    return str;
  }
  
  public int getLineNumber()
  {
    return this.lineNumber;
  }
  
  public int getColumnNumber()
  {
    return this.columnNumber;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
}
