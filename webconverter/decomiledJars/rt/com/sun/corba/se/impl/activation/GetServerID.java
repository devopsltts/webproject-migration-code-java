package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class GetServerID
  implements CommandHandler
{
  GetServerID() {}
  
  public String getCommandName()
  {
    return "getserverid";
  }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean)
  {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid1"));
    }
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream)
  {
    if ((paramArrayOfString.length == 2) && (paramArrayOfString[0].equals("-applicationName")))
    {
      String str = paramArrayOfString[1];
      try
      {
        Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
        try
        {
          int i = localRepository.getServerID(str);
          paramPrintStream.println();
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid2", str, Integer.toString(i)));
          paramPrintStream.println();
        }
        catch (ServerNotRegistered localServerNotRegistered)
        {
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return false;
    }
    return true;
  }
}
