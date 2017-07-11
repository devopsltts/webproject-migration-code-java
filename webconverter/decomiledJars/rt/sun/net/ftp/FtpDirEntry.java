package sun.net.ftp;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class FtpDirEntry
{
  private final String name;
  private String user = null;
  private String group = null;
  private long size = -1L;
  private Date created = null;
  private Date lastModified = null;
  private Type type = Type.FILE;
  private boolean[][] permissions = (boolean[][])null;
  private HashMap<String, String> facts = new HashMap();
  
  private FtpDirEntry()
  {
    this.name = null;
  }
  
  public FtpDirEntry(String paramString)
  {
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getUser()
  {
    return this.user;
  }
  
  public FtpDirEntry setUser(String paramString)
  {
    this.user = paramString;
    return this;
  }
  
  public String getGroup()
  {
    return this.group;
  }
  
  public FtpDirEntry setGroup(String paramString)
  {
    this.group = paramString;
    return this;
  }
  
  public long getSize()
  {
    return this.size;
  }
  
  public FtpDirEntry setSize(long paramLong)
  {
    this.size = paramLong;
    return this;
  }
  
  public Type getType()
  {
    return this.type;
  }
  
  public FtpDirEntry setType(Type paramType)
  {
    this.type = paramType;
    return this;
  }
  
  public Date getLastModified()
  {
    return this.lastModified;
  }
  
  public FtpDirEntry setLastModified(Date paramDate)
  {
    this.lastModified = paramDate;
    return this;
  }
  
  public boolean canRead(Permission paramPermission)
  {
    if (this.permissions != null) {
      return this.permissions[paramPermission.value][0];
    }
    return false;
  }
  
  public boolean canWrite(Permission paramPermission)
  {
    if (this.permissions != null) {
      return this.permissions[paramPermission.value][1];
    }
    return false;
  }
  
  public boolean canExexcute(Permission paramPermission)
  {
    if (this.permissions != null) {
      return this.permissions[paramPermission.value][2];
    }
    return false;
  }
  
  public FtpDirEntry setPermissions(boolean[][] paramArrayOfBoolean)
  {
    this.permissions = paramArrayOfBoolean;
    return this;
  }
  
  public FtpDirEntry addFact(String paramString1, String paramString2)
  {
    this.facts.put(paramString1.toLowerCase(), paramString2);
    return this;
  }
  
  public String getFact(String paramString)
  {
    return (String)this.facts.get(paramString.toLowerCase());
  }
  
  public Date getCreated()
  {
    return this.created;
  }
  
  public FtpDirEntry setCreated(Date paramDate)
  {
    this.created = paramDate;
    return this;
  }
  
  public String toString()
  {
    if (this.lastModified == null) {
      return this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") " + this.size;
    }
    return this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") {" + this.size + "} " + DateFormat.getDateInstance().format(this.lastModified);
  }
  
  public static enum Permission
  {
    USER(0),  GROUP(1),  OTHERS(2);
    
    int value;
    
    private Permission(int paramInt)
    {
      this.value = paramInt;
    }
  }
  
  public static enum Type
  {
    FILE,  DIR,  PDIR,  CDIR,  LINK;
    
    private Type() {}
  }
}
