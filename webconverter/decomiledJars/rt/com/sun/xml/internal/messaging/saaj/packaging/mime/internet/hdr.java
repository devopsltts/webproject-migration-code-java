package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;

class hdr
  implements Header
{
  String name;
  String line;
  
  hdr(String paramString)
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      this.name = paramString.trim();
    } else {
      this.name = paramString.substring(0, i).trim();
    }
    this.line = paramString;
  }
  
  hdr(String paramString1, String paramString2)
  {
    this.name = paramString1;
    this.line = (paramString1 + ": " + paramString2);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getValue()
  {
    int i = this.line.indexOf(':');
    if (i < 0) {
      return this.line;
    }
    int k;
    if (this.name.equalsIgnoreCase("Content-Description")) {
      for (j = i + 1; j < this.line.length(); j++)
      {
        k = this.line.charAt(j);
        if ((k != 9) && (k != 13) && (k != 10)) {
          break;
        }
      }
    }
    for (int j = i + 1; j < this.line.length(); j++)
    {
      k = this.line.charAt(j);
      if ((k != 32) && (k != 9) && (k != 13) && (k != 10)) {
        break;
      }
    }
    return this.line.substring(j);
  }
}
