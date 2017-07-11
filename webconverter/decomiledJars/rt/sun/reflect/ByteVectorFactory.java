package sun.reflect;

class ByteVectorFactory
{
  ByteVectorFactory() {}
  
  static ByteVector create()
  {
    return new ByteVectorImpl();
  }
  
  static ByteVector create(int paramInt)
  {
    return new ByteVectorImpl(paramInt);
  }
}
