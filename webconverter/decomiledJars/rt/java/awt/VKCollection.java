package java.awt;

import java.util.HashMap;
import java.util.Map;

class VKCollection
{
  Map<Integer, String> code2name = new HashMap();
  Map<String, Integer> name2code = new HashMap();
  
  public VKCollection() {}
  
  public synchronized void put(String paramString, Integer paramInteger)
  {
    assert ((paramString != null) && (paramInteger != null));
    assert (findName(paramInteger) == null);
    assert (findCode(paramString) == null);
    this.code2name.put(paramInteger, paramString);
    this.name2code.put(paramString, paramInteger);
  }
  
  public synchronized Integer findCode(String paramString)
  {
    assert (paramString != null);
    return (Integer)this.name2code.get(paramString);
  }
  
  public synchronized String findName(Integer paramInteger)
  {
    assert (paramInteger != null);
    return (String)this.code2name.get(paramInteger);
  }
}
