package com.sun.corba.se.spi.orb;

import com.sun.corba.se.impl.orb.ParserAction;
import com.sun.corba.se.impl.orb.ParserActionFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyParser
{
  private List actions = new LinkedList();
  
  public PropertyParser() {}
  
  public PropertyParser add(String paramString1, Operation paramOperation, String paramString2)
  {
    this.actions.add(ParserActionFactory.makeNormalAction(paramString1, paramOperation, paramString2));
    return this;
  }
  
  public PropertyParser addPrefix(String paramString1, Operation paramOperation, String paramString2, Class paramClass)
  {
    this.actions.add(ParserActionFactory.makePrefixAction(paramString1, paramOperation, paramString2, paramClass));
    return this;
  }
  
  public Map parse(Properties paramProperties)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = this.actions.iterator();
    while (localIterator.hasNext())
    {
      ParserAction localParserAction = (ParserAction)localIterator.next();
      Object localObject = localParserAction.apply(paramProperties);
      if (localObject != null) {
        localHashMap.put(localParserAction.getFieldName(), localObject);
      }
    }
    return localHashMap;
  }
  
  public Iterator iterator()
  {
    return this.actions.iterator();
  }
}
