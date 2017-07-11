package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class LocateServer
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  LocateServer() {}
  
  public String getCommandName()
  {
    return "locate";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    int i = -1;
    String str1 = "IIOP_CLEAR_TEXT";
    try
    {
      int j = 0;
      while (j < paramArrayOfString.length)
      {
        String str2 = paramArrayOfString[(j++)];
        if (str2.equals("-serverid"))
        {
          if (j < paramArrayOfString.length) {
            i = Integer.valueOf(paramArrayOfString[(j++)]).intValue();
          } else {
            return true;
          }
        }
        else if (str2.equals("-applicationName"))
        {
          if (j < paramArrayOfString.length) {
            i = ServerTool.getServerIdForAlias(paramORB, paramArrayOfString[(j++)]);
          } else {
            return true;
          }
        }
        else if ((str2.equals("-endpointType")) && (j < paramArrayOfString.length)) {
          str1 = paramArrayOfString[(j++)];
        }
      }
      if (i == -1) {
        return true;
      }
      Locator localLocator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
      ServerLocation localServerLocation = localLocator.locateServer(i, str1);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate2", localServerLocation.hostname));
      int k = localServerLocation.ports.length;
      for (j = 0; j < k; j++)
      {
        ORBPortInfo localORBPortInfo = localServerLocation.ports[j];
        paramPrintStream.println("\t\t" + localORBPortInfo.port + "\t\t" + str1 + "\t\t" + localORBPortInfo.orbId);
      }
    }
    catch (NoSuchEndPoint localNoSuchEndPoint) {}catch (ServerHeldDown localServerHeldDown)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.helddown"));
    }
    catch (ServerNotRegistered localServerNotRegistered)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return false;
  }
}
