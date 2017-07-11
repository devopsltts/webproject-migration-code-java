package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public abstract interface JvmMemMgrPoolRelEntryMBean
{
  public abstract String getJvmMemMgrRelPoolName()
    throws SnmpStatusException;
  
  public abstract String getJvmMemMgrRelManagerName()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemManagerIndex()
    throws SnmpStatusException;
  
  public abstract Integer getJvmMemPoolIndex()
    throws SnmpStatusException;
}
