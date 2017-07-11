package javax.activation;

import java.io.IOException;

public abstract interface CommandObject
{
  public abstract void setCommandContext(String paramString, DataHandler paramDataHandler)
    throws IOException;
}
