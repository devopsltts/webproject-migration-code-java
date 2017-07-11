package java.util.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun.security.util.ManifestEntryVerifier;

public class JarInputStream
  extends ZipInputStream
{
  private Manifest man;
  private JarEntry first;
  private JarVerifier jv;
  private ManifestEntryVerifier mev;
  private final boolean doVerify;
  private boolean tryManifest;
  
  public JarInputStream(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, true);
  }
  
  public JarInputStream(InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    super(paramInputStream);
    this.doVerify = paramBoolean;
    JarEntry localJarEntry = (JarEntry)super.getNextEntry();
    if ((localJarEntry != null) && (localJarEntry.getName().equalsIgnoreCase("META-INF/"))) {
      localJarEntry = (JarEntry)super.getNextEntry();
    }
    this.first = checkManifest(localJarEntry);
  }
  
  private JarEntry checkManifest(JarEntry paramJarEntry)
    throws IOException
  {
    if ((paramJarEntry != null) && ("META-INF/MANIFEST.MF".equalsIgnoreCase(paramJarEntry.getName())))
    {
      this.man = new Manifest();
      byte[] arrayOfByte = getBytes(new BufferedInputStream(this));
      this.man.read(new ByteArrayInputStream(arrayOfByte));
      closeEntry();
      if (this.doVerify)
      {
        this.jv = new JarVerifier(arrayOfByte);
        this.mev = new ManifestEntryVerifier(this.man);
      }
      return (JarEntry)super.getNextEntry();
    }
    return paramJarEntry;
  }
  
  private byte[] getBytes(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte[' '];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(2048);
    int i;
    while ((i = paramInputStream.read(arrayOfByte, 0, arrayOfByte.length)) != -1) {
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
    return localByteArrayOutputStream.toByteArray();
  }
  
  public Manifest getManifest()
  {
    return this.man;
  }
  
  public ZipEntry getNextEntry()
    throws IOException
  {
    JarEntry localJarEntry;
    if (this.first == null)
    {
      localJarEntry = (JarEntry)super.getNextEntry();
      if (this.tryManifest)
      {
        localJarEntry = checkManifest(localJarEntry);
        this.tryManifest = false;
      }
    }
    else
    {
      localJarEntry = this.first;
      if (this.first.getName().equalsIgnoreCase("META-INF/INDEX.LIST")) {
        this.tryManifest = true;
      }
      this.first = null;
    }
    if ((this.jv != null) && (localJarEntry != null)) {
      if (this.jv.nothingToVerify() == true)
      {
        this.jv = null;
        this.mev = null;
      }
      else
      {
        this.jv.beginEntry(localJarEntry, this.mev);
      }
    }
    return localJarEntry;
  }
  
  public JarEntry getNextJarEntry()
    throws IOException
  {
    return (JarEntry)getNextEntry();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i;
    if (this.first == null) {
      i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    } else {
      i = -1;
    }
    if (this.jv != null) {
      this.jv.update(i, paramArrayOfByte, paramInt1, paramInt2, this.mev);
    }
    return i;
  }
  
  protected ZipEntry createZipEntry(String paramString)
  {
    JarEntry localJarEntry = new JarEntry(paramString);
    if (this.man != null) {
      localJarEntry.attr = this.man.getAttributes(paramString);
    }
    return localJarEntry;
  }
}
