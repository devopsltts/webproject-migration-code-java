package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xslt.EnvironmentCheck;
import com.sun.org.apache.xml.internal.utils.Hashtree2Node;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXNotSupportedException;

public class Extensions
{
  static final String JDK_DEFAULT_DOM = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
  
  private Extensions() {}
  
  public static NodeSet nodeset(ExpressionContext paramExpressionContext, Object paramObject)
  {
    if ((paramObject instanceof NodeIterator)) {
      return new NodeSet((NodeIterator)paramObject);
    }
    String str;
    if ((paramObject instanceof String)) {
      str = (String)paramObject;
    } else if ((paramObject instanceof Boolean)) {
      str = new XBoolean(((Boolean)paramObject).booleanValue()).str();
    } else if ((paramObject instanceof Double)) {
      str = new XNumber(((Double)paramObject).doubleValue()).str();
    } else {
      str = paramObject.toString();
    }
    Document localDocument = getDocument();
    Text localText = localDocument.createTextNode(str);
    DocumentFragment localDocumentFragment = localDocument.createDocumentFragment();
    localDocumentFragment.appendChild(localText);
    return new NodeSet(localDocumentFragment);
  }
  
  public static NodeList intersection(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    return ExsltSets.intersection(paramNodeList1, paramNodeList2);
  }
  
  public static NodeList difference(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    return ExsltSets.difference(paramNodeList1, paramNodeList2);
  }
  
  public static NodeList distinct(NodeList paramNodeList)
  {
    return ExsltSets.distinct(paramNodeList);
  }
  
  public static boolean hasSameNodes(NodeList paramNodeList1, NodeList paramNodeList2)
  {
    NodeSet localNodeSet1 = new NodeSet(paramNodeList1);
    NodeSet localNodeSet2 = new NodeSet(paramNodeList2);
    if (localNodeSet1.getLength() != localNodeSet2.getLength()) {
      return false;
    }
    for (int i = 0; i < localNodeSet1.getLength(); i++)
    {
      Node localNode = localNodeSet1.elementAt(i);
      if (!localNodeSet2.contains(localNode)) {
        return false;
      }
    }
    return true;
  }
  
  public static XObject evaluate(ExpressionContext paramExpressionContext, String paramString)
    throws SAXNotSupportedException
  {
    return ExsltDynamic.evaluate(paramExpressionContext, paramString);
  }
  
  public static NodeList tokenize(String paramString1, String paramString2)
  {
    Document localDocument = getDocument();
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString1, paramString2);
    NodeSet localNodeSet = new NodeSet();
    synchronized (localDocument)
    {
      while (localStringTokenizer.hasMoreTokens()) {
        localNodeSet.addNode(localDocument.createTextNode(localStringTokenizer.nextToken()));
      }
    }
    return localNodeSet;
  }
  
  public static NodeList tokenize(String paramString)
  {
    return tokenize(paramString, " \t\n\r");
  }
  
  public static Node checkEnvironment(ExpressionContext paramExpressionContext)
  {
    Document localDocument = getDocument();
    Object localObject = null;
    try
    {
      localObject = checkEnvironmentUsingWhich(paramExpressionContext, localDocument);
      if (null != localObject) {
        return localObject;
      }
      EnvironmentCheck localEnvironmentCheck = new EnvironmentCheck();
      Map localMap = localEnvironmentCheck.getEnvironmentHash();
      localObject = localDocument.createElement("checkEnvironmentExtension");
      localEnvironmentCheck.appendEnvironmentReport((Node)localObject, localDocument, localMap);
      localEnvironmentCheck = null;
    }
    catch (Exception localException)
    {
      throw new WrappedRuntimeException(localException);
    }
    return localObject;
  }
  
  private static Node checkEnvironmentUsingWhich(ExpressionContext paramExpressionContext, Document paramDocument)
  {
    String str1 = "org.apache.env.Which";
    String str2 = "which";
    Class[] arrayOfClass = { Hashtable.class, String.class, String.class };
    try
    {
      Class localClass = ObjectFactory.findProviderClass("org.apache.env.Which", true);
      if (null == localClass) {
        return null;
      }
      Method localMethod = localClass.getMethod("which", arrayOfClass);
      Hashtable localHashtable = new Hashtable();
      Object[] arrayOfObject = { localHashtable, "XmlCommons;Xalan;Xerces;Crimson;Ant", "" };
      Object localObject = localMethod.invoke(null, arrayOfObject);
      Element localElement = paramDocument.createElement("checkEnvironmentExtension");
      Hashtree2Node.appendHashToNode(localHashtable, "whichReport", localElement, paramDocument);
      return localElement;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  private static Document getDocument()
  {
    try
    {
      if (System.getSecurityManager() == null) {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      }
      return DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", null).newDocumentBuilder().newDocument();
    }
    catch (ParserConfigurationException localParserConfigurationException)
    {
      throw new WrappedRuntimeException(localParserConfigurationException);
    }
  }
}
