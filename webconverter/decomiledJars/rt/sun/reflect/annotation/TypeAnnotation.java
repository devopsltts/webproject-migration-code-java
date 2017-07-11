package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class TypeAnnotation
{
  private final TypeAnnotationTargetInfo targetInfo;
  private final LocationInfo loc;
  private final Annotation annotation;
  private final AnnotatedElement baseDeclaration;
  
  public TypeAnnotation(TypeAnnotationTargetInfo paramTypeAnnotationTargetInfo, LocationInfo paramLocationInfo, Annotation paramAnnotation, AnnotatedElement paramAnnotatedElement)
  {
    this.targetInfo = paramTypeAnnotationTargetInfo;
    this.loc = paramLocationInfo;
    this.annotation = paramAnnotation;
    this.baseDeclaration = paramAnnotatedElement;
  }
  
  public TypeAnnotationTargetInfo getTargetInfo()
  {
    return this.targetInfo;
  }
  
  public Annotation getAnnotation()
  {
    return this.annotation;
  }
  
  public AnnotatedElement getBaseDeclaration()
  {
    return this.baseDeclaration;
  }
  
  public LocationInfo getLocationInfo()
  {
    return this.loc;
  }
  
  public static List<TypeAnnotation> filter(TypeAnnotation[] paramArrayOfTypeAnnotation, TypeAnnotationTarget paramTypeAnnotationTarget)
  {
    ArrayList localArrayList = new ArrayList(paramArrayOfTypeAnnotation.length);
    for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation) {
      if (localTypeAnnotation.getTargetInfo().getTarget() == paramTypeAnnotationTarget) {
        localArrayList.add(localTypeAnnotation);
      }
    }
    localArrayList.trimToSize();
    return localArrayList;
  }
  
  public String toString()
  {
    return this.annotation.toString() + " with Targetnfo: " + this.targetInfo.toString() + " on base declaration: " + this.baseDeclaration.toString();
  }
  
  public static final class LocationInfo
  {
    private final int depth;
    private final Location[] locations;
    public static final LocationInfo BASE_LOCATION = new LocationInfo();
    
    private LocationInfo()
    {
      this(0, new Location[0]);
    }
    
    private LocationInfo(int paramInt, Location[] paramArrayOfLocation)
    {
      this.depth = paramInt;
      this.locations = paramArrayOfLocation;
    }
    
    public static LocationInfo parseLocationInfo(ByteBuffer paramByteBuffer)
    {
      int i = paramByteBuffer.get() & 0xFF;
      if (i == 0) {
        return BASE_LOCATION;
      }
      Location[] arrayOfLocation = new Location[i];
      for (int j = 0; j < i; j++)
      {
        byte b = paramByteBuffer.get();
        short s = (short)(paramByteBuffer.get() & 0xFF);
        if (b != 0) {
          if ((((b == 1 ? 1 : 0) | (b == 2 ? 1 : 0)) == 0) && (b != 3)) {
            throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
          }
        }
        if ((b != 3) && (s != 0)) {
          throw new AnnotationFormatError("Bad Location encoding in Type Annotation");
        }
        arrayOfLocation[j] = new Location(b, s);
      }
      return new LocationInfo(i, arrayOfLocation);
    }
    
    public LocationInfo pushArray()
    {
      return pushLocation((byte)0, (short)0);
    }
    
    public LocationInfo pushInner()
    {
      return pushLocation((byte)1, (short)0);
    }
    
    public LocationInfo pushWildcard()
    {
      return pushLocation((byte)2, (short)0);
    }
    
    public LocationInfo pushTypeArg(short paramShort)
    {
      return pushLocation((byte)3, paramShort);
    }
    
    public LocationInfo pushLocation(byte paramByte, short paramShort)
    {
      int i = this.depth + 1;
      Location[] arrayOfLocation = new Location[i];
      System.arraycopy(this.locations, 0, arrayOfLocation, 0, this.depth);
      arrayOfLocation[(i - 1)] = new Location(paramByte, (short)(paramShort & 0xFF));
      return new LocationInfo(i, arrayOfLocation);
    }
    
    public TypeAnnotation[] filter(TypeAnnotation[] paramArrayOfTypeAnnotation)
    {
      ArrayList localArrayList = new ArrayList(paramArrayOfTypeAnnotation.length);
      for (TypeAnnotation localTypeAnnotation : paramArrayOfTypeAnnotation) {
        if (isSameLocationInfo(localTypeAnnotation.getLocationInfo())) {
          localArrayList.add(localTypeAnnotation);
        }
      }
      return (TypeAnnotation[])localArrayList.toArray(new TypeAnnotation[0]);
    }
    
    boolean isSameLocationInfo(LocationInfo paramLocationInfo)
    {
      if (this.depth != paramLocationInfo.depth) {
        return false;
      }
      for (int i = 0; i < this.depth; i++) {
        if (!this.locations[i].isSameLocation(paramLocationInfo.locations[i])) {
          return false;
        }
      }
      return true;
    }
    
    public static final class Location
    {
      public final byte tag;
      public final short index;
      
      boolean isSameLocation(Location paramLocation)
      {
        return (this.tag == paramLocation.tag) && (this.index == paramLocation.index);
      }
      
      public Location(byte paramByte, short paramShort)
      {
        this.tag = paramByte;
        this.index = paramShort;
      }
    }
  }
  
  public static enum TypeAnnotationTarget
  {
    CLASS_TYPE_PARAMETER,  METHOD_TYPE_PARAMETER,  CLASS_EXTENDS,  CLASS_IMPLEMENTS,  CLASS_TYPE_PARAMETER_BOUND,  METHOD_TYPE_PARAMETER_BOUND,  FIELD,  METHOD_RETURN,  METHOD_RECEIVER,  METHOD_FORMAL_PARAMETER,  THROWS;
    
    private TypeAnnotationTarget() {}
  }
  
  public static final class TypeAnnotationTargetInfo
  {
    private final TypeAnnotation.TypeAnnotationTarget target;
    private final int count;
    private final int secondaryIndex;
    private static final int UNUSED_INDEX = -2;
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget)
    {
      this(paramTypeAnnotationTarget, -2, -2);
    }
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, int paramInt)
    {
      this(paramTypeAnnotationTarget, paramInt, -2);
    }
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget paramTypeAnnotationTarget, int paramInt1, int paramInt2)
    {
      this.target = paramTypeAnnotationTarget;
      this.count = paramInt1;
      this.secondaryIndex = paramInt2;
    }
    
    public TypeAnnotation.TypeAnnotationTarget getTarget()
    {
      return this.target;
    }
    
    public int getCount()
    {
      return this.count;
    }
    
    public int getSecondaryIndex()
    {
      return this.secondaryIndex;
    }
    
    public String toString()
    {
      return "" + this.target + ": " + this.count + ", " + this.secondaryIndex;
    }
  }
}
