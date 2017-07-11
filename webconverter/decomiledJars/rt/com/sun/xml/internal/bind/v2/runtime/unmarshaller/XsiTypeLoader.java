package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XsiTypeLoader
  extends Loader
{
  private final JaxBeanInfo defaultBeanInfo;
  static final QName XsiTypeQNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
  
  public XsiTypeLoader(JaxBeanInfo paramJaxBeanInfo)
  {
    super(true);
    this.defaultBeanInfo = paramJaxBeanInfo;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName)
    throws SAXException
  {
    JaxBeanInfo localJaxBeanInfo = parseXsiType(paramState, paramTagName, this.defaultBeanInfo);
    if (localJaxBeanInfo == null) {
      localJaxBeanInfo = this.defaultBeanInfo;
    }
    Loader localLoader = localJaxBeanInfo.getLoader(null, false);
    paramState.setLoader(localLoader);
    localLoader.startElement(paramState, paramTagName);
  }
  
  static JaxBeanInfo parseXsiType(UnmarshallingContext.State paramState, TagName paramTagName, @Nullable JaxBeanInfo paramJaxBeanInfo)
    throws SAXException
  {
    UnmarshallingContext localUnmarshallingContext = paramState.getContext();
    JaxBeanInfo localJaxBeanInfo = null;
    Attributes localAttributes = paramTagName.atts;
    int i = localAttributes.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
    if (i >= 0)
    {
      String str1 = localAttributes.getValue(i);
      QName localQName = DatatypeConverterImpl._parseQName(str1, localUnmarshallingContext);
      if (localQName == null)
      {
        reportError(Messages.NOT_A_QNAME.format(new Object[] { str1 }), true);
      }
      else
      {
        if ((paramJaxBeanInfo != null) && (paramJaxBeanInfo.getTypeNames().contains(localQName))) {
          return paramJaxBeanInfo;
        }
        localJaxBeanInfo = localUnmarshallingContext.getJAXBContext().getGlobalType(localQName);
        if ((localJaxBeanInfo == null) && (localUnmarshallingContext.parent.hasEventHandler()) && (localUnmarshallingContext.shouldErrorBeReported()))
        {
          String str2 = localUnmarshallingContext.getJAXBContext().getNearestTypeName(localQName);
          if (str2 != null) {
            reportError(Messages.UNRECOGNIZED_TYPE_NAME_MAYBE.format(new Object[] { localQName, str2 }), true);
          } else {
            reportError(Messages.UNRECOGNIZED_TYPE_NAME.format(new Object[] { localQName }), true);
          }
        }
      }
    }
    return localJaxBeanInfo;
  }
  
  public Collection<QName> getExpectedAttributes()
  {
    HashSet localHashSet = new HashSet();
    localHashSet.addAll(super.getExpectedAttributes());
    localHashSet.add(XsiTypeQNAME);
    return Collections.unmodifiableCollection(localHashSet);
  }
}
