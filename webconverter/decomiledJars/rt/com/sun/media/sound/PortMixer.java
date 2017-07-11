package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.BooleanControl.Type;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.CompoundControl.Type;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;

final class PortMixer
  extends AbstractMixer
{
  private static final int SRC_UNKNOWN = 1;
  private static final int SRC_MICROPHONE = 2;
  private static final int SRC_LINE_IN = 3;
  private static final int SRC_COMPACT_DISC = 4;
  private static final int SRC_MASK = 255;
  private static final int DST_UNKNOWN = 256;
  private static final int DST_SPEAKER = 512;
  private static final int DST_HEADPHONE = 768;
  private static final int DST_LINE_OUT = 1024;
  private static final int DST_MASK = 65280;
  private Port.Info[] portInfos;
  private PortMixerPort[] ports;
  private long id = 0L;
  
  PortMixer(PortMixerProvider.PortMixerInfo paramPortMixerInfo)
  {
    super(paramPortMixerInfo, null, null, null);
    int i = 0;
    int j = 0;
    int k = 0;
    try
    {
      try
      {
        this.id = nOpen(getMixerIndex());
        if (this.id != 0L)
        {
          i = nGetPortCount(this.id);
          if (i < 0) {
            i = 0;
          }
        }
      }
      catch (Exception localException) {}
      this.portInfos = new Port.Info[i];
      for (m = 0; m < i; m++)
      {
        int n = nGetPortType(this.id, m);
        j += ((n & 0xFF) != 0 ? 1 : 0);
        k += ((n & 0xFF00) != 0 ? 1 : 0);
        this.portInfos[m] = getPortInfo(m, n);
      }
    }
    finally
    {
      if (this.id != 0L) {
        nClose(this.id);
      }
      this.id = 0L;
    }
    this.sourceLineInfo = new Port.Info[j];
    this.targetLineInfo = new Port.Info[k];
    j = 0;
    k = 0;
    for (int m = 0; m < i; m++) {
      if (this.portInfos[m].isSource()) {
        this.sourceLineInfo[(j++)] = this.portInfos[m];
      } else {
        this.targetLineInfo[(k++)] = this.portInfos[m];
      }
    }
  }
  
  public Line getLine(Line.Info paramInfo)
    throws LineUnavailableException
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if ((localInfo != null) && ((localInfo instanceof Port.Info))) {
      for (int i = 0; i < this.portInfos.length; i++) {
        if (localInfo.equals(this.portInfos[i])) {
          return getPort(i);
        }
      }
    }
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo)
  {
    Line.Info localInfo = getLineInfo(paramInfo);
    if (localInfo == null) {
      return 0;
    }
    if ((localInfo instanceof Port.Info)) {
      return 1;
    }
    return 0;
  }
  
  protected void implOpen()
    throws LineUnavailableException
  {
    this.id = nOpen(getMixerIndex());
  }
  
  protected void implClose()
  {
    long l = this.id;
    this.id = 0L;
    nClose(l);
    if (this.ports != null) {
      for (int i = 0; i < this.ports.length; i++) {
        if (this.ports[i] != null) {
          this.ports[i].disposeControls();
        }
      }
    }
  }
  
  protected void implStart() {}
  
  protected void implStop() {}
  
  private Port.Info getPortInfo(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    case 1: 
      return new PortInfo(nGetPortName(getID(), paramInt1), true, null);
    case 2: 
      return Port.Info.MICROPHONE;
    case 3: 
      return Port.Info.LINE_IN;
    case 4: 
      return Port.Info.COMPACT_DISC;
    case 256: 
      return new PortInfo(nGetPortName(getID(), paramInt1), false, null);
    case 512: 
      return Port.Info.SPEAKER;
    case 768: 
      return Port.Info.HEADPHONE;
    case 1024: 
      return Port.Info.LINE_OUT;
    }
    return null;
  }
  
  int getMixerIndex()
  {
    return ((PortMixerProvider.PortMixerInfo)getMixerInfo()).getIndex();
  }
  
  Port getPort(int paramInt)
  {
    if (this.ports == null) {
      this.ports = new PortMixerPort[this.portInfos.length];
    }
    if (this.ports[paramInt] == null)
    {
      this.ports[paramInt] = new PortMixerPort(this.portInfos[paramInt], this, paramInt, null);
      return this.ports[paramInt];
    }
    return this.ports[paramInt];
  }
  
  long getID()
  {
    return this.id;
  }
  
  private static native long nOpen(int paramInt)
    throws LineUnavailableException;
  
  private static native void nClose(long paramLong);
  
  private static native int nGetPortCount(long paramLong);
  
  private static native int nGetPortType(long paramLong, int paramInt);
  
  private static native String nGetPortName(long paramLong, int paramInt);
  
  private static native void nGetControls(long paramLong, int paramInt, Vector paramVector);
  
  private static native void nControlSetIntValue(long paramLong, int paramInt);
  
  private static native int nControlGetIntValue(long paramLong);
  
  private static native void nControlSetFloatValue(long paramLong, float paramFloat);
  
  private static native float nControlGetFloatValue(long paramLong);
  
  private static final class BoolCtrl
    extends BooleanControl
  {
    private final long controlID;
    private boolean closed = false;
    
    private static BooleanControl.Type createType(String paramString)
    {
      if (paramString.equals("Mute")) {
        return BooleanControl.Type.MUTE;
      }
      if (paramString.equals("Select")) {}
      return new BCT(paramString, null);
    }
    
    private BoolCtrl(long paramLong, String paramString)
    {
      this(paramLong, createType(paramString));
    }
    
    private BoolCtrl(long paramLong, BooleanControl.Type paramType)
    {
      super(false);
      this.controlID = paramLong;
    }
    
    public void setValue(boolean paramBoolean)
    {
      if (!this.closed) {
        PortMixer.nControlSetIntValue(this.controlID, paramBoolean ? 1 : 0);
      }
    }
    
    public boolean getValue()
    {
      if (!this.closed) {
        return PortMixer.nControlGetIntValue(this.controlID) != 0;
      }
      return false;
    }
    
    private static final class BCT
      extends BooleanControl.Type
    {
      private BCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class CompCtrl
    extends CompoundControl
  {
    private CompCtrl(String paramString, Control[] paramArrayOfControl)
    {
      super(paramArrayOfControl);
    }
    
    private static final class CCT
      extends CompoundControl.Type
    {
      private CCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class FloatCtrl
    extends FloatControl
  {
    private final long controlID;
    private boolean closed = false;
    private static final FloatControl.Type[] FLOAT_CONTROL_TYPES = { null, FloatControl.Type.BALANCE, FloatControl.Type.MASTER_GAIN, FloatControl.Type.PAN, FloatControl.Type.VOLUME };
    
    private FloatCtrl(long paramLong, String paramString1, float paramFloat1, float paramFloat2, float paramFloat3, String paramString2)
    {
      this(paramLong, new FCT(paramString1, null), paramFloat1, paramFloat2, paramFloat3, paramString2);
    }
    
    private FloatCtrl(long paramLong, int paramInt, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
    {
      this(paramLong, FLOAT_CONTROL_TYPES[paramInt], paramFloat1, paramFloat2, paramFloat3, paramString);
    }
    
    private FloatCtrl(long paramLong, FloatControl.Type paramType, float paramFloat1, float paramFloat2, float paramFloat3, String paramString)
    {
      super(paramFloat1, paramFloat2, paramFloat3, 1000, paramFloat1, paramString);
      this.controlID = paramLong;
    }
    
    public void setValue(float paramFloat)
    {
      if (!this.closed) {
        PortMixer.nControlSetFloatValue(this.controlID, paramFloat);
      }
    }
    
    public float getValue()
    {
      if (!this.closed) {
        return PortMixer.nControlGetFloatValue(this.controlID);
      }
      return getMinimum();
    }
    
    private static final class FCT
      extends FloatControl.Type
    {
      private FCT(String paramString)
      {
        super();
      }
    }
  }
  
  private static final class PortInfo
    extends Port.Info
  {
    private PortInfo(String paramString, boolean paramBoolean)
    {
      super(paramString, paramBoolean);
    }
  }
  
  private static final class PortMixerPort
    extends AbstractLine
    implements Port
  {
    private final int portIndex;
    private long id;
    
    private PortMixerPort(Port.Info paramInfo, PortMixer paramPortMixer, int paramInt)
    {
      super(paramPortMixer, null);
      this.portIndex = paramInt;
    }
    
    void implOpen()
      throws LineUnavailableException
    {
      long l = ((PortMixer)this.mixer).getID();
      if ((this.id == 0L) || (l != this.id) || (this.controls.length == 0))
      {
        this.id = l;
        Vector localVector = new Vector();
        synchronized (localVector)
        {
          PortMixer.nGetControls(this.id, this.portIndex, localVector);
          this.controls = new Control[localVector.size()];
          for (int i = 0; i < this.controls.length; i++) {
            this.controls[i] = ((Control)localVector.elementAt(i));
          }
        }
      }
      else
      {
        enableControls(this.controls, true);
      }
    }
    
    private void enableControls(Control[] paramArrayOfControl, boolean paramBoolean)
    {
      for (int i = 0; i < paramArrayOfControl.length; i++) {
        if ((paramArrayOfControl[i] instanceof PortMixer.BoolCtrl)) {
          ((PortMixer.BoolCtrl)paramArrayOfControl[i]).closed = (!paramBoolean);
        } else if ((paramArrayOfControl[i] instanceof PortMixer.FloatCtrl)) {
          ((PortMixer.FloatCtrl)paramArrayOfControl[i]).closed = (!paramBoolean);
        } else if ((paramArrayOfControl[i] instanceof CompoundControl)) {
          enableControls(((CompoundControl)paramArrayOfControl[i]).getMemberControls(), paramBoolean);
        }
      }
    }
    
    private void disposeControls()
    {
      enableControls(this.controls, false);
      this.controls = new Control[0];
    }
    
    void implClose()
    {
      enableControls(this.controls, false);
    }
    
    public void open()
      throws LineUnavailableException
    {
      synchronized (this.mixer)
      {
        if (!isOpen())
        {
          this.mixer.open(this);
          try
          {
            implOpen();
            setOpen(true);
          }
          catch (LineUnavailableException localLineUnavailableException)
          {
            this.mixer.close(this);
            throw localLineUnavailableException;
          }
        }
      }
    }
    
    public void close()
    {
      synchronized (this.mixer)
      {
        if (isOpen())
        {
          setOpen(false);
          implClose();
          this.mixer.close(this);
        }
      }
    }
  }
}
