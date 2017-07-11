package java.awt.peer;

import java.awt.Dimension;

public abstract interface TextFieldPeer
  extends TextComponentPeer
{
  public abstract void setEchoChar(char paramChar);
  
  public abstract Dimension getPreferredSize(int paramInt);
  
  public abstract Dimension getMinimumSize(int paramInt);
}
