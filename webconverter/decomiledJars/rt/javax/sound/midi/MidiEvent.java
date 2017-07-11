package javax.sound.midi;

public class MidiEvent
{
  private final MidiMessage message;
  private long tick;
  
  public MidiEvent(MidiMessage paramMidiMessage, long paramLong)
  {
    this.message = paramMidiMessage;
    this.tick = paramLong;
  }
  
  public MidiMessage getMessage()
  {
    return this.message;
  }
  
  public void setTick(long paramLong)
  {
    this.tick = paramLong;
  }
  
  public long getTick()
  {
    return this.tick;
  }
}
