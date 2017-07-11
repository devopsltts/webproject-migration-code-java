package sun.management.jdp;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class JdpJmxPacket
  extends JdpGenericPacket
  implements JdpPacket
{
  public static final String UUID_KEY = "DISCOVERABLE_SESSION_UUID";
  public static final String MAIN_CLASS_KEY = "MAIN_CLASS";
  public static final String JMX_SERVICE_URL_KEY = "JMX_SERVICE_URL";
  public static final String INSTANCE_NAME_KEY = "INSTANCE_NAME";
  public static final String PROCESS_ID_KEY = "PROCESS_ID";
  public static final String RMI_HOSTNAME_KEY = "RMI_HOSTNAME";
  public static final String BROADCAST_INTERVAL_KEY = "BROADCAST_INTERVAL";
  private UUID id;
  private String mainClass;
  private String jmxServiceUrl;
  private String instanceName;
  private String processId;
  private String rmiHostname;
  private String broadcastInterval;
  
  public JdpJmxPacket(UUID paramUUID, String paramString)
  {
    this.id = paramUUID;
    this.jmxServiceUrl = paramString;
  }
  
  public JdpJmxPacket(byte[] paramArrayOfByte)
    throws JdpException
  {
    JdpPacketReader localJdpPacketReader = new JdpPacketReader(paramArrayOfByte);
    Map localMap = localJdpPacketReader.getDiscoveryDataAsMap();
    String str = (String)localMap.get("DISCOVERABLE_SESSION_UUID");
    this.id = (str == null ? null : UUID.fromString(str));
    this.jmxServiceUrl = ((String)localMap.get("JMX_SERVICE_URL"));
    this.mainClass = ((String)localMap.get("MAIN_CLASS"));
    this.instanceName = ((String)localMap.get("INSTANCE_NAME"));
    this.processId = ((String)localMap.get("PROCESS_ID"));
    this.rmiHostname = ((String)localMap.get("RMI_HOSTNAME"));
    this.broadcastInterval = ((String)localMap.get("BROADCAST_INTERVAL"));
  }
  
  public void setMainClass(String paramString)
  {
    this.mainClass = paramString;
  }
  
  public void setInstanceName(String paramString)
  {
    this.instanceName = paramString;
  }
  
  public UUID getId()
  {
    return this.id;
  }
  
  public String getMainClass()
  {
    return this.mainClass;
  }
  
  public String getJmxServiceUrl()
  {
    return this.jmxServiceUrl;
  }
  
  public String getInstanceName()
  {
    return this.instanceName;
  }
  
  public String getProcessId()
  {
    return this.processId;
  }
  
  public void setProcessId(String paramString)
  {
    this.processId = paramString;
  }
  
  public String getRmiHostname()
  {
    return this.rmiHostname;
  }
  
  public void setRmiHostname(String paramString)
  {
    this.rmiHostname = paramString;
  }
  
  public String getBroadcastInterval()
  {
    return this.broadcastInterval;
  }
  
  public void setBroadcastInterval(String paramString)
  {
    this.broadcastInterval = paramString;
  }
  
  public byte[] getPacketData()
    throws IOException
  {
    JdpPacketWriter localJdpPacketWriter = new JdpPacketWriter();
    localJdpPacketWriter.addEntry("DISCOVERABLE_SESSION_UUID", this.id == null ? null : this.id.toString());
    localJdpPacketWriter.addEntry("MAIN_CLASS", this.mainClass);
    localJdpPacketWriter.addEntry("JMX_SERVICE_URL", this.jmxServiceUrl);
    localJdpPacketWriter.addEntry("INSTANCE_NAME", this.instanceName);
    localJdpPacketWriter.addEntry("PROCESS_ID", this.processId);
    localJdpPacketWriter.addEntry("RMI_HOSTNAME", this.rmiHostname);
    localJdpPacketWriter.addEntry("BROADCAST_INTERVAL", this.broadcastInterval);
    return localJdpPacketWriter.getPacketBytes();
  }
  
  public int hashCode()
  {
    int i = 1;
    i = i * 31 + this.id.hashCode();
    i = i * 31 + this.jmxServiceUrl.hashCode();
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof JdpJmxPacket))) {
      return false;
    }
    JdpJmxPacket localJdpJmxPacket = (JdpJmxPacket)paramObject;
    return (Objects.equals(this.id, localJdpJmxPacket.getId())) && (Objects.equals(this.jmxServiceUrl, localJdpJmxPacket.getJmxServiceUrl()));
  }
}
