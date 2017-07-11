package com.lnt.demo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lnt.utility.*;
import com.restgen.util.RoaGenerator;

public class ParserToHtml {

	static File fileForParse;
	static int count = 0;
	static Map<String, Integer> compSize = new HashMap<>();
	static Map<String, Map<String, String>> CsvMap = new HashMap<String, Map<String, String>>();

	public static void parseFileForServer(String serverPath, String inputServerPath) throws IOException {

		/*****************
		 * Start Fetch Method List for Server Side
		 ********************/
		ArrayList<String> listOfMethod = null;

		if (null != inputServerPath) {
			File folder = new File(inputServerPath);

			if (folder.isDirectory()) {

				File[] listOfFiles = folder.listFiles();
				if (listOfFiles.length != 0) {
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {

							listOfMethod = MapObject.fetchListOfMethod(listOfFiles[i].getPath());
						}
					}

					if (null != listOfMethod) {
						/* generation of rest interfaces */
						RoaGenerator.generateRoaInterface(listOfMethod, serverPath);
					}
				}
			}
		}

	}

	public static int parseFile(String path, String outputPath) throws IOException {

		Map<String, String> parserMap = new HashMap<String, String>();
		parserMap = MapObject.getCompMap(path);

		System.out.println("==== map from mapobject :" + parserMap);

		int parsedCounter = 0;
		Map<String, String> comp = new HashMap<String, String>();

		/* Action Listener */
		ConcurrentHashMap<String, String> actionListenerMap = MapObject.getActionListener(path);

		if (!parserMap.isEmpty()) {
			String[] labelFields = null;

			int countComp = 0;

			String[] componentName = null;

			for (Entry<String, String> entry : parserMap.entrySet()) {
				if (entry.getKey().toString().equalsIgnoreCase("JButton")
						|| entry.getKey().toString().equalsIgnoreCase("JTextField")
						|| entry.getKey().toString().equalsIgnoreCase("JTextArea")
						|| entry.getKey().toString().equalsIgnoreCase("JPanel")
						|| entry.getKey().toString().equalsIgnoreCase("JFrame")
						|| entry.getKey().toString().equalsIgnoreCase("JCheckBox")
						|| entry.getKey().toString().equalsIgnoreCase("JComboBox")
						|| entry.getKey().toString().equalsIgnoreCase("JDialog")
						|| entry.getKey().toString().equalsIgnoreCase("JFileChooser")
						|| entry.getKey().toString().equalsIgnoreCase("JLabel")
						|| entry.getKey().toString().equalsIgnoreCase("JTable")
						|| entry.getKey().toString().equalsIgnoreCase("JMenu")
						|| entry.getKey().toString().equalsIgnoreCase("JMenuBar")
						|| entry.getKey().toString().equalsIgnoreCase("JMenuItem")
						|| entry.getKey().toString().equalsIgnoreCase("JOptionPane")
						|| entry.getKey().toString().equalsIgnoreCase("JRadioButton")
						|| entry.getKey().toString().equalsIgnoreCase("JPasswordField")
						|| entry.getKey().toString().equalsIgnoreCase("JDialog")
						|| entry.getKey().toString().equalsIgnoreCase("JSplitPane")
						|| entry.getKey().toString().equalsIgnoreCase("JScrollBar")
						|| entry.getKey().toString().equalsIgnoreCase("JProgressBar")) {
					componentName = entry.getValue().toString().split("Variablename");

					Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(componentName[1]);
					if (m.find()) {
						String value = m.group(1);
						String line1 = value.replace("\"", "");
						labelFields = line1.split(",");

						countComp = countComp + labelFields.length;
						compSize.put(new File(path).getName(), countComp);
						for (int i = 0; i < labelFields.length; i++) {

							comp.put(labelFields[i], entry.getKey().toString());
						}
					}
				}
				CsvMap.put(new File(path).getName(), comp);
			}
		}

		Map<String, String> htmlMap = new LinkedHashMap<String, String>();

		for(Entry<String, String> entryMap : parserMap.entrySet()){
			//System.out.println("key ::: "+entryMap.getKey());  
			String dynamicKey = entryMap.getKey();
			String parserKey="";
			if(dynamicKey.contains("==")){
				String[] splitDynamicKey = dynamicKey.split("\\==");
				parserKey=splitDynamicKey[1];
			}else{
				parserKey=dynamicKey;
			} 
			
			if(parserKey.equalsIgnoreCase("JLabel") || parserKey.equalsIgnoreCase("JTextArea") || parserKey.equalsIgnoreCase("JPanel")
					|| parserKey.equalsIgnoreCase("JCheckBox") || parserKey.equalsIgnoreCase("JRadioButton") || parserKey.contains("JComboBox")
					|| parserKey.equalsIgnoreCase("JPasswordField") || parserKey.equalsIgnoreCase("JButton") || parserKey.equalsIgnoreCase("JTextField")
					|| parserKey.equalsIgnoreCase("JTable") || parserKey.equalsIgnoreCase("JDialog") ||  parserKey.equalsIgnoreCase("JOptionPane")
					|| parserKey.equalsIgnoreCase("JScrollBar") || parserKey.equalsIgnoreCase("JSplitPane") || parserKey.equalsIgnoreCase("JFileChooser")
					|| parserKey.equalsIgnoreCase("JList") || parserKey.equalsIgnoreCase("JProgressBar") || parserKey.equalsIgnoreCase("JMenu")
					|| parserKey.equalsIgnoreCase("JTabbedPane") || parserKey.equalsIgnoreCase("JToggleButton")){

				String totalLabal = entryMap.getValue(); 
				String[] eachLabel = totalLabal.split("\\$");
				for(int i=0;i<eachLabel.length;i++){
					Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(eachLabel[i]); 
					if(eachLabel[i].contains("Variablevalue")){
						if(m.find()) {
							String labelVal = m.group(1);
							String line = labelVal.replace("\"", "");
							if(line.contains(",")){ 
								String[] labelFields = line.split("\\,");  

								if(labelFields.length==0){
									htmlMap.put("NA-"+count,parserKey); 
								}
								for(int j=0;j<labelFields.length;j++){
									//System.out.println("Each Label :::"+labelFields[j]); 
									htmlMap.put(labelFields[j]+"-"+count,parserKey);
								}
							}else{
								htmlMap.put(line+"-"+count,parserKey);
							}
						}else{ 
							htmlMap.put("NA-"+count,parserKey);
						}
					}
					if(eachLabel[i].contains("Variablename")){
						if(m.find()) {
							String labelVal = m.group(1);
							String line = labelVal.replace("\"", "");
							if(line.contains(",")){ 
								String[] labelFields = line.split("\\,");  

								if(labelFields.length==0){
									htmlMap.put("NA-"+count+"#",parserKey); 
								}
								for(int j=0;j<labelFields.length;j++){
									//System.out.println("Each Label :::"+labelFields[j]); 
									htmlMap.put(labelFields[j]+"-"+count+"#",parserKey);
								}
							}else{
								htmlMap.put(line+"-"+count+"#",parserKey);
							}
						}else{ 
							htmlMap.put("NA-"+count+"#",parserKey);
						}
					}
				}
			}
			count++;
		}
		System.out.println("Html map ::: "+htmlMap); 

		HashMap<String, String> reactmapper = new HashMap<String, String>();

		reactmapper.put("JLabel", "<label for =\"@VariableValue\" " + ">@VariableValue</label>");
		reactmapper.put("JTextArea", "<textarea rows =\"4\" cols =\"50\" " + ">@VariableValue</textarea>");
		reactmapper.put("JCheckBox", "<input type =\"checkbox\" name=\"@VariableValue\" "+" onclick=@VariableName(this)>@VariableValue</input>");
		reactmapper.put("JRadioButton", "<input type =\"radio\" name=\"@VariableValue\" "+" onclick=@VariableName(this)>@VariableValue</input>");
		reactmapper.put("JComboBox","<select name =\"@VariableValue\"><option value=\"@VariableValue\">@VariableValue</option></select>");
		reactmapper.put("JPasswordField", "<input type =\"password\" name=\"@VariableValue\" " + "></input>");
		reactmapper.put("JButton","<button type =\"@VariableValue\" onclick= this.@VariableName()>@VariableValue</button>");
		reactmapper.put("JTextField", "<input type =\"text\" name=\"@VariableValue\"></input>");
		reactmapper.put("JTable", "<table border =\"1\"><tr></tr></table>");
		reactmapper.put("JDialog", "<dialog id =\"@VariableValue\"></dialog >");// totally depends upon button
		reactmapper.put("JPanel","<fieldset>" + "<legend>@VariableValue</legend>" + "Dynamic Data.!" + "</fieldset>");
		reactmapper.put("JFileChooser", "<input type =\"file\" name=\"@VariableValue\"  accept=\"image/*\"></input>");
		reactmapper.put("JList","<select name =\"@VariableValue\" multiple><option value=\"@VariableValue\">@VariableValue</option></select>");

		if (htmlMap.size() > 0) {

			String filename = "";
			File dir = null;
			dir = new File(outputPath);

			if (!dir.exists()) {
				dir.mkdir();
			}

			File fileToCreate = new File(path);
			filename = fileToCreate.getName();

			filename = filename.substring(0, filename.indexOf(".java"));
			File tagFile = new File(dir + "\\" + filename + ".js");

			FileWriter fw = null;
			InputStream is = ParserToHtml.class.getResourceAsStream("/placeHolder/PlaceHolder.txt");
			
		//	File fileReact = new File("/placeHolder/PlaceHolder.txt");
			String fileContent = "";
		
			
		//	if (fileReact.exists())
			{
				Scanner sc = new Scanner(is).useDelimiter("\\Z");
				fileContent = sc.next();
			}

			
			
			fileContent = fileContent.replace("@FileNameMarker", filename);
			is.close();
			StringBuffer bufferContent2 = new StringBuffer();

			int i=0;
			int j=0;
			boolean loopflag=false;
			
			Map<String,Object> hs = new LinkedHashMap<String,Object>();
			ArrayList<String> al1  = new ArrayList<String>();

			for(Entry<String,String> htmlMapEntry : htmlMap.entrySet()){
				if(reactmapper.containsKey(htmlMapEntry.getValue())){
					String reactLine = reactmapper.get(htmlMapEntry.getValue());
					//System.out.println("reactLine-->>"+reactLine);
					String varReplacer = htmlMapEntry.getKey();
					System.out.println("varReplacer-->>"+varReplacer);
					String[] temlStr;
					String name="";
					String value="";
				
					
					if(varReplacer.contains("#")){ 
						
						System.out.println("loopflag IS-"+loopflag);
						
						if(loopflag) 
						 al1.clear();
						
						temlStr = varReplacer.split("-");
						name = temlStr.length>0?temlStr[0]:name;
						al1.add(name);
						i=0;
						loopflag=false;
						
						//reactLine=reactLine.replace("@VariableName", name); 
						System.out.println("al1111 ---------------"+al1);
					}else{
						temlStr = varReplacer.split("-");
						value = temlStr.length>0?temlStr[0]:value;
						//al2.add(value);
						System.out.println("Inside --->>> "+al1);
						i++;
						if(reactLine.contains("@ArrayValue")){ 
							j++;
							hs.put("text:", value);
							hs.put("value:", j);
						}
						
						System.out.println("HSSS :"+hs);
						
						reactLine=reactLine.replace("@VariableValue", value).
											replace("@VariableName", al1.size()>=i?al1.get(i-1):"NO Data").
											replace("@ArrayValue", hs.size()>0?hs.toString():"No").
											replace("@LabelValue", value);
						System.out.println("reactLine-->>"+reactLine);
						bufferContent2.append(reactLine+"\n");
						loopflag=true;
					}
				}
			}

			String fullStringBuffer = bufferContent2.toString();
			fileContent = fileContent.replace("@ReactDynamicCode", fullStringBuffer);
			//fileContent = fileContent.replace("@ReactDynamicCode", fullStringBuffer);

			fw = new FileWriter(tagFile);
			fw.flush();
			fw.write(fileContent);
			fw.close(); 
			getJString(path, outputPath, actionListenerMap); 

			if (!parserMap.isEmpty()) {
				parsedCounter++;
			}
			System.out.println("\n ***********  DONE ***************");
		}

		return parsedCounter;
	}

	public static void getJString(String path, String outputPath, ConcurrentHashMap<String, String> actionListnerMap) {
		File dir = new File(outputPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File fileToCreate = new File(path);
		String fileName = fileToCreate.getName();
		fileName = fileName.substring(0, fileName.indexOf(".java"));
		String jsFileContent = "";
		/***** ActionListener To JavaScript File Creation *****/
		for (Entry<String, String> entry : actionListnerMap.entrySet()) {

			String jsString = FindNReplaceJavaToJs.replacerUtil((String) entry.getValue());
			jsString = FindNReplaceJavaToJs.replaceAnyConstOrMessageBox(jsString);
			jsString = FindNReplaceJavaToJs.formatterUtil(jsString);
			jsFileContent = jsFileContent + "\n\n" + entry.getKey() + " = () => {" + jsString + "};}";
		}
		System.out.println("The JsConent to be added is - " + jsFileContent);
		
	
		
		FileWriterUtil.writeJs(dir + "\\" + fileName + ".js", jsFileContent);

	}

	public Map<String, Map<String, String>> getCompMap() {
		return CsvMap;
	}

	public Map<String, Integer> getComponntSize() {
		return compSize;

	}

}
