package javax.print.event;

import javax.print.PrintService;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintServiceAttributeSet;

public class PrintServiceAttributeEvent
  extends PrintEvent
{
  private static final long serialVersionUID = -7565987018140326600L;
  private PrintServiceAttributeSet attributes;
  
  public PrintServiceAttributeEvent(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet)
  {
    super(paramPrintService);
    this.attributes = AttributeSetUtilities.unmodifiableView(paramPrintServiceAttributeSet);
  }
  
  public PrintService getPrintService()
  {
    return (PrintService)getSource();
  }
  
  public PrintServiceAttributeSet getAttributes()
  {
    return this.attributes;
  }
}
