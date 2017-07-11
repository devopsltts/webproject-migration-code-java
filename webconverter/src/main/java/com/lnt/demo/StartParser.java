package com.lnt.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

public class StartParser {
	private final static Logger logger = Logger.getLogger(StartParser.class);

	static int numOfFilesCopy;
	static int numOfFiles;
	static String outputDirHtml;
	static String serverPath;
	static File inputServerPath;

	static int counterForFiles;

	static String displayLogs;

	// For Target Folder navigation
	static Desktop desktop = Desktop.getDesktop();

	static File fileFolder;
	static int i;

	static int count;

	static String status;

	static String outputDir;
	static int counter;
	static String outputDirIndexHtml;
	static String outputDirCss;
	static String outputDirJs;
	static String outputDirStoreJs;
	static String outputDirContainerJs;
	static String outputDirClientCss;
	static String outputReducerJs;
	static String outputActionJs;
	static String outApiJ;
	static String outputAPIJsMiddleWare;
	static String outApiJs;
	static String outActionJs;
	static String outReducerJs;
	static String outContainerJs;
	static String outStoreJs;
	static String outDirJs;
	static JPanel panel = new JPanel();

	static Map<String, String> CsvData = new HashMap<String, String>();
	
	static ClassDependencyResolver4 cdr = new ClassDependencyResolver4();

	static String fileNamePath;
	static String fileNameExec;

	static ParserToHtml rf = new ParserToHtml();

	static JButton convertButton;
	static JButton resetButton;

	static StringBuilder buffer = new StringBuilder();
	static JTextArea display = new JTextArea(10, 20);

	static Map<String, Map<String, String>> CsvMap = new HashMap<String, Map<String, String>>();

	static JMenu FileMenu = new JMenu("File");
	static JMenu helpMenu = new JMenu("Help");

	static JMenuBar menuBar = new JMenuBar();

	static JTextField userText = new JTextField("D:\\NewWorkspace\\feereportproject\\", 20);

	static JTextField htmlText = new JTextField("D:\\output", 20);

	static JLabel htmlDestLabel = new JLabel("Html Destination Location");

	static JFrame frame = new JFrame();

	// For Help Menu
	static JScrollPane helpScrollpane;
	static JScrollPane aboutScrollpane;
	static JScrollPane scrollpane;

	static String fileOpFolder;
	static int height;
	static int width;

	static ArrayList<File> fileName = new ArrayList<File>();

	static JLabel totalFiles = new JLabel("Total no. of Files parsed: 0");
	static JLabel filesFailed = new JLabel("Files not parse: 0 ");
	static JLabel filesSuccess = new JLabel("Files successfully parsed: 0");
	static JTextArea logsOutput = new JTextArea("Text area");

