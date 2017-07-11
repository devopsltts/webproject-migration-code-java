package com.sun.media.sound;

import java.util.Map;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public abstract interface AudioSynthesizer
  extends Synthesizer
{
  public abstract AudioFormat getFormat();
  
  public abstract AudioSynthesizerPropertyInfo[] getPropertyInfo(Map<String, Object> paramMap);
  
  public abstract void open(SourceDataLine paramSourceDataLine, Map<String, Object> paramMap)
    throws MidiUnavailableException;
  
  public abstract AudioInputStream openStream(AudioFormat paramAudioFormat, Map<String, Object> paramMap)
    throws MidiUnavailableException;
}
