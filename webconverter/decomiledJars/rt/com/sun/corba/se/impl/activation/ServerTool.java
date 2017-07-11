package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.omg.CORBA.ORB;

public class ServerTool
{
  static final String helpCommand = "help";
  static final String toolName = "servertool";
  static final String commandArg = "-cmd";
  private static final boolean debug = false;
  ORB orb = null;
  static Vector handlers = new Vector();
  static int maxNameLen;
  
  public ServerTool() {}
  
  static int getServerIdForAlias(ORB paramORB, String paramString)
    throws ServerNotRegistered
  {
    try
    {
      Repository localRepository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      int i = localRepository.getServerID(paramString);
      return localRepository.getServerID(paramString);
    }
    catch (Exception localException)
    {
      throw new ServerNotRegistered();
    }
  }
  
  void run(String[] paramArrayOfString)
  {
    String[] arrayOfString = null;
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-cmd"))
      {
        int j = paramArrayOfString.length - i - 1;
        arrayOfString = new String[j];
        for (int k = 0; k < j; k++) {
          arrayOfString[k] = paramArrayOfString[(++i)];
        }
        break;
      }
    }
    try
    {
      Properties localProperties = System.getProperties();
      localProperties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      this.orb = ORB.init(paramArrayOfString, localProperties);
      if (arrayOfString != null)
      {
        executeCommand(arrayOfString);
      }
      else
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(CorbaResourceUtil.getText("servertool.banner"));
        for (;;)
        {
          arrayOfString = readCommand(localBufferedReader);
          if (arrayOfString != null) {
            executeCommand(arrayOfString);
          } else {
            printAvailableCommands();
          }
        }
      }
    }
    catch (Exception localException)
    {
      System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
      System.out.println();
      localException.printStackTrace();
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    ServerTool localServerTool = new ServerTool();
    localServerTool.run(paramArrayOfString);
  }
  
  String[] readCommand(BufferedReader paramBufferedReader)
  {
    System.out.print("servertool > ");
    try
    {
      int i = 0;
      String[] arrayOfString = null;
      String str = paramBufferedReader.readLine();
      if (str != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str);
        if (localStringTokenizer.countTokens() != 0)
        {
          arrayOfString = new String[localStringTokenizer.countTokens()];
          while (localStringTokenizer.hasMoreTokens()) {
            arrayOfString[(i++)] = localStringTokenizer.nextToken();
          }
        }
      }
      return arrayOfString;
    }
    catch (Exception localException)
    {
      System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
      System.out.println();
      localException.printStackTrace();
    }
    return null;
  }
  
  void printAvailableCommands()
  {
    System.out.println(CorbaResourceUtil.getText("servertool.shorthelp"));
    for (int i = 0; i < handlers.size(); i++)
    {
      CommandHandler localCommandHandler = (CommandHandler)handlers.elementAt(i);
      System.out.print("\t" + localCommandHandler.getCommandName());
      for (int j = localCommandHandler.getCommandName().length(); j < maxNameLen; j++) {
        System.out.print(" ");
      }
      System.out.print(" - ");
      localCommandHandler.printCommandHelp(System.out, true);
    }
    System.out.println();
  }
  
  void executeCommand(String[] paramArrayOfString)
  {
    CommandHandler localCommandHandler;
    if (paramArrayOfString[0].equals("help"))
    {
      if (paramArrayOfString.length == 1) {
        printAvailableCommands();
      } else {
        for (i = 0; i < handlers.size(); i++)
        {
          localCommandHandler = (CommandHandler)handlers.elementAt(i);
          if (localCommandHandler.getCommandName().equals(paramArrayOfString[1])) {
            localCommandHandler.printCommandHelp(System.out, false);
          }
        }
      }
      return;
    }
    for (int i = 0; i < handlers.size(); i++)
    {
      localCommandHandler = (CommandHandler)handlers.elementAt(i);
      if (localCommandHandler.getCommandName().equals(paramArrayOfString[0]))
      {
        String[] arrayOfString = new String[paramArrayOfString.length - 1];
        for (int j = 0; j < arrayOfString.length; j++) {
          arrayOfString[j] = paramArrayOfString[(j + 1)];
        }
        try
        {
          System.out.println();
          boolean bool = localCommandHandler.processCommand(arrayOfString, this.orb, System.out);
          if (bool == true) {
            localCommandHandler.printCommandHelp(System.out, false);
          }
          System.out.println();
        }
        catch (Exception localException) {}
        return;
      }
    }
    printAvailableCommands();
  }
  
  static
  {
    handlers.addElement(new RegisterServer());
    handlers.addElement(new UnRegisterServer());
    handlers.addElement(new GetServerID());
    handlers.addElement(new ListServers());
    handlers.addElement(new ListAliases());
    handlers.addElement(new ListActiveServers());
    handlers.addElement(new LocateServer());
    handlers.addElement(new LocateServerForORB());
    handlers.addElement(new ListORBs());
    handlers.addElement(new ShutdownServer());
    handlers.addElement(new StartServer());
    handlers.addElement(new Help());
    handlers.addElement(new Quit());
    maxNameLen = 0;
    for (int j = 0; j < handlers.size(); j++)
    {
      CommandHandler localCommandHandler = (CommandHandler)handlers.elementAt(j);
      int i = localCommandHandler.getCommandName().length();
      if (i > maxNameLen) {
        maxNameLen = i;
      }
    }
  }
}
