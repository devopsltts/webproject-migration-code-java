package com.sun.media.sound;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

public abstract class AbstractMidiDeviceProvider
  extends MidiDeviceProvider
{
  private static final boolean enabled = Platform.isMidiIOEnabled();
  
  public AbstractMidiDeviceProvider() {}
  
  final synchronized void readDeviceInfos()
  {
    Info[] arrayOfInfo1 = getInfoCache();
    MidiDevice[] arrayOfMidiDevice1 = getDeviceCache();
    if (!enabled)
    {
      if ((arrayOfInfo1 == null) || (arrayOfInfo1.length != 0)) {
        setInfoCache(new Info[0]);
      }
      if ((arrayOfMidiDevice1 == null) || (arrayOfMidiDevice1.length != 0)) {
        setDeviceCache(new MidiDevice[0]);
      }
      return;
    }
    int i = arrayOfInfo1 == null ? -1 : arrayOfInfo1.length;
    int j = getNumDevices();
    if (i != j)
    {
      Info[] arrayOfInfo2 = new Info[j];
      MidiDevice[] arrayOfMidiDevice2 = new MidiDevice[j];
      for (int k = 0; k < j; k++)
      {
        Info localInfo1 = createInfo(k);
        if (arrayOfInfo1 != null) {
          for (int m = 0; m < arrayOfInfo1.length; m++)
          {
            Info localInfo2 = arrayOfInfo1[m];
            if ((localInfo2 != null) && (localInfo2.equalStrings(localInfo1)))
            {
              arrayOfInfo2[k] = localInfo2;
              localInfo2.setIndex(k);
              arrayOfInfo1[m] = null;
              arrayOfMidiDevice2[k] = arrayOfMidiDevice1[m];
              arrayOfMidiDevice1[m] = null;
              break;
            }
          }
        }
        if (arrayOfInfo2[k] == null) {
          arrayOfInfo2[k] = localInfo1;
        }
      }
      if (arrayOfInfo1 != null) {
        for (k = 0; k < arrayOfInfo1.length; k++) {
          if (arrayOfInfo1[k] != null) {
            arrayOfInfo1[k].setIndex(-1);
          }
        }
      }
      setInfoCache(arrayOfInfo2);
      setDeviceCache(arrayOfMidiDevice2);
    }
  }
  
  public final MidiDevice.Info[] getDeviceInfo()
  {
    readDeviceInfos();
    Info[] arrayOfInfo = getInfoCache();
    MidiDevice.Info[] arrayOfInfo1 = new MidiDevice.Info[arrayOfInfo.length];
    System.arraycopy(arrayOfInfo, 0, arrayOfInfo1, 0, arrayOfInfo.length);
    return arrayOfInfo1;
  }
  
  public final MidiDevice getDevice(MidiDevice.Info paramInfo)
  {
    if ((paramInfo instanceof Info))
    {
      readDeviceInfos();
      MidiDevice[] arrayOfMidiDevice = getDeviceCache();
      Info[] arrayOfInfo = getInfoCache();
      Info localInfo = (Info)paramInfo;
      int i = localInfo.getIndex();
      if ((i >= 0) && (i < arrayOfMidiDevice.length) && (arrayOfInfo[i] == paramInfo))
      {
        if (arrayOfMidiDevice[i] == null) {
          arrayOfMidiDevice[i] = createDevice(localInfo);
        }
        if (arrayOfMidiDevice[i] != null) {
          return arrayOfMidiDevice[i];
        }
      }
    }
    throw new IllegalArgumentException("MidiDevice " + paramInfo.toString() + " not supported by this provider.");
  }
  
  abstract int getNumDevices();
  
  abstract MidiDevice[] getDeviceCache();
  
  abstract void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice);
  
  abstract Info[] getInfoCache();
  
  abstract void setInfoCache(Info[] paramArrayOfInfo);
  
  abstract Info createInfo(int paramInt);
  
  abstract MidiDevice createDevice(Info paramInfo);
  
  static {}
  
  static class Info
    extends MidiDevice.Info
  {
    private int index;
    
    Info(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt)
    {
      super(paramString2, paramString3, paramString4);
      this.index = paramInt;
    }
    
    final boolean equalStrings(Info paramInfo)
    {
      return (paramInfo != null) && (getName().equals(paramInfo.getName())) && (getVendor().equals(paramInfo.getVendor())) && (getDescription().equals(paramInfo.getDescription())) && (getVersion().equals(paramInfo.getVersion()));
    }
    
    final int getIndex()
    {
      return this.index;
    }
    
    final void setIndex(int paramInt)
    {
      this.index = paramInt;
    }
  }
}
