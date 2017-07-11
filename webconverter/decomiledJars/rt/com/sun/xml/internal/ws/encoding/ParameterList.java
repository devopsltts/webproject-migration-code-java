package com.sun.xml.internal.ws.encoding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.WebServiceException;

final class ParameterList
{
  private final Map<String, String> list;
  
  ParameterList(String paramString)
  {
    HeaderTokenizer localHeaderTokenizer = new HeaderTokenizer(paramString, "()<>@,;:\\\"\t []/?=");
    this.list = new HashMap();
    for (;;)
    {
      HeaderTokenizer.Token localToken = localHeaderTokenizer.next();
      int i = localToken.getType();
      if (i == -4) {
        return;
      }
      if ((char)i != ';') {
        break;
      }
      localToken = localHeaderTokenizer.next();
      if (localToken.getType() == -4) {
        return;
      }
      if (localToken.getType() != -1) {
        throw new WebServiceException();
      }
      String str = localToken.getValue().toLowerCase();
      localToken = localHeaderTokenizer.next();
      if ((char)localToken.getType() != '=') {
        throw new WebServiceException();
      }
      localToken = localHeaderTokenizer.next();
      i = localToken.getType();
      if ((i != -1) && (i != -2)) {
        throw new WebServiceException();
      }
      this.list.put(str, localToken.getValue());
    }
    throw new WebServiceException();
  }
  
  int size()
  {
    return this.list.size();
  }
  
  String get(String paramString)
  {
    return (String)this.list.get(paramString.trim().toLowerCase());
  }
  
  Iterator<String> getNames()
  {
    return this.list.keySet().iterator();
  }
}
