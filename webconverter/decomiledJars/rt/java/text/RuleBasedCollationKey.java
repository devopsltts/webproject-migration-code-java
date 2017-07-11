package java.text;

final class RuleBasedCollationKey
  extends CollationKey
{
  private String key = null;
  
  public int compareTo(CollationKey paramCollationKey)
  {
    int i = this.key.compareTo(((RuleBasedCollationKey)paramCollationKey).key);
    if (i <= -1) {
      return -1;
    }
    if (i >= 1) {
      return 1;
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!getClass().equals(paramObject.getClass()))) {
      return false;
    }
    RuleBasedCollationKey localRuleBasedCollationKey = (RuleBasedCollationKey)paramObject;
    return this.key.equals(localRuleBasedCollationKey.key);
  }
  
  public int hashCode()
  {
    return this.key.hashCode();
  }
  
  public byte[] toByteArray()
  {
    char[] arrayOfChar = this.key.toCharArray();
    byte[] arrayOfByte = new byte[2 * arrayOfChar.length];
    int i = 0;
    for (int j = 0; j < arrayOfChar.length; j++)
    {
      arrayOfByte[(i++)] = ((byte)(arrayOfChar[j] >>> '\b'));
      arrayOfByte[(i++)] = ((byte)(arrayOfChar[j] & 0xFF));
    }
    return arrayOfByte;
  }
  
  RuleBasedCollationKey(String paramString1, String paramString2)
  {
    super(paramString1);
    this.key = paramString2;
  }
}
