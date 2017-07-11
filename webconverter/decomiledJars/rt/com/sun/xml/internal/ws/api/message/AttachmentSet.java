package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;

public abstract interface AttachmentSet
  extends Iterable<Attachment>
{
  @Nullable
  public abstract Attachment get(String paramString);
  
  public abstract boolean isEmpty();
  
  public abstract void add(Attachment paramAttachment);
}
