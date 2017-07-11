package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NamingException;

class Header
{
  static final int HEADER_SIZE = 12;
  static final short QR_BIT = -32768;
  static final short OPCODE_MASK = 30720;
  static final int OPCODE_SHIFT = 11;
  static final short AA_BIT = 1024;
  static final short TC_BIT = 512;
  static final short RD_BIT = 256;
  static final short RA_BIT = 128;
  static final short RCODE_MASK = 15;
  int xid;
  boolean query;
  int opcode;
  boolean authoritative;
  boolean truncated;
  boolean recursionDesired;
  boolean recursionAvail;
  int rcode;
  int numQuestions;
  int numAnswers;
  int numAuthorities;
  int numAdditionals;
  
  Header(byte[] paramArrayOfByte, int paramInt)
    throws NamingException
  {
    decode(paramArrayOfByte, paramInt);
  }
  
  private void decode(byte[] paramArrayOfByte, int paramInt)
    throws NamingException
  {
    try
    {
      int i = 0;
      if (paramInt < 12) {
        throw new CommunicationException("DNS error: corrupted message header");
      }
      this.xid = getShort(paramArrayOfByte, i);
      i += 2;
      int j = (short)getShort(paramArrayOfByte, i);
      i += 2;
      this.query = ((j & 0x8000) == 0);
      this.opcode = ((j & 0x7800) >>> 11);
      this.authoritative = ((j & 0x400) != 0);
      this.truncated = ((j & 0x200) != 0);
      this.recursionDesired = ((j & 0x100) != 0);
      this.recursionAvail = ((j & 0x80) != 0);
      this.rcode = (j & 0xF);
      this.numQuestions = getShort(paramArrayOfByte, i);
      i += 2;
      this.numAnswers = getShort(paramArrayOfByte, i);
      i += 2;
      this.numAuthorities = getShort(paramArrayOfByte, i);
      i += 2;
      this.numAdditionals = getShort(paramArrayOfByte, i);
      i += 2;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      throw new CommunicationException("DNS error: corrupted message header");
    }
  }
  
  private static int getShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF;
  }
}
