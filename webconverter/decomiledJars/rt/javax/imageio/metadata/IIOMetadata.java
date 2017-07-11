package javax.imageio.metadata;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.w3c.dom.Node;

public abstract class IIOMetadata
{
  protected boolean standardFormatSupported;
  protected String nativeMetadataFormatName = null;
  protected String nativeMetadataFormatClassName = null;
  protected String[] extraMetadataFormatNames = null;
  protected String[] extraMetadataFormatClassNames = null;
  protected IIOMetadataController defaultController = null;
  protected IIOMetadataController controller = null;
  
  protected IIOMetadata() {}
  
  protected IIOMetadata(boolean paramBoolean, String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    this.standardFormatSupported = paramBoolean;
    this.nativeMetadataFormatName = paramString1;
    this.nativeMetadataFormatClassName = paramString2;
    if (paramArrayOfString1 != null)
    {
      if (paramArrayOfString1.length == 0) {
        throw new IllegalArgumentException("extraMetadataFormatNames.length == 0!");
      }
      if (paramArrayOfString2 == null) {
        throw new IllegalArgumentException("extraMetadataFormatNames != null && extraMetadataFormatClassNames == null!");
      }
      if (paramArrayOfString2.length != paramArrayOfString1.length) {
        throw new IllegalArgumentException("extraMetadataFormatClassNames.length != extraMetadataFormatNames.length!");
      }
      this.extraMetadataFormatNames = ((String[])paramArrayOfString1.clone());
      this.extraMetadataFormatClassNames = ((String[])paramArrayOfString2.clone());
    }
    else if (paramArrayOfString2 != null)
    {
      throw new IllegalArgumentException("extraMetadataFormatNames == null && extraMetadataFormatClassNames != null!");
    }
  }
  
  public boolean isStandardMetadataFormatSupported()
  {
    return this.standardFormatSupported;
  }
  
  public abstract boolean isReadOnly();
  
  public String getNativeMetadataFormatName()
  {
    return this.nativeMetadataFormatName;
  }
  
  public String[] getExtraMetadataFormatNames()
  {
    if (this.extraMetadataFormatNames == null) {
      return null;
    }
    return (String[])this.extraMetadataFormatNames.clone();
  }
  
  public String[] getMetadataFormatNames()
  {
    String str = getNativeMetadataFormatName();
    Object localObject = isStandardMetadataFormatSupported() ? "javax_imageio_1.0" : null;
    String[] arrayOfString1 = getExtraMetadataFormatNames();
    int i = 0;
    if (str != null) {
      i++;
    }
    if (localObject != null) {
      i++;
    }
    if (arrayOfString1 != null) {
      i += arrayOfString1.length;
    }
    if (i == 0) {
      return null;
    }
    String[] arrayOfString2 = new String[i];
    int j = 0;
    if (str != null) {
      arrayOfString2[(j++)] = str;
    }
    if (localObject != null) {
      arrayOfString2[(j++)] = localObject;
    }
    if (arrayOfString1 != null) {
      for (int k = 0; k < arrayOfString1.length; k++) {
        arrayOfString2[(j++)] = arrayOfString1[k];
      }
    }
    return arrayOfString2;
  }
  
  public IIOMetadataFormat getMetadataFormat(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("formatName == null!");
    }
    if ((this.standardFormatSupported) && (paramString.equals("javax_imageio_1.0"))) {
      return IIOMetadataFormatImpl.getStandardFormatInstance();
    }
    String str = null;
    if (paramString.equals(this.nativeMetadataFormatName)) {
      str = this.nativeMetadataFormatClassName;
    } else if (this.extraMetadataFormatNames != null) {
      for (int i = 0; i < this.extraMetadataFormatNames.length; i++) {
        if (paramString.equals(this.extraMetadataFormatNames[i]))
        {
          str = this.extraMetadataFormatClassNames[i];
          break;
        }
      }
    }
    if (str == null) {
      throw new IllegalArgumentException("Unsupported format name");
    }
    try
    {
      Class localClass = null;
      localObject = this;
      ClassLoader localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return localObject.getClass().getClassLoader();
        }
      });
      try
      {
        localClass = Class.forName(str, true, localClassLoader);
      }
      catch (ClassNotFoundException localClassNotFoundException1)
      {
        localClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            return Thread.currentThread().getContextClassLoader();
          }
        });
        try
        {
          localClass = Class.forName(str, true, localClassLoader);
        }
        catch (ClassNotFoundException localClassNotFoundException2)
        {
          localClass = Class.forName(str, true, ClassLoader.getSystemClassLoader());
        }
      }
      Method localMethod = localClass.getMethod("getInstance", new Class[0]);
      return (IIOMetadataFormat)localMethod.invoke(null, new Object[0]);
    }
    catch (Exception localException)
    {
      final Object localObject = new IllegalStateException("Can't obtain format");
      ((RuntimeException)localObject).initCause(localException);
      throw ((Throwable)localObject);
    }
  }
  
  public abstract Node getAsTree(String paramString);
  
  public abstract void mergeTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException;
  
  protected IIOMetadataNode getStandardChromaNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardCompressionNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardDataNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardDimensionNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardDocumentNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardTextNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardTileNode()
  {
    return null;
  }
  
  protected IIOMetadataNode getStandardTransparencyNode()
  {
    return null;
  }
  
  private void append(IIOMetadataNode paramIIOMetadataNode1, IIOMetadataNode paramIIOMetadataNode2)
  {
    if (paramIIOMetadataNode2 != null) {
      paramIIOMetadataNode1.appendChild(paramIIOMetadataNode2);
    }
  }
  
  protected final IIOMetadataNode getStandardTree()
  {
    IIOMetadataNode localIIOMetadataNode = new IIOMetadataNode("javax_imageio_1.0");
    append(localIIOMetadataNode, getStandardChromaNode());
    append(localIIOMetadataNode, getStandardCompressionNode());
    append(localIIOMetadataNode, getStandardDataNode());
    append(localIIOMetadataNode, getStandardDimensionNode());
    append(localIIOMetadataNode, getStandardDocumentNode());
    append(localIIOMetadataNode, getStandardTextNode());
    append(localIIOMetadataNode, getStandardTileNode());
    append(localIIOMetadataNode, getStandardTransparencyNode());
    return localIIOMetadataNode;
  }
  
  public void setFromTree(String paramString, Node paramNode)
    throws IIOInvalidTreeException
  {
    reset();
    mergeTree(paramString, paramNode);
  }
  
  public abstract void reset();
  
  public void setController(IIOMetadataController paramIIOMetadataController)
  {
    this.controller = paramIIOMetadataController;
  }
  
  public IIOMetadataController getController()
  {
    return this.controller;
  }
  
  public IIOMetadataController getDefaultController()
  {
    return this.defaultController;
  }
  
  public boolean hasController()
  {
    return getController() != null;
  }
  
  public boolean activateController()
  {
    if (!hasController()) {
      throw new IllegalStateException("hasController() == false!");
    }
    return getController().activate(this);
  }
}
