package sun.text;

public final class SupplementaryCharacterData
  implements Cloneable
{
  private static final byte IGNORE = -1;
  private int[] dataTable;
  
  public SupplementaryCharacterData(int[] paramArrayOfInt)
  {
    this.dataTable = paramArrayOfInt;
  }
  
  public int getValue(int paramInt)
  {
    assert ((paramInt >= 65536) && (paramInt <= 1114111)) : ("Invalid code point:" + Integer.toHexString(paramInt));
    int i = 0;
    int j = this.dataTable.length - 1;
    for (;;)
    {
      int k = (i + j) / 2;
      int m = this.dataTable[k] >> 8;
      int n = this.dataTable[(k + 1)] >> 8;
      if (paramInt < m)
      {
        j = k;
      }
      else if (paramInt > n - 1)
      {
        i = k;
      }
      else
      {
        int i1 = this.dataTable[k] & 0xFF;
        return i1 == 255 ? -1 : i1;
      }
    }
  }
  
  public int[] getArray()
  {
    return this.dataTable;
  }
}
