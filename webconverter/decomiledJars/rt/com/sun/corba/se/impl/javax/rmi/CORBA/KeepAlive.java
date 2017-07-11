package com.sun.corba.se.impl.javax.rmi.CORBA;

class KeepAlive
  extends Thread
{
  boolean quit = false;
  
  public KeepAlive()
  {
    setDaemon(false);
  }
  
  public synchronized void run()
  {
    while (!this.quit) {
      try
      {
        wait();
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  public synchronized void quit()
  {
    this.quit = true;
    notifyAll();
  }
}
