package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;

class NullProvider
  extends ProviderSkeleton
{
  NullProvider(Class<? extends Provider> paramClass)
  {
    super(paramClass);
  }
  
  protected ProbeSkeleton createProbe(Method paramMethod)
  {
    return new NullProbe(paramMethod.getParameterTypes());
  }
}
