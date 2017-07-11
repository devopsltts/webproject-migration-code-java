package com.sun.media.sound;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class AiffFileWriter
  extends SunFileWriter
{
  private static final int DOUBLE_MANTISSA_LENGTH = 52;
  private static final int DOUBLE_EXPONENT_LENGTH = 11;
  private static final long DOUBLE_SIGN_MASK = Long.MIN_VALUE;
  private static final long DOUBLE_EXPONENT_MASK = 9218868437227405312L;
  private static final long DOUBLE_MANTISSA_MASK = 4503599627370495L;
  private static final int DOUBLE_EXPONENT_OFFSET = 1023;
  private static final int EXTENDED_EXPONENT_OFFSET = 16383;
  private static final int EXTENDED_MANTISSA_LENGTH = 63;
  private static final int EXTENDED_EXPONENT_LENGTH = 15;
  private static final long EXTENDED_INTEGER_MASK = Long.MIN_VALUE;
  
  public AiffFileWriter()
  {
    super(new AudioFileFormat.Type[] { AudioFileFormat.Type.AIFF });
  }
  
  public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream paramAudioInputStream)
  {
    AudioFileFormat.Type[] arrayOfType = new AudioFileFormat.Type[this.types.length];
    System.arraycopy(this.types, 0, arrayOfType, 0, this.types.length);
    AudioFormat localAudioFormat = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding = localAudioFormat.getEncoding();
    if ((AudioFormat.Encoding.ALAW.equals(localEncoding)) || (AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) || (AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding))) {
      return arrayOfType;
    }
    return new AudioFileFormat.Type[0];
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, OutputStream paramOutputStream)
    throws IOException
  {
    AiffFileFormat localAiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    if (paramAudioInputStream.getFrameLength() == -1L) {
      throw new IOException("stream length not specified");
    }
    int i = writeAiffFile(paramAudioInputStream, localAiffFileFormat, paramOutputStream);
    return i;
  }
  
  public int write(AudioInputStream paramAudioInputStream, AudioFileFormat.Type paramType, File paramFile)
    throws IOException
  {
    AiffFileFormat localAiffFileFormat = (AiffFileFormat)getAudioFileFormat(paramType, paramAudioInputStream);
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 4096);
    int i = writeAiffFile(paramAudioInputStream, localAiffFileFormat, localBufferedOutputStream);
    localBufferedOutputStream.close();
    if (localAiffFileFormat.getByteLength() == -1)
    {
      int j = localAiffFileFormat.getFormat().getChannels() * localAiffFileFormat.getFormat().getSampleSizeInBits();
      int k = i;
      int m = k - localAiffFileFormat.getHeaderSize() + 16;
      long l = m - 16;
      int n = (int)(l * 8L / j);
      RandomAccessFile localRandomAccessFile = new RandomAccessFile(paramFile, "rw");
      localRandomAccessFile.skipBytes(4);
      localRandomAccessFile.writeInt(k - 8);
      localRandomAccessFile.skipBytes(4 + localAiffFileFormat.getFverChunkSize() + 4 + 4 + 2);
      localRandomAccessFile.writeInt(n);
      localRandomAccessFile.skipBytes(16);
      localRandomAccessFile.writeInt(m - 8);
      localRandomAccessFile.close();
    }
    return i;
  }
  
  private AudioFileFormat getAudioFileFormat(AudioFileFormat.Type paramType, AudioInputStream paramAudioInputStream)
  {
    AudioFormat localAudioFormat1 = null;
    AiffFileFormat localAiffFileFormat = null;
    AudioFormat.Encoding localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
    AudioFormat localAudioFormat2 = paramAudioInputStream.getFormat();
    AudioFormat.Encoding localEncoding2 = localAudioFormat2.getEncoding();
    int k = 0;
    if (!this.types[0].equals(paramType)) {
      throw new IllegalArgumentException("File type " + paramType + " not supported.");
    }
    int i;
    if ((AudioFormat.Encoding.ALAW.equals(localEncoding2)) || (AudioFormat.Encoding.ULAW.equals(localEncoding2)))
    {
      if (localAudioFormat2.getSampleSizeInBits() == 8)
      {
        localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
        i = 16;
        k = 1;
      }
      else
      {
        throw new IllegalArgumentException("Encoding " + localEncoding2 + " supported only for 8-bit data.");
      }
    }
    else if (localAudioFormat2.getSampleSizeInBits() == 8)
    {
      localEncoding1 = AudioFormat.Encoding.PCM_UNSIGNED;
      i = 8;
    }
    else
    {
      localEncoding1 = AudioFormat.Encoding.PCM_SIGNED;
      i = localAudioFormat2.getSampleSizeInBits();
    }
    localAudioFormat1 = new AudioFormat(localEncoding1, localAudioFormat2.getSampleRate(), i, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true);
    int j;
    if (paramAudioInputStream.getFrameLength() != -1L)
    {
      if (k != 0) {
        j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() * 2 + 54;
      } else {
        j = (int)paramAudioInputStream.getFrameLength() * localAudioFormat2.getFrameSize() + 54;
      }
    }
    else {
      j = -1;
    }
    localAiffFileFormat = new AiffFileFormat(AudioFileFormat.Type.AIFF, j, localAudioFormat1, (int)paramAudioInputStream.getFrameLength());
    return localAiffFileFormat;
  }
  
  private int writeAiffFile(InputStream paramInputStream, AiffFileFormat paramAiffFileFormat, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 0;
    int j = 0;
    InputStream localInputStream = getFileStream(paramAiffFileFormat, paramInputStream);
    byte[] arrayOfByte = new byte['က'];
    int k = paramAiffFileFormat.getByteLength();
    while ((i = localInputStream.read(arrayOfByte)) >= 0) {
      if (k > 0)
      {
        if (i < k)
        {
          paramOutputStream.write(arrayOfByte, 0, i);
          j += i;
          k -= i;
        }
        else
        {
          paramOutputStream.write(arrayOfByte, 0, k);
          j += k;
          k = 0;
          break;
        }
      }
      else
      {
        paramOutputStream.write(arrayOfByte, 0, i);
        j += i;
      }
    }
    return j;
  }
  
  private InputStream getFileStream(AiffFileFormat paramAiffFileFormat, InputStream paramInputStream)
    throws IOException
  {
    AudioFormat localAudioFormat1 = paramAiffFileFormat.getFormat();
    AudioFormat localAudioFormat2 = null;
    AudioFormat.Encoding localEncoding = null;
    int i = paramAiffFileFormat.getHeaderSize();
    int j = paramAiffFileFormat.getFverChunkSize();
    int k = paramAiffFileFormat.getCommChunkSize();
    int m = -1;
    int n = -1;
    int i1 = paramAiffFileFormat.getSsndChunkOffset();
    int i2 = (short)localAudioFormat1.getChannels();
    int i3 = (short)localAudioFormat1.getSampleSizeInBits();
    int i4 = i2 * i3;
    int i5 = paramAiffFileFormat.getFrameLength();
    long l = -1L;
    if (i5 != -1)
    {
      l = i5 * i4 / 8L;
      n = (int)l + 16;
      m = (int)l + i;
    }
    float f = localAudioFormat1.getSampleRate();
    int i6 = 1313820229;
    byte[] arrayOfByte = null;
    ByteArrayInputStream localByteArrayInputStream = null;
    ByteArrayOutputStream localByteArrayOutputStream = null;
    DataOutputStream localDataOutputStream = null;
    SequenceInputStream localSequenceInputStream = null;
    Object localObject = paramInputStream;
    if ((paramInputStream instanceof AudioInputStream))
    {
      localAudioFormat2 = ((AudioInputStream)paramInputStream).getFormat();
      localEncoding = localAudioFormat2.getEncoding();
      if ((AudioFormat.Encoding.PCM_UNSIGNED.equals(localEncoding)) || ((AudioFormat.Encoding.PCM_SIGNED.equals(localEncoding)) && (!localAudioFormat2.isBigEndian())))
      {
        localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits(), localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize(), localAudioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
      }
      else if ((AudioFormat.Encoding.ULAW.equals(localEncoding)) || (AudioFormat.Encoding.ALAW.equals(localEncoding)))
      {
        if (localAudioFormat2.getSampleSizeInBits() != 8) {
          throw new IllegalArgumentException("unsupported encoding");
        }
        localObject = AudioSystem.getAudioInputStream(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, localAudioFormat2.getSampleRate(), localAudioFormat2.getSampleSizeInBits() * 2, localAudioFormat2.getChannels(), localAudioFormat2.getFrameSize() * 2, localAudioFormat2.getFrameRate(), true), (AudioInputStream)paramInputStream);
      }
    }
    localByteArrayOutputStream = new ByteArrayOutputStream();
    localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    localDataOutputStream.writeInt(1179603533);
    localDataOutputStream.writeInt(m - 8);
    localDataOutputStream.writeInt(1095321158);
    localDataOutputStream.writeInt(1129270605);
    localDataOutputStream.writeInt(k - 8);
    localDataOutputStream.writeShort(i2);
    localDataOutputStream.writeInt(i5);
    localDataOutputStream.writeShort(i3);
    write_ieee_extended(localDataOutputStream, f);
    localDataOutputStream.writeInt(1397968452);
    localDataOutputStream.writeInt(n - 8);
    localDataOutputStream.writeInt(0);
    localDataOutputStream.writeInt(0);
    localDataOutputStream.close();
    arrayOfByte = localByteArrayOutputStream.toByteArray();
    localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    localSequenceInputStream = new SequenceInputStream(localByteArrayInputStream, new SunFileWriter.NoCloseInputStream(this, (InputStream)localObject));
    return localSequenceInputStream;
  }
  
  private void write_ieee_extended(DataOutputStream paramDataOutputStream, float paramFloat)
    throws IOException
  {
    long l1 = Double.doubleToLongBits(paramFloat);
    long l2 = (l1 & 0x8000000000000000) >> 63;
    long l3 = (l1 & 0x7FF0000000000000) >> 52;
    long l4 = l1 & 0xFFFFFFFFFFFFF;
    long l5 = l3 - 1023L + 16383L;
    long l6 = l4 << 11;
    long l7 = l2 << 15;
    int i = (short)(int)(l7 | l5);
    long l8 = 0x8000000000000000 | l6;
    paramDataOutputStream.writeShort(i);
    paramDataOutputStream.writeLong(l8);
  }
}
