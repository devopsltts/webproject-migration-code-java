package javax.print.attribute;

import java.io.Serializable;

public abstract interface Attribute
  extends Serializable
{
  public abstract Class<? extends Attribute> getCategory();
  
  public abstract String getName();
}
