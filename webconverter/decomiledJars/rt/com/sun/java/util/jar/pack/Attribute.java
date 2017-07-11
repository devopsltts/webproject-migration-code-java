package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Attribute
  implements Comparable<Attribute>
{
  Layout def;
  byte[] bytes;
  Object fixups;
  private static final Map<List<Attribute>, List<Attribute>> canonLists;
  private static final Map<Layout, Attribute> attributes;
  private static final Map<Layout, Attribute> standardDefs;
  static final byte EK_INT = 1;
  static final byte EK_BCI = 2;
  static final byte EK_BCO = 3;
  static final byte EK_FLAG = 4;
  static final byte EK_REPL = 5;
  static final byte EK_REF = 6;
  static final byte EK_UN = 7;
  static final byte EK_CASE = 8;
  static final byte EK_CALL = 9;
  static final byte EK_CBLE = 10;
  static final byte EF_SIGN = 1;
  static final byte EF_DELTA = 2;
  static final byte EF_NULL = 4;
  static final byte EF_BACK = 8;
  static final int NO_BAND_INDEX = -1;
  
  public String name()
  {
    return this.def.name();
  }
  
  public Layout layout()
  {
    return this.def;
  }
  
  public byte[] bytes()
  {
    return this.bytes;
  }
  
  public int size()
  {
    return this.bytes.length;
  }
  
  public ConstantPool.Entry getNameRef()
  {
    return this.def.getNameRef();
  }
  
  private Attribute(Attribute paramAttribute)
  {
    this.def = paramAttribute.def;
    this.bytes = paramAttribute.bytes;
    this.fixups = paramAttribute.fixups;
  }
  
  public Attribute(Layout paramLayout, byte[] paramArrayOfByte, Object paramObject)
  {
    this.def = paramLayout;
    this.bytes = paramArrayOfByte;
    this.fixups = paramObject;
    Fixups.setBytes(paramObject, paramArrayOfByte);
  }
  
  public Attribute(Layout paramLayout, byte[] paramArrayOfByte)
  {
    this(paramLayout, paramArrayOfByte, null);
  }
  
  public Attribute addContent(byte[] paramArrayOfByte, Object paramObject)
  {
    assert (isCanonical());
    if ((paramArrayOfByte.length == 0) && (paramObject == null)) {
      return this;
    }
    Attribute localAttribute = new Attribute(this);
    localAttribute.bytes = paramArrayOfByte;
    localAttribute.fixups = paramObject;
    Fixups.setBytes(paramObject, paramArrayOfByte);
    return localAttribute;
  }
  
  public Attribute addContent(byte[] paramArrayOfByte)
  {
    return addContent(paramArrayOfByte, null);
  }
  
  public void finishRefs(ConstantPool.Index paramIndex)
  {
    if (this.fixups != null)
    {
      Fixups.finishRefs(this.fixups, this.bytes, paramIndex);
      this.fixups = null;
    }
  }
  
  public boolean isCanonical()
  {
    return this == this.def.canon;
  }
  
  public int compareTo(Attribute paramAttribute)
  {
    return this.def.compareTo(paramAttribute.def);
  }
  
  public static List<Attribute> getCanonList(List<Attribute> paramList)
  {
    synchronized (canonLists)
    {
      Object localObject1 = (List)canonLists.get(paramList);
      if (localObject1 == null)
      {
        localObject1 = new ArrayList(paramList.size());
        ((List)localObject1).addAll(paramList);
        localObject1 = Collections.unmodifiableList((List)localObject1);
        canonLists.put(paramList, localObject1);
      }
      return localObject1;
    }
  }
  
  public static Attribute find(int paramInt, String paramString1, String paramString2)
  {
    Layout localLayout = Layout.makeKey(paramInt, paramString1, paramString2);
    synchronized (attributes)
    {
      Attribute localAttribute = (Attribute)attributes.get(localLayout);
      if (localAttribute == null)
      {
        localAttribute = new Layout(paramInt, paramString1, paramString2).canonicalInstance();
        attributes.put(localLayout, localAttribute);
      }
      return localAttribute;
    }
  }
  
  public static Layout keyForLookup(int paramInt, String paramString)
  {
    return Layout.makeKey(paramInt, paramString);
  }
  
  public static Attribute lookup(Map<Layout, Attribute> paramMap, int paramInt, String paramString)
  {
    if (paramMap == null) {
      paramMap = standardDefs;
    }
    return (Attribute)paramMap.get(Layout.makeKey(paramInt, paramString));
  }
  
  public static Attribute define(Map<Layout, Attribute> paramMap, int paramInt, String paramString1, String paramString2)
  {
    Attribute localAttribute = find(paramInt, paramString1, paramString2);
    paramMap.put(Layout.makeKey(paramInt, paramString1), localAttribute);
    return localAttribute;
  }
  
  public static String contextName(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "class";
    case 1: 
      return "field";
    case 2: 
      return "method";
    case 3: 
      return "code";
    }
    return null;
  }
  
  void visitRefs(Holder paramHolder, int paramInt, final Collection<ConstantPool.Entry> paramCollection)
  {
    if (paramInt == 0) {
      paramCollection.add(getNameRef());
    }
    if (this.bytes.length == 0) {
      return;
    }
    if (!this.def.hasRefs) {
      return;
    }
    if (this.fixups != null)
    {
      Fixups.visitRefs(this.fixups, paramCollection);
      return;
    }
    this.def.parse(paramHolder, this.bytes, 0, this.bytes.length, new ValueStream()
    {
      public void putInt(int paramAnonymousInt1, int paramAnonymousInt2) {}
      
      public void putRef(int paramAnonymousInt, ConstantPool.Entry paramAnonymousEntry)
      {
        paramCollection.add(paramAnonymousEntry);
      }
      
      public int encodeBCI(int paramAnonymousInt)
      {
        return paramAnonymousInt;
      }
    });
  }
  
  public void parse(Holder paramHolder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ValueStream paramValueStream)
  {
    this.def.parse(paramHolder, paramArrayOfByte, paramInt1, paramInt2, paramValueStream);
  }
  
  public Object unparse(ValueStream paramValueStream, ByteArrayOutputStream paramByteArrayOutputStream)
  {
    return this.def.unparse(paramValueStream, paramByteArrayOutputStream);
  }
  
  public String toString()
  {
    return this.def + "{" + (this.bytes == null ? -1 : size()) + "}" + (this.fixups == null ? "" : this.fixups.toString());
  }
  
  public static String normalizeLayoutString(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      char c = paramString.charAt(i++);
      if (c > ' ')
      {
        int k;
        int m;
        if (c == '#')
        {
          k = paramString.indexOf('\n', i);
          m = paramString.indexOf('\r', i);
          if (k < 0) {
            k = j;
          }
          if (m < 0) {
            m = j;
          }
          i = Math.min(k, m);
        }
        else if (c == '\\')
        {
          localStringBuilder.append(paramString.charAt(i++));
        }
        else if ((c == '0') && (paramString.startsWith("0x", i - 1)))
        {
          k = i - 1;
          for (m = k + 2; m < j; m++)
          {
            int n = paramString.charAt(m);
            if (((n < 48) || (n > 57)) && ((n < 97) || (n > 102))) {
              break;
            }
          }
          if (m > k)
          {
            String str2 = paramString.substring(k, m);
            localStringBuilder.append(Integer.decode(str2));
            i = m;
          }
          else
          {
            localStringBuilder.append(c);
          }
        }
        else
        {
          localStringBuilder.append(c);
        }
      }
    }
    String str1 = localStringBuilder.toString();
    return str1;
  }
  
  static Attribute.Layout.Element[] tokenizeLayout(Layout paramLayout, int paramInt, String paramString)
  {
    ArrayList localArrayList = new ArrayList(paramString.length());
    tokenizeLayout(paramLayout, paramInt, paramString, localArrayList);
    Attribute.Layout.Element[] arrayOfElement = new Attribute.Layout.Element[localArrayList.size()];
    localArrayList.toArray(arrayOfElement);
    return arrayOfElement;
  }
  
  static void tokenizeLayout(Layout paramLayout, int paramInt, String paramString, List<Attribute.Layout.Element> paramList)
  {
    int i = 0;
    int j = paramString.length();
    int k = 0;
    while (k < j)
    {
      int m = k;
      paramLayout.getClass();
      Attribute.Layout.Element localElement1 = new Attribute.Layout.Element(paramLayout);
      byte b;
      int n;
      int i3;
      Object localObject2;
      switch (paramString.charAt(k++))
      {
      case 'B': 
      case 'H': 
      case 'I': 
      case 'V': 
        b = 1;
        k--;
        k = tokenizeUInt(localElement1, paramString, k);
        break;
      case 'S': 
        b = 1;
        k--;
        k = tokenizeSInt(localElement1, paramString, k);
        break;
      case 'P': 
        b = 2;
        if (paramString.charAt(k++) == 'O')
        {
          Attribute.Layout.Element tmp305_303 = localElement1;
          tmp305_303.flags = ((byte)(tmp305_303.flags | 0x2));
          if (i == 0) {
            k = -k;
          } else {
            k++;
          }
        }
        else
        {
          k--;
          k = tokenizeUInt(localElement1, paramString, k);
        }
        break;
      case 'O': 
        b = 3;
        Attribute.Layout.Element tmp352_350 = localElement1;
        tmp352_350.flags = ((byte)(tmp352_350.flags | 0x2));
        if (i == 0) {
          k = -k;
        } else {
          k = tokenizeSInt(localElement1, paramString, k);
        }
        break;
      case 'F': 
        b = 4;
        k = tokenizeUInt(localElement1, paramString, k);
        break;
      case 'N': 
        b = 5;
        k = tokenizeUInt(localElement1, paramString, k);
        if (paramString.charAt(k++) != '[')
        {
          k = -k;
        }
        else
        {
          k = skipBody(paramString, n = k);
          localElement1.body = tokenizeLayout(paramLayout, paramInt, paramString.substring(n, k++));
        }
        break;
      case 'T': 
        b = 7;
        k = tokenizeSInt(localElement1, paramString, k);
        ArrayList localArrayList = new ArrayList();
        Object localObject1;
        for (;;)
        {
          if (paramString.charAt(k++) != '(')
          {
            k = -k;
            break;
          }
          i1 = k;
          k = paramString.indexOf(')', k);
          localObject1 = paramString.substring(i1, k++);
          i3 = ((String)localObject1).length();
          if (paramString.charAt(k++) != '[')
          {
            k = -k;
            break;
          }
          if (paramString.charAt(k) == ']') {
            n = k;
          } else {
            k = skipBody(paramString, n = k);
          }
          localObject2 = tokenizeLayout(paramLayout, paramInt, paramString.substring(n, k++));
          if (i3 == 0)
          {
            paramLayout.getClass();
            Attribute.Layout.Element localElement2 = new Attribute.Layout.Element(paramLayout);
            localElement2.body = ((Attribute.Layout.Element[])localObject2);
            localElement2.kind = 8;
            localElement2.removeBand();
            localArrayList.add(localElement2);
            break;
          }
          int i4 = 1;
          int i6;
          for (int i5 = 0;; i5 = i6 + 1)
          {
            i6 = ((String)localObject1).indexOf(',', i5);
            if (i6 < 0) {
              i6 = i3;
            }
            String str2 = ((String)localObject1).substring(i5, i6);
            if (str2.length() == 0) {
              str2 = "empty";
            }
            int i9 = findCaseDash(str2, 0);
            int i8;
            if (i9 >= 0)
            {
              i7 = parseIntBefore(str2, i9);
              i8 = parseIntAfter(str2, i9);
              if (i7 < i8) {
                break label779;
              }
              k = -k;
              break;
            }
            for (int i7 = i8 = Integer.parseInt(str2);; i7++)
            {
              paramLayout.getClass();
              Attribute.Layout.Element localElement3 = new Attribute.Layout.Element(paramLayout);
              localElement3.body = ((Attribute.Layout.Element[])localObject2);
              localElement3.kind = 8;
              localElement3.removeBand();
              if (i4 == 0)
              {
                Attribute.Layout.Element tmp820_818 = localElement3;
                tmp820_818.flags = ((byte)(tmp820_818.flags | 0x8));
              }
              i4 = 0;
              localElement3.value = i7;
              localArrayList.add(localElement3);
              if (i7 == i8) {
                break;
              }
            }
            if (i6 == i3) {
              break;
            }
          }
        }
        localElement1.body = new Attribute.Layout.Element[localArrayList.size()];
        localArrayList.toArray(localElement1.body);
        localElement1.kind = b;
        for (int i1 = 0; i1 < localElement1.body.length - 1; i1++)
        {
          localObject1 = localElement1.body[i1];
          if (matchCase(localElement1, ((Attribute.Layout.Element)localObject1).value) != localObject1)
          {
            k = -k;
            break;
          }
        }
        break;
      case '(': 
        b = 9;
        localElement1.removeBand();
        k = paramString.indexOf(')', k);
        String str1 = paramString.substring(m + 1, k++);
        int i2 = Integer.parseInt(str1);
        i3 = paramInt + i2;
        if ((!(i2 + "").equals(str1)) || (paramLayout.elems == null) || (i3 < 0) || (i3 >= paramLayout.elems.length))
        {
          k = -k;
        }
        else
        {
          localObject2 = paramLayout.elems[i3];
          assert (((Attribute.Layout.Element)localObject2).kind == 10);
          localElement1.value = i3;
          localElement1.body = new Attribute.Layout.Element[] { localObject2 };
          if (i2 <= 0)
          {
            Attribute.Layout.Element tmp1148_1146 = localElement1;
            tmp1148_1146.flags = ((byte)(tmp1148_1146.flags | 0x8));
            Object tmp1161_1159 = localObject2;
            tmp1161_1159.flags = ((byte)(tmp1161_1159.flags | 0x8));
          }
        }
        break;
      case 'K': 
        b = 6;
        switch (paramString.charAt(k++))
        {
        case 'I': 
          localElement1.refKind = 3;
          break;
        case 'J': 
          localElement1.refKind = 5;
          break;
        case 'F': 
          localElement1.refKind = 4;
          break;
        case 'D': 
          localElement1.refKind = 6;
          break;
        case 'S': 
          localElement1.refKind = 8;
          break;
        case 'Q': 
          localElement1.refKind = 53;
          break;
        case 'M': 
          localElement1.refKind = 15;
          break;
        case 'T': 
          localElement1.refKind = 16;
          break;
        case 'L': 
          localElement1.refKind = 51;
          break;
        case 'E': 
        case 'G': 
        case 'H': 
        case 'K': 
        case 'N': 
        case 'O': 
        case 'P': 
        case 'R': 
        default: 
          k = -k;
        }
        break;
      case 'R': 
        b = 6;
        switch (paramString.charAt(k++))
        {
        case 'C': 
          localElement1.refKind = 7;
          break;
        case 'S': 
          localElement1.refKind = 13;
          break;
        case 'D': 
          localElement1.refKind = 12;
          break;
        case 'F': 
          localElement1.refKind = 9;
          break;
        case 'M': 
          localElement1.refKind = 10;
          break;
        case 'I': 
          localElement1.refKind = 11;
          break;
        case 'U': 
          localElement1.refKind = 1;
          break;
        case 'Q': 
          localElement1.refKind = 50;
          break;
        case 'Y': 
          localElement1.refKind = 18;
          break;
        case 'B': 
          localElement1.refKind = 17;
          break;
        case 'N': 
          localElement1.refKind = 52;
          break;
        case 'E': 
        case 'G': 
        case 'H': 
        case 'J': 
        case 'K': 
        case 'L': 
        case 'O': 
        case 'P': 
        case 'R': 
        case 'T': 
        case 'V': 
        case 'W': 
        case 'X': 
        default: 
          k = -k;
        }
        break;
      case ')': 
      case '*': 
      case '+': 
      case ',': 
      case '-': 
      case '.': 
      case '/': 
      case '0': 
      case '1': 
      case '2': 
      case '3': 
      case '4': 
      case '5': 
      case '6': 
      case '7': 
      case '8': 
      case '9': 
      case ':': 
      case ';': 
      case '<': 
      case '=': 
      case '>': 
      case '?': 
      case '@': 
      case 'A': 
      case 'C': 
      case 'D': 
      case 'E': 
      case 'G': 
      case 'J': 
      case 'L': 
      case 'M': 
      case 'Q': 
      case 'U': 
      default: 
        label779:
        k = -k;
        continue;
        if (b == 6)
        {
          if (paramString.charAt(k++) == 'N')
          {
            Attribute.Layout.Element tmp1640_1638 = localElement1;
            tmp1640_1638.flags = ((byte)(tmp1640_1638.flags | 0x4));
            k++;
          }
          k--;
          k = tokenizeUInt(localElement1, paramString, k);
          paramLayout.hasRefs = true;
        }
        i = b == 2 ? 1 : 0;
        localElement1.kind = b;
        localElement1.layout = paramString.substring(m, k);
        paramList.add(localElement1);
      }
    }
  }
  
  static String[] splitBodies(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramString.length(); i++)
    {
      if (paramString.charAt(i++) != '[') {
        paramString.charAt(-i);
      }
      int j;
      i = skipBody(paramString, j = i);
      localArrayList.add(paramString.substring(j, i));
    }
    String[] arrayOfString = new String[localArrayList.size()];
    localArrayList.toArray(arrayOfString);
    return arrayOfString;
  }
  
  private static int skipBody(String paramString, int paramInt)
  {
    assert (paramString.charAt(paramInt - 1) == '[');
    if (paramString.charAt(paramInt) == ']') {
      return -paramInt;
    }
    int i = 1;
    while (i > 0) {
      switch (paramString.charAt(paramInt++))
      {
      case '[': 
        i++;
        break;
      case ']': 
        i--;
      }
    }
    paramInt--;
    assert (paramString.charAt(paramInt) == ']');
    return paramInt;
  }
  
  private static int tokenizeUInt(Attribute.Layout.Element paramElement, String paramString, int paramInt)
  {
    switch (paramString.charAt(paramInt++))
    {
    case 'V': 
      paramElement.len = 0;
      break;
    case 'B': 
      paramElement.len = 1;
      break;
    case 'H': 
      paramElement.len = 2;
      break;
    case 'I': 
      paramElement.len = 4;
      break;
    default: 
      return -paramInt;
    }
    return paramInt;
  }
  
  private static int tokenizeSInt(Attribute.Layout.Element paramElement, String paramString, int paramInt)
  {
    if (paramString.charAt(paramInt) == 'S')
    {
      paramElement.flags = ((byte)(paramElement.flags | 0x1));
      paramInt++;
    }
    return tokenizeUInt(paramElement, paramString, paramInt);
  }
  
  private static boolean isDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  static int findCaseDash(String paramString, int paramInt)
  {
    if (paramInt <= 0) {
      paramInt = 1;
    }
    int i = paramString.length() - 2;
    for (;;)
    {
      int j = paramString.indexOf('-', paramInt);
      if ((j < 0) || (j > i)) {
        return -1;
      }
      if (isDigit(paramString.charAt(j - 1)))
      {
        char c = paramString.charAt(j + 1);
        if ((c == '-') && (j + 2 < paramString.length())) {
          c = paramString.charAt(j + 2);
        }
        if (isDigit(c)) {
          return j;
        }
      }
      paramInt = j + 1;
    }
  }
  
  static int parseIntBefore(String paramString, int paramInt)
  {
    int i = paramInt;
    for (int j = i; (j > 0) && (isDigit(paramString.charAt(j - 1))); j--) {}
    if (j == i) {
      return Integer.parseInt("empty");
    }
    if ((j >= 1) && (paramString.charAt(j - 1) == '-')) {
      j--;
    }
    assert ((j == 0) || (!isDigit(paramString.charAt(j - 1))));
    return Integer.parseInt(paramString.substring(j, i));
  }
  
  static int parseIntAfter(String paramString, int paramInt)
  {
    int i = paramInt + 1;
    int j = i;
    int k = paramString.length();
    if ((j < k) && (paramString.charAt(j) == '-')) {
      j++;
    }
    while ((j < k) && (isDigit(paramString.charAt(j)))) {
      j++;
    }
    if (i == j) {
      return Integer.parseInt("empty");
    }
    return Integer.parseInt(paramString.substring(i, j));
  }
  
  static String expandCaseDashNotation(String paramString)
  {
    int i = findCaseDash(paramString, 0);
    if (i < 0) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length() * 3);
    int j = 0;
    for (;;)
    {
      localStringBuilder.append(paramString.substring(j, i));
      j = i + 1;
      int k = parseIntBefore(paramString, i);
      int m = parseIntAfter(paramString, i);
      assert (k < m);
      localStringBuilder.append(",");
      for (int n = k + 1; n < m; n++)
      {
        localStringBuilder.append(n);
        localStringBuilder.append(",");
      }
      i = findCaseDash(paramString, j);
      if (i < 0) {
        break;
      }
    }
    localStringBuilder.append(paramString.substring(j));
    return localStringBuilder.toString();
  }
  
  static int parseUsing(Attribute.Layout.Element[] paramArrayOfElement, Holder paramHolder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, ValueStream paramValueStream)
  {
    int i = 0;
    int j = 0;
    int k = paramInt1 + paramInt2;
    int[] arrayOfInt = { 0 };
    for (int m = 0; m < paramArrayOfElement.length; m++)
    {
      Attribute.Layout.Element localElement1 = paramArrayOfElement[m];
      int n = localElement1.bandIndex;
      int i1;
      int i2;
      int i3;
      switch (localElement1.kind)
      {
      case 1: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i1 = arrayOfInt[0];
        paramValueStream.putInt(n, i1);
        break;
      case 2: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i2 = arrayOfInt[0];
        i3 = paramValueStream.encodeBCI(i2);
        if (!localElement1.flagTest((byte)2)) {
          i1 = i3;
        } else {
          i1 = i3 - j;
        }
        i = i2;
        j = i3;
        paramValueStream.putInt(n, i1);
        break;
      case 3: 
        assert (localElement1.flagTest((byte)2));
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i2 = i + arrayOfInt[0];
        i3 = paramValueStream.encodeBCI(i2);
        i1 = i3 - j;
        i = i2;
        j = i3;
        paramValueStream.putInt(n, i1);
        break;
      case 4: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i1 = arrayOfInt[0];
        paramValueStream.putInt(n, i1);
        break;
      case 5: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i1 = arrayOfInt[0];
        paramValueStream.putInt(n, i1);
        for (int i4 = 0; i4 < i1; i4++) {
          paramInt1 = parseUsing(localElement1.body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream);
        }
        break;
      case 7: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        i1 = arrayOfInt[0];
        paramValueStream.putInt(n, i1);
        Attribute.Layout.Element localElement2 = matchCase(localElement1, i1);
        paramInt1 = parseUsing(localElement2.body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream);
        break;
      case 9: 
        assert (localElement1.body.length == 1);
        assert (localElement1.body[0].kind == 10);
        if (localElement1.flagTest((byte)8)) {
          paramValueStream.noteBackCall(localElement1.value);
        }
        paramInt1 = parseUsing(localElement1.body[0].body, paramHolder, paramArrayOfByte, paramInt1, k - paramInt1, paramValueStream);
        break;
      case 6: 
        paramInt1 = parseInt(localElement1, paramArrayOfByte, paramInt1, arrayOfInt);
        int i5 = arrayOfInt[0];
        ConstantPool.SignatureEntry localSignatureEntry;
        if (i5 == 0)
        {
          localSignatureEntry = null;
        }
        else
        {
          ConstantPool.Entry[] arrayOfEntry = paramHolder.getCPMap();
          localSignatureEntry = (i5 >= 0) && (i5 < arrayOfEntry.length) ? arrayOfEntry[i5] : null;
          int i6 = localElement1.refKind;
          if ((localSignatureEntry != null) && (i6 == 13) && (localSignatureEntry.getTag() == 1))
          {
            str = localSignatureEntry.stringValue();
            localSignatureEntry = ConstantPool.getSignatureEntry(str);
          }
          String str = "type=" + ConstantPool.tagName(localSignatureEntry.tag);
          if ((localSignatureEntry == null) || (!localSignatureEntry.tagMatches(i6))) {
            throw new IllegalArgumentException("Bad constant, expected type=" + ConstantPool.tagName(i6) + " got " + str);
          }
        }
        paramValueStream.putRef(n, localSignatureEntry);
        break;
      case 8: 
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
    return paramInt1;
  }
  
  static Attribute.Layout.Element matchCase(Attribute.Layout.Element paramElement, int paramInt)
  {
    assert (paramElement.kind == 7);
    int i = paramElement.body.length - 1;
    for (int j = 0; j < i; j++)
    {
      Attribute.Layout.Element localElement = paramElement.body[j];
      assert (localElement.kind == 8);
      if (paramInt == localElement.value) {
        return localElement;
      }
    }
    return paramElement.body[i];
  }
  
  private static int parseInt(Attribute.Layout.Element paramElement, byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
  {
    int i = 0;
    int j = paramElement.len * 8;
    int k = j;
    for (;;)
    {
      k -= 8;
      if (k < 0) {
        break;
      }
      i += ((paramArrayOfByte[(paramInt++)] & 0xFF) << k);
    }
    if ((j < 32) && (paramElement.flagTest((byte)1)))
    {
      k = 32 - j;
      i = i << k >> k;
    }
    paramArrayOfInt[0] = i;
    return paramInt;
  }
  
  static void unparseUsing(Attribute.Layout.Element[] paramArrayOfElement, Object[] paramArrayOfObject, ValueStream paramValueStream, ByteArrayOutputStream paramByteArrayOutputStream)
  {
    int i = 0;
    int j = 0;
    for (int k = 0; k < paramArrayOfElement.length; k++)
    {
      Attribute.Layout.Element localElement1 = paramArrayOfElement[k];
      int m = localElement1.bandIndex;
      int n;
      int i2;
      int i1;
      switch (localElement1.kind)
      {
      case 1: 
        n = paramValueStream.getInt(m);
        unparseInt(localElement1, n, paramByteArrayOutputStream);
        break;
      case 2: 
        n = paramValueStream.getInt(m);
        if (!localElement1.flagTest((byte)2)) {
          i2 = n;
        } else {
          i2 = j + n;
        }
        assert (i == paramValueStream.decodeBCI(j));
        i1 = paramValueStream.decodeBCI(i2);
        unparseInt(localElement1, i1, paramByteArrayOutputStream);
        i = i1;
        j = i2;
        break;
      case 3: 
        n = paramValueStream.getInt(m);
        assert (localElement1.flagTest((byte)2));
        assert (i == paramValueStream.decodeBCI(j));
        i2 = j + n;
        i1 = paramValueStream.decodeBCI(i2);
        unparseInt(localElement1, i1 - i, paramByteArrayOutputStream);
        i = i1;
        j = i2;
        break;
      case 4: 
        n = paramValueStream.getInt(m);
        unparseInt(localElement1, n, paramByteArrayOutputStream);
        break;
      case 5: 
        n = paramValueStream.getInt(m);
        unparseInt(localElement1, n, paramByteArrayOutputStream);
        for (int i3 = 0; i3 < n; i3++) {
          unparseUsing(localElement1.body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream);
        }
        break;
      case 7: 
        n = paramValueStream.getInt(m);
        unparseInt(localElement1, n, paramByteArrayOutputStream);
        Attribute.Layout.Element localElement2 = matchCase(localElement1, n);
        unparseUsing(localElement2.body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream);
        break;
      case 9: 
        assert (localElement1.body.length == 1);
        assert (localElement1.body[0].kind == 10);
        unparseUsing(localElement1.body[0].body, paramArrayOfObject, paramValueStream, paramByteArrayOutputStream);
        break;
      case 6: 
        ConstantPool.Entry localEntry = paramValueStream.getRef(m);
        int i4;
        if (localEntry != null)
        {
          paramArrayOfObject[0] = Fixups.addRefWithLoc(paramArrayOfObject[0], paramByteArrayOutputStream.size(), localEntry);
          i4 = 0;
        }
        else
        {
          i4 = 0;
        }
        unparseInt(localElement1, i4, paramByteArrayOutputStream);
        break;
      case 8: 
      default: 
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
        break;
      }
    }
  }
  
  private static void unparseInt(Attribute.Layout.Element paramElement, int paramInt, ByteArrayOutputStream paramByteArrayOutputStream)
  {
    int i = paramElement.len * 8;
    if (i == 0) {
      return;
    }
    if (i < 32)
    {
      j = 32 - i;
      int k;
      if (paramElement.flagTest((byte)1)) {
        k = paramInt << j >> j;
      } else {
        k = paramInt << j >>> j;
      }
      if (k != paramInt) {
        throw new InternalError("cannot code in " + paramElement.len + " bytes: " + paramInt);
      }
    }
    int j = i;
    for (;;)
    {
      j -= 8;
      if (j < 0) {
        break;
      }
      paramByteArrayOutputStream.write((byte)(paramInt >>> j));
    }
  }
  
  static
  {
    canonLists = new HashMap();
    attributes = new HashMap();
    standardDefs = new HashMap();
    Object localObject = standardDefs;
    define((Map)localObject, 0, "Signature", "RSH");
    define((Map)localObject, 0, "Synthetic", "");
    define((Map)localObject, 0, "Deprecated", "");
    define((Map)localObject, 0, "SourceFile", "RUH");
    define((Map)localObject, 0, "EnclosingMethod", "RCHRDNH");
    define((Map)localObject, 0, "InnerClasses", "NH[RCHRCNHRUNHFH]");
    define((Map)localObject, 0, "BootstrapMethods", "NH[RMHNH[KLH]]");
    define((Map)localObject, 1, "Signature", "RSH");
    define((Map)localObject, 1, "Synthetic", "");
    define((Map)localObject, 1, "Deprecated", "");
    define((Map)localObject, 1, "ConstantValue", "KQH");
    define((Map)localObject, 2, "Signature", "RSH");
    define((Map)localObject, 2, "Synthetic", "");
    define((Map)localObject, 2, "Deprecated", "");
    define((Map)localObject, 2, "Exceptions", "NH[RCH]");
    define((Map)localObject, 2, "MethodParameters", "NB[RUNHFH]");
    define((Map)localObject, 3, "StackMapTable", "[NH[(1)]][TB(64-127)[(2)](247)[(1)(2)](248-251)[(1)](252)[(1)(2)](253)[(1)(2)(2)](254)[(1)(2)(2)(2)](255)[(1)NH[(2)]NH[(2)]]()[]][H][TB(7)[RCH](8)[PH]()[]]");
    define((Map)localObject, 3, "LineNumberTable", "NH[PHH]");
    define((Map)localObject, 3, "LocalVariableTable", "NH[PHOHRUHRSHH]");
    define((Map)localObject, 3, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
    localObject = new String[] { normalizeLayoutString("\n  # parameter_annotations :=\n  [ NB[(1)] ]     # forward call to annotations"), normalizeLayoutString("\n  # annotations :=\n  [ NH[(1)] ]     # forward call to annotation\n  "), normalizeLayoutString("\n  # annotation :=\n  [RSH\n    NH[RUH (1)]   # forward call to value\n    ]"), normalizeLayoutString("\n  # value :=\n  [TB # Callable 2 encodes one tagged value.\n    (\\B,\\C,\\I,\\S,\\Z)[KIH]\n    (\\D)[KDH]\n    (\\F)[KFH]\n    (\\J)[KJH]\n    (\\c)[RSH]\n    (\\e)[RSH RUH]\n    (\\s)[RUH]\n    (\\[)[NH[(0)]] # backward self-call to value\n    (\\@)[RSH NH[RUH (0)]] # backward self-call to value\n    ()[] ]") };
    String[] arrayOfString = { normalizeLayoutString("\n # type-annotations :=\n  [ NH[(1)(2)(3)] ]     # forward call to type-annotations"), normalizeLayoutString("\n  # type-annotation :=\n  [TB\n    (0-1) [B] # {CLASS, METHOD}_TYPE_PARAMETER\n    (16) [FH] # CLASS_EXTENDS\n    (17-18) [BB] # {CLASS, METHOD}_TYPE_PARAMETER_BOUND\n    (19-21) [] # FIELD, METHOD_RETURN, METHOD_RECEIVER\n    (22) [B] # METHOD_FORMAL_PARAMETER\n    (23) [H] # THROWS\n    (64-65) [NH[PHOHH]] # LOCAL_VARIABLE, RESOURCE_VARIABLE\n    (66) [H] # EXCEPTION_PARAMETER\n    (67-70) [PH] # INSTANCEOF, NEW, {CONSTRUCTOR, METHOD}_REFERENCE_RECEIVER\n    (71-75) [PHB] # CAST, {CONSTRUCTOR,METHOD}_INVOCATION_TYPE_ARGUMENT, {CONSTRUCTOR, METHOD}_REFERENCE_TYPE_ARGUMENT\n    ()[] ]"), normalizeLayoutString("\n # type-path\n [ NB[BB] ]") };
    Map localMap = standardDefs;
    String str1 = localObject[3];
    String str2 = localObject[1] + localObject[2] + localObject[3];
    String str3 = localObject[0] + str2;
    String str4 = arrayOfString[0] + arrayOfString[1] + arrayOfString[2] + localObject[2] + localObject[3];
    for (int i = 0; i < 4; i++)
    {
      if (i != 3)
      {
        define(localMap, i, "RuntimeVisibleAnnotations", str2);
        define(localMap, i, "RuntimeInvisibleAnnotations", str2);
        if (i == 2)
        {
          define(localMap, i, "RuntimeVisibleParameterAnnotations", str3);
          define(localMap, i, "RuntimeInvisibleParameterAnnotations", str3);
          define(localMap, i, "AnnotationDefault", str1);
        }
      }
      define(localMap, i, "RuntimeVisibleTypeAnnotations", str4);
      define(localMap, i, "RuntimeInvisibleTypeAnnotations", str4);
    }
    assert (expandCaseDashNotation("1-5").equals("1,2,3,4,5"));
    assert (expandCaseDashNotation("-2--1").equals("-2,-1"));
    assert (expandCaseDashNotation("-2-1").equals("-2,-1,0,1"));
    assert (expandCaseDashNotation("-1-0").equals("-1,0"));
  }
  
  public static class FormatException
    extends IOException
  {
    private static final long serialVersionUID = -2542243830788066513L;
    private int ctype;
    private String name;
    String layout;
    
    public FormatException(String paramString1, int paramInt, String paramString2, String paramString3)
    {
      super();
      this.ctype = paramInt;
      this.name = paramString2;
      this.layout = paramString3;
    }
    
    public FormatException(String paramString1, int paramInt, String paramString2)
    {
      this(paramString1, paramInt, paramString2, null);
    }
  }
  
  public static abstract class Holder
  {
    protected int flags;
    protected List<Attribute> attributes;
    static final List<Attribute> noAttributes = Arrays.asList(new Attribute[0]);
    
    public Holder() {}
    
    protected abstract ConstantPool.Entry[] getCPMap();
    
    public int attributeSize()
    {
      return this.attributes == null ? 0 : this.attributes.size();
    }
    
    public void trimToSize()
    {
      if (this.attributes == null) {
        return;
      }
      if (this.attributes.isEmpty())
      {
        this.attributes = null;
        return;
      }
      if ((this.attributes instanceof ArrayList))
      {
        ArrayList localArrayList = (ArrayList)this.attributes;
        localArrayList.trimToSize();
        int i = 1;
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          Attribute localAttribute = (Attribute)localIterator.next();
          if (!localAttribute.isCanonical()) {
            i = 0;
          }
          if (localAttribute.fixups != null)
          {
            assert (!localAttribute.isCanonical());
            localAttribute.fixups = Fixups.trimToSize(localAttribute.fixups);
          }
        }
        if (i != 0) {
          this.attributes = Attribute.getCanonList(localArrayList);
        }
      }
    }
    
    public void addAttribute(Attribute paramAttribute)
    {
      if (this.attributes == null) {
        this.attributes = new ArrayList(3);
      } else if (!(this.attributes instanceof ArrayList)) {
        this.attributes = new ArrayList(this.attributes);
      }
      this.attributes.add(paramAttribute);
    }
    
    public Attribute removeAttribute(Attribute paramAttribute)
    {
      if (this.attributes == null) {
        return null;
      }
      if (!this.attributes.contains(paramAttribute)) {
        return null;
      }
      if (!(this.attributes instanceof ArrayList)) {
        this.attributes = new ArrayList(this.attributes);
      }
      this.attributes.remove(paramAttribute);
      return paramAttribute;
    }
    
    public Attribute getAttribute(int paramInt)
    {
      return (Attribute)this.attributes.get(paramInt);
    }
    
    protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
    {
      if (this.attributes == null) {
        return;
      }
      Iterator localIterator = this.attributes.iterator();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        localAttribute.visitRefs(this, paramInt, paramCollection);
      }
    }
    
    public List<Attribute> getAttributes()
    {
      if (this.attributes == null) {
        return noAttributes;
      }
      return this.attributes;
    }
    
    public void setAttributes(List<Attribute> paramList)
    {
      if (paramList.isEmpty()) {
        this.attributes = null;
      } else {
        this.attributes = paramList;
      }
    }
    
    public Attribute getAttribute(String paramString)
    {
      if (this.attributes == null) {
        return null;
      }
      Iterator localIterator = this.attributes.iterator();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        if (localAttribute.name().equals(paramString)) {
          return localAttribute;
        }
      }
      return null;
    }
    
    public Attribute getAttribute(Attribute.Layout paramLayout)
    {
      if (this.attributes == null) {
        return null;
      }
      Iterator localIterator = this.attributes.iterator();
      while (localIterator.hasNext())
      {
        Attribute localAttribute = (Attribute)localIterator.next();
        if (localAttribute.layout() == paramLayout) {
          return localAttribute;
        }
      }
      return null;
    }
    
    public Attribute removeAttribute(String paramString)
    {
      return removeAttribute(getAttribute(paramString));
    }
    
    public Attribute removeAttribute(Attribute.Layout paramLayout)
    {
      return removeAttribute(getAttribute(paramLayout));
    }
    
    public void strip(String paramString)
    {
      removeAttribute(getAttribute(paramString));
    }
  }
  
  public static class Layout
    implements Comparable<Layout>
  {
    int ctype;
    String name;
    boolean hasRefs;
    String layout;
    int bandCount;
    Element[] elems;
    Attribute canon;
    private static final Element[] noElems = new Element[0];
    
    public int ctype()
    {
      return this.ctype;
    }
    
    public String name()
    {
      return this.name;
    }
    
    public String layout()
    {
      return this.layout;
    }
    
    public Attribute canonicalInstance()
    {
      return this.canon;
    }
    
    public ConstantPool.Entry getNameRef()
    {
      return ConstantPool.getUtf8Entry(name());
    }
    
    public boolean isEmpty()
    {
      return this.layout.isEmpty();
    }
    
    public Layout(int paramInt, String paramString1, String paramString2)
    {
      this.ctype = paramInt;
      this.name = paramString1.intern();
      this.layout = paramString2.intern();
      assert (paramInt < 4);
      boolean bool = paramString2.startsWith("[");
      try
      {
        if (!bool)
        {
          this.elems = Attribute.tokenizeLayout(this, -1, paramString2);
        }
        else
        {
          String[] arrayOfString = Attribute.splitBodies(paramString2);
          Element[] arrayOfElement = new Element[arrayOfString.length];
          this.elems = arrayOfElement;
          Element localElement;
          for (int i = 0; i < arrayOfElement.length; i++)
          {
            localElement = new Element();
            localElement.kind = 10;
            localElement.removeBand();
            localElement.bandIndex = -1;
            localElement.layout = arrayOfString[i];
            arrayOfElement[i] = localElement;
          }
          for (i = 0; i < arrayOfElement.length; i++)
          {
            localElement = arrayOfElement[i];
            localElement.body = Attribute.tokenizeLayout(this, i, arrayOfString[i]);
          }
        }
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
      {
        throw new RuntimeException("Bad attribute layout: " + paramString2, localStringIndexOutOfBoundsException);
      }
      this.canon = new Attribute(this, Constants.noBytes);
    }
    
    private Layout() {}
    
    static Layout makeKey(int paramInt, String paramString1, String paramString2)
    {
      Layout localLayout = new Layout();
      localLayout.ctype = paramInt;
      localLayout.name = paramString1.intern();
      localLayout.layout = paramString2.intern();
      assert (paramInt < 4);
      return localLayout;
    }
    
    static Layout makeKey(int paramInt, String paramString)
    {
      return makeKey(paramInt, paramString, "");
    }
    
    public Attribute addContent(byte[] paramArrayOfByte, Object paramObject)
    {
      return this.canon.addContent(paramArrayOfByte, paramObject);
    }
    
    public Attribute addContent(byte[] paramArrayOfByte)
    {
      return this.canon.addContent(paramArrayOfByte, null);
    }
    
    public boolean equals(Object paramObject)
    {
      return (paramObject != null) && (paramObject.getClass() == Layout.class) && (equals((Layout)paramObject));
    }
    
    public boolean equals(Layout paramLayout)
    {
      return (this.name.equals(paramLayout.name)) && (this.layout.equals(paramLayout.layout)) && (this.ctype == paramLayout.ctype);
    }
    
    public int hashCode()
    {
      return ((17 + this.name.hashCode()) * 37 + this.layout.hashCode()) * 37 + this.ctype;
    }
    
    public int compareTo(Layout paramLayout)
    {
      int i = this.name.compareTo(paramLayout.name);
      if (i != 0) {
        return i;
      }
      i = this.layout.compareTo(paramLayout.layout);
      if (i != 0) {
        return i;
      }
      return this.ctype - paramLayout.ctype;
    }
    
    public String toString()
    {
      String str = Attribute.contextName(this.ctype) + "." + this.name + "[" + this.layout + "]";
      assert ((str = stringForDebug()) != null);
      return str;
    }
    
    private String stringForDebug()
    {
      return Attribute.contextName(this.ctype) + "." + this.name + Arrays.asList(this.elems);
    }
    
    public boolean hasCallables()
    {
      return (this.elems.length > 0) && (this.elems[0].kind == 10);
    }
    
    public Element[] getCallables()
    {
      if (hasCallables())
      {
        Element[] arrayOfElement = (Element[])Arrays.copyOf(this.elems, this.elems.length);
        return arrayOfElement;
      }
      return noElems;
    }
    
    public Element[] getEntryPoint()
    {
      if (hasCallables()) {
        return this.elems[0].body;
      }
      Element[] arrayOfElement = (Element[])Arrays.copyOf(this.elems, this.elems.length);
      return arrayOfElement;
    }
    
    public void parse(Attribute.Holder paramHolder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, Attribute.ValueStream paramValueStream)
    {
      int i = Attribute.parseUsing(getEntryPoint(), paramHolder, paramArrayOfByte, paramInt1, paramInt2, paramValueStream);
      if (i != paramInt1 + paramInt2) {
        throw new InternalError("layout parsed " + (i - paramInt1) + " out of " + paramInt2 + " bytes");
      }
    }
    
    public Object unparse(Attribute.ValueStream paramValueStream, ByteArrayOutputStream paramByteArrayOutputStream)
    {
      Object[] arrayOfObject = { null };
      Attribute.unparseUsing(getEntryPoint(), arrayOfObject, paramValueStream, paramByteArrayOutputStream);
      return arrayOfObject[0];
    }
    
    public String layoutForClassVersion(Package.Version paramVersion)
    {
      if (paramVersion.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
        return Attribute.expandCaseDashNotation(this.layout);
      }
      return this.layout;
    }
    
    public class Element
    {
      String layout;
      byte flags;
      byte kind;
      byte len;
      byte refKind;
      int bandIndex = Attribute.Layout.this.bandCount++;
      int value;
      Element[] body;
      
      boolean flagTest(byte paramByte)
      {
        return (this.flags & paramByte) != 0;
      }
      
      Element() {}
      
      void removeBand()
      {
        Attribute.Layout.this.bandCount -= 1;
        assert (this.bandIndex == Attribute.Layout.this.bandCount);
        this.bandIndex = -1;
      }
      
      public boolean hasBand()
      {
        return this.bandIndex >= 0;
      }
      
      public String toString()
      {
        String str = this.layout;
        assert ((str = stringForDebug()) != null);
        return str;
      }
      
      private String stringForDebug()
      {
        Element[] arrayOfElement = this.body;
        switch (this.kind)
        {
        case 9: 
          arrayOfElement = null;
          break;
        case 8: 
          if (flagTest((byte)8)) {
            arrayOfElement = null;
          }
          break;
        }
        return this.layout + (!hasBand() ? "" : new StringBuilder().append("#").append(this.bandIndex).toString()) + "<" + (this.flags == 0 ? "" : new StringBuilder().append("").append(this.flags).toString()) + this.kind + this.len + (this.refKind == 0 ? "" : new StringBuilder().append("").append(this.refKind).toString()) + ">" + (this.value == 0 ? "" : new StringBuilder().append("(").append(this.value).append(")").toString()) + (arrayOfElement == null ? "" : new StringBuilder().append("").append(Arrays.asList(arrayOfElement)).toString());
      }
    }
  }
  
  public static abstract class ValueStream
  {
    public ValueStream() {}
    
    public int getInt(int paramInt)
    {
      throw undef();
    }
    
    public void putInt(int paramInt1, int paramInt2)
    {
      throw undef();
    }
    
    public ConstantPool.Entry getRef(int paramInt)
    {
      throw undef();
    }
    
    public void putRef(int paramInt, ConstantPool.Entry paramEntry)
    {
      throw undef();
    }
    
    public int decodeBCI(int paramInt)
    {
      throw undef();
    }
    
    public int encodeBCI(int paramInt)
    {
      throw undef();
    }
    
    public void noteBackCall(int paramInt) {}
    
    private RuntimeException undef()
    {
      return new UnsupportedOperationException("ValueStream method");
    }
  }
}
