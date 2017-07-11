package javax.xml.soap;

public abstract class SAAJMetaFactory
{
  private static final String META_FACTORY_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
  static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";
  
  static SAAJMetaFactory getInstance()
    throws SOAPException
  {
    try
    {
      SAAJMetaFactory localSAAJMetaFactory = (SAAJMetaFactory)FactoryFinder.find("javax.xml.soap.MetaFactory", "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl");
      return localSAAJMetaFactory;
    }
    catch (Exception localException)
    {
      throw new SOAPException("Unable to create SAAJ meta-factory" + localException.getMessage());
    }
  }
  
  protected SAAJMetaFactory() {}
  
  protected abstract MessageFactory newMessageFactory(String paramString)
    throws SOAPException;
  
  protected abstract SOAPFactory newSOAPFactory(String paramString)
    throws SOAPException;
}
