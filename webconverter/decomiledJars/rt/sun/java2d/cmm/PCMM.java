package sun.java2d.cmm;

import java.awt.color.ICC_Profile;

public abstract interface PCMM
{
  public abstract Profile loadProfile(byte[] paramArrayOfByte);
  
  public abstract void freeProfile(Profile paramProfile);
  
  public abstract int getProfileSize(Profile paramProfile);
  
  public abstract void getProfileData(Profile paramProfile, byte[] paramArrayOfByte);
  
  public abstract void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte);
  
  public abstract int getTagSize(Profile paramProfile, int paramInt);
  
  public abstract void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte);
  
  public abstract ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2);
  
  public abstract ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform);
}
