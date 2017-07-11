package java.awt.print;

public abstract interface Pageable
{
  public static final int UNKNOWN_NUMBER_OF_PAGES = -1;
  
  public abstract int getNumberOfPages();
  
  public abstract PageFormat getPageFormat(int paramInt)
    throws IndexOutOfBoundsException;
  
  public abstract Printable getPrintable(int paramInt)
    throws IndexOutOfBoundsException;
}
