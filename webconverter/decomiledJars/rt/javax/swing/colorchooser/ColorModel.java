package javax.swing.colorchooser;

import java.awt.Component;
import javax.swing.UIManager;

class ColorModel
{
  private final String prefix;
  private final String[] labels;
  
  ColorModel(String paramString, String... paramVarArgs)
  {
    this.prefix = ("ColorChooser." + paramString);
    this.labels = paramVarArgs;
  }
  
  ColorModel()
  {
    this("rgb", new String[] { "Red", "Green", "Blue", "Alpha" });
  }
  
  void setColor(int paramInt, float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[0] = normalize(paramInt >> 16);
    paramArrayOfFloat[1] = normalize(paramInt >> 8);
    paramArrayOfFloat[2] = normalize(paramInt);
    paramArrayOfFloat[3] = normalize(paramInt >> 24);
  }
  
  int getColor(float[] paramArrayOfFloat)
  {
    return to8bit(paramArrayOfFloat[2]) | to8bit(paramArrayOfFloat[1]) << 8 | to8bit(paramArrayOfFloat[0]) << 16 | to8bit(paramArrayOfFloat[3]) << 24;
  }
  
  int getCount()
  {
    return this.labels.length;
  }
  
  int getMinimum(int paramInt)
  {
    return 0;
  }
  
  int getMaximum(int paramInt)
  {
    return 255;
  }
  
  float getDefault(int paramInt)
  {
    return 0.0F;
  }
  
  final String getLabel(Component paramComponent, int paramInt)
  {
    return getText(paramComponent, this.labels[paramInt]);
  }
  
  private static float normalize(int paramInt)
  {
    return (paramInt & 0xFF) / 255.0F;
  }
  
  private static int to8bit(float paramFloat)
  {
    return (int)(255.0F * paramFloat);
  }
  
  final String getText(Component paramComponent, String paramString)
  {
    return UIManager.getString(this.prefix + paramString + "Text", paramComponent.getLocale());
  }
  
  final int getInteger(Component paramComponent, String paramString)
  {
    Object localObject = UIManager.get(this.prefix + paramString, paramComponent.getLocale());
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return -1;
  }
}
