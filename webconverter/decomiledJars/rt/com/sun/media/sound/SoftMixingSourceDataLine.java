package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingSourceDataLine
  extends SoftMixingDataLine
  implements SourceDataLine
{
  private boolean open = false;
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  private int framesize;
  private int bufferSize = -1;
  private float[] readbuffer;
  private boolean active = false;
  private byte[] cycling_buffer;
  private int cycling_read_pos = 0;
  private int cycling_write_pos = 0;
  private int cycling_avail = 0;
  private long cycling_framepos = 0L;
  private AudioFloatInputStream afis;
  private boolean _active = false;
  private AudioFormat outputformat;
  private int out_nrofchannels;
  private int in_nrofchannels;
  private float _rightgain;
  private float _leftgain;
  private float _eff1gain;
  private float _eff2gain;
  
  SoftMixingSourceDataLine(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo)
  {
    super(paramSoftMixingMixer, paramInfo);
  }
  
  public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!isOpen()) {
      return 0;
    }
    if (paramInt2 % this.framesize != 0) {
      throw new IllegalArgumentException("Number of bytes does not represent an integral number of sample frames.");
    }
    if (paramInt1 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
      throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
    }
    byte[] arrayOfByte = this.cycling_buffer;
    int i = this.cycling_buffer.length;
    int j = 0;
    while (j != paramInt2)
    {
      int k;
      synchronized (this.cycling_buffer)
      {
        int m = this.cycling_write_pos;
        k = this.cycling_avail;
        while ((j != paramInt2) && (k != i))
        {
          arrayOfByte[(m++)] = paramArrayOfByte[(paramInt1++)];
          j++;
          k++;
          if (m == i) {
            m = 0;
          }
        }
        this.cycling_avail = k;
        this.cycling_write_pos = m;
        if (j == paramInt2) {
          return j;
        }
      }
      if (k == i)
      {
        try
        {
          Thread.sleep(1L);
        }
        catch (InterruptedException localInterruptedException)
        {
          return j;
        }
        if (!isRunning()) {
          return j;
        }
      }
    }
    return j;
  }
  
  protected void processControlLogic()
  {
    this._active = this.active;
    this._rightgain = this.rightgain;
    this._leftgain = this.leftgain;
    this._eff1gain = this.eff1gain;
    this._eff2gain = this.eff2gain;
  }
  
  protected void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer)
  {
    if (this._active)
    {
      float[] arrayOfFloat1 = paramArrayOfSoftAudioBuffer[0].array();
      float[] arrayOfFloat2 = paramArrayOfSoftAudioBuffer[1].array();
      int i = paramArrayOfSoftAudioBuffer[0].getSize();
      int j = i * this.in_nrofchannels;
      if ((this.readbuffer == null) || (this.readbuffer.length < j)) {
        this.readbuffer = new float[j];
      }
      int k = 0;
      try
      {
        k = this.afis.read(this.readbuffer);
        if (k != this.in_nrofchannels) {
          Arrays.fill(this.readbuffer, k, j, 0.0F);
        }
      }
      catch (IOException localIOException) {}
      int m = this.in_nrofchannels;
      int n = 0;
      int i1 = 0;
      while (n < i)
      {
        arrayOfFloat1[n] += this.readbuffer[i1] * this._leftgain;
        n++;
        i1 += m;
      }
      if (this.out_nrofchannels != 1) {
        if (this.in_nrofchannels == 1)
        {
          n = 0;
          i1 = 0;
          while (n < i)
          {
            arrayOfFloat2[n] += this.readbuffer[i1] * this._rightgain;
            n++;
            i1 += m;
          }
        }
        else
        {
          n = 0;
          i1 = 1;
          while (n < i)
          {
            arrayOfFloat2[n] += this.readbuffer[i1] * this._rightgain;
            n++;
            i1 += m;
          }
        }
      }
      float[] arrayOfFloat3;
      int i2;
      if (this._eff1gain > 1.0E-4D)
      {
        arrayOfFloat3 = paramArrayOfSoftAudioBuffer[2].array();
        i1 = 0;
        i2 = 0;
        while (i1 < i)
        {
          arrayOfFloat3[i1] += this.readbuffer[i2] * this._eff1gain;
          i1++;
          i2 += m;
        }
        if (this.in_nrofchannels == 2)
        {
          i1 = 0;
          i2 = 1;
          while (i1 < i)
          {
            arrayOfFloat3[i1] += this.readbuffer[i2] * this._eff1gain;
            i1++;
            i2 += m;
          }
        }
      }
      if (this._eff2gain > 1.0E-4D)
      {
        arrayOfFloat3 = paramArrayOfSoftAudioBuffer[3].array();
        i1 = 0;
        i2 = 0;
        while (i1 < i)
        {
          arrayOfFloat3[i1] += this.readbuffer[i2] * this._eff2gain;
          i1++;
          i2 += m;
        }
        if (this.in_nrofchannels == 2)
        {
          i1 = 0;
          i2 = 1;
          while (i1 < i)
          {
            arrayOfFloat3[i1] += this.readbuffer[i2] * this._eff2gain;
            i1++;
            i2 += m;
          }
        }
      }
    }
  }
  
  public void open()
    throws LineUnavailableException
  {
    open(this.format);
  }
  
  public void open(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    if (this.bufferSize == -1) {
      this.bufferSize = ((int)(paramAudioFormat.getFrameRate() / 2.0F) * paramAudioFormat.getFrameSize());
    }
    open(paramAudioFormat, this.bufferSize);
  }
  
  public void open(AudioFormat paramAudioFormat, int paramInt)
    throws LineUnavailableException
  {
    LineEvent localLineEvent = null;
    if (paramInt < paramAudioFormat.getFrameSize() * 32) {
      paramInt = paramAudioFormat.getFrameSize() * 32;
    }
    synchronized (this.control_mutex)
    {
      if (!isOpen())
      {
        if (!this.mixer.isOpen())
        {
          this.mixer.open();
          this.mixer.implicitOpen = true;
        }
        localLineEvent = new LineEvent(this, LineEvent.Type.OPEN, 0L);
        this.bufferSize = (paramInt - paramInt % paramAudioFormat.getFrameSize());
        this.format = paramAudioFormat;
        this.framesize = paramAudioFormat.getFrameSize();
        this.outputformat = this.mixer.getFormat();
        this.out_nrofchannels = this.outputformat.getChannels();
        this.in_nrofchannels = paramAudioFormat.getChannels();
        this.open = true;
        this.mixer.getMainMixer().openLine(this);
        this.cycling_buffer = new byte[this.framesize * paramInt];
        this.cycling_read_pos = 0;
        this.cycling_write_pos = 0;
        this.cycling_avail = 0;
        this.cycling_framepos = 0L;
        InputStream local1 = new InputStream()
        {
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
          
          public int available()
            throws IOException
          {
            synchronized (SoftMixingSourceDataLine.this.cycling_buffer)
            {
              return SoftMixingSourceDataLine.this.cycling_avail;
            }
          }
          
          public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            synchronized (SoftMixingSourceDataLine.this.cycling_buffer)
            {
              if (paramAnonymousInt2 > SoftMixingSourceDataLine.this.cycling_avail) {
                paramAnonymousInt2 = SoftMixingSourceDataLine.this.cycling_avail;
              }
              int i = SoftMixingSourceDataLine.this.cycling_read_pos;
              byte[] arrayOfByte = SoftMixingSourceDataLine.this.cycling_buffer;
              int j = arrayOfByte.length;
              for (int k = 0; k < paramAnonymousInt2; k++)
              {
                paramAnonymousArrayOfByte[(paramAnonymousInt1++)] = arrayOfByte[i];
                i++;
                if (i == j) {
                  i = 0;
                }
              }
              SoftMixingSourceDataLine.this.cycling_read_pos = i;
              SoftMixingSourceDataLine.this.cycling_avail = (SoftMixingSourceDataLine.this.cycling_avail - paramAnonymousInt2);
              SoftMixingSourceDataLine.this.cycling_framepos = (SoftMixingSourceDataLine.this.cycling_framepos + paramAnonymousInt2 / SoftMixingSourceDataLine.this.framesize);
            }
            return paramAnonymousInt2;
          }
        };
        this.afis = AudioFloatInputStream.getInputStream(new AudioInputStream(local1, paramAudioFormat, -1L));
        this.afis = new NonBlockingFloatInputStream(this.afis);
        if (Math.abs(paramAudioFormat.getSampleRate() - this.outputformat.getSampleRate()) > 1.0E-6D) {
          this.afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(this.afis, this.outputformat);
        }
      }
      else if (!paramAudioFormat.matches(getFormat()))
      {
        throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public int available()
  {
    synchronized (this.cycling_buffer)
    {
      return this.cycling_buffer.length - this.cycling_avail;
    }
  }
  
  public void drain()
  {
    for (;;)
    {
      int i;
      synchronized (this.cycling_buffer)
      {
        i = this.cycling_avail;
      }
      if (i != 0) {
        return;
      }
      try
      {
        Thread.sleep(1L);
      }
      catch (InterruptedException localInterruptedException)
      {
        return;
      }
    }
  }
  
  public void flush()
  {
    synchronized (this.cycling_buffer)
    {
      this.cycling_read_pos = 0;
      this.cycling_write_pos = 0;
      this.cycling_avail = 0;
    }
  }
  
  public int getBufferSize()
  {
    synchronized (this.control_mutex)
    {
      return this.bufferSize;
    }
  }
  
  public AudioFormat getFormat()
  {
    synchronized (this.control_mutex)
    {
      return this.format;
    }
  }
  
  public int getFramePosition()
  {
    return (int)getLongFramePosition();
  }
  
  public float getLevel()
  {
    return -1.0F;
  }
  
  public long getLongFramePosition()
  {
    synchronized (this.cycling_buffer)
    {
      return this.cycling_framepos;
    }
  }
  
  public long getMicrosecondPosition()
  {
    return (getLongFramePosition() * (1000000.0D / getFormat().getSampleRate()));
  }
  
  public boolean isActive()
  {
    synchronized (this.control_mutex)
    {
      return this.active;
    }
  }
  
  public boolean isRunning()
  {
    synchronized (this.control_mutex)
    {
      return this.active;
    }
  }
  
  public void start()
  {
    LineEvent localLineEvent = null;
    synchronized (this.control_mutex)
    {
      if (isOpen())
      {
        if (this.active) {
          return;
        }
        this.active = true;
        localLineEvent = new LineEvent(this, LineEvent.Type.START, getLongFramePosition());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public void stop()
  {
    LineEvent localLineEvent = null;
    synchronized (this.control_mutex)
    {
      if (isOpen())
      {
        if (!this.active) {
          return;
        }
        this.active = false;
        localLineEvent = new LineEvent(this, LineEvent.Type.STOP, getLongFramePosition());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public void close()
  {
    LineEvent localLineEvent = null;
    synchronized (this.control_mutex)
    {
      if (!isOpen()) {
        return;
      }
      stop();
      localLineEvent = new LineEvent(this, LineEvent.Type.CLOSE, getLongFramePosition());
      this.open = false;
      this.mixer.getMainMixer().closeLine(this);
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public boolean isOpen()
  {
    synchronized (this.control_mutex)
    {
      return this.open;
    }
  }
  
  private static class NonBlockingFloatInputStream
    extends AudioFloatInputStream
  {
    AudioFloatInputStream ais;
    
    NonBlockingFloatInputStream(AudioFloatInputStream paramAudioFloatInputStream)
    {
      this.ais = paramAudioFloatInputStream;
    }
    
    public int available()
      throws IOException
    {
      return this.ais.available();
    }
    
    public void close()
      throws IOException
    {
      this.ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return this.ais.getFormat();
    }
    
    public long getFrameLength()
    {
      return this.ais.getFrameLength();
    }
    
    public void mark(int paramInt)
    {
      this.ais.mark(paramInt);
    }
    
    public boolean markSupported()
    {
      return this.ais.markSupported();
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = available();
      if (paramInt2 > i)
      {
        int j = this.ais.read(paramArrayOfFloat, paramInt1, i);
        Arrays.fill(paramArrayOfFloat, paramInt1 + j, paramInt1 + paramInt2, 0.0F);
        return paramInt2;
      }
      return this.ais.read(paramArrayOfFloat, paramInt1, paramInt2);
    }
    
    public void reset()
      throws IOException
    {
      this.ais.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      return this.ais.skip(paramLong);
    }
  }
}
