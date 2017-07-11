package sun.util.locale;

import java.lang.ref.SoftReference;

public final class BaseLocale
{
  public static final String SEP = "_";
  private static final Cache CACHE = new Cache();
  private final String language;
  private final String script;
  private final String region;
  private final String variant;
  private volatile int hash = 0;
  
  private BaseLocale(String paramString1, String paramString2)
  {
    this.language = paramString1;
    this.script = "";
    this.region = paramString2;
    this.variant = "";
  }
  
  private BaseLocale(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.language = (paramString1 != null ? LocaleUtils.toLowerString(paramString1).intern() : "");
    this.script = (paramString2 != null ? LocaleUtils.toTitleString(paramString2).intern() : "");
    this.region = (paramString3 != null ? LocaleUtils.toUpperString(paramString3).intern() : "");
    this.variant = (paramString4 != null ? paramString4.intern() : "");
  }
  
  public static BaseLocale createInstance(String paramString1, String paramString2)
  {
    BaseLocale localBaseLocale = new BaseLocale(paramString1, paramString2);
    CACHE.put(new Key(paramString1, paramString2, null), localBaseLocale);
    return localBaseLocale;
  }
  
  public static BaseLocale getInstance(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    if (paramString1 != null) {
      if (LocaleUtils.caseIgnoreMatch(paramString1, "he")) {
        paramString1 = "iw";
      } else if (LocaleUtils.caseIgnoreMatch(paramString1, "yi")) {
        paramString1 = "ji";
      } else if (LocaleUtils.caseIgnoreMatch(paramString1, "id")) {
        paramString1 = "in";
      }
    }
    Key localKey = new Key(paramString1, paramString2, paramString3, paramString4);
    BaseLocale localBaseLocale = (BaseLocale)CACHE.get(localKey);
    return localBaseLocale;
  }
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public String getScript()
  {
    return this.script;
  }
  
  public String getRegion()
  {
    return this.region;
  }
  
