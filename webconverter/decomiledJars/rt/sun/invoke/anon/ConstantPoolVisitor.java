package sun.invoke.anon;

public class ConstantPoolVisitor
{
  public static final byte CONSTANT_None = 0;
  public static final byte CONSTANT_Utf8 = 1;
  public static final byte CONSTANT_Integer = 3;
  public static final byte CONSTANT_Float = 4;
  public static final byte CONSTANT_Long = 5;
  public static final byte CONSTANT_Double = 6;
  public static final byte CONSTANT_Class = 7;
  public static final byte CONSTANT_String = 8;
  public static final byte CONSTANT_Fieldref = 9;
  public static final byte CONSTANT_Methodref = 10;
  public static final byte CONSTANT_InterfaceMethodref = 11;
  public static final byte CONSTANT_NameAndType = 12;
  private static String[] TAG_NAMES = { "Empty", "Utf8", null, "Integer", "Float", "Long", "Double", "Class", "String", "Fieldref", "Methodref", "InterfaceMethodref", "NameAndType" };
  
  public ConstantPoolVisitor() {}
  
  public void visitUTF8(int paramInt, byte paramByte, String paramString) {}
  
  public void visitConstantValue(int paramInt, byte paramByte, Object paramObject) {}
  
  public void visitConstantString(int paramInt1, byte paramByte, String paramString, int paramInt2) {}
  
  public void visitDescriptor(int paramInt1, byte paramByte, String paramString1, String paramString2, int paramInt2, int paramInt3) {}
  
  public void visitMemberRef(int paramInt1, byte paramByte, String paramString1, String paramString2, String paramString3, int paramInt2, int paramInt3) {}
  
  public static String tagName(byte paramByte)
  {
    String str = null;
    if ((paramByte & 0xFF) < TAG_NAMES.length) {
      str = TAG_NAMES[paramByte];
    }
    if (str == null) {
      str = "Unknown#" + (paramByte & 0xFF);
    }
    return str;
  }
}
