package com.sun.jndi.dns;

import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

class Resolver
{
  private DnsClient dnsClient;
  private int timeout;
  private int retries;
  
  Resolver(String[] paramArrayOfString, int paramInt1, int paramInt2)
    throws NamingException
  {
    this.timeout = paramInt1;
    this.retries = paramInt2;
    this.dnsClient = new DnsClient(paramArrayOfString, paramInt1, paramInt2);
  }
  
  public void close()
  {
    this.dnsClient.close();
    this.dnsClient = null;
  }
  
  ResourceRecords query(DnsName paramDnsName, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    throws NamingException
  {
    return this.dnsClient.query(paramDnsName, paramInt1, paramInt2, paramBoolean1, paramBoolean2);
  }
  
  ResourceRecords queryZone(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    DnsClient localDnsClient = new DnsClient(findNameServers(paramDnsName, paramBoolean), this.timeout, this.retries);
    try
    {
      ResourceRecords localResourceRecords = localDnsClient.queryZone(paramDnsName, paramInt, paramBoolean);
      return localResourceRecords;
    }
    finally
    {
      localDnsClient.close();
    }
  }
  
  DnsName findZoneName(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    paramDnsName = (DnsName)paramDnsName.clone();
    while (paramDnsName.size() > 1)
    {
      ResourceRecords localResourceRecords = null;
      try
      {
        localResourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
      }
      catch (NameNotFoundException localNameNotFoundException)
      {
        throw localNameNotFoundException;
      }
      catch (NamingException localNamingException) {}
      if (localResourceRecords != null)
      {
        if (localResourceRecords.answer.size() > 0) {
          return paramDnsName;
        }
        for (int i = 0; i < localResourceRecords.authority.size(); i++)
        {
          ResourceRecord localResourceRecord = (ResourceRecord)localResourceRecords.authority.elementAt(i);
          if (localResourceRecord.getType() == 6)
          {
            DnsName localDnsName = localResourceRecord.getName();
            if (paramDnsName.endsWith(localDnsName)) {
              return localDnsName;
            }
          }
        }
      }
      paramDnsName.remove(paramDnsName.size() - 1);
    }
    return paramDnsName;
  }
  
  ResourceRecord findSoa(DnsName paramDnsName, int paramInt, boolean paramBoolean)
    throws NamingException
  {
    ResourceRecords localResourceRecords = query(paramDnsName, paramInt, 6, paramBoolean, false);
    for (int i = 0; i < localResourceRecords.answer.size(); i++)
    {
      ResourceRecord localResourceRecord = (ResourceRecord)localResourceRecords.answer.elementAt(i);
      if (localResourceRecord.getType() == 6) {
        return localResourceRecord;
      }
    }
    return null;
  }
  
  private String[] findNameServers(DnsName paramDnsName, boolean paramBoolean)
    throws NamingException
  {
    ResourceRecords localResourceRecords = query(paramDnsName, 1, 2, paramBoolean, false);
    String[] arrayOfString = new String[localResourceRecords.answer.size()];
    for (int i = 0; i < arrayOfString.length; i++)
    {
      ResourceRecord localResourceRecord = (ResourceRecord)localResourceRecords.answer.elementAt(i);
      if (localResourceRecord.getType() != 2) {
        throw new CommunicationException("Corrupted DNS message");
      }
      arrayOfString[i] = ((String)localResourceRecord.getRdata());
      arrayOfString[i] = arrayOfString[i].substring(0, arrayOfString[i].length() - 1);
    }
    return arrayOfString;
  }
}
