package com.sun.xml.internal.ws.api.databinding;

public enum SoapBodyStyle
{
  DocumentBare,  DocumentWrapper,  RpcLiteral,  RpcEncoded,  Unspecificed;
  
  private SoapBodyStyle() {}
  
  public boolean isDocument()
  {
    return (equals(DocumentBare)) || (equals(DocumentWrapper));
  }
  
  public boolean isRpc()
  {
    return (equals(RpcLiteral)) || (equals(RpcEncoded));
  }
  
  public boolean isLiteral()
  {
    return (equals(RpcLiteral)) || (isDocument());
  }
  
  public boolean isBare()
  {
    return equals(DocumentBare);
  }
  
  public boolean isDocumentWrapper()
  {
    return equals(DocumentWrapper);
  }
}
