package javax.sound.sampled;

import java.security.BasicPermission;

public class AudioPermission
  extends BasicPermission
{
  public AudioPermission(String paramString)
  {
    super(paramString);
  }
  
  public AudioPermission(String paramString1, String paramString2)
  {
    super(paramString1, paramString2);
  }
}
