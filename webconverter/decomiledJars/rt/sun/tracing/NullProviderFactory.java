package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;

public class NullProviderFactory
  extends ProviderFactory
{
  public NullProviderFactory() {}
  
  public <T extends Provider> T createProvider(Class<T> paramClass)
  {
    NullProvider localNullProvider = new NullProvider(paramClass);
    localNullProvider.init();
    return localNullProvider.newProxyInstance();
  }
}