	static {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		panel.setVisible(false);

		getAllLoggerToPrint("info", "Converter Logger begin");
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultLookAndFeelDecorated(true);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		height = screenSize.height;
		width = screenSize.width;

		// For Top panel Image Icon
		JLabel label1 = new JLabel();

		ImageIcon icon1 = new ImageIcon(new ImageIcon(StartParser.class.getResource("/images/Screenshot_Swing.png")).getImage().getScaledInstance(120,
				120, Image.SCALE_DEFAULT));
		label1.setIcon(icon1);
		label1.setBounds(120, 20, 300, 200);

		JLabel label2 = new JLabel();
		ImageIcon icon2 = new ImageIcon(new ImageIcon(StartParser.class.getResource("/images/Arrow.jpg")).getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
		label2.setIcon(icon2);
		label2.setBounds(350, 20, 300, 220);

		JLabel label3 = new JLabel();
		ImageIcon icon3 = new ImageIcon(new ImageIcon(StartParser.class.getResource("/images/htmlImage.png")).getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
		label3.setIcon(icon3);
		label3.setBounds(550, 20, 200, 220);

		// For adding the label to Panel
		panel.add(label1);
		panel.add(label2);
		panel.add(label3);

		// Setting the border for main Panel
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Converter Panel"));
		panel.setBounds(100, 10, 500, 50);

		panel.setLayout(null);

		// Label for Source folder
		JLabel userFileLabel = new JLabel("Folder to convert");
		userFileLabel.setBounds(70, 150, 200, 250);
		panel.add(userFileLabel);

		// TextBox for Source Folder
		userText.setBounds(220, 250, 250, 35);
		panel.add(userText);

		// Button for Source folder
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(550, 250, 100, 35);
		panel.add(btnBrowse);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				fileChooser.setAcceptAllFileFilterUsed(false);

				int rVal = fileChooser.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					userText.setText(fileChooser.getSelectedFile().toString());
				}
			}
		});

		// Adding Menu Bar
		menuBar.add(FileMenu);
		menuBar.add(helpMenu);

		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem help = new JMenuItem("Help");
		JMenuItem about = new JMenuItem("About");
		exit.addActionListener(new exitApp());

		FileMenu.add(exit);
		helpMenu.add(about);
		helpMenu.add(help);

		// Label for Destination folder
		JLabel htmlDestLabel = new JLabel("Html Source Location");
		htmlDestLabel.setBounds(70, 220, 200, 250);
		panel.add(htmlDestLabel);

		htmlText.setBounds(220, 320, 250, 35);
		panel.add(htmlText);

		JButton browseHtml = new JButton("Browse");
		browseHtml.setBounds(550, 320, 100, 35);
		panel.add(browseHtml);
		browseHtml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int rVal = fileChooser.showOpenDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					htmlText.setText(fileChooser.getSelectedFile().toString());
				}
			}
		});

		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
		frame.pack();
		frame.setSize(width / 2, height);
		frame.add(panel);
		frame.setResizable(false);

		panel.setBackground(Color.WHITE);

		getAllLoggerToPrint("infoEnd", "Test");

		String helpCategories[] = { "Swing to Html Application", "Version 1.0" };
		String aboutCategories[] = { "Swing to Html Application",
				"Swing to Html Converter is for source code location.",
				"By using a browser button we can select the our source code.Html Destination location is for Destination location",
				"we can select the directory to place the converted code.",
				"Converter: Click on converter button it will be converted java swing code to Html code.",
				"Reset: click on reset button we can erase all the text data.",
		"logging:It will be display all the logs of conversion code with respective time and date" };

		JList helpList = new JList(helpCategories);
		JList aboutList = new JList(aboutCategories);

		helpScrollpane = new JScrollPane(helpList);
		aboutScrollpane = new JScrollPane(aboutList);

		helpScrollpane.getViewport().add(helpList);
		aboutScrollpane.getViewport().add(aboutList);

		about.addActionListener(new ActionListener() { // we add an action
			// listener to the
			// JMenuItem
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, aboutScrollpane, "View About", JOptionPane.PLAIN_MESSAGE);
			}
		});

		help.addActionListener(new ActionListener() { // we add an action

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, helpScrollpane, "View Help", JOptionPane.PLAIN_MESSAGE);
			}
		});
		// end help and help about in menu bar

	}

	public static void main(String[] args) throws IOException {

		totalFiles.setBounds(590, 540, 200, 30);

		filesFailed.setBounds(590, 620, 200, 30);

		filesSuccess.setBounds(590, 580, 200, 30);

		logsOutput.setBounds(10, 300, 100, 250);

		resetButton = new JButton("Reset");
		resetButton.setBounds(350, 400, 100, 35);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userText.setText("");
				htmlText.setText("");
			}

		});

		panel.add(resetButton);

		convertButton = new JButton("Convert");

		convertButton.setBounds(220, 400, 100, 35);
		panel.add(convertButton);

		convertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {

				convertButton.setEnabled(false);
				resetButton.setEnabled(false);
				try {
					StartFileParse();
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				getAllLoggerToPrint("debugFile", fileNamePath);
			}
		});

		panel.add(filesSuccess);
		panel.add(filesFailed);
		panel.add(totalFiles);

		JLabel logData = new JLabel("Logs generated...");
		logData.setBounds(100, 380, 200, 250);
		panel.add(logData);

		display.setEditable(true); // set textArea non-editable

		JScrollPane scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(100, 520, 400, 400);

		panel.add(scroll);

		panel.setVisible(true);
		panel.setPreferredSize(new Dimension(640, 480));
		panel.setLayout(new BorderLayout(5, 5));

		getAllLoggerToPrint("infoEnd", "Test");

		System.out.println("******************* END ***********************");

	}

	// creating a java project structure
	public static boolean createDirectoriesWithCommonParent(File parent, String... subs) {

		try {

			parent.mkdirs();

			if (!parent.exists() || !parent.isDirectory()) {
				return false;
			}
			for (String sub : subs) {

				File subFile = new File(parent, sub);
				subFile.mkdirs();
				if (!subFile.exists() || !subFile.isDirectory()) {
					return false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	public static void getAllLoggerToPrint(String param1, String param2) {

		if (param1.equals("info")) {
			logger.info("Converter Logger begin in log");
		}

		if (param1.equals("debugIn")) {
			logger.debug("Input Directory :" + param2);
		}

		if (param1.equals("debugOut")) {
			logger.debug("Output Directory :" + param2);
		}
		if (param1.equals("debugFile")) {
			logger.debug("FileNamePath :" + param2);
		}

		if (param1.equals("debugFile")) {
			logger.debug("FileNamePath :" + param2);
		}

		if (param1.equals("infoEnd")) {
			logger.info("Converter Logger end in log");
		}
	}

	public static void ReadLogFile() throws ParserException, IOException {

		InputStream stream = null;
		File propsFile = null;
		BufferedReader br = null;
		try {
			String home = System.getProperty("user.home");
			propsFile = new File(home, "log4j\\log4j.properties");
			stream = new FileInputStream(propsFile);
			br = new BufferedReader(new InputStreamReader(stream));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				display.setText(display.getText() + strLine + "\r\n");
			}
		} catch (Exception ex) {
			logger.info("Message:" + ex.getMessage());
		} finally {
			br.close();
			stream.close();
		}
	}

	public static void StartFileParse() throws InterruptedException, IOException {
		String inputDir = userText.getText();

		System.out.println(inputDir);

		getAllLoggerToPrint("debugIn", inputDir);
		String outputDir = htmlText.getText();
		fileFolder = new File(outputDir);

		/***** If Any folder File Previously present *****/

		recursiveDelete(fileFolder);

		/******** Those are removed *******/
		createDirectoriesWithCommonParent(fileFolder, "output\\servers", "output\\public", "output\\src\\client",
				"output\\src\\common", "output\\src\\common\\actions", "output\\src\\common\\api",
				"output\\src\\common\\components", "output\\src\\common\\reducers", "output\\src\\common\\store",
				"output\\src\\common\\container");

		serverPath = outputDir + "\\" + "output\\servers";
		outputDirHtml = outputDir + "\\" + "output\\src\\common\\components";
		outputDirIndexHtml = outputDir + "\\" + "output\\public";
		outputDirCss = outputDir + "\\" + "output\\src\\client";
		outputDirJs = outputDir + "\\" + "output\\src\\common";
		outDirJs = outputDir + "\\" + "output\\src";
		outputDirStoreJs = outputDir + "\\" + "output\\src\\common\\store";
		outputDirContainerJs = outputDir + "\\" + "output\\src\\common\\container";
		outputReducerJs = outputDir + "\\" + "output\\src\\common\\reducers";
		outputActionJs = outputDir + "\\" + "output\\src\\common\\actions";
		outApiJs = outputDir + "\\" + "output\\src\\common\\api";
		outApiJ = outputDir + "\\" + "output\\src\\common\\api";

		File directory = new File(inputDir);

		try {
			traverse(directory);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		totalFiles.setText("Total no. of Files parsed:" + String.valueOf(numOfFiles));

		filesFailed.setText("Files not parsed:" + (numOfFiles - numOfFilesCopy));
		filesSuccess.setText("Files successfully parsed:" + numOfFilesCopy);

		fileOpFolder = fileFolder + "\\output\\" + "SummaryReport.csv";

		CsvMap = rf.getCompMap();

		for (Entry<String, Map<String, String>> entry : CsvMap.entrySet()) {
			System.out.println("CsvMap Keys ::::::" + entry.getKey() + " CsvMap Value::::::" + entry.getValue());
		}
		
		cdr.decompiledJarFiles();

		try {
			generateCsvFile(fileOpFolder);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			createDefaultHtml();
			createDefaultCss();
			createDefaultJs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			ReadLogFile();
		} catch (ParserException e1) {
			logger.info("Message" + e1.getMessage());
		}

		getComponents();
		placeHolderAction();
		placeHolderReducer();

	
	}

	// Exit functionality of menu bar
	static class exitApp implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	// CSV file generated code
	public static void generateCsvFile(String fileOpFolder) throws FileNotFoundException {
		PrintWriter pw = null;
		StringBuffer writer = new StringBuffer();
		// FileWriter writer = null;
		try {

			pw = new PrintWriter(fileOpFolder);

			writer.append("Path");
			writer.append(',');
			writer.append("FileName");
			writer.append(',');
			writer.append("Migration Status");
			writer.append(',');
			writer.append('\n');
			for (File fName : fileName) {
				for (Entry<String, String> data : CsvData.entrySet()) {
					if (fName.getName().equals(data.getKey())) {
						writer.append(fName.getPath());
						writer.append(',');
						writer.append(data.getKey());
						writer.append(',');
						writer.append(data.getValue());
						writer.append('\n');
					}
				}
			}

			pw.write(writer.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {

				pw.flush();
				pw.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		convertButton.setEnabled(true);
		resetButton.setEnabled(true);

		try {
			desktop.open(fileFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	private static void traverse(File dir) throws NullPointerException, IOException {
		String inputServerPathName = "";

		if (dir.isDirectory()) {
			String[] children = dir.list();

			for (int i = 0; children != null && i < children.length; i++) {

				if (children[i].equalsIgnoreCase("server")) {
					inputServerPath = new File(dir, children[i]);
					System.out.println("children[i]::"+children[i]+"::inputServerPath::"+inputServerPath.getAbsolutePath());
			
					if (inputServerPath.isDirectory()) {
						inputServerPathName = inputServerPath.getAbsolutePath();
						System.out.println("inputServerPathName:" + inputServerPathName);
						rf.parseFileForServer(serverPath, inputServerPathName);
					}

				} else {
					traverse(new File(dir, children[i]));
				}
			}
		}
		if (dir.isFile()) {

			if (dir.getName().endsWith(".java")) {

				fileName.add(new File(dir.getPath()));
				numOfFiles++;
				counterForFiles = rf.parseFile(dir.getAbsolutePath(), outputDirHtml);
				if (counterForFiles == 1) {
					numOfFilesCopy++;
					CsvData.put(dir.getName(), "Passed");
				} else {
					CsvData.put(dir.getName(), "NA");
				}
			}
		}

	}

	public static void recursiveDelete(File file) {
		// to end the recursive loop
		if (!file.exists())
			return;

		// if directory, go inside and call recursively
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				// call recursively
				recursiveDelete(f);
			}
		}
		// call delete to delete files and empty directory
		file.delete();
		System.out.println("Deleted file/folder: " + file.getAbsolutePath());
	}

	/* create default html file in folder structure */
	public static void createDefaultHtml() throws IOException {
		File fileHtml = new File(outputDirIndexHtml, "index.html");
		/*
		 * if (!fileHtml.exists()) { fileHtml.createNewFile(); }
		 */
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileHtml));
		bw.write("<!doctype html>\n" + "<html lang=\"en\">\n" + "<head>\n" + "<meta charset=\"utf-8\">\n"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
				+ "<link rel=\"shortcut icon\" href=\"%PUBLIC_URL%/favicon.ico\">\n"
				+ "<link rel=\"stylesheet\" href=\"//demo.productionready.io/main.css\">\n"
				+ "<link href=\"//code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css\" rel=\"stylesheet\" type=\"text/css\">\n"
				+ "<link href=\"//fonts.googleapis.com/css?family=Titillium+Web:700|Source+Serif+Pro:400,700|Merriweather+Sans:400,700|Source+Sans+Pro:400,300,600,700,300italic,400italic,600italic,700italic\" rel=\"stylesheet\" "
				+ "type=\"text/css\">\n" + "<!--\n" + " Notice the use of %PUBLIC_URL% in the tag above.\n"
				+ " It will be replaced with the URL of the `public` folder during the build.\n"
				+ "Only files inside the `public` folder can be referenced from the HTML.\n"
				+ "Unlike \"favicon.ico\" or \"favico.ico\", \"%PUBLIC_URL%/favicon.ico\" will\n"
				+ "work correctly both with client-side routing and a non-root public URL.\n"
				+ "Learn how to configure a non-root public URL by running `npm run build`.\n" + " -->\n"
				+ "<title>Conduit</title>\n" + "</head>\n" + "<body>\n" + "<div id=\"root\"></div>\n" + " <!--\n"
				+ " This HTML file is a template.\n"
				+ "If you open it directly in the browser, you will see an empty page.\n"
				+ " You can add webfonts, meta tags, or analytics to this file.\n"
				+ " The build step will place the bundled scripts into the <body> tag.\n"
				+ " To begin the development, run `npm start`.\n"
				+ "To create a production bundle, use `npm run build`.\n" + "-->\n" + "</body>\n" + "</html>\n");
		bw.close();
	}

	/* create default js file in folder structure */
	public static void createDefaultJs() throws IOException {
		File fileRouteJs = new File(outputDirJs, "route.js");
		File fileIndexJs = new File(outDirJs, "index.js");
		File fileStoreJs = new File(outputDirStoreJs, "configureStore.js");
		File fileContainerJs = new File(outputDirContainerJs, "App.js");
		File fileReducerJs = new File(outputReducerJs, "index.js");

		File bufferActionTELinkJs = new File(outputActionJs, "action.js");

		File fileApiJs = new File(outApiJs, "fetchComponentDataBeforeRender.js");
		File fileApiJ = new File(outApiJ, "promiseMiddleware.js");

		BufferedWriter bufferIndexJs = new BufferedWriter(new FileWriter(fileIndexJs));
		bufferIndexJs.write("import \'babel-core/register\';\n" + "import ReactDOM from \'react-dom\';\n"
				+ "import React from \'react\';\n" + "import { Router } from \'react-router\';\n"
				+ "import { Provider } from \'react-redux\';\n" + "import { ReduxRouter } from \'redux-router\';\n"
				+ "import createBrowserHistory from \'history/lib/createBrowserHistory\'\n"
				+ "import configureStore from \'../common/store/configureStore\';\n"
				+ "import routes from \'../common/routes\';\n" + "import \"../../styles/index.css\";\n"
				+ "const history = createBrowserHistory();\n" + "const initialState = window.__INITIAL_STATE__;\n"
				+ "const store = configureStore(initialState);\n"
				+ "const rootElement = document.getElementById(\'root\');\n" + "ReactDOM.render(\n"
				+ "<Provider store={store}>\n" + "<ReduxRouter>\n" + "<Router children={routes} history={history} />\n"
				+ "</ReduxRouter>\n" + "</Provider>,\n" + "document.getElementById(\'root\')\n" + ");\n");
		bufferIndexJs.close();

		BufferedWriter bufferRouteJs = new BufferedWriter(new FileWriter(fileRouteJs));
		bufferRouteJs.write("import { Route } from \"react-router\";\n" + "import React from \"react\";\n"
				+ "import App from \"./containers/App\";\n" + "//Redux Smart\n"
				+ "import LoginPage from \"./containers/LoginPage\";\n"
				+ "/*import RegisterPage from \"./containers/RegisterPage\";*/\n"
				+ "import Dashboard from \'./components/personal/Dashboard\';\n" + "//Redux Dumb\n"
				+ "import HomePage from \"./components/Home\";\n" + "import AboutPage from \"./components/About\";\n"
				+ "import RegisterPage from \"./components/Register\";\n"
				+ "import error404 from \"./components/404\";\n" + "import cookie from \'react-cookie\';\n"
				+ "export default (\n" + "<Route name=\"app\" path=\"/\" component={App}>\n"
				+ "<Route path=\"home\" component={HomePage} />\n" + "<Route path=\"about\" component={AboutPage} />\n"
				+ "<Route path=\"register\" component={RegisterPage} />\n"
				+ "<Route path=\"login\" component={LoginPage} />\n" + "<Route path=\"*\" component={error404}/>\n"
				+ "</Route>\n" + ");\n");
		bufferRouteJs.close();

		BufferedWriter bufferStore = new BufferedWriter(new FileWriter(fileStoreJs));
		bufferStore.write("import { createStore, applyMiddleware, compose } from \'redux\';\n"
				+ "import { reduxReactRouter } from \'redux-router\'\n;" + "import thunk from \'redux-thunk\';\n"
				+ "import createHistory from \'history/lib/createBrowserHistory\';\n"
				+ "import createLogger from \'redux-logger\';\n"
				+ "import promiseMiddleware from \'../api/promiseMiddleware\';"
				+ "import rootReducer from \'../reducers\';\n" + "const middlewareBuilder = () => {\n"
				+ "let middleware = {};\n" + "let universalMiddleware = [promiseMiddleware];\n"
				+ "let allComposeElements = [];\n" + "if(process.browser){\n"
				+ "if(process.env.NODE_ENV === 'production' || process.env.NODE_ENV === 'test\'){\n"
				+ "middleware = applyMiddleware(...universalMiddleware);\n" + " allComposeElements = [\n"
				+ "middleware,\n" + "reduxReactRouter({" + "createHistory" + " })" + "]" + " }else{"
				+ " middleware = applyMiddleware(...universalMiddleware,createLogger());" + " allComposeElements = ["
				+ "  middleware," + "  reduxReactRouter({" + "createHistory" + "})," + "]" + "  }" + "}else{"
				+ " middleware = applyMiddleware(...universalMiddleware);" + "  allComposeElements = [" + "middleware"
				+ " ]" + " }" + " return allComposeElements;" + "}"
				+ "const finalCreateStore = compose(...middlewareBuilder())(createStore);"
				+ "export default function configureStore(initialState) {"
				+ " const store = finalCreateStore(rootReducer, initialState);"
				+ "//const store = finalCreateStore(rootReducer, initialState, applyMiddleware());"
				+ "store.subscribe(() => {" + "console.log(\"store changed\", store.getState());"
				+ "// load our view part using subscriber// store.subscriber();" + "})" + "if (module.hot) {"
				+ " module.hot.accept('../reducers', () => {" + "const nextRootReducer = require('../reducers');"
				+ " store.replaceReducer(nextRootReducer);\n" + "});" + "}" + "  return store;" + "}");
		bufferStore.close();

		BufferedWriter bufferContainerJs = new BufferedWriter(new FileWriter(fileContainerJs));
		bufferContainerJs.write("import React, { Component, PropTypes } from \'react\';\n"
				+ "import { bindActionCreators } from \'redux\';\n" + "import request from \'axios\';\n"
				+ "import { Link } from \'react-router\';\n" + "import classNames from \'classnames\';\n"
				+ "import * as LayoutActions from \'../actions/layout\';\n"
				+ "import * as RegisterActions from \'../actions/register\';\n"
				+ "import Helmet from \'react-helmet\';\n" + "import Home from \'../components/Home\'\n"
				+ "import Header from \'../components/layout/Header\'\n"
				+ "import Paper from \'material-ui/lib/paper\';\n" + "import cookie from \'react-cookie\';\n"
				+ "class App extends Component {\n" + "constructor(props){\n" + "super(props);\n" + "}\n"
				+ "render() {\n" + "return (\n" + "<div>\n" + "<Header/>\n"
				+ "<div style={{paddingLeft: 256, paddingTop: 0, margin: \'48px 72px\'}}>\n"
				+ "<Paper style={{padding:20}}>\n" + "{!this.props.children && <Home />}\n" + "{this.props.children}\n"
				+ "</Paper>\n" + "</div>\n" + "</div>\n" + ");\n" + "}\n" + "}\n");
		bufferContainerJs.close();

		BufferedWriter bufferReducerJs = new BufferedWriter(new FileWriter(fileReducerJs));
		bufferReducerJs.write("import { combineReducers } from \'redux\';\n"
				+ "import { routerStateReducer } from \'redux-router\';\n" + "import undoable from \'redux-undo\';\n"
				+ "const rootReducer = combineReducers({\n" + "router : routerStateReducer\n" + "});\n"
				+ "export default rootReducer;\n");
		bufferReducerJs.close();

		BufferedWriter bufferAction = new BufferedWriter(new FileWriter(bufferActionTELinkJs));
		bufferAction.write("import request from \'axios\';\n\n\n" + "export const HOME='HOME';\n"
				+ "export const HOMEGET='HOMEGET';\n" + "export const HOME_REQUEST='HOME_REQUEST';\n"
				+ "export const HOME_SUCCESS='HOME_SUCCESS';\n" + "export const HOME_FAILURE='HOME_FAILURE';\n"
				+ "export function homedetailsfetch(data)\n" + "{ let formData = data;\n"
				+ "console.log(\"filter form data\",data);\n " + "return { \n" + "type:HOME,\n"
				+ "promise:request.post('<>',formData.toString())\n" + "};\n" + "}");
		bufferAction.close();

		BufferedWriter bufferApiJs = new BufferedWriter(new FileWriter(fileApiJs));
		bufferApiJs.write(
				"/**\n" + "* This looks at static needs parameter in components and waits for the promise to be fullfilled\n"
						+ "* It is used to make sure server side rendered pages wait for APIs to resolve before returning res.end()\n"
						+ "*/\n" + "export function fetchComponentDataBeforeRender(dispatch, components, params) {\n"
						+ "const needs = components.reduce( (prev, current) => {\n" + "return (current.need || [])\n"
						+ ".concat((current.WrappedComponent ? current.WrappedComponent.need : []) || [])\n"
						+ ".concat(prev);\n" + "},[]);\n" + "const promises = needs.map(need => dispatch(need()));\n"
						+ "return Promise.all(promises);\n" + "}\n");
		bufferApiJs.close();

		/*
		 * if (!fileApiJ.exists()) { fileApiJ.createNewFile(); }
		 */
		BufferedWriter bufferApis = new BufferedWriter(new FileWriter(fileApiJ));
		bufferApis.write("export default function promiseMiddleware() {\n" + "return next => action => {\n"
				+ "const { promise, type, ...rest } = action;\n" + "if (!promise) return next(action);\n"
				+ "const SUCCESS = type + \'_SUCCESS\';\n" + "const REQUEST = type + \'_REQUEST\';\n"
				+ "const FAILURE = type + \'_FAILURE\';\n" + "next({ ...rest, type: REQUEST });\n" + "return promise\n"
				+ ".then(req => {\n" + "next({ ...rest, req, type: SUCCESS });\n" + "return true;\n" + "})\n"
				+ ".catch(error => {\n" + "next({ ...rest, error, type: FAILURE });\n" + "console.log(error);\n"
				+ "return false;\n" + "});\n" + "};\n" + "}\n");
		bufferApis.close();

	}

	/* create default css file in folder structure */
	public static void createDefaultCss() throws IOException {
		File fileHtml = new File(outputDirCss, "index.css");
		if (!fileHtml.exists()) {
			fileHtml.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileHtml));
		bw.write(".re_mainDiv{\n" + "height:auto;\n" + "width:80%;\n" + "border:1px solid black;\n"
				+ "background-color:#D8D8D8;\n" + "position:absolute;\n" + "padding:10px;\n" + "}\n" + ".re_textbox{\n"
				+ "width:200px;\n" + "height:25px;\n" + "margin-top:5px;\n" + "}\n" + ".re_selectBox{\n"
				+ "width:200px;\n" + "height:25px;\n" + "margin-top:5px;\n" + "}\n" + ".re_button{\n" + "height:30px;\n"
				+ "background-color:white;\n" + "}\n" + ".rounded{\n" + "border-radius:.25rem;\n" + "}\n"
				+ ".re_textArea{\n" + "     width:200px;\n" + "     margin-top:5px;\n" + "}\n");
		bw.close();
	}

	public static void getComponents() {
		Map<String, Integer> mapComponents = rf.getComponntSize();
		/*
		 * for (Entry<String, Integer> entryMap : mapComponents.entrySet()) {
		 * System.out.println("Map Components key ::: " + entryMap.getKey() +
		 * ", Map Components Value ::: " + entryMap.getValue()); }
		 */
	}

	public static void placeHolderAction() throws IOException {

		File fileReducer = null;
		BufferedReader readAction = null;

		String Path = outputDirHtml;
		String output = outputActionJs + "\\";

		File dirName = new File(Path);

		File listFiles[] = dirName.listFiles();

		for (File filesName : listFiles) {
			String name = filesName.getName();
			if (name.contains(".js")) {
				String fileNames = name.substring(0, name.indexOf("."));

				fileReducer = new File(output + fileNames + "_action.js");
				fileReducer.createNewFile();

				InputStream is = StartParser.class.getResourceAsStream("/placeHolder/placeHolderActionReducer.txt");
				readAction = new BufferedReader(new InputStreamReader(is));


				String line = "";
				String CompleteLine = "";

				while ((line = readAction.readLine()) != null) {
					CompleteLine = CompleteLine + line;
					CompleteLine = CompleteLine + "\n";
				}


				is.close();
				CompleteLine = CompleteLine.replaceAll("@FileNameMarker", fileNames);

				OutputStream out = new FileOutputStream(fileReducer);
				out.write(CompleteLine.getBytes());
				out.close();
			}
		}
	}

	public static void placeHolderReducer() throws IOException {

		File fileAction = null;
		BufferedReader readAction = null;

		String Path = outputDirHtml;
		String output = outputReducerJs + "\\";

		File dirName = new File(Path);

		File listFiles[] = dirName.listFiles();

		for (File filesName : listFiles) {
			String name = filesName.getName();
			if (name.contains(".js")) {
				// fileContent.add(filesName);
				String fileNames = name.substring(0, name.indexOf("."));

				fileAction = new File(output + fileNames + "_reducer.js");
				fileAction.createNewFile();

				InputStream is = StartParser.class.getResourceAsStream("/placeHolder/placeHolderActionReducer.txt");
				readAction = new BufferedReader(new InputStreamReader(is));

				String line = "";
				String CompleteLine = "";

				while ((line = readAction.readLine()) != null) {
					CompleteLine = CompleteLine + line;
					CompleteLine = CompleteLine + "\n";
				}
				// System.out.println(CompleteLine);
				is.close();

				CompleteLine = CompleteLine.replaceAll("@FileNameMarker", fileNames);
				System.out.println(CompleteLine);

				OutputStream out = new FileOutputStream(fileAction);
				out.write(CompleteLine.getBytes());
				out.close();
			}
		}

	}

	/*
	 * public static void getComponents(){ Map<String,Integer>
	 * mapComponents=rf.getComponntSize(); for(Entry<String, Integer> entryMap :
	 * mapComponents.entrySet()){
	 * System.out.println("Map Components key ::: "+entryMap.getKey()
	 * +", Map Components Value ::: "+entryMap.getValue()); } }
	 */
}
