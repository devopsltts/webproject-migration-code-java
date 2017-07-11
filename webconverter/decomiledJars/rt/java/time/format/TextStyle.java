package java.time.format;

public enum TextStyle
{
  FULL(2, 0),  FULL_STANDALONE(32770, 0),  SHORT(1, 1),  SHORT_STANDALONE(32769, 1),  NARROW(4, 1),  NARROW_STANDALONE(32772, 1);
  
  private final int calendarStyle;
  private final int zoneNameStyleIndex;
  
  private TextStyle(int paramInt1, int paramInt2)
  {
    this.calendarStyle = paramInt1;
    this.zoneNameStyleIndex = paramInt2;
  }
  
  public boolean isStandalone()
  {
    return (ordinal() & 0x1) == 1;
  }
  
  public TextStyle asStandalone()
  {
    return values()[(ordinal() | 0x1)];
  }
  
  public TextStyle asNormal()
  {
    return values()[(ordinal() & 0xFFFFFFFE)];
  }
  
  int toCalendarStyle()
  {
    return this.calendarStyle;
  }
  
  int zoneNameStyleIndex()
  {
    return this.zoneNameStyleIndex;
  }
}
