package sun.net.www.protocol.http.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpLogFormatter
  extends SimpleFormatter
{
  private static volatile Pattern pattern = null;
  private static volatile Pattern cpattern = null;
  
  public HttpLogFormatter()
  {
    if (pattern == null)
    {
      pattern = Pattern.compile("\\{[^\\}]*\\}");
      cpattern = Pattern.compile("[^,\\] ]{2,}");
    }
  }
  
  public String format(LogRecord paramLogRecord)
  {
    String str1 = paramLogRecord.getSourceClassName();
    if ((str1 == null) || ((!str1.startsWith("sun.net.www.protocol.http")) && (!str1.startsWith("sun.net.www.http")))) {
      return super.format(paramLogRecord);
    }
    String str2 = paramLogRecord.getMessage();
    StringBuilder localStringBuilder = new StringBuilder("HTTP: ");
    Object localObject1;
    int j;
    Object localObject2;
    if (str2.startsWith("sun.net.www.MessageHeader@"))
    {
      localObject1 = pattern.matcher(str2);
      while (((Matcher)localObject1).find())
      {
        int i = ((Matcher)localObject1).start();
        j = ((Matcher)localObject1).end();
        localObject2 = str2.substring(i + 1, j - 1);
        if (((String)localObject2).startsWith("null: ")) {
          localObject2 = ((String)localObject2).substring(6);
        }
        if (((String)localObject2).endsWith(": null")) {
          localObject2 = ((String)localObject2).substring(0, ((String)localObject2).length() - 6);
        }
        localStringBuilder.append("\t").append((String)localObject2).append("\n");
      }
    }
    else if (str2.startsWith("Cookies retrieved: {"))
    {
      localObject1 = str2.substring(20);
      localStringBuilder.append("Cookies from handler:\n");
      while (((String)localObject1).length() >= 7)
      {
        String str3;
        int k;
        int m;
        String str4;
        if (((String)localObject1).startsWith("Cookie=["))
        {
          str3 = ((String)localObject1).substring(8);
          j = str3.indexOf("Cookie2=[");
          if (j > 0)
          {
            str3 = str3.substring(0, j - 1);
            localObject1 = str3.substring(j);
          }
          else
          {
            localObject1 = "";
          }
          if (str3.length() >= 4)
          {
            localObject2 = cpattern.matcher(str3);
            while (((Matcher)localObject2).find())
            {
              k = ((Matcher)localObject2).start();
              m = ((Matcher)localObject2).end();
              if (k >= 0)
              {
                str4 = str3.substring(k + 1, m > 0 ? m - 1 : str3.length() - 1);
                localStringBuilder.append("\t").append(str4).append("\n");
              }
            }
          }
        }
        else if (((String)localObject1).startsWith("Cookie2=["))
        {
          str3 = ((String)localObject1).substring(9);
          j = str3.indexOf("Cookie=[");
          if (j > 0)
          {
            str3 = str3.substring(0, j - 1);
            localObject1 = str3.substring(j);
          }
          else
          {
            localObject1 = "";
          }
          localObject2 = cpattern.matcher(str3);
          while (((Matcher)localObject2).find())
          {
            k = ((Matcher)localObject2).start();
            m = ((Matcher)localObject2).end();
            if (k >= 0)
            {
              str4 = str3.substring(k + 1, m > 0 ? m - 1 : str3.length() - 1);
              localStringBuilder.append("\t").append(str4).append("\n");
            }
          }
        }
      }
    }
    else
    {
      localStringBuilder.append(str2).append("\n");
    }
    return localStringBuilder.toString();
  }
}
