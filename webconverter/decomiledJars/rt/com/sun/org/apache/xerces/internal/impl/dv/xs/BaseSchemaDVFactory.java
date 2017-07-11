package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.SchemaDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.impl.xs.XSDeclarationPool;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class BaseSchemaDVFactory
  extends SchemaDVFactory
{
  static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
  protected XSDeclarationPool fDeclPool = null;
  
  public BaseSchemaDVFactory() {}
  
  protected static void createBuiltInTypes(SymbolHash paramSymbolHash, XSSimpleTypeDecl paramXSSimpleTypeDecl)
  {
    String str1 = "anySimpleType";
    String str2 = "anyURI";
    String str3 = "base64Binary";
    String str4 = "boolean";
    String str5 = "byte";
    String str6 = "date";
    String str7 = "dateTime";
    String str8 = "gDay";
    String str9 = "decimal";
    String str10 = "double";
    String str11 = "duration";
    String str12 = "ENTITY";
    String str13 = "ENTITIES";
    String str14 = "float";
    String str15 = "hexBinary";
    String str16 = "ID";
    String str17 = "IDREF";
    String str18 = "IDREFS";
    String str19 = "int";
    String str20 = "integer";
    String str21 = "long";
    String str22 = "Name";
    String str23 = "negativeInteger";
    String str24 = "gMonth";
    String str25 = "gMonthDay";
    String str26 = "NCName";
    String str27 = "NMTOKEN";
    String str28 = "NMTOKENS";
    String str29 = "language";
    String str30 = "nonNegativeInteger";
    String str31 = "nonPositiveInteger";
    String str32 = "normalizedString";
    String str33 = "NOTATION";
    String str34 = "positiveInteger";
    String str35 = "QName";
    String str36 = "short";
    String str37 = "string";
    String str38 = "time";
    String str39 = "token";
    String str40 = "unsignedByte";
    String str41 = "unsignedInt";
    String str42 = "unsignedLong";
    String str43 = "unsignedShort";
    String str44 = "gYear";
    String str45 = "gYearMonth";
    XSFacets localXSFacets = new XSFacets();
    paramSymbolHash.put("anySimpleType", XSSimpleTypeDecl.fAnySimpleType);
    XSSimpleTypeDecl localXSSimpleTypeDecl1 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "string", (short)1, (short)0, false, false, false, true, (short)2);
    paramSymbolHash.put("string", localXSSimpleTypeDecl1);
    paramSymbolHash.put("boolean", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "boolean", (short)2, (short)0, false, true, false, true, (short)3));
    XSSimpleTypeDecl localXSSimpleTypeDecl2 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "decimal", (short)3, (short)2, false, false, true, true, (short)4);
    paramSymbolHash.put("decimal", localXSSimpleTypeDecl2);
    paramSymbolHash.put("anyURI", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "anyURI", (short)17, (short)0, false, false, false, true, (short)18));
    paramSymbolHash.put("base64Binary", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "base64Binary", (short)16, (short)0, false, false, false, true, (short)17));
    XSSimpleTypeDecl localXSSimpleTypeDecl3 = new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "duration", (short)6, (short)1, false, false, false, true, (short)7);
    paramSymbolHash.put("duration", localXSSimpleTypeDecl3);
    paramSymbolHash.put("dateTime", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "dateTime", (short)7, (short)1, false, false, false, true, (short)8));
    paramSymbolHash.put("time", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "time", (short)8, (short)1, false, false, false, true, (short)9));
    paramSymbolHash.put("date", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "date", (short)9, (short)1, false, false, false, true, (short)10));
    paramSymbolHash.put("gYearMonth", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gYearMonth", (short)10, (short)1, false, false, false, true, (short)11));
    paramSymbolHash.put("gYear", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gYear", (short)11, (short)1, false, false, false, true, (short)12));
    paramSymbolHash.put("gMonthDay", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gMonthDay", (short)12, (short)1, false, false, false, true, (short)13));
    paramSymbolHash.put("gDay", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gDay", (short)13, (short)1, false, false, false, true, (short)14));
    paramSymbolHash.put("gMonth", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "gMonth", (short)14, (short)1, false, false, false, true, (short)15));
    XSSimpleTypeDecl localXSSimpleTypeDecl4 = new XSSimpleTypeDecl(localXSSimpleTypeDecl2, "integer", (short)24, (short)2, false, false, true, true, (short)30);
    paramSymbolHash.put("integer", localXSSimpleTypeDecl4);
    localXSFacets.maxInclusive = "0";
    XSSimpleTypeDecl localXSSimpleTypeDecl5 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "nonPositiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)31);
    localXSSimpleTypeDecl5.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("nonPositiveInteger", localXSSimpleTypeDecl5);
    localXSFacets.maxInclusive = "-1";
    XSSimpleTypeDecl localXSSimpleTypeDecl6 = new XSSimpleTypeDecl(localXSSimpleTypeDecl5, "negativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)32);
    localXSSimpleTypeDecl6.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("negativeInteger", localXSSimpleTypeDecl6);
    localXSFacets.maxInclusive = "9223372036854775807";
    localXSFacets.minInclusive = "-9223372036854775808";
    XSSimpleTypeDecl localXSSimpleTypeDecl7 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "long", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)33);
    localXSSimpleTypeDecl7.applyFacets1(localXSFacets, (short)288, (short)0);
    paramSymbolHash.put("long", localXSSimpleTypeDecl7);
    localXSFacets.maxInclusive = "2147483647";
    localXSFacets.minInclusive = "-2147483648";
    XSSimpleTypeDecl localXSSimpleTypeDecl8 = new XSSimpleTypeDecl(localXSSimpleTypeDecl7, "int", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)34);
    localXSSimpleTypeDecl8.applyFacets1(localXSFacets, (short)288, (short)0);
    paramSymbolHash.put("int", localXSSimpleTypeDecl8);
    localXSFacets.maxInclusive = "32767";
    localXSFacets.minInclusive = "-32768";
    XSSimpleTypeDecl localXSSimpleTypeDecl9 = new XSSimpleTypeDecl(localXSSimpleTypeDecl8, "short", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)35);
    localXSSimpleTypeDecl9.applyFacets1(localXSFacets, (short)288, (short)0);
    paramSymbolHash.put("short", localXSSimpleTypeDecl9);
    localXSFacets.maxInclusive = "127";
    localXSFacets.minInclusive = "-128";
    XSSimpleTypeDecl localXSSimpleTypeDecl10 = new XSSimpleTypeDecl(localXSSimpleTypeDecl9, "byte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)36);
    localXSSimpleTypeDecl10.applyFacets1(localXSFacets, (short)288, (short)0);
    paramSymbolHash.put("byte", localXSSimpleTypeDecl10);
    localXSFacets.minInclusive = "0";
    XSSimpleTypeDecl localXSSimpleTypeDecl11 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "nonNegativeInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)37);
    localXSSimpleTypeDecl11.applyFacets1(localXSFacets, (short)256, (short)0);
    paramSymbolHash.put("nonNegativeInteger", localXSSimpleTypeDecl11);
    localXSFacets.maxInclusive = "18446744073709551615";
    XSSimpleTypeDecl localXSSimpleTypeDecl12 = new XSSimpleTypeDecl(localXSSimpleTypeDecl11, "unsignedLong", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)38);
    localXSSimpleTypeDecl12.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedLong", localXSSimpleTypeDecl12);
    localXSFacets.maxInclusive = "4294967295";
    XSSimpleTypeDecl localXSSimpleTypeDecl13 = new XSSimpleTypeDecl(localXSSimpleTypeDecl12, "unsignedInt", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)39);
    localXSSimpleTypeDecl13.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedInt", localXSSimpleTypeDecl13);
    localXSFacets.maxInclusive = "65535";
    XSSimpleTypeDecl localXSSimpleTypeDecl14 = new XSSimpleTypeDecl(localXSSimpleTypeDecl13, "unsignedShort", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)40);
    localXSSimpleTypeDecl14.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedShort", localXSSimpleTypeDecl14);
    localXSFacets.maxInclusive = "255";
    XSSimpleTypeDecl localXSSimpleTypeDecl15 = new XSSimpleTypeDecl(localXSSimpleTypeDecl14, "unsignedByte", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)41);
    localXSSimpleTypeDecl15.applyFacets1(localXSFacets, (short)32, (short)0);
    paramSymbolHash.put("unsignedByte", localXSSimpleTypeDecl15);
    localXSFacets.minInclusive = "1";
    XSSimpleTypeDecl localXSSimpleTypeDecl16 = new XSSimpleTypeDecl(localXSSimpleTypeDecl11, "positiveInteger", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)42);
    localXSSimpleTypeDecl16.applyFacets1(localXSFacets, (short)256, (short)0);
    paramSymbolHash.put("positiveInteger", localXSSimpleTypeDecl16);
    paramSymbolHash.put("float", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "float", (short)4, (short)1, true, true, true, true, (short)5));
    paramSymbolHash.put("double", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "double", (short)5, (short)1, true, true, true, true, (short)6));
    paramSymbolHash.put("hexBinary", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
    paramSymbolHash.put("NOTATION", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
    localXSFacets.whiteSpace = 1;
    XSSimpleTypeDecl localXSSimpleTypeDecl17 = new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
    localXSSimpleTypeDecl17.applyFacets1(localXSFacets, (short)16, (short)0);
    paramSymbolHash.put("normalizedString", localXSSimpleTypeDecl17);
    localXSFacets.whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl18 = new XSSimpleTypeDecl(localXSSimpleTypeDecl17, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
    localXSSimpleTypeDecl18.applyFacets1(localXSFacets, (short)16, (short)0);
    paramSymbolHash.put("token", localXSSimpleTypeDecl18);
    localXSFacets.whiteSpace = 2;
    localXSFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
    XSSimpleTypeDecl localXSSimpleTypeDecl19 = new XSSimpleTypeDecl(localXSSimpleTypeDecl18, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
    localXSSimpleTypeDecl19.applyFacets1(localXSFacets, (short)24, (short)0);
    paramSymbolHash.put("language", localXSSimpleTypeDecl19);
    localXSFacets.whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl20 = new XSSimpleTypeDecl(localXSSimpleTypeDecl18, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
    localXSSimpleTypeDecl20.applyFacets1(localXSFacets, (short)16, (short)0, (short)2);
    paramSymbolHash.put("Name", localXSSimpleTypeDecl20);
    localXSFacets.whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl21 = new XSSimpleTypeDecl(localXSSimpleTypeDecl20, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
    localXSSimpleTypeDecl21.applyFacets1(localXSFacets, (short)16, (short)0, (short)3);
    paramSymbolHash.put("NCName", localXSSimpleTypeDecl21);
    paramSymbolHash.put("QName", new XSSimpleTypeDecl(paramXSSimpleTypeDecl, "QName", (short)18, (short)0, false, false, false, true, (short)19));
    paramSymbolHash.put("ID", new XSSimpleTypeDecl(localXSSimpleTypeDecl21, "ID", (short)21, (short)0, false, false, false, true, (short)27));
    XSSimpleTypeDecl localXSSimpleTypeDecl22 = new XSSimpleTypeDecl(localXSSimpleTypeDecl21, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
    paramSymbolHash.put("IDREF", localXSSimpleTypeDecl22);
    localXSFacets.minLength = 1;
    XSSimpleTypeDecl localXSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl22, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl24 = new XSSimpleTypeDecl(localXSSimpleTypeDecl23, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl24.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("IDREFS", localXSSimpleTypeDecl24);
    XSSimpleTypeDecl localXSSimpleTypeDecl25 = new XSSimpleTypeDecl(localXSSimpleTypeDecl21, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
    paramSymbolHash.put("ENTITY", localXSSimpleTypeDecl25);
    localXSFacets.minLength = 1;
    localXSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl25, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl26 = new XSSimpleTypeDecl(localXSSimpleTypeDecl23, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl26.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("ENTITIES", localXSSimpleTypeDecl26);
    localXSFacets.whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl27 = new XSSimpleTypeDecl(localXSSimpleTypeDecl18, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
    localXSSimpleTypeDecl27.applyFacets1(localXSFacets, (short)16, (short)0, (short)1);
    paramSymbolHash.put("NMTOKEN", localXSSimpleTypeDecl27);
    localXSFacets.minLength = 1;
    localXSSimpleTypeDecl23 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl27, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl28 = new XSSimpleTypeDecl(localXSSimpleTypeDecl23, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl28.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("NMTOKENS", localXSSimpleTypeDecl28);
  }
  
  public XSSimpleType createTypeRestriction(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList)
  {
    if (this.fDeclPool != null)
    {
      XSSimpleTypeDecl localXSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return localXSSimpleTypeDecl.setRestrictionValues((XSSimpleTypeDecl)paramXSSimpleType, paramString1, paramString2, paramShort, paramXSObjectList);
    }
    return new XSSimpleTypeDecl((XSSimpleTypeDecl)paramXSSimpleType, paramString1, paramString2, paramShort, false, paramXSObjectList);
  }
  
  public XSSimpleType createTypeList(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList)
  {
    if (this.fDeclPool != null)
    {
      XSSimpleTypeDecl localXSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return localXSSimpleTypeDecl.setListValues(paramString1, paramString2, paramShort, (XSSimpleTypeDecl)paramXSSimpleType, paramXSObjectList);
    }
    return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, (XSSimpleTypeDecl)paramXSSimpleType, false, paramXSObjectList);
  }
  
  public XSSimpleType createTypeUnion(String paramString1, String paramString2, short paramShort, XSSimpleType[] paramArrayOfXSSimpleType, XSObjectList paramXSObjectList)
  {
    int i = paramArrayOfXSSimpleType.length;
    XSSimpleTypeDecl[] arrayOfXSSimpleTypeDecl = new XSSimpleTypeDecl[i];
    System.arraycopy(paramArrayOfXSSimpleType, 0, arrayOfXSSimpleTypeDecl, 0, i);
    if (this.fDeclPool != null)
    {
      XSSimpleTypeDecl localXSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
      return localXSSimpleTypeDecl.setUnionValues(paramString1, paramString2, paramShort, arrayOfXSSimpleTypeDecl, paramXSObjectList);
    }
    return new XSSimpleTypeDecl(paramString1, paramString2, paramShort, arrayOfXSSimpleTypeDecl, paramXSObjectList);
  }
  
  public void setDeclPool(XSDeclarationPool paramXSDeclarationPool)
  {
    this.fDeclPool = paramXSDeclarationPool;
  }
  
  public XSSimpleTypeDecl newXSSimpleTypeDecl()
  {
    return new XSSimpleTypeDecl();
  }
}
