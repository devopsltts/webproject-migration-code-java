package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedLongSynchronizer
  extends AbstractOwnableSynchronizer
  implements Serializable
{
  private static final long serialVersionUID = 7373984972572414692L;
  private volatile transient Node head;
  private volatile transient Node tail;
  private volatile long state;
  static final long spinForTimeoutThreshold = 1000L;
  private static final Unsafe unsafe = ;
  private static final long stateOffset;
  private static final long headOffset;
  private static final long tailOffset;
  private static final long waitStatusOffset;
  private static final long nextOffset;
  
  protected AbstractQueuedLongSynchronizer() {}
  
  protected final long getState()
  {
    return this.state;
  }
  
  protected final void setState(long paramLong)
  {
    this.state = paramLong;
  }
  
  protected final boolean compareAndSetState(long paramLong1, long paramLong2)
  {
    return unsafe.compareAndSwapLong(this, stateOffset, paramLong1, paramLong2);
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
  
  private void setHeadAndPropagate(Node paramNode, long paramLong)
  {
    Node localNode1 = this.head;
    setHead(paramNode);
    if ((paramLong > 0L) || (localNode1 == null) || (localNode1.waitStatus < 0) || ((localNode1 = this.head) == null) || (localNode1.waitStatus < 0))
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
  final boolean acquireQueued(Node paramNode, long paramLong)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: iconst_0
    //   4: istore 5
    //   6: aload_1
    //   7: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   10: astore 6
    //   12: aload 6
    //   14: aload_0
    //   15: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   18: if_acmpne +42 -> 60
    //   21: aload_0
    //   22: lload_2
    //   23: invokevirtual 335	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquire	(J)Z
    //   26: ifeq +34 -> 60
    //   29: aload_0
    //   30: aload_1
    //   31: invokespecial 343	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   34: aload 6
    //   36: aconst_null
    //   37: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   40: iconst_0
    //   41: istore 4
    //   43: iload 5
    //   45: istore 7
    //   47: iload 4
    //   49: ifeq +8 -> 57
    //   52: aload_0
    //   53: aload_1
    //   54: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   57: iload 7
    //   59: ireturn
    //   60: aload 6
    //   62: aload_1
    //   63: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   66: ifeq +13 -> 79
    //   69: aload_0
    //   70: invokespecial 329	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:parkAndCheckInterrupt	()Z
    //   73: ifeq +6 -> 79
    //   76: iconst_1
    //   77: istore 5
    //   79: goto -73 -> 6
    //   82: astore 8
    //   84: iload 4
    //   86: ifeq +8 -> 94
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   94: aload 8
    //   96: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	97	0	this	AbstractQueuedLongSynchronizer
    //   0	97	1	paramNode	Node
    //   0	97	2	paramLong	long
    //   1	84	4	i	int
    //   4	74	5	bool1	boolean
    //   10	51	6	localNode	Node
    //   45	13	7	bool2	boolean
    //   82	13	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	47	82	finally
    //   60	84	82	finally
  }
  
  /* Error */
  private void doAcquireInterruptibly(long paramLong)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 301	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:EXCLUSIVE	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   4: invokespecial 351	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   7: astore_3
    //   8: iconst_1
    //   9: istore 4
    //   11: aload_3
    //   12: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   15: astore 5
    //   17: aload 5
    //   19: aload_0
    //   20: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   23: if_acmpne +36 -> 59
    //   26: aload_0
    //   27: lload_1
    //   28: invokevirtual 335	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquire	(J)Z
    //   31: ifeq +28 -> 59
    //   34: aload_0
    //   35: aload_3
    //   36: invokespecial 343	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   39: aload 5
    //   41: aconst_null
    //   42: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   45: iconst_0
    //   46: istore 4
    //   48: iload 4
    //   50: ifeq +8 -> 58
    //   53: aload_0
    //   54: aload_3
    //   55: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   58: return
    //   59: aload 5
    //   61: aload_3
    //   62: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   65: ifeq +18 -> 83
    //   68: aload_0
    //   69: invokespecial 329	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:parkAndCheckInterrupt	()Z
    //   72: ifeq +11 -> 83
    //   75: new 168	java/lang/InterruptedException
    //   78: dup
    //   79: invokespecial 309	java/lang/InterruptedException:<init>	()V
    //   82: athrow
    //   83: goto -72 -> 11
    //   86: astore 6
    //   88: iload 4
    //   90: ifeq +8 -> 98
    //   93: aload_0
    //   94: aload_3
    //   95: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   98: aload 6
    //   100: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	AbstractQueuedLongSynchronizer
    //   0	101	1	paramLong	long
    //   7	88	3	localNode1	Node
    //   9	80	4	i	int
    //   15	45	5	localNode2	Node
    //   86	13	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	48	86	finally
    //   59	88	86	finally
  }
  
  /* Error */
  private boolean doAcquireNanos(long paramLong1, long paramLong2)
    throws InterruptedException
  {
    // Byte code:
    //   0: lload_3
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt +5 -> 8
    //   6: iconst_0
    //   7: ireturn
    //   8: invokestatic 316	java/lang/System:nanoTime	()J
    //   11: lload_3
    //   12: ladd
    //   13: lstore 5
    //   15: aload_0
    //   16: getstatic 301	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:EXCLUSIVE	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   19: invokespecial 351	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   22: astore 7
    //   24: iconst_1
    //   25: istore 8
    //   27: aload 7
    //   29: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   32: astore 9
    //   34: aload 9
    //   36: aload_0
    //   37: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   40: if_acmpne +43 -> 83
    //   43: aload_0
    //   44: lload_1
    //   45: invokevirtual 335	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquire	(J)Z
    //   48: ifeq +35 -> 83
    //   51: aload_0
    //   52: aload 7
    //   54: invokespecial 343	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHead	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   57: aload 9
    //   59: aconst_null
    //   60: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   63: iconst_0
    //   64: istore 8
    //   66: iconst_1
    //   67: istore 10
    //   69: iload 8
    //   71: ifeq +9 -> 80
    //   74: aload_0
    //   75: aload 7
    //   77: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   80: iload 10
    //   82: ireturn
    //   83: lload 5
    //   85: invokestatic 316	java/lang/System:nanoTime	()J
    //   88: lsub
    //   89: lstore_3
    //   90: lload_3
    //   91: lconst_0
    //   92: lcmp
    //   93: ifgt +20 -> 113
    //   96: iconst_0
    //   97: istore 10
    //   99: iload 8
    //   101: ifeq +9 -> 110
    //   104: aload_0
    //   105: aload 7
    //   107: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   110: iload 10
    //   112: ireturn
    //   113: aload 9
    //   115: aload 7
    //   117: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   120: ifeq +16 -> 136
    //   123: lload_3
    //   124: ldc2_w 158
    //   127: lcmp
    //   128: ifle +8 -> 136
    //   131: aload_0
    //   132: lload_3
    //   133: invokestatic 365	java/util/concurrent/locks/LockSupport:parkNanos	(Ljava/lang/Object;J)V
    //   136: invokestatic 319	java/lang/Thread:interrupted	()Z
    //   139: ifeq +11 -> 150
    //   142: new 168	java/lang/InterruptedException
    //   145: dup
    //   146: invokespecial 309	java/lang/InterruptedException:<init>	()V
    //   149: athrow
    //   150: goto -123 -> 27
    //   153: astore 11
    //   155: iload 8
    //   157: ifeq +9 -> 166
    //   160: aload_0
    //   161: aload 7
    //   163: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   166: aload 11
    //   168: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	169	0	this	AbstractQueuedLongSynchronizer
    //   0	169	1	paramLong1	long
    //   0	169	3	paramLong2	long
    //   13	71	5	l	long
    //   22	140	7	localNode1	Node
    //   25	131	8	i	int
    //   32	82	9	localNode2	Node
    //   67	44	10	bool	boolean
    //   153	14	11	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	69	153	finally
    //   83	99	153	finally
    //   113	155	153	finally
  }
  
  /* Error */
  private void doAcquireShared(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 302	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   4: invokespecial 351	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   7: astore_3
    //   8: iconst_1
    //   9: istore 4
    //   11: iconst_0
    //   12: istore 5
    //   14: aload_3
    //   15: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   18: astore 6
    //   20: aload 6
    //   22: aload_0
    //   23: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   26: if_acmpne +52 -> 78
    //   29: aload_0
    //   30: lload_1
    //   31: invokevirtual 330	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquireShared	(J)J
    //   34: lstore 7
    //   36: lload 7
    //   38: lconst_0
    //   39: lcmp
    //   40: iflt +38 -> 78
    //   43: aload_0
    //   44: aload_3
    //   45: lload 7
    //   47: invokespecial 349	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;J)V
    //   50: aload 6
    //   52: aconst_null
    //   53: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   56: iload 5
    //   58: ifeq +6 -> 64
    //   61: invokestatic 327	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:selfInterrupt	()V
    //   64: iconst_0
    //   65: istore 4
    //   67: iload 4
    //   69: ifeq +8 -> 77
    //   72: aload_0
    //   73: aload_3
    //   74: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   77: return
    //   78: aload 6
    //   80: aload_3
    //   81: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   84: ifeq +13 -> 97
    //   87: aload_0
    //   88: invokespecial 329	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:parkAndCheckInterrupt	()Z
    //   91: ifeq +6 -> 97
    //   94: iconst_1
    //   95: istore 5
    //   97: goto -83 -> 14
    //   100: astore 9
    //   102: iload 4
    //   104: ifeq +8 -> 112
    //   107: aload_0
    //   108: aload_3
    //   109: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   112: aload 9
    //   114: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	115	0	this	AbstractQueuedLongSynchronizer
    //   0	115	1	paramLong	long
    //   7	102	3	localNode1	Node
    //   9	94	4	i	int
    //   12	84	5	j	int
    //   18	61	6	localNode2	Node
    //   34	12	7	l	long
    //   100	13	9	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	67	100	finally
    //   78	102	100	finally
  }
  
  /* Error */
  private void doAcquireSharedInterruptibly(long paramLong)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getstatic 302	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   4: invokespecial 351	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   7: astore_3
    //   8: iconst_1
    //   9: istore 4
    //   11: aload_3
    //   12: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   15: astore 5
    //   17: aload 5
    //   19: aload_0
    //   20: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   23: if_acmpne +44 -> 67
    //   26: aload_0
    //   27: lload_1
    //   28: invokevirtual 330	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquireShared	(J)J
    //   31: lstore 6
    //   33: lload 6
    //   35: lconst_0
    //   36: lcmp
    //   37: iflt +30 -> 67
    //   40: aload_0
    //   41: aload_3
    //   42: lload 6
    //   44: invokespecial 349	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;J)V
    //   47: aload 5
    //   49: aconst_null
    //   50: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   53: iconst_0
    //   54: istore 4
    //   56: iload 4
    //   58: ifeq +8 -> 66
    //   61: aload_0
    //   62: aload_3
    //   63: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   66: return
    //   67: aload 5
    //   69: aload_3
    //   70: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   73: ifeq +18 -> 91
    //   76: aload_0
    //   77: invokespecial 329	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:parkAndCheckInterrupt	()Z
    //   80: ifeq +11 -> 91
    //   83: new 168	java/lang/InterruptedException
    //   86: dup
    //   87: invokespecial 309	java/lang/InterruptedException:<init>	()V
    //   90: athrow
    //   91: goto -80 -> 11
    //   94: astore 8
    //   96: iload 4
    //   98: ifeq +8 -> 106
    //   101: aload_0
    //   102: aload_3
    //   103: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   106: aload 8
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	AbstractQueuedLongSynchronizer
    //   0	109	1	paramLong	long
    //   7	96	3	localNode1	Node
    //   9	88	4	i	int
    //   15	53	5	localNode2	Node
    //   31	12	6	l	long
    //   94	13	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	56	94	finally
    //   67	96	94	finally
  }
  
  /* Error */
  private boolean doAcquireSharedNanos(long paramLong1, long paramLong2)
    throws InterruptedException
  {
    // Byte code:
    //   0: lload_3
    //   1: lconst_0
    //   2: lcmp
    //   3: ifgt +5 -> 8
    //   6: iconst_0
    //   7: ireturn
    //   8: invokestatic 316	java/lang/System:nanoTime	()J
    //   11: lload_3
    //   12: ladd
    //   13: lstore 5
    //   15: aload_0
    //   16: getstatic 302	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:SHARED	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   19: invokespecial 351	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:addWaiter	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   22: astore 7
    //   24: iconst_1
    //   25: istore 8
    //   27: aload 7
    //   29: invokevirtual 362	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:predecessor	()Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   32: astore 9
    //   34: aload 9
    //   36: aload_0
    //   37: getfield 296	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:head	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   40: if_acmpne +51 -> 91
    //   43: aload_0
    //   44: lload_1
    //   45: invokevirtual 330	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:tryAcquireShared	(J)J
    //   48: lstore 10
    //   50: lload 10
    //   52: lconst_0
    //   53: lcmp
    //   54: iflt +37 -> 91
    //   57: aload_0
    //   58: aload 7
    //   60: lload 10
    //   62: invokespecial 349	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:setHeadAndPropagate	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;J)V
    //   65: aload 9
    //   67: aconst_null
    //   68: putfield 303	java/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node:next	Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;
    //   71: iconst_0
    //   72: istore 8
    //   74: iconst_1
    //   75: istore 12
    //   77: iload 8
    //   79: ifeq +9 -> 88
    //   82: aload_0
    //   83: aload 7
    //   85: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   88: iload 12
    //   90: ireturn
    //   91: lload 5
    //   93: invokestatic 316	java/lang/System:nanoTime	()J
    //   96: lsub
    //   97: lstore_3
    //   98: lload_3
    //   99: lconst_0
    //   100: lcmp
    //   101: ifgt +20 -> 121
    //   104: iconst_0
    //   105: istore 10
    //   107: iload 8
    //   109: ifeq +9 -> 118
    //   112: aload_0
    //   113: aload 7
    //   115: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   118: iload 10
    //   120: ireturn
    //   121: aload 9
    //   123: aload 7
    //   125: invokestatic 354	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:shouldParkAfterFailedAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)Z
    //   128: ifeq +16 -> 144
    //   131: lload_3
    //   132: ldc2_w 158
    //   135: lcmp
    //   136: ifle +8 -> 144
    //   139: aload_0
    //   140: lload_3
    //   141: invokestatic 365	java/util/concurrent/locks/LockSupport:parkNanos	(Ljava/lang/Object;J)V
    //   144: invokestatic 319	java/lang/Thread:interrupted	()Z
    //   147: ifeq +11 -> 158
    //   150: new 168	java/lang/InterruptedException
    //   153: dup
    //   154: invokespecial 309	java/lang/InterruptedException:<init>	()V
    //   157: athrow
    //   158: goto -131 -> 27
    //   161: astore 13
    //   163: iload 8
    //   165: ifeq +9 -> 174
    //   168: aload_0
    //   169: aload 7
    //   171: invokespecial 342	java/util/concurrent/locks/AbstractQueuedLongSynchronizer:cancelAcquire	(Ljava/util/concurrent/locks/AbstractQueuedLongSynchronizer$Node;)V
    //   174: aload 13
    //   176: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	177	0	this	AbstractQueuedLongSynchronizer
    //   0	177	1	paramLong1	long
    //   0	177	3	paramLong2	long
    //   13	79	5	l1	long
    //   22	148	7	localNode1	Node
    //   25	139	8	i	int
    //   32	90	9	localNode2	Node
    //   48	13	10	l2	long
    //   105	14	10	bool1	boolean
    //   75	14	12	bool2	boolean
    //   161	14	13	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   27	77	161	finally
    //   91	107	161	finally
    //   121	163	161	finally
  }
  
  protected boolean tryAcquire(long paramLong)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean tryRelease(long paramLong)
  {
    throw new UnsupportedOperationException();
  }
  
  protected long tryAcquireShared(long paramLong)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean tryReleaseShared(long paramLong)
  {
    throw new UnsupportedOperationException();
  }
  
  protected boolean isHeldExclusively()
  {
    throw new UnsupportedOperationException();
  }
  
  public final void acquire(long paramLong)
  {
    if ((!tryAcquire(paramLong)) && (acquireQueued(addWaiter(Node.EXCLUSIVE), paramLong))) {
      selfInterrupt();
    }
  }
  
  public final void acquireInterruptibly(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (!tryAcquire(paramLong)) {
      doAcquireInterruptibly(paramLong);
    }
  }
  
  public final boolean tryAcquireNanos(long paramLong1, long paramLong2)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return (tryAcquire(paramLong1)) || (doAcquireNanos(paramLong1, paramLong2));
  }
  
  public final boolean release(long paramLong)
  {
    if (tryRelease(paramLong))
    {
      Node localNode = this.head;
      if ((localNode != null) && (localNode.waitStatus != 0)) {
        unparkSuccessor(localNode);
      }
      return true;
    }
    return false;
  }
  
  public final void acquireShared(long paramLong)
  {
    if (tryAcquireShared(paramLong) < 0L) {
      doAcquireShared(paramLong);
    }
  }
  
  public final void acquireSharedInterruptibly(long paramLong)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    if (tryAcquireShared(paramLong) < 0L) {
      doAcquireSharedInterruptibly(paramLong);
    }
  }
  
  public final boolean tryAcquireSharedNanos(long paramLong1, long paramLong2)
    throws InterruptedException
  {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    return (tryAcquireShared(paramLong1) >= 0L) || (doAcquireSharedNanos(paramLong1, paramLong2));
  }
  
  public final boolean releaseShared(long paramLong)
  {
    if (tryReleaseShared(paramLong))
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
    long l = getState();
    String str = hasQueuedThreads() ? "non" : "";
    return super.toString() + "[State = " + l + ", " + str + "empty queue]";
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
  
  final long fullyRelease(Node paramNode)
  {
    int i = 1;
    try
    {
      long l1 = getState();
      if (release(l1))
      {
        i = 0;
        long l2 = l1;
        return l2;
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
      stateOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("state"));
      headOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("head"));
      tailOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("tail"));
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
    private transient AbstractQueuedLongSynchronizer.Node firstWaiter;
    private transient AbstractQueuedLongSynchronizer.Node lastWaiter;
    private static final int REINTERRUPT = 1;
    private static final int THROW_IE = -1;
    
    public ConditionObject() {}
    
    private AbstractQueuedLongSynchronizer.Node addConditionWaiter()
    {
      AbstractQueuedLongSynchronizer.Node localNode1 = this.lastWaiter;
      if ((localNode1 != null) && (localNode1.waitStatus != -2))
      {
        unlinkCancelledWaiters();
        localNode1 = this.lastWaiter;
      }
      AbstractQueuedLongSynchronizer.Node localNode2 = new AbstractQueuedLongSynchronizer.Node(Thread.currentThread(), -2);
      if (localNode1 == null) {
        this.firstWaiter = localNode2;
      } else {
        localNode1.nextWaiter = localNode2;
      }
      this.lastWaiter = localNode2;
      return localNode2;
    }
    
    private void doSignal(AbstractQueuedLongSynchronizer.Node paramNode)
    {
      do
      {
        if ((this.firstWaiter = paramNode.nextWaiter) == null) {
          this.lastWaiter = null;
        }
        paramNode.nextWaiter = null;
      } while ((!AbstractQueuedLongSynchronizer.this.transferForSignal(paramNode)) && ((paramNode = this.firstWaiter) != null));
    }
    
    private void doSignalAll(AbstractQueuedLongSynchronizer.Node paramNode)
    {
      this.lastWaiter = (this.firstWaiter = null);
      do
      {
        AbstractQueuedLongSynchronizer.Node localNode = paramNode.nextWaiter;
        paramNode.nextWaiter = null;
        AbstractQueuedLongSynchronizer.this.transferForSignal(paramNode);
        paramNode = localNode;
      } while (paramNode != null);
    }
    
    private void unlinkCancelledWaiters()
    {
      Object localObject1 = this.firstWaiter;
      Object localObject2 = null;
      while (localObject1 != null)
      {
        AbstractQueuedLongSynchronizer.Node localNode = ((AbstractQueuedLongSynchronizer.Node)localObject1).nextWaiter;
        if (((AbstractQueuedLongSynchronizer.Node)localObject1).waitStatus != -2)
        {
          ((AbstractQueuedLongSynchronizer.Node)localObject1).nextWaiter = null;
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
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      AbstractQueuedLongSynchronizer.Node localNode = this.firstWaiter;
      if (localNode != null) {
        doSignal(localNode);
      }
    }
    
    public final void signalAll()
    {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      AbstractQueuedLongSynchronizer.Node localNode = this.firstWaiter;
      if (localNode != null) {
        doSignalAll(localNode);
      }
    }
    
    public final void awaitUninterruptibly()
    {
      AbstractQueuedLongSynchronizer.Node localNode = addConditionWaiter();
      long l = AbstractQueuedLongSynchronizer.this.fullyRelease(localNode);
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(localNode))
      {
        LockSupport.park(this);
        if (Thread.interrupted()) {
          i = 1;
        }
      }
      if ((AbstractQueuedLongSynchronizer.this.acquireQueued(localNode, l)) || (i != 0)) {
        AbstractQueuedLongSynchronizer.selfInterrupt();
      }
    }
    
    private int checkInterruptWhileWaiting(AbstractQueuedLongSynchronizer.Node paramNode)
    {
      return Thread.interrupted() ? 1 : AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(paramNode) ? -1 : 0;
    }
    
    private void reportInterruptAfterWait(int paramInt)
      throws InterruptedException
    {
      if (paramInt == -1) {
        throw new InterruptedException();
      }
      if (paramInt == 1) {
        AbstractQueuedLongSynchronizer.selfInterrupt();
      }
    }
    
    public final void await()
      throws InterruptedException
    {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedLongSynchronizer.Node localNode = addConditionWaiter();
      long l = AbstractQueuedLongSynchronizer.this.fullyRelease(localNode);
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(localNode))
      {
        LockSupport.park(this);
        if ((i = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
      }
      if ((AbstractQueuedLongSynchronizer.this.acquireQueued(localNode, l)) && (i != -1)) {
        i = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (i != 0) {
        reportInterruptAfterWait(i);
      }
    }
    
    public final long awaitNanos(long paramLong)
      throws InterruptedException
    {
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedLongSynchronizer.Node localNode = addConditionWaiter();
      long l1 = AbstractQueuedLongSynchronizer.this.fullyRelease(localNode);
      long l2 = System.nanoTime() + paramLong;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(localNode))
      {
        if (paramLong <= 0L)
        {
          AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(localNode);
          break;
        }
        if (paramLong >= 1000L) {
          LockSupport.parkNanos(this, paramLong);
        }
        if ((i = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
        paramLong = l2 - System.nanoTime();
      }
      if ((AbstractQueuedLongSynchronizer.this.acquireQueued(localNode, l1)) && (i != -1)) {
        i = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (i != 0) {
        reportInterruptAfterWait(i);
      }
      return l2 - System.nanoTime();
    }
    
    public final boolean awaitUntil(Date paramDate)
      throws InterruptedException
    {
      long l1 = paramDate.getTime();
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      AbstractQueuedLongSynchronizer.Node localNode = addConditionWaiter();
      long l2 = AbstractQueuedLongSynchronizer.this.fullyRelease(localNode);
      boolean bool = false;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(localNode)) {
        if (System.currentTimeMillis() > l1)
        {
          bool = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(localNode);
        }
        else
        {
          LockSupport.parkUntil(this, l1);
          if ((i = checkInterruptWhileWaiting(localNode)) != 0) {
            break;
          }
        }
      }
      if ((AbstractQueuedLongSynchronizer.this.acquireQueued(localNode, l2)) && (i != -1)) {
        i = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (i != 0) {
        reportInterruptAfterWait(i);
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
      AbstractQueuedLongSynchronizer.Node localNode = addConditionWaiter();
      long l2 = AbstractQueuedLongSynchronizer.this.fullyRelease(localNode);
      long l3 = System.nanoTime() + l1;
      boolean bool = false;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(localNode))
      {
        if (l1 <= 0L)
        {
          bool = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(localNode);
          break;
        }
        if (l1 >= 1000L) {
          LockSupport.parkNanos(this, l1);
        }
        if ((i = checkInterruptWhileWaiting(localNode)) != 0) {
          break;
        }
        l1 = l3 - System.nanoTime();
      }
      if ((AbstractQueuedLongSynchronizer.this.acquireQueued(localNode, l2)) && (i != -1)) {
        i = 1;
      }
      if (localNode.nextWaiter != null) {
        unlinkCancelledWaiters();
      }
      if (i != 0) {
        reportInterruptAfterWait(i);
      }
      return !bool;
    }
    
    final boolean isOwnedBy(AbstractQueuedLongSynchronizer paramAbstractQueuedLongSynchronizer)
    {
      return paramAbstractQueuedLongSynchronizer == AbstractQueuedLongSynchronizer.this;
    }
    
    protected final boolean hasWaiters()
    {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      for (AbstractQueuedLongSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
        if (localNode.waitStatus == -2) {
          return true;
        }
      }
      return false;
    }
    
    protected final int getWaitQueueLength()
    {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      int i = 0;
      for (AbstractQueuedLongSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
        if (localNode.waitStatus == -2) {
          i++;
        }
      }
      return i;
    }
    
    protected final Collection<Thread> getWaitingThreads()
    {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
        throw new IllegalMonitorStateException();
      }
      ArrayList localArrayList = new ArrayList();
      for (AbstractQueuedLongSynchronizer.Node localNode = this.firstWaiter; localNode != null; localNode = localNode.nextWaiter) {
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
