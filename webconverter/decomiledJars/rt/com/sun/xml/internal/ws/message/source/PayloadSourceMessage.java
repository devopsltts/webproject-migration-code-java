package com.sun.xml.internal.ws.message.source;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import javax.xml.transform.Source;

public class PayloadSourceMessage
  extends PayloadStreamReaderMessage
{
  public PayloadSourceMessage(@Nullable MessageHeaders paramMessageHeaders, @NotNull Source paramSource, @NotNull AttachmentSet paramAttachmentSet, @NotNull SOAPVersion paramSOAPVersion)
  {
    super(paramMessageHeaders, SourceReaderFactory.createSourceReader(paramSource, true), paramAttachmentSet, paramSOAPVersion);
  }
  
  public PayloadSourceMessage(Source paramSource, SOAPVersion paramSOAPVersion)
  {
    this(null, paramSource, new AttachmentSetImpl(), paramSOAPVersion);
  }
}
