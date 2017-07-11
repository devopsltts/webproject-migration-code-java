package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;

public final class AudioFloatFormatConverter
  extends FormatConversionProvider
{
  private final AudioFormat.Encoding[] formats = { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  
  public AudioFloatFormatConverter() {}
  
  public AudioInputStream getAudioInputStream(AudioFormat.Encoding paramEncoding, AudioInputStream paramAudioInputStream)
  {
    if (paramAudioInputStream.getFormat().getEncoding().equals(paramEncoding)) {
      return paramAudioInputStream;
    }
    AudioFormat localAudioFormat1 = paramAudioInputStream.getFormat();
    int i = localAudioFormat1.getChannels();
    AudioFormat.Encoding localEncoding = paramEncoding;
    float f = localAudioFormat1.getSampleRate();
    int j = localAudioFormat1.getSampleSizeInBits();
    boolean bool = localAudioFormat1.isBigEndian();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT)) {
      j = 32;
    }
    AudioFormat localAudioFormat2 = new AudioFormat(localEncoding, f, j, i, i * j / 8, f, bool);
    return getAudioInputStream(localAudioFormat2, paramAudioInputStream);
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream)
  {
    if (!isConversionSupported(paramAudioFormat, paramAudioInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
    }
    return getAudioInputStream(paramAudioFormat, AudioFloatInputStream.getInputStream(paramAudioInputStream));
  }
  
  public AudioInputStream getAudioInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
  {
    if (!isConversionSupported(paramAudioFormat, paramAudioFloatInputStream.getFormat())) {
      throw new IllegalArgumentException("Unsupported conversion: " + paramAudioFloatInputStream.getFormat().toString() + " to " + paramAudioFormat.toString());
    }
    if (paramAudioFormat.getChannels() != paramAudioFloatInputStream.getFormat().getChannels()) {
      paramAudioFloatInputStream = new AudioFloatInputStreamChannelMixer(paramAudioFloatInputStream, paramAudioFormat.getChannels());
    }
    if (Math.abs(paramAudioFormat.getSampleRate() - paramAudioFloatInputStream.getFormat().getSampleRate()) > 1.0E-6D) {
      paramAudioFloatInputStream = new AudioFloatInputStreamResampler(paramAudioFloatInputStream, paramAudioFormat);
    }
    return new AudioInputStream(new AudioFloatFormatConverterInputStream(paramAudioFormat, paramAudioFloatInputStream), paramAudioFormat, paramAudioFloatInputStream.getFrameLength());
  }
  
  public AudioFormat.Encoding[] getSourceEncodings()
  {
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat.Encoding[] getTargetEncodings()
  {
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat.Encoding[] getTargetEncodings(AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return new AudioFormat.Encoding[0];
    }
    return new AudioFormat.Encoding[] { AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT };
  }
  
  public AudioFormat[] getTargetFormats(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return new AudioFormat[0];
    }
    int i = paramAudioFormat.getChannels();
    ArrayList localArrayList = new ArrayList();
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, i, i, -1.0F, false));
    }
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, i, i, -1.0F, false));
    }
    for (int j = 16; j < 32; j += 8)
    {
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_SIGNED))
      {
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
      }
      if (paramEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))
      {
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
      }
    }
    if (paramEncoding.equals(AudioFormat.Encoding.PCM_FLOAT))
    {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, true));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, true));
    }
    return (AudioFormat[])localArrayList.toArray(new AudioFormat[localArrayList.size()]);
  }
  
  public boolean isConversionSupported(AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat2) == null) {
      return false;
    }
    if (AudioFloatConverter.getConverter(paramAudioFormat1) == null) {
      return false;
    }
    if (paramAudioFormat2.getChannels() <= 0) {
      return false;
    }
    return paramAudioFormat1.getChannels() > 0;
  }
  
  public boolean isConversionSupported(AudioFormat.Encoding paramEncoding, AudioFormat paramAudioFormat)
  {
    if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
      return false;
    }
    for (int i = 0; i < this.formats.length; i++) {
      if (paramEncoding.equals(this.formats[i])) {
        return true;
      }
    }
    return false;
  }
  
  private static class AudioFloatFormatConverterInputStream
    extends InputStream
  {
    private final AudioFloatConverter converter;
    private final AudioFloatInputStream stream;
    private float[] readfloatbuffer;
    private final int fsize;
    
    AudioFloatFormatConverterInputStream(AudioFormat paramAudioFormat, AudioFloatInputStream paramAudioFloatInputStream)
    {
      this.stream = paramAudioFloatInputStream;
      this.converter = AudioFloatConverter.getConverter(paramAudioFormat);
      this.fsize = ((paramAudioFormat.getSampleSizeInBits() + 7) / 8);
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte);
      if (i < 0) {
        return i;
      }
      return arrayOfByte[0] & 0xFF;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2 / this.fsize;
      if ((this.readfloatbuffer == null) || (this.readfloatbuffer.length < i)) {
        this.readfloatbuffer = new float[i];
      }
      int j = this.stream.read(this.readfloatbuffer, 0, i);
      if (j < 0) {
        return j;
      }
      this.converter.toByteArray(this.readfloatbuffer, 0, j, paramArrayOfByte, paramInt1);
      return j * this.fsize;
    }
    
    public int available()
      throws IOException
    {
      int i = this.stream.available();
      if (i < 0) {
        return i;
      }
      return i * this.fsize;
    }
    
    public void close()
      throws IOException
    {
      this.stream.close();
    }
    
    public synchronized void mark(int paramInt)
    {
      this.stream.mark(paramInt * this.fsize);
    }
    
    public boolean markSupported()
    {
      return this.stream.markSupported();
    }
    
    public synchronized void reset()
      throws IOException
    {
      this.stream.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l = this.stream.skip(paramLong / this.fsize);
      if (l < 0L) {
        return l;
      }
      return l * this.fsize;
    }
  }
  
  private static class AudioFloatInputStreamChannelMixer
    extends AudioFloatInputStream
  {
    private final int targetChannels;
    private final int sourceChannels;
    private final AudioFloatInputStream ais;
    private final AudioFormat targetFormat;
    private float[] conversion_buffer;
    
    AudioFloatInputStreamChannelMixer(AudioFloatInputStream paramAudioFloatInputStream, int paramInt)
    {
      this.sourceChannels = paramAudioFloatInputStream.getFormat().getChannels();
      this.targetChannels = paramInt;
      this.ais = paramAudioFloatInputStream;
      AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
      this.targetFormat = new AudioFormat(localAudioFormat.getEncoding(), localAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), paramInt, localAudioFormat.getFrameSize() / this.sourceChannels * paramInt, localAudioFormat.getFrameRate(), localAudioFormat.isBigEndian());
    }
    
    public int available()
      throws IOException
    {
      return this.ais.available() / this.sourceChannels * this.targetChannels;
    }
    
    public void close()
      throws IOException
    {
      this.ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return this.targetFormat;
    }
    
    public long getFrameLength()
    {
      return this.ais.getFrameLength();
    }
    
    public void mark(int paramInt)
    {
      this.ais.mark(paramInt / this.targetChannels * this.sourceChannels);
    }
    
    public boolean markSupported()
    {
      return this.ais.markSupported();
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = paramInt2 / this.targetChannels * this.sourceChannels;
      if ((this.conversion_buffer == null) || (this.conversion_buffer.length < i)) {
        this.conversion_buffer = new float[i];
      }
      int j = this.ais.read(this.conversion_buffer, 0, i);
      if (j < 0) {
        return j;
      }
      int k;
      int m;
      int i1;
      int i2;
      if (this.sourceChannels == 1)
      {
        k = this.targetChannels;
        for (m = 0; m < this.targetChannels; m++)
        {
          i1 = 0;
          i2 = paramInt1 + m;
          while (i1 < i)
          {
            paramArrayOfFloat[i2] = this.conversion_buffer[i1];
            i1++;
            i2 += k;
          }
        }
      }
      else if (this.targetChannels == 1)
      {
        k = this.sourceChannels;
        m = 0;
        for (i1 = paramInt1; m < i; i1++)
        {
          paramArrayOfFloat[i1] = this.conversion_buffer[m];
          m += k;
        }
        for (m = 1; m < this.sourceChannels; m++)
        {
          i1 = m;
          for (i2 = paramInt1; i1 < i; i2++)
          {
            paramArrayOfFloat[i2] += this.conversion_buffer[i1];
            i1 += k;
          }
        }
        float f = 1.0F / this.sourceChannels;
        i1 = 0;
        for (i2 = paramInt1; i1 < i; i2++)
        {
          paramArrayOfFloat[i2] *= f;
          i1 += k;
        }
      }
      else
      {
        k = Math.min(this.sourceChannels, this.targetChannels);
        int n = paramInt1 + paramInt2;
        i1 = this.targetChannels;
        i2 = this.sourceChannels;
        int i4;
        for (int i3 = 0; i3 < k; i3++)
        {
          i4 = paramInt1 + i3;
          int i5 = i3;
          while (i4 < n)
          {
            paramArrayOfFloat[i4] = this.conversion_buffer[i5];
            i4 += i1;
            i5 += i2;
          }
        }
        for (i3 = k; i3 < this.targetChannels; i3++)
        {
          i4 = paramInt1 + i3;
          while (i4 < n)
          {
            paramArrayOfFloat[i4] = 0.0F;
            i4 += i1;
          }
        }
      }
      return j / this.sourceChannels * this.targetChannels;
    }
    
    public void reset()
      throws IOException
    {
      this.ais.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      long l = this.ais.skip(paramLong / this.targetChannels * this.sourceChannels);
      if (l < 0L) {
        return l;
      }
      return l / this.sourceChannels * this.targetChannels;
    }
  }
  
  private static class AudioFloatInputStreamResampler
    extends AudioFloatInputStream
  {
    private final AudioFloatInputStream ais;
    private final AudioFormat targetFormat;
    private float[] skipbuffer;
    private SoftAbstractResampler resampler;
    private final float[] pitch = new float[1];
    private final float[] ibuffer2;
    private final float[][] ibuffer;
    private float ibuffer_index = 0.0F;
    private int ibuffer_len = 0;
    private final int nrofchannels;
    private float[][] cbuffer;
    private final int buffer_len = 512;
    private final int pad;
    private final int pad2;
    private final float[] ix = new float[1];
    private final int[] ox = new int[1];
    private float[][] mark_ibuffer = (float[][])null;
    private float mark_ibuffer_index = 0.0F;
    private int mark_ibuffer_len = 0;
    
    AudioFloatInputStreamResampler(AudioFloatInputStream paramAudioFloatInputStream, AudioFormat paramAudioFormat)
    {
      this.ais = paramAudioFloatInputStream;
      AudioFormat localAudioFormat = paramAudioFloatInputStream.getFormat();
      this.targetFormat = new AudioFormat(localAudioFormat.getEncoding(), paramAudioFormat.getSampleRate(), localAudioFormat.getSampleSizeInBits(), localAudioFormat.getChannels(), localAudioFormat.getFrameSize(), paramAudioFormat.getSampleRate(), localAudioFormat.isBigEndian());
      this.nrofchannels = this.targetFormat.getChannels();
      Object localObject = paramAudioFormat.getProperty("interpolation");
      if ((localObject != null) && ((localObject instanceof String)))
      {
        String str = (String)localObject;
        if (str.equalsIgnoreCase("point")) {
          this.resampler = new SoftPointResampler();
        }
        if (str.equalsIgnoreCase("linear")) {
          this.resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("linear1")) {
          this.resampler = new SoftLinearResampler();
        }
        if (str.equalsIgnoreCase("linear2")) {
          this.resampler = new SoftLinearResampler2();
        }
        if (str.equalsIgnoreCase("cubic")) {
          this.resampler = new SoftCubicResampler();
        }
        if (str.equalsIgnoreCase("lanczos")) {
          this.resampler = new SoftLanczosResampler();
        }
        if (str.equalsIgnoreCase("sinc")) {
          this.resampler = new SoftSincResampler();
        }
      }
      if (this.resampler == null) {
        this.resampler = new SoftLinearResampler2();
      }
      this.pitch[0] = (localAudioFormat.getSampleRate() / paramAudioFormat.getSampleRate());
      this.pad = this.resampler.getPadding();
      this.pad2 = (this.pad * 2);
      this.ibuffer = new float[this.nrofchannels][512 + this.pad2];
      this.ibuffer2 = new float[this.nrofchannels * 512];
      this.ibuffer_index = (512 + this.pad);
      this.ibuffer_len = 512;
    }
    
    public int available()
      throws IOException
    {
      return 0;
    }
    
    public void close()
      throws IOException
    {
      this.ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return this.targetFormat;
    }
    
    public long getFrameLength()
    {
      return -1L;
    }
    
    public void mark(int paramInt)
    {
      this.ais.mark((int)(paramInt * this.pitch[0]));
      this.mark_ibuffer_index = this.ibuffer_index;
      this.mark_ibuffer_len = this.ibuffer_len;
      if (this.mark_ibuffer == null) {
        this.mark_ibuffer = new float[this.ibuffer.length][this.ibuffer[0].length];
      }
      for (int i = 0; i < this.ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = this.ibuffer[i];
        float[] arrayOfFloat2 = this.mark_ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public boolean markSupported()
    {
      return this.ais.markSupported();
    }
    
    private void readNextBuffer()
      throws IOException
    {
      if (this.ibuffer_len == -1) {
        return;
      }
      int m;
      int n;
      for (int i = 0; i < this.nrofchannels; i++)
      {
        float[] arrayOfFloat1 = this.ibuffer[i];
        int k = this.ibuffer_len + this.pad2;
        m = this.ibuffer_len;
        for (n = 0; m < k; n++)
        {
          arrayOfFloat1[n] = arrayOfFloat1[m];
          m++;
        }
      }
      this.ibuffer_index -= this.ibuffer_len;
      this.ibuffer_len = this.ais.read(this.ibuffer2);
      if (this.ibuffer_len >= 0)
      {
        while (this.ibuffer_len < this.ibuffer2.length)
        {
          i = this.ais.read(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length - this.ibuffer_len);
          if (i == -1) {
            break;
          }
          this.ibuffer_len += i;
        }
        Arrays.fill(this.ibuffer2, this.ibuffer_len, this.ibuffer2.length, 0.0F);
        this.ibuffer_len /= this.nrofchannels;
      }
      else
      {
        Arrays.fill(this.ibuffer2, 0, this.ibuffer2.length, 0.0F);
      }
      i = this.ibuffer2.length;
      for (int j = 0; j < this.nrofchannels; j++)
      {
        float[] arrayOfFloat2 = this.ibuffer[j];
        m = j;
        for (n = this.pad2; m < i; n++)
        {
          arrayOfFloat2[n] = this.ibuffer2[m];
          m += this.nrofchannels;
        }
      }
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      if ((this.cbuffer == null) || (this.cbuffer[0].length < paramInt2 / this.nrofchannels)) {
        this.cbuffer = new float[this.nrofchannels][paramInt2 / this.nrofchannels];
      }
      if (this.ibuffer_len == -1) {
        return -1;
      }
      if (paramInt2 < 0) {
        return 0;
      }
      int i = paramInt1 + paramInt2;
      int j = paramInt2 / this.nrofchannels;
      int k = 0;
      int m = this.ibuffer_len;
      int i1;
      float[] arrayOfFloat;
      while (j > 0)
      {
        if (this.ibuffer_len >= 0)
        {
          if (this.ibuffer_index >= this.ibuffer_len + this.pad) {
            readNextBuffer();
          }
          m = this.ibuffer_len + this.pad;
        }
        if (this.ibuffer_len < 0)
        {
          m = this.pad2;
          if (this.ibuffer_index >= m) {
            break;
          }
        }
        if (this.ibuffer_index < 0.0F) {
          break;
        }
        n = k;
        for (i1 = 0; i1 < this.nrofchannels; i1++)
        {
          this.ix[0] = this.ibuffer_index;
          this.ox[0] = k;
          arrayOfFloat = this.ibuffer[i1];
          this.resampler.interpolate(arrayOfFloat, this.ix, m, this.pitch, 0.0F, this.cbuffer[i1], this.ox, paramInt2 / this.nrofchannels);
        }
        this.ibuffer_index = this.ix[0];
        k = this.ox[0];
        j -= k - n;
      }
      for (int n = 0; n < this.nrofchannels; n++)
      {
        i1 = 0;
        arrayOfFloat = this.cbuffer[n];
        int i2 = n + paramInt1;
        while (i2 < i)
        {
          paramArrayOfFloat[i2] = arrayOfFloat[(i1++)];
          i2 += this.nrofchannels;
        }
      }
      return paramInt2 - j * this.nrofchannels;
    }
    
    public void reset()
      throws IOException
    {
      this.ais.reset();
      if (this.mark_ibuffer == null) {
        return;
      }
      this.ibuffer_index = this.mark_ibuffer_index;
      this.ibuffer_len = this.mark_ibuffer_len;
      for (int i = 0; i < this.ibuffer.length; i++)
      {
        float[] arrayOfFloat1 = this.mark_ibuffer[i];
        float[] arrayOfFloat2 = this.ibuffer[i];
        for (int j = 0; j < arrayOfFloat2.length; j++) {
          arrayOfFloat2[j] = arrayOfFloat1[j];
        }
      }
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong < 0L) {
        return 0L;
      }
      if (this.skipbuffer == null) {
        this.skipbuffer = new float[1024 * this.targetFormat.getFrameSize()];
      }
      float[] arrayOfFloat = this.skipbuffer;
      int i;
      for (long l = paramLong; l > 0L; l -= i)
      {
        i = read(arrayOfFloat, 0, (int)Math.min(l, this.skipbuffer.length));
        if (i < 0)
        {
          if (l != paramLong) {
            break;
          }
          return i;
        }
      }
      return paramLong - l;
    }
  }
}
