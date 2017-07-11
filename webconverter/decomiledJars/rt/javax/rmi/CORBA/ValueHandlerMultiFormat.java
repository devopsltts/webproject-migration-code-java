package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public abstract interface ValueHandlerMultiFormat
  extends ValueHandler
{
  public abstract byte getMaximumStreamFormatVersion();
  
  public abstract void writeValue(OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte);
}
