package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.util.Map;

final class PrimitiveArrayListerCharacter<BeanT>
  extends Lister<BeanT, char[], Character, CharacterArrayPack>
{
  private PrimitiveArrayListerCharacter() {}
  
  static void register()
  {
    Lister.primitiveArrayListers.put(Character.TYPE, new PrimitiveArrayListerCharacter());
  }
  
  public ListIterator<Character> iterator(final char[] paramArrayOfChar, XMLSerializer paramXMLSerializer)
  {
    new ListIterator()
    {
      int idx = 0;
      
      public boolean hasNext()
      {
        return this.idx < paramArrayOfChar.length;
      }
      
      public Character next()
      {
        return Character.valueOf(paramArrayOfChar[(this.idx++)]);
      }
    };
  }
  
  public CharacterArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
  {
    return new CharacterArrayPack();
  }
  
  public void addToPack(CharacterArrayPack paramCharacterArrayPack, Character paramCharacter)
  {
    paramCharacterArrayPack.add(paramCharacter);
  }
  
  public void endPacking(CharacterArrayPack paramCharacterArrayPack, BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, paramCharacterArrayPack.build());
  }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, char[]> paramAccessor)
    throws AccessorException
  {
    paramAccessor.set(paramBeanT, new char[0]);
  }
  
  static final class CharacterArrayPack
  {
    char[] buf = new char[16];
    int size;
    
    CharacterArrayPack() {}
    
    void add(Character paramCharacter)
    {
      if (this.buf.length == this.size)
      {
        char[] arrayOfChar = new char[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfChar, 0, this.buf.length);
        this.buf = arrayOfChar;
      }
      if (paramCharacter != null) {
        this.buf[(this.size++)] = paramCharacter.charValue();
      }
    }
    
    char[] build()
    {
      if (this.buf.length == this.size) {
        return this.buf;
      }
      char[] arrayOfChar = new char[this.size];
      System.arraycopy(this.buf, 0, arrayOfChar, 0, this.size);
      return arrayOfChar;
    }
  }
}
