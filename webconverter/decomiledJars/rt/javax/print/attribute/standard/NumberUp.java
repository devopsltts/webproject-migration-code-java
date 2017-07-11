package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class NumberUp
  extends IntegerSyntax
  implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
  private static final long serialVersionUID = -3040436486786527811L;
  
  public NumberUp(int paramInt)
  {
    super(paramInt, 1, Integer.MAX_VALUE);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof NumberUp));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return NumberUp.class;
  }
  
  public final String getName()
  {
    return "number-up";
  }
}
