package com.sun.org.apache.regexp.internal;

import java.util.Hashtable;

public class RECompiler
{
  char[] instruction = new char[''];
  int lenInstruction = 0;
  String pattern;
  int len;
  int idx;
  int parens;
  static final int NODE_NORMAL = 0;
  static final int NODE_NULLABLE = 1;
  static final int NODE_TOPLEVEL = 2;
  static final int ESC_MASK = 1048560;
  static final int ESC_BACKREF = 1048575;
  static final int ESC_COMPLEX = 1048574;
  static final int ESC_CLASS = 1048573;
  int maxBrackets = 10;
  static final int bracketUnbounded = -1;
  int brackets = 0;
  int[] bracketStart = null;
  int[] bracketEnd = null;
  int[] bracketMin = null;
  int[] bracketOpt = null;
  static Hashtable hashPOSIX = new Hashtable();
  
  public RECompiler() {}
  
  void ensure(int paramInt)
  {
    int i = this.instruction.length;
    if (this.lenInstruction + paramInt >= i)
    {
      while (this.lenInstruction + paramInt >= i) {
        i *= 2;
      }
      char[] arrayOfChar = new char[i];
      System.arraycopy(this.instruction, 0, arrayOfChar, 0, this.lenInstruction);
      this.instruction = arrayOfChar;
    }
  }
  
  void emit(char paramChar)
  {
    ensure(1);
    this.instruction[(this.lenInstruction++)] = paramChar;
  }
  
  void nodeInsert(char paramChar, int paramInt1, int paramInt2)
  {
    ensure(3);
    System.arraycopy(this.instruction, paramInt2, this.instruction, paramInt2 + 3, this.lenInstruction - paramInt2);
    this.instruction[(paramInt2 + 0)] = paramChar;
    this.instruction[(paramInt2 + 1)] = ((char)paramInt1);
    this.instruction[(paramInt2 + 2)] = '\000';
    this.lenInstruction += 3;
  }
  
  void setNextOfEnd(int paramInt1, int paramInt2)
  {
    for (int i = this.instruction[(paramInt1 + 2)]; (i != 0) && (paramInt1 < this.lenInstruction); i = this.instruction[(paramInt1 + 2)])
    {
      if (paramInt1 == paramInt2) {
        paramInt2 = this.lenInstruction;
      }
      paramInt1 += i;
    }
    if (paramInt1 < this.lenInstruction) {
      this.instruction[(paramInt1 + 2)] = ((char)(short)(paramInt2 - paramInt1));
    }
  }
  
  int node(char paramChar, int paramInt)
  {
    ensure(3);
    this.instruction[(this.lenInstruction + 0)] = paramChar;
    this.instruction[(this.lenInstruction + 1)] = ((char)paramInt);
    this.instruction[(this.lenInstruction + 2)] = '\000';
    this.lenInstruction += 3;
    return this.lenInstruction - 3;
  }
  
  void internalError()
    throws Error
  {
    throw new Error("Internal error!");
  }
  
  void syntaxError(String paramString)
    throws RESyntaxException
  {
    throw new RESyntaxException(paramString);
  }
  
  void allocBrackets()
  {
    if (this.bracketStart == null)
    {
      this.bracketStart = new int[this.maxBrackets];
      this.bracketEnd = new int[this.maxBrackets];
      this.bracketMin = new int[this.maxBrackets];
      this.bracketOpt = new int[this.maxBrackets];
      for (int i = 0; i < this.maxBrackets; i++)
      {
        byte tmp82_81 = (this.bracketMin[i] = this.bracketOpt[i] = -1);
        this.bracketEnd[i] = tmp82_81;
        this.bracketStart[i] = tmp82_81;
      }
    }
  }
  
