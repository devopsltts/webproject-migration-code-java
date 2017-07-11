package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

abstract class AbstractMidiDevice
  implements MidiDevice, ReferenceCountingDevice
{
  private static final boolean TRACE_TRANSMITTER = false;
  private ArrayList<Receiver> receiverList;
  private TransmitterList transmitterList;
  private final Object traRecLock = new Object();
  private final MidiDevice.Info info;
  private boolean open = false;
  private int openRefCount;
  private List openKeepingObjects;
  protected long id = 0L;
  
  protected AbstractMidiDevice(MidiDevice.Info paramInfo)
  {
    this.info = paramInfo;
    this.openRefCount = 0;
  }
  
  public final MidiDevice.Info getDeviceInfo()
  {
    return this.info;
  }
  
  public final void open()
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      this.openRefCount = -1;
      doOpen();
    }
  }
  
  private void openInternal(Object paramObject)
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      if (this.openRefCount != -1)
      {
        this.openRefCount += 1;
        getOpenKeepingObjects().add(paramObject);
      }
      doOpen();
    }
  }
  
  private void doOpen()
    throws MidiUnavailableException
  {
    synchronized (this)
    {
      if (!isOpen())
      {
        implOpen();
        this.open = true;
      }
    }
  }
  
  public final void close()
  {
    synchronized (this)
    {
      doClose();
      this.openRefCount = 0;
    }
  }
  
  public final void closeInternal(Object paramObject)
  {
    synchronized (this)
    {
      if ((getOpenKeepingObjects().remove(paramObject)) && (this.openRefCount > 0))
      {
        this.openRefCount -= 1;
        if (this.openRefCount == 0) {
          doClose();
        }
      }
    }
  }
  
  public final void doClose()
  {
    synchronized (this)
    {
      if (isOpen())
      {
        implClose();
        this.open = false;
      }
    }
  }
  
  public final boolean isOpen()
  {
    return this.open;
  }
  
  protected void implClose()
  {
    synchronized (this.traRecLock)
    {
      if (this.receiverList != null)
      {
        for (int i = 0; i < this.receiverList.size(); i++) {
          ((Receiver)this.receiverList.get(i)).close();
        }
        this.receiverList.clear();
      }
      if (this.transmitterList != null) {
        this.transmitterList.close();
      }
    }
  }
  
  public long getMicrosecondPosition()
  {
    return -1L;
  }
  
  public final int getMaxReceivers()
  {
    if (hasReceivers()) {
      return -1;
    }
    return 0;
  }
  
  public final int getMaxTransmitters()
  {
    if (hasTransmitters()) {
      return -1;
    }
    return 0;
  }
  
  public final Receiver getReceiver()
    throws MidiUnavailableException
  {
    Receiver localReceiver;
    synchronized (this.traRecLock)
    {
      localReceiver = createReceiver();
      getReceiverList().add(localReceiver);
    }
    return localReceiver;
  }
  
  public final List<Receiver> getReceivers()
  {
    List localList;
    synchronized (this.traRecLock)
    {
      if (this.receiverList == null) {
        localList = Collections.unmodifiableList(new ArrayList(0));
      } else {
        localList = Collections.unmodifiableList((List)this.receiverList.clone());
      }
    }
    return localList;
  }
  
  public final Transmitter getTransmitter()
    throws MidiUnavailableException
  {
    Transmitter localTransmitter;
    synchronized (this.traRecLock)
    {
      localTransmitter = createTransmitter();
      getTransmitterList().add(localTransmitter);
    }
    return localTransmitter;
  }
  
  public final List<Transmitter> getTransmitters()
  {
    List localList;
    synchronized (this.traRecLock)
    {
      if ((this.transmitterList == null) || (this.transmitterList.transmitters.size() == 0)) {
        localList = Collections.unmodifiableList(new ArrayList(0));
      } else {
        localList = Collections.unmodifiableList((List)this.transmitterList.transmitters.clone());
      }
    }
    return localList;
  }
  
  final long getId()
  {
    return this.id;
  }
  
  public final Receiver getReceiverReferenceCounting()
    throws MidiUnavailableException
  {
    Receiver localReceiver;
    synchronized (this.traRecLock)
    {
      localReceiver = getReceiver();
      openInternal(localReceiver);
    }
    return localReceiver;
  }
  
  public final Transmitter getTransmitterReferenceCounting()
    throws MidiUnavailableException
  {
    Transmitter localTransmitter;
    synchronized (this.traRecLock)
    {
      localTransmitter = getTransmitter();
      openInternal(localTransmitter);
    }
    return localTransmitter;
  }
  
  private synchronized List getOpenKeepingObjects()
  {
    if (this.openKeepingObjects == null) {
      this.openKeepingObjects = new ArrayList();
    }
    return this.openKeepingObjects;
  }
  
  private List<Receiver> getReceiverList()
  {
    synchronized (this.traRecLock)
    {
      if (this.receiverList == null) {
        this.receiverList = new ArrayList();
      }
    }
    return this.receiverList;
  }
  
  protected boolean hasReceivers()
  {
    return false;
  }
  
  protected Receiver createReceiver()
    throws MidiUnavailableException
  {
    throw new MidiUnavailableException("MIDI IN receiver not available");
  }
  
  final TransmitterList getTransmitterList()
  {
    synchronized (this.traRecLock)
    {
      if (this.transmitterList == null) {
        this.transmitterList = new TransmitterList();
      }
    }
    return this.transmitterList;
  }
  
  protected boolean hasTransmitters()
  {
    return false;
  }
  
  protected Transmitter createTransmitter()
    throws MidiUnavailableException
  {
    throw new MidiUnavailableException("MIDI OUT transmitter not available");
  }
  
  protected abstract void implOpen()
    throws MidiUnavailableException;
  
  protected final void finalize()
  {
    close();
  }
  
  abstract class AbstractReceiver
    implements MidiDeviceReceiver
  {
    private boolean open = true;
    
    AbstractReceiver() {}
    
    public final synchronized void send(MidiMessage paramMidiMessage, long paramLong)
    {
      if (!this.open) {
        throw new IllegalStateException("Receiver is not open");
      }
      implSend(paramMidiMessage, paramLong);
    }
    
    abstract void implSend(MidiMessage paramMidiMessage, long paramLong);
    
    public final void close()
    {
      this.open = false;
      synchronized (AbstractMidiDevice.this.traRecLock)
      {
        AbstractMidiDevice.this.getReceiverList().remove(this);
      }
      AbstractMidiDevice.this.closeInternal(this);
    }
    
    public final MidiDevice getMidiDevice()
    {
      return AbstractMidiDevice.this;
    }
    
    final boolean isOpen()
    {
      return this.open;
    }
  }
  
  class BasicTransmitter
    implements MidiDeviceTransmitter
  {
    private Receiver receiver = null;
    AbstractMidiDevice.TransmitterList tlist = null;
    
    protected BasicTransmitter() {}
    
    private void setTransmitterList(AbstractMidiDevice.TransmitterList paramTransmitterList)
    {
      this.tlist = paramTransmitterList;
    }
    
    public final void setReceiver(Receiver paramReceiver)
    {
      if ((this.tlist != null) && (this.receiver != paramReceiver))
      {
        AbstractMidiDevice.TransmitterList.access$400(this.tlist, this, this.receiver, paramReceiver);
        this.receiver = paramReceiver;
      }
    }
    
    public final Receiver getReceiver()
    {
      return this.receiver;
    }
    
    public final void close()
    {
      AbstractMidiDevice.this.closeInternal(this);
      if (this.tlist != null)
      {
        AbstractMidiDevice.TransmitterList.access$400(this.tlist, this, this.receiver, null);
        AbstractMidiDevice.TransmitterList.access$500(this.tlist, this);
        this.tlist = null;
      }
    }
    
    public final MidiDevice getMidiDevice()
    {
      return AbstractMidiDevice.this;
    }
  }
  
  final class TransmitterList
  {
    private final ArrayList<Transmitter> transmitters = new ArrayList();
    private MidiOutDevice.MidiOutReceiver midiOutReceiver;
    private int optimizedReceiverCount = 0;
    
    TransmitterList() {}
    
    private void add(Transmitter paramTransmitter)
    {
      synchronized (this.transmitters)
      {
        this.transmitters.add(paramTransmitter);
      }
      if ((paramTransmitter instanceof AbstractMidiDevice.BasicTransmitter)) {
        ((AbstractMidiDevice.BasicTransmitter)paramTransmitter).setTransmitterList(this);
      }
    }
    
    private void remove(Transmitter paramTransmitter)
    {
      synchronized (this.transmitters)
      {
        int i = this.transmitters.indexOf(paramTransmitter);
        if (i >= 0) {
          this.transmitters.remove(i);
        }
      }
    }
    
    private void receiverChanged(AbstractMidiDevice.BasicTransmitter paramBasicTransmitter, Receiver paramReceiver1, Receiver paramReceiver2)
    {
      synchronized (this.transmitters)
      {
        if (this.midiOutReceiver == paramReceiver1) {
          this.midiOutReceiver = null;
        }
        if ((paramReceiver2 != null) && ((paramReceiver2 instanceof MidiOutDevice.MidiOutReceiver)) && (this.midiOutReceiver == null)) {
          this.midiOutReceiver = ((MidiOutDevice.MidiOutReceiver)paramReceiver2);
        }
        this.optimizedReceiverCount = (this.midiOutReceiver != null ? 1 : 0);
      }
    }
    
    void close()
    {
      synchronized (this.transmitters)
      {
        for (int i = 0; i < this.transmitters.size(); i++) {
          ((Transmitter)this.transmitters.get(i)).close();
        }
        this.transmitters.clear();
      }
    }
    
    void sendMessage(int paramInt, long paramLong)
    {
      try
      {
        synchronized (this.transmitters)
        {
          int i = this.transmitters.size();
          if (this.optimizedReceiverCount == i)
          {
            if (this.midiOutReceiver != null) {
              this.midiOutReceiver.sendPackedMidiMessage(paramInt, paramLong);
            }
          }
          else {
            for (int j = 0; j < i; j++)
            {
              Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
              if (localReceiver != null) {
                if (this.optimizedReceiverCount > 0)
                {
                  if ((localReceiver instanceof MidiOutDevice.MidiOutReceiver)) {
                    ((MidiOutDevice.MidiOutReceiver)localReceiver).sendPackedMidiMessage(paramInt, paramLong);
                  } else {
                    localReceiver.send(new FastShortMessage(paramInt), paramLong);
                  }
                }
                else {
                  localReceiver.send(new FastShortMessage(paramInt), paramLong);
                }
              }
            }
          }
        }
      }
      catch (InvalidMidiDataException localInvalidMidiDataException) {}
    }
    
    void sendMessage(byte[] paramArrayOfByte, long paramLong)
    {
      try
      {
        synchronized (this.transmitters)
        {
          int i = this.transmitters.size();
          for (int j = 0; j < i; j++)
          {
            Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
            if (localReceiver != null) {
              localReceiver.send(new FastSysexMessage(paramArrayOfByte), paramLong);
            }
          }
        }
      }
      catch (InvalidMidiDataException localInvalidMidiDataException) {}
    }
    
    void sendMessage(MidiMessage paramMidiMessage, long paramLong)
    {
      if ((paramMidiMessage instanceof FastShortMessage))
      {
        sendMessage(((FastShortMessage)paramMidiMessage).getPackedMsg(), paramLong);
        return;
      }
      synchronized (this.transmitters)
      {
        int i = this.transmitters.size();
        if (this.optimizedReceiverCount == i)
        {
          if (this.midiOutReceiver != null) {
            this.midiOutReceiver.send(paramMidiMessage, paramLong);
          }
        }
        else {
          for (int j = 0; j < i; j++)
          {
            Receiver localReceiver = ((Transmitter)this.transmitters.get(j)).getReceiver();
            if (localReceiver != null) {
              localReceiver.send(paramMidiMessage, paramLong);
            }
          }
        }
      }
    }
  }
}
