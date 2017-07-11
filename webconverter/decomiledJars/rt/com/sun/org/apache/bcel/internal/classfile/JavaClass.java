package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.util.ClassQueue;
import com.sun.org.apache.bcel.internal.util.ClassVector;
import com.sun.org.apache.bcel.internal.util.Repository;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

public class JavaClass
  extends AccessFlags
  implements Cloneable, Node
{
  private String file_name;
  private String package_name;
  private String source_file_name = "<Unknown>";
  private int class_name_index;
  private int superclass_name_index;
  private String class_name;
  private String superclass_name;
  private int major;
  private int minor;
  private ConstantPool constant_pool;
  private int[] interfaces;
  private String[] interface_names;
  private Field[] fields;
  private Method[] methods;
  private Attribute[] attributes;
  private byte source = 1;
  public static final byte HEAP = 1;
  public static final byte FILE = 2;
  public static final byte ZIP = 3;
  static boolean debug = false;
  static char sep = '/';
  private transient Repository repository = SyntheticRepository.getInstance();
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute, byte paramByte)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[0];
    }
    if (paramArrayOfAttribute == null) {
      this.attributes = new Attribute[0];
    }
    if (paramArrayOfField == null) {
      paramArrayOfField = new Field[0];
    }
    if (paramArrayOfMethod == null) {
      paramArrayOfMethod = new Method[0];
    }
    this.class_name_index = paramInt1;
    this.superclass_name_index = paramInt2;
    this.file_name = paramString;
    this.major = paramInt3;
    this.minor = paramInt4;
    this.access_flags = paramInt5;
    this.constant_pool = paramConstantPool;
    this.interfaces = paramArrayOfInt;
    this.fields = paramArrayOfField;
    this.methods = paramArrayOfMethod;
    this.attributes = paramArrayOfAttribute;
    this.source = paramByte;
    for (int i = 0; i < paramArrayOfAttribute.length; i++) {
      if ((paramArrayOfAttribute[i] instanceof SourceFile))
      {
        this.source_file_name = ((SourceFile)paramArrayOfAttribute[i]).getSourceFileName();
        break;
      }
    }
    this.class_name = paramConstantPool.getConstantString(paramInt1, (byte)7);
    this.class_name = Utility.compactClassName(this.class_name, false);
    i = this.class_name.lastIndexOf('.');
    if (i < 0) {
      this.package_name = "";
    } else {
      this.package_name = this.class_name.substring(0, i);
    }
    if (paramInt2 > 0)
    {
      this.superclass_name = paramConstantPool.getConstantString(paramInt2, (byte)7);
      this.superclass_name = Utility.compactClassName(this.superclass_name, false);
    }
    else
    {
      this.superclass_name = "java.lang.Object";
    }
    this.interface_names = new String[paramArrayOfInt.length];
    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      String str = paramConstantPool.getConstantString(paramArrayOfInt[j], (byte)7);
      this.interface_names[j] = Utility.compactClassName(str, false);
    }
  }
  
  public JavaClass(int paramInt1, int paramInt2, String paramString, int paramInt3, int paramInt4, int paramInt5, ConstantPool paramConstantPool, int[] paramArrayOfInt, Field[] paramArrayOfField, Method[] paramArrayOfMethod, Attribute[] paramArrayOfAttribute)
  {
    this(paramInt1, paramInt2, paramString, paramInt3, paramInt4, paramInt5, paramConstantPool, paramArrayOfInt, paramArrayOfField, paramArrayOfMethod, paramArrayOfAttribute, (byte)1);
  }
  
  public void accept(Visitor paramVisitor)
  {
    paramVisitor.visitJavaClass(this);
  }
  
  static final void Debug(String paramString)
  {
    if (debug) {
      System.out.println(paramString);
    }
  }
  
  public void dump(File paramFile)
    throws IOException
  {
    String str = paramFile.getParent();
    if (str != null)
    {
      File localFile = new File(str);
      if (localFile != null) {
        localFile.mkdirs();
      }
    }
    dump(new DataOutputStream(new FileOutputStream(paramFile)));
  }
  
  public void dump(String paramString)
    throws IOException
  {
    dump(new File(paramString));
  }
  
  public byte[] getBytes()
  {
    localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    try
    {
      dump(localDataOutputStream);
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException2)
    {
      localIOException2.printStackTrace();
    }
    finally
    {
      try
      {
        localDataOutputStream.close();
      }
      catch (IOException localIOException4)
      {
        localIOException4.printStackTrace();
      }
    }
  }
  
  public void dump(OutputStream paramOutputStream)
    throws IOException
  {
    dump(new DataOutputStream(paramOutputStream));
  }
  
  public void dump(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    paramDataOutputStream.writeInt(-889275714);
    paramDataOutputStream.writeShort(this.minor);
    paramDataOutputStream.writeShort(this.major);
    this.constant_pool.dump(paramDataOutputStream);
    paramDataOutputStream.writeShort(this.access_flags);
    paramDataOutputStream.writeShort(this.class_name_index);
    paramDataOutputStream.writeShort(this.superclass_name_index);
    paramDataOutputStream.writeShort(this.interfaces.length);
    for (int i = 0; i < this.interfaces.length; i++) {
      paramDataOutputStream.writeShort(this.interfaces[i]);
    }
    paramDataOutputStream.writeShort(this.fields.length);
    for (i = 0; i < this.fields.length; i++) {
      this.fields[i].dump(paramDataOutputStream);
    }
    paramDataOutputStream.writeShort(this.methods.length);
    for (i = 0; i < this.methods.length; i++) {
      this.methods[i].dump(paramDataOutputStream);
    }
    if (this.attributes != null)
    {
      paramDataOutputStream.writeShort(this.attributes.length);
      for (i = 0; i < this.attributes.length; i++) {
        this.attributes[i].dump(paramDataOutputStream);
      }
    }
    else
    {
      paramDataOutputStream.writeShort(0);
    }
    paramDataOutputStream.close();
  }
  
  public Attribute[] getAttributes()
  {
    return this.attributes;
  }
  
  public String getClassName()
  {
    return this.class_name;
  }
  
  public String getPackageName()
  {
    return this.package_name;
  }
  
  public int getClassNameIndex()
  {
    return this.class_name_index;
  }
  
  public ConstantPool getConstantPool()
  {
    return this.constant_pool;
  }
  
  public Field[] getFields()
  {
    return this.fields;
  }
  
  public String getFileName()
  {
    return this.file_name;
  }
  
  public String[] getInterfaceNames()
  {
    return this.interface_names;
  }
  
  public int[] getInterfaceIndices()
  {
    return this.interfaces;
  }
  
  public int getMajor()
  {
    return this.major;
  }
  
  public Method[] getMethods()
  {
    return this.methods;
  }
  
  public Method getMethod(java.lang.reflect.Method paramMethod)
  {
    for (int i = 0; i < this.methods.length; i++)
    {
      Method localMethod = this.methods[i];
      if ((paramMethod.getName().equals(localMethod.getName())) && (paramMethod.getModifiers() == localMethod.getModifiers()) && (Type.getSignature(paramMethod).equals(localMethod.getSignature()))) {
        return localMethod;
      }
    }
    return null;
  }
  
  public int getMinor()
  {
    return this.minor;
  }
  
  public String getSourceFileName()
  {
    return this.source_file_name;
  }
  
  public String getSuperclassName()
  {
    return this.superclass_name;
  }
  
  public int getSuperclassNameIndex()
  {
    return this.superclass_name_index;
  }
  
  public void setAttributes(Attribute[] paramArrayOfAttribute)
  {
    this.attributes = paramArrayOfAttribute;
  }
  
  public void setClassName(String paramString)
  {
    this.class_name = paramString;
  }
  
  public void setClassNameIndex(int paramInt)
  {
    this.class_name_index = paramInt;
  }
  
  public void setConstantPool(ConstantPool paramConstantPool)
  {
    this.constant_pool = paramConstantPool;
  }
  
  public void setFields(Field[] paramArrayOfField)
  {
    this.fields = paramArrayOfField;
  }
  
  public void setFileName(String paramString)
  {
    this.file_name = paramString;
  }
  
  public void setInterfaceNames(String[] paramArrayOfString)
  {
    this.interface_names = paramArrayOfString;
  }
  
  public void setInterfaces(int[] paramArrayOfInt)
  {
    this.interfaces = paramArrayOfInt;
  }
  
  public void setMajor(int paramInt)
  {
    this.major = paramInt;
  }
  
  public void setMethods(Method[] paramArrayOfMethod)
  {
    this.methods = paramArrayOfMethod;
  }
  
  public void setMinor(int paramInt)
  {
    this.minor = paramInt;
  }
  
  public void setSourceFileName(String paramString)
  {
    this.source_file_name = paramString;
  }
  
  public void setSuperclassName(String paramString)
  {
    this.superclass_name = paramString;
  }
  
  public void setSuperclassNameIndex(int paramInt)
  {
    this.superclass_name_index = paramInt;
  }
  
  public String toString()
  {
    String str = Utility.accessToString(this.access_flags, true);
    str = str + " ";
    StringBuffer localStringBuffer = new StringBuffer(str + Utility.classOrInterface(this.access_flags) + " " + this.class_name + " extends " + Utility.compactClassName(this.superclass_name, false) + '\n');
    int i = this.interfaces.length;
    int j;
    if (i > 0)
    {
      localStringBuffer.append("implements\t\t");
      for (j = 0; j < i; j++)
      {
        localStringBuffer.append(this.interface_names[j]);
        if (j < i - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append('\n');
    }
    localStringBuffer.append("filename\t\t" + this.file_name + '\n');
    localStringBuffer.append("compiled from\t\t" + this.source_file_name + '\n');
    localStringBuffer.append("compiler version\t" + this.major + "." + this.minor + '\n');
    localStringBuffer.append("access flags\t\t" + this.access_flags + '\n');
    localStringBuffer.append("constant pool\t\t" + this.constant_pool.getLength() + " entries\n");
    localStringBuffer.append("ACC_SUPER flag\t\t" + isSuper() + "\n");
    if (this.attributes.length > 0)
    {
      localStringBuffer.append("\nAttribute(s):\n");
      for (j = 0; j < this.attributes.length; j++) {
        localStringBuffer.append(indent(this.attributes[j]));
      }
    }
    if (this.fields.length > 0)
    {
      localStringBuffer.append("\n" + this.fields.length + " fields:\n");
      for (j = 0; j < this.fields.length; j++) {
        localStringBuffer.append("\t" + this.fields[j] + '\n');
      }
    }
    if (this.methods.length > 0)
    {
      localStringBuffer.append("\n" + this.methods.length + " methods:\n");
      for (j = 0; j < this.methods.length; j++) {
        localStringBuffer.append("\t" + this.methods[j] + '\n');
      }
    }
    return localStringBuffer.toString();
  }
  
  private static final String indent(Object paramObject)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramObject.toString(), "\n");
    StringBuffer localStringBuffer = new StringBuffer();
    while (localStringTokenizer.hasMoreTokens()) {
      localStringBuffer.append("\t" + localStringTokenizer.nextToken() + "\n");
    }
    return localStringBuffer.toString();
  }
  
  public JavaClass copy()
  {
    JavaClass localJavaClass = null;
    try
    {
      localJavaClass = (JavaClass)clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    localJavaClass.constant_pool = this.constant_pool.copy();
    localJavaClass.interfaces = ((int[])this.interfaces.clone());
    localJavaClass.interface_names = ((String[])this.interface_names.clone());
    localJavaClass.fields = new Field[this.fields.length];
    for (int i = 0; i < this.fields.length; i++) {
      localJavaClass.fields[i] = this.fields[i].copy(localJavaClass.constant_pool);
    }
    localJavaClass.methods = new Method[this.methods.length];
    for (i = 0; i < this.methods.length; i++) {
      localJavaClass.methods[i] = this.methods[i].copy(localJavaClass.constant_pool);
    }
    localJavaClass.attributes = new Attribute[this.attributes.length];
    for (i = 0; i < this.attributes.length; i++) {
      localJavaClass.attributes[i] = this.attributes[i].copy(localJavaClass.constant_pool);
    }
    return localJavaClass;
  }
  
  public final boolean isSuper()
  {
    return (this.access_flags & 0x20) != 0;
  }
  
  public final boolean isClass()
  {
    return (this.access_flags & 0x200) == 0;
  }
  
  public final byte getSource()
  {
    return this.source;
  }
  
  public Repository getRepository()
  {
    return this.repository;
  }
  
  public void setRepository(Repository paramRepository)
  {
    this.repository = paramRepository;
  }
  
  public final boolean instanceOf(JavaClass paramJavaClass)
  {
    if (equals(paramJavaClass)) {
      return true;
    }
    JavaClass[] arrayOfJavaClass = getSuperClasses();
    for (int i = 0; i < arrayOfJavaClass.length; i++) {
      if (arrayOfJavaClass[i].equals(paramJavaClass)) {
        return true;
      }
    }
    if (paramJavaClass.isInterface()) {
      return implementationOf(paramJavaClass);
    }
    return false;
  }
  
  public boolean implementationOf(JavaClass paramJavaClass)
  {
    if (!paramJavaClass.isInterface()) {
      throw new IllegalArgumentException(paramJavaClass.getClassName() + " is no interface");
    }
    if (equals(paramJavaClass)) {
      return true;
    }
    JavaClass[] arrayOfJavaClass = getAllInterfaces();
    for (int i = 0; i < arrayOfJavaClass.length; i++) {
      if (arrayOfJavaClass[i].equals(paramJavaClass)) {
        return true;
      }
    }
    return false;
  }
  
  public JavaClass getSuperClass()
  {
    if ("java.lang.Object".equals(getClassName())) {
      return null;
    }
    try
    {
      return this.repository.loadClass(getSuperclassName());
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println(localClassNotFoundException);
    }
    return null;
  }
  
  public JavaClass[] getSuperClasses()
  {
    JavaClass localJavaClass = this;
    ClassVector localClassVector = new ClassVector();
    for (localJavaClass = localJavaClass.getSuperClass(); localJavaClass != null; localJavaClass = localJavaClass.getSuperClass()) {
      localClassVector.addElement(localJavaClass);
    }
    return localClassVector.toArray();
  }
  
  public JavaClass[] getInterfaces()
  {
    String[] arrayOfString = getInterfaceNames();
    JavaClass[] arrayOfJavaClass = new JavaClass[arrayOfString.length];
    try
    {
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfJavaClass[i] = this.repository.loadClass(arrayOfString[i]);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      System.err.println(localClassNotFoundException);
      return null;
    }
    return arrayOfJavaClass;
  }
  
  public JavaClass[] getAllInterfaces()
  {
    ClassQueue localClassQueue = new ClassQueue();
    ClassVector localClassVector = new ClassVector();
    localClassQueue.enqueue(this);
    while (!localClassQueue.empty())
    {
      JavaClass localJavaClass1 = localClassQueue.dequeue();
      JavaClass localJavaClass2 = localJavaClass1.getSuperClass();
      JavaClass[] arrayOfJavaClass = localJavaClass1.getInterfaces();
      if (localJavaClass1.isInterface()) {
        localClassVector.addElement(localJavaClass1);
      } else if (localJavaClass2 != null) {
        localClassQueue.enqueue(localJavaClass2);
      }
      for (int i = 0; i < arrayOfJavaClass.length; i++) {
        localClassQueue.enqueue(arrayOfJavaClass[i]);
      }
    }
    return localClassVector.toArray();
  }
  
  static
  {
    String str1 = null;
    String str2 = null;
    try
    {
      str1 = SecuritySupport.getSystemProperty("JavaClass.debug");
      str2 = SecuritySupport.getSystemProperty("file.separator");
    }
    catch (SecurityException localSecurityException) {}
    if (str1 != null) {
      debug = new Boolean(str1).booleanValue();
    }
    if (str2 != null) {
      try
      {
        sep = str2.charAt(0);
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
    }
  }
}
