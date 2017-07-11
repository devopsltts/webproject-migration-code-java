package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.FlowList;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import java.security.AccessControlContext;
import java.security.AccessController;

public abstract class Type
  implements Constants
{
  public static final Type Int = new IntType();
  public static final Type Real = new RealType();
  public static final Type Boolean = new BooleanType();
  public static final Type NodeSet = new NodeSetType();
  public static final Type String = new StringType();
  public static final Type ResultTree = new ResultTreeType();
  public static final Type Reference = new ReferenceType();
  public static final Type Void = new VoidType();
  public static final Type Object = new ObjectType(Object.class);
  public static final Type ObjectString = new ObjectType(String.class);
  public static final Type Node = new NodeType(-1);
  public static final Type Root = new NodeType(9);
  public static final Type Element = new NodeType(1);
  public static final Type Attribute = new NodeType(2);
  public static final Type Text = new NodeType(3);
  public static final Type Comment = new NodeType(8);
  public static final Type Processing_Instruction = new NodeType(7);
  
  public Type() {}
  
  public static Type newObjectType(String paramString)
  {
    if (paramString == "java.lang.Object") {
      return Object;
    }
    if (paramString == "java.lang.String") {
      return ObjectString;
    }
    AccessControlContext localAccessControlContext = AccessController.getContext();
    localAccessControlContext.checkPermission(new RuntimePermission("getContextClassLoader"));
    return new ObjectType(paramString);
  }
  
  public static Type newObjectType(Class paramClass)
  {
    if (paramClass == Object.class) {
      return Object;
    }
    if (paramClass == String.class) {
      return ObjectString;
    }
    return new ObjectType(paramClass);
  }
  
  public abstract String toString();
  
  public abstract boolean identicalTo(Type paramType);
  
  public boolean isNumber()
  {
    return false;
  }
  
  public boolean implementedAsMethod()
  {
    return false;
  }
  
  public boolean isSimple()
  {
    return false;
  }
  
  public abstract com.sun.org.apache.bcel.internal.generic.Type toJCType();
  
  public int distanceTo(Type paramType)
  {
    return paramType == this ? 0 : Integer.MAX_VALUE;
  }
  
  public abstract String toSignature();
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramType.toString());
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Type paramType)
  {
    FlowList localFlowList = null;
    if (paramType == Boolean) {
      localFlowList = translateToDesynthesized(paramClassGenerator, paramMethodGenerator, (BooleanType)paramType);
    } else {
      translateTo(paramClassGenerator, paramMethodGenerator, paramType);
    }
    return localFlowList;
  }
  
  public FlowList translateToDesynthesized(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, BooleanType paramBooleanType)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramBooleanType.toString());
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
    return null;
  }
  
  public void translateTo(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), paramClass.getClass().toString());
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
  }
  
  public void translateFrom(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Class paramClass)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", paramClass.getClass().toString(), toString());
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
  }
  
  public void translateBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", toString(), "[" + toString() + "]");
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
  }
  
  public void translateUnBox(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ErrorMsg localErrorMsg = new ErrorMsg("DATA_CONVERSION_ERR", "[" + toString() + "]", toString());
    paramClassGenerator.getParser().reportError(2, localErrorMsg);
  }
  
  public String getClassName()
  {
    return "";
  }
  
  public Instruction ADD()
  {
    return null;
  }
  
  public Instruction SUB()
  {
    return null;
  }
  
  public Instruction MUL()
  {
    return null;
  }
  
  public Instruction DIV()
  {
    return null;
  }
  
  public Instruction REM()
  {
    return null;
  }
  
  public Instruction NEG()
  {
    return null;
  }
  
  public Instruction LOAD(int paramInt)
  {
    return null;
  }
  
  public Instruction STORE(int paramInt)
  {
    return null;
  }
  
  public Instruction POP()
  {
    return POP;
  }
  
  public BranchInstruction GT(boolean paramBoolean)
  {
    return null;
  }
  
  public BranchInstruction GE(boolean paramBoolean)
  {
    return null;
  }
  
  public BranchInstruction LT(boolean paramBoolean)
  {
    return null;
  }
  
  public BranchInstruction LE(boolean paramBoolean)
  {
    return null;
  }
  
  public Instruction CMP(boolean paramBoolean)
  {
    return null;
  }
  
  public Instruction DUP()
  {
    return DUP;
  }
}
