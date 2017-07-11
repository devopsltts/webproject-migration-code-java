package sun.text;

import sun.text.normalizer.NormalizerBase;
import sun.text.normalizer.NormalizerBase.Mode;

public class CollatorUtilities
{
  static NormalizerBase.Mode[] legacyModeMap = { NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD };
  
  public CollatorUtilities() {}
  
  public static int toLegacyMode(NormalizerBase.Mode paramMode)
  {
    int i = legacyModeMap.length;
    while (i > 0)
    {
      i--;
      if (legacyModeMap[i] == paramMode) {
        break;
      }
    }
    return i;
  }
  
  public static NormalizerBase.Mode toNormalizerMode(int paramInt)
  {
    NormalizerBase.Mode localMode;
    try
    {
      localMode = legacyModeMap[paramInt];
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
    {
      localMode = NormalizerBase.NONE;
    }
    return localMode;
  }
}
