package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.Serializable;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;

public class SerialArray
  implements Array, Serializable, Cloneable
{
  private Object[] elements;
  private int baseType;
  private String baseTypeName;
  private int len;
  static final long serialVersionUID = -8466174297270688520L;
  
  public SerialArray(Array paramArray, Map<String, Class<?>> paramMap)
    throws SerialException, SQLException
  {
    if ((paramArray == null) || (paramMap == null)) {
      throw new SQLException("Cannot instantiate a SerialArray object with null parameters");
    }
    if ((this.elements = (Object[])paramArray.getArray()) == null) {
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
    }
    this.elements = ((Object[])paramArray.getArray(paramMap));
    this.baseType = paramArray.getBaseType();
    this.baseTypeName = paramArray.getBaseTypeName();
    this.len = this.elements.length;
    int i;
    switch (this.baseType)
    {
    case 2002: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialStruct((Struct)this.elements[i], paramMap);
      }
      break;
    case 2003: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialArray((Array)this.elements[i], paramMap);
      }
      break;
    case 2004: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialBlob((Blob)this.elements[i]);
      }
      break;
    case 2005: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialClob((Clob)this.elements[i]);
      }
      break;
    case 70: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialDatalink((URL)this.elements[i]);
      }
      break;
    case 2000: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialJavaObject(this.elements[i]);
      }
    }
  }
  
  public void free()
    throws SQLException
  {
    if (this.elements != null)
    {
      this.elements = null;
      this.baseTypeName = null;
    }
  }
  
  public SerialArray(Array paramArray)
    throws SerialException, SQLException
  {
    if (paramArray == null) {
      throw new SQLException("Cannot instantiate a SerialArray object with a null Array object");
    }
    if ((this.elements = (Object[])paramArray.getArray()) == null) {
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized");
    }
    this.baseType = paramArray.getBaseType();
    this.baseTypeName = paramArray.getBaseTypeName();
    this.len = this.elements.length;
    int i;
    switch (this.baseType)
    {
    case 2004: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialBlob((Blob)this.elements[i]);
      }
      break;
    case 2005: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialClob((Clob)this.elements[i]);
      }
      break;
    case 70: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialDatalink((URL)this.elements[i]);
      }
      break;
    case 2000: 
      for (i = 0; i < this.len; i++) {
        this.elements[i] = new SerialJavaObject(this.elements[i]);
      }
    }
  }
  
  public Object getArray()
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[this.len];
    System.arraycopy(this.elements, 0, arrayOfObject, 0, this.len);
    return arrayOfObject;
  }
  
  public Object getArray(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[this.len];
    System.arraycopy(this.elements, 0, arrayOfObject, 0, this.len);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public int getBaseType()
    throws SerialException
  {
    isValid();
    return this.baseType;
  }
  
  public String getBaseTypeName()
    throws SerialException
  {
    isValid();
    return this.baseTypeName;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet(Map<String, Class<?>> paramMap)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet()
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt, Map<String, Class<?>> paramMap)
    throws SerialException
  {
    SerialException localSerialException = new SerialException();
    localSerialException.initCause(new UnsupportedOperationException());
    throw localSerialException;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialArray))
    {
      SerialArray localSerialArray = (SerialArray)paramObject;
      return (this.baseType == localSerialArray.baseType) && (this.baseTypeName.equals(localSerialArray.baseTypeName)) && (Arrays.equals(this.elements, localSerialArray.elements));
    }
    return false;
  }
  
  public int hashCode()
  {
    return (((31 + Arrays.hashCode(this.elements)) * 31 + this.len) * 31 + this.baseType) * 31 + this.baseTypeName.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialArray localSerialArray = (SerialArray)super.clone();
      localSerialArray.elements = (this.elements != null ? Arrays.copyOf(this.elements, this.len) : null);
      return localSerialArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Object[] arrayOfObject = (Object[])localGetField.get("elements", null);
    if (arrayOfObject == null) {
      throw new InvalidObjectException("elements is null and should not be!");
    }
    this.elements = ((Object[])arrayOfObject.clone());
    this.len = localGetField.get("len", 0);
    if (this.elements.length != this.len) {
      throw new InvalidObjectException("elements is not the expected size");
    }
    this.baseType = localGetField.get("baseType", 0);
    this.baseTypeName = ((String)localGetField.get("baseTypeName", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("elements", this.elements);
    localPutField.put("len", this.len);
    localPutField.put("baseType", this.baseType);
    localPutField.put("baseTypeName", this.baseTypeName);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid()
    throws SerialException
  {
    if (this.elements == null) {
      throw new SerialException("Error: You cannot call a method on a SerialArray instance once free() has been called.");
    }
  }
}
