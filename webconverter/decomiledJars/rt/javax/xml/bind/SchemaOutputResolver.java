package javax.xml.bind;

import java.io.IOException;
import javax.xml.transform.Result;

public abstract class SchemaOutputResolver
{
  public SchemaOutputResolver() {}
  
  public abstract Result createOutput(String paramString1, String paramString2)
    throws IOException;
}
