package com.sun.corba.se.impl.orbutil.concurrent;

public class SyncUtil
{
  private SyncUtil() {}
  
  public static void acquire(Sync paramSync)
  {
    int i = 0;
    while (i == 0) {
      try
      {
        paramSync.acquire();
        i = 1;
      }
      catch (InterruptedException localInterruptedException)
      {
        i = 0;
      }
    }
  }
}
