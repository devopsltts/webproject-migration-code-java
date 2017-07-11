package javax.xml.bind.helpers;

import java.io.PrintStream;
import java.net.URL;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import org.w3c.dom.Node;

public class DefaultValidationEventHandler
  implements ValidationEventHandler
{
  public DefaultValidationEventHandler() {}
  
  public boolean handleEvent(ValidationEvent paramValidationEvent)
  {
    if (paramValidationEvent == null) {
      throw new IllegalArgumentException();
    }
    String str1 = null;
    boolean bool = false;
    switch (paramValidationEvent.getSeverity())
    {
    case 0: 
      str1 = Messages.format("DefaultValidationEventHandler.Warning");
      bool = true;
      break;
    case 1: 
      str1 = Messages.format("DefaultValidationEventHandler.Error");
      bool = false;
      break;
    case 2: 
      str1 = Messages.format("DefaultValidationEventHandler.FatalError");
      bool = false;
      break;
    default: 
      if (!$assertionsDisabled) {
        throw new AssertionError(Messages.format("DefaultValidationEventHandler.UnrecognizedSeverity", Integer.valueOf(paramValidationEvent.getSeverity())));
      }
      break;
    }
    String str2 = getLocation(paramValidationEvent);
    System.out.println(Messages.format("DefaultValidationEventHandler.SeverityMessage", str1, paramValidationEvent.getMessage(), str2));
    return bool;
  }
  
  private String getLocation(ValidationEvent paramValidationEvent)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    ValidationEventLocator localValidationEventLocator = paramValidationEvent.getLocator();
    if (localValidationEventLocator != null)
    {
      URL localURL = localValidationEventLocator.getURL();
      Object localObject = localValidationEventLocator.getObject();
      Node localNode = localValidationEventLocator.getNode();
      int i = localValidationEventLocator.getLineNumber();
      if ((localURL != null) || (i != -1))
      {
        localStringBuffer.append("line " + i);
        if (localURL != null) {
          localStringBuffer.append(" of " + localURL);
        }
      }
      else if (localObject != null)
      {
        localStringBuffer.append(" obj: " + localObject.toString());
      }
      else if (localNode != null)
      {
        localStringBuffer.append(" node: " + localNode.toString());
      }
    }
    else
    {
      localStringBuffer.append(Messages.format("DefaultValidationEventHandler.LocationUnavailable"));
    }
    return localStringBuffer.toString();
  }
}
