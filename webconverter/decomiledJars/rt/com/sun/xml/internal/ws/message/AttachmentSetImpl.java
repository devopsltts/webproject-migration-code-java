package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

public final class AttachmentSetImpl
  implements AttachmentSet
{
  private final ArrayList<Attachment> attList = new ArrayList();
  
  public AttachmentSetImpl() {}
  
  public AttachmentSetImpl(Iterable<Attachment> paramIterable)
  {
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Attachment localAttachment = (Attachment)localIterator.next();
      add(localAttachment);
    }
  }
  
  public Attachment get(String paramString)
  {
    for (int i = this.attList.size() - 1; i >= 0; i--)
    {
      Attachment localAttachment = (Attachment)this.attList.get(i);
      if (localAttachment.getContentId().equals(paramString)) {
        return localAttachment;
      }
    }
    return null;
  }
  
  public boolean isEmpty()
  {
    return this.attList.isEmpty();
  }
  
  public void add(Attachment paramAttachment)
  {
    this.attList.add(paramAttachment);
  }
  
  public Iterator<Attachment> iterator()
  {
    return this.attList.iterator();
  }
}
