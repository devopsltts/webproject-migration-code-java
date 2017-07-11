package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class Association
{
  private final int associationID;
  private final int maxInStreams;
  private final int maxOutStreams;
  
  protected Association(int paramInt1, int paramInt2, int paramInt3)
  {
    this.associationID = paramInt1;
    this.maxInStreams = paramInt2;
    this.maxOutStreams = paramInt3;
  }
  
  public final int associationID()
  {
    return this.associationID;
  }
  
  public final int maxInboundStreams()
  {
    return this.maxInStreams;
  }
  
  public final int maxOutboundStreams()
  {
    return this.maxOutStreams;
  }
}
