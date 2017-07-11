package javax.management;

import java.util.Vector;

public class AttributeChangeNotificationFilter
  implements NotificationFilter
{
  private static final long serialVersionUID = -6347317584796410029L;
  private Vector<String> enabledAttributes = new Vector();
  
  public AttributeChangeNotificationFilter() {}
  
  public synchronized boolean isNotificationEnabled(Notification paramNotification)
  {
    String str1 = paramNotification.getType();
    if ((str1 == null) || (!str1.equals("jmx.attribute.change")) || (!(paramNotification instanceof AttributeChangeNotification))) {
      return false;
    }
    String str2 = ((AttributeChangeNotification)paramNotification).getAttributeName();
    return this.enabledAttributes.contains(str2);
  }
  
  public synchronized void enableAttribute(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("The name cannot be null.");
    }
    if (!this.enabledAttributes.contains(paramString)) {
      this.enabledAttributes.addElement(paramString);
    }
  }
  
  public synchronized void disableAttribute(String paramString)
  {
    this.enabledAttributes.removeElement(paramString);
  }
  
  public synchronized void disableAllAttributes()
  {
    this.enabledAttributes.removeAllElements();
  }
  
  public synchronized Vector<String> getEnabledAttributes()
  {
    return this.enabledAttributes;
  }
}
