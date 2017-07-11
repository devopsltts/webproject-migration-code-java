package com.sun.media.sound;

import java.util.Arrays;

public final class SoftReverb
  implements SoftAudioProcessor
{
  private float roomsize;
  private float damp;
  private float gain = 1.0F;
  private Delay delay;
  private Comb[] combL;
  private Comb[] combR;
  private AllPass[] allpassL;
  private AllPass[] allpassR;
  private float[] input;
  private float[] out;
  private float[] pre1;
  private float[] pre2;
  private float[] pre3;
  private boolean denormal_flip = false;
  private boolean mix = true;
  private SoftAudioBuffer inputA;
  private SoftAudioBuffer left;
  private SoftAudioBuffer right;
  private boolean dirty = true;
  private float dirty_roomsize;
  private float dirty_damp;
  private float dirty_predelay;
  private float dirty_gain;
  private float samplerate;
  private boolean light = true;
  private boolean silent = true;
  
  public SoftReverb() {}
  
  public void init(float paramFloat1, float paramFloat2)
  {
    this.samplerate = paramFloat1;
    double d = paramFloat1 / 44100.0D;
    int i = 23;
    this.delay = new Delay();
    this.combL = new Comb[8];
    this.combR = new Comb[8];
    this.combL[0] = new Comb((int)(d * 1116.0D));
    this.combR[0] = new Comb((int)(d * (1116 + i)));
    this.combL[1] = new Comb((int)(d * 1188.0D));
    this.combR[1] = new Comb((int)(d * (1188 + i)));
    this.combL[2] = new Comb((int)(d * 1277.0D));
    this.combR[2] = new Comb((int)(d * (1277 + i)));
    this.combL[3] = new Comb((int)(d * 1356.0D));
    this.combR[3] = new Comb((int)(d * (1356 + i)));
    this.combL[4] = new Comb((int)(d * 1422.0D));
    this.combR[4] = new Comb((int)(d * (1422 + i)));
    this.combL[5] = new Comb((int)(d * 1491.0D));
    this.combR[5] = new Comb((int)(d * (1491 + i)));
    this.combL[6] = new Comb((int)(d * 1557.0D));
    this.combR[6] = new Comb((int)(d * (1557 + i)));
    this.combL[7] = new Comb((int)(d * 1617.0D));
    this.combR[7] = new Comb((int)(d * (1617 + i)));
    this.allpassL = new AllPass[4];
    this.allpassR = new AllPass[4];
    this.allpassL[0] = new AllPass((int)(d * 556.0D));
    this.allpassR[0] = new AllPass((int)(d * (556 + i)));
    this.allpassL[1] = new AllPass((int)(d * 441.0D));
    this.allpassR[1] = new AllPass((int)(d * (441 + i)));
    this.allpassL[2] = new AllPass((int)(d * 341.0D));
    this.allpassR[2] = new AllPass((int)(d * (341 + i)));
    this.allpassL[3] = new AllPass((int)(d * 225.0D));
    this.allpassR[3] = new AllPass((int)(d * (225 + i)));
    for (int j = 0; j < this.allpassL.length; j++)
    {
      this.allpassL[j].setFeedBack(0.5F);
      this.allpassR[j].setFeedBack(0.5F);
    }
    globalParameterControlChange(new int[] { 129 }, 0L, 4L);
  }
  
  public void setInput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      this.inputA = paramSoftAudioBuffer;
    }
  }
  
  public void setOutput(int paramInt, SoftAudioBuffer paramSoftAudioBuffer)
  {
    if (paramInt == 0) {
      this.left = paramSoftAudioBuffer;
    }
    if (paramInt == 1) {
      this.right = paramSoftAudioBuffer;
    }
  }
  
  public void setMixMode(boolean paramBoolean)
  {
    this.mix = paramBoolean;
  }
  
  public void processAudio()
  {
    boolean bool = this.inputA.isSilent();
    if (!bool) {
      this.silent = false;
    }
    if (this.silent)
    {
      if (!this.mix)
      {
        this.left.clear();
        this.right.clear();
      }
      return;
    }
    float[] arrayOfFloat1 = this.inputA.array();
    float[] arrayOfFloat2 = this.left.array();
    float[] arrayOfFloat3 = this.right == null ? null : this.right.array();
    int i = arrayOfFloat1.length;
    if ((this.input == null) || (this.input.length < i)) {
      this.input = new float[i];
    }
    float f1 = this.gain * 0.018F / 2.0F;
    this.denormal_flip = (!this.denormal_flip);
    int j;
    if (this.denormal_flip) {
      for (j = 0; j < i; j++) {
        this.input[j] = (arrayOfFloat1[j] * f1 + 1.0E-20F);
      }
    } else {
      for (j = 0; j < i; j++) {
        this.input[j] = (arrayOfFloat1[j] * f1 - 1.0E-20F);
      }
    }
    this.delay.processReplace(this.input);
    float f2;
    if ((this.light) && (arrayOfFloat3 != null))
    {
      if ((this.pre1 == null) || (this.pre1.length < i))
      {
        this.pre1 = new float[i];
        this.pre2 = new float[i];
        this.pre3 = new float[i];
      }
      for (j = 0; j < this.allpassL.length; j++) {
        this.allpassL[j].processReplace(this.input);
      }
      this.combL[0].processReplace(this.input, this.pre3);
      this.combL[1].processReplace(this.input, this.pre3);
      this.combL[2].processReplace(this.input, this.pre1);
      for (j = 4; j < this.combL.length - 2; j += 2) {
        this.combL[j].processMix(this.input, this.pre1);
      }
      this.combL[3].processReplace(this.input, this.pre2);
      for (j = 5; j < this.combL.length - 2; j += 2) {
        this.combL[j].processMix(this.input, this.pre2);
      }
      if (!this.mix)
      {
        Arrays.fill(arrayOfFloat3, 0.0F);
        Arrays.fill(arrayOfFloat2, 0.0F);
      }
      for (j = this.combR.length - 2; j < this.combR.length; j++) {
        this.combR[j].processMix(this.input, arrayOfFloat3);
      }
      for (j = this.combL.length - 2; j < this.combL.length; j++) {
        this.combL[j].processMix(this.input, arrayOfFloat2);
      }
      for (j = 0; j < i; j++)
      {
        f2 = this.pre1[j] - this.pre2[j];
        float f3 = this.pre3[j];
        arrayOfFloat2[j] += f3 + f2;
        arrayOfFloat3[j] += f3 - f2;
      }
    }
    else
    {
      if ((this.out == null) || (this.out.length < i)) {
        this.out = new float[i];
      }
      if (arrayOfFloat3 != null)
      {
        if (!this.mix) {
          Arrays.fill(arrayOfFloat3, 0.0F);
        }
        this.allpassR[0].processReplace(this.input, this.out);
        for (j = 1; j < this.allpassR.length; j++) {
          this.allpassR[j].processReplace(this.out);
        }
        for (j = 0; j < this.combR.length; j++) {
          this.combR[j].processMix(this.out, arrayOfFloat3);
        }
      }
      if (!this.mix) {
        Arrays.fill(arrayOfFloat2, 0.0F);
      }
      this.allpassL[0].processReplace(this.input, this.out);
      for (j = 1; j < this.allpassL.length; j++) {
        this.allpassL[j].processReplace(this.out);
      }
      for (j = 0; j < this.combL.length; j++) {
        this.combL[j].processMix(this.out, arrayOfFloat2);
      }
    }
    if (bool)
    {
      this.silent = true;
      for (j = 0; j < i; j++)
      {
        f2 = arrayOfFloat2[j];
        if ((f2 > 1.0E-10D) || (f2 < -1.0E-10D))
        {
          this.silent = false;
          break;
        }
      }
    }
  }
  
  public void globalParameterControlChange(int[] paramArrayOfInt, long paramLong1, long paramLong2)
  {
    if ((paramArrayOfInt.length == 1) && (paramArrayOfInt[0] == 129)) {
      if (paramLong1 == 0L)
      {
        if (paramLong2 == 0L)
        {
          this.dirty_roomsize = 1.1F;
          this.dirty_damp = 5000.0F;
          this.dirty_predelay = 0.0F;
          this.dirty_gain = 4.0F;
          this.dirty = true;
        }
        if (paramLong2 == 1L)
        {
          this.dirty_roomsize = 1.3F;
          this.dirty_damp = 5000.0F;
          this.dirty_predelay = 0.0F;
          this.dirty_gain = 3.0F;
          this.dirty = true;
        }
        if (paramLong2 == 2L)
        {
          this.dirty_roomsize = 1.5F;
          this.dirty_damp = 5000.0F;
          this.dirty_predelay = 0.0F;
          this.dirty_gain = 2.0F;
          this.dirty = true;
        }
        if (paramLong2 == 3L)
        {
          this.dirty_roomsize = 1.8F;
          this.dirty_damp = 24000.0F;
          this.dirty_predelay = 0.02F;
          this.dirty_gain = 1.5F;
          this.dirty = true;
        }
        if (paramLong2 == 4L)
        {
          this.dirty_roomsize = 1.8F;
          this.dirty_damp = 24000.0F;
          this.dirty_predelay = 0.03F;
          this.dirty_gain = 1.5F;
          this.dirty = true;
        }
        if (paramLong2 == 8L)
        {
          this.dirty_roomsize = 1.3F;
          this.dirty_damp = 2500.0F;
          this.dirty_predelay = 0.0F;
          this.dirty_gain = 6.0F;
          this.dirty = true;
        }
      }
      else if (paramLong1 == 1L)
      {
        this.dirty_roomsize = ((float)Math.exp((paramLong2 - 40L) * 0.025D));
        this.dirty = true;
      }
    }
  }
  
  public void processControlLogic()
  {
    if (this.dirty)
    {
      this.dirty = false;
      setRoomSize(this.dirty_roomsize);
      setDamp(this.dirty_damp);
      setPreDelay(this.dirty_predelay);
      setGain(this.dirty_gain);
    }
  }
  
  public void setRoomSize(float paramFloat)
  {
    this.roomsize = (1.0F - 0.17F / paramFloat);
    for (int i = 0; i < this.combL.length; i++)
    {
      this.combL[i].feedback = this.roomsize;
      this.combR[i].feedback = this.roomsize;
    }
  }
  
  public void setPreDelay(float paramFloat)
  {
    this.delay.setDelay((int)(paramFloat * this.samplerate));
  }
  
  public void setGain(float paramFloat)
  {
    this.gain = paramFloat;
  }
  
  public void setDamp(float paramFloat)
  {
    double d1 = paramFloat / this.samplerate * 6.283185307179586D;
    double d2 = 2.0D - Math.cos(d1);
    this.damp = ((float)(d2 - Math.sqrt(d2 * d2 - 1.0D)));
    if (this.damp > 1.0F) {
      this.damp = 1.0F;
    }
    if (this.damp < 0.0F) {
      this.damp = 0.0F;
    }
    for (int i = 0; i < this.combL.length; i++)
    {
      this.combL[i].setDamp(this.damp);
      this.combR[i].setDamp(this.damp);
    }
  }
  
  public void setLightMode(boolean paramBoolean)
  {
    this.light = paramBoolean;
  }
  
  private static final class AllPass
  {
    private final float[] delaybuffer;
    private final int delaybuffersize;
    private int rovepos = 0;
    private float feedback;
    
    AllPass(int paramInt)
    {
      this.delaybuffer = new float[paramInt];
      this.delaybuffersize = paramInt;
    }
    
    public void setFeedBack(float paramFloat)
    {
      this.feedback = paramFloat;
    }
    
    public void processReplace(float[] paramArrayOfFloat)
    {
      int i = paramArrayOfFloat.length;
      int j = this.delaybuffersize;
      int k = this.rovepos;
      for (int m = 0; m < i; m++)
      {
        float f1 = this.delaybuffer[k];
        float f2 = paramArrayOfFloat[m];
        paramArrayOfFloat[m] = (f1 - f2);
        this.delaybuffer[k] = (f2 + f1 * this.feedback);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      this.rovepos = k;
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = this.delaybuffersize;
      int k = this.rovepos;
      for (int m = 0; m < i; m++)
      {
        float f1 = this.delaybuffer[k];
        float f2 = paramArrayOfFloat1[m];
        paramArrayOfFloat2[m] = (f1 - f2);
        this.delaybuffer[k] = (f2 + f1 * this.feedback);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      this.rovepos = k;
    }
  }
  
  private static final class Comb
  {
    private final float[] delaybuffer;
    private final int delaybuffersize;
    private int rovepos = 0;
    private float feedback;
    private float filtertemp = 0.0F;
    private float filtercoeff1 = 0.0F;
    private float filtercoeff2 = 1.0F;
    
    Comb(int paramInt)
    {
      this.delaybuffer = new float[paramInt];
      this.delaybuffersize = paramInt;
    }
    
    public void setFeedBack(float paramFloat)
    {
      this.feedback = paramFloat;
      this.filtercoeff2 = ((1.0F - this.filtercoeff1) * paramFloat);
    }
    
    public void processMix(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = this.delaybuffersize;
      int k = this.rovepos;
      float f1 = this.filtertemp;
      float f2 = this.filtercoeff1;
      float f3 = this.filtercoeff2;
      for (int m = 0; m < i; m++)
      {
        float f4 = this.delaybuffer[k];
        f1 = f4 * f3 + f1 * f2;
        paramArrayOfFloat2[m] += f4;
        this.delaybuffer[k] = (paramArrayOfFloat1[m] + f1);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      this.filtertemp = f1;
      this.rovepos = k;
    }
    
    public void processReplace(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = paramArrayOfFloat1.length;
      int j = this.delaybuffersize;
      int k = this.rovepos;
      float f1 = this.filtertemp;
      float f2 = this.filtercoeff1;
      float f3 = this.filtercoeff2;
      for (int m = 0; m < i; m++)
      {
        float f4 = this.delaybuffer[k];
        f1 = f4 * f3 + f1 * f2;
        paramArrayOfFloat2[m] = f4;
        this.delaybuffer[k] = (paramArrayOfFloat1[m] + f1);
        k++;
        if (k == j) {
          k = 0;
        }
      }
      this.filtertemp = f1;
      this.rovepos = k;
    }
    
    public void setDamp(float paramFloat)
    {
      this.filtercoeff1 = paramFloat;
      this.filtercoeff2 = ((1.0F - this.filtercoeff1) * this.feedback);
    }
  }
  
  private static final class Delay
  {
    private float[] delaybuffer = null;
    private int rovepos = 0;
    
    Delay() {}
    
    public void setDelay(int paramInt)
    {
      if (paramInt == 0) {
        this.delaybuffer = null;
      } else {
        this.delaybuffer = new float[paramInt];
      }
      this.rovepos = 0;
    }
    
    public void processReplace(float[] paramArrayOfFloat)
    {
      if (this.delaybuffer == null) {
        return;
      }
      int i = paramArrayOfFloat.length;
      int j = this.delaybuffer.length;
      int k = this.rovepos;
      for (int m = 0; m < i; m++)
      {
        float f = paramArrayOfFloat[m];
        paramArrayOfFloat[m] = this.delaybuffer[k];
        this.delaybuffer[k] = f;
        k++;
        if (k == j) {
          k = 0;
        }
      }
      this.rovepos = k;
    }
  }
}
