package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class GeneralName
{
  private GeneralNameInterface name = null;
  
  public GeneralName(GeneralNameInterface paramGeneralNameInterface)
  {
    if (paramGeneralNameInterface == null) {
      throw new NullPointerException("GeneralName must not be null");
    }
    this.name = paramGeneralNameInterface;
  }
  
  public GeneralName(DerValue paramDerValue)
    throws IOException
  {
    this(paramDerValue, false);
  }
  
  public GeneralName(DerValue paramDerValue, boolean paramBoolean)
    throws IOException
  {
    int i = (short)(byte)(paramDerValue.tag & 0x1F);
    switch (i)
    {
    case 0: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)48);
        this.name = new OtherName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of Other-Name");
      }
      break;
    case 1: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        this.name = new RFC822Name(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of RFC822 name");
      }
      break;
    case 2: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        this.name = new DNSName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of DNS name");
      }
      break;
    case 6: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)22);
        this.name = (paramBoolean ? URIName.nameConstraint(paramDerValue) : new URIName(paramDerValue));
      }
      else
      {
        throw new IOException("Invalid encoding of URI");
      }
      break;
    case 7: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)4);
        this.name = new IPAddressName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of IP address");
      }
      break;
    case 8: 
      if ((paramDerValue.isContextSpecific()) && (!paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)6);
        this.name = new OIDName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of OID name");
      }
      break;
    case 4: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed())) {
        this.name = new X500Name(paramDerValue.getData());
      } else {
        throw new IOException("Invalid encoding of Directory name");
      }
      break;
    case 5: 
      if ((paramDerValue.isContextSpecific()) && (paramDerValue.isConstructed()))
      {
        paramDerValue.resetTag((byte)48);
        this.name = new EDIPartyName(paramDerValue);
      }
      else
      {
        throw new IOException("Invalid encoding of EDI name");
      }
      break;
    case 3: 
    default: 
      throw new IOException("Unrecognized GeneralName tag, (" + i + ")");
    }
  }
  
  public int getType()
  {
    return this.name.getType();
  }
  
  public GeneralNameInterface getName()
  {
    return this.name;
  }
  
  public String toString()
  {
    return this.name.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof GeneralName)) {
      return false;
    }
    GeneralNameInterface localGeneralNameInterface = ((GeneralName)paramObject).name;
    try
    {
      return this.name.constrains(localGeneralNameInterface) == 0;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException) {}
    return false;
  }
  
  public int hashCode()
  {
    return this.name.hashCode();
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    this.name.encode(localDerOutputStream);
    int i = this.name.getType();
    if ((i == 0) || (i == 3) || (i == 5)) {
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)i), localDerOutputStream);
    } else if (i == 4) {
      paramDerOutputStream.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)i), localDerOutputStream);
    } else {
      paramDerOutputStream.writeImplicit(DerValue.createTag((byte)Byte.MIN_VALUE, false, (byte)i), localDerOutputStream);
    }
  }
}
