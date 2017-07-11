package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;

public final class SoftInstrument
  extends Instrument
{
  private SoftPerformer[] performers;
  private ModelPerformer[] modelperformers;
  private final Object data;
  private final ModelInstrument ins;
  
  public SoftInstrument(ModelInstrument paramModelInstrument)
  {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    this.data = paramModelInstrument.getData();
    this.ins = paramModelInstrument;
    initPerformers(paramModelInstrument.getPerformers());
  }
  
  public SoftInstrument(ModelInstrument paramModelInstrument, ModelPerformer[] paramArrayOfModelPerformer)
  {
    super(paramModelInstrument.getSoundbank(), paramModelInstrument.getPatch(), paramModelInstrument.getName(), paramModelInstrument.getDataClass());
    this.data = paramModelInstrument.getData();
    this.ins = paramModelInstrument;
    initPerformers(paramArrayOfModelPerformer);
  }
  
  private void initPerformers(ModelPerformer[] paramArrayOfModelPerformer)
  {
    this.modelperformers = paramArrayOfModelPerformer;
    this.performers = new SoftPerformer[paramArrayOfModelPerformer.length];
    for (int i = 0; i < paramArrayOfModelPerformer.length; i++) {
      this.performers[i] = new SoftPerformer(paramArrayOfModelPerformer[i]);
    }
  }
  
  public ModelDirector getDirector(MidiChannel paramMidiChannel, ModelDirectedPlayer paramModelDirectedPlayer)
  {
    return this.ins.getDirector(this.modelperformers, paramMidiChannel, paramModelDirectedPlayer);
  }
  
  public ModelInstrument getSourceInstrument()
  {
    return this.ins;
  }
  
  public Object getData()
  {
    return this.data;
  }
  
  public SoftPerformer getPerformer(int paramInt)
  {
    return this.performers[paramInt];
  }
}
