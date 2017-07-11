package sun.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioData
{
  private static final AudioFormat DEFAULT_FORMAT = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
  AudioFormat format;
  byte[] buffer;
  
  public AudioData(byte[] paramArrayOfByte)
  {
    this(DEFAULT_FORMAT, paramArrayOfByte);
    try
    {
      AudioInputStream localAudioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(paramArrayOfByte));
      this.format = localAudioInputStream.getFormat();
      localAudioInputStream.close();
    }
    catch (IOException localIOException) {}catch (UnsupportedAudioFileException localUnsupportedAudioFileException) {}
  }
  
  AudioData(AudioFormat paramAudioFormat, byte[] paramArrayOfByte)
  {
    this.format = paramAudioFormat;
    if (paramArrayOfByte != null) {
      this.buffer = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
    }
  }
}
