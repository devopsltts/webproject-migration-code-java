package com.lnt.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.io.File;

public class PropertyToolUtil {

	public static final String PROP_FILENAME = "properties"+File.separator+"classDependency.properties";
	public static Properties myAppProps;
	public static FileInputStream input;
	static {
		
		myAppProps = new Properties();
		
		
	        InputStream fin = null;
	        try {
	            fin = ClassLoader.getSystemResourceAsStream("classDependency.properties");
	            myAppProps.load(fin);
	        } catch(IOException e) {
	            e.printStackTrace();
	        } finally {
	            if(fin != null){
	                try {
	                    fin.close();
	                } catch(IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	
		
/*
		try {
//			input = new FileInputStream(PROP_FILENAME);
			input = new FileInputStream(PropertyToolUtil.class
                    .getClassLoader().getResource("classDependency.properties").getFile());
			
			myAppProps = new Properties();
			myAppProps.load(input);
		} catch (IOException e) {
			System.out.println("Check if the property file is present" + e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		}*/
	}

	public static String getProperty(String propKey) {

		return myAppProps == null || myAppProps.getProperty(propKey) == null ? propKey: myAppProps.getProperty(propKey);
	}

	/*
	 * public static void main(String arg[]) { PropertyReaderUtil obj = new
	 * PropertyReaderUtil(); System.out.println(obj.getProperty("String"));
	 * 
	 * }
	 */

	public static Set<Object> getAllPropKey() {

		return myAppProps.keySet();
	}

}
