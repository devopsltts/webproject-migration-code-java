package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class W3CDomHandler
  implements DomHandler<Element, DOMResult>
{
  private DocumentBuilder builder;
  
  public W3CDomHandler()
  {
    this.builder = null;
  }
  
  public W3CDomHandler(DocumentBuilder paramDocumentBuilder)
  {
    if (paramDocumentBuilder == null) {
      throw new IllegalArgumentException();
    }
    this.builder = paramDocumentBuilder;
  }
  
  public DocumentBuilder getBuilder()
  {
    return this.builder;
  }
  
  public void setBuilder(DocumentBuilder paramDocumentBuilder)
  {
    this.builder = paramDocumentBuilder;
  }
  
  public DOMResult createUnmarshaller(ValidationEventHandler paramValidationEventHandler)
  {
    if (this.builder == null) {
      return new DOMResult();
    }
    return new DOMResult(this.builder.newDocument());
  }
  
  public Element getElement(DOMResult paramDOMResult)
  {
    Node localNode = paramDOMResult.getNode();
    if ((localNode instanceof Document)) {
      return ((Document)localNode).getDocumentElement();
    }
    if ((localNode instanceof Element)) {
      return (Element)localNode;
    }
    if ((localNode instanceof DocumentFragment)) {
      return (Element)localNode.getChildNodes().item(0);
    }
    throw new IllegalStateException(localNode.toString());
  }
  
  public Source marshal(Element paramElement, ValidationEventHandler paramValidationEventHandler)
  {
    return new DOMSource(paramElement);
  }
}
