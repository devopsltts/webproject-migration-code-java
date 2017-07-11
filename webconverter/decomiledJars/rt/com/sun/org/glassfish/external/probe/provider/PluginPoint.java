package com.sun.org.glassfish.external.probe.provider;

public enum PluginPoint
{
  SERVER("server", "server"),  APPLICATIONS("applications", "server/applications");
  
  String name;
  String path;
  
  private PluginPoint(String paramString1, String paramString2)
  {
    this.name = paramString1;
    this.path = paramString2;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public String getPath()
  {
    return this.path;
  }
}
