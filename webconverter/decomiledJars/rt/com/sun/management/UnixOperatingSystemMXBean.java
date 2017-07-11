package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface UnixOperatingSystemMXBean
  extends OperatingSystemMXBean
{
  public abstract long getOpenFileDescriptorCount();
  
  public abstract long getMaxFileDescriptorCount();
}
