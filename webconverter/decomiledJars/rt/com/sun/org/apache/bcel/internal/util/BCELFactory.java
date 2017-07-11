package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.AllocationInstruction;
import com.sun.org.apache.bcel.internal.generic.ArrayInstruction;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.CPInstruction;
import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import com.sun.org.apache.bcel.internal.generic.EmptyVisitor;
import com.sun.org.apache.bcel.internal.generic.FieldInstruction;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.generic.LDC;
import com.sun.org.apache.bcel.internal.generic.LDC2_W;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.ReturnInstruction;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.Type;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class BCELFactory
  extends EmptyVisitor
{
  private MethodGen _mg;
  private PrintWriter _out;
  private ConstantPoolGen _cp;
  private HashMap branch_map = new HashMap();
  private ArrayList branches = new ArrayList();
  
  BCELFactory(MethodGen paramMethodGen, PrintWriter paramPrintWriter)
  {
    this._mg = paramMethodGen;
    this._cp = paramMethodGen.getConstantPool();
    this._out = paramPrintWriter;
  }
  
  public void start()
  {
    if ((!this._mg.isAbstract()) && (!this._mg.isNative()))
    {
      for (InstructionHandle localInstructionHandle = this._mg.getInstructionList().getStart(); localInstructionHandle != null; localInstructionHandle = localInstructionHandle.getNext())
      {
        Instruction localInstruction = localInstructionHandle.getInstruction();
        if ((localInstruction instanceof BranchInstruction)) {
          this.branch_map.put(localInstruction, localInstructionHandle);
        }
        if (localInstructionHandle.hasTargeters())
        {
          if ((localInstruction instanceof BranchInstruction)) {
            this._out.println("    InstructionHandle ih_" + localInstructionHandle.getPosition() + ";");
          } else {
            this._out.print("    InstructionHandle ih_" + localInstructionHandle.getPosition() + " = ");
          }
        }
        else {
          this._out.print("    ");
        }
        if (!visitInstruction(localInstruction)) {
          localInstruction.accept(this);
        }
      }
      updateBranchTargets();
      updateExceptionHandlers();
    }
  }
  
  private boolean visitInstruction(Instruction paramInstruction)
  {
    int i = paramInstruction.getOpcode();
    if ((com.sun.org.apache.bcel.internal.generic.InstructionConstants.INSTRUCTIONS[i] != null) && (!(paramInstruction instanceof ConstantPushInstruction)) && (!(paramInstruction instanceof ReturnInstruction)))
    {
      this._out.println("il.append(InstructionConstants." + paramInstruction.getName().toUpperCase() + ");");
      return true;
    }
    return false;
  }
  
  public void visitLocalVariableInstruction(LocalVariableInstruction paramLocalVariableInstruction)
  {
    int i = paramLocalVariableInstruction.getOpcode();
    Type localType = paramLocalVariableInstruction.getType(this._cp);
    if (i == 132)
    {
      this._out.println("il.append(new IINC(" + paramLocalVariableInstruction.getIndex() + ", " + ((IINC)paramLocalVariableInstruction).getIncrement() + "));");
    }
    else
    {
      String str = i < 54 ? "Load" : "Store";
      this._out.println("il.append(_factory.create" + str + "(" + BCELifier.printType(localType) + ", " + paramLocalVariableInstruction.getIndex() + "));");
    }
  }
  
  public void visitArrayInstruction(ArrayInstruction paramArrayInstruction)
  {
    int i = paramArrayInstruction.getOpcode();
    Type localType = paramArrayInstruction.getType(this._cp);
    String str = i < 79 ? "Load" : "Store";
    this._out.println("il.append(_factory.createArray" + str + "(" + BCELifier.printType(localType) + "));");
  }
  
  public void visitFieldInstruction(FieldInstruction paramFieldInstruction)
  {
    int i = paramFieldInstruction.getOpcode();
    String str1 = paramFieldInstruction.getClassName(this._cp);
    String str2 = paramFieldInstruction.getFieldName(this._cp);
    Type localType = paramFieldInstruction.getFieldType(this._cp);
    this._out.println("il.append(_factory.createFieldAccess(\"" + str1 + "\", \"" + str2 + "\", " + BCELifier.printType(localType) + ", " + "Constants." + com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[i].toUpperCase() + "));");
  }
  
  public void visitInvokeInstruction(InvokeInstruction paramInvokeInstruction)
  {
    int i = paramInvokeInstruction.getOpcode();
    String str1 = paramInvokeInstruction.getClassName(this._cp);
    String str2 = paramInvokeInstruction.getMethodName(this._cp);
    Type localType = paramInvokeInstruction.getReturnType(this._cp);
    Type[] arrayOfType = paramInvokeInstruction.getArgumentTypes(this._cp);
    this._out.println("il.append(_factory.createInvoke(\"" + str1 + "\", \"" + str2 + "\", " + BCELifier.printType(localType) + ", " + BCELifier.printArgumentTypes(arrayOfType) + ", " + "Constants." + com.sun.org.apache.bcel.internal.Constants.OPCODE_NAMES[i].toUpperCase() + "));");
  }
  
  public void visitAllocationInstruction(AllocationInstruction paramAllocationInstruction)
  {
    Type localType;
    if ((paramAllocationInstruction instanceof CPInstruction)) {
      localType = ((CPInstruction)paramAllocationInstruction).getType(this._cp);
    } else {
      localType = ((NEWARRAY)paramAllocationInstruction).getType();
    }
    int i = ((Instruction)paramAllocationInstruction).getOpcode();
    int j = 1;
    switch (i)
    {
    case 187: 
      this._out.println("il.append(_factory.createNew(\"" + ((ObjectType)localType).getClassName() + "\"));");
      break;
    case 197: 
      j = ((MULTIANEWARRAY)paramAllocationInstruction).getDimensions();
    case 188: 
    case 189: 
      this._out.println("il.append(_factory.createNewArray(" + BCELifier.printType(localType) + ", (short) " + j + "));");
      break;
    default: 
      throw new RuntimeException("Oops: " + i);
    }
  }
  
  private void createConstant(Object paramObject)
  {
    String str = paramObject.toString();
    if ((paramObject instanceof String)) {
      str = '"' + Utility.convertString(paramObject.toString()) + '"';
    } else if ((paramObject instanceof Character)) {
      str = "(char)0x" + Integer.toHexString(((Character)paramObject).charValue());
    }
    this._out.println("il.append(new PUSH(_cp, " + str + "));");
  }
  
  public void visitLDC(LDC paramLDC)
  {
    createConstant(paramLDC.getValue(this._cp));
  }
  
  public void visitLDC2_W(LDC2_W paramLDC2_W)
  {
    createConstant(paramLDC2_W.getValue(this._cp));
  }
  
  public void visitConstantPushInstruction(ConstantPushInstruction paramConstantPushInstruction)
  {
    createConstant(paramConstantPushInstruction.getValue());
  }
  
  public void visitINSTANCEOF(INSTANCEOF paramINSTANCEOF)
  {
    Type localType = paramINSTANCEOF.getType(this._cp);
    this._out.println("il.append(new INSTANCEOF(_cp.addClass(" + BCELifier.printType(localType) + ")));");
  }
  
  public void visitCHECKCAST(CHECKCAST paramCHECKCAST)
  {
    Type localType = paramCHECKCAST.getType(this._cp);
    this._out.println("il.append(_factory.createCheckCast(" + BCELifier.printType(localType) + "));");
  }
  
  public void visitReturnInstruction(ReturnInstruction paramReturnInstruction)
  {
    Type localType = paramReturnInstruction.getType(this._cp);
    this._out.println("il.append(_factory.createReturn(" + BCELifier.printType(localType) + "));");
  }
  
  public void visitBranchInstruction(BranchInstruction paramBranchInstruction)
  {
    BranchHandle localBranchHandle = (BranchHandle)this.branch_map.get(paramBranchInstruction);
    int i = localBranchHandle.getPosition();
    String str = paramBranchInstruction.getName() + "_" + i;
    Object localObject;
    if ((paramBranchInstruction instanceof Select))
    {
      Select localSelect = (Select)paramBranchInstruction;
      this.branches.add(paramBranchInstruction);
      localObject = new StringBuffer("new int[] { ");
      int[] arrayOfInt = localSelect.getMatchs();
      for (int k = 0; k < arrayOfInt.length; k++)
      {
        ((StringBuffer)localObject).append(arrayOfInt[k]);
        if (k < arrayOfInt.length - 1) {
          ((StringBuffer)localObject).append(", ");
        }
      }
      ((StringBuffer)localObject).append(" }");
      this._out.print("    Select " + str + " = new " + paramBranchInstruction.getName().toUpperCase() + "(" + localObject + ", new InstructionHandle[] { ");
      for (k = 0; k < arrayOfInt.length; k++)
      {
        this._out.print("null");
        if (k < arrayOfInt.length - 1) {
          this._out.print(", ");
        }
      }
      this._out.println(");");
    }
    else
    {
      int j = localBranchHandle.getTarget().getPosition();
      if (i > j)
      {
        localObject = "ih_" + j;
      }
      else
      {
        this.branches.add(paramBranchInstruction);
        localObject = "null";
      }
      this._out.println("    BranchInstruction " + str + " = _factory.createBranchInstruction(" + "Constants." + paramBranchInstruction.getName().toUpperCase() + ", " + (String)localObject + ");");
    }
    if (localBranchHandle.hasTargeters()) {
      this._out.println("    ih_" + i + " = il.append(" + str + ");");
    } else {
      this._out.println("    il.append(" + str + ");");
    }
  }
  
  public void visitRET(RET paramRET)
  {
    this._out.println("il.append(new RET(" + paramRET.getIndex() + ")));");
  }
  
  private void updateBranchTargets()
  {
    Iterator localIterator = this.branches.iterator();
    while (localIterator.hasNext())
    {
      BranchInstruction localBranchInstruction = (BranchInstruction)localIterator.next();
      BranchHandle localBranchHandle = (BranchHandle)this.branch_map.get(localBranchInstruction);
      int i = localBranchHandle.getPosition();
      String str = localBranchInstruction.getName() + "_" + i;
      int j = localBranchHandle.getTarget().getPosition();
      this._out.println("    " + str + ".setTarget(ih_" + j + ");");
      if ((localBranchInstruction instanceof Select))
      {
        InstructionHandle[] arrayOfInstructionHandle = ((Select)localBranchInstruction).getTargets();
        for (int k = 0; k < arrayOfInstructionHandle.length; k++)
        {
          j = arrayOfInstructionHandle[k].getPosition();
          this._out.println("    " + str + ".setTarget(" + k + ", ih_" + j + ");");
        }
      }
    }
  }
  
  private void updateExceptionHandlers()
  {
    CodeExceptionGen[] arrayOfCodeExceptionGen = this._mg.getExceptionHandlers();
    for (int i = 0; i < arrayOfCodeExceptionGen.length; i++)
    {
      CodeExceptionGen localCodeExceptionGen = arrayOfCodeExceptionGen[i];
      String str = localCodeExceptionGen.getCatchType() == null ? "null" : BCELifier.printType(localCodeExceptionGen.getCatchType());
      this._out.println("    method.addExceptionHandler(ih_" + localCodeExceptionGen.getStartPC().getPosition() + ", " + "ih_" + localCodeExceptionGen.getEndPC().getPosition() + ", " + "ih_" + localCodeExceptionGen.getHandlerPC().getPosition() + ", " + str + ");");
    }
  }
}
