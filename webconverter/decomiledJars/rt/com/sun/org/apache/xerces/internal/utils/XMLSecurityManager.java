package com.sun.org.apache.xerces.internal.utils;

import com.sun.org.apache.xerces.internal.util.SecurityManager;

public final class XMLSecurityManager
{
  private static final int NO_LIMIT = 0;
  private final int[] values = new int[Limit.values().length];
  private State[] states = new State[Limit.values().length];
  boolean secureProcessing;
  private boolean[] isSet = new boolean[Limit.values().length];
  private final int indexEntityCountInfo = 10000;
  private String printEntityCountInfo = "";
  
  public XMLSecurityManager()
  {
    this(false);
  }
  
  public XMLSecurityManager(boolean paramBoolean)
  {
    this.secureProcessing = paramBoolean;
    for (Limit localLimit : Limit.values()) {
      if (paramBoolean)
      {
        this.values[localLimit.ordinal()] = localLimit.secureValue;
        this.states[localLimit.ordinal()] = State.FSP;
      }
      else
      {
        this.values[localLimit.ordinal()] = localLimit.defaultValue();
        this.states[localLimit.ordinal()] = State.DEFAULT;
      }
    }
    readSystemProperties();
  }
  
  public void setSecureProcessing(boolean paramBoolean)
  {
    this.secureProcessing = paramBoolean;
    for (Limit localLimit : Limit.values()) {
      if (paramBoolean) {
        setLimit(localLimit.ordinal(), State.FSP, localLimit.secureValue());
      } else {
        setLimit(localLimit.ordinal(), State.FSP, localLimit.defaultValue());
      }
    }
  }
  
  public boolean isSecureProcessing()
  {
    return this.secureProcessing;
  }
  
  public boolean setLimit(String paramString, State paramState, Object paramObject)
  {
    int i = getIndex(paramString);
    if (i > -1)
    {
      setLimit(i, paramState, paramObject);
      return true;
    }
    return false;
  }
  
  public void setLimit(Limit paramLimit, State paramState, int paramInt)
  {
    setLimit(paramLimit.ordinal(), paramState, paramInt);
  }
  
  public void setLimit(int paramInt, State paramState, Object paramObject)
  {
    if (paramInt == 10000)
    {
      this.printEntityCountInfo = ((String)paramObject);
    }
    else
    {
      int i;
      if (Integer.class.isAssignableFrom(paramObject.getClass()))
      {
        i = ((Integer)paramObject).intValue();
      }
      else
      {
        i = Integer.parseInt((String)paramObject);
        if (i < 0) {
          i = 0;
        }
      }
      setLimit(paramInt, paramState, i);
    }
  }
  
  public void setLimit(int paramInt1, State paramState, int paramInt2)
  {
    if (paramInt1 == 10000)
    {
      this.printEntityCountInfo = "yes";
    }
    else if (paramState.compareTo(this.states[paramInt1]) >= 0)
    {
      this.values[paramInt1] = paramInt2;
      this.states[paramInt1] = paramState;
      this.isSet[paramInt1] = true;
    }
  }
  
  public String getLimitAsString(String paramString)
  {
    int i = getIndex(paramString);
    if (i > -1) {
      return getLimitValueByIndex(i);
    }
    return null;
  }
  
  public int getLimit(Limit paramLimit)
  {
    return this.values[paramLimit.ordinal()];
  }
  
  public String getLimitValueAsString(Limit paramLimit)
  {
    return Integer.toString(this.values[paramLimit.ordinal()]);
  }
  
  public String getLimitValueByIndex(int paramInt)
  {
    if (paramInt == 10000) {
      return this.printEntityCountInfo;
    }
    return Integer.toString(this.values[paramInt]);
  }
  
  public State getState(Limit paramLimit)
  {
    return this.states[paramLimit.ordinal()];
  }
  
  public String getStateLiteral(Limit paramLimit)
  {
    return this.states[paramLimit.ordinal()].literal();
  }
  
