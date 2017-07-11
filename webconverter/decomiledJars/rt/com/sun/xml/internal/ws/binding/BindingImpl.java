package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.developer.BindingTypeFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

public abstract class BindingImpl
  implements WSBinding
{
  protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
  private HandlerConfiguration handlerConfig;
  private final Set<QName> addedHeaders = new HashSet();
  private final Set<QName> knownHeaders = new HashSet();
  private final Set<QName> unmodKnownHeaders = Collections.unmodifiableSet(this.knownHeaders);
  private final BindingID bindingId;
  protected final WebServiceFeatureList features;
  protected final Map<QName, WebServiceFeatureList> operationFeatures = new HashMap();
  protected final Map<QName, WebServiceFeatureList> inputMessageFeatures = new HashMap();
  protected final Map<QName, WebServiceFeatureList> outputMessageFeatures = new HashMap();
  protected final Map<MessageKey, WebServiceFeatureList> faultMessageFeatures = new HashMap();
  protected Service.Mode serviceMode = Service.Mode.PAYLOAD;
  protected MessageContextFactory messageContextFactory;
  
  protected BindingImpl(BindingID paramBindingID, WebServiceFeature... paramVarArgs)
  {
    this.bindingId = paramBindingID;
    this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
    if (this.handlerConfig.getHandlerKnownHeaders() != null) {
      this.knownHeaders.addAll(this.handlerConfig.getHandlerKnownHeaders());
    }
    this.features = new WebServiceFeatureList(paramVarArgs);
    this.features.validate();
  }
  
  @NotNull
  public List<Handler> getHandlerChain()
  {
    return this.handlerConfig.getHandlerChain();
  }
  
  public HandlerConfiguration getHandlerConfig()
  {
    return this.handlerConfig;
  }
  
  protected void setHandlerConfig(HandlerConfiguration paramHandlerConfiguration)
  {
    this.handlerConfig = paramHandlerConfiguration;
    this.knownHeaders.clear();
    this.knownHeaders.addAll(this.addedHeaders);
    if ((paramHandlerConfiguration != null) && (paramHandlerConfiguration.getHandlerKnownHeaders() != null)) {
      this.knownHeaders.addAll(paramHandlerConfiguration.getHandlerKnownHeaders());
    }
  }
  
  public void setMode(@NotNull Service.Mode paramMode)
  {
    this.serviceMode = paramMode;
  }
  
  public Set<QName> getKnownHeaders()
  {
    return this.unmodKnownHeaders;
  }
  
  public boolean addKnownHeader(QName paramQName)
  {
    this.addedHeaders.add(paramQName);
    return this.knownHeaders.add(paramQName);
  }
  
  @NotNull
  public BindingID getBindingId()
  {
    return this.bindingId;
  }
  
  public final SOAPVersion getSOAPVersion()
  {
    return this.bindingId.getSOAPVersion();
  }
  
  public AddressingVersion getAddressingVersion()
  {
    AddressingVersion localAddressingVersion;
    if (this.features.isEnabled(AddressingFeature.class)) {
      localAddressingVersion = AddressingVersion.W3C;
    } else if (this.features.isEnabled(MemberSubmissionAddressingFeature.class)) {
      localAddressingVersion = AddressingVersion.MEMBER;
    } else {
      localAddressingVersion = null;
    }
    return localAddressingVersion;
  }
  
  @NotNull
  public final Codec createCodec()
  {
    initializeJavaActivationHandlers();
    return this.bindingId.createEncoder(this);
  }
  
  public static void initializeJavaActivationHandlers()
  {
    try
    {
      CommandMap localCommandMap = CommandMap.getDefaultCommandMap();
      if ((localCommandMap instanceof MailcapCommandMap))
      {
        MailcapCommandMap localMailcapCommandMap = (MailcapCommandMap)localCommandMap;
        if (!cmdMapInitialized(localMailcapCommandMap))
        {
          localMailcapCommandMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          localMailcapCommandMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.ws.encoding.ImageDataContentHandler");
          localMailcapCommandMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.ws.encoding.StringDataContentHandler");
        }
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  private static boolean cmdMapInitialized(MailcapCommandMap paramMailcapCommandMap)
  {
    CommandInfo[] arrayOfCommandInfo1 = paramMailcapCommandMap.getAllCommands("text/xml");
    if ((arrayOfCommandInfo1 == null) || (arrayOfCommandInfo1.length == 0)) {
      return false;
    }
    String str1 = "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler";
    String str2 = "com.sun.xml.internal.ws.encoding.XmlDataContentHandler";
    for (CommandInfo localCommandInfo : arrayOfCommandInfo1)
    {
      String str3 = localCommandInfo.getCommandClass();
      if ((str1.equals(str3)) || (str2.equals(str3))) {
        return true;
      }
    }
    return false;
  }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID)
  {
    if (paramBindingID.equals(BindingID.XML_HTTP)) {
      return new HTTPBindingImpl();
    }
    return new SOAPBindingImpl(paramBindingID);
  }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID, WebServiceFeature[] paramArrayOfWebServiceFeature)
  {
    for (WebServiceFeature localWebServiceFeature : paramArrayOfWebServiceFeature) {
      if ((localWebServiceFeature instanceof BindingTypeFeature))
      {
        BindingTypeFeature localBindingTypeFeature = (BindingTypeFeature)localWebServiceFeature;
        paramBindingID = BindingID.parse(localBindingTypeFeature.getBindingId());
      }
    }
    if (paramBindingID.equals(BindingID.XML_HTTP)) {
      return new HTTPBindingImpl(paramArrayOfWebServiceFeature);
    }
    return new SOAPBindingImpl(paramBindingID, paramArrayOfWebServiceFeature);
  }
  
  public static WSBinding getDefaultBinding()
  {
    return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
  }
  
  public String getBindingID()
  {
    return this.bindingId.toString();
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass)
  {
    return this.features.get(paramClass);
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> paramClass, @NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return FeatureListUtil.mergeFeature(paramClass, localWebServiceFeatureList, this.features);
  }
  
  public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass)
  {
    return this.features.isEnabled(paramClass);
  }
  
  public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return FeatureListUtil.isFeatureEnabled(paramClass, localWebServiceFeatureList, this.features);
  }
  
  @NotNull
  public WebServiceFeatureList getFeatures()
  {
    if (!isFeatureEnabled(EnvelopeStyleFeature.class))
    {
      WebServiceFeature[] arrayOfWebServiceFeature = { getSOAPVersion().toFeature() };
      this.features.mergeFeatures(arrayOfWebServiceFeature, false);
    }
    return this.features;
  }
  
  @NotNull
  public WebServiceFeatureList getOperationFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getInputMessageFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)this.inputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getOutputMessageFeatures(@NotNull QName paramQName)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)this.outputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2)
  {
    WebServiceFeatureList localWebServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName1);
    WebServiceFeatureList localWebServiceFeatureList2 = (WebServiceFeatureList)this.faultMessageFeatures.get(new MessageKey(paramQName1, paramQName2));
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { localWebServiceFeatureList1, localWebServiceFeatureList2, this.features });
  }
  
  public void setOperationFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      this.operationFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setInputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.inputMessageFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      this.inputMessageFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setOutputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.outputMessageFeatures.get(paramQName);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      this.outputMessageFeatures.put(paramQName, localWebServiceFeatureList);
    }
  }
  
  public void setFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2, WebServiceFeature... paramVarArgs)
  {
    if (paramVarArgs != null)
    {
      MessageKey localMessageKey = new MessageKey(paramQName1, paramQName2);
      WebServiceFeatureList localWebServiceFeatureList = (WebServiceFeatureList)this.faultMessageFeatures.get(localMessageKey);
      if (localWebServiceFeatureList == null) {
        localWebServiceFeatureList = new WebServiceFeatureList();
      }
      for (WebServiceFeature localWebServiceFeature : paramVarArgs) {
        localWebServiceFeatureList.add(localWebServiceFeature);
      }
      this.faultMessageFeatures.put(localMessageKey, localWebServiceFeatureList);
    }
  }
  
  @NotNull
  public synchronized MessageContextFactory getMessageContextFactory()
  {
    if (this.messageContextFactory == null) {
      this.messageContextFactory = MessageContextFactory.createFactory(getFeatures().toArray());
    }
    return this.messageContextFactory;
  }
  
  protected static class MessageKey
  {
    private final QName operationName;
    private final QName messageName;
    
    public MessageKey(QName paramQName1, QName paramQName2)
    {
      this.operationName = paramQName1;
      this.messageName = paramQName2;
    }
    
    public int hashCode()
    {
      int i = this.operationName != null ? this.operationName.hashCode() : 0;
      int j = this.messageName != null ? this.messageName.hashCode() : 0;
      return (i + j) * j + i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == null) {
        return false;
      }
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      MessageKey localMessageKey = (MessageKey)paramObject;
      if ((this.operationName != localMessageKey.operationName) && ((this.operationName == null) || (!this.operationName.equals(localMessageKey.operationName)))) {
        return false;
      }
      return (this.messageName == localMessageKey.messageName) || ((this.messageName != null) && (this.messageName.equals(localMessageKey.messageName)));
    }
    
    public String toString()
    {
      return "(" + this.operationName + ", " + this.messageName + ")";
    }
  }
}
