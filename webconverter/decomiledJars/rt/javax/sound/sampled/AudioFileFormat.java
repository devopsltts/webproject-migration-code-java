package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFileFormat
{
  private Type type;
  private int byteLength;
  private AudioFormat format;
  private int frameLength;
  private HashMap<String, Object> properties;
  
  protected AudioFileFormat(Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2)
  {
    this.type = paramType;
    this.byteLength = paramInt1;
    this.format = paramAudioFormat;
    this.frameLength = paramInt2;
    this.properties = null;
  }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt)
  {
    this(paramType, -1, paramAudioFormat, paramInt);
  }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt, Map<String, Object> paramMap)
  {
    this(paramType, -1, paramAudioFormat, paramInt);
    this.properties = new HashMap(paramMap);
  }
  
  public Type getType()
  {
    return this.type;
  }
  
  public int getByteLength()
  {
    return this.byteLength;
  }
  
  public AudioFormat getFormat()
  {
    return this.format;
  }
  
  public int getFrameLength()
  {
    return this.frameLength;
  }
  
  public Map<String, Object> properties()
  {
    Object localObject;
    if (this.properties == null) {
      localObject = new HashMap(0);
    } else {
      localObject = (Map)this.properties.clone();
    }
    return Collections.unmodifiableMap((Map)localObject);
  }
  
  public Object getProperty(String paramString)
  {
    if (this.properties == null) {
      return null;
    }
    return this.properties.get(paramString);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (this.type != null) {
      localStringBuffer.append(this.type.toString() + " (." + this.type.getExtension() + ") file");
    } else {
      localStringBuffer.append("unknown file format");
    }
    if (this.byteLength != -1) {
      localStringBuffer.append(", byte length: " + this.byteLength);
    }
    localStringBuffer.append(", data format: " + this.format);
    if (this.frameLength != -1) {
      localStringBuffer.append(", frame length: " + this.frameLength);
    }
    return new String(localStringBuffer);
  }
  
  public static class Type
  {
    public static final Type WAVE = new Type("WAVE", "wav");
    public static final Type AU = new Type("AU", "au");
    public static final Type AIFF = new Type("AIFF", "aif");
    public static final Type AIFC = new Type("AIFF-C", "aifc");
    public static final Type SND = new Type("SND", "snd");
    private final String name;
    private final String extension;
    
    public Type(String paramString1, String paramString2)
    {
      this.name = paramString1;
      this.extension = paramString2;
    }
    
    public final boolean equals(Object paramObject)
    {
      if (toString() == null) {
        return (paramObject != null) && (paramObject.toString() == null);
      }
      if ((paramObject instanceof Type)) {
        return toString().equals(paramObject.toString());
      }
      return false;
    }
    
    public final int hashCode()
    {
      if (toString() == null) {
        return 0;
      }
      return toString().hashCode();
    }
    
    public final String toString()
    {
      return this.name;
    }
    
    public String getExtension()
    {
      return this.extension;
    }
  }
}
