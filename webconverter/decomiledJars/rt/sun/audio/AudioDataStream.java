package sun.audio;

import java.io.ByteArrayInputStream;

public class AudioDataStream
  extends ByteArrayInputStream
{
  private final AudioData ad;
  
  public AudioDataStream(AudioData paramAudioData)
  {
    super(paramAudioData.buffer);
    this.ad = paramAudioData;
  }
  
  final AudioData getAudioData()
  {
    return this.ad;
  }
}
