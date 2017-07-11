package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class NthIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private final int _position;
  private boolean _ready;
  
  public NthIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    this._source = paramDTMAxisIterator;
    this._position = paramInt;
  }
  
  public void setRestartable(boolean paramBoolean)
  {
    this._isRestartable = paramBoolean;
    this._source.setRestartable(paramBoolean);
  }
  
  public DTMAxisIterator cloneIterator()
  {
    try
    {
      NthIterator localNthIterator = (NthIterator)super.clone();
      localNthIterator._source = this._source.cloneIterator();
      localNthIterator._isRestartable = false;
      return localNthIterator;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
    }
    return null;
  }
  
  public int next()
  {
    if (this._ready)
    {
      this._ready = false;
      return this._source.getNodeByPosition(this._position);
    }
    return -1;
  }
  
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (this._isRestartable)
    {
      this._source.setStartNode(paramInt);
      this._ready = true;
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    this._source.reset();
    this._ready = true;
    return this;
  }
  
  public int getLast()
  {
    return 1;
  }
  
  public int getPosition()
  {
    return 1;
  }
  
  public void setMark()
  {
    this._source.setMark();
  }
  
  public void gotoMark()
  {
    this._source.gotoMark();
  }
}
