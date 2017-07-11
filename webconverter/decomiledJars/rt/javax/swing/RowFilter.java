package javax.swing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RowFilter<M, I>
{
  public RowFilter() {}
  
  private static void checkIndices(int[] paramArrayOfInt)
  {
    for (int i = paramArrayOfInt.length - 1; i >= 0; i--) {
      if (paramArrayOfInt[i] < 0) {
        throw new IllegalArgumentException("Index must be >= 0");
      }
    }
  }
  
  public static <M, I> RowFilter<M, I> regexFilter(String paramString, int... paramVarArgs)
  {
    return new RegexFilter(Pattern.compile(paramString), paramVarArgs);
  }
  
  public static <M, I> RowFilter<M, I> dateFilter(ComparisonType paramComparisonType, Date paramDate, int... paramVarArgs)
  {
    return new DateFilter(paramComparisonType, paramDate.getTime(), paramVarArgs);
  }
  
  public static <M, I> RowFilter<M, I> numberFilter(ComparisonType paramComparisonType, Number paramNumber, int... paramVarArgs)
  {
    return new NumberFilter(paramComparisonType, paramNumber, paramVarArgs);
  }
  
  public static <M, I> RowFilter<M, I> orFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable)
  {
    return new OrFilter(paramIterable);
  }
  
  public static <M, I> RowFilter<M, I> andFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable)
  {
    return new AndFilter(paramIterable);
  }
  
  public static <M, I> RowFilter<M, I> notFilter(RowFilter<M, I> paramRowFilter)
  {
    return new NotFilter(paramRowFilter);
  }
  
  public abstract boolean include(Entry<? extends M, ? extends I> paramEntry);
  
  private static class AndFilter<M, I>
    extends RowFilter.OrFilter<M, I>
  {
    AndFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable)
    {
      super();
    }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> paramEntry)
    {
      Iterator localIterator = this.filters.iterator();
      while (localIterator.hasNext())
      {
        RowFilter localRowFilter = (RowFilter)localIterator.next();
        if (!localRowFilter.include(paramEntry)) {
          return false;
        }
      }
      return true;
    }
  }
  
  public static enum ComparisonType
  {
    BEFORE,  AFTER,  EQUAL,  NOT_EQUAL;
    
    private ComparisonType() {}
  }
  
  private static class DateFilter
    extends RowFilter.GeneralFilter
  {
    private long date;
    private RowFilter.ComparisonType type;
    
    DateFilter(RowFilter.ComparisonType paramComparisonType, long paramLong, int[] paramArrayOfInt)
    {
      super();
      if (paramComparisonType == null) {
        throw new IllegalArgumentException("type must be non-null");
      }
      this.type = paramComparisonType;
      this.date = paramLong;
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> paramEntry, int paramInt)
    {
      Object localObject = paramEntry.getValue(paramInt);
      if ((localObject instanceof Date))
      {
        long l = ((Date)localObject).getTime();
        switch (RowFilter.1.$SwitchMap$javax$swing$RowFilter$ComparisonType[this.type.ordinal()])
        {
        case 1: 
          return l < this.date;
        case 2: 
          return l > this.date;
        case 3: 
          return l == this.date;
        case 4: 
          return l != this.date;
        }
      }
      return false;
    }
  }
  
  public static abstract class Entry<M, I>
  {
    public Entry() {}
    
    public abstract M getModel();
    
    public abstract int getValueCount();
    
    public abstract Object getValue(int paramInt);
    
    public String getStringValue(int paramInt)
    {
      Object localObject = getValue(paramInt);
      return localObject == null ? "" : localObject.toString();
    }
    
    public abstract I getIdentifier();
  }
  
  private static abstract class GeneralFilter
    extends RowFilter<Object, Object>
  {
    private int[] columns;
    
    GeneralFilter(int[] paramArrayOfInt)
    {
      RowFilter.checkIndices(paramArrayOfInt);
      this.columns = paramArrayOfInt;
    }
    
    public boolean include(RowFilter.Entry<? extends Object, ? extends Object> paramEntry)
    {
      int i = paramEntry.getValueCount();
      if (this.columns.length > 0)
      {
        for (int j = this.columns.length - 1; j >= 0; j--)
        {
          int k = this.columns[j];
          if ((k < i) && (include(paramEntry, k))) {
            return true;
          }
        }
      }
      else
      {
        do
        {
          i--;
          if (i < 0) {
            break;
          }
        } while (!include(paramEntry, i));
        return true;
      }
      return false;
    }
    
    protected abstract boolean include(RowFilter.Entry<? extends Object, ? extends Object> paramEntry, int paramInt);
  }
  
  private static class NotFilter<M, I>
    extends RowFilter<M, I>
  {
    private RowFilter<M, I> filter;
    
    NotFilter(RowFilter<M, I> paramRowFilter)
    {
      if (paramRowFilter == null) {
        throw new IllegalArgumentException("filter must be non-null");
      }
      this.filter = paramRowFilter;
    }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> paramEntry)
    {
      return !this.filter.include(paramEntry);
    }
  }
  
  private static class NumberFilter
    extends RowFilter.GeneralFilter
  {
    private boolean isComparable;
    private Number number;
    private RowFilter.ComparisonType type;
    
    NumberFilter(RowFilter.ComparisonType paramComparisonType, Number paramNumber, int[] paramArrayOfInt)
    {
      super();
      if ((paramComparisonType == null) || (paramNumber == null)) {
        throw new IllegalArgumentException("type and number must be non-null");
      }
      this.type = paramComparisonType;
      this.number = paramNumber;
      this.isComparable = (paramNumber instanceof Comparable);
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> paramEntry, int paramInt)
    {
      Object localObject = paramEntry.getValue(paramInt);
      if ((localObject instanceof Number))
      {
        int i = 1;
        Class localClass = localObject.getClass();
        int j;
        if ((this.number.getClass() == localClass) && (this.isComparable)) {
          j = ((Comparable)this.number).compareTo(localObject);
        } else {
          j = longCompare((Number)localObject);
        }
        switch (RowFilter.1.$SwitchMap$javax$swing$RowFilter$ComparisonType[this.type.ordinal()])
        {
        case 1: 
          return j > 0;
        case 2: 
          return j < 0;
        case 3: 
          return j == 0;
        case 4: 
          return j != 0;
        }
      }
      return false;
    }
    
    private int longCompare(Number paramNumber)
    {
      long l = this.number.longValue() - paramNumber.longValue();
      if (l < 0L) {
        return -1;
      }
      if (l > 0L) {
        return 1;
      }
      return 0;
    }
  }
  
  private static class OrFilter<M, I>
    extends RowFilter<M, I>
  {
    List<RowFilter<? super M, ? super I>> filters = new ArrayList();
    
    OrFilter(Iterable<? extends RowFilter<? super M, ? super I>> paramIterable)
    {
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        RowFilter localRowFilter = (RowFilter)localIterator.next();
        if (localRowFilter == null) {
          throw new IllegalArgumentException("Filter must be non-null");
        }
        this.filters.add(localRowFilter);
      }
    }
    
    public boolean include(RowFilter.Entry<? extends M, ? extends I> paramEntry)
    {
      Iterator localIterator = this.filters.iterator();
      while (localIterator.hasNext())
      {
        RowFilter localRowFilter = (RowFilter)localIterator.next();
        if (localRowFilter.include(paramEntry)) {
          return true;
        }
      }
      return false;
    }
  }
  
  private static class RegexFilter
    extends RowFilter.GeneralFilter
  {
    private Matcher matcher;
    
    RegexFilter(Pattern paramPattern, int[] paramArrayOfInt)
    {
      super();
      if (paramPattern == null) {
        throw new IllegalArgumentException("Pattern must be non-null");
      }
      this.matcher = paramPattern.matcher("");
    }
    
    protected boolean include(RowFilter.Entry<? extends Object, ? extends Object> paramEntry, int paramInt)
    {
      this.matcher.reset(paramEntry.getStringValue(paramInt));
      return this.matcher.find();
    }
  }
}
