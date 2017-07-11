package javax.sound.midi;

import com.sun.media.sound.AutoConnectSequencer;
import com.sun.media.sound.JDK13Services;
import com.sun.media.sound.MidiDeviceReceiverEnvelope;
import com.sun.media.sound.MidiDeviceTransmitterEnvelope;
import com.sun.media.sound.ReferenceCountingDevice;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.sound.midi.spi.MidiDeviceProvider;
import javax.sound.midi.spi.MidiFileReader;
import javax.sound.midi.spi.MidiFileWriter;
import javax.sound.midi.spi.SoundbankReader;

public class MidiSystem
{
  private MidiSystem() {}
  
  public static MidiDevice.Info[] getMidiDeviceInfo()
  {
    ArrayList localArrayList = new ArrayList();
    List localList = getMidiDeviceProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiDeviceProvider localMidiDeviceProvider = (MidiDeviceProvider)localList.get(i);
      MidiDevice.Info[] arrayOfInfo2 = localMidiDeviceProvider.getDeviceInfo();
      for (int j = 0; j < arrayOfInfo2.length; j++) {
        localArrayList.add(arrayOfInfo2[j]);
      }
    }
    MidiDevice.Info[] arrayOfInfo1 = (MidiDevice.Info[])localArrayList.toArray(new MidiDevice.Info[0]);
    return arrayOfInfo1;
  }
  
  public static MidiDevice getMidiDevice(MidiDevice.Info paramInfo)
    throws MidiUnavailableException
  {
    List localList = getMidiDeviceProviders();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiDeviceProvider localMidiDeviceProvider = (MidiDeviceProvider)localList.get(i);
      if (localMidiDeviceProvider.isDeviceSupported(paramInfo))
      {
        MidiDevice localMidiDevice = localMidiDeviceProvider.getDevice(paramInfo);
        return localMidiDevice;
      }
    }
    throw new IllegalArgumentException("Requested device not installed: " + paramInfo);
  }
  
  public static Receiver getReceiver()
    throws MidiUnavailableException
  {
    MidiDevice localMidiDevice = getDefaultDeviceWrapper(Receiver.class);
    Object localObject;
    if ((localMidiDevice instanceof ReferenceCountingDevice)) {
      localObject = ((ReferenceCountingDevice)localMidiDevice).getReceiverReferenceCounting();
    } else {
      localObject = localMidiDevice.getReceiver();
    }
    if (!(localObject instanceof MidiDeviceReceiver)) {
      localObject = new MidiDeviceReceiverEnvelope(localMidiDevice, (Receiver)localObject);
    }
    return localObject;
  }
  
  public static Transmitter getTransmitter()
    throws MidiUnavailableException
  {
    MidiDevice localMidiDevice = getDefaultDeviceWrapper(Transmitter.class);
    Object localObject;
    if ((localMidiDevice instanceof ReferenceCountingDevice)) {
      localObject = ((ReferenceCountingDevice)localMidiDevice).getTransmitterReferenceCounting();
    } else {
      localObject = localMidiDevice.getTransmitter();
    }
    if (!(localObject instanceof MidiDeviceTransmitter)) {
      localObject = new MidiDeviceTransmitterEnvelope(localMidiDevice, (Transmitter)localObject);
    }
    return localObject;
  }
  
  public static Synthesizer getSynthesizer()
    throws MidiUnavailableException
  {
    return (Synthesizer)getDefaultDeviceWrapper(Synthesizer.class);
  }
  
  public static Sequencer getSequencer()
    throws MidiUnavailableException
  {
    return getSequencer(true);
  }
  
  public static Sequencer getSequencer(boolean paramBoolean)
    throws MidiUnavailableException
  {
    Sequencer localSequencer = (Sequencer)getDefaultDeviceWrapper(Sequencer.class);
    if (paramBoolean)
    {
      Receiver localReceiver = null;
      Object localObject1 = null;
      try
      {
        Synthesizer localSynthesizer = getSynthesizer();
        if ((localSynthesizer instanceof ReferenceCountingDevice))
        {
          localReceiver = ((ReferenceCountingDevice)localSynthesizer).getReceiverReferenceCounting();
        }
        else
        {
          localSynthesizer.open();
          try
          {
            localReceiver = localSynthesizer.getReceiver();
          }
          finally
          {
            if (localReceiver == null) {
              localSynthesizer.close();
            }
          }
        }
      }
      catch (MidiUnavailableException localMidiUnavailableException)
      {
        if ((localMidiUnavailableException instanceof MidiUnavailableException)) {
          localObject1 = localMidiUnavailableException;
        }
      }
      if (localReceiver == null) {
        try
        {
          localReceiver = getReceiver();
        }
        catch (Exception localException)
        {
          if ((localException instanceof MidiUnavailableException)) {
            localObject1 = (MidiUnavailableException)localException;
          }
        }
      }
      if (localReceiver != null)
      {
        localSequencer.getTransmitter().setReceiver(localReceiver);
        if ((localSequencer instanceof AutoConnectSequencer)) {
          ((AutoConnectSequencer)localSequencer).setAutoConnect(localReceiver);
        }
      }
      else
      {
        if (localObject1 != null) {
          throw ((Throwable)localObject1);
        }
        throw new MidiUnavailableException("no receiver available");
      }
    }
    return localSequencer;
  }
  
  public static Soundbank getSoundbank(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    SoundbankReader localSoundbankReader = null;
    Soundbank localSoundbank = null;
    List localList = getSoundbankReaders();
    for (int i = 0; i < localList.size(); i++)
    {
      localSoundbankReader = (SoundbankReader)localList.get(i);
      localSoundbank = localSoundbankReader.getSoundbank(paramInputStream);
      if (localSoundbank != null) {
        return localSoundbank;
      }
    }
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static Soundbank getSoundbank(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    SoundbankReader localSoundbankReader = null;
    Soundbank localSoundbank = null;
    List localList = getSoundbankReaders();
    for (int i = 0; i < localList.size(); i++)
    {
      localSoundbankReader = (SoundbankReader)localList.get(i);
      localSoundbank = localSoundbankReader.getSoundbank(paramURL);
      if (localSoundbank != null) {
        return localSoundbank;
      }
    }
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static Soundbank getSoundbank(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    SoundbankReader localSoundbankReader = null;
    Soundbank localSoundbank = null;
    List localList = getSoundbankReaders();
    for (int i = 0; i < localList.size(); i++)
    {
      localSoundbankReader = (SoundbankReader)localList.get(i);
      localSoundbank = localSoundbankReader.getSoundbank(paramFile);
      if (localSoundbank != null) {
        return localSoundbank;
      }
    }
    throw new InvalidMidiDataException("cannot get soundbank from stream");
  }
  
  public static MidiFileFormat getMidiFileFormat(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    MidiFileFormat localMidiFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localMidiFileFormat = localMidiFileReader.getMidiFileFormat(paramInputStream);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localMidiFileFormat == null) {
      throw new InvalidMidiDataException("input stream is not a supported file type");
    }
    return localMidiFileFormat;
  }
  
  public static MidiFileFormat getMidiFileFormat(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    MidiFileFormat localMidiFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localMidiFileFormat = localMidiFileReader.getMidiFileFormat(paramURL);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localMidiFileFormat == null) {
      throw new InvalidMidiDataException("url is not a supported file type");
    }
    return localMidiFileFormat;
  }
  
  public static MidiFileFormat getMidiFileFormat(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    MidiFileFormat localMidiFileFormat = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localMidiFileFormat = localMidiFileReader.getMidiFileFormat(paramFile);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localMidiFileFormat == null) {
      throw new InvalidMidiDataException("file is not a supported file type");
    }
    return localMidiFileFormat;
  }
  
  public static Sequence getSequence(InputStream paramInputStream)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    Sequence localSequence = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localSequence = localMidiFileReader.getSequence(paramInputStream);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localSequence == null) {
      throw new InvalidMidiDataException("could not get sequence from input stream");
    }
    return localSequence;
  }
  
  public static Sequence getSequence(URL paramURL)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    Sequence localSequence = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localSequence = localMidiFileReader.getSequence(paramURL);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localSequence == null) {
      throw new InvalidMidiDataException("could not get sequence from URL");
    }
    return localSequence;
  }
  
  public static Sequence getSequence(File paramFile)
    throws InvalidMidiDataException, IOException
  {
    List localList = getMidiFileReaders();
    Sequence localSequence = null;
    int i = 0;
    while (i < localList.size())
    {
      MidiFileReader localMidiFileReader = (MidiFileReader)localList.get(i);
      try
      {
        localSequence = localMidiFileReader.getSequence(paramFile);
      }
      catch (InvalidMidiDataException localInvalidMidiDataException)
      {
        i++;
      }
    }
    if (localSequence == null) {
      throw new InvalidMidiDataException("could not get sequence from file");
    }
    return localSequence;
  }
  
  public static int[] getMidiFileTypes()
  {
    List localList = getMidiFileWriters();
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(i);
      localObject = localMidiFileWriter.getMidiFileTypes();
      for (int k = 0; k < localObject.length; k++) {
        localHashSet.add(new Integer(localObject[k]));
      }
    }
    int[] arrayOfInt = new int[localHashSet.size()];
    int j = 0;
    Object localObject = localHashSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject).next();
      arrayOfInt[(j++)] = localInteger.intValue();
    }
    return arrayOfInt;
  }
  
  public static boolean isFileTypeSupported(int paramInt)
  {
    List localList = getMidiFileWriters();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(i);
      if (localMidiFileWriter.isFileTypeSupported(paramInt)) {
        return true;
      }
    }
    return false;
  }
  
  public static int[] getMidiFileTypes(Sequence paramSequence)
  {
    List localList = getMidiFileWriters();
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(i);
      localObject = localMidiFileWriter.getMidiFileTypes(paramSequence);
      for (int k = 0; k < localObject.length; k++) {
        localHashSet.add(new Integer(localObject[k]));
      }
    }
    int[] arrayOfInt = new int[localHashSet.size()];
    int j = 0;
    Object localObject = localHashSet.iterator();
    while (((Iterator)localObject).hasNext())
    {
      Integer localInteger = (Integer)((Iterator)localObject).next();
      arrayOfInt[(j++)] = localInteger.intValue();
    }
    return arrayOfInt;
  }
  
  public static boolean isFileTypeSupported(int paramInt, Sequence paramSequence)
  {
    List localList = getMidiFileWriters();
    for (int i = 0; i < localList.size(); i++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(i);
      if (localMidiFileWriter.isFileTypeSupported(paramInt, paramSequence)) {
        return true;
      }
    }
    return false;
  }
  
  public static int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    List localList = getMidiFileWriters();
    int i = -2;
    for (int j = 0; j < localList.size(); j++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(j);
      if (localMidiFileWriter.isFileTypeSupported(paramInt, paramSequence))
      {
        i = localMidiFileWriter.write(paramSequence, paramInt, paramOutputStream);
        break;
      }
    }
    if (i == -2) {
      throw new IllegalArgumentException("MIDI file type is not supported");
    }
    return i;
  }
  
  public static int write(Sequence paramSequence, int paramInt, File paramFile)
    throws IOException
  {
    List localList = getMidiFileWriters();
    int i = -2;
    for (int j = 0; j < localList.size(); j++)
    {
      MidiFileWriter localMidiFileWriter = (MidiFileWriter)localList.get(j);
      if (localMidiFileWriter.isFileTypeSupported(paramInt, paramSequence))
      {
        i = localMidiFileWriter.write(paramSequence, paramInt, paramFile);
        break;
      }
    }
    if (i == -2) {
      throw new IllegalArgumentException("MIDI file type is not supported");
    }
    return i;
  }
  
  private static List getMidiDeviceProviders()
  {
    return getProviders(MidiDeviceProvider.class);
  }
  
  private static List getSoundbankReaders()
  {
    return getProviders(SoundbankReader.class);
  }
  
  private static List getMidiFileWriters()
  {
    return getProviders(MidiFileWriter.class);
  }
  
  private static List getMidiFileReaders()
  {
    return getProviders(MidiFileReader.class);
  }
  
  private static MidiDevice getDefaultDeviceWrapper(Class paramClass)
    throws MidiUnavailableException
  {
    try
    {
      return getDefaultDevice(paramClass);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      MidiUnavailableException localMidiUnavailableException = new MidiUnavailableException();
      localMidiUnavailableException.initCause(localIllegalArgumentException);
      throw localMidiUnavailableException;
    }
  }
  
  private static MidiDevice getDefaultDevice(Class paramClass)
  {
    List localList = getMidiDeviceProviders();
    String str1 = JDK13Services.getDefaultProviderClassName(paramClass);
    String str2 = JDK13Services.getDefaultInstanceName(paramClass);
    if (str1 != null)
    {
      MidiDeviceProvider localMidiDeviceProvider = getNamedProvider(str1, localList);
      if (localMidiDeviceProvider != null)
      {
        if (str2 != null)
        {
          localMidiDevice = getNamedDevice(str2, localMidiDeviceProvider, paramClass);
          if (localMidiDevice != null) {
            return localMidiDevice;
          }
        }
        localMidiDevice = getFirstDevice(localMidiDeviceProvider, paramClass);
        if (localMidiDevice != null) {
          return localMidiDevice;
        }
      }
    }
    if (str2 != null)
    {
      localMidiDevice = getNamedDevice(str2, localList, paramClass);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    MidiDevice localMidiDevice = getFirstDevice(localList, paramClass);
    if (localMidiDevice != null) {
      return localMidiDevice;
    }
    throw new IllegalArgumentException("Requested device not installed");
  }
  
  private static MidiDeviceProvider getNamedProvider(String paramString, List paramList)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      MidiDeviceProvider localMidiDeviceProvider = (MidiDeviceProvider)paramList.get(i);
      if (localMidiDeviceProvider.getClass().getName().equals(paramString)) {
        return localMidiDeviceProvider;
      }
    }
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, MidiDeviceProvider paramMidiDeviceProvider, Class paramClass)
  {
    MidiDevice localMidiDevice = getNamedDevice(paramString, paramMidiDeviceProvider, paramClass, false, false);
    if (localMidiDevice != null) {
      return localMidiDevice;
    }
    if (paramClass == Receiver.class)
    {
      localMidiDevice = getNamedDevice(paramString, paramMidiDeviceProvider, paramClass, true, false);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, MidiDeviceProvider paramMidiDeviceProvider, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    MidiDevice.Info[] arrayOfInfo = paramMidiDeviceProvider.getDeviceInfo();
    for (int i = 0; i < arrayOfInfo.length; i++) {
      if (arrayOfInfo[i].getName().equals(paramString))
      {
        MidiDevice localMidiDevice = paramMidiDeviceProvider.getDevice(arrayOfInfo[i]);
        if (isAppropriateDevice(localMidiDevice, paramClass, paramBoolean1, paramBoolean2)) {
          return localMidiDevice;
        }
      }
    }
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, List paramList, Class paramClass)
  {
    MidiDevice localMidiDevice = getNamedDevice(paramString, paramList, paramClass, false, false);
    if (localMidiDevice != null) {
      return localMidiDevice;
    }
    if (paramClass == Receiver.class)
    {
      localMidiDevice = getNamedDevice(paramString, paramList, paramClass, true, false);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getNamedDevice(String paramString, List paramList, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      MidiDeviceProvider localMidiDeviceProvider = (MidiDeviceProvider)paramList.get(i);
      MidiDevice localMidiDevice = getNamedDevice(paramString, localMidiDeviceProvider, paramClass, paramBoolean1, paramBoolean2);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getFirstDevice(MidiDeviceProvider paramMidiDeviceProvider, Class paramClass)
  {
    MidiDevice localMidiDevice = getFirstDevice(paramMidiDeviceProvider, paramClass, false, false);
    if (localMidiDevice != null) {
      return localMidiDevice;
    }
    if (paramClass == Receiver.class)
    {
      localMidiDevice = getFirstDevice(paramMidiDeviceProvider, paramClass, true, false);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getFirstDevice(MidiDeviceProvider paramMidiDeviceProvider, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    MidiDevice.Info[] arrayOfInfo = paramMidiDeviceProvider.getDeviceInfo();
    for (int i = 0; i < arrayOfInfo.length; i++)
    {
      MidiDevice localMidiDevice = paramMidiDeviceProvider.getDevice(arrayOfInfo[i]);
      if (isAppropriateDevice(localMidiDevice, paramClass, paramBoolean1, paramBoolean2)) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getFirstDevice(List paramList, Class paramClass)
  {
    MidiDevice localMidiDevice = getFirstDevice(paramList, paramClass, false, false);
    if (localMidiDevice != null) {
      return localMidiDevice;
    }
    if (paramClass == Receiver.class)
    {
      localMidiDevice = getFirstDevice(paramList, paramClass, true, false);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static MidiDevice getFirstDevice(List paramList, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      MidiDeviceProvider localMidiDeviceProvider = (MidiDeviceProvider)paramList.get(i);
      MidiDevice localMidiDevice = getFirstDevice(localMidiDeviceProvider, paramClass, paramBoolean1, paramBoolean2);
      if (localMidiDevice != null) {
        return localMidiDevice;
      }
    }
    return null;
  }
  
  private static boolean isAppropriateDevice(MidiDevice paramMidiDevice, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramClass.isInstance(paramMidiDevice)) {
      return true;
    }
    return ((!(paramMidiDevice instanceof Sequencer)) && (!(paramMidiDevice instanceof Synthesizer))) || (((paramMidiDevice instanceof Sequencer)) && (paramBoolean2)) || (((paramMidiDevice instanceof Synthesizer)) && (paramBoolean1) && (((paramClass == Receiver.class) && (paramMidiDevice.getMaxReceivers() != 0)) || ((paramClass == Transmitter.class) && (paramMidiDevice.getMaxTransmitters() != 0))));
  }
  
  private static List getProviders(Class paramClass)
  {
    return JDK13Services.getProviders(paramClass);
  }
}
