package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public final class ObjectType
  extends ReferenceType
{
  private String class_name;
  
  public ObjectType(String paramString)
  {
    super((byte)14, "L" + paramString.replace('.', '/') + ";");
    this.class_name = paramString.replace('/', '.');
  }
  
  public String getClassName()
  {
    return this.class_name;
  }
  
  public int hashCode()
  {
    return this.class_name.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject instanceof ObjectType) ? ((ObjectType)paramObject).class_name.equals(this.class_name) : false;
  }
  
  public boolean referencesClass()
  {
    JavaClass localJavaClass = Repository.lookupClass(this.class_name);
    if (localJavaClass == null) {
      return false;
    }
    return localJavaClass.isClass();
  }
  
  public boolean referencesInterface()
  {
    JavaClass localJavaClass = Repository.lookupClass(this.class_name);
    if (localJavaClass == null) {
      return false;
    }
    return !localJavaClass.isClass();
  }
  
  public boolean subclassOf(ObjectType paramObjectType)
  {
    if ((referencesInterface()) || (paramObjectType.referencesInterface())) {
      return false;
    }
    return Repository.instanceOf(this.class_name, paramObjectType.class_name);
  }
  
  public boolean accessibleTo(ObjectType paramObjectType)
  {
    JavaClass localJavaClass1 = Repository.lookupClass(this.class_name);
    if (localJavaClass1.isPublic()) {
      return true;
    }
    JavaClass localJavaClass2 = Repository.lookupClass(paramObjectType.class_name);
    return localJavaClass2.getPackageName().equals(localJavaClass1.getPackageName());
  }
}
