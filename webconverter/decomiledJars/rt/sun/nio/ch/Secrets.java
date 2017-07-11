package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public final class Secrets
{
  private Secrets() {}
  
  private static SelectorProvider provider()
  {
    SelectorProvider localSelectorProvider = SelectorProvider.provider();
    if (!(localSelectorProvider instanceof SelectorProviderImpl)) {
      throw new UnsupportedOperationException();
    }
    return localSelectorProvider;
  }
  
  public static SocketChannel newSocketChannel(FileDescriptor paramFileDescriptor)
  {
    try
    {
      return new SocketChannelImpl(provider(), paramFileDescriptor, false);
    }
    catch (IOException localIOException)
    {
      throw new AssertionError(localIOException);
    }
  }
  
  public static ServerSocketChannel newServerSocketChannel(FileDescriptor paramFileDescriptor)
  {
    try
    {
      return new ServerSocketChannelImpl(provider(), paramFileDescriptor, false);
    }
    catch (IOException localIOException)
    {
      throw new AssertionError(localIOException);
    }
  }
}
