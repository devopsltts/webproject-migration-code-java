package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

public class ServerProvider
  implements JMXConnectorServerProvider
{
  public ServerProvider() {}
  
  public JMXConnectorServer newJMXConnectorServer(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap, MBeanServer paramMBeanServer)
    throws IOException
  {
    if (!paramJMXServiceURL.getProtocol().equals("iiop")) {
      throw new MalformedURLException("Protocol not iiop: " + paramJMXServiceURL.getProtocol());
    }
    return new RMIConnectorServer(paramJMXServiceURL, paramMap, paramMBeanServer);
  }
}
