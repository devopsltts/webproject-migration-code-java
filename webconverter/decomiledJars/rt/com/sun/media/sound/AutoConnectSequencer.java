package com.sun.media.sound;

import javax.sound.midi.Receiver;

public abstract interface AutoConnectSequencer
{
  public abstract void setAutoConnect(Receiver paramReceiver);
}
