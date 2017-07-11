package javax.management;

class InstanceOfQueryExp
  extends QueryEval
  implements QueryExp
{
  private static final long serialVersionUID = -1081892073854801359L;
  private StringValueExp classNameValue;
  
  public InstanceOfQueryExp(StringValueExp paramStringValueExp)
  {
    if (paramStringValueExp == null) {
      throw new IllegalArgumentException("Null class name.");
    }
    this.classNameValue = paramStringValueExp;
  }
  
  public StringValueExp getClassNameValue()
  {
    return this.classNameValue;
  }
  
  public boolean apply(ObjectName paramObjectName)
    throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException
  {
    StringValueExp localStringValueExp;
    try
    {
      localStringValueExp = (StringValueExp)this.classNameValue.apply(paramObjectName);
    }
    catch (ClassCastException localClassCastException)
    {
      BadStringOperationException localBadStringOperationException = new BadStringOperationException(localClassCastException.toString());
      localBadStringOperationException.initCause(localClassCastException);
      throw localBadStringOperationException;
    }
    try
    {
      return getMBeanServer().isInstanceOf(paramObjectName, localStringValueExp.getValue());
    }
    catch (InstanceNotFoundException localInstanceNotFoundException) {}
    return false;
  }
  
  public String toString()
  {
    return "InstanceOf " + this.classNameValue.toString();
  }
}
