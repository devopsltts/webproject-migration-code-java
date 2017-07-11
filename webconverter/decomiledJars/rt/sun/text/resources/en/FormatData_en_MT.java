package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_MT
  extends ParallelListResourceBundle
{
  public FormatData_en_MT() {}
  
  protected final Object[][] getContents()
  {
    return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "dd/MM/yyyy" } }, { "DateTimePatterns", { "{1} {0}" } } };
  }
}
