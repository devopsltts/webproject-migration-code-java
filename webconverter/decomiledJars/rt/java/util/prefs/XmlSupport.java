package java.util.prefs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class XmlSupport
{
  private static final String PREFS_DTD_URI = "http://java.sun.com/dtd/preferences.dtd";
  private static final String PREFS_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >";
  private static final String EXTERNAL_XML_VERSION = "1.0";
  private static final String MAP_XML_VERSION = "1.0";
  
  XmlSupport() {}
  
  static void export(OutputStream paramOutputStream, Preferences paramPreferences, boolean paramBoolean)
    throws IOException, BackingStoreException
  {
    if (((AbstractPreferences)paramPreferences).isRemoved()) {
      throw new IllegalStateException("Node has been removed");
    }
    Document localDocument = createPrefsDoc("preferences");
    Element localElement1 = localDocument.getDocumentElement();
    localElement1.setAttribute("EXTERNAL_XML_VERSION", "1.0");
    Element localElement2 = (Element)localElement1.appendChild(localDocument.createElement("root"));
    localElement2.setAttribute("type", paramPreferences.isUserNode() ? "user" : "system");
    ArrayList localArrayList = new ArrayList();
    Object localObject = paramPreferences;
    for (Preferences localPreferences = ((Preferences)localObject).parent(); localPreferences != null; localPreferences = ((Preferences)localObject).parent())
    {
      localArrayList.add(localObject);
      localObject = localPreferences;
    }
    localObject = localElement2;
    for (int i = localArrayList.size() - 1; i >= 0; i--)
    {
      ((Element)localObject).appendChild(localDocument.createElement("map"));
      localObject = (Element)((Element)localObject).appendChild(localDocument.createElement("node"));
      ((Element)localObject).setAttribute("name", ((Preferences)localArrayList.get(i)).name());
    }
    putPreferencesInXml((Element)localObject, localDocument, paramPreferences, paramBoolean);
    writeDoc(localDocument, paramOutputStream);
  }
  
  private static void putPreferencesInXml(Element paramElement, Document paramDocument, Preferences paramPreferences, boolean paramBoolean)
    throws BackingStoreException
  {
    Preferences[] arrayOfPreferences = null;
    String[] arrayOfString = null;
    Object localObject1;
    synchronized (((AbstractPreferences)paramPreferences).lock)
    {
      if (((AbstractPreferences)paramPreferences).isRemoved())
      {
        paramElement.getParentNode().removeChild(paramElement);
        return;
      }
      localObject1 = paramPreferences.keys();
      Element localElement1 = (Element)paramElement.appendChild(paramDocument.createElement("map"));
      for (int j = 0; j < localObject1.length; j++)
      {
        Element localElement2 = (Element)localElement1.appendChild(paramDocument.createElement("entry"));
        localElement2.setAttribute("key", localObject1[j]);
        localElement2.setAttribute("value", paramPreferences.get(localObject1[j], null));
      }
      if (paramBoolean)
      {
        arrayOfString = paramPreferences.childrenNames();
        arrayOfPreferences = new Preferences[arrayOfString.length];
        for (j = 0; j < arrayOfString.length; j++) {
          arrayOfPreferences[j] = paramPreferences.node(arrayOfString[j]);
        }
      }
    }
    if (paramBoolean) {
      for (int i = 0; i < arrayOfString.length; i++)
      {
        localObject1 = (Element)paramElement.appendChild(paramDocument.createElement("node"));
        ((Element)localObject1).setAttribute("name", arrayOfString[i]);
        putPreferencesInXml((Element)localObject1, paramDocument, arrayOfPreferences[i], paramBoolean);
      }
    }
  }
  
  static void importPreferences(InputStream paramInputStream)
    throws IOException, InvalidPreferencesFormatException
  {
    try
    {
      Document localDocument = loadPrefsDoc(paramInputStream);
      String str = localDocument.getDocumentElement().getAttribute("EXTERNAL_XML_VERSION");
      if (str.compareTo("1.0") > 0) {
        throw new InvalidPreferencesFormatException("Exported preferences file format version " + str + " is not supported. This java installation can read" + " versions " + "1.0" + " or older. You may need" + " to install a newer version of JDK.");
      }
      Element localElement = (Element)localDocument.getDocumentElement().getChildNodes().item(0);
      Preferences localPreferences = localElement.getAttribute("type").equals("user") ? Preferences.userRoot() : Preferences.systemRoot();
      ImportSubtree(localPreferences, localElement);
    }
    catch (SAXException localSAXException)
    {
      throw new InvalidPreferencesFormatException(localSAXException);
    }
  }
  
  private static Document createPrefsDoc(String paramString)
  {
    try
    {
      DOMImplementation localDOMImplementation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
      DocumentType localDocumentType = localDOMImplementation.createDocumentType(paramString, null, "http://java.sun.com/dtd/preferences.dtd");
      return localDOMImplementation.createDocument(null, paramString, localDocumentType);
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new AssertionError(localParserConfigurationException);
    }
  }
  
  private static Document loadPrefsDoc(InputStream paramInputStream)
    throws SAXException, IOException
  {
    DocumentBuilderFactory localDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    localDocumentBuilderFactory.setIgnoringElementContentWhitespace(true);
    localDocumentBuilderFactory.setValidating(true);
    localDocumentBuilderFactory.setCoalescing(true);
    localDocumentBuilderFactory.setIgnoringComments(true);
    try
    {
      DocumentBuilder localDocumentBuilder = localDocumentBuilderFactory.newDocumentBuilder();
      localDocumentBuilder.setEntityResolver(new Resolver(null));
      localDocumentBuilder.setErrorHandler(new EH(null));
      return localDocumentBuilder.parse(new InputSource(paramInputStream));
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new AssertionError(localParserConfigurationException);
    }
  }
  
  private static final void writeDoc(Document paramDocument, OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
      try
      {
        localTransformerFactory.setAttribute("indent-number", new Integer(2));
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      Transformer localTransformer = localTransformerFactory.newTransformer();
      localTransformer.setOutputProperty("doctype-system", paramDocument.getDoctype().getSystemId());
      localTransformer.setOutputProperty("indent", "yes");
      localTransformer.transform(new DOMSource(paramDocument), new StreamResult(new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF-8"))));
    }
    catch (TransformerException localTransformerException)
    {
      throw new AssertionError(localTransformerException);
    }
  }
  
  private static void ImportSubtree(Preferences paramPreferences, Element paramElement)
  {
    NodeList localNodeList = paramElement.getChildNodes();
    Object localObject1 = localNodeList.getLength();
    Preferences[] arrayOfPreferences;
    synchronized (((AbstractPreferences)paramPreferences).lock)
    {
      if (((AbstractPreferences)paramPreferences).isRemoved()) {
        return;
      }
      Element localElement1 = (Element)localNodeList.item(0);
      ImportPrefs(paramPreferences, localElement1);
      arrayOfPreferences = new Preferences[localObject1 - 1];
      for (Object localObject2 = 1; localObject2 < localObject1; localObject2++)
      {
        Element localElement2 = (Element)localNodeList.item(localObject2);
        arrayOfPreferences[(localObject2 - 1)] = paramPreferences.node(localElement2.getAttribute("name"));
      }
    }
    for (??? = 1; ??? < localObject1; ???++) {
      ImportSubtree(arrayOfPreferences[(??? - 1)], (Element)localNodeList.item(???));
    }
  }
  
  private static void ImportPrefs(Preferences paramPreferences, Element paramElement)
  {
    NodeList localNodeList = paramElement.getChildNodes();
    int i = 0;
    int j = localNodeList.getLength();
    while (i < j)
    {
      Element localElement = (Element)localNodeList.item(i);
      paramPreferences.put(localElement.getAttribute("key"), localElement.getAttribute("value"));
      i++;
    }
  }
  
  static void exportMap(OutputStream paramOutputStream, Map<String, String> paramMap)
    throws IOException
  {
    Document localDocument = createPrefsDoc("map");
    Element localElement1 = localDocument.getDocumentElement();
    localElement1.setAttribute("MAP_XML_VERSION", "1.0");
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Element localElement2 = (Element)localElement1.appendChild(localDocument.createElement("entry"));
      localElement2.setAttribute("key", (String)localEntry.getKey());
      localElement2.setAttribute("value", (String)localEntry.getValue());
    }
    writeDoc(localDocument, paramOutputStream);
  }
  
  static void importMap(InputStream paramInputStream, Map<String, String> paramMap)
    throws IOException, InvalidPreferencesFormatException
  {
    try
    {
      Document localDocument = loadPrefsDoc(paramInputStream);
      Element localElement1 = localDocument.getDocumentElement();
      String str = localElement1.getAttribute("MAP_XML_VERSION");
      if (str.compareTo("1.0") > 0) {
        throw new InvalidPreferencesFormatException("Preferences map file format version " + str + " is not supported. This java installation can read" + " versions " + "1.0" + " or older. You may need" + " to install a newer version of JDK.");
      }
      NodeList localNodeList = localElement1.getChildNodes();
      int i = 0;
      int j = localNodeList.getLength();
      while (i < j)
      {
        Element localElement2 = (Element)localNodeList.item(i);
        paramMap.put(localElement2.getAttribute("key"), localElement2.getAttribute("value"));
        i++;
      }
    }
    catch (SAXException localSAXException)
    {
      throw new InvalidPreferencesFormatException(localSAXException);
    }
  }
  
  private static class EH
    implements ErrorHandler
  {
    private EH() {}
    
    public void error(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
    
    public void fatalError(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
    
    public void warning(SAXParseException paramSAXParseException)
      throws SAXException
    {
      throw paramSAXParseException;
    }
  }
  
  private static class Resolver
    implements EntityResolver
  {
    private Resolver() {}
    
    public InputSource resolveEntity(String paramString1, String paramString2)
      throws SAXException
    {
      if (paramString2.equals("http://java.sun.com/dtd/preferences.dtd"))
      {
        InputSource localInputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- DTD for preferences --><!ELEMENT preferences (root) ><!ATTLIST preferences EXTERNAL_XML_VERSION CDATA \"0.0\"  ><!ELEMENT root (map, node*) ><!ATTLIST root          type (system|user) #REQUIRED ><!ELEMENT node (map, node*) ><!ATTLIST node          name CDATA #REQUIRED ><!ELEMENT map (entry*) ><!ATTLIST map  MAP_XML_VERSION CDATA \"0.0\"  ><!ELEMENT entry EMPTY ><!ATTLIST entry          key CDATA #REQUIRED          value CDATA #REQUIRED >"));
        localInputSource.setSystemId("http://java.sun.com/dtd/preferences.dtd");
        return localInputSource;
      }
      throw new SAXException("Invalid system identifier: " + paramString2);
    }
  }
}
