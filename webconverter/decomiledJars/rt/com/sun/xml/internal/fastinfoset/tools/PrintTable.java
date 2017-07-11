package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import java.io.File;
import java.io.PrintStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class PrintTable
{
  public PrintTable() {}
  
  public static void printVocabulary(ParserVocabulary paramParserVocabulary)
  {
    printArray("Attribute Name Table", paramParserVocabulary.attributeName);
    printArray("Attribute Value Table", paramParserVocabulary.attributeValue);
    printArray("Character Content Chunk Table", paramParserVocabulary.characterContentChunk);
    printArray("Element Name Table", paramParserVocabulary.elementName);
    printArray("Local Name Table", paramParserVocabulary.localName);
    printArray("Namespace Name Table", paramParserVocabulary.namespaceName);
    printArray("Other NCName Table", paramParserVocabulary.otherNCName);
    printArray("Other String Table", paramParserVocabulary.otherString);
    printArray("Other URI Table", paramParserVocabulary.otherURI);
    printArray("Prefix Table", paramParserVocabulary.prefix);
  }
  
  public static void printArray(String paramString, StringArray paramStringArray)
  {
    System.out.println(paramString);
    for (int i = 0; i < paramStringArray.getSize(); i++) {
      System.out.println("" + (i + 1) + ": " + paramStringArray.getArray()[i]);
    }
  }
  
  public static void printArray(String paramString, PrefixArray paramPrefixArray)
  {
    System.out.println(paramString);
    for (int i = 0; i < paramPrefixArray.getSize(); i++) {
      System.out.println("" + (i + 1) + ": " + paramPrefixArray.getArray()[i]);
    }
  }
  
  public static void printArray(String paramString, CharArrayArray paramCharArrayArray)
  {
    System.out.println(paramString);
    for (int i = 0; i < paramCharArrayArray.getSize(); i++) {
      System.out.println("" + (i + 1) + ": " + paramCharArrayArray.getArray()[i]);
    }
  }
  
  public static void printArray(String paramString, ContiguousCharArrayArray paramContiguousCharArrayArray)
  {
    System.out.println(paramString);
    for (int i = 0; i < paramContiguousCharArrayArray.getSize(); i++) {
      System.out.println("" + (i + 1) + ": " + paramContiguousCharArrayArray.getString(i));
    }
  }
  
  public static void printArray(String paramString, QualifiedNameArray paramQualifiedNameArray)
  {
    System.out.println(paramString);
    for (int i = 0; i < paramQualifiedNameArray.getSize(); i++)
    {
      QualifiedName localQualifiedName = paramQualifiedNameArray.getArray()[i];
      System.out.println("" + (localQualifiedName.index + 1) + ": " + "{" + localQualifiedName.namespaceName + "}" + localQualifiedName.prefix + ":" + localQualifiedName.localName);
    }
  }
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
      localSAXParserFactory.setNamespaceAware(true);
      SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
      ParserVocabulary localParserVocabulary = new ParserVocabulary();
      VocabularyGenerator localVocabularyGenerator = new VocabularyGenerator(localParserVocabulary);
      File localFile = new File(paramArrayOfString[0]);
      localSAXParser.parse(localFile, localVocabularyGenerator);
      printVocabulary(localParserVocabulary);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}
