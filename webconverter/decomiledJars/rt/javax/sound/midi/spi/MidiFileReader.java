package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.Sequence;

public abstract class MidiFileReader
{
  public MidiFileReader() {}
  
  public abstract MidiFileFormat getMidiFileFormat(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException;
  
  public abstract MidiFileFormat getMidiFileFormat(URL paramURL)
    throws InvalidMidiDataException, IOException;
  
  public abstract MidiFileFormat getMidiFileFormat(File paramFile)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(URL paramURL)
    throws InvalidMidiDataException, IOException;
  
  public abstract Sequence getSequence(File paramFile)
    throws InvalidMidiDataException, IOException;
}
