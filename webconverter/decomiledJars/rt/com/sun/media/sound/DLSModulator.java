package com.sun.media.sound;

public final class DLSModulator
{
  public static final int CONN_DST_NONE = 0;
  public static final int CONN_DST_GAIN = 1;
  public static final int CONN_DST_PITCH = 3;
  public static final int CONN_DST_PAN = 4;
  public static final int CONN_DST_LFO_FREQUENCY = 260;
  public static final int CONN_DST_LFO_STARTDELAY = 261;
  public static final int CONN_DST_EG1_ATTACKTIME = 518;
  public static final int CONN_DST_EG1_DECAYTIME = 519;
  public static final int CONN_DST_EG1_RELEASETIME = 521;
  public static final int CONN_DST_EG1_SUSTAINLEVEL = 522;
  public static final int CONN_DST_EG2_ATTACKTIME = 778;
  public static final int CONN_DST_EG2_DECAYTIME = 779;
  public static final int CONN_DST_EG2_RELEASETIME = 781;
  public static final int CONN_DST_EG2_SUSTAINLEVEL = 782;
  public static final int CONN_DST_KEYNUMBER = 5;
  public static final int CONN_DST_LEFT = 16;
  public static final int CONN_DST_RIGHT = 17;
  public static final int CONN_DST_CENTER = 18;
  public static final int CONN_DST_LEFTREAR = 19;
  public static final int CONN_DST_RIGHTREAR = 20;
  public static final int CONN_DST_LFE_CHANNEL = 21;
  public static final int CONN_DST_CHORUS = 128;
  public static final int CONN_DST_REVERB = 129;
  public static final int CONN_DST_VIB_FREQUENCY = 276;
  public static final int CONN_DST_VIB_STARTDELAY = 277;
  public static final int CONN_DST_EG1_DELAYTIME = 523;
  public static final int CONN_DST_EG1_HOLDTIME = 524;
  public static final int CONN_DST_EG1_SHUTDOWNTIME = 525;
  public static final int CONN_DST_EG2_DELAYTIME = 783;
  public static final int CONN_DST_EG2_HOLDTIME = 784;
  public static final int CONN_DST_FILTER_CUTOFF = 1280;
  public static final int CONN_DST_FILTER_Q = 1281;
  public static final int CONN_SRC_NONE = 0;
  public static final int CONN_SRC_LFO = 1;
  public static final int CONN_SRC_KEYONVELOCITY = 2;
  public static final int CONN_SRC_KEYNUMBER = 3;
  public static final int CONN_SRC_EG1 = 4;
  public static final int CONN_SRC_EG2 = 5;
  public static final int CONN_SRC_PITCHWHEEL = 6;
  public static final int CONN_SRC_CC1 = 129;
  public static final int CONN_SRC_CC7 = 135;
  public static final int CONN_SRC_CC10 = 138;
  public static final int CONN_SRC_CC11 = 139;
  public static final int CONN_SRC_RPN0 = 256;
  public static final int CONN_SRC_RPN1 = 257;
  public static final int CONN_SRC_RPN2 = 258;
  public static final int CONN_SRC_POLYPRESSURE = 7;
  public static final int CONN_SRC_CHANNELPRESSURE = 8;
  public static final int CONN_SRC_VIBRATO = 9;
  public static final int CONN_SRC_MONOPRESSURE = 10;
  public static final int CONN_SRC_CC91 = 219;
  public static final int CONN_SRC_CC93 = 221;
  public static final int CONN_TRN_NONE = 0;
  public static final int CONN_TRN_CONCAVE = 1;
  public static final int CONN_TRN_CONVEX = 2;
  public static final int CONN_TRN_SWITCH = 3;
  public static final int DST_FORMAT_CB = 1;
  public static final int DST_FORMAT_CENT = 1;
  public static final int DST_FORMAT_TIMECENT = 2;
  public static final int DST_FORMAT_PERCENT = 3;
  int source;
  int control;
  int destination;
  int transform;
  int scale;
  int version = 1;
  
  public DLSModulator() {}
  
  public int getControl()
  {
    return this.control;
  }
  
  public void setControl(int paramInt)
  {
    this.control = paramInt;
  }
  
