package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class ColorSupported
  extends EnumSyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = -2700555589688535545L;
  public static final ColorSupported NOT_SUPPORTED = new ColorSupported(0);
  public static final ColorSupported SUPPORTED = new ColorSupported(1);
  private static final String[] myStringTable = { "not-supported", "supported" };
  private static final ColorSupported[] myEnumValueTable = { NOT_SUPPORTED, SUPPORTED };
  
  protected ColorSupported(int paramInt)
  {
    super(paramInt);
  }
  
  protected String[] getStringTable()
  {
    return myStringTable;
  }
  
  protected EnumSyntax[] getEnumValueTable()
  {
    return myEnumValueTable;
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return ColorSupported.class;
  }
  
  public final String getName()
  {
    return "color-supported";
  }
}
