package java.io;

@FunctionalInterface
public abstract interface FilenameFilter
{
  public abstract boolean accept(File paramFile, String paramString);
}
