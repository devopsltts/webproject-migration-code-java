package javax.print;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import sun.print.ServiceDialog;
import sun.print.SunAlternateMedia;

public class ServiceUI
{
  public ServiceUI() {}
  
  public static PrintService printDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
    throws HeadlessException
  {
    int i = -1;
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if ((paramArrayOfPrintService == null) || (paramArrayOfPrintService.length == 0)) {
      throw new IllegalArgumentException("services must be non-null and non-empty");
    }
    if (paramPrintRequestAttributeSet == null) {
      throw new IllegalArgumentException("attributes must be non-null");
    }
    if (paramPrintService != null)
    {
      for (int j = 0; j < paramArrayOfPrintService.length; j++) {
        if (paramArrayOfPrintService[j].equals(paramPrintService))
        {
          i = j;
          break;
        }
      }
      if (i < 0) {
        throw new IllegalArgumentException("services must contain defaultService");
      }
    }
    else
    {
      i = 0;
    }
    Component localComponent = null;
    Rectangle localRectangle1 = paramGraphicsConfiguration == null ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds() : paramGraphicsConfiguration.getBounds();
    ServiceDialog localServiceDialog;
    if ((localComponent instanceof Frame)) {
      localServiceDialog = new ServiceDialog(paramGraphicsConfiguration, paramInt1 + localRectangle1.x, paramInt2 + localRectangle1.y, paramArrayOfPrintService, i, paramDocFlavor, paramPrintRequestAttributeSet, (Frame)localComponent);
    } else {
      localServiceDialog = new ServiceDialog(paramGraphicsConfiguration, paramInt1 + localRectangle1.x, paramInt2 + localRectangle1.y, paramArrayOfPrintService, i, paramDocFlavor, paramPrintRequestAttributeSet, (Dialog)localComponent);
    }
    Rectangle localRectangle2 = localServiceDialog.getBounds();
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = localGraphicsEnvironment.getScreenDevices();
    for (int k = 0; k < arrayOfGraphicsDevice.length; k++) {
      localRectangle1 = localRectangle1.union(arrayOfGraphicsDevice[k].getDefaultConfiguration().getBounds());
    }
    if (!localRectangle1.contains(localRectangle2)) {
      localServiceDialog.setLocationRelativeTo(localComponent);
    }
    localServiceDialog.show();
    if (localServiceDialog.getStatus() == 1)
    {
      PrintRequestAttributeSet localPrintRequestAttributeSet = localServiceDialog.getAttributes();
      Destination localDestination = Destination.class;
      SunAlternateMedia localSunAlternateMedia = SunAlternateMedia.class;
      Fidelity localFidelity1 = Fidelity.class;
      if ((paramPrintRequestAttributeSet.containsKey(localDestination)) && (!localPrintRequestAttributeSet.containsKey(localDestination))) {
        paramPrintRequestAttributeSet.remove(localDestination);
      }
      if ((paramPrintRequestAttributeSet.containsKey(localSunAlternateMedia)) && (!localPrintRequestAttributeSet.containsKey(localSunAlternateMedia))) {
        paramPrintRequestAttributeSet.remove(localSunAlternateMedia);
      }
      paramPrintRequestAttributeSet.addAll(localPrintRequestAttributeSet);
      Fidelity localFidelity2 = (Fidelity)paramPrintRequestAttributeSet.get(localFidelity1);
      if ((localFidelity2 != null) && (localFidelity2 == Fidelity.FIDELITY_TRUE)) {
        removeUnsupportedAttributes(localServiceDialog.getPrintService(), paramDocFlavor, paramPrintRequestAttributeSet);
      }
    }
    return localServiceDialog.getPrintService();
  }
  
  private static void removeUnsupportedAttributes(PrintService paramPrintService, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet)
  {
    AttributeSet localAttributeSet = paramPrintService.getUnsupportedAttributes(paramDocFlavor, paramAttributeSet);
    if (localAttributeSet != null)
    {
      Attribute[] arrayOfAttribute = localAttributeSet.toArray();
      for (int i = 0; i < arrayOfAttribute.length; i++)
      {
        Class localClass = arrayOfAttribute[i].getCategory();
        if (paramPrintService.isAttributeCategorySupported(localClass))
        {
          Attribute localAttribute = (Attribute)paramPrintService.getDefaultAttributeValue(localClass);
          if (localAttribute != null) {
            paramAttributeSet.add(localAttribute);
          } else {
            paramAttributeSet.remove(localClass);
          }
        }
        else
        {
          paramAttributeSet.remove(localClass);
        }
      }
    }
  }
}
