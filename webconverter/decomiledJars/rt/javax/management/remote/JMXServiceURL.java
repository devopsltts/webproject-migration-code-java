package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.StringTokenizer;

public class JMXServiceURL
  implements Serializable
{
  private static final long serialVersionUID = 8173364409860779292L;
  private static final String INVALID_INSTANCE_MSG = "Trying to deserialize an invalid instance of JMXServiceURL";
  private static final Exception randomException = new Exception();
  private static final BitSet alphaBitSet = new BitSet(128);
  private static final BitSet numericBitSet = new BitSet(128);
  private static final BitSet alphaNumericBitSet = new BitSet(128);
  private static final BitSet protocolBitSet = new BitSet(128);
  private static final BitSet hostNameBitSet = new BitSet(128);
  private String protocol;
  private String host;
  private int port;
  private String urlPath;
  private transient String toString;
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXServiceURL");
  
  public JMXServiceURL(String paramString)
    throws MalformedURLException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      k = paramString.charAt(j);
      if ((k < 32) || (k >= 127)) {
        throw new MalformedURLException("Service URL contains non-ASCII character 0x" + Integer.toHexString(k));
      }
    }
    int k = "service:jmx:".length();
    if (!paramString.regionMatches(true, 0, "service:jmx:", 0, k)) {
      throw new MalformedURLException("Service URL must start with service:jmx:");
    }
    int m = k;
    int n = indexOf(paramString, ':', m);
    this.protocol = paramString.substring(m, n).toLowerCase();
    if (!paramString.regionMatches(n, "://", 0, 3)) {
      throw new MalformedURLException("Missing \"://\" after protocol name");
    }
    int i1 = n + 3;
    int i2;
    if ((i1 < i) && (paramString.charAt(i1) == '['))
    {
      i2 = paramString.indexOf(']', i1) + 1;
      if (i2 == 0) {
        throw new MalformedURLException("Bad host name: [ without ]");
      }
      this.host = paramString.substring(i1 + 1, i2 - 1);
      if (!isNumericIPv6Address(this.host)) {
        throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
      }
    }
    else
    {
      i2 = indexOfFirstNotInSet(paramString, hostNameBitSet, i1);
      this.host = paramString.substring(i1, i2);
    }
    int i3;
    if ((i2 < i) && (paramString.charAt(i2) == ':'))
    {
      if (this.host.length() == 0) {
        throw new MalformedURLException("Cannot give port number without host name");
      }
      i4 = i2 + 1;
      i3 = indexOfFirstNotInSet(paramString, numericBitSet, i4);
      String str = paramString.substring(i4, i3);
      try
      {
        this.port = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new MalformedURLException("Bad port number: \"" + str + "\": " + localNumberFormatException);
      }
    }
    else
    {
      i3 = i2;
      this.port = 0;
    }
    int i4 = i3;
    if (i4 < i) {
      this.urlPath = paramString.substring(i4);
    } else {
      this.urlPath = "";
    }
    validate();
  }
  
  public JMXServiceURL(String paramString1, String paramString2, int paramInt)
    throws MalformedURLException
  {
    this(paramString1, paramString2, paramInt, null);
  }
  
  public JMXServiceURL(String paramString1, String paramString2, int paramInt, String paramString3)
    throws MalformedURLException
  {
    if (paramString1 == null) {
      paramString1 = "jmxmp";
    }
    if (paramString2 == null)
    {
      InetAddress localInetAddress;
      try
      {
        localInetAddress = InetAddress.getLocalHost();
      }
      catch (UnknownHostException localUnknownHostException)
      {
        throw new MalformedURLException("Local host name unknown: " + localUnknownHostException);
      }
      paramString2 = localInetAddress.getHostName();
      try
      {
        validateHost(paramString2, paramInt);
      }
      catch (MalformedURLException localMalformedURLException)
      {
        if (logger.fineOn()) {
          logger.fine("JMXServiceURL", "Replacing illegal local host name " + paramString2 + " with numeric IP address " + "(see RFC 1034)", localMalformedURLException);
        }
        paramString2 = localInetAddress.getHostAddress();
      }
    }
    if (paramString2.startsWith("["))
    {
      if (!paramString2.endsWith("]")) {
        throw new MalformedURLException("Host starts with [ but does not end with ]");
      }
      paramString2 = paramString2.substring(1, paramString2.length() - 1);
      if (!isNumericIPv6Address(paramString2)) {
        throw new MalformedURLException("Address inside [...] must be numeric IPv6 address");
      }
      if (paramString2.startsWith("[")) {
        throw new MalformedURLException("More than one [[...]]");
      }
    }
    this.protocol = paramString1.toLowerCase();
    this.host = paramString2;
    this.port = paramInt;
    if (paramString3 == null) {
      paramString3 = "";
    }
    this.urlPath = paramString3;
    validate();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    String str1 = (String)localGetField.get("host", null);
    int i = localGetField.get("port", -1);
    String str2 = (String)localGetField.get("protocol", null);
    String str3 = (String)localGetField.get("urlPath", null);
    if ((str2 == null) || (str3 == null) || (str1 == null))
    {
      StringBuilder localStringBuilder = new StringBuilder("Trying to deserialize an invalid instance of JMXServiceURL").append('[');
      int j = 1;
      if (str2 == null)
      {
        localStringBuilder.append("protocol=null");
        j = 0;
      }
      if (str1 == null)
      {
        localStringBuilder.append(j != 0 ? "" : ",").append("host=null");
        j = 0;
      }
      if (str3 == null) {
        localStringBuilder.append(j != 0 ? "" : ",").append("urlPath=null");
      }
      localStringBuilder.append(']');
      throw new InvalidObjectException(localStringBuilder.toString());
    }
    if ((str1.contains("[")) || (str1.contains("]"))) {
      throw new InvalidObjectException("Invalid host name: " + str1);
    }
    try
    {
      validate(str2, str1, i, str3);
      this.protocol = str2;
      this.host = str1;
      this.port = i;
      this.urlPath = str3;
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new InvalidObjectException("Trying to deserialize an invalid instance of JMXServiceURL: " + localMalformedURLException.getMessage());
    }
  }
  
  private void validate(String paramString1, String paramString2, int paramInt, String paramString3)
    throws MalformedURLException
  {
    int i = indexOfFirstNotInSet(paramString1, protocolBitSet, 0);
    if ((i == 0) || (i < paramString1.length()) || (!alphaBitSet.get(paramString1.charAt(0)))) {
      throw new MalformedURLException("Missing or invalid protocol name: \"" + paramString1 + "\"");
    }
    validateHost(paramString2, paramInt);
    if (paramInt < 0) {
      throw new MalformedURLException("Bad port: " + paramInt);
    }
    if ((paramString3.length() > 0) && (!paramString3.startsWith("/")) && (!paramString3.startsWith(";"))) {
      throw new MalformedURLException("Bad URL path: " + paramString3);
    }
  }
  
  private void validate()
    throws MalformedURLException
  {
    validate(this.protocol, this.host, this.port, this.urlPath);
  }
  
  private static void validateHost(String paramString, int paramInt)
    throws MalformedURLException
  {
    if (paramString.length() == 0)
    {
      if (paramInt != 0) {
        throw new MalformedURLException("Cannot give port number without host name");
      }
      return;
    }
    if (isNumericIPv6Address(paramString))
    {
      try
      {
        InetAddress.getByName(paramString);
      }
      catch (Exception localException1)
      {
        MalformedURLException localMalformedURLException = new MalformedURLException("Bad IPv6 address: " + paramString);
        EnvHelp.initCause(localMalformedURLException, localException1);
        throw localMalformedURLException;
      }
    }
    else
    {
      int i = paramString.length();
      int j = 46;
      int k = 0;
      int m = 0;
      int i1;
      for (int n = 0; n < i; n++)
      {
        i1 = paramString.charAt(n);
        boolean bool = alphaNumericBitSet.get(i1);
        if (j == 46) {
          m = i1;
        }
        if (bool)
        {
          j = 97;
        }
        else if (i1 == 45)
        {
          if (j == 46) {
            break;
          }
          j = 45;
        }
        else if (i1 == 46)
        {
          k = 1;
          if (j != 97) {
            break;
          }
          j = 46;
        }
        else
        {
          j = 46;
          break;
        }
      }
      try
      {
        if (j != 97) {
          throw randomException;
        }
        if ((k != 0) && (!alphaBitSet.get(m)))
        {
          StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ".", true);
          for (i1 = 0; i1 < 4; i1++)
          {
            String str = localStringTokenizer.nextToken();
            int i2 = Integer.parseInt(str);
            if ((i2 < 0) || (i2 > 255)) {
              throw randomException;
            }
            if ((i1 < 3) && (!localStringTokenizer.nextToken().equals("."))) {
              throw randomException;
            }
          }
          if (localStringTokenizer.hasMoreTokens()) {
            throw randomException;
          }
        }
      }
      catch (Exception localException2)
      {
        throw new MalformedURLException("Bad host: \"" + paramString + "\"");
      }
    }
  }
  
  public String getProtocol()
  {
    return this.protocol;
  }
  
  public String getHost()
  {
    return this.host;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public String getURLPath()
  {
    return this.urlPath;
  }
  
  public String toString()
  {
    if (this.toString != null) {
      return this.toString;
    }
    StringBuilder localStringBuilder = new StringBuilder("service:jmx:");
    localStringBuilder.append(getProtocol()).append("://");
    String str = getHost();
    if (isNumericIPv6Address(str)) {
      localStringBuilder.append('[').append(str).append(']');
    } else {
      localStringBuilder.append(str);
    }
    int i = getPort();
    if (i != 0) {
      localStringBuilder.append(':').append(i);
    }
    localStringBuilder.append(getURLPath());
    this.toString = localStringBuilder.toString();
    return this.toString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof JMXServiceURL)) {
      return false;
    }
    JMXServiceURL localJMXServiceURL = (JMXServiceURL)paramObject;
    return (localJMXServiceURL.getProtocol().equalsIgnoreCase(getProtocol())) && (localJMXServiceURL.getHost().equalsIgnoreCase(getHost())) && (localJMXServiceURL.getPort() == getPort()) && (localJMXServiceURL.getURLPath().equals(getURLPath()));
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
  
  private static boolean isNumericIPv6Address(String paramString)
  {
    return paramString.indexOf(':') >= 0;
  }
  
  private static int indexOf(String paramString, char paramChar, int paramInt)
  {
    int i = paramString.indexOf(paramChar, paramInt);
    if (i < 0) {
      return paramString.length();
    }
    return i;
  }
  
  private static int indexOfFirstNotInSet(String paramString, BitSet paramBitSet, int paramInt)
  {
    int i = paramString.length();
    for (int j = paramInt; j < i; j++)
    {
      int k = paramString.charAt(j);
      if ((k >= 128) || (!paramBitSet.get(k))) {
        break;
      }
    }
    return j;
  }
  
  static
  {
    for (int i = 48; i <= 57; i = (char)(i + 1)) {
      numericBitSet.set(i);
    }
    for (i = 65; i <= 90; i = (char)(i + 1)) {
      alphaBitSet.set(i);
    }
    for (i = 97; i <= 122; i = (char)(i + 1)) {
      alphaBitSet.set(i);
    }
    alphaNumericBitSet.or(alphaBitSet);
    alphaNumericBitSet.or(numericBitSet);
    protocolBitSet.or(alphaNumericBitSet);
    protocolBitSet.set(43);
    protocolBitSet.set(45);
    hostNameBitSet.or(alphaNumericBitSet);
    hostNameBitSet.set(45);
    hostNameBitSet.set(46);
  }
}
