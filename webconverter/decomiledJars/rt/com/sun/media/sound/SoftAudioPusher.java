package com.sun.media.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class SoftAudioPusher
  implements Runnable
{
  private volatile boolean active = false;
  private SourceDataLine sourceDataLine = null;
  private Thread audiothread;
  private final AudioInputStream ais;
  private final byte[] buffer;
  
  public SoftAudioPusher(SourceDataLine paramSourceDataLine, AudioInputStream paramAudioInputStream, int paramInt)
  {
    this.ais = paramAudioInputStream;
    this.buffer = new byte[paramInt];
    this.sourceDataLine = paramSourceDataLine;
  }
  
  public synchronized void start()
  {
    if (this.active) {
      return;
    }
    this.active = true;
    this.audiothread = new Thread(this);
    this.audiothread.setDaemon(true);
    this.audiothread.setPriority(10);
    this.audiothread.start();
  }
  
  public synchronized void stop()
  {
    if (!this.active) {
      return;
    }
    this.active = false;
    try
    {
      this.audiothread.join();
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void run()
  {
    byte[] arrayOfByte = this.buffer;
    AudioInputStream localAudioInputStream = this.ais;
    SourceDataLine localSourceDataLine = this.sourceDataLine;
    try
    {
      while (this.active)
      {
        int i = localAudioInputStream.read(arrayOfByte);
        if (i < 0) {
          break;
        }
        localSourceDataLine.write(arrayOfByte, 0, i);
      }
    }
    catch (IOException localIOException)
    {
      this.active = false;
    }
  }
}
