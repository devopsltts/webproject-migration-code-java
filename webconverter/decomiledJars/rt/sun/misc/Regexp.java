package sun.misc;

public class Regexp
{
  public boolean ignoreCase;
  public String exp;
  public String prefix;
  public String suffix;
  public boolean exact;
  public int prefixLen;
  public int suffixLen;
  public int totalLen;
  public String[] mids;
  
  public Regexp(String paramString)
  {
    this.exp = paramString;
    int i = paramString.indexOf('*');
    int j = paramString.lastIndexOf('*');
    if (i < 0)
    {
      this.totalLen = paramString.length();
      this.exact = true;
    }
    else
    {
      this.prefixLen = i;
      if (i == 0) {
        this.prefix = null;
      } else {
        this.prefix = paramString.substring(0, i);
      }
      this.suffixLen = (paramString.length() - j - 1);
      if (this.suffixLen == 0) {
        this.suffix = null;
      } else {
        this.suffix = paramString.substring(j + 1);
      }
      int k = 0;
      for (int m = i; (m < j) && (m >= 0); m = paramString.indexOf('*', m + 1)) {
        k++;
      }
      this.totalLen = (this.prefixLen + this.suffixLen);
      if (k > 0)
      {
        this.mids = new String[k];
        m = i;
        for (int n = 0; n < k; n++)
        {
          m++;
          int i1 = paramString.indexOf('*', m);
          if (m < i1)
          {
            this.mids[n] = paramString.substring(m, i1);
            this.totalLen += this.mids[n].length();
          }
          m = i1;
        }
      }
    }
  }
  
  final boolean matches(String paramString)
  {
    return matches(paramString, 0, paramString.length());
  }
  
  boolean matches(String paramString, int paramInt1, int paramInt2)
  {
    if (this.exact) {
      return (paramInt2 == this.totalLen) && (this.exp.regionMatches(this.ignoreCase, 0, paramString, paramInt1, paramInt2));
    }
    if (paramInt2 < this.totalLen) {
      return false;
    }
    if (((this.prefixLen > 0) && (!this.prefix.regionMatches(this.ignoreCase, 0, paramString, paramInt1, this.prefixLen))) || ((this.suffixLen > 0) && (!this.suffix.regionMatches(this.ignoreCase, 0, paramString, paramInt1 + paramInt2 - this.suffixLen, this.suffixLen)))) {
      return false;
    }
    if (this.mids == null) {
      return true;
    }
    int i = this.mids.length;
    int j = paramInt1 + this.prefixLen;
    int k = paramInt1 + paramInt2 - this.suffixLen;
    for (int m = 0; m < i; m++)
    {
      String str = this.mids[m];
      int n = str.length();
      while ((j + n <= k) && (!str.regionMatches(this.ignoreCase, 0, paramString, j, n))) {
        j++;
      }
      if (j + n > k) {
        return false;
      }
      j += n;
    }
    return true;
  }
}
