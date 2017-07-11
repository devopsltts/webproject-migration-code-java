package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class FieldNode
  extends FieldVisitor
{
  public int access;
  public String name;
  public String desc;
  public String signature;
  public Object value;
  public List<AnnotationNode> visibleAnnotations;
  public List<AnnotationNode> invisibleAnnotations;
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  public List<Attribute> attrs;
  
  public FieldNode(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    this(327680, paramInt, paramString1, paramString2, paramString3, paramObject);
    if (getClass() != FieldNode.class) {
      throw new IllegalStateException();
    }
  }
  
  public FieldNode(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    super(paramInt1);
    this.access = paramInt2;
    this.name = paramString1;
    this.desc = paramString2;
    this.signature = paramString3;
    this.value = paramObject;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationNode localAnnotationNode = new AnnotationNode(paramString);
    if (paramBoolean)
    {
      if (this.visibleAnnotations == null) {
        this.visibleAnnotations = new ArrayList(1);
      }
      this.visibleAnnotations.add(localAnnotationNode);
    }
    else
    {
      if (this.invisibleAnnotations == null) {
        this.invisibleAnnotations = new ArrayList(1);
      }
      this.invisibleAnnotations.add(localAnnotationNode);
    }
    return localAnnotationNode;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    TypeAnnotationNode localTypeAnnotationNode = new TypeAnnotationNode(paramInt, paramTypePath, paramString);
    if (paramBoolean)
    {
      if (this.visibleTypeAnnotations == null) {
        this.visibleTypeAnnotations = new ArrayList(1);
      }
      this.visibleTypeAnnotations.add(localTypeAnnotationNode);
    }
    else
    {
      if (this.invisibleTypeAnnotations == null) {
        this.invisibleTypeAnnotations = new ArrayList(1);
      }
      this.invisibleTypeAnnotations.add(localTypeAnnotationNode);
    }
    return localTypeAnnotationNode;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (this.attrs == null) {
      this.attrs = new ArrayList(1);
    }
    this.attrs.add(paramAttribute);
  }
  
  public void visitEnd() {}
  
  public void check(int paramInt)
  {
    if (paramInt == 262144)
    {
      if ((this.visibleTypeAnnotations != null) && (this.visibleTypeAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
      if ((this.invisibleTypeAnnotations != null) && (this.invisibleTypeAnnotations.size() > 0)) {
        throw new RuntimeException();
      }
    }
  }
  
  public void accept(ClassVisitor paramClassVisitor)
  {
    FieldVisitor localFieldVisitor = paramClassVisitor.visitField(this.access, this.name, this.desc, this.signature, this.value);
    if (localFieldVisitor == null) {
      return;
    }
    int j = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
    Object localObject;
    for (int i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)this.visibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(localFieldVisitor.visitAnnotation(((AnnotationNode)localObject).desc, true));
    }
    j = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (AnnotationNode)this.invisibleAnnotations.get(i);
      ((AnnotationNode)localObject).accept(localFieldVisitor.visitAnnotation(((AnnotationNode)localObject).desc, false));
    }
    j = this.visibleTypeAnnotations == null ? 0 : this.visibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)this.visibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(localFieldVisitor.visitTypeAnnotation(((TypeAnnotationNode)localObject).typeRef, ((TypeAnnotationNode)localObject).typePath, ((TypeAnnotationNode)localObject).desc, true));
    }
    j = this.invisibleTypeAnnotations == null ? 0 : this.invisibleTypeAnnotations.size();
    for (i = 0; i < j; i++)
    {
      localObject = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(i);
      ((TypeAnnotationNode)localObject).accept(localFieldVisitor.visitTypeAnnotation(((TypeAnnotationNode)localObject).typeRef, ((TypeAnnotationNode)localObject).typePath, ((TypeAnnotationNode)localObject).desc, false));
    }
    j = this.attrs == null ? 0 : this.attrs.size();
    for (i = 0; i < j; i++) {
      localFieldVisitor.visitAttribute((Attribute)this.attrs.get(i));
    }
    localFieldVisitor.visitEnd();
  }
}