  synchronized void reallocBrackets()
  {
    if (this.bracketStart == null) {
      allocBrackets();
    }
    int i = this.maxBrackets * 2;
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    for (int j = this.brackets; j < i; j++)
    {
      byte tmp67_66 = (arrayOfInt3[j] = arrayOfInt4[j] = -1);
      arrayOfInt2[j] = tmp67_66;
      arrayOfInt1[j] = tmp67_66;
    }
    System.arraycopy(this.bracketStart, 0, arrayOfInt1, 0, this.brackets);
    System.arraycopy(this.bracketEnd, 0, arrayOfInt2, 0, this.brackets);
    System.arraycopy(this.bracketMin, 0, arrayOfInt3, 0, this.brackets);
    System.arraycopy(this.bracketOpt, 0, arrayOfInt4, 0, this.brackets);
    this.bracketStart = arrayOfInt1;
    this.bracketEnd = arrayOfInt2;
    this.bracketMin = arrayOfInt3;
    this.bracketOpt = arrayOfInt4;
    this.maxBrackets = i;
  }
  
  void bracket()
    throws RESyntaxException
  {
    if ((this.idx >= this.len) || (this.pattern.charAt(this.idx++) != '{')) {
      internalError();
    }
    if ((this.idx >= this.len) || (!Character.isDigit(this.pattern.charAt(this.idx)))) {
      syntaxError("Expected digit");
    }
    StringBuffer localStringBuffer = new StringBuffer();
    while ((this.idx < this.len) && (Character.isDigit(this.pattern.charAt(this.idx)))) {
      localStringBuffer.append(this.pattern.charAt(this.idx++));
    }
    try
    {
      this.bracketMin[this.brackets] = Integer.parseInt(localStringBuffer.toString());
    }
    catch (NumberFormatException localNumberFormatException1)
    {
      syntaxError("Expected valid number");
    }
    if (this.idx >= this.len) {
      syntaxError("Expected comma or right bracket");
    }
    if (this.pattern.charAt(this.idx) == '}')
    {
      this.idx += 1;
      this.bracketOpt[this.brackets] = 0;
      return;
    }
    if ((this.idx >= this.len) || (this.pattern.charAt(this.idx++) != ',')) {
      syntaxError("Expected comma");
    }
    if (this.idx >= this.len) {
      syntaxError("Expected comma or right bracket");
    }
    if (this.pattern.charAt(this.idx) == '}')
    {
      this.idx += 1;
      this.bracketOpt[this.brackets] = -1;
      return;
    }
    if ((this.idx >= this.len) || (!Character.isDigit(this.pattern.charAt(this.idx)))) {
      syntaxError("Expected digit");
    }
    localStringBuffer.setLength(0);
    while ((this.idx < this.len) && (Character.isDigit(this.pattern.charAt(this.idx)))) {
      localStringBuffer.append(this.pattern.charAt(this.idx++));
    }
    try
    {
      this.bracketOpt[this.brackets] = (Integer.parseInt(localStringBuffer.toString()) - this.bracketMin[this.brackets]);
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      syntaxError("Expected valid number");
    }
    if (this.bracketOpt[this.brackets] < 0) {
      syntaxError("Bad range");
    }
    if ((this.idx >= this.len) || (this.pattern.charAt(this.idx++) != '}')) {
      syntaxError("Missing close brace");
    }
  }
  
  int escape()
    throws RESyntaxException
  {
    if (this.pattern.charAt(this.idx) != '\\') {
      internalError();
    }
    if (this.idx + 1 == this.len) {
      syntaxError("Escape terminates string");
    }
    this.idx += 2;
    char c = this.pattern.charAt(this.idx - 1);
    int i;
    switch (c)
    {
    case 'B': 
    case 'b': 
      return 1048574;
    case 'D': 
    case 'S': 
    case 'W': 
    case 'd': 
    case 's': 
    case 'w': 
      return 1048573;
    case 'u': 
    case 'x': 
      i = c == 'u' ? 4 : 2;
      int j = 0;
      while ((this.idx < this.len) && (i-- > 0))
      {
        int k = this.pattern.charAt(this.idx);
        if ((k >= 48) && (k <= 57))
        {
          j = (j << 4) + k - 48;
        }
        else
        {
          int m = Character.toLowerCase(k);
          if ((m >= 97) && (m <= 102)) {
            j = (j << 4) + (m - 97) + 10;
          } else {
            syntaxError("Expected " + i + " hexadecimal digits after \\" + c);
          }
        }
        this.idx += 1;
      }
      return j;
    case 't': 
      return 9;
    case 'n': 
      return 10;
    case 'r': 
      return 13;
    case 'f': 
      return 12;
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
      if (((this.idx < this.len) && (Character.isDigit(this.pattern.charAt(this.idx)))) || (c == '0'))
      {
        i = c - '0';
        if ((this.idx < this.len) && (Character.isDigit(this.pattern.charAt(this.idx))))
        {
          i = (i << 3) + (this.pattern.charAt(this.idx++) - '0');
          if ((this.idx < this.len) && (Character.isDigit(this.pattern.charAt(this.idx)))) {
            i = (i << 3) + (this.pattern.charAt(this.idx++) - '0');
          }
        }
        return i;
      }
      return 1048575;
    }
    return c;
  }
  
