package com.sun.org.apache.xerces.internal.util;

public final class ShadowedSymbolTable
  extends SymbolTable
{
  protected SymbolTable fSymbolTable;
  
  public ShadowedSymbolTable(SymbolTable paramSymbolTable)
  {
    this.fSymbolTable = paramSymbolTable;
  }
  
  public String addSymbol(String paramString)
  {
    if (this.fSymbolTable.containsSymbol(paramString)) {
      return this.fSymbolTable.addSymbol(paramString);
    }
    return super.addSymbol(paramString);
  }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (this.fSymbolTable.containsSymbol(paramArrayOfChar, paramInt1, paramInt2)) {
      return this.fSymbolTable.addSymbol(paramArrayOfChar, paramInt1, paramInt2);
    }
    return super.addSymbol(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public int hash(String paramString)
  {
    return this.fSymbolTable.hash(paramString);
  }
  
  public int hash(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return this.fSymbolTable.hash(paramArrayOfChar, paramInt1, paramInt2);
  }
}
