package jdk.internal.org.objectweb.asm.util;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;

public abstract interface Textifiable
{
  public abstract void textify(StringBuffer paramStringBuffer, Map<Label, String> paramMap);
}
