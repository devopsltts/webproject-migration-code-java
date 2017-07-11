package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.UIResource;
import sun.awt.shell.ShellFolder;
import sun.swing.DefaultLookup;
import sun.swing.FilePane;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicFileChooserUI
  extends FileChooserUI
{
  protected Icon directoryIcon = null;
  protected Icon fileIcon = null;
  protected Icon computerIcon = null;
  protected Icon hardDriveIcon = null;
  protected Icon floppyDriveIcon = null;
  protected Icon newFolderIcon = null;
  protected Icon upFolderIcon = null;
  protected Icon homeFolderIcon = null;
  protected Icon listViewIcon = null;
  protected Icon detailsViewIcon = null;
  protected Icon viewMenuIcon = null;
  protected int saveButtonMnemonic = 0;
  protected int openButtonMnemonic = 0;
  protected int cancelButtonMnemonic = 0;
  protected int updateButtonMnemonic = 0;
  protected int helpButtonMnemonic = 0;
  protected int directoryOpenButtonMnemonic = 0;
  protected String saveButtonText = null;
  protected String openButtonText = null;
  protected String cancelButtonText = null;
  protected String updateButtonText = null;
  protected String helpButtonText = null;
  protected String directoryOpenButtonText = null;
  private String openDialogTitleText = null;
  private String saveDialogTitleText = null;
  protected String saveButtonToolTipText = null;
  protected String openButtonToolTipText = null;
  protected String cancelButtonToolTipText = null;
  protected String updateButtonToolTipText = null;
  protected String helpButtonToolTipText = null;
  protected String directoryOpenButtonToolTipText = null;
  private Action approveSelectionAction = new ApproveSelectionAction();
  private Action cancelSelectionAction = new CancelSelectionAction();
  private Action updateAction = new UpdateAction();
  private Action newFolderAction;
  private Action goHomeAction = new GoHomeAction();
  private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();
  private String newFolderErrorSeparator = null;
  private String newFolderErrorText = null;
  private String newFolderParentDoesntExistTitleText = null;
  private String newFolderParentDoesntExistText = null;
  private String fileDescriptionText = null;
  private String directoryDescriptionText = null;
  private JFileChooser filechooser = null;
  private boolean directorySelected = false;
  private File directory = null;
  private PropertyChangeListener propertyChangeListener = null;
  private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
  private FileFilter actualFileFilter = null;
  private GlobFilter globFilter = null;
  private BasicDirectoryModel model = null;
  private BasicFileView fileView = new BasicFileView();
  private boolean usesSingleFilePane;
  private boolean readOnly;
  private JPanel accessoryPanel = null;
  private Handler handler;
  private static final TransferHandler defaultTransferHandler = new FileTransferHandler();
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicFileChooserUI((JFileChooser)paramJComponent);
  }
  
  public BasicFileChooserUI(JFileChooser paramJFileChooser) {}
  
  public void installUI(JComponent paramJComponent)
  {
    this.accessoryPanel = new JPanel(new BorderLayout());
    this.filechooser = ((JFileChooser)paramJComponent);
    createModel();
    clearIconCache();
    installDefaults(this.filechooser);
    installComponents(this.filechooser);
    installListeners(this.filechooser);
    this.filechooser.applyComponentOrientation(this.filechooser.getComponentOrientation());
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallListeners(this.filechooser);
    uninstallComponents(this.filechooser);
    uninstallDefaults(this.filechooser);
    if (this.accessoryPanel != null) {
      this.accessoryPanel.removeAll();
    }
    this.accessoryPanel = null;
    getFileChooser().removeAll();
    this.handler = null;
  }
  
  public void installComponents(JFileChooser paramJFileChooser) {}
  
  public void uninstallComponents(JFileChooser paramJFileChooser) {}
  
  protected void installListeners(JFileChooser paramJFileChooser)
  {
    this.propertyChangeListener = createPropertyChangeListener(paramJFileChooser);
    if (this.propertyChangeListener != null) {
      paramJFileChooser.addPropertyChangeListener(this.propertyChangeListener);
    }
    paramJFileChooser.addPropertyChangeListener(getModel());
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(paramJFileChooser, 1, localInputMap);
    ActionMap localActionMap = getActionMap();
    SwingUtilities.replaceUIActionMap(paramJFileChooser, localActionMap);
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(getFileChooser(), this, "FileChooser.ancestorInputMap");
    }
    return null;
  }
  
  ActionMap getActionMap()
  {
    return createActionMap();
  }
  
  ActionMap createActionMap()
  {
    ActionMapUIResource localActionMapUIResource = new ActionMapUIResource();
    UIAction local1 = new UIAction("refresh")
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        BasicFileChooserUI.this.getFileChooser().rescanCurrentDirectory();
      }
    };
    localActionMapUIResource.put("approveSelection", getApproveSelectionAction());
    localActionMapUIResource.put("cancelSelection", getCancelSelectionAction());
    localActionMapUIResource.put("refresh", local1);
    localActionMapUIResource.put("Go Up", getChangeToParentDirectoryAction());
    return localActionMapUIResource;
  }
  
  protected void uninstallListeners(JFileChooser paramJFileChooser)
  {
    if (this.propertyChangeListener != null) {
      paramJFileChooser.removePropertyChangeListener(this.propertyChangeListener);
    }
    paramJFileChooser.removePropertyChangeListener(getModel());
    SwingUtilities.replaceUIInputMap(paramJFileChooser, 1, null);
    SwingUtilities.replaceUIActionMap(paramJFileChooser, null);
  }
  
  protected void installDefaults(JFileChooser paramJFileChooser)
  {
    installIcons(paramJFileChooser);
    installStrings(paramJFileChooser);
    this.usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
    this.readOnly = UIManager.getBoolean("FileChooser.readOnly");
    TransferHandler localTransferHandler = paramJFileChooser.getTransferHandler();
    if ((localTransferHandler == null) || ((localTransferHandler instanceof UIResource))) {
      paramJFileChooser.setTransferHandler(defaultTransferHandler);
    }
    LookAndFeel.installProperty(paramJFileChooser, "opaque", Boolean.FALSE);
  }
  
  protected void installIcons(JFileChooser paramJFileChooser)
  {
    this.directoryIcon = UIManager.getIcon("FileView.directoryIcon");
    this.fileIcon = UIManager.getIcon("FileView.fileIcon");
    this.computerIcon = UIManager.getIcon("FileView.computerIcon");
    this.hardDriveIcon = UIManager.getIcon("FileView.hardDriveIcon");
    this.floppyDriveIcon = UIManager.getIcon("FileView.floppyDriveIcon");
    this.newFolderIcon = UIManager.getIcon("FileChooser.newFolderIcon");
    this.upFolderIcon = UIManager.getIcon("FileChooser.upFolderIcon");
    this.homeFolderIcon = UIManager.getIcon("FileChooser.homeFolderIcon");
    this.detailsViewIcon = UIManager.getIcon("FileChooser.detailsViewIcon");
    this.listViewIcon = UIManager.getIcon("FileChooser.listViewIcon");
    this.viewMenuIcon = UIManager.getIcon("FileChooser.viewMenuIcon");
  }
  
  protected void installStrings(JFileChooser paramJFileChooser)
  {
    Locale localLocale = paramJFileChooser.getLocale();
    this.newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText", localLocale);
    this.newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator", localLocale);
    this.newFolderParentDoesntExistTitleText = UIManager.getString("FileChooser.newFolderParentDoesntExistTitleText", localLocale);
    this.newFolderParentDoesntExistText = UIManager.getString("FileChooser.newFolderParentDoesntExistText", localLocale);
    this.fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText", localLocale);
    this.directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText", localLocale);
    this.saveButtonText = UIManager.getString("FileChooser.saveButtonText", localLocale);
    this.openButtonText = UIManager.getString("FileChooser.openButtonText", localLocale);
    this.saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText", localLocale);
    this.openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText", localLocale);
    this.cancelButtonText = UIManager.getString("FileChooser.cancelButtonText", localLocale);
    this.updateButtonText = UIManager.getString("FileChooser.updateButtonText", localLocale);
    this.helpButtonText = UIManager.getString("FileChooser.helpButtonText", localLocale);
    this.directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText", localLocale);
    this.saveButtonMnemonic = getMnemonic("FileChooser.saveButtonMnemonic", localLocale);
    this.openButtonMnemonic = getMnemonic("FileChooser.openButtonMnemonic", localLocale);
    this.cancelButtonMnemonic = getMnemonic("FileChooser.cancelButtonMnemonic", localLocale);
    this.updateButtonMnemonic = getMnemonic("FileChooser.updateButtonMnemonic", localLocale);
    this.helpButtonMnemonic = getMnemonic("FileChooser.helpButtonMnemonic", localLocale);
    this.directoryOpenButtonMnemonic = getMnemonic("FileChooser.directoryOpenButtonMnemonic", localLocale);
    this.saveButtonToolTipText = UIManager.getString("FileChooser.saveButtonToolTipText", localLocale);
    this.openButtonToolTipText = UIManager.getString("FileChooser.openButtonToolTipText", localLocale);
    this.cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText", localLocale);
    this.updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText", localLocale);
    this.helpButtonToolTipText = UIManager.getString("FileChooser.helpButtonToolTipText", localLocale);
    this.directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText", localLocale);
  }
  
  protected void uninstallDefaults(JFileChooser paramJFileChooser)
  {
    uninstallIcons(paramJFileChooser);
    uninstallStrings(paramJFileChooser);
    if ((paramJFileChooser.getTransferHandler() instanceof UIResource)) {
      paramJFileChooser.setTransferHandler(null);
    }
  }
  
  protected void uninstallIcons(JFileChooser paramJFileChooser)
  {
    this.directoryIcon = null;
    this.fileIcon = null;
    this.computerIcon = null;
    this.hardDriveIcon = null;
    this.floppyDriveIcon = null;
    this.newFolderIcon = null;
    this.upFolderIcon = null;
    this.homeFolderIcon = null;
    this.detailsViewIcon = null;
    this.listViewIcon = null;
    this.viewMenuIcon = null;
  }
  
  protected void uninstallStrings(JFileChooser paramJFileChooser)
  {
    this.saveButtonText = null;
    this.openButtonText = null;
    this.cancelButtonText = null;
    this.updateButtonText = null;
    this.helpButtonText = null;
    this.directoryOpenButtonText = null;
    this.saveButtonToolTipText = null;
    this.openButtonToolTipText = null;
    this.cancelButtonToolTipText = null;
    this.updateButtonToolTipText = null;
    this.helpButtonToolTipText = null;
    this.directoryOpenButtonToolTipText = null;
  }
  
  protected void createModel()
  {
    if (this.model != null) {
      this.model.invalidateFileCache();
    }
    this.model = new BasicDirectoryModel(getFileChooser());
  }
  
  public BasicDirectoryModel getModel()
  {
    return this.model;
  }
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
  {
    return null;
  }
  
  public String getFileName()
  {
    return null;
  }
  
  public String getDirectoryName()
  {
    return null;
  }
  
  public void setFileName(String paramString) {}
  
  public void setDirectoryName(String paramString) {}
  
  public void rescanCurrentDirectory(JFileChooser paramJFileChooser) {}
  
  public void ensureFileIsVisible(JFileChooser paramJFileChooser, File paramFile) {}
  
  public JFileChooser getFileChooser()
  {
    return this.filechooser;
  }
  
  public JPanel getAccessoryPanel()
  {
    return this.accessoryPanel;
  }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser)
  {
    return null;
  }
  
  public JButton getDefaultButton(JFileChooser paramJFileChooser)
  {
    return getApproveButton(paramJFileChooser);
  }
  
  public String getApproveButtonToolTipText(JFileChooser paramJFileChooser)
  {
    String str = paramJFileChooser.getApproveButtonToolTipText();
    if (str != null) {
      return str;
    }
    if (paramJFileChooser.getDialogType() == 0) {
      return this.openButtonToolTipText;
    }
    if (paramJFileChooser.getDialogType() == 1) {
      return this.saveButtonToolTipText;
    }
    return null;
  }
  
  public void clearIconCache()
  {
    this.fileView.clearIconCache();
  }
  
  private Handler getHandler()
  {
    if (this.handler == null) {
      this.handler = new Handler();
    }
    return this.handler;
  }
  
  protected MouseListener createDoubleClickListener(JFileChooser paramJFileChooser, JList paramJList)
  {
    return new Handler(paramJList);
  }
  
  public ListSelectionListener createListSelectionListener(JFileChooser paramJFileChooser)
  {
    return getHandler();
  }
  
  protected boolean isDirectorySelected()
  {
    return this.directorySelected;
  }
  
  protected void setDirectorySelected(boolean paramBoolean)
  {
    this.directorySelected = paramBoolean;
  }
  
  protected File getDirectory()
  {
    return this.directory;
  }
  
  protected void setDirectory(File paramFile)
  {
    this.directory = paramFile;
  }
  
  private int getMnemonic(String paramString, Locale paramLocale)
  {
    return SwingUtilities2.getUIDefaultsInt(paramString, paramLocale);
  }
  
  public FileFilter getAcceptAllFileFilter(JFileChooser paramJFileChooser)
  {
    return this.acceptAllFileFilter;
  }
  
  public FileView getFileView(JFileChooser paramJFileChooser)
  {
    return this.fileView;
  }
  
  public String getDialogTitle(JFileChooser paramJFileChooser)
  {
    String str = paramJFileChooser.getDialogTitle();
    if (str != null) {
      return str;
    }
    if (paramJFileChooser.getDialogType() == 0) {
      return this.openDialogTitleText;
    }
    if (paramJFileChooser.getDialogType() == 1) {
      return this.saveDialogTitleText;
    }
    return getApproveButtonText(paramJFileChooser);
  }
  
  public int getApproveButtonMnemonic(JFileChooser paramJFileChooser)
  {
    int i = paramJFileChooser.getApproveButtonMnemonic();
    if (i > 0) {
      return i;
    }
    if (paramJFileChooser.getDialogType() == 0) {
      return this.openButtonMnemonic;
    }
    if (paramJFileChooser.getDialogType() == 1) {
      return this.saveButtonMnemonic;
    }
    return i;
  }
  
  public String getApproveButtonText(JFileChooser paramJFileChooser)
  {
    String str = paramJFileChooser.getApproveButtonText();
    if (str != null) {
      return str;
    }
    if (paramJFileChooser.getDialogType() == 0) {
      return this.openButtonText;
    }
    if (paramJFileChooser.getDialogType() == 1) {
      return this.saveButtonText;
    }
    return null;
  }
  
  public Action getNewFolderAction()
  {
    if (this.newFolderAction == null)
    {
      this.newFolderAction = new NewFolderAction();
      if (this.readOnly) {
        this.newFolderAction.setEnabled(false);
      }
    }
    return this.newFolderAction;
  }
  
  public Action getGoHomeAction()
  {
    return this.goHomeAction;
  }
  
  public Action getChangeToParentDirectoryAction()
  {
    return this.changeToParentDirectoryAction;
  }
  
  public Action getApproveSelectionAction()
  {
    return this.approveSelectionAction;
  }
  
  public Action getCancelSelectionAction()
  {
    return this.cancelSelectionAction;
  }
  
  public Action getUpdateAction()
  {
    return this.updateAction;
  }
  
  private void resetGlobFilter()
  {
    if (this.actualFileFilter != null)
    {
      JFileChooser localJFileChooser = getFileChooser();
      FileFilter localFileFilter = localJFileChooser.getFileFilter();
      if ((localFileFilter != null) && (localFileFilter.equals(this.globFilter)))
      {
        localJFileChooser.setFileFilter(this.actualFileFilter);
        localJFileChooser.removeChoosableFileFilter(this.globFilter);
      }
      this.actualFileFilter = null;
    }
  }
  
  private static boolean isGlobPattern(String paramString)
  {
    return ((File.separatorChar == '\\') && ((paramString.indexOf('*') >= 0) || (paramString.indexOf('?') >= 0))) || ((File.separatorChar == '/') && ((paramString.indexOf('*') >= 0) || (paramString.indexOf('?') >= 0) || (paramString.indexOf('[') >= 0)));
  }
  
  private void changeDirectory(File paramFile)
  {
    JFileChooser localJFileChooser = getFileChooser();
    if ((paramFile != null) && (FilePane.usesShellFolder(localJFileChooser))) {
      try
      {
        ShellFolder localShellFolder1 = ShellFolder.getShellFolder(paramFile);
        if (localShellFolder1.isLink())
        {
          ShellFolder localShellFolder2 = localShellFolder1.getLinkLocation();
          if (localShellFolder2 != null)
          {
            if (localJFileChooser.isTraversable(localShellFolder2)) {
              paramFile = localShellFolder2;
            }
          }
          else {
            paramFile = localShellFolder1;
          }
        }
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        return;
      }
    }
    localJFileChooser.setCurrentDirectory(paramFile);
    if ((localJFileChooser.getFileSelectionMode() == 2) && (localJFileChooser.getFileSystemView().isFileSystem(paramFile))) {
      setFileName(paramFile.getAbsolutePath());
    }
  }
  
  protected class AcceptAllFileFilter
    extends FileFilter
  {
    public AcceptAllFileFilter() {}
    
    public boolean accept(File paramFile)
    {
      return true;
    }
    
    public String getDescription()
    {
      return UIManager.getString("FileChooser.acceptAllFileFilterText");
    }
  }
  
  protected class ApproveSelectionAction
    extends AbstractAction
  {
    protected ApproveSelectionAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicFileChooserUI.this.isDirectorySelected())
      {
        localObject1 = BasicFileChooserUI.this.getDirectory();
        if (localObject1 != null)
        {
          try
          {
            localObject1 = ShellFolder.getNormalizedFile((File)localObject1);
          }
          catch (IOException localIOException) {}
          BasicFileChooserUI.this.changeDirectory((File)localObject1);
          return;
        }
      }
      Object localObject1 = BasicFileChooserUI.this.getFileChooser();
      String str1 = BasicFileChooserUI.this.getFileName();
      FileSystemView localFileSystemView = ((JFileChooser)localObject1).getFileSystemView();
      File localFile1 = ((JFileChooser)localObject1).getCurrentDirectory();
      if (str1 != null)
      {
        for (int i = str1.length() - 1; (i >= 0) && (str1.charAt(i) <= ' '); i--) {}
        str1 = str1.substring(0, i + 1);
      }
      if ((str1 == null) || (str1.length() == 0))
      {
        BasicFileChooserUI.this.resetGlobFilter();
        return;
      }
      File localFile2 = null;
      File[] arrayOfFile1 = null;
      if (File.separatorChar == '/') {
        if (str1.startsWith("~/")) {
          str1 = System.getProperty("user.home") + str1.substring(1);
        } else if (str1.equals("~")) {
          str1 = System.getProperty("user.home");
        }
      }
      Object localObject2;
      if ((((JFileChooser)localObject1).isMultiSelectionEnabled()) && (str1.length() > 1) && (str1.charAt(0) == '"') && (str1.charAt(str1.length() - 1) == '"'))
      {
        localObject2 = new ArrayList();
        String[] arrayOfString1 = str1.substring(1, str1.length() - 1).split("\" \"");
        Arrays.sort(arrayOfString1);
        File[] arrayOfFile2 = null;
        int m = 0;
        for (String str2 : arrayOfString1)
        {
          File localFile3 = localFileSystemView.createFileObject(str2);
          if (!localFile3.isAbsolute())
          {
            if (arrayOfFile2 == null)
            {
              arrayOfFile2 = localFileSystemView.getFiles(localFile1, false);
              Arrays.sort(arrayOfFile2);
            }
            for (int i2 = 0; i2 < arrayOfFile2.length; i2++)
            {
              int i3 = (m + i2) % arrayOfFile2.length;
              if (arrayOfFile2[i3].getName().equals(str2))
              {
                localFile3 = arrayOfFile2[i3];
                m = i3 + 1;
                break;
              }
            }
          }
          ((List)localObject2).add(localFile3);
        }
        if (!((List)localObject2).isEmpty()) {
          arrayOfFile1 = (File[])((List)localObject2).toArray(new File[((List)localObject2).size()]);
        }
        BasicFileChooserUI.this.resetGlobFilter();
      }
      else
      {
        localFile2 = localFileSystemView.createFileObject(str1);
        if (!localFile2.isAbsolute()) {
          localFile2 = localFileSystemView.getChild(localFile1, str1);
        }
        localObject2 = ((JFileChooser)localObject1).getFileFilter();
        if ((!localFile2.exists()) && (BasicFileChooserUI.isGlobPattern(str1)))
        {
          BasicFileChooserUI.this.changeDirectory(localFile2.getParentFile());
          if (BasicFileChooserUI.this.globFilter == null) {
            BasicFileChooserUI.this.globFilter = new BasicFileChooserUI.GlobFilter(BasicFileChooserUI.this);
          }
          try
          {
            BasicFileChooserUI.this.globFilter.setPattern(localFile2.getName());
            if (!(localObject2 instanceof BasicFileChooserUI.GlobFilter)) {
              BasicFileChooserUI.this.actualFileFilter = ((FileFilter)localObject2);
            }
            ((JFileChooser)localObject1).setFileFilter(null);
            ((JFileChooser)localObject1).setFileFilter(BasicFileChooserUI.this.globFilter);
            return;
          }
          catch (PatternSyntaxException localPatternSyntaxException) {}
        }
        BasicFileChooserUI.this.resetGlobFilter();
        int j = (localFile2 != null) && (localFile2.isDirectory()) ? 1 : 0;
        int k = (localFile2 != null) && (((JFileChooser)localObject1).isTraversable(localFile2)) ? 1 : 0;
        boolean bool1 = ((JFileChooser)localObject1).isDirectorySelectionEnabled();
        boolean bool2 = ((JFileChooser)localObject1).isFileSelectionEnabled();
        ??? = (paramActionEvent != null) && ((paramActionEvent.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0) ? 1 : 0;
        if ((j != 0) && (k != 0) && ((??? != 0) || (!bool1)))
        {
          BasicFileChooserUI.this.changeDirectory(localFile2);
          return;
        }
        if (((j != 0) || (!bool2)) && ((j == 0) || (!bool1)) && ((!bool1) || (localFile2.exists()))) {
          localFile2 = null;
        }
      }
      if ((arrayOfFile1 != null) || (localFile2 != null))
      {
        if ((arrayOfFile1 != null) || (((JFileChooser)localObject1).isMultiSelectionEnabled()))
        {
          if (arrayOfFile1 == null) {
            arrayOfFile1 = new File[] { localFile2 };
          }
          ((JFileChooser)localObject1).setSelectedFiles(arrayOfFile1);
          ((JFileChooser)localObject1).setSelectedFiles(arrayOfFile1);
        }
        else
        {
          ((JFileChooser)localObject1).setSelectedFile(localFile2);
        }
        ((JFileChooser)localObject1).approveSelection();
      }
      else
      {
        if (((JFileChooser)localObject1).isMultiSelectionEnabled()) {
          ((JFileChooser)localObject1).setSelectedFiles(null);
        } else {
          ((JFileChooser)localObject1).setSelectedFile(null);
        }
        ((JFileChooser)localObject1).cancelSelection();
      }
    }
  }
  
  protected class BasicFileView
    extends FileView
  {
    protected Hashtable<File, Icon> iconCache = new Hashtable();
    
    public BasicFileView() {}
    
    public void clearIconCache()
    {
      this.iconCache = new Hashtable();
    }
    
    public String getName(File paramFile)
    {
      String str = null;
      if (paramFile != null) {
        str = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemDisplayName(paramFile);
      }
      return str;
    }
    
    public String getDescription(File paramFile)
    {
      return paramFile.getName();
    }
    
    public String getTypeDescription(File paramFile)
    {
      String str = BasicFileChooserUI.this.getFileChooser().getFileSystemView().getSystemTypeDescription(paramFile);
      if (str == null) {
        if (paramFile.isDirectory()) {
          str = BasicFileChooserUI.this.directoryDescriptionText;
        } else {
          str = BasicFileChooserUI.this.fileDescriptionText;
        }
      }
      return str;
    }
    
    public Icon getCachedIcon(File paramFile)
    {
      return (Icon)this.iconCache.get(paramFile);
    }
    
    public void cacheIcon(File paramFile, Icon paramIcon)
    {
      if ((paramFile == null) || (paramIcon == null)) {
        return;
      }
      this.iconCache.put(paramFile, paramIcon);
    }
    
    public Icon getIcon(File paramFile)
    {
      Icon localIcon = getCachedIcon(paramFile);
      if (localIcon != null) {
        return localIcon;
      }
      localIcon = BasicFileChooserUI.this.fileIcon;
      if (paramFile != null)
      {
        FileSystemView localFileSystemView = BasicFileChooserUI.this.getFileChooser().getFileSystemView();
        if (localFileSystemView.isFloppyDrive(paramFile)) {
          localIcon = BasicFileChooserUI.this.floppyDriveIcon;
        } else if (localFileSystemView.isDrive(paramFile)) {
          localIcon = BasicFileChooserUI.this.hardDriveIcon;
        } else if (localFileSystemView.isComputerNode(paramFile)) {
          localIcon = BasicFileChooserUI.this.computerIcon;
        } else if (paramFile.isDirectory()) {
          localIcon = BasicFileChooserUI.this.directoryIcon;
        }
      }
      cacheIcon(paramFile, localIcon);
      return localIcon;
    }
    
    public Boolean isHidden(File paramFile)
    {
      String str = paramFile.getName();
      if ((str != null) && (str.charAt(0) == '.')) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }
  }
  
  protected class CancelSelectionAction
    extends AbstractAction
  {
    protected CancelSelectionAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      BasicFileChooserUI.this.getFileChooser().cancelSelection();
    }
  }
  
  protected class ChangeToParentDirectoryAction
    extends AbstractAction
  {
    protected ChangeToParentDirectoryAction()
    {
      super();
      putValue("ActionCommandKey", "Go Up");
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      BasicFileChooserUI.this.getFileChooser().changeToParentDirectory();
    }
  }
  
  protected class DoubleClickListener
    extends MouseAdapter
  {
    BasicFileChooserUI.Handler handler;
    
    public DoubleClickListener(JList paramJList)
    {
      this.handler = new BasicFileChooserUI.Handler(BasicFileChooserUI.this, paramJList);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      this.handler.mouseEntered(paramMouseEvent);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      this.handler.mouseClicked(paramMouseEvent);
    }
  }
  
  static class FileTransferHandler
    extends TransferHandler
    implements UIResource
  {
    FileTransferHandler() {}
    
    protected Transferable createTransferable(JComponent paramJComponent)
    {
      Object[] arrayOfObject1 = null;
      if ((paramJComponent instanceof JList))
      {
        arrayOfObject1 = ((JList)paramJComponent).getSelectedValues();
      }
      else if ((paramJComponent instanceof JTable))
      {
        localObject1 = (JTable)paramJComponent;
        localObject2 = ((JTable)localObject1).getSelectedRows();
        if (localObject2 != null)
        {
          arrayOfObject1 = new Object[localObject2.length];
          for (int i = 0; i < localObject2.length; i++) {
            arrayOfObject1[i] = ((JTable)localObject1).getValueAt(localObject2[i], 0);
          }
        }
      }
      if ((arrayOfObject1 == null) || (arrayOfObject1.length == 0)) {
        return null;
      }
      Object localObject1 = new StringBuffer();
      Object localObject2 = new StringBuffer();
      ((StringBuffer)localObject2).append("<html>\n<body>\n<ul>\n");
      for (Object localObject3 : arrayOfObject1)
      {
        String str = localObject3 == null ? "" : localObject3.toString();
        ((StringBuffer)localObject1).append(str + "\n");
        ((StringBuffer)localObject2).append("  <li>" + str + "\n");
      }
      ((StringBuffer)localObject1).deleteCharAt(((StringBuffer)localObject1).length() - 1);
      ((StringBuffer)localObject2).append("</ul>\n</body>\n</html>");
      return new FileTransferable(((StringBuffer)localObject1).toString(), ((StringBuffer)localObject2).toString(), arrayOfObject1);
    }
    
    public int getSourceActions(JComponent paramJComponent)
    {
      return 1;
    }
    
    static class FileTransferable
      extends BasicTransferable
    {
      Object[] fileData;
      
      FileTransferable(String paramString1, String paramString2, Object[] paramArrayOfObject)
      {
        super(paramString2);
        this.fileData = paramArrayOfObject;
      }
      
      protected DataFlavor[] getRicherFlavors()
      {
        DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
        arrayOfDataFlavor[0] = DataFlavor.javaFileListFlavor;
        return arrayOfDataFlavor;
      }
      
      protected Object getRicherData(DataFlavor paramDataFlavor)
      {
        if (DataFlavor.javaFileListFlavor.equals(paramDataFlavor))
        {
          ArrayList localArrayList = new ArrayList();
          for (Object localObject : this.fileData) {
            localArrayList.add(localObject);
          }
          return localArrayList;
        }
        return null;
      }
    }
  }
  
  class GlobFilter
    extends FileFilter
  {
    Pattern pattern;
    String globPattern;
    
    GlobFilter() {}
    
    public void setPattern(String paramString)
    {
      char[] arrayOfChar1 = paramString.toCharArray();
      char[] arrayOfChar2 = new char[arrayOfChar1.length * 2];
      int i = File.separatorChar == '\\' ? 1 : 0;
      int j = 0;
      int k = 0;
      this.globPattern = paramString;
      int m;
      if (i != 0)
      {
        m = arrayOfChar1.length;
        if (paramString.endsWith("*.*")) {
          m -= 2;
        }
        for (int n = 0; n < m; n++) {
          switch (arrayOfChar1[n])
          {
          case '*': 
            arrayOfChar2[(k++)] = '.';
            arrayOfChar2[(k++)] = '*';
            break;
          case '?': 
            arrayOfChar2[(k++)] = '.';
            break;
          case '\\': 
            arrayOfChar2[(k++)] = '\\';
            arrayOfChar2[(k++)] = '\\';
            break;
          default: 
            if ("+()^$.{}[]".indexOf(arrayOfChar1[n]) >= 0) {
              arrayOfChar2[(k++)] = '\\';
            }
            arrayOfChar2[(k++)] = arrayOfChar1[n];
          }
        }
      }
      else
      {
        for (m = 0; m < arrayOfChar1.length; m++) {
          switch (arrayOfChar1[m])
          {
          case '*': 
            if (j == 0) {
              arrayOfChar2[(k++)] = '.';
            }
            arrayOfChar2[(k++)] = '*';
            break;
          case '?': 
            arrayOfChar2[(k++)] = (j != 0 ? 63 : '.');
            break;
          case '[': 
            j = 1;
            arrayOfChar2[(k++)] = arrayOfChar1[m];
            if (m < arrayOfChar1.length - 1) {
              switch (arrayOfChar1[(m + 1)])
              {
              case '!': 
              case '^': 
                arrayOfChar2[(k++)] = '^';
                m++;
                break;
              case ']': 
                arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
              }
            }
            break;
          case ']': 
            arrayOfChar2[(k++)] = arrayOfChar1[m];
            j = 0;
            break;
          case '\\': 
            if ((m == 0) && (arrayOfChar1.length > 1) && (arrayOfChar1[1] == '~'))
            {
              arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
            }
            else
            {
              arrayOfChar2[(k++)] = '\\';
              if ((m < arrayOfChar1.length - 1) && ("*?[]".indexOf(arrayOfChar1[(m + 1)]) >= 0)) {
                arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
              } else {
                arrayOfChar2[(k++)] = '\\';
              }
            }
            break;
          default: 
            if (!Character.isLetterOrDigit(arrayOfChar1[m])) {
              arrayOfChar2[(k++)] = '\\';
            }
            arrayOfChar2[(k++)] = arrayOfChar1[m];
          }
        }
      }
      this.pattern = Pattern.compile(new String(arrayOfChar2, 0, k), 2);
    }
    
    public boolean accept(File paramFile)
    {
      if (paramFile == null) {
        return false;
      }
      if (paramFile.isDirectory()) {
        return true;
      }
      return this.pattern.matcher(paramFile.getName()).matches();
    }
    
    public String getDescription()
    {
      return this.globPattern;
    }
  }
  
  protected class GoHomeAction
    extends AbstractAction
  {
    protected GoHomeAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JFileChooser localJFileChooser = BasicFileChooserUI.this.getFileChooser();
      BasicFileChooserUI.this.changeDirectory(localJFileChooser.getFileSystemView().getHomeDirectory());
    }
  }
  
  private class Handler
    implements MouseListener, ListSelectionListener
  {
    JList list;
    
    Handler() {}
    
    Handler(JList paramJList)
    {
      this.list = paramJList;
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      if ((this.list != null) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.getClickCount() % 2 == 0))
      {
        int i = SwingUtilities2.loc2IndexFileList(this.list, paramMouseEvent.getPoint());
        if (i >= 0)
        {
          File localFile = (File)this.list.getModel().getElementAt(i);
          try
          {
            localFile = ShellFolder.getNormalizedFile(localFile);
          }
          catch (IOException localIOException) {}
          if (BasicFileChooserUI.this.getFileChooser().isTraversable(localFile))
          {
            this.list.clearSelection();
            BasicFileChooserUI.this.changeDirectory(localFile);
          }
          else
          {
            BasicFileChooserUI.this.getFileChooser().approveSelection();
          }
        }
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      if (this.list != null)
      {
        TransferHandler localTransferHandler1 = BasicFileChooserUI.this.getFileChooser().getTransferHandler();
        TransferHandler localTransferHandler2 = this.list.getTransferHandler();
        if (localTransferHandler1 != localTransferHandler2) {
          this.list.setTransferHandler(localTransferHandler1);
        }
        if (BasicFileChooserUI.this.getFileChooser().getDragEnabled() != this.list.getDragEnabled()) {
          this.list.setDragEnabled(BasicFileChooserUI.this.getFileChooser().getDragEnabled());
        }
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      if (!paramListSelectionEvent.getValueIsAdjusting())
      {
        JFileChooser localJFileChooser = BasicFileChooserUI.this.getFileChooser();
        FileSystemView localFileSystemView = localJFileChooser.getFileSystemView();
        JList localJList = (JList)paramListSelectionEvent.getSource();
        int i = localJFileChooser.getFileSelectionMode();
        int j = (BasicFileChooserUI.this.usesSingleFilePane) && (i == 0) ? 1 : 0;
        Object localObject1;
        if (localJFileChooser.isMultiSelectionEnabled())
        {
          localObject1 = null;
          Object[] arrayOfObject1 = localJList.getSelectedValues();
          if (arrayOfObject1 != null) {
            if ((arrayOfObject1.length == 1) && (((File)arrayOfObject1[0]).isDirectory()) && (localJFileChooser.isTraversable((File)arrayOfObject1[0])) && ((j != 0) || (!localFileSystemView.isFileSystem((File)arrayOfObject1[0]))))
            {
              BasicFileChooserUI.this.setDirectorySelected(true);
              BasicFileChooserUI.this.setDirectory((File)arrayOfObject1[0]);
            }
            else
            {
              ArrayList localArrayList = new ArrayList(arrayOfObject1.length);
              for (Object localObject2 : arrayOfObject1)
              {
                File localFile = (File)localObject2;
                boolean bool = localFile.isDirectory();
                if (((localJFileChooser.isFileSelectionEnabled()) && (!bool)) || ((localJFileChooser.isDirectorySelectionEnabled()) && (localFileSystemView.isFileSystem(localFile)) && (bool))) {
                  localArrayList.add(localFile);
                }
              }
              if (localArrayList.size() > 0) {
                localObject1 = (File[])localArrayList.toArray(new File[localArrayList.size()]);
              }
              BasicFileChooserUI.this.setDirectorySelected(false);
            }
          }
          localJFileChooser.setSelectedFiles((File[])localObject1);
        }
        else
        {
          localObject1 = (File)localJList.getSelectedValue();
          if ((localObject1 != null) && (((File)localObject1).isDirectory()) && (localJFileChooser.isTraversable((File)localObject1)) && ((j != 0) || (!localFileSystemView.isFileSystem((File)localObject1))))
          {
            BasicFileChooserUI.this.setDirectorySelected(true);
            BasicFileChooserUI.this.setDirectory((File)localObject1);
            if (BasicFileChooserUI.this.usesSingleFilePane) {
              localJFileChooser.setSelectedFile(null);
            }
          }
          else
          {
            BasicFileChooserUI.this.setDirectorySelected(false);
            if (localObject1 != null) {
              localJFileChooser.setSelectedFile((File)localObject1);
            }
          }
        }
      }
    }
  }
  
  protected class NewFolderAction
    extends AbstractAction
  {
    protected NewFolderAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (BasicFileChooserUI.this.readOnly) {
        return;
      }
      JFileChooser localJFileChooser = BasicFileChooserUI.this.getFileChooser();
      File localFile1 = localJFileChooser.getCurrentDirectory();
      if (!localFile1.exists())
      {
        JOptionPane.showMessageDialog(localJFileChooser, BasicFileChooserUI.this.newFolderParentDoesntExistText, BasicFileChooserUI.this.newFolderParentDoesntExistTitleText, 2);
        return;
      }
      try
      {
        File localFile2 = localJFileChooser.getFileSystemView().createNewFolder(localFile1);
        if (localJFileChooser.isMultiSelectionEnabled()) {
          localJFileChooser.setSelectedFiles(new File[] { localFile2 });
        } else {
          localJFileChooser.setSelectedFile(localFile2);
        }
      }
      catch (IOException localIOException)
      {
        JOptionPane.showMessageDialog(localJFileChooser, BasicFileChooserUI.this.newFolderErrorText + BasicFileChooserUI.this.newFolderErrorSeparator + localIOException, BasicFileChooserUI.this.newFolderErrorText, 0);
        return;
      }
      localJFileChooser.rescanCurrentDirectory();
    }
  }
  
  protected class SelectionListener
    implements ListSelectionListener
  {
    protected SelectionListener() {}
    
    public void valueChanged(ListSelectionEvent paramListSelectionEvent)
    {
      BasicFileChooserUI.this.getHandler().valueChanged(paramListSelectionEvent);
    }
  }
  
  protected class UpdateAction
    extends AbstractAction
  {
    protected UpdateAction() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JFileChooser localJFileChooser = BasicFileChooserUI.this.getFileChooser();
      localJFileChooser.setCurrentDirectory(localJFileChooser.getFileSystemView().createFileObject(BasicFileChooserUI.this.getDirectoryName()));
      localJFileChooser.rescanCurrentDirectory();
    }
  }
}
