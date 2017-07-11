package com.sun.org.apache.xml.internal.dtm.ref;

import java.io.PrintStream;

public class DTMSafeStringPool
  extends DTMStringPool
{
  public DTMSafeStringPool() {}
  
  public synchronized void removeAllElements()
  {
    super.removeAllElements();
  }
  
  public synchronized String indexToString(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    return super.indexToString(paramInt);
  }
  
  public synchronized int stringToIndex(String paramString)
  {
    return super.stringToIndex(paramString);
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    String[] arrayOfString = { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine" };
    DTMSafeStringPool localDTMSafeStringPool = new DTMSafeStringPool();
    System.out.println("If no complaints are printed below, we passed initial test.");
    for (int i = 0; i <= 1; i++)
    {
      int k;
      for (int j = 0; j < arrayOfString.length; j++)
      {
        k = localDTMSafeStringPool.stringToIndex(arrayOfString[j]);
        if (k != j) {
          System.out.println("\tMismatch populating pool: assigned " + k + " for create " + j);
        }
      }
      for (j = 0; j < arrayOfString.length; j++)
      {
        k = localDTMSafeStringPool.stringToIndex(arrayOfString[j]);
        if (k != j) {
          System.out.println("\tMismatch in stringToIndex: returned " + k + " for lookup " + j);
        }
      }
      for (j = 0; j < arrayOfString.length; j++)
      {
        String str = localDTMSafeStringPool.indexToString(j);
        if (!arrayOfString[j].equals(str)) {
          System.out.println("\tMismatch in indexToString: returned" + str + " for lookup " + j);
        }
      }
      localDTMSafeStringPool.removeAllElements();
      System.out.println("\nPass " + i + " complete\n");
    }
  }
}
