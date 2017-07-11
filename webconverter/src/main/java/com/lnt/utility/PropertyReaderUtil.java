/**
 * 
 */
package com.lnt.utility;

import java.io.*;
import java.util.*;

/**
 * @author 20071387
 *
 */
public class PropertyReaderUtil {

//	public static final String PROP_FILENAME = "properties"+File.separator+"myapp.properties";
	public static Properties myAppProps;
	public static FileInputStream input;
	static {

		myAppProps = new Properties();
		
		
        InputStream fin = null;
        try {
            fin = ClassLoader.getSystemResourceAsStream("myapp.properties");
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
		
	/*	try {
	//		input = new FileInputStream(PROP_FILENAME);
			input = new FileInputStream(PropertyReaderUtil.class
                    .getClassLoader().getResource("myapp.properties").getFile());
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

		return myAppProps == null || myAppProps.getProperty(propKey) == null ? propKey
				: myAppProps.getProperty(propKey);
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
