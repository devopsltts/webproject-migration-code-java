package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class StubIORImpl
{
  private int hashCode;
  private byte[] typeData;
  private int[] profileTags;
  private byte[][] profileData;
  
  public StubIORImpl()
  {
    this.hashCode = 0;
    this.typeData = null;
    this.profileTags = null;
    this.profileData = ((byte[][])null);
  }
  
  public String getRepositoryId()
  {
    if (this.typeData == null) {
      return null;
    }
    return new String(this.typeData);
  }
  
  public StubIORImpl(org.omg.CORBA.Object paramObject)
  {
    OutputStream localOutputStream = StubAdapter.getORB(paramObject).create_output_stream();
    localOutputStream.write_Object(paramObject);
    InputStream localInputStream = localOutputStream.create_input_stream();
    int i = localInputStream.read_long();
    this.typeData = new byte[i];
    localInputStream.read_octet_array(this.typeData, 0, i);
    int j = localInputStream.read_long();
    this.profileTags = new int[j];
    this.profileData = new byte[j][];
    for (int k = 0; k < j; k++)
    {
      this.profileTags[k] = localInputStream.read_long();
      this.profileData[k] = new byte[localInputStream.read_long()];
      localInputStream.read_octet_array(this.profileData[k], 0, this.profileData[k].length);
    }
  }
  
  public Delegate getDelegate(ORB paramORB)
  {
    OutputStream localOutputStream = paramORB.create_output_stream();
    localOutputStream.write_long(this.typeData.length);
    localOutputStream.write_octet_array(this.typeData, 0, this.typeData.length);
    localOutputStream.write_long(this.profileTags.length);
    for (int i = 0; i < this.profileTags.length; i++)
    {
      localOutputStream.write_long(this.profileTags[i]);
      localOutputStream.write_long(this.profileData[i].length);
      localOutputStream.write_octet_array(this.profileData[i], 0, this.profileData[i].length);
    }
    InputStream localInputStream = localOutputStream.create_input_stream();
    org.omg.CORBA.Object localObject = localInputStream.read_Object();
    return StubAdapter.getDelegate(localObject);
  }
  
  public void doRead(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = paramObjectInputStream.readInt();
    this.typeData = new byte[i];
    paramObjectInputStream.readFully(this.typeData);
    int j = paramObjectInputStream.readInt();
    this.profileTags = new int[j];
    this.profileData = new byte[j][];
    for (int k = 0; k < j; k++)
    {
      this.profileTags[k] = paramObjectInputStream.readInt();
      this.profileData[k] = new byte[paramObjectInputStream.readInt()];
      paramObjectInputStream.readFully(this.profileData[k]);
    }
  }
  
  public void doWrite(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.typeData.length);
    paramObjectOutputStream.write(this.typeData);
    paramObjectOutputStream.writeInt(this.profileTags.length);
    for (int i = 0; i < this.profileTags.length; i++)
    {
      paramObjectOutputStream.writeInt(this.profileTags[i]);
      paramObjectOutputStream.writeInt(this.profileData[i].length);
      paramObjectOutputStream.write(this.profileData[i]);
    }
  }
  
  public synchronized int hashCode()
  {
    if (this.hashCode == 0)
    {
      for (int i = 0; i < this.typeData.length; i++) {
        this.hashCode = (this.hashCode * 37 + this.typeData[i]);
      }
      for (i = 0; i < this.profileTags.length; i++)
      {
        this.hashCode = (this.hashCode * 37 + this.profileTags[i]);
        for (int j = 0; j < this.profileData[i].length; j++) {
          this.hashCode = (this.hashCode * 37 + this.profileData[i][j]);
        }
      }
    }
    return this.hashCode;
  }
  
  private boolean equalArrays(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1.length != paramArrayOfInt2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfInt1.length; i++) {
      if (paramArrayOfInt1[i] != paramArrayOfInt2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equalArrays(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equalArrays(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2)
  {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (!equalArrays(paramArrayOfByte1[i], paramArrayOfByte2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StubIORImpl)) {
      return false;
    }
    StubIORImpl localStubIORImpl = (StubIORImpl)paramObject;
    if (localStubIORImpl.hashCode() != hashCode()) {
      return false;
    }
    return (equalArrays(this.typeData, localStubIORImpl.typeData)) && (equalArrays(this.profileTags, localStubIORImpl.profileTags)) && (equalArrays(this.profileData, localStubIORImpl.profileData));
  }
  
  private void appendByteArray(StringBuffer paramStringBuffer, byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      paramStringBuffer.append(Integer.toHexString(paramArrayOfByte[i]));
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("SimpleIORImpl[");
    String str = new String(this.typeData);
    localStringBuffer.append(str);
    for (int i = 0; i < this.profileTags.length; i++)
    {
      localStringBuffer.append(",(");
      localStringBuffer.append(this.profileTags[i]);
      localStringBuffer.append(")");
      appendByteArray(localStringBuffer, this.profileData[i]);
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}
