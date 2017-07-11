package javax.sound.midi;

public abstract interface Receiver
  extends AutoCloseable
{
  public abstract void send(MidiMessage paramMidiMessage, long paramLong);
  
  public abstract void close();
}