  int characterClass()
    throws RESyntaxException
  {
    if (this.pattern.charAt(this.idx) != '[') {
      internalError();
    }
    if ((this.idx + 1 >= this.len) || (this.pattern.charAt(++this.idx) == ']')) {
      syntaxError("Empty or unterminated class");
    }
    if ((this.idx < this.len) && (this.pattern.charAt(this.idx) == ':'))
    {
      this.idx += 1;
      i = this.idx;
      while ((this.idx < this.len) && (this.pattern.charAt(this.idx) >= 'a') && (this.pattern.charAt(this.idx) <= 'z')) {
        this.idx += 1;
      }
      if ((this.idx + 1 < this.len) && (this.pattern.charAt(this.idx) == ':') && (this.pattern.charAt(this.idx + 1) == ']'))
      {
        String str = this.pattern.substring(i, this.idx);
        localCharacter2 = (Character)hashPOSIX.get(str);
        if (localCharacter2 != null)
        {
          this.idx += 2;
          return node('P', localCharacter2.charValue());
        }
        syntaxError("Invalid POSIX character class '" + str + "'");
      }
      syntaxError("Invalid POSIX character class syntax");
    }
    int i = node('[', 0);
    Character localCharacter1 = 65535;
    Character localCharacter2 = localCharacter1;
    int k = 0;
    boolean bool = true;
    int m = 0;
    int n = this.idx;
    int i1 = 0;
    RERange localRERange = new RERange();
    while ((this.idx < this.len) && (this.pattern.charAt(this.idx) != ']'))
    {
      int i2;
      switch (this.pattern.charAt(this.idx))
      {
      case '^': 
        bool = !bool;
        if (this.idx == n) {
          localRERange.include(0, 65535, true);
        }
        this.idx += 1;
        break;
      case '\\': 
        switch (i4 = escape())
        {
        case 1048574: 
        case 1048575: 
          syntaxError("Bad character class");
        case 1048573: 
          if (m != 0) {
            syntaxError("Bad character class");
          }
          switch (this.pattern.charAt(this.idx - 1))
          {
          case 'D': 
          case 'S': 
          case 'W': 
            syntaxError("Bad character class");
          case 's': 
            localRERange.include('\t', bool);
            localRERange.include('\r', bool);
            localRERange.include('\f', bool);
            localRERange.include('\n', bool);
            localRERange.include('\b', bool);
            localRERange.include(' ', bool);
            break;
          case 'w': 
            localRERange.include(97, 122, bool);
            localRERange.include(65, 90, bool);
            localRERange.include('_', bool);
          case 'd': 
            localRERange.include(48, 57, bool);
          }
          localCharacter2 = localCharacter1;
          break;
        default: 
          k = (char)i4;
        }
        break;
      case '-': 
        if (m != 0) {
          syntaxError("Bad class range");
        }
        m = 1;
        i2 = localCharacter2 == localCharacter1 ? 0 : localCharacter2;
        if ((this.idx + 1 < this.len) && (this.pattern.charAt(++this.idx) == ']')) {
          k = 65535;
        }
        break;
      default: 
        k = this.pattern.charAt(this.idx++);
        int j;
        if (m != 0)
        {
          int i3 = k;
          if (i2 >= i3) {
            syntaxError("Bad character class");
          }
          localRERange.include(i2, i3, bool);
          j = localCharacter1;
          m = 0;
        }
        else
        {
          if ((this.idx >= this.len) || (this.pattern.charAt(this.idx) != '-')) {
            localRERange.include(k, bool);
          }
          j = k;
        }
        break;
      }
    }
    if (this.idx == this.len) {
      syntaxError("Unterminated character class");
    }
    this.idx += 1;
    this.instruction[(i + 1)] = ((char)localRERange.num);
    for (int i4 = 0; i4 < localRERange.num; i4++)
    {
      emit((char)localRERange.minRange[i4]);
      emit((char)localRERange.maxRange[i4]);
    }
    return i;
  }
  
