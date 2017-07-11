package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.UnionType;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleTypeVisitor7<R, P>
  extends SimpleTypeVisitor6<R, P>
{
  protected SimpleTypeVisitor7()
  {
    super(null);
  }
  
  protected SimpleTypeVisitor7(R paramR)
  {
    super(paramR);
  }
  
  public R visitUnion(UnionType paramUnionType, P paramP)
  {
    return defaultAction(paramUnionType, paramP);
  }
}
