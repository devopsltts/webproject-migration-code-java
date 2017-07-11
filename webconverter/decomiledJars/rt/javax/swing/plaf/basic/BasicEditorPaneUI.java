package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicEditorPaneUI
  extends BasicTextUI
{
  private static final String FONT_ATTRIBUTE_KEY = "FONT_ATTRIBUTE_KEY";
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicEditorPaneUI();
  }
  
  public BasicEditorPaneUI() {}
  
  protected String getPropertyPrefix()
  {
    return "EditorPane";
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    updateDisplayProperties(paramJComponent.getFont(), paramJComponent.getForeground());
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    cleanDisplayProperties();
    super.uninstallUI(paramJComponent);
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent)
  {
    JEditorPane localJEditorPane = (JEditorPane)getComponent();
    return localJEditorPane.getEditorKit();
  }
  
  ActionMap getActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    localActionMapUIResource.put("requestFocus", new BasicTextUI.FocusAction(this));
    EditorKit localEditorKit = getEditorKit(getComponent());
    if (localEditorKit != null)
    {
      Action[] arrayOfAction = localEditorKit.getActions();
      if (arrayOfAction != null) {
        addActions(localActionMapUIResource, arrayOfAction);
      }
    }
    localActionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    localActionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    localActionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    return localActionMapUIResource;
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    Object localObject1;
    Object localObject2;
    if ("editorKit".equals(str))
    {
      localObject1 = SwingUtilities.getUIActionMap(getComponent());
      if (localObject1 != null)
      {
        localObject2 = paramPropertyChangeEvent.getOldValue();
        if ((localObject2 instanceof EditorKit))
        {
          localObject3 = ((EditorKit)localObject2).getActions();
          if (localObject3 != null) {
            removeActions((ActionMap)localObject1, (Action[])localObject3);
          }
        }
        Object localObject3 = paramPropertyChangeEvent.getNewValue();
        if ((localObject3 instanceof EditorKit))
        {
          Action[] arrayOfAction = ((EditorKit)localObject3).getActions();
          if (arrayOfAction != null) {
            addActions((ActionMap)localObject1, arrayOfAction);
          }
        }
      }
      updateFocusTraversalKeys();
    }
    else if ("editable".equals(str))
    {
      updateFocusTraversalKeys();
    }
    else if (("foreground".equals(str)) || ("font".equals(str)) || ("document".equals(str)) || ("JEditorPane.w3cLengthUnits".equals(str)) || ("JEditorPane.honorDisplayProperties".equals(str)))
    {
      localObject1 = getComponent();
      updateDisplayProperties(((JComponent)localObject1).getFont(), ((JComponent)localObject1).getForeground());
      if (("JEditorPane.w3cLengthUnits".equals(str)) || ("JEditorPane.honorDisplayProperties".equals(str))) {
        modelChanged();
      }
      if ("foreground".equals(str))
      {
        localObject2 = ((JComponent)localObject1).getClientProperty("JEditorPane.honorDisplayProperties");
        boolean bool = false;
        if ((localObject2 instanceof Boolean)) {
          bool = ((Boolean)localObject2).booleanValue();
        }
        if (bool) {
          modelChanged();
        }
      }
    }
  }
  
  void removeActions(ActionMap paramActionMap, Action[] paramArrayOfAction)
  {
    int i = paramArrayOfAction.length;
    for (int j = 0; j < i; j++)
    {
      Action localAction = paramArrayOfAction[j];
      paramActionMap.remove(localAction.getValue("Name"));
    }
  }
  
  void addActions(ActionMap paramActionMap, Action[] paramArrayOfAction)
  {
    int i = paramArrayOfAction.length;
    for (int j = 0; j < i; j++)
    {
      Action localAction = paramArrayOfAction[j];
      paramActionMap.put(localAction.getValue("Name"), localAction);
    }
  }
  
  void updateDisplayProperties(Font paramFont, Color paramColor)
  {
    JTextComponent localJTextComponent = getComponent();
    Object localObject1 = localJTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    boolean bool1 = false;
    Object localObject2 = localJTextComponent.getClientProperty("JEditorPane.w3cLengthUnits");
    boolean bool2 = false;
    if ((localObject1 instanceof Boolean)) {
      bool1 = ((Boolean)localObject1).booleanValue();
    }
    if ((localObject2 instanceof Boolean)) {
      bool2 = ((Boolean)localObject2).booleanValue();
    }
    Document localDocument;
    if (((this instanceof BasicTextPaneUI)) || (bool1))
    {
      localDocument = getComponent().getDocument();
      if ((localDocument instanceof StyledDocument)) {
        if (((localDocument instanceof HTMLDocument)) && (bool1)) {
          updateCSS(paramFont, paramColor);
        } else {
          updateStyle(paramFont, paramColor);
        }
      }
    }
    else
    {
      cleanDisplayProperties();
    }
    StyleSheet localStyleSheet;
    if (bool2)
    {
      localDocument = getComponent().getDocument();
      if ((localDocument instanceof HTMLDocument))
      {
        localStyleSheet = ((HTMLDocument)localDocument).getStyleSheet();
        localStyleSheet.addRule("W3C_LENGTH_UNITS_ENABLE");
      }
    }
    else
    {
      localDocument = getComponent().getDocument();
      if ((localDocument instanceof HTMLDocument))
      {
        localStyleSheet = ((HTMLDocument)localDocument).getStyleSheet();
        localStyleSheet.addRule("W3C_LENGTH_UNITS_DISABLE");
      }
    }
  }
  
  void cleanDisplayProperties()
  {
    Document localDocument = getComponent().getDocument();
    if ((localDocument instanceof HTMLDocument))
    {
      StyleSheet localStyleSheet1 = ((HTMLDocument)localDocument).getStyleSheet();
      StyleSheet[] arrayOfStyleSheet = localStyleSheet1.getStyleSheets();
      if (arrayOfStyleSheet != null) {
        for (StyleSheet localStyleSheet2 : arrayOfStyleSheet) {
          if ((localStyleSheet2 instanceof StyleSheetUIResource))
          {
            localStyleSheet1.removeStyleSheet(localStyleSheet2);
            localStyleSheet1.addRule("BASE_SIZE_DISABLE");
            break;
          }
        }
      }
      ??? = ((StyledDocument)localDocument).getStyle("default");
      if (((Style)???).getAttribute("FONT_ATTRIBUTE_KEY") != null) {
        ((Style)???).removeAttribute("FONT_ATTRIBUTE_KEY");
      }
    }
  }
  
  private void updateCSS(Font paramFont, Color paramColor)
  {
    JTextComponent localJTextComponent = getComponent();
    Document localDocument = localJTextComponent.getDocument();
    if ((localDocument instanceof HTMLDocument))
    {
      StyleSheetUIResource localStyleSheetUIResource = new StyleSheetUIResource();
      StyleSheet localStyleSheet1 = ((HTMLDocument)localDocument).getStyleSheet();
      StyleSheet[] arrayOfStyleSheet = localStyleSheet1.getStyleSheets();
      if (arrayOfStyleSheet != null) {
        for (StyleSheet localStyleSheet2 : arrayOfStyleSheet) {
          if ((localStyleSheet2 instanceof StyleSheetUIResource)) {
            localStyleSheet1.removeStyleSheet(localStyleSheet2);
          }
        }
      }
      ??? = SwingUtilities2.displayPropertiesToCSS(paramFont, paramColor);
      localStyleSheetUIResource.addRule((String)???);
      localStyleSheet1.addStyleSheet(localStyleSheetUIResource);
      localStyleSheet1.addRule("BASE_SIZE " + localJTextComponent.getFont().getSize());
      Style localStyle = ((StyledDocument)localDocument).getStyle("default");
      if (!paramFont.equals(localStyle.getAttribute("FONT_ATTRIBUTE_KEY"))) {
        localStyle.addAttribute("FONT_ATTRIBUTE_KEY", paramFont);
      }
    }
  }
  
  private void updateStyle(Font paramFont, Color paramColor)
  {
    updateFont(paramFont);
    updateForeground(paramColor);
  }
  
  private void updateForeground(Color paramColor)
  {
    StyledDocument localStyledDocument = (StyledDocument)getComponent().getDocument();
    Style localStyle = localStyledDocument.getStyle("default");
    if (localStyle == null) {
      return;
    }
    if (paramColor == null)
    {
      if (localStyle.getAttribute(StyleConstants.Foreground) != null) {
        localStyle.removeAttribute(StyleConstants.Foreground);
      }
    }
    else if (!paramColor.equals(StyleConstants.getForeground(localStyle))) {
      StyleConstants.setForeground(localStyle, paramColor);
    }
  }
  
  private void updateFont(Font paramFont)
  {
    StyledDocument localStyledDocument = (StyledDocument)getComponent().getDocument();
    Style localStyle = localStyledDocument.getStyle("default");
    if (localStyle == null) {
      return;
    }
    String str = (String)localStyle.getAttribute(StyleConstants.FontFamily);
    Integer localInteger = (Integer)localStyle.getAttribute(StyleConstants.FontSize);
    Boolean localBoolean1 = (Boolean)localStyle.getAttribute(StyleConstants.Bold);
    Boolean localBoolean2 = (Boolean)localStyle.getAttribute(StyleConstants.Italic);
    Font localFont = (Font)localStyle.getAttribute("FONT_ATTRIBUTE_KEY");
    if (paramFont == null)
    {
      if (str != null) {
        localStyle.removeAttribute(StyleConstants.FontFamily);
      }
      if (localInteger != null) {
        localStyle.removeAttribute(StyleConstants.FontSize);
      }
      if (localBoolean1 != null) {
        localStyle.removeAttribute(StyleConstants.Bold);
      }
      if (localBoolean2 != null) {
        localStyle.removeAttribute(StyleConstants.Italic);
      }
      if (localFont != null) {
        localStyle.removeAttribute("FONT_ATTRIBUTE_KEY");
      }
    }
    else
    {
      if (!paramFont.getName().equals(str)) {
        StyleConstants.setFontFamily(localStyle, paramFont.getName());
      }
      if ((localInteger == null) || (localInteger.intValue() != paramFont.getSize())) {
        StyleConstants.setFontSize(localStyle, paramFont.getSize());
      }
      if ((localBoolean1 == null) || (localBoolean1.booleanValue() != paramFont.isBold())) {
        StyleConstants.setBold(localStyle, paramFont.isBold());
      }
      if ((localBoolean2 == null) || (localBoolean2.booleanValue() != paramFont.isItalic())) {
        StyleConstants.setItalic(localStyle, paramFont.isItalic());
      }
      if (!paramFont.equals(localFont)) {
        localStyle.addAttribute("FONT_ATTRIBUTE_KEY", paramFont);
      }
    }
  }
  
  static class StyleSheetUIResource
    extends StyleSheet
    implements UIResource
  {
    StyleSheetUIResource() {}
  }
}
