package com.sun.media.sound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

public final class PortMixerProvider
  extends MixerProvider
{
  private static PortMixerInfo[] infos;
  private static PortMixer[] devices;
  
  public PortMixerProvider()
  {
    synchronized (PortMixerProvider.class)
    {
      if (Platform.isPortsEnabled())
      {
        init();
      }
      else
      {
        infos = new PortMixerInfo[0];
        devices = new PortMixer[0];
      }
    }
  }
  
  private static void init()
  {
    int i = nGetNumDevices();
    if ((infos == null) || (infos.length != i))
    {
      infos = new PortMixerInfo[i];
      devices = new PortMixer[i];
      for (int j = 0; j < infos.length; j++) {
        infos[j] = nNewPortMixerInfo(j);
      }
    }
  }
  
  public Mixer.Info[] getMixerInfo()
  {
    synchronized (PortMixerProvider.class)
    {
      Mixer.Info[] arrayOfInfo = new Mixer.Info[infos.length];
      System.arraycopy(infos, 0, arrayOfInfo, 0, infos.length);
      return arrayOfInfo;
    }
  }
  
  public Mixer getMixer(Mixer.Info paramInfo)
  {
    synchronized (PortMixerProvider.class)
    {
      for (int i = 0; i < infos.length; i++) {
        if (infos[i].equals(paramInfo)) {
          return getDevice(infos[i]);
        }
      }
    }
    throw new IllegalArgumentException("Mixer " + paramInfo.toString() + " not supported by this provider.");
  }
  
  private static Mixer getDevice(PortMixerInfo paramPortMixerInfo)
  {
    int i = paramPortMixerInfo.getIndex();
    if (devices[i] == null) {
      devices[i] = new PortMixer(paramPortMixerInfo);
    }
    return devices[i];
  }
  
  private static native int nGetNumDevices();
  
  private static native PortMixerInfo nNewPortMixerInfo(int paramInt);
  
  static {}
  
  static final class PortMixerInfo
    extends Mixer.Info
  {
    private final int index;
    
    private PortMixerInfo(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
    {
      super(paramString2, paramString3, paramString4);
      this.index = paramInt;
    }
    
    int getIndex()
    {
      return this.index;
    }
  }
}
