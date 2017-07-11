package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.ServerRequestImpl;
import com.sun.corba.se.impl.encoding.CodeSetComponentInfo.CodeSetContext;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.oa.NullServant;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.servicecontext.CodeSetServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;

public class CorbaServerRequestDispatcherImpl
  implements CorbaServerRequestDispatcher
{
  protected ORB orb;
  private ORBUtilSystemException wrapper;
  private POASystemException poaWrapper;
  
  public CorbaServerRequestDispatcherImpl(ORB paramORB)
  {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.poaWrapper = POASystemException.get(paramORB, "rpc.protocol");
  }
  
  public IOR locate(ObjectKey paramObjectKey)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".locate->");
      }
      ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
      try
      {
        checkServerId(paramObjectKey);
      }
      catch (ForwardException localForwardException)
      {
        IOR localIOR2 = localForwardException.getIOR();
        return localIOR2;
      }
      findObjectAdapter(localObjectKeyTemplate);
      IOR localIOR1 = null;
      return localIOR1;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".locate<-");
      }
    }
  }
  
  public void dispatch(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".dispatch->: " + opAndId(localCorbaMessageMediator));
      }
      consumeServiceContexts(localCorbaMessageMediator);
      ((MarshalInputStream)localCorbaMessageMediator.getInputObject()).performORBVersionSpecificInit();
      ObjectKey localObjectKey = localCorbaMessageMediator.getObjectKey();
      try
      {
        checkServerId(localObjectKey);
      }
      catch (ForwardException localForwardException1)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": bad server id");
        }
        localCorbaMessageMediator.getProtocolHandler().createLocationForward(localCorbaMessageMediator, localForwardException1.getIOR(), null);
        return;
      }
      String str = localCorbaMessageMediator.getOperationName();
      ObjectAdapter localObjectAdapter = null;
      try
      {
        byte[] arrayOfByte = localObjectKey.getId().getId();
        localObject1 = localObjectKey.getTemplate();
        localObjectAdapter = findObjectAdapter((ObjectKeyTemplate)localObject1);
        localObject2 = getServantWithPI(localCorbaMessageMediator, localObjectAdapter, arrayOfByte, (ObjectKeyTemplate)localObject1, str);
        dispatchToServant(localObject2, localCorbaMessageMediator, arrayOfByte, localObjectAdapter);
      }
      catch (ForwardException localForwardException2)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": ForwardException caught");
        }
        localCorbaMessageMediator.getProtocolHandler().createLocationForward(localCorbaMessageMediator, localForwardException2.getIOR(), null);
      }
      catch (OADestroyed localOADestroyed)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": OADestroyed exception caught");
        }
        dispatch(localCorbaMessageMediator);
      }
      catch (RequestCanceledException localRequestCanceledException)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": RequestCanceledException caught");
        }
        throw localRequestCanceledException;
      }
      catch (UnknownException localUnknownException)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": UnknownException caught " + localUnknownException);
        }
        if ((localUnknownException.originalEx instanceof RequestCanceledException)) {
          throw ((RequestCanceledException)localUnknownException.originalEx);
        }
        Object localObject1 = new ServiceContexts(this.orb);
        Object localObject2 = new UEInfoServiceContext(localUnknownException.originalEx);
        ((ServiceContexts)localObject1).put((ServiceContext)localObject2);
        UNKNOWN localUNKNOWN = this.wrapper.unknownExceptionInDispatch(CompletionStatus.COMPLETED_MAYBE, localUnknownException);
        localCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(localCorbaMessageMediator, localUNKNOWN, (ServiceContexts)localObject1);
      }
      catch (Throwable localThrowable)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatch: " + opAndId(localCorbaMessageMediator) + ": other exception " + localThrowable);
        }
        localCorbaMessageMediator.getProtocolHandler().handleThrowableDuringServerDispatch(localCorbaMessageMediator, localThrowable, CompletionStatus.COMPLETED_MAYBE);
      }
      return;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".dispatch<-: " + opAndId(localCorbaMessageMediator));
      }
    }
  }
  
  private void releaseServant(ObjectAdapter paramObjectAdapter)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".releaseServant->");
      }
      if (paramObjectAdapter == null)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".releaseServant: null object adapter");
        }
        return;
      }
      try
      {
        paramObjectAdapter.returnServant();
        paramObjectAdapter.exit();
        this.orb.popInvocationInfo();
      }
      finally
      {
        paramObjectAdapter.exit();
        this.orb.popInvocationInfo();
      }
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".releaseServant<-");
      }
    }
  }
  
  private Object getServant(ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, String paramString)
    throws OADestroyed
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".getServant->");
      }
      OAInvocationInfo localOAInvocationInfo = paramObjectAdapter.makeInvocationInfo(paramArrayOfByte);
      localOAInvocationInfo.setOperation(paramString);
      this.orb.pushInvocationInfo(localOAInvocationInfo);
      paramObjectAdapter.getInvocationServant(localOAInvocationInfo);
      Object localObject1 = localOAInvocationInfo.getServantContainer();
      return localObject1;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".getServant<-");
      }
    }
  }
  
  protected Object getServantWithPI(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate, String paramString)
    throws OADestroyed
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".getServantWithPI->");
      }
      this.orb.getPIHandler().initializeServerPIInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
      this.orb.getPIHandler().invokeServerPIStartingPoint();
      paramObjectAdapter.enter();
      if (paramCorbaMessageMediator != null) {
        paramCorbaMessageMediator.setExecuteReturnServantInResponseConstructor(true);
      }
      Object localObject1 = getServant(paramObjectAdapter, paramArrayOfByte, paramString);
      String str = "unknown";
      if ((localObject1 instanceof NullServant)) {
        handleNullServant(paramString, (NullServant)localObject1);
      } else {
        str = paramObjectAdapter.getInterfaces(localObject1, paramArrayOfByte)[0];
      }
      this.orb.getPIHandler().setServerPIInfo(localObject1, str);
      if (((localObject1 != null) && (!(localObject1 instanceof org.omg.CORBA.DynamicImplementation)) && (!(localObject1 instanceof org.omg.PortableServer.DynamicImplementation))) || (SpecialMethod.getSpecialMethod(paramString) != null)) {
        this.orb.getPIHandler().invokeServerPIIntermediatePoint();
      }
      Object localObject2 = localObject1;
      return localObject2;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".getServantWithPI<-");
      }
    }
  }
  
  protected void checkServerId(ObjectKey paramObjectKey)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".checkServerId->");
      }
      ObjectKeyTemplate localObjectKeyTemplate = paramObjectKey.getTemplate();
      int i = localObjectKeyTemplate.getServerId();
      int j = localObjectKeyTemplate.getSubcontractId();
      if (!this.orb.isLocalServerId(j, i))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".checkServerId: bad server id");
        }
        this.orb.handleBadServerId(paramObjectKey);
      }
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".checkServerId<-");
      }
    }
  }
  
  private ObjectAdapter findObjectAdapter(ObjectKeyTemplate paramObjectKeyTemplate)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".findObjectAdapter->");
      }
      RequestDispatcherRegistry localRequestDispatcherRegistry = this.orb.getRequestDispatcherRegistry();
      int i = paramObjectKeyTemplate.getSubcontractId();
      ObjectAdapterFactory localObjectAdapterFactory = localRequestDispatcherRegistry.getObjectAdapterFactory(i);
      if (localObjectAdapterFactory == null) {
        throw this.wrapper.noObjectAdapterFactory();
      }
      ObjectAdapterId localObjectAdapterId = paramObjectKeyTemplate.getObjectAdapterId();
      ObjectAdapter localObjectAdapter1 = localObjectAdapterFactory.find(localObjectAdapterId);
      if (localObjectAdapter1 == null) {
        throw this.wrapper.badAdapterId();
      }
      ObjectAdapter localObjectAdapter2 = localObjectAdapter1;
      return localObjectAdapter2;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".findObjectAdapter<-");
      }
    }
  }
  
  protected void handleNullServant(String paramString, NullServant paramNullServant)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".handleNullServant->: " + paramString);
      }
      SpecialMethod localSpecialMethod = SpecialMethod.getSpecialMethod(paramString);
      if ((localSpecialMethod == null) || (!localSpecialMethod.isNonExistentMethod()))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".handleNullServant: " + paramString + ": throwing OBJECT_NOT_EXIST");
        }
        throw paramNullServant.getException();
      }
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".handleNullServant<-: " + paramString);
      }
    }
  }
  
  protected void consumeServiceContexts(CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".consumeServiceContexts->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = paramCorbaMessageMediator.getRequestServiceContexts();
      GIOPVersion localGIOPVersion = paramCorbaMessageMediator.getGIOPVersion();
      boolean bool = processCodeSetContext(paramCorbaMessageMediator, localServiceContexts);
      if (this.orb.subcontractDebugFlag)
      {
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": GIOP version: " + localGIOPVersion);
        dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": as code set context? " + bool);
      }
      ServiceContext localServiceContext = localServiceContexts.get(6);
      Object localObject1;
      if (localServiceContext != null)
      {
        SendingContextServiceContext localSendingContextServiceContext = (SendingContextServiceContext)localServiceContext;
        localObject1 = localSendingContextServiceContext.getIOR();
        try
        {
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeBaseIOR((IOR)localObject1);
        }
        catch (ThreadDeath localThreadDeath)
        {
          throw localThreadDeath;
        }
        catch (Throwable localThrowable)
        {
          throw this.wrapper.badStringifiedIor(localThrowable);
        }
      }
      int i = 0;
      if ((localGIOPVersion.equals(GIOPVersion.V1_0)) && (bool))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".consumeServiceCOntexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be an old Sun ORB");
        }
        this.orb.setORBVersion(ORBVersionFactory.getOLD());
      }
      else
      {
        i = 1;
      }
      localServiceContext = localServiceContexts.get(1313165056);
      if (localServiceContext != null)
      {
        localObject1 = (ORBVersionServiceContext)localServiceContext;
        ORBVersion localORBVersion = ((ORBVersionServiceContext)localObject1).getVersion();
        this.orb.setORBVersion(localORBVersion);
        i = 0;
      }
      if (i != 0)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".consumeServiceContexts: " + opAndId(paramCorbaMessageMediator) + ": Determined to be a foreign ORB");
        }
        this.orb.setORBVersion(ORBVersionFactory.getFOREIGN());
      }
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".consumeServiceContexts<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator dispatchToServant(Object paramObject, CorbaMessageMediator paramCorbaMessageMediator, byte[] paramArrayOfByte, ObjectAdapter paramObjectAdapter)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".dispatchToServant->: " + opAndId(paramCorbaMessageMediator));
      }
      CorbaMessageMediator localCorbaMessageMediator = null;
      String str = paramCorbaMessageMediator.getOperationName();
      SpecialMethod localSpecialMethod = SpecialMethod.getSpecialMethod(str);
      if (localSpecialMethod != null)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling special method");
        }
        localCorbaMessageMediator = localSpecialMethod.invoke(paramObject, paramCorbaMessageMediator, paramArrayOfByte, paramObjectAdapter);
        localObject1 = localCorbaMessageMediator;
        return localObject1;
      }
      Object localObject2;
      if ((paramObject instanceof org.omg.CORBA.DynamicImplementation))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling old style DSI type servant");
        }
        localObject1 = (org.omg.CORBA.DynamicImplementation)paramObject;
        localObject2 = new ServerRequestImpl(paramCorbaMessageMediator, this.orb);
        ((org.omg.CORBA.DynamicImplementation)localObject1).invoke((ServerRequest)localObject2);
        localCorbaMessageMediator = handleDynamicResult((ServerRequestImpl)localObject2, paramCorbaMessageMediator);
      }
      else if ((paramObject instanceof org.omg.PortableServer.DynamicImplementation))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling POA DSI type servant");
        }
        localObject1 = (org.omg.PortableServer.DynamicImplementation)paramObject;
        localObject2 = new ServerRequestImpl(paramCorbaMessageMediator, this.orb);
        ((org.omg.PortableServer.DynamicImplementation)localObject1).invoke((ServerRequest)localObject2);
        localCorbaMessageMediator = handleDynamicResult((ServerRequestImpl)localObject2, paramCorbaMessageMediator);
      }
      else
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".dispatchToServant: " + opAndId(paramCorbaMessageMediator) + ": Handling invoke handler type servant");
        }
        localObject1 = (InvokeHandler)paramObject;
        localObject2 = ((InvokeHandler)localObject1)._invoke(str, (InputStream)paramCorbaMessageMediator.getInputObject(), paramCorbaMessageMediator);
        localCorbaMessageMediator = (CorbaMessageMediator)((OutputObject)localObject2).getMessageMediator();
      }
      Object localObject1 = localCorbaMessageMediator;
      return localObject1;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".dispatchToServant<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator handleDynamicResult(ServerRequestImpl paramServerRequestImpl, CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".handleDynamicResult->: " + opAndId(paramCorbaMessageMediator));
      }
      CorbaMessageMediator localCorbaMessageMediator = null;
      Any localAny = paramServerRequestImpl.checkResultCalled();
      if (localAny == null)
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling normal result");
        }
        localCorbaMessageMediator = sendingReply(paramCorbaMessageMediator);
        localObject1 = (OutputStream)localCorbaMessageMediator.getOutputObject();
        paramServerRequestImpl.marshalReplyParams((OutputStream)localObject1);
      }
      else
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".handleDynamicResult: " + opAndId(paramCorbaMessageMediator) + ": handling error");
        }
        localCorbaMessageMediator = sendingReply(paramCorbaMessageMediator, localAny);
      }
      Object localObject1 = localCorbaMessageMediator;
      return localObject1;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".handleDynamicResult<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".sendingReply->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = new ServiceContexts(this.orb);
      CorbaMessageMediator localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createResponse(paramCorbaMessageMediator, localServiceContexts);
      return localCorbaMessageMediator;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".sendingReply<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected CorbaMessageMediator sendingReply(CorbaMessageMediator paramCorbaMessageMediator, Any paramAny)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".sendingReply/Any->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContexts localServiceContexts = new ServiceContexts(this.orb);
      String str = null;
      try
      {
        str = paramAny.type().id();
      }
      catch (BadKind localBadKind)
      {
        throw this.wrapper.problemWithExceptionTypecode(localBadKind);
      }
      CorbaMessageMediator localCorbaMessageMediator;
      if (ORBUtility.isSystemException(str))
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling system exception");
        }
        localObject1 = paramAny.create_input_stream();
        SystemException localSystemException = ORBUtility.readSystemException((InputStream)localObject1);
        localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createSystemExceptionResponse(paramCorbaMessageMediator, localSystemException, localServiceContexts);
      }
      else
      {
        if (this.orb.subcontractDebugFlag) {
          dprint(".sendingReply/Any: " + opAndId(paramCorbaMessageMediator) + ": handling user exception");
        }
        localCorbaMessageMediator = paramCorbaMessageMediator.getProtocolHandler().createUserExceptionResponse(paramCorbaMessageMediator, localServiceContexts);
        localObject1 = (OutputStream)localCorbaMessageMediator.getOutputObject();
        paramAny.write_value((OutputStream)localObject1);
      }
      Object localObject1 = localCorbaMessageMediator;
      return localObject1;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".sendingReply/Any<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected boolean processCodeSetContext(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    try
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".processCodeSetContext->: " + opAndId(paramCorbaMessageMediator));
      }
      ServiceContext localServiceContext = paramServiceContexts.get(1);
      if (localServiceContext != null)
      {
        boolean bool1;
        if (paramCorbaMessageMediator.getConnection() == null)
        {
          bool1 = true;
          return bool1;
        }
        if (paramCorbaMessageMediator.getGIOPVersion().equals(GIOPVersion.V1_0))
        {
          bool1 = true;
          return bool1;
        }
        CodeSetServiceContext localCodeSetServiceContext = (CodeSetServiceContext)localServiceContext;
        CodeSetComponentInfo.CodeSetContext localCodeSetContext = localCodeSetServiceContext.getCodeSetContext();
        if (((CorbaConnection)paramCorbaMessageMediator.getConnection()).getCodeSetContext() == null)
        {
          if (this.orb.subcontractDebugFlag) {
            dprint(".processCodeSetContext: " + opAndId(paramCorbaMessageMediator) + ": Setting code sets to: " + localCodeSetContext);
          }
          ((CorbaConnection)paramCorbaMessageMediator.getConnection()).setCodeSetContext(localCodeSetContext);
          if (localCodeSetContext.getCharCodeSet() != OSFCodeSetRegistry.ISO_8859_1.getNumber()) {
            ((MarshalInputStream)paramCorbaMessageMediator.getInputObject()).resetCodeSetConverters();
          }
        }
      }
      boolean bool2 = localServiceContext != null;
      return bool2;
    }
    finally
    {
      if (this.orb.subcontractDebugFlag) {
        dprint(".processCodeSetContext<-: " + opAndId(paramCorbaMessageMediator));
      }
    }
  }
  
  protected void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaServerRequestDispatcherImpl", paramString);
  }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
  }
}
