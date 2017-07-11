package com.sun.media.sound;

import javax.sound.midi.MidiDevice;

public final class MidiInDeviceProvider
  extends AbstractMidiDeviceProvider
{
  private static AbstractMidiDeviceProvider.Info[] infos = null;
  private static MidiDevice[] devices = null;
  private static final boolean enabled = Platform.isMidiIOEnabled();
  
  public MidiInDeviceProvider() {}
  
  AbstractMidiDeviceProvider.Info createInfo(int paramInt)
  {
    if (!enabled) {
      return null;
    }
    return new MidiInDeviceInfo(paramInt, MidiInDeviceProvider.class, null);
  }
  
  MidiDevice createDevice(AbstractMidiDeviceProvider.Info paramInfo)
  {
    if ((enabled) && ((paramInfo instanceof MidiInDeviceInfo))) {
      return new MidiInDevice(paramInfo);
    }
    return null;
  }
  
  int getNumDevices()
  {
    if (!enabled) {
      return 0;
    }
    int i = nGetNumDevices();
    return i;
  }
  
  MidiDevice[] getDeviceCache()
  {
    return devices;
  }
  
  void setDeviceCache(MidiDevice[] paramArrayOfMidiDevice)
  {
    devices = paramArrayOfMidiDevice;
  }
  
  AbstractMidiDeviceProvider.Info[] getInfoCache()
  {
    return infos;
  }
  
  void setInfoCache(AbstractMidiDeviceProvider.Info[] paramArrayOfInfo)
  {
    infos = paramArrayOfInfo;
  }
  
  private static native int nGetNumDevices();
  
  private static native String nGetName(int paramInt);
  
  private static native String nGetVendor(int paramInt);
  
  private static native String nGetDescription(int paramInt);
  
  private static native String nGetVersion(int paramInt);
  
  static
  {
    Platform.initialize();
  }
  
  static final class MidiInDeviceInfo
    extends AbstractMidiDeviceProvider.Info
  {
    private final Class providerClass;
    
    private MidiInDeviceInfo(int paramInt, Class paramClass)
    {
      super(MidiInDeviceProvider.nGetVendor(paramInt), MidiInDeviceProvider.nGetDescription(paramInt), MidiInDeviceProvider.nGetVersion(paramInt), paramInt);
      this.providerClass = paramClass;
    }
  }
}
