package com.sun.jndi.dns;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.Vector;

class ZoneNode
  extends NameNode
{
  private SoftReference<NameNode> contentsRef = null;
  private long serialNumber = -1L;
  private Date expiration = null;
  
  ZoneNode(String paramString)
  {
    super(paramString);
  }
  
  protected NameNode newNameNode(String paramString)
  {
    return new ZoneNode(paramString);
  }
  
  synchronized void depopulate()
  {
    this.contentsRef = null;
    this.serialNumber = -1L;
  }
  
  synchronized boolean isPopulated()
  {
    return getContents() != null;
  }
  
  synchronized NameNode getContents()
  {
    return this.contentsRef != null ? (NameNode)this.contentsRef.get() : null;
  }
  
  synchronized boolean isExpired()
  {
    return (this.expiration != null) && (this.expiration.before(new Date()));
  }
  
  ZoneNode getDeepestPopulated(DnsName paramDnsName)
  {
    ZoneNode localZoneNode1 = this;
    ZoneNode localZoneNode2 = isPopulated() ? this : null;
    for (int i = 1; i < paramDnsName.size(); i++)
    {
      localZoneNode1 = (ZoneNode)localZoneNode1.get(paramDnsName.getKey(i));
      if (localZoneNode1 == null) {
        break;
      }
      if (localZoneNode1.isPopulated()) {
        localZoneNode2 = localZoneNode1;
      }
    }
    return localZoneNode2;
  }
  
  NameNode populate(DnsName paramDnsName, ResourceRecords paramResourceRecords)
  {
    NameNode localNameNode1 = new NameNode(null);
    for (int i = 0; i < paramResourceRecords.answer.size(); i++)
    {
      ResourceRecord localResourceRecord2 = (ResourceRecord)paramResourceRecords.answer.elementAt(i);
      DnsName localDnsName = localResourceRecord2.getName();
      if ((localDnsName.size() > paramDnsName.size()) && (localDnsName.startsWith(paramDnsName)))
      {
        NameNode localNameNode2 = localNameNode1.add(localDnsName, paramDnsName.size());
        if (localResourceRecord2.getType() == 2) {
          localNameNode2.setZoneCut(true);
        }
      }
    }
    ResourceRecord localResourceRecord1 = (ResourceRecord)paramResourceRecords.answer.firstElement();
    synchronized (this)
    {
      this.contentsRef = new SoftReference(localNameNode1);
      this.serialNumber = getSerialNumber(localResourceRecord1);
      setExpiration(getMinimumTtl(localResourceRecord1));
      return localNameNode1;
    }
  }
  
  private void setExpiration(long paramLong)
  {
    this.expiration = new Date(System.currentTimeMillis() + 1000L * paramLong);
  }
  
  private static long getMinimumTtl(ResourceRecord paramResourceRecord)
  {
    String str = (String)paramResourceRecord.getRdata();
    int i = str.lastIndexOf(' ') + 1;
    return Long.parseLong(str.substring(i));
  }
  
  int compareSerialNumberTo(ResourceRecord paramResourceRecord)
  {
    return ResourceRecord.compareSerialNumbers(this.serialNumber, getSerialNumber(paramResourceRecord));
  }
  
  private static long getSerialNumber(ResourceRecord paramResourceRecord)
  {
    String str = (String)paramResourceRecord.getRdata();
    int i = str.length();
    int j = -1;
    for (int k = 0; k < 5; k++)
    {
      j = i;
      i = str.lastIndexOf(' ', j - 1);
    }
    return Long.parseLong(str.substring(i + 1, j));
  }
}
