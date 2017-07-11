package sun.util.cldr;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;
import sun.util.locale.provider.JRELocaleProviderAdapter;
import sun.util.locale.provider.LocaleProviderAdapter.Type;

public class CLDRLocaleProviderAdapter
  extends JRELocaleProviderAdapter
{
  private static final String LOCALE_DATA_JAR_NAME = "cldrdata.jar";
  
  public CLDRLocaleProviderAdapter()
  {
    String str1 = File.separator;
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + str1 + "lib" + str1 + "ext" + str1 + "cldrdata.jar";
    final File localFile = new File(str2);
    boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return Boolean.valueOf(localFile.exists());
      }
    })).booleanValue();
    if (!bool) {
      throw new UnsupportedOperationException();
    }
  }
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.CLDR;
  }
  
  public BreakIteratorProvider getBreakIteratorProvider()
  {
    return null;
  }
  
  public CollatorProvider getCollatorProvider()
  {
    return null;
  }
  
  public Locale[] getAvailableLocales()
  {
    Set localSet = createLanguageTagSet("All");
    Locale[] arrayOfLocale = new Locale[localSet.size()];
    int i = 0;
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      arrayOfLocale[(i++)] = Locale.forLanguageTag(str);
    }
    return arrayOfLocale;
  }
  
  protected Set<String> createLanguageTagSet(String paramString)
  {
    ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.util.cldr.CLDRLocaleDataMetaInfo", Locale.ROOT);
    String str = localResourceBundle.getString(paramString);
    if (str == null) {
      return Collections.emptySet();
    }
    HashSet localHashSet = new HashSet();
    StringTokenizer localStringTokenizer = new StringTokenizer(str);
    while (localStringTokenizer.hasMoreTokens()) {
      localHashSet.add(localStringTokenizer.nextToken());
    }
    return localHashSet;
  }
}
