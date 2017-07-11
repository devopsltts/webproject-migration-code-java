package java.awt.font;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public final class NumericShaper
  implements Serializable
{
  private int key;
  private int mask;
  private Range shapingRange;
  private transient Set<Range> rangeSet;
  private transient Range[] rangeArray;
  private static final int BSEARCH_THRESHOLD = 3;
  private static final long serialVersionUID = -8022764705923730308L;
  public static final int EUROPEAN = 1;
  public static final int ARABIC = 2;
  public static final int EASTERN_ARABIC = 4;
  public static final int DEVANAGARI = 8;
  public static final int BENGALI = 16;
  public static final int GURMUKHI = 32;
  public static final int GUJARATI = 64;
  public static final int ORIYA = 128;
  public static final int TAMIL = 256;
  public static final int TELUGU = 512;
  public static final int KANNADA = 1024;
  public static final int MALAYALAM = 2048;
  public static final int THAI = 4096;
  public static final int LAO = 8192;
  public static final int TIBETAN = 16384;
  public static final int MYANMAR = 32768;
  public static final int ETHIOPIC = 65536;
  public static final int KHMER = 131072;
  public static final int MONGOLIAN = 262144;
  public static final int ALL_RANGES = 524287;
  private static final int EUROPEAN_KEY = 0;
  private static final int ARABIC_KEY = 1;
  private static final int EASTERN_ARABIC_KEY = 2;
  private static final int DEVANAGARI_KEY = 3;
  private static final int BENGALI_KEY = 4;
  private static final int GURMUKHI_KEY = 5;
  private static final int GUJARATI_KEY = 6;
  private static final int ORIYA_KEY = 7;
  private static final int TAMIL_KEY = 8;
  private static final int TELUGU_KEY = 9;
  private static final int KANNADA_KEY = 10;
  private static final int MALAYALAM_KEY = 11;
  private static final int THAI_KEY = 12;
  private static final int LAO_KEY = 13;
  private static final int TIBETAN_KEY = 14;
  private static final int MYANMAR_KEY = 15;
  private static final int ETHIOPIC_KEY = 16;
  private static final int KHMER_KEY = 17;
  private static final int MONGOLIAN_KEY = 18;
  private static final int NUM_KEYS = 19;
  private static final int CONTEXTUAL_MASK = Integer.MIN_VALUE;
  private static final char[] bases = { '\000', 'ذ', 'ۀ', 'श', 'শ', 'ਸ਼', 'શ', 'ଶ', 'ஶ', 'శ', 'ಶ', 'ശ', 'ภ', 'ຠ', '໰', 'တ', 'ጸ', 'ឰ', '០' };
  private static final char[] contexts = { '\000', '̀', '؀', 'ހ', '؀', 'ހ', 'ऀ', 'ঀ', 'ঀ', '਀', '਀', '઀', '઀', '଀', '଀', '஀', '஀', 'ఀ', 'ఀ', 'ಀ', 'ಀ', 'ഀ', 'ഀ', '඀', '฀', '຀', '຀', 'ༀ', 'ༀ', 'က', 'က', 'ႀ', 'ሀ', 'ᎀ', 'ក', '᠀', '᠀', 'ᤀ', 65535 };
  private static int ctCache = 0;
  private static int ctCacheLimit = contexts.length - 2;
  private volatile transient Range currentRange = Range.EUROPEAN;
  private static int[] strongTable = { 0, 65, 91, 97, 123, 170, 171, 181, 182, 186, 187, 192, 215, 216, 247, 248, 697, 699, 706, 720, 722, 736, 741, 750, 751, 880, 884, 886, 894, 902, 903, 904, 1014, 1015, 1155, 1162, 1418, 1470, 1471, 1472, 1473, 1475, 1476, 1478, 1479, 1488, 1536, 1544, 1545, 1547, 1548, 1549, 1550, 1563, 1611, 1645, 1648, 1649, 1750, 1765, 1767, 1774, 1776, 1786, 1809, 1810, 1840, 1869, 1958, 1969, 2027, 2036, 2038, 2042, 2070, 2074, 2075, 2084, 2085, 2088, 2089, 2096, 2137, 2142, 2276, 2307, 2362, 2363, 2364, 2365, 2369, 2377, 2381, 2382, 2385, 2392, 2402, 2404, 2433, 2434, 2492, 2493, 2497, 2503, 2509, 2510, 2530, 2534, 2546, 2548, 2555, 2563, 2620, 2622, 2625, 2649, 2672, 2674, 2677, 2691, 2748, 2749, 2753, 2761, 2765, 2768, 2786, 2790, 2801, 2818, 2876, 2877, 2879, 2880, 2881, 2887, 2893, 2903, 2914, 2918, 2946, 2947, 3008, 3009, 3021, 3024, 3059, 3073, 3134, 3137, 3142, 3160, 3170, 3174, 3192, 3199, 3260, 3261, 3276, 3285, 3298, 3302, 3393, 3398, 3405, 3406, 3426, 3430, 3530, 3535, 3538, 3544, 3633, 3634, 3636, 3648, 3655, 3663, 3761, 3762, 3764, 3773, 3784, 3792, 3864, 3866, 3893, 3894, 3895, 3896, 3897, 3902, 3953, 3967, 3968, 3973, 3974, 3976, 3981, 4030, 4038, 4039, 4141, 4145, 4146, 4152, 4153, 4155, 4157, 4159, 4184, 4186, 4190, 4193, 4209, 4213, 4226, 4227, 4229, 4231, 4237, 4238, 4253, 4254, 4957, 4960, 5008, 5024, 5120, 5121, 5760, 5761, 5787, 5792, 5906, 5920, 5938, 5941, 5970, 5984, 6002, 6016, 6068, 6070, 6071, 6078, 6086, 6087, 6089, 6100, 6107, 6108, 6109, 6112, 6128, 6160, 6313, 6314, 6432, 6435, 6439, 6441, 6450, 6451, 6457, 6470, 6622, 6656, 6679, 6681, 6742, 6743, 6744, 6753, 6754, 6755, 6757, 6765, 6771, 6784, 6912, 6916, 6964, 6965, 6966, 6971, 6972, 6973, 6978, 6979, 7019, 7028, 7040, 7042, 7074, 7078, 7080, 7082, 7083, 7084, 7142, 7143, 7144, 7146, 7149, 7150, 7151, 7154, 7212, 7220, 7222, 7227, 7376, 7379, 7380, 7393, 7394, 7401, 7405, 7406, 7412, 7413, 7616, 7680, 8125, 8126, 8127, 8130, 8141, 8144, 8157, 8160, 8173, 8178, 8189, 8206, 8208, 8305, 8308, 8319, 8320, 8336, 8352, 8450, 8451, 8455, 8456, 8458, 8468, 8469, 8470, 8473, 8478, 8484, 8485, 8486, 8487, 8488, 8489, 8490, 8494, 8495, 8506, 8508, 8512, 8517, 8522, 8526, 8528, 8544, 8585, 9014, 9083, 9109, 9110, 9372, 9450, 9900, 9901, 10240, 10496, 11264, 11493, 11499, 11503, 11506, 11513, 11520, 11647, 11648, 11744, 12293, 12296, 12321, 12330, 12337, 12342, 12344, 12349, 12353, 12441, 12445, 12448, 12449, 12539, 12540, 12736, 12784, 12829, 12832, 12880, 12896, 12924, 12927, 12977, 12992, 13004, 13008, 13175, 13179, 13278, 13280, 13311, 13312, 19904, 19968, 42128, 42192, 42509, 42512, 42607, 42624, 42655, 42656, 42736, 42738, 42752, 42786, 42888, 42889, 43010, 43011, 43014, 43015, 43019, 43020, 43045, 43047, 43048, 43056, 43064, 43072, 43124, 43136, 43204, 43214, 43232, 43250, 43302, 43310, 43335, 43346, 43392, 43395, 43443, 43444, 43446, 43450, 43452, 43453, 43561, 43567, 43569, 43571, 43573, 43584, 43587, 43588, 43596, 43597, 43696, 43697, 43698, 43701, 43703, 43705, 43710, 43712, 43713, 43714, 43756, 43758, 43766, 43777, 44005, 44006, 44008, 44009, 44013, 44016, 64286, 64287, 64297, 64298, 64830, 64848, 65021, 65136, 65279, 65313, 65339, 65345, 65371, 65382, 65504, 65536, 65793, 65794, 65856, 66000, 66045, 66176, 67871, 67872, 68097, 68112, 68152, 68160, 68409, 68416, 69216, 69632, 69633, 69634, 69688, 69703, 69714, 69734, 69760, 69762, 69811, 69815, 69817, 69819, 69888, 69891, 69927, 69932, 69933, 69942, 70016, 70018, 70070, 70079, 71339, 71340, 71341, 71342, 71344, 71350, 71351, 71360, 94095, 94099, 119143, 119146, 119155, 119171, 119173, 119180, 119210, 119214, 119296, 119648, 120539, 120540, 120597, 120598, 120655, 120656, 120713, 120714, 120771, 120772, 120782, 126464, 126704, 127248, 127338, 127344, 127744, 128140, 128141, 128292, 128293, 131072, 917505, 983040, 1114110, 1114111 };
  private volatile transient int stCache = 0;
  
  private static int getContextKey(char paramChar)
  {
    if (paramChar < contexts[ctCache]) {
      while ((ctCache > 0) && (paramChar < contexts[ctCache])) {
        ctCache -= 1;
      }
    }
    if (paramChar >= contexts[(ctCache + 1)]) {
      while ((ctCache < ctCacheLimit) && (paramChar >= contexts[(ctCache + 1)])) {
        ctCache += 1;
      }
    }
    return (ctCache & 0x1) == 0 ? ctCache / 2 : 0;
  }
  
  private Range rangeForCodePoint(int paramInt)
  {
    if (this.currentRange.inRange(paramInt)) {
      return this.currentRange;
    }
    Range[] arrayOfRange = this.rangeArray;
    int i;
    if (arrayOfRange.length > 3)
    {
      i = 0;
      int j = arrayOfRange.length - 1;
      while (i <= j)
      {
        int k = (i + j) / 2;
        Range localRange = arrayOfRange[k];
        if (paramInt < localRange.start)
        {
          j = k - 1;
        }
        else if (paramInt >= localRange.end)
        {
          i = k + 1;
        }
        else
        {
          this.currentRange = localRange;
          return localRange;
        }
      }
    }
    else
    {
      for (i = 0; i < arrayOfRange.length; i++) {
        if (arrayOfRange[i].inRange(paramInt)) {
          return arrayOfRange[i];
        }
      }
    }
    return Range.EUROPEAN;
  }
  
  private boolean isStrongDirectional(char paramChar)
  {
    int i = this.stCache;
    if (paramChar < strongTable[i]) {
      i = search(paramChar, strongTable, 0, i);
    } else if (paramChar >= strongTable[(i + 1)]) {
      i = search(paramChar, strongTable, i + 1, strongTable.length - i - 1);
    }
    boolean bool = (i & 0x1) == 1;
    this.stCache = i;
    return bool;
  }
  
  private static int getKeyFromMask(int paramInt)
  {
    for (int i = 0; (i < 19) && ((paramInt & 1 << i) == 0); i++) {}
    if ((i == 19) || ((paramInt & (1 << i ^ 0xFFFFFFFF)) != 0)) {
      throw new IllegalArgumentException("invalid shaper: " + Integer.toHexString(paramInt));
    }
    return i;
  }
  
  public static NumericShaper getShaper(int paramInt)
  {
    int i = getKeyFromMask(paramInt);
    return new NumericShaper(i, paramInt);
  }
  
  public static NumericShaper getShaper(Range paramRange)
  {
    return new NumericShaper(paramRange, EnumSet.of(paramRange));
  }
  
  public static NumericShaper getContextualShaper(int paramInt)
  {
    paramInt |= 0x80000000;
    return new NumericShaper(0, paramInt);
  }
  
  public static NumericShaper getContextualShaper(Set<Range> paramSet)
  {
    NumericShaper localNumericShaper = new NumericShaper(Range.EUROPEAN, paramSet);
    localNumericShaper.mask = Integer.MIN_VALUE;
    return localNumericShaper;
  }
  
  public static NumericShaper getContextualShaper(int paramInt1, int paramInt2)
  {
    int i = getKeyFromMask(paramInt2);
    paramInt1 |= 0x80000000;
    return new NumericShaper(i, paramInt1);
  }
  
  public static NumericShaper getContextualShaper(Set<Range> paramSet, Range paramRange)
  {
    if (paramRange == null) {
      throw new NullPointerException();
    }
    NumericShaper localNumericShaper = new NumericShaper(paramRange, paramSet);
    localNumericShaper.mask = Integer.MIN_VALUE;
    return localNumericShaper;
  }
  
  private NumericShaper(int paramInt1, int paramInt2)
  {
    this.key = paramInt1;
    this.mask = paramInt2;
  }
  
  private NumericShaper(Range paramRange, Set<Range> paramSet)
  {
    this.shapingRange = paramRange;
    this.rangeSet = EnumSet.copyOf(paramSet);
    if ((this.rangeSet.contains(Range.EASTERN_ARABIC)) && (this.rangeSet.contains(Range.ARABIC))) {
      this.rangeSet.remove(Range.ARABIC);
    }
    if ((this.rangeSet.contains(Range.TAI_THAM_THAM)) && (this.rangeSet.contains(Range.TAI_THAM_HORA))) {
      this.rangeSet.remove(Range.TAI_THAM_HORA);
    }
    this.rangeArray = ((Range[])this.rangeSet.toArray(new Range[this.rangeSet.size()]));
    if (this.rangeArray.length > 3) {
      Arrays.sort(this.rangeArray, new Comparator()
      {
        public int compare(NumericShaper.Range paramAnonymousRange1, NumericShaper.Range paramAnonymousRange2)
        {
          return NumericShaper.Range.access$400(paramAnonymousRange1) == NumericShaper.Range.access$400(paramAnonymousRange2) ? 0 : NumericShaper.Range.access$400(paramAnonymousRange1) > NumericShaper.Range.access$400(paramAnonymousRange2) ? 1 : -1;
        }
      });
    }
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (isContextual())
    {
      if (this.rangeSet == null) {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.key);
      } else {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.shapingRange);
      }
    }
    else {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (isContextual())
    {
      int i = getKeyFromMask(paramInt3);
      if (this.rangeSet == null) {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, i);
      } else {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, Range.values()[i]);
      }
    }
    else
    {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void shape(char[] paramArrayOfChar, int paramInt1, int paramInt2, Range paramRange)
  {
    checkParams(paramArrayOfChar, paramInt1, paramInt2);
    if (paramRange == null) {
      throw new NullPointerException("context is null");
    }
    if (isContextual())
    {
      if (this.rangeSet != null)
      {
        shapeContextually(paramArrayOfChar, paramInt1, paramInt2, paramRange);
      }
      else
      {
        int i = Range.toRangeIndex(paramRange);
        if (i >= 0) {
          shapeContextually(paramArrayOfChar, paramInt1, paramInt2, i);
        } else {
          shapeContextually(paramArrayOfChar, paramInt1, paramInt2, this.shapingRange);
        }
      }
    }
    else {
      shapeNonContextually(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  private void checkParams(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      throw new NullPointerException("text is null");
    }
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfChar.length) || (paramInt1 + paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfChar.length)) {
      throw new IndexOutOfBoundsException("bad start or count for text of length " + paramArrayOfChar.length);
    }
  }
  
  public boolean isContextual()
  {
    return (this.mask & 0x80000000) != 0;
  }
  
  public int getRanges()
  {
    return this.mask & 0x7FFFFFFF;
  }
  
  public Set<Range> getRangeSet()
  {
    if (this.rangeSet != null) {
      return EnumSet.copyOf(this.rangeSet);
    }
    return Range.maskToRangeSet(this.mask);
  }
  
  private void shapeNonContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int j = 48;
    int i;
    if (this.shapingRange != null)
    {
      i = this.shapingRange.getDigitBase();
      j = (char)(j + this.shapingRange.getNumericBase());
    }
    else
    {
      i = bases[this.key];
      if (this.key == 16) {
        j = (char)(j + 1);
      }
    }
    int k = paramInt1;
    int m = paramInt1 + paramInt2;
    while (k < m)
    {
      int n = paramArrayOfChar[k];
      if ((n >= j) && (n <= 57)) {
        paramArrayOfChar[k] = ((char)(n + i));
      }
      k++;
    }
  }
  
  private synchronized void shapeContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.mask & 1 << paramInt3) == 0) {
      paramInt3 = 0;
    }
    int i = paramInt3;
    int j = bases[paramInt3];
    int k = paramInt3 == 16 ? 49 : 48;
    synchronized (NumericShaper.class)
    {
      int m = paramInt1;
      int n = paramInt1 + paramInt2;
      while (m < n)
      {
        char c = paramArrayOfChar[m];
        if ((c >= k) && (c <= '9')) {
          paramArrayOfChar[m] = ((char)(c + j));
        }
        if (isStrongDirectional(c))
        {
          int i1 = getContextKey(c);
          if (i1 != i)
          {
            i = i1;
            paramInt3 = i1;
            if (((this.mask & 0x4) != 0) && ((paramInt3 == 1) || (paramInt3 == 2))) {
              paramInt3 = 2;
            } else if (((this.mask & 0x2) != 0) && ((paramInt3 == 1) || (paramInt3 == 2))) {
              paramInt3 = 1;
            } else if ((this.mask & 1 << paramInt3) == 0) {
              paramInt3 = 0;
            }
            j = bases[paramInt3];
            k = paramInt3 == 16 ? 49 : 48;
          }
        }
        m++;
      }
    }
  }
  
  private void shapeContextually(char[] paramArrayOfChar, int paramInt1, int paramInt2, Range paramRange)
  {
    if ((paramRange == null) || (!this.rangeSet.contains(paramRange))) {
      paramRange = Range.EUROPEAN;
    }
    Range localRange = paramRange;
    int i = paramRange.getDigitBase();
    int j = (char)('0' + paramRange.getNumericBase());
    int k = paramInt1 + paramInt2;
    for (int m = paramInt1; m < k; m++)
    {
      int n = paramArrayOfChar[m];
      if ((n >= j) && (n <= 57))
      {
        paramArrayOfChar[m] = ((char)(n + i));
      }
      else if (isStrongDirectional(n))
      {
        paramRange = rangeForCodePoint(n);
        if (paramRange != localRange)
        {
          localRange = paramRange;
          i = paramRange.getDigitBase();
          j = (char)('0' + paramRange.getNumericBase());
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = this.mask;
    if (this.rangeSet != null)
    {
      i &= 0x80000000;
      i ^= this.rangeSet.hashCode();
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject != null) {
      try
      {
        NumericShaper localNumericShaper = (NumericShaper)paramObject;
        if (this.rangeSet != null)
        {
          if (localNumericShaper.rangeSet != null) {
            return (isContextual() == localNumericShaper.isContextual()) && (this.rangeSet.equals(localNumericShaper.rangeSet)) && (this.shapingRange == localNumericShaper.shapingRange);
          }
          return (isContextual() == localNumericShaper.isContextual()) && (this.rangeSet.equals(Range.maskToRangeSet(localNumericShaper.mask))) && (this.shapingRange == Range.indexToRange(localNumericShaper.key));
        }
        if (localNumericShaper.rangeSet != null)
        {
          Set localSet = Range.maskToRangeSet(this.mask);
          Range localRange = Range.indexToRange(this.key);
          return (isContextual() == localNumericShaper.isContextual()) && (localSet.equals(localNumericShaper.rangeSet)) && (localRange == localNumericShaper.shapingRange);
        }
        return (localNumericShaper.mask == this.mask) && (localNumericShaper.key == this.key);
      }
      catch (ClassCastException localClassCastException) {}
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(super.toString());
    localStringBuilder.append("[contextual:").append(isContextual());
    Object localObject = null;
    if (isContextual())
    {
      localStringBuilder.append(", context:");
      localStringBuilder.append(this.shapingRange == null ? Range.values()[this.key] : this.shapingRange);
    }
    if (this.rangeSet == null)
    {
      localStringBuilder.append(", range(s): ");
      int i = 1;
      for (int j = 0; j < 19; j++) {
        if ((this.mask & 1 << j) != 0)
        {
          if (i != 0) {
            i = 0;
          } else {
            localStringBuilder.append(", ");
          }
          localStringBuilder.append(Range.values()[j]);
        }
      }
    }
    else
    {
      localStringBuilder.append(", range set: ").append(this.rangeSet);
    }
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  private static int getHighBit(int paramInt)
  {
    if (paramInt <= 0) {
      return -32;
    }
    int i = 0;
    if (paramInt >= 65536)
    {
      paramInt >>= 16;
      i += 16;
    }
    if (paramInt >= 256)
    {
      paramInt >>= 8;
      i += 8;
    }
    if (paramInt >= 16)
    {
      paramInt >>= 4;
      i += 4;
    }
    if (paramInt >= 4)
    {
      paramInt >>= 2;
      i += 2;
    }
    if (paramInt >= 2) {
      i++;
    }
    return i;
  }
  
  private static int search(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3)
  {
    int i = 1 << getHighBit(paramInt3);
    int j = paramInt3 - i;
    int k = i;
    int m = paramInt2;
    if (paramInt1 >= paramArrayOfInt[(m + j)]) {
      m += j;
    }
    while (k > 1)
    {
      k >>= 1;
      if (paramInt1 >= paramArrayOfInt[(m + k)]) {
        m += k;
      }
    }
    return m;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (this.shapingRange != null)
    {
      int i = Range.toRangeIndex(this.shapingRange);
      if (i >= 0) {
        this.key = i;
      }
    }
    if (this.rangeSet != null) {
      this.mask |= Range.toRangeMask(this.rangeSet);
    }
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public static enum Range
  {
    EUROPEAN(48, 0, 768),  ARABIC(1632, 1536, 1920),  EASTERN_ARABIC(1776, 1536, 1920),  DEVANAGARI(2406, 2304, 2432),  BENGALI(2534, 2432, 2560),  GURMUKHI(2662, 2560, 2688),  GUJARATI(2790, 2816, 2944),  ORIYA(2918, 2816, 2944),  TAMIL(3046, 2944, 3072),  TELUGU(3174, 3072, 3200),  KANNADA(3302, 3200, 3328),  MALAYALAM(3430, 3328, 3456),  THAI(3664, 3584, 3712),  LAO(3792, 3712, 3840),  TIBETAN(3872, 3840, 4096),  MYANMAR(4160, 4096, 4224),  ETHIOPIC(4969, 4608, 4992),  KHMER(6112, 6016, 6144),  MONGOLIAN(6160, 6144, 6400),  NKO(1984, 1984, 2048),  MYANMAR_SHAN(4240, 4096, 4256),  LIMBU(6470, 6400, 6480),  NEW_TAI_LUE(6608, 6528, 6624),  BALINESE(6992, 6912, 7040),  SUNDANESE(7088, 7040, 7104),  LEPCHA(7232, 7168, 7248),  OL_CHIKI(7248, 7248, 7296),  VAI(42528, 42240, 42560),  SAURASHTRA(43216, 43136, 43232),  KAYAH_LI(43264, 43264, 43312),  CHAM(43600, 43520, 43616),  TAI_THAM_HORA(6784, 6688, 6832),  TAI_THAM_THAM(6800, 6688, 6832),  JAVANESE(43472, 43392, 43488),  MEETEI_MAYEK(44016, 43968, 44032);
    
    private final int base;
    private final int start;
    private final int end;
    
    private static int toRangeIndex(Range paramRange)
    {
      int i = paramRange.ordinal();
      return i < 19 ? i : -1;
    }
    
    private static Range indexToRange(int paramInt)
    {
      return paramInt < 19 ? values()[paramInt] : null;
    }
    
    private static int toRangeMask(Set<Range> paramSet)
    {
      int i = 0;
      Iterator localIterator = paramSet.iterator();
      while (localIterator.hasNext())
      {
        Range localRange = (Range)localIterator.next();
        int j = localRange.ordinal();
        if (j < 19) {
          i |= 1 << j;
        }
      }
      return i;
    }
    
    private static Set<Range> maskToRangeSet(int paramInt)
    {
      EnumSet localEnumSet = EnumSet.noneOf(Range.class);
      Range[] arrayOfRange = values();
      for (int i = 0; i < 19; i++) {
        if ((paramInt & 1 << i) != 0) {
          localEnumSet.add(arrayOfRange[i]);
        }
      }
      return localEnumSet;
    }
    
    private Range(int paramInt1, int paramInt2, int paramInt3)
    {
      this.base = (paramInt1 - ('0' + getNumericBase()));
      this.start = paramInt2;
      this.end = paramInt3;
    }
    
    private int getDigitBase()
    {
      return this.base;
    }
    
    char getNumericBase()
    {
      return '\000';
    }
    
    private boolean inRange(int paramInt)
    {
      return (this.start <= paramInt) && (paramInt < this.end);
    }
  }
}
