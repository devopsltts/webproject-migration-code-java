package com.sun.management;

import jdk.Exported;

@Exported
public abstract interface GarbageCollectorMXBean
  extends java.lang.management.GarbageCollectorMXBean
{
  public abstract GcInfo getLastGcInfo();
}
