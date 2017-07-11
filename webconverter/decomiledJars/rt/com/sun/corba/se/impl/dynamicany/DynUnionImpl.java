package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynUnion;

public class DynUnionImpl
  extends DynAnyConstructedImpl
  implements DynUnion
{
  DynAny discriminator = null;
  DynAny currentMember = null;
  int currentMemberIndex = -1;
  
  private DynUnionImpl()
  {
    this(null, (Any)null, false);
  }
  
  protected DynUnionImpl(ORB paramORB, Any paramAny, boolean paramBoolean)
  {
    super(paramORB, paramAny, paramBoolean);
  }
  
  protected DynUnionImpl(ORB paramORB, TypeCode paramTypeCode)
  {
    super(paramORB, paramTypeCode);
  }
  
  protected boolean initializeComponentsFromAny()
  {
    try
    {
      InputStream localInputStream = this.any.create_input_stream();
      Any localAny1 = DynAnyUtil.extractAnyFromStream(discriminatorType(), localInputStream, this.orb);
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(localAny1, this.orb, false);
      this.currentMemberIndex = currentUnionMemberIndex(localAny1);
      Any localAny2 = DynAnyUtil.extractAnyFromStream(memberType(this.currentMemberIndex), localInputStream, this.orb);
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(localAny2, this.orb, false);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    return true;
  }
  
  protected boolean initializeComponentsFromTypeCode()
  {
    try
    {
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(memberLabel(0), this.orb, false);
      this.index = 0;
      this.currentMemberIndex = 0;
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(0), this.orb);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
    return true;
  }
  
  private TypeCode discriminatorType()
  {
    TypeCode localTypeCode = null;
    try
    {
      localTypeCode = this.any.type().discriminator_type();
    }
    catch (BadKind localBadKind) {}
    return localTypeCode;
  }
  
  private int memberCount()
  {
    int i = 0;
    try
    {
      i = this.any.type().member_count();
    }
    catch (BadKind localBadKind) {}
    return i;
  }
  
  private Any memberLabel(int paramInt)
  {
    Any localAny = null;
    try
    {
      localAny = this.any.type().member_label(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return localAny;
  }
  
  private TypeCode memberType(int paramInt)
  {
    TypeCode localTypeCode = null;
    try
    {
      localTypeCode = this.any.type().member_type(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return localTypeCode;
  }
  
  private String memberName(int paramInt)
  {
    String str = null;
    try
    {
      str = this.any.type().member_name(paramInt);
    }
    catch (BadKind localBadKind) {}catch (Bounds localBounds) {}
    return str;
  }
  
  private int defaultIndex()
  {
    int i = -1;
    try
    {
      i = this.any.type().default_index();
    }
    catch (BadKind localBadKind) {}
    return i;
  }
  
  private int currentUnionMemberIndex(Any paramAny)
  {
    int i = memberCount();
    for (int j = 0; j < i; j++)
    {
      Any localAny = memberLabel(j);
      if (localAny.equal(paramAny)) {
        return j;
      }
    }
    if (defaultIndex() != -1) {
      return defaultIndex();
    }
    return -1;
  }
  
  protected void clearData()
  {
    super.clearData();
    this.discriminator = null;
    this.currentMember.destroy();
    this.currentMember = null;
    this.currentMemberIndex = -1;
  }
  
  public DynAny get_discriminator()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    return checkInitComponents() ? this.discriminator : null;
  }
  
  public void set_discriminator(DynAny paramDynAny)
    throws TypeMismatch
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (!paramDynAny.type().equal(discriminatorType())) {
      throw new TypeMismatch();
    }
    paramDynAny = DynAnyUtil.convertToNative(paramDynAny, this.orb);
    Any localAny = getAny(paramDynAny);
    int i = currentUnionMemberIndex(localAny);
    if (i == -1)
    {
      clearData();
      this.index = 0;
    }
    else
    {
      checkInitComponents();
      if ((this.currentMemberIndex == -1) || (i != this.currentMemberIndex))
      {
        clearData();
        this.index = 1;
        this.currentMemberIndex = i;
        try
        {
          this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(this.currentMemberIndex), this.orb);
        }
        catch (InconsistentTypeCode localInconsistentTypeCode) {}
        this.discriminator = paramDynAny;
        this.components = new DynAny[] { this.discriminator, this.currentMember };
        this.representations = 4;
      }
    }
  }
  
  public void set_to_default_member()
    throws TypeMismatch
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    int i = defaultIndex();
    if (i == -1) {
      throw new TypeMismatch();
    }
    try
    {
      clearData();
      this.index = 1;
      this.currentMemberIndex = i;
      this.currentMember = DynAnyUtil.createMostDerivedDynAny(memberType(i), this.orb);
      this.components = new DynAny[] { this.discriminator, this.currentMember };
      Any localAny = this.orb.create_any();
      localAny.insert_octet((byte)0);
      this.discriminator = DynAnyUtil.createMostDerivedDynAny(localAny, this.orb, false);
      this.representations = 4;
    }
    catch (InconsistentTypeCode localInconsistentTypeCode) {}
  }
  
  public void set_to_no_active_member()
    throws TypeMismatch
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (defaultIndex() != -1) {
      throw new TypeMismatch();
    }
    checkInitComponents();
    Any localAny = getAny(this.discriminator);
    localAny.type(localAny.type());
    this.index = 0;
    this.currentMemberIndex = -1;
    this.currentMember.destroy();
    this.currentMember = null;
    this.components[0] = this.discriminator;
    this.representations = 4;
  }
  
  public boolean has_no_active_member()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if (defaultIndex() != -1) {
      return false;
    }
    checkInitComponents();
    return this.currentMemberIndex == -1;
  }
  
  public TCKind discriminator_kind()
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    return discriminatorType().kind();
  }
  
  public DynAny member()
    throws InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (this.currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    return this.currentMember;
  }
  
  public String member_name()
    throws InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (this.currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    String str = memberName(this.currentMemberIndex);
    return str == null ? "" : str;
  }
  
  public TCKind member_kind()
    throws InvalidValue
  {
    if (this.status == 2) {
      throw this.wrapper.dynAnyDestroyed();
    }
    if ((!checkInitComponents()) || (this.currentMemberIndex == -1)) {
      throw new InvalidValue();
    }
    return memberType(this.currentMemberIndex).kind();
  }
}
