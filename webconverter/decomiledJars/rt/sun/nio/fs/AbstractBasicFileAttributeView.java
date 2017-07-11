package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class AbstractBasicFileAttributeView
  implements BasicFileAttributeView, DynamicFileAttributeView
{
  private static final String SIZE_NAME = "size";
  private static final String CREATION_TIME_NAME = "creationTime";
  private static final String LAST_ACCESS_TIME_NAME = "lastAccessTime";
  private static final String LAST_MODIFIED_TIME_NAME = "lastModifiedTime";
  private static final String FILE_KEY_NAME = "fileKey";
  private static final String IS_DIRECTORY_NAME = "isDirectory";
  private static final String IS_REGULAR_FILE_NAME = "isRegularFile";
  private static final String IS_SYMBOLIC_LINK_NAME = "isSymbolicLink";
  private static final String IS_OTHER_NAME = "isOther";
  static final Set<String> basicAttributeNames = Util.newSet(new String[] { "size", "creationTime", "lastAccessTime", "lastModifiedTime", "fileKey", "isDirectory", "isRegularFile", "isSymbolicLink", "isOther" });
  
  protected AbstractBasicFileAttributeView() {}
  
  public String name()
  {
    return "basic";
  }
  
  public void setAttribute(String paramString, Object paramObject)
    throws IOException
  {
    if (paramString.equals("lastModifiedTime"))
    {
      setTimes((FileTime)paramObject, null, null);
      return;
    }
    if (paramString.equals("lastAccessTime"))
    {
      setTimes(null, (FileTime)paramObject, null);
      return;
    }
    if (paramString.equals("creationTime"))
    {
      setTimes(null, null, (FileTime)paramObject);
      return;
    }
    throw new IllegalArgumentException("'" + name() + ":" + paramString + "' not recognized");
  }
  
  final void addRequestedBasicAttributes(BasicFileAttributes paramBasicFileAttributes, AttributesBuilder paramAttributesBuilder)
  {
    if (paramAttributesBuilder.match("size")) {
      paramAttributesBuilder.add("size", Long.valueOf(paramBasicFileAttributes.size()));
    }
    if (paramAttributesBuilder.match("creationTime")) {
      paramAttributesBuilder.add("creationTime", paramBasicFileAttributes.creationTime());
    }
    if (paramAttributesBuilder.match("lastAccessTime")) {
      paramAttributesBuilder.add("lastAccessTime", paramBasicFileAttributes.lastAccessTime());
    }
    if (paramAttributesBuilder.match("lastModifiedTime")) {
      paramAttributesBuilder.add("lastModifiedTime", paramBasicFileAttributes.lastModifiedTime());
    }
    if (paramAttributesBuilder.match("fileKey")) {
      paramAttributesBuilder.add("fileKey", paramBasicFileAttributes.fileKey());
    }
    if (paramAttributesBuilder.match("isDirectory")) {
      paramAttributesBuilder.add("isDirectory", Boolean.valueOf(paramBasicFileAttributes.isDirectory()));
    }
    if (paramAttributesBuilder.match("isRegularFile")) {
      paramAttributesBuilder.add("isRegularFile", Boolean.valueOf(paramBasicFileAttributes.isRegularFile()));
    }
    if (paramAttributesBuilder.match("isSymbolicLink")) {
      paramAttributesBuilder.add("isSymbolicLink", Boolean.valueOf(paramBasicFileAttributes.isSymbolicLink()));
    }
    if (paramAttributesBuilder.match("isOther")) {
      paramAttributesBuilder.add("isOther", Boolean.valueOf(paramBasicFileAttributes.isOther()));
    }
  }
  
  public Map<String, Object> readAttributes(String[] paramArrayOfString)
    throws IOException
  {
    AttributesBuilder localAttributesBuilder = AttributesBuilder.create(basicAttributeNames, paramArrayOfString);
    addRequestedBasicAttributes(readAttributes(), localAttributesBuilder);
    return localAttributesBuilder.unmodifiableMap();
  }
  
  static class AttributesBuilder
  {
    private Set<String> names = new HashSet();
    private Map<String, Object> map = new HashMap();
    private boolean copyAll;
    
    private AttributesBuilder(Set<String> paramSet, String[] paramArrayOfString)
    {
      for (String str : paramArrayOfString) {
        if (str.equals("*"))
        {
          this.copyAll = true;
        }
        else
        {
          if (!paramSet.contains(str)) {
            throw new IllegalArgumentException("'" + str + "' not recognized");
          }
          this.names.add(str);
        }
      }
    }
    
    static AttributesBuilder create(Set<String> paramSet, String[] paramArrayOfString)
    {
      return new AttributesBuilder(paramSet, paramArrayOfString);
    }
    
    boolean match(String paramString)
    {
      return (this.copyAll) || (this.names.contains(paramString));
    }
    
    void add(String paramString, Object paramObject)
    {
      this.map.put(paramString, paramObject);
    }
    
    Map<String, Object> unmodifiableMap()
    {
      return Collections.unmodifiableMap(this.map);
    }
  }
}
