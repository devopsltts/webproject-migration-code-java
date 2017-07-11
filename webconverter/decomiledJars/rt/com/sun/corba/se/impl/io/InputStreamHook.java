package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class InputStreamHook
  extends ObjectInputStream
{
  static final OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  static final UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  protected ReadObjectState readObjectState = DEFAULT_STATE;
  protected static final ReadObjectState DEFAULT_STATE = new DefaultState();
  protected static final ReadObjectState IN_READ_OBJECT_OPT_DATA = new InReadObjectOptionalDataState();
  protected static final ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA = new InReadObjectNoMoreOptionalDataState();
  protected static final ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT = new InReadObjectDefaultsSentState();
  protected static final ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT = new NoReadObjectDefaultsSentState();
  protected static final ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED = new InReadObjectRemoteDidNotUseWriteObjectState();
  protected static final ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM = new InReadObjectPastDefaultsRemoteDidNotUseWOState();
  
  public InputStreamHook()
    throws IOException
  {}
  
  public void defaultReadObject()
    throws IOException, ClassNotFoundException, NotActiveException
  {
    this.readObjectState.beginDefaultReadObject(this);
    defaultReadObjectDelegate();
    this.readObjectState.endDefaultReadObject(this);
  }
  
  abstract void defaultReadObjectDelegate();
  
  abstract void readFields(Map paramMap)
    throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException;
  
  public ObjectInputStream.GetField readFields()
    throws IOException, ClassNotFoundException, NotActiveException
  {
    HashMap localHashMap = new HashMap();
    readFields(localHashMap);
    this.readObjectState.endDefaultReadObject(this);
    return new HookGetFields(localHashMap);
  }
  
  protected void setState(ReadObjectState paramReadObjectState)
  {
    this.readObjectState = paramReadObjectState;
  }
  
  protected abstract byte getStreamFormatVersion();
  
  abstract InputStream getOrbStream();
  
  protected void throwOptionalDataIncompatibleException()
  {
    throw omgWrapper.rmiiiopOptionalDataIncompatible2();
  }
  
  protected static class DefaultState
    extends InputStreamHook.ReadObjectState
  {
    protected DefaultState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
      throws IOException
    {
      if (paramBoolean2)
      {
        if (paramBoolean1)
        {
          paramInputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_DEFAULTS_SENT);
        }
        else
        {
          try
          {
            if (paramInputStreamHook.getStreamFormatVersion() == 2) {
              ((ValueInputStream)paramInputStreamHook.getOrbStream()).start_value();
            }
          }
          catch (Exception localException) {}
          paramInputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
        }
      }
      else if (paramBoolean1) {
        paramInputStreamHook.setState(InputStreamHook.NO_READ_OBJECT_DEFAULTS_SENT);
      } else {
        throw new StreamCorruptedException("No default data sent");
      }
    }
  }
  
  private class HookGetFields
    extends ObjectInputStream.GetField
  {
    private Map fields = null;
    
    HookGetFields(Map paramMap)
    {
      this.fields = paramMap;
    }
    
    public ObjectStreamClass getObjectStreamClass()
    {
      return null;
    }
    
    public boolean defaulted(String paramString)
      throws IOException, IllegalArgumentException
    {
      return !this.fields.containsKey(paramString);
    }
    
    public boolean get(String paramString, boolean paramBoolean)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramBoolean;
      }
      return ((Boolean)this.fields.get(paramString)).booleanValue();
    }
    
    public char get(String paramString, char paramChar)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramChar;
      }
      return ((Character)this.fields.get(paramString)).charValue();
    }
    
    public byte get(String paramString, byte paramByte)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramByte;
      }
      return ((Byte)this.fields.get(paramString)).byteValue();
    }
    
    public short get(String paramString, short paramShort)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramShort;
      }
      return ((Short)this.fields.get(paramString)).shortValue();
    }
    
    public int get(String paramString, int paramInt)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramInt;
      }
      return ((Integer)this.fields.get(paramString)).intValue();
    }
    
    public long get(String paramString, long paramLong)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramLong;
      }
      return ((Long)this.fields.get(paramString)).longValue();
    }
    
    public float get(String paramString, float paramFloat)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramFloat;
      }
      return ((Float)this.fields.get(paramString)).floatValue();
    }
    
    public double get(String paramString, double paramDouble)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramDouble;
      }
      return ((Double)this.fields.get(paramString)).doubleValue();
    }
    
    public Object get(String paramString, Object paramObject)
      throws IOException, IllegalArgumentException
    {
      if (defaulted(paramString)) {
        return paramObject;
      }
      return this.fields.get(paramString);
    }
    
    public String toString()
    {
      return this.fields.toString();
    }
  }
  
  protected static class InReadObjectDefaultsSentState
    extends InputStreamHook.ReadObjectState
  {
    protected InReadObjectDefaultsSentState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
    {
      throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
    }
    
    public void endUnmarshalCustomValue(InputStreamHook paramInputStreamHook)
    {
      if (paramInputStreamHook.getStreamFormatVersion() == 2)
      {
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).start_value();
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).end_value();
      }
      paramInputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
    
    public void endDefaultReadObject(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      if (paramInputStreamHook.getStreamFormatVersion() == 2) {
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).start_value();
      }
      paramInputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
    }
    
    public void readData(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      org.omg.CORBA.ORB localORB = paramInputStreamHook.getOrbStream().orb();
      if ((localORB == null) || (!(localORB instanceof com.sun.corba.se.spi.orb.ORB))) {
        throw new StreamCorruptedException("Default data must be read first");
      }
      ORBVersion localORBVersion = ((com.sun.corba.se.spi.orb.ORB)localORB).getORBVersion();
      if ((ORBVersionFactory.getPEORB().compareTo(localORBVersion) <= 0) || (localORBVersion.equals(ORBVersionFactory.getFOREIGN()))) {
        throw new StreamCorruptedException("Default data must be read first");
      }
    }
  }
  
  protected static class InReadObjectNoMoreOptionalDataState
    extends InputStreamHook.InReadObjectOptionalDataState
  {
    protected InReadObjectNoMoreOptionalDataState() {}
    
    public void readData(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      paramInputStreamHook.throwOptionalDataIncompatibleException();
    }
  }
  
  protected static class InReadObjectOptionalDataState
    extends InputStreamHook.ReadObjectState
  {
    protected InReadObjectOptionalDataState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
    {
      throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
    }
    
    public void endUnmarshalCustomValue(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      if (paramInputStreamHook.getStreamFormatVersion() == 2) {
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).end_value();
      }
      paramInputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
    
    public void beginDefaultReadObject(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      throw new StreamCorruptedException("Default data not sent or already read/passed");
    }
  }
  
  protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState
    extends InputStreamHook.ReadObjectState
  {
    protected InReadObjectPastDefaultsRemoteDidNotUseWOState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
    {
      throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
    }
    
    public void beginDefaultReadObject(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      throw new StreamCorruptedException("Default data already read");
    }
    
    public void readData(InputStreamHook paramInputStreamHook)
    {
      paramInputStreamHook.throwOptionalDataIncompatibleException();
    }
  }
  
  protected static class InReadObjectRemoteDidNotUseWriteObjectState
    extends InputStreamHook.ReadObjectState
  {
    protected InReadObjectRemoteDidNotUseWriteObjectState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
    {
      throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue();
    }
    
    public void endDefaultReadObject(InputStreamHook paramInputStreamHook)
    {
      paramInputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM);
    }
    
    public void readData(InputStreamHook paramInputStreamHook)
    {
      paramInputStreamHook.throwOptionalDataIncompatibleException();
    }
  }
  
  protected static class NoReadObjectDefaultsSentState
    extends InputStreamHook.ReadObjectState
  {
    protected NoReadObjectDefaultsSentState() {}
    
    public void endUnmarshalCustomValue(InputStreamHook paramInputStreamHook)
      throws IOException
    {
      if (paramInputStreamHook.getStreamFormatVersion() == 2)
      {
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).start_value();
        ((ValueInputStream)paramInputStreamHook.getOrbStream()).end_value();
      }
      paramInputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
  }
  
  protected static class ReadObjectState
  {
    protected ReadObjectState() {}
    
    public void beginUnmarshalCustomValue(InputStreamHook paramInputStreamHook, boolean paramBoolean1, boolean paramBoolean2)
      throws IOException
    {}
    
    public void endUnmarshalCustomValue(InputStreamHook paramInputStreamHook)
      throws IOException
    {}
    
    public void beginDefaultReadObject(InputStreamHook paramInputStreamHook)
      throws IOException
    {}
    
    public void endDefaultReadObject(InputStreamHook paramInputStreamHook)
      throws IOException
    {}
    
    public void readData(InputStreamHook paramInputStreamHook)
      throws IOException
    {}
  }
}
