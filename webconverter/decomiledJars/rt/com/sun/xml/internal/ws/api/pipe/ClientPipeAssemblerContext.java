package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.api.server.Container;
import java.io.PrintStream;

/**
 * @deprecated
 */
public final class ClientPipeAssemblerContext
  extends ClientTubeAssemblerContext
{
  public ClientPipeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @NotNull WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding)
  {
    this(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, Container.NONE);
  }
  
  public ClientPipeAssemblerContext(@NotNull EndpointAddress paramEndpointAddress, @NotNull WSDLPort paramWSDLPort, @NotNull WSService paramWSService, @NotNull WSBinding paramWSBinding, @NotNull Container paramContainer)
  {
    super(paramEndpointAddress, paramWSDLPort, paramWSService, paramWSBinding, paramContainer);
  }
  
  public Pipe createDumpPipe(String paramString, PrintStream paramPrintStream, Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createDumpTube(paramString, paramPrintStream, PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createWsaPipe(Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createWsaTube(PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createClientMUPipe(Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createClientMUTube(PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createValidationPipe(Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createValidationTube(PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createHandlerPipe(Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createHandlerTube(PipeAdapter.adapt(paramPipe)));
  }
  
  @NotNull
  public Pipe createSecurityPipe(@NotNull Pipe paramPipe)
  {
    return PipeAdapter.adapt(super.createSecurityTube(PipeAdapter.adapt(paramPipe)));
  }
  
  public Pipe createTransportPipe()
  {
    return PipeAdapter.adapt(super.createTransportTube());
  }
}
