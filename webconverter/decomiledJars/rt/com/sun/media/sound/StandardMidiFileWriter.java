package com.sun.media.sound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.SequenceInputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileWriter;

public final class StandardMidiFileWriter
  extends MidiFileWriter
{
  private static final int MThd_MAGIC = 1297377380;
  private static final int MTrk_MAGIC = 1297379947;
  private static final int ONE_BYTE = 1;
  private static final int TWO_BYTE = 2;
  private static final int SYSEX = 3;
  private static final int META = 4;
  private static final int ERROR = 5;
  private static final int IGNORE = 6;
  private static final int MIDI_TYPE_0 = 0;
  private static final int MIDI_TYPE_1 = 1;
  private static final int bufferSize = 16384;
  private DataOutputStream tddos;
  private static final int[] types = { 0, 1 };
  private static final long mask = 127L;
  
  public StandardMidiFileWriter() {}
  
  public int[] getMidiFileTypes()
  {
    int[] arrayOfInt = new int[types.length];
    System.arraycopy(types, 0, arrayOfInt, 0, types.length);
    return arrayOfInt;
  }
  
  public int[] getMidiFileTypes(Sequence paramSequence)
  {
    Track[] arrayOfTrack = paramSequence.getTracks();
    int[] arrayOfInt;
    if (arrayOfTrack.length == 1)
    {
      arrayOfInt = new int[2];
      arrayOfInt[0] = 0;
      arrayOfInt[1] = 1;
    }
    else
    {
      arrayOfInt = new int[1];
      arrayOfInt[0] = 1;
    }
    return arrayOfInt;
  }
  
  public boolean isFileTypeSupported(int paramInt)
  {
    for (int i = 0; i < types.length; i++) {
      if (paramInt == types[i]) {
        return true;
      }
    }
    return false;
  }
  
  public int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = null;
    int i = 0;
    long l = 0L;
    if (!isFileTypeSupported(paramInt, paramSequence)) {
      throw new IllegalArgumentException("Could not write MIDI file");
    }
    InputStream localInputStream = getFileStream(paramInt, paramSequence);
    if (localInputStream == null) {
      throw new IllegalArgumentException("Could not write MIDI file");
    }
    arrayOfByte = new byte['䀀'];
    while ((i = localInputStream.read(arrayOfByte)) >= 0)
    {
      paramOutputStream.write(arrayOfByte, 0, i);
      l += i;
    }
    return (int)l;
  }
  
  public int write(Sequence paramSequence, int paramInt, File paramFile)
    throws IOException
  {
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
    int i = write(paramSequence, paramInt, localFileOutputStream);
    localFileOutputStream.close();
    return i;
  }
  
  private InputStream getFileStream(int paramInt, Sequence paramSequence)
    throws IOException
  {
    Track[] arrayOfTrack = paramSequence.getTracks();
    int i = 0;
    int j = 14;
    int k = 0;
    PipedOutputStream localPipedOutputStream = null;
    DataOutputStream localDataOutputStream = null;
    PipedInputStream localPipedInputStream = null;
    InputStream[] arrayOfInputStream = null;
    Object localObject = null;
    SequenceInputStream localSequenceInputStream = null;
    if (paramInt == 0)
    {
      if (arrayOfTrack.length != 1) {
        return null;
      }
    }
    else if (paramInt == 1)
    {
      if (arrayOfTrack.length < 1) {
        return null;
      }
    }
    else if (arrayOfTrack.length == 1) {
      paramInt = 0;
    } else if (arrayOfTrack.length > 1) {
      paramInt = 1;
    } else {
      return null;
    }
    arrayOfInputStream = new InputStream[arrayOfTrack.length];
    int n = 0;
    for (int i1 = 0; i1 < arrayOfTrack.length; i1++) {
      try
      {
        arrayOfInputStream[n] = writeTrack(arrayOfTrack[i1], paramInt);
        n++;
      }
      catch (InvalidMidiDataException localInvalidMidiDataException) {}
    }
    if (n == 1)
    {
      localObject = arrayOfInputStream[0];
    }
    else if (n > 1)
    {
      localObject = arrayOfInputStream[0];
      for (i1 = 1; i1 < arrayOfTrack.length; i1++) {
        if (arrayOfInputStream[i1] != null) {
          localObject = new SequenceInputStream((InputStream)localObject, arrayOfInputStream[i1]);
        }
      }
    }
    else
    {
      throw new IllegalArgumentException("invalid MIDI data in sequence");
    }
    localPipedOutputStream = new PipedOutputStream();
    localDataOutputStream = new DataOutputStream(localPipedOutputStream);
    localPipedInputStream = new PipedInputStream(localPipedOutputStream);
    localDataOutputStream.writeInt(1297377380);
    localDataOutputStream.writeInt(j - 8);
    if (paramInt == 0) {
      localDataOutputStream.writeShort(0);
    } else {
      localDataOutputStream.writeShort(1);
    }
    localDataOutputStream.writeShort((short)n);
    float f = paramSequence.getDivisionType();
    int m;
    if (f == 0.0F)
    {
      m = paramSequence.getResolution();
    }
    else if (f == 24.0F)
    {
      m = 59392;
      m += (paramSequence.getResolution() & 0xFF);
    }
    else if (f == 25.0F)
    {
      m = 59136;
      m += (paramSequence.getResolution() & 0xFF);
    }
    else if (f == 29.97F)
    {
      m = 58112;
      m += (paramSequence.getResolution() & 0xFF);
    }
    else if (f == 30.0F)
    {
      m = 57856;
      m += (paramSequence.getResolution() & 0xFF);
    }
    else
    {
      return null;
    }
    localDataOutputStream.writeShort(m);
    localSequenceInputStream = new SequenceInputStream(localPipedInputStream, (InputStream)localObject);
    localDataOutputStream.close();
    k = i + j;
    return localSequenceInputStream;
  }
  
  private int getType(int paramInt)
  {
    if ((paramInt & 0xF0) == 240)
    {
      switch (paramInt)
      {
      case 240: 
      case 247: 
        return 3;
      case 255: 
        return 4;
      }
      return 6;
    }
    switch (paramInt & 0xF0)
    {
    case 128: 
    case 144: 
    case 160: 
    case 176: 
    case 224: 
      return 2;
    case 192: 
    case 208: 
      return 1;
    }
    return 5;
  }
  
  private int writeVarInt(long paramLong)
    throws IOException
  {
    int i = 1;
    for (int j = 63; (j > 0) && ((paramLong & 127L << j) == 0L); j -= 7) {}
    while (j > 0)
    {
      this.tddos.writeByte((int)((paramLong & 127L << j) >> j | 0x80));
      j -= 7;
      i++;
    }
    this.tddos.writeByte((int)(paramLong & 0x7F));
    return i;
  }
  
  private InputStream writeTrack(Track paramTrack, int paramInt)
    throws IOException, InvalidMidiDataException
  {
    int i = 0;
    int j = 0;
    int k = paramTrack.size();
    PipedOutputStream localPipedOutputStream = new PipedOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localPipedOutputStream);
    PipedInputStream localPipedInputStream = new PipedInputStream(localPipedOutputStream);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    this.tddos = new DataOutputStream(localByteArrayOutputStream);
    ByteArrayInputStream localByteArrayInputStream = null;
    SequenceInputStream localSequenceInputStream = null;
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int m = -1;
    for (int n = 0; n < k; n++)
    {
      MidiEvent localMidiEvent = paramTrack.get(n);
      byte[] arrayOfByte = null;
      ShortMessage localShortMessage = null;
      MetaMessage localMetaMessage = null;
      SysexMessage localSysexMessage = null;
      l3 = localMidiEvent.getTick();
      l2 = localMidiEvent.getTick() - l1;
      l1 = localMidiEvent.getTick();
      int i1 = localMidiEvent.getMessage().getStatus();
      int i2 = getType(i1);
      int i3;
      int i5;
      switch (i2)
      {
      case 1: 
        localShortMessage = (ShortMessage)localMidiEvent.getMessage();
        i3 = localShortMessage.getData1();
        i += writeVarInt(l2);
        if (i1 != m)
        {
          m = i1;
          this.tddos.writeByte(i1);
          i++;
        }
        this.tddos.writeByte(i3);
        i++;
        break;
      case 2: 
        localShortMessage = (ShortMessage)localMidiEvent.getMessage();
        i3 = localShortMessage.getData1();
        int i4 = localShortMessage.getData2();
        i += writeVarInt(l2);
        if (i1 != m)
        {
          m = i1;
          this.tddos.writeByte(i1);
          i++;
        }
        this.tddos.writeByte(i3);
        i++;
        this.tddos.writeByte(i4);
        i++;
        break;
      case 3: 
        localSysexMessage = (SysexMessage)localMidiEvent.getMessage();
        i5 = localSysexMessage.getLength();
        arrayOfByte = localSysexMessage.getMessage();
        i += writeVarInt(l2);
        m = i1;
        this.tddos.writeByte(arrayOfByte[0]);
        i++;
        i += writeVarInt(arrayOfByte.length - 1);
        this.tddos.write(arrayOfByte, 1, arrayOfByte.length - 1);
        i += arrayOfByte.length - 1;
        break;
      case 4: 
        localMetaMessage = (MetaMessage)localMidiEvent.getMessage();
        i5 = localMetaMessage.getLength();
        arrayOfByte = localMetaMessage.getMessage();
        i += writeVarInt(l2);
        m = i1;
        this.tddos.write(arrayOfByte, 0, arrayOfByte.length);
        i += arrayOfByte.length;
        break;
      case 6: 
        break;
      case 5: 
        break;
      default: 
        throw new InvalidMidiDataException("internal file writer error");
      }
    }
    localDataOutputStream.writeInt(1297379947);
    localDataOutputStream.writeInt(i);
    i += 8;
    localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    localSequenceInputStream = new SequenceInputStream(localPipedInputStream, localByteArrayInputStream);
    localDataOutputStream.close();
    this.tddos.close();
    return localSequenceInputStream;
  }
}
