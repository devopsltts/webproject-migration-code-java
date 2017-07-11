package java.nio.file;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public final class DirectoryIteratorException
  extends ConcurrentModificationException
{
  private static final long serialVersionUID = -6012699886086212874L;
  
  public DirectoryIteratorException(IOException paramIOException)
  {
    super((Throwable)Objects.requireNonNull(paramIOException));
  }
  
  public IOException getCause()
  {
    return (IOException)super.getCause();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Throwable localThrowable = super.getCause();
    if (!(localThrowable instanceof IOException)) {
      throw new InvalidObjectException("Cause must be an IOException");
    }
  }
}
