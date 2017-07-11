/**
 * 
 */
package com.lnt.demo;
import java.io.*;
import java.util.*;

import com.lnt.utility.PropertyReaderUtil;


public class FindNReplaceJavaToJs {
	public static String replacerUtil(String jString){
		jString = (jString.contains("public void")) ? 
				jString.substring(jString.indexOf("{") + 1, jString.length() - 2)
				: jString;
				jString = (jString.contains("@Override")) ? 
						jString.substring(jString.indexOf("{") + 1, jString.length() - 6)
						: jString;
						jString=jString.replaceAll("\\[\\]", "");
						Set<Object> keys = PropertyReaderUtil.getAllPropKey();
						for(Object k:keys){
							String key = (String)k;
							String value=(String)PropertyReaderUtil.getProperty(key);
	//						System.out.println(key+": "+value);
							jString=jString.replaceAll(key, value);
						}
						return jString;
	}
	public static String formatterUtil(String jString){
		jString=jString.replace("//"," ");
		return jString;
	}
	public static boolean matcherUtil(String jString){

		return true;
	}


	public static String replaceAnyConstOrMessageBox(String jsString) {
		// TODO Auto-generated method stub

		StringTokenizer jStk = new StringTokenizer(jsString, ";");
		String jStringNew = "";
		while (jStk.hasMoreTokens()) {
			String jToken = jStk.nextToken().toString().trim();

			jToken = jToken.contains("=new ") ? "const " + jToken.substring(jToken.indexOf(" ")) : jToken;
			jToken=formatAlert(jToken);

			jStringNew = jStringNew + jToken + ";";

		}
		return jStringNew.substring(0, jStringNew.length() - 1);
	}
	private static String formatAlert(String jToken) {
		// TODO Auto-generated method stub
		String[] alertformat=IFeeConstants.ALERT_FORMAT; 
		for(int i=0;i<alertformat.length;i++){
			String alertFormatST=alertformat[i];
			if(jToken.contains(alertFormatST)){
				String jMessage=jToken.substring(jToken.indexOf(alertFormatST));
				jMessage=jMessage.substring(jMessage.indexOf('"')+1);
				jMessage=jMessage.substring(0,jMessage.indexOf('"'));
				jToken=jToken.substring(0, jToken.indexOf(alertFormatST)) + alertFormatST+"("+'"'
						+ jMessage+'"'+')';

			}
		}
		return jToken;
	}

}