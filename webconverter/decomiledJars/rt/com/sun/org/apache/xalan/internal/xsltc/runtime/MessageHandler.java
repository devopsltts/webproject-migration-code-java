package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.io.PrintStream;

public class MessageHandler
{
  public MessageHandler() {}
  
  public void displayMessage(String paramString)
  {
    System.err.println(paramString);
  }
}