  int atom()
    throws RESyntaxException
  {
    int i = node('A', 0);
    int j = 0;
    while (this.idx < this.len)
    {
      int k;
      int m;
      if (this.idx + 1 < this.len)
      {
        k = this.pattern.charAt(this.idx + 1);
        if (this.pattern.charAt(this.idx) == '\\')
        {
          m = this.idx;
          escape();
          if (this.idx < this.len) {
            k = this.pattern.charAt(this.idx);
          }
          this.idx = m;
        }
        switch (k)
        {
        case 42: 
        case 43: 
        case 63: 
        case 123: 
          if (j != 0) {
            break;
          }
        }
      }
      switch (this.pattern.charAt(this.idx))
      {
      case '$': 
      case '(': 
      case ')': 
      case '.': 
      case '[': 
      case ']': 
      case '^': 
      case '|': 
        break;
      case '*': 
      case '+': 
      case '?': 
      case '{': 
        if (j != 0) {
          break label366;
        }
        syntaxError("Missing operand to closure");
        break;
      case '\\': 
        k = this.idx;
        m = escape();
        if ((m & 0xFFFF0) == 1048560)
        {
          this.idx = k;
          break label366;
        }
        emit((char)m);
        j++;
        break;
      default: 
        emit(this.pattern.charAt(this.idx++));
        j++;
      }
    }
    label366:
    if (j == 0) {
      internalError();
    }
    this.instruction[(i + 1)] = ((char)j);
    return i;
  }
  
  int terminal(int[] paramArrayOfInt)
    throws RESyntaxException
  {
    switch (this.pattern.charAt(this.idx))
    {
    case '$': 
    case '.': 
    case '^': 
      return node(this.pattern.charAt(this.idx++), 0);
    case '[': 
      return characterClass();
    case '(': 
      return expr(paramArrayOfInt);
    case ')': 
      syntaxError("Unexpected close paren");
    case '|': 
      internalError();
    case ']': 
      syntaxError("Mismatched class");
    case '\000': 
      syntaxError("Unexpected end of input");
    case '*': 
    case '+': 
    case '?': 
    case '{': 
      syntaxError("Missing operand to closure");
    case '\\': 
      int i = this.idx;
      switch (escape())
      {
      case 1048573: 
      case 1048574: 
        paramArrayOfInt[0] &= 0xFFFFFFFE;
        return node('\\', this.pattern.charAt(this.idx - 1));
      case 1048575: 
        int j = (char)(this.pattern.charAt(this.idx - 1) - '0');
        if (this.parens <= j) {
          syntaxError("Bad backreference");
        }
        paramArrayOfInt[0] |= 0x1;
        return node('#', j);
      }
      this.idx = i;
      paramArrayOfInt[0] &= 0xFFFFFFFE;
    }
    paramArrayOfInt[0] &= 0xFFFFFFFE;
    return atom();
  }
  
