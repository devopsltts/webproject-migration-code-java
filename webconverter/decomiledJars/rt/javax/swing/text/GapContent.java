package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

public class GapContent
  extends GapVector
  implements AbstractDocument.Content, Serializable
{
  private static final char[] empty = new char[0];
  private transient MarkVector marks;
  private transient MarkData search;
  private transient int unusedMarks = 0;
  private transient ReferenceQueue<StickyPosition> queue;
  static final int GROWTH_SIZE = 524288;
  
  public GapContent()
  {
    this(10);
  }
  
  public GapContent(int paramInt)
  {
    super(Math.max(paramInt, 2));
    char[] arrayOfChar = new char[1];
    arrayOfChar[0] = '\n';
    replace(0, 0, arrayOfChar, arrayOfChar.length);
    this.marks = new MarkVector();
    this.search = new MarkData(0);
    this.queue = new ReferenceQueue();
  }
  
  protected Object allocateArray(int paramInt)
  {
    return new char[paramInt];
  }
  
  protected int getArrayLength()
  {
    char[] arrayOfChar = (char[])getArray();
    return arrayOfChar.length;
  }
  
  public int length()
  {
    int i = getArrayLength() - (getGapEnd() - getGapStart());
    return i;
  }
  
  public UndoableEdit insertString(int paramInt, String paramString)
    throws BadLocationException
  {
    if ((paramInt > length()) || (paramInt < 0)) {
      throw new BadLocationException("Invalid insert", length());
    }
    char[] arrayOfChar = paramString.toCharArray();
    replace(paramInt, 0, arrayOfChar, arrayOfChar.length);
    return new InsertUndo(paramInt, paramString.length());
  }
  
  public UndoableEdit remove(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    if (paramInt1 + paramInt2 >= length()) {
      throw new BadLocationException("Invalid remove", length() + 1);
    }
    String str = getString(paramInt1, paramInt2);
    RemoveUndo localRemoveUndo = new RemoveUndo(paramInt1, str);
    replace(paramInt1, paramInt2, empty, 0);
    return localRemoveUndo;
  }
  
  public String getString(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    Segment localSegment = new Segment();
    getChars(paramInt1, paramInt2, localSegment);
    return new String(localSegment.array, localSegment.offset, localSegment.count);
  }
  
  public void getChars(int paramInt1, int paramInt2, Segment paramSegment)
    throws BadLocationException
  {
    int i = paramInt1 + paramInt2;
    if ((paramInt1 < 0) || (i < 0)) {
      throw new BadLocationException("Invalid location", -1);
    }
    if ((i > length()) || (paramInt1 > length())) {
      throw new BadLocationException("Invalid location", length() + 1);
    }
    int j = getGapStart();
    int k = getGapEnd();
    char[] arrayOfChar = (char[])getArray();
    if (paramInt1 + paramInt2 <= j)
    {
      paramSegment.array = arrayOfChar;
      paramSegment.offset = paramInt1;
    }
    else if (paramInt1 >= j)
    {
      paramSegment.array = arrayOfChar;
      paramSegment.offset = (k + paramInt1 - j);
    }
    else
    {
      int m = j - paramInt1;
      if (paramSegment.isPartialReturn())
      {
        paramSegment.array = arrayOfChar;
        paramSegment.offset = paramInt1;
        paramSegment.count = m;
        return;
      }
      paramSegment.array = new char[paramInt2];
      paramSegment.offset = 0;
      System.arraycopy(arrayOfChar, paramInt1, paramSegment.array, 0, m);
      System.arraycopy(arrayOfChar, k, paramSegment.array, m, paramInt2 - m);
    }
    paramSegment.count = paramInt2;
  }
  
  public Position createPosition(int paramInt)
    throws BadLocationException
  {
    while (this.queue.poll() != null) {
      this.unusedMarks += 1;
    }
    if (this.unusedMarks > Math.max(5, this.marks.size() / 10)) {
      removeUnusedMarks();
    }
    int i = getGapStart();
    int j = getGapEnd();
    int k = paramInt < i ? paramInt : paramInt + (j - i);
    this.search.index = k;
    int m = findSortIndex(this.search);
    MarkData localMarkData;
    StickyPosition localStickyPosition;
    if ((m >= this.marks.size()) || ((localMarkData = this.marks.elementAt(m)).index != k) || ((localStickyPosition = localMarkData.getPosition()) == null))
    {
      localStickyPosition = new StickyPosition();
      localMarkData = new MarkData(k, localStickyPosition, this.queue);
      localStickyPosition.setMark(localMarkData);
      this.marks.insertElementAt(localMarkData, m);
    }
    return localStickyPosition;
  }
  
  protected void shiftEnd(int paramInt)
  {
    int i = getGapEnd();
    super.shiftEnd(paramInt);
    int j = getGapEnd() - i;
    int k = findMarkAdjustIndex(i);
    int m = this.marks.size();
    for (int n = k; n < m; n++)
    {
      MarkData localMarkData = this.marks.elementAt(n);
      localMarkData.index += j;
    }
  }
  
  int getNewArraySize(int paramInt)
  {
    if (paramInt < 524288) {
      return super.getNewArraySize(paramInt);
    }
    return paramInt + 524288;
  }
  
  protected void shiftGap(int paramInt)
  {
    int i = getGapStart();
    int j = paramInt - i;
    int k = getGapEnd();
    int m = k + j;
    int n = k - i;
    super.shiftGap(paramInt);
    int i1;
    int i2;
    int i3;
    MarkData localMarkData;
    if (j > 0)
    {
      i1 = findMarkAdjustIndex(i);
      i2 = this.marks.size();
      for (i3 = i1; i3 < i2; i3++)
      {
        localMarkData = this.marks.elementAt(i3);
        if (localMarkData.index >= m) {
          break;
        }
        localMarkData.index -= n;
      }
    }
    else if (j < 0)
    {
      i1 = findMarkAdjustIndex(paramInt);
      i2 = this.marks.size();
      for (i3 = i1; i3 < i2; i3++)
      {
        localMarkData = this.marks.elementAt(i3);
        if (localMarkData.index >= k) {
          break;
        }
        localMarkData.index += n;
      }
    }
    resetMarksAtZero();
  }
  
  protected void resetMarksAtZero()
  {
    if ((this.marks != null) && (getGapStart() == 0))
    {
      int i = getGapEnd();
      int j = 0;
      int k = this.marks.size();
      while (j < k)
      {
        MarkData localMarkData = this.marks.elementAt(j);
        if (localMarkData.index > i) {
          break;
        }
        localMarkData.index = 0;
        j++;
      }
    }
  }
  
  protected void shiftGapStartDown(int paramInt)
  {
    int i = findMarkAdjustIndex(paramInt);
    int j = this.marks.size();
    int k = getGapStart();
    int m = getGapEnd();
    for (int n = i; n < j; n++)
    {
      MarkData localMarkData = this.marks.elementAt(n);
      if (localMarkData.index > k) {
        break;
      }
      localMarkData.index = m;
    }
    super.shiftGapStartDown(paramInt);
    resetMarksAtZero();
  }
  
  protected void shiftGapEndUp(int paramInt)
  {
    int i = findMarkAdjustIndex(getGapEnd());
    int j = this.marks.size();
    for (int k = i; k < j; k++)
    {
      MarkData localMarkData = this.marks.elementAt(k);
      if (localMarkData.index >= paramInt) {
        break;
      }
      localMarkData.index = paramInt;
    }
    super.shiftGapEndUp(paramInt);
    resetMarksAtZero();
  }
  
  final int compare(MarkData paramMarkData1, MarkData paramMarkData2)
  {
    if (paramMarkData1.index < paramMarkData2.index) {
      return -1;
    }
    if (paramMarkData1.index > paramMarkData2.index) {
      return 1;
    }
    return 0;
  }
  
  final int findMarkAdjustIndex(int paramInt)
  {
    this.search.index = Math.max(paramInt, 1);
    int i = findSortIndex(this.search);
    for (int j = i - 1; j >= 0; j--)
    {
      MarkData localMarkData = this.marks.elementAt(j);
      if (localMarkData.index != this.search.index) {
        break;
      }
      i--;
    }
    return i;
  }
  
  final int findSortIndex(MarkData paramMarkData)
  {
    int i = 0;
    int j = this.marks.size() - 1;
    int k = 0;
    if (j == -1) {
      return 0;
    }
    MarkData localMarkData1 = this.marks.elementAt(j);
    int m = compare(paramMarkData, localMarkData1);
    if (m > 0) {
      return j + 1;
    }
    while (i <= j)
    {
      k = i + (j - i) / 2;
      MarkData localMarkData2 = this.marks.elementAt(k);
      m = compare(paramMarkData, localMarkData2);
      if (m == 0) {
        return k;
      }
      if (m < 0) {
        j = k - 1;
      } else {
        i = k + 1;
      }
    }
    return m < 0 ? k : k + 1;
  }
  
  final void removeUnusedMarks()
  {
    int i = this.marks.size();
    MarkVector localMarkVector = new MarkVector(i);
    for (int j = 0; j < i; j++)
    {
      MarkData localMarkData = this.marks.elementAt(j);
      if (localMarkData.get() != null) {
        localMarkVector.addElement(localMarkData);
      }
    }
    this.marks = localMarkVector;
    this.unusedMarks = 0;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    this.marks = new MarkVector();
    this.search = new MarkData(0);
    this.queue = new ReferenceQueue();
  }
  
  protected Vector getPositionsInRange(Vector paramVector, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    int m = getGapStart();
    int n = getGapEnd();
    int j;
    int k;
    if (paramInt1 < m)
    {
      if (paramInt1 == 0) {
        j = 0;
      } else {
        j = findMarkAdjustIndex(paramInt1);
      }
      if (i >= m) {
        k = findMarkAdjustIndex(i + (n - m) + 1);
      } else {
        k = findMarkAdjustIndex(i + 1);
      }
    }
    else
    {
      j = findMarkAdjustIndex(paramInt1 + (n - m));
      k = findMarkAdjustIndex(i + (n - m) + 1);
    }
    Vector localVector = paramVector == null ? new Vector(Math.max(1, k - j)) : paramVector;
    for (int i1 = j; i1 < k; i1++) {
      localVector.addElement(new UndoPosRef(this.marks.elementAt(i1)));
    }
    return localVector;
  }
  
  protected void updateUndoPositions(Vector paramVector, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    int j = getGapEnd();
    int m = findMarkAdjustIndex(j + 1);
    int k;
    if (paramInt1 != 0) {
      k = findMarkAdjustIndex(j);
    } else {
      k = 0;
    }
    for (int n = paramVector.size() - 1; n >= 0; n--)
    {
      UndoPosRef localUndoPosRef = (UndoPosRef)paramVector.elementAt(n);
      localUndoPosRef.resetLocation(i, j);
    }
    if (k < m)
    {
      Object[] arrayOfObject = new Object[m - k];
      int i1 = 0;
      MarkData localMarkData;
      if (paramInt1 == 0)
      {
        for (i2 = k; i2 < m; i2++)
        {
          localMarkData = this.marks.elementAt(i2);
          if (localMarkData.index == 0) {
            arrayOfObject[(i1++)] = localMarkData;
          }
        }
        for (i2 = k; i2 < m; i2++)
        {
          localMarkData = this.marks.elementAt(i2);
          if (localMarkData.index != 0) {
            arrayOfObject[(i1++)] = localMarkData;
          }
        }
      }
      for (int i2 = k; i2 < m; i2++)
      {
        localMarkData = this.marks.elementAt(i2);
        if (localMarkData.index != j) {
          arrayOfObject[(i1++)] = localMarkData;
        }
      }
      for (i2 = k; i2 < m; i2++)
      {
        localMarkData = this.marks.elementAt(i2);
        if (localMarkData.index == j) {
          arrayOfObject[(i1++)] = localMarkData;
        }
      }
      this.marks.replaceRange(k, m, arrayOfObject);
    }
  }
  
  class InsertUndo
    extends AbstractUndoableEdit
  {
    protected int offset;
    protected int length;
    protected String string;
    protected Vector posRefs;
    
    protected InsertUndo(int paramInt1, int paramInt2)
    {
      this.offset = paramInt1;
      this.length = paramInt2;
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      try
      {
        this.posRefs = GapContent.this.getPositionsInRange(null, this.offset, this.length);
        this.string = GapContent.this.getString(this.offset, this.length);
        GapContent.this.remove(this.offset, this.length);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotUndoException();
      }
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      try
      {
        GapContent.this.insertString(this.offset, this.string);
        this.string = null;
        if (this.posRefs != null)
        {
          GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
          this.posRefs = null;
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotRedoException();
      }
    }
  }
  
  final class MarkData
    extends WeakReference<GapContent.StickyPosition>
  {
    int index;
    
    MarkData(int paramInt)
    {
      super();
      this.index = paramInt;
    }
    
    MarkData(GapContent.StickyPosition paramStickyPosition, ReferenceQueue<? super GapContent.StickyPosition> paramReferenceQueue)
    {
      super(localReferenceQueue);
      this.index = paramStickyPosition;
    }
    
    public final int getOffset()
    {
      int i = GapContent.this.getGapStart();
      int j = GapContent.this.getGapEnd();
      int k = this.index < i ? this.index : this.index - (j - i);
      return Math.max(k, 0);
    }
    
    GapContent.StickyPosition getPosition()
    {
      return (GapContent.StickyPosition)get();
    }
  }
  
  static class MarkVector
    extends GapVector
  {
    GapContent.MarkData[] oneMark = new GapContent.MarkData[1];
    
    MarkVector() {}
    
    MarkVector(int paramInt)
    {
      super();
    }
    
    protected Object allocateArray(int paramInt)
    {
      return new GapContent.MarkData[paramInt];
    }
    
    protected int getArrayLength()
    {
      GapContent.MarkData[] arrayOfMarkData = (GapContent.MarkData[])getArray();
      return arrayOfMarkData.length;
    }
    
    public int size()
    {
      int i = getArrayLength() - (getGapEnd() - getGapStart());
      return i;
    }
    
    public void insertElementAt(GapContent.MarkData paramMarkData, int paramInt)
    {
      this.oneMark[0] = paramMarkData;
      replace(paramInt, 0, this.oneMark, 1);
    }
    
    public void addElement(GapContent.MarkData paramMarkData)
    {
      insertElementAt(paramMarkData, size());
    }
    
    public GapContent.MarkData elementAt(int paramInt)
    {
      int i = getGapStart();
      int j = getGapEnd();
      GapContent.MarkData[] arrayOfMarkData = (GapContent.MarkData[])getArray();
      if (paramInt < i) {
        return arrayOfMarkData[paramInt];
      }
      paramInt += j - i;
      return arrayOfMarkData[paramInt];
    }
    
    protected void replaceRange(int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      int i = getGapStart();
      int j = getGapEnd();
      int k = paramInt1;
      int m = 0;
      Object[] arrayOfObject = (Object[])getArray();
      if (paramInt1 >= i)
      {
        k += j - i;
        paramInt2 += j - i;
      }
      else if (paramInt2 >= i)
      {
        paramInt2 += j - i;
        while (k < i) {
          arrayOfObject[(k++)] = paramArrayOfObject[(m++)];
        }
        k = j;
      }
      else
      {
        while (k < paramInt2) {
          arrayOfObject[(k++)] = paramArrayOfObject[(m++)];
        }
      }
      while (k < paramInt2) {
        arrayOfObject[(k++)] = paramArrayOfObject[(m++)];
      }
    }
  }
  
  class RemoveUndo
    extends AbstractUndoableEdit
  {
    protected int offset;
    protected int length;
    protected String string;
    protected Vector posRefs;
    
    protected RemoveUndo(int paramInt, String paramString)
    {
      this.offset = paramInt;
      this.string = paramString;
      this.length = paramString.length();
      this.posRefs = GapContent.this.getPositionsInRange(null, paramInt, this.length);
    }
    
    public void undo()
      throws CannotUndoException
    {
      super.undo();
      try
      {
        GapContent.this.insertString(this.offset, this.string);
        if (this.posRefs != null)
        {
          GapContent.this.updateUndoPositions(this.posRefs, this.offset, this.length);
          this.posRefs = null;
        }
        this.string = null;
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotUndoException();
      }
    }
    
    public void redo()
      throws CannotRedoException
    {
      super.redo();
      try
      {
        this.string = GapContent.this.getString(this.offset, this.length);
        this.posRefs = GapContent.this.getPositionsInRange(null, this.offset, this.length);
        GapContent.this.remove(this.offset, this.length);
      }
      catch (BadLocationException localBadLocationException)
      {
        throw new CannotRedoException();
      }
    }
  }
  
  final class StickyPosition
    implements Position
  {
    GapContent.MarkData mark;
    
    StickyPosition() {}
    
    void setMark(GapContent.MarkData paramMarkData)
    {
      this.mark = paramMarkData;
    }
    
    public final int getOffset()
    {
      return this.mark.getOffset();
    }
    
    public String toString()
    {
      return Integer.toString(getOffset());
    }
  }
  
  final class UndoPosRef
  {
    protected int undoLocation;
    protected GapContent.MarkData rec;
    
    UndoPosRef(GapContent.MarkData paramMarkData)
    {
      this.rec = paramMarkData;
      this.undoLocation = paramMarkData.getOffset();
    }
    
    protected void resetLocation(int paramInt1, int paramInt2)
    {
      if (this.undoLocation != paramInt1) {
        this.rec.index = this.undoLocation;
      } else {
        this.rec.index = paramInt2;
      }
    }
  }
}
