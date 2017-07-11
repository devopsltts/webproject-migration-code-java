package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class YearDV
  extends AbstractDateTimeDV
{
  public YearDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gYear" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    int j = 0;
    if (paramString.charAt(0) == '-') {
      j = 1;
    }
    int k = findUTCSign(paramString, j, i);
    int m = (k == -1 ? i : k) - j;
    if (m < 4) {
      throw new RuntimeException("Year must have 'CCYY' format");
    }
    if ((m > 4) && (paramString.charAt(j) == '0')) {
      throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
    }
    if (k == -1)
    {
      localDateTimeData.year = parseIntYear(paramString, i);
    }
    else
    {
      localDateTimeData.year = parseIntYear(paramString, k);
      getTimeZone(paramString, localDateTimeData, k, i);
    }
    localDateTimeData.month = 1;
    localDateTimeData.day = 1;
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((localDateTimeData.utc != 0) && (localDateTimeData.utc != 90)) {
      normalize(localDateTimeData);
    }
    localDateTimeData.position = 0;
    return localDateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    StringBuffer localStringBuffer = new StringBuffer(5);
    append(localStringBuffer, paramDateTimeData.year, 4);
    append(localStringBuffer, (char)paramDateTimeData.utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(paramDateTimeData.unNormYear, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, paramDateTimeData.hasTimeZone() ? paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin : Integer.MIN_VALUE);
  }
}
