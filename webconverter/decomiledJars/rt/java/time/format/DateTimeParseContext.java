package java.time.format;

import java.time.ZoneId;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DateTimeParseContext
{
  private DateTimeFormatter formatter;
  private boolean caseSensitive = true;
  private boolean strict = true;
  private final ArrayList<Parsed> parsed = new ArrayList();
  private ArrayList<Consumer<Chronology>> chronoListeners = null;
  
  DateTimeParseContext(DateTimeFormatter paramDateTimeFormatter)
  {
    this.formatter = paramDateTimeFormatter;
    this.parsed.add(new Parsed());
  }
  
  DateTimeParseContext copy()
  {
    DateTimeParseContext localDateTimeParseContext = new DateTimeParseContext(this.formatter);
    localDateTimeParseContext.caseSensitive = this.caseSensitive;
    localDateTimeParseContext.strict = this.strict;
    return localDateTimeParseContext;
  }
  
  Locale getLocale()
  {
    return this.formatter.getLocale();
  }
  
  DecimalStyle getDecimalStyle()
  {
    return this.formatter.getDecimalStyle();
  }
  
  Chronology getEffectiveChronology()
  {
    Object localObject = currentParsed().chrono;
    if (localObject == null)
    {
      localObject = this.formatter.getChronology();
      if (localObject == null) {
        localObject = IsoChronology.INSTANCE;
      }
    }
    return localObject;
  }
  
  boolean isCaseSensitive()
  {
    return this.caseSensitive;
  }
  
  void setCaseSensitive(boolean paramBoolean)
  {
    this.caseSensitive = paramBoolean;
  }
  
  boolean subSequenceEquals(CharSequence paramCharSequence1, int paramInt1, CharSequence paramCharSequence2, int paramInt2, int paramInt3)
  {
    if ((paramInt1 + paramInt3 > paramCharSequence1.length()) || (paramInt2 + paramInt3 > paramCharSequence2.length())) {
      return false;
    }
    int i;
    char c1;
    char c2;
    if (isCaseSensitive()) {
      for (i = 0; i < paramInt3; i++)
      {
        c1 = paramCharSequence1.charAt(paramInt1 + i);
        c2 = paramCharSequence2.charAt(paramInt2 + i);
        if (c1 != c2) {
          return false;
        }
      }
    } else {
      for (i = 0; i < paramInt3; i++)
      {
        c1 = paramCharSequence1.charAt(paramInt1 + i);
        c2 = paramCharSequence2.charAt(paramInt2 + i);
        if ((c1 != c2) && (Character.toUpperCase(c1) != Character.toUpperCase(c2)) && (Character.toLowerCase(c1) != Character.toLowerCase(c2))) {
          return false;
        }
      }
    }
    return true;
  }
  
  boolean charEquals(char paramChar1, char paramChar2)
  {
    if (isCaseSensitive()) {
      return paramChar1 == paramChar2;
    }
    return charEqualsIgnoreCase(paramChar1, paramChar2);
  }
  
  static boolean charEqualsIgnoreCase(char paramChar1, char paramChar2)
  {
    return (paramChar1 == paramChar2) || (Character.toUpperCase(paramChar1) == Character.toUpperCase(paramChar2)) || (Character.toLowerCase(paramChar1) == Character.toLowerCase(paramChar2));
  }
  
  boolean isStrict()
  {
    return this.strict;
  }
  
  void setStrict(boolean paramBoolean)
  {
    this.strict = paramBoolean;
  }
  
  void startOptional()
  {
    this.parsed.add(currentParsed().copy());
  }
  
  void endOptional(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.parsed.remove(this.parsed.size() - 2);
    } else {
      this.parsed.remove(this.parsed.size() - 1);
    }
  }
  
  private Parsed currentParsed()
  {
    return (Parsed)this.parsed.get(this.parsed.size() - 1);
  }
  
  Parsed toUnresolved()
  {
    return currentParsed();
  }
  
  TemporalAccessor toResolved(ResolverStyle paramResolverStyle, Set<TemporalField> paramSet)
  {
    Parsed localParsed = currentParsed();
    localParsed.chrono = getEffectiveChronology();
    localParsed.zone = (localParsed.zone != null ? localParsed.zone : this.formatter.getZone());
    return localParsed.resolve(paramResolverStyle, paramSet);
  }
  
  Long getParsed(TemporalField paramTemporalField)
  {
    return (Long)currentParsed().fieldValues.get(paramTemporalField);
  }
  
  int setParsedField(TemporalField paramTemporalField, long paramLong, int paramInt1, int paramInt2)
  {
    Objects.requireNonNull(paramTemporalField, "field");
    Long localLong = (Long)currentParsed().fieldValues.put(paramTemporalField, Long.valueOf(paramLong));
    return (localLong != null) && (localLong.longValue() != paramLong) ? paramInt1 ^ 0xFFFFFFFF : paramInt2;
  }
  
  void setParsed(Chronology paramChronology)
  {
    Objects.requireNonNull(paramChronology, "chrono");
    currentParsed().chrono = paramChronology;
    if ((this.chronoListeners != null) && (!this.chronoListeners.isEmpty()))
    {
      Consumer[] arrayOfConsumer1 = new Consumer[1];
      Consumer[] arrayOfConsumer2 = (Consumer[])this.chronoListeners.toArray(arrayOfConsumer1);
      this.chronoListeners.clear();
      for (Consumer localConsumer : arrayOfConsumer2) {
        localConsumer.accept(paramChronology);
      }
    }
  }
  
  void addChronoChangedListener(Consumer<Chronology> paramConsumer)
  {
    if (this.chronoListeners == null) {
      this.chronoListeners = new ArrayList();
    }
    this.chronoListeners.add(paramConsumer);
  }
  
  void setParsed(ZoneId paramZoneId)
  {
    Objects.requireNonNull(paramZoneId, "zone");
    currentParsed().zone = paramZoneId;
  }
  
  void setParsedLeapSecond()
  {
    currentParsed().leapSecond = true;
  }
  
  public String toString()
  {
    return currentParsed().toString();
  }
}
