package javax.swing.plaf.nimbus;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;

public abstract class State<T extends JComponent>
{
  static final Map<String, StandardState> standardStates = new HashMap(7);
  static final State Enabled = new StandardState(1, null);
  static final State MouseOver = new StandardState(2, null);
  static final State Pressed = new StandardState(4, null);
  static final State Disabled = new StandardState(8, null);
  static final State Focused = new StandardState(256, null);
  static final State Selected = new StandardState(512, null);
  static final State Default = new StandardState(1024, null);
  private String name;
  
  protected State(String paramString)
  {
    this.name = paramString;
  }
  
  public String toString()
  {
    return this.name;
  }
  
  boolean isInState(T paramT, int paramInt)
  {
    return isInState(paramT);
  }
  
  protected abstract boolean isInState(T paramT);
  
  String getName()
  {
    return this.name;
  }
  
  static boolean isStandardStateName(String paramString)
  {
    return standardStates.containsKey(paramString);
  }
  
  static StandardState getStandardState(String paramString)
  {
    return (StandardState)standardStates.get(paramString);
  }
  
  static final class StandardState
    extends State<JComponent>
  {
    private int state;
    
    private StandardState(int paramInt)
    {
      super();
      this.state = paramInt;
      standardStates.put(getName(), this);
    }
    
    public int getState()
    {
      return this.state;
    }
    
    boolean isInState(JComponent paramJComponent, int paramInt)
    {
      return (paramInt & this.state) == this.state;
    }
    
    protected boolean isInState(JComponent paramJComponent)
    {
      throw new AssertionError("This method should never be called");
    }
    
    private static String toString(int paramInt)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      if ((paramInt & 0x400) == 1024) {
        localStringBuffer.append("Default");
      }
      if ((paramInt & 0x8) == 8)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("Disabled");
      }
      if ((paramInt & 0x1) == 1)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("Enabled");
      }
      if ((paramInt & 0x100) == 256)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("Focused");
      }
      if ((paramInt & 0x2) == 2)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("MouseOver");
      }
      if ((paramInt & 0x4) == 4)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("Pressed");
      }
      if ((paramInt & 0x200) == 512)
      {
        if (localStringBuffer.length() > 0) {
          localStringBuffer.append("+");
        }
        localStringBuffer.append("Selected");
      }
      return localStringBuffer.toString();
    }
  }
}
