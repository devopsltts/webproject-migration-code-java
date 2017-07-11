package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelUnmarshaller;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public class SafePolicyReader
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(SafePolicyReader.class);
  private final Set<String> urlsRead = new HashSet();
  private final Set<String> qualifiedPolicyUris = new HashSet();
  
  public SafePolicyReader() {}
  
  public PolicyRecord readPolicyElement(XMLStreamReader paramXMLStreamReader, String paramString)
  {
    if ((null == paramXMLStreamReader) || (!paramXMLStreamReader.isStartElement())) {
      return null;
    }
    StringBuffer localStringBuffer1 = new StringBuffer();
    PolicyRecord localPolicyRecord = new PolicyRecord();
    QName localQName1 = paramXMLStreamReader.getName();
    int j = 0;
    try
    {
      do
      {
        QName localQName2;
        switch (paramXMLStreamReader.getEventType())
        {
        case 1: 
          localQName2 = paramXMLStreamReader.getName();
          int i = NamespaceVersion.resolveAsToken(localQName2) == XmlToken.PolicyReference ? 1 : 0;
          if (localQName1.equals(localQName2)) {
            j++;
          }
          StringBuffer localStringBuffer2 = new StringBuffer();
          HashSet localHashSet = new HashSet();
          if ((null == localQName2.getPrefix()) || ("".equals(localQName2.getPrefix())))
          {
            localStringBuffer1.append('<').append(localQName2.getLocalPart());
            localStringBuffer2.append(" xmlns=\"").append(localQName2.getNamespaceURI()).append('"');
          }
          else
          {
            localStringBuffer1.append('<').append(localQName2.getPrefix()).append(':').append(localQName2.getLocalPart());
            localStringBuffer2.append(" xmlns:").append(localQName2.getPrefix()).append("=\"").append(localQName2.getNamespaceURI()).append('"');
            localHashSet.add(localQName2.getPrefix());
          }
          int k = paramXMLStreamReader.getAttributeCount();
          StringBuffer localStringBuffer3 = new StringBuffer();
          for (int m = 0; m < k; m++)
          {
            int n = 0;
            if ((i != 0) && ("URI".equals(paramXMLStreamReader.getAttributeName(m).getLocalPart())))
            {
              n = 1;
              if (null == localPolicyRecord.unresolvedURIs) {
                localPolicyRecord.unresolvedURIs = new HashSet();
              }
              localPolicyRecord.unresolvedURIs.add(relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(m), paramString));
            }
            if ((!"xmlns".equals(paramXMLStreamReader.getAttributePrefix(m))) || (!localHashSet.contains(paramXMLStreamReader.getAttributeLocalName(m)))) {
              if ((null == paramXMLStreamReader.getAttributePrefix(m)) || ("".equals(paramXMLStreamReader.getAttributePrefix(m))))
              {
                localStringBuffer3.append(' ').append(paramXMLStreamReader.getAttributeLocalName(m)).append("=\"").append(n != 0 ? relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(m), paramString) : paramXMLStreamReader.getAttributeValue(m)).append('"');
              }
              else
              {
                localStringBuffer3.append(' ').append(paramXMLStreamReader.getAttributePrefix(m)).append(':').append(paramXMLStreamReader.getAttributeLocalName(m)).append("=\"").append(n != 0 ? relativeToAbsoluteUrl(paramXMLStreamReader.getAttributeValue(m), paramString) : paramXMLStreamReader.getAttributeValue(m)).append('"');
                if (!localHashSet.contains(paramXMLStreamReader.getAttributePrefix(m)))
                {
                  localStringBuffer2.append(" xmlns:").append(paramXMLStreamReader.getAttributePrefix(m)).append("=\"").append(paramXMLStreamReader.getAttributeNamespace(m)).append('"');
                  localHashSet.add(paramXMLStreamReader.getAttributePrefix(m));
                }
              }
            }
          }
          localStringBuffer1.append(localStringBuffer2).append(localStringBuffer3).append('>');
          break;
        case 2: 
          localQName2 = paramXMLStreamReader.getName();
          if (localQName1.equals(localQName2)) {
            j--;
          }
          localStringBuffer1.append("</").append(localQName2.getPrefix() + ':').append(localQName2.getLocalPart()).append('>');
          break;
        case 4: 
          localStringBuffer1.append(paramXMLStreamReader.getText());
          break;
        case 12: 
          localStringBuffer1.append("<![CDATA[").append(paramXMLStreamReader.getText()).append("]]>");
          break;
        case 5: 
          break;
        }
        if ((paramXMLStreamReader.hasNext()) && (j > 0)) {
          paramXMLStreamReader.next();
        }
      } while ((8 != paramXMLStreamReader.getEventType()) && (j > 0));
      localPolicyRecord.policyModel = ModelUnmarshaller.getUnmarshaller().unmarshalModel(new StringReader(localStringBuffer1.toString()));
      if (null != localPolicyRecord.policyModel.getPolicyId()) {
        localPolicyRecord.setUri(paramString + "#" + localPolicyRecord.policyModel.getPolicyId(), localPolicyRecord.policyModel.getPolicyId());
      } else if (localPolicyRecord.policyModel.getPolicyName() != null) {
        localPolicyRecord.setUri(localPolicyRecord.policyModel.getPolicyName(), localPolicyRecord.policyModel.getPolicyName());
      }
    }
    catch (Exception localException)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1013_EXCEPTION_WHEN_READING_POLICY_ELEMENT(localStringBuffer1.toString()), localException)));
    }
    this.urlsRead.add(paramString);
    return localPolicyRecord;
  }
  
  public Set<String> getUrlsRead()
  {
    return this.urlsRead;
  }
  
  public String readPolicyReferenceElement(XMLStreamReader paramXMLStreamReader)
  {
    try
    {
      if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.PolicyReference) {
        for (int i = 0; i < paramXMLStreamReader.getAttributeCount(); i++) {
          if (XmlToken.resolveToken(paramXMLStreamReader.getAttributeName(i).getLocalPart()) == XmlToken.Uri)
          {
            String str = paramXMLStreamReader.getAttributeValue(i);
            paramXMLStreamReader.next();
            return str;
          }
        }
      }
      paramXMLStreamReader.next();
      return null;
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1001_XML_EXCEPTION_WHEN_PROCESSING_POLICY_REFERENCE(), localXMLStreamException)));
    }
  }
  
  public static String relativeToAbsoluteUrl(String paramString1, String paramString2)
  {
    if ('#' != paramString1.charAt(0)) {
      return paramString1;
    }
    return paramString2 + paramString1;
  }
  
  public final class PolicyRecord
  {
    PolicyRecord next;
    PolicySourceModel policyModel;
    Set<String> unresolvedURIs;
    private String uri;
    
    PolicyRecord() {}
    
    PolicyRecord insert(PolicyRecord paramPolicyRecord)
    {
      if ((null == paramPolicyRecord.unresolvedURIs) || (paramPolicyRecord.unresolvedURIs.isEmpty()))
      {
        paramPolicyRecord.next = this;
        return paramPolicyRecord;
      }
      PolicyRecord localPolicyRecord1 = this;
      Object localObject = null;
      for (PolicyRecord localPolicyRecord2 = localPolicyRecord1; null != localPolicyRecord2.next; localPolicyRecord2 = localPolicyRecord2.next)
      {
        if ((null != localPolicyRecord2.unresolvedURIs) && (localPolicyRecord2.unresolvedURIs.contains(paramPolicyRecord.uri)))
        {
          if (null == localObject)
          {
            paramPolicyRecord.next = localPolicyRecord2;
            return paramPolicyRecord;
          }
          localObject.next = paramPolicyRecord;
          paramPolicyRecord.next = localPolicyRecord2;
          return localPolicyRecord1;
        }
        if ((paramPolicyRecord.unresolvedURIs.remove(localPolicyRecord2.uri)) && (paramPolicyRecord.unresolvedURIs.isEmpty()))
        {
          paramPolicyRecord.next = localPolicyRecord2.next;
          localPolicyRecord2.next = paramPolicyRecord;
          return localPolicyRecord1;
        }
        localObject = localPolicyRecord2;
      }
      paramPolicyRecord.next = null;
      localPolicyRecord2.next = paramPolicyRecord;
      return localPolicyRecord1;
    }
    
    public void setUri(String paramString1, String paramString2)
      throws PolicyException
    {
      if (SafePolicyReader.this.qualifiedPolicyUris.contains(paramString1)) {
        throw ((PolicyException)SafePolicyReader.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1020_DUPLICATE_ID(paramString2))));
      }
      this.uri = paramString1;
      SafePolicyReader.this.qualifiedPolicyUris.add(paramString1);
    }
    
    public String getUri()
    {
      return this.uri;
    }
    
    public String toString()
    {
      String str = this.uri;
      if (null != this.next) {
        str = str + "->" + this.next.toString();
      }
      return str;
    }
  }
}
