package javax.security.auth.callback;

import java.io.Serializable;

public class PasswordCallback
  implements Callback, Serializable
{
  private static final long serialVersionUID = 2267422647454909926L;
  private String prompt;
  private boolean echoOn;
  private char[] inputPassword;
  
  public PasswordCallback(String paramString, boolean paramBoolean)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException();
    }
    this.prompt = paramString;
    this.echoOn = paramBoolean;
  }
  
  public String getPrompt()
  {
    return this.prompt;
  }
  
  public boolean isEchoOn()
  {
    return this.echoOn;
  }
  
  public void setPassword(char[] paramArrayOfChar)
  {
    this.inputPassword = (paramArrayOfChar == null ? null : (char[])paramArrayOfChar.clone());
  }
  
  public char[] getPassword()
  {
    return this.inputPassword == null ? null : (char[])this.inputPassword.clone();
  }
  
  public void clearPassword()
  {
    if (this.inputPassword != null) {
      for (int i = 0; i < this.inputPassword.length; i++) {
        this.inputPassword[i] = ' ';
      }
    }
  }
}
