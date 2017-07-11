package java.nio.channels;

import java.io.IOException;

public abstract interface InterruptibleChannel
  extends Channel
{
  public abstract void close()
    throws IOException;
}
