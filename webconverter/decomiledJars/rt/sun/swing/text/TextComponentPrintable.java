package sun.swing.text;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;
import sun.font.FontDesignMetrics;
import sun.swing.text.html.FrameEditorPaneTag;

public class TextComponentPrintable
  implements CountingPrintable
{
  private static final int LIST_SIZE = 1000;
  private boolean isLayouted = false;
  private final JTextComponent textComponentToPrint;
  private final AtomicReference<FontRenderContext> frc = new AtomicReference(null);
  private final JTextComponent printShell;
  private final MessageFormat headerFormat;
  private final MessageFormat footerFormat;
  private static final float HEADER_FONT_SIZE = 18.0F;
  private static final float FOOTER_FONT_SIZE = 12.0F;
  private final Font headerFont;
  private final Font footerFont;
  private final List<IntegerSegment> rowsMetrics;
  private final List<IntegerSegment> pagesMetrics;
  private boolean needReadLock = false;
  
  public static Printable getPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
  {
    if (((paramJTextComponent instanceof JEditorPane)) && (isFrameSetDocument(paramJTextComponent.getDocument())))
    {
      List localList = getFrames((JEditorPane)paramJTextComponent);
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        JEditorPane localJEditorPane = (JEditorPane)localIterator.next();
        localArrayList.add((CountingPrintable)getPrintable(localJEditorPane, paramMessageFormat1, paramMessageFormat2));
      }
      return new CompoundPrintable(localArrayList);
    }
    return new TextComponentPrintable(paramJTextComponent, paramMessageFormat1, paramMessageFormat2);
  }
  
  private static boolean isFrameSetDocument(Document paramDocument)
  {
    boolean bool = false;
    if ((paramDocument instanceof HTMLDocument))
    {
      HTMLDocument localHTMLDocument = (HTMLDocument)paramDocument;
      if (localHTMLDocument.getIterator(HTML.Tag.FRAME).isValid()) {
        bool = true;
      }
    }
    return bool;
  }
  
  private static List<JEditorPane> getFrames(JEditorPane paramJEditorPane)
  {
    ArrayList localArrayList = new ArrayList();
    getFrames(paramJEditorPane, localArrayList);
    if (localArrayList.size() == 0)
    {
      createFrames(paramJEditorPane);
      getFrames(paramJEditorPane, localArrayList);
    }
    return localArrayList;
  }
  
  private static void getFrames(Container paramContainer, List<JEditorPane> paramList)
  {
    for (Component localComponent : paramContainer.getComponents()) {
      if (((localComponent instanceof FrameEditorPaneTag)) && ((localComponent instanceof JEditorPane))) {
        paramList.add((JEditorPane)localComponent);
      } else if ((localComponent instanceof Container)) {
        getFrames((Container)localComponent, paramList);
      }
    }
  }
  
  private static void createFrames(JEditorPane paramJEditorPane)
  {
    Runnable local1 = new Runnable()
    {
      public void run()
      {
        CellRendererPane localCellRendererPane = new CellRendererPane();
        localCellRendererPane.add(this.val$editor);
        localCellRendererPane.setSize(500, 500);
      }
    };
    if (SwingUtilities.isEventDispatchThread()) {
      local1.run();
    } else {
      try
      {
        SwingUtilities.invokeAndWait(local1);
      }
      catch (Exception localException)
      {
        if ((localException instanceof RuntimeException)) {
          throw ((RuntimeException)localException);
        }
        throw new RuntimeException(localException);
      }
    }
  }
  
  private TextComponentPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2)
  {
    this.textComponentToPrint = paramJTextComponent;
    this.headerFormat = paramMessageFormat1;
    this.footerFormat = paramMessageFormat2;
    this.headerFont = paramJTextComponent.getFont().deriveFont(1, 18.0F);
    this.footerFont = paramJTextComponent.getFont().deriveFont(0, 12.0F);
    this.pagesMetrics = Collections.synchronizedList(new ArrayList());
    this.rowsMetrics = new ArrayList(1000);
    this.printShell = createPrintShell(paramJTextComponent);
  }
  
  private JTextComponent createPrintShell(final JTextComponent paramJTextComponent)
  {
    if (SwingUtilities.isEventDispatchThread()) {
      return createPrintShellOnEDT(paramJTextComponent);
    }
    FutureTask localFutureTask = new FutureTask(new Callable()
    {
      public JTextComponent call()
        throws Exception
      {
        return TextComponentPrintable.this.createPrintShellOnEDT(paramJTextComponent);
      }
    });
    SwingUtilities.invokeLater(localFutureTask);
    try
    {
      return (JTextComponent)localFutureTask.get();
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
    catch (ExecutionException localExecutionException)
    {
      Throwable localThrowable = localExecutionException.getCause();
      if ((localThrowable instanceof Error)) {
        throw ((Error)localThrowable);
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      throw new AssertionError(localThrowable);
    }
  }
  
  private JTextComponent createPrintShellOnEDT(final JTextComponent paramJTextComponent)
  {
    assert (SwingUtilities.isEventDispatchThread());
    Object localObject = null;
    if ((paramJTextComponent instanceof JPasswordField)) {
      localObject = new JPasswordField()
      {
        public FontMetrics getFontMetrics(Font paramAnonymousFont)
        {
          return TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(paramAnonymousFont) : FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
        }
      };
    } else if ((paramJTextComponent instanceof JTextField)) {
      localObject = new JTextField()
      {
        public FontMetrics getFontMetrics(Font paramAnonymousFont)
        {
          return TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(paramAnonymousFont) : FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
        }
      };
    } else if ((paramJTextComponent instanceof JTextArea)) {
      localObject = new JTextArea()
      {
        public FontMetrics getFontMetrics(Font paramAnonymousFont)
        {
          return TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(paramAnonymousFont) : FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
        }
      };
    } else if ((paramJTextComponent instanceof JTextPane)) {
      localObject = new JTextPane()
      {
        public FontMetrics getFontMetrics(Font paramAnonymousFont)
        {
          return TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(paramAnonymousFont) : FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
        }
        
        public EditorKit getEditorKit()
        {
          if (getDocument() == paramJTextComponent.getDocument()) {
            return ((JTextPane)paramJTextComponent).getEditorKit();
          }
          return super.getEditorKit();
        }
      };
    } else if ((paramJTextComponent instanceof JEditorPane)) {
      localObject = new JEditorPane()
      {
        public FontMetrics getFontMetrics(Font paramAnonymousFont)
        {
          return TextComponentPrintable.this.frc.get() == null ? super.getFontMetrics(paramAnonymousFont) : FontDesignMetrics.getMetrics(paramAnonymousFont, (FontRenderContext)TextComponentPrintable.this.frc.get());
        }
        
        public EditorKit getEditorKit()
        {
          if (getDocument() == paramJTextComponent.getDocument()) {
            return ((JEditorPane)paramJTextComponent).getEditorKit();
          }
          return super.getEditorKit();
        }
      };
    }
    ((JTextComponent)localObject).setBorder(null);
    ((JTextComponent)localObject).setOpaque(paramJTextComponent.isOpaque());
    ((JTextComponent)localObject).setEditable(paramJTextComponent.isEditable());
    ((JTextComponent)localObject).setEnabled(paramJTextComponent.isEnabled());
    ((JTextComponent)localObject).setFont(paramJTextComponent.getFont());
    ((JTextComponent)localObject).setBackground(paramJTextComponent.getBackground());
    ((JTextComponent)localObject).setForeground(paramJTextComponent.getForeground());
    ((JTextComponent)localObject).setComponentOrientation(paramJTextComponent.getComponentOrientation());
    if ((localObject instanceof JEditorPane))
    {
      ((JTextComponent)localObject).putClientProperty("JEditorPane.honorDisplayProperties", paramJTextComponent.getClientProperty("JEditorPane.honorDisplayProperties"));
      ((JTextComponent)localObject).putClientProperty("JEditorPane.w3cLengthUnits", paramJTextComponent.getClientProperty("JEditorPane.w3cLengthUnits"));
      ((JTextComponent)localObject).putClientProperty("charset", paramJTextComponent.getClientProperty("charset"));
    }
    ((JTextComponent)localObject).setDocument(paramJTextComponent.getDocument());
    return localObject;
  }
  
  public int getNumberOfPages()
  {
    return this.pagesMetrics.size();
  }
  
  public int print(final Graphics paramGraphics, final PageFormat paramPageFormat, final int paramInt)
    throws PrinterException
  {
    if (!this.isLayouted)
    {
      if ((paramGraphics instanceof Graphics2D)) {
        this.frc.set(((Graphics2D)paramGraphics).getFontRenderContext());
      }
      layout((int)Math.floor(paramPageFormat.getImageableWidth()));
      calculateRowsMetrics();
    }
    int i;
    if (!SwingUtilities.isEventDispatchThread())
    {
      Callable local8 = new Callable()
      {
        public Integer call()
          throws Exception
        {
          return Integer.valueOf(TextComponentPrintable.this.printOnEDT(paramGraphics, paramPageFormat, paramInt));
        }
      };
      FutureTask localFutureTask = new FutureTask(local8);
      SwingUtilities.invokeLater(localFutureTask);
      try
      {
        i = ((Integer)localFutureTask.get()).intValue();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (ExecutionException localExecutionException)
      {
        Throwable localThrowable = localExecutionException.getCause();
        if ((localThrowable instanceof PrinterException)) {
          throw ((PrinterException)localThrowable);
        }
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new RuntimeException(localThrowable);
      }
    }
    else
    {
      i = printOnEDT(paramGraphics, paramPageFormat, paramInt);
    }
    return i;
  }
  
  private int printOnEDT(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt)
    throws PrinterException
  {
    assert (SwingUtilities.isEventDispatchThread());
    Object localObject1 = BorderFactory.createEmptyBorder();
    if ((this.headerFormat != null) || (this.footerFormat != null))
    {
      localObject2 = new Object[] { Integer.valueOf(paramInt + 1) };
      if (this.headerFormat != null) {
        localObject1 = new TitledBorder((Border)localObject1, this.headerFormat.format(localObject2), 2, 1, this.headerFont, this.printShell.getForeground());
      }
      if (this.footerFormat != null) {
        localObject1 = new TitledBorder((Border)localObject1, this.footerFormat.format(localObject2), 2, 6, this.footerFont, this.printShell.getForeground());
      }
    }
    Object localObject2 = ((Border)localObject1).getBorderInsets(this.printShell);
    updatePagesMetrics(paramInt, (int)Math.floor(paramPageFormat.getImageableHeight()) - ((Insets)localObject2).top - ((Insets)localObject2).bottom);
    if (this.pagesMetrics.size() <= paramInt) {
      return 1;
    }
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics.create();
    localGraphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    ((Border)localObject1).paintBorder(this.printShell, localGraphics2D, 0, 0, (int)Math.floor(paramPageFormat.getImageableWidth()), (int)Math.floor(paramPageFormat.getImageableHeight()));
    localGraphics2D.translate(0, ((Insets)localObject2).top);
    Rectangle localRectangle = new Rectangle(0, 0, (int)paramPageFormat.getWidth(), ((IntegerSegment)this.pagesMetrics.get(paramInt)).end - ((IntegerSegment)this.pagesMetrics.get(paramInt)).start + 1);
    localGraphics2D.clip(localRectangle);
    int i = 0;
    if (ComponentOrientation.RIGHT_TO_LEFT == this.printShell.getComponentOrientation()) {
      i = (int)paramPageFormat.getImageableWidth() - this.printShell.getWidth();
    }
    localGraphics2D.translate(i, -((IntegerSegment)this.pagesMetrics.get(paramInt)).start);
    this.printShell.print(localGraphics2D);
    localGraphics2D.dispose();
    return 0;
  }
  
  private void releaseReadLock()
  {
    assert (!SwingUtilities.isEventDispatchThread());
    Document localDocument = this.textComponentToPrint.getDocument();
    if ((localDocument instanceof AbstractDocument)) {
      try
      {
        ((AbstractDocument)localDocument).readUnlock();
        this.needReadLock = true;
      }
      catch (Error localError) {}
    }
  }
  
  private void acquireReadLock()
  {
    assert (!SwingUtilities.isEventDispatchThread());
    if (this.needReadLock)
    {
      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
        {
          public void run() {}
        });
      }
      catch (InterruptedException localInterruptedException) {}catch (InvocationTargetException localInvocationTargetException) {}
      Document localDocument = this.textComponentToPrint.getDocument();
      ((AbstractDocument)localDocument).readLock();
      this.needReadLock = false;
    }
  }
  
  private void layout(final int paramInt)
  {
    if (!SwingUtilities.isEventDispatchThread())
    {
      Callable local10 = new Callable()
      {
        public Object call()
          throws Exception
        {
          TextComponentPrintable.this.layoutOnEDT(paramInt);
          return null;
        }
      };
      FutureTask localFutureTask = new FutureTask(local10);
      releaseReadLock();
      SwingUtilities.invokeLater(localFutureTask);
      try
      {
        localFutureTask.get();
      }
      catch (InterruptedException localInterruptedException)
      {
        throw new RuntimeException(localInterruptedException);
      }
      catch (ExecutionException localExecutionException)
      {
        Throwable localThrowable = localExecutionException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new RuntimeException(localThrowable);
      }
      finally
      {
        acquireReadLock();
      }
    }
    else
    {
      layoutOnEDT(paramInt);
    }
    this.isLayouted = true;
  }
  
  private void layoutOnEDT(int paramInt)
  {
    assert (SwingUtilities.isEventDispatchThread());
    CellRendererPane localCellRendererPane = new CellRendererPane();
    JViewport localJViewport = new JViewport();
    localJViewport.setBorder(null);
    Dimension localDimension = new Dimension(paramInt, 2147482647);
    if ((this.printShell instanceof JTextField)) {
      localDimension = new Dimension(localDimension.width, this.printShell.getPreferredSize().height);
    }
    this.printShell.setSize(localDimension);
    localJViewport.setComponentOrientation(this.printShell.getComponentOrientation());
    localJViewport.setSize(localDimension);
    localJViewport.add(this.printShell);
    localCellRendererPane.add(localJViewport);
  }
  
  private void updatePagesMetrics(int paramInt1, int paramInt2)
  {
    while ((paramInt1 >= this.pagesMetrics.size()) && (!this.rowsMetrics.isEmpty()))
    {
      int i = this.pagesMetrics.size() - 1;
      int j = i >= 0 ? ((IntegerSegment)this.pagesMetrics.get(i)).end + 1 : 0;
      for (int k = 0; (k < this.rowsMetrics.size()) && (((IntegerSegment)this.rowsMetrics.get(k)).end - j + 1 <= paramInt2); k++) {}
      if (k == 0)
      {
        this.pagesMetrics.add(new IntegerSegment(j, j + paramInt2 - 1));
      }
      else
      {
        k--;
        this.pagesMetrics.add(new IntegerSegment(j, ((IntegerSegment)this.rowsMetrics.get(k)).end));
        for (int m = 0; m <= k; m++) {
          this.rowsMetrics.remove(0);
        }
      }
    }
  }
  
  private void calculateRowsMetrics()
  {
    int i = this.printShell.getDocument().getLength();
    ArrayList localArrayList = new ArrayList(1000);
    int j = 0;
    int k = -1;
    int m = -1;
    while (j < i)
    {
      try
      {
        Rectangle localRectangle = this.printShell.modelToView(j);
        if (localRectangle != null)
        {
          int n = (int)localRectangle.getY();
          int i1 = (int)localRectangle.getHeight();
          if ((i1 != 0) && ((n != k) || (i1 != m)))
          {
            k = n;
            m = i1;
            localArrayList.add(new IntegerSegment(n, n + i1 - 1));
          }
        }
      }
      catch (BadLocationException localBadLocationException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError();
        }
      }
      j++;
    }
    Collections.sort(localArrayList);
    j = Integer.MIN_VALUE;
    k = Integer.MIN_VALUE;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      IntegerSegment localIntegerSegment = (IntegerSegment)localIterator.next();
      if (k < localIntegerSegment.start)
      {
        if (k != Integer.MIN_VALUE) {
          this.rowsMetrics.add(new IntegerSegment(j, k));
        }
        j = localIntegerSegment.start;
        k = localIntegerSegment.end;
      }
      else
      {
        k = localIntegerSegment.end;
      }
    }
    if (k != Integer.MIN_VALUE) {
      this.rowsMetrics.add(new IntegerSegment(j, k));
    }
  }
  
  private static class IntegerSegment
    implements Comparable<IntegerSegment>
  {
    final int start;
    final int end;
    
    IntegerSegment(int paramInt1, int paramInt2)
    {
      this.start = paramInt1;
      this.end = paramInt2;
    }
    
    public int compareTo(IntegerSegment paramIntegerSegment)
    {
      int i = this.start - paramIntegerSegment.start;
      return i != 0 ? i : this.end - paramIntegerSegment.end;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof IntegerSegment)) {
        return compareTo((IntegerSegment)paramObject) == 0;
      }
      return false;
    }
    
    public int hashCode()
    {
      int i = 17;
      i = 37 * i + this.start;
      i = 37 * i + this.end;
      return i;
    }
    
    public String toString()
    {
      return "IntegerSegment [" + this.start + ", " + this.end + "]";
    }
  }
}
