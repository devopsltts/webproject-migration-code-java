package sun.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.DefaultRowSorter.ModelWrapper;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Position.Bias;
import sun.awt.shell.ShellFolder;
import sun.awt.shell.ShellFolderColumnInfo;

public class FilePane
  extends JPanel
  implements PropertyChangeListener
{
  public static final String ACTION_APPROVE_SELECTION = "approveSelection";
  public static final String ACTION_CANCEL = "cancelSelection";
  public static final String ACTION_EDIT_FILE_NAME = "editFileName";
  public static final String ACTION_REFRESH = "refresh";
  public static final String ACTION_CHANGE_TO_PARENT_DIRECTORY = "Go Up";
  public static final String ACTION_NEW_FOLDER = "New Folder";
  public static final String ACTION_VIEW_LIST = "viewTypeList";
  public static final String ACTION_VIEW_DETAILS = "viewTypeDetails";
  private Action[] actions;
  public static final int VIEWTYPE_LIST = 0;
  public static final int VIEWTYPE_DETAILS = 1;
  private static final int VIEWTYPE_COUNT = 2;
  private int viewType = -1;
  private JPanel[] viewPanels = new JPanel[2];
  private JPanel currentViewPanel;
  private String[] viewTypeActionNames;
  private String filesListAccessibleName = null;
  private String filesDetailsAccessibleName = null;
  private JPopupMenu contextMenu;
  private JMenu viewMenu;
  private String viewMenuLabelText;
  private String refreshActionLabelText;
  private String newFolderActionLabelText;
  private String kiloByteString;
  private String megaByteString;
  private String gigaByteString;
  private String renameErrorTitleText;
  private String renameErrorText;
  private String renameErrorFileExistsText;
  private static final Cursor waitCursor = Cursor.getPredefinedCursor(3);
  private final KeyListener detailsKeyListener = new KeyAdapter()
  {
    private final long timeFactor;
    private final StringBuilder typedString = new StringBuilder();
    private long lastTime = 1000L;
    
    public void keyTyped(KeyEvent paramAnonymousKeyEvent)
    {
      BasicDirectoryModel localBasicDirectoryModel = FilePane.this.getModel();
      int i = localBasicDirectoryModel.getSize();
      if ((FilePane.this.detailsTable == null) || (i == 0) || (paramAnonymousKeyEvent.isAltDown()) || (paramAnonymousKeyEvent.isControlDown()) || (paramAnonymousKeyEvent.isMetaDown())) {
        return;
      }
      InputMap localInputMap = FilePane.this.detailsTable.getInputMap(1);
      KeyStroke localKeyStroke = KeyStroke.getKeyStrokeForEvent(paramAnonymousKeyEvent);
      if ((localInputMap != null) && (localInputMap.get(localKeyStroke) != null)) {
        return;
      }
      int j = FilePane.this.detailsTable.getSelectionModel().getLeadSelectionIndex();
      if (j < 0) {
        j = 0;
      }
      if (j >= i) {
        j = i - 1;
      }
      char c = paramAnonymousKeyEvent.getKeyChar();
      long l = paramAnonymousKeyEvent.getWhen();
      if (l - this.lastTime < this.timeFactor)
      {
        if ((this.typedString.length() == 1) && (this.typedString.charAt(0) == c)) {
          j++;
        } else {
          this.typedString.append(c);
        }
      }
      else
      {
        j++;
        this.typedString.setLength(0);
        this.typedString.append(c);
      }
      this.lastTime = l;
      if (j >= i) {
        j = 0;
      }
      int k = getNextMatch(j, i - 1);
      if ((k < 0) && (j > 0)) {
        k = getNextMatch(0, j - 1);
      }
      if (k >= 0)
      {
        FilePane.this.detailsTable.getSelectionModel().setSelectionInterval(k, k);
        Rectangle localRectangle = FilePane.this.detailsTable.getCellRect(k, FilePane.this.detailsTable.convertColumnIndexToView(0), false);
        FilePane.this.detailsTable.scrollRectToVisible(localRectangle);
      }
    }
    
    private int getNextMatch(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      BasicDirectoryModel localBasicDirectoryModel = FilePane.this.getModel();
      JFileChooser localJFileChooser = FilePane.this.getFileChooser();
      FilePane.DetailsTableRowSorter localDetailsTableRowSorter = FilePane.this.getRowSorter();
      String str1 = this.typedString.toString().toLowerCase();
      for (int i = paramAnonymousInt1; i <= paramAnonymousInt2; i++)
      {
        File localFile = (File)localBasicDirectoryModel.getElementAt(localDetailsTableRowSorter.convertRowIndexToModel(i));
        String str2 = localJFileChooser.getName(localFile).toLowerCase();
        if (str2.startsWith(str1)) {
          return i;
        }
      }
      return -1;
    }
  };
  private FocusListener editorFocusListener = new FocusAdapter()
  {
    public void focusLost(FocusEvent paramAnonymousFocusEvent)
    {
      if (!paramAnonymousFocusEvent.isTemporary()) {
        FilePane.this.applyEdit();
      }
    }
  };
  private static FocusListener repaintListener = new FocusListener()
  {
    public void focusGained(FocusEvent paramAnonymousFocusEvent)
    {
      repaintSelection(paramAnonymousFocusEvent.getSource());
    }
    
    public void focusLost(FocusEvent paramAnonymousFocusEvent)
    {
      repaintSelection(paramAnonymousFocusEvent.getSource());
    }
    
    private void repaintSelection(Object paramAnonymousObject)
    {
      if ((paramAnonymousObject instanceof JList)) {
        repaintListSelection((JList)paramAnonymousObject);
      } else if ((paramAnonymousObject instanceof JTable)) {
        repaintTableSelection((JTable)paramAnonymousObject);
      }
    }
    
    private void repaintListSelection(JList paramAnonymousJList)
    {
      int[] arrayOfInt1 = paramAnonymousJList.getSelectedIndices();
      for (int k : arrayOfInt1)
      {
        Rectangle localRectangle = paramAnonymousJList.getCellBounds(k, k);
        paramAnonymousJList.repaint(localRectangle);
      }
    }
    
    private void repaintTableSelection(JTable paramAnonymousJTable)
    {
      int i = paramAnonymousJTable.getSelectionModel().getMinSelectionIndex();
      int j = paramAnonymousJTable.getSelectionModel().getMaxSelectionIndex();
      if ((i == -1) || (j == -1)) {
        return;
      }
      int k = paramAnonymousJTable.convertColumnIndexToView(0);
      Rectangle localRectangle1 = paramAnonymousJTable.getCellRect(i, k, false);
      Rectangle localRectangle2 = paramAnonymousJTable.getCellRect(j, k, false);
      Rectangle localRectangle3 = localRectangle1.union(localRectangle2);
      paramAnonymousJTable.repaint(localRectangle3);
    }
  };
  private boolean smallIconsView = false;
  private Border listViewBorder;
  private Color listViewBackground;
  private boolean listViewWindowsStyle;
  private boolean readOnly;
  private boolean fullRowSelection = false;
  private ListSelectionModel listSelectionModel;
  private JList list;
  private JTable detailsTable;
  private static final int COLUMN_FILENAME = 0;
  private File newFolderFile;
  private FileChooserUIAccessor fileChooserUIAccessor;
  private DetailsTableModel detailsTableModel;
  private DetailsTableRowSorter rowSorter;
  private DetailsTableCellEditor tableCellEditor;
  int lastIndex = -1;
  File editFile = null;
  JTextField editCell = null;
  protected Action newFolderAction;
  private Handler handler;
  
  public FilePane(FileChooserUIAccessor paramFileChooserUIAccessor)
  {
    super(new BorderLayout());
    this.fileChooserUIAccessor = paramFileChooserUIAccessor;
    installDefaults();
    createActionMap();
  }
  
  public void uninstallUI()
  {
    if (getModel() != null) {
      getModel().removePropertyChangeListener(this);
    }
  }
  
  protected JFileChooser getFileChooser()
  {
    return this.fileChooserUIAccessor.getFileChooser();
  }
  
  protected BasicDirectoryModel getModel()
  {
    return this.fileChooserUIAccessor.getModel();
  }
  
  public int getViewType()
  {
    return this.viewType;
  }
  
  public void setViewType(int paramInt)
  {
    if (paramInt == this.viewType) {
      return;
    }
    int i = this.viewType;
    this.viewType = paramInt;
    JPanel localJPanel = null;
    Object localObject = null;
    switch (paramInt)
    {
    case 0: 
      if (this.viewPanels[paramInt] == null)
      {
        localJPanel = this.fileChooserUIAccessor.createList();
        if (localJPanel == null) {
          localJPanel = createList();
        }
        this.list = ((JList)findChildComponent(localJPanel, JList.class));
        if (this.listSelectionModel == null)
        {
          this.listSelectionModel = this.list.getSelectionModel();
          if (this.detailsTable != null) {
            this.detailsTable.setSelectionModel(this.listSelectionModel);
          }
        }
        else
        {
          this.list.setSelectionModel(this.listSelectionModel);
        }
      }
      this.list.setLayoutOrientation(1);
      localObject = this.list;
      break;
    case 1: 
      if (this.viewPanels[paramInt] == null)
      {
        localJPanel = this.fileChooserUIAccessor.createDetailsView();
        if (localJPanel == null) {
          localJPanel = createDetailsView();
        }
        this.detailsTable = ((JTable)findChildComponent(localJPanel, JTable.class));
        this.detailsTable.setRowHeight(Math.max(this.detailsTable.getFont().getSize() + 4, 17));
        if (this.listSelectionModel != null) {
          this.detailsTable.setSelectionModel(this.listSelectionModel);
        }
      }
      localObject = this.detailsTable;
    }
    if (localJPanel != null)
    {
      this.viewPanels[paramInt] = localJPanel;
      recursivelySetInheritsPopupMenu(localJPanel, true);
    }
    int j = 0;
    if (this.currentViewPanel != null)
    {
      Component localComponent = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
      j = (localComponent == this.detailsTable) || (localComponent == this.list) ? 1 : 0;
      remove(this.currentViewPanel);
    }
    this.currentViewPanel = this.viewPanels[paramInt];
    add(this.currentViewPanel, "Center");
    if ((j != 0) && (localObject != null)) {
      ((Component)localObject).requestFocusInWindow();
    }
    revalidate();
    repaint();
    updateViewMenu();
    firePropertyChange("viewType", i, paramInt);
  }
  
  public Action getViewTypeAction(int paramInt)
  {
    return new ViewTypeAction(paramInt);
  }
  
  private static void recursivelySetInheritsPopupMenu(Container paramContainer, boolean paramBoolean)
  {
    if ((paramContainer instanceof JComponent)) {
      ((JComponent)paramContainer).setInheritsPopupMenu(paramBoolean);
    }
    int i = paramContainer.getComponentCount();
    for (int j = 0; j < i; j++) {
      recursivelySetInheritsPopupMenu((Container)paramContainer.getComponent(j), paramBoolean);
    }
  }
  
  protected void installDefaults()
  {
    Locale localLocale = getFileChooser().getLocale();
    this.listViewBorder = UIManager.getBorder("FileChooser.listViewBorder");
    this.listViewBackground = UIManager.getColor("FileChooser.listViewBackground");
    this.listViewWindowsStyle = UIManager.getBoolean("FileChooser.listViewWindowsStyle");
    this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
    this.viewMenuLabelText = UIManager.getString("FileChooser.viewMenuLabelText", localLocale);
    this.refreshActionLabelText = UIManager.getString("FileChooser.refreshActionLabelText", localLocale);
    this.newFolderActionLabelText = UIManager.getString("FileChooser.newFolderActionLabelText", localLocale);
    this.viewTypeActionNames = new String[2];
    this.viewTypeActionNames[0] = UIManager.getString("FileChooser.listViewActionLabelText", localLocale);
    this.viewTypeActionNames[1] = UIManager.getString("FileChooser.detailsViewActionLabelText", localLocale);
    this.kiloByteString = UIManager.getString("FileChooser.fileSizeKiloBytes", localLocale);
    this.megaByteString = UIManager.getString("FileChooser.fileSizeMegaBytes", localLocale);
    this.gigaByteString = UIManager.getString("FileChooser.fileSizeGigaBytes", localLocale);
    this.fullRowSelection = UIManager.getBoolean("FileView.fullRowSelection");
    this.filesListAccessibleName = UIManager.getString("FileChooser.filesListAccessibleName", localLocale);
    this.filesDetailsAccessibleName = UIManager.getString("FileChooser.filesDetailsAccessibleName", localLocale);
    this.renameErrorTitleText = UIManager.getString("FileChooser.renameErrorTitleText", localLocale);
    this.renameErrorText = UIManager.getString("FileChooser.renameErrorText", localLocale);
    this.renameErrorFileExistsText = UIManager.getString("FileChooser.renameErrorFileExistsText", localLocale);
  }
  
  public Action[] getActions()
  {
    if (this.actions == null)
    {
      ArrayList localArrayList = new ArrayList(8);
      localArrayList.add(new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection")
          {
            if (FilePane.this.editFile != null) {
              FilePane.this.cancelEdit();
            } else {
              FilePane.this.getFileChooser().cancelSelection();
            }
          }
          else if (str == "editFileName")
          {
            JFileChooser localJFileChooser = FilePane.this.getFileChooser();
            int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
            if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1))) {
              FilePane.this.editFileName(i);
            }
          }
          else if (str == "refresh")
          {
            FilePane.this.getFileChooser().rescanCurrentDirectory();
          }
        }
        
        public boolean isEnabled()
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection") {
            return FilePane.this.getFileChooser().isEnabled();
          }
          if (str == "editFileName") {
            return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
          }
          return true;
        }
      });
      localArrayList.add(new AbstractAction()
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection")
          {
            if (FilePane.this.editFile != null) {
              FilePane.this.cancelEdit();
            } else {
              FilePane.this.getFileChooser().cancelSelection();
            }
          }
          else if (str == "editFileName")
          {
            JFileChooser localJFileChooser = FilePane.this.getFileChooser();
            int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
            if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1))) {
              FilePane.this.editFileName(i);
            }
          }
          else if (str == "refresh")
          {
            FilePane.this.getFileChooser().rescanCurrentDirectory();
          }
        }
        
        public boolean isEnabled()
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection") {
            return FilePane.this.getFileChooser().isEnabled();
          }
          if (str == "editFileName") {
            return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
          }
          return true;
        }
      });
      localArrayList.add(new AbstractAction(this.refreshActionLabelText)
      {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection")
          {
            if (FilePane.this.editFile != null) {
              FilePane.this.cancelEdit();
            } else {
              FilePane.this.getFileChooser().cancelSelection();
            }
          }
          else if (str == "editFileName")
          {
            JFileChooser localJFileChooser = FilePane.this.getFileChooser();
            int i = FilePane.this.listSelectionModel.getMinSelectionIndex();
            if ((i >= 0) && (FilePane.this.editFile == null) && ((!localJFileChooser.isMultiSelectionEnabled()) || (localJFileChooser.getSelectedFiles().length <= 1))) {
              FilePane.this.editFileName(i);
            }
          }
          else if (str == "refresh")
          {
            FilePane.this.getFileChooser().rescanCurrentDirectory();
          }
        }
        
        public boolean isEnabled()
        {
          String str = (String)getValue("ActionCommandKey");
          if (str == "cancelSelection") {
            return FilePane.this.getFileChooser().isEnabled();
          }
          if (str == "editFileName") {
            return (!FilePane.this.readOnly) && (FilePane.this.getFileChooser().isEnabled());
          }
          return true;
        }
      });
      Action localAction = this.fileChooserUIAccessor.getApproveSelectionAction();
      if (localAction != null) {
        localArrayList.add(localAction);
      }
      localAction = this.fileChooserUIAccessor.getChangeToParentDirectoryAction();
      if (localAction != null) {
        localArrayList.add(localAction);
      }
      localAction = getNewFolderAction();
      if (localAction != null) {
        localArrayList.add(localAction);
      }
      localAction = getViewTypeAction(0);
      if (localAction != null) {
        localArrayList.add(localAction);
      }
      localAction = getViewTypeAction(1);
      if (localAction != null) {
        localArrayList.add(localAction);
      }
      this.actions = ((Action[])localArrayList.toArray(new Action[localArrayList.size()]));
    }
    return this.actions;
  }
  
  protected void createActionMap()
  {
    addActionsToMap(super.getActionMap(), getActions());
  }
  
  public static void addActionsToMap(ActionMap paramActionMap, Action[] paramArrayOfAction)
  {
    if ((paramActionMap != null) && (paramArrayOfAction != null)) {
      for (Action localAction : paramArrayOfAction)
      {
        String str = (String)localAction.getValue("ActionCommandKey");
        if (str == null) {
          str = (String)localAction.getValue("Name");
        }
        paramActionMap.put(str, localAction);
      }
    }
  }
  
  private void updateListRowCount(JList paramJList)
  {
    if (this.smallIconsView) {
      paramJList.setVisibleRowCount(getModel().getSize() / 3);
    } else {
      paramJList.setVisibleRowCount(-1);
    }
  }
  
  public JPanel createList()
  {
    JPanel localJPanel = new JPanel(new BorderLayout());
    final JFileChooser localJFileChooser = getFileChooser();
    final JList local4 = new JList()
    {
      public int getNextMatch(String paramAnonymousString, int paramAnonymousInt, Position.Bias paramAnonymousBias)
      {
        ListModel localListModel = getModel();
        int i = localListModel.getSize();
        if ((paramAnonymousString == null) || (paramAnonymousInt < 0) || (paramAnonymousInt >= i)) {
          throw new IllegalArgumentException();
        }
        int j = paramAnonymousBias == Position.Bias.Backward ? 1 : 0;
        int k = paramAnonymousInt;
        while (j != 0 ? k >= 0 : k < i)
        {
          String str = localJFileChooser.getName((File)localListModel.getElementAt(k));
          if (str.regionMatches(true, 0, paramAnonymousString, 0, paramAnonymousString.length())) {
            return k;
          }
          k += (j != 0 ? -1 : 1);
        }
        return -1;
      }
    };
    local4.setCellRenderer(new FileRenderer());
    local4.setLayoutOrientation(1);
    local4.putClientProperty("List.isFileList", Boolean.TRUE);
    if (this.listViewWindowsStyle) {
      local4.addFocusListener(repaintListener);
    }
    updateListRowCount(local4);
    getModel().addListDataListener(new ListDataListener()
    {
      public void intervalAdded(ListDataEvent paramAnonymousListDataEvent)
      {
        FilePane.this.updateListRowCount(local4);
      }
      
      public void intervalRemoved(ListDataEvent paramAnonymousListDataEvent)
      {
        FilePane.this.updateListRowCount(local4);
      }
      
      public void contentsChanged(ListDataEvent paramAnonymousListDataEvent)
      {
        if (FilePane.this.isShowing()) {
          FilePane.this.clearSelection();
        }
        FilePane.this.updateListRowCount(local4);
      }
    });
    getModel().addPropertyChangeListener(this);
    if (localJFileChooser.isMultiSelectionEnabled()) {
      local4.setSelectionMode(2);
    } else {
      local4.setSelectionMode(0);
    }
    local4.setModel(new SortableListModel());
    local4.addListSelectionListener(createListSelectionListener());
    local4.addMouseListener(getMouseHandler());
    JScrollPane localJScrollPane = new JScrollPane(local4);
    if (this.listViewBackground != null) {
      local4.setBackground(this.listViewBackground);
    }
    if (this.listViewBorder != null) {
      localJScrollPane.setBorder(this.listViewBorder);
    }
    local4.putClientProperty("AccessibleName", this.filesListAccessibleName);
    localJPanel.add(localJScrollPane, "Center");
    return localJPanel;
  }
  
  private DetailsTableModel getDetailsTableModel()
  {
    if (this.detailsTableModel == null) {
      this.detailsTableModel = new DetailsTableModel(getFileChooser());
    }
    return this.detailsTableModel;
  }
  
  private void updateDetailsColumnModel(JTable paramJTable)
  {
    if (paramJTable != null)
    {
      ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = this.detailsTableModel.getColumns();
      DefaultTableColumnModel localDefaultTableColumnModel = new DefaultTableColumnModel();
      for (int i = 0; i < arrayOfShellFolderColumnInfo.length; i++)
      {
        ShellFolderColumnInfo localShellFolderColumnInfo = arrayOfShellFolderColumnInfo[i];
        TableColumn localTableColumn = new TableColumn(i);
        Object localObject1 = localShellFolderColumnInfo.getTitle();
        if ((localObject1 != null) && (((String)localObject1).startsWith("FileChooser.")) && (((String)localObject1).endsWith("HeaderText")))
        {
          localObject2 = UIManager.getString(localObject1, paramJTable.getLocale());
          if (localObject2 != null) {
            localObject1 = localObject2;
          }
        }
        localTableColumn.setHeaderValue(localObject1);
        Object localObject2 = localShellFolderColumnInfo.getWidth();
        if (localObject2 != null) {
          localTableColumn.setPreferredWidth(((Integer)localObject2).intValue());
        }
        localDefaultTableColumnModel.addColumn(localTableColumn);
      }
      if ((!this.readOnly) && (localDefaultTableColumnModel.getColumnCount() > 0)) {
        localDefaultTableColumnModel.getColumn(0).setCellEditor(getDetailsTableCellEditor());
      }
      paramJTable.setColumnModel(localDefaultTableColumnModel);
    }
  }
  
  private DetailsTableRowSorter getRowSorter()
  {
    if (this.rowSorter == null) {
      this.rowSorter = new DetailsTableRowSorter();
    }
    return this.rowSorter;
  }
  
  private DetailsTableCellEditor getDetailsTableCellEditor()
  {
    if (this.tableCellEditor == null) {
      this.tableCellEditor = new DetailsTableCellEditor(new JTextField());
    }
    return this.tableCellEditor;
  }
  
  public JPanel createDetailsView()
  {
    final JFileChooser localJFileChooser = getFileChooser();
    JPanel localJPanel = new JPanel(new BorderLayout());
    JTable local6 = new JTable(getDetailsTableModel())
    {
      protected boolean processKeyBinding(KeyStroke paramAnonymousKeyStroke, KeyEvent paramAnonymousKeyEvent, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        if ((paramAnonymousKeyEvent.getKeyCode() == 27) && (getCellEditor() == null))
        {
          localJFileChooser.dispatchEvent(paramAnonymousKeyEvent);
          return true;
        }
        return super.processKeyBinding(paramAnonymousKeyStroke, paramAnonymousKeyEvent, paramAnonymousInt, paramAnonymousBoolean);
      }
      
      public void tableChanged(TableModelEvent paramAnonymousTableModelEvent)
      {
        super.tableChanged(paramAnonymousTableModelEvent);
        if (paramAnonymousTableModelEvent.getFirstRow() == -1) {
          FilePane.this.updateDetailsColumnModel(this);
        }
      }
    };
    local6.setRowSorter(getRowSorter());
    local6.setAutoCreateColumnsFromModel(false);
    local6.setComponentOrientation(localJFileChooser.getComponentOrientation());
    local6.setAutoResizeMode(0);
    local6.setShowGrid(false);
    local6.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
    local6.addKeyListener(this.detailsKeyListener);
    Font localFont = this.list.getFont();
    local6.setFont(localFont);
    local6.setIntercellSpacing(new Dimension(0, 0));
    AlignableTableHeaderRenderer localAlignableTableHeaderRenderer = new AlignableTableHeaderRenderer(local6.getTableHeader().getDefaultRenderer());
    local6.getTableHeader().setDefaultRenderer(localAlignableTableHeaderRenderer);
    DetailsTableCellRenderer localDetailsTableCellRenderer = new DetailsTableCellRenderer(localJFileChooser);
    local6.setDefaultRenderer(Object.class, localDetailsTableCellRenderer);
    local6.getColumnModel().getSelectionModel().setSelectionMode(0);
    local6.addMouseListener(getMouseHandler());
    local6.putClientProperty("Table.isFileList", Boolean.TRUE);
    if (this.listViewWindowsStyle) {
      local6.addFocusListener(repaintListener);
    }
    ActionMap localActionMap = SwingUtilities.getUIActionMap(local6);
    localActionMap.remove("selectNextRowCell");
    localActionMap.remove("selectPreviousRowCell");
    localActionMap.remove("selectNextColumnCell");
    localActionMap.remove("selectPreviousColumnCell");
    local6.setFocusTraversalKeys(0, null);
    local6.setFocusTraversalKeys(1, null);
    JScrollPane localJScrollPane = new JScrollPane(local6);
    localJScrollPane.setComponentOrientation(localJFileChooser.getComponentOrientation());
    LookAndFeel.installColors(localJScrollPane.getViewport(), "Table.background", "Table.foreground");
    localJScrollPane.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(ComponentEvent paramAnonymousComponentEvent)
      {
        JScrollPane localJScrollPane = (JScrollPane)paramAnonymousComponentEvent.getComponent();
        FilePane.this.fixNameColumnWidth(localJScrollPane.getViewport().getSize().width);
        localJScrollPane.removeComponentListener(this);
      }
    });
    localJScrollPane.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent paramAnonymousMouseEvent)
      {
        JScrollPane localJScrollPane = (JScrollPane)paramAnonymousMouseEvent.getComponent();
        JTable localJTable = (JTable)localJScrollPane.getViewport().getView();
        if ((!paramAnonymousMouseEvent.isShiftDown()) || (localJTable.getSelectionModel().getSelectionMode() == 0))
        {
          FilePane.this.clearSelection();
          TableCellEditor localTableCellEditor = localJTable.getCellEditor();
          if (localTableCellEditor != null) {
            localTableCellEditor.stopCellEditing();
          }
        }
      }
    });
    local6.setForeground(this.list.getForeground());
    local6.setBackground(this.list.getBackground());
    if (this.listViewBorder != null) {
      localJScrollPane.setBorder(this.listViewBorder);
    }
    localJPanel.add(localJScrollPane, "Center");
    this.detailsTableModel.fireTableStructureChanged();
    local6.putClientProperty("AccessibleName", this.filesDetailsAccessibleName);
    return localJPanel;
  }
  
  private void fixNameColumnWidth(int paramInt)
  {
    TableColumn localTableColumn = this.detailsTable.getColumnModel().getColumn(0);
    int i = this.detailsTable.getPreferredSize().width;
    if (i < paramInt) {
      localTableColumn.setPreferredWidth(localTableColumn.getPreferredWidth() + paramInt - i);
    }
  }
  
  public ListSelectionListener createListSelectionListener()
  {
    return this.fileChooserUIAccessor.createListSelectionListener();
  }
  
  private int getEditIndex()
  {
    return this.lastIndex;
  }
  
  private void setEditIndex(int paramInt)
  {
    this.lastIndex = paramInt;
  }
  
  private void resetEditIndex()
  {
    this.lastIndex = -1;
  }
  
  private void cancelEdit()
  {
    if (this.editFile != null)
    {
      this.editFile = null;
      this.list.remove(this.editCell);
      repaint();
    }
    else if ((this.detailsTable != null) && (this.detailsTable.isEditing()))
    {
      this.detailsTable.getCellEditor().cancelCellEditing();
    }
  }
  
  private void editFileName(int paramInt)
  {
    JFileChooser localJFileChooser = getFileChooser();
    File localFile = localJFileChooser.getCurrentDirectory();
    if ((this.readOnly) || (!canWrite(localFile))) {
      return;
    }
    ensureIndexIsVisible(paramInt);
    switch (this.viewType)
    {
    case 0: 
      this.editFile = ((File)getModel().getElementAt(getRowSorter().convertRowIndexToModel(paramInt)));
      Rectangle localRectangle = this.list.getCellBounds(paramInt, paramInt);
      if (this.editCell == null)
      {
        this.editCell = new JTextField();
        this.editCell.setName("Tree.cellEditor");
        this.editCell.addActionListener(new EditActionListener());
        this.editCell.addFocusListener(this.editorFocusListener);
        this.editCell.setNextFocusableComponent(this.list);
      }
      this.list.add(this.editCell);
      this.editCell.setText(localJFileChooser.getName(this.editFile));
      ComponentOrientation localComponentOrientation = this.list.getComponentOrientation();
      this.editCell.setComponentOrientation(localComponentOrientation);
      Icon localIcon = localJFileChooser.getIcon(this.editFile);
      int i = localIcon == null ? 20 : localIcon.getIconWidth() + 4;
      if (localComponentOrientation.isLeftToRight()) {
        this.editCell.setBounds(i + localRectangle.x, localRectangle.y, localRectangle.width - i, localRectangle.height);
      } else {
        this.editCell.setBounds(localRectangle.x, localRectangle.y, localRectangle.width - i, localRectangle.height);
      }
      this.editCell.requestFocus();
      this.editCell.selectAll();
      break;
    case 1: 
      this.detailsTable.editCellAt(paramInt, 0);
    }
  }
  
  private void applyEdit()
  {
    if ((this.editFile != null) && (this.editFile.exists()))
    {
      JFileChooser localJFileChooser = getFileChooser();
      String str1 = localJFileChooser.getName(this.editFile);
      String str2 = this.editFile.getName();
      String str3 = this.editCell.getText().trim();
      if (!str3.equals(str1))
      {
        String str4 = str3;
        int i = str2.length();
        int j = str1.length();
        if ((i > j) && (str2.charAt(j) == '.')) {
          str4 = str3 + str2.substring(j);
        }
        FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
        File localFile = localFileSystemView.createFileObject(this.editFile.getParentFile(), str4);
        if (localFile.exists()) {
          JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(this.renameErrorFileExistsText, new Object[] { str2 }), this.renameErrorTitleText, 0);
        } else if (getModel().renameFile(this.editFile, localFile))
        {
          if (localFileSystemView.isParent(localJFileChooser.getCurrentDirectory(), localFile)) {
            if (localJFileChooser.isMultiSelectionEnabled()) {
              localJFileChooser.setSelectedFiles(new File[] { localFile });
            } else {
              localJFileChooser.setSelectedFile(localFile);
            }
          }
        }
        else {
          JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(this.renameErrorText, new Object[] { str2 }), this.renameErrorTitleText, 0);
        }
      }
    }
    if ((this.detailsTable != null) && (this.detailsTable.isEditing())) {
      this.detailsTable.getCellEditor().stopCellEditing();
    }
    cancelEdit();
  }
  
  public Action getNewFolderAction()
  {
    if ((!this.readOnly) && (this.newFolderAction == null)) {
      this.newFolderAction = new AbstractAction(this.newFolderActionLabelText)
      {
        private Action basicNewFolderAction;
        
        public void actionPerformed(ActionEvent paramAnonymousActionEvent)
        {
          if (this.basicNewFolderAction == null) {
            this.basicNewFolderAction = FilePane.this.fileChooserUIAccessor.getNewFolderAction();
          }
          JFileChooser localJFileChooser = FilePane.this.getFileChooser();
          File localFile1 = localJFileChooser.getSelectedFile();
          this.basicNewFolderAction.actionPerformed(paramAnonymousActionEvent);
          File localFile2 = localJFileChooser.getSelectedFile();
          if ((localFile2 != null) && (!localFile2.equals(localFile1)) && (localFile2.isDirectory())) {
            FilePane.this.newFolderFile = localFile2;
          }
        }
      };
    }
    return this.newFolderAction;
  }
  
  void setFileSelected()
  {
    Object localObject1;
    Object localObject2;
    int i;
    int j;
    if ((getFileChooser().isMultiSelectionEnabled()) && (!isDirectorySelected()))
    {
      localObject1 = getFileChooser().getSelectedFiles();
      localObject2 = this.list.getSelectedValues();
      this.listSelectionModel.setValueIsAdjusting(true);
      try
      {
        i = this.listSelectionModel.getLeadSelectionIndex();
        j = this.listSelectionModel.getAnchorSelectionIndex();
        Arrays.sort((Object[])localObject1);
        Arrays.sort((Object[])localObject2);
        int k = 0;
        int m = 0;
        while ((k < localObject1.length) && (m < localObject2.length))
        {
          int n = localObject1[k].compareTo((File)localObject2[m]);
          if (n < 0)
          {
            doSelectFile(localObject1[(k++)]);
          }
          else if (n > 0)
          {
            doDeselectFile(localObject2[(m++)]);
          }
          else
          {
            k++;
            m++;
          }
        }
        while (k < localObject1.length) {
          doSelectFile(localObject1[(k++)]);
        }
        while (m < localObject2.length) {
          doDeselectFile(localObject2[(m++)]);
        }
        if ((this.listSelectionModel instanceof DefaultListSelectionModel))
        {
          ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(i);
          this.listSelectionModel.setAnchorSelectionIndex(j);
        }
      }
      finally
      {
        this.listSelectionModel.setValueIsAdjusting(false);
      }
    }
    else
    {
      localObject1 = getFileChooser();
      if (isDirectorySelected()) {
        localObject2 = getDirectory();
      } else {
        localObject2 = ((JFileChooser)localObject1).getSelectedFile();
      }
      if ((localObject2 != null) && ((i = getModel().indexOf(localObject2)) >= 0))
      {
        j = getRowSorter().convertRowIndexToView(i);
        this.listSelectionModel.setSelectionInterval(j, j);
        ensureIndexIsVisible(j);
      }
      else
      {
        clearSelection();
      }
    }
  }
  
  private void doSelectFile(File paramFile)
  {
    int i = getModel().indexOf(paramFile);
    if (i >= 0)
    {
      i = getRowSorter().convertRowIndexToView(i);
      this.listSelectionModel.addSelectionInterval(i, i);
    }
  }
  
  private void doDeselectFile(Object paramObject)
  {
    int i = getRowSorter().convertRowIndexToView(getModel().indexOf(paramObject));
    this.listSelectionModel.removeSelectionInterval(i, i);
  }
  
  private void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    applyEdit();
    File localFile = (File)paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((localFile != null) && (((localJFileChooser.isFileSelectionEnabled()) && (!localFile.isDirectory())) || ((localFile.isDirectory()) && (localJFileChooser.isDirectorySelectionEnabled())))) {
      setFileSelected();
    }
  }
  
  private void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    applyEdit();
    File[] arrayOfFile = (File[])paramPropertyChangeEvent.getNewValue();
    JFileChooser localJFileChooser = getFileChooser();
    if ((arrayOfFile != null) && (arrayOfFile.length > 0) && ((arrayOfFile.length > 1) || (localJFileChooser.isDirectorySelectionEnabled()) || (!arrayOfFile[0].isDirectory()))) {
      setFileSelected();
    }
  }
  
  private void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    getDetailsTableModel().updateColumnInfo();
    JFileChooser localJFileChooser = getFileChooser();
    FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
    applyEdit();
    resetEditIndex();
    ensureIndexIsVisible(0);
    File localFile = localJFileChooser.getCurrentDirectory();
    if (localFile != null)
    {
      if (!this.readOnly) {
        getNewFolderAction().setEnabled(canWrite(localFile));
      }
      this.fileChooserUIAccessor.getChangeToParentDirectoryAction().setEnabled(!localFileSystemView.isRoot(localFile));
    }
    if (this.list != null) {
      this.list.clearSelection();
    }
  }
  
  private void doFilterChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }
  
  private void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    applyEdit();
    resetEditIndex();
    clearSelection();
  }
  
  private void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (getFileChooser().isMultiSelectionEnabled())
    {
      this.listSelectionModel.setSelectionMode(2);
    }
    else
    {
      this.listSelectionModel.setSelectionMode(0);
      clearSelection();
      getFileChooser().setSelectedFiles(null);
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (this.viewType == -1) {
      setViewType(0);
    }
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str.equals("SelectedFileChangedProperty"))
    {
      doSelectedFileChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("SelectedFilesChangedProperty"))
    {
      doSelectedFilesChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("directoryChanged"))
    {
      doDirectoryChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("fileFilterChanged"))
    {
      doFilterChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("fileSelectionChanged"))
    {
      doFileSelectionModeChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("MultiSelectionEnabledChangedProperty"))
    {
      doMultiSelectionChanged(paramPropertyChangeEvent);
    }
    else if (str.equals("CancelSelection"))
    {
      applyEdit();
    }
    else if (str.equals("busy"))
    {
      setCursor(((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue() ? waitCursor : null);
    }
    else if (str.equals("componentOrientation"))
    {
      ComponentOrientation localComponentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
      JFileChooser localJFileChooser = (JFileChooser)paramPropertyChangeEvent.getSource();
      if (localComponentOrientation != paramPropertyChangeEvent.getOldValue()) {
        localJFileChooser.applyComponentOrientation(localComponentOrientation);
      }
      if (this.detailsTable != null)
      {
        this.detailsTable.setComponentOrientation(localComponentOrientation);
        this.detailsTable.getParent().getParent().setComponentOrientation(localComponentOrientation);
      }
    }
  }
  
  private void ensureIndexIsVisible(int paramInt)
  {
    if (paramInt >= 0)
    {
      if (this.list != null) {
        this.list.ensureIndexIsVisible(paramInt);
      }
      if (this.detailsTable != null) {
        this.detailsTable.scrollRectToVisible(this.detailsTable.getCellRect(paramInt, 0, true));
      }
    }
  }
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile)
  {
    int i = getModel().indexOf(paramFile);
    if (i >= 0) {
      ensureIndexIsVisible(getRowSorter().convertRowIndexToView(i));
    }
  }
  
  public void rescanCurrentDirectory()
  {
    getModel().validateFileCache();
  }
  
  public void clearSelection()
  {
    if (this.listSelectionModel != null)
    {
      this.listSelectionModel.clearSelection();
      if ((this.listSelectionModel instanceof DefaultListSelectionModel))
      {
        ((DefaultListSelectionModel)this.listSelectionModel).moveLeadSelectionIndex(0);
        this.listSelectionModel.setAnchorSelectionIndex(0);
      }
    }
  }
  
  public JMenu getViewMenu()
  {
    if (this.viewMenu == null)
    {
      this.viewMenu = new JMenu(this.viewMenuLabelText);
      ButtonGroup localButtonGroup = new ButtonGroup();
      for (int i = 0; i < 2; i++)
      {
        JRadioButtonMenuItem localJRadioButtonMenuItem = new JRadioButtonMenuItem(new ViewTypeAction(i));
        localButtonGroup.add(localJRadioButtonMenuItem);
        this.viewMenu.add(localJRadioButtonMenuItem);
      }
      updateViewMenu();
    }
    return this.viewMenu;
  }
  
  private void updateViewMenu()
  {
    if (this.viewMenu != null)
    {
      Component[] arrayOfComponent1 = this.viewMenu.getMenuComponents();
      for (Component localComponent : arrayOfComponent1) {
        if ((localComponent instanceof JRadioButtonMenuItem))
        {
          JRadioButtonMenuItem localJRadioButtonMenuItem = (JRadioButtonMenuItem)localComponent;
          if (((ViewTypeAction)localJRadioButtonMenuItem.getAction()).viewType == this.viewType) {
            localJRadioButtonMenuItem.setSelected(true);
          }
        }
      }
    }
  }
  
  public JPopupMenu getComponentPopupMenu()
  {
    JPopupMenu localJPopupMenu = getFileChooser().getComponentPopupMenu();
    if (localJPopupMenu != null) {
      return localJPopupMenu;
    }
    JMenu localJMenu = getViewMenu();
    if (this.contextMenu == null)
    {
      this.contextMenu = new JPopupMenu();
      if (localJMenu != null)
      {
        this.contextMenu.add(localJMenu);
        if (this.listViewWindowsStyle) {
          this.contextMenu.addSeparator();
        }
      }
      ActionMap localActionMap = getActionMap();
      Action localAction1 = localActionMap.get("refresh");
      Action localAction2 = localActionMap.get("New Folder");
      if (localAction1 != null)
      {
        this.contextMenu.add(localAction1);
        if ((this.listViewWindowsStyle) && (localAction2 != null)) {
          this.contextMenu.addSeparator();
        }
      }
      if (localAction2 != null) {
        this.contextMenu.add(localAction2);
      }
    }
    if (localJMenu != null) {
      localJMenu.getPopupMenu().setInvoker(localJMenu);
    }
    return this.contextMenu;
  }
  
  protected Handler getMouseHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler(null);
    }
    return this.handler;
  }
  
  protected boolean isDirectorySelected()
  {
    return this.fileChooserUIAccessor.isDirectorySelected();
  }
  
  protected File getDirectory()
  {
    return this.fileChooserUIAccessor.getDirectory();
  }
  
  private Component findChildComponent(Container paramContainer, Class paramClass)
  {
    int i = paramContainer.getComponentCount();
    for (int j = 0; j < i; j++)
    {
      Component localComponent1 = paramContainer.getComponent(j);
      if (paramClass.isInstance(localComponent1)) {
        return localComponent1;
      }
      if ((localComponent1 instanceof Container))
      {
        Component localComponent2 = findChildComponent((Container)localComponent1, paramClass);
        if (localComponent2 != null) {
          return localComponent2;
        }
      }
    }
    return null;
  }
  
  public boolean canWrite(File paramFile)
  {
    if (!paramFile.exists()) {
      return false;
    }
    try
    {
      if ((paramFile instanceof ShellFolder)) {
        return paramFile.canWrite();
      }
      if (usesShellFolder(getFileChooser())) {
        try
        {
          return ShellFolder.getShellFolder(paramFile).canWrite();
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          return false;
        }
      }
      return paramFile.canWrite();
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  public static boolean usesShellFolder(JFileChooser paramJFileChooser)
  {
    Boolean localBoolean = (Boolean)paramJFileChooser.getClientProperty("FileChooser.useShellFolder");
    return localBoolean == null ? paramJFileChooser.getFileSystemView().equals(FileSystemView.getFileSystemView()) : localBoolean.booleanValue();
  }
  
  private class AlignableTableHeaderRenderer
    implements TableCellRenderer
  {
    TableCellRenderer wrappedRenderer;
    
    public AlignableTableHeaderRenderer(TableCellRenderer paramTableCellRenderer)
    {
      this.wrappedRenderer = paramTableCellRenderer;
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      Component localComponent = this.wrappedRenderer.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      int i = paramJTable.convertColumnIndexToModel(paramInt2);
      ShellFolderColumnInfo localShellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
      Integer localInteger = localShellFolderColumnInfo.getAlignment();
      if (localInteger == null) {
        localInteger = Integer.valueOf(0);
      }
      if ((localComponent instanceof JLabel)) {
        ((JLabel)localComponent).setHorizontalAlignment(localInteger.intValue());
      }
      return localComponent;
    }
  }
  
  private class DelayedSelectionUpdater
    implements Runnable
  {
    File editFile;
    
    DelayedSelectionUpdater()
    {
      this(null);
    }
    
    DelayedSelectionUpdater(File paramFile)
    {
      this.editFile = paramFile;
      if (FilePane.this.isShowing()) {
        SwingUtilities.invokeLater(this);
      }
    }
    
    public void run()
    {
      FilePane.this.setFileSelected();
      if (this.editFile != null)
      {
        FilePane.this.editFileName(FilePane.access$100(FilePane.this).convertRowIndexToView(FilePane.this.getModel().indexOf(this.editFile)));
        this.editFile = null;
      }
    }
  }
  
  private class DetailsTableCellEditor
    extends DefaultCellEditor
  {
    private final JTextField tf;
    
    public DetailsTableCellEditor(JTextField paramJTextField)
    {
      super();
      this.tf = paramJTextField;
      paramJTextField.setName("Table.editor");
      paramJTextField.addFocusListener(FilePane.this.editorFocusListener);
    }
    
    public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      Component localComponent = super.getTableCellEditorComponent(paramJTable, paramObject, paramBoolean, paramInt1, paramInt2);
      if ((paramObject instanceof File))
      {
        this.tf.setText(FilePane.this.getFileChooser().getName((File)paramObject));
        this.tf.selectAll();
      }
      return localComponent;
    }
  }
  
  class DetailsTableCellRenderer
    extends DefaultTableCellRenderer
  {
    JFileChooser chooser;
    DateFormat df;
    
    DetailsTableCellRenderer(JFileChooser paramJFileChooser)
    {
      this.chooser = paramJFileChooser;
      this.df = DateFormat.getDateTimeInstance(3, 3, paramJFileChooser.getLocale());
    }
    
    public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((getHorizontalAlignment() == 10) && (!FilePane.this.fullRowSelection)) {
        paramInt3 = Math.min(paramInt3, getPreferredSize().width + 4);
      } else {
        paramInt1 -= 4;
      }
      super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public Insets getInsets(Insets paramInsets)
    {
      paramInsets = super.getInsets(paramInsets);
      paramInsets.left += 4;
      paramInsets.right += 4;
      return paramInsets;
    }
    
    public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2)
    {
      if (((paramJTable.convertColumnIndexToModel(paramInt2) != 0) || ((FilePane.this.listViewWindowsStyle) && (!paramJTable.isFocusOwner()))) && (!FilePane.this.fullRowSelection)) {
        paramBoolean1 = false;
      }
      super.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
      setIcon(null);
      int i = paramJTable.convertColumnIndexToModel(paramInt2);
      ShellFolderColumnInfo localShellFolderColumnInfo = FilePane.this.detailsTableModel.getColumns()[i];
      Integer localInteger = localShellFolderColumnInfo.getAlignment();
      if (localInteger == null) {
        localInteger = Integer.valueOf((paramObject instanceof Number) ? 4 : 10);
      }
      setHorizontalAlignment(localInteger.intValue());
      String str;
      if (paramObject == null)
      {
        str = "";
      }
      else if ((paramObject instanceof File))
      {
        File localFile = (File)paramObject;
        str = this.chooser.getName(localFile);
        Icon localIcon = this.chooser.getIcon(localFile);
        setIcon(localIcon);
      }
      else if ((paramObject instanceof Long))
      {
        long l = ((Long)paramObject).longValue() / 1024L;
        if (FilePane.this.listViewWindowsStyle)
        {
          str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf(l + 1L) });
        }
        else if (l < 1024L)
        {
          str = MessageFormat.format(FilePane.this.kiloByteString, new Object[] { Long.valueOf(l == 0L ? 1L : l) });
        }
        else
        {
          l /= 1024L;
          if (l < 1024L)
          {
            str = MessageFormat.format(FilePane.this.megaByteString, new Object[] { Long.valueOf(l) });
          }
          else
          {
            l /= 1024L;
            str = MessageFormat.format(FilePane.this.gigaByteString, new Object[] { Long.valueOf(l) });
          }
        }
      }
      else if ((paramObject instanceof Date))
      {
        str = this.df.format((Date)paramObject);
      }
      else
      {
        str = paramObject.toString();
      }
      setText(str);
      return this;
    }
  }
  
  class DetailsTableModel
    extends AbstractTableModel
    implements ListDataListener
  {
    JFileChooser chooser;
    BasicDirectoryModel directoryModel;
    ShellFolderColumnInfo[] columns;
    int[] columnMap;
    
    DetailsTableModel(JFileChooser paramJFileChooser)
    {
      this.chooser = paramJFileChooser;
      this.directoryModel = FilePane.this.getModel();
      this.directoryModel.addListDataListener(this);
      updateColumnInfo();
    }
    
    void updateColumnInfo()
    {
      Object localObject = this.chooser.getCurrentDirectory();
      if ((localObject != null) && (FilePane.usesShellFolder(this.chooser))) {
        try
        {
          localObject = ShellFolder.getShellFolder((File)localObject);
        }
        catch (FileNotFoundException localFileNotFoundException) {}
      }
      ShellFolderColumnInfo[] arrayOfShellFolderColumnInfo = ShellFolder.getFolderColumns((File)localObject);
      ArrayList localArrayList = new ArrayList();
      this.columnMap = new int[arrayOfShellFolderColumnInfo.length];
      for (int i = 0; i < arrayOfShellFolderColumnInfo.length; i++)
      {
        ShellFolderColumnInfo localShellFolderColumnInfo = arrayOfShellFolderColumnInfo[i];
        if (localShellFolderColumnInfo.isVisible())
        {
          this.columnMap[localArrayList.size()] = i;
          localArrayList.add(localShellFolderColumnInfo);
        }
      }
      this.columns = new ShellFolderColumnInfo[localArrayList.size()];
      localArrayList.toArray(this.columns);
      this.columnMap = Arrays.copyOf(this.columnMap, this.columns.length);
      List localList = FilePane.this.rowSorter == null ? null : FilePane.this.rowSorter.getSortKeys();
      fireTableStructureChanged();
      restoreSortKeys(localList);
    }
    
    private void restoreSortKeys(List<? extends RowSorter.SortKey> paramList)
    {
      if (paramList != null)
      {
        for (int i = 0; i < paramList.size(); i++)
        {
          RowSorter.SortKey localSortKey = (RowSorter.SortKey)paramList.get(i);
          if (localSortKey.getColumn() >= this.columns.length)
          {
            paramList = null;
            break;
          }
        }
        if (paramList != null) {
          FilePane.this.rowSorter.setSortKeys(paramList);
        }
      }
    }
    
    public int getRowCount()
    {
      return this.directoryModel.getSize();
    }
    
    public int getColumnCount()
    {
      return this.columns.length;
    }
    
    public Object getValueAt(int paramInt1, int paramInt2)
    {
      return getFileColumnValue((File)this.directoryModel.getElementAt(paramInt1), paramInt2);
    }
    
    private Object getFileColumnValue(File paramFile, int paramInt)
    {
      return paramInt == 0 ? paramFile : ShellFolder.getFolderColumnValue(paramFile, this.columnMap[paramInt]);
    }
    
    public void setValueAt(Object paramObject, int paramInt1, int paramInt2)
    {
      if (paramInt2 == 0)
      {
        final JFileChooser localJFileChooser = FilePane.this.getFileChooser();
        File localFile1 = (File)getValueAt(paramInt1, paramInt2);
        if (localFile1 != null)
        {
          String str1 = localJFileChooser.getName(localFile1);
          String str2 = localFile1.getName();
          String str3 = ((String)paramObject).trim();
          if (!str3.equals(str1))
          {
            String str4 = str3;
            int i = str2.length();
            int j = str1.length();
            if ((i > j) && (str2.charAt(j) == '.')) {
              str4 = str3 + str2.substring(j);
            }
            FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
            final File localFile2 = localFileSystemView.createFileObject(localFile1.getParentFile(), str4);
            if (localFile2.exists()) {
              JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(FilePane.this.renameErrorFileExistsText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
            } else if (FilePane.this.getModel().renameFile(localFile1, localFile2))
            {
              if (localFileSystemView.isParent(localJFileChooser.getCurrentDirectory(), localFile2)) {
                SwingUtilities.invokeLater(new Runnable()
                {
                  public void run()
                  {
                    if (localJFileChooser.isMultiSelectionEnabled()) {
                      localJFileChooser.setSelectedFiles(new File[] { localFile2 });
                    } else {
                      localJFileChooser.setSelectedFile(localFile2);
                    }
                  }
                });
              }
            }
            else {
              JOptionPane.showMessageDialog(localJFileChooser, MessageFormat.format(FilePane.this.renameErrorText, new Object[] { str2 }), FilePane.this.renameErrorTitleText, 0);
            }
          }
        }
      }
    }
    
    public boolean isCellEditable(int paramInt1, int paramInt2)
    {
      File localFile = FilePane.this.getFileChooser().getCurrentDirectory();
      return (!FilePane.this.readOnly) && (paramInt2 == 0) && (FilePane.this.canWrite(localFile));
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      new FilePane.DelayedSelectionUpdater(FilePane.this);
      fireTableDataChanged();
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      int i = paramListDataEvent.getIndex0();
      int j = paramListDataEvent.getIndex1();
      if (i == j)
      {
        File localFile = (File)FilePane.this.getModel().getElementAt(i);
        if (localFile.equals(FilePane.this.newFolderFile))
        {
          new FilePane.DelayedSelectionUpdater(FilePane.this, localFile);
          FilePane.this.newFolderFile = null;
        }
      }
      fireTableRowsInserted(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      fireTableRowsDeleted(paramListDataEvent.getIndex0(), paramListDataEvent.getIndex1());
    }
    
    public ShellFolderColumnInfo[] getColumns()
    {
      return this.columns;
    }
  }
  
  private class DetailsTableRowSorter
    extends TableRowSorter<TableModel>
  {
    public DetailsTableRowSorter()
    {
      setModelWrapper(new SorterModelWrapper(null));
    }
    
    public void updateComparators(ShellFolderColumnInfo[] paramArrayOfShellFolderColumnInfo)
    {
      for (int i = 0; i < paramArrayOfShellFolderColumnInfo.length; i++)
      {
        Object localObject = paramArrayOfShellFolderColumnInfo[i].getComparator();
        if (localObject != null) {
          localObject = new FilePane.DirectoriesFirstComparatorWrapper(FilePane.this, i, (Comparator)localObject);
        }
        setComparator(i, (Comparator)localObject);
      }
    }
    
    public void sort()
    {
      ShellFolder.invoke(new Callable()
      {
        public Void call()
        {
          FilePane.DetailsTableRowSorter.this.sort();
          return null;
        }
      });
    }
    
    public void modelStructureChanged()
    {
      super.modelStructureChanged();
      updateComparators(FilePane.this.detailsTableModel.getColumns());
    }
    
    private class SorterModelWrapper
      extends DefaultRowSorter.ModelWrapper<TableModel, Integer>
    {
      private SorterModelWrapper() {}
      
      public TableModel getModel()
      {
        return FilePane.this.getDetailsTableModel();
      }
      
      public int getColumnCount()
      {
        return FilePane.this.getDetailsTableModel().getColumnCount();
      }
      
      public int getRowCount()
      {
        return FilePane.this.getDetailsTableModel().getRowCount();
      }
      
      public Object getValueAt(int paramInt1, int paramInt2)
      {
        return FilePane.this.getModel().getElementAt(paramInt1);
      }
      
      public Integer getIdentifier(int paramInt)
      {
        return Integer.valueOf(paramInt);
      }
    }
  }
  
  private class DirectoriesFirstComparatorWrapper
    implements Comparator<File>
  {
    private Comparator comparator;
    private int column;
    
    public DirectoriesFirstComparatorWrapper(int paramInt, Comparator paramComparator)
    {
      this.column = paramInt;
      this.comparator = paramComparator;
    }
    
    public int compare(File paramFile1, File paramFile2)
    {
      if ((paramFile1 != null) && (paramFile2 != null))
      {
        boolean bool1 = FilePane.this.getFileChooser().isTraversable(paramFile1);
        boolean bool2 = FilePane.this.getFileChooser().isTraversable(paramFile2);
        if ((bool1) && (!bool2)) {
          return -1;
        }
        if ((!bool1) && (bool2)) {
          return 1;
        }
      }
      if (FilePane.this.detailsTableModel.getColumns()[this.column].isCompareByColumn()) {
        return this.comparator.compare(FilePane.access$900(FilePane.this).getFileColumnValue(paramFile1, this.column), FilePane.access$900(FilePane.this).getFileColumnValue(paramFile2, this.column));
      }
      return this.comparator.compare(paramFile1, paramFile2);
    }
  }
  
  class EditActionListener
    implements ActionListener
  {
    EditActionListener() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      FilePane.this.applyEdit();
    }
  }
  
  public static abstract interface FileChooserUIAccessor
  {
    public abstract JFileChooser getFileChooser();
    
    public abstract BasicDirectoryModel getModel();
    
    public abstract JPanel createList();
    
    public abstract JPanel createDetailsView();
    
    public abstract boolean isDirectorySelected();
    
    public abstract File getDirectory();
    
    public abstract Action getApproveSelectionAction();
    
    public abstract Action getChangeToParentDirectoryAction();
    
    public abstract Action getNewFolderAction();
    
    public abstract MouseListener createDoubleClickListener(JList paramJList);
    
    public abstract ListSelectionListener createListSelectionListener();
  }
  
  protected class FileRenderer
    extends DefaultListCellRenderer
  {
    protected FileRenderer() {}
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((FilePane.this.listViewWindowsStyle) && (!paramJList.isFocusOwner())) {
        paramBoolean1 = false;
      }
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      File localFile = (File)paramObject;
      String str = FilePane.this.getFileChooser().getName(localFile);
      setText(str);
      setFont(paramJList.getFont());
      Icon localIcon = FilePane.this.getFileChooser().getIcon(localFile);
      if (localIcon != null) {
        setIcon(localIcon);
      } else if (FilePane.this.getFileChooser().getFileSystemView().isTraversable(localFile).booleanValue()) {
        setText(str + File.separator);
      }
      return this;
    }
  }
  
  private class Handler
    implements MouseListener
  {
    private MouseListener doubleClickListener;
    
    private Handler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
      int i;
      Object localObject;
      if ((localJComponent instanceof JList))
      {
        i = SwingUtilities2.loc2IndexFileList(FilePane.this.list, paramMouseEvent.getPoint());
      }
      else if ((localJComponent instanceof JTable))
      {
        localObject = (JTable)localJComponent;
        Point localPoint = paramMouseEvent.getPoint();
        i = ((JTable)localObject).rowAtPoint(localPoint);
        boolean bool = SwingUtilities2.pointOutsidePrefSize((JTable)localObject, i, ((JTable)localObject).columnAtPoint(localPoint), localPoint);
        if ((bool) && (!FilePane.this.fullRowSelection)) {
          return;
        }
        if ((i >= 0) && (FilePane.this.list != null) && (FilePane.this.listSelectionModel.isSelectedIndex(i)))
        {
          Rectangle localRectangle = FilePane.this.list.getCellBounds(i, i);
          paramMouseEvent = new MouseEvent(FilePane.this.list, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), localRectangle.x + 1, localRectangle.y + localRectangle.height / 2, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
        }
      }
      else
      {
        return;
      }
      if ((i >= 0) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)))
      {
        localObject = FilePane.this.getFileChooser();
        if ((paramMouseEvent.getClickCount() == 1) && ((localJComponent instanceof JList)))
        {
          if (((!((JFileChooser)localObject).isMultiSelectionEnabled()) || (((JFileChooser)localObject).getSelectedFiles().length <= 1)) && (i >= 0) && (FilePane.this.listSelectionModel.isSelectedIndex(i)) && (FilePane.this.getEditIndex() == i) && (FilePane.this.editFile == null)) {
            FilePane.this.editFileName(i);
          } else if (i >= 0) {
            FilePane.this.setEditIndex(i);
          } else {
            FilePane.this.resetEditIndex();
          }
        }
        else if (paramMouseEvent.getClickCount() == 2) {
          FilePane.this.resetEditIndex();
        }
      }
      if (getDoubleClickListener() != null) {
        getDoubleClickListener().mouseClicked(paramMouseEvent);
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      JComponent localJComponent = (JComponent)paramMouseEvent.getSource();
      if ((localJComponent instanceof JTable))
      {
        JTable localJTable = (JTable)paramMouseEvent.getSource();
        TransferHandler localTransferHandler1 = FilePane.this.getFileChooser().getTransferHandler();
        TransferHandler localTransferHandler2 = localJTable.getTransferHandler();
        if (localTransferHandler1 != localTransferHandler2) {
          localJTable.setTransferHandler(localTransferHandler1);
        }
        boolean bool = FilePane.this.getFileChooser().getDragEnabled();
        if (bool != localJTable.getDragEnabled()) {
          localJTable.setDragEnabled(bool);
        }
      }
      else if (((localJComponent instanceof JList)) && (getDoubleClickListener() != null))
      {
        getDoubleClickListener().mouseEntered(paramMouseEvent);
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      if (((paramMouseEvent.getSource() instanceof JList)) && (getDoubleClickListener() != null)) {
        getDoubleClickListener().mouseExited(paramMouseEvent);
      }
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (((paramMouseEvent.getSource() instanceof JList)) && (getDoubleClickListener() != null)) {
        getDoubleClickListener().mousePressed(paramMouseEvent);
      }
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (((paramMouseEvent.getSource() instanceof JList)) && (getDoubleClickListener() != null)) {
        getDoubleClickListener().mouseReleased(paramMouseEvent);
      }
    }
    
    private MouseListener getDoubleClickListener()
    {
      if ((this.doubleClickListener == null) && (FilePane.this.list != null)) {
        this.doubleClickListener = FilePane.this.fileChooserUIAccessor.createDoubleClickListener(FilePane.this.list);
      }
      return this.doubleClickListener;
    }
  }
  
  private class SortableListModel
    extends AbstractListModel<Object>
    implements TableModelListener, RowSorterListener
  {
    public SortableListModel()
    {
      FilePane.this.getDetailsTableModel().addTableModelListener(this);
      FilePane.this.getRowSorter().addRowSorterListener(this);
    }
    
    public int getSize()
    {
      return FilePane.this.getModel().getSize();
    }
    
    public Object getElementAt(int paramInt)
    {
      return FilePane.this.getModel().getElementAt(FilePane.this.getRowSorter().convertRowIndexToModel(paramInt));
    }
    
    public void tableChanged(TableModelEvent paramTableModelEvent)
    {
      fireContentsChanged(this, 0, getSize());
    }
    
    public void sorterChanged(RowSorterEvent paramRowSorterEvent)
    {
      fireContentsChanged(this, 0, getSize());
    }
  }
  
  class ViewTypeAction
    extends AbstractAction
  {
    private int viewType;
    
    ViewTypeAction(int paramInt)
    {
      super();
      this.viewType = paramInt;
      String str;
      switch (paramInt)
      {
      case 0: 
        str = "viewTypeList";
        break;
      case 1: 
        str = "viewTypeDetails";
        break;
      default: 
        str = (String)getValue("Name");
      }
      putValue("ActionCommandKey", str);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      FilePane.this.setViewType(this.viewType);
    }
  }
}
