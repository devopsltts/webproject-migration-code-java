package javax.print.attribute;

import java.io.Serializable;

public class HashPrintJobAttributeSet
  extends HashAttributeSet
  implements PrintJobAttributeSet, Serializable
{
  private static final long serialVersionUID = -4204473656070350348L;
  
  public HashPrintJobAttributeSet()
  {
    super(PrintJobAttribute.class);
  }
  
  public HashPrintJobAttributeSet(PrintJobAttribute paramPrintJobAttribute)
  {
    super(paramPrintJobAttribute, PrintJobAttribute.class);
  }
  
  public HashPrintJobAttributeSet(PrintJobAttribute[] paramArrayOfPrintJobAttribute)
  {
    super(paramArrayOfPrintJobAttribute, PrintJobAttribute.class);
  }
  
  public HashPrintJobAttributeSet(PrintJobAttributeSet paramPrintJobAttributeSet)
  {
    super(paramPrintJobAttributeSet, PrintJobAttribute.class);
  }
}
