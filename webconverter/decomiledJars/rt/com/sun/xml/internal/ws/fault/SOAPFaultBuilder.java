package com.sun.xml.internal.ws.fault;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import com.sun.xml.internal.ws.message.FaultMessage;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPFault;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class SOAPFaultBuilder
{
  private static final JAXBContext JAXB_CONTEXT = createJAXBContext();
  private static final Logger logger = Logger.getLogger(SOAPFaultBuilder.class.getName());
  public static final boolean captureStackTrace;
  static final String CAPTURE_STACK_TRACE_PROPERTY = SOAPFaultBuilder.class.getName() + ".captureStackTrace";
  
  public SOAPFaultBuilder() {}
  
  abstract DetailType getDetail();
  
  abstract void setDetail(DetailType paramDetailType);
  
  @XmlTransient
  @Nullable
  public QName getFirstDetailEntryName()
  {
    DetailType localDetailType = getDetail();
    if (localDetailType != null)
    {
      Node localNode = localDetailType.getDetail(0);
      if (localNode != null) {
        return new QName(localNode.getNamespaceURI(), localNode.getLocalName());
      }
    }
    return null;
  }
  
  abstract String getFaultString();
  
  public Throwable createException(Map<QName, CheckedExceptionImpl> paramMap)
    throws JAXBException
  {
    DetailType localDetailType = getDetail();
    Node localNode = null;
    if (localDetailType != null) {
      localNode = localDetailType.getDetail(0);
    }
    if ((localNode == null) || (paramMap == null)) {
      return attachServerException(getProtocolException());
    }
    QName localQName = new QName(localNode.getNamespaceURI(), localNode.getLocalName());
    CheckedExceptionImpl localCheckedExceptionImpl = (CheckedExceptionImpl)paramMap.get(localQName);
    if (localCheckedExceptionImpl == null) {
      return attachServerException(getProtocolException());
    }
    if (localCheckedExceptionImpl.getExceptionType().equals(ExceptionType.UserDefined)) {
      return attachServerException(createUserDefinedException(localCheckedExceptionImpl));
    }
    Class localClass = localCheckedExceptionImpl.getExceptionClass();
    try
    {
      Constructor localConstructor = localClass.getConstructor(new Class[] { String.class, (Class)localCheckedExceptionImpl.getDetailType().type });
      Exception localException2 = (Exception)localConstructor.newInstance(new Object[] { getFaultString(), getJAXBObject(localNode, localCheckedExceptionImpl) });
      return attachServerException(localException2);
    }
    catch (Exception localException1)
    {
      throw new WebServiceException(localException1);
    }
  }
  
  @NotNull
  public static Message createSOAPFaultMessage(@NotNull SOAPVersion paramSOAPVersion, @NotNull ProtocolException paramProtocolException, @Nullable QName paramQName)
  {
    Object localObject = getFaultDetail(null, paramProtocolException);
    if (paramSOAPVersion == SOAPVersion.SOAP_12) {
      return createSOAP12Fault(paramSOAPVersion, paramProtocolException, localObject, null, paramQName);
    }
    return createSOAP11Fault(paramSOAPVersion, paramProtocolException, localObject, null, paramQName);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable)
  {
    Throwable localThrowable = (paramThrowable instanceof InvocationTargetException) ? ((InvocationTargetException)paramThrowable).getTargetException() : paramThrowable;
    return createSOAPFaultMessage(paramSOAPVersion, paramCheckedExceptionImpl, localThrowable, null);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable, QName paramQName)
  {
    Object localObject = getFaultDetail(paramCheckedExceptionImpl, paramThrowable);
    if (paramSOAPVersion == SOAPVersion.SOAP_12) {
      return createSOAP12Fault(paramSOAPVersion, paramThrowable, localObject, paramCheckedExceptionImpl, paramQName);
    }
    return createSOAP11Fault(paramSOAPVersion, paramThrowable, localObject, paramCheckedExceptionImpl, paramQName);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, String paramString, QName paramQName)
  {
    if (paramQName == null) {
      paramQName = getDefaultFaultCode(paramSOAPVersion);
    }
    return createSOAPFaultMessage(paramSOAPVersion, paramString, paramQName, null);
  }
  
  public static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, SOAPFault paramSOAPFault)
  {
    switch (2.$SwitchMap$com$sun$xml$internal$ws$api$SOAPVersion[paramSOAPVersion.ordinal()])
    {
    case 1: 
      return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(paramSOAPFault), paramSOAPVersion);
    case 2: 
      return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(paramSOAPFault), paramSOAPVersion);
    }
    throw new AssertionError();
  }
  
  private static Message createSOAPFaultMessage(SOAPVersion paramSOAPVersion, String paramString, QName paramQName, Element paramElement)
  {
    switch (2.$SwitchMap$com$sun$xml$internal$ws$api$SOAPVersion[paramSOAPVersion.ordinal()])
    {
    case 1: 
      return JAXBMessage.create(JAXB_CONTEXT, new SOAP11Fault(paramQName, paramString, null, paramElement), paramSOAPVersion);
    case 2: 
      return JAXBMessage.create(JAXB_CONTEXT, new SOAP12Fault(paramQName, paramString, paramElement), paramSOAPVersion);
    }
    throw new AssertionError();
  }
  
  final void captureStackTrace(@Nullable Throwable paramThrowable)
  {
    if (paramThrowable == null) {
      return;
    }
    if (!captureStackTrace) {
      return;
    }
    try
    {
      Document localDocument = DOMUtil.createDom();
      ExceptionBean.marshal(paramThrowable, localDocument);
      DetailType localDetailType = getDetail();
      if (localDetailType == null) {
        setDetail(localDetailType = new DetailType());
      }
      localDetailType.getDetails().add(localDocument.getDocumentElement());
    }
    catch (JAXBException localJAXBException)
    {
      logger.log(Level.WARNING, "Unable to capture the stack trace into XML", localJAXBException);
    }
  }
  
  private <T extends Throwable> T attachServerException(T paramT)
  {
    DetailType localDetailType = getDetail();
    if (localDetailType == null) {
      return paramT;
    }
    Iterator localIterator = localDetailType.getDetails().iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      if (ExceptionBean.isStackTraceXml(localElement))
      {
        try
        {
          paramT.initCause(ExceptionBean.unmarshal(localElement));
        }
        catch (JAXBException localJAXBException)
        {
          logger.log(Level.WARNING, "Unable to read the capture stack trace in the fault", localJAXBException);
        }
        return paramT;
      }
    }
    return paramT;
  }
  
  protected abstract Throwable getProtocolException();
  
  private Object getJAXBObject(Node paramNode, CheckedExceptionImpl paramCheckedExceptionImpl)
    throws JAXBException
  {
    XMLBridge localXMLBridge = paramCheckedExceptionImpl.getBond();
    return localXMLBridge.unmarshal(paramNode, null);
  }
  
  /* Error */
  private Exception createUserDefinedException(CheckedExceptionImpl paramCheckedExceptionImpl)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 503	com/sun/xml/internal/ws/model/CheckedExceptionImpl:getExceptionClass	()Ljava/lang/Class;
    //   4: astore_2
    //   5: aload_1
    //   6: invokevirtual 502	com/sun/xml/internal/ws/model/CheckedExceptionImpl:getDetailBean	()Ljava/lang/Class;
    //   9: astore_3
    //   10: aload_0
    //   11: invokevirtual 474	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:getDetail	()Lcom/sun/xml/internal/ws/fault/DetailType;
    //   14: invokevirtual 460	com/sun/xml/internal/ws/fault/DetailType:getDetails	()Ljava/util/List;
    //   17: iconst_0
    //   18: invokeinterface 553 2 0
    //   23: checkcast 256	org/w3c/dom/Node
    //   26: astore 4
    //   28: aload_0
    //   29: aload 4
    //   31: aload_1
    //   32: invokespecial 487	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:getJAXBObject	(Lorg/w3c/dom/Node;Lcom/sun/xml/internal/ws/model/CheckedExceptionImpl;)Ljava/lang/Object;
    //   35: astore 5
    //   37: aload_2
    //   38: iconst_2
    //   39: anewarray 222	java/lang/Class
    //   42: dup
    //   43: iconst_0
    //   44: ldc 14
    //   46: aastore
    //   47: dup
    //   48: iconst_1
    //   49: aload_3
    //   50: aastore
    //   51: invokevirtual 512	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   54: astore 6
    //   56: aload 6
    //   58: iconst_2
    //   59: anewarray 226	java/lang/Object
    //   62: dup
    //   63: iconst_0
    //   64: aload_0
    //   65: invokevirtual 476	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:getFaultString	()Ljava/lang/String;
    //   68: aastore
    //   69: dup
    //   70: iconst_1
    //   71: aload 5
    //   73: aastore
    //   74: invokevirtual 527	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   77: checkcast 224	java/lang/Exception
    //   80: areturn
    //   81: astore 7
    //   83: aload_2
    //   84: iconst_1
    //   85: anewarray 222	java/lang/Class
    //   88: dup
    //   89: iconst_0
    //   90: ldc 14
    //   92: aastore
    //   93: invokevirtual 512	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   96: astore 6
    //   98: aload 6
    //   100: iconst_1
    //   101: anewarray 226	java/lang/Object
    //   104: dup
    //   105: iconst_0
    //   106: aload_0
    //   107: invokevirtual 476	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:getFaultString	()Ljava/lang/String;
    //   110: aastore
    //   111: invokevirtual 527	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   114: checkcast 224	java/lang/Exception
    //   117: areturn
    //   118: astore 4
    //   120: new 252	javax/xml/ws/WebServiceException
    //   123: dup
    //   124: aload 4
    //   126: invokespecial 547	javax/xml/ws/WebServiceException:<init>	(Ljava/lang/Throwable;)V
    //   129: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	130	0	this	SOAPFaultBuilder
    //   0	130	1	paramCheckedExceptionImpl	CheckedExceptionImpl
    //   4	80	2	localClass1	Class
    //   9	41	3	localClass2	Class
    //   26	4	4	localNode	Node
    //   118	7	4	localException	Exception
    //   35	37	5	localObject	Object
    //   54	45	6	localConstructor	Constructor
    //   81	1	7	localNoSuchMethodException	NoSuchMethodException
    // Exception table:
    //   from	to	target	type
    //   37	80	81	java/lang/NoSuchMethodException
    //   10	80	118	java/lang/Exception
    //   81	117	118	java/lang/Exception
  }
  
  private static String getWriteMethod(Field paramField)
  {
    return "set" + StringUtils.capitalize(paramField.getName());
  }
  
  private static Object getFaultDetail(CheckedExceptionImpl paramCheckedExceptionImpl, Throwable paramThrowable)
  {
    if (paramCheckedExceptionImpl == null) {
      return null;
    }
    if (paramCheckedExceptionImpl.getExceptionType().equals(ExceptionType.UserDefined)) {
      return createDetailFromUserDefinedException(paramCheckedExceptionImpl, paramThrowable);
    }
    try
    {
      Method localMethod = paramThrowable.getClass().getMethod("getFaultInfo", new Class[0]);
      return localMethod.invoke(paramThrowable, new Object[0]);
    }
    catch (Exception localException)
    {
      throw new SerializationException(localException);
    }
  }
  
  private static Object createDetailFromUserDefinedException(CheckedExceptionImpl paramCheckedExceptionImpl, Object paramObject)
  {
    Class localClass = paramCheckedExceptionImpl.getDetailBean();
    Field[] arrayOfField1 = localClass.getDeclaredFields();
    try
    {
      Object localObject = localClass.newInstance();
      for (Field localField1 : arrayOfField1)
      {
        Method localMethod1 = paramObject.getClass().getMethod(getReadMethod(localField1), new Class[0]);
        try
        {
          Method localMethod2 = localClass.getMethod(getWriteMethod(localField1), new Class[] { localMethod1.getReturnType() });
          localMethod2.invoke(localObject, new Object[] { localMethod1.invoke(paramObject, new Object[0]) });
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          Field localField2 = localClass.getField(localField1.getName());
          localField2.set(localObject, localMethod1.invoke(paramObject, new Object[0]));
        }
      }
      return localObject;
    }
    catch (Exception localException)
    {
      throw new SerializationException(localException);
    }
  }
  
  private static String getReadMethod(Field paramField)
  {
    if (paramField.getType().isAssignableFrom(Boolean.TYPE)) {
      return "is" + StringUtils.capitalize(paramField.getName());
    }
    return "get" + StringUtils.capitalize(paramField.getName());
  }
  
  private static Message createSOAP11Fault(SOAPVersion paramSOAPVersion, Throwable paramThrowable, Object paramObject, CheckedExceptionImpl paramCheckedExceptionImpl, QName paramQName)
  {
    SOAPFaultException localSOAPFaultException = null;
    String str1 = null;
    String str2 = null;
    Throwable localThrowable = paramThrowable.getCause();
    if ((paramThrowable instanceof SOAPFaultException)) {
      localSOAPFaultException = (SOAPFaultException)paramThrowable;
    } else if ((localThrowable != null) && ((localThrowable instanceof SOAPFaultException))) {
      localSOAPFaultException = (SOAPFaultException)paramThrowable.getCause();
    }
    if (localSOAPFaultException != null)
    {
      localObject = localSOAPFaultException.getFault().getFaultCodeAsQName();
      if (localObject != null) {
        paramQName = (QName)localObject;
      }
      str1 = localSOAPFaultException.getFault().getFaultString();
      str2 = localSOAPFaultException.getFault().getFaultActor();
    }
    if (paramQName == null) {
      paramQName = getDefaultFaultCode(paramSOAPVersion);
    }
    if (str1 == null)
    {
      str1 = paramThrowable.getMessage();
      if (str1 == null) {
        str1 = paramThrowable.toString();
      }
    }
    Object localObject = null;
    QName localQName = null;
    if ((paramObject == null) && (localSOAPFaultException != null))
    {
      localObject = localSOAPFaultException.getFault().getDetail();
      localQName = getFirstDetailEntryName((Detail)localObject);
    }
    else if (paramCheckedExceptionImpl != null)
    {
      try
      {
        DOMResult localDOMResult = new DOMResult();
        paramCheckedExceptionImpl.getBond().marshal(paramObject, localDOMResult);
        localObject = (Element)localDOMResult.getNode().getFirstChild();
        localQName = getFirstDetailEntryName((Element)localObject);
      }
      catch (JAXBException localJAXBException)
      {
        str1 = paramThrowable.getMessage();
        paramQName = getDefaultFaultCode(paramSOAPVersion);
      }
    }
    SOAP11Fault localSOAP11Fault = new SOAP11Fault(paramQName, str1, str2, (Element)localObject);
    if (paramCheckedExceptionImpl == null) {
      localSOAP11Fault.captureStackTrace(paramThrowable);
    }
    Message localMessage = JAXBMessage.create(JAXB_CONTEXT, localSOAP11Fault, paramSOAPVersion);
    return new FaultMessage(localMessage, localQName);
  }
  
  @Nullable
  private static QName getFirstDetailEntryName(@Nullable Detail paramDetail)
  {
    if (paramDetail != null)
    {
      Iterator localIterator = paramDetail.getDetailEntries();
      if (localIterator.hasNext())
      {
        DetailEntry localDetailEntry = (DetailEntry)localIterator.next();
        return getFirstDetailEntryName(localDetailEntry);
      }
    }
    return null;
  }
  
  @NotNull
  private static QName getFirstDetailEntryName(@NotNull Element paramElement)
  {
    return new QName(paramElement.getNamespaceURI(), paramElement.getLocalName());
  }
  
  private static Message createSOAP12Fault(SOAPVersion paramSOAPVersion, Throwable paramThrowable, Object paramObject, CheckedExceptionImpl paramCheckedExceptionImpl, QName paramQName)
  {
    SOAPFaultException localSOAPFaultException = null;
    CodeType localCodeType = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    Throwable localThrowable = paramThrowable.getCause();
    if ((paramThrowable instanceof SOAPFaultException)) {
      localSOAPFaultException = (SOAPFaultException)paramThrowable;
    } else if ((localThrowable != null) && ((localThrowable instanceof SOAPFaultException))) {
      localSOAPFaultException = (SOAPFaultException)paramThrowable.getCause();
    }
    if (localSOAPFaultException != null)
    {
      localObject1 = localSOAPFaultException.getFault();
      localObject2 = ((SOAPFault)localObject1).getFaultCodeAsQName();
      if (localObject2 != null)
      {
        paramQName = (QName)localObject2;
        localCodeType = new CodeType(paramQName);
        localObject3 = ((SOAPFault)localObject1).getFaultSubcodes();
        int i = 1;
        localObject4 = null;
        while (((Iterator)localObject3).hasNext())
        {
          QName localQName = (QName)((Iterator)localObject3).next();
          if (i != 0)
          {
            SubcodeType localSubcodeType = new SubcodeType(localQName);
            localCodeType.setSubcode(localSubcodeType);
            localObject4 = localSubcodeType;
            i = 0;
          }
          else
          {
            localObject4 = fillSubcodes((SubcodeType)localObject4, localQName);
          }
        }
      }
      str1 = localSOAPFaultException.getFault().getFaultString();
      str2 = localSOAPFaultException.getFault().getFaultActor();
      str3 = localSOAPFaultException.getFault().getFaultNode();
    }
    if (paramQName == null)
    {
      paramQName = getDefaultFaultCode(paramSOAPVersion);
      localCodeType = new CodeType(paramQName);
    }
    else if (localCodeType == null)
    {
      localCodeType = new CodeType(paramQName);
    }
    if (str1 == null)
    {
      str1 = paramThrowable.getMessage();
      if (str1 == null) {
        str1 = paramThrowable.toString();
      }
    }
    Object localObject1 = new ReasonType(str1);
    Object localObject2 = null;
    Object localObject3 = null;
    if ((paramObject == null) && (localSOAPFaultException != null))
    {
      localObject2 = localSOAPFaultException.getFault().getDetail();
      localObject3 = getFirstDetailEntryName((Detail)localObject2);
    }
    else if (paramObject != null)
    {
      try
      {
        DOMResult localDOMResult = new DOMResult();
        paramCheckedExceptionImpl.getBond().marshal(paramObject, localDOMResult);
        localObject2 = (Element)localDOMResult.getNode().getFirstChild();
        localObject3 = getFirstDetailEntryName((Element)localObject2);
      }
      catch (JAXBException localJAXBException)
      {
        str1 = paramThrowable.getMessage();
      }
    }
    SOAP12Fault localSOAP12Fault = new SOAP12Fault(localCodeType, (ReasonType)localObject1, str3, str2, (Element)localObject2);
    if (paramCheckedExceptionImpl == null) {
      localSOAP12Fault.captureStackTrace(paramThrowable);
    }
    Object localObject4 = JAXBMessage.create(JAXB_CONTEXT, localSOAP12Fault, paramSOAPVersion);
    return new FaultMessage((Message)localObject4, (QName)localObject3);
  }
  
  private static SubcodeType fillSubcodes(SubcodeType paramSubcodeType, QName paramQName)
  {
    SubcodeType localSubcodeType = new SubcodeType(paramQName);
    paramSubcodeType.setSubcode(localSubcodeType);
    return localSubcodeType;
  }
  
  private static QName getDefaultFaultCode(SOAPVersion paramSOAPVersion)
  {
    return paramSOAPVersion.faultCodeServer;
  }
  
  public static SOAPFaultBuilder create(Message paramMessage)
    throws JAXBException
  {
    return (SOAPFaultBuilder)paramMessage.readPayloadAsJAXB(JAXB_CONTEXT.createUnmarshaller());
  }
  
  private static JAXBContext createJAXBContext()
  {
    if (isJDKRuntime())
    {
      Permissions localPermissions = new Permissions();
      localPermissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.fault"));
      localPermissions.add(new ReflectPermission("suppressAccessChecks"));
      (JAXBContext)AccessController.doPrivileged(new PrivilegedAction()new AccessControlContextnew ProtectionDomain
      {
        public JAXBContext run()
        {
          try
          {
            return JAXBContext.newInstance(new Class[] { SOAP11Fault.class, SOAP12Fault.class });
          }
          catch (JAXBException localJAXBException)
          {
            throw new Error(localJAXBException);
          }
        }
      }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, localPermissions) }));
    }
    try
    {
      return JAXBContext.newInstance(new Class[] { SOAP11Fault.class, SOAP12Fault.class });
    }
    catch (JAXBException localJAXBException)
    {
      throw new Error(localJAXBException);
    }
  }
  
  private static boolean isJDKRuntime()
  {
    return SOAPFaultBuilder.class.getName().contains("internal");
  }
  
  static
  {
    boolean bool = false;
    try
    {
      bool = Boolean.getBoolean(CAPTURE_STACK_TRACE_PROPERTY);
    }
    catch (SecurityException localSecurityException) {}
    captureStackTrace = bool;
  }
}
