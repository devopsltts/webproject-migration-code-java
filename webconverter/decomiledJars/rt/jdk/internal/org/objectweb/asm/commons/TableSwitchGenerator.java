package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Label;

public abstract interface TableSwitchGenerator
{
  public abstract void generateCase(int paramInt, Label paramLabel);
  
  public abstract void generateDefault();
}
