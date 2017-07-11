package java.beans;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

public class PropertyEditorSupport
  implements PropertyEditor
{
  private Object value;
  private Object source;
  private Vector<PropertyChangeListener> listeners;
  
  public PropertyEditorSupport()
  {
    setSource(this);
  }
  
  public PropertyEditorSupport(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException();
    }
    setSource(paramObject);
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public void setSource(Object paramObject)
  {
    this.source = paramObject;
  }
  
  public void setValue(Object paramObject)
  {
    this.value = paramObject;
    firePropertyChange();
  }
  
  public Object getValue()
  {
    return this.value;
  }
  
  public boolean isPaintable()
  {
    return false;
  }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {}
  
  public String getJavaInitializationString()
  {
    return "???";
  }
  
  public String getAsText()
  {
    return this.value != null ? this.value.toString() : null;
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    if ((this.value instanceof String))
    {
      setValue(paramString);
      return;
    }
    throw new IllegalArgumentException(paramString);
  }
  
  public String[] getTags()
  {
    return null;
  }
  
  public Component getCustomEditor()
  {
    return null;
  }
  
  public boolean supportsCustomEditor()
  {
    return false;
  }
  
  public synchronized void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (this.listeners == null) {
      this.listeners = new Vector();
    }
    this.listeners.addElement(paramPropertyChangeListener);
  }
  
  public synchronized void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    if (this.listeners == null) {
      return;
    }
    this.listeners.removeElement(paramPropertyChangeListener);
  }
  
  public void firePropertyChange()
  {
    Vector localVector;
    synchronized (this)
    {
      if (this.listeners == null) {
        return;
      }
      localVector = unsafeClone(this.listeners);
    }
    ??? = new PropertyChangeEvent(this.source, null, null, null);
    for (int i = 0; i < localVector.size(); i++)
    {
      PropertyChangeListener localPropertyChangeListener = (PropertyChangeListener)localVector.elementAt(i);
      localPropertyChangeListener.propertyChange((PropertyChangeEvent)???);
    }
  }
  
  private <T> Vector<T> unsafeClone(Vector<T> paramVector)
  {
    return (Vector)paramVector.clone();
  }
}
