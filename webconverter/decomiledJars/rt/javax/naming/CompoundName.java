package javax.naming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;
import java.util.Properties;

public class CompoundName
  implements Name
{
  protected transient NameImpl impl;
  protected transient Properties mySyntax;
  private static final long serialVersionUID = 3513100557083972036L;
  
  protected CompoundName(Enumeration<String> paramEnumeration, Properties paramProperties)
  {
    if (paramProperties == null) {
      throw new NullPointerException();
    }
    this.mySyntax = paramProperties;
    this.impl = new NameImpl(paramProperties, paramEnumeration);
  }
  
  public CompoundName(String paramString, Properties paramProperties)
    throws InvalidNameException
  {
    if (paramProperties == null) {
      throw new NullPointerException();
    }
    this.mySyntax = paramProperties;
    this.impl = new NameImpl(paramProperties, paramString);
  }
  
  public String toString()
  {
    return this.impl.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && ((paramObject instanceof CompoundName)) && (this.impl.equals(((CompoundName)paramObject).impl));
  }
  
  public int hashCode()
  {
    return this.impl.hashCode();
  }
  
  public Object clone()
  {
    return new CompoundName(getAll(), this.mySyntax);
  }
  
  public int compareTo(Object paramObject)
  {
    if (!(paramObject instanceof CompoundName)) {
      throw new ClassCastException("Not a CompoundName");
    }
    return this.impl.compareTo(((CompoundName)paramObject).impl);
  }
  
  public int size()
  {
    return this.impl.size();
  }
  
  public boolean isEmpty()
  {
    return this.impl.isEmpty();
  }
  
  public Enumeration<String> getAll()
  {
    return this.impl.getAll();
  }
  
  public String get(int paramInt)
  {
    return this.impl.get(paramInt);
  }
  
  public Name getPrefix(int paramInt)
  {
    Enumeration localEnumeration = this.impl.getPrefix(paramInt);
    return new CompoundName(localEnumeration, this.mySyntax);
  }
  
  public Name getSuffix(int paramInt)
  {
    Enumeration localEnumeration = this.impl.getSuffix(paramInt);
    return new CompoundName(localEnumeration, this.mySyntax);
  }
  
  public boolean startsWith(Name paramName)
  {
    if ((paramName instanceof CompoundName)) {
      return this.impl.startsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public boolean endsWith(Name paramName)
  {
    if ((paramName instanceof CompoundName)) {
      return this.impl.endsWith(paramName.size(), paramName.getAll());
    }
    return false;
  }
  
  public Name addAll(Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompoundName))
    {
      this.impl.addAll(paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a compound name: " + paramName.toString());
  }
  
  public Name addAll(int paramInt, Name paramName)
    throws InvalidNameException
  {
    if ((paramName instanceof CompoundName))
    {
      this.impl.addAll(paramInt, paramName.getAll());
      return this;
    }
    throw new InvalidNameException("Not a compound name: " + paramName.toString());
  }
  
  public Name add(String paramString)
    throws InvalidNameException
  {
    this.impl.add(paramString);
    return this;
  }
  
  public Name add(int paramInt, String paramString)
    throws InvalidNameException
  {
    this.impl.add(paramInt, paramString);
    return this;
  }
  
  public Object remove(int paramInt)
    throws InvalidNameException
  {
    return this.impl.remove(paramInt);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeObject(this.mySyntax);
    paramObjectOutputStream.writeInt(size());
    Enumeration localEnumeration = getAll();
    while (localEnumeration.hasMoreElements()) {
      paramObjectOutputStream.writeObject(localEnumeration.nextElement());
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.mySyntax = ((Properties)paramObjectInputStream.readObject());
    this.impl = new NameImpl(this.mySyntax);
    int i = paramObjectInputStream.readInt();
    try
    {
      for (;;)
      {
        i--;
        if (i < 0) {
          break;
        }
        add((String)paramObjectInputStream.readObject());
      }
    }
    catch (InvalidNameException localInvalidNameException)
    {
      throw new StreamCorruptedException("Invalid name");
    }
  }
}
