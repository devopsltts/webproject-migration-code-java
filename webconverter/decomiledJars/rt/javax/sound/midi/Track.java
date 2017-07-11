package javax.sound.midi;

import com.sun.media.sound.MidiUtils;
import java.util.ArrayList;
import java.util.HashSet;

public class Track
{
  private ArrayList eventsList = new ArrayList();
  private HashSet set = new HashSet();
  private MidiEvent eotEvent;
  
  Track()
  {
    ImmutableEndOfTrack localImmutableEndOfTrack = new ImmutableEndOfTrack(null);
    this.eotEvent = new MidiEvent(localImmutableEndOfTrack, 0L);
    this.eventsList.add(this.eotEvent);
    this.set.add(this.eotEvent);
  }
  
  public boolean add(MidiEvent paramMidiEvent)
  {
    if (paramMidiEvent == null) {
      return false;
    }
    synchronized (this.eventsList)
    {
      if (!this.set.contains(paramMidiEvent))
      {
        int i = this.eventsList.size();
        MidiEvent localMidiEvent = null;
        if (i > 0) {
          localMidiEvent = (MidiEvent)this.eventsList.get(i - 1);
        }
        if (localMidiEvent != this.eotEvent)
        {
          if (localMidiEvent != null) {
            this.eotEvent.setTick(localMidiEvent.getTick());
          } else {
            this.eotEvent.setTick(0L);
          }
          this.eventsList.add(this.eotEvent);
          this.set.add(this.eotEvent);
          i = this.eventsList.size();
        }
        if (MidiUtils.isMetaEndOfTrack(paramMidiEvent.getMessage()))
        {
          if (paramMidiEvent.getTick() > this.eotEvent.getTick()) {
            this.eotEvent.setTick(paramMidiEvent.getTick());
          }
          return true;
        }
        this.set.add(paramMidiEvent);
        for (int j = i; (j > 0) && (paramMidiEvent.getTick() < ((MidiEvent)this.eventsList.get(j - 1)).getTick()); j--) {}
        if (j == i)
        {
          this.eventsList.set(i - 1, paramMidiEvent);
          if (this.eotEvent.getTick() < paramMidiEvent.getTick()) {
            this.eotEvent.setTick(paramMidiEvent.getTick());
          }
          this.eventsList.add(this.eotEvent);
        }
        else
        {
          this.eventsList.add(j, paramMidiEvent);
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean remove(MidiEvent paramMidiEvent)
  {
    synchronized (this.eventsList)
    {
      if (this.set.remove(paramMidiEvent))
      {
        int i = this.eventsList.indexOf(paramMidiEvent);
        if (i >= 0)
        {
          this.eventsList.remove(i);
          return true;
        }
      }
    }
    return false;
  }
  
  /* Error */
  public MidiEvent get(int paramInt)
    throws java.lang.ArrayIndexOutOfBoundsException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   4: dup
    //   5: astore_2
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 83	javax/sound/midi/Track:eventsList	Ljava/util/ArrayList;
    //   11: iload_1
    //   12: invokevirtual 92	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   15: checkcast 45	javax/sound/midi/MidiEvent
    //   18: aload_2
    //   19: monitorexit
    //   20: areturn
    //   21: astore_3
    //   22: aload_2
    //   23: monitorexit
    //   24: aload_3
    //   25: athrow
    //   26: astore_2
    //   27: new 39	java/lang/ArrayIndexOutOfBoundsException
    //   30: dup
    //   31: aload_2
    //   32: invokevirtual 88	java/lang/IndexOutOfBoundsException:getMessage	()Ljava/lang/String;
    //   35: invokespecial 87	java/lang/ArrayIndexOutOfBoundsException:<init>	(Ljava/lang/String;)V
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	Track
    //   0	39	1	paramInt	int
    //   26	6	2	localIndexOutOfBoundsException	IndexOutOfBoundsException
    //   21	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	20	21	finally
    //   21	24	21	finally
    //   0	20	26	java/lang/IndexOutOfBoundsException
    //   21	26	26	java/lang/IndexOutOfBoundsException
  }
  
  public int size()
  {
    synchronized (this.eventsList)
    {
      return this.eventsList.size();
    }
  }
  
  public long ticks()
  {
    long l = 0L;
    synchronized (this.eventsList)
    {
      if (this.eventsList.size() > 0) {
        l = ((MidiEvent)this.eventsList.get(this.eventsList.size() - 1)).getTick();
      }
    }
    return l;
  }
  
  private static class ImmutableEndOfTrack
    extends MetaMessage
  {
    private ImmutableEndOfTrack()
    {
      super();
      this.data[0] = -1;
      this.data[1] = 47;
      this.data[2] = 0;
    }
    
    public void setMessage(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
      throws InvalidMidiDataException
    {
      throw new InvalidMidiDataException("cannot modify end of track message");
    }
  }
}
