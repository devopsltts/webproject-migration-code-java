package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedSynchronizer
  extends AbstractOwnableSynchronizer
  implements Serializable
{
  private static final long serialVersionUID = 7373984972572414691L;
  private volatile transient Node head;
  private volatile transient Node tail;
  private volatile int state;
  static final long spinForTimeoutThreshold = 1000L;
  private static final Unsafe unsafe = ;
  private static final long stateOffset;
  private static final long headOffset;
  private static final long tailOffset;
  private static final long waitStatusOffset;
  private static final long nextOffset;
  
  protected AbstractQueuedSynchronizer() {}
  
  protected final int getState()
  {
    return this.state;
  }
  
  protected final void setState(int paramInt)
  {
    this.state = paramInt;
  }
  
  protected final boolean compareAndSetState(int paramInt1, int paramInt2)
  {
    return unsafe.compareAndSwapInt(this, stateOffset, paramInt1, paramInt2);
  }
  
  private Node enq(Node paramNode)
  {
    for (;;)
    {
      Node localNode = this.tail;
      if (localNode == null)
      {
        if (compareAndSetHead(new Node())) {
          this.tail = this.head;
        }
      }
      else
      {
        paramNode.prev = localNode;
        if (compareAndSetTail(localNode, paramNode))
        {
          localNode.next = paramNode;
          return localNode;
        }
      }
    }
  }
  
  private Node addWaiter(Node paramNode)
  {
    Node localNode1 = new Node(Thread.currentThread(), paramNode);
    Node localNode2 = this.tail;
    if (localNode2 != null)
    {
      localNode1.prev = localNode2;
      if (compareAndSetTail(localNode2, localNode1))
      {
        localNode2.next = localNode1;
        return localNode1;
      }
    }
    enq(localNode1);
    return localNode1;
  }
  
  private void setHead(Node paramNode)
  {
    this.head = paramNode;
    paramNode.thread = null;
    paramNode.prev = null;
  }
  
  private void unparkSuccessor(Node paramNode)
  {
    int i = paramNode.waitStatus;
    if (i < 0) {
      compareAndSetWaitStatus(paramNode, i, 0);
    }
    Object localObject = paramNode.next;
    if ((localObject == null) || (((Node)localObject).waitStatus > 0))
    {
      localObject = null;
      for (Node localNode = this.tail; (localNode != null) && (localNode != paramNode); localNode = localNode.prev) {
        if (localNode.waitStatus <= 0) {
          localObject = localNode;
        }
      }
    }
    if (localObject != null) {
      LockSupport.unpark(((Node)localObject).thread);
    }
  }
  
  private void doReleaseShared()
  {
    for (;;)
    {
      Node localNode = this.head;
      if ((localNode != null) && (localNode != this.tail))
      {
        int i = localNode.waitStatus;
        if (i == -1)
        {
          if (!compareAndSetWaitStatus(localNode, -1, 0)) {
            continue;
          }
          unparkSuccessor(localNode);
        }
        else
        {
          if ((i == 0) && (!compareAndSetWaitStatus(localNode, 0, -3))) {
            continue;
          }
        }
      }
      if (localNode == this.head) {
        break;
      }
    }
  }
  
  private void setHeadAndPropagate(Node paramNode, int paramInt)
  {
    Node localNode1 = this.head;
    setHead(paramNode);
    if ((paramInt > 0) || (localNode1 == null) || (localNode1.waitStatus < 0) || ((localNode1 = this.head) == null) || (localNode1.waitStatus < 0))
    {
      Node localNode2 = paramNode.next;
      if ((localNode2 == null) || (localNode2.isShared())) {
        doReleaseShared();
      }
    }
  }
  
  private void cancelAcquire(Node paramNode)
  {
    if (paramNode == null) {
      return;
    }
    paramNode.thread = null;
    Node localNode1 = paramNode.prev;
    while (localNode1.waitStatus > 0) {
      paramNode.prev = (localNode1 = localNode1.prev);
    }
    Node localNode2 = localNode1.next;
    paramNode.waitStatus = 1;
    if ((paramNode == this.tail) && (compareAndSetTail(paramNode, localNode1)))
    {
      compareAndSetNext(localNode1, localNode2, null);
    }
    else
    {
      int i;
      if ((localNode1 != this.head) && (((i = localNode1.waitStatus) == -1) || ((i <= 0) && (compareAndSetWaitStatus(localNode1, i, -1)))) && (localNode1.thread != null))
      {
        Node localNode3 = paramNode.next;
        if ((localNode3 != null) && (localNode3.waitStatus <= 0)) {
          compareAndSetNext(localNode1, localNode2, localNode3);
        }
      }
      else
      {
        unparkSuccessor(paramNode);
      }
      paramNode.next = paramNode;
    }
  }
  
  private static boolean shouldParkAfterFailedAcquire(Node paramNode1, Node paramNode2)
  {
    int i = paramNode1.waitStatus;
    if (i == -1) {
      return true;
    }
    if (i > 0)
    {
      do
      {
        paramNode2.prev = (paramNode1 = paramNode1.prev);
      } while (paramNode1.waitStatus > 0);
      paramNode1.next = paramNode2;
    }
    else
    {
      compareAndSetWaitStatus(paramNode1, i, -1);
    }
    return false;
  }
  
  static void selfInterrupt()
  {
    Thread.currentThread().interrupt();
  }
  
  private final boolean parkAndCheckInterrupt()
  {
    LockSupport.park(this);
    return Thread.interrupted();
  }
  
  /* Error */
  final boolean acquireQueued(Node paramNode, int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: iconst_0
    //   3: istore 4
    //   5: aload_1
    //   6: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   9: astore 5
    //   11: aload 5
    //   13: aload_0
    //   14: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   17: if_acmpne +40 -> 57
    //   20: aload_0
    //   21: iload_2
    //   22: invokevirtual 333	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquire	(I)Z
    //   25: ifeq +32 -> 57
    //   28: aload_0
    //   29: aload_1
    //   30: invokespecial 341	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   33: aload 5
    //   35: aconst_null
    //   36: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   39: iconst_0
    //   40: istore_3
    //   41: iload 4
    //   43: istore 6
    //   45: iload_3
    //   46: ifeq +8 -> 54
    //   49: aload_0
    //   50: aload_1
    //   51: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   54: iload 6
    //   56: ireturn
    //   57: aload 5
    //   59: aload_1
    //   60: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   63: ifeq +13 -> 76
    //   66: aload_0
    //   67: invokespecial 327	java/util/concurrent/locks/AbstractQueuedSynchronizer:parkAndCheckInterrupt	()Z
    //   70: ifeq +6 -> 76
    //   73: iconst_1
    //   74: istore 4
    //   76: goto -71 -> 5
    //   79: astore 7
    //   81: iload_3
    //   82: ifeq +8 -> 90
    //   85: aload_0
    //   86: aload_1
    //   87: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   90: aload 7
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	AbstractQueuedSynchronizer
    //   0	93	1	paramNode	Node
    //   0	93	2	paramInt	int
    //   1	81	3	i	int
    //   3	72	4	bool1	boolean
    //   9	49	5	localNode	Node
    //   43	12	6	bool2	boolean
    //   79	12	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	45	79	finally
    //   57	81	79	finally
  }
  
  /* Error */
  private void doAcquireInterruptibly(int paramInt)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 299	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:EXCLUSIVE	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   4: invokespecial 349	java/util/concurrent/locks/AbstractQueuedSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   7: astore_2
    //   8: iconst_1
    //   9: istore_3
    //   10: aload_2
    //   11: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   14: astore 4
    //   16: aload 4
    //   18: aload_0
    //   19: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   22: if_acmpne +34 -> 56
    //   25: aload_0
    //   26: iload_1
    //   27: invokevirtual 333	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquire	(I)Z
    //   30: ifeq +26 -> 56
    //   33: aload_0
    //   34: aload_2
    //   35: invokespecial 341	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   38: aload 4
    //   40: aconst_null
    //   41: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   44: iconst_0
    //   45: istore_3
    //   46: iload_3
    //   47: ifeq +8 -> 55
    //   50: aload_0
    //   51: aload_2
    //   52: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   55: return
    //   56: aload 4
    //   58: aload_2
    //   59: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   62: ifeq +18 -> 80
    //   65: aload_0
    //   66: invokespecial 327	java/util/concurrent/locks/AbstractQueuedSynchronizer:parkAndCheckInterrupt	()Z
    //   69: ifeq +11 -> 80
    //   72: new 168	java/lang/InterruptedException
    //   75: dup
    //   76: invokespecial 307	java/lang/InterruptedException:<init>	()V
    //   79: athrow
    //   80: goto -70 -> 10
    //   83: astore 5
    //   85: iload_3
    //   86: ifeq +8 -> 94
    //   89: aload_0
    //   90: aload_2
    //   91: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   94: aload 5
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	AbstractQueuedSynchronizer
    //   0	97	1	paramInt	int
    //   7	84	2	localNode1	Node
    //   9	77	3	i	int
    //   14	43	4	localNode2	Node
    //   83	12	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	46	83	finally
    //   56	85	83	finally
  }
  
  /* Error */
  private boolean doAcquireNanos(int paramInt, long paramLong)
    throws InterruptedException
  {
    // Byte code:
    //   0: lload_2
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt +5 -> 8
    //   6: iconst_0
    //   7: ireturn
    //   8: invokestatic 314	java/lang/System:nanoTime	()J
    //   11: lload_2
    //   12: ladd
    //   13: lstore 4
    //   15: aload_0
    //   16: getstatic 299	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:EXCLUSIVE	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   19: invokespecial 349	java/util/concurrent/locks/AbstractQueuedSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   22: astore 6
    //   24: iconst_1
    //   25: istore 7
    //   27: aload 6
    //   29: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   32: astore 8
    //   34: aload 8
    //   36: aload_0
    //   37: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   40: if_acmpne +43 -> 83
    //   43: aload_0
    //   44: iload_1
    //   45: invokevirtual 333	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquire	(I)Z
    //   48: ifeq +35 -> 83
    //   51: aload_0
    //   52: aload 6
    //   54: invokespecial 341	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   57: aload 8
    //   59: aconst_null
    //   60: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   63: iconst_0
    //   64: istore 7
    //   66: iconst_1
    //   67: istore 9
    //   69: iload 7
    //   71: ifeq +9 -> 80
    //   74: aload_0
    //   75: aload 6
    //   77: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   80: iload 9
    //   82: ireturn
    //   83: lload 4
    //   85: invokestatic 314	java/lang/System:nanoTime	()J
    //   88: lsub
    //   89: lstore_2
    //   90: lload_2
    //   91: lconst_0
    //   92: lcmp
    //   93: ifgt +20 -> 113
    //   96: iconst_0
    //   97: istore 9
    //   99: iload 7
    //   101: ifeq +9 -> 110
    //   104: aload_0
    //   105: aload 6
    //   107: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   110: iload 9
    //   112: ireturn
    //   113: aload 8
    //   115: aload 6
    //   117: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   120: ifeq +16 -> 136
    //   123: lload_2
    //   124: ldc2_w 158
    //   127: lcmp
    //   128: ifle +8 -> 136
    //   131: aload_0
    //   132: lload_2
    //   133: invokestatic 363	java/util/concurrent/locks/LockSupport:parkNanos	(Ljava/lang/Object;J)V
    //   136: invokestatic 317	java/lang/Thread:interrupted	()Z
    //   139: ifeq +11 -> 150
    //   142: new 168	java/lang/InterruptedException
    //   145: dup
    //   146: invokespecial 307	java/lang/InterruptedException:<init>	()V
    //   149: athrow
    //   150: goto -123 -> 27
    //   153: astore 10
    //   155: iload 7
    //   157: ifeq +9 -> 166
    //   160: aload_0
    //   161: aload 6
    //   163: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   166: aload 10
    //   168: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	AbstractQueuedSynchronizer
    //   0	169	1	paramInt	int
    //   0	169	2	paramLong	long
    //   13	71	4	l	long
    //   22	140	6	localNode1	Node
    //   25	131	7	i	int
    //   32	82	8	localNode2	Node
    //   67	44	9	bool	boolean
    //   153	14	10	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	69	153	finally
    //   83	99	153	finally
    //   113	155	153	finally
  }
  
  /* Error */
  private void doAcquireShared(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 300	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   4: invokespecial 349	java/util/concurrent/locks/AbstractQueuedSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   7: astore_2
    //   8: iconst_1
    //   9: istore_3
    //   10: iconst_0
    //   11: istore 4
    //   13: aload_2
    //   14: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   17: astore 5
    //   19: aload 5
    //   21: aload_0
    //   22: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   25: if_acmpne +48 -> 73
    //   28: aload_0
    //   29: iload_1
    //   30: invokevirtual 328	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquireShared	(I)I
    //   33: istore 6
    //   35: iload 6
    //   37: iflt +36 -> 73
    //   40: aload_0
    //   41: aload_2
    //   42: iload 6
    //   44: invokespecial 346	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;I)V
    //   47: aload 5
    //   49: aconst_null
    //   50: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   53: iload 4
    //   55: ifeq +6 -> 61
    //   58: invokestatic 325	java/util/concurrent/locks/AbstractQueuedSynchronizer:selfInterrupt	()V
    //   61: iconst_0
    //   62: istore_3
    //   63: iload_3
    //   64: ifeq +8 -> 72
    //   67: aload_0
    //   68: aload_2
    //   69: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   72: return
    //   73: aload 5
    //   75: aload_2
    //   76: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   79: ifeq +13 -> 92
    //   82: aload_0
    //   83: invokespecial 327	java/util/concurrent/locks/AbstractQueuedSynchronizer:parkAndCheckInterrupt	()Z
    //   86: ifeq +6 -> 92
    //   89: iconst_1
    //   90: istore 4
    //   92: goto -79 -> 13
    //   95: astore 7
    //   97: iload_3
    //   98: ifeq +8 -> 106
    //   101: aload_0
    //   102: aload_2
    //   103: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   106: aload 7
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	AbstractQueuedSynchronizer
    //   0	109	1	paramInt	int
    //   7	96	2	localNode1	Node
    //   9	89	3	i	int
    //   11	80	4	j	int
    //   17	57	5	localNode2	Node
    //   33	10	6	k	int
    //   95	12	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	63	95	finally
    //   73	97	95	finally
  }
  
  /* Error */
  private void doAcquireSharedInterruptibly(int paramInt)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 300	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   4: invokespecial 349	java/util/concurrent/locks/AbstractQueuedSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   7: astore_2
    //   8: iconst_1
    //   9: istore_3
    //   10: aload_2
    //   11: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   14: astore 4
    //   16: aload 4
    //   18: aload_0
    //   19: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   22: if_acmpne +40 -> 62
    //   25: aload_0
    //   26: iload_1
    //   27: invokevirtual 328	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquireShared	(I)I
    //   30: istore 5
    //   32: iload 5
    //   34: iflt +28 -> 62
    //   37: aload_0
    //   38: aload_2
    //   39: iload 5
    //   41: invokespecial 346	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;I)V
    //   44: aload 4
    //   46: aconst_null
    //   47: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   50: iconst_0
    //   51: istore_3
    //   52: iload_3
    //   53: ifeq +8 -> 61
    //   56: aload_0
    //   57: aload_2
    //   58: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   61: return
    //   62: aload 4
    //   64: aload_2
    //   65: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   68: ifeq +18 -> 86
    //   71: aload_0
    //   72: invokespecial 327	java/util/concurrent/locks/AbstractQueuedSynchronizer:parkAndCheckInterrupt	()Z
    //   75: ifeq +11 -> 86
    //   78: new 168	java/lang/InterruptedException
    //   81: dup
    //   82: invokespecial 307	java/lang/InterruptedException:<init>	()V
    //   85: athrow
    //   86: goto -76 -> 10
    //   89: astore 6
    //   91: iload_3
    //   92: ifeq +8 -> 100
    //   95: aload_0
    //   96: aload_2
    //   97: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   100: aload 6
    //   102: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	103	0	this	AbstractQueuedSynchronizer
    //   0	103	1	paramInt	int
    //   7	90	2	localNode1	Node
    //   9	83	3	i	int
    //   14	49	4	localNode2	Node
    //   30	10	5	j	int
    //   89	12	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	52	89	finally
    //   62	91	89	finally
  }
  
  /* Error */
  private boolean doAcquireSharedNanos(int paramInt, long paramLong)
    throws InterruptedException
  {
    // Byte code:
    //   0: lload_2
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt +5 -> 8
    //   6: iconst_0
    //   7: ireturn
    //   8: invokestatic 314	java/lang/System:nanoTime	()J
    //   11: lload_2
    //   12: ladd
    //   13: lstore 4
    //   15: aload_0
    //   16: getstatic 300	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   19: invokespecial 349	java/util/concurrent/locks/AbstractQueuedSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   22: astore 6
    //   24: iconst_1
    //   25: istore 7
    //   27: aload 6
    //   29: invokevirtual 360	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   32: astore 8
    //   34: aload 8
    //   36: aload_0
    //   37: getfield 294	java/util/concurrent/locks/AbstractQueuedSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   40: if_acmpne +49 -> 89
    //   43: aload_0
    //   44: iload_1
    //   45: invokevirtual 328	java/util/concurrent/locks/AbstractQueuedSynchronizer:tryAcquireShared	(I)I
    //   48: istore 9
    //   50: iload 9
    //   52: iflt +37 -> 89
    //   55: aload_0
    //   56: aload 6
    //   58: iload 9
    //   60: invokespecial 346	java/util/concurrent/locks/AbstractQueuedSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;I)V
    //   63: aload 8
    //   65: aconst_null
    //   66: putfield 301	java/util/concurrent/locks/AbstractQueuedSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;
    //   69: iconst_0
    //   70: istore 7
    //   72: iconst_1
    //   73: istore 10
    //   75: iload 7
    //   77: ifeq +9 -> 86
    //   80: aload_0
    //   81: aload 6
    //   83: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   86: iload 10
    //   88: ireturn
    //   89: lload 4
    //   91: invokestatic 314	java/lang/System:nanoTime	()J
    //   94: lsub
    //   95: lstore_2
    //   96: lload_2
    //   97: lconst_0
    //   98: lcmp
    //   99: ifgt +20 -> 119
    //   102: iconst_0
    //   103: istore 9
    //   105: iload 7
    //   107: ifeq +9 -> 116
    //   110: aload_0
    //   111: aload 6
    //   113: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   116: iload 9
    //   118: ireturn
    //   119: aload 8
    //   121: aload 6
    //   123: invokestatic 352	java/util/concurrent/locks/AbstractQueuedSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)Z
    //   126: ifeq +16 -> 142
    //   129: lload_2
    //   130: ldc2_w 158
    //   133: lcmp
    //   134: ifle +8 -> 142
    //   137: aload_0
    //   138: lload_2
    //   139: invokestatic 363	java/util/concurrent/locks/LockSupport:parkNanos	(Ljava/lang/Object;J)V
    //   142: invokestatic 317	java/lang/Thread:interrupted	()Z
    //   145: ifeq +11 -> 156
    //   148: new 168	java/lang/InterruptedException
    //   151: dup
    //   152: invokespecial 307	java/lang/InterruptedException:<init>	()V
    //   155: athrow
    //   156: goto -129 -> 27
    //   159: astore 11
    //   161: iload 7
    //   163: ifeq +9 -> 172
    //   166: aload_0
    //   167: aload 6
    //   169: invokespecial 340	java/util/concurrent/locks/AbstractQueuedSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedSynchronizer$Node;)V
    //   172: aload 11
    //   174: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	175	0	this	AbstractQueuedSynchronizer
    //   0	175	1	paramInt	int
    //   0	175	2	paramLong	long
    //   13	77	4	l	long
    //   22	146	6	localNode1	Node
    //   25	137	7	i	int
    //   32	88	8	localNode2	Node
    //   48	69	9	j	int
    //   73	14	10	bool	boolean
    //   159	14	11	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	75	159	finally
    //   89	105	159	finally
    //   119	161	159	finally
  }
  
  protected boolean tryAcquire(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean tryRelease(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  protected int tryAcquireShared(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean tryReleaseShared(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean isHeldExclusively()
  {
    throw new UnsupportedOperationException();
  }
  
  public final void acquire(int paramInt)
  {
    if ((!tryAcquire(paramInt)) && (acquireQueued(addWaiter(Node.EXCLUSIVE), paramInt))) {
      selfInterrupt();
    }
  }
  
  public final void acquireInterruptibly(int paramInt)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (!tryAcquire(paramInt)) {
      doAcquireInterruptibly(paramInt);
    }
  }
  
  public final boolean tryAcquireNanos(int paramInt, long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return (tryAcquire(paramInt)) || (doAcquireNanos(paramInt, paramLong));
  }
  
  public final boolean release(int paramInt)
  {
    if (tryRelease(paramInt))
    {
      Node localNode = this.head;
      if ((localNode != null) && (localNode.waitStatus != 0)) {
        unparkSuccessor(localNode);
      }
      return true;
    }
    return false;
  }
  
  public final void acquireShared(int paramInt)
  {
    if (tryAcquireShared(paramInt) < 0) {
      doAcquireShared(paramInt);
    }
  }
  
  public final void acquireSharedInterruptibly(int paramInt)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (tryAcquireShared(paramInt) < 0) {
      doAcquireSharedInterruptibly(paramInt);
    }
  }
  
  public final boolean tryAcquireSharedNanos(int paramInt, long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return (tryAcquireShared(paramInt) >= 0) || (doAcquireSharedNanos(paramInt, paramLong));
  }
  
  public final boolean releaseShared(int paramInt)
  {
    if (tryReleaseShared(paramInt))
    {
      doReleaseShared();
      return true;
    }
    return false;
  }
  
  public final boolean hasQueuedThreads()
  {
    return this.head != this.tail;
  }
  
  public final boolean hasContended()
  {
    return this.head != null;
  }
  
  public final Thread getFirstQueuedThread()
  {
    return this.head == this.tail ? null : fullGetFirstQueuedThread();
  }
  
  private Thread fullGetFirstQueuedThread()
  {
    Node localNode1;
    Node localNode2;
    Thread localThread1;
    if ((((localNode1 = this.head) != null) && ((localNode2 = localNode1.next) != null) && (localNode2.prev == this.head) && ((localThread1 = localNode2.thread) != null)) || (((localNode1 = this.head) != null) && ((localNode2 = localNode1.next) != null) && (localNode2.prev == this.head) && ((localThread1 = localNode2.thread) != null))) {
      return localThread1;
    }
    Node localNode3 = this.tail;
    Object localObject = null;
    while ((localNode3 != null) && (localNode3 != this.head))
    {
      Thread localThread2 = localNode3.thread;
      if (localThread2 != null) {
        localObject = localThread2;
      }
      localNode3 = localNode3.prev;
    }
    return localObject;
  }
  
  public final boolean isQueued(Thread paramThread)
  {
    if (paramThread == null) {
      throw new NullPointerException();
    }
    for (Node localNode = this.tail; localNode != null; localNode = localNode.prev) {
      if (localNode.thread == paramThread) {
        return true;
      }
    }
    return false;
  }
  
  final boolean apparentlyFirstQueuedIsExclusive()
  {
    Node localNode1;
    Node localNode2;
    return ((localNode1 = this.head) != null) && ((localNode2 = localNode1.next) != null) && (!localNode2.isShared()) && (localNode2.thread != null);
  }
  
  public final boolean hasQueuedPredecessors()
  {
    Node localNode1 = this.tail;
    Node localNode2 = this.head;
    Node localNode3;
    return (localNode2 != localNode1) && (((localNode3 = localNode2.next) == null) || (localNode3.thread != Thread.currentThread()));
  }
  
  public final int getQueueLength()
  {
    int i = 0;
    for (Node localNode = this.tail; localNode != null; localNode = localNode.prev) {
      if (localNode.thread != null) {
        i++;
      }
    }
    return i;
  }
  
  public final Collection<Thread> getQueuedThreads()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = this.tail; localNode != null; localNode = localNode.prev)
    {
      Thread localThread = localNode.thread;
      if (localThread != null) {
        localArrayList.add(localThread);
      }
    }
    return localArrayList;
  }
  
  public final Collection<Thread> getExclusiveQueuedThreads()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = this.tail; localNode != null; localNode = localNode.prev) {
      if (!localNode.isShared())
      {
        Thread localThread = localNode.thread;
        if (localThread != null) {
          localArrayList.add(localThread);
        }
      }
    }
    return localArrayList;
  }
  
  public final Collection<Thread> getSharedQueuedThreads()
  {
    ArrayList localArrayList = new ArrayList();
    for (Node localNode = this.tail; localNode != null; localNode = localNode.prev) {
      if (localNode.isShared())
      {
        Thread localThread = localNode.thread;
        if (localThread != null) {
          localArrayList.add(localThread);
        }
      }
    }
    return localArrayList;
  }
  
  public String toString()
  {
    int i = getState();
    String str = hasQueuedThreads() ? "non" : "";
    return super.toString() + "[State = " + i + ", " + str + "empty queue]";
  }
  
  final boolean isOnSyncQueue(Node paramNode)
  {
    if ((paramNode.waitStatus == -2) || (paramNode.prev == null)) {
      return false;
    }
    if (paramNode.next != null) {
      return true;
    }
    return findNodeFromTail(paramNode);
  }
  
  private boolean findNodeFromTail(Node paramNode)
  {
    for (Node localNode = this.tail;; localNode = localNode.prev)
    {
      if (localNode == paramNode) {
        return true;
      }
      if (localNode == null) {
        return false;
      }
    }
  }
  
  final boolean transferForSignal(Node paramNode)
  {
    if (!compareAndSetWaitStatus(paramNode, -2, 0)) {
      return false;
    }
    Node localNode = enq(paramNode);
    int i = localNode.waitStatus;
    if ((i > 0) || (!compareAndSetWaitStatus(localNode, i, -1))) {
      LockSupport.unpark(paramNode.thread);
    }
    return true;
  }
  
  final boolean transferAfterCancelledWait(Node paramNode)
  {
    if (compareAndSetWaitStatus(paramNode, -2, 0))
    {
      enq(paramNode);
      return true;
    }
    while (!isOnSyncQueue(paramNode)) {
      Thread.yield();
    }
    return false;
  }
  
  final int fullyRelease(Node paramNode)
  {
    int i = 1;
    try
    {
      int j = getState();
      if (release(j))
      {
        i = 0;
        int k = j;
        return k;
      }
      throw new IllegalMonitorStateException();
    }
    finally
    {
      if (i != 0) {
        paramNode.waitStatus = 1;
      }
    }
  }
  
  public final boolean owns(ConditionObject paramConditionObject)
  {
    return paramConditionObject.isOwnedBy(this);
  }
  
  public final boolean hasWaiters(ConditionObject paramConditionObject)
  {
    if (!owns(paramConditionObject)) {
      throw new IllegalArgumentException("Not owner");
    }
    return paramConditionObject.hasWaiters();
  }
  
  public final int getWaitQueueLength(ConditionObject paramConditionObject)
  {
    if (!owns(paramConditionObject)) {
      throw new IllegalArgumentException("Not owner");
    }
    return paramConditionObject.getWaitQueueLength();
  }
  
  public final Collection<Thread> getWaitingThreads(ConditionObject paramConditionObject)
  {
    if (!owns(paramConditionObject)) {
      throw new IllegalArgumentException("Not owner");
    }
    return paramConditionObject.getWaitingThreads();
  }
  
  private final boolean compareAndSetHead(Node paramNode)
  {
    return unsafe.compareAndSwapObject(this, headOffset, null, paramNode);
  }
  
  private final boolean compareAndSetTail(Node paramNode1, Node paramNode2)
  {
    return unsafe.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2);
  }
  
  private static final boolean compareAndSetWaitStatus(Node paramNode, int paramInt1, int paramInt2)
  {
    return unsafe.compareAndSwapInt(paramNode, waitStatusOffset, paramInt1, paramInt2);
  }
  
  private static final boolean compareAndSetNext(Node paramNode1, Node paramNode2, Node paramNode3)
  {
    return unsafe.compareAndSwapObject(paramNode1, nextOffset, paramNode2, paramNode3);
  }
  
  static
  {
    try
    {
      stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
      headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
      tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
      waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
      nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
    }
    catch (Exception localException)
    {
      throw new Error(localException);
    }
  }
  
  public class ConditionObject
    implements Condition, Serializable
  {
    private static final long serialVersionUID = 1173984872572414699L;
    private transient AbstractQueuedSynchronizer.Node firstWaiter;
    private transient AbstractQueuedSynchronizer.Node lastWaiter;
    private static final int REINTERRUPT = 1;
    private static final int THROW_IE = -1;
    
    public ConditionObject() {}
    
    private AbstractQueuedSynchronizer.Node addConditionWaiter()
    {
      AbstractQueuedSynchronizer.Node localNode1 = this.lastWaiter;
      if ((localNode1 != null) && (localNode1.waitStatus != -2))
      {
        unlinkCancelledWaiters();
        localNode1 = this.lastWaiter;
      }
      AbstractQueuedSynchronizer.Node localNode2 = new AbstractQueuedSynchronizer.Node(Thread.currentThread(), -2);
      if (localNode1 == null) {
        this.firstWaiter = localNode2;
      } else {
        localNode1.nextWaiter = localNode2;
      }
      this.lastWaiter = localNode2;
      return localNode2;
    }
    
    private void doSignal(AbstractQueuedSynchronizer.Node paramNode)
    {
      do
      {
        if ((this.firstWaiter = paramNode.nextWaiter) == null) {
          this.lastWaiter = null;
        }
        paramNode.nextWaiter = null;
      } while ((!AbstractQueuedSynchronizer.this.transferForSignal(paramNode)) && ((paramNode = this.firstWaiter) != null));
    }
    
    private void doSignalAll(AbstractQueuedSynchronizer.Node paramNode)
    {
      this.lastWaiter = (this.firstWaiter = null);
      do
      {
        AbstractQueuedSynchronizer.Node localNode = paramNode.nextWaiter;
        paramNode.nextWaiter = null;
        AbstractQueuedSynchronizer.this.transferForSignal(paramNode);
        paramNode = localNode;
      } while (paramNode != null);
    }
    
    private void unlinkCancelledWaiters()
    {
      Object localObject1 = this.firstWaiter;
      Object localObject2 = null;
      while (localObject1 != null)
      {
        AbstractQueuedSynchronizer.Node localNode = ((AbstractQueuedSynchronizer.Node)localObject1).nextWaiter;
        if (((AbstractQueuedSynchronizer.Node)localObject1).waitStatus != -2)
        {
          ((AbstractQueuedSynchronizer.Node)localObject1).nextWaiter = null;
          if (localObject2 == null) {
            this.firstWaiter = localNode;
          } else {
            localObject2.nextWaiter = localNode;
          }
          if (localNode == null) {
            this.lastWaiter = localObject2;
          }
        }
        else
        {
          localObject2 = localObject1;
        }
        localObject1 = localNode;
      }
    }
    
    public final void signal()
    {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      AbstractQueuedSynchronizer.Node localNode = this.firstWaiter;
      if (localNode != null) {
        doSignal(localNode);
      }
    }
    
    public final void signalAll()
    {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      AbstractQueuedSynchronizer.Node localNode = this.firstWaiter;
      if (localNode != null) {
        doSignalAll(localNode);
      }
    }
    
    public final void awaitUninterruptibly()
    {
      AbstractQueuedSynchronizer.Node localNode = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(localNode);
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(localNode))
      {
        LockSupport.park(this);
        if (Thread.interrupted()) {
          j = 1;
        }
      }
      if ((AbstractQueuedSynchronizer.this.acquireQueued(localNode, i)) || (j != 0)) {
        AbstractQueuedSynchronizer.selfInterrupt();
      }
    }
    
    private int checkInterruptWhileWaiting(AbstractQueuedSynchronizer.Node paramNode)
    {
      return Thread.interrupted() ? 1 : AbstractQueuedSynchronizer.this.transferAfterCancelledWait(paramNode) ? -1 : 0;
    }
    
    private void reportInterruptAfterWait(int paramInt)
      throws InterruptedException
    {
      if (paramInt == -1) {
        throw new InterruptedException();
      }
      if (paramInt == 1) {
        AbstractQueuedSynchronizer.selfInterrupt();
      }
    }
    
    public final void await()
      throws InterruptedException
    {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedSynchronizer.Node localNode = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(localNode);
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(localNode))
      {
        LockSupport.park(this);
        if ((j = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
      }
      if ((AbstractQueuedSynchronizer.this.acquireQueued(localNode, i)) && (j != -1)) {
        j = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (j != 0) {
        reportInterruptAfterWait(j);
      }
    }
    
    public final long awaitNanos(long paramLong)
      throws InterruptedException
    {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedSynchronizer.Node localNode = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(localNode);
      long l = System.nanoTime() + paramLong;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(localNode))
      {
        if (paramLong <= 0L)
        {
          AbstractQueuedSynchronizer.this.transferAfterCancelledWait(localNode);
          break;
        }
        if (paramLong >= 1000L) {
          LockSupport.parkNanos(this, paramLong);
        }
        if ((j = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
        paramLong = l - System.nanoTime();
      }
      if ((AbstractQueuedSynchronizer.this.acquireQueued(localNode, i)) && (j != -1)) {
        j = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (j != 0) {
        reportInterruptAfterWait(j);
      }
      return l - System.nanoTime();
    }
    
    public final boolean awaitUntil(Date paramDate)
      throws InterruptedException
    {
      long l = paramDate.getTime();
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedSynchronizer.Node localNode = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(localNode);
      boolean bool = false;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(localNode)) {
        if (System.currentTimeMillis() > l)
        {
          bool = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(localNode);
        }
        else
        {
          LockSupport.parkUntil(this, l);
          if ((j = checkInterruptWhileWaiting(localNode)) != 0) {
            break;
          }
        }
      }
      if ((AbstractQueuedSynchronizer.this.acquireQueued(localNode, i)) && (j != -1)) {
        j = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (j != 0) {
        reportInterruptAfterWait(j);
      }
      return !bool;
    }
    
    public final boolean await(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      long l1 = paramTimeUnit.toNanos(paramLong);
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedSynchronizer.Node localNode = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(localNode);
      long l2 = System.nanoTime() + l1;
      boolean bool = false;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(localNode))
      {
        if (l1 <= 0L)
        {
          bool = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(localNode);
          break;
        }
        if (l1 >= 1000L) {
          LockSupport.parkNanos(this, l1);
        }
        if ((j = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
        l1 = l2 - System.nanoTime();
      }
      if ((AbstractQueuedSynchronizer.this.acquireQueued(localNode, i)) && (j != -1)) {
        j = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (j != 0) {
        reportInterruptAfterWait(j);
      }
      return !bool;
    }
    
    final boolean isOwnedBy(AbstractQueuedSynchronizer paramAbstractQueuedSynchronizer)
    {
      return paramAbstractQueuedSynchronizer == AbstractQueuedSynchronizer.this;
    }
    
    protected final boolean hasWaiters()
    {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      for (AbstractQueuedSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
        if (localNode.waitStatus == -2) {
          return true;
        }
      }
      return false;
    }
    
    protected final int getWaitQueueLength()
    {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      int i = 0;
      for (AbstractQueuedSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
        if (localNode.waitStatus == -2) {
          i++;
        }
      }
      return i;
    }
    
    protected final Collection<Thread> getWaitingThreads()
    {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      ArrayList localArrayList = new ArrayList();
      for (AbstractQueuedSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
        if (localNode.waitStatus == -2)
        {
          Thread localThread = localNode.thread;
          if (localThread != null) {
            localArrayList.add(localThread);
          }
        }
      }
      return localArrayList;
    }
  }
  
  static final class Node
  {
    static final Node SHARED = new Node();
    static final Node EXCLUSIVE = null;
    static final int CANCELLED = 1;
    static final int SIGNAL = -1;
    static final int CONDITION = -2;
    static final int PROPAGATE = -3;
    volatile int waitStatus;
    volatile Node prev;
    volatile Node next;
    volatile Thread thread;
    Node nextWaiter;
    
    final boolean isShared()
    {
      return this.nextWaiter == SHARED;
    }
    
    final Node predecessor()
      throws NullPointerException
    {
      Node localNode = this.prev;
      if (localNode == null) {
        throw new NullPointerException();
      }
      return localNode;
    }
    
    Node() {}
    
    Node(Thread paramThread, Node paramNode)
    {
      this.nextWaiter = paramNode;
      this.thread = paramThread;
    }
    
    Node(Thread paramThread, int paramInt)
    {
      this.waitStatus = paramInt;
      this.thread = paramThread;
    }
  }
}
