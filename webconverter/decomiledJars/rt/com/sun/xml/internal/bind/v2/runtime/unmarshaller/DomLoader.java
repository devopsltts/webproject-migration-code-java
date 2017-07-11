package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;

public class DomLoader<ResultT extends Result>
  extends Loader
{
  private final DomHandler<?, ResultT> dom;
  
  public DomLoader(DomHandler<?, ResultT> paramDomHandler)
  {
    super(true);
    this.dom = paramDomHandler;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    if (paramState.getTarget() == null) {
      paramState.setTarget(new State(localUnmarshallingContext));
    }
    State localState = (State)paramState.getTarget();
    try
    {
      localState.declarePrefixes(localUnmarshallingContext, localUnmarshallingContext.getNewlyDeclaredPrefixes());
      localState.handler.startElement(paramTagName.uri, paramTagName.local, paramTagName.getQname(), paramTagName.atts);
    }
    catch (SAXException localSAXException)
    {
      localUnmarshallingContext.handleError(localSAXException);
      throw localSAXException;
    }
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    paramState.setLoader(this);
    State localState = (State)paramState.getPrev().getTarget();
    localState.depth += 1;
    paramState.setTarget(localState);
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    if (paramCharSequence.length() == 0) {
      return;
    }
    try
    {
      State localState = (State)paramState.getTarget();
      localState.handler.characters(paramCharSequence.toString().toCharArray(), 0, paramCharSequence.length());
    }
    catch (SAXException localSAXException)
    {
      paramState.getContext().handleError(localSAXException);
      throw localSAXException;
    }
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    State localState = (State)paramState.getTarget();
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    try
    {
      localState.handler.endElement(paramTagName.uri, paramTagName.local, paramTagName.getQname());
      localState.undeclarePrefixes(localUnmarshallingContext.getNewlyDeclaredPrefixes());
    }
    catch (SAXException localSAXException1)
    {
      localUnmarshallingContext.handleError(localSAXException1);
      throw localSAXException1;
    }
    if (--localState.depth == 0)
    {
      try
      {
        localState.undeclarePrefixes(localUnmarshallingContext.getAllDeclaredPrefixes());
        localState.handler.endDocument();
      }
      catch (SAXException localSAXException2)
      {
        localUnmarshallingContext.handleError(localSAXException2);
        throw localSAXException2;
      }
      paramState.setTarget(localState.getElement());
    }
  }
  
  private final class State
  {
    private TransformerHandler handler = null;
    private final ResultT result;
    int depth = 1;
    
    public State(UnmarshallingContext paramUnmarshallingContext)
      throws SAXException
    {
      this.handler = JAXBContextImpl.createTransformerHandler(paramUnmarshallingContext.getJAXBContext().disableSecurityProcessing);
      this.result = DomLoader.this.dom.createUnmarshaller(paramUnmarshallingContext);
      this.handler.setResult(this.result);
      try
      {
        this.handler.setDocumentLocator(paramUnmarshallingContext.getLocator());
        this.handler.startDocument();
        declarePrefixes(paramUnmarshallingContext, paramUnmarshallingContext.getAllDeclaredPrefixes());
      }
      catch (SAXException localSAXException)
      {
        paramUnmarshallingContext.handleError(localSAXException);
        throw localSAXException;
      }
    }
    
    public Object getElement()
    {
      return DomLoader.this.dom.getElement(this.result);
    }
    
    private void declarePrefixes(UnmarshallingContext paramUnmarshallingContext, String[] paramArrayOfString)
      throws SAXException
    {
      for (int i = paramArrayOfString.length - 1; i >= 0; i--)
      {
        String str = paramUnmarshallingContext.getNamespaceURI(paramArrayOfString[i]);
        if (str == null) {
          throw new IllegalStateException("prefix '" + paramArrayOfString[i] + "' isn't bound");
        }
        this.handler.startPrefixMapping(paramArrayOfString[i], str);
      }
    }
    
    private void undeclarePrefixes(String[] paramArrayOfString)
      throws SAXException
    {
      for (int i = paramArrayOfString.length - 1; i >= 0; i--) {
        this.handler.endPrefixMapping(paramArrayOfString[i]);
      }
    }
  }
}
