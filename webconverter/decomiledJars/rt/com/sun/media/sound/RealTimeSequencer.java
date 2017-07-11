package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequencer.SyncMode;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

final class RealTimeSequencer
  extends AbstractMidiDevice
  implements Sequencer, AutoConnectSequencer
{
  private static final boolean DEBUG_PUMP = false;
  private static final boolean DEBUG_PUMP_ALL = false;
  private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
  static final RealTimeSequencerInfo info = new RealTimeSequencerInfo(null);
  private static final Sequencer.SyncMode[] masterSyncModes = { Sequencer.SyncMode.INTERNAL_CLOCK };
  private static final Sequencer.SyncMode[] slaveSyncModes = { Sequencer.SyncMode.NO_SYNC };
  private static final Sequencer.SyncMode masterSyncMode = Sequencer.SyncMode.INTERNAL_CLOCK;
  private static final Sequencer.SyncMode slaveSyncMode = Sequencer.SyncMode.NO_SYNC;
  private Sequence sequence = null;
  private double cacheTempoMPQ = -1.0D;
  private float cacheTempoFactor = -1.0F;
  private boolean[] trackMuted = null;
  private boolean[] trackSolo = null;
  private final MidiUtils.TempoCache tempoCache = new MidiUtils.TempoCache();
  private boolean running = false;
  private PlayThread playThread;
  private boolean recording = false;
  private final List recordingTracks = new ArrayList();
  private long loopStart = 0L;
  private long loopEnd = -1L;
  private int loopCount = 0;
  private final ArrayList metaEventListeners = new ArrayList();
  private final ArrayList controllerEventListeners = new ArrayList();
  private boolean autoConnect = false;
  private boolean doAutoConnectAtNextOpen = false;
  Receiver autoConnectedReceiver = null;
  
  RealTimeSequencer()
    throws MidiUnavailableException
  {
    super(info);
  }
  
  public synchronized void setSequence(Sequence paramSequence)
    throws InvalidMidiDataException
  {
    if (paramSequence != this.sequence)
    {
      if ((this.sequence != null) && (paramSequence == null))
      {
        setCaches();
        stop();
        this.trackMuted = null;
        this.trackSolo = null;
        this.loopStart = 0L;
        this.loopEnd = -1L;
        this.loopCount = 0;
        if (getDataPump() != null)
        {
          getDataPump().setTickPos(0L);
          getDataPump().resetLoopCount();
        }
      }
      if (this.playThread != null) {
        this.playThread.setSequence(paramSequence);
      }
      this.sequence = paramSequence;
      if (paramSequence != null)
      {
        this.tempoCache.refresh(paramSequence);
        setTickPosition(0L);
        propagateCaches();
      }
    }
    else if (paramSequence != null)
    {
      this.tempoCache.refresh(paramSequence);
      if (this.playThread != null) {
        this.playThread.setSequence(paramSequence);
      }
    }
  }
  
  public synchronized void setSequence(InputStream paramInputStream)
    throws IOException, InvalidMidiDataException
  {
    if (paramInputStream == null)
    {
      setSequence((Sequence)null);
      return;
    }
    Sequence localSequence = MidiSystem.getSequence(paramInputStream);
    setSequence(localSequence);
  }
  
  public Sequence getSequence()
  {
    return this.sequence;
  }
  
  public synchronized void start()
  {
    if (!isOpen()) {
      throw new IllegalStateException("sequencer not open");
    }
    if (this.sequence == null) {
      throw new IllegalStateException("sequence not set");
    }
    if (this.running == true) {
      return;
    }
    implStart();
  }
  
  public synchronized void stop()
  {
    if (!isOpen()) {
      throw new IllegalStateException("sequencer not open");
    }
    stopRecording();
    if (!this.running) {
      return;
    }
    implStop();
  }
  
  public boolean isRunning()
  {
    return this.running;
  }
  
  public void startRecording()
  {
    if (!isOpen()) {
      throw new IllegalStateException("Sequencer not open");
    }
    start();
    this.recording = true;
  }
  
  public void stopRecording()
  {
    if (!isOpen()) {
      throw new IllegalStateException("Sequencer not open");
    }
    this.recording = false;
  }
  
  public boolean isRecording()
  {
    return this.recording;
  }
  
  public void recordEnable(Track paramTrack, int paramInt)
  {
    if (!findTrack(paramTrack)) {
      throw new IllegalArgumentException("Track does not exist in the current sequence");
    }
    synchronized (this.recordingTracks)
    {
      RecordingTrack localRecordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
      if (localRecordingTrack != null) {
        localRecordingTrack.channel = paramInt;
      } else {
        this.recordingTracks.add(new RecordingTrack(paramTrack, paramInt));
      }
    }
  }
  
  public void recordDisable(Track paramTrack)
  {
    synchronized (this.recordingTracks)
    {
      RecordingTrack localRecordingTrack = RecordingTrack.get(this.recordingTracks, paramTrack);
      if (localRecordingTrack != null) {
        this.recordingTracks.remove(localRecordingTrack);
      }
    }
  }
  
  private boolean findTrack(Track paramTrack)
  {
    boolean bool = false;
    if (this.sequence != null)
    {
      Track[] arrayOfTrack = this.sequence.getTracks();
      for (int i = 0; i < arrayOfTrack.length; i++) {
        if (paramTrack == arrayOfTrack[i])
        {
          bool = true;
          break;
        }
      }
    }
    return bool;
  }
  
  public float getTempoInBPM()
  {
    return (float)MidiUtils.convertTempo(getTempoInMPQ());
  }
  
  public void setTempoInBPM(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      paramFloat = 1.0F;
    }
    setTempoInMPQ((float)MidiUtils.convertTempo(paramFloat));
  }
  
  public float getTempoInMPQ()
  {
    if (needCaching())
    {
      if (this.cacheTempoMPQ != -1.0D) {
        return (float)this.cacheTempoMPQ;
      }
      if (this.sequence != null) {
        return this.tempoCache.getTempoMPQAt(getTickPosition());
      }
      return 500000.0F;
    }
    return getDataPump().getTempoMPQ();
  }
  
  public void setTempoInMPQ(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      paramFloat = 1.0F;
    }
    if (needCaching())
    {
      this.cacheTempoMPQ = paramFloat;
    }
    else
    {
      getDataPump().setTempoMPQ(paramFloat);
      this.cacheTempoMPQ = -1.0D;
    }
  }
  
  public void setTempoFactor(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      return;
    }
    if (needCaching())
    {
      this.cacheTempoFactor = paramFloat;
    }
    else
    {
      getDataPump().setTempoFactor(paramFloat);
      this.cacheTempoFactor = -1.0F;
    }
  }
  
  public float getTempoFactor()
  {
    if (needCaching())
    {
      if (this.cacheTempoFactor != -1.0F) {
        return this.cacheTempoFactor;
      }
      return 1.0F;
    }
    return getDataPump().getTempoFactor();
  }
  
  public long getTickLength()
  {
    if (this.sequence == null) {
      return 0L;
    }
    return this.sequence.getTickLength();
  }
  
  public synchronized long getTickPosition()
  {
    if ((getDataPump() == null) || (this.sequence == null)) {
      return 0L;
    }
    return getDataPump().getTickPos();
  }
  
  public synchronized void setTickPosition(long paramLong)
  {
    if (paramLong < 0L) {
      return;
    }
    if (getDataPump() == null)
    {
      if (paramLong == 0L) {}
    }
    else if (this.sequence == null)
    {
      if (paramLong == 0L) {}
    }
    else {
      getDataPump().setTickPos(paramLong);
    }
  }
  
  public long getMicrosecondLength()
  {
    if (this.sequence == null) {
      return 0L;
    }
    return this.sequence.getMicrosecondLength();
  }
  
  public long getMicrosecondPosition()
  {
    if ((getDataPump() == null) || (this.sequence == null)) {
      return 0L;
    }
    synchronized (this.tempoCache)
    {
      return MidiUtils.tick2microsecond(this.sequence, getDataPump().getTickPos(), this.tempoCache);
    }
  }
  
  public void setMicrosecondPosition(long paramLong)
  {
    if (paramLong < 0L) {
      return;
    }
    if (getDataPump() == null)
    {
      if (paramLong == 0L) {}
    }
    else if (this.sequence == null)
    {
      if (paramLong == 0L) {}
    }
    else {
      synchronized (this.tempoCache)
      {
        setTickPosition(MidiUtils.microsecond2tick(this.sequence, paramLong, this.tempoCache));
      }
    }
  }
  
  public void setMasterSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getMasterSyncMode()
  {
    return masterSyncMode;
  }
  
  public Sequencer.SyncMode[] getMasterSyncModes()
  {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[masterSyncModes.length];
    System.arraycopy(masterSyncModes, 0, arrayOfSyncMode, 0, masterSyncModes.length);
    return arrayOfSyncMode;
  }
  
  public void setSlaveSyncMode(Sequencer.SyncMode paramSyncMode) {}
  
  public Sequencer.SyncMode getSlaveSyncMode()
  {
    return slaveSyncMode;
  }
  
  public Sequencer.SyncMode[] getSlaveSyncModes()
  {
    Sequencer.SyncMode[] arrayOfSyncMode = new Sequencer.SyncMode[slaveSyncModes.length];
    System.arraycopy(slaveSyncModes, 0, arrayOfSyncMode, 0, slaveSyncModes.length);
    return arrayOfSyncMode;
  }
  
  int getTrackCount()
  {
    Sequence localSequence = getSequence();
    if (localSequence != null) {
      return this.sequence.getTracks().length;
    }
    return 0;
  }
  
  public synchronized void setTrackMute(int paramInt, boolean paramBoolean)
  {
    int i = getTrackCount();
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return;
    }
    this.trackMuted = ensureBoolArraySize(this.trackMuted, i);
    this.trackMuted[paramInt] = paramBoolean;
    if (getDataPump() != null) {
      getDataPump().muteSoloChanged();
    }
  }
  
  public synchronized boolean getTrackMute(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return false;
    }
    if ((this.trackMuted == null) || (this.trackMuted.length <= paramInt)) {
      return false;
    }
    return this.trackMuted[paramInt];
  }
  
  public synchronized void setTrackSolo(int paramInt, boolean paramBoolean)
  {
    int i = getTrackCount();
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return;
    }
    this.trackSolo = ensureBoolArraySize(this.trackSolo, i);
    this.trackSolo[paramInt] = paramBoolean;
    if (getDataPump() != null) {
      getDataPump().muteSoloChanged();
    }
  }
  
  public synchronized boolean getTrackSolo(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getTrackCount())) {
      return false;
    }
    if ((this.trackSolo == null) || (this.trackSolo.length <= paramInt)) {
      return false;
    }
    return this.trackSolo[paramInt];
  }
  
  public boolean addMetaEventListener(MetaEventListener paramMetaEventListener)
  {
    synchronized (this.metaEventListeners)
    {
      if (!this.metaEventListeners.contains(paramMetaEventListener)) {
        this.metaEventListeners.add(paramMetaEventListener);
      }
      return true;
    }
  }
  
  public void removeMetaEventListener(MetaEventListener paramMetaEventListener)
  {
    synchronized (this.metaEventListeners)
    {
      int i = this.metaEventListeners.indexOf(paramMetaEventListener);
      if (i >= 0) {
        this.metaEventListeners.remove(i);
      }
    }
  }
  
  public int[] addControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
  {
    synchronized (this.controllerEventListeners)
    {
      ControllerListElement localControllerListElement = null;
      int i = 0;
      for (int j = 0; j < this.controllerEventListeners.size(); j++)
      {
        localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(j);
        if (localControllerListElement.listener.equals(paramControllerEventListener))
        {
          localControllerListElement.addControllers(paramArrayOfInt);
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        localControllerListElement = new ControllerListElement(paramControllerEventListener, paramArrayOfInt, null);
        this.controllerEventListeners.add(localControllerListElement);
      }
      return localControllerListElement.getControllers();
    }
  }
  
  public int[] removeControllerEventListener(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
  {
    synchronized (this.controllerEventListeners)
    {
      ControllerListElement localControllerListElement = null;
      int i = 0;
      for (int j = 0; j < this.controllerEventListeners.size(); j++)
      {
        localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(j);
        if (localControllerListElement.listener.equals(paramControllerEventListener))
        {
          localControllerListElement.removeControllers(paramArrayOfInt);
          i = 1;
          break;
        }
      }
      if (i == 0) {
        return new int[0];
      }
      if (paramArrayOfInt == null)
      {
        j = this.controllerEventListeners.indexOf(localControllerListElement);
        if (j >= 0) {
          this.controllerEventListeners.remove(j);
        }
        return new int[0];
      }
      return localControllerListElement.getControllers();
    }
  }
  
  public void setLoopStartPoint(long paramLong)
  {
    if ((paramLong > getTickLength()) || ((this.loopEnd != -1L) && (paramLong > this.loopEnd)) || (paramLong < 0L)) {
      throw new IllegalArgumentException("invalid loop start point: " + paramLong);
    }
    this.loopStart = paramLong;
  }
  
  public long getLoopStartPoint()
  {
    return this.loopStart;
  }
  
  public void setLoopEndPoint(long paramLong)
  {
    if ((paramLong > getTickLength()) || ((this.loopStart > paramLong) && (paramLong != -1L)) || (paramLong < -1L)) {
      throw new IllegalArgumentException("invalid loop end point: " + paramLong);
    }
    this.loopEnd = paramLong;
  }
  
  public long getLoopEndPoint()
  {
    return this.loopEnd;
  }
  
  public void setLoopCount(int paramInt)
  {
    if ((paramInt != -1) && (paramInt < 0)) {
      throw new IllegalArgumentException("illegal value for loop count: " + paramInt);
    }
    this.loopCount = paramInt;
    if (getDataPump() != null) {
      getDataPump().resetLoopCount();
    }
  }
  
  public int getLoopCount()
  {
    return this.loopCount;
  }
  
  protected void implOpen()
    throws MidiUnavailableException
  {
    this.playThread = new PlayThread();
    if (this.sequence != null) {
      this.playThread.setSequence(this.sequence);
    }
    propagateCaches();
    if (this.doAutoConnectAtNextOpen) {
      doAutoConnect();
    }
  }
  
  /* Error */
  private void doAutoConnect()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: invokestatic 568	javax/sound/midi/MidiSystem:getSynthesizer	()Ljavax/sound/midi/Synthesizer;
    //   5: astore_2
    //   6: aload_2
    //   7: instanceof 259
    //   10: ifeq +16 -> 26
    //   13: aload_2
    //   14: checkcast 259	com/sun/media/sound/ReferenceCountingDevice
    //   17: invokeinterface 574 1 0
    //   22: astore_1
    //   23: goto +42 -> 65
    //   26: aload_2
    //   27: invokeinterface 581 1 0
    //   32: aload_2
    //   33: invokeinterface 582 1 0
    //   38: astore_1
    //   39: aload_1
    //   40: ifnonnull +25 -> 65
    //   43: aload_2
    //   44: invokeinterface 580 1 0
    //   49: goto +16 -> 65
    //   52: astore_3
    //   53: aload_1
    //   54: ifnonnull +9 -> 63
    //   57: aload_2
    //   58: invokeinterface 580 1 0
    //   63: aload_3
    //   64: athrow
    //   65: goto +4 -> 69
    //   68: astore_2
    //   69: aload_1
    //   70: ifnonnull +11 -> 81
    //   73: invokestatic 567	javax/sound/midi/MidiSystem:getReceiver	()Ljavax/sound/midi/Receiver;
    //   76: astore_1
    //   77: goto +4 -> 81
    //   80: astore_2
    //   81: aload_1
    //   82: ifnull +22 -> 104
    //   85: aload_0
    //   86: aload_1
    //   87: putfield 476	com/sun/media/sound/RealTimeSequencer:autoConnectedReceiver	Ljavax/sound/midi/Receiver;
    //   90: aload_0
    //   91: invokevirtual 523	com/sun/media/sound/RealTimeSequencer:getTransmitter	()Ljavax/sound/midi/Transmitter;
    //   94: aload_1
    //   95: invokeinterface 583 2 0
    //   100: goto +4 -> 104
    //   103: astore_2
    //   104: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	RealTimeSequencer
    //   1	94	1	localReceiver	Receiver
    //   5	53	2	localSynthesizer	javax.sound.midi.Synthesizer
    //   68	1	2	localException1	Exception
    //   80	1	2	localException2	Exception
    //   103	1	2	localException3	Exception
    //   52	12	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   32	39	52	finally
    //   2	65	68	java/lang/Exception
    //   73	77	80	java/lang/Exception
    //   90	100	103	java/lang/Exception
  }
  
  private synchronized void propagateCaches()
  {
    if ((this.sequence != null) && (isOpen()))
    {
      if (this.cacheTempoFactor != -1.0F) {
        setTempoFactor(this.cacheTempoFactor);
      }
      if (this.cacheTempoMPQ == -1.0D) {
        setTempoInMPQ(new MidiUtils.TempoCache(this.sequence).getTempoMPQAt(getTickPosition()));
      } else {
        setTempoInMPQ((float)this.cacheTempoMPQ);
      }
    }
  }
  
  private synchronized void setCaches()
  {
    this.cacheTempoFactor = getTempoFactor();
    this.cacheTempoMPQ = getTempoInMPQ();
  }
  
  protected synchronized void implClose()
  {
    if (this.playThread != null)
    {
      this.playThread.close();
      this.playThread = null;
    }
    super.implClose();
    this.sequence = null;
    this.running = false;
    this.cacheTempoMPQ = -1.0D;
    this.cacheTempoFactor = -1.0F;
    this.trackMuted = null;
    this.trackSolo = null;
    this.loopStart = 0L;
    this.loopEnd = -1L;
    this.loopCount = 0;
    this.doAutoConnectAtNextOpen = this.autoConnect;
    if (this.autoConnectedReceiver != null)
    {
      try
      {
        this.autoConnectedReceiver.close();
      }
      catch (Exception localException) {}
      this.autoConnectedReceiver = null;
    }
  }
  
  void implStart()
  {
    if (this.playThread == null) {
      return;
    }
    this.tempoCache.refresh(this.sequence);
    if (!this.running)
    {
      this.running = true;
      this.playThread.start();
    }
  }
  
  void implStop()
  {
    if (this.playThread == null) {
      return;
    }
    this.recording = false;
    if (this.running)
    {
      this.running = false;
      this.playThread.stop();
    }
  }
  
  private static EventDispatcher getEventDispatcher()
  {
    ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
    synchronized (dispatchers)
    {
      EventDispatcher localEventDispatcher = (EventDispatcher)dispatchers.get(localThreadGroup);
      if (localEventDispatcher == null)
      {
        localEventDispatcher = new EventDispatcher();
        dispatchers.put(localThreadGroup, localEventDispatcher);
        localEventDispatcher.start();
      }
      return localEventDispatcher;
    }
  }
  
  void sendMetaEvents(MidiMessage paramMidiMessage)
  {
    if (this.metaEventListeners.size() == 0) {
      return;
    }
    getEventDispatcher().sendAudioEvents(paramMidiMessage, this.metaEventListeners);
  }
  
  void sendControllerEvents(MidiMessage paramMidiMessage)
  {
    int i = this.controllerEventListeners.size();
    if (i == 0) {
      return;
    }
    if (!(paramMidiMessage instanceof ShortMessage)) {
      return;
    }
    ShortMessage localShortMessage = (ShortMessage)paramMidiMessage;
    int j = localShortMessage.getData1();
    ArrayList localArrayList = new ArrayList();
    for (int k = 0; k < i; k++)
    {
      ControllerListElement localControllerListElement = (ControllerListElement)this.controllerEventListeners.get(k);
      for (int m = 0; m < localControllerListElement.controllers.length; m++) {
        if (localControllerListElement.controllers[m] == j)
        {
          localArrayList.add(localControllerListElement.listener);
          break;
        }
      }
    }
    getEventDispatcher().sendAudioEvents(paramMidiMessage, localArrayList);
  }
  
  private boolean needCaching()
  {
    return (!isOpen()) || (this.sequence == null) || (this.playThread == null);
  }
  
  private DataPump getDataPump()
  {
    if (this.playThread != null) {
      return this.playThread.getDataPump();
    }
    return null;
  }
  
  private MidiUtils.TempoCache getTempoCache()
  {
    return this.tempoCache;
  }
  
  private static boolean[] ensureBoolArraySize(boolean[] paramArrayOfBoolean, int paramInt)
  {
    if (paramArrayOfBoolean == null) {
      return new boolean[paramInt];
    }
    if (paramArrayOfBoolean.length < paramInt)
    {
      boolean[] arrayOfBoolean = new boolean[paramInt];
      System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, paramArrayOfBoolean.length);
      return arrayOfBoolean;
    }
    return paramArrayOfBoolean;
  }
  
  protected boolean hasReceivers()
  {
    return true;
  }
  
  protected Receiver createReceiver()
    throws MidiUnavailableException
  {
    return new SequencerReceiver();
  }
  
  protected boolean hasTransmitters()
  {
    return true;
  }
  
  protected Transmitter createTransmitter()
    throws MidiUnavailableException
  {
    return new SequencerTransmitter(null);
  }
  
  public void setAutoConnect(Receiver paramReceiver)
  {
    this.autoConnect = (paramReceiver != null);
    this.autoConnectedReceiver = paramReceiver;
  }
  
  private class ControllerListElement
  {
    int[] controllers;
    final ControllerEventListener listener;
    
    private ControllerListElement(ControllerEventListener paramControllerEventListener, int[] paramArrayOfInt)
    {
      this.listener = paramControllerEventListener;
      if (paramArrayOfInt == null)
      {
        paramArrayOfInt = new int[''];
        for (int i = 0; i < 128; i++) {
          paramArrayOfInt[i] = i;
        }
      }
      this.controllers = paramArrayOfInt;
    }
    
    private void addControllers(int[] paramArrayOfInt)
    {
      if (paramArrayOfInt == null)
      {
        this.controllers = new int[''];
        for (int i = 0; i < 128; i++) {
          this.controllers[i] = i;
        }
        return;
      }
      int[] arrayOfInt1 = new int[this.controllers.length + paramArrayOfInt.length];
      for (int k = 0; k < this.controllers.length; k++) {
        arrayOfInt1[k] = this.controllers[k];
      }
      int j = this.controllers.length;
      for (k = 0; k < paramArrayOfInt.length; k++)
      {
        m = 0;
        for (int n = 0; n < this.controllers.length; n++) {
          if (paramArrayOfInt[k] == this.controllers[n])
          {
            m = 1;
            break;
          }
        }
        if (m == 0) {
          arrayOfInt1[(j++)] = paramArrayOfInt[k];
        }
      }
      int[] arrayOfInt2 = new int[j];
      for (int m = 0; m < j; m++) {
        arrayOfInt2[m] = arrayOfInt1[m];
      }
      this.controllers = arrayOfInt2;
    }
    
    private void removeControllers(int[] paramArrayOfInt)
    {
      if (paramArrayOfInt == null)
      {
        this.controllers = new int[0];
      }
      else
      {
        int[] arrayOfInt1 = new int[this.controllers.length];
        int i = 0;
        for (int j = 0; j < this.controllers.length; j++)
        {
          k = 0;
          for (int m = 0; m < paramArrayOfInt.length; m++) {
            if (this.controllers[j] == paramArrayOfInt[m])
            {
              k = 1;
              break;
            }
          }
          if (k == 0) {
            arrayOfInt1[(i++)] = this.controllers[j];
          }
        }
        int[] arrayOfInt2 = new int[i];
        for (int k = 0; k < i; k++) {
          arrayOfInt2[k] = arrayOfInt1[k];
        }
        this.controllers = arrayOfInt2;
      }
    }
    
    private int[] getControllers()
    {
      if (this.controllers == null) {
        return null;
      }
      int[] arrayOfInt = new int[this.controllers.length];
      for (int i = 0; i < this.controllers.length; i++) {
        arrayOfInt[i] = this.controllers[i];
      }
      return arrayOfInt;
    }
  }
  
  private class DataPump
  {
    private float currTempo;
    private float tempoFactor;
    private float inverseTempoFactor;
    private long ignoreTempoEventAt;
    private int resolution;
    private float divisionType;
    private long checkPointMillis;
    private long checkPointTick;
    private int[] noteOnCache;
    private Track[] tracks;
    private boolean[] trackDisabled;
    private int[] trackReadPos;
    private long lastTick;
    private boolean needReindex = false;
    private int currLoopCounter = 0;
    
    DataPump()
    {
      init();
    }
    
    synchronized void init()
    {
      this.ignoreTempoEventAt = -1L;
      this.tempoFactor = 1.0F;
      this.inverseTempoFactor = 1.0F;
      this.noteOnCache = new int[''];
      this.tracks = null;
      this.trackDisabled = null;
    }
    
    synchronized void setTickPos(long paramLong)
    {
      long l = paramLong;
      this.lastTick = paramLong;
      if (RealTimeSequencer.this.running) {
        notesOff(false);
      }
      if ((RealTimeSequencer.this.running) || (paramLong > 0L)) {
        chaseEvents(l, paramLong);
      } else {
        this.needReindex = true;
      }
      if (!hasCachedTempo())
      {
        setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
        this.ignoreTempoEventAt = -1L;
      }
      this.checkPointMillis = 0L;
    }
    
    long getTickPos()
    {
      return this.lastTick;
    }
    
    boolean hasCachedTempo()
    {
      if (this.ignoreTempoEventAt != this.lastTick) {
        this.ignoreTempoEventAt = -1L;
      }
      return this.ignoreTempoEventAt >= 0L;
    }
    
    synchronized void setTempoMPQ(float paramFloat)
    {
      if ((paramFloat > 0.0F) && (paramFloat != this.currTempo))
      {
        this.ignoreTempoEventAt = this.lastTick;
        this.currTempo = paramFloat;
        this.checkPointMillis = 0L;
      }
    }
    
    float getTempoMPQ()
    {
      return this.currTempo;
    }
    
    synchronized void setTempoFactor(float paramFloat)
    {
      if ((paramFloat > 0.0F) && (paramFloat != this.tempoFactor))
      {
        this.tempoFactor = paramFloat;
        this.inverseTempoFactor = (1.0F / paramFloat);
        this.checkPointMillis = 0L;
      }
    }
    
    float getTempoFactor()
    {
      return this.tempoFactor;
    }
    
    synchronized void muteSoloChanged()
    {
      boolean[] arrayOfBoolean = makeDisabledArray();
      if (RealTimeSequencer.this.running) {
        applyDisabledTracks(this.trackDisabled, arrayOfBoolean);
      }
      this.trackDisabled = arrayOfBoolean;
    }
    
    synchronized void setSequence(Sequence paramSequence)
    {
      if (paramSequence == null)
      {
        init();
        return;
      }
      this.tracks = paramSequence.getTracks();
      muteSoloChanged();
      this.resolution = paramSequence.getResolution();
      this.divisionType = paramSequence.getDivisionType();
      this.trackReadPos = new int[this.tracks.length];
      this.checkPointMillis = 0L;
      this.needReindex = true;
    }
    
    synchronized void resetLoopCount()
    {
      this.currLoopCounter = RealTimeSequencer.this.loopCount;
    }
    
    void clearNoteOnCache()
    {
      for (int i = 0; i < 128; i++) {
        this.noteOnCache[i] = 0;
      }
    }
    
    void notesOff(boolean paramBoolean)
    {
      int i = 0;
      for (int j = 0; j < 16; j++)
      {
        int k = 1 << j;
        for (int m = 0; m < 128; m++) {
          if ((this.noteOnCache[m] & k) != 0)
          {
            this.noteOnCache[m] ^= k;
            RealTimeSequencer.this.getTransmitterList().sendMessage(0x90 | j | m << 8, -1L);
            i++;
          }
        }
        RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x7B00, -1L);
        RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x4000, -1L);
        if (paramBoolean)
        {
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | j | 0x7900, -1L);
          i++;
        }
      }
    }
    
    private boolean[] makeDisabledArray()
    {
      if (this.tracks == null) {
        return null;
      }
      boolean[] arrayOfBoolean1 = new boolean[this.tracks.length];
      boolean[] arrayOfBoolean3;
      boolean[] arrayOfBoolean2;
      synchronized (RealTimeSequencer.this)
      {
        arrayOfBoolean3 = RealTimeSequencer.this.trackMuted;
        arrayOfBoolean2 = RealTimeSequencer.this.trackSolo;
      }
      int i = 0;
      int j;
      if (arrayOfBoolean2 != null) {
        for (j = 0; j < arrayOfBoolean2.length; j++) {
          if (arrayOfBoolean2[j] != 0)
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0) {
        for (j = 0; j < arrayOfBoolean1.length; j++) {
          arrayOfBoolean1[j] = ((j >= arrayOfBoolean2.length) || (arrayOfBoolean2[j] == 0) ? 1 : false);
        }
      } else {
        for (j = 0; j < arrayOfBoolean1.length; j++) {
          arrayOfBoolean1[j] = ((arrayOfBoolean3 != null) && (j < arrayOfBoolean3.length) && (arrayOfBoolean3[j] != 0) ? 1 : false);
        }
      }
      return arrayOfBoolean1;
    }
    
    private void sendNoteOffIfOn(Track paramTrack, long paramLong)
    {
      int i = paramTrack.size();
      int j = 0;
      try
      {
        for (int k = 0; k < i; k++)
        {
          MidiEvent localMidiEvent = paramTrack.get(k);
          if (localMidiEvent.getTick() > paramLong) {
            break;
          }
          MidiMessage localMidiMessage = localMidiEvent.getMessage();
          int m = localMidiMessage.getStatus();
          int n = localMidiMessage.getLength();
          if ((n == 3) && ((m & 0xF0) == 144))
          {
            int i1 = -1;
            Object localObject;
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              if (((ShortMessage)localObject).getData2() > 0) {
                i1 = ((ShortMessage)localObject).getData1();
              }
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              if ((localObject[2] & 0x7F) > 0) {
                i1 = localObject[1] & 0x7F;
              }
            }
            if (i1 >= 0)
            {
              int i2 = 1 << (m & 0xF);
              if ((this.noteOnCache[i1] & i2) != 0)
              {
                RealTimeSequencer.this.getTransmitterList().sendMessage(m | i1 << 8, -1L);
                this.noteOnCache[i1] &= (0xFFFF ^ i2);
                j++;
              }
            }
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    }
    
    private void applyDisabledTracks(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
    {
      byte[][] arrayOfByte = (byte[][])null;
      synchronized (RealTimeSequencer.this)
      {
        for (int i = 0; i < paramArrayOfBoolean2.length; i++) {
          if (((paramArrayOfBoolean1 == null) || (i >= paramArrayOfBoolean1.length) || (paramArrayOfBoolean1[i] == 0)) && (paramArrayOfBoolean2[i] != 0))
          {
            if (this.tracks.length > i) {
              sendNoteOffIfOn(this.tracks[i], this.lastTick);
            }
          }
          else if ((paramArrayOfBoolean1 != null) && (i < paramArrayOfBoolean1.length) && (paramArrayOfBoolean1[i] != 0) && (paramArrayOfBoolean2[i] == 0))
          {
            if (arrayOfByte == null) {
              arrayOfByte = new byte[''][16];
            }
            chaseTrackEvents(i, 0L, this.lastTick, true, arrayOfByte);
          }
        }
      }
    }
    
    private void chaseTrackEvents(int paramInt, long paramLong1, long paramLong2, boolean paramBoolean, byte[][] paramArrayOfByte)
    {
      if (paramLong1 > paramLong2) {
        paramLong1 = 0L;
      }
      byte[] arrayOfByte = new byte[16];
      for (int i = 0; i < 16; i++)
      {
        arrayOfByte[i] = -1;
        for (j = 0; j < 128; j++) {
          paramArrayOfByte[j][i] = -1;
        }
      }
      Track localTrack = this.tracks[paramInt];
      int j = localTrack.size();
      int i2;
      int i3;
      try
      {
        for (int k = 0; k < j; k++)
        {
          MidiEvent localMidiEvent = localTrack.get(k);
          if (localMidiEvent.getTick() >= paramLong2)
          {
            if ((!paramBoolean) || (paramInt >= this.trackReadPos.length)) {
              break;
            }
            this.trackReadPos[paramInt] = (k > 0 ? k - 1 : 0);
            break;
          }
          MidiMessage localMidiMessage = localMidiEvent.getMessage();
          i2 = localMidiMessage.getStatus();
          i3 = localMidiMessage.getLength();
          Object localObject;
          if ((i3 == 3) && ((i2 & 0xF0) == 176)) {
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              paramArrayOfByte[(localObject.getData1() & 0x7F)][(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData2());
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              paramArrayOfByte[(localObject[1] & 0x7F)][(i2 & 0xF)] = localObject[2];
            }
          }
          if ((i3 == 2) && ((i2 & 0xF0) == 192)) {
            if ((localMidiMessage instanceof ShortMessage))
            {
              localObject = (ShortMessage)localMidiMessage;
              arrayOfByte[(i2 & 0xF)] = ((byte)((ShortMessage)localObject).getData1());
            }
            else
            {
              localObject = localMidiMessage.getMessage();
              arrayOfByte[(i2 & 0xF)] = localObject[1];
            }
          }
        }
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
      int m = 0;
      for (int n = 0; n < 16; n++)
      {
        for (int i1 = 0; i1 < 128; i1++)
        {
          i2 = paramArrayOfByte[i1][n];
          if (i2 >= 0)
          {
            i3 = 0xB0 | n | i1 << 8 | i2 << 16;
            RealTimeSequencer.this.getTransmitterList().sendMessage(i3, -1L);
            m++;
          }
        }
        if (arrayOfByte[n] >= 0) {
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xC0 | n | arrayOfByte[n] << 8, -1L);
        }
        if ((arrayOfByte[n] >= 0) || (paramLong1 == 0L) || (paramLong2 == 0L))
        {
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xE0 | n | 0x400000, -1L);
          RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | n | 0x4000, -1L);
        }
      }
    }
    
    synchronized void chaseEvents(long paramLong1, long paramLong2)
    {
      byte[][] arrayOfByte = new byte[''][16];
      for (int i = 0; i < this.tracks.length; i++) {
        if ((this.trackDisabled == null) || (this.trackDisabled.length <= i) || (this.trackDisabled[i] == 0)) {
          chaseTrackEvents(i, paramLong1, paramLong2, true, arrayOfByte);
        }
      }
    }
    
    private long getCurrentTimeMillis()
    {
      return System.nanoTime() / 1000000L;
    }
    
    private long millis2tick(long paramLong)
    {
      if (this.divisionType != 0.0F)
      {
        double d = paramLong * this.tempoFactor * this.divisionType * this.resolution / 1000.0D;
        return d;
      }
      return MidiUtils.microsec2ticks(paramLong * 1000L, this.currTempo * this.inverseTempoFactor, this.resolution);
    }
    
    private long tick2millis(long paramLong)
    {
      if (this.divisionType != 0.0F)
      {
        double d = paramLong * 1000.0D / (this.tempoFactor * this.divisionType * this.resolution);
        return d;
      }
      return MidiUtils.ticks2microsec(paramLong, this.currTempo * this.inverseTempoFactor, this.resolution) / 1000L;
    }
    
    private void ReindexTrack(int paramInt, long paramLong)
    {
      if ((paramInt < this.trackReadPos.length) && (paramInt < this.tracks.length)) {
        this.trackReadPos[paramInt] = MidiUtils.tick2index(this.tracks[paramInt], paramLong);
      }
    }
    
    private boolean dispatchMessage(int paramInt, MidiEvent paramMidiEvent)
    {
      boolean bool = false;
      MidiMessage localMidiMessage = paramMidiEvent.getMessage();
      int i = localMidiMessage.getStatus();
      int j = localMidiMessage.getLength();
      int k;
      if ((i == 255) && (j >= 2))
      {
        if (paramInt == 0)
        {
          k = MidiUtils.getTempoMPQ(localMidiMessage);
          if (k > 0)
          {
            if (paramMidiEvent.getTick() != this.ignoreTempoEventAt)
            {
              setTempoMPQ(k);
              bool = true;
            }
            this.ignoreTempoEventAt = -1L;
          }
        }
        RealTimeSequencer.this.sendMetaEvents(localMidiMessage);
      }
      else
      {
        RealTimeSequencer.this.getTransmitterList().sendMessage(localMidiMessage, -1L);
        switch (i & 0xF0)
        {
        case 128: 
          k = ((ShortMessage)localMidiMessage).getData1() & 0x7F;
          this.noteOnCache[k] &= (0xFFFF ^ 1 << (i & 0xF));
          break;
        case 144: 
          ShortMessage localShortMessage = (ShortMessage)localMidiMessage;
          int m = localShortMessage.getData1() & 0x7F;
          int n = localShortMessage.getData2() & 0x7F;
          if (n > 0) {
            this.noteOnCache[m] |= 1 << (i & 0xF);
          } else {
            this.noteOnCache[m] &= (0xFFFF ^ 1 << (i & 0xF));
          }
          break;
        case 176: 
          RealTimeSequencer.this.sendControllerEvents(localMidiMessage);
        }
      }
      return bool;
    }
    
    synchronized boolean pump()
    {
      long l2 = this.lastTick;
      boolean bool1 = false;
      int i = 0;
      boolean bool2 = false;
      long l1 = getCurrentTimeMillis();
      int j = 0;
      do
      {
        bool1 = false;
        if (this.needReindex)
        {
          if (this.trackReadPos.length < this.tracks.length) {
            this.trackReadPos = new int[this.tracks.length];
          }
          for (k = 0; k < this.tracks.length; k++) {
            ReindexTrack(k, l2);
          }
          this.needReindex = false;
          this.checkPointMillis = 0L;
        }
        if (this.checkPointMillis == 0L)
        {
          l1 = getCurrentTimeMillis();
          this.checkPointMillis = l1;
          l2 = this.lastTick;
          this.checkPointTick = l2;
        }
        else
        {
          l2 = this.checkPointTick + millis2tick(l1 - this.checkPointMillis);
          if ((RealTimeSequencer.this.loopEnd != -1L) && (((RealTimeSequencer.this.loopCount > 0) && (this.currLoopCounter > 0)) || ((RealTimeSequencer.this.loopCount == -1) && (this.lastTick <= RealTimeSequencer.this.loopEnd) && (l2 >= RealTimeSequencer.this.loopEnd))))
          {
            l2 = RealTimeSequencer.this.loopEnd - 1L;
            i = 1;
          }
          this.lastTick = l2;
        }
        j = 0;
        for (int k = 0; k < this.tracks.length; k++)
        {
          try
          {
            int m = this.trackDisabled[k];
            Track localTrack = this.tracks[k];
            int n = this.trackReadPos[k];
            int i1 = localTrack.size();
            MidiEvent localMidiEvent;
            while ((!bool1) && (n < i1) && ((localMidiEvent = localTrack.get(n)).getTick() <= l2))
            {
              if ((n == i1 - 1) && (MidiUtils.isMetaEndOfTrack(localMidiEvent.getMessage())))
              {
                n = i1;
                break;
              }
              n++;
              if ((m == 0) || ((k == 0) && (MidiUtils.isMetaTempo(localMidiEvent.getMessage())))) {
                bool1 = dispatchMessage(k, localMidiEvent);
              }
            }
            if (n >= i1) {
              j++;
            }
            this.trackReadPos[k] = n;
          }
          catch (Exception localException)
          {
            if ((localException instanceof ArrayIndexOutOfBoundsException))
            {
              this.needReindex = true;
              bool1 = true;
            }
          }
          if (bool1) {
            break;
          }
        }
        bool2 = j == this.tracks.length;
        if ((i != 0) || (((RealTimeSequencer.this.loopCount > 0) && (this.currLoopCounter > 0)) || ((RealTimeSequencer.this.loopCount == -1) && (!bool1) && (RealTimeSequencer.this.loopEnd == -1L) && (bool2))))
        {
          long l3 = this.checkPointMillis;
          long l4 = RealTimeSequencer.this.loopEnd;
          if (l4 == -1L) {
            l4 = this.lastTick;
          }
          if (RealTimeSequencer.this.loopCount != -1) {
            this.currLoopCounter -= 1;
          }
          setTickPos(RealTimeSequencer.this.loopStart);
          this.checkPointMillis = (l3 + tick2millis(l4 - this.checkPointTick));
          this.checkPointTick = RealTimeSequencer.this.loopStart;
          this.needReindex = false;
          bool1 = false;
          i = 0;
          bool2 = false;
        }
      } while (bool1);
      return bool2;
    }
  }
  
  final class PlayThread
    implements Runnable
  {
    private Thread thread;
    private final Object lock = new Object();
    boolean interrupted = false;
    boolean isPumping = false;
    private final RealTimeSequencer.DataPump dataPump = new RealTimeSequencer.DataPump(RealTimeSequencer.this);
    
    PlayThread()
    {
      int i = 8;
      this.thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, i, true);
    }
    
    RealTimeSequencer.DataPump getDataPump()
    {
      return this.dataPump;
    }
    
    synchronized void setSequence(Sequence paramSequence)
    {
      this.dataPump.setSequence(paramSequence);
    }
    
    synchronized void start()
    {
      RealTimeSequencer.this.running = true;
      if (!this.dataPump.hasCachedTempo())
      {
        long l = RealTimeSequencer.this.getTickPosition();
        this.dataPump.setTempoMPQ(RealTimeSequencer.this.tempoCache.getTempoMPQAt(l));
      }
      this.dataPump.checkPointMillis = 0L;
      this.dataPump.clearNoteOnCache();
      this.dataPump.needReindex = true;
      this.dataPump.resetLoopCount();
      synchronized (this.lock)
      {
        this.lock.notifyAll();
      }
    }
    
    synchronized void stop()
    {
      playThreadImplStop();
      long l = System.nanoTime() / 1000000L;
      while (this.isPumping)
      {
        synchronized (this.lock)
        {
          try
          {
            this.lock.wait(2000L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
        if (System.nanoTime() / 1000000L - l <= 1900L) {}
      }
    }
    
    void playThreadImplStop()
    {
      RealTimeSequencer.this.running = false;
      synchronized (this.lock)
      {
        this.lock.notifyAll();
      }
    }
    
    void close()
    {
      Thread localThread = null;
      synchronized (this)
      {
        this.interrupted = true;
        localThread = this.thread;
        this.thread = null;
      }
      if (localThread != null) {
        synchronized (this.lock)
        {
          this.lock.notifyAll();
        }
      }
      if (localThread != null) {
        try
        {
          localThread.join(2000L);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
    
    public void run()
    {
      while (!this.interrupted)
      {
        boolean bool1 = false;
        boolean bool2 = RealTimeSequencer.this.running;
        this.isPumping = ((!this.interrupted) && (RealTimeSequencer.this.running));
        while ((!bool1) && (!this.interrupted) && (RealTimeSequencer.this.running))
        {
          bool1 = this.dataPump.pump();
          try
          {
            Thread.sleep(1L);
          }
          catch (InterruptedException localInterruptedException) {}
        }
        playThreadImplStop();
        if (bool2) {
          this.dataPump.notesOff(true);
        }
        if (bool1)
        {
          this.dataPump.setTickPos(RealTimeSequencer.this.sequence.getTickLength());
          MetaMessage localMetaMessage = new MetaMessage();
          try
          {
            localMetaMessage.setMessage(47, new byte[0], 0);
          }
          catch (InvalidMidiDataException localInvalidMidiDataException) {}
          RealTimeSequencer.this.sendMetaEvents(localMetaMessage);
        }
        synchronized (this.lock)
        {
          this.isPumping = false;
          this.lock.notifyAll();
          while ((!RealTimeSequencer.this.running) && (!this.interrupted)) {
            try
            {
              this.lock.wait();
            }
            catch (Exception localException) {}
          }
        }
      }
    }
  }
  
  private static class RealTimeSequencerInfo
    extends MidiDevice.Info
  {
    private static final String name = "Real Time Sequencer";
    private static final String vendor = "Oracle Corporation";
    private static final String description = "Software sequencer";
    private static final String version = "Version 1.0";
    
    private RealTimeSequencerInfo()
    {
      super("Oracle Corporation", "Software sequencer", "Version 1.0");
    }
  }
  
  static class RecordingTrack
  {
    private final Track track;
    private int channel;
    
    RecordingTrack(Track paramTrack, int paramInt)
    {
      this.track = paramTrack;
      this.channel = paramInt;
    }
    
    static RecordingTrack get(List paramList, Track paramTrack)
    {
      synchronized (paramList)
      {
        int i = paramList.size();
        for (int j = 0; j < i; j++)
        {
          RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
          if (localRecordingTrack.track == paramTrack) {
            return localRecordingTrack;
          }
        }
      }
      return null;
    }
    
    static Track get(List paramList, int paramInt)
    {
      synchronized (paramList)
      {
        int i = paramList.size();
        for (int j = 0; j < i; j++)
        {
          RecordingTrack localRecordingTrack = (RecordingTrack)paramList.get(j);
          if ((localRecordingTrack.channel == paramInt) || (localRecordingTrack.channel == -1)) {
            return localRecordingTrack.track;
          }
        }
      }
      return null;
    }
  }
  
  final class SequencerReceiver
    extends AbstractMidiDevice.AbstractReceiver
  {
    SequencerReceiver()
    {
      super();
    }
    
    void implSend(MidiMessage paramMidiMessage, long paramLong)
    {
      if (RealTimeSequencer.this.recording)
      {
        long l = 0L;
        if (paramLong < 0L) {
          l = RealTimeSequencer.this.getTickPosition();
        } else {
          synchronized (RealTimeSequencer.this.tempoCache)
          {
            l = MidiUtils.microsecond2tick(RealTimeSequencer.this.sequence, paramLong, RealTimeSequencer.this.tempoCache);
          }
        }
        ??? = null;
        if (paramMidiMessage.getLength() > 1)
        {
          Object localObject2;
          if ((paramMidiMessage instanceof ShortMessage))
          {
            localObject2 = (ShortMessage)paramMidiMessage;
            if ((((ShortMessage)localObject2).getStatus() & 0xF0) != 240) {
              ??? = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, ((ShortMessage)localObject2).getChannel());
            }
          }
          else
          {
            ??? = RealTimeSequencer.RecordingTrack.get(RealTimeSequencer.this.recordingTracks, -1);
          }
          if (??? != null)
          {
            if ((paramMidiMessage instanceof ShortMessage)) {
              paramMidiMessage = new FastShortMessage((ShortMessage)paramMidiMessage);
            } else {
              paramMidiMessage = (MidiMessage)paramMidiMessage.clone();
            }
            localObject2 = new MidiEvent(paramMidiMessage, l);
            ((Track)???).add((MidiEvent)localObject2);
          }
        }
      }
    }
  }
  
  private class SequencerTransmitter
    extends AbstractMidiDevice.BasicTransmitter
  {
    private SequencerTransmitter()
    {
      super();
    }
  }
}
