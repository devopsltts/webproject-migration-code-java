package javax.sound.sampled;

public abstract interface Port
  extends Line
{
  public static class Info
    extends Line.Info
  {
    public static final Info MICROPHONE = new Info(Port.class, "MICROPHONE", true);
    public static final Info LINE_IN = new Info(Port.class, "LINE_IN", true);
    public static final Info COMPACT_DISC = new Info(Port.class, "COMPACT_DISC", true);
    public static final Info SPEAKER = new Info(Port.class, "SPEAKER", false);
    public static final Info HEADPHONE = new Info(Port.class, "HEADPHONE", false);
    public static final Info LINE_OUT = new Info(Port.class, "LINE_OUT", false);
    private String name;
    private boolean isSource;
    
    public Info(Class<?> paramClass, String paramString, boolean paramBoolean)
    {
      super();
      this.name = paramString;
      this.isSource = paramBoolean;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public boolean isSource()
    {
      return this.isSource;
    }
    
    public boolean matches(Line.Info paramInfo)
    {
      if (!super.matches(paramInfo)) {
        return false;
      }
      if (!this.name.equals(((Info)paramInfo).getName())) {
        return false;
      }
      return this.isSource == ((Info)paramInfo).isSource();
    }
    
    public final boolean equals(Object paramObject)
    {
      return super.equals(paramObject);
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
    
    public final String toString()
    {
      return this.name + (this.isSource == true ? " source" : " target") + " port";
    }
  }
}
