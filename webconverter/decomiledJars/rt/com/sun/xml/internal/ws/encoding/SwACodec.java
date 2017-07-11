package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class SwACodec
  extends MimeCodec
{
  public SwACodec(SOAPVersion paramSOAPVersion, WSFeatureList paramWSFeatureList, Codec paramCodec)
  {
    super(paramSOAPVersion, paramWSFeatureList);
    this.mimeRootCodec = paramCodec;
  }
  
  private SwACodec(SwACodec paramSwACodec)
  {
    super(paramSwACodec);
    this.mimeRootCodec = paramSwACodec.mimeRootCodec.copy();
  }
  
  protected void decode(MimeMultipartParser paramMimeMultipartParser, Packet paramPacket)
    throws IOException
  {
    Attachment localAttachment = paramMimeMultipartParser.getRootPart();
    Codec localCodec = getMimeRootCodec(paramPacket);
    if ((localCodec instanceof RootOnlyCodec))
    {
      ((RootOnlyCodec)localCodec).decode(localAttachment.asInputStream(), localAttachment.getContentType(), paramPacket, new MimeAttachmentSet(paramMimeMultipartParser));
    }
    else
    {
      localCodec.decode(localAttachment.asInputStream(), localAttachment.getContentType(), paramPacket);
      Map localMap = paramMimeMultipartParser.getAttachmentParts();
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        paramPacket.getMessage().getAttachments().add((Attachment)localEntry.getValue());
      }
    }
  }
  
  public ContentType encode(Packet paramPacket, WritableByteChannel paramWritableByteChannel)
  {
    throw new UnsupportedOperationException();
  }
  
  public SwACodec copy()
  {
    return new SwACodec(this);
  }
}
