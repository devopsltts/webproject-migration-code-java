package com.sun.xml.internal.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class NameUtil
{
  protected static final int UPPER_LETTER = 0;
  protected static final int LOWER_LETTER = 1;
  protected static final int OTHER_LETTER = 2;
  protected static final int DIGIT = 3;
  protected static final int OTHER = 4;
  private static final byte[] actionTable = new byte[25];
  private static final byte ACTION_CHECK_PUNCT = 0;
  private static final byte ACTION_CHECK_C2 = 1;
  private static final byte ACTION_BREAK = 2;
  private static final byte ACTION_NOBREAK = 3;
  
  NameUtil() {}
  
  protected boolean isPunct(char paramChar)
  {
    return (paramChar == '-') || (paramChar == '.') || (paramChar == ':') || (paramChar == '_') || (paramChar == '·') || (paramChar == '·') || (paramChar == '۝') || (paramChar == '۞');
  }
  
  protected static boolean isDigit(char paramChar)
  {
    return ((paramChar >= '0') && (paramChar <= '9')) || (Character.isDigit(paramChar));
  }
  
  protected static boolean isUpper(char paramChar)
  {
    return ((paramChar >= 'A') && (paramChar <= 'Z')) || (Character.isUpperCase(paramChar));
  }
  
  protected static boolean isLower(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || (Character.isLowerCase(paramChar));
  }
  
  protected boolean isLetter(char paramChar)
  {
    return ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= 'a') && (paramChar <= 'z')) || (Character.isLetter(paramChar));
  }
  
  private String toLowerCase(String paramString)
  {
    return paramString.toLowerCase(Locale.ENGLISH);
  }
  
  private String toUpperCase(char paramChar)
  {
    return String.valueOf(paramChar).toUpperCase(Locale.ENGLISH);
  }
  
  private String toUpperCase(String paramString)
  {
    return paramString.toUpperCase(Locale.ENGLISH);
  }
  
  public String capitalize(String paramString)
  {
    if (!isLower(paramString.charAt(0))) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    localStringBuilder.append(toUpperCase(paramString.charAt(0)));
    localStringBuilder.append(toLowerCase(paramString.substring(1)));
    return localStringBuilder.toString();
  }
  
  private int nextBreak(String paramString, int paramInt)
  {
    int i = paramString.length();
    char c1 = paramString.charAt(paramInt);
    int j = classify(c1);
    for (int k = paramInt + 1; k < i; k++)
    {
      int m = j;
      c1 = paramString.charAt(k);
      j = classify(c1);
      switch (actionTable[(m * 5 + j)])
      {
      case 0: 
        if (isPunct(c1)) {
          return k;
        }
        break;
      case 1: 
        if (k < i - 1)
        {
          char c2 = paramString.charAt(k + 1);
          if (isLower(c2)) {
            return k;
          }
        }
        break;
      case 2: 
        return k;
      }
    }
    return -1;
  }
  
  private static byte decideAction(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 4) && (paramInt2 == 4)) {
      return 0;
    }
    if (!xor(paramInt1 == 3, paramInt2 == 3)) {
      return 2;
    }
    if ((paramInt1 == 1) && (paramInt2 != 1)) {
      return 2;
    }
    if (!xor(paramInt1 <= 2, paramInt2 <= 2)) {
      return 2;
    }
    if (!xor(paramInt1 == 2, paramInt2 == 2)) {
      return 2;
    }
    if ((paramInt1 == 0) && (paramInt2 == 0)) {
      return 1;
    }
    return 3;
  }
  
  private static boolean xor(boolean paramBoolean1, boolean paramBoolean2)
  {
    return ((paramBoolean1) && (paramBoolean2)) || ((!paramBoolean1) && (!paramBoolean2));
  }
  
  protected int classify(char paramChar)
  {
    switch (Character.getType(paramChar))
    {
    case 1: 
      return 0;
    case 2: 
      return 1;
    case 3: 
    case 4: 
    case 5: 
      return 2;
    case 9: 
      return 3;
    }
    return 4;
  }
  
  public List<String> toWordList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramString.length();
    int k;
    for (int j = 0; j < i; j = k)
    {
      while ((j < i) && (isPunct(paramString.charAt(j)))) {
        j++;
      }
      if (j >= i) {
        break;
      }
      k = nextBreak(paramString, j);
      String str = k == -1 ? paramString.substring(j) : paramString.substring(j, k);
      localArrayList.add(escape(capitalize(str)));
      if (k == -1) {
        break;
      }
    }
    return localArrayList;
  }
  
  protected String toMixedCaseName(List<String> paramList, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!paramList.isEmpty())
    {
      localStringBuilder.append(paramBoolean ? (String)paramList.get(0) : toLowerCase((String)paramList.get(0)));
      for (int i = 1; i < paramList.size(); i++) {
        localStringBuilder.append((String)paramList.get(i));
      }
    }
    return localStringBuilder.toString();
  }
  
  protected String toMixedCaseVariableName(String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {
      for (int i = 1; i < paramArrayOfString.length; i++) {
        paramArrayOfString[i] = capitalize(paramArrayOfString[i]);
      }
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramArrayOfString.length > 0)
    {
      localStringBuilder.append(paramBoolean1 ? paramArrayOfString[0] : toLowerCase(paramArrayOfString[0]));
      for (int j = 1; j < paramArrayOfString.length; j++) {
        localStringBuilder.append(paramArrayOfString[j]);
      }
    }
    return localStringBuilder.toString();
  }
  
  public String toConstantName(String paramString)
  {
    return toConstantName(toWordList(paramString));
  }
  
  public String toConstantName(List<String> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (!paramList.isEmpty())
    {
      localStringBuilder.append(toUpperCase((String)paramList.get(0)));
      for (int i = 1; i < paramList.size(); i++)
      {
        localStringBuilder.append('_');
        localStringBuilder.append(toUpperCase((String)paramList.get(i)));
      }
    }
    return localStringBuilder.toString();
  }
  
  public static void escape(StringBuilder paramStringBuilder, String paramString, int paramInt)
  {
    int i = paramString.length();
    for (int j = paramInt; j < i; j++)
    {
      char c = paramString.charAt(j);
      if (Character.isJavaIdentifierPart(c))
      {
        paramStringBuilder.append(c);
      }
      else
      {
        paramStringBuilder.append('_');
        if (c <= '\017') {
          paramStringBuilder.append("000");
        } else if (c <= 'ÿ') {
          paramStringBuilder.append("00");
        } else if (c <= '࿿') {
          paramStringBuilder.append('0');
        }
        paramStringBuilder.append(Integer.toString(c, 16));
      }
    }
  }
  
  private static String escape(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      if (!Character.isJavaIdentifierPart(paramString.charAt(j)))
      {
        StringBuilder localStringBuilder = new StringBuilder(paramString.substring(0, j));
        escape(localStringBuilder, paramString, j);
        return localStringBuilder.toString();
      }
    }
    return paramString;
  }
  
  static
  {
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        actionTable[(i * 5 + j)] = decideAction(i, j);
      }
    }
  }
}
