package com.lnt.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lnt.utility.PropertyToolUtil;

public class MapObject {

	private static int increment = 0;

	private static Set<String> swingComponentValues = null;

	static String PATH;

	static String projectPath = PropertyToolUtil.getProperty("projectFolderPath");
	// actionListnerMathod
	// (give
	// till
	// project
	// directory
	// which
	// we
	// need
	// to
	// convert)

	static int count = 0; // actionListnerMathod

	static ConcurrentHashMap<String, String> tempListenerMap = new ConcurrentHashMap<>(); // actionListnerMathod

	static ArrayList<String> listOfMethodsTemp = new ArrayList<String>();

	public static void main(String[] args) {

		// Map<String, String> finalMap = getCompMap(PATH);

		// System.out.println("==== final map **** :"+ finalMap);

		// getActionListener("D:\\NewWorkspace\\feereportproject\\feereport\\src\\com\\javatpoint\\feereport\\AdminSection.java");

		// getActionListener("D:\\NewWorkspace\\feereportproject\\feereport\\src\\com\\javatpoint\\feereport\\FeeReport.java");

	}

	public static Map<String, String> getCompMap(String path) {
		PATH = path;

		swingComponentValues = ClassDependencyResolver4.getSwingComponentValues();

		Map<String, String> swingCompmap = ClassDependencyResolver4.getSwingComponentsSet(path);

		// System.out.println("==== swingCompmap === :"+ swingCompmap);

		File file = new File(path);
		char[] contents = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = IFeeConstants.BLANK;
			while ((line = br.readLine()) != null) {
				sb.append(line + IFeeConstants.END_LINE);
			}
			contents = new char[sb.length()];
			sb.getChars(0, sb.length() - 1, contents, 0);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

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

		if (cu.getAST().hasBindingsRecovery()) {
		}
		Multimap<String, String> variablemultimap = ArrayListMultimap.create();
		Multimap<String, String> valuemultimap = ArrayListMultimap.create();
		Multimap<String, String> propertymultimap = ArrayListMultimap.create();
		Map<String, String> finalMap = new HashMap<>();
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(VariableDeclarationFragment node) {
				IVariableBinding binding = node.resolveBinding();

				if (swingComponentValues.contains(binding.getVariableDeclaration().getType().getName())) {
					variablemultimap.put(binding.getVariableDeclaration().getType().getName(),
							binding.getVariableDeclaration().getName());
				} else if (swingCompmap.containsKey(binding.getVariableDeclaration().getType().getName())) {
					variablemultimap.put(
							binding.getVariableDeclaration().getType().getName() + "=="
									+ swingCompmap.get(binding.getVariableDeclaration().getType().getName()),
							binding.getVariableDeclaration().getName());
				}
				return true;
			}

			@Override
			public boolean visit(ClassInstanceCreation node) {
				String str = node.toString();
				String doublecheck = str;
				Pattern p = Pattern.compile("\\(|\\)");
				Matcher m = p.matcher(str);
				int ount = 0;
				while (m.find()) {
					++ount;
				}
				// System.out.println(ount);
				if (ount > 2) {
					str = str.substring(0, str.indexOf("("));
					str = str + "(du.m,y)";
				}
				String[] splitString = str.split("new\\s");
				// System.out.println("&&&&&&&&&& :"+ splitString[1]);
				if (swingComponentValues.contains(splitString[1].split("\\(")[0].trim())) {
					String str2 = splitString[1];
					String[] splitString2 = str2.split("\\(");
					// System.out.println("splitString2....>>>>>>>:"+Arrays.toString(splitString2)+"<<<<<<<<.......");
					String value = "";
					String val = "";
					if (splitString2[1] != null && !splitString2[1].trim().isEmpty()) {
						int length = splitString2[1].length();
						value = splitString2[1].substring(0, length - 1);
						val = value;
					} else {
						value = "";
					}
					if (value.length() > 2 && value.charAt(0) == '"') {
						value = value.substring(1, value.length() - 1);
					} else if (value.contains("(") || value.contains(")") || value.contains(".") || value.contains(","))
						value = "dummy:" + (++increment);
					else if (!value.isEmpty()) {
						value = "dummy:" + (++increment);
					}
					valuemultimap.put(splitString2[0], value);
				} else {
					String str2 = splitString[1];
					String[] splitString2 = str2.split("\\(");
					int length = splitString2[1].length();
					if (swingCompmap.containsKey(splitString2[0].trim())) {
						String value = "";
						if (splitString2[1] != null && (!splitString2[1].trim().isEmpty())) {
							value = splitString2[1].substring(0, length - 1);
						} else {
							value = "";
						}
						if (value.length() > 2 && value.charAt(0) == '"') {
							value = value.substring(1, value.length() - 1);
						} else if (value.contains("(") || value.contains(")") || value.contains(".")
								|| value.contains(","))
							value = "dummy:" + (++increment);
						else if (!value.isEmpty()) {
							value = "dummy:" + (++increment);
						}
						valuemultimap.put(splitString2[0] + "==" + swingCompmap.get(splitString2[0]), value);
					}

				}
				return super.visit(node);
			}

		});

		// System.out.println("===== variablemultimap :"+ variablemultimap);

		// System.out.println("===== valuemultimap :"+ valuemultimap);

		Set<String> variableKeyset = variablemultimap.keySet();
		Iterator<String> variableIterator = variableKeyset.iterator();
		// Set<String> propertyKeyset = propertymultimap.keySet();

		while (variableIterator.hasNext()) {

			StringBuilder sb1 = new StringBuilder("Variablename(");
			StringBuilder sb2 = new StringBuilder("Variablevalue(");
			// StringBuilder sb3 = new StringBuilder("Properties(");

			String variablekey = variableIterator.next();

			List<String> variableList = (List<String>) variablemultimap.get(variablekey);

			for (String variable : variableList) {
				sb1.append(variable + ",");
			}
			if (!variableList.isEmpty())
				sb1.deleteCharAt(sb1.length() - 1);
			sb1.append(")");
			List<String> valueList = (List<String>) valuemultimap.get(variablekey);

			for (String value : valueList) {
				sb2.append(value + ",");
			}
			if (!valueList.isEmpty())
				sb2.deleteCharAt(sb2.length() - 1);
			sb2.append(")");
			/*
			 * List<String> propertyList = null; for (String variableValue :
			 * variableList) {
			 * 
			 * if (propertyKeyset.contains(variableValue)) { propertyList =
			 * (List<String>) propertymultimap.get(variableValue);
			 * sb3.append("(" + variableValue + ")"); for (String propery :
			 * propertyList) { sb3.append(propery + ","); }
			 * sb3.deleteCharAt(sb3.length() - 1); }
			 * 
			 * } sb3.append(")");
			 */

			finalMap.put(variablekey, sb1.toString() + "$" + sb2.toString());

			// System.out.println(variablekey+" "+ sb1.toString()+ " "+
			// sb2.toString()+ " "+ sb3.toString());

		}
		return finalMap;
	}

	/*
	 * public static HashMap<String, String> getActionListener(String path) {
	 * String fileName=path; String val = ""; String key = ""; String value =
	 * ""; String readLine=""; HashMap<String, String> results1 = new
	 * HashMap<String,String>();
	 * 
	 * boolean nextLine = false; HashMap<String, String>results=new
	 * HashMap<String,String>();
	 * 
	 * 
	 * try {
	 * 
	 * File f = new File(fileName); BufferedReader b = new BufferedReader(new
	 * FileReader(f)); //String readLine = ""; while ((readLine = b.readLine())
	 * != null) { readLine = readLine.trim(); readLine = " " + readLine;
	 * 
	 * if(readLine.contains("addItemListner".trim())||readLine.contains(
	 * "addActionListener".trim())) {
	 * key=readLine.substring(0,readLine.indexOf(".add")); nextLine=true;} else
	 * if (nextLine==true) { value=value+readLine; }
	 * 
	 * else if(readLine.contains("});")){ if(!key.isEmpty())
	 * value=value.replaceAll("\\s+", "").trim(); results.put(key.trim(),value);
	 * nextLine=false; } } val=val+readLine+"";
	 * 
	 * 
	 * Map<String, String> methodBody = new HashMap<String,String>();
	 * methodBody=getMethodBody(path);
	 * 
	 * for(Entry<String, String> entry1:results.entrySet()){ String
	 * mkey=entry1.getKey(); System.out.println("mkey : "+mkey); String
	 * mvalue=entry1.getValue(); System.out.println("mvalue : "+mvalue);
	 * value=getValueWithreplacedMtdBody(mvalue,path); results1.put(mkey,
	 * value); }
	 * 
	 * for(Entry<String, String> entry1:results1.entrySet()){
	 * System.out.println(
	 * "entry1.getKey()entry1.getKey()entry1.getKey()!!!!!!!!!!!!!!!!!!!!!!!!"+
	 * entry1.getKey()); }
	 * 
	 * }catch (IOException e) { e.printStackTrace(); } return results1;
	 * 
	 * }
	 */

	/*
	 * public static HashMap<String, String> getActionListener(String path) {
	 * String fileName=path; String val = ""; String key = ""; String value =
	 * "";
	 * 
	 * boolean nextLine = false; HashMap<String, String>results=new
	 * HashMap<String,String>();
	 * 
	 * 
	 * try {
	 * 
	 * File f=new File(fileName); BufferedReader br = new BufferedReader(new
	 * FileReader(f)); String readLine=""; while ((readLine = br.readLine()) !=
	 * null) { // append the content and the lost new line. readLine.trim();
	 * 
	 * if(readLine.contains("addItemListener")||readLine.contains(
	 * "addActionListener")) {
	 * key=readLine.substring(0,readLine.indexOf(".add")); nextLine=true; } else
	 * if (nextLine==true) { value=value+readLine; }
	 * 
	 * if(readLine.contains("});")){ if(!key.isEmpty()){
	 * value=value.replaceAll("\\s+", " ").trim();
	 * results.put(key.trim(),value); } nextLine=false; key=""; value=""; }
	 * val=val+readLine+""; } }
	 * 
	 * catch (IOException e) { e.printStackTrace();
	 * 
	 * } Map<String, String> methodBody = new HashMap<String,String>();
	 * methodBody=getMethodNameAndBody(PATH); HashMap<String, String> results1 =
	 * new HashMap<String,String>(); for(Entry<String, String>
	 * entry1:results.entrySet()){ String mkey=entry1.getKey();
	 * System.out.println("mkeymkeymkeymkey"+mkey); String
	 * mvalue=entry1.getValue(); value=getValueWithreplacedMtdBody(mvalue,PATH);
	 * results1.put(mkey, value); } for(Entry<String, String>
	 * entry1:results1.entrySet()){
	 * System.out.println("Get Key ::"+entry1.getKey()+"Get Value ::"+entry1.
	 * getValue()); }
	 * 
	 * return results1; }
	 */

	public static ConcurrentHashMap<String, String> getActionListener(String path) {

		ConcurrentHashMap<String, String> finalMethodListnerMap = new ConcurrentHashMap<>();

		Map<String, String> ASTMap = new HashMap<>();

		ASTMap = getMethodNameAndBody(path);

		// System.out.println("==== ASTMap " + ASTMap);

		HashMap<String, String> results = new HashMap<String, String>();
		try {
			String fileName = path;
			String val = "";
			String key = "";
			String value = "";
			String readLine = "";

			boolean nextLine = false;

			File f = new File(fileName);
			BufferedReader b = new BufferedReader(new FileReader(f));
			// String readLine = "";
			boolean isComment = false;
			while ((readLine = b.readLine()) != null) {
				readLine = readLine.trim();
				readLine = " " + readLine;

				if (readLine.contains("addItemListner".trim()) || readLine.contains("addActionListener".trim())) {
					key = readLine.substring(0, readLine.indexOf(".add"));
					nextLine = true;
				}

				else if (nextLine == true) {

					if (readLine.trim().startsWith("/") || readLine.trim().startsWith("/*")
							|| readLine.trim().startsWith("*"))
						continue;
					value = value + readLine;
				}
				if (readLine.trim().startsWith("});")) {
					if (!key.isEmpty()) {
						value = value.replaceAll("\\s+", " ").trim();
						results.put(key.trim(), value);
					}
					nextLine = false;
					key = "";
					value = "";
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// System.out.println("==== result map " + results);
		finalMethodListnerMap.putAll(results);

		// System.out.println("==== finalMethodListnerMap " +
		// finalMethodListnerMap);

		for (Entry<String, String> entryTemp : finalMethodListnerMap.entrySet()) {
			finalMethodListnerMap.putAll(actionListenerMethods(entryTemp.getValue(), ASTMap));
		}

		finalMethodListnerMap.putAll(multilevelMethodResolution(finalMethodListnerMap, ASTMap));

		// System.out.println("Action listener Map final : : " +
		// finalMethodListnerMap);

		tempListenerMap.clear();

		return finalMethodListnerMap;
	}

	// Muthanna End

	private static ConcurrentHashMap<String, String> multilevelMethodResolution(
			ConcurrentHashMap<String, String> finalMethodListnerMap, Map<String, String> ASTMap) {

		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		count++;
		for (Entry<String, String> entryTemp : finalMethodListnerMap.entrySet()) {
			if (null != entryTemp.getValue()) {
				String body = entryTemp.getValue();
				map.putAll(actionListenerMethods(body, ASTMap));
			}
		}

		if (count < 3) {
			tempListenerMap.putAll(map);
			multilevelMethodResolution(map, ASTMap);
		} else {
			count = 0;
		}

		return tempListenerMap;
	}

	private static ConcurrentHashMap<String, String> actionListenerMethods(String body, Map<String, String> ASTMap) {

		// System.out.println("projectFolderPath :"+projectPath);

		ConcurrentHashMap<String, String> tempListenerMap = new ConcurrentHashMap<>();

		Map<String, String> internalMap = new HashMap<>();

		boolean flag = false;

		if (null != body && body.contains(";")) {
			String[] splitlines = body.split(" ");

			for (String temp : splitlines) {
				temp = temp.trim();
				if (temp.contains(";") && (temp.contains("(") == true)) {
					temp = temp.replace('}', ' ');
					temp = temp.replace('\n', ' ');
					temp = temp.replace(';', ' ');
					temp = temp.trim();
					int index = temp.indexOf('=');
					if (index > 0)
						temp = temp.substring(index + 1);
					temp = temp.trim();
					index = temp.indexOf('(');
					if (index > 0)
						temp = temp.substring(0, index);
					index = temp.indexOf('.');
					if (index > 0)
						temp = temp.substring(index + 1);
					index = temp.indexOf('(');
					if (index > 0)
						temp = temp.substring(0, index);
					temp.trim();

					// System.out.println("=== temp :" + temp);

					// if(flag == false) {
					if (tempListenerMap.containsKey(temp.trim())) {
						// no logic
					} else {
						if (ASTMap.containsKey(temp.trim())) {
							tempListenerMap.put(temp, ASTMap.get(temp));
						} else {
							File filePath = new File(projectPath);
							String searchFileName = DosCmd.searchFileName(temp, filePath);

							if (null != searchFileName) {
								searchFileName = projectPath + searchFileName;
								internalMap = getMethodNameAndBody(searchFileName);
								if (!internalMap.isEmpty() && internalMap.containsKey(temp)) {
									tempListenerMap.put(temp, internalMap.get(temp));
								}
							} else {
								continue;
							}

						}
					}

					// }
				}
			}

		}

		return tempListenerMap;
	}

	public static String getValueWithreplacedMtdBody(String body, Object path) {

		String value = "";

		Scanner scanner = new Scanner(body);
		while (scanner.hasNextLine()) {
			String readLine = scanner.nextLine();
			if (readLine.contains("();")) {

				if (readLine.contains("=")) {
					String[] str = readLine.split("=");
					String str2 = readLine.substring(readLine.indexOf("=") + 1, readLine.indexOf("()"));
					String str1 = replaceWithImpl(str2);

					readLine = str[0] + "=" + str1;
					value = value + " " + readLine;
				} else {

					String str1 = readLine.substring(0, readLine.indexOf("()"));
					str1 = replaceWithImpl(str1.trim());
					value = value + " " + str1;
				}
			} else {
				value = value + readLine;
			}

		}
		return value;

	}

	private static String replaceWithImpl(String stReplacable) {
		Map<String, String> map = new HashMap<>();
		map = getMethodNameAndBody(PATH);
		String replaced = stReplacable;
		if (map.get(stReplacable) != null) {
			replaced = map.get(stReplacable);
			replaced = StringUtils.substringBetween(replaced, "{", "}");
			// System.out.println("replaced : "+replaced);
		}
		return replaced;

	}

	/*
	 * public static Map<String, String> getMethodNameAndBody() {
	 * 
	 * 
	 * }
	 */
	public static String getValueWithReplacedMethodBody(String body, String path) {
		String value = body;
		String str2 = "";

		if (body.contains("(e);")) {
			String[] splits = body.split(" ");
			for (String r : splits) {
				if (r.contains("(e);")) {
					str2 = r.substring(0, r.indexOf("(e)"));
				}
			}
			String str1 = replaceWithImpl(str2.trim(), path);
			String str3 = str2 + "(e);";
			if (str1 != null)
				value = body.replace(str3, str1);
		}

		if (body.contains("();")) {
			String[] splits = body.split("");
			for (String r : splits) {
				if (r.contains("(),")) {
					str2 = r.substring(0, r.indexOf("()"));
				}
			}
			String str1 = replaceWithImpl(str2.trim(), path);
			String str3 = str2 + "();";
			if (str1 != null)
				value = body.replace(str3, str1);
		}
		return value;
	}

	private static String replaceWithImpl(String stReplacable, String path) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		map = getMethodNameAndBody(path);
		String replaced = stReplacable;

		if (map.get(stReplacable) != null) {
			replaced = map.get(stReplacable);
			replaced = StringUtils.substringBetween(replaced, "{", "}");
		}

		return replaced;
	}

	private static Map<String, String> getMethodNameAndBody(String PATH) {
		String path = PATH;

		// System.out.println("path::"+path);

		File file = new File(path);
		Map<String, String> methodNameandBodyMap = new HashMap<>();
		// Map<String, String> addListnerMap = new HashMap<>();
		char[] contents = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
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
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

		if (cu.getAST().hasBindingsRecovery()) {
		}

		cu.accept(new ASTVisitor() {
			int count = 1;

			public boolean visit(MethodDeclaration node) {
				if (node != null) {

					String str = null, str1 = null;
					if (node.getBody() != null)
						str = node.getBody().toString();

					if (node.getName() != null)
						str1 = node.getName().toString();
					// System.out.println("Key - " + str1);
					if (methodNameandBodyMap.containsKey(str1)) {
						str1 = str1 + count;
						count++;
					}

					methodNameandBodyMap.put(str1, str);

				}
				return true;
			}

		});
		return methodNameandBodyMap;
	}

	// Fetch List of methods Name

	static ArrayList<String> fetchListOfMethod(String PATH) {

		String path = PATH;

		File file = new File(path);
		Map<String, String> methodNameandBodyMap = new HashMap<>();
		// Map<String, String> addListnerMap = new HashMap<>();
		char[] contents = null;
		BufferedReader br = null;

		// System.out.println("I'm Here"+file.getName());

		try {
			br = new BufferedReader(new FileReader(file));

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
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

		if (cu.getAST().hasBindingsRecovery()) {
		}

		cu.accept(new ASTVisitor() {

			public boolean visit(MethodDeclaration node) {
				return listOfMethodsTemp.add(node.toString());

			}

		});
		return listOfMethodsTemp;

	}

}
