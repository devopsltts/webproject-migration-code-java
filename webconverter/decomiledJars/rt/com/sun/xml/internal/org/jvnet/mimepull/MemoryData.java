package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MemoryData
  implements Data
{
  private static final Logger LOGGER = Logger.getLogger(MemoryData.class.getName());
  private final byte[] data;
  private final int len;
  private final MIMEConfig config;
  
  MemoryData(ByteBuffer paramByteBuffer, MIMEConfig paramMIMEConfig)
  {
    this.data = paramByteBuffer.array();
    this.len = paramByteBuffer.limit();
    this.config = paramMIMEConfig;
  }
  
  public int size()
  {
    return this.len;
  }
  
  public byte[] read()
  {
    return this.data;
  }
  
  public long writeTo(DataFile paramDataFile)
  {
    return paramDataFile.writeTo(this.data, 0, this.len);
  }
  
  public Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer)
  {
    if ((!this.config.isOnlyMemory()) && (paramDataHead.inMemory >= this.config.memoryThreshold))
    {
      try
      {
        String str1 = this.config.getTempFilePrefix();
        String str2 = this.config.getTempFileSuffix();
        File localFile = TempFiles.createTempFile(str1, str2, this.config.getTempDir());
        localFile.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "Created temp file = {0}", localFile);
        }
        localFile.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE)) {
          LOGGER.log(Level.FINE, "Created temp file = {0}", localFile);
        }
        paramDataHead.dataFile = new DataFile(localFile);
      }
      catch (IOException localIOException)
      {
        throw new MIMEParsingException(localIOException);
      }
      if (paramDataHead.head != null) {
        for (Chunk localChunk = paramDataHead.head; localChunk != null; localChunk = localChunk.next)
        {
          long l = localChunk.data.writeTo(paramDataHead.dataFile);
          localChunk.data = new FileData(paramDataHead.dataFile, l, this.len);
        }
      }
      return new FileData(paramDataHead.dataFile, paramByteBuffer);
    }
    return new MemoryData(paramByteBuffer, this.config);
  }
}
