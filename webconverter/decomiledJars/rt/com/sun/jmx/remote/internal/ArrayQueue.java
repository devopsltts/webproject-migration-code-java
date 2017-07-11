package com.sun.jmx.remote.internal;

import java.util.AbstractList;

public class ArrayQueue<T>
  extends AbstractList<T>
{
  private int capacity;
  private T[] queue;
  private int head;
  private int tail;
  
  public ArrayQueue(int paramInt)
  {
    this.capacity = (paramInt + 1);
    this.queue = newArray(paramInt + 1);
    this.head = 0;
    this.tail = 0;
  }
  
  public void resize(int paramInt)
  {
    int i = size();
    if (paramInt < i) {
      throw new IndexOutOfBoundsException("Resizing would lose data");
    }
    paramInt++;
    if (paramInt == this.capacity) {
      return;
    }
    Object[] arrayOfObject = newArray(paramInt);
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = get(j);
    }
    this.capacity = paramInt;
    this.queue = arrayOfObject;
    this.head = 0;
    this.tail = i;
  }
  
  private T[] newArray(int paramInt)
  {
    return (Object[])new Object[paramInt];
  }
  
  public boolean add(T paramT)
  {
    this.queue[this.tail] = paramT;
    int i = (this.tail + 1) % this.capacity;
    if (i == this.head) {
      throw new IndexOutOfBoundsException("Queue full");
    }
    this.tail = i;
    return true;
  }
  
  public T remove(int paramInt)
  {
    if (paramInt != 0) {
      throw new IllegalArgumentException("Can only remove head of queue");
    }
    if (this.head == this.tail) {
      throw new IndexOutOfBoundsException("Queue empty");
    }
    Object localObject = this.queue[this.head];
    this.queue[this.head] = null;
    this.head = ((this.head + 1) % this.capacity);
    return localObject;
  }
  
  public T get(int paramInt)
  {
    int i = size();
    if ((paramInt < 0) || (paramInt >= i))
    {
      String str = "Index " + paramInt + ", queue size " + i;
      throw new IndexOutOfBoundsException(str);
    }
    int j = (this.head + paramInt) % this.capacity;
    return this.queue[j];
  }
  
  public int size()
  {
    int i = this.tail - this.head;
    if (i < 0) {
      i += this.capacity;
    }
    return i;
  }
}
