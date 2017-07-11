package com.sun.media.sound;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

final class SMFParser
{
  private static final int MTrk_MAGIC = 1297379947;
  private static final boolean STRICT_PARSER = false;
  private static final boolean DEBUG = false;
  int tracks;
  DataInputStream stream;
  private int trackLength = 0;
  private byte[] trackData = null;
  private int pos = 0;
  
  SMFParser() {}
  
  private int readUnsigned()
    throws IOException
  {
    return this.trackData[(this.pos++)] & 0xFF;
  }
  
  private void read(byte[] paramArrayOfByte)
    throws IOException
  {
    System.arraycopy(this.trackData, this.pos, paramArrayOfByte, 0, paramArrayOfByte.length);
    this.pos += paramArrayOfByte.length;
  }
  
  private long readVarInt()
    throws IOException
  {
    long l = 0L;
    int i = 0;
    do
    {
      i = this.trackData[(this.pos++)] & 0xFF;
      l = (l << 7) + (i & 0x7F);
    } while ((i & 0x80) != 0);
    return l;
  }
  
  private int readIntFromStream()
    throws IOException
  {
    try
    {
      return this.stream.readInt();
    }
    catch (EOFException localEOFException)
    {
      throw new EOFException("invalid MIDI file");
    }
  }
  
  boolean nextTrack()
    throws IOException, InvalidMidiDataException
  {
    this.trackLength = 0;
    int i;
    do
    {
      if (this.stream.skipBytes(this.trackLength) != this.trackLength) {
        return false;
      }
      i = readIntFromStream();
      this.trackLength = readIntFromStream();
    } while (i != 1297379947);
    if (this.trackLength < 0) {
      return false;
    }
    try
    {
      this.trackData = new byte[this.trackLength];
    }
    catch (OutOfMemoryError localOutOfMemoryError)
    {
      throw new IOException("Track length too big", localOutOfMemoryError);
    }
    try
    {
      this.stream.readFully(this.trackData);
    }
    catch (EOFException localEOFException)
    {
      return false;
    }
    this.pos = 0;
    return true;
  }
  
  private boolean trackFinished()
  {
    return this.pos >= this.trackLength;
  }
  
  void readTrack(Track paramTrack)
    throws IOException, InvalidMidiDataException
  {
    try
    {
      long l = 0L;
      int i = 0;
      int j = 0;
      while ((!trackFinished()) && (j == 0))
      {
        int k = -1;
        int m = 0;
        l += readVarInt();
        int n = readUnsigned();
        if (n >= 128) {
          i = n;
        } else {
          k = n;
        }
        Object localObject;
        switch (i & 0xF0)
        {
        case 128: 
        case 144: 
        case 160: 
        case 176: 
        case 224: 
          if (k == -1) {
            k = readUnsigned();
          }
          m = readUnsigned();
          localObject = new FastShortMessage(i | k << 8 | m << 16);
          break;
        case 192: 
        case 208: 
          if (k == -1) {
            k = readUnsigned();
          }
          localObject = new FastShortMessage(i | k << 8);
          break;
        case 240: 
          switch (i)
          {
          case 240: 
          case 247: 
            int i1 = (int)readVarInt();
            byte[] arrayOfByte1 = new byte[i1];
            read(arrayOfByte1);
            SysexMessage localSysexMessage = new SysexMessage();
            localSysexMessage.setMessage(i, arrayOfByte1, i1);
            localObject = localSysexMessage;
            break;
          case 255: 
            int i2 = readUnsigned();
            int i3 = (int)readVarInt();
            byte[] arrayOfByte2;
            try
            {
              arrayOfByte2 = new byte[i3];
            }
            catch (OutOfMemoryError localOutOfMemoryError)
            {
              throw new IOException("Meta length too big", localOutOfMemoryError);
            }
            read(arrayOfByte2);
            MetaMessage localMetaMessage = new MetaMessage();
            localMetaMessage.setMessage(i2, arrayOfByte2, i3);
            localObject = localMetaMessage;
            if (i2 == 47) {
              j = 1;
            }
            break;
          default: 
            throw new InvalidMidiDataException("Invalid status byte: " + i);
          }
          break;
        default: 
          throw new InvalidMidiDataException("Invalid status byte: " + i);
        }
        paramTrack.add(new MidiEvent((MidiMessage)localObject, l));
      }
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      throw new EOFException("invalid MIDI file");
    }
  }
}
