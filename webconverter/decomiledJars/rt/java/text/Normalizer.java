package java.text;

import sun.text.normalizer.NormalizerBase;

public final class Normalizer
{
  private Normalizer() {}
  
  public static String normalize(CharSequence paramCharSequence, Form paramForm)
  {
    return NormalizerBase.normalize(paramCharSequence.toString(), paramForm);
  }
  
  public static boolean isNormalized(CharSequence paramCharSequence, Form paramForm)
  {
    return NormalizerBase.isNormalized(paramCharSequence.toString(), paramForm);
  }
  
  public static enum Form
  {
    NFD,  NFC,  NFKD,  NFKC;
    
    private Form() {}
  }
}
