package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterInfo
  extends TextSyntax
  implements PrintServiceAttribute
{
  private static final long serialVersionUID = 7765280618777599727L;
  
  public PrinterInfo(String paramString, Locale paramLocale)
  {
    super(paramString, paramLocale);
  }
  
  public boolean equals(Object paramObject)
  {
    return (super.equals(paramObject)) && ((paramObject instanceof PrinterInfo));
  }
  
  public final Class<? extends Attribute> getCategory()
  {
    return PrinterInfo.class;
  }
  
  public final String getName()
  {
    return "printer-info";
  }
}
