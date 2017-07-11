package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;

class WindowsUserPrincipals
{
  private WindowsUserPrincipals() {}
  
  static UserPrincipal fromSid(long paramLong)
    throws IOException
  {
    String str1;
    try
    {
      str1 = WindowsNativeDispatcher.ConvertSidToStringSid(paramLong);
      if (str1 == null) {
        throw new AssertionError();
      }
    }
    catch (WindowsException localWindowsException1)
    {
      throw new IOException("Unable to convert SID to String: " + localWindowsException1.errorString());
    }
    WindowsNativeDispatcher.Account localAccount = null;
    String str2;
    try
    {
      localAccount = WindowsNativeDispatcher.LookupAccountSid(paramLong);
      str2 = localAccount.domain() + "\\" + localAccount.name();
    }
    catch (WindowsException localWindowsException2)
    {
      str2 = str1;
    }
    int i = localAccount == null ? 8 : localAccount.use();
    if ((i == 2) || (i == 5) || (i == 4)) {
      return new Group(str1, i, str2);
    }
    return new User(str1, i, str2);
  }
  
  static UserPrincipal lookup(String paramString)
    throws IOException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("lookupUserInformation"));
    }
    int i = 0;
    try
    {
      i = WindowsNativeDispatcher.LookupAccountName(paramString, 0L, 0);
    }
    catch (WindowsException localWindowsException1)
    {
      if (localWindowsException1.lastError() == 1332) {
        throw new UserPrincipalNotFoundException(paramString);
      }
      throw new IOException(paramString + ": " + localWindowsException1.errorString());
    }
    assert (i > 0);
    NativeBuffer localNativeBuffer = NativeBuffers.getNativeBuffer(i);
    try
    {
      int j = WindowsNativeDispatcher.LookupAccountName(paramString, localNativeBuffer.address(), i);
      if (j != i) {
        throw new AssertionError("SID change during lookup");
      }
      UserPrincipal localUserPrincipal = fromSid(localNativeBuffer.address());
      return localUserPrincipal;
    }
    catch (WindowsException localWindowsException2)
    {
      throw new IOException(paramString + ": " + localWindowsException2.errorString());
    }
    finally
    {
      localNativeBuffer.release();
    }
  }
  
  static class Group
    extends WindowsUserPrincipals.User
    implements GroupPrincipal
  {
    Group(String paramString1, int paramInt, String paramString2)
    {
      super(paramInt, paramString2);
    }
  }
  
  static class User
    implements UserPrincipal
  {
    private final String sidString;
    private final int sidType;
    private final String accountName;
    
    User(String paramString1, int paramInt, String paramString2)
    {
      this.sidString = paramString1;
      this.sidType = paramInt;
      this.accountName = paramString2;
    }
    
    String sidString()
    {
      return this.sidString;
    }
    
    public String getName()
    {
      return this.accountName;
    }
    
    public String toString()
    {
      String str;
      switch (this.sidType)
      {
      case 1: 
        str = "User";
        break;
      case 2: 
        str = "Group";
        break;
      case 3: 
        str = "Domain";
        break;
      case 4: 
        str = "Alias";
        break;
      case 5: 
        str = "Well-known group";
        break;
      case 6: 
        str = "Deleted";
        break;
      case 7: 
        str = "Invalid";
        break;
      case 9: 
        str = "Computer";
        break;
      case 8: 
      default: 
        str = "Unknown";
      }
      return this.accountName + " (" + str + ")";
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof User)) {
        return false;
      }
      User localUser = (User)paramObject;
      return this.sidString.equals(localUser.sidString);
    }
    
    public int hashCode()
    {
      return this.sidString.hashCode();
    }
  }
}
