package javax.print;

import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;

public abstract interface DocPrintJob
{
  public abstract PrintService getPrintService();
  
  public abstract PrintJobAttributeSet getAttributes();
  
  public abstract void addPrintJobListener(PrintJobListener paramPrintJobListener);
  
  public abstract void removePrintJobListener(PrintJobListener paramPrintJobListener);
  
  public abstract void addPrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener, PrintJobAttributeSet paramPrintJobAttributeSet);
  
  public abstract void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener);
  
  public abstract void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws PrintException;
}
