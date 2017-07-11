package sun.net.www.protocol.http.spnego;

import com.sun.security.jgss.ExtendedGSSContext;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.net.www.protocol.http.HttpCallerInfo;
import sun.net.www.protocol.http.Negotiator;
import sun.security.action.GetBooleanAction;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.HttpCaller;

public class NegotiatorImpl
  extends Negotiator
{
  private static final boolean DEBUG = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.debug"))).booleanValue();
  private GSSContext context;
  private byte[] oneToken;
  
  private void init(HttpCallerInfo paramHttpCallerInfo)
    throws GSSException
  {
    Oid localOid;
    if (paramHttpCallerInfo.scheme.equalsIgnoreCase("Kerberos"))
    {
      localOid = GSSUtil.GSS_KRB5_MECH_OID;
    }
    else
    {
      localObject = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          return System.getProperty("http.auth.preference", "spnego");
        }
      });
      if (((String)localObject).equalsIgnoreCase("kerberos")) {
        localOid = GSSUtil.GSS_KRB5_MECH_OID;
      } else {
        localOid = GSSUtil.GSS_SPNEGO_MECH_OID;
      }
    }
    Object localObject = new GSSManagerImpl(new HttpCaller(paramHttpCallerInfo));
    String str = "HTTP@" + paramHttpCallerInfo.host.toLowerCase();
    GSSName localGSSName = ((GSSManagerImpl)localObject).createName(str, GSSName.NT_HOSTBASED_SERVICE);
    this.context = ((GSSManagerImpl)localObject).createContext(localGSSName, localOid, null, 0);
    if ((this.context instanceof ExtendedGSSContext)) {
      ((ExtendedGSSContext)this.context).requestDelegPolicy(true);
    }
    this.oneToken = this.context.initSecContext(new byte[0], 0, 0);
  }
  
  public NegotiatorImpl(HttpCallerInfo paramHttpCallerInfo)
    throws IOException
  {
    try
    {
      init(paramHttpCallerInfo);
    }
    catch (GSSException localGSSException)
    {
      if (DEBUG)
      {
        System.out.println("Negotiate support not initiated, will fallback to other scheme if allowed. Reason:");
        localGSSException.printStackTrace();
      }
      IOException localIOException = new IOException("Negotiate support not initiated");
      localIOException.initCause(localGSSException);
      throw localIOException;
    }
  }
  
  public byte[] firstToken()
  {
    return this.oneToken;
  }
  
  public byte[] nextToken(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      return this.context.initSecContext(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    catch (GSSException localGSSException)
    {
      if (DEBUG)
      {
        System.out.println("Negotiate support cannot continue. Reason:");
        localGSSException.printStackTrace();
      }
      IOException localIOException = new IOException("Negotiate support cannot continue");
      localIOException.initCause(localGSSException);
      throw localIOException;
    }
  }
}
