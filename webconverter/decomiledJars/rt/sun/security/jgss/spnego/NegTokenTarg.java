package sun.security.jgss.spnego;

import java.io.IOException;
import java.io.PrintStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class NegTokenTarg
  extends SpNegoToken
{
  private int negResult = 0;
  private Oid supportedMech = null;
  private byte[] responseToken = null;
  private byte[] mechListMIC = null;
  
  NegTokenTarg(int paramInt, Oid paramOid, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(1);
    this.negResult = paramInt;
    this.supportedMech = paramOid;
    this.responseToken = paramArrayOfByte1;
    this.mechListMIC = paramArrayOfByte2;
  }
  
  public NegTokenTarg(byte[] paramArrayOfByte)
    throws GSSException
  {
    super(1);
    parseToken(paramArrayOfByte);
  }
  
  final byte[] encode()
    throws GSSException
  {
    try
    {
      DerOutputStream localDerOutputStream1 = new DerOutputStream();
      DerOutputStream localDerOutputStream2 = new DerOutputStream();
      localDerOutputStream2.putEnumerated(this.negResult);
      localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
      if (this.supportedMech != null)
      {
        localDerOutputStream3 = new DerOutputStream();
        byte[] arrayOfByte = this.supportedMech.getDER();
        localDerOutputStream3.write(arrayOfByte);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)1), localDerOutputStream3);
      }
      if (this.responseToken != null)
      {
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(this.responseToken);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)2), localDerOutputStream3);
      }
      if (this.mechListMIC != null)
      {
        if (DEBUG) {
          System.out.println("SpNegoToken NegTokenTarg: sending MechListMIC");
        }
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(this.mechListMIC);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream3);
      }
      else if ((GSSUtil.useMSInterop()) && (this.responseToken != null))
      {
        if (DEBUG) {
          System.out.println("SpNegoToken NegTokenTarg: sending additional token for MS Interop");
        }
        localDerOutputStream3 = new DerOutputStream();
        localDerOutputStream3.putOctetString(this.responseToken);
        localDerOutputStream1.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream3);
      }
      DerOutputStream localDerOutputStream3 = new DerOutputStream();
      localDerOutputStream3.write((byte)48, localDerOutputStream1);
      return localDerOutputStream3.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + localIOException.getMessage());
    }
  }
  
  private void parseToken(byte[] paramArrayOfByte)
    throws GSSException
  {
    try
    {
      DerValue localDerValue1 = new DerValue(paramArrayOfByte);
      if (!localDerValue1.isContextSpecific((byte)1)) {
        throw new IOException("SPNEGO NegoTokenTarg : did not have the right token type");
      }
      DerValue localDerValue2 = localDerValue1.data.getDerValue();
      if (localDerValue2.tag != 48) {
        throw new IOException("SPNEGO NegoTokenTarg : did not have the Sequence tag");
      }
      int i = -1;
      while (localDerValue2.data.available() > 0)
      {
        DerValue localDerValue3 = localDerValue2.data.getDerValue();
        if (localDerValue3.isContextSpecific((byte)0))
        {
          i = checkNextField(i, 0);
          this.negResult = localDerValue3.data.getEnumerated();
          if (DEBUG) {
            System.out.println("SpNegoToken NegTokenTarg: negotiated result = " + getNegoResultString(this.negResult));
          }
        }
        else if (localDerValue3.isContextSpecific((byte)1))
        {
          i = checkNextField(i, 1);
          ObjectIdentifier localObjectIdentifier = localDerValue3.data.getOID();
          this.supportedMech = new Oid(localObjectIdentifier.toString());
          if (DEBUG) {
            System.out.println("SpNegoToken NegTokenTarg: supported mechanism = " + this.supportedMech);
          }
        }
        else if (localDerValue3.isContextSpecific((byte)2))
        {
          i = checkNextField(i, 2);
          this.responseToken = localDerValue3.data.getOctetString();
        }
        else if (localDerValue3.isContextSpecific((byte)3))
        {
          i = checkNextField(i, 3);
          if (!GSSUtil.useMSInterop())
          {
            this.mechListMIC = localDerValue3.data.getOctetString();
            if (DEBUG) {
              System.out.println("SpNegoToken NegTokenTarg: MechListMIC Token = " + getHexBytes(this.mechListMIC));
            }
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new GSSException(10, -1, "Invalid SPNEGO NegTokenTarg token : " + localIOException.getMessage());
    }
  }
  
  int getNegotiatedResult()
  {
    return this.negResult;
  }
  
  public Oid getSupportedMech()
  {
    return this.supportedMech;
  }
  
  byte[] getResponseToken()
  {
    return this.responseToken;
  }
  
  byte[] getMechListMIC()
  {
    return this.mechListMIC;
  }
}
