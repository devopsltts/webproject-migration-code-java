package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class WsdlBindingSubject
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
  private final QName name;
  private final WsdlMessageType messageType;
  private final WsdlNameScope nameScope;
  private final WsdlBindingSubject parent;
  
  WsdlBindingSubject(QName paramQName, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject)
  {
    this(paramQName, WsdlMessageType.NO_MESSAGE, paramWsdlNameScope, paramWsdlBindingSubject);
  }
  
  WsdlBindingSubject(QName paramQName, WsdlMessageType paramWsdlMessageType, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject)
  {
    this.name = paramQName;
    this.messageType = paramWsdlMessageType;
    this.nameScope = paramWsdlNameScope;
    this.parent = paramWsdlBindingSubject;
  }
  
  public static WsdlBindingSubject createBindingSubject(QName paramQName)
  {
    return new WsdlBindingSubject(paramQName, WsdlNameScope.ENDPOINT, null);
  }
  
  public static WsdlBindingSubject createBindingOperationSubject(QName paramQName1, QName paramQName2)
  {
    WsdlBindingSubject localWsdlBindingSubject = createBindingSubject(paramQName1);
    return new WsdlBindingSubject(paramQName2, WsdlNameScope.OPERATION, localWsdlBindingSubject);
  }
  
  public static WsdlBindingSubject createBindingMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3, WsdlMessageType paramWsdlMessageType)
  {
    if (paramWsdlMessageType == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL())));
    }
    if (paramWsdlMessageType == WsdlMessageType.NO_MESSAGE) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE())));
    }
    if ((paramWsdlMessageType == WsdlMessageType.FAULT) && (paramQName3 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME())));
    }
    WsdlBindingSubject localWsdlBindingSubject = createBindingOperationSubject(paramQName1, paramQName2);
    return new WsdlBindingSubject(paramQName3, paramWsdlMessageType, WsdlNameScope.MESSAGE, localWsdlBindingSubject);
  }
  
  public QName getName()
  {
    return this.name;
  }
  
  public WsdlMessageType getMessageType()
  {
    return this.messageType;
  }
  
  public WsdlBindingSubject getParent()
  {
    return this.parent;
  }
  
  public boolean isBindingSubject()
  {
    if (this.nameScope == WsdlNameScope.ENDPOINT) {
      return this.parent == null;
    }
    return false;
  }
  
  public boolean isBindingOperationSubject()
  {
    if ((this.nameScope == WsdlNameScope.OPERATION) && (this.parent != null)) {
      return this.parent.isBindingSubject();
    }
    return false;
  }
  
  public boolean isBindingMessageSubject()
  {
    if ((this.nameScope == WsdlNameScope.MESSAGE) && (this.parent != null)) {
      return this.parent.isBindingOperationSubject();
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof WsdlBindingSubject))) {
      return false;
    }
    WsdlBindingSubject localWsdlBindingSubject = (WsdlBindingSubject)paramObject;
    boolean bool = true;
    bool = (bool) && (this.name == null ? localWsdlBindingSubject.name == null : this.name.equals(localWsdlBindingSubject.name));
    bool = (bool) && (this.messageType.equals(localWsdlBindingSubject.messageType));
    bool = (bool) && (this.nameScope.equals(localWsdlBindingSubject.nameScope));
    bool = (bool) && (this.parent == null ? localWsdlBindingSubject.parent == null : this.parent.equals(localWsdlBindingSubject.parent));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 23;
    i = 31 * i + (this.name == null ? 0 : this.name.hashCode());
    i = 31 * i + this.messageType.hashCode();
    i = 31 * i + this.nameScope.hashCode();
    i = 31 * i + (this.parent == null ? 0 : this.parent.hashCode());
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("WsdlBindingSubject[");
    localStringBuilder.append(this.name).append(", ").append(this.messageType);
    localStringBuilder.append(", ").append(this.nameScope).append(", ").append(this.parent);
    return "]";
  }
  
  public static enum WsdlMessageType
  {
    NO_MESSAGE,  INPUT,  OUTPUT,  FAULT;
    
    private WsdlMessageType() {}
  }
  
  public static enum WsdlNameScope
  {
    SERVICE,  ENDPOINT,  OPERATION,  MESSAGE;
    
    private WsdlNameScope() {}
  }
}
