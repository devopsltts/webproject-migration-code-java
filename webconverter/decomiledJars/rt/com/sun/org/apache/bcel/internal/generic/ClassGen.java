package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.AccessFlags;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ClassGen
  extends AccessFlags
  implements Cloneable
{
  private String class_name;
  private String super_class_name;
  private String file_name;
  private int class_name_index = -1;
  private int superclass_name_index = -1;
  private int major = 45;
  private int minor = 3;
  private ConstantPoolGen cp;
  private ArrayList field_vec = new ArrayList();
  private ArrayList method_vec = new ArrayList();
  private ArrayList attribute_vec = new ArrayList();
  private ArrayList interface_vec = new ArrayList();
  private ArrayList observers;
  
  public ClassGen(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, ConstantPoolGen paramConstantPoolGen)
  {
    this.class_name = paramString1;
    this.super_class_name = paramString2;
    this.file_name = paramString3;
    this.access_flags = paramInt;
    this.cp = paramConstantPoolGen;
    if (paramString3 != null) {
      addAttribute(new SourceFile(paramConstantPoolGen.addUtf8("SourceFile"), 2, paramConstantPoolGen.addUtf8(paramString3), paramConstantPoolGen.getConstantPool()));
    }
    this.class_name_index = paramConstantPoolGen.addClass(paramString1);
    this.superclass_name_index = paramConstantPoolGen.addClass(paramString2);
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++) {
        addInterface(paramArrayOfString[i]);
      }
    }
  }
  
  public ClassGen(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString)
  {
    this(paramString1, paramString2, paramString3, paramInt, paramArrayOfString, new ConstantPoolGen());
  }
  
  public ClassGen(JavaClass paramJavaClass)
  {
    this.class_name_index = paramJavaClass.getClassNameIndex();
    this.superclass_name_index = paramJavaClass.getSuperclassNameIndex();
    this.class_name = paramJavaClass.getClassName();
    this.super_class_name = paramJavaClass.getSuperclassName();
    this.file_name = paramJavaClass.getSourceFileName();
    this.access_flags = paramJavaClass.getAccessFlags();
    this.cp = new ConstantPoolGen(paramJavaClass.getConstantPool());
    this.major = paramJavaClass.getMajor();
    this.minor = paramJavaClass.getMinor();
    Attribute[] arrayOfAttribute = paramJavaClass.getAttributes();
    Method[] arrayOfMethod = paramJavaClass.getMethods();
    Field[] arrayOfField = paramJavaClass.getFields();
    String[] arrayOfString = paramJavaClass.getInterfaceNames();
    for (int i = 0; i < arrayOfString.length; i++) {
      addInterface(arrayOfString[i]);
    }
    for (i = 0; i < arrayOfAttribute.length; i++) {
      addAttribute(arrayOfAttribute[i]);
    }
    for (i = 0; i < arrayOfMethod.length; i++) {
      addMethod(arrayOfMethod[i]);
    }
    for (i = 0; i < arrayOfField.length; i++) {
      addField(arrayOfField[i]);
    }
  }
  
  public JavaClass getJavaClass()
  {
    int[] arrayOfInt = getInterfaces();
    Field[] arrayOfField = getFields();
    Method[] arrayOfMethod = getMethods();
    Attribute[] arrayOfAttribute = getAttributes();
    ConstantPool localConstantPool = this.cp.getFinalConstantPool();
    return new JavaClass(this.class_name_index, this.superclass_name_index, this.file_name, this.major, this.minor, this.access_flags, localConstantPool, arrayOfInt, arrayOfField, arrayOfMethod, arrayOfAttribute);
  }
  
  public void addInterface(String paramString)
  {
    this.interface_vec.add(paramString);
  }
  
  public void removeInterface(String paramString)
  {
    this.interface_vec.remove(paramString);
  }
  
  public int getMajor()
  {
    return this.major;
  }
  
  public void setMajor(int paramInt)
  {
    this.major = paramInt;
  }
  
  public void setMinor(int paramInt)
  {
    this.minor = paramInt;
  }
  
  public int getMinor()
  {
    return this.minor;
  }
  
  public void addAttribute(Attribute paramAttribute)
  {
    this.attribute_vec.add(paramAttribute);
  }
  
  public void addMethod(Method paramMethod)
  {
    this.method_vec.add(paramMethod);
  }
  
  public void addEmptyConstructor(int paramInt)
  {
    InstructionList localInstructionList = new InstructionList();
    localInstructionList.append(InstructionConstants.THIS);
    localInstructionList.append(new INVOKESPECIAL(this.cp.addMethodref(this.super_class_name, "<init>", "()V")));
    localInstructionList.append(InstructionConstants.RETURN);
    MethodGen localMethodGen = new MethodGen(paramInt, Type.VOID, Type.NO_ARGS, null, "<init>", this.class_name, localInstructionList, this.cp);
    localMethodGen.setMaxStack(1);
    addMethod(localMethodGen.getMethod());
  }
  
  public void addField(Field paramField)
  {
    this.field_vec.add(paramField);
  }
  
  public boolean containsField(Field paramField)
  {
    return this.field_vec.contains(paramField);
  }
  
  public Field containsField(String paramString)
  {
    Iterator localIterator = this.field_vec.iterator();
    while (localIterator.hasNext())
    {
      Field localField = (Field)localIterator.next();
      if (localField.getName().equals(paramString)) {
        return localField;
      }
    }
    return null;
  }
  
  public Method containsMethod(String paramString1, String paramString2)
  {
    Iterator localIterator = this.method_vec.iterator();
    while (localIterator.hasNext())
    {
      Method localMethod = (Method)localIterator.next();
      if ((localMethod.getName().equals(paramString1)) && (localMethod.getSignature().equals(paramString2))) {
        return localMethod;
      }
    }
    return null;
  }
  
  public void removeAttribute(Attribute paramAttribute)
  {
    this.attribute_vec.remove(paramAttribute);
  }
  
  public void removeMethod(Method paramMethod)
  {
    this.method_vec.remove(paramMethod);
  }
  
  public void replaceMethod(Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod2 == null) {
      throw new ClassGenException("Replacement method must not be null");
    }
    int i = this.method_vec.indexOf(paramMethod1);
    if (i < 0) {
      this.method_vec.add(paramMethod2);
    } else {
      this.method_vec.set(i, paramMethod2);
    }
  }
  
  public void replaceField(Field paramField1, Field paramField2)
  {
    if (paramField2 == null) {
      throw new ClassGenException("Replacement method must not be null");
    }
    int i = this.field_vec.indexOf(paramField1);
    if (i < 0) {
      this.field_vec.add(paramField2);
    } else {
      this.field_vec.set(i, paramField2);
    }
  }
  
  public void removeField(Field paramField)
  {
    this.field_vec.remove(paramField);
  }
  
  public String getClassName()
  {
    return this.class_name;
  }
  
  public String getSuperclassName()
  {
    return this.super_class_name;
  }
  
  public String getFileName()
  {
    return this.file_name;
  }
  
  public void setClassName(String paramString)
  {
    this.class_name = paramString.replace('/', '.');
    this.class_name_index = this.cp.addClass(paramString);
  }
  
  public void setSuperclassName(String paramString)
  {
    this.super_class_name = paramString.replace('/', '.');
    this.superclass_name_index = this.cp.addClass(paramString);
  }
  
  public Method[] getMethods()
  {
    Method[] arrayOfMethod = new Method[this.method_vec.size()];
    this.method_vec.toArray(arrayOfMethod);
    return arrayOfMethod;
  }
  
  public void setMethods(Method[] paramArrayOfMethod)
  {
    this.method_vec.clear();
    for (int i = 0; i < paramArrayOfMethod.length; i++) {
      addMethod(paramArrayOfMethod[i]);
    }
  }
  
  public void setMethodAt(Method paramMethod, int paramInt)
  {
    this.method_vec.set(paramInt, paramMethod);
  }
  
  public Method getMethodAt(int paramInt)
  {
    return (Method)this.method_vec.get(paramInt);
  }
  
  public String[] getInterfaceNames()
  {
    int i = this.interface_vec.size();
    String[] arrayOfString = new String[i];
    this.interface_vec.toArray(arrayOfString);
    return arrayOfString;
  }
  
  public int[] getInterfaces()
  {
    int i = this.interface_vec.size();
    int[] arrayOfInt = new int[i];
    for (int j = 0; j < i; j++) {
      arrayOfInt[j] = this.cp.addClass((String)this.interface_vec.get(j));
    }
    return arrayOfInt;
  }
  
  public Field[] getFields()
  {
    Field[] arrayOfField = new Field[this.field_vec.size()];
    this.field_vec.toArray(arrayOfField);
    return arrayOfField;
  }
  
  public Attribute[] getAttributes()
  {
    Attribute[] arrayOfAttribute = new Attribute[this.attribute_vec.size()];
    this.attribute_vec.toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public ConstantPoolGen getConstantPool()
  {
    return this.cp;
  }
  
  public void setConstantPool(ConstantPoolGen paramConstantPoolGen)
  {
    this.cp = paramConstantPoolGen;
  }
  
  public void setClassNameIndex(int paramInt)
  {
    this.class_name_index = paramInt;
    this.class_name = this.cp.getConstantPool().getConstantString(paramInt, (byte)7).replace('/', '.');
  }
  
  public void setSuperclassNameIndex(int paramInt)
  {
    this.superclass_name_index = paramInt;
    this.super_class_name = this.cp.getConstantPool().getConstantString(paramInt, (byte)7).replace('/', '.');
  }
  
  public int getSuperclassNameIndex()
  {
    return this.superclass_name_index;
  }
  
  public int getClassNameIndex()
  {
    return this.class_name_index;
  }
  
  public void addObserver(ClassObserver paramClassObserver)
  {
    if (this.observers == null) {
      this.observers = new ArrayList();
    }
    this.observers.add(paramClassObserver);
  }
  
  public void removeObserver(ClassObserver paramClassObserver)
  {
    if (this.observers != null) {
      this.observers.remove(paramClassObserver);
    }
  }
  
  public void update()
  {
    if (this.observers != null)
    {
      Iterator localIterator = this.observers.iterator();
      while (localIterator.hasNext()) {
        ((ClassObserver)localIterator.next()).notify(this);
      }
    }
  }
  
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      System.err.println(localCloneNotSupportedException);
    }
    return null;
  }
}