  int closure(int[] paramArrayOfInt)
    throws RESyntaxException
  {
    int i = this.idx;
    int[] arrayOfInt = { 0 };
    int j = terminal(arrayOfInt);
    paramArrayOfInt[0] |= arrayOfInt[0];
    if (this.idx >= this.len) {
      return j;
    }
    int k = 1;
    int m = this.pattern.charAt(this.idx);
    int n;
    switch (m)
    {
    case 42: 
    case 63: 
      paramArrayOfInt[0] |= 0x1;
    case 43: 
      this.idx += 1;
    case 123: 
      n = this.instruction[(j + 0)];
      if ((n == 94) || (n == 36)) {
        syntaxError("Bad closure operand");
      }
      if ((arrayOfInt[0] & 0x1) != 0) {
        syntaxError("Closure operand can't be nullable");
      }
      break;
    }
    if ((this.idx < this.len) && (this.pattern.charAt(this.idx) == '?'))
    {
      this.idx += 1;
      k = 0;
    }
    if (k != 0)
    {
      switch (m)
      {
      case 123: 
        n = 0;
        allocBrackets();
        for (int i1 = 0; i1 < this.brackets; i1++) {
          if (this.bracketStart[i1] == this.idx)
          {
            n = 1;
            break;
          }
        }
        if (n == 0)
        {
          if (this.brackets >= this.maxBrackets) {
            reallocBrackets();
          }
          this.bracketStart[this.brackets] = this.idx;
          bracket();
          this.bracketEnd[this.brackets] = this.idx;
          i1 = this.brackets++;
        }
        int tmp370_368 = i1;
        int[] tmp370_365 = this.bracketMin;
        int tmp372_371 = tmp370_365[tmp370_368];
        tmp370_365[tmp370_368] = (tmp372_371 - 1);
        if (tmp372_371 > 0)
        {
          if ((this.bracketMin[i1] > 0) || (this.bracketOpt[i1] != 0))
          {
            for (int i2 = 0; i2 < this.brackets; i2++) {
              if ((i2 != i1) && (this.bracketStart[i2] < this.idx) && (this.bracketStart[i2] >= i))
              {
                this.brackets -= 1;
                this.bracketStart[i2] = this.bracketStart[this.brackets];
                this.bracketEnd[i2] = this.bracketEnd[this.brackets];
                this.bracketMin[i2] = this.bracketMin[this.brackets];
                this.bracketOpt[i2] = this.bracketOpt[this.brackets];
              }
            }
            this.idx = i;
          }
          else
          {
            this.idx = this.bracketEnd[i1];
          }
        }
        else if (this.bracketOpt[i1] == -1)
        {
          m = 42;
          this.bracketOpt[i1] = 0;
          this.idx = this.bracketEnd[i1];
        }
        else
        {
          int tmp588_586 = i1;
          int[] tmp588_583 = this.bracketOpt;
          int tmp590_589 = tmp588_583[tmp588_586];
          tmp588_583[tmp588_586] = (tmp590_589 - 1);
          if (tmp590_589 > 0)
          {
            if (this.bracketOpt[i1] > 0) {
              this.idx = i;
            } else {
              this.idx = this.bracketEnd[i1];
            }
            m = 63;
          }
          else
          {
            this.lenInstruction = j;
            node('N', 0);
            this.idx = this.bracketEnd[i1];
          }
        }
        break;
      case 42: 
      case 63: 
        if (k != 0)
        {
          if (m == 63)
          {
            nodeInsert('|', 0, j);
            setNextOfEnd(j, node('|', 0));
            n = node('N', 0);
            setNextOfEnd(j, n);
            setNextOfEnd(j + 3, n);
          }
          if (m == 42)
          {
            nodeInsert('|', 0, j);
            setNextOfEnd(j + 3, node('|', 0));
            setNextOfEnd(j + 3, node('G', 0));
            setNextOfEnd(j + 3, j);
            setNextOfEnd(j, node('|', 0));
            setNextOfEnd(j, node('N', 0));
          }
        }
        break;
      case 43: 
        n = node('|', 0);
        setNextOfEnd(j, n);
        setNextOfEnd(node('G', 0), j);
        setNextOfEnd(n, node('|', 0));
        setNextOfEnd(j, node('N', 0));
      }
    }
    else
    {
      setNextOfEnd(j, node('E', 0));
      switch (m)
      {
      case 63: 
        nodeInsert('/', 0, j);
        break;
      case 42: 
        nodeInsert('8', 0, j);
        break;
      case 43: 
        nodeInsert('=', 0, j);
      }
      setNextOfEnd(j, this.lenInstruction);
    }
    return j;
  }
  
