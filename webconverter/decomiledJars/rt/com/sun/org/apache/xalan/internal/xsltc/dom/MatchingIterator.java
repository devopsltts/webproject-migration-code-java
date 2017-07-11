package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class MatchingIterator
  extends DTMAxisIteratorBase
{
  private DTMAxisIterator _source;
  private final int _match;
  
  public MatchingIterator(int paramInt, DTMAxisIterator paramDTMAxisIterator)
  {
    this._source = paramDTMAxisIterator;
    this._match = paramInt;
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
      MatchingIterator localMatchingIterator = (MatchingIterator)super.clone();
      localMatchingIterator._source = this._source.cloneIterator();
      localMatchingIterator._isRestartable = false;
      return localMatchingIterator.reset();
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
      this._source.setStartNode(paramInt);
      for (this._position = 1; ((paramInt = this._source.next()) != -1) && (paramInt != this._match); this._position += 1) {}
    }
    return this;
  }
  
  public DTMAxisIterator reset()
  {
    this._source.reset();
    return resetPosition();
  }
  
  public int next()
  {
    return this._source.next();
  }
  
  public int getLast()
  {
    if (this._last == -1) {
      this._last = this._source.getLast();
    }
    return this._last;
  }
  
  public int getPosition()
  {
    return this._position;
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
