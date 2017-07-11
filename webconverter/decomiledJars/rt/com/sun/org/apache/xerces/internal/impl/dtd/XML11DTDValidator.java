package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class XML11DTDValidator
  extends XMLDTDValidator
{
  protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
  
  public XML11DTDValidator() {}
  
  public void reset(XMLComponentManager paramXMLComponentManager)
  {
    XMLDTDValidator localXMLDTDValidator = null;
    if (((localXMLDTDValidator = (XMLDTDValidator)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/dtd")) != null) && (localXMLDTDValidator != this)) {
      this.fGrammarBucket = localXMLDTDValidator.getGrammarBucket();
    }
    super.reset(paramXMLComponentManager);
  }
  
  protected void init()
  {
    if ((this.fValidation) || (this.fDynamicValidation))
    {
      super.init();
      try
      {
        this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
        this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
        this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
        this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
        this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");
      }
      catch (Exception localException)
      {
        localException.printStackTrace(System.err);
      }
    }
  }
}
