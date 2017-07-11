package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.developer.SchemaValidationFeature;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube.MetadataResolverImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ServerSchemaValidationTube
  extends AbstractSchemaValidationTube
{
  private static final Logger LOGGER = Logger.getLogger(ServerSchemaValidationTube.class.getName());
  private final Schema schema;
  private final Validator validator;
  private final boolean noValidation;
  private final SEIModel seiModel;
  private final WSDLPort wsdlPort;
  
  public ServerSchemaValidationTube(WSEndpoint paramWSEndpoint, WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramWSBinding, paramTube);
    this.seiModel = paramSEIModel;
    this.wsdlPort = paramWSDLPort;
    if (paramWSEndpoint.getServiceDefinition() != null)
    {
      AbstractSchemaValidationTube.MetadataResolverImpl localMetadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this, paramWSEndpoint.getServiceDefinition());
      Source[] arrayOfSource1 = getSchemaSources(paramWSEndpoint.getServiceDefinition(), localMetadataResolverImpl);
      for (Source localSource : arrayOfSource1) {
        LOGGER.fine("Constructing service validation schema from = " + localSource.getSystemId());
      }
      if (arrayOfSource1.length != 0)
      {
        this.noValidation = false;
        this.sf.setResourceResolver(localMetadataResolverImpl);
        try
        {
          this.schema = this.sf.newSchema(arrayOfSource1);
        }
        catch (SAXException localSAXException)
        {
          throw new WebServiceException(localSAXException);
        }
        this.validator = this.schema.newValidator();
        return;
      }
    }
    this.noValidation = true;
    this.schema = null;
    this.validator = null;
  }
  
  protected Validator getValidator()
  {
    return this.validator;
  }
  
  protected boolean isNoValidation()
  {
    return this.noValidation;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    if ((isNoValidation()) || (!this.feature.isInbound()) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
      return super.processRequest(paramPacket);
    }
    try
    {
      doProcess(paramPacket);
    }
    catch (SAXException localSAXException)
    {
      LOGGER.log(Level.WARNING, "Client Request doesn't pass Service's Schema Validation", localSAXException);
      SOAPVersion localSOAPVersion = this.binding.getSOAPVersion();
      Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(localSOAPVersion, null, localSAXException, localSOAPVersion.faultCodeClient);
      return doReturnWith(paramPacket.createServerResponse(localMessage, this.wsdlPort, this.seiModel, this.binding));
    }
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    if ((isNoValidation()) || (!this.feature.isOutbound()) || (paramPacket.getMessage() == null) || (!paramPacket.getMessage().hasPayload()) || (paramPacket.getMessage().isFault())) {
      return super.processResponse(paramPacket);
    }
    try
    {
      doProcess(paramPacket);
    }
    catch (SAXException localSAXException)
    {
      throw new WebServiceException(localSAXException);
    }
    return super.processResponse(paramPacket);
  }
  
  protected ServerSchemaValidationTube(ServerSchemaValidationTube paramServerSchemaValidationTube, TubeCloner paramTubeCloner)
  {
    super(paramServerSchemaValidationTube, paramTubeCloner);
    this.schema = paramServerSchemaValidationTube.schema;
    this.validator = this.schema.newValidator();
    this.noValidation = paramServerSchemaValidationTube.noValidation;
    this.seiModel = paramServerSchemaValidationTube.seiModel;
    this.wsdlPort = paramServerSchemaValidationTube.wsdlPort;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ServerSchemaValidationTube(this, paramTubeCloner);
  }
}
