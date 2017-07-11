package com.sun.corba.se.spi.orbutil.threadpool;

public abstract interface WorkQueue
{
  public abstract void addWork(Work paramWork);
  
  public abstract String getName();
  
  public abstract long totalWorkItemsAdded();
  
  public abstract int workItemsInQueue();
  
  public abstract long averageTimeInQueue();
  
  public abstract void setThreadPool(ThreadPool paramThreadPool);
  
  public abstract ThreadPool getThreadPool();
}
