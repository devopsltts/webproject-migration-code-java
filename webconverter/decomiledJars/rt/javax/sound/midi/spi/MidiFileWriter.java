package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.midi.Sequence;

public abstract class MidiFileWriter
{
  public MidiFileWriter() {}
  
  public abstract int[] getMidiFileTypes();
  
  public abstract int[] getMidiFileTypes(Sequence paramSequence);
  
  public boolean isFileTypeSupported(int paramInt)
  {
    int[] arrayOfInt = getMidiFileTypes();
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (paramInt == arrayOfInt[i]) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isFileTypeSupported(int paramInt, Sequence paramSequence)
  {
    int[] arrayOfInt = getMidiFileTypes(paramSequence);
    for (int i = 0; i < arrayOfInt.length; i++) {
      if (paramInt == arrayOfInt[i]) {
        return true;
      }
    }
    return false;
  }
  
  public abstract int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract int write(Sequence paramSequence, int paramInt, File paramFile)
    throws IOException;
}
