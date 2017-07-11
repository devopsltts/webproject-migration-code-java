package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.generic.ClassGenException;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class InstructionFinder
{
  private static final int OFFSET = 32767;
  private static final int NO_OPCODES = 256;
  private static final HashMap map = new HashMap();
  private InstructionList il;
  private String il_string;
  private InstructionHandle[] handles;
  
  public InstructionFinder(InstructionList paramInstructionList)
  {
    this.il = paramInstructionList;
    reread();
  }
  
  public final void reread()
  {
    int i = this.il.getLength();
    char[] arrayOfChar = new char[i];
    this.handles = this.il.getInstructionHandles();
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = makeChar(this.handles[j].getInstruction().getOpcode());
    }
    this.il_string = new String(arrayOfChar);
  }
  
  private static final String mapName(String paramString)
  {
    String str = (String)map.get(paramString);
    if (str != null) {
      return str;
    }
    for (short s = 0; s < 256; s = (short)(s + 1)) {
      if (paramString.equals(com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[s])) {
        return "" + makeChar(s);
      }
    }
    throw new RuntimeException("Instruction unknown: " + paramString);
  }
  
  private static final String compilePattern(String paramString)
  {
    String str = paramString.toLowerCase();
    StringBuffer localStringBuffer1 = new StringBuffer();
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c = str.charAt(j);
      if (Character.isLetterOrDigit(c))
      {
        StringBuffer localStringBuffer2 = new StringBuffer();
        while (((Character.isLetterOrDigit(c)) || (c == '_')) && (j < i))
        {
          localStringBuffer2.append(c);
          j++;
          if (j >= i) {
            break;
          }
          c = str.charAt(j);
        }
        j--;
        localStringBuffer1.append(mapName(localStringBuffer2.toString()));
      }
      else if (!Character.isWhitespace(c))
      {
        localStringBuffer1.append(c);
      }
    }
    return localStringBuffer1.toString();
  }
  
  private InstructionHandle[] getMatch(int paramInt1, int paramInt2)
  {
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[paramInt2];
    System.arraycopy(this.handles, paramInt1, arrayOfInstructionHandle, 0, paramInt2);
    return arrayOfInstructionHandle;
  }
  
  public final Iterator search(String paramString, InstructionHandle paramInstructionHandle, CodeConstraint paramCodeConstraint)
  {
    String str = compilePattern(paramString);
    int i = -1;
    for (int j = 0; j < this.handles.length; j++) {
      if (this.handles[j] == paramInstructionHandle)
      {
        i = j;
        break;
      }
    }
    if (i == -1) {
      throw new ClassGenException("Instruction handle " + paramInstructionHandle + " not found in instruction list.");
    }
    try
    {
      RE localRE = new RE(str);
      ArrayList localArrayList = new ArrayList();
      while ((i < this.il_string.length()) && (localRE.match(this.il_string, i)))
      {
        int k = localRE.getParenStart(0);
        int m = localRE.getParenEnd(0);
        int n = localRE.getParenLength(0);
        InstructionHandle[] arrayOfInstructionHandle = getMatch(k, n);
        if ((paramCodeConstraint == null) || (paramCodeConstraint.checkCode(arrayOfInstructionHandle))) {
          localArrayList.add(arrayOfInstructionHandle);
        }
        i = m;
      }
      return localArrayList.iterator();
    }
    catch (RESyntaxException localRESyntaxException)
    {
      System.err.println(localRESyntaxException);
    }
    return null;
  }
  
  public final Iterator search(String paramString)
  {
    return search(paramString, this.il.getStart(), null);
  }
  
  public final Iterator search(String paramString, InstructionHandle paramInstructionHandle)
  {
    return search(paramString, paramInstructionHandle, null);
  }
  
  public final Iterator search(String paramString, CodeConstraint paramCodeConstraint)
  {
    return search(paramString, this.il.getStart(), paramCodeConstraint);
  }
  
  private static final char makeChar(short paramShort)
  {
    return (char)(paramShort + Short.MAX_VALUE);
  }
  
  public final InstructionList getInstructionList()
  {
    return this.il;
  }
  
  private static String precompile(short paramShort1, short paramShort2, short paramShort3)
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    for (short s = paramShort1; s <= paramShort2; s = (short)(s + 1))
    {
      localStringBuffer.append(makeChar(s));
      localStringBuffer.append('|');
    }
    localStringBuffer.append(makeChar(paramShort3));
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
  
  private static final String pattern2string(String paramString)
  {
    return pattern2string(paramString, true);
  }
  
  private static final String pattern2string(String paramString, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if (c >= '翿')
      {
        if (paramBoolean) {
          localStringBuffer.append(com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[(c - '翿')]);
        } else {
          localStringBuffer.append(c - '翿');
        }
      }
      else {
        localStringBuffer.append(c);
      }
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    map.put("arithmeticinstruction", "(irem|lrem|iand|ior|ineg|isub|lneg|fneg|fmul|ldiv|fadd|lxor|frem|idiv|land|ixor|ishr|fsub|lshl|fdiv|iadd|lor|dmul|lsub|ishl|imul|lmul|lushr|dneg|iushr|lshr|ddiv|drem|dadd|ladd|dsub)");
    map.put("invokeinstruction", "(invokevirtual|invokeinterface|invokestatic|invokespecial)");
    map.put("arrayinstruction", "(baload|aastore|saload|caload|fastore|lastore|iaload|castore|iastore|aaload|bastore|sastore|faload|laload|daload|dastore)");
    map.put("gotoinstruction", "(goto|goto_w)");
    map.put("conversioninstruction", "(d2l|l2d|i2s|d2i|l2i|i2b|l2f|d2f|f2i|i2d|i2l|f2d|i2c|f2l|i2f)");
    map.put("localvariableinstruction", "(fstore|iinc|lload|dstore|dload|iload|aload|astore|istore|fload|lstore)");
    map.put("loadinstruction", "(fload|dload|lload|iload|aload)");
    map.put("fieldinstruction", "(getfield|putstatic|getstatic|putfield)");
    map.put("cpinstruction", "(ldc2_w|invokeinterface|multianewarray|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|ldc_w|invokestatic|invokevirtual|putfield|ldc|new|anewarray)");
    map.put("stackinstruction", "(dup2|swap|dup2_x2|pop|pop2|dup|dup2_x1|dup_x2|dup_x1)");
    map.put("branchinstruction", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");
    map.put("returninstruction", "(lreturn|ireturn|freturn|dreturn|areturn|return)");
    map.put("storeinstruction", "(istore|fstore|dstore|astore|lstore)");
    map.put("select", "(tableswitch|lookupswitch)");
    map.put("ifinstruction", "(ifeq|ifgt|if_icmpne|if_icmpeq|ifge|ifnull|ifne|if_icmple|if_icmpge|if_acmpeq|if_icmplt|if_acmpne|ifnonnull|iflt|if_icmpgt|ifle)");
    map.put("jsrinstruction", "(jsr|jsr_w)");
    map.put("variablelengthinstruction", "(tableswitch|jsr|goto|lookupswitch)");
    map.put("unconditionalbranch", "(goto|jsr|jsr_w|athrow|goto_w)");
    map.put("constantpushinstruction", "(dconst|bipush|sipush|fconst|iconst|lconst)");
    map.put("typedinstruction", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dastore|ret|f2d|f2i|drem|iinc|i2c|checkcast|frem|lreturn|astore|lushr|daload|dneg|fastore|istore|lshl|ldiv|lstore|areturn|ishr|ldc_w|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|faload|sipush|iushr|caload|instanceof|invokespecial|putfield|fmul|ireturn|laload|d2f|lneg|ixor|i2l|fdiv|lastore|multianewarray|i2b|getstatic|i2d|putstatic|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|freturn|ldc|aconst_null|castore|lmul|ldc2_w|dadd|iconst|f2l|ddiv|dstore|land|jsr|anewarray|dmul|bipush|dsub|sastore|d2i|i2s|lshr|iadd|l2i|lload|bastore|fstore|fneg|iload|fadd|baload|fconst|ior|ineg|dreturn|l2f|lconst|getfield|invokevirtual|invokestatic|iastore)");
    map.put("popinstruction", "(fstore|dstore|pop|pop2|astore|putstatic|istore|lstore)");
    map.put("allocationinstruction", "(multianewarray|new|anewarray|newarray)");
    map.put("indexedinstruction", "(lload|lstore|fload|ldc2_w|invokeinterface|multianewarray|astore|dload|putstatic|instanceof|getstatic|checkcast|getfield|invokespecial|dstore|istore|iinc|ldc_w|ret|fstore|invokestatic|iload|putfield|invokevirtual|ldc|new|aload|anewarray)");
    map.put("pushinstruction", "(dup|lload|dup2|bipush|fload|ldc2_w|sipush|lconst|fconst|dload|getstatic|ldc_w|aconst_null|dconst|iload|ldc|iconst|aload)");
    map.put("stackproducer", "(imul|lsub|aload|fload|lor|new|aaload|fcmpg|iand|iaload|lrem|idiv|d2l|isub|dcmpg|dup|f2d|f2i|drem|i2c|checkcast|frem|lushr|daload|dneg|lshl|ldiv|ishr|ldc_w|invokeinterface|lxor|ishl|l2d|i2f|faload|sipush|iushr|caload|instanceof|invokespecial|fmul|laload|d2f|lneg|ixor|i2l|fdiv|getstatic|i2b|swap|i2d|dup2|fcmpl|saload|ladd|irem|dload|jsr_w|dconst|dcmpl|fsub|ldc|arraylength|aconst_null|tableswitch|lmul|ldc2_w|iconst|dadd|f2l|ddiv|land|jsr|anewarray|dmul|bipush|dsub|d2i|newarray|i2s|lshr|iadd|lload|l2i|fneg|iload|fadd|baload|fconst|lookupswitch|ior|ineg|lconst|l2f|getfield|invokevirtual|invokestatic)");
    map.put("stackconsumer", "(imul|lsub|lor|iflt|fcmpg|if_icmpgt|iand|ifeq|if_icmplt|lrem|ifnonnull|idiv|d2l|isub|dcmpg|dastore|if_icmpeq|f2d|f2i|drem|i2c|checkcast|frem|lreturn|astore|lushr|pop2|monitorexit|dneg|fastore|istore|lshl|ldiv|lstore|areturn|if_icmpge|ishr|monitorenter|invokeinterface|aastore|lxor|ishl|l2d|i2f|return|iushr|instanceof|invokespecial|fmul|ireturn|d2f|lneg|ixor|pop|i2l|ifnull|fdiv|lastore|i2b|if_acmpeq|ifge|swap|i2d|putstatic|fcmpl|ladd|irem|dcmpl|fsub|freturn|ifgt|castore|lmul|dadd|f2l|ddiv|dstore|land|if_icmpne|if_acmpne|dmul|dsub|sastore|ifle|d2i|i2s|lshr|iadd|l2i|bastore|fstore|fneg|fadd|ior|ineg|ifne|dreturn|l2f|if_icmple|getfield|invokevirtual|invokestatic|iastore)");
    map.put("exceptionthrower", "(irem|lrem|laload|putstatic|baload|dastore|areturn|getstatic|ldiv|anewarray|iastore|castore|idiv|saload|lastore|fastore|putfield|lreturn|caload|getfield|return|aastore|freturn|newarray|instanceof|multianewarray|athrow|faload|iaload|aaload|dreturn|monitorenter|checkcast|bastore|arraylength|new|invokevirtual|sastore|ldc_w|ireturn|invokespecial|monitorexit|invokeinterface|ldc|invokestatic|daload)");
    map.put("loadclass", "(multianewarray|invokeinterface|instanceof|invokespecial|putfield|checkcast|putstatic|invokevirtual|new|getstatic|invokestatic|getfield|anewarray)");
    map.put("instructiontargeter", "(ifle|if_acmpne|if_icmpeq|if_acmpeq|ifnonnull|goto_w|iflt|ifnull|if_icmpne|tableswitch|if_icmple|ifeq|if_icmplt|jsr_w|if_icmpgt|ifgt|jsr|goto|ifne|ifge|lookupswitch|if_icmpge)");
    map.put("if_icmp", "(if_icmpne|if_icmpeq|if_icmple|if_icmpge|if_icmplt|if_icmpgt)");
    map.put("if_acmp", "(if_acmpeq|if_acmpne)");
    map.put("if", "(ifeq|ifne|iflt|ifge|ifgt|ifle)");
    map.put("iconst", precompile((short)3, (short)8, (short)2));
    map.put("lconst", new String(new char[] { '(', makeChar(9), '|', makeChar(10), ')' }));
    map.put("dconst", new String(new char[] { '(', makeChar(14), '|', makeChar(15), ')' }));
    map.put("fconst", new String(new char[] { '(', makeChar(11), '|', makeChar(12), ')' }));
    map.put("iload", precompile((short)26, (short)29, (short)21));
    map.put("dload", precompile((short)38, (short)41, (short)24));
    map.put("fload", precompile((short)34, (short)37, (short)23));
    map.put("aload", precompile((short)42, (short)45, (short)25));
    map.put("istore", precompile((short)59, (short)62, (short)54));
    map.put("dstore", precompile((short)71, (short)74, (short)57));
    map.put("fstore", precompile((short)67, (short)70, (short)56));
    map.put("astore", precompile((short)75, (short)78, (short)58));
    Object localObject = map.keySet().iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str1 = (String)((Iterator)localObject).next();
      String str2 = (String)map.get(str1);
      int i = str2.charAt(1);
      if (i < 32767) {
        map.put(str1, compilePattern(str2));
      }
    }
    localObject = new StringBuffer("(");
    for (short s = 0; s < 256; s = (short)(s + 1)) {
      if (com.sun.org.apache.bcel.internal.Constants.NO_OF_OPERANDS[s] != -1)
      {
        ((StringBuffer)localObject).append(makeChar(s));
        if (s < 255) {
          ((StringBuffer)localObject).append('|');
        }
      }
    }
    ((StringBuffer)localObject).append(')');
    map.put("instruction", ((StringBuffer)localObject).toString());
  }
  
  public static abstract interface CodeConstraint
  {
    public abstract boolean checkCode(InstructionHandle[] paramArrayOfInstructionHandle);
  }
}
