package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;

public abstract class ReferenceType
  extends Type
{
  protected ReferenceType(byte paramByte, String paramString)
  {
    super(paramByte, paramString);
  }
  
  ReferenceType()
  {
    super((byte)14, "<null object>");
  }
  
  public boolean isCastableTo(Type paramType)
  {
    if (equals(Type.NULL)) {
      return true;
    }
    return isAssignmentCompatibleWith(paramType);
  }
  
  public boolean isAssignmentCompatibleWith(Type paramType)
  {
    if (!(paramType instanceof ReferenceType)) {
      return false;
    }
    ReferenceType localReferenceType = (ReferenceType)paramType;
    if (equals(Type.NULL)) {
      return true;
    }
    if (((this instanceof ObjectType)) && (((ObjectType)this).referencesClass()))
    {
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesClass()))
      {
        if (equals(localReferenceType)) {
          return true;
        }
        if (Repository.instanceOf(((ObjectType)this).getClassName(), ((ObjectType)localReferenceType).getClassName())) {
          return true;
        }
      }
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesInterface()) && (Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)localReferenceType).getClassName()))) {
        return true;
      }
    }
    if (((this instanceof ObjectType)) && (((ObjectType)this).referencesInterface()))
    {
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesClass()) && (localReferenceType.equals(Type.OBJECT))) {
        return true;
      }
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesInterface()))
      {
        if (equals(localReferenceType)) {
          return true;
        }
        if (Repository.implementationOf(((ObjectType)this).getClassName(), ((ObjectType)localReferenceType).getClassName())) {
          return true;
        }
      }
    }
    if ((this instanceof ArrayType))
    {
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesClass()) && (localReferenceType.equals(Type.OBJECT))) {
        return true;
      }
      if ((localReferenceType instanceof ArrayType))
      {
        Type localType1 = ((ArrayType)this).getElementType();
        Type localType2 = ((ArrayType)this).getElementType();
        if (((localType1 instanceof BasicType)) && ((localType2 instanceof BasicType)) && (localType1.equals(localType2))) {
          return true;
        }
        if (((localType2 instanceof ReferenceType)) && ((localType1 instanceof ReferenceType)) && (((ReferenceType)localType1).isAssignmentCompatibleWith((ReferenceType)localType2))) {
          return true;
        }
      }
      if (((localReferenceType instanceof ObjectType)) && (((ObjectType)localReferenceType).referencesInterface())) {
        for (int i = 0; i < Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS.length; i++) {
          if (localReferenceType.equals(new ObjectType(Constants.INTERFACES_IMPLEMENTED_BY_ARRAYS[i]))) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public ReferenceType getFirstCommonSuperclass(ReferenceType paramReferenceType)
  {
    if (equals(Type.NULL)) {
      return paramReferenceType;
    }
    if (paramReferenceType.equals(Type.NULL)) {
      return this;
    }
    if (equals(paramReferenceType)) {
      return this;
    }
    if (((this instanceof ArrayType)) && ((paramReferenceType instanceof ArrayType)))
    {
      localObject1 = (ArrayType)this;
      localObject2 = (ArrayType)paramReferenceType;
      if ((((ArrayType)localObject1).getDimensions() == ((ArrayType)localObject2).getDimensions()) && ((((ArrayType)localObject1).getBasicType() instanceof ObjectType)) && ((((ArrayType)localObject2).getBasicType() instanceof ObjectType))) {
        return new ArrayType(((ObjectType)((ArrayType)localObject1).getBasicType()).getFirstCommonSuperclass((ObjectType)((ArrayType)localObject2).getBasicType()), ((ArrayType)localObject1).getDimensions());
      }
    }
    if (((this instanceof ArrayType)) || ((paramReferenceType instanceof ArrayType))) {
      return Type.OBJECT;
    }
    if ((((this instanceof ObjectType)) && (((ObjectType)this).referencesInterface())) || (((paramReferenceType instanceof ObjectType)) && (((ObjectType)paramReferenceType).referencesInterface()))) {
      return Type.OBJECT;
    }
    Object localObject1 = (ObjectType)this;
    Object localObject2 = (ObjectType)paramReferenceType;
    JavaClass[] arrayOfJavaClass1 = Repository.getSuperClasses(((ObjectType)localObject1).getClassName());
    JavaClass[] arrayOfJavaClass2 = Repository.getSuperClasses(((ObjectType)localObject2).getClassName());
    if ((arrayOfJavaClass1 == null) || (arrayOfJavaClass2 == null)) {
      return null;
    }
    JavaClass[] arrayOfJavaClass3 = new JavaClass[arrayOfJavaClass1.length + 1];
    JavaClass[] arrayOfJavaClass4 = new JavaClass[arrayOfJavaClass2.length + 1];
    System.arraycopy(arrayOfJavaClass1, 0, arrayOfJavaClass3, 1, arrayOfJavaClass1.length);
    System.arraycopy(arrayOfJavaClass2, 0, arrayOfJavaClass4, 1, arrayOfJavaClass2.length);
    arrayOfJavaClass3[0] = Repository.lookupClass(((ObjectType)localObject1).getClassName());
    arrayOfJavaClass4[0] = Repository.lookupClass(((ObjectType)localObject2).getClassName());
    for (int i = 0; i < arrayOfJavaClass4.length; i++) {
      for (int j = 0; j < arrayOfJavaClass3.length; j++) {
        if (arrayOfJavaClass3[j].equals(arrayOfJavaClass4[i])) {
          return new ObjectType(arrayOfJavaClass3[j].getClassName());
        }
      }
    }
    return null;
  }
  
  /**
   * @deprecated
   */
  public ReferenceType firstCommonSuperclass(ReferenceType paramReferenceType)
  {
    if (equals(Type.NULL)) {
      return paramReferenceType;
    }
    if (paramReferenceType.equals(Type.NULL)) {
      return this;
    }
    if (equals(paramReferenceType)) {
      return this;
    }
    if (((this instanceof ArrayType)) || ((paramReferenceType instanceof ArrayType))) {
      return Type.OBJECT;
    }
    if ((((this instanceof ObjectType)) && (((ObjectType)this).referencesInterface())) || (((paramReferenceType instanceof ObjectType)) && (((ObjectType)paramReferenceType).referencesInterface()))) {
      return Type.OBJECT;
    }
    ObjectType localObjectType1 = (ObjectType)this;
    ObjectType localObjectType2 = (ObjectType)paramReferenceType;
    JavaClass[] arrayOfJavaClass1 = Repository.getSuperClasses(localObjectType1.getClassName());
    JavaClass[] arrayOfJavaClass2 = Repository.getSuperClasses(localObjectType2.getClassName());
    if ((arrayOfJavaClass1 == null) || (arrayOfJavaClass2 == null)) {
      return null;
    }
    JavaClass[] arrayOfJavaClass3 = new JavaClass[arrayOfJavaClass1.length + 1];
    JavaClass[] arrayOfJavaClass4 = new JavaClass[arrayOfJavaClass2.length + 1];
    System.arraycopy(arrayOfJavaClass1, 0, arrayOfJavaClass3, 1, arrayOfJavaClass1.length);
    System.arraycopy(arrayOfJavaClass2, 0, arrayOfJavaClass4, 1, arrayOfJavaClass2.length);
    arrayOfJavaClass3[0] = Repository.lookupClass(localObjectType1.getClassName());
    arrayOfJavaClass4[0] = Repository.lookupClass(localObjectType2.getClassName());
    for (int i = 0; i < arrayOfJavaClass4.length; i++) {
      for (int j = 0; j < arrayOfJavaClass3.length; j++) {
        if (arrayOfJavaClass3[j].equals(arrayOfJavaClass4[i])) {
          return new ObjectType(arrayOfJavaClass3[j].getClassName());
        }
      }
    }
    return null;
  }
}
