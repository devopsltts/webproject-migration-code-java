package sun.audio;

import java.io.IOException;

final class InvalidAudioFormatException
  extends IOException
{
  InvalidAudioFormatException() {}
  
  InvalidAudioFormatException(String paramString)
  {
    super(paramString);
  }
}
