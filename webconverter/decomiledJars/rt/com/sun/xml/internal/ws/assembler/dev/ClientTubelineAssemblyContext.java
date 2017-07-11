package com.sun.xml.internal.ws.assembler.dev;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;

public abstract interface ClientTubelineAssemblyContext
  extends TubelineAssemblyContext
{
  @NotNull
  public abstract EndpointAddress getAddress();
  
  @NotNull
  public abstract WSBinding getBinding();
  
  @NotNull
  public abstract Codec getCodec();
  
  public abstract Container getContainer();
  
  public abstract PolicyMap getPolicyMap();
  
  public abstract WSPortInfo getPortInfo();
  
  @Nullable
  public abstract SEIModel getSEIModel();
  
  @NotNull
  public abstract WSService getService();
  
  public abstract ClientTubeAssemblerContext getWrappedContext();
  
  public abstract WSDLPort getWsdlPort();
  
  public abstract boolean isPolicyAvailable();
  
  public abstract void setCodec(@NotNull Codec paramCodec);
}
