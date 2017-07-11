package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public abstract interface SOAPHandler<T extends SOAPMessageContext>
  extends Handler<T>
{
  public abstract Set<QName> getHeaders();
}
