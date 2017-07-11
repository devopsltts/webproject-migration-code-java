package java.nio.file;

public final class StandardWatchEventKinds
{
  public static final WatchEvent.Kind<Object> OVERFLOW = new StdWatchEventKind("OVERFLOW", Object.class);
  public static final WatchEvent.Kind<Path> ENTRY_CREATE = new StdWatchEventKind("ENTRY_CREATE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_DELETE = new StdWatchEventKind("ENTRY_DELETE", Path.class);
  public static final WatchEvent.Kind<Path> ENTRY_MODIFY = new StdWatchEventKind("ENTRY_MODIFY", Path.class);
  
  private StandardWatchEventKinds() {}
  
  private static class StdWatchEventKind<T>
    implements WatchEvent.Kind<T>
  {
    private final String name;
    private final Class<T> type;
    
    StdWatchEventKind(String paramString, Class<T> paramClass)
    {
      this.name = paramString;
      this.type = paramClass;
    }
    
    public String name()
    {
      return this.name;
    }
    
    public Class<T> type()
    {
      return this.type;
    }
    
    public String toString()
    {
      return this.name;
    }
  }
}
