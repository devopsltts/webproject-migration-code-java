package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class CounterDB
  implements Serializable
{
  private Integer counter;
  private static String counterFileName = "counter";
  private transient File counterFile;
  public static final int rootCounter = 0;
  
  CounterDB(File paramFile)
  {
    counterFileName = "counter";
    this.counterFile = new File(paramFile, counterFileName);
    if (!this.counterFile.exists())
    {
      this.counter = new Integer(0);
      writeCounter();
    }
    else
    {
      readCounter();
    }
  }
  
  private void readCounter()
  {
    try
    {
      FileInputStream localFileInputStream = new FileInputStream(this.counterFile);
      ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
      this.counter = ((Integer)localObjectInputStream.readObject());
      localObjectInputStream.close();
    }
    catch (Exception localException) {}
  }
  
  private void writeCounter()
  {
    try
    {
      this.counterFile.delete();
      FileOutputStream localFileOutputStream = new FileOutputStream(this.counterFile);
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
      localObjectOutputStream.writeObject(this.counter);
      localObjectOutputStream.flush();
      localObjectOutputStream.close();
    }
    catch (Exception localException) {}
  }
  
  public synchronized int getNextCounter()
  {
    int i = this.counter.intValue();
    this.counter = new Integer(++i);
    writeCounter();
    return i;
  }
}
