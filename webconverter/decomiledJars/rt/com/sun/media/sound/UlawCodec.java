package com.sun.media.sound;

import java.io.IOException;
import java.util.Vector;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

public final class UlawCodec
  extends SunCodec
{
  private static final byte[] ULAW_TABH = new byte['Ā'];
  private static final byte[] ULAW_TABL = new byte['Ā'];
  private static final AudioFormat.Encoding[] ulawEncodings = { AudioFormat.Encoding.ULAW, AudioFormat.Encoding.PCM_SIGNED };
  private static final short[] seg_end = { 255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE };
  
  public UlawCodec()
  {
    super(ulawEncodings, ulawEncodings);
  }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    AudioFormat.Encoding[] arrayOfEncoding;
    if (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding()))
    {
      if (paramAudioFormat.getSampleSizeInBits() == 16)
      {
        arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.ULAW;
        return arrayOfEncoding;
      }
      return new AudioFormat.Encoding[0];
    }
    if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding()))
    {
      if (paramAudioFormat.getSampleSizeInBits() == 8)
      {
        arrayOfEncoding = new AudioFormat.Encoding[1];
        arrayOfEncoding[0] = AudioFormat.Encoding.PCM_SIGNED;
        return arrayOfEncoding;
      }
      return new AudioFormat.Encoding[0];
    }
    return new AudioFormat.Encoding[0];
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (((AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding)) && (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding()))) || ((AudioFormat.Encoding.ULAW.equals(paramEncoding)) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())))) {
      return getOutputFormats(paramAudioFormat);
    }
    return new AudioFormat[0];
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat1.getEncoding();
    if (localEncoding.equals(paramEncoding)) {
      return paramAudioInputStream;
    }
    AudioFormat localAudioFormat2 = null;
    if (!isConversionSupported(paramEncoding, paramAudioInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
    }
    if ((AudioFormat.Encoding.ULAW.equals(localEncoding)) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramEncoding))) {
      localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 16, localAudioFormat1.getChannels(), 2 * localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), localAudioFormat1.isBigEndian());
    } else if ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (AudioFormat.Encoding.ULAW.equals(paramEncoding))) {
      localAudioFormat2 = new AudioFormat(paramEncoding, localAudioFormat1.getSampleRate(), 8, localAudioFormat1.getChannels(), localAudioFormat1.getChannels(), localAudioFormat1.getSampleRate(), false);
    } else {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramEncoding.toString());
    }
    return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    return getConvertedStream(paramAudioFormat, paramAudioInputStream);
  }
  
  private AudioInputStream getConvertedStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    Object localObject = null;
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    if (localAudioFormat.matches(paramAudioFormat)) {
      localObject = paramAudioInputStream;
    } else {
      localObject = new UlawCodecStream(paramAudioInputStream, paramAudioFormat);
    }
    return localObject;
  }
  
  private AudioFormat[] getOutputFormats(AudioFormat paramAudioFormat)
  {
    Vector localVector = new Vector();
    AudioFormat localAudioFormat;
    if ((paramAudioFormat.getSampleSizeInBits() == 16) && (AudioFormat.Encoding.PCM_SIGNED.equals(paramAudioFormat.getEncoding())))
    {
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, paramAudioFormat.getSampleRate(), 8, paramAudioFormat.getChannels(), paramAudioFormat.getChannels(), paramAudioFormat.getSampleRate(), false);
      localVector.addElement(localAudioFormat);
    }
    if (AudioFormat.Encoding.ULAW.equals(paramAudioFormat.getEncoding()))
    {
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), false);
      localVector.addElement(localAudioFormat);
      localAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, paramAudioFormat.getSampleRate(), 16, paramAudioFormat.getChannels(), paramAudioFormat.getChannels() * 2, paramAudioFormat.getSampleRate(), true);
      localVector.addElement(localAudioFormat);
    }
    AudioFormat[] arrayOfAudioFormat = new AudioFormat[localVector.size()];
    for (int i = 0; i < arrayOfAudioFormat.length; i++) {
      arrayOfAudioFormat[i] = ((AudioFormat)(AudioFormat)localVector.elementAt(i));
    }
    return arrayOfAudioFormat;
  }
  
  static
  {
    for (int i = 0; i < 256; i++)
    {
      int j = i ^ 0xFFFFFFFF;
      j &= 0xFF;
      int k = ((j & 0xF) << 3) + 132;
      k <<= (j & 0x70) >> 4;
      k = (j & 0x80) != 0 ? 132 - k : k - 132;
      ULAW_TABL[i] = ((byte)(k & 0xFF));
      ULAW_TABH[i] = ((byte)(k >> 8 & 0xFF));
    }
  }
  
  class UlawCodecStream
    extends AudioInputStream
  {
    private static final int tempBufferSize = 64;
    private byte[] tempBuffer = null;
    boolean encode = false;
    AudioFormat encodeFormat;
    AudioFormat decodeFormat;
    byte[] tabByte1 = null;
    byte[] tabByte2 = null;
    int highByte = 0;
    int lowByte = 1;
    
    UlawCodecStream(AudioInputStream paramAudioInputStream, AudioFormat paramAudioFormat)
    {
      super(paramAudioFormat, -1L);
      AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
      if (!UlawCodec.this.isConversionSupported(paramAudioFormat, localAudioFormat)) {
        throw new IllegalArgumentException("Unsupported conversion: " + localAudioFormat.toString() + " to " + paramAudioFormat.toString());
      }
      boolean bool;
      if (AudioFormat.Encoding.ULAW.equals(localAudioFormat.getEncoding()))
      {
        this.encode = false;
        this.encodeFormat = localAudioFormat;
        this.decodeFormat = paramAudioFormat;
        bool = paramAudioFormat.isBigEndian();
      }
      else
      {
        this.encode = true;
        this.encodeFormat = paramAudioFormat;
        this.decodeFormat = localAudioFormat;
        bool = localAudioFormat.isBigEndian();
        this.tempBuffer = new byte[64];
      }
      if (bool)
      {
        this.tabByte1 = UlawCodec.ULAW_TABH;
        this.tabByte2 = UlawCodec.ULAW_TABL;
        this.highByte = 0;
        this.lowByte = 1;
      }
      else
      {
        this.tabByte1 = UlawCodec.ULAW_TABL;
        this.tabByte2 = UlawCodec.ULAW_TABH;
        this.highByte = 1;
        this.lowByte = 0;
      }
      if ((paramAudioInputStream instanceof AudioInputStream)) {
        this.frameLength = paramAudioInputStream.getFrameLength();
      }
      this.framePos = 0L;
      this.frameSize = localAudioFormat.getFrameSize();
      if (this.frameSize == -1) {
        this.frameSize = 1;
      }
    }
    
    private short search(short paramShort1, short[] paramArrayOfShort, short paramShort2)
    {
      for (short s = 0; s < paramShort2; s = (short)(s + 1)) {
        if (paramShort1 <= paramArrayOfShort[s]) {
          return s;
        }
      }
      return paramShort2;
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      if (read(arrayOfByte, 0, arrayOfByte.length) == 1) {
        return arrayOfByte[1] & 0xFF;
      }
      return -1;
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      return read(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (paramInt2 % this.frameSize != 0) {
        paramInt2 -= paramInt2 % this.frameSize;
      }
      if (this.encode)
      {
        i = 132;
        int i2 = 0;
        int i3 = paramInt1;
        int i4 = paramInt2 * 2;
        for (int i5 = i4 > 64 ? 64 : i4; (i2 = super.read(this.tempBuffer, 0, i5)) > 0; i5 = i4 > 64 ? 64 : i4)
        {
          for (m = 0; m < i2; m += 2)
          {
            int n = (short)(this.tempBuffer[(m + this.highByte)] << 8 & 0xFF00);
            n = (short)(n | (short)((short)this.tempBuffer[(m + this.lowByte)] & 0xFF));
            if (n < 0)
            {
              n = (short)(i - n);
              j = 127;
            }
            else
            {
              n = (short)(n + i);
              j = 255;
            }
            k = search(n, UlawCodec.seg_end, (short)8);
            int i1;
            if (k >= 8)
            {
              i1 = (byte)(0x7F ^ j);
            }
            else
            {
              i1 = (byte)(k << 4 | n >> k + 3 & 0xF);
              i1 = (byte)(i1 ^ j);
            }
            paramArrayOfByte[i3] = i1;
            i3++;
          }
          i4 -= i2;
        }
        if ((i3 == paramInt1) && (i2 < 0)) {
          return i2;
        }
        return i3 - paramInt1;
      }
      int j = paramInt2 / 2;
      int k = paramInt1 + paramInt2 / 2;
      int m = super.read(paramArrayOfByte, k, j);
      if (m < 0) {
        return m;
      }
      for (int i = paramInt1; i < paramInt1 + m * 2; i += 2)
      {
        paramArrayOfByte[i] = this.tabByte1[(paramArrayOfByte[k] & 0xFF)];
        paramArrayOfByte[(i + 1)] = this.tabByte2[(paramArrayOfByte[k] & 0xFF)];
        k++;
      }
      return i - paramInt1;
    }
  }
}
