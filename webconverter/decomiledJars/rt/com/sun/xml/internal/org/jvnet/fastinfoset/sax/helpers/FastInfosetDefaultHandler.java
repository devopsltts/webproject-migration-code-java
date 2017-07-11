package com.sun.xml.internal.org.jvnet.fastinfoset.sax.helpers;

import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class FastInfosetDefaultHandler
  extends DefaultHandler
  implements LexicalHandler, EncodingAlgorithmContentHandler, PrimitiveTypeContentHandler
{
  public FastInfosetDefaultHandler() {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void octets(String paramString, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SAXException
  {}
  
  public void object(String paramString, int paramInt, Object paramObject)
    throws SAXException
  {}
  
  public void booleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void shorts(short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void ints(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void longs(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void floats(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void doubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void uuids(long[] paramArrayOfLong, int paramInt1, int paramInt2)
    throws SAXException
  {}
}
