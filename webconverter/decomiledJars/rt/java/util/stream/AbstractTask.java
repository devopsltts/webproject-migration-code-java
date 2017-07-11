package java.util.stream;

import java.util.Spliterator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>>
  extends CountedCompleter<R>
{
  static final int LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
  protected final PipelineHelper<P_OUT> helper;
  protected Spliterator<P_IN> spliterator;
  protected long targetSize;
  protected K leftChild;
  protected K rightChild;
  private R localResult;
  
  protected AbstractTask(PipelineHelper<P_OUT> paramPipelineHelper, Spliterator<P_IN> paramSpliterator)
  {
    super(null);
    this.helper = paramPipelineHelper;
    this.spliterator = paramSpliterator;
    this.targetSize = 0L;
  }
  
  protected AbstractTask(K paramK, Spliterator<P_IN> paramSpliterator)
  {
    super(paramK);
    this.spliterator = paramSpliterator;
    this.helper = paramK.helper;
    this.targetSize = paramK.targetSize;
  }
  
  protected abstract K makeChild(Spliterator<P_IN> paramSpliterator);
  
  protected abstract R doLeaf();
  
  public static long suggestTargetSize(long paramLong)
  {
    long l = paramLong / LEAF_TARGET;
    return l > 0L ? l : 1L;
  }
  
  protected final long getTargetSize(long paramLong)
  {
    long l;
    return this.targetSize = suggestTargetSize(paramLong);
  }
  
  public R getRawResult()
  {
    return this.localResult;
  }
  
  protected void setRawResult(R paramR)
  {
    if (paramR != null) {
      throw new IllegalStateException();
    }
  }
  
  protected R getLocalResult()
  {
    return this.localResult;
  }
  
  protected void setLocalResult(R paramR)
  {
    this.localResult = paramR;
  }
  
  protected boolean isLeaf()
  {
    return this.leftChild == null;
  }
  
  protected boolean isRoot()
  {
    return getParent() == null;
  }
  
  protected K getParent()
  {
    return (AbstractTask)getCompleter();
  }
  
  public void compute()
  {
    Object localObject1 = this.spliterator;
    long l1 = ((Spliterator)localObject1).estimateSize();
    long l2 = getTargetSize(l1);
    int i = 0;
    Object localObject2 = this;
    Spliterator localSpliterator;
    while ((l1 > l2) && ((localSpliterator = ((Spliterator)localObject1).trySplit()) != null))
    {
      AbstractTask localAbstractTask1;
      ((AbstractTask)localObject2).leftChild = (localAbstractTask1 = ((AbstractTask)localObject2).makeChild(localSpliterator));
      AbstractTask localAbstractTask2;
      ((AbstractTask)localObject2).rightChild = (localAbstractTask2 = ((AbstractTask)localObject2).makeChild((Spliterator)localObject1));
      ((AbstractTask)localObject2).setPendingCount(1);
      AbstractTask localAbstractTask3;
      if (i != 0)
      {
        i = 0;
        localObject1 = localSpliterator;
        localObject2 = localAbstractTask1;
        localAbstractTask3 = localAbstractTask2;
      }
      else
      {
        i = 1;
        localObject2 = localAbstractTask2;
        localAbstractTask3 = localAbstractTask1;
      }
      localAbstractTask3.fork();
      l1 = ((Spliterator)localObject1).estimateSize();
    }
    ((AbstractTask)localObject2).setLocalResult(((AbstractTask)localObject2).doLeaf());
    ((AbstractTask)localObject2).tryComplete();
  }
  
  public void onCompletion(CountedCompleter<?> paramCountedCompleter)
  {
    this.spliterator = null;
    this.leftChild = (this.rightChild = null);
  }
  
  protected boolean isLeftmostNode()
  {
    AbstractTask localAbstractTask;
    for (Object localObject = this; localObject != null; localObject = localAbstractTask)
    {
      localAbstractTask = ((AbstractTask)localObject).getParent();
      if ((localAbstractTask != null) && (localAbstractTask.leftChild != localObject)) {
        return false;
      }
    }
    return true;
  }
}
