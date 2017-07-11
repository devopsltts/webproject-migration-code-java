package sun.security.krb5.internal.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.krb5.internal.ktab.KeyTabEntry;

public class Ktab
{
  KeyTab table;
  char action;
  String name;
  String principal;
  boolean showEType;
  boolean showTime;
  int etype = -1;
  char[] password = null;
  boolean forced = false;
  boolean append = false;
  int vDel = -1;
  int vAdd = -1;
  
  public Ktab() {}
  
  public static void main(String[] paramArrayOfString)
  {
    Ktab localKtab = new Ktab();
    if ((paramArrayOfString.length == 1) && (paramArrayOfString[0].equalsIgnoreCase("-help")))
    {
      localKtab.printHelp();
      return;
    }
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      localKtab.action = 'l';
    } else {
      localKtab.processArgs(paramArrayOfString);
    }
    localKtab.table = KeyTab.getInstance(localKtab.name);
    if ((localKtab.table.isMissing()) && (localKtab.action != 'a'))
    {
      if (localKtab.name == null) {
        System.out.println("No default key table exists.");
      } else {
        System.out.println("Key table " + localKtab.name + " does not exist.");
      }
      System.exit(-1);
    }
    if (!localKtab.table.isValid())
    {
      if (localKtab.name == null) {
        System.out.println("The format of the default key table  is incorrect.");
      } else {
        System.out.println("The format of key table " + localKtab.name + " is incorrect.");
      }
      System.exit(-1);
    }
    switch (localKtab.action)
    {
    case 'l': 
      localKtab.listKt();
      break;
    case 'a': 
      localKtab.addEntry();
      break;
    case 'd': 
      localKtab.deleteEntry();
      break;
    default: 
      localKtab.error(new String[] { "A command must be provided" });
    }
  }
  
  void processArgs(String[] paramArrayOfString)
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfString.length; j++) {
      if (paramArrayOfString[j].startsWith("-"))
      {
        switch (paramArrayOfString[j].toLowerCase(Locale.US))
        {
        case "-l": 
          this.action = 'l';
          break;
        case "-a": 
          this.action = 'a';
          j++;
          if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
            error(new String[] { "A principal name must be specified after -a" });
          }
          this.principal = paramArrayOfString[j];
          break;
        case "-d": 
          this.action = 'd';
          j++;
          if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
            error(new String[] { "A principal name must be specified after -d" });
          }
          this.principal = paramArrayOfString[j];
          break;
        case "-e": 
          if (this.action == 'l')
          {
            this.showEType = true;
          }
          else if (this.action == 'd')
          {
            j++;
            if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
              error(new String[] { "An etype must be specified after -e" });
            }
            try
            {
              this.etype = Integer.parseInt(paramArrayOfString[j]);
              if (this.etype <= 0) {
                throw new NumberFormatException();
              }
            }
            catch (NumberFormatException localNumberFormatException1)
            {
              error(new String[] { paramArrayOfString[j] + " is not a valid etype" });
            }
          }
          else
          {
            error(new String[] { paramArrayOfString[j] + " is not valid after -" + this.action });
          }
          break;
        case "-n": 
          j++;
          if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
            error(new String[] { "A KVNO must be specified after -n" });
          }
          try
          {
            this.vAdd = Integer.parseInt(paramArrayOfString[j]);
            if (this.vAdd < 0) {
              throw new NumberFormatException();
            }
          }
          catch (NumberFormatException localNumberFormatException2)
          {
            error(new String[] { paramArrayOfString[j] + " is not a valid KVNO" });
          }
        case "-k": 
          j++;
          if ((j >= paramArrayOfString.length) || (paramArrayOfString[j].startsWith("-"))) {
            error(new String[] { "A keytab name must be specified after -k" });
          }
          if ((paramArrayOfString[j].length() >= 5) && (paramArrayOfString[j].substring(0, 5).equalsIgnoreCase("FILE:"))) {
            this.name = paramArrayOfString[j].substring(5);
          } else {
            this.name = paramArrayOfString[j];
          }
          break;
        case "-t": 
          this.showTime = true;
          break;
        case "-f": 
          this.forced = true;
          break;
        case "-append": 
          this.append = true;
          break;
        default: 
          error(new String[] { "Unknown command: " + paramArrayOfString[j] });
        }
      }
      else
      {
        if (i != 0) {
          error(new String[] { "Useless extra argument " + paramArrayOfString[j] });
        }
        if (this.action == 'a') {
          this.password = paramArrayOfString[j].toCharArray();
        } else if (this.action == 'd') {
          switch (paramArrayOfString[j])
          {
          case "all": 
            this.vDel = -1;
            break;
          case "old": 
            this.vDel = -2;
            break;
          default: 
            try
            {
              this.vDel = Integer.parseInt(paramArrayOfString[j]);
              if (this.vDel < 0) {
                throw new NumberFormatException();
              }
            }
            catch (NumberFormatException localNumberFormatException3)
            {
              error(new String[] { paramArrayOfString[j] + " is not a valid KVNO" });
            }
          }
        } else {
          error(new String[] { "Useless extra argument " + paramArrayOfString[j] });
        }
        i = 1;
      }
    }
  }
  
  void addEntry()
  {
    PrincipalName localPrincipalName = null;
    try
    {
      localPrincipalName = new PrincipalName(this.principal);
    }
    catch (KrbException localKrbException1)
    {
      System.err.println("Failed to add " + this.principal + " to keytab.");
      localKrbException1.printStackTrace();
      System.exit(-1);
    }
    if (this.password == null) {
      try
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Password for " + localPrincipalName.toString() + ":");
        System.out.flush();
        this.password = localBufferedReader.readLine().toCharArray();
      }
      catch (IOException localIOException1)
      {
        System.err.println("Failed to read the password.");
        localIOException1.printStackTrace();
        System.exit(-1);
      }
    }
    try
    {
      this.table.addEntry(localPrincipalName, this.password, this.vAdd, this.append);
      Arrays.fill(this.password, '0');
      this.table.save();
      System.out.println("Done!");
      System.out.println("Service key for " + this.principal + " is saved in " + this.table.tabName());
    }
    catch (KrbException localKrbException2)
    {
      System.err.println("Failed to add " + this.principal + " to keytab.");
      localKrbException2.printStackTrace();
      System.exit(-1);
    }
    catch (IOException localIOException2)
    {
      System.err.println("Failed to save new entry.");
      localIOException2.printStackTrace();
      System.exit(-1);
    }
  }
  
  void listKt()
  {
    System.out.println("Keytab name: " + this.table.tabName());
    KeyTabEntry[] arrayOfKeyTabEntry = this.table.getEntries();
    if ((arrayOfKeyTabEntry != null) && (arrayOfKeyTabEntry.length > 0))
    {
      String[][] arrayOfString = new String[arrayOfKeyTabEntry.length + 1][this.showTime ? 3 : 2];
      int i = 0;
      arrayOfString[0][(i++)] = "KVNO";
      if (this.showTime) {
        arrayOfString[0][(i++)] = "Timestamp";
      }
      arrayOfString[0][(i++)] = "Principal";
      int m;
      for (int j = 0; j < arrayOfKeyTabEntry.length; j++)
      {
        i = 0;
        arrayOfString[(j + 1)][(i++)] = arrayOfKeyTabEntry[j].getKey().getKeyVersionNumber().toString();
        if (this.showTime) {
          arrayOfString[(j + 1)][(i++)] = DateFormat.getDateTimeInstance(3, 3).format(new Date(arrayOfKeyTabEntry[j].getTimeStamp().getTime()));
        }
        String str = arrayOfKeyTabEntry[j].getService().toString();
        if (this.showEType)
        {
          m = arrayOfKeyTabEntry[j].getKey().getEType();
          arrayOfString[(j + 1)][(i++)] = (str + " (" + m + ":" + EType.toString(m) + ")");
        }
        else
        {
          arrayOfString[(j + 1)][(i++)] = str;
        }
      }
      int[] arrayOfInt = new int[i];
      for (int k = 0; k < i; k++)
      {
        for (m = 0; m <= arrayOfKeyTabEntry.length; m++) {
          if (arrayOfString[m][k].length() > arrayOfInt[k]) {
            arrayOfInt[k] = arrayOfString[m][k].length();
          }
        }
        if (k != 0) {
          arrayOfInt[k] = (-arrayOfInt[k]);
        }
      }
      for (k = 0; k < i; k++) {
        System.out.printf("%" + arrayOfInt[k] + "s ", new Object[] { arrayOfString[0][k] });
      }
      System.out.println();
      for (k = 0; k < i; k++)
      {
        for (m = 0; m < Math.abs(arrayOfInt[k]); m++) {
          System.out.print("-");
        }
        System.out.print(" ");
      }
      System.out.println();
      for (k = 0; k < arrayOfKeyTabEntry.length; k++)
      {
        for (m = 0; m < i; m++) {
          System.out.printf("%" + arrayOfInt[m] + "s ", new Object[] { arrayOfString[(k + 1)][m] });
        }
        System.out.println();
      }
    }
    else
    {
      System.out.println("0 entry.");
    }
  }
  
  void deleteEntry()
  {
    PrincipalName localPrincipalName = null;
    try
    {
      localPrincipalName = new PrincipalName(this.principal);
      if (!this.forced)
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Are you sure you want to delete service key(s) for " + localPrincipalName.toString() + " (" + (this.etype == -1 ? "all etypes" : new StringBuilder().append("etype=").append(this.etype).toString()) + ", " + (this.vDel == -2 ? "old kvno" : this.vDel == -1 ? "all kvno" : new StringBuilder().append("kvno=").append(this.vDel).toString()) + ") in " + this.table.tabName() + "? (Y/[N]): ");
        System.out.flush();
        String str = localBufferedReader.readLine();
        if ((!str.equalsIgnoreCase("Y")) && (!str.equalsIgnoreCase("Yes"))) {
          System.exit(0);
        }
      }
    }
    catch (KrbException localKrbException)
    {
      System.err.println("Error occurred while deleting the entry. Deletion failed.");
      localKrbException.printStackTrace();
      System.exit(-1);
    }
    catch (IOException localIOException1)
    {
      System.err.println("Error occurred while deleting the entry.  Deletion failed.");
      localIOException1.printStackTrace();
      System.exit(-1);
    }
    int i = this.table.deleteEntries(localPrincipalName, this.etype, this.vDel);
    if (i == 0)
    {
      System.err.println("No matched entry in the keytab. Deletion fails.");
      System.exit(-1);
    }
    else
    {
      try
      {
        this.table.save();
      }
      catch (IOException localIOException2)
      {
        System.err.println("Error occurs while saving the keytab. Deletion fails.");
        localIOException2.printStackTrace();
        System.exit(-1);
      }
      System.out.println("Done! " + i + " entries removed.");
    }
  }
  
  void error(String... paramVarArgs)
  {
    for (String str : paramVarArgs) {
      System.out.println("Error: " + str + ".");
    }
    printHelp();
    System.exit(-1);
  }
  
  void printHelp()
  {
    System.out.println("\nUsage: ktab <commands> <options>");
    System.out.println();
    System.out.println("Available commands:");
    System.out.println();
    System.out.println("-l [-e] [-t]\n    list the keytab name and entries. -e with etype, -t with timestamp.");
    System.out.println("-a <principal name> [<password>] [-n <kvno>] [-append]\n    add new key entries to the keytab for the given principal name with\n    optional <password>. If a <kvno> is specified, new keys' Key Version\n    Numbers equal to the value, otherwise, automatically incrementing\n    the Key Version Numbers. If -append is specified, new keys are\n    appended to the keytab, otherwise, old keys for the\n    same principal are removed.");
    System.out.println("-d <principal name> [-f] [-e <etype>] [<kvno> | all | old]\n    delete key entries from the keytab for the specified principal. If\n    <kvno> is specified, delete keys whose Key Version Numbers match\n    kvno. If \"all\" is specified, delete all keys. If \"old\" is specified,\n    delete all keys except those with the highest kvno. Default action\n    is \"all\". If <etype> is specified, only keys of this encryption type\n    are deleted. <etype> should be specified as the numberic value etype\n    defined in RFC 3961, section 8. A prompt to confirm the deletion is\n    displayed unless -f is specified.");
    System.out.println();
    System.out.println("Common option(s):");
    System.out.println();
    System.out.println("-k <keytab name>\n    specify keytab name and path with prefix FILE:");
  }
}
