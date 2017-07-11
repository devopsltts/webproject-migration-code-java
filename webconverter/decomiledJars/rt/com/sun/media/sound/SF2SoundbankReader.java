package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;

public final class SF2SoundbankReader
  extends SoundbankReader
{
  public SF2SoundbankReader() {}
  
  public Soundbank getSoundbank(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      return new SF2Soundbank(paramURL);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException)
    {
      return null;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      paramInputStream.mark(512);
      return new SF2Soundbank(paramInputStream);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException)
    {
      paramInputStream.reset();
    }
    return null;
  }
  
  public Soundbank getSoundbank(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    try
    {
      return new SF2Soundbank(paramFile);
    }
    catch (RIFFInvalidFormatException localRIFFInvalidFormatException) {}
    return null;
  }
}
