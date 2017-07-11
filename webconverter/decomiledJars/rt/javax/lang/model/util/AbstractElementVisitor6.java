package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.UnknownElementException;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractElementVisitor6<R, P>
  implements ElementVisitor<R, P>
{
  protected AbstractElementVisitor6() {}
  
  public final R visit(Element paramElement, P paramP)
  {
    return paramElement.accept(this, paramP);
  }
  
  public final R visit(Element paramElement)
  {
    return paramElement.accept(this, null);
  }
  
  public R visitUnknown(Element paramElement, P paramP)
  {
    throw new UnknownElementException(paramElement, paramP);
  }
}
