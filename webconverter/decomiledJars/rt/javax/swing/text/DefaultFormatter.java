package javax.swing.text;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.plaf.TextUI;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class DefaultFormatter
  extends JFormattedTextField.AbstractFormatter
  implements Cloneable, Serializable
{
  private boolean allowsInvalid = true;
  private boolean overwriteMode = true;
  private boolean commitOnEdit;
  private Class<?> valueClass;
  private NavigationFilter navigationFilter;
  private DocumentFilter documentFilter;
  transient ReplaceHolder replaceHolder;
  
  public DefaultFormatter() {}
  
  public void install(JFormattedTextField paramJFormattedTextField)
  {
    super.install(paramJFormattedTextField);
    positionCursorAtInitialLocation();
  }
  
  public void setCommitsOnValidEdit(boolean paramBoolean)
  {
    this.commitOnEdit = paramBoolean;
  }
  
  public boolean getCommitsOnValidEdit()
  {
    return this.commitOnEdit;
  }
  
  public void setOverwriteMode(boolean paramBoolean)
  {
    this.overwriteMode = paramBoolean;
  }
  
  public boolean getOverwriteMode()
  {
    return this.overwriteMode;
  }
  
  public void setAllowsInvalid(boolean paramBoolean)
  {
    this.allowsInvalid = paramBoolean;
  }
  
  public boolean getAllowsInvalid()
  {
    return this.allowsInvalid;
  }
  
  public void setValueClass(Class<?> paramClass)
  {
    this.valueClass = paramClass;
  }
  
  public Class<?> getValueClass()
  {
    return this.valueClass;
  }
  
  public Object stringToValue(String paramString)
    throws ParseException
  {
    Class localClass = getValueClass();
    JFormattedTextField localJFormattedTextField = getFormattedTextField();
    Object localObject;
    if ((localClass == null) && (localJFormattedTextField != null))
    {
      localObject = localJFormattedTextField.getValue();
      if (localObject != null) {
        localClass = localObject.getClass();
      }
    }
    if (localClass != null)
    {
      try
      {
        ReflectUtil.checkPackageAccess(localClass);
        SwingUtilities2.checkAccess(localClass.getModifiers());
        localObject = localClass.getConstructor(new Class[] { String.class });
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localObject = null;
      }
      if (localObject != null) {
        try
        {
          SwingUtilities2.checkAccess(((Constructor)localObject).getModifiers());
          return ((Constructor)localObject).newInstance(new Object[] { paramString });
        }
        catch (Throwable localThrowable)
        {
          throw new ParseException("Error creating instance", 0);
        }
      }
    }
    return paramString;
  }
  
  public String valueToString(Object paramObject)
    throws ParseException
  {
    if (paramObject == null) {
      return "";
    }
    return paramObject.toString();
  }
  
  protected DocumentFilter getDocumentFilter()
  {
    if (this.documentFilter == null) {
      this.documentFilter = new DefaultDocumentFilter(null);
    }
    return this.documentFilter;
  }
  
  protected NavigationFilter getNavigationFilter()
  {
    if (this.navigationFilter == null) {
      this.navigationFilter = new DefaultNavigationFilter(null);
    }
    return this.navigationFilter;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    DefaultFormatter localDefaultFormatter = (DefaultFormatter)super.clone();
    localDefaultFormatter.navigationFilter = null;
    localDefaultFormatter.documentFilter = null;
    localDefaultFormatter.replaceHolder = null;
    return localDefaultFormatter;
  }
  
  void positionCursorAtInitialLocation()
  {
    JFormattedTextField localJFormattedTextField = getFormattedTextField();
    if (localJFormattedTextField != null) {
      localJFormattedTextField.setCaretPosition(getInitialVisualPosition());
    }
  }
  
  int getInitialVisualPosition()
  {
    return getNextNavigatableChar(0, 1);
  }
  
  boolean isNavigatable(int paramInt)
  {
    return true;
  }
  
  boolean isLegalInsertText(String paramString)
  {
    return true;
  }
  
  private int getNextNavigatableChar(int paramInt1, int paramInt2)
  {
    int i = getFormattedTextField().getDocument().getLength();
    while ((paramInt1 >= 0) && (paramInt1 < i))
    {
      if (isNavigatable(paramInt1)) {
        return paramInt1;
      }
      paramInt1 += paramInt2;
    }
    return paramInt1;
  }
  
  String getReplaceString(int paramInt1, int paramInt2, String paramString)
  {
    String str1 = getFormattedTextField().getText();
    String str2 = str1.substring(0, paramInt1);
    if (paramString != null) {
      str2 = str2 + paramString;
    }
    if (paramInt1 + paramInt2 < str1.length()) {
      str2 = str2 + str1.substring(paramInt1 + paramInt2);
    }
    return str2;
  }
  
  boolean isValidEdit(ReplaceHolder paramReplaceHolder)
  {
    if (!getAllowsInvalid())
    {
      String str = getReplaceString(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text);
      try
      {
        paramReplaceHolder.value = stringToValue(str);
        return true;
      }
      catch (ParseException localParseException)
      {
        return false;
      }
    }
    return true;
  }
  
  void commitEdit()
    throws ParseException
  {
    JFormattedTextField localJFormattedTextField = getFormattedTextField();
    if (localJFormattedTextField != null) {
      localJFormattedTextField.commitEdit();
    }
  }
  
  void updateValue()
  {
    updateValue(null);
  }
  
  void updateValue(Object paramObject)
  {
    try
    {
      if (paramObject == null)
      {
        String str = getFormattedTextField().getText();
        paramObject = stringToValue(str);
      }
      if (getCommitsOnValidEdit()) {
        commitEdit();
      }
      setEditValid(true);
    }
    catch (ParseException localParseException)
    {
      setEditValid(false);
    }
  }
  
  int getNextCursorPosition(int paramInt1, int paramInt2)
  {
    int i = getNextNavigatableChar(paramInt1, paramInt2);
    int j = getFormattedTextField().getDocument().getLength();
    if (!getAllowsInvalid()) {
      if ((paramInt2 == -1) && (paramInt1 == i))
      {
        i = getNextNavigatableChar(i, 1);
        if (i >= j) {
          i = paramInt1;
        }
      }
      else if ((paramInt2 == 1) && (i >= j))
      {
        i = getNextNavigatableChar(j - 1, -1);
        if (i < j) {
          i++;
        }
      }
    }
    return i;
  }
  
  void repositionCursor(int paramInt1, int paramInt2)
  {
    getFormattedTextField().getCaret().setDot(getNextCursorPosition(paramInt1, paramInt2));
  }
  
  int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    int i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    if (i == -1) {
      return -1;
    }
    if ((!getAllowsInvalid()) && ((paramInt2 == 3) || (paramInt2 == 7)))
    {
      int j = -1;
      while ((!isNavigatable(i)) && (i != j))
      {
        j = i;
        i = paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, i, paramBias, paramInt2, paramArrayOfBias);
      }
      int k = getFormattedTextField().getDocument().getLength();
      if ((j == i) || (i == k))
      {
        if (i == 0)
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          i = getInitialVisualPosition();
        }
        if ((i >= k) && (k > 0))
        {
          paramArrayOfBias[0] = Position.Bias.Forward;
          i = getNextNavigatableChar(k - 1, -1) + 1;
        }
      }
    }
    return i;
  }
  
  boolean canReplace(ReplaceHolder paramReplaceHolder)
  {
    return isValidEdit(paramReplaceHolder);
  }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    throws BadLocationException
  {
    ReplaceHolder localReplaceHolder = getReplaceHolder(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
    replace(localReplaceHolder);
  }
  
  boolean replace(ReplaceHolder paramReplaceHolder)
    throws BadLocationException
  {
    int i = 1;
    int j = 1;
    if ((paramReplaceHolder.length > 0) && ((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0)) && ((getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset) || (paramReplaceHolder.length > 1))) {
      j = -1;
    }
    if ((getOverwriteMode()) && (paramReplaceHolder.text != null) && (getFormattedTextField().getSelectedText() == null)) {
      paramReplaceHolder.length = Math.min(Math.max(paramReplaceHolder.length, paramReplaceHolder.text.length()), paramReplaceHolder.fb.getDocument().getLength() - paramReplaceHolder.offset);
    }
    if (((paramReplaceHolder.text != null) && (!isLegalInsertText(paramReplaceHolder.text))) || (!canReplace(paramReplaceHolder)) || ((paramReplaceHolder.length == 0) && ((paramReplaceHolder.text == null) || (paramReplaceHolder.text.length() == 0)))) {
      i = 0;
    }
    if (i != 0)
    {
      int k = paramReplaceHolder.cursorPosition;
      paramReplaceHolder.fb.replace(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text, paramReplaceHolder.attrs);
      if (k == -1)
      {
        k = paramReplaceHolder.offset;
        if ((j == 1) && (paramReplaceHolder.text != null)) {
          k = paramReplaceHolder.offset + paramReplaceHolder.text.length();
        }
      }
      updateValue(paramReplaceHolder.value);
      repositionCursor(k, j);
      return true;
    }
    invalidEdit();
    return false;
  }
  
  void setDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
  {
    paramFilterBypass.setDot(paramInt, paramBias);
  }
  
  void moveDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
  {
    paramFilterBypass.moveDot(paramInt, paramBias);
  }
  
  ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
  {
    if (this.replaceHolder == null) {
      this.replaceHolder = new ReplaceHolder();
    }
    this.replaceHolder.reset(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
    return this.replaceHolder;
  }
  
  private class DefaultDocumentFilter
    extends DocumentFilter
    implements Serializable
  {
    private DefaultDocumentFilter() {}
    
    public void remove(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2)
      throws BadLocationException
    {
      JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (localJFormattedTextField.composedTextExists()) {
        paramFilterBypass.remove(paramInt1, paramInt2);
      } else {
        DefaultFormatter.this.replace(paramFilterBypass, paramInt1, paramInt2, null, null);
      }
    }
    
    public void insertString(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException
    {
      JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if ((localJFormattedTextField.composedTextExists()) || (Utilities.isComposedTextAttributeDefined(paramAttributeSet))) {
        paramFilterBypass.insertString(paramInt, paramString, paramAttributeSet);
      } else {
        DefaultFormatter.this.replace(paramFilterBypass, paramInt, 0, paramString, paramAttributeSet);
      }
    }
    
    public void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
      throws BadLocationException
    {
      JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if ((localJFormattedTextField.composedTextExists()) || (Utilities.isComposedTextAttributeDefined(paramAttributeSet))) {
        paramFilterBypass.replace(paramInt1, paramInt2, paramString, paramAttributeSet);
      } else {
        DefaultFormatter.this.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
      }
    }
  }
  
  private class DefaultNavigationFilter
    extends NavigationFilter
    implements Serializable
  {
    private DefaultNavigationFilter() {}
    
    public void setDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
    {
      JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (localJFormattedTextField.composedTextExists()) {
        paramFilterBypass.setDot(paramInt, paramBias);
      } else {
        DefaultFormatter.this.setDot(paramFilterBypass, paramInt, paramBias);
      }
    }
    
    public void moveDot(NavigationFilter.FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
    {
      JFormattedTextField localJFormattedTextField = DefaultFormatter.this.getFormattedTextField();
      if (localJFormattedTextField.composedTextExists()) {
        paramFilterBypass.moveDot(paramInt, paramBias);
      } else {
        DefaultFormatter.this.moveDot(paramFilterBypass, paramInt, paramBias);
      }
    }
    
    public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
      throws BadLocationException
    {
      if (paramJTextComponent.composedTextExists()) {
        return paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
      }
      return DefaultFormatter.this.getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
    }
  }
  
  static class ReplaceHolder
  {
    DocumentFilter.FilterBypass fb;
    int offset;
    int length;
    String text;
    AttributeSet attrs;
    Object value;
    int cursorPosition;
    
    ReplaceHolder() {}
    
    void reset(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet)
    {
      this.fb = paramFilterBypass;
      this.offset = paramInt1;
      this.length = paramInt2;
      this.text = paramString;
      this.attrs = paramAttributeSet;
      this.value = null;
      this.cursorPosition = -1;
    }
  }
}
