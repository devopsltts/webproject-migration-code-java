package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.Parameter;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.spi.db.RepeatedElementBridge;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.WrapperComposite;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.List;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;

public class ParameterImpl
  implements Parameter
{
  private ParameterBinding binding;
  private ParameterBinding outBinding;
  private String partName;
  private final int index;
  private final WebParam.Mode mode;
  /**
   * @deprecated
   */
  private TypeReference typeReference;
  private TypeInfo typeInfo;
  private QName name;
  private final JavaMethodImpl parent;
  WrapperParameter wrapper;
  TypeInfo itemTypeInfo;
  
  public ParameterImpl(JavaMethodImpl paramJavaMethodImpl, TypeInfo paramTypeInfo, WebParam.Mode paramMode, int paramInt)
  {
    assert (paramTypeInfo != null);
    this.typeInfo = paramTypeInfo;
    this.name = paramTypeInfo.tagName;
    this.mode = paramMode;
    this.index = paramInt;
    this.parent = paramJavaMethodImpl;
  }
  
  public AbstractSEIModelImpl getOwner()
  {
    return this.parent.owner;
  }
  
  public JavaMethod getParent()
  {
    return this.parent;
  }
  
  public QName getName()
  {
    return this.name;
  }
  
  public XMLBridge getXMLBridge()
  {
    return getOwner().getXMLBridge(this.typeInfo);
  }
  
  public XMLBridge getInlinedRepeatedElementBridge()
  {
    TypeInfo localTypeInfo = getItemType();
    if (localTypeInfo != null)
    {
      XMLBridge localXMLBridge = getOwner().getXMLBridge(localTypeInfo);
      if (localXMLBridge != null) {
        return new RepeatedElementBridge(this.typeInfo, localXMLBridge);
      }
    }
    return null;
  }
  
  public TypeInfo getItemType()
  {
    if (this.itemTypeInfo != null) {
      return this.itemTypeInfo;
    }
    if ((this.parent.getBinding().isRpcLit()) || (this.wrapper == null)) {
      return null;
    }
    if (!WrapperComposite.class.equals(this.wrapper.getTypeInfo().type)) {
      return null;
    }
    if (!getBinding().isBody()) {
      return null;
    }
    this.itemTypeInfo = this.typeInfo.getItemType();
    return this.itemTypeInfo;
  }
  
  /**
   * @deprecated
   */
  public Bridge getBridge()
  {
    return getOwner().getBridge(this.typeReference);
  }
  
  /**
   * @deprecated
   */
  protected Bridge getBridge(TypeReference paramTypeReference)
  {
    return getOwner().getBridge(paramTypeReference);
  }
  
  /**
   * @deprecated
   */
  public TypeReference getTypeReference()
  {
    return this.typeReference;
  }
  
  public TypeInfo getTypeInfo()
  {
    return this.typeInfo;
  }
  
  /**
   * @deprecated
   */
  void setTypeReference(TypeReference paramTypeReference)
  {
    this.typeReference = paramTypeReference;
    this.name = paramTypeReference.tagName;
  }
  
  public WebParam.Mode getMode()
  {
    return this.mode;
  }
  
  public int getIndex()
  {
    return this.index;
  }
  
  public boolean isWrapperStyle()
  {
    return false;
  }
  
  public boolean isReturnValue()
  {
    return this.index == -1;
  }
  
  public ParameterBinding getBinding()
  {
    if (this.binding == null) {
      return ParameterBinding.BODY;
    }
    return this.binding;
  }
  
  public void setBinding(ParameterBinding paramParameterBinding)
  {
    this.binding = paramParameterBinding;
  }
  
  public void setInBinding(ParameterBinding paramParameterBinding)
  {
    this.binding = paramParameterBinding;
  }
  
  public void setOutBinding(ParameterBinding paramParameterBinding)
  {
    this.outBinding = paramParameterBinding;
  }
  
  public ParameterBinding getInBinding()
  {
    return this.binding;
  }
  
  public ParameterBinding getOutBinding()
  {
    if (this.outBinding == null) {
      return this.binding;
    }
    return this.outBinding;
  }
  
  public boolean isIN()
  {
    return this.mode == WebParam.Mode.IN;
  }
  
  public boolean isOUT()
  {
    return this.mode == WebParam.Mode.OUT;
  }
  
  public boolean isINOUT()
  {
    return this.mode == WebParam.Mode.INOUT;
  }
  
  public boolean isResponse()
  {
    return this.index == -1;
  }
  
  public Object getHolderValue(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Holder))) {
      return ((Holder)paramObject).value;
    }
    return paramObject;
  }
  
  public String getPartName()
  {
    if (this.partName == null) {
      return this.name.getLocalPart();
    }
    return this.partName;
  }
  
  public void setPartName(String paramString)
  {
    this.partName = paramString;
  }
  
  void fillTypes(List<TypeInfo> paramList)
  {
    TypeInfo localTypeInfo = getItemType();
    paramList.add(localTypeInfo != null ? localTypeInfo : getTypeInfo());
  }
}
