package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.VoiceStatus;
import javax.sound.sampled.AudioFormat;

public final class SoftVoice
  extends VoiceStatus
{
  public int exclusiveClass = 0;
  public boolean releaseTriggered = false;
  private int noteOn_noteNumber = 0;
  private int noteOn_velocity = 0;
  private int noteOff_velocity = 0;
  private int delay = 0;
  ModelChannelMixer channelmixer = null;
  double tunedKey = 0.0D;
  SoftTuning tuning = null;
  SoftChannel stealer_channel = null;
  ModelConnectionBlock[] stealer_extendedConnectionBlocks = null;
  SoftPerformer stealer_performer = null;
  ModelChannelMixer stealer_channelmixer = null;
  int stealer_voiceID = -1;
  int stealer_noteNumber = 0;
  int stealer_velocity = 0;
  boolean stealer_releaseTriggered = false;
  int voiceID = -1;
  boolean sustain = false;
  boolean sostenuto = false;
  boolean portamento = false;
  private final SoftFilter filter_left;
  private final SoftFilter filter_right;
  private final SoftProcess eg = new SoftEnvelopeGenerator();
  private final SoftProcess lfo = new SoftLowFrequencyOscillator();
  Map<String, SoftControl> objects = new HashMap();
  SoftSynthesizer synthesizer;
  SoftInstrument instrument;
  SoftPerformer performer;
  SoftChannel softchannel = null;
  boolean on = false;
  private boolean audiostarted = false;
  private boolean started = false;
  private boolean stopping = false;
  private float osc_attenuation = 0.0F;
  private ModelOscillatorStream osc_stream;
  private int osc_stream_nrofchannels;
  private float[][] osc_buff = new float[2][];
  private boolean osc_stream_off_transmitted = false;
  private boolean out_mixer_end = false;
  private float out_mixer_left = 0.0F;
  private float out_mixer_right = 0.0F;
  private float out_mixer_effect1 = 0.0F;
  private float out_mixer_effect2 = 0.0F;
  private float last_out_mixer_left = 0.0F;
  private float last_out_mixer_right = 0.0F;
  private float last_out_mixer_effect1 = 0.0F;
  private float last_out_mixer_effect2 = 0.0F;
  ModelConnectionBlock[] extendedConnectionBlocks = null;
  private ModelConnectionBlock[] connections;
  private double[] connections_last = new double[50];
  private double[][][] connections_src = new double[50][3][];
  private int[][] connections_src_kc = new int[50][3];
  private double[][] connections_dst = new double[50][];
  private boolean soundoff = false;
  private float lastMuteValue = 0.0F;
  private float lastSoloMuteValue = 0.0F;
  double[] co_noteon_keynumber = new double[1];
  double[] co_noteon_velocity = new double[1];
  double[] co_noteon_on = new double[1];
  private final SoftControl co_noteon = new SoftControl()
  {
    double[] keynumber = SoftVoice.this.co_noteon_keynumber;
    double[] velocity = SoftVoice.this.co_noteon_velocity;
    double[] on = SoftVoice.this.co_noteon_on;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("keynumber")) {
        return this.keynumber;
      }
      if (paramAnonymousString.equals("velocity")) {
        return this.velocity;
      }
      if (paramAnonymousString.equals("on")) {
        return this.on;
      }
      return null;
    }
  };
  private final double[] co_mixer_active = new double[1];
  private final double[] co_mixer_gain = new double[1];
  private final double[] co_mixer_pan = new double[1];
  private final double[] co_mixer_balance = new double[1];
  private final double[] co_mixer_reverb = new double[1];
  private final double[] co_mixer_chorus = new double[1];
  private final SoftControl co_mixer = new SoftControl()
  {
    double[] active = SoftVoice.this.co_mixer_active;
    double[] gain = SoftVoice.this.co_mixer_gain;
    double[] pan = SoftVoice.this.co_mixer_pan;
    double[] balance = SoftVoice.this.co_mixer_balance;
    double[] reverb = SoftVoice.this.co_mixer_reverb;
    double[] chorus = SoftVoice.this.co_mixer_chorus;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("active")) {
        return this.active;
      }
      if (paramAnonymousString.equals("gain")) {
        return this.gain;
      }
      if (paramAnonymousString.equals("pan")) {
        return this.pan;
      }
      if (paramAnonymousString.equals("balance")) {
        return this.balance;
      }
      if (paramAnonymousString.equals("reverb")) {
        return this.reverb;
      }
      if (paramAnonymousString.equals("chorus")) {
        return this.chorus;
      }
      return null;
    }
  };
  private final double[] co_osc_pitch = new double[1];
  private final SoftControl co_osc = new SoftControl()
  {
    double[] pitch = SoftVoice.this.co_osc_pitch;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("pitch")) {
        return this.pitch;
      }
      return null;
    }
  };
  private final double[] co_filter_freq = new double[1];
  private final double[] co_filter_type = new double[1];
  private final double[] co_filter_q = new double[1];
  private final SoftControl co_filter = new SoftControl()
  {
    double[] freq = SoftVoice.this.co_filter_freq;
    double[] ftype = SoftVoice.this.co_filter_type;
    double[] q = SoftVoice.this.co_filter_q;
    
    public double[] get(int paramAnonymousInt, String paramAnonymousString)
    {
      if (paramAnonymousString == null) {
        return null;
      }
      if (paramAnonymousString.equals("freq")) {
        return this.freq;
      }
      if (paramAnonymousString.equals("type")) {
        return this.ftype;
      }
      if (paramAnonymousString.equals("q")) {
        return this.q;
      }
      return null;
    }
  };
  SoftResamplerStreamer resampler;
  private final int nrofchannels;
  
  public SoftVoice(SoftSynthesizer paramSoftSynthesizer)
  {
    this.synthesizer = paramSoftSynthesizer;
    this.filter_left = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    this.filter_right = new SoftFilter(paramSoftSynthesizer.getFormat().getSampleRate());
    this.nrofchannels = paramSoftSynthesizer.getFormat().getChannels();
  }
  
  private int getValueKC(ModelIdentifier paramModelIdentifier)
  {
    if (paramModelIdentifier.getObject().equals("midi_cc"))
    {
      int i = Integer.parseInt(paramModelIdentifier.getVariable());
      if ((i != 0) && (i != 32) && (i < 120)) {
        return i;
      }
    }
    else if (paramModelIdentifier.getObject().equals("midi_rpn"))
    {
      if (paramModelIdentifier.getVariable().equals("1")) {
        return 120;
      }
      if (paramModelIdentifier.getVariable().equals("2")) {
        return 121;
      }
    }
    return -1;
  }
  
  private double[] getValue(ModelIdentifier paramModelIdentifier)
  {
    SoftControl localSoftControl = (SoftControl)this.objects.get(paramModelIdentifier.getObject());
    if (localSoftControl == null) {
      return null;
    }
    return localSoftControl.get(paramModelIdentifier.getInstance(), paramModelIdentifier.getVariable());
  }
  
  private double transformValue(double paramDouble, ModelSource paramModelSource)
  {
    if (paramModelSource.getTransform() != null) {
      return paramModelSource.getTransform().transform(paramDouble);
    }
    return paramDouble;
  }
  
  private double transformValue(double paramDouble, ModelDestination paramModelDestination)
  {
    if (paramModelDestination.getTransform() != null) {
      return paramModelDestination.getTransform().transform(paramDouble);
    }
    return paramDouble;
  }
  
  private double processKeyBasedController(double paramDouble, int paramInt)
  {
    if (paramInt == -1) {
      return paramDouble;
    }
    if ((this.softchannel.keybasedcontroller_active != null) && (this.softchannel.keybasedcontroller_active[this.note] != null) && (this.softchannel.keybasedcontroller_active[this.note][paramInt] != 0))
    {
      double d = this.softchannel.keybasedcontroller_value[this.note][paramInt];
      if ((paramInt == 10) || (paramInt == 91) || (paramInt == 93)) {
        return d;
      }
      paramDouble += d * 2.0D - 1.0D;
      if (paramDouble > 1.0D) {
        paramDouble = 1.0D;
      } else if (paramDouble < 0.0D) {
        paramDouble = 0.0D;
      }
    }
    return paramDouble;
  }
  
  private void processConnection(int paramInt)
  {
    ModelConnectionBlock localModelConnectionBlock = this.connections[paramInt];
    double[][] arrayOfDouble = this.connections_src[paramInt];
    double[] arrayOfDouble1 = this.connections_dst[paramInt];
    if ((arrayOfDouble1 == null) || (Double.isInfinite(arrayOfDouble1[0]))) {
      return;
    }
    double d = localModelConnectionBlock.getScale();
    ModelSource[] arrayOfModelSource;
    if (this.softchannel.keybasedcontroller_active == null)
    {
      arrayOfModelSource = localModelConnectionBlock.getSources();
      for (int i = 0; i < arrayOfModelSource.length; i++)
      {
        d *= transformValue(arrayOfDouble[i][0], arrayOfModelSource[i]);
        if (d == 0.0D) {
          break;
        }
      }
    }
    else
    {
      arrayOfModelSource = localModelConnectionBlock.getSources();
      int[] arrayOfInt = this.connections_src_kc[paramInt];
      for (int j = 0; j < arrayOfModelSource.length; j++)
      {
        d *= transformValue(processKeyBasedController(arrayOfDouble[j][0], arrayOfInt[j]), arrayOfModelSource[j]);
        if (d == 0.0D) {
          break;
        }
      }
    }
    d = transformValue(d, localModelConnectionBlock.getDestination());
    arrayOfDouble1[0] = (arrayOfDouble1[0] - this.connections_last[paramInt] + d);
    this.connections_last[paramInt] = d;
  }
  
  void updateTuning(SoftTuning paramSoftTuning)
  {
    this.tuning = paramSoftTuning;
    this.tunedKey = (this.tuning.getTuning(this.note) / 100.0D);
    if (!this.portamento)
    {
      this.co_noteon_keynumber[0] = (this.tunedKey * 0.0078125D);
      if (this.performer == null) {
        return;
      }
      int[] arrayOfInt = this.performer.midi_connections[4];
      if (arrayOfInt == null) {
        return;
      }
      for (int i = 0; i < arrayOfInt.length; i++) {
        processConnection(arrayOfInt[i]);
      }
    }
  }
  
  void setNote(int paramInt)
  {
    this.note = paramInt;
    this.tunedKey = (this.tuning.getTuning(paramInt) / 100.0D);
  }
  
  void noteOn(int paramInt1, int paramInt2, int paramInt3)
  {
    this.sustain = false;
    this.sostenuto = false;
    this.portamento = false;
    this.soundoff = false;
    this.on = true;
    this.active = true;
    this.started = true;
    this.noteOn_noteNumber = paramInt1;
    this.noteOn_velocity = paramInt2;
    this.delay = paramInt3;
    this.lastMuteValue = 0.0F;
    this.lastSoloMuteValue = 0.0F;
    setNote(paramInt1);
    if (this.performer.forcedKeynumber) {
      this.co_noteon_keynumber[0] = 0.0D;
    } else {
      this.co_noteon_keynumber[0] = (this.tunedKey * 0.0078125D);
    }
    if (this.performer.forcedVelocity) {
      this.co_noteon_velocity[0] = 0.0D;
    } else {
      this.co_noteon_velocity[0] = (paramInt2 * 0.0078125F);
    }
    this.co_mixer_active[0] = 0.0D;
    this.co_mixer_gain[0] = 0.0D;
    this.co_mixer_pan[0] = 0.0D;
    this.co_mixer_balance[0] = 0.0D;
    this.co_mixer_reverb[0] = 0.0D;
    this.co_mixer_chorus[0] = 0.0D;
    this.co_osc_pitch[0] = 0.0D;
    this.co_filter_freq[0] = 0.0D;
    this.co_filter_q[0] = 0.0D;
    this.co_filter_type[0] = 0.0D;
    this.co_noteon_on[0] = 1.0D;
    this.eg.reset();
    this.lfo.reset();
    this.filter_left.reset();
    this.filter_right.reset();
    this.objects.put("master", this.synthesizer.getMainMixer().co_master);
    this.objects.put("eg", this.eg);
    this.objects.put("lfo", this.lfo);
    this.objects.put("noteon", this.co_noteon);
    this.objects.put("osc", this.co_osc);
    this.objects.put("mixer", this.co_mixer);
    this.objects.put("filter", this.co_filter);
    this.connections = this.performer.connections;
    if ((this.connections_last == null) || (this.connections_last.length < this.connections.length)) {
      this.connections_last = new double[this.connections.length];
    }
    if ((this.connections_src == null) || (this.connections_src.length < this.connections.length))
    {
      this.connections_src = new double[this.connections.length][][];
      this.connections_src_kc = new int[this.connections.length][];
    }
    if ((this.connections_dst == null) || (this.connections_dst.length < this.connections.length)) {
      this.connections_dst = new double[this.connections.length][];
    }
    Object localObject1;
    for (int i = 0; i < this.connections.length; i++)
    {
      ModelConnectionBlock localModelConnectionBlock = this.connections[i];
      this.connections_last[i] = 0.0D;
      if (localModelConnectionBlock.getSources() != null)
      {
        ModelSource[] arrayOfModelSource = localModelConnectionBlock.getSources();
        if ((this.connections_src[i] == null) || (this.connections_src[i].length < arrayOfModelSource.length))
        {
          this.connections_src[i] = new double[arrayOfModelSource.length][];
          this.connections_src_kc[i] = new int[arrayOfModelSource.length];
        }
        localObject1 = this.connections_src[i];
        int[] arrayOfInt = this.connections_src_kc[i];
        this.connections_src[i] = localObject1;
        for (int m = 0; m < arrayOfModelSource.length; m++)
        {
          arrayOfInt[m] = getValueKC(arrayOfModelSource[m].getIdentifier());
          localObject1[m] = getValue(arrayOfModelSource[m].getIdentifier());
        }
      }
      if (localModelConnectionBlock.getDestination() != null) {
        this.connections_dst[i] = getValue(localModelConnectionBlock.getDestination().getIdentifier());
      } else {
        this.connections_dst[i] = null;
      }
    }
    for (i = 0; i < this.connections.length; i++) {
      processConnection(i);
    }
    if (this.extendedConnectionBlocks != null) {
      for (localObject1 : this.extendedConnectionBlocks)
      {
        double d1 = 0.0D;
        Object localObject3;
        double d2;
        ModelTransform localModelTransform2;
        if (this.softchannel.keybasedcontroller_active == null) {
          for (localObject3 : ((ModelConnectionBlock)localObject1).getSources())
          {
            d2 = getValue(localObject3.getIdentifier())[0];
            localModelTransform2 = localObject3.getTransform();
            if (localModelTransform2 == null) {
              d1 += d2;
            } else {
              d1 += localModelTransform2.transform(d2);
            }
          }
        } else {
          for (localObject3 : ((ModelConnectionBlock)localObject1).getSources())
          {
            d2 = getValue(localObject3.getIdentifier())[0];
            d2 = processKeyBasedController(d2, getValueKC(localObject3.getIdentifier()));
            localModelTransform2 = localObject3.getTransform();
            if (localModelTransform2 == null) {
              d1 += d2;
            } else {
              d1 += localModelTransform2.transform(d2);
            }
          }
        }
        ??? = ((ModelConnectionBlock)localObject1).getDestination();
        ModelTransform localModelTransform1 = ((ModelDestination)???).getTransform();
        if (localModelTransform1 != null) {
          d1 = localModelTransform1.transform(d1);
        }
        getValue(((ModelDestination)???).getIdentifier())[0] += d1;
      }
    }
    this.eg.init(this.synthesizer);
    this.lfo.init(this.synthesizer);
  }
  
  void setPolyPressure(int paramInt)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[2];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setChannelPressure(int paramInt)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[1];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void controlChange(int paramInt1, int paramInt2)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_ctrl_connections[paramInt1];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void nrpnChange(int paramInt1, int paramInt2)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = (int[])this.performer.midi_nrpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void rpnChange(int paramInt1, int paramInt2)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = (int[])this.performer.midi_rpn_connections.get(Integer.valueOf(paramInt1));
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setPitchBend(int paramInt)
  {
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[0];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void setMute(boolean paramBoolean)
  {
    this.co_mixer_gain[0] -= this.lastMuteValue;
    this.lastMuteValue = (paramBoolean ? -960.0F : 0.0F);
    this.co_mixer_gain[0] += this.lastMuteValue;
  }
  
  void setSoloMute(boolean paramBoolean)
  {
    this.co_mixer_gain[0] -= this.lastSoloMuteValue;
    this.lastSoloMuteValue = (paramBoolean ? -960.0F : 0.0F);
    this.co_mixer_gain[0] += this.lastSoloMuteValue;
  }
  
  void shutdown()
  {
    if (this.co_noteon_on[0] < -0.5D) {
      return;
    }
    this.on = false;
    this.co_noteon_on[0] = -1.0D;
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void soundOff()
  {
    this.on = false;
    this.soundoff = true;
  }
  
  void noteOff(int paramInt)
  {
    if (!this.on) {
      return;
    }
    this.on = false;
    this.noteOff_velocity = paramInt;
    if (this.softchannel.sustain)
    {
      this.sustain = true;
      return;
    }
    if (this.sostenuto) {
      return;
    }
    this.co_noteon_on[0] = 0.0D;
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void redamp()
  {
    if (this.co_noteon_on[0] > 0.5D) {
      return;
    }
    if (this.co_noteon_on[0] < -0.5D) {
      return;
    }
    this.sustain = true;
    this.co_noteon_on[0] = 1.0D;
    if (this.performer == null) {
      return;
    }
    int[] arrayOfInt = this.performer.midi_connections[3];
    if (arrayOfInt == null) {
      return;
    }
    for (int i = 0; i < arrayOfInt.length; i++) {
      processConnection(arrayOfInt[i]);
    }
  }
  
  void processControlLogic()
  {
    if (this.stopping)
    {
      this.active = false;
      this.stopping = false;
      this.audiostarted = false;
      this.instrument = null;
      this.performer = null;
      this.connections = null;
      this.extendedConnectionBlocks = null;
      this.channelmixer = null;
      if (this.osc_stream != null) {
        try
        {
          this.osc_stream.close();
        }
        catch (IOException localIOException1) {}
      }
      if (this.stealer_channel != null)
      {
        this.stealer_channel.initVoice(this, this.stealer_performer, this.stealer_voiceID, this.stealer_noteNumber, this.stealer_velocity, 0, this.stealer_extendedConnectionBlocks, this.stealer_channelmixer, this.stealer_releaseTriggered);
        this.stealer_releaseTriggered = false;
        this.stealer_channel = null;
        this.stealer_performer = null;
        this.stealer_voiceID = -1;
        this.stealer_noteNumber = 0;
        this.stealer_velocity = 0;
        this.stealer_extendedConnectionBlocks = null;
        this.stealer_channelmixer = null;
      }
    }
    if (this.started)
    {
      this.audiostarted = true;
      ModelOscillator localModelOscillator = this.performer.oscillators[0];
      this.osc_stream_off_transmitted = false;
      if ((localModelOscillator instanceof ModelWavetable)) {
        try
        {
          this.resampler.open((ModelWavetable)localModelOscillator, this.synthesizer.getFormat().getSampleRate());
          this.osc_stream = this.resampler;
        }
        catch (IOException localIOException2) {}
      } else {
        this.osc_stream = localModelOscillator.open(this.synthesizer.getFormat().getSampleRate());
      }
      this.osc_attenuation = localModelOscillator.getAttenuation();
      this.osc_stream_nrofchannels = localModelOscillator.getChannels();
      if ((this.osc_buff == null) || (this.osc_buff.length < this.osc_stream_nrofchannels)) {
        this.osc_buff = new float[this.osc_stream_nrofchannels][];
      }
      if (this.osc_stream != null) {
        this.osc_stream.noteOn(this.softchannel, this, this.noteOn_noteNumber, this.noteOn_velocity);
      }
    }
    if (this.audiostarted)
    {
      if (this.portamento)
      {
        double d1 = this.tunedKey - this.co_noteon_keynumber[0] * 128.0D;
        double d3 = Math.abs(d1);
        if (d3 < 1.0E-10D)
        {
          this.co_noteon_keynumber[0] = (this.tunedKey * 0.0078125D);
          this.portamento = false;
        }
        else
        {
          if (d3 > this.softchannel.portamento_time) {
            d1 = Math.signum(d1) * this.softchannel.portamento_time;
          }
          this.co_noteon_keynumber[0] += d1 * 0.0078125D;
        }
        int[] arrayOfInt = this.performer.midi_connections[4];
        if (arrayOfInt == null) {
          return;
        }
        for (int j = 0; j < arrayOfInt.length; j++) {
          processConnection(arrayOfInt[j]);
        }
      }
      this.eg.processControlLogic();
      this.lfo.processControlLogic();
      for (int i = 0; i < this.performer.ctrl_connections.length; i++) {
        processConnection(this.performer.ctrl_connections[i]);
      }
      this.osc_stream.setPitch((float)this.co_osc_pitch[0]);
      i = (int)this.co_filter_type[0];
      double d2;
      if (this.co_filter_freq[0] == 13500.0D) {
        d2 = 19912.126958213175D;
      } else {
        d2 = 440.0D * Math.exp((this.co_filter_freq[0] - 6900.0D) * (Math.log(2.0D) / 1200.0D));
      }
      double d4 = this.co_filter_q[0] / 10.0D;
      this.filter_left.setFilterType(i);
      this.filter_left.setFrequency(d2);
      this.filter_left.setResonance(d4);
      this.filter_right.setFilterType(i);
      this.filter_right.setFrequency(d2);
      this.filter_right.setResonance(d4);
      float f = (float)Math.exp((-this.osc_attenuation + this.co_mixer_gain[0]) * (Math.log(10.0D) / 200.0D));
      if (this.co_mixer_gain[0] <= -960.0D) {
        f = 0.0F;
      }
      if (this.soundoff)
      {
        this.stopping = true;
        f = 0.0F;
      }
      this.volume = ((int)(Math.sqrt(f) * 128.0D));
      double d5 = this.co_mixer_pan[0] * 0.001D;
      if (d5 < 0.0D) {
        d5 = 0.0D;
      } else if (d5 > 1.0D) {
        d5 = 1.0D;
      }
      if (d5 == 0.5D)
      {
        this.out_mixer_left = (f * 0.70710677F);
        this.out_mixer_right = this.out_mixer_left;
      }
      else
      {
        this.out_mixer_left = (f * (float)Math.cos(d5 * 3.141592653589793D * 0.5D));
        this.out_mixer_right = (f * (float)Math.sin(d5 * 3.141592653589793D * 0.5D));
      }
      double d6 = this.co_mixer_balance[0] * 0.001D;
      if (d6 != 0.5D) {
        if (d6 > 0.5D) {
          this.out_mixer_left = ((float)(this.out_mixer_left * ((1.0D - d6) * 2.0D)));
        } else {
          this.out_mixer_right = ((float)(this.out_mixer_right * (d6 * 2.0D)));
        }
      }
      if (this.synthesizer.reverb_on)
      {
        this.out_mixer_effect1 = ((float)(this.co_mixer_reverb[0] * 0.001D));
        this.out_mixer_effect1 *= f;
      }
      else
      {
        this.out_mixer_effect1 = 0.0F;
      }
      if (this.synthesizer.chorus_on)
      {
        this.out_mixer_effect2 = ((float)(this.co_mixer_chorus[0] * 0.001D));
        this.out_mixer_effect2 *= f;
      }
      else
      {
        this.out_mixer_effect2 = 0.0F;
      }
      this.out_mixer_end = (this.co_mixer_active[0] < 0.5D);
      if ((!this.on) && (!this.osc_stream_off_transmitted))
      {
        this.osc_stream_off_transmitted = true;
        if (this.osc_stream != null) {
          this.osc_stream.noteOff(this.noteOff_velocity);
        }
      }
    }
    if (this.started)
    {
      this.last_out_mixer_left = this.out_mixer_left;
      this.last_out_mixer_right = this.out_mixer_right;
      this.last_out_mixer_effect1 = this.out_mixer_effect1;
      this.last_out_mixer_effect2 = this.out_mixer_effect2;
      this.started = false;
    }
  }
  
  void mixAudioStream(SoftAudioBuffer paramSoftAudioBuffer1, SoftAudioBuffer paramSoftAudioBuffer2, SoftAudioBuffer paramSoftAudioBuffer3, float paramFloat1, float paramFloat2)
  {
    int i = paramSoftAudioBuffer1.getSize();
    if ((paramFloat1 < 1.0E-9D) && (paramFloat2 < 1.0E-9D)) {
      return;
    }
    float[] arrayOfFloat7;
    int n;
    if ((paramSoftAudioBuffer3 != null) && (this.delay != 0))
    {
      if (paramFloat1 == paramFloat2)
      {
        float[] arrayOfFloat1 = paramSoftAudioBuffer2.array();
        float[] arrayOfFloat3 = paramSoftAudioBuffer1.array();
        int j = 0;
        for (int m = this.delay; m < i; m++) {
          arrayOfFloat1[m] += arrayOfFloat3[(j++)] * paramFloat2;
        }
        arrayOfFloat1 = paramSoftAudioBuffer3.array();
        for (m = 0; m < this.delay; m++) {
          arrayOfFloat1[m] += arrayOfFloat3[(j++)] * paramFloat2;
        }
      }
      else
      {
        float f1 = paramFloat1;
        float f3 = (paramFloat2 - paramFloat1) / i;
        float[] arrayOfFloat5 = paramSoftAudioBuffer2.array();
        arrayOfFloat7 = paramSoftAudioBuffer1.array();
        n = 0;
        for (int i1 = this.delay; i1 < i; i1++)
        {
          f1 += f3;
          arrayOfFloat5[i1] += arrayOfFloat7[(n++)] * f1;
        }
        arrayOfFloat5 = paramSoftAudioBuffer3.array();
        for (i1 = 0; i1 < this.delay; i1++)
        {
          f1 += f3;
          arrayOfFloat5[i1] += arrayOfFloat7[(n++)] * f1;
        }
      }
    }
    else if (paramFloat1 == paramFloat2)
    {
      float[] arrayOfFloat2 = paramSoftAudioBuffer2.array();
      float[] arrayOfFloat4 = paramSoftAudioBuffer1.array();
      for (int k = 0; k < i; k++) {
        arrayOfFloat2[k] += arrayOfFloat4[k] * paramFloat2;
      }
    }
    else
    {
      float f2 = paramFloat1;
      float f4 = (paramFloat2 - paramFloat1) / i;
      float[] arrayOfFloat6 = paramSoftAudioBuffer2.array();
      arrayOfFloat7 = paramSoftAudioBuffer1.array();
      for (n = 0; n < i; n++)
      {
        f2 += f4;
        arrayOfFloat6[n] += arrayOfFloat7[n] * f2;
      }
    }
  }
  
  void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer)
  {
    if (!this.audiostarted) {
      return;
    }
    int i = paramArrayOfSoftAudioBuffer[0].getSize();
    try
    {
      this.osc_buff[0] = paramArrayOfSoftAudioBuffer[10].array();
      if (this.nrofchannels != 1) {
        this.osc_buff[1] = paramArrayOfSoftAudioBuffer[11].array();
      }
      int j = this.osc_stream.read(this.osc_buff, 0, i);
      if (j == -1)
      {
        this.stopping = true;
        return;
      }
      if (j != i)
      {
        Arrays.fill(this.osc_buff[0], j, i, 0.0F);
        if (this.nrofchannels != 1) {
          Arrays.fill(this.osc_buff[1], j, i, 0.0F);
        }
      }
    }
    catch (IOException localIOException) {}
    SoftAudioBuffer localSoftAudioBuffer1 = paramArrayOfSoftAudioBuffer[0];
    SoftAudioBuffer localSoftAudioBuffer2 = paramArrayOfSoftAudioBuffer[1];
    SoftAudioBuffer localSoftAudioBuffer3 = paramArrayOfSoftAudioBuffer[2];
    SoftAudioBuffer localSoftAudioBuffer4 = paramArrayOfSoftAudioBuffer[6];
    SoftAudioBuffer localSoftAudioBuffer5 = paramArrayOfSoftAudioBuffer[7];
    SoftAudioBuffer localSoftAudioBuffer6 = paramArrayOfSoftAudioBuffer[3];
    SoftAudioBuffer localSoftAudioBuffer7 = paramArrayOfSoftAudioBuffer[4];
    SoftAudioBuffer localSoftAudioBuffer8 = paramArrayOfSoftAudioBuffer[5];
    SoftAudioBuffer localSoftAudioBuffer9 = paramArrayOfSoftAudioBuffer[8];
    SoftAudioBuffer localSoftAudioBuffer10 = paramArrayOfSoftAudioBuffer[9];
    SoftAudioBuffer localSoftAudioBuffer11 = paramArrayOfSoftAudioBuffer[10];
    SoftAudioBuffer localSoftAudioBuffer12 = paramArrayOfSoftAudioBuffer[11];
    if (this.osc_stream_nrofchannels == 1) {
      localSoftAudioBuffer12 = null;
    }
    if (!Double.isInfinite(this.co_filter_freq[0]))
    {
      this.filter_left.processAudio(localSoftAudioBuffer11);
      if (localSoftAudioBuffer12 != null) {
        this.filter_right.processAudio(localSoftAudioBuffer12);
      }
    }
    if (this.nrofchannels == 1)
    {
      this.out_mixer_left = ((this.out_mixer_left + this.out_mixer_right) / 2.0F);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer1, localSoftAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
      if (localSoftAudioBuffer12 != null) {
        mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer1, localSoftAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
      }
    }
    else if ((localSoftAudioBuffer12 == null) && (this.last_out_mixer_left == this.last_out_mixer_right) && (this.out_mixer_left == this.out_mixer_right))
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer3, localSoftAudioBuffer8, this.last_out_mixer_left, this.out_mixer_left);
    }
    else
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer1, localSoftAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
      if (localSoftAudioBuffer12 != null) {
        mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer2, localSoftAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
      } else {
        mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer2, localSoftAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
      }
    }
    if (localSoftAudioBuffer12 == null)
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer4, localSoftAudioBuffer9, this.last_out_mixer_effect1, this.out_mixer_effect1);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer5, localSoftAudioBuffer10, this.last_out_mixer_effect2, this.out_mixer_effect2);
    }
    else
    {
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer4, localSoftAudioBuffer9, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
      mixAudioStream(localSoftAudioBuffer11, localSoftAudioBuffer5, localSoftAudioBuffer10, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
      mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer4, localSoftAudioBuffer9, this.last_out_mixer_effect1 * 0.5F, this.out_mixer_effect1 * 0.5F);
      mixAudioStream(localSoftAudioBuffer12, localSoftAudioBuffer5, localSoftAudioBuffer10, this.last_out_mixer_effect2 * 0.5F, this.out_mixer_effect2 * 0.5F);
    }
    this.last_out_mixer_left = this.out_mixer_left;
    this.last_out_mixer_right = this.out_mixer_right;
    this.last_out_mixer_effect1 = this.out_mixer_effect1;
    this.last_out_mixer_effect2 = this.out_mixer_effect2;
    if (this.out_mixer_end) {
      this.stopping = true;
    }
  }
}
