package sun.util.locale.provider;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class FallbackLocaleProviderAdapter
  extends JRELocaleProviderAdapter
{
  private static final Set<String> rootTagSet = Collections.singleton(Locale.ROOT.toLanguageTag());
  private final LocaleResources rootLocaleResources = new LocaleResources(this, Locale.ROOT);
  
  public FallbackLocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.FALLBACK;
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale)
  {
    return this.rootLocaleResources;
  }
  
  protected Set<String> createLanguageTagSet(String paramString)
  {
    return rootTagSet;
  }
}
