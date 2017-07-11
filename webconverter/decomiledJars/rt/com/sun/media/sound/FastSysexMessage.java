package com.sun.media.sound;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

final class FastSysexMessage
  extends SysexMessage
{
  FastSysexMessage(byte[] paramArrayOfByte)
    throws InvalidMidiDataException
  {
    super(paramArrayOfByte);
    if ((paramArrayOfByte.length == 0) || (((paramArrayOfByte[0] & 0xFF) != 240) && ((paramArrayOfByte[0] & 0xFF) != 247))) {
      super.setMessage(paramArrayOfByte, paramArrayOfByte.length);
    }
  }
  
  byte[] getReadOnlyMessage()
  {
    return this.data;
  }
  
  public void setMessage(byte[] paramArrayOfByte, int paramInt)
    throws InvalidMidiDataException
  {
    if ((paramArrayOfByte.length == 0) || (((paramArrayOfByte[0] & 0xFF) != 240) && ((paramArrayOfByte[0] & 0xFF) != 247))) {
      super.setMessage(paramArrayOfByte, paramArrayOfByte.length);
    }
    this.length = paramInt;
    this.data = new byte[this.length];
    System.arraycopy(paramArrayOfByte, 0, this.data, 0, paramInt);
  }
}
