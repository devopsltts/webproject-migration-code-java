package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class LocateServerForORB
  implements CommandHandler
{
  static final int illegalServerId = -1;
  
  LocateServerForORB() {}
  
  public String getCommandName()
  {
    return "locateperorb";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    int i = -1;
    String str1 = "";
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
        else if ((str2.equals("-orbid")) && (j < paramArrayOfString.length)) {
          str1 = paramArrayOfString[(j++)];
        }
      }
      if (i == -1) {
        return true;
      }
      Locator localLocator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
      ServerLocationPerORB localServerLocationPerORB = localLocator.locateServerForORB(i, str1);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb2", localServerLocationPerORB.hostname));
      int k = localServerLocationPerORB.ports.length;
      for (j = 0; j < k; j++)
      {
        EndPointInfo localEndPointInfo = localServerLocationPerORB.ports[j];
        paramPrintStream.println("\t\t" + localEndPointInfo.port + "\t\t" + localEndPointInfo.endpointType + "\t\t" + str1);
      }
    }
    catch (InvalidORBid localInvalidORBid)
    {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchorb"));
    }
    catch (ServerHeldDown localServerHeldDown)
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
