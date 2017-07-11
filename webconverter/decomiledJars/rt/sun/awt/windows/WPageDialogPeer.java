package sun.awt.windows;

final class WPageDialogPeer
  extends WPrintDialogPeer
{
  WPageDialogPeer(WPageDialog paramWPageDialog)
  {
    super(paramWPageDialog);
  }
  
  private native boolean _show();
  
  public void show()
  {
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
          ((WPrintDialog)WPageDialogPeer.this.target).setRetVal(WPageDialogPeer.this._show());
        }
        catch (Exception localException) {}
        ((WPrintDialog)WPageDialogPeer.this.target).setVisible(false);
      }
    }).start();
  }
}
