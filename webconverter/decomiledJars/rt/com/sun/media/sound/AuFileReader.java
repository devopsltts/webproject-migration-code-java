package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
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

public final class AuFileReader
  extends SunFileReader
{
  public AuFileReader() {}
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFormat localAudioFormat = null;
    AuFileFormat localAuFileFormat = null;
    int i = 28;
    boolean bool = false;
    int j = -1;
    int k = -1;
    int m = -1;
    int n = -1;
    int i1 = -1;
    int i2 = -1;
    int i3 = -1;
    int i4 = -1;
    int i6 = 0;
    int i7 = 0;
    AudioFormat.Encoding localEncoding = null;
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    localDataInputStream.mark(i);
    j = localDataInputStream.readInt();
    if ((j != 779316836) || (j == 779314176) || (j == 1684960046) || (j == 6583086))
    {
      localDataInputStream.reset();
      throw new UnsupportedAudioFileException("not an AU file");
    }
    if ((j == 779316836) || (j == 779314176)) {
      bool = true;
    }
    k = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    i7 += 4;
    m = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    i7 += 4;
    n = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    i7 += 4;
    i1 = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    i7 += 4;
    i4 = bool == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    i7 += 4;
    if (i4 <= 0)
    {
      localDataInputStream.reset();
      throw new UnsupportedAudioFileException("Invalid number of channels");
    }
    i2 = i1;
    int i5;
    switch (n)
    {
    case 1: 
      localEncoding = AudioFormat.Encoding.ULAW;
      i5 = 8;
      break;
    case 27: 
      localEncoding = AudioFormat.Encoding.ALAW;
      i5 = 8;
      break;
    case 2: 
      localEncoding = AudioFormat.Encoding.PCM_SIGNED;
      i5 = 8;
      break;
    case 3: 
      localEncoding = AudioFormat.Encoding.PCM_SIGNED;
      i5 = 16;
      break;
    case 4: 
      localEncoding = AudioFormat.Encoding.PCM_SIGNED;
      i5 = 24;
      break;
    case 5: 
      localEncoding = AudioFormat.Encoding.PCM_SIGNED;
      i5 = 32;
      break;
    default: 
      localDataInputStream.reset();
      throw new UnsupportedAudioFileException("not a valid AU file");
    }
    i3 = calculatePCMFrameSize(i5, i4);
    if (m < 0) {
      i6 = -1;
    } else {
      i6 = m / i3;
    }
    localAudioFormat = new AudioFormat(localEncoding, i1, i5, i4, i3, i2, bool);
    localAuFileFormat = new AuFileFormat(AudioFileFormat.Type.AU, m + k, localAudioFormat, i6);
    localDataInputStream.reset();
    return localAuFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = null;
    BufferedInputStream localBufferedInputStream = null;
    AudioFileFormat localAudioFileFormat = null;
    Object localObject1 = null;
    localInputStream = paramURL.openStream();
    try
    {
      localBufferedInputStream = new BufferedInputStream(localInputStream, 4096);
      localAudioFileFormat = getAudioFileFormat(localBufferedInputStream);
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
    FileInputStream localFileInputStream = null;
    BufferedInputStream localBufferedInputStream = null;
    AudioFileFormat localAudioFileFormat = null;
    Object localObject1 = null;
    localFileInputStream = new FileInputStream(paramFile);
    try
    {
      localBufferedInputStream = new BufferedInputStream(localFileInputStream, 4096);
      localAudioFileFormat = getAudioFileFormat(localBufferedInputStream);
    }
    finally
    {
      localFileInputStream.close();
    }
    return localAudioFileFormat;
  }
  
  public AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    DataInputStream localDataInputStream = null;
    AudioFileFormat localAudioFileFormat = null;
    AudioFormat localAudioFormat = null;
    localAudioFileFormat = getAudioFileFormat(paramInputStream);
    localAudioFormat = localAudioFileFormat.getFormat();
    localDataInputStream = new DataInputStream(paramInputStream);
    localDataInputStream.readInt();
    int i = localAudioFormat.isBigEndian() == true ? localDataInputStream.readInt() : rllong(localDataInputStream);
    localDataInputStream.skipBytes(i - 8);
    return new AudioInputStream(localDataInputStream, localAudioFormat, localAudioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = null;
    BufferedInputStream localBufferedInputStream = null;
    Object localObject1 = null;
    localInputStream = paramURL.openStream();
    AudioInputStream localAudioInputStream = null;
    try
    {
      localBufferedInputStream = new BufferedInputStream(localInputStream, 4096);
      localAudioInputStream = getAudioInputStream(localBufferedInputStream);
    }
    finally
    {
      if (localAudioInputStream == null) {
        localInputStream.close();
      }
    }
    return localAudioInputStream;
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    FileInputStream localFileInputStream = null;
    BufferedInputStream localBufferedInputStream = null;
    Object localObject1 = null;
    localFileInputStream = new FileInputStream(paramFile);
    AudioInputStream localAudioInputStream = null;
    try
    {
      localBufferedInputStream = new BufferedInputStream(localFileInputStream, 4096);
      localAudioInputStream = getAudioInputStream(localBufferedInputStream);
    }
    finally
    {
      if (localAudioInputStream == null) {
        localFileInputStream.close();
      }
    }
    return localAudioInputStream;
  }
}
