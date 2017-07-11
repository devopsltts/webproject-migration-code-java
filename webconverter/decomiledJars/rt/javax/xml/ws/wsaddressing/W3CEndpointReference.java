package javax.xml.ws.wsaddressing;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

@XmlRootElement(name="EndpointReference", namespace="http://www.w3.org/2005/08/addressing")
@XmlType(name="EndpointReferenceType", namespace="http://www.w3.org/2005/08/addressing")
public final class W3CEndpointReference
  extends EndpointReference
{
  private final JAXBContext w3cjc = getW3CJaxbContext();
  protected static final String NS = "http://www.w3.org/2005/08/addressing";
  @XmlElement(name="Address", namespace="http://www.w3.org/2005/08/addressing")
  private Address address;
  @XmlElement(name="ReferenceParameters", namespace="http://www.w3.org/2005/08/addressing")
  private Elements referenceParameters;
  @XmlElement(name="Metadata", namespace="http://www.w3.org/2005/08/addressing")
  private Elements metadata;
  @XmlAnyAttribute
  Map<QName, String> attributes;
  @XmlAnyElement
  List<Element> elements;
  
  protected W3CEndpointReference() {}
  
  public W3CEndpointReference(Source paramSource)
  {
    try
    {
      W3CEndpointReference localW3CEndpointReference = (W3CEndpointReference)this.w3cjc.createUnmarshaller().unmarshal(paramSource, W3CEndpointReference.class).getValue();
      this.address = localW3CEndpointReference.address;
      this.metadata = localW3CEndpointReference.metadata;
      this.referenceParameters = localW3CEndpointReference.referenceParameters;
      this.elements = localW3CEndpointReference.elements;
      this.attributes = localW3CEndpointReference.attributes;
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error unmarshalling W3CEndpointReference ", localJAXBException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new WebServiceException("Source did not contain W3CEndpointReference", localClassCastException);
    }
  }
  
  public void writeTo(Result paramResult)
  {
    try
    {
      Marshaller localMarshaller = this.w3cjc.createMarshaller();
      localMarshaller.marshal(this, paramResult);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error marshalling W3CEndpointReference. ", localJAXBException);
    }
  }
  
  private static JAXBContext getW3CJaxbContext()
  {
    try
    {
      return JAXBContext.newInstance(new Class[] { W3CEndpointReference.class });
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", localJAXBException);
    }
  }
  
  @XmlType(name="address", namespace="http://www.w3.org/2005/08/addressing")
  private static class Address
  {
    @XmlValue
    String uri;
    @XmlAnyAttribute
    Map<QName, String> attributes;
    
    protected Address() {}
  }
  
  @XmlType(name="elements", namespace="http://www.w3.org/2005/08/addressing")
  private static class Elements
  {
    @XmlAnyElement
    List<Element> elements;
    @XmlAnyAttribute
    Map<QName, String> attributes;
    
    protected Elements() {}
  }
}
