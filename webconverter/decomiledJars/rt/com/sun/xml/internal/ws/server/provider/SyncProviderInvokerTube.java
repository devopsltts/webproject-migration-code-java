package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncProviderInvokerTube<T>
  extends ProviderInvokerTube<T>
{
  private static final Logger LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.SyncProviderInvokerTube");
  
  public SyncProviderInvokerTube(Invoker paramInvoker, ProviderArgumentsBuilder<T> paramProviderArgumentsBuilder)
  {
    super(paramInvoker, paramProviderArgumentsBuilder);
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    WSDLPort localWSDLPort = getEndpoint().getPort();
    WSBinding localWSBinding = getEndpoint().getBinding();
    Object localObject1 = this.argsBuilder.getParameter(paramPacket);
    LOGGER.fine("Invoking Provider Endpoint");
    Object localObject2;
    try
    {
      localObject2 = getInvoker(paramPacket).invokeProvider(paramPacket, localObject1);
    }
    catch (Exception localException)
    {
      LOGGER.log(Level.SEVERE, localException.getMessage(), localException);
      localObject3 = this.argsBuilder.getResponse(paramPacket, localException, localWSDLPort, localWSBinding);
      return doReturnWith((Packet)localObject3);
    }
    if ((localObject2 == null) && (paramPacket.transportBackChannel != null)) {
      paramPacket.transportBackChannel.close();
    }
    Packet localPacket = this.argsBuilder.getResponse(paramPacket, localObject2, localWSDLPort, localWSBinding);
    Object localObject3 = (ThrowableContainerPropertySet)localPacket.getSatellite(ThrowableContainerPropertySet.class);
    Throwable localThrowable = localObject3 != null ? ((ThrowableContainerPropertySet)localObject3).getThrowable() : null;
    return localThrowable != null ? doThrow(localPacket, localThrowable) : doReturnWith(localPacket);
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    return doReturnWith(paramPacket);
  }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    return doThrow(paramThrowable);
  }
}
