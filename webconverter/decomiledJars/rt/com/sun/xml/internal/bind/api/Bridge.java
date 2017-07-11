package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class Bridge<T>
{
  protected final JAXBContextImpl context;
  
  protected Bridge(JAXBContextImpl paramJAXBContextImpl)
  {
    this.context = paramJAXBContextImpl;
  }
  
  @NotNull
  public JAXBRIContext getContext()
  {
    return this.context;
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    marshal(paramT, paramXMLStreamWriter, null);
  }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)this.context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramXMLStreamWriter);
    localMarshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException
  {
    marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramXMLStreamWriter);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter)
    throws JAXBException;
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    marshal(paramT, paramOutputStream, paramNamespaceContext, null);
  }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)this.context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramOutputStream, paramNamespaceContext);
    localMarshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException
  {
    marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramOutputStream, paramNamespaceContext);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext)
    throws JAXBException;
  
  public final void marshal(T paramT, Node paramNode)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)this.context.marshallerPool.take();
    marshal(localMarshaller, paramT, paramNode);
    this.context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Node paramNode)
    throws JAXBException
  {
    marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramNode);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Node paramNode)
    throws JAXBException;
  
  public final void marshal(T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    marshal(paramT, paramContentHandler, null);
  }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)this.context.marshallerPool.take();
    localMarshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(localMarshaller, paramT, paramContentHandler);
    localMarshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, ContentHandler paramContentHandler)
    throws JAXBException
  {
    marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramContentHandler);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler)
    throws JAXBException;
  
  public final void marshal(T paramT, Result paramResult)
    throws JAXBException
  {
    Marshaller localMarshaller = (Marshaller)this.context.marshallerPool.take();
    marshal(localMarshaller, paramT, paramResult);
    this.context.marshallerPool.recycle(localMarshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Result paramResult)
    throws JAXBException
  {
    marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramResult);
  }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Result paramResult)
    throws JAXBException;
  
  private T exit(T paramT, Unmarshaller paramUnmarshaller)
  {
    paramUnmarshaller.setAttachmentUnmarshaller(null);
    this.context.unmarshallerPool.recycle(paramUnmarshaller);
    return paramT;
  }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return unmarshal(paramXMLStreamReader, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return exit(unmarshal(localUnmarshaller, paramXMLStreamReader), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException
  {
    return unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramXMLStreamReader);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull XMLStreamReader paramXMLStreamReader)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource)
    throws JAXBException
  {
    return unmarshal(paramSource, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return exit(unmarshal(localUnmarshaller, paramSource), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Source paramSource)
    throws JAXBException
  {
    return unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramSource);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Source paramSource)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull InputStream paramInputStream)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    return exit(unmarshal(localUnmarshaller, paramInputStream), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull InputStream paramInputStream)
    throws JAXBException
  {
    return unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramInputStream);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull InputStream paramInputStream)
    throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode)
    throws JAXBException
  {
    return unmarshal(paramNode, null);
  }
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller)
    throws JAXBException
  {
    Unmarshaller localUnmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    localUnmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return exit(unmarshal(localUnmarshaller, paramNode), localUnmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Node paramNode)
    throws JAXBException
  {
    return unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramNode);
  }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Node paramNode)
    throws JAXBException;
  
  public abstract TypeReference getTypeReference();
}
