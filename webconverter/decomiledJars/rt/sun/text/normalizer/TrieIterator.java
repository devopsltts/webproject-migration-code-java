package sun.text.normalizer;

public class TrieIterator
  implements RangeValueIterator
{
  private static final int BMP_INDEX_LENGTH_ = 2048;
  private static final int LEAD_SURROGATE_MIN_VALUE_ = 55296;
  private static final int TRAIL_SURROGATE_MIN_VALUE_ = 56320;
  private static final int TRAIL_SURROGATE_COUNT_ = 1024;
  private static final int TRAIL_SURROGATE_INDEX_BLOCK_LENGTH_ = 32;
  private static final int DATA_BLOCK_LENGTH_ = 32;
  private Trie m_trie_;
  private int m_initialValue_;
  private int m_currentCodepoint_;
  private int m_nextCodepoint_;
  private int m_nextValue_;
  private int m_nextIndex_;
  private int m_nextBlock_;
  private int m_nextBlockIndex_;
  private int m_nextTrailIndexOffset_;
  
  public TrieIterator(Trie paramTrie)
  {
    if (paramTrie == null) {
      throw new IllegalArgumentException("Argument trie cannot be null");
    }
    this.m_trie_ = paramTrie;
    this.m_initialValue_ = extract(this.m_trie_.getInitialValue());
    reset();
  }
  
  public final boolean next(RangeValueIterator.Element paramElement)
  {
    if (this.m_nextCodepoint_ > 1114111) {
      return false;
    }
    if ((this.m_nextCodepoint_ < 65536) && (calculateNextBMPElement(paramElement))) {
      return true;
    }
    calculateNextSupplementaryElement(paramElement);
    return true;
  }
  
  public final void reset()
  {
    this.m_currentCodepoint_ = 0;
    this.m_nextCodepoint_ = 0;
    this.m_nextIndex_ = 0;
    this.m_nextBlock_ = (this.m_trie_.m_index_[0] << '\002');
    if (this.m_nextBlock_ == 0) {
      this.m_nextValue_ = this.m_initialValue_;
    } else {
      this.m_nextValue_ = extract(this.m_trie_.getValue(this.m_nextBlock_));
    }
    this.m_nextBlockIndex_ = 0;
    this.m_nextTrailIndexOffset_ = 32;
  }
  
  protected int extract(int paramInt)
  {
    return paramInt;
  }
  
  private final void setResult(RangeValueIterator.Element paramElement, int paramInt1, int paramInt2, int paramInt3)
  {
    paramElement.start = paramInt1;
    paramElement.limit = paramInt2;
    paramElement.value = paramInt3;
  }
  
  private final boolean calculateNextBMPElement(RangeValueIterator.Element paramElement)
  {
    int i = this.m_nextBlock_;
    int j = this.m_nextValue_;
    this.m_currentCodepoint_ = this.m_nextCodepoint_;
    this.m_nextCodepoint_ += 1;
    this.m_nextBlockIndex_ += 1;
    if (!checkBlockDetail(j))
    {
      setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, j);
      return true;
    }
    while (this.m_nextCodepoint_ < 65536)
    {
      this.m_nextIndex_ += 1;
      if (this.m_nextCodepoint_ == 55296) {
        this.m_nextIndex_ = 2048;
      } else if (this.m_nextCodepoint_ == 56320) {
        this.m_nextIndex_ = (this.m_nextCodepoint_ >> 5);
      }
      this.m_nextBlockIndex_ = 0;
      if (!checkBlock(i, j))
      {
        setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, j);
        return true;
      }
    }
    this.m_nextCodepoint_ -= 1;
    this.m_nextBlockIndex_ -= 1;
    return false;
  }
  
  private final void calculateNextSupplementaryElement(RangeValueIterator.Element paramElement)
  {
    int i = this.m_nextValue_;
    int j = this.m_nextBlock_;
    this.m_nextCodepoint_ += 1;
    this.m_nextBlockIndex_ += 1;
    if (UTF16.getTrailSurrogate(this.m_nextCodepoint_) != 56320)
    {
      if ((!checkNullNextTrailIndex()) && (!checkBlockDetail(i)))
      {
        setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, i);
        this.m_currentCodepoint_ = this.m_nextCodepoint_;
        return;
      }
      this.m_nextIndex_ += 1;
      this.m_nextTrailIndexOffset_ += 1;
      if (!checkTrailBlock(j, i))
      {
        setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, i);
        this.m_currentCodepoint_ = this.m_nextCodepoint_;
        return;
      }
    }
    int k = UTF16.getLeadSurrogate(this.m_nextCodepoint_);
    while (k < 56320)
    {
      int m = this.m_trie_.m_index_[(k >> 5)] << '\002';
      if (m == this.m_trie_.m_dataOffset_)
      {
        if (i != this.m_initialValue_)
        {
          this.m_nextValue_ = this.m_initialValue_;
          this.m_nextBlock_ = 0;
          this.m_nextBlockIndex_ = 0;
          setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, i);
          this.m_currentCodepoint_ = this.m_nextCodepoint_;
          return;
        }
        k += 32;
        this.m_nextCodepoint_ = UCharacterProperty.getRawSupplementary((char)k, 56320);
      }
      else
      {
        if (this.m_trie_.m_dataManipulate_ == null) {
          throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(m + (k & 0x1F)));
        if (this.m_nextIndex_ <= 0)
        {
          if (i != this.m_initialValue_)
          {
            this.m_nextValue_ = this.m_initialValue_;
            this.m_nextBlock_ = 0;
            this.m_nextBlockIndex_ = 0;
            setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, i);
            this.m_currentCodepoint_ = this.m_nextCodepoint_;
            return;
          }
          this.m_nextCodepoint_ += 1024;
        }
        else
        {
          this.m_nextTrailIndexOffset_ = 0;
          if (!checkTrailBlock(j, i))
          {
            setResult(paramElement, this.m_currentCodepoint_, this.m_nextCodepoint_, i);
            this.m_currentCodepoint_ = this.m_nextCodepoint_;
            return;
          }
        }
        k++;
      }
    }
    setResult(paramElement, this.m_currentCodepoint_, 1114112, i);
  }
  
  private final boolean checkBlockDetail(int paramInt)
  {
    while (this.m_nextBlockIndex_ < 32)
    {
      this.m_nextValue_ = extract(this.m_trie_.getValue(this.m_nextBlock_ + this.m_nextBlockIndex_));
      if (this.m_nextValue_ != paramInt) {
        return false;
      }
      this.m_nextBlockIndex_ += 1;
      this.m_nextCodepoint_ += 1;
    }
    return true;
  }
  
  private final boolean checkBlock(int paramInt1, int paramInt2)
  {
    this.m_nextBlock_ = (this.m_trie_.m_index_[this.m_nextIndex_] << '\002');
    if ((this.m_nextBlock_ == paramInt1) && (this.m_nextCodepoint_ - this.m_currentCodepoint_ >= 32))
    {
      this.m_nextCodepoint_ += 32;
    }
    else if (this.m_nextBlock_ == 0)
    {
      if (paramInt2 != this.m_initialValue_)
      {
        this.m_nextValue_ = this.m_initialValue_;
        this.m_nextBlockIndex_ = 0;
        return false;
      }
      this.m_nextCodepoint_ += 32;
    }
    else if (!checkBlockDetail(paramInt2))
    {
      return false;
    }
    return true;
  }
  
  private final boolean checkTrailBlock(int paramInt1, int paramInt2)
  {
    while (this.m_nextTrailIndexOffset_ < 32)
    {
      this.m_nextBlockIndex_ = 0;
      if (!checkBlock(paramInt1, paramInt2)) {
        return false;
      }
      this.m_nextTrailIndexOffset_ += 1;
      this.m_nextIndex_ += 1;
    }
    return true;
  }
  
  private final boolean checkNullNextTrailIndex()
  {
    if (this.m_nextIndex_ <= 0)
    {
      this.m_nextCodepoint_ += 1023;
      int i = UTF16.getLeadSurrogate(this.m_nextCodepoint_);
      int j = this.m_trie_.m_index_[(i >> 5)] << '\002';
      if (this.m_trie_.m_dataManipulate_ == null) {
        throw new NullPointerException("The field DataManipulate in this Trie is null");
      }
      this.m_nextIndex_ = this.m_trie_.m_dataManipulate_.getFoldingOffset(this.m_trie_.getValue(j + (i & 0x1F)));
      this.m_nextIndex_ -= 1;
      this.m_nextBlockIndex_ = 32;
      return true;
    }
    return false;
  }
}
