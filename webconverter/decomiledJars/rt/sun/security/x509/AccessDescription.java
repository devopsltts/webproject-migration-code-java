package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class AccessDescription
{
  private int myhash = -1;
  private ObjectIdentifier accessMethod;
  private GeneralName accessLocation;
  public static final ObjectIdentifier Ad_OCSP_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1 });
  public static final ObjectIdentifier Ad_CAISSUERS_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 2 });
  public static final ObjectIdentifier Ad_TIMESTAMPING_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 3 });
  public static final ObjectIdentifier Ad_CAREPOSITORY_Id = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 5 });
  
  public AccessDescription(ObjectIdentifier paramObjectIdentifier, GeneralName paramGeneralName)
  {
    this.accessMethod = paramObjectIdentifier;
    this.accessLocation = paramGeneralName;
  }
  
  public AccessDescription(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = paramDerValue.getData();
    this.accessMethod = localDerInputStream.getOID();
    this.accessLocation = new GeneralName(localDerInputStream.getDerValue());
  }
  
  public ObjectIdentifier getAccessMethod()
  {
    return this.accessMethod;
  }
  
  public GeneralName getAccessLocation()
  {
    return this.accessLocation;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(this.accessMethod);
    this.accessLocation.encode(localDerOutputStream);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public int hashCode()
  {
    if (this.myhash == -1) {
      this.myhash = (this.accessMethod.hashCode() + this.accessLocation.hashCode());
    }
    return this.myhash;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof AccessDescription))) {
      return false;
    }
    AccessDescription localAccessDescription = (AccessDescription)paramObject;
    if (this == localAccessDescription) {
      return true;
    }
    return (this.accessMethod.equals(localAccessDescription.getAccessMethod())) && (this.accessLocation.equals(localAccessDescription.getAccessLocation()));
  }
  
  public String toString()
  {
    String str = null;
    if (this.accessMethod.equals(Ad_CAISSUERS_Id)) {
      str = "caIssuers";
    } else if (this.accessMethod.equals(Ad_CAREPOSITORY_Id)) {
      str = "caRepository";
    } else if (this.accessMethod.equals(Ad_TIMESTAMPING_Id)) {
      str = "timeStamping";
    } else if (this.accessMethod.equals(Ad_OCSP_Id)) {
      str = "ocsp";
    } else {
      str = this.accessMethod.toString();
    }
    return "\n   accessMethod: " + str + "\n   accessLocation: " + this.accessLocation.toString() + "\n";
  }
}
