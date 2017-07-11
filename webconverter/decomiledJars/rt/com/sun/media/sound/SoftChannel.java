package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

public final class SoftChannel
  implements MidiChannel, ModelDirectedPlayer
{
  private static boolean[] dontResetControls = new boolean[''];
  private static final int RPN_NULL_VALUE = 16383;
  private int rpn_control = 16383;
  private int nrpn_control = 16383;
  double portamento_time = 1.0D;
  int[] portamento_lastnote = new int[''];
  int portamento_lastnote_ix = 0;
  private boolean portamento = false;
  private boolean mono = false;
  private boolean mute = false;
  private boolean solo = false;
  private boolean solomute = false;
  private final Object control_mutex;
  private int channel;
  private SoftVoice[] voices;
  private int bank;
  private int program;
  private SoftSynthesizer synthesizer;
  private SoftMainMixer mainmixer;
  private int[] polypressure = new int[''];
  private int channelpressure = 0;
  private int[] controller = new int[''];
  private int pitchbend;
  private double[] co_midi_pitch = new double[1];
  private double[] co_midi_channel_pressure = new double[1];
  SoftTuning tuning = new SoftTuning();
  int tuning_bank = 0;
  int tuning_program = 0;
  SoftInstrument current_instrument = null;
  ModelChannelMixer current_mixer = null;
  ModelDirector current_director = null;
  int cds_control_number = -1;
  ModelConnectionBlock[] cds_control_connections = null;
  ModelConnectionBlock[] cds_channelpressure_connections = null;
  ModelConnectionBlock[] cds_polypressure_connections = null;
  boolean sustain = false;
  boolean[][] keybasedcontroller_active = (boolean[][])null;
  double[][] keybasedcontroller_value = (double[][])null;
  private SoftControl[] co_midi = new SoftControl[''];
  private double[][] co_midi_cc_cc;
  private SoftControl co_midi_cc;
  Map<Integer, int[]> co_midi_rpn_rpn_i;
  Map<Integer, double[]> co_midi_rpn_rpn;
  private SoftControl co_midi_rpn;
  Map<Integer, int[]> co_midi_nrpn_nrpn_i;
  Map<Integer, double[]> co_midi_nrpn_nrpn;
  private SoftControl co_midi_nrpn;
  private int[] lastVelocity;
  private int prevVoiceID;
  private boolean firstVoice;
  private int voiceNo;
  private int play_noteNumber;
  private int play_velocity;
  private int play_delay;
  private boolean play_releasetriggered;
  
  private static int restrict7Bit(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt > 127) {
      return 127;
    }
    return paramInt;
  }
  
  private static int restrict14Bit(int paramInt)
  {
    if (paramInt < 0) {
      return 0;
    }
    if (paramInt > 16256) {
      return 16256;
    }
    return paramInt;
  }
  
  public SoftChannel(SoftSynthesizer paramSoftSynthesizer, int paramInt)
  {
    for (int i = 0; i < this.co_midi.length; i++) {
      this.co_midi[i] = new MidiControlObject(null);
    }
    this.co_midi_cc_cc = new double[''][1];
    this.co_midi_cc = new SoftControl()
    {
      double[][] cc = SoftChannel.this.co_midi_cc_cc;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        return this.cc[Integer.parseInt(paramAnonymousString)];
      }
    };
    this.co_midi_rpn_rpn_i = new HashMap();
    this.co_midi_rpn_rpn = new HashMap();
    this.co_midi_rpn = new SoftControl()
    {
      Map<Integer, double[]> rpn = SoftChannel.this.co_midi_rpn_rpn;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        int i = Integer.parseInt(paramAnonymousString);
        double[] arrayOfDouble = (double[])this.rpn.get(Integer.valueOf(i));
        if (arrayOfDouble == null)
        {
          arrayOfDouble = new double[1];
          this.rpn.put(Integer.valueOf(i), arrayOfDouble);
        }
        return arrayOfDouble;
      }
    };
    this.co_midi_nrpn_nrpn_i = new HashMap();
    this.co_midi_nrpn_nrpn = new HashMap();
    this.co_midi_nrpn = new SoftControl()
    {
      Map<Integer, double[]> nrpn = SoftChannel.this.co_midi_nrpn_nrpn;
      
      public double[] get(int paramAnonymousInt, String paramAnonymousString)
      {
        if (paramAnonymousString == null) {
          return null;
        }
        int i = Integer.parseInt(paramAnonymousString);
        double[] arrayOfDouble = (double[])this.nrpn.get(Integer.valueOf(i));
        if (arrayOfDouble == null)
        {
          arrayOfDouble = new double[1];
          this.nrpn.put(Integer.valueOf(i), arrayOfDouble);
        }
        return arrayOfDouble;
      }
    };
    this.lastVelocity = new int[''];
    this.firstVoice = true;
    this.voiceNo = 0;
    this.play_noteNumber = 0;
    this.play_velocity = 0;
    this.play_delay = 0;
    this.play_releasetriggered = false;
    this.channel = paramInt;
    this.voices = paramSoftSynthesizer.getVoices();
    this.synthesizer = paramSoftSynthesizer;
    this.mainmixer = paramSoftSynthesizer.getMainMixer();
    this.control_mutex = paramSoftSynthesizer.control_mutex;
    resetAllControllers(true);
  }
  
  private int findFreeVoice(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    for (int i = paramInt; i < this.voices.length; i++) {
      if (!this.voices[i].active) {
        return i;
      }
    }
    i = this.synthesizer.getVoiceAllocationMode();
    if (i == 1)
    {
      j = this.channel;
      for (int k = 0; k < this.voices.length; k++) {
        if (this.voices[k].stealer_channel == null) {
          if (j == 9) {
            j = this.voices[k].channel;
          } else if ((this.voices[k].channel != 9) && (this.voices[k].channel > j)) {
            j = this.voices[k].channel;
          }
        }
      }
      k = -1;
      SoftVoice localSoftVoice2 = null;
      for (int n = 0; n < this.voices.length; n++) {
        if ((this.voices[n].channel == j) && (this.voices[n].stealer_channel == null) && (!this.voices[n].on))
        {
          if (localSoftVoice2 == null)
          {
            localSoftVoice2 = this.voices[n];
            k = n;
          }
          if (this.voices[n].voiceID < localSoftVoice2.voiceID)
          {
            localSoftVoice2 = this.voices[n];
            k = n;
          }
        }
      }
      if (k == -1) {
        for (n = 0; n < this.voices.length; n++) {
          if ((this.voices[n].channel == j) && (this.voices[n].stealer_channel == null))
          {
            if (localSoftVoice2 == null)
            {
              localSoftVoice2 = this.voices[n];
              k = n;
            }
            if (this.voices[n].voiceID < localSoftVoice2.voiceID)
            {
              localSoftVoice2 = this.voices[n];
              k = n;
            }
          }
        }
      }
      return k;
    }
    int j = -1;
    SoftVoice localSoftVoice1 = null;
    for (int m = 0; m < this.voices.length; m++) {
      if ((this.voices[m].stealer_channel == null) && (!this.voices[m].on))
      {
        if (localSoftVoice1 == null)
        {
          localSoftVoice1 = this.voices[m];
          j = m;
        }
        if (this.voices[m].voiceID < localSoftVoice1.voiceID)
        {
          localSoftVoice1 = this.voices[m];
          j = m;
        }
      }
    }
    if (j == -1) {
      for (m = 0; m < this.voices.length; m++) {
        if (this.voices[m].stealer_channel == null)
        {
          if (localSoftVoice1 == null)
          {
            localSoftVoice1 = this.voices[m];
            j = m;
          }
          if (this.voices[m].voiceID < localSoftVoice1.voiceID)
          {
            localSoftVoice1 = this.voices[m];
            j = m;
          }
        }
      }
    }
    return j;
  }
  
  void initVoice(SoftVoice paramSoftVoice, SoftPerformer paramSoftPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ModelConnectionBlock[] paramArrayOfModelConnectionBlock, ModelChannelMixer paramModelChannelMixer, boolean paramBoolean)
  {
    if (paramSoftVoice.active)
    {
      paramSoftVoice.stealer_channel = this;
      paramSoftVoice.stealer_performer = paramSoftPerformer;
      paramSoftVoice.stealer_voiceID = paramInt1;
      paramSoftVoice.stealer_noteNumber = paramInt2;
      paramSoftVoice.stealer_velocity = paramInt3;
      paramSoftVoice.stealer_extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
      paramSoftVoice.stealer_channelmixer = paramModelChannelMixer;
      paramSoftVoice.stealer_releaseTriggered = paramBoolean;
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].active) && (this.voices[i].voiceID == paramSoftVoice.voiceID)) {
          this.voices[i].soundOff();
        }
      }
      return;
    }
    paramSoftVoice.extendedConnectionBlocks = paramArrayOfModelConnectionBlock;
    paramSoftVoice.channelmixer = paramModelChannelMixer;
    paramSoftVoice.releaseTriggered = paramBoolean;
    paramSoftVoice.voiceID = paramInt1;
    paramSoftVoice.tuning = this.tuning;
    paramSoftVoice.exclusiveClass = paramSoftPerformer.exclusiveClass;
    paramSoftVoice.softchannel = this;
    paramSoftVoice.channel = this.channel;
    paramSoftVoice.bank = this.bank;
    paramSoftVoice.program = this.program;
    paramSoftVoice.instrument = this.current_instrument;
    paramSoftVoice.performer = paramSoftPerformer;
    paramSoftVoice.objects.clear();
    paramSoftVoice.objects.put("midi", this.co_midi[paramInt2]);
    paramSoftVoice.objects.put("midi_cc", this.co_midi_cc);
    paramSoftVoice.objects.put("midi_rpn", this.co_midi_rpn);
    paramSoftVoice.objects.put("midi_nrpn", this.co_midi_nrpn);
    paramSoftVoice.noteOn(paramInt2, paramInt3, paramInt4);
    paramSoftVoice.setMute(this.mute);
    paramSoftVoice.setSoloMute(this.solomute);
    if (paramBoolean) {
      return;
    }
    if (this.controller[84] != 0)
    {
      paramSoftVoice.co_noteon_keynumber[0] = (this.tuning.getTuning(this.controller[84]) / 100.0D * 0.0078125D);
      paramSoftVoice.portamento = true;
      controlChange(84, 0);
    }
    else if (this.portamento)
    {
      if (this.mono)
      {
        if (this.portamento_lastnote[0] != -1)
        {
          paramSoftVoice.co_noteon_keynumber[0] = (this.tuning.getTuning(this.portamento_lastnote[0]) / 100.0D * 0.0078125D);
          paramSoftVoice.portamento = true;
          controlChange(84, 0);
        }
        this.portamento_lastnote[0] = paramInt2;
      }
      else if (this.portamento_lastnote_ix != 0)
      {
        this.portamento_lastnote_ix -= 1;
        paramSoftVoice.co_noteon_keynumber[0] = (this.tuning.getTuning(this.portamento_lastnote[this.portamento_lastnote_ix]) / 100.0D * 0.0078125D);
        paramSoftVoice.portamento = true;
      }
    }
  }
  
  public void noteOn(int paramInt1, int paramInt2)
  {
    noteOn(paramInt1, paramInt2, 0);
  }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOn_internal(paramInt1, paramInt2, paramInt3);
    if (this.current_mixer != null) {
      this.current_mixer.noteOn(paramInt1, paramInt2);
    }
  }
  
  private void noteOn_internal(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt2 == 0)
    {
      noteOff_internal(paramInt1, 64);
      return;
    }
    synchronized (this.control_mutex)
    {
      if (this.sustain)
      {
        this.sustain = false;
        for (i = 0; i < this.voices.length; i++) {
          if (((this.voices[i].sustain) || (this.voices[i].on)) && (this.voices[i].channel == this.channel) && (this.voices[i].active) && (this.voices[i].note == paramInt1))
          {
            this.voices[i].sustain = false;
            this.voices[i].on = true;
            this.voices[i].noteOff(0);
          }
        }
        this.sustain = true;
      }
      this.mainmixer.activity();
      if (this.mono)
      {
        int j;
        if (this.portamento)
        {
          i = 0;
          for (j = 0; j < this.voices.length; j++) {
            if ((this.voices[j].on) && (this.voices[j].channel == this.channel) && (this.voices[j].active) && (!this.voices[j].releaseTriggered))
            {
              this.voices[j].portamento = true;
              this.voices[j].setNote(paramInt1);
              i = 1;
            }
          }
          if (i != 0)
          {
            this.portamento_lastnote[0] = paramInt1;
            return;
          }
        }
        if (this.controller[84] != 0)
        {
          i = 0;
          for (j = 0; j < this.voices.length; j++) {
            if ((this.voices[j].on) && (this.voices[j].channel == this.channel) && (this.voices[j].active) && (this.voices[j].note == this.controller[84]) && (!this.voices[j].releaseTriggered))
            {
              this.voices[j].portamento = true;
              this.voices[j].setNote(paramInt1);
              i = 1;
            }
          }
          controlChange(84, 0);
          if (i != 0) {
            return;
          }
        }
      }
      if (this.mono) {
        allNotesOff();
      }
      if (this.current_instrument == null)
      {
        this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
        if (this.current_instrument == null) {
          return;
        }
        if (this.current_mixer != null) {
          this.mainmixer.stopMixer(this.current_mixer);
        }
        this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
        if (this.current_mixer != null) {
          this.mainmixer.registerMixer(this.current_mixer);
        }
        this.current_director = this.current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      }
      this.prevVoiceID = (this.synthesizer.voiceIDCounter++);
      this.firstVoice = true;
      this.voiceNo = 0;
      int i = (int)Math.round(this.tuning.getTuning(paramInt1) / 100.0D);
      this.play_noteNumber = paramInt1;
      this.play_velocity = paramInt2;
      this.play_delay = paramInt3;
      this.play_releasetriggered = false;
      this.lastVelocity[paramInt1] = paramInt2;
      this.current_director.noteOn(i, paramInt2);
    }
  }
  
  public void noteOff(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    noteOff_internal(paramInt1, paramInt2);
    if (this.current_mixer != null) {
      this.current_mixer.noteOff(paramInt1, paramInt2);
    }
  }
  
  private void noteOff_internal(int paramInt1, int paramInt2)
  {
    synchronized (this.control_mutex)
    {
      if ((!this.mono) && (this.portamento) && (this.portamento_lastnote_ix != 127))
      {
        this.portamento_lastnote[this.portamento_lastnote_ix] = paramInt1;
        this.portamento_lastnote_ix += 1;
      }
      this.mainmixer.activity();
      for (int i = 0; i < this.voices.length; i++)
      {
        if ((this.voices[i].on) && (this.voices[i].channel == this.channel) && (this.voices[i].note == paramInt1) && (!this.voices[i].releaseTriggered)) {
          this.voices[i].noteOff(paramInt2);
        }
        if ((this.voices[i].stealer_channel == this) && (this.voices[i].stealer_noteNumber == paramInt1))
        {
          SoftVoice localSoftVoice = this.voices[i];
          localSoftVoice.stealer_releaseTriggered = false;
          localSoftVoice.stealer_channel = null;
          localSoftVoice.stealer_performer = null;
          localSoftVoice.stealer_voiceID = -1;
          localSoftVoice.stealer_noteNumber = 0;
          localSoftVoice.stealer_velocity = 0;
          localSoftVoice.stealer_extendedConnectionBlocks = null;
          localSoftVoice.stealer_channelmixer = null;
        }
      }
      if (this.current_instrument == null)
      {
        this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
        if (this.current_instrument == null) {
          return;
        }
        if (this.current_mixer != null) {
          this.mainmixer.stopMixer(this.current_mixer);
        }
        this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
        if (this.current_mixer != null) {
          this.mainmixer.registerMixer(this.current_mixer);
        }
        this.current_director = this.current_instrument.getDirector(this, this);
        applyInstrumentCustomization();
      }
      this.prevVoiceID = (this.synthesizer.voiceIDCounter++);
      this.firstVoice = true;
      this.voiceNo = 0;
      i = (int)Math.round(this.tuning.getTuning(paramInt1) / 100.0D);
      this.play_noteNumber = paramInt1;
      this.play_velocity = this.lastVelocity[paramInt1];
      this.play_releasetriggered = true;
      this.play_delay = 0;
      this.current_director.noteOff(i, paramInt2);
    }
  }
  
  public void play(int paramInt, ModelConnectionBlock[] paramArrayOfModelConnectionBlock)
  {
    int i = this.play_noteNumber;
    int j = this.play_velocity;
    int k = this.play_delay;
    boolean bool = this.play_releasetriggered;
    SoftPerformer localSoftPerformer = this.current_instrument.getPerformer(paramInt);
    if (this.firstVoice)
    {
      this.firstVoice = false;
      if (localSoftPerformer.exclusiveClass != 0)
      {
        int m = localSoftPerformer.exclusiveClass;
        for (int n = 0; n < this.voices.length; n++) {
          if ((this.voices[n].active) && (this.voices[n].channel == this.channel) && (this.voices[n].exclusiveClass == m) && ((!localSoftPerformer.selfNonExclusive) || (this.voices[n].note != i))) {
            this.voices[n].shutdown();
          }
        }
      }
    }
    this.voiceNo = findFreeVoice(this.voiceNo);
    if (this.voiceNo == -1) {
      return;
    }
    initVoice(this.voices[this.voiceNo], localSoftPerformer, this.prevVoiceID, i, j, k, paramArrayOfModelConnectionBlock, this.current_mixer, bool);
  }
  
  public void noteOff(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 127)) {
      return;
    }
    noteOff_internal(paramInt, 64);
  }
  
  public void setPolyPressure(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (this.current_mixer != null) {
      this.current_mixer.setPolyPressure(paramInt1, paramInt2);
    }
    synchronized (this.control_mutex)
    {
      this.mainmixer.activity();
      this.co_midi[paramInt1].get(0, "poly_pressure")[0] = (paramInt2 * 0.0078125D);
      this.polypressure[paramInt1] = paramInt2;
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].active) && (this.voices[i].note == paramInt1)) {
          this.voices[i].setPolyPressure(paramInt2);
        }
      }
    }
  }
  
  public int getPolyPressure(int paramInt)
  {
    synchronized (this.control_mutex)
    {
      return this.polypressure[paramInt];
    }
  }
  
  public void setChannelPressure(int paramInt)
  {
    paramInt = restrict7Bit(paramInt);
    if (this.current_mixer != null) {
      this.current_mixer.setChannelPressure(paramInt);
    }
    synchronized (this.control_mutex)
    {
      this.mainmixer.activity();
      this.co_midi_channel_pressure[0] = (paramInt * 0.0078125D);
      this.channelpressure = paramInt;
      for (int i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].setChannelPressure(paramInt);
        }
      }
    }
  }
  
  public int getChannelPressure()
  {
    synchronized (this.control_mutex)
    {
      return this.channelpressure;
    }
  }
  
  void applyInstrumentCustomization()
  {
    if ((this.cds_control_connections == null) && (this.cds_channelpressure_connections == null) && (this.cds_polypressure_connections == null)) {
      return;
    }
    ModelInstrument localModelInstrument = this.current_instrument.getSourceInstrument();
    ModelPerformer[] arrayOfModelPerformer1 = localModelInstrument.getPerformers();
    ModelPerformer[] arrayOfModelPerformer2 = new ModelPerformer[arrayOfModelPerformer1.length];
    for (int i = 0; i < arrayOfModelPerformer2.length; i++)
    {
      ModelPerformer localModelPerformer1 = arrayOfModelPerformer1[i];
      ModelPerformer localModelPerformer2 = new ModelPerformer();
      localModelPerformer2.setName(localModelPerformer1.getName());
      localModelPerformer2.setExclusiveClass(localModelPerformer1.getExclusiveClass());
      localModelPerformer2.setKeyFrom(localModelPerformer1.getKeyFrom());
      localModelPerformer2.setKeyTo(localModelPerformer1.getKeyTo());
      localModelPerformer2.setVelFrom(localModelPerformer1.getVelFrom());
      localModelPerformer2.setVelTo(localModelPerformer1.getVelTo());
      localModelPerformer2.getOscillators().addAll(localModelPerformer1.getOscillators());
      localModelPerformer2.getConnectionBlocks().addAll(localModelPerformer1.getConnectionBlocks());
      arrayOfModelPerformer2[i] = localModelPerformer2;
      List localList = localModelPerformer2.getConnectionBlocks();
      Object localObject1;
      Object localObject2;
      int i1;
      if (this.cds_control_connections != null)
      {
        localObject1 = Integer.toString(this.cds_control_number);
        localObject2 = localList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          ModelConnectionBlock localModelConnectionBlock2 = (ModelConnectionBlock)((Iterator)localObject2).next();
          ModelSource[] arrayOfModelSource2 = localModelConnectionBlock2.getSources();
          i1 = 0;
          if (arrayOfModelSource2 != null) {
            for (int i2 = 0; i2 < arrayOfModelSource2.length; i2++)
            {
              ModelSource localModelSource = arrayOfModelSource2[i2];
              if (("midi_cc".equals(localModelSource.getIdentifier().getObject())) && (((String)localObject1).equals(localModelSource.getIdentifier().getVariable()))) {
                i1 = 1;
              }
            }
          }
          if (i1 != 0) {
            ((Iterator)localObject2).remove();
          }
        }
        for (int m = 0; m < this.cds_control_connections.length; m++) {
          localList.add(this.cds_control_connections[m]);
        }
      }
      ModelSource[] arrayOfModelSource1;
      int n;
      Object localObject3;
      if (this.cds_polypressure_connections != null)
      {
        localObject1 = localList.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (ModelConnectionBlock)((Iterator)localObject1).next();
          arrayOfModelSource1 = ((ModelConnectionBlock)localObject2).getSources();
          n = 0;
          if (arrayOfModelSource1 != null) {
            for (i1 = 0; i1 < arrayOfModelSource1.length; i1++)
            {
              localObject3 = arrayOfModelSource1[i1];
              if (("midi".equals(((ModelSource)localObject3).getIdentifier().getObject())) && ("poly_pressure".equals(((ModelSource)localObject3).getIdentifier().getVariable()))) {
                n = 1;
              }
            }
          }
          if (n != 0) {
            ((Iterator)localObject1).remove();
          }
        }
        for (int j = 0; j < this.cds_polypressure_connections.length; j++) {
          localList.add(this.cds_polypressure_connections[j]);
        }
      }
      if (this.cds_channelpressure_connections != null)
      {
        localObject1 = localList.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          ModelConnectionBlock localModelConnectionBlock1 = (ModelConnectionBlock)((Iterator)localObject1).next();
          arrayOfModelSource1 = localModelConnectionBlock1.getSources();
          n = 0;
          if (arrayOfModelSource1 != null) {
            for (i1 = 0; i1 < arrayOfModelSource1.length; i1++)
            {
              localObject3 = arrayOfModelSource1[i1].getIdentifier();
              if (("midi".equals(((ModelIdentifier)localObject3).getObject())) && ("channel_pressure".equals(((ModelIdentifier)localObject3).getVariable()))) {
                n = 1;
              }
            }
          }
          if (n != 0) {
            ((Iterator)localObject1).remove();
          }
        }
        for (int k = 0; k < this.cds_channelpressure_connections.length; k++) {
          localList.add(this.cds_channelpressure_connections[k]);
        }
      }
    }
    this.current_instrument = new SoftInstrument(localModelInstrument, arrayOfModelPerformer2);
  }
  
  private ModelConnectionBlock[] createModelConnections(ModelIdentifier paramModelIdentifier, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfInt1.length; i++)
    {
      int j = paramArrayOfInt1[i];
      int k = paramArrayOfInt2[i];
      final double d;
      Object localObject;
      if (j == 0)
      {
        d = (k - 64) * 100;
        localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        localArrayList.add(localObject);
      }
      if (j == 1)
      {
        d = (k / 64.0D - 1.0D) * 9600.0D;
        if (d > 0.0D) {
          localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, true, false, 0), -d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        } else {
          localObject = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        }
        localArrayList.add(localObject);
      }
      ModelConnectionBlock localModelConnectionBlock;
      if (j == 2)
      {
        d = k / 64.0D;
        localObject = new ModelTransform()
        {
          double s = d;
          
          public double transform(double paramAnonymousDouble)
          {
            if (this.s < 1.0D) {
              paramAnonymousDouble = this.s + paramAnonymousDouble * (1.0D - this.s);
            } else if (this.s > 1.0D) {
              paramAnonymousDouble = 1.0D + paramAnonymousDouble * (this.s - 1.0D);
            } else {
              return 0.0D;
            }
            return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(paramAnonymousDouble);
          }
        };
        localModelConnectionBlock = new ModelConnectionBlock(new ModelSource(paramModelIdentifier, (ModelTransform)localObject), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        localArrayList.add(localModelConnectionBlock);
      }
      if (j == 3)
      {
        d = (k / 64.0D - 1.0D) * 9600.0D;
        localObject = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_PITCH));
        localArrayList.add(localObject);
      }
      if (j == 4)
      {
        d = k / 128.0D * 2400.0D;
        localObject = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(paramModelIdentifier, false, false, 0), d, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        localArrayList.add(localObject);
      }
      if (j == 5)
      {
        d = k / 127.0D;
        localObject = new ModelTransform()
        {
          double s = d;
          
          public double transform(double paramAnonymousDouble)
          {
            return -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - paramAnonymousDouble * this.s);
          }
        };
        localModelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, false, 0), new ModelSource(paramModelIdentifier, (ModelTransform)localObject), -960.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        localArrayList.add(localModelConnectionBlock);
      }
    }
    return (ModelConnectionBlock[])localArrayList.toArray(new ModelConnectionBlock[localArrayList.size()]);
  }
  
  public void mapPolyPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    this.current_instrument = null;
    if (paramArrayOfInt1.length == 0)
    {
      this.cds_polypressure_connections = null;
      return;
    }
    this.cds_polypressure_connections = createModelConnections(new ModelIdentifier("midi", "poly_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapChannelPressureToDestination(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    this.current_instrument = null;
    if (paramArrayOfInt1.length == 0)
    {
      this.cds_channelpressure_connections = null;
      return;
    }
    this.cds_channelpressure_connections = createModelConnections(new ModelIdentifier("midi", "channel_pressure"), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void mapControlToDestination(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (((paramInt < 1) || (paramInt > 31)) && ((paramInt < 64) || (paramInt > 95)))
    {
      this.cds_control_connections = null;
      return;
    }
    this.current_instrument = null;
    this.cds_control_number = paramInt;
    if (paramArrayOfInt1.length == 0)
    {
      this.cds_control_connections = null;
      return;
    }
    this.cds_control_connections = createModelConnections(new ModelIdentifier("midi_cc", Integer.toString(paramInt)), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void controlChangePerNote(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.keybasedcontroller_active == null)
    {
      this.keybasedcontroller_active = new boolean[''][];
      this.keybasedcontroller_value = new double[''][];
    }
    if (this.keybasedcontroller_active[paramInt1] == null)
    {
      this.keybasedcontroller_active[paramInt1] = new boolean[''];
      Arrays.fill(this.keybasedcontroller_active[paramInt1], false);
      this.keybasedcontroller_value[paramInt1] = new double[''];
      Arrays.fill(this.keybasedcontroller_value[paramInt1], 0.0D);
    }
    if (paramInt3 == -1)
    {
      this.keybasedcontroller_active[paramInt1][paramInt2] = 0;
    }
    else
    {
      this.keybasedcontroller_active[paramInt1][paramInt2] = 1;
      this.keybasedcontroller_value[paramInt1][paramInt2] = (paramInt3 / 128.0D);
    }
    int i;
    if (paramInt2 < 120) {
      for (i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].controlChange(paramInt2, -1);
        }
      }
    } else if (paramInt2 == 120) {
      for (i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].rpnChange(1, -1);
        }
      }
    } else if (paramInt2 == 121) {
      for (i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].rpnChange(2, -1);
        }
      }
    }
  }
  
  public int getControlPerNote(int paramInt1, int paramInt2)
  {
    if (this.keybasedcontroller_active == null) {
      return -1;
    }
    if (this.keybasedcontroller_active[paramInt1] == null) {
      return -1;
    }
    if (this.keybasedcontroller_active[paramInt1][paramInt2] == 0) {
      return -1;
    }
    return (int)(this.keybasedcontroller_value[paramInt1][paramInt2] * 128.0D);
  }
  
  public void controlChange(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict7Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    if (this.current_mixer != null) {
      this.current_mixer.controlChange(paramInt1, paramInt2);
    }
    synchronized (this.control_mutex)
    {
      int k;
      int m;
      switch (paramInt1)
      {
      case 5: 
        double d = -Math.asin(paramInt2 / 128.0D * 2.0D - 1.0D) / 3.141592653589793D + 0.5D;
        d = Math.pow(100000.0D, d) / 100.0D;
        d /= 100.0D;
        d *= 1000.0D;
        d /= this.synthesizer.getControlRate();
        this.portamento_time = d;
        break;
      case 6: 
      case 38: 
      case 96: 
      case 97: 
        int j = 0;
        int[] arrayOfInt;
        if (this.nrpn_control != 16383)
        {
          arrayOfInt = (int[])this.co_midi_nrpn_nrpn_i.get(Integer.valueOf(this.nrpn_control));
          if (arrayOfInt != null) {
            j = arrayOfInt[0];
          }
        }
        if (this.rpn_control != 16383)
        {
          arrayOfInt = (int[])this.co_midi_rpn_rpn_i.get(Integer.valueOf(this.rpn_control));
          if (arrayOfInt != null) {
            j = arrayOfInt[0];
          }
        }
        if (paramInt1 == 6)
        {
          j = (j & 0x7F) + (paramInt2 << 7);
        }
        else if (paramInt1 == 38)
        {
          j = (j & 0x3F80) + paramInt2;
        }
        else if ((paramInt1 == 96) || (paramInt1 == 97))
        {
          k = 1;
          if ((this.rpn_control == 2) || (this.rpn_control == 3) || (this.rpn_control == 4)) {
            k = 128;
          }
          if (paramInt1 == 96) {
            j += k;
          }
          if (paramInt1 == 97) {
            j -= k;
          }
        }
        if (this.nrpn_control != 16383) {
          nrpnChange(this.nrpn_control, j);
        }
        if (this.rpn_control != 16383) {
          rpnChange(this.rpn_control, j);
        }
        break;
      case 64: 
        k = paramInt2 >= 64 ? 1 : 0;
        if (this.sustain != k)
        {
          this.sustain = k;
          if (k == 0) {
            for (m = 0; m < this.voices.length; m++) {
              if ((this.voices[m].active) && (this.voices[m].sustain) && (this.voices[m].channel == this.channel))
              {
                this.voices[m].sustain = false;
                if (!this.voices[m].on)
                {
                  this.voices[m].on = true;
                  this.voices[m].noteOff(0);
                }
              }
            }
          } else {
            for (m = 0; m < this.voices.length; m++) {
              if ((this.voices[m].active) && (this.voices[m].channel == this.channel)) {
                this.voices[m].redamp();
              }
            }
          }
        }
        break;
      case 65: 
        this.portamento = (paramInt2 >= 64);
        this.portamento_lastnote[0] = -1;
        this.portamento_lastnote_ix = 0;
        break;
      case 66: 
        k = paramInt2 >= 64 ? 1 : 0;
        if (k != 0) {
          for (m = 0; m < this.voices.length; m++) {
            if ((this.voices[m].active) && (this.voices[m].on) && (this.voices[m].channel == this.channel)) {
              this.voices[m].sostenuto = true;
            }
          }
        }
        if (k == 0) {
          for (m = 0; m < this.voices.length; m++) {
            if ((this.voices[m].active) && (this.voices[m].sostenuto) && (this.voices[m].channel == this.channel))
            {
              this.voices[m].sostenuto = false;
              if (!this.voices[m].on)
              {
                this.voices[m].on = true;
                this.voices[m].noteOff(0);
              }
            }
          }
        }
        break;
      case 98: 
        this.nrpn_control = ((this.nrpn_control & 0x3F80) + paramInt2);
        this.rpn_control = 16383;
        break;
      case 99: 
        this.nrpn_control = ((this.nrpn_control & 0x7F) + (paramInt2 << 7));
        this.rpn_control = 16383;
        break;
      case 100: 
        this.rpn_control = ((this.rpn_control & 0x3F80) + paramInt2);
        this.nrpn_control = 16383;
        break;
      case 101: 
        this.rpn_control = ((this.rpn_control & 0x7F) + (paramInt2 << 7));
        this.nrpn_control = 16383;
        break;
      case 120: 
        allSoundOff();
        break;
      case 121: 
        resetAllControllers(paramInt2 == 127);
        break;
      case 122: 
        localControl(paramInt2 >= 64);
        break;
      case 123: 
        allNotesOff();
        break;
      case 124: 
        setOmni(false);
        break;
      case 125: 
        setOmni(true);
        break;
      case 126: 
        if (paramInt2 == 1) {
          setMono(true);
        }
        break;
      case 127: 
        setMono(false);
        break;
      }
      this.co_midi_cc_cc[paramInt1][0] = (paramInt2 * 0.0078125D);
      if (paramInt1 == 0)
      {
        this.bank = (paramInt2 << 7);
        return;
      }
      if (paramInt1 == 32)
      {
        this.bank = ((this.bank & 0x3F80) + paramInt2);
        return;
      }
      this.controller[paramInt1] = paramInt2;
      if (paramInt1 < 32) {
        this.controller[(paramInt1 + 32)] = 0;
      }
      for (int i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].controlChange(paramInt1, paramInt2);
        }
      }
    }
  }
  
  public int getController(int paramInt)
  {
    synchronized (this.control_mutex)
    {
      return this.controller[paramInt] & 0x7F;
    }
  }
  
  public void tuningChange(int paramInt)
  {
    tuningChange(0, paramInt);
  }
  
  public void tuningChange(int paramInt1, int paramInt2)
  {
    synchronized (this.control_mutex)
    {
      this.tuning = this.synthesizer.getTuning(new Patch(paramInt1, paramInt2));
    }
  }
  
  public void programChange(int paramInt)
  {
    programChange(this.bank, paramInt);
  }
  
  public void programChange(int paramInt1, int paramInt2)
  {
    paramInt1 = restrict14Bit(paramInt1);
    paramInt2 = restrict7Bit(paramInt2);
    synchronized (this.control_mutex)
    {
      this.mainmixer.activity();
      if ((this.bank != paramInt1) || (this.program != paramInt2))
      {
        this.bank = paramInt1;
        this.program = paramInt2;
        this.current_instrument = null;
      }
    }
  }
  
  public int getProgram()
  {
    synchronized (this.control_mutex)
    {
      return this.program;
    }
  }
  
  public void setPitchBend(int paramInt)
  {
    paramInt = restrict14Bit(paramInt);
    if (this.current_mixer != null) {
      this.current_mixer.setPitchBend(paramInt);
    }
    synchronized (this.control_mutex)
    {
      this.mainmixer.activity();
      this.co_midi_pitch[0] = (paramInt * 6.103515625E-5D);
      this.pitchbend = paramInt;
      for (int i = 0; i < this.voices.length; i++) {
        if (this.voices[i].active) {
          this.voices[i].setPitchBend(paramInt);
        }
      }
    }
  }
  
  public int getPitchBend()
  {
    synchronized (this.control_mutex)
    {
      return this.pitchbend;
    }
  }
  
  public void nrpnChange(int paramInt1, int paramInt2)
  {
    if (this.synthesizer.getGeneralMidiMode() == 0)
    {
      if (paramInt1 == 136) {
        controlChange(76, paramInt2 >> 7);
      }
      if (paramInt1 == 137) {
        controlChange(77, paramInt2 >> 7);
      }
      if (paramInt1 == 138) {
        controlChange(78, paramInt2 >> 7);
      }
      if (paramInt1 == 160) {
        controlChange(74, paramInt2 >> 7);
      }
      if (paramInt1 == 161) {
        controlChange(71, paramInt2 >> 7);
      }
      if (paramInt1 == 227) {
        controlChange(73, paramInt2 >> 7);
      }
      if (paramInt1 == 228) {
        controlChange(75, paramInt2 >> 7);
      }
      if (paramInt1 == 230) {
        controlChange(72, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 24) {
        controlChangePerNote(paramInt1 % 128, 120, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 26) {
        controlChangePerNote(paramInt1 % 128, 7, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 28) {
        controlChangePerNote(paramInt1 % 128, 10, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 29) {
        controlChangePerNote(paramInt1 % 128, 91, paramInt2 >> 7);
      }
      if (paramInt1 >> 7 == 30) {
        controlChangePerNote(paramInt1 % 128, 93, paramInt2 >> 7);
      }
    }
    int[] arrayOfInt = (int[])this.co_midi_nrpn_nrpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])this.co_midi_nrpn_nrpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[1];
      this.co_midi_nrpn_nrpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    }
    if (arrayOfDouble == null)
    {
      arrayOfDouble = new double[1];
      this.co_midi_nrpn_nrpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    }
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = (arrayOfInt[0] * 6.103515625E-5D);
    for (int i = 0; i < this.voices.length; i++) {
      if (this.voices[i].active) {
        this.voices[i].nrpnChange(paramInt1, arrayOfInt[0]);
      }
    }
  }
  
  public void rpnChange(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 3)
    {
      this.tuning_program = (paramInt2 >> 7 & 0x7F);
      tuningChange(this.tuning_bank, this.tuning_program);
    }
    if (paramInt1 == 4) {
      this.tuning_bank = (paramInt2 >> 7 & 0x7F);
    }
    int[] arrayOfInt = (int[])this.co_midi_rpn_rpn_i.get(Integer.valueOf(paramInt1));
    double[] arrayOfDouble = (double[])this.co_midi_rpn_rpn.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null)
    {
      arrayOfInt = new int[1];
      this.co_midi_rpn_rpn_i.put(Integer.valueOf(paramInt1), arrayOfInt);
    }
    if (arrayOfDouble == null)
    {
      arrayOfDouble = new double[1];
      this.co_midi_rpn_rpn.put(Integer.valueOf(paramInt1), arrayOfDouble);
    }
    arrayOfInt[0] = paramInt2;
    arrayOfDouble[0] = (arrayOfInt[0] * 6.103515625E-5D);
    for (int i = 0; i < this.voices.length; i++) {
      if (this.voices[i].active) {
        this.voices[i].rpnChange(paramInt1, arrayOfInt[0]);
      }
    }
  }
  
  public void resetAllControllers()
  {
    resetAllControllers(false);
  }
  
  public void resetAllControllers(boolean paramBoolean)
  {
    synchronized (this.control_mutex)
    {
      this.mainmixer.activity();
      for (int i = 0; i < 128; i++) {
        setPolyPressure(i, 0);
      }
      setChannelPressure(0);
      setPitchBend(8192);
      for (i = 0; i < 128; i++) {
        if (dontResetControls[i] == 0) {
          controlChange(i, 0);
        }
      }
      controlChange(71, 64);
      controlChange(72, 64);
      controlChange(73, 64);
      controlChange(74, 64);
      controlChange(75, 64);
      controlChange(76, 64);
      controlChange(77, 64);
      controlChange(78, 64);
      controlChange(8, 64);
      controlChange(11, 127);
      controlChange(98, 127);
      controlChange(99, 127);
      controlChange(100, 127);
      controlChange(101, 127);
      if (paramBoolean)
      {
        this.keybasedcontroller_active = ((boolean[][])null);
        this.keybasedcontroller_value = ((double[][])null);
        controlChange(7, 100);
        controlChange(10, 64);
        controlChange(91, 40);
        Iterator localIterator = this.co_midi_rpn_rpn.keySet().iterator();
        int j;
        while (localIterator.hasNext())
        {
          j = ((Integer)localIterator.next()).intValue();
          if ((j != 3) && (j != 4)) {
            rpnChange(j, 0);
          }
        }
        localIterator = this.co_midi_nrpn_nrpn.keySet().iterator();
        while (localIterator.hasNext())
        {
          j = ((Integer)localIterator.next()).intValue();
          nrpnChange(j, 0);
        }
        rpnChange(0, 256);
        rpnChange(1, 8192);
        rpnChange(2, 8192);
        rpnChange(5, 64);
        this.tuning_bank = 0;
        this.tuning_program = 0;
        this.tuning = new SoftTuning();
      }
    }
  }
  
  public void allNotesOff()
  {
    if (this.current_mixer != null) {
      this.current_mixer.allNotesOff();
    }
    synchronized (this.control_mutex)
    {
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].on) && (this.voices[i].channel == this.channel) && (!this.voices[i].releaseTriggered)) {
          this.voices[i].noteOff(0);
        }
      }
    }
  }
  
  public void allSoundOff()
  {
    if (this.current_mixer != null) {
      this.current_mixer.allSoundOff();
    }
    synchronized (this.control_mutex)
    {
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].on) && (this.voices[i].channel == this.channel)) {
          this.voices[i].soundOff();
        }
      }
    }
  }
  
  public boolean localControl(boolean paramBoolean)
  {
    return false;
  }
  
  public void setMono(boolean paramBoolean)
  {
    if (this.current_mixer != null) {
      this.current_mixer.setMono(paramBoolean);
    }
    synchronized (this.control_mutex)
    {
      allNotesOff();
      this.mono = paramBoolean;
    }
  }
  
  public boolean getMono()
  {
    synchronized (this.control_mutex)
    {
      return this.mono;
    }
  }
  
  public void setOmni(boolean paramBoolean)
  {
    if (this.current_mixer != null) {
      this.current_mixer.setOmni(paramBoolean);
    }
    allNotesOff();
  }
  
  public boolean getOmni()
  {
    return false;
  }
  
  public void setMute(boolean paramBoolean)
  {
    if (this.current_mixer != null) {
      this.current_mixer.setMute(paramBoolean);
    }
    synchronized (this.control_mutex)
    {
      this.mute = paramBoolean;
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].active) && (this.voices[i].channel == this.channel)) {
          this.voices[i].setMute(paramBoolean);
        }
      }
    }
  }
  
  public boolean getMute()
  {
    synchronized (this.control_mutex)
    {
      return this.mute;
    }
  }
  
  public void setSolo(boolean paramBoolean)
  {
    if (this.current_mixer != null) {
      this.current_mixer.setSolo(paramBoolean);
    }
    synchronized (this.control_mutex)
    {
      this.solo = paramBoolean;
      int i = 0;
      SoftChannel localSoftChannel;
      for (localSoftChannel : this.synthesizer.channels) {
        if (localSoftChannel.solo)
        {
          i = 1;
          break;
        }
      }
      if (i == 0)
      {
        for (localSoftChannel : this.synthesizer.channels) {
          localSoftChannel.setSoloMute(false);
        }
        return;
      }
      for (localSoftChannel : this.synthesizer.channels) {
        localSoftChannel.setSoloMute(!localSoftChannel.solo);
      }
    }
  }
  
  private void setSoloMute(boolean paramBoolean)
  {
    synchronized (this.control_mutex)
    {
      if (this.solomute == paramBoolean) {
        return;
      }
      this.solomute = paramBoolean;
      for (int i = 0; i < this.voices.length; i++) {
        if ((this.voices[i].active) && (this.voices[i].channel == this.channel)) {
          this.voices[i].setSoloMute(this.solomute);
        }
      }
    }
  }
  
  public boolean getSolo()
  {
    synchronized (this.control_mutex)
    {
      return this.solo;
    }
  }
  
  static
  {
    for (int i = 0; i < dontResetControls.length; i++) {
      dontResetControls[i] = false;
    }
    dontResetControls[0] = true;
    dontResetControls[32] = true;
    dontResetControls[7] = true;
    dontResetControls[8] = true;
    dontResetControls[10] = true;
    dontResetControls[11] = true;
    dontResetControls[91] = true;
    dontResetControls[92] = true;
    dontResetControls[93] = true;
    dontResetControls[94] = true;
    dontResetControls[95] = true;
    dontResetControls[70] = true;
    dontResetControls[71] = true;
    dontResetControls[72] = true;
    dontResetControls[73] = true;
    dontResetControls[74] = true;
    dontResetControls[75] = true;
    dontResetControls[76] = true;
    dontResetControls[77] = true;
    dontResetControls[78] = true;
    dontResetControls[79] = true;
    dontResetControls[120] = true;
    dontResetControls[121] = true;
    dontResetControls[122] = true;
    dontResetControls[123] = true;
    dontResetControls[124] = true;
    dontResetControls[125] = true;
    dontResetControls[126] = true;
    dontResetControls[127] = true;
    dontResetControls[6] = true;
    dontResetControls[38] = true;
    dontResetControls[96] = true;
    dontResetControls[97] = true;
    dontResetControls[98] = true;
    dontResetControls[99] = true;
    dontResetControls[100] = true;
    dontResetControls[101] = true;
  }
  
  private class MidiControlObject
    implements SoftControl
  {
    double[] pitch = SoftChannel.this.co_midi_pitch;
    double[] channel_pressure = SoftChannel.this.co_midi_channel_pressure;
    double[] poly_pressure = new double[1];
    
    private MidiControlObject() {}
    
    public double[] get(int paramInt, String paramString)
    {
      if (paramString == null) {
        return null;
      }
      if (paramString.equals("pitch")) {
        return this.pitch;
      }
      if (paramString.equals("channel_pressure")) {
        return this.channel_pressure;
      }
      if (paramString.equals("poly_pressure")) {
        return this.poly_pressure;
      }
      return null;
    }
  }
}
