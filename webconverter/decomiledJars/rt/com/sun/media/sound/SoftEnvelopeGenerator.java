package com.sun.media.sound;

public final class SoftEnvelopeGenerator
  implements SoftProcess
{
  public static final int EG_OFF = 0;
  public static final int EG_DELAY = 1;
  public static final int EG_ATTACK = 2;
  public static final int EG_HOLD = 3;
  public static final int EG_DECAY = 4;
  public static final int EG_SUSTAIN = 5;
  public static final int EG_RELEASE = 6;
  public static final int EG_SHUTDOWN = 7;
  public static final int EG_END = 8;
  int max_count = 10;
  int used_count = 0;
  private final int[] stage = new int[this.max_count];
  private final int[] stage_ix = new int[this.max_count];
  private final double[] stage_v = new double[this.max_count];
  private final int[] stage_count = new int[this.max_count];
  private final double[][] on = new double[this.max_count][1];
  private final double[][] active = new double[this.max_count][1];
  private final double[][] out = new double[this.max_count][1];
  private final double[][] delay = new double[this.max_count][1];
  private final double[][] attack = new double[this.max_count][1];
  private final double[][] hold = new double[this.max_count][1];
  private final double[][] decay = new double[this.max_count][1];
  private final double[][] sustain = new double[this.max_count][1];
  private final double[][] release = new double[this.max_count][1];
  private final double[][] shutdown = new double[this.max_count][1];
  private final double[][] release2 = new double[this.max_count][1];
  private final double[][] attack2 = new double[this.max_count][1];
  private final double[][] decay2 = new double[this.max_count][1];
  private double control_time = 0.0D;
  
  public SoftEnvelopeGenerator() {}
  
  public void reset()
  {
    for (int i = 0; i < this.used_count; i++)
    {
      this.stage[i] = 0;
      this.on[i][0] = 0.0D;
      this.out[i][0] = 0.0D;
      this.delay[i][0] = 0.0D;
      this.attack[i][0] = 0.0D;
      this.hold[i][0] = 0.0D;
      this.decay[i][0] = 0.0D;
      this.sustain[i][0] = 0.0D;
      this.release[i][0] = 0.0D;
      this.shutdown[i][0] = 0.0D;
      this.attack2[i][0] = 0.0D;
      this.decay2[i][0] = 0.0D;
      this.release2[i][0] = 0.0D;
    }
    this.used_count = 0;
  }
  
  public void init(SoftSynthesizer paramSoftSynthesizer)
  {
    this.control_time = (1.0D / paramSoftSynthesizer.getControlRate());
    processControlLogic();
  }
  
  public double[] get(int paramInt, String paramString)
  {
    if (paramInt >= this.used_count) {
      this.used_count = (paramInt + 1);
    }
    if (paramString == null) {
      return this.out[paramInt];
    }
    if (paramString.equals("on")) {
      return this.on[paramInt];
    }
    if (paramString.equals("active")) {
      return this.active[paramInt];
    }
    if (paramString.equals("delay")) {
      return this.delay[paramInt];
    }
    if (paramString.equals("attack")) {
      return this.attack[paramInt];
    }
    if (paramString.equals("hold")) {
      return this.hold[paramInt];
    }
    if (paramString.equals("decay")) {
      return this.decay[paramInt];
    }
    if (paramString.equals("sustain")) {
      return this.sustain[paramInt];
    }
    if (paramString.equals("release")) {
      return this.release[paramInt];
    }
    if (paramString.equals("shutdown")) {
      return this.shutdown[paramInt];
    }
    if (paramString.equals("attack2")) {
      return this.attack2[paramInt];
    }
    if (paramString.equals("decay2")) {
      return this.decay2[paramInt];
    }
    if (paramString.equals("release2")) {
      return this.release2[paramInt];
    }
    return null;
  }
  
  public void processControlLogic()
  {
    for (int i = 0; i < this.used_count; i++) {
      if (this.stage[i] != 8)
      {
        double d1;
        if ((this.stage[i] > 0) && (this.stage[i] < 6) && (this.on[i][0] < 0.5D)) {
          if (this.on[i][0] < -0.5D)
          {
            this.stage_count[i] = ((int)(Math.pow(2.0D, this.shutdown[i][0] / 1200.0D) / this.control_time));
            if (this.stage_count[i] < 0) {
              this.stage_count[i] = 0;
            }
            this.stage_v[i] = this.out[i][0];
            this.stage_ix[i] = 0;
            this.stage[i] = 7;
          }
          else
          {
            if ((this.release2[i][0] < 1.0E-6D) && (this.release[i][0] < 0.0D) && (Double.isInfinite(this.release[i][0])))
            {
              this.out[i][0] = 0.0D;
              this.active[i][0] = 0.0D;
              this.stage[i] = 8;
              continue;
            }
            this.stage_count[i] = ((int)(Math.pow(2.0D, this.release[i][0] / 1200.0D) / this.control_time));
            this.stage_count[i] += (int)(this.release2[i][0] / (this.control_time * 1000.0D));
            if (this.stage_count[i] < 0) {
              this.stage_count[i] = 0;
            }
            this.stage_ix[i] = 0;
            d1 = 1.0D - this.out[i][0];
            this.stage_ix[i] = ((int)(this.stage_count[i] * d1));
            this.stage[i] = 6;
          }
        }
        double d2;
        switch (this.stage[i])
        {
        case 0: 
          this.active[i][0] = 1.0D;
          if (this.on[i][0] >= 0.5D)
          {
            this.stage[i] = 1;
            this.stage_ix[i] = ((int)(Math.pow(2.0D, this.delay[i][0] / 1200.0D) / this.control_time));
            if (this.stage_ix[i] < 0) {
              this.stage_ix[i] = 0;
            }
          }
          break;
        case 1: 
          if (this.stage_ix[i] == 0)
          {
            d1 = this.attack[i][0];
            d2 = this.attack2[i][0];
            if ((d2 < 1.0E-6D) && (d1 < 0.0D) && (Double.isInfinite(d1)))
            {
              this.out[i][0] = 1.0D;
              this.stage[i] = 3;
              this.stage_count[i] = ((int)(Math.pow(2.0D, this.hold[i][0] / 1200.0D) / this.control_time));
              this.stage_ix[i] = 0;
            }
            else
            {
              this.stage[i] = 2;
              this.stage_count[i] = ((int)(Math.pow(2.0D, d1 / 1200.0D) / this.control_time));
              this.stage_count[i] += (int)(d2 / (this.control_time * 1000.0D));
              if (this.stage_count[i] < 0) {
                this.stage_count[i] = 0;
              }
              this.stage_ix[i] = 0;
            }
          }
          else
          {
            this.stage_ix[i] -= 1;
          }
          break;
        case 2: 
          this.stage_ix[i] += 1;
          if (this.stage_ix[i] >= this.stage_count[i])
          {
            this.out[i][0] = 1.0D;
            this.stage[i] = 3;
          }
          else
          {
            d1 = this.stage_ix[i] / this.stage_count[i];
            d1 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(d1);
            if (d1 < 0.0D) {
              d1 = 0.0D;
            } else if (d1 > 1.0D) {
              d1 = 1.0D;
            }
            this.out[i][0] = d1;
          }
          break;
        case 3: 
          this.stage_ix[i] += 1;
          if (this.stage_ix[i] >= this.stage_count[i])
          {
            this.stage[i] = 4;
            this.stage_count[i] = ((int)(Math.pow(2.0D, this.decay[i][0] / 1200.0D) / this.control_time));
            this.stage_count[i] += (int)(this.decay2[i][0] / (this.control_time * 1000.0D));
            if (this.stage_count[i] < 0) {
              this.stage_count[i] = 0;
            }
            this.stage_ix[i] = 0;
          }
          break;
        case 4: 
          this.stage_ix[i] += 1;
          d1 = this.sustain[i][0] * 0.001D;
          if (this.stage_ix[i] >= this.stage_count[i])
          {
            this.out[i][0] = d1;
            this.stage[i] = 5;
            if (d1 < 0.001D)
            {
              this.out[i][0] = 0.0D;
              this.active[i][0] = 0.0D;
              this.stage[i] = 8;
            }
          }
          else
          {
            d2 = this.stage_ix[i] / this.stage_count[i];
            this.out[i][0] = (1.0D - d2 + d1 * d2);
          }
          break;
        case 5: 
          break;
        case 6: 
          this.stage_ix[i] += 1;
          if (this.stage_ix[i] >= this.stage_count[i])
          {
            this.out[i][0] = 0.0D;
            this.active[i][0] = 0.0D;
            this.stage[i] = 8;
          }
          else
          {
            d2 = this.stage_ix[i] / this.stage_count[i];
            this.out[i][0] = (1.0D - d2);
            if (this.on[i][0] < -0.5D)
            {
              this.stage_count[i] = ((int)(Math.pow(2.0D, this.shutdown[i][0] / 1200.0D) / this.control_time));
              if (this.stage_count[i] < 0) {
                this.stage_count[i] = 0;
              }
              this.stage_v[i] = this.out[i][0];
              this.stage_ix[i] = 0;
              this.stage[i] = 7;
            }
            if (this.on[i][0] > 0.5D)
            {
              d1 = this.sustain[i][0] * 0.001D;
              if (this.out[i][0] > d1)
              {
                this.stage[i] = 4;
                this.stage_count[i] = ((int)(Math.pow(2.0D, this.decay[i][0] / 1200.0D) / this.control_time));
                this.stage_count[i] += (int)(this.decay2[i][0] / (this.control_time * 1000.0D));
                if (this.stage_count[i] < 0) {
                  this.stage_count[i] = 0;
                }
                d2 = (this.out[i][0] - 1.0D) / (d1 - 1.0D);
                this.stage_ix[i] = ((int)(this.stage_count[i] * d2));
              }
            }
          }
          break;
        case 7: 
          this.stage_ix[i] += 1;
          if (this.stage_ix[i] >= this.stage_count[i])
          {
            this.out[i][0] = 0.0D;
            this.active[i][0] = 0.0D;
            this.stage[i] = 8;
          }
          else
          {
            d2 = this.stage_ix[i] / this.stage_count[i];
            this.out[i][0] = ((1.0D - d2) * this.stage_v[i]);
          }
          break;
        }
      }
    }
  }
}
