package com.lnt.demo;

/**This is common interface constants.
 * @author 20125910
 *
 */
public interface IFeeConstants {

	static String BLANK = "";
	static String END_LINE = "\n";
	/**
	 * Small Opening Braces.
	 */
	static char SMALL_OPEN_BR = '(';
	static char SMALL_CLOSE_BR = ')';
	static char CURL_OPEN_BR = '{';
	static char CURL_CLOSE_BR = '}';
	static String DOT = ".";
	static String EQUAL_SYMBL = "=";
	
	/**
	 * FILTER[0] = "public void"
	 * FILTER[1] = "ActionListener"
	 */
	static String[] FILTER={"ActionListener","AncestorListener","AdjustmentListener","CaretListener","CellEditorListener","ChangeListener","ComponentListener",
			"ContainerListener","DocumentListener","ExceptionListener","FocusListener","HierarchyBoundsListener","HierarchyListener","InputMethodListener",
			"InternalFrameListener","ItemListener","KeyListener","ListDataListener","ListSelectionListener","MenuDragMouseListener","MenuKeyListener","MouseListener",
			"MenuListener","MouseMotionListener","MouseWheelListener","PopupMenuListener","PropertyChangeListener","TableColumnModelListener","TableModelListener","TreeExpansionListener",
			"TreeModelListener","TreeSelectionListener","TreeWillExpandListener","UndoableEditListener","VetoableChangeListener","WindowFocusListener","WindowListener","WindowStateListener"                    
	};
	static String[] ALERT_FORMAT = {"alert","confirm"};

}