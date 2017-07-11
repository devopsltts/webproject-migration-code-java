package com.sun.xml.internal.bind.v2.runtime.property;

import java.text.MessageFormat;
import java.util.ResourceBundle;

 enum Messages
{
  UNSUBSTITUTABLE_TYPE,  UNEXPECTED_JAVA_TYPE;
  
  private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());
  
  private Messages() {}
  
  public String toString()
  {
    return format(new Object[0]);
  }
  
  public String format(Object... paramVarArgs)
  {
    return MessageFormat.format(rb.getString(name()), paramVarArgs);
  }
}