  public static int getDestinationFormat(int paramInt)
  {
    if (paramInt == 1) {
      return 1;
    }
    if (paramInt == 3) {
      return 1;
    }
    if (paramInt == 4) {
      return 3;
    }
    if (paramInt == 260) {
      return 1;
    }
    if (paramInt == 261) {
      return 2;
    }
    if (paramInt == 518) {
      return 2;
    }
    if (paramInt == 519) {
      return 2;
    }
    if (paramInt == 521) {
      return 2;
    }
    if (paramInt == 522) {
      return 3;
    }
    if (paramInt == 778) {
      return 2;
    }
    if (paramInt == 779) {
      return 2;
    }
    if (paramInt == 781) {
      return 2;
    }
    if (paramInt == 782) {
      return 3;
    }
    if (paramInt == 5) {
      return 1;
    }
    if (paramInt == 16) {
      return 1;
    }
    if (paramInt == 17) {
      return 1;
    }
    if (paramInt == 18) {
      return 1;
    }
    if (paramInt == 19) {
      return 1;
    }
    if (paramInt == 20) {
      return 1;
    }
    if (paramInt == 21) {
      return 1;
    }
    if (paramInt == 128) {
      return 3;
    }
    if (paramInt == 129) {
      return 3;
    }
    if (paramInt == 276) {
      return 1;
    }
    if (paramInt == 277) {
      return 2;
    }
    if (paramInt == 523) {
      return 2;
    }
    if (paramInt == 524) {
      return 2;
    }
    if (paramInt == 525) {
      return 2;
    }
    if (paramInt == 783) {
      return 2;
    }
    if (paramInt == 784) {
      return 2;
    }
    if (paramInt == 1280) {
      return 1;
    }
    if (paramInt == 1281) {
      return 1;
    }
    return -1;
  }
  
  public static String getDestinationName(int paramInt)
  {
    if (paramInt == 1) {
      return "gain";
    }
    if (paramInt == 3) {
      return "pitch";
    }
    if (paramInt == 4) {
      return "pan";
    }
    if (paramInt == 260) {
      return "lfo1.freq";
    }
    if (paramInt == 261) {
      return "lfo1.delay";
    }
    if (paramInt == 518) {
      return "eg1.attack";
    }
    if (paramInt == 519) {
      return "eg1.decay";
    }
    if (paramInt == 521) {
      return "eg1.release";
    }
    if (paramInt == 522) {
      return "eg1.sustain";
    }
    if (paramInt == 778) {
      return "eg2.attack";
    }
    if (paramInt == 779) {
      return "eg2.decay";
    }
    if (paramInt == 781) {
      return "eg2.release";
    }
    if (paramInt == 782) {
      return "eg2.sustain";
    }
    if (paramInt == 5) {
      return "keynumber";
    }
    if (paramInt == 16) {
      return "left";
    }
    if (paramInt == 17) {
      return "right";
    }
    if (paramInt == 18) {
      return "center";
    }
    if (paramInt == 19) {
      return "leftrear";
    }
    if (paramInt == 20) {
      return "rightrear";
    }
    if (paramInt == 21) {
      return "lfe_channel";
    }
    if (paramInt == 128) {
      return "chorus";
    }
    if (paramInt == 129) {
      return "reverb";
    }
    if (paramInt == 276) {
      return "vib.freq";
    }
    if (paramInt == 277) {
      return "vib.delay";
    }
    if (paramInt == 523) {
      return "eg1.delay";
    }
    if (paramInt == 524) {
      return "eg1.hold";
    }
    if (paramInt == 525) {
      return "eg1.shutdown";
    }
    if (paramInt == 783) {
      return "eg2.delay";
    }
    if (paramInt == 784) {
      return "eg.2hold";
    }
    if (paramInt == 1280) {
      return "filter.cutoff";
    }
    if (paramInt == 1281) {
      return "filter.q";
    }
    return null;
  }
  
  public static String getSourceName(int paramInt)
  {
    if (paramInt == 0) {
      return "none";
    }
    if (paramInt == 1) {
      return "lfo";
    }
    if (paramInt == 2) {
      return "keyonvelocity";
    }
    if (paramInt == 3) {
      return "keynumber";
    }
    if (paramInt == 4) {
      return "eg1";
    }
    if (paramInt == 5) {
      return "eg2";
    }
    if (paramInt == 6) {
      return "pitchweel";
    }
    if (paramInt == 129) {
      return "cc1";
    }
    if (paramInt == 135) {
      return "cc7";
    }
    if (paramInt == 138) {
      return "c10";
    }
    if (paramInt == 139) {
      return "cc11";
    }
    if (paramInt == 7) {
      return "polypressure";
    }
    if (paramInt == 8) {
      return "channelpressure";
    }
    if (paramInt == 9) {
      return "vibrato";
    }
    if (paramInt == 10) {
      return "monopressure";
    }
    if (paramInt == 219) {
      return "cc91";
    }
    if (paramInt == 221) {
      return "cc93";
    }
    return null;
  }
  
  public int getDestination()
  {
    return this.destination;
  }
  
  public void setDestination(int paramInt)
  {
    this.destination = paramInt;
  }
  
  public int getScale()
  {
    return this.scale;
  }
  
  public void setScale(int paramInt)
  {
    this.scale = paramInt;
  }
  
  public int getSource()
  {
    return this.source;
  }
  
  public void setSource(int paramInt)
  {
    this.source = paramInt;
  }
  
  public int getVersion()
  {
    return this.version;
  }
  
  public void setVersion(int paramInt)
  {
    this.version = paramInt;
  }
  
  public int getTransform()
  {
    return this.transform;
  }
  
  public void setTransform(int paramInt)
  {
    this.transform = paramInt;
  }
}
