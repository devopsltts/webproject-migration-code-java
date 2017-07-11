package javax.swing.text;

import java.lang.reflect.Constructor;
import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.NumberFormat.Field;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JFormattedTextField;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class NumberFormatter
  extends InternationalFormatter
{
  private String specialChars;
  
  public NumberFormatter()
  {
    this(NumberFormat.getNumberInstance());
  }
  
  public NumberFormatter(NumberFormat paramNumberFormat)
  {
    super(paramNumberFormat);
    setFormat(paramNumberFormat);
    setAllowsInvalid(true);
    setCommitsOnValidEdit(false);
    setOverwriteMode(false);
  }
  
  public void setFormat(Format paramFormat)
  {
    super.setFormat(paramFormat);
    DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
    if (localDecimalFormatSymbols != null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(localDecimalFormatSymbols.getCurrencySymbol());
      localStringBuilder.append(localDecimalFormatSymbols.getDecimalSeparator());
      localStringBuilder.append(localDecimalFormatSymbols.getGroupingSeparator());
      localStringBuilder.append(localDecimalFormatSymbols.getInfinity());
      localStringBuilder.append(localDecimalFormatSymbols.getInternationalCurrencySymbol());
      localStringBuilder.append(localDecimalFormatSymbols.getMinusSign());
      localStringBuilder.append(localDecimalFormatSymbols.getMonetaryDecimalSeparator());
      localStringBuilder.append(localDecimalFormatSymbols.getNaN());
      localStringBuilder.append(localDecimalFormatSymbols.getPercent());
      localStringBuilder.append('+');
      this.specialChars = localStringBuilder.toString();
    }
    else
    {
      this.specialChars = "";
    }
  }
  
  Object stringToValue(String paramString, Format paramFormat)
    throws ParseException
  {
    if (paramFormat == null) {
      return paramString;
    }
    Object localObject = paramFormat.parseObject(paramString);
    return convertValueToValueClass(localObject, getValueClass());
  }
  
  private Object convertValueToValueClass(Object paramObject, Class paramClass)
  {
    if ((paramClass != null) && ((paramObject instanceof Number)))
    {
      Number localNumber = (Number)paramObject;
      if (paramClass == Integer.class) {
        return Integer.valueOf(localNumber.intValue());
      }
      if (paramClass == Long.class) {
        return Long.valueOf(localNumber.longValue());
      }
      if (paramClass == Float.class) {
        return Float.valueOf(localNumber.floatValue());
      }
      if (paramClass == Double.class) {
        return Double.valueOf(localNumber.doubleValue());
      }
      if (paramClass == Byte.class) {
        return Byte.valueOf(localNumber.byteValue());
      }
      if (paramClass == Short.class) {
        return Short.valueOf(localNumber.shortValue());
      }
    }
    return paramObject;
  }
  
  private char getPositiveSign()
  {
    return '+';
  }
  
  private char getMinusSign()
  {
    DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
    if (localDecimalFormatSymbols != null) {
      return localDecimalFormatSymbols.getMinusSign();
    }
    return '-';
  }
  
  private char getDecimalSeparator()
  {
    DecimalFormatSymbols localDecimalFormatSymbols = getDecimalFormatSymbols();
    if (localDecimalFormatSymbols != null) {
      return localDecimalFormatSymbols.getDecimalSeparator();
    }
    return '.';
  }
  
  private DecimalFormatSymbols getDecimalFormatSymbols()
  {
    Format localFormat = getFormat();
    if ((localFormat instanceof DecimalFormat)) {
      return ((DecimalFormat)localFormat).getDecimalFormatSymbols();
    }
    return null;
  }
  
  boolean isLegalInsertText(String paramString)
  {
    if (getAllowsInvalid()) {
      return true;
    }
    for (int i = paramString.length() - 1; i >= 0; i--)
    {
      char c = paramString.charAt(i);
      if ((!Character.isDigit(c)) && (this.specialChars.indexOf(c) == -1)) {
        return false;
      }
    }
    return true;
  }
  
  boolean isLiteral(Map paramMap)
  {
    if (!super.isLiteral(paramMap))
    {
      if (paramMap == null) {
        return false;
      }
      int i = paramMap.size();
      if (paramMap.get(NumberFormat.Field.GROUPING_SEPARATOR) != null)
      {
        i--;
        if (paramMap.get(NumberFormat.Field.INTEGER) != null) {
          i--;
        }
      }
      if (paramMap.get(NumberFormat.Field.EXPONENT_SYMBOL) != null) {
        i--;
      }
      if (paramMap.get(NumberFormat.Field.PERCENT) != null) {
        i--;
      }
      if (paramMap.get(NumberFormat.Field.PERMILLE) != null) {
        i--;
      }
      if (paramMap.get(NumberFormat.Field.CURRENCY) != null) {
        i--;
      }
      if (paramMap.get(NumberFormat.Field.SIGN) != null) {
        i--;
      }
      return i == 0;
    }
    return true;
  }
  
  boolean isNavigatable(int paramInt)
  {
    if (!super.isNavigatable(paramInt)) {
      return getBufferedChar(paramInt) == getDecimalSeparator();
    }
    return true;
  }
  
  private NumberFormat.Field getFieldFrom(int paramInt1, int paramInt2)
  {
    if (isValidMask())
    {
      int i = getFormattedTextField().getDocument().getLength();
      AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
      if (paramInt1 >= i) {
        paramInt1 += paramInt2;
      }
      while ((paramInt1 >= 0) && (paramInt1 < i))
      {
        localAttributedCharacterIterator.setIndex(paramInt1);
        Map localMap = localAttributedCharacterIterator.getAttributes();
        if ((localMap != null) && (localMap.size() > 0))
        {
          Iterator localIterator = localMap.keySet().iterator();
          while (localIterator.hasNext())
          {
            Object localObject = localIterator.next();
            if ((localObject instanceof NumberFormat.Field)) {
              return (NumberFormat.Field)localObject;
            }
          }
        }
        paramInt1 += paramInt2;
      }
    }
    return null;
  }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    if ((!getAllowsInvalid()) && (paramInt2 == 0) && (paramString != null) && (paramString.length() == 1) && (toggleSignIfNecessary(paramFilterBypass, paramInt1, paramString.charAt(0)))) {
      return;
    }
    super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  private boolean toggleSignIfNecessary(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, char paramChar)
    throws BadLocationException
  {
    if ((paramChar == getMinusSign()) || (paramChar == getPositiveSign()))
    {
      NumberFormat.Field localField = getFieldFrom(paramInt, -1);
      try
      {
        Object localObject;
        if ((localField == null) || ((localField != NumberFormat.Field.EXPONENT) && (localField != NumberFormat.Field.EXPONENT_SYMBOL) && (localField != NumberFormat.Field.EXPONENT_SIGN))) {
          localObject = toggleSign(paramChar == getPositiveSign());
        } else {
          localObject = toggleExponentSign(paramInt, paramChar);
        }
        if ((localObject != null) && (isValidValue(localObject, false)))
        {
          int i = getLiteralCountTo(paramInt);
          String str = valueToString(localObject);
          paramFilterBypass.remove(0, paramFilterBypass.getDocument().getLength());
          paramFilterBypass.insertString(0, str, null);
          updateValue(localObject);
          repositionCursor(getLiteralCountTo(paramInt) - i + paramInt, 1);
          return true;
        }
      }
      catch (ParseException localParseException)
      {
        invalidEdit();
      }
    }
    return false;
  }
  
  private Object toggleSign(boolean paramBoolean)
    throws ParseException
  {
    Object localObject = stringToValue(getFormattedTextField().getText());
    if (localObject != null)
    {
      String str = localObject.toString();
      if ((str != null) && (str.length() > 0))
      {
        if (paramBoolean)
        {
          if (str.charAt(0) == '-') {
            str = str.substring(1);
          }
        }
        else
        {
          if (str.charAt(0) == '+') {
            str = str.substring(1);
          }
          if ((str.length() > 0) && (str.charAt(0) != '-')) {
            str = "-" + str;
          }
        }
        if (str != null)
        {
          Class localClass = getValueClass();
          if (localClass == null) {
            localClass = localObject.getClass();
          }
          try
          {
            ReflectUtil.checkPackageAccess(localClass);
            SwingUtilities2.checkAccess(localClass.getModifiers());
            Constructor localConstructor = localClass.getConstructor(new Class[] { String.class });
            if (localConstructor != null)
            {
              SwingUtilities2.checkAccess(localConstructor.getModifiers());
              return localConstructor.newInstance(new Object[] { str });
            }
          }
          catch (Throwable localThrowable) {}
        }
      }
    }
    return null;
  }
  
  private Object toggleExponentSign(int paramInt, char paramChar)
    throws BadLocationException, ParseException
  {
    String str = getFormattedTextField().getText();
    int i = 0;
    int j = getAttributeStart(NumberFormat.Field.EXPONENT_SIGN);
    if (j >= 0)
    {
      i = 1;
      paramInt = j;
    }
    if (paramChar == getPositiveSign()) {
      str = getReplaceString(paramInt, i, null);
    } else {
      str = getReplaceString(paramInt, i, new String(new char[] { paramChar }));
    }
    return stringToValue(str);
  }
}
