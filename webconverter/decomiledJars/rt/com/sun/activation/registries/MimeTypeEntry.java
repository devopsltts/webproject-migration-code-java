package com.sun.activation.registries;

public class MimeTypeEntry
{
  private String type;
  private String extension;
  
  public MimeTypeEntry(String paramString1, String paramString2)
  {
    this.type = paramString1;
    this.extension = paramString2;
  }
  
  public String getMIMEType()
  {
    return this.type;
  }
  
  public String getFileExtension()
  {
    return this.extension;
  }
  
  public String toString()
  {
    return "MIMETypeEntry: " + this.type + ", " + this.extension;
  }
}
