package javax.swing.text;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.Format;
import java.text.Format.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFormattedTextField;

public class InternationalFormatter
  extends DefaultFormatter
{
  private static final Format.Field[] EMPTY_FIELD_ARRAY = new Format.Field[0];
  private Format format;
  private Comparable max;
  private Comparable min;
  private transient BitSet literalMask;
  private transient AttributedCharacterIterator iterator;
  private transient boolean validMask;
  private transient String string;
  private transient boolean ignoreDocumentMutate;
  
  public InternationalFormatter()
  {
    setOverwriteMode(false);
  }
  
  public InternationalFormatter(Format paramFormat)
  {
    this();
    setFormat(paramFormat);
  }
  
  public void setFormat(Format paramFormat)
  {
    this.format = paramFormat;
  }
  
  public Format getFormat()
  {
    return this.format;
  }
  
  public void setMinimum(Comparable paramComparable)
  {
    if ((getValueClass() == null) && (paramComparable != null)) {
      setValueClass(paramComparable.getClass());
    }
    this.min = paramComparable;
  }
  
  public Comparable getMinimum()
  {
    return this.min;
  }
  
  public void setMaximum(Comparable paramComparable)
  {
    if ((getValueClass() == null) && (paramComparable != null)) {
      setValueClass(paramComparable.getClass());
    }
    this.max = paramComparable;
  }
  
  public Comparable getMaximum()
  {
    return this.max;
  }
  
  public void install(JFormattedTextField paramJFormattedTextField)
  {
    super.install(paramJFormattedTextField);
    updateMaskIfNecessary();
    positionCursorAtInitialLocation();
  }
  
  public String valueToString(Object paramObject)
    throws ParseException
  {
    if (paramObject == null) {
      return "";
    }
    Format localFormat = getFormat();
    if (localFormat == null) {
      return paramObject.toString();
    }
    return localFormat.format(paramObject);
  }
  
  public Object stringToValue(String paramString)
    throws ParseException
  {
    Object localObject = stringToValue(paramString, getFormat());
    if ((localObject != null) && (getValueClass() != null) && (!getValueClass().isInstance(localObject))) {
      localObject = super.stringToValue(localObject.toString());
    }
    try
    {
      if (!isValidValue(localObject, true)) {
        throw new ParseException("Value not within min/max range", 0);
      }
    }
    catch (ClassCastException localClassCastException)
    {
      throw new ParseException("Class cast exception comparing values: " + localClassCastException, 0);
    }
    return localObject;
  }
  
  public Format.Field[] getFields(int paramInt)
  {
    if (getAllowsInvalid()) {
      updateMask();
    }
    Map localMap = getAttributes(paramInt);
    if ((localMap != null) && (localMap.size() > 0))
    {
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(localMap.keySet());
      return (Format.Field[])localArrayList.toArray(EMPTY_FIELD_ARRAY);
    }
    return EMPTY_FIELD_ARRAY;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    InternationalFormatter localInternationalFormatter = (InternationalFormatter)super.clone();
    localInternationalFormatter.literalMask = null;
    localInternationalFormatter.iterator = null;
    localInternationalFormatter.validMask = false;
    localInternationalFormatter.string = null;
    return localInternationalFormatter;
  }
  
  protected Action[] getActions()
  {
    if (getSupportsIncrement()) {
      return new Action[] { new IncrementAction("increment", 1), new IncrementAction("decrement", -1) };
    }
    return null;
  }
  
  Object stringToValue(String paramString, Format paramFormat)
    throws ParseException
  {
    if (paramFormat == null) {
      return paramString;
    }
    return paramFormat.parseObject(paramString);
  }
  
  boolean isValidValue(Object paramObject, boolean paramBoolean)
  {
    Comparable localComparable1 = getMinimum();
    try
    {
      if ((localComparable1 != null) && (localComparable1.compareTo(paramObject) > 0)) {
        return false;
      }
    }
    catch (ClassCastException localClassCastException1)
    {
      if (paramBoolean) {
        throw localClassCastException1;
      }
      return false;
    }
    Comparable localComparable2 = getMaximum();
    try
    {
      if ((localComparable2 != null) && (localComparable2.compareTo(paramObject) < 0)) {
        return false;
      }
    }
    catch (ClassCastException localClassCastException2)
    {
      if (paramBoolean) {
        throw localClassCastException2;
      }
      return false;
    }
    return true;
  }
  
  Map<AttributedCharacterIterator.Attribute, Object> getAttributes(int paramInt)
  {
    if (isValidMask())
    {
      AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
      if ((paramInt >= 0) && (paramInt <= localAttributedCharacterIterator.getEndIndex()))
      {
        localAttributedCharacterIterator.setIndex(paramInt);
        return localAttributedCharacterIterator.getAttributes();
      }
    }
    return null;
  }
  
  int getAttributeStart(AttributedCharacterIterator.Attribute paramAttribute)
  {
    if (isValidMask())
    {
      AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
      localAttributedCharacterIterator.first();
      while (localAttributedCharacterIterator.current() != 65535)
      {
        if (localAttributedCharacterIterator.getAttribute(paramAttribute) != null) {
          return localAttributedCharacterIterator.getIndex();
        }
        localAttributedCharacterIterator.next();
      }
    }
    return -1;
  }
  
  AttributedCharacterIterator getIterator()
  {
    return this.iterator;
  }
  
  void updateMaskIfNecessary()
  {
    if ((!getAllowsInvalid()) && (getFormat() != null)) {
      if (!isValidMask())
      {
        updateMask();
      }
      else
      {
        String str = getFormattedTextField().getText();
        if (!str.equals(this.string)) {
          updateMask();
        }
      }
    }
  }
  
  void updateMask()
  {
    if (getFormat() != null)
    {
      Document localDocument = getFormattedTextField().getDocument();
      this.validMask = false;
      if (localDocument != null)
      {
        try
        {
          this.string = localDocument.getText(0, localDocument.getLength());
        }
        catch (BadLocationException localBadLocationException)
        {
          this.string = null;
        }
        if (this.string != null) {
          try
          {
            Object localObject = stringToValue(this.string);
            AttributedCharacterIterator localAttributedCharacterIterator = getFormat().formatToCharacterIterator(localObject);
            updateMask(localAttributedCharacterIterator);
          }
          catch (ParseException localParseException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (NullPointerException localNullPointerException) {}
        }
      }
    }
  }
  
  int getLiteralCountTo(int paramInt)
  {
    int i = 0;
    for (int j = 0; j < paramInt; j++) {
      if (isLiteral(j)) {
        i++;
      }
    }
    return i;
  }
  
  boolean isLiteral(int paramInt)
  {
    if ((isValidMask()) && (paramInt < this.string.length())) {
      return this.literalMask.get(paramInt);
    }
    return false;
  }
  
  char getLiteral(int paramInt)
  {
    if ((isValidMask()) && (this.string != null) && (paramInt < this.string.length())) {
      return this.string.charAt(paramInt);
    }
    return '\000';
  }
  
  boolean isNavigatable(int paramInt)
  {
    return !isLiteral(paramInt);
  }
  
  void updateValue(Object paramObject)
  {
    super.updateValue(paramObject);
    updateMaskIfNecessary();
  }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    if (this.ignoreDocumentMutate)
    {
      paramFilterBypass.replace(paramInt1, paramInt2, paramString, paramAttributeSet);
      return;
    }
    super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  private int getNextNonliteralIndex(int paramInt1, int paramInt2)
  {
    int i = getFormattedTextField().getDocument().getLength();
    while ((paramInt1 >= 0) && (paramInt1 < i)) {
      if (isLiteral(paramInt1)) {
        paramInt1 += paramInt2;
      } else {
        return paramInt1;
      }
    }
    return paramInt2 == -1 ? 0 : i;
  }
  
  boolean canReplace(DefaultFormatter.ReplaceHolder paramReplaceHolder)
  {
    if (!getAllowsInvalid())
    {
      String str = paramReplaceHolder.text;
      int i = str != null ? str.length() : 0;
      JFormattedTextField localJFormattedTextField = getFormattedTextField();
      if ((i == 0) && (paramReplaceHolder.length == 1) && (localJFormattedTextField.getSelectionStart() != paramReplaceHolder.offset))
      {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, -1);
      }
      else if (getOverwriteMode())
      {
        int j = paramReplaceHolder.offset;
        int k = j;
        int m = 0;
        for (int n = 0; n < paramReplaceHolder.length; n++)
        {
          while (isLiteral(j)) {
            j++;
          }
          if (j >= this.string.length())
          {
            j = k;
            m = 1;
            break;
          }
          j++;
          k = j;
        }
        if ((m != 0) || (localJFormattedTextField.getSelectedText() == null)) {
          paramReplaceHolder.length = (j - paramReplaceHolder.offset);
        }
      }
      else if (i > 0)
      {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, 1);
      }
      else
      {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, -1);
      }
      ((ExtendedReplaceHolder)paramReplaceHolder).endOffset = paramReplaceHolder.offset;
      ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength = (paramReplaceHolder.text != null ? paramReplaceHolder.text.length() : 0);
    }
    else
    {
      ((ExtendedReplaceHolder)paramReplaceHolder).endOffset = paramReplaceHolder.offset;
      ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength = (paramReplaceHolder.text != null ? paramReplaceHolder.text.length() : 0);
    }
    boolean bool = super.canReplace(paramReplaceHolder);
    if ((bool) && (!getAllowsInvalid())) {
      ((ExtendedReplaceHolder)paramReplaceHolder).resetFromValue(this);
    }
    return bool;
  }
  
  boolean replace(DefaultFormatter.ReplaceHolder paramReplaceHolder)
    throws BadLocationException
  {
    int i = -1;
    int j = 1;
    int k = -1;
    if ((paramReplaceHolder.length > 0) && ((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0)) && ((getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset) || (paramReplaceHolder.length > 1))) {
      j = -1;
    }
    if (!getAllowsInvalid())
    {
      if (((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0)) && (paramReplaceHolder.length > 0)) {
        i = getFormattedTextField().getSelectionStart();
      } else {
        i = paramReplaceHolder.offset;
      }
      k = getLiteralCountTo(i);
    }
    if (super.replace(paramReplaceHolder))
    {
      if (i != -1)
      {
        int m = ((ExtendedReplaceHolder)paramReplaceHolder).endOffset;
        m += ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength;
        repositionCursor(k, m, j);
      }
      else
      {
        i = ((ExtendedReplaceHolder)paramReplaceHolder).endOffset;
        if (j == 1) {
          i += ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength;
        }
        repositionCursor(i, j);
      }
      return true;
    }
    return false;
  }
  
  private void repositionCursor(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = getLiteralCountTo(paramInt2);
    if (i != paramInt2)
    {
      paramInt2 -= paramInt1;
      for (int j = 0; j < paramInt2; j++) {
        if (isLiteral(j)) {
          paramInt2++;
        }
      }
    }
    repositionCursor(paramInt2, 1);
  }
  
  char getBufferedChar(int paramInt)
  {
    if ((isValidMask()) && (this.string != null) && (paramInt < this.string.length())) {
      return this.string.charAt(paramInt);
    }
    return '\000';
  }
  
  boolean isValidMask()
  {
    return this.validMask;
  }
  
  boolean isLiteral(Map paramMap)
  {
    return (paramMap == null) || (paramMap.size() == 0);
  }
  
  private void updateMask(AttributedCharacterIterator paramAttributedCharacterIterator)
  {
    if (paramAttributedCharacterIterator != null)
    {
      this.validMask = true;
      this.iterator = paramAttributedCharacterIterator;
      if (this.literalMask == null) {
        this.literalMask = new BitSet();
      } else {
        for (int i = this.literalMask.length() - 1; i >= 0; i--) {
          this.literalMask.clear(i);
        }
      }
      paramAttributedCharacterIterator.first();
      while (paramAttributedCharacterIterator.current() != 65535)
      {
        Map localMap = paramAttributedCharacterIterator.getAttributes();
        boolean bool = isLiteral(localMap);
        int j = paramAttributedCharacterIterator.getIndex();
        int k = paramAttributedCharacterIterator.getRunLimit();
        while (j < k)
        {
          if (bool) {
            this.literalMask.set(j);
          } else {
            this.literalMask.clear(j);
          }
          j++;
        }
        paramAttributedCharacterIterator.setIndex(j);
      }
    }
  }
  
  boolean canIncrement(Object paramObject, int paramInt)
  {
    return paramObject != null;
  }
  
  void selectField(Object paramObject, int paramInt)
  {
    AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
    if ((localAttributedCharacterIterator != null) && ((paramObject instanceof AttributedCharacterIterator.Attribute)))
    {
      AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)paramObject;
      localAttributedCharacterIterator.first();
      while (localAttributedCharacterIterator.current() != 65535)
      {
        while ((localAttributedCharacterIterator.getAttribute(localAttribute) == null) && (localAttributedCharacterIterator.next() != 65535)) {}
        if (localAttributedCharacterIterator.current() != 65535)
        {
          int i = localAttributedCharacterIterator.getRunLimit(localAttribute);
          paramInt--;
          if (paramInt <= 0)
          {
            getFormattedTextField().select(localAttributedCharacterIterator.getIndex(), i);
            break;
          }
          localAttributedCharacterIterator.setIndex(i);
          localAttributedCharacterIterator.next();
        }
      }
    }
  }
  
  Object getAdjustField(int paramInt, Map paramMap)
  {
    return null;
  }
  
  private int getFieldTypeCountTo(Object paramObject, int paramInt)
  {
    AttributedCharacterIterator localAttributedCharacterIterator = getIterator();
    int i = 0;
    if ((localAttributedCharacterIterator != null) && ((paramObject instanceof AttributedCharacterIterator.Attribute)))
    {
      AttributedCharacterIterator.Attribute localAttribute = (AttributedCharacterIterator.Attribute)paramObject;
      localAttributedCharacterIterator.first();
      while (localAttributedCharacterIterator.getIndex() < paramInt)
      {
        while ((localAttributedCharacterIterator.getAttribute(localAttribute) == null) && (localAttributedCharacterIterator.next() != 65535)) {}
        if (localAttributedCharacterIterator.current() == 65535) {
          break;
        }
        localAttributedCharacterIterator.setIndex(localAttributedCharacterIterator.getRunLimit(localAttribute));
        localAttributedCharacterIterator.next();
        i++;
      }
    }
    return i;
  }
  
  Object adjustValue(Object paramObject1, Map paramMap, Object paramObject2, int paramInt)
    throws BadLocationException, ParseException
  {
    return null;
  }
  
  boolean getSupportsIncrement()
  {
    return false;
  }
  
  void resetValue(Object paramObject)
    throws BadLocationException, ParseException
  {
    Document localDocument = getFormattedTextField().getDocument();
    String str = valueToString(paramObject);
    try
    {
      this.ignoreDocumentMutate = true;
      localDocument.remove(0, localDocument.getLength());
      localDocument.insertString(0, str, null);
    }
    finally
    {
      this.ignoreDocumentMutate = false;
    }
    updateValue(paramObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    updateMaskIfNecessary();
  }
  
  DefaultFormatter.ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
  {
    if (this.replaceHolder == null) {
      this.replaceHolder = new ExtendedReplaceHolder();
    }
    return super.getReplaceHolder(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  static class ExtendedReplaceHolder
    extends DefaultFormatter.ReplaceHolder
  {
    int endOffset;
    int endTextLength;
    
    ExtendedReplaceHolder() {}
    
    void resetFromValue(InternationalFormatter paramInternationalFormatter)
    {
      this.offset = 0;
      try
      {
        this.text = paramInternationalFormatter.valueToString(this.value);
      }
      catch (ParseException localParseException)
      {
        this.text = "";
      }
      this.length = this.fb.getDocument().getLength();
    }
  }
  
  private class IncrementAction
    extends AbstractAction
  {
    private int direction;
    
    IncrementAction(String paramString, int paramInt)
    {
      super();
      this.direction = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (InternationalFormatter.this.getFormattedTextField().isEditable())
      {
        if (InternationalFormatter.this.getAllowsInvalid()) {
          InternationalFormatter.this.updateMask();
        }
        int i = 0;
        if (InternationalFormatter.this.isValidMask())
        {
          int j = InternationalFormatter.this.getFormattedTextField().getSelectionStart();
          if (j != -1)
          {
            AttributedCharacterIterator localAttributedCharacterIterator = InternationalFormatter.this.getIterator();
            localAttributedCharacterIterator.setIndex(j);
            Map localMap = localAttributedCharacterIterator.getAttributes();
            Object localObject1 = InternationalFormatter.this.getAdjustField(j, localMap);
            if (InternationalFormatter.this.canIncrement(localObject1, j)) {
              try
              {
                Object localObject2 = InternationalFormatter.this.stringToValue(InternationalFormatter.this.getFormattedTextField().getText());
                int k = InternationalFormatter.this.getFieldTypeCountTo(localObject1, j);
                localObject2 = InternationalFormatter.this.adjustValue(localObject2, localMap, localObject1, this.direction);
                if ((localObject2 != null) && (InternationalFormatter.this.isValidValue(localObject2, false)))
                {
                  InternationalFormatter.this.resetValue(localObject2);
                  InternationalFormatter.this.updateMask();
                  if (InternationalFormatter.this.isValidMask()) {
                    InternationalFormatter.this.selectField(localObject1, k);
                  }
                  i = 1;
                }
              }
              catch (ParseException localParseException) {}catch (BadLocationException localBadLocationException) {}
            }
          }
        }
        if (i == 0) {
          InternationalFormatter.this.invalidEdit();
        }
      }
    }
  }
}
