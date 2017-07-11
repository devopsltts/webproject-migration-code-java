package java.nio.file.spi;

import java.io.IOException;
import java.nio.file.Path;

public abstract class FileTypeDetector
{
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new RuntimePermission("fileTypeDetector"));
    }
    return null;
  }
  
  private FileTypeDetector(Void paramVoid) {}
  
  protected FileTypeDetector()
  {
    this(checkPermission());
  }
  
  public abstract String probeContentType(Path paramPath)
    throws IOException;
}
