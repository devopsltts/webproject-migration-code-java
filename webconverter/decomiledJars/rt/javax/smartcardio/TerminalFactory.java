package javax.smartcardio;

import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Collections;
import java.util.List;
import sun.security.action.GetPropertyAction;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;

public final class TerminalFactory
{
  private static final String PROP_NAME = "javax.smartcardio.TerminalFactory.DefaultType";
  private static final String defaultType;
  private static final TerminalFactory defaultFactory;
  private final TerminalFactorySpi spi;
  private final Provider provider;
  private final String type;
  
  private TerminalFactory(TerminalFactorySpi paramTerminalFactorySpi, Provider paramProvider, String paramString)
  {
    this.spi = paramTerminalFactorySpi;
    this.provider = paramProvider;
    this.type = paramString;
  }
  
  public static String getDefaultType()
  {
    return defaultType;
  }
  
  public static TerminalFactory getDefault()
  {
    return defaultFactory;
  }
  
  public static TerminalFactory getInstance(String paramString, Object paramObject)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, paramString, paramObject);
    return new TerminalFactory((TerminalFactorySpi)localInstance.impl, localInstance.provider, paramString);
  }
  
  public static TerminalFactory getInstance(String paramString1, Object paramObject, String paramString2)
    throws NoSuchAlgorithmException, NoSuchProviderException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, paramString1, paramObject, paramString2);
    return new TerminalFactory((TerminalFactorySpi)localInstance.impl, localInstance.provider, paramString1);
  }
  
  public static TerminalFactory getInstance(String paramString, Object paramObject, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    GetInstance.Instance localInstance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, paramString, paramObject, paramProvider);
    return new TerminalFactory((TerminalFactorySpi)localInstance.impl, localInstance.provider, paramString);
  }
  
  public Provider getProvider()
  {
    return this.provider;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public CardTerminals terminals()
  {
    return this.spi.engineTerminals();
  }
  
  public String toString()
  {
    return "TerminalFactory for type " + this.type + " from provider " + this.provider.getName();
  }
  
  static
  {
    String str = ((String)AccessController.doPrivileged(new GetPropertyAction("javax.smartcardio.TerminalFactory.DefaultType", "PC/SC"))).trim();
    TerminalFactory localTerminalFactory = null;
    try
    {
      localTerminalFactory = getInstance(str, null);
    }
    catch (Exception localException1) {}
    if (localTerminalFactory == null) {
      try
      {
        str = "PC/SC";
        Provider localProvider = Security.getProvider("SunPCSC");
        if (localProvider == null)
        {
          Class localClass = Class.forName("sun.security.smartcardio.SunPCSC");
          localProvider = (Provider)localClass.newInstance();
        }
        localTerminalFactory = getInstance(str, null, localProvider);
      }
      catch (Exception localException2) {}
    }
    if (localTerminalFactory == null)
    {
      str = "None";
      localTerminalFactory = new TerminalFactory(NoneFactorySpi.INSTANCE, NoneProvider.INSTANCE, "None");
    }
    defaultType = str;
    defaultFactory = localTerminalFactory;
  }
  
  private static final class NoneCardTerminals
    extends CardTerminals
  {
    static final CardTerminals INSTANCE = new NoneCardTerminals();
    
    private NoneCardTerminals() {}
    
    public List<CardTerminal> list(CardTerminals.State paramState)
      throws CardException
    {
      if (paramState == null) {
        throw new NullPointerException();
      }
      return Collections.emptyList();
    }
    
    public boolean waitForChange(long paramLong)
      throws CardException
    {
      throw new IllegalStateException("no terminals");
    }
  }
  
  private static final class NoneFactorySpi
    extends TerminalFactorySpi
  {
    static final TerminalFactorySpi INSTANCE = new NoneFactorySpi();
    
    private NoneFactorySpi() {}
    
    protected CardTerminals engineTerminals()
    {
      return TerminalFactory.NoneCardTerminals.INSTANCE;
    }
  }
  
  private static final class NoneProvider
    extends Provider
  {
    private static final long serialVersionUID = 2745808869881593918L;
    static final Provider INSTANCE = new NoneProvider();
    
    private NoneProvider()
    {
      super(1.0D, "none");
    }
  }
}
