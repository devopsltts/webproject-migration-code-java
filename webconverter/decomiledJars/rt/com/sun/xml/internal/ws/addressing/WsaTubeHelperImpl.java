package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

public class WsaTubeHelperImpl
  extends WsaTubeHelper
{
  static final JAXBContext jc;
  
  public WsaTubeHelperImpl(WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding)
  {
    super(paramWSBinding, paramSEIModel, paramWSDLPort);
  }
  
  private Marshaller createMarshaller()
    throws JAXBException
  {
    Marshaller localMarshaller = jc.createMarshaller();
    localMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
    return localMarshaller;
  }
  
  public final void getProblemActionDetail(String paramString, Element paramElement)
  {
    ProblemAction localProblemAction = new ProblemAction(paramString);
    try
    {
      createMarshaller().marshal(localProblemAction, paramElement);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  public final void getInvalidMapDetail(QName paramQName, Element paramElement)
  {
    ProblemHeaderQName localProblemHeaderQName = new ProblemHeaderQName(paramQName);
    try
    {
      createMarshaller().marshal(localProblemHeaderQName, paramElement);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  public final void getMapRequiredDetail(QName paramQName, Element paramElement)
  {
    getInvalidMapDetail(paramQName, paramElement);
  }
  
  static
  {
    try
    {
      jc = JAXBContext.newInstance(new Class[] { ProblemAction.class, ProblemHeaderQName.class });
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
}
