package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;

public class BadAttributeValueExpException
  extends Exception
{
  private static final long serialVersionUID = -3105272988410493376L;
  private Object val;
  
  public BadAttributeValueExpException(Object paramObject)
  {
    this.val = (paramObject == null ? null : paramObject.toString());
  }
  
  public String toString()
  {
    return "BadAttributeValueException: " + this.val;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Object localObject = localGetField.get("val", null);
    if (localObject == null) {
      this.val = null;
    } else if ((localObject instanceof String)) {
      this.val = localObject;
    } else if ((System.getSecurityManager() == null) || ((localObject instanceof Long)) || ((localObject instanceof Integer)) || ((localObject instanceof Float)) || ((localObject instanceof Double)) || ((localObject instanceof Byte)) || ((localObject instanceof Short)) || ((localObject instanceof Boolean))) {
      this.val = localObject.toString();
    } else {
      this.val = (System.identityHashCode(localObject) + "@" + localObject.getClass().getName());
    }
  }
}
