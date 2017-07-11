package java.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SignedObject
  implements Serializable
{
  private static final long serialVersionUID = 720502720485447167L;
  private byte[] content;
  private byte[] signature;
  private String thealgorithm;
  
  public SignedObject(Serializable paramSerializable, PrivateKey paramPrivateKey, Signature paramSignature)
    throws IOException, InvalidKeyException, SignatureException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
    localObjectOutputStream.writeObject(paramSerializable);
    localObjectOutputStream.flush();
    localObjectOutputStream.close();
    this.content = localByteArrayOutputStream.toByteArray();
    localByteArrayOutputStream.close();
    sign(paramPrivateKey, paramSignature);
  }
  
  public Object getObject()
    throws IOException, ClassNotFoundException
  {
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(this.content);
    ObjectInputStream localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
    Object localObject = localObjectInputStream.readObject();
    localByteArrayInputStream.close();
    localObjectInputStream.close();
    return localObject;
  }
  
  public byte[] getSignature()
  {
    return (byte[])this.signature.clone();
  }
  
  public String getAlgorithm()
  {
    return this.thealgorithm;
  }
  
  public boolean verify(PublicKey paramPublicKey, Signature paramSignature)
    throws InvalidKeyException, SignatureException
  {
    paramSignature.initVerify(paramPublicKey);
    paramSignature.update((byte[])this.content.clone());
    return paramSignature.verify((byte[])this.signature.clone());
  }
  
  private void sign(PrivateKey paramPrivateKey, Signature paramSignature)
    throws InvalidKeyException, SignatureException
  {
    paramSignature.initSign(paramPrivateKey);
    paramSignature.update((byte[])this.content.clone());
    this.signature = ((byte[])paramSignature.sign().clone());
    this.thealgorithm = paramSignature.getAlgorithm();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    this.content = ((byte[])((byte[])localGetField.get("content", null)).clone());
    this.signature = ((byte[])((byte[])localGetField.get("signature", null)).clone());
    this.thealgorithm = ((String)localGetField.get("thealgorithm", null));
  }
}
