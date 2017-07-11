package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class StepIterator
  extends DTMAxisIteratorBase
{
  protected DTMAxisIterator _source;
  protected DTMAxisIterator _iterator;
  private int _pos = -1;
  
  public StepIterator(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2)
  {
    this._source = paramDTMAxisIterator1;
    this._iterator = paramDTMAxisIterator2;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
    this._iterator.setRestartable(true);
  }
  
  public DTMAxisIterator cloneIterator()
  {
    this._isRestartable = false;
    try
    {
      StepIterator localStepIterator = (StepIterator)super.clone();
      localStepIterator._source = this._source.cloneIterator();
      localStepIterator._iterator = this._iterator.cloneIterator();
      localStepIterator._iterator.setRestartable(true);
      localStepIterator._isRestartable = false;
      return localStepIterator.reset();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (this._isRestartable)
    {
      this._source.setStartNode(this._startNode = paramInt);
      this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
      return resetPosition();
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    this._source.reset();
    this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
    return resetPosition();
  }
  
  public int next()
  {
    for (;;)
    {
      int i;
      if ((i = this._iterator.next()) != -1) {
        return returnNode(i);
      }
      if ((i = this._source.next()) == -1) {
        return -1;
      }
      this._iterator.setStartNode(i);
    }
  }
  
  public void setMark()
  {
    this._source.setMark();
    this._iterator.setMark();
  }
  
  public void gotoMark()
  {
    this._source.gotoMark();
    this._iterator.gotoMark();
  }
}
