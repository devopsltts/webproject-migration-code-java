package com.lnt.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lnt.utility.PropertyToolUtil;

import jd.core.Decompiler;
import jd.core.DecompilerException;

public class ClassDependencyResolver4 {

	static Map<String, String> srcfileMap = new LinkedHashMap<>();

	static String PATH ; 
	
	static String projectPath=PropertyToolUtil.getProperty("projectFolderPath");
	
	static String libPath=PropertyToolUtil.getProperty("libFolderPath");
	
	static String deplibPath=PropertyToolUtil.getProperty("deplibPath");//Added for Dependency libs resolver
	
	static List<String> list = new LinkedList<>();
	
	static Map<String, String> swingClassMap = new LinkedHashMap<>();
	
	static List<String> libFileList = new ArrayList<String>();
	
	static Set<String> swingComponentValues;
	
	static {
		loadClassFileList();
		
		File[] files = new File(projectPath).listFiles();
		createFilemapFromSrc(files);
		
		swingComponentValues = new LinkedHashSet<>();
		
		swingComponentValues.add("JButton");
		swingComponentValues.add("JTextField");
		swingComponentValues.add("JTextArea");
		swingComponentValues.add("JPanel");
		swingComponentValues.add("JFrame");
		swingComponentValues.add("JCheckBox");
		swingComponentValues.add("JComboBox");
		swingComponentValues.add("JDialog");
		swingComponentValues.add("JFileChooser");
		swingComponentValues.add("JLabel");
		swingComponentValues.add("JTable");
		swingComponentValues.add("JMenu");
		swingComponentValues.add("JMenuBar");
		swingComponentValues.add("JMenuItem");
		swingComponentValues.add("JOptionPane");
		swingComponentValues.add("JRadioButton");
		swingComponentValues.add("JPasswordField");
		swingComponentValues.add("JDialog");
		swingComponentValues.add("JSplitPane");
		swingComponentValues.add("JScrollBar");
		swingComponentValues.add("JProgressBar");
	}
	
	public static Set<String> getSwingComponentValues() {
		return swingComponentValues;
	}

	public static void main(String[] args) {

		/*Map<String, String> swingCompMap = getSwingComponentsSet(PropertyToolUtil.getProperty("serversrcPath"));

		System.out.println(swingCompMap);*/
		decompiledJarFiles();
		

	}

	public static Map<String, String> getSwingComponentsSet(String path) {

		CompilationUnit cu = getAstCompilationUnit(path);

		if (cu.getAST().hasBindingsRecovery()) {
		}

		Set<String> classSet = new LinkedHashSet<>();
		
		Set<String> importSet = new LinkedHashSet<>();

		cu.accept(new ASTVisitor() {

			public boolean visit(ImportDeclaration node) {
				importSet.add(node.getName().toString());
				return true;
			}
			
			public boolean visit(VariableDeclarationFragment node) {

				IVariableBinding binding = node.resolveBinding();

				if (!(binding.getVariableDeclaration().getType().getName().startsWith("String"))
						&& !(binding.getVariableDeclaration().getType().getName().startsWith("char"))
						&& !(binding.getVariableDeclaration().getType().getName().startsWith("int"))) {
					
					String name = binding.getVariableDeclaration().getType().getName();
					
					if(srcfileMap.containsKey(name+".java")) {
						String value = srcfileMap.get(name+".java");
						System.out.println("value ::"+value);
						value = value.substring(value.indexOf("com"), value.lastIndexOf("."));	
						classSet.add(value.replace("\\", "."));
					}
					
					for(String importName : importSet) {
						String truncateName = importName.substring(importName.lastIndexOf(".")+1);
						if(truncateName.equalsIgnoreCase(name) && !importName.contains("javax.") && !importName.contains("java.")){
							
							classSet.add(importName);
						}
					}
					
				}
				return true;
			}
			
			

		});

	//	System.out.println(classSet);
		//System.out.println(importSet);
		
		//System.exit(0);

		Iterator<String> classIterater = classSet.iterator();
		
		/** Iterating on the each component from SET. **/
		while (classIterater.hasNext()) {
			String qualifiedSearchClass = classIterater.next();
			
			String searchClass = qualifiedSearchClass.substring(qualifiedSearchClass.lastIndexOf(".")+1);
			
			
			if(srcfileMap.containsKey(searchClass.trim()+".java") && !swingClassMap.containsKey(searchClass)) {
				//System.out.println("Search Class is in a Src Directory:"+ searchClass.trim()+".java");
				String value = isSwingComp(searchClass);
				if(null != value) {
					swingClassMap.put(searchClass, value);
				}
			} else if(!swingClassMap.containsKey(searchClass)){
				//System.out.println("Search Class is not in a Src Directory:");
				String value = isSwingComp(qualifiedSearchClass);
				if(null != value) {
					swingClassMap.put(searchClass, value);
				}
			}
		}

		return swingClassMap;

	}
	
