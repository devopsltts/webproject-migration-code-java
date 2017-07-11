package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MonthDV
  extends AbstractDateTimeDV
{
  public MonthDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "gMonth" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    localDateTimeData.year = 2000;
    localDateTimeData.day = 1;
    if ((paramString.charAt(0) != '-') || (paramString.charAt(1) != '-')) {
      throw new SchemaDateTimeException("Invalid format for gMonth: " + paramString);
    }
    int j = 4;
    localDateTimeData.month = parseInt(paramString, 2, j);
    if ((paramString.length() >= j + 2) && (paramString.charAt(j) == '-') && (paramString.charAt(j + 1) == '-')) {
      j += 2;
    }
    if (j < i)
    {
      if (!isNextCharUTCSign(paramString, j, i)) {
        throw new SchemaDateTimeException("Error in month parsing: " + paramString);
      }
      getTimeZone(paramString, localDateTimeData, j, i);
    }
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((localDateTimeData.utc != 0) && (localDateTimeData.utc != 90)) {
      normalize(localDateTimeData);
    }
    localDateTimeData.position = 1;
    return localDateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    StringBuffer localStringBuffer = new StringBuffer(5);
    localStringBuffer.append('-');
    localStringBuffer.append('-');
    append(localStringBuffer, paramDateTimeData.month, 2);
    append(localStringBuffer, (char)paramDateTimeData.utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, paramDateTimeData.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, paramDateTimeData.hasTimeZone() ? paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin : Integer.MIN_VALUE);
  }
}
