package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public final class WaveFloatFileReader
  extends AudioFileReader
{
  public WaveFloatFileReader() {}
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    paramInputStream.mark(200);
    AudioFileFormat localAudioFileFormat;
    try
    {
      localAudioFileFormat = internal_getAudioFileFormat(paramInputStream);
      paramInputStream.reset();
    }
    finally
    {
      paramInputStream.reset();
    }
    return localAudioFileFormat;
  }
  
  private AudioFileFormat internal_getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    RIFFReader localRIFFReader = new RIFFReader(paramInputStream);
    if (!localRIFFReader.getFormat().equals("RIFF")) {
      throw new UnsupportedAudioFileException();
    }
    if (!localRIFFReader.getType().equals("WAVE")) {
      throw new UnsupportedAudioFileException();
    }
    int i = 0;
    int j = 0;
    int k = 1;
    long l = 1L;
    int m = 1;
    int n = 1;
    while (localRIFFReader.hasNextChunk())
    {
      localObject = localRIFFReader.nextChunk();
      if (((RIFFReader)localObject).getFormat().equals("fmt "))
      {
        i = 1;
        int i1 = ((RIFFReader)localObject).readUnsignedShort();
        if (i1 != 3) {
          throw new UnsupportedAudioFileException();
        }
        k = ((RIFFReader)localObject).readUnsignedShort();
        l = ((RIFFReader)localObject).readUnsignedInt();
        ((RIFFReader)localObject).readUnsignedInt();
        m = ((RIFFReader)localObject).readUnsignedShort();
        n = ((RIFFReader)localObject).readUnsignedShort();
      }
      if (((RIFFReader)localObject).getFormat().equals("data"))
      {
        j = 1;
        break;
      }
    }
    if (i == 0) {
      throw new UnsupportedAudioFileException();
    }
    if (j == 0) {
      throw new UnsupportedAudioFileException();
    }
    Object localObject = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, n, k, m, (float)l, false);
    AudioFileFormat localAudioFileFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE, (AudioFormat)localObject, -1);
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = getAudioFileFormat(paramInputStream);
    RIFFReader localRIFFReader1 = new RIFFReader(paramInputStream);
    if (!localRIFFReader1.getFormat().equals("RIFF")) {
      throw new UnsupportedAudioFileException();
    }
    if (!localRIFFReader1.getType().equals("WAVE")) {
      throw new UnsupportedAudioFileException();
    }
    while (localRIFFReader1.hasNextChunk())
    {
      RIFFReader localRIFFReader2 = localRIFFReader1.nextChunk();
      if (localRIFFReader2.getFormat().equals("data")) {
        return new AudioInputStream(localRIFFReader2, localAudioFileFormat.getFormat(), localRIFFReader2.getSize());
      }
    }
    throw new UnsupportedAudioFileException();
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    AudioFileFormat localAudioFileFormat;
    try
    {
      localAudioFileFormat = getAudioFileFormat(new BufferedInputStream(localInputStream));
    }
    finally
    {
      localInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    AudioFileFormat localAudioFileFormat;
    try
    {
      localAudioFileFormat = getAudioFileFormat(new BufferedInputStream(localFileInputStream));
    }
    finally
    {
      localFileInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    return getAudioInputStream(new BufferedInputStream(paramURL.openStream()));
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    return getAudioInputStream(new BufferedInputStream(new FileInputStream(paramFile)));
  }
}
