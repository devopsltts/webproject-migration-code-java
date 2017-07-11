package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_2
  extends CDROutputStream_1_1
{
  protected boolean primitiveAcrossFragmentedChunk = false;
  protected boolean specialChunk = false;
  private boolean headerPadding;
  
  public CDROutputStream_1_2() {}
  
  protected void handleSpecialChunkBegin(int paramInt)
  {
    if ((this.inBlock) && (paramInt + this.bbwi.position() > this.bbwi.buflen))
    {
      int i = this.bbwi.position();
      this.bbwi.position(this.blockSizeIndex - 4);
      writeLongWithoutAlign(i - this.blockSizeIndex + paramInt);
      this.bbwi.position(i);
      this.specialChunk = true;
    }
  }
  
  protected void handleSpecialChunkEnd()
  {
    if ((this.inBlock) && (this.specialChunk))
    {
      this.inBlock = false;
      this.blockSizeIndex = -1;
      this.blockSizePosition = -1;
      start_block();
      this.specialChunk = false;
    }
  }
  
  private void checkPrimitiveAcrossFragmentedChunk()
  {
    if (this.primitiveAcrossFragmentedChunk)
    {
      this.primitiveAcrossFragmentedChunk = false;
      this.inBlock = false;
      this.blockSizeIndex = -1;
      this.blockSizePosition = -1;
      start_block();
    }
  }
  
  public void write_octet(byte paramByte)
  {
    super.write_octet(paramByte);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_short(short paramShort)
  {
    super.write_short(paramShort);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_long(int paramInt)
  {
    super.write_long(paramInt);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  public void write_longlong(long paramLong)
  {
    super.write_longlong(paramLong);
    checkPrimitiveAcrossFragmentedChunk();
  }
  
  void setHeaderPadding(boolean paramBoolean)
  {
    this.headerPadding = paramBoolean;
  }
  
  protected void alignAndReserve(int paramInt1, int paramInt2)
  {
    if (this.headerPadding == true)
    {
      this.headerPadding = false;
      alignOnBoundary(8);
    }
    this.bbwi.position(this.bbwi.position() + computeAlignment(paramInt1));
    if (this.bbwi.position() + paramInt2 > this.bbwi.buflen) {
      grow(paramInt1, paramInt2);
    }
  }
  
  protected void grow(int paramInt1, int paramInt2)
  {
    int i = this.bbwi.position();
    int j = (this.inBlock) && (!this.specialChunk) ? 1 : 0;
    if (j != 0)
    {
      int k = this.bbwi.position();
      this.bbwi.position(this.blockSizeIndex - 4);
      writeLongWithoutAlign(k - this.blockSizeIndex + paramInt2);
      this.bbwi.position(k);
    }
    this.bbwi.needed = paramInt2;
    this.bufferManagerWrite.overflow(this.bbwi);
    if (this.bbwi.fragmented)
    {
      this.bbwi.fragmented = false;
      this.fragmentOffset += i - this.bbwi.position();
      if (j != 0) {
        this.primitiveAcrossFragmentedChunk = true;
      }
    }
  }
  
  public GIOPVersion getGIOPVersion()
  {
    return GIOPVersion.V1_2;
  }
  
  public void write_wchar(char paramChar)
  {
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramChar);
    handleSpecialChunkBegin(1 + localCTBConverter.getNumBytes());
    write_octet((byte)localCTBConverter.getNumBytes());
    byte[] arrayOfByte = localCTBConverter.getBytes();
    internalWriteOctetArray(arrayOfByte, 0, localCTBConverter.getNumBytes());
    handleSpecialChunkEnd();
  }
  
  public void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    int i = 0;
    int j = (int)Math.ceil(localCTBConverter.getMaxBytesPerChar() * paramInt2);
    byte[] arrayOfByte = new byte[j + paramInt2];
    for (int k = 0; k < paramInt2; k++)
    {
      localCTBConverter.convert(paramArrayOfChar[(paramInt1 + k)]);
      arrayOfByte[(i++)] = ((byte)localCTBConverter.getNumBytes());
      System.arraycopy(localCTBConverter.getBytes(), 0, arrayOfByte, i, localCTBConverter.getNumBytes());
      i += localCTBConverter.getNumBytes();
    }
    handleSpecialChunkBegin(i);
    internalWriteOctetArray(arrayOfByte, 0, i);
    handleSpecialChunkEnd();
  }
  
  public void write_wstring(String paramString)
  {
    if (paramString == null) {
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE);
    }
    if (paramString.length() == 0)
    {
      write_long(0);
      return;
    }
    CodeSetConversion.CTBConverter localCTBConverter = getWCharConverter();
    localCTBConverter.convert(paramString);
    handleSpecialChunkBegin(computeAlignment(4) + 4 + localCTBConverter.getNumBytes());
    write_long(localCTBConverter.getNumBytes());
    internalWriteOctetArray(localCTBConverter.getBytes(), 0, localCTBConverter.getNumBytes());
    handleSpecialChunkEnd();
  }
}
