package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class BandStructure
{
  static final int MAX_EFFORT = 9;
  static final int MIN_EFFORT = 1;
  static final int DEFAULT_EFFORT = 5;
  PropMap p200 = Utils.currentPropMap();
  int verbose = this.p200.getInteger("com.sun.java.util.jar.pack.verbose");
  int effort = this.p200.getInteger("pack.effort");
  boolean optDumpBands;
  boolean optDebugBands;
  boolean optVaryCodings;
  boolean optBigStrings;
  private Package.Version highestClassVersion;
  private final boolean isReader;
  static final Coding BYTE1;
  static final Coding CHAR3;
  static final Coding BCI5;
  static final Coding BRANCH5;
  static final Coding UNSIGNED5;
  static final Coding UDELTA5;
  static final Coding SIGNED5;
  static final Coding DELTA5;
  static final Coding MDELTA5;
  private static final Coding[] basicCodings;
  private static final Map<Coding, Integer> basicCodingIndexes;
  protected byte[] bandHeaderBytes;
  protected int bandHeaderBytePos;
  protected int bandHeaderBytePos0;
  static final int SHORT_BAND_HEURISTIC = 100;
  public static final int NO_PHASE = 0;
  public static final int COLLECT_PHASE = 1;
  public static final int FROZEN_PHASE = 3;
  public static final int WRITE_PHASE = 5;
  public static final int EXPECT_PHASE = 2;
  public static final int READ_PHASE = 4;
  public static final int DISBURSE_PHASE = 6;
  public static final int DONE_PHASE = 8;
  private final List<CPRefBand> allKQBands;
  private List<Object[]> needPredefIndex;
  private CodingChooser codingChooser;
  static final byte[] defaultMetaCoding;
  static final byte[] noMetaCoding;
  ByteCounter outputCounter;
  protected int archiveOptions;
  protected long archiveSize0;
  protected long archiveSize1;
  protected int archiveNextCount;
  static final int AH_LENGTH_0 = 3;
  static final int AH_LENGTH_MIN = 15;
  static final int AH_LENGTH_S = 2;
  static final int AH_ARCHIVE_SIZE_HI = 0;
  static final int AH_ARCHIVE_SIZE_LO = 1;
  static final int AH_FILE_HEADER_LEN = 5;
  static final int AH_SPECIAL_FORMAT_LEN = 2;
  static final int AH_CP_NUMBER_LEN = 4;
  static final int AH_CP_EXTRA_LEN = 4;
  static final int AB_FLAGS_HI = 0;
  static final int AB_FLAGS_LO = 1;
  static final int AB_ATTR_COUNT = 2;
  static final int AB_ATTR_INDEXES = 3;
  static final int AB_ATTR_CALLS = 4;
  private static final boolean NULL_IS_OK = true;
  MultiBand all_bands;
  ByteBand archive_magic;
  IntBand archive_header_0;
  IntBand archive_header_S;
  IntBand archive_header_1;
  ByteBand band_headers;
  MultiBand cp_bands;
  IntBand cp_Utf8_prefix;
  IntBand cp_Utf8_suffix;
  IntBand cp_Utf8_chars;
  IntBand cp_Utf8_big_suffix;
  MultiBand cp_Utf8_big_chars;
  IntBand cp_Int;
  IntBand cp_Float;
  IntBand cp_Long_hi;
  IntBand cp_Long_lo;
  IntBand cp_Double_hi;
  IntBand cp_Double_lo;
  CPRefBand cp_String;
  CPRefBand cp_Class;
  CPRefBand cp_Signature_form;
  CPRefBand cp_Signature_classes;
  CPRefBand cp_Descr_name;
  CPRefBand cp_Descr_type;
  CPRefBand cp_Field_class;
  CPRefBand cp_Field_desc;
  CPRefBand cp_Method_class;
  CPRefBand cp_Method_desc;
  CPRefBand cp_Imethod_class;
  CPRefBand cp_Imethod_desc;
  IntBand cp_MethodHandle_refkind;
  CPRefBand cp_MethodHandle_member;
  CPRefBand cp_MethodType;
  CPRefBand cp_BootstrapMethod_ref;
  IntBand cp_BootstrapMethod_arg_count;
  CPRefBand cp_BootstrapMethod_arg;
  CPRefBand cp_InvokeDynamic_spec;
  CPRefBand cp_InvokeDynamic_desc;
  MultiBand attr_definition_bands;
  ByteBand attr_definition_headers;
  CPRefBand attr_definition_name;
  CPRefBand attr_definition_layout;
  MultiBand ic_bands;
  CPRefBand ic_this_class;
  IntBand ic_flags;
  CPRefBand ic_outer_class;
  CPRefBand ic_name;
  MultiBand class_bands;
  CPRefBand class_this;
  CPRefBand class_super;
  IntBand class_interface_count;
  CPRefBand class_interface;
  IntBand class_field_count;
  IntBand class_method_count;
  CPRefBand field_descr;
  MultiBand field_attr_bands;
  IntBand field_flags_hi;
  IntBand field_flags_lo;
  IntBand field_attr_count;
  IntBand field_attr_indexes;
  IntBand field_attr_calls;
  CPRefBand field_ConstantValue_KQ;
  CPRefBand field_Signature_RS;
  MultiBand field_metadata_bands;
  MultiBand field_type_metadata_bands;
  CPRefBand method_descr;
  MultiBand method_attr_bands;
  IntBand method_flags_hi;
  IntBand method_flags_lo;
  IntBand method_attr_count;
  IntBand method_attr_indexes;
  IntBand method_attr_calls;
  IntBand method_Exceptions_N;
  CPRefBand method_Exceptions_RC;
  CPRefBand method_Signature_RS;
  MultiBand method_metadata_bands;
  IntBand method_MethodParameters_NB;
  CPRefBand method_MethodParameters_name_RUN;
  IntBand method_MethodParameters_flag_FH;
  MultiBand method_type_metadata_bands;
  MultiBand class_attr_bands;
  IntBand class_flags_hi;
  IntBand class_flags_lo;
  IntBand class_attr_count;
  IntBand class_attr_indexes;
  IntBand class_attr_calls;
  CPRefBand class_SourceFile_RUN;
  CPRefBand class_EnclosingMethod_RC;
  CPRefBand class_EnclosingMethod_RDN;
  CPRefBand class_Signature_RS;
  MultiBand class_metadata_bands;
  IntBand class_InnerClasses_N;
  CPRefBand class_InnerClasses_RC;
  IntBand class_InnerClasses_F;
  CPRefBand class_InnerClasses_outer_RCN;
  CPRefBand class_InnerClasses_name_RUN;
  IntBand class_ClassFile_version_minor_H;
  IntBand class_ClassFile_version_major_H;
  MultiBand class_type_metadata_bands;
  MultiBand code_bands;
  ByteBand code_headers;
  IntBand code_max_stack;
  IntBand code_max_na_locals;
  IntBand code_handler_count;
  IntBand code_handler_start_P;
  IntBand code_handler_end_PO;
  IntBand code_handler_catch_PO;
  CPRefBand code_handler_class_RCN;
  MultiBand code_attr_bands;
  IntBand code_flags_hi;
  IntBand code_flags_lo;
  IntBand code_attr_count;
  IntBand code_attr_indexes;
  IntBand code_attr_calls;
  MultiBand stackmap_bands;
  IntBand code_StackMapTable_N;
  IntBand code_StackMapTable_frame_T;
  IntBand code_StackMapTable_local_N;
  IntBand code_StackMapTable_stack_N;
  IntBand code_StackMapTable_offset;
  IntBand code_StackMapTable_T;
  CPRefBand code_StackMapTable_RC;
  IntBand code_StackMapTable_P;
  IntBand code_LineNumberTable_N;
  IntBand code_LineNumberTable_bci_P;
  IntBand code_LineNumberTable_line;
  IntBand code_LocalVariableTable_N;
  IntBand code_LocalVariableTable_bci_P;
  IntBand code_LocalVariableTable_span_O;
  CPRefBand code_LocalVariableTable_name_RU;
  CPRefBand code_LocalVariableTable_type_RS;
  IntBand code_LocalVariableTable_slot;
  IntBand code_LocalVariableTypeTable_N;
  IntBand code_LocalVariableTypeTable_bci_P;
  IntBand code_LocalVariableTypeTable_span_O;
  CPRefBand code_LocalVariableTypeTable_name_RU;
  CPRefBand code_LocalVariableTypeTable_type_RS;
  IntBand code_LocalVariableTypeTable_slot;
  MultiBand code_type_metadata_bands;
  MultiBand bc_bands;
  ByteBand bc_codes;
  IntBand bc_case_count;
  IntBand bc_case_value;
  ByteBand bc_byte;
  IntBand bc_short;
  IntBand bc_local;
  IntBand bc_label;
  CPRefBand bc_intref;
  CPRefBand bc_floatref;
  CPRefBand bc_longref;
  CPRefBand bc_doubleref;
  CPRefBand bc_stringref;
  CPRefBand bc_loadablevalueref;
  CPRefBand bc_classref;
  CPRefBand bc_fieldref;
  CPRefBand bc_methodref;
  CPRefBand bc_imethodref;
  CPRefBand bc_indyref;
  CPRefBand bc_thisfield;
  CPRefBand bc_superfield;
  CPRefBand bc_thismethod;
  CPRefBand bc_supermethod;
  IntBand bc_initref;
  CPRefBand bc_escref;
  IntBand bc_escrefsize;
  IntBand bc_escsize;
  ByteBand bc_escbyte;
  MultiBand file_bands;
  CPRefBand file_name;
  IntBand file_size_hi;
  IntBand file_size_lo;
  IntBand file_modtime;
  IntBand file_options;
  ByteBand file_bits;
  protected MultiBand[] metadataBands;
  protected MultiBand[] typeMetadataBands;
  public static final int ADH_CONTEXT_MASK = 3;
  public static final int ADH_BIT_SHIFT = 2;
  public static final int ADH_BIT_IS_LSB = 1;
  public static final int ATTR_INDEX_OVERFLOW = -1;
  public int[] attrIndexLimit;
  protected long[] attrFlagMask;
  protected long[] attrDefSeen;
  protected int[] attrOverflowMask;
  protected int attrClassFileVersionMask;
  protected Map<Attribute.Layout, Band[]> attrBandTable;
  protected final Attribute.Layout attrCodeEmpty;
  protected final Attribute.Layout attrInnerClassesEmpty;
  protected final Attribute.Layout attrClassFileVersion;
  protected final Attribute.Layout attrConstantValue;
  Map<Attribute.Layout, Integer> attrIndexTable;
  protected List<List<Attribute.Layout>> attrDefs;
  protected MultiBand[] attrBands;
  private static final int[][] shortCodeLimits = { { 12, 12 }, { 8, 8 }, { 7, 7 } };
  public final int shortCodeHeader_h_limit;
  static final int LONG_CODE_HEADER = 0;
  static int nextSeqForDebug;
  static File dumpDir = null;
  private Map<Band, Band> prevForAssertMap;
  static LinkedList<String> bandSequenceList = null;
  
  protected abstract ConstantPool.Index getCPIndex(byte paramByte);
  
  public void initHighestClassVersion(Package.Version paramVersion)
    throws IOException
  {
    if (this.highestClassVersion != null) {
      throw new IOException("Highest class major version is already initialized to " + this.highestClassVersion + "; new setting is " + paramVersion);
    }
    this.highestClassVersion = paramVersion;
    adjustToClassVersion();
  }
  
  public Package.Version getHighestClassVersion()
  {
    return this.highestClassVersion;
  }
  
  protected BandStructure()
  {
    if (this.effort == 0) {
      this.effort = 5;
    }
    this.optDumpBands = this.p200.getBoolean("com.sun.java.util.jar.pack.dump.bands");
    this.optDebugBands = this.p200.getBoolean("com.sun.java.util.jar.pack.debug.bands");
    this.optVaryCodings = (!this.p200.getBoolean("com.sun.java.util.jar.pack.no.vary.codings"));
    this.optBigStrings = (!this.p200.getBoolean("com.sun.java.util.jar.pack.no.big.strings"));
    this.highestClassVersion = null;
    this.isReader = (this instanceof PackageReader);
    this.allKQBands = new ArrayList();
    this.needPredefIndex = new ArrayList();
    this.all_bands = ((MultiBand)new MultiBand("(package)", UNSIGNED5).init());
    this.archive_magic = this.all_bands.newByteBand("archive_magic");
    this.archive_header_0 = this.all_bands.newIntBand("archive_header_0", UNSIGNED5);
    this.archive_header_S = this.all_bands.newIntBand("archive_header_S", UNSIGNED5);
    this.archive_header_1 = this.all_bands.newIntBand("archive_header_1", UNSIGNED5);
    this.band_headers = this.all_bands.newByteBand("band_headers");
    this.cp_bands = this.all_bands.newMultiBand("(constant_pool)", DELTA5);
    this.cp_Utf8_prefix = this.cp_bands.newIntBand("cp_Utf8_prefix");
    this.cp_Utf8_suffix = this.cp_bands.newIntBand("cp_Utf8_suffix", UNSIGNED5);
    this.cp_Utf8_chars = this.cp_bands.newIntBand("cp_Utf8_chars", CHAR3);
    this.cp_Utf8_big_suffix = this.cp_bands.newIntBand("cp_Utf8_big_suffix");
    this.cp_Utf8_big_chars = this.cp_bands.newMultiBand("(cp_Utf8_big_chars)", DELTA5);
    this.cp_Int = this.cp_bands.newIntBand("cp_Int", UDELTA5);
    this.cp_Float = this.cp_bands.newIntBand("cp_Float", UDELTA5);
    this.cp_Long_hi = this.cp_bands.newIntBand("cp_Long_hi", UDELTA5);
    this.cp_Long_lo = this.cp_bands.newIntBand("cp_Long_lo");
    this.cp_Double_hi = this.cp_bands.newIntBand("cp_Double_hi", UDELTA5);
    this.cp_Double_lo = this.cp_bands.newIntBand("cp_Double_lo");
    this.cp_String = this.cp_bands.newCPRefBand("cp_String", UDELTA5, (byte)1);
    this.cp_Class = this.cp_bands.newCPRefBand("cp_Class", UDELTA5, (byte)1);
    this.cp_Signature_form = this.cp_bands.newCPRefBand("cp_Signature_form", (byte)1);
    this.cp_Signature_classes = this.cp_bands.newCPRefBand("cp_Signature_classes", UDELTA5, (byte)7);
    this.cp_Descr_name = this.cp_bands.newCPRefBand("cp_Descr_name", (byte)1);
    this.cp_Descr_type = this.cp_bands.newCPRefBand("cp_Descr_type", UDELTA5, (byte)13);
    this.cp_Field_class = this.cp_bands.newCPRefBand("cp_Field_class", (byte)7);
    this.cp_Field_desc = this.cp_bands.newCPRefBand("cp_Field_desc", UDELTA5, (byte)12);
    this.cp_Method_class = this.cp_bands.newCPRefBand("cp_Method_class", (byte)7);
    this.cp_Method_desc = this.cp_bands.newCPRefBand("cp_Method_desc", UDELTA5, (byte)12);
    this.cp_Imethod_class = this.cp_bands.newCPRefBand("cp_Imethod_class", (byte)7);
    this.cp_Imethod_desc = this.cp_bands.newCPRefBand("cp_Imethod_desc", UDELTA5, (byte)12);
    this.cp_MethodHandle_refkind = this.cp_bands.newIntBand("cp_MethodHandle_refkind", DELTA5);
    this.cp_MethodHandle_member = this.cp_bands.newCPRefBand("cp_MethodHandle_member", UDELTA5, (byte)52);
    this.cp_MethodType = this.cp_bands.newCPRefBand("cp_MethodType", UDELTA5, (byte)13);
    this.cp_BootstrapMethod_ref = this.cp_bands.newCPRefBand("cp_BootstrapMethod_ref", DELTA5, (byte)15);
    this.cp_BootstrapMethod_arg_count = this.cp_bands.newIntBand("cp_BootstrapMethod_arg_count", UDELTA5);
    this.cp_BootstrapMethod_arg = this.cp_bands.newCPRefBand("cp_BootstrapMethod_arg", DELTA5, (byte)51);
    this.cp_InvokeDynamic_spec = this.cp_bands.newCPRefBand("cp_InvokeDynamic_spec", DELTA5, (byte)17);
    this.cp_InvokeDynamic_desc = this.cp_bands.newCPRefBand("cp_InvokeDynamic_desc", UDELTA5, (byte)12);
    this.attr_definition_bands = this.all_bands.newMultiBand("(attr_definition_bands)", UNSIGNED5);
    this.attr_definition_headers = this.attr_definition_bands.newByteBand("attr_definition_headers");
    this.attr_definition_name = this.attr_definition_bands.newCPRefBand("attr_definition_name", (byte)1);
    this.attr_definition_layout = this.attr_definition_bands.newCPRefBand("attr_definition_layout", (byte)1);
    this.ic_bands = this.all_bands.newMultiBand("(ic_bands)", DELTA5);
    this.ic_this_class = this.ic_bands.newCPRefBand("ic_this_class", UDELTA5, (byte)7);
    this.ic_flags = this.ic_bands.newIntBand("ic_flags", UNSIGNED5);
    this.ic_outer_class = this.ic_bands.newCPRefBand("ic_outer_class", DELTA5, (byte)7, true);
    this.ic_name = this.ic_bands.newCPRefBand("ic_name", DELTA5, (byte)1, true);
    this.class_bands = this.all_bands.newMultiBand("(class_bands)", DELTA5);
    this.class_this = this.class_bands.newCPRefBand("class_this", (byte)7);
    this.class_super = this.class_bands.newCPRefBand("class_super", (byte)7);
    this.class_interface_count = this.class_bands.newIntBand("class_interface_count");
    this.class_interface = this.class_bands.newCPRefBand("class_interface", (byte)7);
    this.class_field_count = this.class_bands.newIntBand("class_field_count");
    this.class_method_count = this.class_bands.newIntBand("class_method_count");
    this.field_descr = this.class_bands.newCPRefBand("field_descr", (byte)12);
    this.field_attr_bands = this.class_bands.newMultiBand("(field_attr_bands)", UNSIGNED5);
    this.field_flags_hi = this.field_attr_bands.newIntBand("field_flags_hi");
    this.field_flags_lo = this.field_attr_bands.newIntBand("field_flags_lo");
    this.field_attr_count = this.field_attr_bands.newIntBand("field_attr_count");
    this.field_attr_indexes = this.field_attr_bands.newIntBand("field_attr_indexes");
    this.field_attr_calls = this.field_attr_bands.newIntBand("field_attr_calls");
    this.field_ConstantValue_KQ = this.field_attr_bands.newCPRefBand("field_ConstantValue_KQ", (byte)53);
    this.field_Signature_RS = this.field_attr_bands.newCPRefBand("field_Signature_RS", (byte)13);
    this.field_metadata_bands = this.field_attr_bands.newMultiBand("(field_metadata_bands)", UNSIGNED5);
    this.field_type_metadata_bands = this.field_attr_bands.newMultiBand("(field_type_metadata_bands)", UNSIGNED5);
    this.method_descr = this.class_bands.newCPRefBand("method_descr", MDELTA5, (byte)12);
    this.method_attr_bands = this.class_bands.newMultiBand("(method_attr_bands)", UNSIGNED5);
    this.method_flags_hi = this.method_attr_bands.newIntBand("method_flags_hi");
    this.method_flags_lo = this.method_attr_bands.newIntBand("method_flags_lo");
    this.method_attr_count = this.method_attr_bands.newIntBand("method_attr_count");
    this.method_attr_indexes = this.method_attr_bands.newIntBand("method_attr_indexes");
    this.method_attr_calls = this.method_attr_bands.newIntBand("method_attr_calls");
    this.method_Exceptions_N = this.method_attr_bands.newIntBand("method_Exceptions_N");
    this.method_Exceptions_RC = this.method_attr_bands.newCPRefBand("method_Exceptions_RC", (byte)7);
    this.method_Signature_RS = this.method_attr_bands.newCPRefBand("method_Signature_RS", (byte)13);
    this.method_metadata_bands = this.method_attr_bands.newMultiBand("(method_metadata_bands)", UNSIGNED5);
    this.method_MethodParameters_NB = this.method_attr_bands.newIntBand("method_MethodParameters_NB", BYTE1);
    this.method_MethodParameters_name_RUN = this.method_attr_bands.newCPRefBand("method_MethodParameters_name_RUN", UNSIGNED5, (byte)1, true);
    this.method_MethodParameters_flag_FH = this.method_attr_bands.newIntBand("method_MethodParameters_flag_FH");
    this.method_type_metadata_bands = this.method_attr_bands.newMultiBand("(method_type_metadata_bands)", UNSIGNED5);
    this.class_attr_bands = this.class_bands.newMultiBand("(class_attr_bands)", UNSIGNED5);
    this.class_flags_hi = this.class_attr_bands.newIntBand("class_flags_hi");
    this.class_flags_lo = this.class_attr_bands.newIntBand("class_flags_lo");
    this.class_attr_count = this.class_attr_bands.newIntBand("class_attr_count");
    this.class_attr_indexes = this.class_attr_bands.newIntBand("class_attr_indexes");
    this.class_attr_calls = this.class_attr_bands.newIntBand("class_attr_calls");
    this.class_SourceFile_RUN = this.class_attr_bands.newCPRefBand("class_SourceFile_RUN", UNSIGNED5, (byte)1, true);
    this.class_EnclosingMethod_RC = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RC", (byte)7);
    this.class_EnclosingMethod_RDN = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RDN", UNSIGNED5, (byte)12, true);
    this.class_Signature_RS = this.class_attr_bands.newCPRefBand("class_Signature_RS", (byte)13);
    this.class_metadata_bands = this.class_attr_bands.newMultiBand("(class_metadata_bands)", UNSIGNED5);
    this.class_InnerClasses_N = this.class_attr_bands.newIntBand("class_InnerClasses_N");
    this.class_InnerClasses_RC = this.class_attr_bands.newCPRefBand("class_InnerClasses_RC", (byte)7);
    this.class_InnerClasses_F = this.class_attr_bands.newIntBand("class_InnerClasses_F");
    this.class_InnerClasses_outer_RCN = this.class_attr_bands.newCPRefBand("class_InnerClasses_outer_RCN", UNSIGNED5, (byte)7, true);
    this.class_InnerClasses_name_RUN = this.class_attr_bands.newCPRefBand("class_InnerClasses_name_RUN", UNSIGNED5, (byte)1, true);
    this.class_ClassFile_version_minor_H = this.class_attr_bands.newIntBand("class_ClassFile_version_minor_H");
    this.class_ClassFile_version_major_H = this.class_attr_bands.newIntBand("class_ClassFile_version_major_H");
    this.class_type_metadata_bands = this.class_attr_bands.newMultiBand("(class_type_metadata_bands)", UNSIGNED5);
    this.code_bands = this.class_bands.newMultiBand("(code_bands)", UNSIGNED5);
    this.code_headers = this.code_bands.newByteBand("code_headers");
    this.code_max_stack = this.code_bands.newIntBand("code_max_stack", UNSIGNED5);
    this.code_max_na_locals = this.code_bands.newIntBand("code_max_na_locals", UNSIGNED5);
    this.code_handler_count = this.code_bands.newIntBand("code_handler_count", UNSIGNED5);
    this.code_handler_start_P = this.code_bands.newIntBand("code_handler_start_P", BCI5);
    this.code_handler_end_PO = this.code_bands.newIntBand("code_handler_end_PO", BRANCH5);
    this.code_handler_catch_PO = this.code_bands.newIntBand("code_handler_catch_PO", BRANCH5);
    this.code_handler_class_RCN = this.code_bands.newCPRefBand("code_handler_class_RCN", UNSIGNED5, (byte)7, true);
    this.code_attr_bands = this.class_bands.newMultiBand("(code_attr_bands)", UNSIGNED5);
    this.code_flags_hi = this.code_attr_bands.newIntBand("code_flags_hi");
    this.code_flags_lo = this.code_attr_bands.newIntBand("code_flags_lo");
    this.code_attr_count = this.code_attr_bands.newIntBand("code_attr_count");
    this.code_attr_indexes = this.code_attr_bands.newIntBand("code_attr_indexes");
    this.code_attr_calls = this.code_attr_bands.newIntBand("code_attr_calls");
    this.stackmap_bands = this.code_attr_bands.newMultiBand("(StackMapTable_bands)", UNSIGNED5);
    this.code_StackMapTable_N = this.stackmap_bands.newIntBand("code_StackMapTable_N");
    this.code_StackMapTable_frame_T = this.stackmap_bands.newIntBand("code_StackMapTable_frame_T", BYTE1);
    this.code_StackMapTable_local_N = this.stackmap_bands.newIntBand("code_StackMapTable_local_N");
    this.code_StackMapTable_stack_N = this.stackmap_bands.newIntBand("code_StackMapTable_stack_N");
    this.code_StackMapTable_offset = this.stackmap_bands.newIntBand("code_StackMapTable_offset", UNSIGNED5);
    this.code_StackMapTable_T = this.stackmap_bands.newIntBand("code_StackMapTable_T", BYTE1);
    this.code_StackMapTable_RC = this.stackmap_bands.newCPRefBand("code_StackMapTable_RC", (byte)7);
    this.code_StackMapTable_P = this.stackmap_bands.newIntBand("code_StackMapTable_P", BCI5);
    this.code_LineNumberTable_N = this.code_attr_bands.newIntBand("code_LineNumberTable_N");
    this.code_LineNumberTable_bci_P = this.code_attr_bands.newIntBand("code_LineNumberTable_bci_P", BCI5);
    this.code_LineNumberTable_line = this.code_attr_bands.newIntBand("code_LineNumberTable_line");
    this.code_LocalVariableTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTable_N");
    this.code_LocalVariableTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTable_bci_P", BCI5);
    this.code_LocalVariableTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTable_span_O", BRANCH5);
    this.code_LocalVariableTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_name_RU", (byte)1);
    this.code_LocalVariableTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_type_RS", (byte)13);
    this.code_LocalVariableTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTable_slot");
    this.code_LocalVariableTypeTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_N");
    this.code_LocalVariableTypeTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_bci_P", BCI5);
    this.code_LocalVariableTypeTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_span_O", BRANCH5);
    this.code_LocalVariableTypeTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_name_RU", (byte)1);
    this.code_LocalVariableTypeTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_type_RS", (byte)13);
    this.code_LocalVariableTypeTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_slot");
    this.code_type_metadata_bands = this.code_attr_bands.newMultiBand("(code_type_metadata_bands)", UNSIGNED5);
    this.bc_bands = this.all_bands.newMultiBand("(byte_codes)", UNSIGNED5);
    this.bc_codes = this.bc_bands.newByteBand("bc_codes");
    this.bc_case_count = this.bc_bands.newIntBand("bc_case_count");
    this.bc_case_value = this.bc_bands.newIntBand("bc_case_value", DELTA5);
    this.bc_byte = this.bc_bands.newByteBand("bc_byte");
    this.bc_short = this.bc_bands.newIntBand("bc_short", DELTA5);
    this.bc_local = this.bc_bands.newIntBand("bc_local");
    this.bc_label = this.bc_bands.newIntBand("bc_label", BRANCH5);
    this.bc_intref = this.bc_bands.newCPRefBand("bc_intref", DELTA5, (byte)3);
    this.bc_floatref = this.bc_bands.newCPRefBand("bc_floatref", DELTA5, (byte)4);
    this.bc_longref = this.bc_bands.newCPRefBand("bc_longref", DELTA5, (byte)5);
    this.bc_doubleref = this.bc_bands.newCPRefBand("bc_doubleref", DELTA5, (byte)6);
    this.bc_stringref = this.bc_bands.newCPRefBand("bc_stringref", DELTA5, (byte)8);
    this.bc_loadablevalueref = this.bc_bands.newCPRefBand("bc_loadablevalueref", DELTA5, (byte)51);
    this.bc_classref = this.bc_bands.newCPRefBand("bc_classref", UNSIGNED5, (byte)7, true);
    this.bc_fieldref = this.bc_bands.newCPRefBand("bc_fieldref", DELTA5, (byte)9);
    this.bc_methodref = this.bc_bands.newCPRefBand("bc_methodref", (byte)10);
    this.bc_imethodref = this.bc_bands.newCPRefBand("bc_imethodref", DELTA5, (byte)11);
    this.bc_indyref = this.bc_bands.newCPRefBand("bc_indyref", DELTA5, (byte)18);
    this.bc_thisfield = this.bc_bands.newCPRefBand("bc_thisfield", (byte)0);
    this.bc_superfield = this.bc_bands.newCPRefBand("bc_superfield", (byte)0);
    this.bc_thismethod = this.bc_bands.newCPRefBand("bc_thismethod", (byte)0);
    this.bc_supermethod = this.bc_bands.newCPRefBand("bc_supermethod", (byte)0);
    this.bc_initref = this.bc_bands.newIntBand("bc_initref");
    this.bc_escref = this.bc_bands.newCPRefBand("bc_escref", (byte)50);
    this.bc_escrefsize = this.bc_bands.newIntBand("bc_escrefsize");
    this.bc_escsize = this.bc_bands.newIntBand("bc_escsize");
    this.bc_escbyte = this.bc_bands.newByteBand("bc_escbyte");
    this.file_bands = this.all_bands.newMultiBand("(file_bands)", UNSIGNED5);
    this.file_name = this.file_bands.newCPRefBand("file_name", (byte)1);
    this.file_size_hi = this.file_bands.newIntBand("file_size_hi");
    this.file_size_lo = this.file_bands.newIntBand("file_size_lo");
    this.file_modtime = this.file_bands.newIntBand("file_modtime", DELTA5);
    this.file_options = this.file_bands.newIntBand("file_options");
    this.file_bits = this.file_bands.newByteBand("file_bits");
    this.metadataBands = new MultiBand[4];
    this.metadataBands[0] = this.class_metadata_bands;
    this.metadataBands[1] = this.field_metadata_bands;
    this.metadataBands[2] = this.method_metadata_bands;
    this.typeMetadataBands = new MultiBand[4];
    this.typeMetadataBands[0] = this.class_type_metadata_bands;
    this.typeMetadataBands[1] = this.field_type_metadata_bands;
    this.typeMetadataBands[2] = this.method_type_metadata_bands;
    this.typeMetadataBands[3] = this.code_type_metadata_bands;
    this.attrIndexLimit = new int[4];
    this.attrFlagMask = new long[4];
    this.attrDefSeen = new long[4];
    this.attrOverflowMask = new int[4];
    this.attrBandTable = new HashMap();
    this.attrIndexTable = new HashMap();
    this.attrDefs = new FixedList(4);
    for (int i = 0; i < 4; i++)
    {
      assert (this.attrIndexLimit[i] == 0);
      this.attrIndexLimit[i] = 32;
      this.attrDefs.set(i, new ArrayList(Collections.nCopies(this.attrIndexLimit[i], (Attribute.Layout)null)));
    }
    this.attrInnerClassesEmpty = predefineAttribute(23, 0, null, "InnerClasses", "");
    assert (this.attrInnerClassesEmpty == Package.attrInnerClassesEmpty);
    predefineAttribute(17, 0, new Band[] { this.class_SourceFile_RUN }, "SourceFile", "RUNH");
    predefineAttribute(18, 0, new Band[] { this.class_EnclosingMethod_RC, this.class_EnclosingMethod_RDN }, "EnclosingMethod", "RCHRDNH");
    this.attrClassFileVersion = predefineAttribute(24, 0, new Band[] { this.class_ClassFile_version_minor_H, this.class_ClassFile_version_major_H }, ".ClassFile.version", "HH");
    predefineAttribute(19, 0, new Band[] { this.class_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 0, null, "Deprecated", "");
    predefineAttribute(16, 0, null, ".Overflow", "");
    this.attrConstantValue = predefineAttribute(17, 1, new Band[] { this.field_ConstantValue_KQ }, "ConstantValue", "KQH");
    predefineAttribute(19, 1, new Band[] { this.field_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 1, null, "Deprecated", "");
    predefineAttribute(16, 1, null, ".Overflow", "");
    this.attrCodeEmpty = predefineAttribute(17, 2, null, "Code", "");
    predefineAttribute(18, 2, new Band[] { this.method_Exceptions_N, this.method_Exceptions_RC }, "Exceptions", "NH[RCH]");
    predefineAttribute(26, 2, new Band[] { this.method_MethodParameters_NB, this.method_MethodParameters_name_RUN, this.method_MethodParameters_flag_FH }, "MethodParameters", "NB[RUNHFH]");
    assert (this.attrCodeEmpty == Package.attrCodeEmpty);
    predefineAttribute(19, 2, new Band[] { this.method_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 2, null, "Deprecated", "");
    predefineAttribute(16, 2, null, ".Overflow", "");
    for (i = 0; i < 4; i++)
    {
      MultiBand localMultiBand1 = this.metadataBands[i];
      if (i != 3)
      {
        predefineAttribute(21, Constants.ATTR_CONTEXT_NAME[i] + "_RVA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeVisibleAnnotations"));
        predefineAttribute(22, Constants.ATTR_CONTEXT_NAME[i] + "_RIA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeInvisibleAnnotations"));
        if (i == 2)
        {
          predefineAttribute(23, "method_RVPA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeVisibleParameterAnnotations"));
          predefineAttribute(24, "method_RIPA_", localMultiBand1, Attribute.lookup(null, i, "RuntimeInvisibleParameterAnnotations"));
          predefineAttribute(25, "method_AD_", localMultiBand1, Attribute.lookup(null, i, "AnnotationDefault"));
        }
      }
      MultiBand localMultiBand2 = this.typeMetadataBands[i];
      predefineAttribute(27, Constants.ATTR_CONTEXT_NAME[i] + "_RVTA_", localMultiBand2, Attribute.lookup(null, i, "RuntimeVisibleTypeAnnotations"));
      predefineAttribute(28, Constants.ATTR_CONTEXT_NAME[i] + "_RITA_", localMultiBand2, Attribute.lookup(null, i, "RuntimeInvisibleTypeAnnotations"));
    }
    Attribute.Layout localLayout = Attribute.lookup(null, 3, "StackMapTable").layout();
    predefineAttribute(0, 3, this.stackmap_bands.toArray(), localLayout.name(), localLayout.layout());
    predefineAttribute(1, 3, new Band[] { this.code_LineNumberTable_N, this.code_LineNumberTable_bci_P, this.code_LineNumberTable_line }, "LineNumberTable", "NH[PHH]");
    predefineAttribute(2, 3, new Band[] { this.code_LocalVariableTable_N, this.code_LocalVariableTable_bci_P, this.code_LocalVariableTable_span_O, this.code_LocalVariableTable_name_RU, this.code_LocalVariableTable_type_RS, this.code_LocalVariableTable_slot }, "LocalVariableTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(3, 3, new Band[] { this.code_LocalVariableTypeTable_N, this.code_LocalVariableTypeTable_bci_P, this.code_LocalVariableTypeTable_span_O, this.code_LocalVariableTypeTable_name_RU, this.code_LocalVariableTypeTable_type_RS, this.code_LocalVariableTypeTable_slot }, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(16, 3, null, ".Overflow", "");
    for (int j = 0; j < 4; j++) {
      this.attrDefSeen[j] = 0L;
    }
    for (j = 0; j < 4; j++)
    {
      this.attrOverflowMask[j] = 65536;
      this.attrIndexLimit[j] = 0;
    }
    this.attrClassFileVersionMask = 16777216;
    this.attrBands = new MultiBand[4];
    this.attrBands[0] = this.class_attr_bands;
    this.attrBands[1] = this.field_attr_bands;
    this.attrBands[2] = this.method_attr_bands;
    this.attrBands[3] = this.code_attr_bands;
    this.shortCodeHeader_h_limit = shortCodeLimits.length;
  }
  
  public static Coding codingForIndex(int paramInt)
  {
    return paramInt < basicCodings.length ? basicCodings[paramInt] : null;
  }
  
  public static int indexOf(Coding paramCoding)
  {
    Integer localInteger = (Integer)basicCodingIndexes.get(paramCoding);
    if (localInteger == null) {
      return 0;
    }
    return localInteger.intValue();
  }
  
  public static Coding[] getBasicCodings()
  {
    return (Coding[])basicCodings.clone();
  }
  
  protected CodingMethod getBandHeader(int paramInt, Coding paramCoding)
  {
    CodingMethod[] arrayOfCodingMethod = { null };
    this.bandHeaderBytes[(--this.bandHeaderBytePos)] = ((byte)paramInt);
    this.bandHeaderBytePos0 = this.bandHeaderBytePos;
    this.bandHeaderBytePos = parseMetaCoding(this.bandHeaderBytes, this.bandHeaderBytePos, paramCoding, arrayOfCodingMethod);
    return arrayOfCodingMethod[0];
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod)
  {
    if ((paramArrayOfByte[paramInt] & 0xFF) == 0)
    {
      paramArrayOfCodingMethod[0] = paramCoding;
      return paramInt + 1;
    }
    int i = Coding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    i = PopulationCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    i = AdaptiveCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt) {
      return i;
    }
    throw new RuntimeException("Bad meta-coding op " + (paramArrayOfByte[paramInt] & 0xFF));
  }
  
  static boolean phaseIsRead(int paramInt)
  {
    return paramInt % 2 == 0;
  }
  
  static int phaseCmp(int paramInt1, int paramInt2)
  {
    assert ((paramInt1 % 2 == paramInt2 % 2) || (paramInt1 % 8 == 0) || (paramInt2 % 8 == 0));
    return paramInt1 - paramInt2;
  }
  
  static int getIntTotal(int[] paramArrayOfInt)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfInt.length; j++) {
      i += paramArrayOfInt[j];
    }
    return i;
  }
  
  int encodeRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
  {
    if (paramIndex == null) {
      throw new RuntimeException("null index for " + paramEntry.stringValue());
    }
    int i = paramIndex.indexOf(paramEntry);
    if (this.verbose > 2) {
      Utils.log.fine("putRef " + i + " => " + paramEntry);
    }
    return i;
  }
  
  ConstantPool.Entry decodeRef(int paramInt, ConstantPool.Index paramIndex)
  {
    if ((paramInt < 0) || (paramInt >= paramIndex.size())) {
      Utils.log.warning("decoding bad ref " + paramInt + " in " + paramIndex);
    }
    ConstantPool.Entry localEntry = paramIndex.getEntry(paramInt);
    if (this.verbose > 2) {
      Utils.log.fine("getRef " + paramInt + " => " + localEntry);
    }
    return localEntry;
  }
  
  protected CodingChooser getCodingChooser()
  {
    if (this.codingChooser == null)
    {
      this.codingChooser = new CodingChooser(this.effort, basicCodings);
      if ((this.codingChooser.stress != null) && ((this instanceof PackageWriter)))
      {
        ArrayList localArrayList = ((PackageWriter)this).pkg.classes;
        if (!localArrayList.isEmpty())
        {
          Package.Class localClass = (Package.Class)localArrayList.get(0);
          this.codingChooser.addStressSeed(localClass.getName().hashCode());
        }
      }
    }
    return this.codingChooser;
  }
  
  public CodingMethod chooseCoding(int[] paramArrayOfInt1, int paramInt1, int paramInt2, Coding paramCoding, String paramString, int[] paramArrayOfInt2)
  {
    assert (this.optVaryCodings);
    if (this.effort <= 1) {
      return paramCoding;
    }
    CodingChooser localCodingChooser = getCodingChooser();
    if ((this.verbose > 1) || (localCodingChooser.verbose > 1)) {
      Utils.log.fine("--- chooseCoding " + paramString);
    }
    return localCodingChooser.choose(paramArrayOfInt1, paramInt1, paramInt2, paramCoding, paramArrayOfInt2);
  }
  
  protected static int decodeEscapeValue(int paramInt, Coding paramCoding)
  {
    if ((paramCoding.B() == 1) || (paramCoding.L() == 0)) {
      return -1;
    }
    int i;
    if (paramCoding.S() != 0)
    {
      if ((65280 <= paramInt) && (paramInt <= -1) && (paramCoding.min() <= 65280))
      {
        i = -1 - paramInt;
        assert ((i >= 0) && (i < 256));
        return i;
      }
    }
    else
    {
      i = paramCoding.L();
      if ((i <= paramInt) && (paramInt <= i + 255) && (paramCoding.max() >= i + 255))
      {
        int j = paramInt - i;
        assert ((j >= 0) && (j < 256));
        return j;
      }
    }
    return -1;
  }
  
  protected static int encodeEscapeValue(int paramInt, Coding paramCoding)
  {
    assert ((paramInt >= 0) && (paramInt < 256));
    assert ((paramCoding.B() > 1) && (paramCoding.L() > 0));
    int i;
    if (paramCoding.S() != 0)
    {
      assert (paramCoding.min() <= 65280);
      i = -1 - paramInt;
    }
    else
    {
      int j = paramCoding.L();
      assert (paramCoding.max() >= j + 255);
      i = paramInt + j;
    }
    assert (decodeEscapeValue(i, paramCoding) == paramInt) : (paramCoding + " XB=" + paramInt + " X=" + i);
    return i;
  }
  
  void writeAllBandsTo(OutputStream paramOutputStream)
    throws IOException
  {
    this.outputCounter = new ByteCounter(paramOutputStream);
    paramOutputStream = this.outputCounter;
    this.all_bands.writeTo(paramOutputStream);
    if (this.verbose > 0)
    {
      long l = this.outputCounter.getCount();
      Utils.log.info("Wrote total of " + l + " bytes.");
      assert (l == this.archiveSize0 + this.archiveSize1);
    }
    this.outputCounter = null;
  }
  
  static IntBand getAttrBand(MultiBand paramMultiBand, int paramInt)
  {
    IntBand localIntBand = (IntBand)paramMultiBand.get(paramInt);
    switch (paramInt)
    {
    case 0: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_flags_hi"))) {
        throw new AssertionError();
      }
      break;
    case 1: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_flags_lo"))) {
        throw new AssertionError();
      }
      break;
    case 2: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_count"))) {
        throw new AssertionError();
      }
      break;
    case 3: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_indexes"))) {
        throw new AssertionError();
      }
      break;
    case 4: 
      if ((!$assertionsDisabled) && (!localIntBand.name().endsWith("_attr_calls"))) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    return localIntBand;
  }
  
  protected void setBandIndexes()
  {
    Iterator localIterator = this.needPredefIndex.iterator();
    while (localIterator.hasNext())
    {
      Object[] arrayOfObject = (Object[])localIterator.next();
      CPRefBand localCPRefBand = (CPRefBand)arrayOfObject[0];
      Byte localByte = (Byte)arrayOfObject[1];
      localCPRefBand.setIndex(getCPIndex(localByte.byteValue()));
    }
    this.needPredefIndex = null;
    if (this.verbose > 3) {
      printCDecl(this.all_bands);
    }
  }
  
  protected void setBandIndex(CPRefBand paramCPRefBand, byte paramByte)
  {
    Object[] arrayOfObject = { paramCPRefBand, Byte.valueOf(paramByte) };
    if (paramByte == 53) {
      this.allKQBands.add(paramCPRefBand);
    } else if (this.needPredefIndex != null) {
      this.needPredefIndex.add(arrayOfObject);
    } else {
      paramCPRefBand.setIndex(getCPIndex(paramByte));
    }
  }
  
  protected void setConstantValueIndex(Package.Class.Field paramField)
  {
    ConstantPool.Index localIndex = null;
    if (paramField != null)
    {
      byte b = paramField.getLiteralTag();
      localIndex = getCPIndex(b);
      if (this.verbose > 2) {
        Utils.log.fine("setConstantValueIndex " + paramField + " " + ConstantPool.tagName(b) + " => " + localIndex);
      }
      assert (localIndex != null);
    }
    Iterator localIterator = this.allKQBands.iterator();
    while (localIterator.hasNext())
    {
      CPRefBand localCPRefBand = (CPRefBand)localIterator.next();
      localCPRefBand.setIndex(localIndex);
    }
  }
  
  private void adjustToClassVersion()
    throws IOException
  {
    if (getHighestClassVersion().lessThan(Constants.JAVA6_MAX_CLASS_VERSION))
    {
      if (this.verbose > 0) {
        Utils.log.fine("Legacy package version");
      }
      undefineAttribute(0, 3);
    }
  }
  
  protected void initAttrIndexLimit()
  {
    for (int i = 0; i < 4; i++)
    {
      assert (this.attrIndexLimit[i] == 0);
      this.attrIndexLimit[i] = (haveFlagsHi(i) ? 63 : 32);
      List localList = (List)this.attrDefs.get(i);
      assert (localList.size() == 32);
      int j = this.attrIndexLimit[i] - localList.size();
      localList.addAll(Collections.nCopies(j, (Attribute.Layout)null));
    }
  }
  
  protected boolean haveFlagsHi(int paramInt)
  {
    int i = 1 << 9 + paramInt;
    switch (paramInt)
    {
    case 0: 
      if ((!$assertionsDisabled) && (i != 512)) {
        throw new AssertionError();
      }
      break;
    case 1: 
      if ((!$assertionsDisabled) && (i != 1024)) {
        throw new AssertionError();
      }
      break;
    case 2: 
      if ((!$assertionsDisabled) && (i != 2048)) {
        throw new AssertionError();
      }
      break;
    case 3: 
      if ((!$assertionsDisabled) && (i != 4096)) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    return testBit(this.archiveOptions, i);
  }
  
  protected List<Attribute.Layout> getPredefinedAttrs(int paramInt)
  {
    assert (this.attrIndexLimit[paramInt] != 0);
    ArrayList localArrayList = new ArrayList(this.attrIndexLimit[paramInt]);
    for (int i = 0; i < this.attrIndexLimit[paramInt]; i++) {
      if (!testBit(this.attrDefSeen[paramInt], 1L << i))
      {
        Attribute.Layout localLayout = (Attribute.Layout)((List)this.attrDefs.get(paramInt)).get(i);
        if (localLayout != null)
        {
          assert (isPredefinedAttr(paramInt, i));
          localArrayList.add(localLayout);
        }
      }
    }
    return localArrayList;
  }
  
  protected boolean isPredefinedAttr(int paramInt1, int paramInt2)
  {
    assert (this.attrIndexLimit[paramInt1] != 0);
    if (paramInt2 >= this.attrIndexLimit[paramInt1]) {
      return false;
    }
    if (testBit(this.attrDefSeen[paramInt1], 1L << paramInt2)) {
      return false;
    }
    return ((List)this.attrDefs.get(paramInt1)).get(paramInt2) != null;
  }
  
  protected void adjustSpecialAttrMasks()
  {
    this.attrClassFileVersionMask = ((int)(this.attrClassFileVersionMask & (this.attrDefSeen[0] ^ 0xFFFFFFFFFFFFFFFF)));
    for (int i = 0; i < 4; i++)
    {
      int tmp33_32 = i;
      int[] tmp33_29 = this.attrOverflowMask;
      tmp33_29[tmp33_32] = ((int)(tmp33_29[tmp33_32] & (this.attrDefSeen[i] ^ 0xFFFFFFFFFFFFFFFF)));
    }
  }
  
  protected Attribute makeClassFileVersionAttr(Package.Version paramVersion)
  {
    return this.attrClassFileVersion.addContent(paramVersion.asBytes());
  }
  
  protected Package.Version parseClassFileVersionAttr(Attribute paramAttribute)
  {
    assert (paramAttribute.layout() == this.attrClassFileVersion);
    assert (paramAttribute.size() == 4);
    return Package.Version.of(paramAttribute.bytes());
  }
  
  private boolean assertBandOKForElems(Band[] paramArrayOfBand, Attribute.Layout.Element[] paramArrayOfElement)
  {
    for (int i = 0; i < paramArrayOfElement.length; i++) {
      assert (assertBandOKForElem(paramArrayOfBand, paramArrayOfElement[i]));
    }
    return true;
  }
  
  private boolean assertBandOKForElem(Band[] paramArrayOfBand, Attribute.Layout.Element paramElement)
  {
    Band localBand = null;
    if (paramElement.bandIndex != -1) {
      localBand = paramArrayOfBand[paramElement.bandIndex];
    }
    Coding localCoding = UNSIGNED5;
    int i = 1;
    switch (paramElement.kind)
    {
    case 1: 
      if (paramElement.flagTest((byte)1)) {
        localCoding = SIGNED5;
      } else if (paramElement.len == 1) {
        localCoding = BYTE1;
      }
      break;
    case 2: 
      if (!paramElement.flagTest((byte)2)) {
        localCoding = BCI5;
      } else {
        localCoding = BRANCH5;
      }
      break;
    case 3: 
      localCoding = BRANCH5;
      break;
    case 4: 
      if (paramElement.len == 1) {
        localCoding = BYTE1;
      }
      break;
    case 5: 
      if (paramElement.len == 1) {
        localCoding = BYTE1;
      }
      assertBandOKForElems(paramArrayOfBand, paramElement.body);
      break;
    case 7: 
      if (paramElement.flagTest((byte)1)) {
        localCoding = SIGNED5;
      } else if (paramElement.len == 1) {
        localCoding = BYTE1;
      }
      assertBandOKForElems(paramArrayOfBand, paramElement.body);
      break;
    case 8: 
      assert (localBand == null);
      assertBandOKForElems(paramArrayOfBand, paramElement.body);
      return true;
    case 9: 
      assert (localBand == null);
      return true;
    case 10: 
      assert (localBand == null);
      assertBandOKForElems(paramArrayOfBand, paramElement.body);
      return true;
    case 6: 
      i = 0;
      assert ((localBand instanceof CPRefBand));
      if ((!$assertionsDisabled) && (((CPRefBand)localBand).nullOK != paramElement.flagTest((byte)4))) {
        throw new AssertionError();
      }
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      break;
    }
    assert (localBand.regularCoding == localCoding) : (paramElement + " // " + localBand);
    if ((i != 0) && (!$assertionsDisabled) && (!(localBand instanceof IntBand))) {
      throw new AssertionError();
    }
    return true;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt1, int paramInt2, Band[] paramArrayOfBand, String paramString1, String paramString2)
  {
    Attribute.Layout localLayout = Attribute.find(paramInt2, paramString1, paramString2).layout();
    if (paramInt1 >= 0) {
      setAttributeLayoutIndex(localLayout, paramInt1);
    }
    if (paramArrayOfBand == null) {
      paramArrayOfBand = new Band[0];
    }
    assert (this.attrBandTable.get(localLayout) == null);
    this.attrBandTable.put(localLayout, paramArrayOfBand);
    assert (localLayout.bandCount == paramArrayOfBand.length) : (localLayout + " // " + Arrays.asList(paramArrayOfBand));
    assert (assertBandOKForElems(paramArrayOfBand, localLayout.elems));
    return localLayout;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt, String paramString, MultiBand paramMultiBand, Attribute paramAttribute)
  {
    Attribute.Layout localLayout = paramAttribute.layout();
    int i = localLayout.ctype();
    return predefineAttribute(paramInt, i, makeNewAttributeBands(paramString, localLayout, paramMultiBand), localLayout.name(), localLayout.layout());
  }
  
  private void undefineAttribute(int paramInt1, int paramInt2)
  {
    if (this.verbose > 1) {
      System.out.println("Removing predefined " + Constants.ATTR_CONTEXT_NAME[paramInt2] + " attribute on bit " + paramInt1);
    }
    List localList = (List)this.attrDefs.get(paramInt2);
    Attribute.Layout localLayout = (Attribute.Layout)localList.get(paramInt1);
    assert (localLayout != null);
    localList.set(paramInt1, null);
    this.attrIndexTable.put(localLayout, null);
    assert (paramInt1 < 64);
    this.attrDefSeen[paramInt2] &= (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFF);
    this.attrFlagMask[paramInt2] &= (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFF);
    Band[] arrayOfBand = (Band[])this.attrBandTable.get(localLayout);
    for (int i = 0; i < arrayOfBand.length; i++) {
      arrayOfBand[i].doneWithUnusedBand();
    }
  }
  
  void makeNewAttributeBands()
  {
    adjustSpecialAttrMasks();
    for (int i = 0; i < 4; i++)
    {
      String str1 = Constants.ATTR_CONTEXT_NAME[i];
      MultiBand localMultiBand = this.attrBands[i];
      long l = this.attrDefSeen[i];
      assert ((l & (this.attrFlagMask[i] ^ 0xFFFFFFFFFFFFFFFF)) == 0L);
      for (int j = 0; j < ((List)this.attrDefs.get(i)).size(); j++)
      {
        Attribute.Layout localLayout = (Attribute.Layout)((List)this.attrDefs.get(i)).get(j);
        if ((localLayout != null) && (localLayout.bandCount != 0)) {
          if ((j < this.attrIndexLimit[i]) && (!testBit(l, 1L << j)))
          {
            if ((!$assertionsDisabled) && (this.attrBandTable.get(localLayout) == null)) {
              throw new AssertionError();
            }
          }
          else
          {
            int k = localMultiBand.size();
            String str2 = str1 + "_" + localLayout.name() + "_";
            if (this.verbose > 1) {
              Utils.log.fine("Making new bands for " + localLayout);
            }
            Band[] arrayOfBand1 = makeNewAttributeBands(str2, localLayout, localMultiBand);
            assert (arrayOfBand1.length == localLayout.bandCount);
            Band[] arrayOfBand2 = (Band[])this.attrBandTable.put(localLayout, arrayOfBand1);
            if (arrayOfBand2 != null) {
              for (int m = 0; m < arrayOfBand2.length; m++) {
                arrayOfBand2[m].doneWithUnusedBand();
              }
            }
          }
        }
      }
    }
  }
  
  private Band[] makeNewAttributeBands(String paramString, Attribute.Layout paramLayout, MultiBand paramMultiBand)
  {
    int i = paramMultiBand.size();
    makeNewAttributeBands(paramString, paramLayout.elems, paramMultiBand);
    int j = paramMultiBand.size() - i;
    Band[] arrayOfBand = new Band[j];
    for (int k = 0; k < j; k++) {
      arrayOfBand[k] = paramMultiBand.get(i + k);
    }
    return arrayOfBand;
  }
  
  private void makeNewAttributeBands(String paramString, Attribute.Layout.Element[] paramArrayOfElement, MultiBand paramMultiBand)
  {
    for (int i = 0; i < paramArrayOfElement.length; i++)
    {
      Attribute.Layout.Element localElement = paramArrayOfElement[i];
      String str = paramString + paramMultiBand.size() + "_" + localElement.layout;
      int j;
      if ((j = str.indexOf('[')) > 0) {
        str = str.substring(0, j);
      }
      if ((j = str.indexOf('(')) > 0) {
        str = str.substring(0, j);
      }
      if (str.endsWith("H")) {
        str = str.substring(0, str.length() - 1);
      }
      Object localObject;
      switch (localElement.kind)
      {
      case 1: 
        localObject = newElemBand(localElement, str, paramMultiBand);
        break;
      case 2: 
        if (!localElement.flagTest((byte)2)) {
          localObject = paramMultiBand.newIntBand(str, BCI5);
        } else {
          localObject = paramMultiBand.newIntBand(str, BRANCH5);
        }
        break;
      case 3: 
        localObject = paramMultiBand.newIntBand(str, BRANCH5);
        break;
      case 4: 
        assert (!localElement.flagTest((byte)1));
        localObject = newElemBand(localElement, str, paramMultiBand);
        break;
      case 5: 
        assert (!localElement.flagTest((byte)1));
        localObject = newElemBand(localElement, str, paramMultiBand);
        makeNewAttributeBands(paramString, localElement.body, paramMultiBand);
        break;
      case 7: 
        localObject = newElemBand(localElement, str, paramMultiBand);
        makeNewAttributeBands(paramString, localElement.body, paramMultiBand);
        break;
      case 8: 
        if (localElement.flagTest((byte)8)) {
          continue;
        }
        makeNewAttributeBands(paramString, localElement.body, paramMultiBand);
        break;
      case 6: 
        byte b = localElement.refKind;
        boolean bool = localElement.flagTest((byte)4);
        localObject = paramMultiBand.newCPRefBand(str, UNSIGNED5, b, bool);
        break;
      case 9: 
        break;
      case 10: 
        makeNewAttributeBands(paramString, localElement.body, paramMultiBand);
        break;
      default: 
        if ($assertionsDisabled) {
          continue;
        }
        throw new AssertionError();
      }
      if (this.verbose > 1) {
        Utils.log.fine("New attribute band " + localObject);
      }
    }
  }
  
  private Band newElemBand(Attribute.Layout.Element paramElement, String paramString, MultiBand paramMultiBand)
  {
    if (paramElement.flagTest((byte)1)) {
      return paramMultiBand.newIntBand(paramString, SIGNED5);
    }
    if (paramElement.len == 1) {
      return paramMultiBand.newIntBand(paramString, BYTE1);
    }
    return paramMultiBand.newIntBand(paramString, UNSIGNED5);
  }
  
  protected int setAttributeLayoutIndex(Attribute.Layout paramLayout, int paramInt)
  {
    int i = paramLayout.ctype;
    assert ((-1 <= paramInt) && (paramInt < this.attrIndexLimit[i]));
    List localList = (List)this.attrDefs.get(i);
    if (paramInt == -1)
    {
      paramInt = localList.size();
      localList.add(paramLayout);
      if (this.verbose > 0) {
        Utils.log.info("Adding new attribute at " + paramLayout + ": " + paramInt);
      }
      this.attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
      return paramInt;
    }
    if (testBit(this.attrDefSeen[i], 1L << paramInt)) {
      throw new RuntimeException("Multiple explicit definition at " + paramInt + ": " + paramLayout);
    }
    this.attrDefSeen[i] |= 1L << paramInt;
    assert ((0 <= paramInt) && (paramInt < this.attrIndexLimit[i]));
    if (this.verbose > (this.attrClassFileVersionMask == 0 ? 2 : 0)) {
      Utils.log.fine("Fixing new attribute at " + paramInt + ": " + paramLayout + (localList.get(paramInt) == null ? "" : new StringBuilder().append("; replacing ").append(localList.get(paramInt)).toString()));
    }
    this.attrFlagMask[i] |= 1L << paramInt;
    this.attrIndexTable.put(localList.get(paramInt), null);
    localList.set(paramInt, paramLayout);
    this.attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
    return paramInt;
  }
  
  static int shortCodeHeader(Code paramCode)
  {
    int i = paramCode.max_stack;
    int j = paramCode.max_locals;
    int k = paramCode.handler_class.length;
    if (k >= shortCodeLimits.length) {
      return 0;
    }
    int m = paramCode.getMethod().getArgumentSize();
    assert (j >= m);
    if (j < m) {
      return 0;
    }
    int n = j - m;
    int i1 = shortCodeLimits[k][0];
    int i2 = shortCodeLimits[k][1];
    if ((i >= i1) || (n >= i2)) {
      return 0;
    }
    int i3 = shortCodeHeader_h_base(k);
    i3 += i + i1 * n;
    if (i3 > 255) {
      return 0;
    }
    assert (shortCodeHeader_max_stack(i3) == i);
    assert (shortCodeHeader_max_na_locals(i3) == n);
    assert (shortCodeHeader_handler_count(i3) == k);
    return i3;
  }
  
  static int shortCodeHeader_handler_count(int paramInt)
  {
    assert ((paramInt > 0) && (paramInt <= 255));
    for (int i = 0;; i++) {
      if (paramInt < shortCodeHeader_h_base(i + 1)) {
        return i;
      }
    }
  }
  
  static int shortCodeHeader_max_stack(int paramInt)
  {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) % j;
  }
  
  static int shortCodeHeader_max_na_locals(int paramInt)
  {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) / j;
  }
  
  private static int shortCodeHeader_h_base(int paramInt)
  {
    assert (paramInt <= shortCodeLimits.length);
    int i = 1;
    for (int j = 0; j < paramInt; j++)
    {
      int k = shortCodeLimits[j][0];
      int m = shortCodeLimits[j][1];
      i += k * m;
    }
    return i;
  }
  
  protected void putLabel(IntBand paramIntBand, Code paramCode, int paramInt1, int paramInt2)
  {
    paramIntBand.putInt(paramCode.encodeBCI(paramInt2) - paramCode.encodeBCI(paramInt1));
  }
  
  protected int getLabel(IntBand paramIntBand, Code paramCode, int paramInt)
  {
    return paramCode.decodeBCI(paramIntBand.getInt() + paramCode.encodeBCI(paramInt));
  }
  
  protected CPRefBand getCPRefOpBand(int paramInt)
  {
    switch (Instruction.getCPRefOpTag(paramInt))
    {
    case 7: 
      return this.bc_classref;
    case 9: 
      return this.bc_fieldref;
    case 10: 
      return this.bc_methodref;
    case 11: 
      return this.bc_imethodref;
    case 18: 
      return this.bc_indyref;
    case 51: 
      switch (paramInt)
      {
      case 234: 
      case 237: 
        return this.bc_intref;
      case 235: 
      case 238: 
        return this.bc_floatref;
      case 20: 
        return this.bc_longref;
      case 239: 
        return this.bc_doubleref;
      case 18: 
      case 19: 
        return this.bc_stringref;
      case 233: 
      case 236: 
        return this.bc_classref;
      case 240: 
      case 241: 
        return this.bc_loadablevalueref;
      }
      break;
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return null;
  }
  
  protected CPRefBand selfOpRefBand(int paramInt)
  {
    assert (Instruction.isSelfLinkerOp(paramInt));
    int i = paramInt - 202;
    int j = i >= 14 ? 1 : 0;
    if (j != 0) {
      i -= 14;
    }
    int k = i >= 7 ? 1 : 0;
    if (k != 0) {
      i -= 7;
    }
    int m = 178 + i;
    boolean bool = Instruction.isFieldOp(m);
    if (j == 0) {
      return bool ? this.bc_thisfield : this.bc_thismethod;
    }
    return bool ? this.bc_superfield : this.bc_supermethod;
  }
  
  static OutputStream getDumpStream(Band paramBand, String paramString)
    throws IOException
  {
    return getDumpStream(paramBand.name, paramBand.seqForDebug, paramString, paramBand);
  }
  
  static OutputStream getDumpStream(ConstantPool.Index paramIndex, String paramString)
    throws IOException
  {
    if (paramIndex.size() == 0) {
      return new ByteArrayOutputStream();
    }
    int i = ConstantPool.TAG_ORDER[paramIndex.cpMap[0].tag];
    return getDumpStream(paramIndex.debugName, i, paramString, paramIndex);
  }
  
  static OutputStream getDumpStream(String paramString1, int paramInt, String paramString2, Object paramObject)
    throws IOException
  {
    if (dumpDir == null)
    {
      dumpDir = File.createTempFile("BD_", "", new File("."));
      dumpDir.delete();
      if (dumpDir.mkdir()) {
        Utils.log.info("Dumping bands to " + dumpDir);
      }
    }
    paramString1 = paramString1.replace('(', ' ').replace(')', ' ');
    paramString1 = paramString1.replace('/', ' ');
    paramString1 = paramString1.replace('*', ' ');
    paramString1 = paramString1.trim().replace(' ', '_');
    paramString1 = (10000 + paramInt + "_" + paramString1).substring(1);
    File localFile = new File(dumpDir, paramString1 + paramString2);
    Utils.log.info("Dumping " + paramObject + " to " + localFile);
    return new BufferedOutputStream(new FileOutputStream(localFile));
  }
  
  static boolean assertCanChangeLength(Band paramBand)
  {
    switch (paramBand.phase)
    {
    case 1: 
    case 4: 
      return true;
    }
    return false;
  }
  
  static boolean assertPhase(Band paramBand, int paramInt)
  {
    if (paramBand.phase() != paramInt)
    {
      Utils.log.warning("phase expected " + paramInt + " was " + paramBand.phase() + " in " + paramBand);
      return false;
    }
    return true;
  }
  
  static int verbose()
  {
    return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
  }
  
  static boolean assertPhaseChangeOK(Band paramBand, int paramInt1, int paramInt2)
  {
    switch (paramInt1 * 10 + paramInt2)
    {
    case 1: 
      assert (!paramBand.isReader());
      assert (paramBand.capacity() >= 0);
      assert (paramBand.length() == 0);
      return true;
    case 13: 
    case 33: 
      assert (paramBand.length() == 0);
      return true;
    case 15: 
    case 35: 
      return true;
    case 58: 
      return true;
    case 2: 
      assert (paramBand.isReader());
      assert (paramBand.capacity() < 0);
      return true;
    case 24: 
      assert (Math.max(0, paramBand.capacity()) >= paramBand.valuesExpected());
      assert (paramBand.length() <= 0);
      return true;
    case 46: 
      assert (paramBand.valuesRemainingForDebug() == paramBand.length());
      return true;
    case 68: 
      assert (assertDoneDisbursing(paramBand));
      return true;
    }
    if (paramInt1 == paramInt2) {
      Utils.log.warning("Already in phase " + paramInt1);
    } else {
      Utils.log.warning("Unexpected phase " + paramInt1 + " -> " + paramInt2);
    }
    return false;
  }
  
  private static boolean assertDoneDisbursing(Band paramBand)
  {
    if (paramBand.phase != 6)
    {
      Utils.log.warning("assertDoneDisbursing: still in phase " + paramBand.phase + ": " + paramBand);
      if (verbose() <= 1) {
        return false;
      }
    }
    int i = paramBand.valuesRemainingForDebug();
    if (i > 0)
    {
      Utils.log.warning("assertDoneDisbursing: " + i + " values left in " + paramBand);
      if (verbose() <= 1) {
        return false;
      }
    }
    if ((paramBand instanceof MultiBand))
    {
      MultiBand localMultiBand = (MultiBand)paramBand;
      for (int j = 0; j < localMultiBand.bandCount; j++)
      {
        Band localBand = localMultiBand.bands[j];
        if (localBand.phase != 8)
        {
          Utils.log.warning("assertDoneDisbursing: sub-band still in phase " + localBand.phase + ": " + localBand);
          if (verbose() <= 1) {
            return false;
          }
        }
      }
    }
    return true;
  }
  
  private static void printCDecl(Band paramBand)
  {
    if ((paramBand instanceof MultiBand))
    {
      localObject1 = (MultiBand)paramBand;
      for (int i = 0; i < ((MultiBand)localObject1).bandCount; i++) {
        printCDecl(localObject1.bands[i]);
      }
      return;
    }
    Object localObject1 = "NULL";
    if ((paramBand instanceof CPRefBand))
    {
      localObject2 = ((CPRefBand)paramBand).index;
      if (localObject2 != null) {
        localObject1 = "INDEX(" + ((ConstantPool.Index)localObject2).debugName + ")";
      }
    }
    Object localObject2 = { BYTE1, CHAR3, BCI5, BRANCH5, UNSIGNED5, UDELTA5, SIGNED5, DELTA5, MDELTA5 };
    String[] arrayOfString = { "BYTE1", "CHAR3", "BCI5", "BRANCH5", "UNSIGNED5", "UDELTA5", "SIGNED5", "DELTA5", "MDELTA5" };
    Coding localCoding = paramBand.regularCoding;
    int j = Arrays.asList((Object[])localObject2).indexOf(localCoding);
    String str;
    if (j >= 0) {
      str = arrayOfString[j];
    } else {
      str = "CODING" + localCoding.keyString();
    }
    System.out.println("  BAND_INIT(\"" + paramBand.name() + "\"" + ", " + str + ", " + (String)localObject1 + "),");
  }
  
  boolean notePrevForAssert(Band paramBand1, Band paramBand2)
  {
    if (this.prevForAssertMap == null) {
      this.prevForAssertMap = new HashMap();
    }
    this.prevForAssertMap.put(paramBand1, paramBand2);
    return true;
  }
  
  private boolean assertReadyToReadFrom(Band paramBand, InputStream paramInputStream)
    throws IOException
  {
    Band localBand = (Band)this.prevForAssertMap.get(paramBand);
    if ((localBand != null) && (phaseCmp(localBand.phase(), 6) < 0))
    {
      Utils.log.warning("Previous band not done reading.");
      Utils.log.info("    Previous band: " + localBand);
      Utils.log.info("        Next band: " + paramBand);
      assert (this.verbose > 0);
    }
    String str1 = paramBand.name;
    if ((this.optDebugBands) && (!str1.startsWith("(")))
    {
      assert (bandSequenceList != null);
      String str2 = (String)bandSequenceList.removeFirst();
      if (!str2.equals(str1))
      {
        Utils.log.warning("Expected " + str1 + " but read: " + str2);
        return false;
      }
      Utils.log.info("Read band in sequence: " + str1);
    }
    return true;
  }
  
  private boolean assertValidCPRefs(CPRefBand paramCPRefBand)
  {
    if (paramCPRefBand.index == null) {
      return true;
    }
    int i = paramCPRefBand.index.size() + 1;
    for (int j = 0; j < paramCPRefBand.length(); j++)
    {
      int k = paramCPRefBand.valueAtForDebug(j);
      if ((k < 0) || (k >= i))
      {
        Utils.log.warning("CP ref out of range [" + j + "] = " + k + " in " + paramCPRefBand);
        return false;
      }
    }
    return true;
  }
  
  private boolean assertReadyToWriteTo(Band paramBand, OutputStream paramOutputStream)
    throws IOException
  {
    Band localBand = (Band)this.prevForAssertMap.get(paramBand);
    if ((localBand != null) && (phaseCmp(localBand.phase(), 8) < 0))
    {
      Utils.log.warning("Previous band not done writing.");
      Utils.log.info("    Previous band: " + localBand);
      Utils.log.info("        Next band: " + paramBand);
      assert (this.verbose > 0);
    }
    String str = paramBand.name;
    if ((this.optDebugBands) && (!str.startsWith("(")))
    {
      if (bandSequenceList == null) {
        bandSequenceList = new LinkedList();
      }
      bandSequenceList.add(str);
    }
    return true;
  }
  
  protected static boolean testBit(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) != 0;
  }
  
  protected static int setBit(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return paramBoolean ? paramInt1 | paramInt2 : paramInt1 & (paramInt2 ^ 0xFFFFFFFF);
  }
  
  protected static boolean testBit(long paramLong1, long paramLong2)
  {
    return (paramLong1 & paramLong2) != 0L;
  }
  
  protected static long setBit(long paramLong1, long paramLong2, boolean paramBoolean)
  {
    return paramBoolean ? paramLong1 | paramLong2 : paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFF);
  }
  
  static void printArrayTo(PrintStream paramPrintStream, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++)
    {
      if (j % 10 == 0) {
        paramPrintStream.println();
      } else {
        paramPrintStream.print(" ");
      }
      paramPrintStream.print(paramArrayOfInt[(paramInt1 + j)]);
    }
    paramPrintStream.println();
  }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2)
  {
    printArrayTo(paramPrintStream, paramArrayOfEntry, paramInt1, paramInt2, false);
  }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++)
    {
      ConstantPool.Entry localEntry = paramArrayOfEntry[(paramInt1 + j)];
      paramPrintStream.print(paramInt1 + j);
      paramPrintStream.print("=");
      if (paramBoolean)
      {
        paramPrintStream.print(localEntry.tag);
        paramPrintStream.print(":");
      }
      String str1 = localEntry.stringValue();
      localStringBuffer.setLength(0);
      for (int k = 0; k < str1.length(); k++)
      {
        char c = str1.charAt(k);
        if ((c >= ' ') && (c <= '~') && (c != '\\'))
        {
          localStringBuffer.append(c);
        }
        else if (c == '\\')
        {
          localStringBuffer.append("\\\\");
        }
        else if (c == '\n')
        {
          localStringBuffer.append("\\n");
        }
        else if (c == '\t')
        {
          localStringBuffer.append("\\t");
        }
        else if (c == '\r')
        {
          localStringBuffer.append("\\r");
        }
        else
        {
          String str2 = "000" + Integer.toHexString(c);
          localStringBuffer.append("\\u").append(str2.substring(str2.length() - 4));
        }
      }
      paramPrintStream.println(localStringBuffer);
    }
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject, int paramInt)
  {
    Class localClass = paramArrayOfObject.getClass().getComponentType();
    Object[] arrayOfObject = (Object[])Array.newInstance(localClass, paramInt);
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, Math.min(paramArrayOfObject.length, paramInt));
    return arrayOfObject;
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject)
  {
    return realloc(paramArrayOfObject, Math.max(10, paramArrayOfObject.length * 2));
  }
  
  protected static int[] realloc(int[] paramArrayOfInt, int paramInt)
  {
    if (paramInt == 0) {
      return Constants.noInts;
    }
    if (paramArrayOfInt == null) {
      return new int[paramInt];
    }
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, Math.min(paramArrayOfInt.length, paramInt));
    return arrayOfInt;
  }
  
  protected static int[] realloc(int[] paramArrayOfInt)
  {
    return realloc(paramArrayOfInt, Math.max(10, paramArrayOfInt.length * 2));
  }
  
  protected static byte[] realloc(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt == 0) {
      return Constants.noBytes;
    }
    if (paramArrayOfByte == null) {
      return new byte[paramInt];
    }
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt));
    return arrayOfByte;
  }
  
  protected static byte[] realloc(byte[] paramArrayOfByte)
  {
    return realloc(paramArrayOfByte, Math.max(10, paramArrayOfByte.length * 2));
  }
  
  static
  {
    BYTE1 = Coding.of(1, 256);
    CHAR3 = Coding.of(3, 128);
    BCI5 = Coding.of(5, 4);
    BRANCH5 = Coding.of(5, 4, 2);
    UNSIGNED5 = Coding.of(5, 64);
    UDELTA5 = UNSIGNED5.getDeltaCoding();
    SIGNED5 = Coding.of(5, 64, 1);
    DELTA5 = SIGNED5.getDeltaCoding();
    MDELTA5 = Coding.of(5, 64, 2).getDeltaCoding();
    basicCodings = new Coding[] { null, Coding.of(1, 256, 0), Coding.of(1, 256, 1), Coding.of(1, 256, 0).getDeltaCoding(), Coding.of(1, 256, 1).getDeltaCoding(), Coding.of(2, 256, 0), Coding.of(2, 256, 1), Coding.of(2, 256, 0).getDeltaCoding(), Coding.of(2, 256, 1).getDeltaCoding(), Coding.of(3, 256, 0), Coding.of(3, 256, 1), Coding.of(3, 256, 0).getDeltaCoding(), Coding.of(3, 256, 1).getDeltaCoding(), Coding.of(4, 256, 0), Coding.of(4, 256, 1), Coding.of(4, 256, 0).getDeltaCoding(), Coding.of(4, 256, 1).getDeltaCoding(), Coding.of(5, 4, 0), Coding.of(5, 4, 1), Coding.of(5, 4, 2), Coding.of(5, 16, 0), Coding.of(5, 16, 1), Coding.of(5, 16, 2), Coding.of(5, 32, 0), Coding.of(5, 32, 1), Coding.of(5, 32, 2), Coding.of(5, 64, 0), Coding.of(5, 64, 1), Coding.of(5, 64, 2), Coding.of(5, 128, 0), Coding.of(5, 128, 1), Coding.of(5, 128, 2), Coding.of(5, 4, 0).getDeltaCoding(), Coding.of(5, 4, 1).getDeltaCoding(), Coding.of(5, 4, 2).getDeltaCoding(), Coding.of(5, 16, 0).getDeltaCoding(), Coding.of(5, 16, 1).getDeltaCoding(), Coding.of(5, 16, 2).getDeltaCoding(), Coding.of(5, 32, 0).getDeltaCoding(), Coding.of(5, 32, 1).getDeltaCoding(), Coding.of(5, 32, 2).getDeltaCoding(), Coding.of(5, 64, 0).getDeltaCoding(), Coding.of(5, 64, 1).getDeltaCoding(), Coding.of(5, 64, 2).getDeltaCoding(), Coding.of(5, 128, 0).getDeltaCoding(), Coding.of(5, 128, 1).getDeltaCoding(), Coding.of(5, 128, 2).getDeltaCoding(), Coding.of(2, 192, 0), Coding.of(2, 224, 0), Coding.of(2, 240, 0), Coding.of(2, 248, 0), Coding.of(2, 252, 0), Coding.of(2, 8, 0).getDeltaCoding(), Coding.of(2, 8, 1).getDeltaCoding(), Coding.of(2, 16, 0).getDeltaCoding(), Coding.of(2, 16, 1).getDeltaCoding(), Coding.of(2, 32, 0).getDeltaCoding(), Coding.of(2, 32, 1).getDeltaCoding(), Coding.of(2, 64, 0).getDeltaCoding(), Coding.of(2, 64, 1).getDeltaCoding(), Coding.of(2, 128, 0).getDeltaCoding(), Coding.of(2, 128, 1).getDeltaCoding(), Coding.of(2, 192, 0).getDeltaCoding(), Coding.of(2, 192, 1).getDeltaCoding(), Coding.of(2, 224, 0).getDeltaCoding(), Coding.of(2, 224, 1).getDeltaCoding(), Coding.of(2, 240, 0).getDeltaCoding(), Coding.of(2, 240, 1).getDeltaCoding(), Coding.of(2, 248, 0).getDeltaCoding(), Coding.of(2, 248, 1).getDeltaCoding(), Coding.of(3, 192, 0), Coding.of(3, 224, 0), Coding.of(3, 240, 0), Coding.of(3, 248, 0), Coding.of(3, 252, 0), Coding.of(3, 8, 0).getDeltaCoding(), Coding.of(3, 8, 1).getDeltaCoding(), Coding.of(3, 16, 0).getDeltaCoding(), Coding.of(3, 16, 1).getDeltaCoding(), Coding.of(3, 32, 0).getDeltaCoding(), Coding.of(3, 32, 1).getDeltaCoding(), Coding.of(3, 64, 0).getDeltaCoding(), Coding.of(3, 64, 1).getDeltaCoding(), Coding.of(3, 128, 0).getDeltaCoding(), Coding.of(3, 128, 1).getDeltaCoding(), Coding.of(3, 192, 0).getDeltaCoding(), Coding.of(3, 192, 1).getDeltaCoding(), Coding.of(3, 224, 0).getDeltaCoding(), Coding.of(3, 224, 1).getDeltaCoding(), Coding.of(3, 240, 0).getDeltaCoding(), Coding.of(3, 240, 1).getDeltaCoding(), Coding.of(3, 248, 0).getDeltaCoding(), Coding.of(3, 248, 1).getDeltaCoding(), Coding.of(4, 192, 0), Coding.of(4, 224, 0), Coding.of(4, 240, 0), Coding.of(4, 248, 0), Coding.of(4, 252, 0), Coding.of(4, 8, 0).getDeltaCoding(), Coding.of(4, 8, 1).getDeltaCoding(), Coding.of(4, 16, 0).getDeltaCoding(), Coding.of(4, 16, 1).getDeltaCoding(), Coding.of(4, 32, 0).getDeltaCoding(), Coding.of(4, 32, 1).getDeltaCoding(), Coding.of(4, 64, 0).getDeltaCoding(), Coding.of(4, 64, 1).getDeltaCoding(), Coding.of(4, 128, 0).getDeltaCoding(), Coding.of(4, 128, 1).getDeltaCoding(), Coding.of(4, 192, 0).getDeltaCoding(), Coding.of(4, 192, 1).getDeltaCoding(), Coding.of(4, 224, 0).getDeltaCoding(), Coding.of(4, 224, 1).getDeltaCoding(), Coding.of(4, 240, 0).getDeltaCoding(), Coding.of(4, 240, 1).getDeltaCoding(), Coding.of(4, 248, 0).getDeltaCoding(), Coding.of(4, 248, 1).getDeltaCoding(), null };
    assert (basicCodings[0] == null);
    assert (basicCodings[1] != null);
    assert (basicCodings[115] != null);
    HashMap localHashMap = new HashMap();
    Coding localCoding;
    for (int j = 0; j < basicCodings.length; j++)
    {
      localCoding = basicCodings[j];
      if (localCoding != null)
      {
        assert (j >= 1);
        assert (j <= 115);
        localHashMap.put(localCoding, Integer.valueOf(j));
      }
    }
    basicCodingIndexes = localHashMap;
    defaultMetaCoding = new byte[] { 0 };
    noMetaCoding = new byte[0];
    int i = 0;
    assert ((i = 1) != 0);
    if (i != 0) {
      for (j = 0; j < basicCodings.length; j++)
      {
        localCoding = basicCodings[j];
        if ((localCoding != null) && (localCoding.B() != 1) && (localCoding.L() != 0)) {
          for (int k = 0; k <= 255; k++) {
            encodeEscapeValue(k, localCoding);
          }
        }
      }
    }
  }
  
  abstract class Band
  {
    private int phase = 0;
    private final String name;
    private int valuesExpected;
    protected long outputSize = -1L;
    public final Coding regularCoding;
    public final int seqForDebug;
    public int elementCountForDebug;
    protected int lengthForDebug = -1;
    
    protected Band(String paramString, Coding paramCoding)
    {
      this.name = paramString;
      this.regularCoding = paramCoding;
      this.seqForDebug = (++BandStructure.nextSeqForDebug);
      if (BandStructure.this.verbose > 2) {
        Utils.log.fine("Band " + this.seqForDebug + " is " + paramString);
      }
    }
    
    public Band init()
    {
      if (BandStructure.this.isReader) {
        readyToExpect();
      } else {
        readyToCollect();
      }
      return this;
    }
    
    boolean isReader()
    {
      return BandStructure.this.isReader;
    }
    
    int phase()
    {
      return this.phase;
    }
    
    String name()
    {
      return this.name;
    }
    
    public abstract int capacity();
    
    protected abstract void setCapacity(int paramInt);
    
    public abstract int length();
    
    protected abstract int valuesRemainingForDebug();
    
    public final int valuesExpected()
    {
      return this.valuesExpected;
    }
    
    public final void writeTo(OutputStream paramOutputStream)
      throws IOException
    {
      assert (BandStructure.this.assertReadyToWriteTo(this, paramOutputStream));
      setPhase(5);
      writeDataTo(paramOutputStream);
      doneWriting();
    }
    
    abstract void chooseBandCodings()
      throws IOException;
    
    public final long outputSize()
    {
      if (this.outputSize >= 0L)
      {
        long l = this.outputSize;
        assert (l == computeOutputSize());
        return l;
      }
      return computeOutputSize();
    }
    
    protected abstract long computeOutputSize();
    
    protected abstract void writeDataTo(OutputStream paramOutputStream)
      throws IOException;
    
    void expectLength(int paramInt)
    {
      assert (BandStructure.assertPhase(this, 2));
      assert (this.valuesExpected == 0);
      assert (paramInt >= 0);
      this.valuesExpected = paramInt;
    }
    
    void expectMoreLength(int paramInt)
    {
      assert (BandStructure.assertPhase(this, 2));
      this.valuesExpected += paramInt;
    }
    
    private void readyToCollect()
    {
      setCapacity(1);
      setPhase(1);
    }
    
    protected void doneWriting()
    {
      assert (BandStructure.assertPhase(this, 5));
      setPhase(8);
    }
    
    private void readyToExpect()
    {
      setPhase(2);
    }
    
    public final void readFrom(InputStream paramInputStream)
      throws IOException
    {
      assert (BandStructure.this.assertReadyToReadFrom(this, paramInputStream));
      setCapacity(valuesExpected());
      setPhase(4);
      readDataFrom(paramInputStream);
      readyToDisburse();
    }
    
    protected abstract void readDataFrom(InputStream paramInputStream)
      throws IOException;
    
    protected void readyToDisburse()
    {
      if (BandStructure.this.verbose > 1) {
        Utils.log.fine("readyToDisburse " + this);
      }
      setPhase(6);
    }
    
    public void doneDisbursing()
    {
      assert (BandStructure.assertPhase(this, 6));
      setPhase(8);
    }
    
    public final void doneWithUnusedBand()
    {
      if (BandStructure.this.isReader)
      {
        assert (BandStructure.assertPhase(this, 2));
        assert (valuesExpected() == 0);
        setPhase(4);
        setPhase(6);
        setPhase(8);
      }
      else
      {
        setPhase(3);
      }
    }
    
    protected void setPhase(int paramInt)
    {
      assert (BandStructure.assertPhaseChangeOK(this, this.phase, paramInt));
      this.phase = paramInt;
    }
    
    public String toString()
    {
      int i = this.lengthForDebug != -1 ? this.lengthForDebug : length();
      String str = this.name;
      if (i != 0) {
        str = str + "[" + i + "]";
      }
      if (this.elementCountForDebug != 0) {
        str = str + "(" + this.elementCountForDebug + ")";
      }
      return str;
    }
  }
  
  class ByteBand
    extends BandStructure.Band
  {
    private ByteArrayOutputStream bytes;
    private ByteArrayOutputStream bytesForDump;
    private InputStream in;
    
    public ByteBand(String paramString)
    {
      super(paramString, BandStructure.BYTE1);
    }
    
    public int capacity()
    {
      return this.bytes == null ? -1 : Integer.MAX_VALUE;
    }
    
    protected void setCapacity(int paramInt)
    {
      assert (this.bytes == null);
      this.bytes = new ByteArrayOutputStream(paramInt);
    }
    
    public void destroy()
    {
      this.lengthForDebug = length();
      this.bytes = null;
    }
    
    public int length()
    {
      return this.bytes == null ? -1 : this.bytes.size();
    }
    
    public void reset()
    {
      this.bytes.reset();
    }
    
    protected int valuesRemainingForDebug()
    {
      return this.bytes == null ? -1 : ((ByteArrayInputStream)this.in).available();
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      assert (BandStructure.decodeEscapeValue(this.regularCoding.min(), this.regularCoding) < 0);
      assert (BandStructure.decodeEscapeValue(this.regularCoding.max(), this.regularCoding) < 0);
    }
    
    protected long computeOutputSize()
    {
      return this.bytes.size();
    }
    
    public void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      if (length() == 0) {
        return;
      }
      this.bytes.writeTo(paramOutputStream);
      if (BandStructure.this.optDumpBands) {
        dumpBand();
      }
      destroy();
    }
    
    private void dumpBand()
      throws IOException
    {
      assert (BandStructure.this.optDumpBands);
      OutputStream localOutputStream = BandStructure.getDumpStream(this, ".bnd");
      Object localObject1 = null;
      try
      {
        if (this.bytesForDump != null) {
          this.bytesForDump.writeTo(localOutputStream);
        } else {
          this.bytes.writeTo(localOutputStream);
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localOutputStream != null) {
          if (localObject1 != null) {
            try
            {
              localOutputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              localObject1.addSuppressed(localThrowable3);
            }
          } else {
            localOutputStream.close();
          }
        }
      }
    }
    
    public void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      int i = valuesExpected();
      if (i == 0) {
        return;
      }
      if (BandStructure.this.verbose > 1)
      {
        this.lengthForDebug = i;
        Utils.log.fine("Reading band " + this);
        this.lengthForDebug = -1;
      }
      byte[] arrayOfByte = new byte[Math.min(i, 16384)];
      while (i > 0)
      {
        int j = paramInputStream.read(arrayOfByte, 0, Math.min(i, arrayOfByte.length));
        if (j < 0) {
          throw new EOFException();
        }
        this.bytes.write(arrayOfByte, 0, j);
        i -= j;
      }
      if (BandStructure.this.optDumpBands) {
        dumpBand();
      }
    }
    
    public void readyToDisburse()
    {
      this.in = new ByteArrayInputStream(this.bytes.toByteArray());
      super.readyToDisburse();
    }
    
    public void doneDisbursing()
    {
      super.doneDisbursing();
      if ((BandStructure.this.optDumpBands) && (this.bytesForDump != null) && (this.bytesForDump.size() > 0)) {
        try
        {
          dumpBand();
        }
        catch (IOException localIOException)
        {
          throw new RuntimeException(localIOException);
        }
      }
      this.in = null;
      this.bytes = null;
      this.bytesForDump = null;
    }
    
    public void setInputStreamFrom(InputStream paramInputStream)
      throws IOException
    {
      assert (this.bytes == null);
      assert (BandStructure.this.assertReadyToReadFrom(this, paramInputStream));
      setPhase(4);
      this.in = paramInputStream;
      if (BandStructure.this.optDumpBands)
      {
        this.bytesForDump = new ByteArrayOutputStream();
        this.in = new FilterInputStream(paramInputStream)
        {
          public int read()
            throws IOException
          {
            int i = this.in.read();
            if (i >= 0) {
              BandStructure.ByteBand.this.bytesForDump.write(i);
            }
            return i;
          }
          
          public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            int i = this.in.read(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
            if (i >= 0) {
              BandStructure.ByteBand.this.bytesForDump.write(paramAnonymousArrayOfByte, paramAnonymousInt1, i);
            }
            return i;
          }
        };
      }
      super.readyToDisburse();
    }
    
    public OutputStream collectorStream()
    {
      assert (phase() == 1);
      assert (this.bytes != null);
      return this.bytes;
    }
    
    public InputStream getInputStream()
    {
      assert (phase() == 6);
      assert (this.in != null);
      return this.in;
    }
    
    public int getByte()
      throws IOException
    {
      int i = getInputStream().read();
      if (i < 0) {
        throw new EOFException();
      }
      return i;
    }
    
    public void putByte(int paramInt)
      throws IOException
    {
      assert (paramInt == (paramInt & 0xFF));
      collectorStream().write(paramInt);
    }
    
    public String toString()
    {
      return "byte " + super.toString();
    }
  }
  
  private static class ByteCounter
    extends FilterOutputStream
  {
    private long count;
    
    public ByteCounter(OutputStream paramOutputStream)
    {
      super();
    }
    
    public long getCount()
    {
      return this.count;
    }
    
    public void setCount(long paramLong)
    {
      this.count = paramLong;
    }
    
    public void write(int paramInt)
      throws IOException
    {
      this.count += 1L;
      if (this.out != null) {
        this.out.write(paramInt);
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.count += paramInt2;
      if (this.out != null) {
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
    
    public String toString()
    {
      return String.valueOf(getCount());
    }
  }
  
  class CPRefBand
    extends BandStructure.ValueBand
  {
    ConstantPool.Index index;
    boolean nullOK;
    
    public CPRefBand(String paramString, Coding paramCoding, byte paramByte, boolean paramBoolean)
    {
      super(paramString, paramCoding);
      this.nullOK = paramBoolean;
      if (paramByte != 0) {
        BandStructure.this.setBandIndex(this, paramByte);
      }
    }
    
    public CPRefBand(String paramString, Coding paramCoding, byte paramByte)
    {
      this(paramString, paramCoding, paramByte, false);
    }
    
    public CPRefBand(String paramString, Coding paramCoding, Object paramObject)
    {
      this(paramString, paramCoding, (byte)0, false);
    }
    
    public void setIndex(ConstantPool.Index paramIndex)
    {
      this.index = paramIndex;
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      super.readDataFrom(paramInputStream);
      assert (BandStructure.this.assertValidCPRefs(this));
    }
    
    public void putRef(ConstantPool.Entry paramEntry)
    {
      addValue(encodeRefOrNull(paramEntry, this.index));
    }
    
    public void putRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
    {
      assert (this.index == null);
      addValue(encodeRefOrNull(paramEntry, paramIndex));
    }
    
    public void putRef(ConstantPool.Entry paramEntry, byte paramByte)
    {
      putRef(paramEntry, BandStructure.this.getCPIndex(paramByte));
    }
    
    public ConstantPool.Entry getRef()
    {
      if (this.index == null) {
        Utils.log.warning("No index for " + this);
      }
      assert (this.index != null);
      return decodeRefOrNull(getValue(), this.index);
    }
    
    public ConstantPool.Entry getRef(ConstantPool.Index paramIndex)
    {
      assert (this.index == null);
      return decodeRefOrNull(getValue(), paramIndex);
    }
    
    public ConstantPool.Entry getRef(byte paramByte)
    {
      return getRef(BandStructure.this.getCPIndex(paramByte));
    }
    
    private int encodeRefOrNull(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex)
    {
      int i;
      if (paramEntry == null) {
        i = -1;
      } else {
        i = BandStructure.this.encodeRef(paramEntry, paramIndex);
      }
      return (this.nullOK ? 1 : 0) + i;
    }
    
    private ConstantPool.Entry decodeRefOrNull(int paramInt, ConstantPool.Index paramIndex)
    {
      int i = paramInt - (this.nullOK ? 1 : 0);
      if (i == -1) {
        return null;
      }
      return BandStructure.this.decodeRef(i, paramIndex);
    }
  }
  
  class IntBand
    extends BandStructure.ValueBand
  {
    public IntBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public void putInt(int paramInt)
    {
      assert (phase() == 1);
      addValue(paramInt);
    }
    
    public int getInt()
    {
      return getValue();
    }
    
    public int getIntTotal()
    {
      assert (phase() == 6);
      assert (valuesRemainingForDebug() == length());
      int i = 0;
      for (int j = length(); j > 0; j--) {
        i += getInt();
      }
      resetForSecondPass();
      return i;
    }
    
    public int getIntCount(int paramInt)
    {
      assert (phase() == 6);
      assert (valuesRemainingForDebug() == length());
      int i = 0;
      for (int j = length(); j > 0; j--) {
        if (getInt() == paramInt) {
          i++;
        }
      }
      resetForSecondPass();
      return i;
    }
  }
  
  class MultiBand
    extends BandStructure.Band
  {
    BandStructure.Band[] bands = new BandStructure.Band[10];
    int bandCount = 0;
    private int cap = -1;
    
    MultiBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public BandStructure.Band init()
    {
      super.init();
      setCapacity(0);
      if (phase() == 2)
      {
        setPhase(4);
        setPhase(6);
      }
      return this;
    }
    
    int size()
    {
      return this.bandCount;
    }
    
    BandStructure.Band get(int paramInt)
    {
      assert (paramInt < this.bandCount);
      return this.bands[paramInt];
    }
    
    BandStructure.Band[] toArray()
    {
      return (BandStructure.Band[])BandStructure.realloc(this.bands, this.bandCount);
    }
    
    void add(BandStructure.Band paramBand)
    {
      assert ((this.bandCount == 0) || (BandStructure.this.notePrevForAssert(paramBand, this.bands[(this.bandCount - 1)])));
      if (this.bandCount == this.bands.length) {
        this.bands = ((BandStructure.Band[])BandStructure.realloc(this.bands));
      }
      this.bands[(this.bandCount++)] = paramBand;
    }
    
    BandStructure.ByteBand newByteBand(String paramString)
    {
      BandStructure.ByteBand localByteBand = new BandStructure.ByteBand(BandStructure.this, paramString);
      localByteBand.init();
      add(localByteBand);
      return localByteBand;
    }
    
    BandStructure.IntBand newIntBand(String paramString)
    {
      BandStructure.IntBand localIntBand = new BandStructure.IntBand(BandStructure.this, paramString, this.regularCoding);
      localIntBand.init();
      add(localIntBand);
      return localIntBand;
    }
    
    BandStructure.IntBand newIntBand(String paramString, Coding paramCoding)
    {
      BandStructure.IntBand localIntBand = new BandStructure.IntBand(BandStructure.this, paramString, paramCoding);
      localIntBand.init();
      add(localIntBand);
      return localIntBand;
    }
    
    MultiBand newMultiBand(String paramString, Coding paramCoding)
    {
      MultiBand localMultiBand = new MultiBand(BandStructure.this, paramString, paramCoding);
      localMultiBand.init();
      add(localMultiBand);
      return localMultiBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, byte paramByte)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, this.regularCoding, paramByte);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, Coding paramCoding, byte paramByte)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, paramCoding, paramByte);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String paramString, Coding paramCoding, byte paramByte, boolean paramBoolean)
    {
      BandStructure.CPRefBand localCPRefBand = new BandStructure.CPRefBand(BandStructure.this, paramString, paramCoding, paramByte, paramBoolean);
      localCPRefBand.init();
      add(localCPRefBand);
      return localCPRefBand;
    }
    
    int bandCount()
    {
      return this.bandCount;
    }
    
    public int capacity()
    {
      return this.cap;
    }
    
    public void setCapacity(int paramInt)
    {
      this.cap = paramInt;
    }
    
    public int length()
    {
      return 0;
    }
    
    public int valuesRemainingForDebug()
    {
      return 0;
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      for (int i = 0; i < this.bandCount; i++)
      {
        BandStructure.Band localBand = this.bands[i];
        localBand.chooseBandCodings();
      }
    }
    
    protected long computeOutputSize()
    {
      long l1 = 0L;
      for (int i = 0; i < this.bandCount; i++)
      {
        BandStructure.Band localBand = this.bands[i];
        long l2 = localBand.outputSize();
        assert (l2 >= 0L) : localBand;
        l1 += l2;
      }
      return l1;
    }
    
    protected void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      long l1 = 0L;
      if (BandStructure.this.outputCounter != null) {
        l1 = BandStructure.this.outputCounter.getCount();
      }
      for (int i = 0; i < this.bandCount; i++)
      {
        BandStructure.Band localBand = this.bands[i];
        localBand.writeTo(paramOutputStream);
        if (BandStructure.this.outputCounter != null)
        {
          long l2 = BandStructure.this.outputCounter.getCount();
          long l3 = l2 - l1;
          l1 = l2;
          if (((BandStructure.this.verbose > 0) && (l3 > 0L)) || (BandStructure.this.verbose > 1)) {
            Utils.log.info("  ...wrote " + l3 + " bytes from " + localBand);
          }
        }
      }
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
      for (int i = 0; i < this.bandCount; i++)
      {
        BandStructure.Band localBand = this.bands[i];
        localBand.readFrom(paramInputStream);
        if (((BandStructure.this.verbose > 0) && (localBand.length() > 0)) || (BandStructure.this.verbose > 1)) {
          Utils.log.info("  ...read " + localBand);
        }
      }
    }
    
    public String toString()
    {
      return "{" + bandCount() + " bands: " + super.toString() + "}";
    }
  }
  
  class ValueBand
    extends BandStructure.Band
  {
    private int[] values;
    private int length;
    private int valuesDisbursed;
    private CodingMethod bandCoding;
    private byte[] metaCoding;
    
    protected ValueBand(String paramString, Coding paramCoding)
    {
      super(paramString, paramCoding);
    }
    
    public int capacity()
    {
      return this.values == null ? -1 : this.values.length;
    }
    
    protected void setCapacity(int paramInt)
    {
      assert (this.length <= paramInt);
      if (paramInt == -1)
      {
        this.values = null;
        return;
      }
      this.values = BandStructure.realloc(this.values, paramInt);
    }
    
    public int length()
    {
      return this.length;
    }
    
    protected int valuesRemainingForDebug()
    {
      return this.length - this.valuesDisbursed;
    }
    
    protected int valueAtForDebug(int paramInt)
    {
      return this.values[paramInt];
    }
    
    void patchValue(int paramInt1, int paramInt2)
    {
      assert (this == BandStructure.this.archive_header_S);
      assert ((paramInt1 == 0) || (paramInt1 == 1));
      assert (paramInt1 < this.length);
      this.values[paramInt1] = paramInt2;
      this.outputSize = -1L;
    }
    
    protected void initializeValues(int[] paramArrayOfInt)
    {
      assert (BandStructure.assertCanChangeLength(this));
      assert (this.length == 0);
      this.values = paramArrayOfInt;
      this.length = paramArrayOfInt.length;
    }
    
    protected void addValue(int paramInt)
    {
      assert (BandStructure.assertCanChangeLength(this));
      if (this.length == this.values.length) {
        setCapacity(this.length < 1000 ? this.length * 10 : this.length * 2);
      }
      this.values[(this.length++)] = paramInt;
    }
    
    private boolean canVaryCoding()
    {
      if (!BandStructure.this.optVaryCodings) {
        return false;
      }
      if (this.length == 0) {
        return false;
      }
      if (this == BandStructure.this.archive_header_0) {
        return false;
      }
      if (this == BandStructure.this.archive_header_S) {
        return false;
      }
      if (this == BandStructure.this.archive_header_1) {
        return false;
      }
      return (this.regularCoding.min() <= 65280) || (this.regularCoding.max() >= 256);
    }
    
    private boolean shouldVaryCoding()
    {
      assert (canVaryCoding());
      return (BandStructure.this.effort >= 9) || (this.length >= 100);
    }
    
    protected void chooseBandCodings()
      throws IOException
    {
      boolean bool = canVaryCoding();
      Object localObject;
      if ((!bool) || (!shouldVaryCoding()))
      {
        if (this.regularCoding.canRepresent(this.values, 0, this.length))
        {
          this.bandCoding = this.regularCoding;
        }
        else
        {
          assert (bool);
          if (BandStructure.this.verbose > 1) {
            Utils.log.fine("regular coding fails in band " + name());
          }
          this.bandCoding = BandStructure.UNSIGNED5;
        }
        this.outputSize = -1L;
      }
      else
      {
        localObject = new int[] { 0, 0 };
        this.bandCoding = BandStructure.this.chooseCoding(this.values, 0, this.length, this.regularCoding, name(), (int[])localObject);
        this.outputSize = localObject[0];
        if (this.outputSize == 0L) {
          this.outputSize = -1L;
        }
      }
      if (this.bandCoding != this.regularCoding)
      {
        this.metaCoding = this.bandCoding.getMetaCoding(this.regularCoding);
        if (BandStructure.this.verbose > 1) {
          Utils.log.fine("alternate coding " + this + " " + this.bandCoding);
        }
      }
      else if ((bool) && (BandStructure.decodeEscapeValue(this.values[0], this.regularCoding) >= 0))
      {
        this.metaCoding = BandStructure.defaultMetaCoding;
      }
      else
      {
        this.metaCoding = BandStructure.noMetaCoding;
      }
      if ((this.metaCoding.length > 0) && ((BandStructure.this.verbose > 2) || ((BandStructure.this.verbose > 1) && (this.metaCoding.length > 1))))
      {
        localObject = new StringBuffer();
        for (int j = 0; j < this.metaCoding.length; j++)
        {
          if (j == 1) {
            ((StringBuffer)localObject).append(" /");
          }
          ((StringBuffer)localObject).append(" ").append(this.metaCoding[j] & 0xFF);
        }
        Utils.log.fine("   meta-coding " + localObject);
      }
      assert ((this.outputSize < 0L) || (!(this.bandCoding instanceof Coding)) || (this.outputSize == ((Coding)this.bandCoding).getLength(this.values, 0, this.length))) : (this.bandCoding + " : " + this.outputSize + " != " + ((Coding)this.bandCoding).getLength(this.values, 0, this.length) + " ?= " + BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length));
      if (this.metaCoding.length > 0)
      {
        if (this.outputSize >= 0L) {
          this.outputSize += computeEscapeSize();
        }
        for (int i = 1; i < this.metaCoding.length; i++) {
          BandStructure.this.band_headers.putByte(this.metaCoding[i] & 0xFF);
        }
      }
    }
    
    protected long computeOutputSize()
    {
      this.outputSize = BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);
      assert (this.outputSize < 2147483647L);
      this.outputSize += computeEscapeSize();
      return this.outputSize;
    }
    
    protected int computeEscapeSize()
    {
      if (this.metaCoding.length == 0) {
        return 0;
      }
      int i = this.metaCoding[0] & 0xFF;
      int j = BandStructure.encodeEscapeValue(i, this.regularCoding);
      return this.regularCoding.setD(0).getLength(j);
    }
    
    protected void writeDataTo(OutputStream paramOutputStream)
      throws IOException
    {
      if (this.length == 0) {
        return;
      }
      long l = 0L;
      if (paramOutputStream == BandStructure.this.outputCounter) {
        l = BandStructure.this.outputCounter.getCount();
      }
      if (this.metaCoding.length > 0)
      {
        int i = this.metaCoding[0] & 0xFF;
        int j = BandStructure.encodeEscapeValue(i, this.regularCoding);
        this.regularCoding.setD(0).writeTo(paramOutputStream, j);
      }
      this.bandCoding.writeArrayTo(paramOutputStream, this.values, 0, this.length);
      if ((paramOutputStream == BandStructure.this.outputCounter) && (!$assertionsDisabled) && (this.outputSize != BandStructure.this.outputCounter.getCount() - l)) {
        throw new AssertionError(this.outputSize + " != " + BandStructure.this.outputCounter.getCount() + "-" + l);
      }
      if (BandStructure.this.optDumpBands) {
        dumpBand();
      }
    }
    
    protected void readDataFrom(InputStream paramInputStream)
      throws IOException
    {
      this.length = valuesExpected();
      if (this.length == 0) {
        return;
      }
      if (BandStructure.this.verbose > 1) {
        Utils.log.fine("Reading band " + this);
      }
      if (!canVaryCoding())
      {
        this.bandCoding = this.regularCoding;
        this.metaCoding = BandStructure.noMetaCoding;
      }
      else
      {
        assert (paramInputStream.markSupported());
        paramInputStream.mark(5);
        int i = this.regularCoding.setD(0).readFrom(paramInputStream);
        int j = BandStructure.decodeEscapeValue(i, this.regularCoding);
        if (j < 0)
        {
          paramInputStream.reset();
          this.bandCoding = this.regularCoding;
          this.metaCoding = BandStructure.noMetaCoding;
        }
        else if (j == 0)
        {
          this.bandCoding = this.regularCoding;
          this.metaCoding = BandStructure.defaultMetaCoding;
        }
        else
        {
          if (BandStructure.this.verbose > 2) {
            Utils.log.fine("found X=" + i + " => XB=" + j);
          }
          this.bandCoding = BandStructure.this.getBandHeader(j, this.regularCoding);
          int k = BandStructure.this.bandHeaderBytePos0;
          int m = BandStructure.this.bandHeaderBytePos;
          this.metaCoding = new byte[m - k];
          System.arraycopy(BandStructure.this.bandHeaderBytes, k, this.metaCoding, 0, this.metaCoding.length);
        }
      }
      if ((this.bandCoding != this.regularCoding) && (BandStructure.this.verbose > 1)) {
        Utils.log.fine(name() + ": irregular coding " + this.bandCoding);
      }
      this.bandCoding.readArrayFrom(paramInputStream, this.values, 0, this.length);
      if (BandStructure.this.optDumpBands) {
        dumpBand();
      }
    }
    
    public void doneDisbursing()
    {
      super.doneDisbursing();
      this.values = null;
    }
    
    private void dumpBand()
      throws IOException
    {
      assert (BandStructure.this.optDumpBands);
      Object localObject1 = new PrintStream(BandStructure.getDumpStream(this, ".txt"));
      Object localObject2 = null;
      try
      {
        String str = this.bandCoding == this.regularCoding ? "" : " irregular";
        ((PrintStream)localObject1).print("# length=" + this.length + " size=" + outputSize() + str + " coding=" + this.bandCoding);
        if (this.metaCoding != BandStructure.noMetaCoding)
        {
          StringBuffer localStringBuffer = new StringBuffer();
          for (int i = 0; i < this.metaCoding.length; i++)
          {
            if (i == 1) {
              localStringBuffer.append(" /");
            }
            localStringBuffer.append(" ").append(this.metaCoding[i] & 0xFF);
          }
          ((PrintStream)localObject1).print(" //header: " + localStringBuffer);
        }
        BandStructure.printArrayTo((PrintStream)localObject1, this.values, 0, this.length);
      }
      catch (Throwable localThrowable2)
      {
        localObject2 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((PrintStream)localObject1).close();
            }
            catch (Throwable localThrowable5)
            {
              localObject2.addSuppressed(localThrowable5);
            }
          } else {
            ((PrintStream)localObject1).close();
          }
        }
      }
      localObject1 = BandStructure.getDumpStream(this, ".bnd");
      localObject2 = null;
      try
      {
        this.bandCoding.writeArrayTo((OutputStream)localObject1, this.values, 0, this.length);
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = localThrowable4;
        throw localThrowable4;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((OutputStream)localObject1).close();
            }
            catch (Throwable localThrowable6)
            {
              localObject2.addSuppressed(localThrowable6);
            }
          } else {
            ((OutputStream)localObject1).close();
          }
        }
      }
    }
    
    protected int getValue()
    {
      assert (phase() == 6);
      if ((BandStructure.this.optDebugBands) && (this.length == 0) && (this.valuesDisbursed == this.length)) {
        return 0;
      }
      assert (this.valuesDisbursed <= this.length);
      return this.values[(this.valuesDisbursed++)];
    }
    
    public void resetForSecondPass()
    {
      assert (phase() == 6);
      assert (this.valuesDisbursed == length());
      this.valuesDisbursed = 0;
    }
  }
}
