package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Vector;

public final class ContentModel
  implements Serializable
{
  public int type;
  public Object content;
  public ContentModel next;
  private boolean[] valSet;
  private boolean[] val;
  
  public ContentModel() {}
  
  public ContentModel(Element paramElement)
  {
    this(0, paramElement, null);
  }
  
  public ContentModel(int paramInt, ContentModel paramContentModel)
  {
    this(paramInt, paramContentModel, null);
  }
  
  public ContentModel(int paramInt, Object paramObject, ContentModel paramContentModel)
  {
    this.type = paramInt;
    this.content = paramObject;
    this.next = paramContentModel;
  }
  
  public boolean empty()
  {
    ContentModel localContentModel;
    switch (this.type)
    {
    case 42: 
    case 63: 
      return true;
    case 43: 
    case 124: 
      for (localContentModel = (ContentModel)this.content; localContentModel != null; localContentModel = localContentModel.next) {
        if (localContentModel.empty()) {
          return true;
        }
      }
      return false;
    case 38: 
    case 44: 
      for (localContentModel = (ContentModel)this.content; localContentModel != null; localContentModel = localContentModel.next) {
        if (!localContentModel.empty()) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public void getElements(Vector<Element> paramVector)
  {
    switch (this.type)
    {
    case 42: 
    case 43: 
    case 63: 
      ((ContentModel)this.content).getElements(paramVector);
      break;
    case 38: 
    case 44: 
    case 124: 
      for (ContentModel localContentModel = (ContentModel)this.content; localContentModel != null; localContentModel = localContentModel.next) {
        localContentModel.getElements(paramVector);
      }
      break;
    default: 
      paramVector.addElement((Element)this.content);
    }
  }
  
  public boolean first(Object paramObject)
  {
    Object localObject;
    switch (this.type)
    {
    case 42: 
    case 43: 
    case 63: 
      return ((ContentModel)this.content).first(paramObject);
    case 44: 
      for (localObject = (ContentModel)this.content; localObject != null; localObject = ((ContentModel)localObject).next)
      {
        if (((ContentModel)localObject).first(paramObject)) {
          return true;
        }
        if (!((ContentModel)localObject).empty()) {
          return false;
        }
      }
      return false;
    case 38: 
    case 124: 
      localObject = (Element)paramObject;
      if ((this.valSet == null) || (this.valSet.length <= Element.getMaxIndex()))
      {
        this.valSet = new boolean[Element.getMaxIndex() + 1];
        this.val = new boolean[this.valSet.length];
      }
      if (this.valSet[localObject.index] != 0) {
        return this.val[localObject.index];
      }
      for (ContentModel localContentModel = (ContentModel)this.content; localContentModel != null; localContentModel = localContentModel.next) {
        if (localContentModel.first(paramObject))
        {
          this.val[localObject.index] = true;
          break;
        }
      }
      this.valSet[localObject.index] = true;
      return this.val[localObject.index];
    }
    return this.content == paramObject;
  }
  
  public Element first()
  {
    switch (this.type)
    {
    case 38: 
    case 42: 
    case 63: 
    case 124: 
      return null;
    case 43: 
    case 44: 
      return ((ContentModel)this.content).first();
    }
    return (Element)this.content;
  }
  
  public String toString()
  {
    switch (this.type)
    {
    case 42: 
      return this.content + "*";
    case 63: 
      return this.content + "?";
    case 43: 
      return this.content + "+";
    case 38: 
    case 44: 
    case 124: 
      char[] arrayOfChar = { ' ', (char)this.type, ' ' };
      String str = "";
      for (ContentModel localContentModel = (ContentModel)this.content; localContentModel != null; localContentModel = localContentModel.next)
      {
        str = str + localContentModel;
        if (localContentModel.next != null) {
          str = str + new String(arrayOfChar);
        }
      }
      return "(" + str + ")";
    }
    return this.content.toString();
  }
}
