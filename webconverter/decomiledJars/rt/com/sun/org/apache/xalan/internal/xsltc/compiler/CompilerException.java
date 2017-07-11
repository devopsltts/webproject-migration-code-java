package com.sun.org.apache.xalan.internal.xsltc.compiler;

public final class CompilerException
  extends Exception
{
  static final long serialVersionUID = 1732939618562742663L;
  private String _msg;
  
  public CompilerException() {}
  
  public CompilerException(Exception paramException)
  {
    super(paramException.toString());
    this._msg = paramException.toString();
  }
  
  public CompilerException(String paramString)
  {
    super(paramString);
    this._msg = paramString;
  }
  
  public String getMessage()
  {
    int i = this._msg.indexOf(':');
    if (i > -1) {
      return this._msg.substring(i);
    }
    return this._msg;
  }
}
