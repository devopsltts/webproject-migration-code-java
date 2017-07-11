package javax.print;

public abstract interface CancelablePrintJob
  extends DocPrintJob
{
  public abstract void cancel()
    throws PrintException;
}
