package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent
  extends EventObject
{
  private static final long serialVersionUID = 7042693688939648123L;
  private String propertyName;
  private Object newValue;
  private Object oldValue;
  private Object propagationId;
  
  public PropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3)
  {
    super(paramObject1);
    this.propertyName = paramString;
    this.newValue = paramObject3;
    this.oldValue = paramObject2;
  }
  
  public String getPropertyName()
  {
    return this.propertyName;
  }
  
  public Object getNewValue()
  {
    return this.newValue;
  }
  
  public Object getOldValue()
  {
    return this.oldValue;
  }
  
  public void setPropagationId(Object paramObject)
  {
    this.propagationId = paramObject;
  }
  
  public Object getPropagationId()
  {
    return this.propagationId;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(getClass().getName());
    localStringBuilder.append("[propertyName=").append(getPropertyName());
    appendTo(localStringBuilder);
    localStringBuilder.append("; oldValue=").append(getOldValue());
    localStringBuilder.append("; newValue=").append(getNewValue());
    localStringBuilder.append("; propagationId=").append(getPropagationId());
    localStringBuilder.append("; source=").append(getSource());
    return "]";
  }
  
  void appendTo(StringBuilder paramStringBuilder) {}
}
