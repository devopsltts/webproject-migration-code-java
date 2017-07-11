package javax.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum StandardLocation
  implements JavaFileManager.Location
{
  CLASS_OUTPUT,  SOURCE_OUTPUT,  CLASS_PATH,  SOURCE_PATH,  ANNOTATION_PROCESSOR_PATH,  PLATFORM_CLASS_PATH,  NATIVE_HEADER_OUTPUT;
  
  private static final ConcurrentMap<String, JavaFileManager.Location> locations = new ConcurrentHashMap();
  
  private StandardLocation() {}
  
  public static JavaFileManager.Location locationFor(String paramString)
  {
    if (locations.isEmpty()) {
      for (StandardLocation localStandardLocation : values()) {
        locations.putIfAbsent(localStandardLocation.getName(), localStandardLocation);
      }
    }
    locations.putIfAbsent(paramString.toString(), new JavaFileManager.Location()
    {
      public String getName()
      {
        return this.val$name;
      }
      
      public boolean isOutputLocation()
      {
        return this.val$name.endsWith("_OUTPUT");
      }
    });
    return (JavaFileManager.Location)locations.get(paramString);
  }
  
  public String getName()
  {
    return name();
  }
  
  public boolean isOutputLocation()
  {
    switch (2.$SwitchMap$javax$tools$StandardLocation[ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
      return true;
    }
    return false;
  }
}
