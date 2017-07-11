package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;
import javax.xml.namespace.QName;

public class ServerMUTube
  extends MUTube
{
  private ServerTubeAssemblerContext tubeContext;
  private final Set<String> roles;
  private final Set<QName> handlerKnownHeaders;
  
  public ServerMUTube(ServerTubeAssemblerContext paramServerTubeAssemblerContext, Tube paramTube)
  {
    super(paramServerTubeAssemblerContext.getEndpoint().getBinding(), paramTube);
    this.tubeContext = paramServerTubeAssemblerContext;
    HandlerConfiguration localHandlerConfiguration = this.binding.getHandlerConfig();
    this.roles = localHandlerConfiguration.getRoles();
    this.handlerKnownHeaders = this.binding.getKnownHeaders();
  }
  
  protected ServerMUTube(ServerMUTube paramServerMUTube, TubeCloner paramTubeCloner)
  {
    super(paramServerMUTube, paramTubeCloner);
    this.tubeContext = paramServerMUTube.tubeContext;
    this.roles = paramServerMUTube.roles;
    this.handlerKnownHeaders = paramServerMUTube.handlerKnownHeaders;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    Set localSet = getMisUnderstoodHeaders(paramPacket.getMessage().getHeaders(), this.roles, this.handlerKnownHeaders);
    if ((localSet == null) || (localSet.isEmpty())) {
      return doInvoke(this.next, paramPacket);
    }
    return doReturnWith(paramPacket.createServerResponse(createMUSOAPFaultMessage(localSet), this.tubeContext.getWsdlModel(), this.tubeContext.getSEIModel(), this.tubeContext.getEndpoint().getBinding()));
  }
  
  public ServerMUTube copy(TubeCloner paramTubeCloner)
  {
    return new ServerMUTube(this, paramTubeCloner);
  }
}
