package com.sun.nio.sctp;

import java.net.SocketAddress;
import jdk.Exported;
import sun.nio.ch.sctp.SctpStdSocketOption;

@Exported
public class SctpStandardSocketOptions
{
  public static final SctpSocketOption<Boolean> SCTP_DISABLE_FRAGMENTS = new SctpStdSocketOption("SCTP_DISABLE_FRAGMENTS", Boolean.class, 1);
  public static final SctpSocketOption<Boolean> SCTP_EXPLICIT_COMPLETE = new SctpStdSocketOption("SCTP_EXPLICIT_COMPLETE", Boolean.class, 2);
  public static final SctpSocketOption<Integer> SCTP_FRAGMENT_INTERLEAVE = new SctpStdSocketOption("SCTP_FRAGMENT_INTERLEAVE", Integer.class, 3);
  public static final SctpSocketOption<InitMaxStreams> SCTP_INIT_MAXSTREAMS = new SctpStdSocketOption("SCTP_INIT_MAXSTREAMS", InitMaxStreams.class);
  public static final SctpSocketOption<Boolean> SCTP_NODELAY = new SctpStdSocketOption("SCTP_NODELAY", Boolean.class, 4);
  public static final SctpSocketOption<SocketAddress> SCTP_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_PRIMARY_ADDR", SocketAddress.class);
  public static final SctpSocketOption<SocketAddress> SCTP_SET_PEER_PRIMARY_ADDR = new SctpStdSocketOption("SCTP_SET_PEER_PRIMARY_ADDR", SocketAddress.class);
  public static final SctpSocketOption<Integer> SO_SNDBUF = new SctpStdSocketOption("SO_SNDBUF", Integer.class, 5);
  public static final SctpSocketOption<Integer> SO_RCVBUF = new SctpStdSocketOption("SO_RCVBUF", Integer.class, 6);
  public static final SctpSocketOption<Integer> SO_LINGER = new SctpStdSocketOption("SO_LINGER", Integer.class, 7);
  
  private SctpStandardSocketOptions() {}
  
  @Exported
  public static class InitMaxStreams
  {
    private int maxInStreams;
    private int maxOutStreams;
    
    private InitMaxStreams(int paramInt1, int paramInt2)
    {
      this.maxInStreams = paramInt1;
      this.maxOutStreams = paramInt2;
    }
    
    public static InitMaxStreams create(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 > 65535)) {
        throw new IllegalArgumentException("Invalid maxOutStreams value");
      }
      if ((paramInt1 < 0) || (paramInt1 > 65535)) {
        throw new IllegalArgumentException("Invalid maxInStreams value");
      }
      return new InitMaxStreams(paramInt1, paramInt2);
    }
    
    public int maxInStreams()
    {
      return this.maxInStreams;
    }
    
    public int maxOutStreams()
    {
      return this.maxOutStreams;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(super.toString()).append(" [");
      localStringBuilder.append("maxInStreams:").append(this.maxInStreams);
      localStringBuilder.append("maxOutStreams:").append(this.maxOutStreams).append("]");
      return localStringBuilder.toString();
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject != null) && ((paramObject instanceof InitMaxStreams)))
      {
        InitMaxStreams localInitMaxStreams = (InitMaxStreams)paramObject;
        if ((this.maxInStreams == localInitMaxStreams.maxInStreams) && (this.maxOutStreams == localInitMaxStreams.maxOutStreams)) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      int i = 0x7 ^ this.maxInStreams ^ this.maxOutStreams;
      return i;
    }
  }
}
