package com.sun.xml.internal.ws.streaming;

import javax.xml.bind.attachment.AttachmentMarshaller;

public abstract interface MtomStreamWriter
{
  public abstract AttachmentMarshaller getAttachmentMarshaller();
}
