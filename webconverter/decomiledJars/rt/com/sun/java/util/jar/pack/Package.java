package com.sun.java.util.jar.pack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class Package
{
  int verbose;
  final int magic = -889270259;
  int default_modtime;
  int default_options;
  Version defaultClassVersion;
  final Version minClassVersion;
  final Version maxClassVersion;
  final Version packageVersion;
  Version observedHighestClassVersion;
  ConstantPool.IndexGroup cp;
  public static final Attribute.Layout attrCodeEmpty;
  public static final Attribute.Layout attrBootstrapMethodsEmpty;
  public static final Attribute.Layout attrInnerClassesEmpty;
  public static final Attribute.Layout attrSourceFileSpecial;
  public static final Map<Attribute.Layout, Attribute> attrDefs;
  ArrayList<Class> classes;
  ArrayList<File> files;
  List<InnerClass> allInnerClasses;
  Map<ConstantPool.ClassEntry, InnerClass> allInnerClassesByThis;
  private static final int SLASH_MIN = 46;
  private static final int SLASH_MAX = 47;
  private static final int DOLLAR_MIN = 0;
  private static final int DOLLAR_MAX = 45;
  static final List<Object> noObjects = Arrays.asList(new Object[0]);
  static final List<Package.Class.Field> noFields = Arrays.asList(new Package.Class.Field[0]);
  static final List<Package.Class.Method> noMethods = Arrays.asList(new Package.Class.Method[0]);
  static final List<InnerClass> noInnerClasses = Arrays.asList(new InnerClass[0]);
  
  public Package()
  {
    PropMap localPropMap = Utils.currentPropMap();
    if (localPropMap != null) {
      this.verbose = localPropMap.getInteger("com.sun.java.util.jar.pack.verbose");
    }
    this.magic = -889270259;
    this.default_modtime = 0;
    this.default_options = 0;
    this.defaultClassVersion = null;
    this.observedHighestClassVersion = null;
    this.cp = new ConstantPool.IndexGroup();
    this.classes = new ArrayList();
    this.files = new ArrayList();
    this.allInnerClasses = new ArrayList();
    this.minClassVersion = Constants.JAVA_MIN_CLASS_VERSION;
    this.maxClassVersion = Constants.JAVA_MAX_CLASS_VERSION;
    this.packageVersion = null;
  }
  
  public Package(Version paramVersion1, Version paramVersion2, Version paramVersion3)
  {
    PropMap localPropMap = Utils.currentPropMap();
    if (localPropMap != null) {
      this.verbose = localPropMap.getInteger("com.sun.java.util.jar.pack.verbose");
    }
    this.magic = -889270259;
    this.default_modtime = 0;
    this.default_options = 0;
    this.defaultClassVersion = null;
    this.observedHighestClassVersion = null;
    this.cp = new ConstantPool.IndexGroup();
    this.classes = new ArrayList();
    this.files = new ArrayList();
    this.allInnerClasses = new ArrayList();
    this.minClassVersion = (paramVersion1 == null ? Constants.JAVA_MIN_CLASS_VERSION : paramVersion1);
    this.maxClassVersion = (paramVersion2 == null ? Constants.JAVA_MAX_CLASS_VERSION : paramVersion2);
    this.packageVersion = paramVersion3;
  }
  
  public void reset()
  {
    this.cp = new ConstantPool.IndexGroup();
    this.classes.clear();
    this.files.clear();
    BandStructure.nextSeqForDebug = 0;
    this.observedHighestClassVersion = null;
  }
  
  Version getDefaultClassVersion()
  {
    return this.defaultClassVersion;
  }
  
  private void setHighestClassVersion()
  {
    if (this.observedHighestClassVersion != null) {
      return;
    }
    Object localObject = Constants.JAVA_MIN_CLASS_VERSION;
    Iterator localIterator = this.classes.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      Version localVersion = localClass.getVersion();
      if (((Version)localObject).lessThan(localVersion)) {
        localObject = localVersion;
      }
    }
    this.observedHighestClassVersion = ((Version)localObject);
  }
  
  Version getHighestClassVersion()
  {
    setHighestClassVersion();
    return this.observedHighestClassVersion;
  }
  
  public List<Class> getClasses()
  {
    return this.classes;
  }
  
  void addClass(Class paramClass)
  {
    assert (paramClass.getPackage() == this);
    boolean bool = this.classes.add(paramClass);
    assert (bool);
    if (paramClass.file == null) {
      paramClass.initFile(null);
    }
    addFile(paramClass.file);
  }
  
  public List<File> getFiles()
  {
    return this.files;
  }
  
  public List<File> getClassStubs()
  {
    ArrayList localArrayList = new ArrayList(this.classes.size());
    Iterator localIterator = this.classes.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      assert (localClass.file.isClassStub());
      localArrayList.add(localClass.file);
    }
    return localArrayList;
  }
  
  File newStub(String paramString)
  {
    File localFile = new File(paramString);
    localFile.options |= 0x2;
    localFile.prepend = null;
    localFile.append = null;
    return localFile;
  }
  
  private static String fixupFileName(String paramString)
  {
    String str = paramString.replace(File.separatorChar, '/');
    if (str.startsWith("/")) {
      throw new IllegalArgumentException("absolute file name " + str);
    }
    return str;
  }
  
  void addFile(File paramFile)
  {
    boolean bool = this.files.add(paramFile);
    assert (bool);
  }
  
  public List<InnerClass> getAllInnerClasses()
  {
    return this.allInnerClasses;
  }
  
  public void setAllInnerClasses(Collection<InnerClass> paramCollection)
  {
    assert (paramCollection != this.allInnerClasses);
    this.allInnerClasses.clear();
    this.allInnerClasses.addAll(paramCollection);
    this.allInnerClassesByThis = new HashMap(this.allInnerClasses.size());
    Iterator localIterator = this.allInnerClasses.iterator();
    while (localIterator.hasNext())
    {
      InnerClass localInnerClass = (InnerClass)localIterator.next();
      Object localObject = this.allInnerClassesByThis.put(localInnerClass.thisClass, localInnerClass);
      assert (localObject == null);
    }
  }
  
  public InnerClass getGlobalInnerClass(ConstantPool.Entry paramEntry)
  {
    assert ((paramEntry instanceof ConstantPool.ClassEntry));
    return (InnerClass)this.allInnerClassesByThis.get(paramEntry);
  }
  
  private static void visitInnerClassRefs(Collection<InnerClass> paramCollection, int paramInt, Collection<ConstantPool.Entry> paramCollection1)
  {
    if (paramCollection == null) {
      return;
    }
    if (paramInt == 0) {
      paramCollection1.add(getRefString("InnerClasses"));
    }
    if (paramCollection.size() > 0)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        InnerClass localInnerClass = (InnerClass)localIterator.next();
        localInnerClass.visitRefs(paramInt, paramCollection1);
      }
    }
  }
  
  static String[] parseInnerClassName(String paramString)
  {
    int k = paramString.length();
    int m = lastIndexOf(46, 47, paramString, paramString.length()) + 1;
    int j = lastIndexOf(0, 45, paramString, paramString.length());
    if (j < m) {
      return null;
    }
    String str2;
    String str3;
    int i;
    if (isDigitString(paramString, j + 1, k))
    {
      str2 = paramString.substring(j + 1, k);
      str3 = null;
      i = j;
    }
    else if (((i = lastIndexOf(0, 45, paramString, j - 1)) > m) && (isDigitString(paramString, i + 1, j)))
    {
      str2 = paramString.substring(i + 1, j);
      str3 = paramString.substring(j + 1, k).intern();
    }
    else
    {
      i = j;
      str2 = null;
      str3 = paramString.substring(j + 1, k).intern();
    }
    String str1;
    if (str2 == null) {
      str1 = paramString.substring(0, i).intern();
    } else {
      str1 = null;
    }
    return new String[] { str1, str2, str3 };
  }
  
  private static int lastIndexOf(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    int i = paramInt3;
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      int j = paramString.charAt(i);
      if ((j >= paramInt1) && (j <= paramInt2)) {
        return i;
      }
    }
    return -1;
  }
  
  private static boolean isDigitString(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {
      return false;
    }
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = paramString.charAt(i);
      if ((j < 48) || (j > 57)) {
        return false;
      }
    }
    return true;
  }
  
  static String getObviousSourceFile(String paramString)
  {
    String str1 = paramString;
    int i = lastIndexOf(46, 47, str1, str1.length()) + 1;
    str1 = str1.substring(i);
    int j = str1.length();
    for (;;)
    {
      int k = lastIndexOf(0, 45, str1, j - 1);
      if (k < 0) {
        break;
      }
      j = k;
      if (j == 0) {
        break;
      }
    }
    String str2 = str1.substring(0, j) + ".java";
    return str2;
  }
  
  static ConstantPool.Utf8Entry getRefString(String paramString)
  {
    return ConstantPool.getUtf8Entry(paramString);
  }
  
  static ConstantPool.LiteralEntry getRefLiteral(Comparable<?> paramComparable)
  {
    return ConstantPool.getLiteralEntry(paramComparable);
  }
  
  void stripAttributeKind(String paramString)
  {
    if (this.verbose > 0) {
      Utils.log.info("Stripping " + paramString.toLowerCase() + " data and attributes...");
    }
    switch (paramString)
    {
    case "Debug": 
      strip("SourceFile");
      strip("LineNumberTable");
      strip("LocalVariableTable");
      strip("LocalVariableTypeTable");
      break;
    case "Compile": 
      strip("Deprecated");
      strip("Synthetic");
      break;
    case "Exceptions": 
      strip("Exceptions");
      break;
    case "Constant": 
      stripConstantFields();
    }
  }
  
  public void trimToSize()
  {
    this.classes.trimToSize();
    Iterator localIterator = this.classes.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      localClass.trimToSize();
    }
    this.files.trimToSize();
  }
  
  public void strip(String paramString)
  {
    Iterator localIterator = this.classes.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      localClass.strip(paramString);
    }
  }
  
  public void stripConstantFields()
  {
    Iterator localIterator1 = this.classes.iterator();
    while (localIterator1.hasNext())
    {
      Class localClass = (Class)localIterator1.next();
      Iterator localIterator2 = localClass.fields.iterator();
      while (localIterator2.hasNext())
      {
        Package.Class.Field localField = (Package.Class.Field)localIterator2.next();
        if ((Modifier.isFinal(localField.flags)) && (Modifier.isStatic(localField.flags)) && (localField.getAttribute("ConstantValue") != null) && (!localField.getName().startsWith("serial")) && (this.verbose > 2))
        {
          Utils.log.fine(">> Strip " + this + " ConstantValue");
          localIterator2.remove();
        }
      }
    }
  }
  
  protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
  {
    Iterator localIterator = this.classes.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Class)localIterator.next();
      ((Class)localObject).visitRefs(paramInt, paramCollection);
    }
    if (paramInt != 0)
    {
      localIterator = this.files.iterator();
      while (localIterator.hasNext())
      {
        localObject = (File)localIterator.next();
        ((File)localObject).visitRefs(paramInt, paramCollection);
      }
      visitInnerClassRefs(this.allInnerClasses, paramInt, paramCollection);
    }
  }
  
  void reorderFiles(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean1) {
      Collections.sort(this.classes);
    }
    List localList = getClassStubs();
    Iterator localIterator = this.files.iterator();
    while (localIterator.hasNext())
    {
      File localFile = (File)localIterator.next();
      if ((localFile.isClassStub()) || ((paramBoolean2) && (localFile.isDirectory()))) {
        localIterator.remove();
      }
    }
    Collections.sort(this.files, new Comparator()
    {
      public int compare(Package.File paramAnonymousFile1, Package.File paramAnonymousFile2)
      {
        String str1 = paramAnonymousFile1.nameString;
        String str2 = paramAnonymousFile2.nameString;
        if (str1.equals(str2)) {
          return 0;
        }
        if ("META-INF/MANIFEST.MF".equals(str1)) {
          return -1;
        }
        if ("META-INF/MANIFEST.MF".equals(str2)) {
          return 1;
        }
        String str3 = str1.substring(1 + str1.lastIndexOf('/'));
        String str4 = str2.substring(1 + str2.lastIndexOf('/'));
        String str5 = str3.substring(1 + str3.lastIndexOf('.'));
        String str6 = str4.substring(1 + str4.lastIndexOf('.'));
        int i = str5.compareTo(str6);
        if (i != 0) {
          return i;
        }
        i = str1.compareTo(str2);
        return i;
      }
    });
    this.files.addAll(localList);
  }
  
  void trimStubs()
  {
    ListIterator localListIterator = this.files.listIterator(this.files.size());
    while (localListIterator.hasPrevious())
    {
      File localFile = (File)localListIterator.previous();
      if (!localFile.isTrivialClassStub())
      {
        if (this.verbose <= 1) {
          break;
        }
        Utils.log.fine("Keeping last non-trivial " + localFile);
        break;
      }
      if (this.verbose > 2) {
        Utils.log.fine("Removing trivial " + localFile);
      }
      localListIterator.remove();
    }
    if (this.verbose > 0) {
      Utils.log.info("Transmitting " + this.files.size() + " files, including per-file data for " + getClassStubs().size() + " classes out of " + this.classes.size());
    }
  }
  
  void buildGlobalConstantPool(Set<ConstantPool.Entry> paramSet)
  {
    if (this.verbose > 1) {
      Utils.log.fine("Checking for unused CP entries");
    }
    paramSet.add(getRefString(""));
    visitRefs(1, paramSet);
    ConstantPool.completeReferencesIn(paramSet, false);
    if (this.verbose > 1) {
      Utils.log.fine("Sorting CP entries");
    }
    ConstantPool.Index localIndex1 = ConstantPool.makeIndex("unsorted", paramSet);
    ConstantPool.Index[] arrayOfIndex = ConstantPool.partitionByTag(localIndex1);
    ConstantPool.Index localIndex3;
    for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; i++)
    {
      byte b1 = ConstantPool.TAGS_IN_ORDER[i];
      localIndex3 = arrayOfIndex[b1];
      if (localIndex3 != null)
      {
        ConstantPool.sort(localIndex3);
        this.cp.initIndexByTag(b1, localIndex3);
        arrayOfIndex[b1] = null;
      }
    }
    for (i = 0; i < arrayOfIndex.length; i++)
    {
      ConstantPool.Index localIndex2 = arrayOfIndex[i];
      assert (localIndex2 == null);
    }
    for (i = 0; i < ConstantPool.TAGS_IN_ORDER.length; i++)
    {
      byte b2 = ConstantPool.TAGS_IN_ORDER[i];
      localIndex3 = this.cp.getIndexByTag(b2);
      assert (localIndex3.assertIsSorted());
      if (this.verbose > 2) {
        Utils.log.fine(localIndex3.dumpString());
      }
    }
  }
  
  void ensureAllClassFiles()
  {
    HashSet localHashSet = new HashSet(this.files);
    Iterator localIterator = this.classes.iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      if (!localHashSet.contains(localClass.file)) {
        this.files.add(localClass.file);
      }
    }
  }
  
  static
  {
    HashMap localHashMap = new HashMap(3);
    attrCodeEmpty = Attribute.define(localHashMap, 2, "Code", "").layout();
    attrBootstrapMethodsEmpty = Attribute.define(localHashMap, 0, "BootstrapMethods", "").layout();
    attrInnerClassesEmpty = Attribute.define(localHashMap, 0, "InnerClasses", "").layout();
    attrSourceFileSpecial = Attribute.define(localHashMap, 0, "SourceFile", "RUNH").layout();
    attrDefs = Collections.unmodifiableMap(localHashMap);
    assert (lastIndexOf(0, 45, "x$$y$", 4) == 2);
    assert (lastIndexOf(46, 47, "x//y/", 4) == 2);
  }
  
  public final class Class
    extends Attribute.Holder
    implements Comparable<Class>
  {
    Package.File file;
    int magic;
    Package.Version version;
    ConstantPool.Entry[] cpMap;
    ConstantPool.ClassEntry thisClass;
    ConstantPool.ClassEntry superClass;
    ConstantPool.ClassEntry[] interfaces;
    ArrayList<Field> fields;
    ArrayList<Method> methods;
    ArrayList<Package.InnerClass> innerClasses;
    ArrayList<ConstantPool.BootstrapMethodEntry> bootstrapMethods;
    
    public Package getPackage()
    {
      return Package.this;
    }
    
    Class(int paramInt, ConstantPool.ClassEntry paramClassEntry1, ConstantPool.ClassEntry paramClassEntry2, ConstantPool.ClassEntry[] paramArrayOfClassEntry)
    {
      this.magic = -889275714;
      this.version = Package.this.defaultClassVersion;
      this.flags = paramInt;
      this.thisClass = paramClassEntry1;
      this.superClass = paramClassEntry2;
      this.interfaces = paramArrayOfClassEntry;
      boolean bool = Package.this.classes.add(this);
      assert (bool);
    }
    
    Class(String paramString)
    {
      initFile(Package.this.newStub(paramString));
    }
    
    List<Field> getFields()
    {
      return this.fields == null ? Package.noFields : this.fields;
    }
    
    List<Method> getMethods()
    {
      return this.methods == null ? Package.noMethods : this.methods;
    }
    
    public String getName()
    {
      return this.thisClass.stringValue();
    }
    
    Package.Version getVersion()
    {
      return this.version;
    }
    
    public int compareTo(Class paramClass)
    {
      String str1 = getName();
      String str2 = paramClass.getName();
      return str1.compareTo(str2);
    }
    
    String getObviousSourceFile()
    {
      return Package.getObviousSourceFile(getName());
    }
    
    private void transformSourceFile(boolean paramBoolean)
    {
      Attribute localAttribute1 = getAttribute(Package.attrSourceFileSpecial);
      if (localAttribute1 == null) {
        return;
      }
      String str = getObviousSourceFile();
      ArrayList localArrayList = new ArrayList(1);
      localAttribute1.visitRefs(this, 1, localArrayList);
      ConstantPool.Utf8Entry localUtf8Entry = (ConstantPool.Utf8Entry)localArrayList.get(0);
      Attribute localAttribute2 = localAttribute1;
      Object localObject1;
      if (localUtf8Entry == null)
      {
        if (paramBoolean)
        {
          localAttribute2 = Attribute.find(0, "SourceFile", "H");
          localAttribute2 = localAttribute2.addContent(new byte[2]);
        }
        else
        {
          localObject1 = new byte[2];
          localUtf8Entry = Package.getRefString(str);
          Object localObject2 = null;
          localObject2 = Fixups.addRefWithBytes(localObject2, (byte[])localObject1, localUtf8Entry);
          localAttribute2 = Package.attrSourceFileSpecial.addContent((byte[])localObject1, localObject2);
        }
      }
      else if (str.equals(localUtf8Entry.stringValue())) {
        if (paramBoolean) {
          localAttribute2 = Package.attrSourceFileSpecial.addContent(new byte[2]);
        } else if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      if (localAttribute2 != localAttribute1)
      {
        if (Package.this.verbose > 2) {
          Utils.log.fine("recoding obvious SourceFile=" + str);
        }
        localObject1 = new ArrayList(getAttributes());
        int i = ((List)localObject1).indexOf(localAttribute1);
        ((List)localObject1).set(i, localAttribute2);
        setAttributes((List)localObject1);
      }
    }
    
    void minimizeSourceFile()
    {
      transformSourceFile(true);
    }
    
    void expandSourceFile()
    {
      transformSourceFile(false);
    }
    
    protected ConstantPool.Entry[] getCPMap()
    {
      return this.cpMap;
    }
    
    protected void setCPMap(ConstantPool.Entry[] paramArrayOfEntry)
    {
      this.cpMap = paramArrayOfEntry;
    }
    
    boolean hasBootstrapMethods()
    {
      return (this.bootstrapMethods != null) && (!this.bootstrapMethods.isEmpty());
    }
    
    List<ConstantPool.BootstrapMethodEntry> getBootstrapMethods()
    {
      return this.bootstrapMethods;
    }
    
    ConstantPool.BootstrapMethodEntry[] getBootstrapMethodMap()
    {
      return hasBootstrapMethods() ? (ConstantPool.BootstrapMethodEntry[])this.bootstrapMethods.toArray(new ConstantPool.BootstrapMethodEntry[this.bootstrapMethods.size()]) : null;
    }
    
    void setBootstrapMethods(Collection<ConstantPool.BootstrapMethodEntry> paramCollection)
    {
      assert (this.bootstrapMethods == null);
      this.bootstrapMethods = new ArrayList(paramCollection);
    }
    
    boolean hasInnerClasses()
    {
      return this.innerClasses != null;
    }
    
    List<Package.InnerClass> getInnerClasses()
    {
      return this.innerClasses;
    }
    
    public void setInnerClasses(Collection<Package.InnerClass> paramCollection)
    {
      this.innerClasses = (paramCollection == null ? null : new ArrayList(paramCollection));
      Attribute localAttribute = getAttribute(Package.attrInnerClassesEmpty);
      if ((this.innerClasses != null) && (localAttribute == null)) {
        addAttribute(Package.attrInnerClassesEmpty.canonicalInstance());
      } else if ((this.innerClasses == null) && (localAttribute != null)) {
        removeAttribute(localAttribute);
      }
    }
    
    public List<Package.InnerClass> computeGloballyImpliedICs()
    {
      HashSet localHashSet = new HashSet();
      Object localObject1 = this.innerClasses;
      this.innerClasses = null;
      visitRefs(0, localHashSet);
      this.innerClasses = ((ArrayList)localObject1);
      ConstantPool.completeReferencesIn(localHashSet, true);
      localObject1 = new HashSet();
      Object localObject2 = localHashSet.iterator();
      Package.InnerClass localInnerClass;
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (ConstantPool.Entry)((Iterator)localObject2).next();
        if ((localObject3 instanceof ConstantPool.ClassEntry)) {
          while (localObject3 != null)
          {
            localInnerClass = Package.this.getGlobalInnerClass((ConstantPool.Entry)localObject3);
            if ((localInnerClass == null) || (!((Set)localObject1).add(localObject3))) {
              break;
            }
            localObject3 = localInnerClass.outerClass;
          }
        }
      }
      localObject2 = new ArrayList();
      Object localObject3 = Package.this.allInnerClasses.iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localInnerClass = (Package.InnerClass)((Iterator)localObject3).next();
        if ((((Set)localObject1).contains(localInnerClass.thisClass)) || (localInnerClass.outerClass == this.thisClass))
        {
          if (Package.this.verbose > 1) {
            Utils.log.fine("Relevant IC: " + localInnerClass);
          }
          ((ArrayList)localObject2).add(localInnerClass);
        }
      }
      return localObject2;
    }
    
    private List<Package.InnerClass> computeICdiff()
    {
      List localList1 = computeGloballyImpliedICs();
      List localList2 = getInnerClasses();
      if (localList2 == null) {
        localList2 = Collections.emptyList();
      }
      if (localList2.isEmpty()) {
        return localList1;
      }
      if (localList1.isEmpty()) {
        return localList2;
      }
      HashSet localHashSet = new HashSet(localList2);
      localHashSet.retainAll(new HashSet(localList1));
      localList1.addAll(localList2);
      localList1.removeAll(localHashSet);
      return localList1;
    }
    
    void minimizeLocalICs()
    {
      List localList1 = computeICdiff();
      ArrayList localArrayList = this.innerClasses;
      List localList2;
      if (localList1.isEmpty())
      {
        localList2 = null;
        if ((localArrayList != null) && (localArrayList.isEmpty()) && (Package.this.verbose > 0)) {
          Utils.log.info("Warning: Dropping empty InnerClasses attribute from " + this);
        }
      }
      else if (localArrayList == null)
      {
        localList2 = Collections.emptyList();
      }
      else
      {
        localList2 = localList1;
      }
      setInnerClasses(localList2);
      if ((Package.this.verbose > 1) && (localList2 != null)) {
        Utils.log.fine("keeping local ICs in " + this + ": " + localList2);
      }
    }
    
    int expandLocalICs()
    {
      ArrayList localArrayList = this.innerClasses;
      List localList1;
      int i;
      if (localArrayList == null)
      {
        List localList2 = computeGloballyImpliedICs();
        if (localList2.isEmpty())
        {
          localList1 = null;
          i = 0;
        }
        else
        {
          localList1 = localList2;
          i = 1;
        }
      }
      else if (localArrayList.isEmpty())
      {
        localList1 = null;
        i = 0;
      }
      else
      {
        localList1 = computeICdiff();
        i = localList1.containsAll(localArrayList) ? 1 : -1;
      }
      setInnerClasses(localList1);
      return i;
    }
    
    public void trimToSize()
    {
      super.trimToSize();
      for (int i = 0; i <= 1; i++)
      {
        ArrayList localArrayList = i == 0 ? this.fields : this.methods;
        if (localArrayList != null)
        {
          localArrayList.trimToSize();
          Iterator localIterator = localArrayList.iterator();
          while (localIterator.hasNext())
          {
            Member localMember = (Member)localIterator.next();
            localMember.trimToSize();
          }
        }
      }
      if (this.innerClasses != null) {
        this.innerClasses.trimToSize();
      }
    }
    
    public void strip(String paramString)
    {
      if ("InnerClass".equals(paramString)) {
        this.innerClasses = null;
      }
      for (int i = 0; i <= 1; i++)
      {
        ArrayList localArrayList = i == 0 ? this.fields : this.methods;
        if (localArrayList != null)
        {
          Iterator localIterator = localArrayList.iterator();
          while (localIterator.hasNext())
          {
            Member localMember = (Member)localIterator.next();
            localMember.strip(paramString);
          }
        }
      }
      super.strip(paramString);
    }
    
    protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
    {
      if (Package.this.verbose > 2) {
        Utils.log.fine("visitRefs " + this);
      }
      paramCollection.add(this.thisClass);
      paramCollection.add(this.superClass);
      paramCollection.addAll(Arrays.asList(this.interfaces));
      for (int i = 0; i <= 1; i++)
      {
        ArrayList localArrayList = i == 0 ? this.fields : this.methods;
        if (localArrayList != null)
        {
          Iterator localIterator = localArrayList.iterator();
          while (localIterator.hasNext())
          {
            Member localMember = (Member)localIterator.next();
            int j = 0;
            try
            {
              localMember.visitRefs(paramInt, paramCollection);
              j = 1;
            }
            finally
            {
              if (j == 0) {
                Utils.log.warning("Error scanning " + localMember);
              }
            }
          }
        }
      }
      visitInnerClassRefs(paramInt, paramCollection);
      super.visitRefs(paramInt, paramCollection);
    }
    
    protected void visitInnerClassRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
    {
      Package.visitInnerClassRefs(this.innerClasses, paramInt, paramCollection);
    }
    
    void finishReading()
    {
      trimToSize();
      maybeChooseFileName();
    }
    
    public void initFile(Package.File paramFile)
    {
      assert (this.file == null);
      if (paramFile == null) {
        paramFile = Package.this.newStub(canonicalFileName());
      }
      this.file = paramFile;
      assert (paramFile.isClassStub());
      paramFile.stubClass = this;
      maybeChooseFileName();
    }
    
    public void maybeChooseFileName()
    {
      if (this.thisClass == null) {
        return;
      }
      String str = canonicalFileName();
      if (this.file.nameString.equals("")) {
        this.file.nameString = str;
      }
      if (this.file.nameString.equals(str))
      {
        this.file.name = Package.getRefString("");
        return;
      }
      if (this.file.name == null) {
        this.file.name = Package.getRefString(this.file.nameString);
      }
    }
    
    public String canonicalFileName()
    {
      if (this.thisClass == null) {
        return null;
      }
      return this.thisClass.stringValue() + ".class";
    }
    
    public File getFileName(File paramFile)
    {
      String str1 = this.file.name.stringValue();
      if (str1.equals("")) {
        str1 = canonicalFileName();
      }
      String str2 = str1.replace('/', File.separatorChar);
      return new File(paramFile, str2);
    }
    
    public File getFileName()
    {
      return getFileName(null);
    }
    
    public String toString()
    {
      return this.thisClass.stringValue();
    }
    
    public class Field
      extends Package.Class.Member
    {
      int order;
      
      public Field(int paramInt, ConstantPool.DescriptorEntry paramDescriptorEntry)
      {
        super(paramInt, paramDescriptorEntry);
        assert (!paramDescriptorEntry.isMethod());
        if (Package.Class.this.fields == null) {
          Package.Class.this.fields = new ArrayList();
        }
        boolean bool = Package.Class.this.fields.add(this);
        assert (bool);
        this.order = Package.Class.this.fields.size();
      }
      
      public byte getLiteralTag()
      {
        return this.descriptor.getLiteralTag();
      }
      
      public int compareTo(Package.Class.Member paramMember)
      {
        Field localField = (Field)paramMember;
        return this.order - localField.order;
      }
    }
    
    public abstract class Member
      extends Attribute.Holder
      implements Comparable<Member>
    {
      ConstantPool.DescriptorEntry descriptor;
      
      protected Member(int paramInt, ConstantPool.DescriptorEntry paramDescriptorEntry)
      {
        this.flags = paramInt;
        this.descriptor = paramDescriptorEntry;
      }
      
      public Package.Class thisClass()
      {
        return Package.Class.this;
      }
      
      public ConstantPool.DescriptorEntry getDescriptor()
      {
        return this.descriptor;
      }
      
      public String getName()
      {
        return this.descriptor.nameRef.stringValue();
      }
      
      public String getType()
      {
        return this.descriptor.typeRef.stringValue();
      }
      
      protected ConstantPool.Entry[] getCPMap()
      {
        return Package.Class.this.cpMap;
      }
      
      protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
      {
        if (Package.this.verbose > 2) {
          Utils.log.fine("visitRefs " + this);
        }
        if (paramInt == 0)
        {
          paramCollection.add(this.descriptor.nameRef);
          paramCollection.add(this.descriptor.typeRef);
        }
        else
        {
          paramCollection.add(this.descriptor);
        }
        super.visitRefs(paramInt, paramCollection);
      }
      
      public String toString()
      {
        return Package.Class.this + "." + this.descriptor.prettyString();
      }
    }
    
    public class Method
      extends Package.Class.Member
    {
      Code code;
      
      public Method(int paramInt, ConstantPool.DescriptorEntry paramDescriptorEntry)
      {
        super(paramInt, paramDescriptorEntry);
        assert (paramDescriptorEntry.isMethod());
        if (Package.Class.this.methods == null) {
          Package.Class.this.methods = new ArrayList();
        }
        boolean bool = Package.Class.this.methods.add(this);
        assert (bool);
      }
      
      public void trimToSize()
      {
        super.trimToSize();
        if (this.code != null) {
          this.code.trimToSize();
        }
      }
      
      public int getArgumentSize()
      {
        int i = this.descriptor.typeRef.computeSize(true);
        int j = Modifier.isStatic(this.flags) ? 0 : 1;
        return j + i;
      }
      
      public int compareTo(Package.Class.Member paramMember)
      {
        Method localMethod = (Method)paramMember;
        return getDescriptor().compareTo(localMethod.getDescriptor());
      }
      
      public void strip(String paramString)
      {
        if ("Code".equals(paramString)) {
          this.code = null;
        }
        if (this.code != null) {
          this.code.strip(paramString);
        }
        super.strip(paramString);
      }
      
      protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
      {
        super.visitRefs(paramInt, paramCollection);
        if (this.code != null)
        {
          if (paramInt == 0) {
            paramCollection.add(Package.getRefString("Code"));
          }
          this.code.visitRefs(paramInt, paramCollection);
        }
      }
    }
  }
  
  public final class File
    implements Comparable<File>
  {
    String nameString;
    ConstantPool.Utf8Entry name;
    int modtime = 0;
    int options = 0;
    Package.Class stubClass;
    ArrayList<byte[]> prepend = new ArrayList();
    ByteArrayOutputStream append = new ByteArrayOutputStream();
    
    File(ConstantPool.Utf8Entry paramUtf8Entry)
    {
      this.name = paramUtf8Entry;
      this.nameString = paramUtf8Entry.stringValue();
    }
    
    File(String paramString)
    {
      paramString = Package.fixupFileName(paramString);
      this.name = Package.getRefString(paramString);
      this.nameString = this.name.stringValue();
    }
    
    public boolean isDirectory()
    {
      return this.nameString.endsWith("/");
    }
    
    public boolean isClassStub()
    {
      return (this.options & 0x2) != 0;
    }
    
    public Package.Class getStubClass()
    {
      assert (isClassStub());
      assert (this.stubClass != null);
      return this.stubClass;
    }
    
    public boolean isTrivialClassStub()
    {
      return (isClassStub()) && (this.name.stringValue().equals("")) && ((this.modtime == 0) || (this.modtime == Package.this.default_modtime)) && ((this.options & 0xFFFFFFFD) == 0);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != File.class)) {
        return false;
      }
      File localFile = (File)paramObject;
      return localFile.nameString.equals(this.nameString);
    }
    
    public int hashCode()
    {
      return this.nameString.hashCode();
    }
    
    public int compareTo(File paramFile)
    {
      return this.nameString.compareTo(paramFile.nameString);
    }
    
    public String toString()
    {
      return this.nameString + "{" + (isClassStub() ? "*" : "") + (BandStructure.testBit(this.options, 1) ? "@" : "") + (this.modtime == 0 ? "" : new StringBuilder().append("M").append(this.modtime).toString()) + (getFileLength() == 0L ? "" : new StringBuilder().append("[").append(getFileLength()).append("]").toString()) + "}";
    }
    
    public File getFileName()
    {
      return getFileName(null);
    }
    
    public File getFileName(File paramFile)
    {
      String str1 = this.nameString;
      String str2 = str1.replace('/', File.separatorChar);
      return new File(paramFile, str2);
    }
    
    public void addBytes(byte[] paramArrayOfByte)
    {
      addBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public void addBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if ((this.append.size() | paramInt2) << 2 < 0)
      {
        this.prepend.add(this.append.toByteArray());
        this.append.reset();
      }
      this.append.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    
    public long getFileLength()
    {
      long l = 0L;
      if ((this.prepend == null) || (this.append == null)) {
        return 0L;
      }
      Iterator localIterator = this.prepend.iterator();
      while (localIterator.hasNext())
      {
        byte[] arrayOfByte = (byte[])localIterator.next();
        l += arrayOfByte.length;
      }
      l += this.append.size();
      return l;
    }
    
    public void writeTo(OutputStream paramOutputStream)
      throws IOException
    {
      if ((this.prepend == null) || (this.append == null)) {
        return;
      }
      Iterator localIterator = this.prepend.iterator();
      while (localIterator.hasNext())
      {
        byte[] arrayOfByte = (byte[])localIterator.next();
        paramOutputStream.write(arrayOfByte);
      }
      this.append.writeTo(paramOutputStream);
    }
    
    public void readFrom(InputStream paramInputStream)
      throws IOException
    {
      byte[] arrayOfByte = new byte[65536];
      int i;
      while ((i = paramInputStream.read(arrayOfByte)) > 0) {
        addBytes(arrayOfByte, 0, i);
      }
    }
    
    public InputStream getInputStream()
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.append.toByteArray());
      if (this.prepend.isEmpty()) {
        return localByteArrayInputStream;
      }
      ArrayList localArrayList = new ArrayList(this.prepend.size() + 1);
      Iterator localIterator = this.prepend.iterator();
      while (localIterator.hasNext())
      {
        byte[] arrayOfByte = (byte[])localIterator.next();
        localArrayList.add(new ByteArrayInputStream(arrayOfByte));
      }
      localArrayList.add(localByteArrayInputStream);
      return new SequenceInputStream(Collections.enumeration(localArrayList));
    }
    
    protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
    {
      assert (this.name != null);
      paramCollection.add(this.name);
    }
  }
  
  static class InnerClass
    implements Comparable<InnerClass>
  {
    final ConstantPool.ClassEntry thisClass;
    final ConstantPool.ClassEntry outerClass;
    final ConstantPool.Utf8Entry name;
    final int flags;
    final boolean predictable;
    
    InnerClass(ConstantPool.ClassEntry paramClassEntry1, ConstantPool.ClassEntry paramClassEntry2, ConstantPool.Utf8Entry paramUtf8Entry, int paramInt)
    {
      this.thisClass = paramClassEntry1;
      this.outerClass = paramClassEntry2;
      this.name = paramUtf8Entry;
      this.flags = paramInt;
      this.predictable = computePredictable();
    }
    
    private boolean computePredictable()
    {
      String[] arrayOfString = Package.parseInnerClassName(this.thisClass.stringValue());
      if (arrayOfString == null) {
        return false;
      }
      String str1 = arrayOfString[0];
      String str2 = arrayOfString[2];
      String str3 = this.name == null ? null : this.name.stringValue();
      String str4 = this.outerClass == null ? null : this.outerClass.stringValue();
      boolean bool = (str2 == str3) && (str1 == str4);
      return bool;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject == null) || (paramObject.getClass() != InnerClass.class)) {
        return false;
      }
      InnerClass localInnerClass = (InnerClass)paramObject;
      return (eq(this.thisClass, localInnerClass.thisClass)) && (eq(this.outerClass, localInnerClass.outerClass)) && (eq(this.name, localInnerClass.name)) && (this.flags == localInnerClass.flags);
    }
    
    private static boolean eq(Object paramObject1, Object paramObject2)
    {
      return paramObject1 == null ? false : paramObject2 == null ? true : paramObject1.equals(paramObject2);
    }
    
    public int hashCode()
    {
      return this.thisClass.hashCode();
    }
    
    public int compareTo(InnerClass paramInnerClass)
    {
      return this.thisClass.compareTo(paramInnerClass.thisClass);
    }
    
    protected void visitRefs(int paramInt, Collection<ConstantPool.Entry> paramCollection)
    {
      paramCollection.add(this.thisClass);
      if ((paramInt == 0) || (!this.predictable))
      {
        paramCollection.add(this.outerClass);
        paramCollection.add(this.name);
      }
    }
    
    public String toString()
    {
      return this.thisClass.stringValue();
    }
  }
  
  protected static final class Version
  {
    public final short major;
    public final short minor;
    
    private Version(short paramShort1, short paramShort2)
    {
      this.major = paramShort1;
      this.minor = paramShort2;
    }
    
    public String toString()
    {
      return this.major + "." + this.minor;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof Version)) && (this.major == ((Version)paramObject).major) && (this.minor == ((Version)paramObject).minor);
    }
    
    public int intValue()
    {
      return (this.major << 16) + this.minor;
    }
    
    public int hashCode()
    {
      return (this.major << 16) + 7 + this.minor;
    }
    
    public static Version of(int paramInt1, int paramInt2)
    {
      return new Version((short)paramInt1, (short)paramInt2);
    }
    
    public static Version of(byte[] paramArrayOfByte)
    {
      int i = (paramArrayOfByte[0] & 0xFF) << 8 | paramArrayOfByte[1] & 0xFF;
      int j = (paramArrayOfByte[2] & 0xFF) << 8 | paramArrayOfByte[3] & 0xFF;
      return new Version((short)j, (short)i);
    }
    
    public static Version of(int paramInt)
    {
      short s1 = (short)paramInt;
      short s2 = (short)(paramInt >>> 16);
      return new Version(s2, s1);
    }
    
    public static Version makeVersion(PropMap paramPropMap, String paramString)
    {
      int i = paramPropMap.getInteger("com.sun.java.util.jar.pack." + paramString + ".minver", -1);
      int j = paramPropMap.getInteger("com.sun.java.util.jar.pack." + paramString + ".majver", -1);
      return (i >= 0) && (j >= 0) ? of(j, i) : null;
    }
    
    public byte[] asBytes()
    {
      byte[] arrayOfByte = { (byte)(this.minor >> 8), (byte)this.minor, (byte)(this.major >> 8), (byte)this.major };
      return arrayOfByte;
    }
    
    public int compareTo(Version paramVersion)
    {
      return intValue() - paramVersion.intValue();
    }
    
    public boolean lessThan(Version paramVersion)
    {
      return compareTo(paramVersion) < 0;
    }
    
    public boolean greaterThan(Version paramVersion)
    {
      return compareTo(paramVersion) > 0;
    }
  }
}
