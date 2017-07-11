package javax.swing.filechooser;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.UIManager;

class UnixFileSystemView
  extends FileSystemView
{
  private static final String newFolderString = UIManager.getString("FileChooser.other.newFolder");
  private static final String newFolderNextString = UIManager.getString("FileChooser.other.newFolder.subsequent");
  
  UnixFileSystemView() {}
  
  public File createNewFolder(File paramFile)
    throws IOException
  {
    if (paramFile == null) {
      throw new IOException("Containing directory is null:");
    }
    File localFile = createFileObject(paramFile, newFolderString);
    for (int i = 1; (localFile.exists()) && (i < 100); i++) {
      localFile = createFileObject(paramFile, MessageFormat.format(newFolderNextString, new Object[] { new Integer(i) }));
    }
    if (localFile.exists()) {
      throw new IOException("Directory already exists:" + localFile.getAbsolutePath());
    }
    localFile.mkdirs();
    return localFile;
  }
  
  public boolean isFileSystemRoot(File paramFile)
  {
    return (paramFile != null) && (paramFile.getAbsolutePath().equals("/"));
  }
  
  public boolean isDrive(File paramFile)
  {
    return isFloppyDrive(paramFile);
  }
  
  public boolean isFloppyDrive(File paramFile)
  {
    return false;
  }
  
  public boolean isComputerNode(File paramFile)
  {
    if (paramFile != null)
    {
      String str = paramFile.getParent();
      if ((str != null) && (str.equals("/net"))) {
        return true;
      }
    }
    return false;
  }
}
