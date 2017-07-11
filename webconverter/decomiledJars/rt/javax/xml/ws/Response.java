package javax.xml.ws;

import java.util.Map;
import java.util.concurrent.Future;

public abstract interface Response<T>
  extends Future<T>
{
  public abstract Map<String, Object> getContext();
}
