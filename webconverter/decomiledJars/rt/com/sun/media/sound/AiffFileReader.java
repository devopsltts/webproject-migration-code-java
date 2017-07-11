package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

public final class AiffFileReader
  extends SunFileReader
{
  private static final int MAX_READ_LENGTH = 8;
  
  public AiffFileReader() {}
  
  public AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = getCOMM(paramInputStream, true);
    paramInputStream.reset();
    return localAudioFileFormat;
  }
  
  public AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    AudioFileFormat localAudioFileFormat = null;
    InputStream localInputStream = paramURL.openStream();
    try
    {
      localAudioFileFormat = getCOMM(localInputStream, false);
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
    AudioFileFormat localAudioFileFormat = null;
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    try
    {
      localAudioFileFormat = getCOMM(localFileInputStream, false);
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
    AudioFileFormat localAudioFileFormat = getCOMM(paramInputStream, true);
    return new AudioInputStream(paramInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException
  {
    InputStream localInputStream = paramURL.openStream();
    AudioFileFormat localAudioFileFormat = null;
    try
    {
      localAudioFileFormat = getCOMM(localInputStream, false);
    }
    finally
    {
      if (localAudioFileFormat == null) {
        localInputStream.close();
      }
    }
    return new AudioInputStream(localInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  public AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    AudioFileFormat localAudioFileFormat = null;
    try
    {
      localAudioFileFormat = getCOMM(localFileInputStream, false);
    }
    finally
    {
      if (localAudioFileFormat == null) {
        localFileInputStream.close();
      }
    }
    return new AudioInputStream(localFileInputStream, localAudioFileFormat.getFormat(), localAudioFileFormat.getFrameLength());
  }
  
  private AudioFileFormat getCOMM(InputStream paramInputStream, boolean paramBoolean)
    throws UnsupportedAudioFileException, IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    if (paramBoolean) {
      localDataInputStream.mark(8);
    }
    int i = 0;
    int j = 0;
    AudioFormat localAudioFormat = null;
    int k = localDataInputStream.readInt();
    if (k != 1179603533)
    {
      if (paramBoolean) {
        localDataInputStream.reset();
      }
      throw new UnsupportedAudioFileException("not an AIFF file");
    }
    int m = localDataInputStream.readInt();
    int n = localDataInputStream.readInt();
    i += 12;
    int i1;
    if (m <= 0)
    {
      m = -1;
      i1 = -1;
    }
    else
    {
      i1 = m + 8;
    }
    int i2 = 0;
    if (n == 1095321155) {
      i2 = 1;
    }
    int i3 = 0;
    while (i3 == 0)
    {
      int i4 = localDataInputStream.readInt();
      int i5 = localDataInputStream.readInt();
      i += 8;
      int i6 = 0;
      int i7;
      switch (i4)
      {
      case 1180058962: 
        break;
      case 1129270605: 
        if (((i2 == 0) && (i5 < 18)) || ((i2 != 0) && (i5 < 22))) {
          throw new UnsupportedAudioFileException("Invalid AIFF/COMM chunksize");
        }
        i7 = localDataInputStream.readUnsignedShort();
        if (i7 <= 0) {
          throw new UnsupportedAudioFileException("Invalid number of channels");
        }
        localDataInputStream.readInt();
        int i8 = localDataInputStream.readUnsignedShort();
        if ((i8 < 1) || (i8 > 32)) {
          throw new UnsupportedAudioFileException("Invalid AIFF/COMM sampleSize");
        }
        float f = (float)read_ieee_extended(localDataInputStream);
        i6 += 18;
        AudioFormat.Encoding localEncoding = AudioFormat.Encoding.PCM_SIGNED;
        if (i2 != 0)
        {
          i9 = localDataInputStream.readInt();
          i6 += 4;
          switch (i9)
          {
          case 1313820229: 
            localEncoding = AudioFormat.Encoding.PCM_SIGNED;
            break;
          case 1970037111: 
            localEncoding = AudioFormat.Encoding.ULAW;
            i8 = 8;
            break;
          default: 
            throw new UnsupportedAudioFileException("Invalid AIFF encoding");
          }
        }
        int i9 = calculatePCMFrameSize(i8, i7);
        localAudioFormat = new AudioFormat(localEncoding, f, i8, i7, i9, f, true);
        break;
      case 1397968452: 
        int i10 = localDataInputStream.readInt();
        int i11 = localDataInputStream.readInt();
        i6 += 8;
        if (i5 < m) {
          j = i5 - i6;
        } else {
          j = m - (i + i6);
        }
        i3 = 1;
      }
      i += i6;
      if (i3 == 0)
      {
        i7 = i5 - i6;
        if (i7 > 0) {
          i += localDataInputStream.skipBytes(i7);
        }
      }
    }
    if (localAudioFormat == null) {
      throw new UnsupportedAudioFileException("missing COMM chunk");
    }
    AudioFileFormat.Type localType = i2 != 0 ? AudioFileFormat.Type.AIFC : AudioFileFormat.Type.AIFF;
    return new AiffFileFormat(localType, i1, localAudioFormat, j / localAudioFormat.getFrameSize());
  }
  
  private void write_ieee_extended(DataOutputStream paramDataOutputStream, double paramDouble)
    throws IOException
  {
    int i = 16398;
    double d = paramDouble;
    while (d < 44000.0D)
    {
      d *= 2.0D;
      i--;
    }
    paramDataOutputStream.writeShort(i);
    paramDataOutputStream.writeInt((int)d << 16);
    paramDataOutputStream.writeInt(0);
  }
  
  private double read_ieee_extended(DataInputStream paramDataInputStream)
    throws IOException
  {
    double d1 = 0.0D;
    int i = 0;
    long l1 = 0L;
    long l2 = 0L;
    double d2 = 3.4028234663852886E38D;
    i = paramDataInputStream.readUnsignedShort();
    long l3 = paramDataInputStream.readUnsignedShort();
    long l4 = paramDataInputStream.readUnsignedShort();
    l1 = l3 << 16 | l4;
    l3 = paramDataInputStream.readUnsignedShort();
    l4 = paramDataInputStream.readUnsignedShort();
    l2 = l3 << 16 | l4;
    if ((i == 0) && (l1 == 0L) && (l2 == 0L))
    {
      d1 = 0.0D;
    }
    else if (i == 32767)
    {
      d1 = d2;
    }
    else
    {
      i -= 16383;
      i -= 31;
      d1 = l1 * Math.pow(2.0D, i);
      i -= 32;
      d1 += l2 * Math.pow(2.0D, i);
    }
    return d1;
  }
}
