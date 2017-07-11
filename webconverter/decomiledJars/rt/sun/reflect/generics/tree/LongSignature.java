package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class LongSignature
  implements BaseType
{
  private static final LongSignature singleton = new LongSignature();
  
  private LongSignature() {}
  
  public static LongSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitLongSignature(this);
  }
}
