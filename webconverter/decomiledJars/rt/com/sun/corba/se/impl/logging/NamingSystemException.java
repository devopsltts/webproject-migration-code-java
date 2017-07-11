package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.UNKNOWN;

public class NamingSystemException
  extends LogWrapperBase
{
  private static LogWrapperFactory factory = new LogWrapperFactory()
  {
    public LogWrapperBase create(Logger paramAnonymousLogger)
    {
      return new NamingSystemException(paramAnonymousLogger);
    }
  };
  public static final int TRANSIENT_NAME_SERVER_BAD_PORT = 1398080088;
  public static final int TRANSIENT_NAME_SERVER_BAD_HOST = 1398080089;
  public static final int OBJECT_IS_NULL = 1398080090;
  public static final int INS_BAD_ADDRESS = 1398080091;
  public static final int BIND_UPDATE_CONTEXT_FAILED = 1398080088;
  public static final int BIND_FAILURE = 1398080089;
  public static final int RESOLVE_CONVERSION_FAILURE = 1398080090;
  public static final int RESOLVE_FAILURE = 1398080091;
  public static final int UNBIND_FAILURE = 1398080092;
  public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC_SYS = 1398080138;
  public static final int TRANS_NS_CANNOT_CREATE_INITIAL_NC = 1398080139;
  public static final int NAMING_CTX_REBIND_ALREADY_BOUND = 1398080088;
  public static final int NAMING_CTX_REBINDCTX_ALREADY_BOUND = 1398080089;
  public static final int NAMING_CTX_BAD_BINDINGTYPE = 1398080090;
  public static final int NAMING_CTX_RESOLVE_CANNOT_NARROW_TO_CTX = 1398080091;
  public static final int NAMING_CTX_BINDING_ITERATOR_CREATE = 1398080092;
  public static final int TRANS_NC_BIND_ALREADY_BOUND = 1398080188;
  public static final int TRANS_NC_LIST_GOT_EXC = 1398080189;
  public static final int TRANS_NC_NEWCTX_GOT_EXC = 1398080190;
  public static final int TRANS_NC_DESTROY_GOT_EXC = 1398080191;
  public static final int INS_BAD_SCHEME_NAME = 1398080193;
  public static final int INS_BAD_SCHEME_SPECIFIC_PART = 1398080195;
  public static final int INS_OTHER = 1398080196;
  
  public NamingSystemException(Logger paramLogger)
  {
    super(paramLogger);
  }
  
  public static NamingSystemException get(ORB paramORB, String paramString)
  {
    NamingSystemException localNamingSystemException = (NamingSystemException)paramORB.getLogWrapper(paramString, "NAMING", factory);
    return localNamingSystemException;
  }
  
  public static NamingSystemException get(String paramString)
  {
    NamingSystemException localNamingSystemException = (NamingSystemException)ORB.staticGetLogWrapper(paramString, "NAMING", factory);
    return localNamingSystemException;
  }
  
  public BAD_PARAM transientNameServerBadPort(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    BAD_PARAM localBAD_PARAM = new BAD_PARAM(1398080088, paramCompletionStatus);
    if (paramThrowable != null) {
      localBAD_PARAM.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transientNameServerBadPort", arrayOfObject, NamingSystemException.class, localBAD_PARAM);
    }
    return localBAD_PARAM;
  }
  
  public BAD_PARAM transientNameServerBadPort(CompletionStatus paramCompletionStatus)
  {
    return transientNameServerBadPort(paramCompletionStatus, null);
  }
  
  public BAD_PARAM transientNameServerBadPort(Throwable paramThrowable)
  {
    return transientNameServerBadPort(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public BAD_PARAM transientNameServerBadPort()
  {
    return transientNameServerBadPort(CompletionStatus.COMPLETED_NO, null);
  }
  
  public BAD_PARAM transientNameServerBadHost(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    BAD_PARAM localBAD_PARAM = new BAD_PARAM(1398080089, paramCompletionStatus);
    if (paramThrowable != null) {
      localBAD_PARAM.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transientNameServerBadHost", arrayOfObject, NamingSystemException.class, localBAD_PARAM);
    }
    return localBAD_PARAM;
  }
  
  public BAD_PARAM transientNameServerBadHost(CompletionStatus paramCompletionStatus)
  {
    return transientNameServerBadHost(paramCompletionStatus, null);
  }
  
  public BAD_PARAM transientNameServerBadHost(Throwable paramThrowable)
  {
    return transientNameServerBadHost(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public BAD_PARAM transientNameServerBadHost()
  {
    return transientNameServerBadHost(CompletionStatus.COMPLETED_NO, null);
  }
  
  public BAD_PARAM objectIsNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    BAD_PARAM localBAD_PARAM = new BAD_PARAM(1398080090, paramCompletionStatus);
    if (paramThrowable != null) {
      localBAD_PARAM.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.objectIsNull", arrayOfObject, NamingSystemException.class, localBAD_PARAM);
    }
    return localBAD_PARAM;
  }
  
  public BAD_PARAM objectIsNull(CompletionStatus paramCompletionStatus)
  {
    return objectIsNull(paramCompletionStatus, null);
  }
  
  public BAD_PARAM objectIsNull(Throwable paramThrowable)
  {
    return objectIsNull(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public BAD_PARAM objectIsNull()
  {
    return objectIsNull(CompletionStatus.COMPLETED_NO, null);
  }
  
  public BAD_PARAM insBadAddress(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    BAD_PARAM localBAD_PARAM = new BAD_PARAM(1398080091, paramCompletionStatus);
    if (paramThrowable != null) {
      localBAD_PARAM.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.insBadAddress", arrayOfObject, NamingSystemException.class, localBAD_PARAM);
    }
    return localBAD_PARAM;
  }
  
  public BAD_PARAM insBadAddress(CompletionStatus paramCompletionStatus)
  {
    return insBadAddress(paramCompletionStatus, null);
  }
  
  public BAD_PARAM insBadAddress(Throwable paramThrowable)
  {
    return insBadAddress(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public BAD_PARAM insBadAddress()
  {
    return insBadAddress(CompletionStatus.COMPLETED_NO, null);
  }
  
  public UNKNOWN bindUpdateContextFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    UNKNOWN localUNKNOWN = new UNKNOWN(1398080088, paramCompletionStatus);
    if (paramThrowable != null) {
      localUNKNOWN.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.bindUpdateContextFailed", arrayOfObject, NamingSystemException.class, localUNKNOWN);
    }
    return localUNKNOWN;
  }
  
  public UNKNOWN bindUpdateContextFailed(CompletionStatus paramCompletionStatus)
  {
    return bindUpdateContextFailed(paramCompletionStatus, null);
  }
  
  public UNKNOWN bindUpdateContextFailed(Throwable paramThrowable)
  {
    return bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public UNKNOWN bindUpdateContextFailed()
  {
    return bindUpdateContextFailed(CompletionStatus.COMPLETED_NO, null);
  }
  
  public UNKNOWN bindFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    UNKNOWN localUNKNOWN = new UNKNOWN(1398080089, paramCompletionStatus);
    if (paramThrowable != null) {
      localUNKNOWN.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.bindFailure", arrayOfObject, NamingSystemException.class, localUNKNOWN);
    }
    return localUNKNOWN;
  }
  
  public UNKNOWN bindFailure(CompletionStatus paramCompletionStatus)
  {
    return bindFailure(paramCompletionStatus, null);
  }
  
  public UNKNOWN bindFailure(Throwable paramThrowable)
  {
    return bindFailure(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public UNKNOWN bindFailure()
  {
    return bindFailure(CompletionStatus.COMPLETED_NO, null);
  }
  
  public UNKNOWN resolveConversionFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    UNKNOWN localUNKNOWN = new UNKNOWN(1398080090, paramCompletionStatus);
    if (paramThrowable != null) {
      localUNKNOWN.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.resolveConversionFailure", arrayOfObject, NamingSystemException.class, localUNKNOWN);
    }
    return localUNKNOWN;
  }
  
  public UNKNOWN resolveConversionFailure(CompletionStatus paramCompletionStatus)
  {
    return resolveConversionFailure(paramCompletionStatus, null);
  }
  
  public UNKNOWN resolveConversionFailure(Throwable paramThrowable)
  {
    return resolveConversionFailure(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public UNKNOWN resolveConversionFailure()
  {
    return resolveConversionFailure(CompletionStatus.COMPLETED_NO, null);
  }
  
  public UNKNOWN resolveFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    UNKNOWN localUNKNOWN = new UNKNOWN(1398080091, paramCompletionStatus);
    if (paramThrowable != null) {
      localUNKNOWN.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.resolveFailure", arrayOfObject, NamingSystemException.class, localUNKNOWN);
    }
    return localUNKNOWN;
  }
  
  public UNKNOWN resolveFailure(CompletionStatus paramCompletionStatus)
  {
    return resolveFailure(paramCompletionStatus, null);
  }
  
  public UNKNOWN resolveFailure(Throwable paramThrowable)
  {
    return resolveFailure(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public UNKNOWN resolveFailure()
  {
    return resolveFailure(CompletionStatus.COMPLETED_NO, null);
  }
  
  public UNKNOWN unbindFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    UNKNOWN localUNKNOWN = new UNKNOWN(1398080092, paramCompletionStatus);
    if (paramThrowable != null) {
      localUNKNOWN.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.unbindFailure", arrayOfObject, NamingSystemException.class, localUNKNOWN);
    }
    return localUNKNOWN;
  }
  
  public UNKNOWN unbindFailure(CompletionStatus paramCompletionStatus)
  {
    return unbindFailure(paramCompletionStatus, null);
  }
  
  public UNKNOWN unbindFailure(Throwable paramThrowable)
  {
    return unbindFailure(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public UNKNOWN unbindFailure()
  {
    return unbindFailure(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INITIALIZE transNsCannotCreateInitialNcSys(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INITIALIZE localINITIALIZE = new INITIALIZE(1398080138, paramCompletionStatus);
    if (paramThrowable != null) {
      localINITIALIZE.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNcSys", arrayOfObject, NamingSystemException.class, localINITIALIZE);
    }
    return localINITIALIZE;
  }
  
  public INITIALIZE transNsCannotCreateInitialNcSys(CompletionStatus paramCompletionStatus)
  {
    return transNsCannotCreateInitialNcSys(paramCompletionStatus, null);
  }
  
  public INITIALIZE transNsCannotCreateInitialNcSys(Throwable paramThrowable)
  {
    return transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INITIALIZE transNsCannotCreateInitialNcSys()
  {
    return transNsCannotCreateInitialNcSys(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INITIALIZE transNsCannotCreateInitialNc(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INITIALIZE localINITIALIZE = new INITIALIZE(1398080139, paramCompletionStatus);
    if (paramThrowable != null) {
      localINITIALIZE.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNsCannotCreateInitialNc", arrayOfObject, NamingSystemException.class, localINITIALIZE);
    }
    return localINITIALIZE;
  }
  
  public INITIALIZE transNsCannotCreateInitialNc(CompletionStatus paramCompletionStatus)
  {
    return transNsCannotCreateInitialNc(paramCompletionStatus, null);
  }
  
  public INITIALIZE transNsCannotCreateInitialNc(Throwable paramThrowable)
  {
    return transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INITIALIZE transNsCannotCreateInitialNc()
  {
    return transNsCannotCreateInitialNc(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL namingCtxRebindAlreadyBound(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080088, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.namingCtxRebindAlreadyBound", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL namingCtxRebindAlreadyBound(CompletionStatus paramCompletionStatus)
  {
    return namingCtxRebindAlreadyBound(paramCompletionStatus, null);
  }
  
  public INTERNAL namingCtxRebindAlreadyBound(Throwable paramThrowable)
  {
    return namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL namingCtxRebindAlreadyBound()
  {
    return namingCtxRebindAlreadyBound(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL namingCtxRebindctxAlreadyBound(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080089, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.namingCtxRebindctxAlreadyBound", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL namingCtxRebindctxAlreadyBound(CompletionStatus paramCompletionStatus)
  {
    return namingCtxRebindctxAlreadyBound(paramCompletionStatus, null);
  }
  
  public INTERNAL namingCtxRebindctxAlreadyBound(Throwable paramThrowable)
  {
    return namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL namingCtxRebindctxAlreadyBound()
  {
    return namingCtxRebindctxAlreadyBound(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL namingCtxBadBindingtype(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080090, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.namingCtxBadBindingtype", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL namingCtxBadBindingtype(CompletionStatus paramCompletionStatus)
  {
    return namingCtxBadBindingtype(paramCompletionStatus, null);
  }
  
  public INTERNAL namingCtxBadBindingtype(Throwable paramThrowable)
  {
    return namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL namingCtxBadBindingtype()
  {
    return namingCtxBadBindingtype(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL namingCtxResolveCannotNarrowToCtx(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080091, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.namingCtxResolveCannotNarrowToCtx", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL namingCtxResolveCannotNarrowToCtx(CompletionStatus paramCompletionStatus)
  {
    return namingCtxResolveCannotNarrowToCtx(paramCompletionStatus, null);
  }
  
  public INTERNAL namingCtxResolveCannotNarrowToCtx(Throwable paramThrowable)
  {
    return namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL namingCtxResolveCannotNarrowToCtx()
  {
    return namingCtxResolveCannotNarrowToCtx(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL namingCtxBindingIteratorCreate(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080092, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.namingCtxBindingIteratorCreate", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL namingCtxBindingIteratorCreate(CompletionStatus paramCompletionStatus)
  {
    return namingCtxBindingIteratorCreate(paramCompletionStatus, null);
  }
  
  public INTERNAL namingCtxBindingIteratorCreate(Throwable paramThrowable)
  {
    return namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL namingCtxBindingIteratorCreate()
  {
    return namingCtxBindingIteratorCreate(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL transNcBindAlreadyBound(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080188, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNcBindAlreadyBound", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL transNcBindAlreadyBound(CompletionStatus paramCompletionStatus)
  {
    return transNcBindAlreadyBound(paramCompletionStatus, null);
  }
  
  public INTERNAL transNcBindAlreadyBound(Throwable paramThrowable)
  {
    return transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL transNcBindAlreadyBound()
  {
    return transNcBindAlreadyBound(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL transNcListGotExc(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080189, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNcListGotExc", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL transNcListGotExc(CompletionStatus paramCompletionStatus)
  {
    return transNcListGotExc(paramCompletionStatus, null);
  }
  
  public INTERNAL transNcListGotExc(Throwable paramThrowable)
  {
    return transNcListGotExc(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL transNcListGotExc()
  {
    return transNcListGotExc(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL transNcNewctxGotExc(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080190, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNcNewctxGotExc", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL transNcNewctxGotExc(CompletionStatus paramCompletionStatus)
  {
    return transNcNewctxGotExc(paramCompletionStatus, null);
  }
  
  public INTERNAL transNcNewctxGotExc(Throwable paramThrowable)
  {
    return transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL transNcNewctxGotExc()
  {
    return transNcNewctxGotExc(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL transNcDestroyGotExc(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080191, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.transNcDestroyGotExc", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL transNcDestroyGotExc(CompletionStatus paramCompletionStatus)
  {
    return transNcDestroyGotExc(paramCompletionStatus, null);
  }
  
  public INTERNAL transNcDestroyGotExc(Throwable paramThrowable)
  {
    return transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL transNcDestroyGotExc()
  {
    return transNcDestroyGotExc(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL insBadSchemeName(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080193, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.insBadSchemeName", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL insBadSchemeName(CompletionStatus paramCompletionStatus)
  {
    return insBadSchemeName(paramCompletionStatus, null);
  }
  
  public INTERNAL insBadSchemeName(Throwable paramThrowable)
  {
    return insBadSchemeName(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL insBadSchemeName()
  {
    return insBadSchemeName(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL insBadSchemeSpecificPart(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080195, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.insBadSchemeSpecificPart", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL insBadSchemeSpecificPart(CompletionStatus paramCompletionStatus)
  {
    return insBadSchemeSpecificPart(paramCompletionStatus, null);
  }
  
  public INTERNAL insBadSchemeSpecificPart(Throwable paramThrowable)
  {
    return insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL insBadSchemeSpecificPart()
  {
    return insBadSchemeSpecificPart(CompletionStatus.COMPLETED_NO, null);
  }
  
  public INTERNAL insOther(CompletionStatus paramCompletionStatus, Throwable paramThrowable)
  {
    INTERNAL localINTERNAL = new INTERNAL(1398080196, paramCompletionStatus);
    if (paramThrowable != null) {
      localINTERNAL.initCause(paramThrowable);
    }
    if (this.logger.isLoggable(Level.WARNING))
    {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "NAMING.insOther", arrayOfObject, NamingSystemException.class, localINTERNAL);
    }
    return localINTERNAL;
  }
  
  public INTERNAL insOther(CompletionStatus paramCompletionStatus)
  {
    return insOther(paramCompletionStatus, null);
  }
  
  public INTERNAL insOther(Throwable paramThrowable)
  {
    return insOther(CompletionStatus.COMPLETED_NO, paramThrowable);
  }
  
  public INTERNAL insOther()
  {
    return insOther(CompletionStatus.COMPLETED_NO, null);
  }
}
