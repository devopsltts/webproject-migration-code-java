package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class AbstractTranslet
  implements Translet
{
  public String _version = "1.0";
  public String _method = null;
  public String _encoding = "UTF-8";
  public boolean _omitHeader = false;
  public String _standalone = null;
  public boolean _isStandalone = false;
  public String _doctypePublic = null;
  public String _doctypeSystem = null;
  public boolean _indent = false;
  public String _mediaType = null;
  public Vector _cdata = null;
  public int _indentamount = -1;
  public static final int FIRST_TRANSLET_VERSION = 100;
  public static final int VER_SPLIT_NAMES_ARRAY = 101;
  public static final int CURRENT_TRANSLET_VERSION = 101;
  protected int transletVersion = 100;
  protected String[] namesArray;
  protected String[] urisArray;
  protected int[] typesArray;
  protected String[] namespaceArray;
  protected Templates _templates = null;
  protected boolean _hasIdCall = false;
  protected StringValueHandler stringValueHandler = new StringValueHandler();
  private static final String EMPTYSTRING = "";
  private static final String ID_INDEX_NAME = "##id";
  private boolean _useServicesMechanism;
  private String _accessExternalStylesheet = "all";
  protected int pbase = 0;
  protected int pframe = 0;
  protected ArrayList paramsStack = new ArrayList();
  private MessageHandler _msgHandler = null;
  public Map<String, DecimalFormat> _formatSymbols = null;
  private Map<String, KeyIndex> _keyIndexes = null;
  private KeyIndex _emptyKeyIndex = null;
  private int _indexSize = 0;
  private int _currentRootForKeys = 0;
  private DOMCache _domCache = null;
  private Map<String, Class<?>> _auxClasses = null;
  protected DOMImplementation _domImplementation = null;
  
  public AbstractTranslet() {}
  
  public void printInternalState()
  {
    System.out.println("-------------------------------------");
    System.out.println("AbstractTranslet this = " + this);
    System.out.println("pbase = " + this.pbase);
    System.out.println("vframe = " + this.pframe);
    System.out.println("paramsStack.size() = " + this.paramsStack.size());
    System.out.println("namesArray.size = " + this.namesArray.length);
    System.out.println("namespaceArray.size = " + this.namespaceArray.length);
    System.out.println("");
    System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
  }
  
  public final DOMAdapter makeDOMAdapter(DOM paramDOM)
    throws TransletException
  {
    setRootForKeys(paramDOM.getDocument());
    return new DOMAdapter(paramDOM, this.namesArray, this.urisArray, this.typesArray, this.namespaceArray);
  }
  
  public final void pushParamFrame()
  {
    this.paramsStack.add(this.pframe, new Integer(this.pbase));
    this.pbase = (++this.pframe);
  }
  
  public final void popParamFrame()
  {
    if (this.pbase > 0)
    {
      int i = ((Integer)this.paramsStack.get(--this.pbase)).intValue();
      for (int j = this.pframe - 1; j >= this.pbase; j--) {
        this.paramsStack.remove(j);
      }
      this.pframe = this.pbase;
      this.pbase = i;
    }
  }
  
  public final Object addParameter(String paramString, Object paramObject)
  {
    paramString = BasisLibrary.mapQNameToJavaName(paramString);
    return addParameter(paramString, paramObject, false);
  }
  
  public final Object addParameter(String paramString, Object paramObject, boolean paramBoolean)
  {
    for (int i = this.pframe - 1; i >= this.pbase; i--)
    {
      Parameter localParameter = (Parameter)this.paramsStack.get(i);
      if (localParameter._name.equals(paramString))
      {
        if ((localParameter._isDefault) || (!paramBoolean))
        {
          localParameter._value = paramObject;
          localParameter._isDefault = paramBoolean;
          return paramObject;
        }
        return localParameter._value;
      }
    }
    this.paramsStack.add(this.pframe++, new Parameter(paramString, paramObject, paramBoolean));
    return paramObject;
  }
  
  public void clearParameters()
  {
    this.pbase = (this.pframe = 0);
    this.paramsStack.clear();
  }
  
  public final Object getParameter(String paramString)
  {
    paramString = BasisLibrary.mapQNameToJavaName(paramString);
    for (int i = this.pframe - 1; i >= this.pbase; i--)
    {
      Parameter localParameter = (Parameter)this.paramsStack.get(i);
      if (localParameter._name.equals(paramString)) {
        return localParameter._value;
      }
    }
    return null;
  }
  
  public final void setMessageHandler(MessageHandler paramMessageHandler)
  {
    this._msgHandler = paramMessageHandler;
  }
  
  public final void displayMessage(String paramString)
  {
    if (this._msgHandler == null) {
      System.err.println(paramString);
    } else {
      this._msgHandler.displayMessage(paramString);
    }
  }
  
  public void addDecimalFormat(String paramString, DecimalFormatSymbols paramDecimalFormatSymbols)
  {
    if (this._formatSymbols == null) {
      this._formatSymbols = new HashMap();
    }
    if (paramString == null) {
      paramString = "";
    }
    DecimalFormat localDecimalFormat = new DecimalFormat();
    if (paramDecimalFormatSymbols != null) {
      localDecimalFormat.setDecimalFormatSymbols(paramDecimalFormatSymbols);
    }
    this._formatSymbols.put(paramString, localDecimalFormat);
  }
  
  public final DecimalFormat getDecimalFormat(String paramString)
  {
    if (this._formatSymbols != null)
    {
      if (paramString == null) {
        paramString = "";
      }
      DecimalFormat localDecimalFormat = (DecimalFormat)this._formatSymbols.get(paramString);
      if (localDecimalFormat == null) {
        localDecimalFormat = (DecimalFormat)this._formatSymbols.get("");
      }
      return localDecimalFormat;
    }
    return null;
  }
  
  public final void prepassDocument(DOM paramDOM)
  {
    setIndexSize(paramDOM.getSize());
    buildIDIndex(paramDOM);
  }
  
  private final void buildIDIndex(DOM paramDOM)
  {
    setRootForKeys(paramDOM.getDocument());
    if ((paramDOM instanceof DOMEnhancedForDTM))
    {
      DOMEnhancedForDTM localDOMEnhancedForDTM = (DOMEnhancedForDTM)paramDOM;
      if (localDOMEnhancedForDTM.hasDOMSource())
      {
        buildKeyIndex("##id", paramDOM);
        return;
      }
      Map localMap = localDOMEnhancedForDTM.getElementsWithIDs();
      if (localMap == null) {
        return;
      }
      int i = 0;
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        int j = paramDOM.getNodeHandle(((Integer)localEntry.getValue()).intValue());
        buildKeyIndex("##id", j, (String)localEntry.getKey());
        i = 1;
      }
      if (i != 0) {
        setKeyIndexDom("##id", paramDOM);
      }
    }
  }
  
  public final void postInitialization()
  {
    if (this.transletVersion < 101)
    {
      int i = this.namesArray.length;
      String[] arrayOfString1 = new String[i];
      String[] arrayOfString2 = new String[i];
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < i; j++)
      {
        String str = this.namesArray[j];
        int k = str.lastIndexOf(':');
        int m = k + 1;
        if (k > -1) {
          arrayOfString1[j] = str.substring(0, k);
        }
        if (str.charAt(m) == '@')
        {
          m++;
          arrayOfInt[j] = 2;
        }
        else if (str.charAt(m) == '?')
        {
          m++;
          arrayOfInt[j] = 13;
        }
        else
        {
          arrayOfInt[j] = 1;
        }
        arrayOfString2[j] = (m == 0 ? str : str.substring(m));
      }
      this.namesArray = arrayOfString2;
      this.urisArray = arrayOfString1;
      this.typesArray = arrayOfInt;
    }
    if (this.transletVersion > 101) {
      BasisLibrary.runTimeError("UNKNOWN_TRANSLET_VERSION_ERR", getClass().getName());
    }
  }
  
  public void setIndexSize(int paramInt)
  {
    if (paramInt > this._indexSize) {
      this._indexSize = paramInt;
    }
  }
  
  public KeyIndex createKeyIndex()
  {
    return new KeyIndex(this._indexSize);
  }
  
  public void buildKeyIndex(String paramString1, int paramInt, String paramString2)
  {
    KeyIndex localKeyIndex = buildKeyIndexHelper(paramString1);
    localKeyIndex.add(paramString2, paramInt, this._currentRootForKeys);
  }
  
  public void buildKeyIndex(String paramString, DOM paramDOM)
  {
    KeyIndex localKeyIndex = buildKeyIndexHelper(paramString);
    localKeyIndex.setDom(paramDOM, paramDOM.getDocument());
  }
  
  private KeyIndex buildKeyIndexHelper(String paramString)
  {
    if (this._keyIndexes == null) {
      this._keyIndexes = new HashMap();
    }
    KeyIndex localKeyIndex = (KeyIndex)this._keyIndexes.get(paramString);
    if (localKeyIndex == null) {
      this._keyIndexes.put(paramString, localKeyIndex = new KeyIndex(this._indexSize));
    }
    return localKeyIndex;
  }
  
  public KeyIndex getKeyIndex(String paramString)
  {
    if (this._keyIndexes == null) {
      return this._emptyKeyIndex = new KeyIndex(1);
    }
    KeyIndex localKeyIndex = (KeyIndex)this._keyIndexes.get(paramString);
    if (localKeyIndex == null) {
      return this._emptyKeyIndex = new KeyIndex(1);
    }
    return localKeyIndex;
  }
  
  private void setRootForKeys(int paramInt)
  {
    this._currentRootForKeys = paramInt;
  }
  
  public void buildKeys(DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler, int paramInt)
    throws TransletException
  {}
  
  public void setKeyIndexDom(String paramString, DOM paramDOM)
  {
    getKeyIndex(paramString).setDom(paramDOM, paramDOM.getDocument());
  }
  
  public void setDOMCache(DOMCache paramDOMCache)
  {
    this._domCache = paramDOMCache;
  }
  
  public DOMCache getDOMCache()
  {
    return this._domCache;
  }
  
  public SerializationHandler openOutputHandler(String paramString, boolean paramBoolean)
    throws TransletException
  {
    try
    {
      TransletOutputHandlerFactory localTransletOutputHandlerFactory = TransletOutputHandlerFactory.newInstance();
      String str = new File(paramString).getParent();
      if ((null != str) && (str.length() > 0))
      {
        localObject = new File(str);
        ((File)localObject).mkdirs();
      }
      localTransletOutputHandlerFactory.setEncoding(this._encoding);
      localTransletOutputHandlerFactory.setOutputMethod(this._method);
      localTransletOutputHandlerFactory.setOutputStream(new BufferedOutputStream(new FileOutputStream(paramString, paramBoolean)));
      localTransletOutputHandlerFactory.setOutputType(0);
      Object localObject = localTransletOutputHandlerFactory.getSerializationHandler();
      transferOutputSettings((SerializationHandler)localObject);
      ((SerializationHandler)localObject).startDocument();
      return localObject;
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  public SerializationHandler openOutputHandler(String paramString)
    throws TransletException
  {
    return openOutputHandler(paramString, false);
  }
  
  public void closeOutputHandler(SerializationHandler paramSerializationHandler)
  {
    try
    {
      paramSerializationHandler.endDocument();
      paramSerializationHandler.close();
    }
    catch (Exception localException) {}
  }
  
  public abstract void transform(DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException;
  
  public final void transform(DOM paramDOM, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    try
    {
      transform(paramDOM, paramDOM.getIterator(), paramSerializationHandler);
      this._keyIndexes = null;
    }
    finally
    {
      this._keyIndexes = null;
    }
  }
  
  public final void characters(String paramString, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramString != null) {
      try
      {
        paramSerializationHandler.characters(paramString);
      }
      catch (Exception localException)
      {
        throw new TransletException(localException);
      }
    }
  }
  
  public void addCdataElement(String paramString)
  {
    if (this._cdata == null) {
      this._cdata = new Vector();
    }
    int i = paramString.lastIndexOf(':');
    if (i > 0)
    {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      this._cdata.addElement(str1);
      this._cdata.addElement(str2);
    }
    else
    {
      this._cdata.addElement(null);
      this._cdata.addElement(paramString);
    }
  }
  
  protected void transferOutputSettings(SerializationHandler paramSerializationHandler)
  {
    if (this._method != null)
    {
      if (this._method.equals("xml"))
      {
        if (this._standalone != null) {
          paramSerializationHandler.setStandalone(this._standalone);
        }
        if (this._omitHeader) {
          paramSerializationHandler.setOmitXMLDeclaration(true);
        }
        paramSerializationHandler.setCdataSectionElements(this._cdata);
        if (this._version != null) {
          paramSerializationHandler.setVersion(this._version);
        }
        paramSerializationHandler.setIndent(this._indent);
        paramSerializationHandler.setIndentAmount(this._indentamount);
        if (this._doctypeSystem != null) {
          paramSerializationHandler.setDoctype(this._doctypeSystem, this._doctypePublic);
        }
        paramSerializationHandler.setIsStandalone(this._isStandalone);
      }
      else if (this._method.equals("html"))
      {
        paramSerializationHandler.setIndent(this._indent);
        paramSerializationHandler.setDoctype(this._doctypeSystem, this._doctypePublic);
        if (this._mediaType != null) {
          paramSerializationHandler.setMediaType(this._mediaType);
        }
      }
    }
    else
    {
      paramSerializationHandler.setCdataSectionElements(this._cdata);
      if (this._version != null) {
        paramSerializationHandler.setVersion(this._version);
      }
      if (this._standalone != null) {
        paramSerializationHandler.setStandalone(this._standalone);
      }
      if (this._omitHeader) {
        paramSerializationHandler.setOmitXMLDeclaration(true);
      }
      paramSerializationHandler.setIndent(this._indent);
      paramSerializationHandler.setDoctype(this._doctypeSystem, this._doctypePublic);
      paramSerializationHandler.setIsStandalone(this._isStandalone);
    }
  }
  
  public void addAuxiliaryClass(Class paramClass)
  {
    if (this._auxClasses == null) {
      this._auxClasses = new HashMap();
    }
    this._auxClasses.put(paramClass.getName(), paramClass);
  }
  
  public void setAuxiliaryClasses(Map<String, Class<?>> paramMap)
  {
    this._auxClasses = paramMap;
  }
  
  public Class getAuxiliaryClass(String paramString)
  {
    if (this._auxClasses == null) {
      return null;
    }
    return (Class)this._auxClasses.get(paramString);
  }
  
  public String[] getNamesArray()
  {
    return this.namesArray;
  }
  
  public String[] getUrisArray()
  {
    return this.urisArray;
  }
  
  public int[] getTypesArray()
  {
    return this.typesArray;
  }
  
  public String[] getNamespaceArray()
  {
    return this.namespaceArray;
  }
  
  public boolean hasIdCall()
  {
    return this._hasIdCall;
  }
  
  public Templates getTemplates()
  {
    return this._templates;
  }
  
  public void setTemplates(Templates paramTemplates)
  {
    this._templates = paramTemplates;
  }
  
  public boolean useServicesMechnism()
  {
    return this._useServicesMechanism;
  }
  
  public void setServicesMechnism(boolean paramBoolean)
  {
    this._useServicesMechanism = paramBoolean;
  }
  
  public String getAllowedProtocols()
  {
    return this._accessExternalStylesheet;
  }
  
  public void setAllowedProtocols(String paramString)
  {
    this._accessExternalStylesheet = paramString;
  }
  
  public Document newDocument(String paramString1, String paramString2)
    throws ParserConfigurationException
  {
    if (this._domImplementation == null)
    {
      DocumentBuilderFactory localDocumentBuilderFactory = FactoryImpl.getDOMFactory(this._useServicesMechanism);
      this._domImplementation = localDocumentBuilderFactory.newDocumentBuilder().getDOMImplementation();
    }
    return this._domImplementation.createDocument(paramString1, paramString2, null);
  }
}
