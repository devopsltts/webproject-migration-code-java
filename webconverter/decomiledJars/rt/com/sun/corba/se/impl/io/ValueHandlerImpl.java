package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.corba.se.impl.util.Utility;
import com.sun.org.omg.SendingContext.CodeBase;
import com.sun.org.omg.SendingContext.CodeBaseHelper;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.SendingContext.RunTime;

public final class ValueHandlerImpl
  implements ValueHandlerMultiFormat
{
  public static final String FORMAT_VERSION_PROPERTY = "com.sun.CORBA.MaxStreamFormatVersion";
  private static final byte MAX_SUPPORTED_FORMAT_VERSION = 2;
  private static final byte STREAM_FORMAT_VERSION_1 = 1;
  private static final byte MAX_STREAM_FORMAT_VERSION = ;
  public static final short kRemoteType = 0;
  public static final short kAbstractType = 1;
  public static final short kValueType = 2;
  private Hashtable inputStreamPairs = null;
  private Hashtable outputStreamPairs = null;
  private CodeBase codeBase = null;
  private boolean useHashtables = true;
  private boolean isInputStream = true;
  private IIOPOutputStream outputStreamBridge = null;
  private IIOPInputStream inputStreamBridge = null;
  private OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  
  private static byte getMaxStreamFormatVersion()
  {
    try
    {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return System.getProperty("com.sun.CORBA.MaxStreamFormatVersion");
        }
      });
      if (str == null) {
        return 2;
      }
      byte b = Byte.parseByte(str);
      if ((b < 1) || (b > 2)) {
        throw new ExceptionInInitializerError("Invalid stream format version: " + b + ".  Valid range is 1 through " + 2);
      }
      return b;
    }
    catch (Exception localException)
    {
      ExceptionInInitializerError localExceptionInInitializerError = new ExceptionInInitializerError(localException);
      localExceptionInInitializerError.initCause(localException);
      throw localExceptionInInitializerError;
    }
  }
  
  public byte getMaximumStreamFormatVersion()
  {
    return MAX_STREAM_FORMAT_VERSION;
  }
  
  public void writeValue(org.omg.CORBA.portable.OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte)
  {
    if (paramByte == 2)
    {
      if (!(paramOutputStream instanceof ValueOutputStream)) {
        throw this.omgWrapper.notAValueoutputstream();
      }
    }
    else if (paramByte != 1) {
      throw this.omgWrapper.invalidStreamFormatVersion(new Integer(paramByte));
    }
    writeValueWithVersion(paramOutputStream, paramSerializable, paramByte);
  }
  
  private ValueHandlerImpl() {}
  
  private ValueHandlerImpl(boolean paramBoolean)
  {
    this();
    this.useHashtables = false;
    this.isInputStream = paramBoolean;
  }
  
  static ValueHandlerImpl getInstance()
  {
    return new ValueHandlerImpl();
  }
  
  static ValueHandlerImpl getInstance(boolean paramBoolean)
  {
    return new ValueHandlerImpl(paramBoolean);
  }
  
  public void writeValue(org.omg.CORBA.portable.OutputStream paramOutputStream, Serializable paramSerializable)
  {
    writeValueWithVersion(paramOutputStream, paramSerializable, (byte)1);
  }
  
  private void writeValueWithVersion(org.omg.CORBA.portable.OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte)
  {
    org.omg.CORBA_2_3.portable.OutputStream localOutputStream = (org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream;
    if (!this.useHashtables)
    {
      if (this.outputStreamBridge == null)
      {
        this.outputStreamBridge = createOutputStream();
        this.outputStreamBridge.setOrbStream(localOutputStream);
      }
      try
      {
        this.outputStreamBridge.increaseRecursionDepth();
        writeValueInternal(this.outputStreamBridge, localOutputStream, paramSerializable, paramByte);
      }
      finally
      {
        this.outputStreamBridge.decreaseRecursionDepth();
      }
      return;
    }
    IIOPOutputStream localIIOPOutputStream = null;
    if (this.outputStreamPairs == null) {
      this.outputStreamPairs = new Hashtable();
    }
    localIIOPOutputStream = (IIOPOutputStream)this.outputStreamPairs.get(paramOutputStream);
    if (localIIOPOutputStream == null)
    {
      localIIOPOutputStream = createOutputStream();
      localIIOPOutputStream.setOrbStream(localOutputStream);
      this.outputStreamPairs.put(paramOutputStream, localIIOPOutputStream);
    }
    try
    {
      localIIOPOutputStream.increaseRecursionDepth();
      writeValueInternal(localIIOPOutputStream, localOutputStream, paramSerializable, paramByte);
    }
    finally
    {
      if (localIIOPOutputStream.decreaseRecursionDepth() == 0) {
        this.outputStreamPairs.remove(paramOutputStream);
      }
    }
  }
  
  private void writeValueInternal(IIOPOutputStream paramIIOPOutputStream, org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte)
  {
    Class localClass = paramSerializable.getClass();
    if (localClass.isArray()) {
      write_Array(paramOutputStream, paramSerializable, localClass.getComponentType());
    } else {
      paramIIOPOutputStream.simpleWriteObject(paramSerializable, paramByte);
    }
  }
  
  public Serializable readValue(org.omg.CORBA.portable.InputStream paramInputStream, int paramInt, Class paramClass, String paramString, RunTime paramRunTime)
  {
    CodeBase localCodeBase = CodeBaseHelper.narrow(paramRunTime);
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)paramInputStream;
    if (!this.useHashtables)
    {
      if (this.inputStreamBridge == null)
      {
        this.inputStreamBridge = createInputStream();
        this.inputStreamBridge.setOrbStream(localInputStream);
        this.inputStreamBridge.setSender(localCodeBase);
        this.inputStreamBridge.setValueHandler(this);
      }
      localObject1 = null;
      try
      {
        this.inputStreamBridge.increaseRecursionDepth();
        localObject1 = readValueInternal(this.inputStreamBridge, localInputStream, paramInt, paramClass, paramString, localCodeBase);
        if (this.inputStreamBridge.decreaseRecursionDepth() != 0) {}
        return localObject1;
      }
      finally
      {
        if (this.inputStreamBridge.decreaseRecursionDepth() != 0) {}
      }
    }
    Object localObject1 = null;
    if (this.inputStreamPairs == null) {
      this.inputStreamPairs = new Hashtable();
    }
    localObject1 = (IIOPInputStream)this.inputStreamPairs.get(paramInputStream);
    if (localObject1 == null)
    {
      localObject1 = createInputStream();
      ((IIOPInputStream)localObject1).setOrbStream(localInputStream);
      ((IIOPInputStream)localObject1).setSender(localCodeBase);
      ((IIOPInputStream)localObject1).setValueHandler(this);
      this.inputStreamPairs.put(paramInputStream, localObject1);
    }
    Serializable localSerializable = null;
    try
    {
      ((IIOPInputStream)localObject1).increaseRecursionDepth();
      localSerializable = readValueInternal((IIOPInputStream)localObject1, localInputStream, paramInt, paramClass, paramString, localCodeBase);
    }
    finally
    {
      if (((IIOPInputStream)localObject1).decreaseRecursionDepth() == 0) {
        this.inputStreamPairs.remove(paramInputStream);
      }
    }
    return localSerializable;
  }
  
  private Serializable readValueInternal(IIOPInputStream paramIIOPInputStream, org.omg.CORBA_2_3.portable.InputStream paramInputStream, int paramInt, Class paramClass, String paramString, CodeBase paramCodeBase)
  {
    Serializable localSerializable = null;
    if (paramClass == null)
    {
      if (isArray(paramString)) {
        read_Array(paramIIOPInputStream, paramInputStream, null, paramCodeBase, paramInt);
      } else {
        paramIIOPInputStream.simpleSkipObject(paramString, paramCodeBase);
      }
      return localSerializable;
    }
    if (paramClass.isArray()) {
      localSerializable = (Serializable)read_Array(paramIIOPInputStream, paramInputStream, paramClass, paramCodeBase, paramInt);
    } else {
      localSerializable = (Serializable)paramIIOPInputStream.simpleReadObject(paramClass, paramString, paramCodeBase, paramInt);
    }
    return localSerializable;
  }
  
  public String getRMIRepositoryID(Class paramClass)
  {
    return RepositoryId.createForJavaType(paramClass);
  }
  
  public boolean isCustomMarshaled(Class paramClass)
  {
    return ObjectStreamClass.lookup(paramClass).isCustomMarshaled();
  }
  
  public RunTime getRunTimeCodeBase()
  {
    if (this.codeBase != null) {
      return this.codeBase;
    }
    this.codeBase = new FVDCodeBaseImpl();
    FVDCodeBaseImpl localFVDCodeBaseImpl = (FVDCodeBaseImpl)this.codeBase;
    localFVDCodeBaseImpl.setValueHandler(this);
    return this.codeBase;
  }
  
  public boolean useFullValueDescription(Class paramClass, String paramString)
    throws IOException
  {
    return RepositoryId.useFullValueDescription(paramClass, paramString);
  }
  
  public String getClassName(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getClassName();
  }
  
  public Class getClassFromType(String paramString)
    throws ClassNotFoundException
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getClassFromType();
  }
  
  public Class getAnyClassFromType(String paramString)
    throws ClassNotFoundException
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getAnyClassFromType();
  }
  
  public String createForAnyType(Class paramClass)
  {
    return RepositoryId.createForAnyType(paramClass);
  }
  
  public String getDefinedInId(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getDefinedInId();
  }
  
  public String getUnqualifiedName(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getUnqualifiedName();
  }
  
  public String getSerialVersionUID(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.getSerialVersionUID();
  }
  
  public boolean isAbstractBase(Class paramClass)
  {
    return RepositoryId.isAbstractBase(paramClass);
  }
  
  public boolean isSequence(String paramString)
  {
    RepositoryId localRepositoryId = RepositoryId.cache.getId(paramString);
    return localRepositoryId.isSequence();
  }
  
  public Serializable writeReplace(Serializable paramSerializable)
  {
    return ObjectStreamClass.lookup(paramSerializable.getClass()).writeReplace(paramSerializable);
  }
  
  private void writeCharArray(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    paramOutputStream.write_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void write_Array(org.omg.CORBA_2_3.portable.OutputStream paramOutputStream, Serializable paramSerializable, Class paramClass)
  {
    Object localObject;
    int j;
    if (paramClass.isPrimitive())
    {
      if (paramClass == Integer.TYPE)
      {
        localObject = (int[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_long_array((int[])localObject, 0, j);
      }
      else if (paramClass == Byte.TYPE)
      {
        localObject = (byte[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_octet_array((byte[])localObject, 0, j);
      }
      else if (paramClass == Long.TYPE)
      {
        localObject = (long[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_longlong_array((long[])localObject, 0, j);
      }
      else if (paramClass == Float.TYPE)
      {
        localObject = (float[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_float_array((float[])localObject, 0, j);
      }
      else if (paramClass == Double.TYPE)
      {
        localObject = (double[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_double_array((double[])localObject, 0, j);
      }
      else if (paramClass == Short.TYPE)
      {
        localObject = (short[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_short_array((short[])localObject, 0, j);
      }
      else if (paramClass == Character.TYPE)
      {
        localObject = (char[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        writeCharArray(paramOutputStream, (char[])localObject, 0, j);
      }
      else if (paramClass == Boolean.TYPE)
      {
        localObject = (boolean[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        paramOutputStream.write_boolean_array((boolean[])localObject, 0, j);
      }
      else
      {
        throw new Error("Invalid primitive type : " + paramSerializable.getClass().getName());
      }
    }
    else
    {
      int i;
      if (paramClass == Object.class)
      {
        localObject = (Object[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        for (i = 0; i < j; i++) {
          Util.writeAny(paramOutputStream, localObject[i]);
        }
      }
      else
      {
        localObject = (Object[])paramSerializable;
        j = localObject.length;
        paramOutputStream.write_ulong(j);
        int k = 2;
        if (paramClass.isInterface())
        {
          String str = paramClass.getName();
          if (Remote.class.isAssignableFrom(paramClass)) {
            k = 0;
          } else if (org.omg.CORBA.Object.class.isAssignableFrom(paramClass)) {
            k = 0;
          } else if (RepositoryId.isAbstractBase(paramClass)) {
            k = 1;
          } else if (ObjectStreamClassCorbaExt.isAbstractInterface(paramClass)) {
            k = 1;
          }
        }
        for (i = 0; i < j; i++) {
          switch (k)
          {
          case 0: 
            Util.writeRemoteObject(paramOutputStream, localObject[i]);
            break;
          case 1: 
            Util.writeAbstractObject(paramOutputStream, localObject[i]);
            break;
          case 2: 
            try
            {
              paramOutputStream.write_value((Serializable)localObject[i]);
            }
            catch (ClassCastException localClassCastException)
            {
              if ((localObject[i] instanceof Serializable)) {
                throw localClassCastException;
              }
              Utility.throwNotSerializableForCorba(localObject[i].getClass().getName());
            }
          }
        }
      }
    }
  }
  
  private void readCharArray(org.omg.CORBA_2_3.portable.InputStream paramInputStream, char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    paramInputStream.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private Object read_Array(IIOPInputStream paramIIOPInputStream, org.omg.CORBA_2_3.portable.InputStream paramInputStream, Class paramClass, CodeBase paramCodeBase, int paramInt)
  {
    try
    {
      int i = paramInputStream.read_ulong();
      if (paramClass == null)
      {
        for (j = 0; j < i; j++) {
          paramInputStream.read_value();
        }
        localObject1 = null;
        return localObject1;
      }
      Object localObject1 = paramClass.getComponentType();
      Object localObject2 = localObject1;
      Object localObject4;
      if (((Class)localObject1).isPrimitive())
      {
        if (localObject1 == Integer.TYPE)
        {
          localObject3 = new int[i];
          paramInputStream.read_long_array((int[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Byte.TYPE)
        {
          localObject3 = new byte[i];
          paramInputStream.read_octet_array((byte[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Long.TYPE)
        {
          localObject3 = new long[i];
          paramInputStream.read_longlong_array((long[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Float.TYPE)
        {
          localObject3 = new float[i];
          paramInputStream.read_float_array((float[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Double.TYPE)
        {
          localObject3 = new double[i];
          paramInputStream.read_double_array((double[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Short.TYPE)
        {
          localObject3 = new short[i];
          paramInputStream.read_short_array((short[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Character.TYPE)
        {
          localObject3 = new char[i];
          readCharArray(paramInputStream, (char[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        if (localObject1 == Boolean.TYPE)
        {
          localObject3 = new boolean[i];
          paramInputStream.read_boolean_array((boolean[])localObject3, 0, i);
          localObject4 = (Serializable)localObject3;
          return localObject4;
        }
        throw new Error("Invalid primitive componentType : " + paramClass.getName());
      }
      if (localObject1 == Object.class)
      {
        localObject3 = (Object[])Array.newInstance((Class)localObject1, i);
        paramIIOPInputStream.activeRecursionMgr.addObject(paramInt, localObject3);
        for (j = 0; j < i; j++)
        {
          localObject4 = null;
          try
          {
            localObject4 = Util.readAny(paramInputStream);
          }
          catch (IndirectionException localIndirectionException1)
          {
            try
            {
              localObject4 = paramIIOPInputStream.activeRecursionMgr.getObject(localIndirectionException1.offset);
            }
            catch (IOException localIOException1)
            {
              throw this.utilWrapper.invalidIndirection(localIOException1, new Integer(localIndirectionException1.offset));
            }
          }
          localObject3[j] = localObject4;
        }
        localObject4 = (Serializable)localObject3;
        return localObject4;
      }
      Object localObject3 = (Object[])Array.newInstance((Class)localObject1, i);
      paramIIOPInputStream.activeRecursionMgr.addObject(paramInt, localObject3);
      int k = 2;
      int m = 0;
      if (((Class)localObject1).isInterface())
      {
        int n = 0;
        if (Remote.class.isAssignableFrom((Class)localObject1))
        {
          k = 0;
          n = 1;
        }
        else if (org.omg.CORBA.Object.class.isAssignableFrom((Class)localObject1))
        {
          k = 0;
          n = 1;
        }
        else if (RepositoryId.isAbstractBase((Class)localObject1))
        {
          k = 1;
          n = 1;
        }
        else if (ObjectStreamClassCorbaExt.isAbstractInterface((Class)localObject1))
        {
          k = 1;
        }
        if (n != 0) {
          try
          {
            String str1 = Util.getCodebase((Class)localObject1);
            String str2 = RepositoryId.createForAnyType((Class)localObject1);
            Class localClass = Utility.loadStubClass(str2, str1, (Class)localObject1);
            localObject2 = localClass;
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            m = 1;
          }
        } else {
          m = 1;
        }
      }
      for (int j = 0; j < i; j++) {
        try
        {
          switch (k)
          {
          case 0: 
            if (m == 0) {
              localObject3[j] = paramInputStream.read_Object(localObject2);
            } else {
              localObject3[j] = Utility.readObjectAndNarrow(paramInputStream, localObject2);
            }
            break;
          case 1: 
            if (m == 0) {
              localObject3[j] = paramInputStream.read_abstract_interface(localObject2);
            } else {
              localObject3[j] = Utility.readAbstractAndNarrow(paramInputStream, localObject2);
            }
            break;
          case 2: 
            localObject3[j] = paramInputStream.read_value(localObject2);
          }
        }
        catch (IndirectionException localIndirectionException2)
        {
          try
          {
            localObject3[j] = paramIIOPInputStream.activeRecursionMgr.getObject(localIndirectionException2.offset);
          }
          catch (IOException localIOException2)
          {
            throw this.utilWrapper.invalidIndirection(localIOException2, new Integer(localIndirectionException2.offset));
          }
        }
      }
      Serializable localSerializable = (Serializable)localObject3;
      return localSerializable;
    }
    finally
    {
      paramIIOPInputStream.activeRecursionMgr.removeObject(paramInt);
    }
  }
  
  private boolean isArray(String paramString)
  {
    return RepositoryId.cache.getId(paramString).isSequence();
  }
  
  private String getOutputStreamClassName()
  {
    return "com.sun.corba.se.impl.io.IIOPOutputStream";
  }
  
  private IIOPOutputStream createOutputStream()
  {
    String str = getOutputStreamClassName();
    try
    {
      IIOPOutputStream localIIOPOutputStream = createOutputStreamBuiltIn(str);
      if (localIIOPOutputStream != null) {
        return localIIOPOutputStream;
      }
      return (IIOPOutputStream)createCustom(IIOPOutputStream.class, str);
    }
    catch (Throwable localThrowable)
    {
      InternalError localInternalError = new InternalError("Error loading " + str);
      localInternalError.initCause(localThrowable);
      throw localInternalError;
    }
  }
  
  private IIOPOutputStream createOutputStreamBuiltIn(final String paramString)
    throws Throwable
  {
    try
    {
      (IIOPOutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public IIOPOutputStream run()
          throws IOException
        {
          return ValueHandlerImpl.this.createOutputStreamBuiltInNoPriv(paramString);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException.getCause();
    }
  }
  
  private IIOPOutputStream createOutputStreamBuiltInNoPriv(String paramString)
    throws IOException
  {
    return paramString.equals(IIOPOutputStream.class.getName()) ? new IIOPOutputStream() : null;
  }
  
  private String getInputStreamClassName()
  {
    return "com.sun.corba.se.impl.io.IIOPInputStream";
  }
  
  private IIOPInputStream createInputStream()
  {
    String str = getInputStreamClassName();
    try
    {
      IIOPInputStream localIIOPInputStream = createInputStreamBuiltIn(str);
      if (localIIOPInputStream != null) {
        return localIIOPInputStream;
      }
      return (IIOPInputStream)createCustom(IIOPInputStream.class, str);
    }
    catch (Throwable localThrowable)
    {
      InternalError localInternalError = new InternalError("Error loading " + str);
      localInternalError.initCause(localThrowable);
      throw localInternalError;
    }
  }
  
  private IIOPInputStream createInputStreamBuiltIn(final String paramString)
    throws Throwable
  {
    try
    {
      (IIOPInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public IIOPInputStream run()
          throws IOException
        {
          return ValueHandlerImpl.this.createInputStreamBuiltInNoPriv(paramString);
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw localPrivilegedActionException.getCause();
    }
  }
  
  private IIOPInputStream createInputStreamBuiltInNoPriv(String paramString)
    throws IOException
  {
    return paramString.equals(IIOPInputStream.class.getName()) ? new IIOPInputStream() : null;
  }
  
  private <T> T createCustom(Class<T> paramClass, String paramString)
    throws Throwable
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    if (localClassLoader == null) {
      localClassLoader = ClassLoader.getSystemClassLoader();
    }
    Class localClass1 = localClassLoader.loadClass(paramString);
    Class localClass2 = localClass1.asSubclass(paramClass);
    return localClass2.newInstance();
  }
  
  TCKind getJavaCharTCKind()
  {
    return TCKind.tk_wchar;
  }
}
