package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.ClientDelegateFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListFactory;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.CORBA.ValueHandlerMultiFormat;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public final class ORBUtility
{
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("util");
  private static OMGSystemException omgWrapper = OMGSystemException.get("util");
  private static StructMember[] members = null;
  private static final Hashtable exceptionClassNames = new Hashtable();
  private static final Hashtable exceptionRepositoryIds = new Hashtable();
  
  private ORBUtility() {}
  
  private static StructMember[] systemExceptionMembers(ORB paramORB)
  {
    if (members == null)
    {
      members = new StructMember[3];
      members[0] = new StructMember("id", paramORB.create_string_tc(0), null);
      members[1] = new StructMember("minor", paramORB.get_primitive_tc(TCKind.tk_long), null);
      members[2] = new StructMember("completed", paramORB.get_primitive_tc(TCKind.tk_long), null);
    }
    return members;
  }
  
  private static TypeCode getSystemExceptionTypeCode(ORB paramORB, String paramString1, String paramString2)
  {
    synchronized (TypeCode.class)
    {
      return paramORB.create_exception_tc(paramString1, paramString2, systemExceptionMembers(paramORB));
    }
  }
  
  private static boolean isSystemExceptionTypeCode(TypeCode paramTypeCode, ORB paramORB)
  {
    StructMember[] arrayOfStructMember = systemExceptionMembers(paramORB);
    try
    {
      return (paramTypeCode.kind().value() == 22) && (paramTypeCode.member_count() == 3) && (paramTypeCode.member_type(0).equal(arrayOfStructMember[0].type)) && (paramTypeCode.member_type(1).equal(arrayOfStructMember[1].type)) && (paramTypeCode.member_type(2).equal(arrayOfStructMember[2].type));
    }
    catch (BadKind localBadKind)
    {
      return false;
    }
    catch (Bounds localBounds) {}
    return false;
  }
  
  public static void insertSystemException(SystemException paramSystemException, Any paramAny)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    ORB localORB = (ORB)localOutputStream.orb();
    String str1 = paramSystemException.getClass().getName();
    String str2 = repositoryIdOf(str1);
    localOutputStream.write_string(str2);
    localOutputStream.write_long(paramSystemException.minor);
    localOutputStream.write_long(paramSystemException.completed.value());
    paramAny.read_value(localOutputStream.create_input_stream(), getSystemExceptionTypeCode(localORB, str2, str1));
  }
  
  public static SystemException extractSystemException(Any paramAny)
  {
    InputStream localInputStream = paramAny.create_input_stream();
    ORB localORB = (ORB)localInputStream.orb();
    if (!isSystemExceptionTypeCode(paramAny.type(), localORB)) {
      throw wrapper.unknownDsiSysex(CompletionStatus.COMPLETED_MAYBE);
    }
    return readSystemException(localInputStream);
  }
  
  public static ValueHandler createValueHandler()
  {
    return Util.createValueHandler();
  }
  
  public static boolean isForeignORB(ORB paramORB)
  {
    if (paramORB == null) {
      return false;
    }
    try
    {
      return paramORB.getORBVersion().equals(ORBVersionFactory.getFOREIGN());
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  public static int bytesToInt(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte[(paramInt++)] << 24 & 0xFF000000;
    int j = paramArrayOfByte[(paramInt++)] << 16 & 0xFF0000;
    int k = paramArrayOfByte[(paramInt++)] << 8 & 0xFF00;
    int m = paramArrayOfByte[(paramInt++)] << 0 & 0xFF;
    return i | j | k | m;
  }
  
  public static void intToBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 24 & 0xFF));
    paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 16 & 0xFF));
    paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 8 & 0xFF));
    paramArrayOfByte[(paramInt2++)] = ((byte)(paramInt1 >>> 0 & 0xFF));
  }
  
  public static int hexOf(char paramChar)
  {
    int i = paramChar - '0';
    if ((i >= 0) && (i <= 9)) {
      return i;
    }
    i = paramChar - 'a' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    i = paramChar - 'A' + 10;
    if ((i >= 10) && (i <= 15)) {
      return i;
    }
    throw wrapper.badHexDigit();
  }
  
  public static void writeSystemException(SystemException paramSystemException, OutputStream paramOutputStream)
  {
    String str = repositoryIdOf(paramSystemException.getClass().getName());
    paramOutputStream.write_string(str);
    paramOutputStream.write_long(paramSystemException.minor);
    paramOutputStream.write_long(paramSystemException.completed.value());
  }
  
  public static SystemException readSystemException(InputStream paramInputStream)
  {
    try
    {
      String str = classNameOf(paramInputStream.read_string());
      SystemException localSystemException = (SystemException)SharedSecrets.getJavaCorbaAccess().loadClass(str).newInstance();
      localSystemException.minor = paramInputStream.read_long();
      localSystemException.completed = CompletionStatus.from_int(paramInputStream.read_long());
      return localSystemException;
    }
    catch (Exception localException)
    {
      throw wrapper.unknownSysex(CompletionStatus.COMPLETED_MAYBE, localException);
    }
  }
  
  public static String classNameOf(String paramString)
  {
    String str = null;
    str = (String)exceptionClassNames.get(paramString);
    if (str == null) {
      str = "org.omg.CORBA.UNKNOWN";
    }
    return str;
  }
  
  public static boolean isSystemException(String paramString)
  {
    String str = null;
    str = (String)exceptionClassNames.get(paramString);
    return str != null;
  }
  
  public static byte getEncodingVersion(ORB paramORB, IOR paramIOR)
  {
    if (paramORB.getORBData().isJavaSerializationEnabled())
    {
      IIOPProfile localIIOPProfile = paramIOR.getProfile();
      IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localIIOPProfile.getTaggedProfileTemplate();
      Iterator localIterator = localIIOPProfileTemplate.iteratorById(1398099458);
      if (localIterator.hasNext())
      {
        JavaSerializationComponent localJavaSerializationComponent = (JavaSerializationComponent)localIterator.next();
        int i = localJavaSerializationComponent.javaSerializationVersion();
        if (i >= 1) {
          return 1;
        }
        if (i > 0) {
          return localJavaSerializationComponent.javaSerializationVersion();
        }
      }
    }
    return 0;
  }
  
  public static String repositoryIdOf(String paramString)
  {
    String str = (String)exceptionRepositoryIds.get(paramString);
    if (str == null) {
      str = "IDL:omg.org/CORBA/UNKNOWN:1.0";
    }
    return str;
  }
  
  public static int[] parseVersion(String paramString)
  {
    if (paramString == null) {
      return new int[0];
    }
    char[] arrayOfChar = paramString.toCharArray();
    for (int i = 0; (i < arrayOfChar.length) && ((arrayOfChar[i] < '0') || (arrayOfChar[i] > '9')); i++) {
      if (i == arrayOfChar.length) {
        return new int[0];
      }
    }
    int j = i + 1;
    int k = 1;
    while (j < arrayOfChar.length)
    {
      if (arrayOfChar[j] == '.') {
        k++;
      } else {
        if ((arrayOfChar[j] < '0') || (arrayOfChar[j] > '9')) {
          break;
        }
      }
      j++;
    }
    int[] arrayOfInt = new int[k];
    for (int m = 0; m < k; m++)
    {
      int n = paramString.indexOf('.', i);
      if ((n == -1) || (n > j)) {
        n = j;
      }
      if (i >= n) {
        arrayOfInt[m] = 0;
      } else {
        arrayOfInt[m] = Integer.parseInt(paramString.substring(i, n));
      }
      i = n + 1;
    }
    return arrayOfInt;
  }
  
  public static int compareVersion(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1 == null) {
      paramArrayOfInt1 = new int[0];
    }
    if (paramArrayOfInt2 == null) {
      paramArrayOfInt2 = new int[0];
    }
    for (int i = 0; i < paramArrayOfInt1.length; i++)
    {
      if ((i >= paramArrayOfInt2.length) || (paramArrayOfInt1[i] > paramArrayOfInt2[i])) {
        return 1;
      }
      if (paramArrayOfInt1[i] < paramArrayOfInt2[i]) {
        return -1;
      }
    }
    return paramArrayOfInt1.length == paramArrayOfInt2.length ? 0 : -1;
  }
  
  public static synchronized int compareVersion(String paramString1, String paramString2)
  {
    return compareVersion(parseVersion(paramString1), parseVersion(paramString2));
  }
  
  private static String compressClassName(String paramString)
  {
    String str = "com.sun.corba.se.";
    if (paramString.startsWith(str)) {
      return "(ORB)." + paramString.substring(str.length());
    }
    return paramString;
  }
  
  public static String getThreadName(Thread paramThread)
  {
    if (paramThread == null) {
      return "null";
    }
    String str = paramThread.getName();
    StringTokenizer localStringTokenizer = new StringTokenizer(str);
    int i = localStringTokenizer.countTokens();
    if (i != 5) {
      return str;
    }
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = localStringTokenizer.nextToken();
    }
    if (!arrayOfString[0].equals("SelectReaderThread")) {
      return str;
    }
    return "SelectReaderThread[" + arrayOfString[2] + ":" + arrayOfString[3] + "]";
  }
  
  private static String formatStackTraceElement(StackTraceElement paramStackTraceElement)
  {
    return compressClassName(paramStackTraceElement.getClassName()) + "." + paramStackTraceElement.getMethodName() + (paramStackTraceElement.getFileName() != null ? "(" + paramStackTraceElement.getFileName() + ")" : (paramStackTraceElement.getFileName() != null) && (paramStackTraceElement.getLineNumber() >= 0) ? "(" + paramStackTraceElement.getFileName() + ":" + paramStackTraceElement.getLineNumber() + ")" : paramStackTraceElement.isNativeMethod() ? "(Native Method)" : "(Unknown Source)");
  }
  
  private static void printStackTrace(StackTraceElement[] paramArrayOfStackTraceElement)
  {
    System.out.println("    Stack Trace:");
    for (int i = 1; i < paramArrayOfStackTraceElement.length; i++)
    {
      System.out.print("        >");
      System.out.println(formatStackTraceElement(paramArrayOfStackTraceElement[i]));
    }
  }
  
  public static synchronized void dprint(Object paramObject, String paramString)
  {
    System.out.println(compressClassName(paramObject.getClass().getName()) + "(" + getThreadName(Thread.currentThread()) + "): " + paramString);
  }
  
  public static synchronized void dprint(String paramString1, String paramString2)
  {
    System.out.println(compressClassName(paramString1) + "(" + getThreadName(Thread.currentThread()) + "): " + paramString2);
  }
  
  public synchronized void dprint(String paramString)
  {
    dprint(this, paramString);
  }
  
  public static synchronized void dprintTrace(Object paramObject, String paramString)
  {
    dprint(paramObject, paramString);
    Throwable localThrowable = new Throwable();
    printStackTrace(localThrowable.getStackTrace());
  }
  
  public static synchronized void dprint(Object paramObject, String paramString, Throwable paramThrowable)
  {
    System.out.println(compressClassName(paramObject.getClass().getName()) + '(' + Thread.currentThread() + "): " + paramString);
    if (paramThrowable != null) {
      printStackTrace(paramThrowable.getStackTrace());
    }
  }
  
  public static String[] concatenateStringArrays(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    String[] arrayOfString = new String[paramArrayOfString1.length + paramArrayOfString2.length];
    for (int i = 0; i < paramArrayOfString1.length; i++) {
      arrayOfString[i] = paramArrayOfString1[i];
    }
    for (i = 0; i < paramArrayOfString2.length; i++) {
      arrayOfString[(i + paramArrayOfString1.length)] = paramArrayOfString2[i];
    }
    return arrayOfString;
  }
  
  public static void throwNotSerializableForCorba(String paramString)
  {
    throw omgWrapper.notSerializable(CompletionStatus.COMPLETED_MAYBE, paramString);
  }
  
  public static byte getMaxStreamFormatVersion()
  {
    ValueHandler localValueHandler = Util.createValueHandler();
    if (!(localValueHandler instanceof ValueHandlerMultiFormat)) {
      return 1;
    }
    return ((ValueHandlerMultiFormat)localValueHandler).getMaximumStreamFormatVersion();
  }
  
  public static CorbaClientDelegate makeClientDelegate(IOR paramIOR)
  {
    ORB localORB = paramIOR.getORB();
    CorbaContactInfoList localCorbaContactInfoList = localORB.getCorbaContactInfoListFactory().create(paramIOR);
    CorbaClientDelegate localCorbaClientDelegate = localORB.getClientDelegateFactory().create(localCorbaContactInfoList);
    return localCorbaClientDelegate;
  }
  
  public static org.omg.CORBA.Object makeObjectReference(IOR paramIOR)
  {
    CorbaClientDelegate localCorbaClientDelegate = makeClientDelegate(paramIOR);
    CORBAObjectImpl localCORBAObjectImpl = new CORBAObjectImpl();
    StubAdapter.setDelegate(localCORBAObjectImpl, localCorbaClientDelegate);
    return localCORBAObjectImpl;
  }
  
  public static IOR getIOR(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      throw wrapper.nullObjectReference();
    }
    IOR localIOR = null;
    if (StubAdapter.isStub(paramObject))
    {
      Delegate localDelegate = StubAdapter.getDelegate(paramObject);
      if ((localDelegate instanceof CorbaClientDelegate))
      {
        CorbaClientDelegate localCorbaClientDelegate = (CorbaClientDelegate)localDelegate;
        ContactInfoList localContactInfoList = localCorbaClientDelegate.getContactInfoList();
        if ((localContactInfoList instanceof CorbaContactInfoList))
        {
          CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localContactInfoList;
          localIOR = localCorbaContactInfoList.getTargetIOR();
          if (localIOR == null) {
            throw wrapper.nullIor();
          }
          return localIOR;
        }
        throw new INTERNAL();
      }
      throw wrapper.objrefFromForeignOrb();
    }
    throw wrapper.localObjectNotAllowed();
  }
  
  public static IOR connectAndGetIOR(ORB paramORB, org.omg.CORBA.Object paramObject)
  {
    IOR localIOR;
    try
    {
      localIOR = getIOR(paramObject);
    }
    catch (BAD_OPERATION localBAD_OPERATION)
    {
      if (StubAdapter.isStub(paramObject)) {
        try
        {
          StubAdapter.connect(paramObject, paramORB);
        }
        catch (RemoteException localRemoteException)
        {
          throw wrapper.connectingServant(localRemoteException);
        }
      } else {
        paramORB.connect(paramObject);
      }
      localIOR = getIOR(paramObject);
    }
    return localIOR;
  }
  
  public static String operationNameAndRequestId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return "op/" + paramCorbaMessageMediator.getOperationName() + " id/" + paramCorbaMessageMediator.getRequestId();
  }
  
  public static boolean isPrintable(char paramChar)
  {
    if (Character.isJavaIdentifierStart(paramChar)) {
      return true;
    }
    if (Character.isDigit(paramChar)) {
      return true;
    }
    switch (Character.getType(paramChar))
    {
    case 27: 
      return true;
    case 20: 
      return true;
    case 25: 
      return true;
    case 24: 
      return true;
    case 21: 
      return true;
    case 22: 
      return true;
    }
    return false;
  }
  
  public static String getClassSecurityInfo(Class paramClass)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        StringBuffer localStringBuffer = new StringBuffer(500);
        ProtectionDomain localProtectionDomain = this.val$cl.getProtectionDomain();
        Policy localPolicy = Policy.getPolicy();
        PermissionCollection localPermissionCollection = localPolicy.getPermissions(localProtectionDomain);
        localStringBuffer.append("\nPermissionCollection ");
        localStringBuffer.append(localPermissionCollection.toString());
        localStringBuffer.append(localProtectionDomain.toString());
        return localStringBuffer.toString();
      }
    });
    return str;
  }
  
  static
  {
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_CONTEXT:1.0", "org.omg.CORBA.BAD_CONTEXT");
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0", "org.omg.CORBA.BAD_INV_ORDER");
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_OPERATION:1.0", "org.omg.CORBA.BAD_OPERATION");
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_PARAM:1.0", "org.omg.CORBA.BAD_PARAM");
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_TYPECODE:1.0", "org.omg.CORBA.BAD_TYPECODE");
    exceptionClassNames.put("IDL:omg.org/CORBA/COMM_FAILURE:1.0", "org.omg.CORBA.COMM_FAILURE");
    exceptionClassNames.put("IDL:omg.org/CORBA/DATA_CONVERSION:1.0", "org.omg.CORBA.DATA_CONVERSION");
    exceptionClassNames.put("IDL:omg.org/CORBA/IMP_LIMIT:1.0", "org.omg.CORBA.IMP_LIMIT");
    exceptionClassNames.put("IDL:omg.org/CORBA/INTF_REPOS:1.0", "org.omg.CORBA.INTF_REPOS");
    exceptionClassNames.put("IDL:omg.org/CORBA/INTERNAL:1.0", "org.omg.CORBA.INTERNAL");
    exceptionClassNames.put("IDL:omg.org/CORBA/INV_FLAG:1.0", "org.omg.CORBA.INV_FLAG");
    exceptionClassNames.put("IDL:omg.org/CORBA/INV_IDENT:1.0", "org.omg.CORBA.INV_IDENT");
    exceptionClassNames.put("IDL:omg.org/CORBA/INV_OBJREF:1.0", "org.omg.CORBA.INV_OBJREF");
    exceptionClassNames.put("IDL:omg.org/CORBA/MARSHAL:1.0", "org.omg.CORBA.MARSHAL");
    exceptionClassNames.put("IDL:omg.org/CORBA/NO_MEMORY:1.0", "org.omg.CORBA.NO_MEMORY");
    exceptionClassNames.put("IDL:omg.org/CORBA/FREE_MEM:1.0", "org.omg.CORBA.FREE_MEM");
    exceptionClassNames.put("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0", "org.omg.CORBA.NO_IMPLEMENT");
    exceptionClassNames.put("IDL:omg.org/CORBA/NO_PERMISSION:1.0", "org.omg.CORBA.NO_PERMISSION");
    exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESOURCES:1.0", "org.omg.CORBA.NO_RESOURCES");
    exceptionClassNames.put("IDL:omg.org/CORBA/NO_RESPONSE:1.0", "org.omg.CORBA.NO_RESPONSE");
    exceptionClassNames.put("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0", "org.omg.CORBA.OBJ_ADAPTER");
    exceptionClassNames.put("IDL:omg.org/CORBA/INITIALIZE:1.0", "org.omg.CORBA.INITIALIZE");
    exceptionClassNames.put("IDL:omg.org/CORBA/PERSIST_STORE:1.0", "org.omg.CORBA.PERSIST_STORE");
    exceptionClassNames.put("IDL:omg.org/CORBA/TRANSIENT:1.0", "org.omg.CORBA.TRANSIENT");
    exceptionClassNames.put("IDL:omg.org/CORBA/UNKNOWN:1.0", "org.omg.CORBA.UNKNOWN");
    exceptionClassNames.put("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0", "org.omg.CORBA.OBJECT_NOT_EXIST");
    exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0", "org.omg.CORBA.INVALID_TRANSACTION");
    exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0", "org.omg.CORBA.TRANSACTION_REQUIRED");
    exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0", "org.omg.CORBA.TRANSACTION_ROLLEDBACK");
    exceptionClassNames.put("IDL:omg.org/CORBA/INV_POLICY:1.0", "org.omg.CORBA.INV_POLICY");
    exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0", "org.omg.CORBA.TRANSACTION_UNAVAILABLE");
    exceptionClassNames.put("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0", "org.omg.CORBA.TRANSACTION_MODE");
    exceptionClassNames.put("IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0", "org.omg.CORBA.CODESET_INCOMPATIBLE");
    exceptionClassNames.put("IDL:omg.org/CORBA/REBIND:1.0", "org.omg.CORBA.REBIND");
    exceptionClassNames.put("IDL:omg.org/CORBA/TIMEOUT:1.0", "org.omg.CORBA.TIMEOUT");
    exceptionClassNames.put("IDL:omg.org/CORBA/BAD_QOS:1.0", "org.omg.CORBA.BAD_QOS");
    exceptionClassNames.put("IDL:omg.org/CORBA/INVALID_ACTIVITY:1.0", "org.omg.CORBA.INVALID_ACTIVITY");
    exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_COMPLETED:1.0", "org.omg.CORBA.ACTIVITY_COMPLETED");
    exceptionClassNames.put("IDL:omg.org/CORBA/ACTIVITY_REQUIRED:1.0", "org.omg.CORBA.ACTIVITY_REQUIRED");
    Enumeration localEnumeration = exceptionClassNames.keys();
    try
    {
      while (localEnumeration.hasMoreElements())
      {
        Object localObject = localEnumeration.nextElement();
        String str1 = (String)localObject;
        String str2 = (String)exceptionClassNames.get(str1);
        exceptionRepositoryIds.put(str2, str1);
      }
    }
    catch (NoSuchElementException localNoSuchElementException) {}
  }
}
