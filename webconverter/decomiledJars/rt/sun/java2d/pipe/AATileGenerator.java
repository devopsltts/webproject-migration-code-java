package sun.java2d.pipe;

public abstract interface AATileGenerator
{
  public abstract int getTileWidth();
  
  public abstract int getTileHeight();
  
  public abstract int getTypicalAlpha();
  
  public abstract void nextTile();
  
  public abstract void getAlpha(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void dispose();
}
