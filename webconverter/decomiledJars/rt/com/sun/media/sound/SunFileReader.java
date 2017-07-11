package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

abstract class SunFileReader
  extends AudioFileReader
{
  protected static final int bisBufferSize = 4096;
  
  SunFileReader() {}
  
  public abstract AudioFileFormat getAudioFileFormat(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioFileFormat getAudioFileFormat(URL paramURL)
    throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioFileFormat getAudioFileFormat(File paramFile)
    throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(InputStream paramInputStream)
    throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(URL paramURL)
    throws UnsupportedAudioFileException, IOException;
  
  public abstract AudioInputStream getAudioInputStream(File paramFile)
    throws UnsupportedAudioFileException, IOException;
  
  final int rllong(DataInputStream paramDataInputStream)
    throws IOException
  {
    int n = 0;
    n = paramDataInputStream.readInt();
    int i = (n & 0xFF) << 24;
    int j = (n & 0xFF00) << 8;
    int k = (n & 0xFF0000) >> 8;
    int m = (n & 0xFF000000) >>> 24;
    n = i | j | k | m;
    return n;
  }
  
  final int big2little(int paramInt)
  {
    int i = (paramInt & 0xFF) << 24;
    int j = (paramInt & 0xFF00) << 8;
    int k = (paramInt & 0xFF0000) >> 8;
    int m = (paramInt & 0xFF000000) >>> 24;
    paramInt = i | j | k | m;
    return paramInt;
  }
  
  final short rlshort(DataInputStream paramDataInputStream)
    throws IOException
  {
    int i = 0;
    i = paramDataInputStream.readShort();
    int j = (short)((i & 0xFF) << 8);
    int k = (short)((i & 0xFF00) >>> 8);
    i = (short)(j | k);
    return i;
  }
  
  final short big2littleShort(short paramShort)
  {
    int i = (short)((paramShort & 0xFF) << 8);
    int j = (short)((paramShort & 0xFF00) >>> 8);
    paramShort = (short)(i | j);
    return paramShort;
  }
  
  static final int calculatePCMFrameSize(int paramInt1, int paramInt2)
  {
    return (paramInt1 + 7) / 8 * paramInt2;
  }
}
