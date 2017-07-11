package javax.swing.text.html.parser;

class ContentModelState
{
  ContentModel model;
  long value;
  ContentModelState next;
  
  public ContentModelState(ContentModel paramContentModel)
  {
    this(paramContentModel, null, 0L);
  }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState)
  {
    this(paramObject, paramContentModelState, 0L);
  }
  
  ContentModelState(Object paramObject, ContentModelState paramContentModelState, long paramLong)
  {
    this.model = ((ContentModel)paramObject);
    this.next = paramContentModelState;
    this.value = paramLong;
  }
  
  public ContentModel getModel()
  {
    ContentModel localContentModel = this.model;
    for (int i = 0; i < this.value; i++) {
      if (localContentModel.next != null) {
        localContentModel = localContentModel.next;
      } else {
        return null;
      }
    }
    return localContentModel;
  }
  
  public boolean terminate()
  {
    ContentModel localContentModel;
    int i;
    switch (this.model.type)
    {
    case 43: 
      if ((this.value == 0L) && (!this.model.empty())) {
        return false;
      }
    case 42: 
    case 63: 
      return (this.next == null) || (this.next.terminate());
    case 124: 
      for (localContentModel = (ContentModel)this.model.content; localContentModel != null; localContentModel = localContentModel.next) {
        if (localContentModel.empty()) {
          return (this.next == null) || (this.next.terminate());
        }
      }
      return false;
    case 38: 
      localContentModel = (ContentModel)this.model.content;
      i = 0;
      while (localContentModel != null)
      {
        if (((this.value & 1L << i) == 0L) && (!localContentModel.empty())) {
          return false;
        }
        i++;
        localContentModel = localContentModel.next;
      }
      return (this.next == null) || (this.next.terminate());
    case 44: 
      localContentModel = (ContentModel)this.model.content;
      i = 0;
      while (i < this.value)
      {
        i++;
        localContentModel = localContentModel.next;
      }
      while ((localContentModel != null) && (localContentModel.empty())) {
        localContentModel = localContentModel.next;
      }
      if (localContentModel != null) {
        return false;
      }
      return (this.next == null) || (this.next.terminate());
    }
    return false;
  }
  
  public Element first()
  {
    switch (this.model.type)
    {
    case 38: 
    case 42: 
    case 63: 
    case 124: 
      return null;
    case 43: 
      return this.model.first();
    case 44: 
      ContentModel localContentModel = (ContentModel)this.model.content;
      int i = 0;
      while (i < this.value)
      {
        i++;
        localContentModel = localContentModel.next;
      }
      return localContentModel.first();
    }
    return this.model.first();
  }
  
  public ContentModelState advance(Object paramObject)
  {
    ContentModel localContentModel;
    int i;
    switch (this.model.type)
    {
    case 43: 
      if (this.model.first(paramObject)) {
        return new ContentModelState(this.model.content, new ContentModelState(this.model, this.next, this.value + 1L)).advance(paramObject);
      }
      if (this.value != 0L)
      {
        if (this.next != null) {
          return this.next.advance(paramObject);
        }
        return null;
      }
      break;
    case 42: 
      if (this.model.first(paramObject)) {
        return new ContentModelState(this.model.content, this).advance(paramObject);
      }
      if (this.next != null) {
        return this.next.advance(paramObject);
      }
      return null;
    case 63: 
      if (this.model.first(paramObject)) {
        return new ContentModelState(this.model.content, this.next).advance(paramObject);
      }
      if (this.next != null) {
        return this.next.advance(paramObject);
      }
      return null;
    case 124: 
      for (localContentModel = (ContentModel)this.model.content; localContentModel != null; localContentModel = localContentModel.next) {
        if (localContentModel.first(paramObject)) {
          return new ContentModelState(localContentModel, this.next).advance(paramObject);
        }
      }
      break;
    case 44: 
      localContentModel = (ContentModel)this.model.content;
      i = 0;
      while (i < this.value)
      {
        i++;
        localContentModel = localContentModel.next;
      }
      if ((localContentModel.first(paramObject)) || (localContentModel.empty()))
      {
        if (localContentModel.next == null) {
          return new ContentModelState(localContentModel, this.next).advance(paramObject);
        }
        return new ContentModelState(localContentModel, new ContentModelState(this.model, this.next, this.value + 1L)).advance(paramObject);
      }
      break;
    case 38: 
      localContentModel = (ContentModel)this.model.content;
      i = 1;
      int j = 0;
      while (localContentModel != null)
      {
        if ((this.value & 1L << j) == 0L)
        {
          if (localContentModel.first(paramObject)) {
            return new ContentModelState(localContentModel, new ContentModelState(this.model, this.next, this.value | 1L << j)).advance(paramObject);
          }
          if (!localContentModel.empty()) {
            i = 0;
          }
        }
        j++;
        localContentModel = localContentModel.next;
      }
      if (i != 0)
      {
        if (this.next != null) {
          return this.next.advance(paramObject);
        }
        return null;
      }
      break;
    default: 
      if (this.model.content == paramObject)
      {
        if ((this.next == null) && ((paramObject instanceof Element)) && (((Element)paramObject).content != null)) {
          return new ContentModelState(((Element)paramObject).content);
        }
        return this.next;
      }
      break;
    }
    return null;
  }
}
