package jdk.internal.org.objectweb.asm.tree.analysis;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

public class AnalyzerException
  extends Exception
{
  public final AbstractInsnNode node;
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString)
  {
    super(paramString);
    this.node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.node = paramAbstractInsnNode;
  }
  
  public AnalyzerException(AbstractInsnNode paramAbstractInsnNode, String paramString, Object paramObject, Value paramValue)
  {
    super((paramString == null ? "Expected " : new StringBuilder().append(paramString).append(": expected ").toString()) + paramObject + ", but found " + paramValue);
    this.node = paramAbstractInsnNode;
  }
}
