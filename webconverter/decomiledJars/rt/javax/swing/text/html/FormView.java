package javax.swing.text.html;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.BitSet;
import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;

public class FormView
  extends ComponentView
  implements ActionListener
{
  @Deprecated
  public static final String SUBMIT = new String("Submit Query");
  @Deprecated
  public static final String RESET = new String("Reset");
  static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
  private short maxIsPreferred;
  
  public FormView(Element paramElement)
  {
    super(paramElement);
  }
  
  protected Component createComponent()
  {
    AttributeSet localAttributeSet = getElement().getAttributes();
    HTML.Tag localTag = (HTML.Tag)localAttributeSet.getAttribute(StyleConstants.NameAttribute);
    Object localObject1 = null;
    Object localObject2 = localAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    removeStaleListenerForModel(localObject2);
    if (localTag == HTML.Tag.INPUT)
    {
      localObject1 = createInputComponent(localAttributeSet, localObject2);
    }
    else
    {
      Object localObject3;
      int i;
      if (localTag == HTML.Tag.SELECT)
      {
        if ((localObject2 instanceof OptionListModel))
        {
          localObject3 = new JList((ListModel)localObject2);
          i = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.SIZE, 1);
          ((JList)localObject3).setVisibleRowCount(i);
          ((JList)localObject3).setSelectionModel((ListSelectionModel)localObject2);
          localObject1 = new JScrollPane((Component)localObject3);
        }
        else
        {
          localObject1 = new JComboBox((ComboBoxModel)localObject2);
          this.maxIsPreferred = 3;
        }
      }
      else if (localTag == HTML.Tag.TEXTAREA)
      {
        localObject3 = new JTextArea((Document)localObject2);
        i = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.ROWS, 1);
        ((JTextArea)localObject3).setRows(i);
        int j = HTML.getIntegerAttributeValue(localAttributeSet, HTML.Attribute.COLS, 20);
        this.maxIsPreferred = 3;
        ((JTextArea)localObject3).setColumns(j);
        localObject1 = new JScrollPane((Component)localObject3, 22, 32);
      }
    }
    if (localObject1 != null) {
      ((JComponent)localObject1).setAlignmentY(1.0F);
    }
    return localObject1;
  }
  
  private JComponent createInputComponent(AttributeSet paramAttributeSet, Object paramObject)
  {
    Object localObject1 = null;
    String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
    String str2;
    Object localObject3;
    if ((str1.equals("submit")) || (str1.equals("reset")))
    {
      str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
      if (str2 == null) {
        if (str1.equals("submit")) {
          str2 = UIManager.getString("FormView.submitButtonText");
        } else {
          str2 = UIManager.getString("FormView.resetButtonText");
        }
      }
      localObject3 = new JButton(str2);
      if (paramObject != null)
      {
        ((JButton)localObject3).setModel((ButtonModel)paramObject);
        ((JButton)localObject3).addActionListener(this);
      }
      localObject1 = localObject3;
      this.maxIsPreferred = 3;
    }
    else
    {
      Object localObject4;
      if (str1.equals("image"))
      {
        str2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.SRC);
        try
        {
          URL localURL = ((HTMLDocument)getElement().getDocument()).getBase();
          localObject4 = new URL(localURL, str2);
          ImageIcon localImageIcon = new ImageIcon((URL)localObject4);
          localObject3 = new JButton(localImageIcon);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localObject3 = new JButton(str2);
        }
        if (paramObject != null)
        {
          ((JButton)localObject3).setModel((ButtonModel)paramObject);
          ((JButton)localObject3).addMouseListener(new MouseEventListener());
        }
        localObject1 = localObject3;
        this.maxIsPreferred = 3;
      }
      else if (str1.equals("checkbox"))
      {
        localObject1 = new JCheckBox();
        if (paramObject != null) {
          ((JCheckBox)localObject1).setModel((JToggleButton.ToggleButtonModel)paramObject);
        }
        this.maxIsPreferred = 3;
      }
      else if (str1.equals("radio"))
      {
        localObject1 = new JRadioButton();
        if (paramObject != null) {
          ((JRadioButton)localObject1).setModel((JToggleButton.ToggleButtonModel)paramObject);
        }
        this.maxIsPreferred = 3;
      }
      else if (str1.equals("text"))
      {
        int i = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
        if (i > 0)
        {
          localObject3 = new JTextField();
          ((JTextField)localObject3).setColumns(i);
        }
        else
        {
          localObject3 = new JTextField();
          ((JTextField)localObject3).setColumns(20);
        }
        localObject1 = localObject3;
        if (paramObject != null) {
          ((JTextField)localObject3).setDocument((Document)paramObject);
        }
        ((JTextField)localObject3).addActionListener(this);
        this.maxIsPreferred = 3;
      }
      else
      {
        Object localObject2;
        int j;
        if (str1.equals("password"))
        {
          localObject2 = new JPasswordField();
          localObject1 = localObject2;
          if (paramObject != null) {
            ((JPasswordField)localObject2).setDocument((Document)paramObject);
          }
          j = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
          ((JPasswordField)localObject2).setColumns(j > 0 ? j : 20);
          ((JPasswordField)localObject2).addActionListener(this);
          this.maxIsPreferred = 3;
        }
        else if (str1.equals("file"))
        {
          localObject2 = new JTextField();
          if (paramObject != null) {
            ((JTextField)localObject2).setDocument((Document)paramObject);
          }
          j = HTML.getIntegerAttributeValue(paramAttributeSet, HTML.Attribute.SIZE, -1);
          ((JTextField)localObject2).setColumns(j > 0 ? j : 20);
          JButton localJButton = new JButton(UIManager.getString("FormView.browseFileButtonText"));
          localObject4 = Box.createHorizontalBox();
          ((Box)localObject4).add((Component)localObject2);
          ((Box)localObject4).add(Box.createHorizontalStrut(5));
          ((Box)localObject4).add(localJButton);
          localJButton.addActionListener(new BrowseFileAction(paramAttributeSet, (Document)paramObject));
          localObject1 = localObject4;
          this.maxIsPreferred = 3;
        }
      }
    }
    return localObject1;
  }
  
  private void removeStaleListenerForModel(Object paramObject)
  {
    Object localObject1;
    String str;
    if ((paramObject instanceof DefaultButtonModel))
    {
      localObject1 = (DefaultButtonModel)paramObject;
      str = "javax.swing.AbstractButton$Handler";
      ActionListener localActionListener;
      for (localActionListener : ((DefaultButtonModel)localObject1).getActionListeners()) {
        if (str.equals(localActionListener.getClass().getName())) {
          ((DefaultButtonModel)localObject1).removeActionListener(localActionListener);
        }
      }
      for (localActionListener : ((DefaultButtonModel)localObject1).getChangeListeners()) {
        if (str.equals(localActionListener.getClass().getName())) {
          ((DefaultButtonModel)localObject1).removeChangeListener(localActionListener);
        }
      }
      for (localActionListener : ((DefaultButtonModel)localObject1).getItemListeners()) {
        if (str.equals(localActionListener.getClass().getName())) {
          ((DefaultButtonModel)localObject1).removeItemListener(localActionListener);
        }
      }
    }
    else
    {
      ListDataListener localListDataListener;
      if ((paramObject instanceof AbstractListModel))
      {
        localObject1 = (AbstractListModel)paramObject;
        str = "javax.swing.plaf.basic.BasicListUI$Handler";
        ??? = "javax.swing.plaf.basic.BasicComboBoxUI$Handler";
        for (localListDataListener : ((AbstractListModel)localObject1).getListDataListeners()) {
          if ((str.equals(localListDataListener.getClass().getName())) || (((String)???).equals(localListDataListener.getClass().getName()))) {
            ((AbstractListModel)localObject1).removeListDataListener(localListDataListener);
          }
        }
      }
      else if ((paramObject instanceof AbstractDocument))
      {
        localObject1 = "javax.swing.plaf.basic.BasicTextUI$UpdateHandler";
        str = "javax.swing.text.DefaultCaret$Handler";
        ??? = (AbstractDocument)paramObject;
        for (localListDataListener : ((AbstractDocument)???).getDocumentListeners()) {
          if ((((String)localObject1).equals(localListDataListener.getClass().getName())) || (str.equals(localListDataListener.getClass().getName()))) {
            ((AbstractDocument)???).removeDocumentListener(localListDataListener);
          }
        }
      }
    }
  }
  
  public float getMaximumSpan(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      if ((this.maxIsPreferred & 0x1) == 1)
      {
        super.getMaximumSpan(paramInt);
        return getPreferredSpan(paramInt);
      }
      return super.getMaximumSpan(paramInt);
    case 1: 
      if ((this.maxIsPreferred & 0x2) == 2)
      {
        super.getMaximumSpan(paramInt);
        return getPreferredSpan(paramInt);
      }
      return super.getMaximumSpan(paramInt);
    }
    return super.getMaximumSpan(paramInt);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Element localElement = getElement();
    StringBuilder localStringBuilder = new StringBuilder();
    HTMLDocument localHTMLDocument = (HTMLDocument)getDocument();
    AttributeSet localAttributeSet = localElement.getAttributes();
    String str = (String)localAttributeSet.getAttribute(HTML.Attribute.TYPE);
    if (str.equals("submit"))
    {
      getFormData(localStringBuilder);
      submitData(localStringBuilder.toString());
    }
    else if (str.equals("reset"))
    {
      resetForm();
    }
    else if ((str.equals("text")) || (str.equals("password")))
    {
      if (isLastTextOrPasswordField())
      {
        getFormData(localStringBuilder);
        submitData(localStringBuilder.toString());
      }
      else
      {
        getComponent().transferFocus();
      }
    }
  }
  
  protected void submitData(String paramString)
  {
    Element localElement = getFormElement();
    AttributeSet localAttributeSet = localElement.getAttributes();
    HTMLDocument localHTMLDocument = (HTMLDocument)localElement.getDocument();
    URL localURL1 = localHTMLDocument.getBase();
    String str1 = (String)localAttributeSet.getAttribute(HTML.Attribute.TARGET);
    if (str1 == null) {
      str1 = "_self";
    }
    String str2 = (String)localAttributeSet.getAttribute(HTML.Attribute.METHOD);
    if (str2 == null) {
      str2 = "GET";
    }
    str2 = str2.toLowerCase();
    boolean bool = str2.equals("post");
    if (bool) {
      storePostData(localHTMLDocument, str1, paramString);
    }
    String str3 = (String)localAttributeSet.getAttribute(HTML.Attribute.ACTION);
    URL localURL2;
    try
    {
      localURL2 = str3 == null ? new URL(localURL1.getProtocol(), localURL1.getHost(), localURL1.getPort(), localURL1.getFile()) : new URL(localURL1, str3);
      if (!bool)
      {
        String str4 = paramString.toString();
        localURL2 = new URL(localURL2 + "?" + str4);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localURL2 = null;
    }
    final JEditorPane localJEditorPane = (JEditorPane)getContainer();
    HTMLEditorKit localHTMLEditorKit = (HTMLEditorKit)localJEditorPane.getEditorKit();
    FormSubmitEvent localFormSubmitEvent = null;
    if ((!localHTMLEditorKit.isAutoFormSubmission()) || (localHTMLDocument.isFrameDocument()))
    {
      localObject = bool ? FormSubmitEvent.MethodType.POST : FormSubmitEvent.MethodType.GET;
      localFormSubmitEvent = new FormSubmitEvent(this, HyperlinkEvent.EventType.ACTIVATED, localURL2, localElement, str1, (FormSubmitEvent.MethodType)localObject, paramString);
    }
    Object localObject = localFormSubmitEvent;
    final URL localURL3 = localURL2;
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        if (this.val$fse != null) {
          localJEditorPane.fireHyperlinkUpdate(this.val$fse);
        } else {
          try
          {
            localJEditorPane.setPage(localURL3);
          }
          catch (IOException localIOException)
          {
            UIManager.getLookAndFeel().provideErrorFeedback(localJEditorPane);
          }
        }
      }
    });
  }
  
  private void storePostData(HTMLDocument paramHTMLDocument, String paramString1, String paramString2)
  {
    Object localObject = paramHTMLDocument;
    String str = "javax.swing.JEditorPane.postdata";
    if (paramHTMLDocument.isFrameDocument())
    {
      FrameView.FrameEditorPane localFrameEditorPane = (FrameView.FrameEditorPane)getContainer();
      FrameView localFrameView = localFrameEditorPane.getFrameView();
      JEditorPane localJEditorPane = localFrameView.getOutermostJEditorPane();
      if (localJEditorPane != null)
      {
        localObject = localJEditorPane.getDocument();
        str = str + "." + paramString1;
      }
    }
    ((Document)localObject).putProperty(str, paramString2);
  }
  
  protected void imageSubmit(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Element localElement = getElement();
    HTMLDocument localHTMLDocument = (HTMLDocument)localElement.getDocument();
    getFormData(localStringBuilder);
    if (localStringBuilder.length() > 0) {
      localStringBuilder.append('&');
    }
    localStringBuilder.append(paramString);
    submitData(localStringBuilder.toString());
  }
  
  private String getImageData(Point paramPoint)
  {
    String str1 = paramPoint.x + ":" + paramPoint.y;
    int i = str1.indexOf(':');
    String str2 = str1.substring(0, i);
    String str3 = str1.substring(++i);
    String str4 = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
    String str5;
    if ((str4 == null) || (str4.equals("")))
    {
      str5 = "x=" + str2 + "&y=" + str3;
    }
    else
    {
      str4 = URLEncoder.encode(str4);
      str5 = str4 + ".x" + "=" + str2 + "&" + str4 + ".y" + "=" + str3;
    }
    return str5;
  }
  
  private Element getFormElement()
  {
    for (Element localElement = getElement(); localElement != null; localElement = localElement.getParentElement()) {
      if (localElement.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM) {
        return localElement;
      }
    }
    return null;
  }
  
  private void getFormData(StringBuilder paramStringBuilder)
  {
    Element localElement1 = getFormElement();
    if (localElement1 != null)
    {
      ElementIterator localElementIterator = new ElementIterator(localElement1);
      Element localElement2;
      while ((localElement2 = localElementIterator.next()) != null) {
        if (isControl(localElement2))
        {
          String str = (String)localElement2.getAttributes().getAttribute(HTML.Attribute.TYPE);
          if (((str == null) || (!str.equals("submit")) || (localElement2 == getElement())) && ((str == null) || (!str.equals("image")))) {
            loadElementDataIntoBuffer(localElement2, paramStringBuilder);
          }
        }
      }
    }
  }
  
  private void loadElementDataIntoBuffer(Element paramElement, StringBuilder paramStringBuilder)
  {
    AttributeSet localAttributeSet = paramElement.getAttributes();
    String str1 = (String)localAttributeSet.getAttribute(HTML.Attribute.NAME);
    if (str1 == null) {
      return;
    }
    String str2 = null;
    HTML.Tag localTag = (HTML.Tag)paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (localTag == HTML.Tag.INPUT) {
      str2 = getInputElementData(localAttributeSet);
    } else if (localTag == HTML.Tag.TEXTAREA) {
      str2 = getTextAreaData(localAttributeSet);
    } else if (localTag == HTML.Tag.SELECT) {
      loadSelectData(localAttributeSet, paramStringBuilder);
    }
    if ((str1 != null) && (str2 != null)) {
      appendBuffer(paramStringBuilder, str1, str2);
    }
  }
  
  private String getInputElementData(AttributeSet paramAttributeSet)
  {
    Object localObject1 = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    String str1 = (String)paramAttributeSet.getAttribute(HTML.Attribute.TYPE);
    Object localObject2 = null;
    Object localObject3;
    if ((str1.equals("text")) || (str1.equals("password")))
    {
      localObject3 = (Document)localObject1;
      try
      {
        localObject2 = ((Document)localObject3).getText(0, ((Document)localObject3).getLength());
      }
      catch (BadLocationException localBadLocationException1)
      {
        localObject2 = null;
      }
    }
    else if ((str1.equals("submit")) || (str1.equals("hidden")))
    {
      localObject2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
      if (localObject2 == null) {
        localObject2 = "";
      }
    }
    else if ((str1.equals("radio")) || (str1.equals("checkbox")))
    {
      localObject3 = (ButtonModel)localObject1;
      if (((ButtonModel)localObject3).isSelected())
      {
        localObject2 = (String)paramAttributeSet.getAttribute(HTML.Attribute.VALUE);
        if (localObject2 == null) {
          localObject2 = "on";
        }
      }
    }
    else if (str1.equals("file"))
    {
      localObject3 = (Document)localObject1;
      String str2;
      try
      {
        str2 = ((Document)localObject3).getText(0, ((Document)localObject3).getLength());
      }
      catch (BadLocationException localBadLocationException2)
      {
        str2 = null;
      }
      if ((str2 != null) && (str2.length() > 0)) {
        localObject2 = str2;
      }
    }
    return localObject2;
  }
  
  private String getTextAreaData(AttributeSet paramAttributeSet)
  {
    Document localDocument = (Document)paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    try
    {
      return localDocument.getText(0, localDocument.getLength());
    }
    catch (BadLocationException localBadLocationException) {}
    return null;
  }
  
  private void loadSelectData(AttributeSet paramAttributeSet, StringBuilder paramStringBuilder)
  {
    String str = (String)paramAttributeSet.getAttribute(HTML.Attribute.NAME);
    if (str == null) {
      return;
    }
    Object localObject1 = paramAttributeSet.getAttribute(StyleConstants.ModelAttribute);
    Object localObject2;
    if ((localObject1 instanceof OptionListModel))
    {
      localObject2 = (OptionListModel)localObject1;
      for (int i = 0; i < ((OptionListModel)localObject2).getSize(); i++) {
        if (((OptionListModel)localObject2).isSelectedIndex(i))
        {
          Option localOption2 = (Option)((OptionListModel)localObject2).getElementAt(i);
          appendBuffer(paramStringBuilder, str, localOption2.getValue());
        }
      }
    }
    else if ((localObject1 instanceof ComboBoxModel))
    {
      localObject2 = (ComboBoxModel)localObject1;
      Option localOption1 = (Option)((ComboBoxModel)localObject2).getSelectedItem();
      if (localOption1 != null) {
        appendBuffer(paramStringBuilder, str, localOption1.getValue());
      }
    }
  }
  
  private void appendBuffer(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    if (paramStringBuilder.length() > 0) {
      paramStringBuilder.append('&');
    }
    String str1 = URLEncoder.encode(paramString1);
    paramStringBuilder.append(str1);
    paramStringBuilder.append('=');
    String str2 = URLEncoder.encode(paramString2);
    paramStringBuilder.append(str2);
  }
  
  private boolean isControl(Element paramElement)
  {
    return paramElement.isLeaf();
  }
  
  boolean isLastTextOrPasswordField()
  {
    Element localElement1 = getFormElement();
    Element localElement2 = getElement();
    if (localElement1 != null)
    {
      ElementIterator localElementIterator = new ElementIterator(localElement1);
      int i = 0;
      Element localElement3;
      while ((localElement3 = localElementIterator.next()) != null) {
        if (localElement3 == localElement2)
        {
          i = 1;
        }
        else if ((i != 0) && (isControl(localElement3)))
        {
          AttributeSet localAttributeSet = localElement3.getAttributes();
          if (HTMLDocument.matchNameAttribute(localAttributeSet, HTML.Tag.INPUT))
          {
            String str = (String)localAttributeSet.getAttribute(HTML.Attribute.TYPE);
            if (("text".equals(str)) || ("password".equals(str))) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }
  
  void resetForm()
  {
    Element localElement1 = getFormElement();
    if (localElement1 != null)
    {
      ElementIterator localElementIterator = new ElementIterator(localElement1);
      Element localElement2;
      while ((localElement2 = localElementIterator.next()) != null) {
        if (isControl(localElement2))
        {
          AttributeSet localAttributeSet = localElement2.getAttributes();
          Object localObject1 = localAttributeSet.getAttribute(StyleConstants.ModelAttribute);
          Object localObject2;
          if ((localObject1 instanceof TextAreaDocument))
          {
            localObject2 = (TextAreaDocument)localObject1;
            ((TextAreaDocument)localObject2).reset();
          }
          else if ((localObject1 instanceof PlainDocument))
          {
            try
            {
              localObject2 = (PlainDocument)localObject1;
              ((PlainDocument)localObject2).remove(0, ((PlainDocument)localObject2).getLength());
              if (HTMLDocument.matchNameAttribute(localAttributeSet, HTML.Tag.INPUT))
              {
                String str = (String)localAttributeSet.getAttribute(HTML.Attribute.VALUE);
                if (str != null) {
                  ((PlainDocument)localObject2).insertString(0, str, null);
                }
              }
            }
            catch (BadLocationException localBadLocationException) {}
          }
          else
          {
            Object localObject3;
            if ((localObject1 instanceof OptionListModel))
            {
              localObject3 = (OptionListModel)localObject1;
              int i = ((OptionListModel)localObject3).getSize();
              for (int j = 0; j < i; j++) {
                ((OptionListModel)localObject3).removeIndexInterval(j, j);
              }
              BitSet localBitSet = ((OptionListModel)localObject3).getInitialSelection();
              for (int k = 0; k < localBitSet.size(); k++) {
                if (localBitSet.get(k)) {
                  ((OptionListModel)localObject3).addSelectionInterval(k, k);
                }
              }
            }
            else
            {
              Object localObject4;
              if ((localObject1 instanceof OptionComboBoxModel))
              {
                localObject3 = (OptionComboBoxModel)localObject1;
                localObject4 = ((OptionComboBoxModel)localObject3).getInitialSelection();
                if (localObject4 != null) {
                  ((OptionComboBoxModel)localObject3).setSelectedItem(localObject4);
                }
              }
              else if ((localObject1 instanceof JToggleButton.ToggleButtonModel))
              {
                boolean bool = (String)localAttributeSet.getAttribute(HTML.Attribute.CHECKED) != null;
                localObject4 = (JToggleButton.ToggleButtonModel)localObject1;
                ((JToggleButton.ToggleButtonModel)localObject4).setSelected(bool);
              }
            }
          }
        }
      }
    }
  }
  
  private class BrowseFileAction
    implements ActionListener
  {
    private AttributeSet attrs;
    private Document model;
    
    BrowseFileAction(AttributeSet paramAttributeSet, Document paramDocument)
    {
      this.attrs = paramAttributeSet;
      this.model = paramDocument;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JFileChooser localJFileChooser = new JFileChooser();
      localJFileChooser.setMultiSelectionEnabled(false);
      if (localJFileChooser.showOpenDialog(FormView.this.getContainer()) == 0)
      {
        File localFile = localJFileChooser.getSelectedFile();
        if (localFile != null) {
          try
          {
            if (this.model.getLength() > 0) {
              this.model.remove(0, this.model.getLength());
            }
            this.model.insertString(0, localFile.getPath(), null);
          }
          catch (BadLocationException localBadLocationException) {}
        }
      }
    }
  }
  
  protected class MouseEventListener
    extends MouseAdapter
  {
    protected MouseEventListener() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      String str = FormView.this.getImageData(paramMouseEvent.getPoint());
      FormView.this.imageSubmit(str);
    }
  }
}
