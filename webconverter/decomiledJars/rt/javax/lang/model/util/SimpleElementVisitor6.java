package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleElementVisitor6<R, P>
  extends AbstractElementVisitor6<R, P>
{
  protected final R DEFAULT_VALUE;
  
  protected SimpleElementVisitor6()
  {
    this.DEFAULT_VALUE = null;
  }
  
  protected SimpleElementVisitor6(R paramR)
  {
    this.DEFAULT_VALUE = paramR;
  }
  
  protected R defaultAction(Element paramElement, P paramP)
  {
    return this.DEFAULT_VALUE;
  }
  
  public R visitPackage(PackageElement paramPackageElement, P paramP)
  {
    return defaultAction(paramPackageElement, paramP);
  }
  
  public R visitType(TypeElement paramTypeElement, P paramP)
  {
    return defaultAction(paramTypeElement, paramP);
  }
  
  public R visitVariable(VariableElement paramVariableElement, P paramP)
  {
    if (paramVariableElement.getKind() != ElementKind.RESOURCE_VARIABLE) {
      return defaultAction(paramVariableElement, paramP);
    }
    return visitUnknown(paramVariableElement, paramP);
  }
  
  public R visitExecutable(ExecutableElement paramExecutableElement, P paramP)
  {
    return defaultAction(paramExecutableElement, paramP);
  }
  
  public R visitTypeParameter(TypeParameterElement paramTypeParameterElement, P paramP)
  {
    return defaultAction(paramTypeParameterElement, paramP);
  }
}