	/**  Recursive Method which will check in SRC, if not found check in Jar  **/
	public static String isSwingComp(String searchClass) {

		String classSrcPath = null;
		
		String truncatedSearchClass = searchClass.substring(searchClass.lastIndexOf(".")+1);
		
		classSrcPath = srcfileMap.get(truncatedSearchClass.trim()+".java");
		//If classpath is not null then it is in SRC directory
		if(null != classSrcPath) {
			String qname = getSrcQualifiedName(classSrcPath.replace("\\", "\\\\"));
//			System.out.println("=== qname from SRC :"+ qname);
			if(null != qname) {
				String type = qname.substring(qname.lastIndexOf(".")+1);
				
				if(swingComponentValues.contains(type)) {
					return type;
				}else {
					return isSwingComp(qname);
				}
			}else {
				return qname;
			}
			
		} else {
			String qname = resolveJarDependency(searchClass);
//			System.out.println("=== qname from Lib :"+ qname);
			if(null != qname) {
				String type = qname.substring(qname.lastIndexOf(".")+1);
				if(swingComponentValues.contains(type)) {
					return type;
				}else {
					return isSwingComp(qname);
				}
			}else {
				return qname;
			}
		}
	}
	
	public static String getSrcQualifiedName(String classSrcPath) {
		File file = new File(classSrcPath);
		StringBuffer sb = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line+"\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return getQualifiedName(sb.toString());
	}
	
	public static String resolveJarDependency(String searchClass) {
		Set<String> jarSet = getJarfile(searchClass.substring(searchClass.lastIndexOf(".")+1));
		
		String qname = null;
//		System.out.println("==== jar set :"+ jarSet);
		if(null != jarSet && !jarSet.isEmpty()) {
			
			for(String jarName : jarSet) {
				try {
					Map<String, String> map = new Decompiler().decompile(jarName);
					for(Entry<String, String> entry : map.entrySet()) {
						String key = entry.getKey();
						if(key.equalsIgnoreCase(searchClass.trim().replace(".", "/")+".java")) {
							String value = map.get(key);
							if(null != value) {
								qname = getQualifiedName(value);
							}
						}
					}
				}catch (DecompilerException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		return qname;
	}
	
	public static String getQualifiedName(String value) {
		String qname = null;
		String subStr = value.substring(0, value.indexOf("{"));
		String firstLine = subStr.substring(subStr.indexOf("public"));

		String type = getExtendsType(firstLine);

		if(null != type) {
			subStr = subStr.replace("import", "");
			String importSplit[] = subStr.split(";");
			
			for(String str : importSplit) {
				if(str.substring(str.lastIndexOf(".")+1).equalsIgnoreCase(type)) {
					qname = str;
					break;
				}
			}
			
			if(null == qname) {
				for(String str : importSplit) {
					if(str.contains("package")) {
						str = str.replace("package", "");
						str = str.concat("."+type);						
						qname = str.trim();
					}
				}
			}
		}
		return qname;
	}
	
	public static String getExtendsType(String line) {
		String type = null;
		line = line.replace("public class", "");
		line = line.substring(0, line.length());
		if (null != line && line.contains("extends")) {
			String[] classSplit = line.split("extends");
			
			if( classSplit[1].trim().contains("implements")) {
				String[] impSplit = classSplit[1].trim().split("implements");
				type = impSplit[0].trim();
			}else {
				type = classSplit[1].trim();
			}
			
		}
		
		return type;
	}
	
	public static Set<String> getJarfile(String className) {
		Set<String> jarSet = new LinkedHashSet<>();
		try {
			
			for (String jarFilePath : libFileList) {
			//	System.out.println(jarFileName);
				File file = new File(jarFilePath);
				FileInputStream fis = new FileInputStream(file);
				JarInputStream jis = new JarInputStream(fis);
				JarEntry je;
				while ((je = jis.getNextJarEntry()) != null) {
					if (je.getName().contains(className)) {
						jarSet.add(jarFilePath);
					}
				}
				jis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jarSet;
	}
	
	public static void loadClassFileList() {
		File[] files = new File(libPath).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				libFileList.add(file.getPath());
			}
		}
	}

	public static CompilationUnit getAstCompilationUnit(String path) {
		char[] contents = getContentsFromFile(path);

		return createParser(contents);
	}
	
	public static CompilationUnit createParser(char[] contents) {
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		parser.setBindingsRecovery(true);

		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);

		String unitName = "";
		parser.setUnitName(unitName);

		String[] sources = { "" };
		String[] classpath = { "" };

		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);

		parser.setSource(contents);

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		return cu;
	}

	public static char[] getContentsFromFile(String path) {
		
//		System.out.println("PATH !!!!!"+path);

		File file = new File(path);

		char[] contents = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				// append the content and the lost new line.
				sb.append(line + "\n");
			}
			contents = new char[sb.length()];
			sb.getChars(0, sb.length() - 1, contents, 0);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return contents;
	}

	public static void createFilemapFromSrc(File[] files) {
		for (File javafile : files) {
			if (javafile.isDirectory()) {
				createFilemapFromSrc(javafile.listFiles()); // Calls same method again.
			} else {
				srcfileMap.put(javafile.getName(), javafile.getPath());
			}
		}
	}
	
	//Decompilation of Jar files 
	public static void decompiledJarFiles()
	{
		//String path=deplibPath;
		File file = new File(deplibPath);
		if(!file.exists())
		{
			file.mkdir();
		}
		
		String outputpath=deplibPath+"\\";
		for(String jarname:libFileList)
		{
			String jarPath=(outputpath+jarname.substring(jarname.lastIndexOf("\\")+1).replace(".jar", " ")).trim();
			System.out.println("jar Path:"+jarPath);
			File file1=new File(jarPath.trim());
			System.out.println(file1.getAbsolutePath());
			if(!file1.exists())
			{
				file1.mkdir();
				try {
					int i=new Decompiler().decompile(jarname, jarPath);
					System.out.println("decomipled jars : "+i);
				} catch (DecompilerException | IOException e) {
					e.printStackTrace();
				}
			}
			
			
		}
	}

}