  public String getVariant()
  {
    return this.variant;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof BaseLocale)) {
      return false;
    }
    BaseLocale localBaseLocale = (BaseLocale)paramObject;
    return (this.language == localBaseLocale.language) && (this.script == localBaseLocale.script) && (this.region == localBaseLocale.region) && (this.variant == localBaseLocale.variant);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.language.length() > 0)
    {
      localStringBuilder.append("language=");
      localStringBuilder.append(this.language);
    }
    if (this.script.length() > 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append("script=");
      localStringBuilder.append(this.script);
    }
    if (this.region.length() > 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append("region=");
      localStringBuilder.append(this.region);
    }
    if (this.variant.length() > 0)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append("variant=");
      localStringBuilder.append(this.variant);
    }
    return localStringBuilder.toString();
  }
  
  public int hashCode()
  {
    int i = this.hash;
    if (i == 0)
    {
      i = this.language.hashCode();
      i = 31 * i + this.script.hashCode();
      i = 31 * i + this.region.hashCode();
      i = 31 * i + this.variant.hashCode();
      this.hash = i;
    }
    return i;
  }
  
  private static class Cache
    extends LocaleObjectCache<BaseLocale.Key, BaseLocale>
  {
    public Cache() {}
    
    protected BaseLocale.Key normalizeKey(BaseLocale.Key paramKey)
    {
      assert ((BaseLocale.Key.access$100(paramKey).get() != null) && (BaseLocale.Key.access$200(paramKey).get() != null) && (BaseLocale.Key.access$300(paramKey).get() != null) && (BaseLocale.Key.access$400(paramKey).get() != null));
      return BaseLocale.Key.normalize(paramKey);
    }
    
    protected BaseLocale createObject(BaseLocale.Key paramKey)
    {
      return new BaseLocale((String)BaseLocale.Key.access$100(paramKey).get(), (String)BaseLocale.Key.access$200(paramKey).get(), (String)BaseLocale.Key.access$300(paramKey).get(), (String)BaseLocale.Key.access$400(paramKey).get(), null);
    }
  }
  
  private static final class Key
  {
    private final SoftReference<String> lang;
    private final SoftReference<String> scrt;
    private final SoftReference<String> regn;
    private final SoftReference<String> vart;
    private final boolean normalized;
    private final int hash;
    
    private Key(String paramString1, String paramString2)
    {
      assert ((paramString1.intern() == paramString1) && (paramString2.intern() == paramString2));
      this.lang = new SoftReference(paramString1);
      this.scrt = new SoftReference("");
      this.regn = new SoftReference(paramString2);
      this.vart = new SoftReference("");
      this.normalized = true;
      int i = paramString1.hashCode();
      if (paramString2 != "")
      {
        int j = paramString2.length();
        for (int k = 0; k < j; k++) {
          i = 31 * i + LocaleUtils.toLower(paramString2.charAt(k));
        }
      }
      this.hash = i;
    }
    
    public Key(String paramString1, String paramString2, String paramString3, String paramString4)
    {
      this(paramString1, paramString2, paramString3, paramString4, false);
    }
    
    private Key(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean)
    {
      int i = 0;
      int j;
      int k;
      if (paramString1 != null)
      {
        this.lang = new SoftReference(paramString1);
        j = paramString1.length();
        for (k = 0; k < j; k++) {
          i = 31 * i + LocaleUtils.toLower(paramString1.charAt(k));
        }
      }
      else
      {
        this.lang = new SoftReference("");
      }
      if (paramString2 != null)
      {
        this.scrt = new SoftReference(paramString2);
        j = paramString2.length();
        for (k = 0; k < j; k++) {
          i = 31 * i + LocaleUtils.toLower(paramString2.charAt(k));
        }
      }
      else
      {
        this.scrt = new SoftReference("");
      }
      if (paramString3 != null)
      {
        this.regn = new SoftReference(paramString3);
        j = paramString3.length();
        for (k = 0; k < j; k++) {
          i = 31 * i + LocaleUtils.toLower(paramString3.charAt(k));
        }
      }
      else
      {
        this.regn = new SoftReference("");
      }
      if (paramString4 != null)
      {
        this.vart = new SoftReference(paramString4);
        j = paramString4.length();
        for (k = 0; k < j; k++) {
          i = 31 * i + paramString4.charAt(k);
        }
      }
      else
      {
        this.vart = new SoftReference("");
      }
      this.hash = i;
      this.normalized = paramBoolean;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (((paramObject instanceof Key)) && (this.hash == ((Key)paramObject).hash))
      {
        String str1 = (String)this.lang.get();
        String str2 = (String)((Key)paramObject).lang.get();
        if ((str1 != null) && (str2 != null) && (LocaleUtils.caseIgnoreMatch(str2, str1)))
        {
          String str3 = (String)this.scrt.get();
          String str4 = (String)((Key)paramObject).scrt.get();
          if ((str3 != null) && (str4 != null) && (LocaleUtils.caseIgnoreMatch(str4, str3)))
          {
            String str5 = (String)this.regn.get();
            String str6 = (String)((Key)paramObject).regn.get();
            if ((str5 != null) && (str6 != null) && (LocaleUtils.caseIgnoreMatch(str6, str5)))
            {
              String str7 = (String)this.vart.get();
              String str8 = (String)((Key)paramObject).vart.get();
              return (str8 != null) && (str8.equals(str7));
            }
          }
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.hash;
    }
    
    public static Key normalize(Key paramKey)
    {
      if (paramKey.normalized) {
        return paramKey;
      }
      String str1 = LocaleUtils.toLowerString((String)paramKey.lang.get()).intern();
      String str2 = LocaleUtils.toTitleString((String)paramKey.scrt.get()).intern();
      String str3 = LocaleUtils.toUpperString((String)paramKey.regn.get()).intern();
      String str4 = ((String)paramKey.vart.get()).intern();
      return new Key(str1, str2, str3, str4, true);
    }
  }
}
