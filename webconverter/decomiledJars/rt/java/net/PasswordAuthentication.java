package java.net;

public final class PasswordAuthentication
{
  private String userName;
  private char[] password;
  
  public PasswordAuthentication(String paramString, char[] paramArrayOfChar)
  {
    this.userName = paramString;
    this.password = ((char[])paramArrayOfChar.clone());
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public char[] getPassword()
  {
    return this.password;
  }
}
