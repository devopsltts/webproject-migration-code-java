package javax.swing.plaf;

import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public abstract class FileChooserUI
  extends ComponentUI
{
  public FileChooserUI() {}
  
  public abstract FileFilter getAcceptAllFileFilter(JFileChooser paramJFileChooser);
  
  public abstract FileView getFileView(JFileChooser paramJFileChooser);
  
  public abstract String getApproveButtonText(JFileChooser paramJFileChooser);
  
  public abstract String getDialogTitle(JFileChooser paramJFileChooser);
  
  public abstract void rescanCurrentDirectory(JFileChooser paramJFileChooser);
  
  public abstract void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile);
  
  public JButton getDefaultButton(JFileChooser paramJFileChooser)
  {
    return null;
  }
}