  int branch(int[] paramArrayOfInt)
    throws RESyntaxException
  {
    int j = node('|', 0);
    int k = -1;
    int[] arrayOfInt = new int[1];
    int m = 1;
    while ((this.idx < this.len) && (this.pattern.charAt(this.idx) != '|') && (this.pattern.charAt(this.idx) != ')'))
    {
      arrayOfInt[0] = 0;
      int i = closure(arrayOfInt);
      if (arrayOfInt[0] == 0) {
        m = 0;
      }
      if (k != -1) {
        setNextOfEnd(k, i);
      }
      k = i;
    }
    if (k == -1) {
      node('N', 0);
    }
    if (m != 0) {
      paramArrayOfInt[0] |= 0x1;
    }
    return j;
  }
  
  int expr(int[] paramArrayOfInt)
    throws RESyntaxException
  {
    int i = -1;
    int j = -1;
    int k = this.parens;
    if (((paramArrayOfInt[0] & 0x2) == 0) && (this.pattern.charAt(this.idx) == '(')) {
      if ((this.idx + 2 < this.len) && (this.pattern.charAt(this.idx + 1) == '?') && (this.pattern.charAt(this.idx + 2) == ':'))
      {
        i = 2;
        this.idx += 3;
        j = node('<', 0);
      }
      else
      {
        i = 1;
        this.idx += 1;
        j = node('(', this.parens++);
      }
    }
    paramArrayOfInt[0] &= 0xFFFFFFFD;
    int m = branch(paramArrayOfInt);
    if (j == -1) {
      j = m;
    } else {
      setNextOfEnd(j, m);
    }
    while ((this.idx < this.len) && (this.pattern.charAt(this.idx) == '|'))
    {
      this.idx += 1;
      m = branch(paramArrayOfInt);
      setNextOfEnd(j, m);
    }
    int n;
    if (i > 0)
    {
      if ((this.idx < this.len) && (this.pattern.charAt(this.idx) == ')')) {
        this.idx += 1;
      } else {
        syntaxError("Missing close paren");
      }
      if (i == 1) {
        n = node(')', k);
      } else {
        n = node('>', 0);
      }
    }
    else
    {
      n = node('E', 0);
    }
    setNextOfEnd(j, n);
    int i1 = j;
    int i2 = this.instruction[(i1 + 2)];
    while ((i2 != 0) && (i1 < this.lenInstruction))
    {
      if (this.instruction[(i1 + 0)] == '|') {
        setNextOfEnd(i1 + 3, n);
      }
      i2 = this.instruction[(i1 + 2)];
      i1 += i2;
    }
    return j;
  }
  
  public REProgram compile(String paramString)
    throws RESyntaxException
  {
    this.pattern = paramString;
    this.len = paramString.length();
    this.idx = 0;
    this.lenInstruction = 0;
    this.parens = 1;
    this.brackets = 0;
    int[] arrayOfInt = { 2 };
    expr(arrayOfInt);
    if (this.idx != this.len)
    {
      if (paramString.charAt(this.idx) == ')') {
        syntaxError("Unmatched close paren");
      }
      syntaxError("Unexpected input remains");
    }
    char[] arrayOfChar = new char[this.lenInstruction];
    System.arraycopy(this.instruction, 0, arrayOfChar, 0, this.lenInstruction);
    return new REProgram(this.parens, arrayOfChar);
  }
  
