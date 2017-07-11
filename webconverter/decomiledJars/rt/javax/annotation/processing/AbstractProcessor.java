package javax.annotation.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

public abstract class AbstractProcessor
  implements Processor
{
  protected ProcessingEnvironment processingEnv;
  private boolean initialized = false;
  
  protected AbstractProcessor() {}
  
  public Set<String> getSupportedOptions()
  {
    SupportedOptions localSupportedOptions = (SupportedOptions)getClass().getAnnotation(SupportedOptions.class);
    if (localSupportedOptions == null) {
      return Collections.emptySet();
    }
    return arrayToSet(localSupportedOptions.value());
  }
  
  public Set<String> getSupportedAnnotationTypes()
  {
    SupportedAnnotationTypes localSupportedAnnotationTypes = (SupportedAnnotationTypes)getClass().getAnnotation(SupportedAnnotationTypes.class);
    if (localSupportedAnnotationTypes == null)
    {
      if (isInitialized()) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedAnnotationTypes annotation found on " + getClass().getName() + ", returning an empty set.");
      }
      return Collections.emptySet();
    }
    return arrayToSet(localSupportedAnnotationTypes.value());
  }
  
  public SourceVersion getSupportedSourceVersion()
  {
    SupportedSourceVersion localSupportedSourceVersion = (SupportedSourceVersion)getClass().getAnnotation(SupportedSourceVersion.class);
    SourceVersion localSourceVersion = null;
    if (localSupportedSourceVersion == null)
    {
      localSourceVersion = SourceVersion.RELEASE_6;
      if (isInitialized()) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "No SupportedSourceVersion annotation found on " + getClass().getName() + ", returning " + localSourceVersion + ".");
      }
    }
    else
    {
      localSourceVersion = localSupportedSourceVersion.value();
    }
    return localSourceVersion;
  }
  
  public synchronized void init(ProcessingEnvironment paramProcessingEnvironment)
  {
    if (this.initialized) {
      throw new IllegalStateException("Cannot call init more than once.");
    }
    Objects.requireNonNull(paramProcessingEnvironment, "Tool provided null ProcessingEnvironment");
    this.processingEnv = paramProcessingEnvironment;
    this.initialized = true;
  }
  
  public abstract boolean process(Set<? extends TypeElement> paramSet, RoundEnvironment paramRoundEnvironment);
  
  public Iterable<? extends Completion> getCompletions(Element paramElement, AnnotationMirror paramAnnotationMirror, ExecutableElement paramExecutableElement, String paramString)
  {
    return Collections.emptyList();
  }
  
  protected synchronized boolean isInitialized()
  {
    return this.initialized;
  }
  
  private static Set<String> arrayToSet(String[] paramArrayOfString)
  {
    assert (paramArrayOfString != null);
    HashSet localHashSet = new HashSet(paramArrayOfString.length);
    for (String str : paramArrayOfString) {
      localHashSet.add(str);
    }
    return Collections.unmodifiableSet(localHashSet);
  }
}
