package javax.print;

import java.io.IOException;

public abstract interface MultiDoc
{
  public abstract Doc getDoc()
    throws IOException;
  
  public abstract MultiDoc next()
    throws IOException;
}
