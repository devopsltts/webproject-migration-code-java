package sun.rmi.transport.tcp;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.transport.Connection;

class ConnectionAcceptor
  implements Runnable
{
  private TCPTransport transport;
  private List<Connection> queue = new ArrayList();
  private static int threadNum = 0;
  
  public ConnectionAcceptor(TCPTransport paramTCPTransport)
  {
    this.transport = paramTCPTransport;
  }
  
  public void startNewAcceptor()
  {
    Thread localThread = (Thread)AccessController.doPrivileged(new NewThreadAction(this, "Multiplex Accept-" + ++threadNum, true));
    localThread.start();
  }
  
  public void accept(Connection paramConnection)
  {
    synchronized (this.queue)
    {
      this.queue.add(paramConnection);
      this.queue.notify();
    }
  }
  
  public void run()
  {
    Connection localConnection;
    synchronized (this.queue)
    {
      while (this.queue.size() == 0) {
        try
        {
          this.queue.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      startNewAcceptor();
      localConnection = (Connection)this.queue.remove(0);
    }
    this.transport.handleMessages(localConnection, true);
  }
}
