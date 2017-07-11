package javax.management;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;

class ClassAttributeValueExp
  extends AttributeValueExp
{
  private static final long oldSerialVersionUID = -2212731951078526753L;
  private static final long newSerialVersionUID = -1081892073854801359L;
  private static final long serialVersionUID;
  private String attr = "Class";
  
  public ClassAttributeValueExp()
  {
    super("Class");
  }
  
  public ValueExp apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    Object localObject = getValue(paramObjectName);
    if ((localObject instanceof String)) {
      return new StringValueExp((String)localObject);
    }
    throw new BadAttributeValueExpException(localObject);
  }
  
  public String toString()
  {
    return this.attr;
  }
  
  protected Object getValue(ObjectName paramObjectName)
  {
    try
    {
      MBeanServer localMBeanServer = QueryEval.getMBeanServer();
      return localMBeanServer.getObjectInstance(paramObjectName).getClassName();
    }
    catch (Exception localException) {}
    return null;
  }
  
  static
  {
    int i = 0;
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      i = (str != null) && (str.equals("1.0")) ? 1 : 0;
    }
    catch (Exception localException) {}
    if (i != 0) {
      serialVersionUID = -2212731951078526753L;
    } else {
      serialVersionUID = -1081892073854801359L;
    }
  }
}