  static
  {
    hashPOSIX.put("alnum", new Character('w'));
    hashPOSIX.put("alpha", new Character('a'));
    hashPOSIX.put("blank", new Character('b'));
    hashPOSIX.put("cntrl", new Character('c'));
    hashPOSIX.put("digit", new Character('d'));
    hashPOSIX.put("graph", new Character('g'));
    hashPOSIX.put("lower", new Character('l'));
    hashPOSIX.put("print", new Character('p'));
    hashPOSIX.put("punct", new Character('!'));
    hashPOSIX.put("space", new Character('s'));
    hashPOSIX.put("upper", new Character('u'));
    hashPOSIX.put("xdigit", new Character('x'));
    hashPOSIX.put("javastart", new Character('j'));
    hashPOSIX.put("javapart", new Character('k'));
  }
  
  class RERange
  {
    int size = 16;
    int[] minRange = new int[this.size];
    int[] maxRange = new int[this.size];
    int num = 0;
    
    RERange() {}
    
    void delete(int paramInt)
    {
      if ((this.num == 0) || (paramInt >= this.num)) {
        return;
      }
      for (;;)
      {
        paramInt++;
        if (paramInt >= this.num) {
          break;
        }
        if (paramInt - 1 >= 0)
        {
          this.minRange[(paramInt - 1)] = this.minRange[paramInt];
          this.maxRange[(paramInt - 1)] = this.maxRange[paramInt];
        }
      }
      this.num -= 1;
    }
    
    void merge(int paramInt1, int paramInt2)
    {
      for (int i = 0; i < this.num; i++)
      {
        if ((paramInt1 >= this.minRange[i]) && (paramInt2 <= this.maxRange[i])) {
          return;
        }
        if ((paramInt1 <= this.minRange[i]) && (paramInt2 >= this.maxRange[i]))
        {
          delete(i);
          merge(paramInt1, paramInt2);
          return;
        }
        if ((paramInt1 >= this.minRange[i]) && (paramInt1 <= this.maxRange[i]))
        {
          delete(i);
          paramInt1 = this.minRange[i];
          merge(paramInt1, paramInt2);
          return;
        }
        if ((paramInt2 >= this.minRange[i]) && (paramInt2 <= this.maxRange[i]))
        {
          delete(i);
          paramInt2 = this.maxRange[i];
          merge(paramInt1, paramInt2);
          return;
        }
      }
      if (this.num >= this.size)
      {
        this.size *= 2;
        int[] arrayOfInt1 = new int[this.size];
        int[] arrayOfInt2 = new int[this.size];
        System.arraycopy(this.minRange, 0, arrayOfInt1, 0, this.num);
        System.arraycopy(this.maxRange, 0, arrayOfInt2, 0, this.num);
        this.minRange = arrayOfInt1;
        this.maxRange = arrayOfInt2;
      }
      this.minRange[this.num] = paramInt1;
      this.maxRange[this.num] = paramInt2;
      this.num += 1;
    }
    
    void remove(int paramInt1, int paramInt2)
    {
      for (int i = 0; i < this.num; i++)
      {
        if ((this.minRange[i] >= paramInt1) && (this.maxRange[i] <= paramInt2))
        {
          delete(i);
          i--;
          return;
        }
        if ((paramInt1 >= this.minRange[i]) && (paramInt2 <= this.maxRange[i]))
        {
          int j = this.minRange[i];
          int k = this.maxRange[i];
          delete(i);
          if (j < paramInt1) {
            merge(j, paramInt1 - 1);
          }
          if (paramInt2 < k) {
            merge(paramInt2 + 1, k);
          }
          return;
        }
        if ((this.minRange[i] >= paramInt1) && (this.minRange[i] <= paramInt2))
        {
          this.minRange[i] = (paramInt2 + 1);
          return;
        }
        if ((this.maxRange[i] >= paramInt1) && (this.maxRange[i] <= paramInt2))
        {
          this.maxRange[i] = (paramInt1 - 1);
          return;
        }
      }
    }
    
    void include(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (paramBoolean) {
        merge(paramInt1, paramInt2);
      } else {
        remove(paramInt1, paramInt2);
      }
    }
    
    void include(char paramChar, boolean paramBoolean)
    {
      include(paramChar, paramChar, paramBoolean);
    }
  }
}
