package javax.swing.filechooser;

import java.io.File;

public abstract class FileFilter
{
  public FileFilter() {}
  
  public abstract boolean accept(File paramFile);
  
  public abstract String getDescription();
}
