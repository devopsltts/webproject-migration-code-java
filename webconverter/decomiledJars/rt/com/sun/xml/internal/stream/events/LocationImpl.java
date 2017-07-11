package com.sun.xml.internal.stream.events;

import javax.xml.stream.Location;

public class LocationImpl
  implements Location
{
  String systemId;
  String publicId;
  int colNo;
  int lineNo;
  int charOffset;
  
  LocationImpl(Location paramLocation)
  {
    this.systemId = paramLocation.getSystemId();
    this.publicId = paramLocation.getPublicId();
    this.lineNo = paramLocation.getLineNumber();
    this.colNo = paramLocation.getColumnNumber();
    this.charOffset = paramLocation.getCharacterOffset();
  }
  
  public int getCharacterOffset()
  {
    return this.charOffset;
  }
  
  public int getColumnNumber()
  {
    return this.colNo;
  }
  
  public int getLineNumber()
  {
    return this.lineNo;
  }
  
  public String getPublicId()
  {
    return this.publicId;
  }
  
  public String getSystemId()
  {
    return this.systemId;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("Line number = " + getLineNumber());
    localStringBuffer.append("\n");
    localStringBuffer.append("Column number = " + getColumnNumber());
    localStringBuffer.append("\n");
    localStringBuffer.append("System Id = " + getSystemId());
    localStringBuffer.append("\n");
    localStringBuffer.append("Public Id = " + getPublicId());
    localStringBuffer.append("\n");
    localStringBuffer.append("CharacterOffset = " + getCharacterOffset());
    localStringBuffer.append("\n");
    return localStringBuffer.toString();
  }
}
