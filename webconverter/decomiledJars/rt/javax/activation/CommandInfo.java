package javax.activation;

import java.beans.Beans;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class CommandInfo
{
  private String verb;
  private String className;
  
  public CommandInfo(String paramString1, String paramString2)
  {
    this.verb = paramString1;
    this.className = paramString2;
  }
  
  public String getCommandName()
  {
    return this.verb;
  }
  
  public String getCommandClass()
  {
    return this.className;
  }
  
  public Object getCommandObject(DataHandler paramDataHandler, ClassLoader paramClassLoader)
    throws IOException, ClassNotFoundException
  {
    Object localObject = null;
    localObject = Beans.instantiate(paramClassLoader, this.className);
    if (localObject != null) {
      if ((localObject instanceof CommandObject))
      {
        ((CommandObject)localObject).setCommandContext(this.verb, paramDataHandler);
      }
      else if (((localObject instanceof Externalizable)) && (paramDataHandler != null))
      {
        InputStream localInputStream = paramDataHandler.getInputStream();
        if (localInputStream != null) {
          ((Externalizable)localObject).readExternal(new ObjectInputStream(localInputStream));
        }
      }
    }
    return localObject;
  }
}