  public int getIndex(String paramString)
  {
    for (Limit localLimit : ) {
      if (localLimit.equalsAPIPropertyName(paramString)) {
        return localLimit.ordinal();
      }
    }
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo")) {
      return 10000;
    }
    return -1;
  }
  
  public boolean isNoLimit(int paramInt)
  {
    return paramInt == 0;
  }
  
  public boolean isOverLimit(Limit paramLimit, String paramString, int paramInt, XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    return isOverLimit(paramLimit.ordinal(), paramString, paramInt, paramXMLLimitAnalyzer);
  }
  
  public boolean isOverLimit(int paramInt1, String paramString, int paramInt2, XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    if (this.values[paramInt1] == 0) {
      return false;
    }
    if (paramInt2 > this.values[paramInt1])
    {
      paramXMLLimitAnalyzer.addValue(paramInt1, paramString, paramInt2);
      return true;
    }
    return false;
  }
  
  public boolean isOverLimit(Limit paramLimit, XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    return isOverLimit(paramLimit.ordinal(), paramXMLLimitAnalyzer);
  }
  
  public boolean isOverLimit(int paramInt, XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    if (this.values[paramInt] == 0) {
      return false;
    }
    if ((paramInt == Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal()) || (paramInt == Limit.ENTITY_EXPANSION_LIMIT.ordinal()) || (paramInt == Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()) || (paramInt == Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal()) || (paramInt == Limit.MAX_NAME_LIMIT.ordinal())) {
      return paramXMLLimitAnalyzer.getTotalValue(paramInt) > this.values[paramInt];
    }
    return paramXMLLimitAnalyzer.getValue(paramInt) > this.values[paramInt];
  }
  
  public void debugPrint(XMLLimitAnalyzer paramXMLLimitAnalyzer)
  {
    if (this.printEntityCountInfo.equals("yes")) {
      paramXMLLimitAnalyzer.debugPrint(this);
    }
  }
  
  public boolean isSet(int paramInt)
  {
    return this.isSet[paramInt];
  }
  
  public boolean printEntityCountInfo()
  {
    return this.printEntityCountInfo.equals("yes");
  }
  
  private void readSystemProperties()
  {
    for (Limit localLimit : ) {
      if (!getSystemProperty(localLimit, localLimit.systemProperty())) {
        for (NameMap localNameMap : NameMap.values())
        {
          String str = localNameMap.getOldName(localLimit.systemProperty());
          if (str != null) {
            getSystemProperty(localLimit, str);
          }
        }
      }
    }
  }
  
  private boolean getSystemProperty(Limit paramLimit, String paramString)
  {
    try
    {
      String str = SecuritySupport.getSystemProperty(paramString);
      if ((str != null) && (!str.equals("")))
      {
        this.values[paramLimit.ordinal()] = Integer.parseInt(str);
        this.states[paramLimit.ordinal()] = State.SYSTEMPROPERTY;
        return true;
      }
      str = SecuritySupport.readJAXPProperty(paramString);
      if ((str != null) && (!str.equals("")))
      {
        this.values[paramLimit.ordinal()] = Integer.parseInt(str);
        this.states[paramLimit.ordinal()] = State.JAXPDOTPROPERTIES;
        return true;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new NumberFormatException("Invalid setting for system property: " + paramLimit.systemProperty());
    }
    return false;
  }
  
  public static XMLSecurityManager convert(Object paramObject, XMLSecurityManager paramXMLSecurityManager)
  {
    if (paramObject == null)
    {
      if (paramXMLSecurityManager == null) {
        paramXMLSecurityManager = new XMLSecurityManager(true);
      }
      return paramXMLSecurityManager;
    }
    if (XMLSecurityManager.class.isAssignableFrom(paramObject.getClass())) {
      return (XMLSecurityManager)paramObject;
    }
    if (paramXMLSecurityManager == null) {
      paramXMLSecurityManager = new XMLSecurityManager(true);
    }
    if (SecurityManager.class.isAssignableFrom(paramObject.getClass()))
    {
      SecurityManager localSecurityManager = (SecurityManager)paramObject;
      paramXMLSecurityManager.setLimit(Limit.MAX_OCCUR_NODE_LIMIT, State.APIPROPERTY, localSecurityManager.getMaxOccurNodeLimit());
      paramXMLSecurityManager.setLimit(Limit.ENTITY_EXPANSION_LIMIT, State.APIPROPERTY, localSecurityManager.getEntityExpansionLimit());
      paramXMLSecurityManager.setLimit(Limit.ELEMENT_ATTRIBUTE_LIMIT, State.APIPROPERTY, localSecurityManager.getElementAttrLimit());
    }
    return paramXMLSecurityManager;
  }
  
  public static enum Limit
  {
    ENTITY_EXPANSION_LIMIT("EntityExpansionLimit", "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "jdk.xml.entityExpansionLimit", 0, 64000),  MAX_OCCUR_NODE_LIMIT("MaxOccurLimit", "http://www.oracle.com/xml/jaxp/properties/maxOccurLimit", "jdk.xml.maxOccurLimit", 0, 5000),  ELEMENT_ATTRIBUTE_LIMIT("ElementAttributeLimit", "http://www.oracle.com/xml/jaxp/properties/elementAttributeLimit", "jdk.xml.elementAttributeLimit", 0, 10000),  TOTAL_ENTITY_SIZE_LIMIT("TotalEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "jdk.xml.totalEntitySizeLimit", 0, 50000000),  GENERAL_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxGeneralEntitySizeLimit", "jdk.xml.maxGeneralEntitySizeLimit", 0, 0),  PARAMETER_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxParameterEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", 0, 1000000),  MAX_ELEMENT_DEPTH_LIMIT("MaxElementDepthLimit", "http://www.oracle.com/xml/jaxp/properties/maxElementDepth", "jdk.xml.maxElementDepth", 0, 0),  MAX_NAME_LIMIT("MaxXMLNameLimit", "http://www.oracle.com/xml/jaxp/properties/maxXMLNameLimit", "jdk.xml.maxXMLNameLimit", 1000, 1000);
    
    final String key;
    final String apiProperty;
    final String systemProperty;
    final int defaultValue;
    final int secureValue;
    
    private Limit(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2)
    {
      this.key = paramString1;
      this.apiProperty = paramString2;
      this.systemProperty = paramString3;
      this.defaultValue = paramInt1;
      this.secureValue = paramInt2;
    }
    
    public boolean equalsAPIPropertyName(String paramString)
    {
      return paramString == null ? false : this.apiProperty.equals(paramString);
    }
    
    public boolean equalsSystemPropertyName(String paramString)
    {
      return paramString == null ? false : this.systemProperty.equals(paramString);
    }
    
    public String key()
    {
      return this.key;
    }
    
    public String apiProperty()
    {
      return this.apiProperty;
    }
    
    String systemProperty()
    {
      return this.systemProperty;
    }
    
    public int defaultValue()
    {
      return this.defaultValue;
    }
    
    int secureValue()
    {
      return this.secureValue;
    }
  }
  
  public static enum NameMap
  {
    ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"),  MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"),  ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");
    
    final String newName;
    final String oldName;
    
    private NameMap(String paramString1, String paramString2)
    {
      this.newName = paramString1;
      this.oldName = paramString2;
    }
    
    String getOldName(String paramString)
    {
      if (paramString.equals(this.newName)) {
        return this.oldName;
      }
      return null;
    }
  }
  
  public static enum State
  {
    DEFAULT("default"),  FSP("FEATURE_SECURE_PROCESSING"),  JAXPDOTPROPERTIES("jaxp.properties"),  SYSTEMPROPERTY("system property"),  APIPROPERTY("property");
    
    final String literal;
    
    private State(String paramString)
    {
      this.literal = paramString;
    }
    
    String literal()
    {
      return this.literal;
    }
  }
}
