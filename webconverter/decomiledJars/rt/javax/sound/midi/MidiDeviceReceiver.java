package javax.sound.midi;

public abstract interface MidiDeviceReceiver
  extends Receiver
{
  public abstract MidiDevice getMidiDevice();
}
