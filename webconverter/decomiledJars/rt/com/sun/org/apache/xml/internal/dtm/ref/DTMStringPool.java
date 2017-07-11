package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.utils.IntVector;
import java.io.PrintStream;
import java.util.Vector;

public class DTMStringPool
{
  Vector m_intToString = new Vector();
  static final int HASHPRIME = 101;
  int[] m_hashStart = new int[101];
  IntVector m_hashChain;
  public static final int NULL = -1;
  
  public DTMStringPool(int paramInt)
  {
    this.m_hashChain = new IntVector(paramInt);
    removeAllElements();
    stringToIndex("");
  }
  
  public DTMStringPool()
  {
    this(512);
  }
  
  public void removeAllElements()
  {
    this.m_intToString.removeAllElements();
    for (int i = 0; i < 101; i++) {
      this.m_hashStart[i] = -1;
    }
    this.m_hashChain.removeAllElements();
  }
  
  public String indexToString(int paramInt)
    throws ArrayIndexOutOfBoundsException
  {
    if (paramInt == -1) {
      return null;
    }
    return (String)this.m_intToString.elementAt(paramInt);
  }
  
  public int stringToIndex(String paramString)
  {
    if (paramString == null) {
      return -1;
    }
    int i = paramString.hashCode() % 101;
    if (i < 0) {
      i = -i;
    }
    int j = this.m_hashStart[i];
    for (int k = j; k != -1; k = this.m_hashChain.elementAt(k))
    {
      if (this.m_intToString.elementAt(k).equals(paramString)) {
        return k;
      }
      j = k;
    }
    int m = this.m_intToString.size();
    this.m_intToString.addElement(paramString);
    this.m_hashChain.addElement(-1);
    if (j == -1) {
      this.m_hashStart[i] = m;
    } else {
      this.m_hashChain.setElementAt(m, j);
    }
    return m;
  }
  
  public static void _main(String[] paramArrayOfString)
  {
    String[] arrayOfString = { "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty", "Twenty-One", "Twenty-Two", "Twenty-Three", "Twenty-Four", "Twenty-Five", "Twenty-Six", "Twenty-Seven", "Twenty-Eight", "Twenty-Nine", "Thirty", "Thirty-One", "Thirty-Two", "Thirty-Three", "Thirty-Four", "Thirty-Five", "Thirty-Six", "Thirty-Seven", "Thirty-Eight", "Thirty-Nine" };
    DTMStringPool localDTMStringPool = new DTMStringPool();
    System.out.println("If no complaints are printed below, we passed initial test.");
    for (int i = 0; i <= 1; i++)
    {
      int k;
      for (int j = 0; j < arrayOfString.length; j++)
      {
        k = localDTMStringPool.stringToIndex(arrayOfString[j]);
        if (k != j) {
          System.out.println("\tMismatch populating pool: assigned " + k + " for create " + j);
        }
      }
      for (j = 0; j < arrayOfString.length; j++)
      {
        k = localDTMStringPool.stringToIndex(arrayOfString[j]);
        if (k != j) {
          System.out.println("\tMismatch in stringToIndex: returned " + k + " for lookup " + j);
        }
      }
      for (j = 0; j < arrayOfString.length; j++)
      {
        String str = localDTMStringPool.indexToString(j);
        if (!arrayOfString[j].equals(str)) {
          System.out.println("\tMismatch in indexToString: returned" + str + " for lookup " + j);
        }
      }
      localDTMStringPool.removeAllElements();
      System.out.println("\nPass " + i + " complete\n");
    }
  }
}
