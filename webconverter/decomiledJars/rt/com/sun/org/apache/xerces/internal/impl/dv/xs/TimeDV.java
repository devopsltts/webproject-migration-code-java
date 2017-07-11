package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class TimeDV
  extends AbstractDateTimeDV
{
  public TimeDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return parse(paramString);
    }
    catch (Exception localException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "time" });
    }
  }
  
  protected AbstractDateTimeDV.DateTimeData parse(String paramString)
    throws SchemaDateTimeException
  {
    AbstractDateTimeDV.DateTimeData localDateTimeData = new AbstractDateTimeDV.DateTimeData(paramString, this);
    int i = paramString.length();
    localDateTimeData.year = 2000;
    localDateTimeData.month = 1;
    localDateTimeData.day = 15;
    getTime(paramString, 0, i, localDateTimeData);
    validateDateTime(localDateTimeData);
    saveUnnormalized(localDateTimeData);
    if ((localDateTimeData.utc != 0) && (localDateTimeData.utc != 90)) {
      normalize(localDateTimeData);
    }
    localDateTimeData.position = 2;
    return localDateTimeData;
  }
  
  protected String dateToString(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    StringBuffer localStringBuffer = new StringBuffer(16);
    append(localStringBuffer, paramDateTimeData.hour, 2);
    localStringBuffer.append(':');
    append(localStringBuffer, paramDateTimeData.minute, 2);
    localStringBuffer.append(':');
    append(localStringBuffer, paramDateTimeData.second);
    append(localStringBuffer, (char)paramDateTimeData.utc, 0);
    return localStringBuffer.toString();
  }
  
  protected XMLGregorianCalendar getXMLGregorianCalendar(AbstractDateTimeDV.DateTimeData paramDateTimeData)
  {
    return datatypeFactory.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, paramDateTimeData.unNormHour, paramDateTimeData.unNormMinute, (int)paramDateTimeData.unNormSecond, paramDateTimeData.unNormSecond != 0.0D ? getFractionalSecondsAsBigDecimal(paramDateTimeData) : null, paramDateTimeData.hasTimeZone() ? paramDateTimeData.timezoneHr * 60 + paramDateTimeData.timezoneMin : Integer.MIN_VALUE);
  }
}
