package javax.xml.crypto.dsig.keyinfo;

import java.math.BigInteger;
import javax.xml.crypto.XMLStructure;

public abstract interface X509IssuerSerial
  extends XMLStructure
{
  public abstract String getIssuerName();
  
  public abstract BigInteger getSerialNumber();
}
