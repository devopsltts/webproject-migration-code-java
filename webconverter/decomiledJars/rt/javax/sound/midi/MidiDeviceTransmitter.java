package javax.sound.midi;

public abstract interface MidiDeviceTransmitter
  extends Transmitter
{
  public abstract MidiDevice getMidiDevice();
}
