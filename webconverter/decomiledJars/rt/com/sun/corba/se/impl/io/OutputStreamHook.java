package com.sun.corba.se.impl.io;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class OutputStreamHook
  extends ObjectOutputStream
{
  private HookPutFields putFields = null;
  protected byte streamFormatVersion = 1;
  protected WriteObjectState writeObjectState = NOT_IN_WRITE_OBJECT;
  protected static final WriteObjectState NOT_IN_WRITE_OBJECT = new DefaultState();
  protected static final WriteObjectState IN_WRITE_OBJECT = new InWriteObjectState();
  protected static final WriteObjectState WROTE_DEFAULT_DATA = new WroteDefaultDataState();
  protected static final WriteObjectState WROTE_CUSTOM_DATA = new WroteCustomDataState();
  
  abstract void writeField(ObjectStreamField paramObjectStreamField, Object paramObject)
    throws IOException;
  
  public OutputStreamHook()
    throws IOException
  {}
  
  public void defaultWriteObject()
    throws IOException
  {
    this.writeObjectState.defaultWriteObject(this);
    defaultWriteObjectDelegate();
  }
  
  public abstract void defaultWriteObjectDelegate();
  
  public ObjectOutputStream.PutField putFields()
    throws IOException
  {
    if (this.putFields == null) {
      this.putFields = new HookPutFields(null);
    }
    return this.putFields;
  }
  
  public byte getStreamFormatVersion()
  {
    return this.streamFormatVersion;
  }
  
  abstract ObjectStreamField[] getFieldsNoCopy();
  
  public void writeFields()
    throws IOException
  {
    this.writeObjectState.defaultWriteObject(this);
    if (this.putFields != null) {
      this.putFields.write(this);
    } else {
      throw new NotActiveException("no current PutField object");
    }
  }
  
  abstract OutputStream getOrbStream();
  
  protected abstract void beginOptionalCustomData();
  
  protected void setState(WriteObjectState paramWriteObjectState)
  {
    this.writeObjectState = paramWriteObjectState;
  }
  
  protected static class DefaultState
    extends OutputStreamHook.WriteObjectState
  {
    protected DefaultState() {}
    
    public void enterWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      paramOutputStreamHook.setState(OutputStreamHook.IN_WRITE_OBJECT);
    }
  }
  
  private class HookPutFields
    extends ObjectOutputStream.PutField
  {
    private Map<String, Object> fields = new HashMap();
    
    private HookPutFields() {}
    
    public void put(String paramString, boolean paramBoolean)
    {
      this.fields.put(paramString, new Boolean(paramBoolean));
    }
    
    public void put(String paramString, char paramChar)
    {
      this.fields.put(paramString, new Character(paramChar));
    }
    
    public void put(String paramString, byte paramByte)
    {
      this.fields.put(paramString, new Byte(paramByte));
    }
    
    public void put(String paramString, short paramShort)
    {
      this.fields.put(paramString, new Short(paramShort));
    }
    
    public void put(String paramString, int paramInt)
    {
      this.fields.put(paramString, new Integer(paramInt));
    }
    
    public void put(String paramString, long paramLong)
    {
      this.fields.put(paramString, new Long(paramLong));
    }
    
    public void put(String paramString, float paramFloat)
    {
      this.fields.put(paramString, new Float(paramFloat));
    }
    
    public void put(String paramString, double paramDouble)
    {
      this.fields.put(paramString, new Double(paramDouble));
    }
    
    public void put(String paramString, Object paramObject)
    {
      this.fields.put(paramString, paramObject);
    }
    
    public void write(ObjectOutput paramObjectOutput)
      throws IOException
    {
      OutputStreamHook localOutputStreamHook = (OutputStreamHook)paramObjectOutput;
      ObjectStreamField[] arrayOfObjectStreamField = localOutputStreamHook.getFieldsNoCopy();
      for (int i = 0; i < arrayOfObjectStreamField.length; i++)
      {
        Object localObject = this.fields.get(arrayOfObjectStreamField[i].getName());
        localOutputStreamHook.writeField(arrayOfObjectStreamField[i], localObject);
      }
    }
  }
  
  protected static class InWriteObjectState
    extends OutputStreamHook.WriteObjectState
  {
    protected InWriteObjectState() {}
    
    public void enterWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      throw new IOException("Internal state failure: Entered writeObject twice");
    }
    
    public void exitWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      paramOutputStreamHook.getOrbStream().write_boolean(false);
      if (paramOutputStreamHook.getStreamFormatVersion() == 2) {
        paramOutputStreamHook.getOrbStream().write_long(0);
      }
      paramOutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      paramOutputStreamHook.getOrbStream().write_boolean(true);
      paramOutputStreamHook.setState(OutputStreamHook.WROTE_DEFAULT_DATA);
    }
    
    public void writeData(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      paramOutputStreamHook.getOrbStream().write_boolean(false);
      paramOutputStreamHook.beginOptionalCustomData();
      paramOutputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
    }
  }
  
  protected static class WriteObjectState
  {
    protected WriteObjectState() {}
    
    public void enterWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {}
    
    public void exitWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {}
    
    public void defaultWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {}
    
    public void writeData(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {}
  }
  
  protected static class WroteCustomDataState
    extends OutputStreamHook.InWriteObjectState
  {
    protected WroteCustomDataState() {}
    
    public void exitWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      if (paramOutputStreamHook.getStreamFormatVersion() == 2) {
        ((ValueOutputStream)paramOutputStreamHook.getOrbStream()).end_value();
      }
      paramOutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      throw new IOException("Cannot call defaultWriteObject/writeFields after writing custom data in RMI-IIOP");
    }
    
    public void writeData(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {}
  }
  
  protected static class WroteDefaultDataState
    extends OutputStreamHook.InWriteObjectState
  {
    protected WroteDefaultDataState() {}
    
    public void exitWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      if (paramOutputStreamHook.getStreamFormatVersion() == 2) {
        paramOutputStreamHook.getOrbStream().write_long(0);
      }
      paramOutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      throw new IOException("Called defaultWriteObject/writeFields twice");
    }
    
    public void writeData(OutputStreamHook paramOutputStreamHook)
      throws IOException
    {
      paramOutputStreamHook.beginOptionalCustomData();
      paramOutputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
    }
  }
}
