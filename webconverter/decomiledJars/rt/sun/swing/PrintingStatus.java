package sun.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PrintingStatus
{
  private final PrinterJob job;
  private final Component parent;
  private JDialog abortDialog;
  private JButton abortButton;
  private JLabel statusLabel;
  private MessageFormat statusFormat;
  private final AtomicBoolean isAborted = new AtomicBoolean(false);
  private final Action abortAction = new AbstractAction()
  {
    public void actionPerformed(ActionEvent paramAnonymousActionEvent)
    {
      if (!PrintingStatus.this.isAborted.get())
      {
        PrintingStatus.this.isAborted.set(true);
        PrintingStatus.this.abortButton.setEnabled(false);
        PrintingStatus.this.abortDialog.setTitle(UIManager.getString("PrintingDialog.titleAbortingText"));
        PrintingStatus.this.statusLabel.setText(UIManager.getString("PrintingDialog.contentAbortingText"));
        PrintingStatus.this.job.cancel();
      }
    }
  };
  private final WindowAdapter closeListener = new WindowAdapter()
  {
    public void windowClosing(WindowEvent paramAnonymousWindowEvent)
    {
      PrintingStatus.this.abortAction.actionPerformed(null);
    }
  };
  
  public static PrintingStatus createPrintingStatus(Component paramComponent, PrinterJob paramPrinterJob)
  {
    return new PrintingStatus(paramComponent, paramPrinterJob);
  }
  
  protected PrintingStatus(Component paramComponent, PrinterJob paramPrinterJob)
  {
    this.job = paramPrinterJob;
    this.parent = paramComponent;
  }
  
  private void init()
  {
    String str1 = UIManager.getString("PrintingDialog.titleProgressText");
    String str2 = UIManager.getString("PrintingDialog.contentInitialText");
    this.statusFormat = new MessageFormat(UIManager.getString("PrintingDialog.contentProgressText"));
    String str3 = UIManager.getString("PrintingDialog.abortButtonText");
    String str4 = UIManager.getString("PrintingDialog.abortButtonToolTipText");
    int i = getInt("PrintingDialog.abortButtonMnemonic", -1);
    int j = getInt("PrintingDialog.abortButtonDisplayedMnemonicIndex", -1);
    this.abortButton = new JButton(str3);
    this.abortButton.addActionListener(this.abortAction);
    this.abortButton.setToolTipText(str4);
    if (i != -1) {
      this.abortButton.setMnemonic(i);
    }
    if (j != -1) {
      this.abortButton.setDisplayedMnemonicIndex(j);
    }
    this.statusLabel = new JLabel(str2);
    JOptionPane localJOptionPane = new JOptionPane(this.statusLabel, 1, -1, null, new Object[] { this.abortButton }, this.abortButton);
    localJOptionPane.getActionMap().put("close", this.abortAction);
    if ((this.parent != null) && ((this.parent.getParent() instanceof JViewport))) {
      this.abortDialog = localJOptionPane.createDialog(this.parent.getParent(), str1);
    } else {
      this.abortDialog = localJOptionPane.createDialog(this.parent, str1);
    }
    this.abortDialog.setDefaultCloseOperation(0);
    this.abortDialog.addWindowListener(this.closeListener);
  }
  
  public void showModal(final boolean paramBoolean)
  {
    if (SwingUtilities.isEventDispatchThread()) {
      showModalOnEDT(paramBoolean);
    } else {
      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
        {
          public void run()
          {
            PrintingStatus.this.showModalOnEDT(paramBoolean);
          }
        });
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new RuntimeException(localThrowable);
      }
    }
  }
  
  private void showModalOnEDT(boolean paramBoolean)
  {
    assert (SwingUtilities.isEventDispatchThread());
    init();
    this.abortDialog.setModal(paramBoolean);
    this.abortDialog.setVisible(true);
  }
  
  public void dispose()
  {
    if (SwingUtilities.isEventDispatchThread()) {
      disposeOnEDT();
    } else {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          PrintingStatus.this.disposeOnEDT();
        }
      });
    }
  }
  
  private void disposeOnEDT()
  {
    assert (SwingUtilities.isEventDispatchThread());
    if (this.abortDialog != null)
    {
      this.abortDialog.removeWindowListener(this.closeListener);
      this.abortDialog.dispose();
      this.abortDialog = null;
    }
  }
  
  public boolean isAborted()
  {
    return this.isAborted.get();
  }
  
  public Printable createNotificationPrintable(Printable paramPrintable)
  {
    return new NotificationPrintable(paramPrintable);
  }
  
  static int getInt(Object paramObject, int paramInt)
  {
    Object localObject = UIManager.get(paramObject);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInt;
  }
  
  private class NotificationPrintable
    implements Printable
  {
    private final Printable printDelegatee;
    
    public NotificationPrintable(Printable paramPrintable)
    {
      if (paramPrintable == null) {
        throw new NullPointerException("Printable is null");
      }
      this.printDelegatee = paramPrintable;
    }
    
    public int print(Graphics paramGraphics, PageFormat paramPageFormat, final int paramInt)
      throws PrinterException
    {
      int i = this.printDelegatee.print(paramGraphics, paramPageFormat, paramInt);
      if ((i != 1) && (!PrintingStatus.this.isAborted())) {
        if (SwingUtilities.isEventDispatchThread()) {
          updateStatusOnEDT(paramInt);
        } else {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              PrintingStatus.NotificationPrintable.this.updateStatusOnEDT(paramInt);
            }
          });
        }
      }
      return i;
    }
    
    private void updateStatusOnEDT(int paramInt)
    {
      assert (SwingUtilities.isEventDispatchThread());
      Object[] arrayOfObject = { new Integer(paramInt + 1) };
      PrintingStatus.this.statusLabel.setText(PrintingStatus.this.statusFormat.format(arrayOfObject));
    }
  }
}
