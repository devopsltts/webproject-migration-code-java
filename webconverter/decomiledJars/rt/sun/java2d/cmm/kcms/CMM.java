package sun.java2d.cmm.kcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.awt.color.ProfileDataException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;

public class CMM
  implements PCMM
{
  private static long ID = 0L;
  static final int cmmStatSuccess = 0;
  static final int cmmStatBadProfile = 503;
  static final int cmmStatBadTagData = 504;
  static final int cmmStatBadTagType = 505;
  static final int cmmStatBadTagId = 506;
  static final int cmmStatBadXform = 507;
  static final int cmmStatXformNotActive = 508;
  static final int cmmStatOutOfRange = 518;
  static final int cmmStatTagNotFound = 519;
  private static CMM theKcms = null;
  
  static native int cmmLoadProfile(byte[] paramArrayOfByte, long[] paramArrayOfLong);
  
  static native int cmmFreeProfile(long paramLong);
  
  static native int cmmGetProfileSize(long paramLong, int[] paramArrayOfInt);
  
  static native int cmmGetProfileData(long paramLong, byte[] paramArrayOfByte);
  
  static native int cmmGetTagSize(long paramLong, int paramInt, int[] paramArrayOfInt);
  
  static native int cmmGetTagData(long paramLong, int paramInt, byte[] paramArrayOfByte);
  
  static native int cmmSetTagData(long paramLong, int paramInt, byte[] paramArrayOfByte);
  
  static native int cmmGetTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2, ICC_Transform paramICC_Transform);
  
  static native int cmmCombineTransforms(ICC_Transform[] paramArrayOfICC_Transform, ICC_Transform paramICC_Transform);
  
  static native int cmmFreeTransform(long paramLong);
  
  static native int cmmGetNumComponents(long paramLong, int[] paramArrayOfInt);
  
  static native int cmmColorConvert(long paramLong, CMMImageLayout paramCMMImageLayout1, CMMImageLayout paramCMMImageLayout2);
  
  private CMM() {}
  
  private long getKcmsPtr(Profile paramProfile)
  {
    if ((paramProfile instanceof KcmsProfile)) {
      return ((KcmsProfile)paramProfile).getKcmsPtr();
    }
    throw new CMMException("Invalid profile");
  }
  
  public Profile loadProfile(byte[] paramArrayOfByte)
  {
    long[] arrayOfLong = new long[1];
    checkStatus(cmmLoadProfile(paramArrayOfByte, arrayOfLong));
    if (arrayOfLong[0] != 0L) {
      return new KcmsProfile(arrayOfLong[0]);
    }
    return null;
  }
  
  public void freeProfile(Profile paramProfile)
  {
    checkStatus(cmmFreeProfile(getKcmsPtr(paramProfile)));
  }
  
  public int getProfileSize(Profile paramProfile)
  {
    int[] arrayOfInt = new int[1];
    checkStatus(cmmGetProfileSize(getKcmsPtr(paramProfile), arrayOfInt));
    return arrayOfInt[0];
  }
  
  public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte)
  {
    checkStatus(cmmGetProfileData(getKcmsPtr(paramProfile), paramArrayOfByte));
  }
  
  public int getTagSize(Profile paramProfile, int paramInt)
  {
    int[] arrayOfInt = new int[1];
    checkStatus(cmmGetTagSize(getKcmsPtr(paramProfile), paramInt, arrayOfInt));
    return arrayOfInt[0];
  }
  
  public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
  {
    checkStatus(cmmGetTagData(getKcmsPtr(paramProfile), paramInt, paramArrayOfByte));
  }
  
  public void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte)
  {
    int i = cmmSetTagData(getKcmsPtr(paramProfile), paramInt, paramArrayOfByte);
    switch (i)
    {
    case 504: 
    case 505: 
    case 519: 
      throw new IllegalArgumentException("Can not write tag data.");
    }
    checkStatus(i);
  }
  
  public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2)
  {
    ICC_Transform localICC_Transform = new ICC_Transform();
    checkStatus(cmmGetTransform(paramICC_Profile, paramInt1, paramInt2, localICC_Transform));
    return localICC_Transform;
  }
  
  public ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform)
  {
    ICC_Transform localICC_Transform = new ICC_Transform();
    ICC_Transform[] arrayOfICC_Transform = new ICC_Transform[paramArrayOfColorTransform.length];
    for (int i = 0; i < paramArrayOfColorTransform.length; i++) {
      arrayOfICC_Transform[i] = ((ICC_Transform)paramArrayOfColorTransform[i]);
    }
    i = cmmCombineTransforms(arrayOfICC_Transform, localICC_Transform);
    if ((i != 0) || (localICC_Transform.getID() == 0L)) {
      throw new ProfileDataException("Invalid profile sequence");
    }
    return localICC_Transform;
  }
  
  static native int cmmInit();
  
  static native int cmmTerminate();
  
  static synchronized PCMM getModule()
  {
    if (theKcms != null) {
      return theKcms;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("kcms");
        return null;
      }
    });
    int i = cmmInit();
    checkStatus(i);
    theKcms = new CMM();
    return theKcms;
  }
  
  protected void finalize()
  {
    checkStatus(cmmTerminate());
  }
  
  public static void checkStatus(int paramInt)
  {
    if (paramInt != 0) {
      throw new CMMException(errorString(paramInt));
    }
  }
  
  static String errorString(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return "Success";
    case 519: 
      return "No such tag";
    case 503: 
      return "Invalid profile data";
    case 504: 
      return "Invalid tag data";
    case 505: 
      return "Invalid tag type";
    case 506: 
      return "Invalid tag signature";
    case 507: 
      return "Invlaid transform";
    case 508: 
      return "Transform is not active";
    case 518: 
      return "Invalid image format";
    }
    return "General CMM error" + paramInt;
  }
  
  final class KcmsProfile
    extends Profile
  {
    KcmsProfile(long paramLong)
    {
      super();
    }
    
    long getKcmsPtr()
    {
      return getNativePtr();
    }
  }
}
