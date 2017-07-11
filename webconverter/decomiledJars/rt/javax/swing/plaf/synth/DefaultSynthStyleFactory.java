package javax.swing.plaf.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import javax.swing.JComponent;
import javax.swing.plaf.FontUIResource;
import sun.swing.BakedArrayList;
import sun.swing.plaf.synth.DefaultSynthStyle;
import sun.swing.plaf.synth.StyleAssociation;

class DefaultSynthStyleFactory
  extends SynthStyleFactory
{
  public static final int NAME = 0;
  public static final int REGION = 1;
  private List<StyleAssociation> _styles = new ArrayList();
  private BakedArrayList _tmpList = new BakedArrayList(5);
  private Map<BakedArrayList, SynthStyle> _resolvedStyles = new HashMap();
  private SynthStyle _defaultStyle;
  
  DefaultSynthStyleFactory() {}
  
  public synchronized void addStyle(DefaultSynthStyle paramDefaultSynthStyle, String paramString, int paramInt)
    throws PatternSyntaxException
  {
    if (paramString == null) {
      paramString = ".*";
    }
    if (paramInt == 0) {
      this._styles.add(StyleAssociation.createStyleAssociation(paramString, paramDefaultSynthStyle, paramInt));
    } else if (paramInt == 1) {
      this._styles.add(StyleAssociation.createStyleAssociation(paramString.toLowerCase(), paramDefaultSynthStyle, paramInt));
    }
  }
  
  public synchronized SynthStyle getStyle(JComponent paramJComponent, Region paramRegion)
  {
    BakedArrayList localBakedArrayList = this._tmpList;
    localBakedArrayList.clear();
    getMatchingStyles(localBakedArrayList, paramJComponent, paramRegion);
    if (localBakedArrayList.size() == 0) {
      return getDefaultStyle();
    }
    localBakedArrayList.cacheHashCode();
    SynthStyle localSynthStyle = getCachedStyle(localBakedArrayList);
    if (localSynthStyle == null)
    {
      localSynthStyle = mergeStyles(localBakedArrayList);
      if (localSynthStyle != null) {
        cacheStyle(localBakedArrayList, localSynthStyle);
      }
    }
    return localSynthStyle;
  }
  
  private SynthStyle getDefaultStyle()
  {
    if (this._defaultStyle == null)
    {
      this._defaultStyle = new DefaultSynthStyle();
      ((DefaultSynthStyle)this._defaultStyle).setFont(new FontUIResource("Dialog", 0, 12));
    }
    return this._defaultStyle;
  }
  
  private void getMatchingStyles(List paramList, JComponent paramJComponent, Region paramRegion)
  {
    String str1 = paramRegion.getLowerCaseName();
    String str2 = paramJComponent.getName();
    if (str2 == null) {
      str2 = "";
    }
    for (int i = this._styles.size() - 1; i >= 0; i--)
    {
      StyleAssociation localStyleAssociation = (StyleAssociation)this._styles.get(i);
      String str3;
      if (localStyleAssociation.getID() == 0) {
        str3 = str2;
      } else {
        str3 = str1;
      }
      if ((localStyleAssociation.matches(str3)) && (paramList.indexOf(localStyleAssociation.getStyle()) == -1)) {
        paramList.add(localStyleAssociation.getStyle());
      }
    }
  }
  
  private void cacheStyle(List paramList, SynthStyle paramSynthStyle)
  {
    BakedArrayList localBakedArrayList = new BakedArrayList(paramList);
    this._resolvedStyles.put(localBakedArrayList, paramSynthStyle);
  }
  
  private SynthStyle getCachedStyle(List paramList)
  {
    if (paramList.size() == 0) {
      return null;
    }
    return (SynthStyle)this._resolvedStyles.get(paramList);
  }
  
  private SynthStyle mergeStyles(List paramList)
  {
    int i = paramList.size();
    if (i == 0) {
      return null;
    }
    if (i == 1) {
      return (SynthStyle)((DefaultSynthStyle)paramList.get(0)).clone();
    }
    DefaultSynthStyle localDefaultSynthStyle = (DefaultSynthStyle)paramList.get(i - 1);
    localDefaultSynthStyle = (DefaultSynthStyle)localDefaultSynthStyle.clone();
    for (int j = i - 2; j >= 0; j--) {
      localDefaultSynthStyle = ((DefaultSynthStyle)paramList.get(j)).addTo(localDefaultSynthStyle);
    }
    return localDefaultSynthStyle;
  }
}
