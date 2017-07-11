package java.nio.channels;

import java.io.Closeable;
import java.io.IOException;

public abstract interface Channel
  extends Closeable
{
  public abstract boolean isOpen();
  
  public abstract void close()
    throws IOException;
}
