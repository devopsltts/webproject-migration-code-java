package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

public class ParameterizedTypeImpl
  implements ParameterizedType
{
  private final Type[] actualTypeArguments;
  private final Class<?> rawType;
  private final Type ownerType;
  
  private ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
  {
    this.actualTypeArguments = paramArrayOfType;
    this.rawType = paramClass;
    this.ownerType = (paramType != null ? paramType : paramClass.getDeclaringClass());
    validateConstructorArguments();
  }
  
  private void validateConstructorArguments()
  {
    TypeVariable[] arrayOfTypeVariable = this.rawType.getTypeParameters();
    if (arrayOfTypeVariable.length != this.actualTypeArguments.length) {
      throw new MalformedParameterizedTypeException();
    }
    for (int i = 0; i < this.actualTypeArguments.length; i++) {}
  }
  
  public static ParameterizedTypeImpl make(Class<?> paramClass, Type[] paramArrayOfType, Type paramType)
  {
    return new ParameterizedTypeImpl(paramClass, paramArrayOfType, paramType);
  }
  
  public Type[] getActualTypeArguments()
  {
    return (Type[])this.actualTypeArguments.clone();
  }
  
  public Class<?> getRawType()
  {
    return this.rawType;
  }
  
  public Type getOwnerType()
  {
    return this.ownerType;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ParameterizedType))
    {
      ParameterizedType localParameterizedType = (ParameterizedType)paramObject;
      if (this == localParameterizedType) {
        return true;
      }
      Type localType1 = localParameterizedType.getOwnerType();
      Type localType2 = localParameterizedType.getRawType();
      return (Objects.equals(this.ownerType, localType1)) && (Objects.equals(this.rawType, localType2)) && (Arrays.equals(this.actualTypeArguments, localParameterizedType.getActualTypeArguments()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.ownerType != null)
    {
      if ((this.ownerType instanceof Class)) {
        localStringBuilder.append(((Class)this.ownerType).getName());
      } else {
        localStringBuilder.append(this.ownerType.toString());
      }
      localStringBuilder.append(".");
      if ((this.ownerType instanceof ParameterizedTypeImpl)) {
        localStringBuilder.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
      } else {
        localStringBuilder.append(this.rawType.getName());
      }
    }
    else
    {
      localStringBuilder.append(this.rawType.getName());
    }
    if ((this.actualTypeArguments != null) && (this.actualTypeArguments.length > 0))
    {
      localStringBuilder.append("<");
      int i = 1;
      for (Type localType : this.actualTypeArguments)
      {
        if (i == 0) {
          localStringBuilder.append(", ");
        }
        localStringBuilder.append(localType.getTypeName());
        i = 0;
      }
      localStringBuilder.append(">");
    }
    return localStringBuilder.toString();
  }
}
