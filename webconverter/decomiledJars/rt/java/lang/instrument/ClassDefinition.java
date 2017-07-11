package java.lang.instrument;

public final class ClassDefinition
{
  private final Class<?> mClass;
  private final byte[] mClassFile;
  
  public ClassDefinition(Class<?> paramClass, byte[] paramArrayOfByte)
  {
    if ((paramClass == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException();
    }
    this.mClass = paramClass;
    this.mClassFile = paramArrayOfByte;
  }
  
  public Class<?> getDefinitionClass()
  {
    return this.mClass;
  }
  
  public byte[] getDefinitionClassFile()
  {
    return this.mClassFile;
  }
}
