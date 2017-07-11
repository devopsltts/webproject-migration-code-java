package javax.sound.midi.spi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;

public abstract class MidiDeviceProvider
{
  public MidiDeviceProvider() {}
  
  public boolean isDeviceSupported(MidiDevice.Info paramInfo)
  {
    MidiDevice.Info[] arrayOfInfo = getDeviceInfo();
    for (int i = 0; i < arrayOfInfo.length; i++) {
      if (paramInfo.equals(arrayOfInfo[i])) {
        return true;
      }
    }
    return false;
  }
  
  public abstract MidiDevice.Info[] getDeviceInfo();
  
  public abstract MidiDevice getDevice(MidiDevice.Info paramInfo);
}
