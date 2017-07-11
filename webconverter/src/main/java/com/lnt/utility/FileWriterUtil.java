/**
 * 
 */
package com.lnt.utility;

import java.io.*;
import java.util.*;

/**
 * @author Sabyasachi Mohapatra
 *
 */
public class FileWriterUtil {

	public static void writeJs(String dir, String jsString) {
		// TODO Auto-generated method stub
		


		FileWriter fw = null;

		try {

			File file = new File(dir);
			String fileContent = "";

			// if file exists already, then get the content
			if (file.exists()) {
				System.out.println("file already present - " + dir);
				Scanner s = new Scanner(file).useDelimiter("\\Z");
				fileContent = s.next();
			}
			
			fileContent=fileContent.replace("@ReplaceJsContents", jsString);
			fw = new FileWriter(dir);
			fw.flush();
			fw.write(fileContent);
		} catch (Exception e) {
			System.out.println("Check if the File is correct" + e.getMessage());
		} finally {

			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
