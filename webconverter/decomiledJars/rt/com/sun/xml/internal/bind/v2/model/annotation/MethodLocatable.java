package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class MethodLocatable<M>
  implements Locatable
{
  private final Locatable upstream;
  private final M method;
  private final Navigator<?, ?, ?, M> nav;
  
  public MethodLocatable(Locatable paramLocatable, M paramM, Navigator<?, ?, ?, M> paramNavigator)
  {
    this.upstream = paramLocatable;
    this.method = paramM;
    this.nav = paramNavigator;
  }
  
  public Locatable getUpstream()
  {
    return this.upstream;
  }
  
  public Location getLocation()
  {
    return this.nav.getMethodLocation(this.method);
  }
}
