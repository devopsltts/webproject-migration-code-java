package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpringLayout
  implements LayoutManager2
{
  private Map<Component, Constraints> componentConstraints = new HashMap();
  private Spring cyclicReference = Spring.constant(Integer.MIN_VALUE);
  private Set<Spring> cyclicSprings;
  private Set<Spring> acyclicSprings;
  public static final String NORTH = "North";
  public static final String SOUTH = "South";
  public static final String EAST = "East";
  public static final String WEST = "West";
  public static final String HORIZONTAL_CENTER = "HorizontalCenter";
  public static final String VERTICAL_CENTER = "VerticalCenter";
  public static final String BASELINE = "Baseline";
  public static final String WIDTH = "Width";
  public static final String HEIGHT = "Height";
  private static String[] ALL_HORIZONTAL = { "West", "Width", "East", "HorizontalCenter" };
  private static String[] ALL_VERTICAL = { "North", "Height", "South", "VerticalCenter", "Baseline" };
  
  public SpringLayout() {}
  
  private void resetCyclicStatuses()
  {
    this.cyclicSprings = new HashSet();
    this.acyclicSprings = new HashSet();
  }
  
  private void setParent(Container paramContainer)
  {
    resetCyclicStatuses();
    Constraints localConstraints = getConstraints(paramContainer);
    localConstraints.setX(Spring.constant(0));
    localConstraints.setY(Spring.constant(0));
    Spring localSpring1 = localConstraints.getWidth();
    if (((localSpring1 instanceof Spring.WidthSpring)) && (((Spring.WidthSpring)localSpring1).c == paramContainer)) {
      localConstraints.setWidth(Spring.constant(0, 0, Integer.MAX_VALUE));
    }
    Spring localSpring2 = localConstraints.getHeight();
    if (((localSpring2 instanceof Spring.HeightSpring)) && (((Spring.HeightSpring)localSpring2).c == paramContainer)) {
      localConstraints.setHeight(Spring.constant(0, 0, Integer.MAX_VALUE));
    }
  }
  
  boolean isCyclic(Spring paramSpring)
  {
    if (paramSpring == null) {
      return false;
    }
    if (this.cyclicSprings.contains(paramSpring)) {
      return true;
    }
    if (this.acyclicSprings.contains(paramSpring)) {
      return false;
    }
    this.cyclicSprings.add(paramSpring);
    boolean bool = paramSpring.isCyclic(this);
    if (!bool)
    {
      this.acyclicSprings.add(paramSpring);
      this.cyclicSprings.remove(paramSpring);
    }
    else
    {
      System.err.println(paramSpring + " is cyclic. ");
    }
    return bool;
  }
  
  private Spring abandonCycles(Spring paramSpring)
  {
    return isCyclic(paramSpring) ? this.cyclicReference : paramSpring;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent)
  {
    this.componentConstraints.remove(paramComponent);
  }
  
  private static Dimension addInsets(int paramInt1, int paramInt2, Container paramContainer)
  {
    Insets localInsets = paramContainer.getInsets();
    return new Dimension(paramInt1 + localInsets.left + localInsets.right, paramInt2 + localInsets.top + localInsets.bottom);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    setParent(paramContainer);
    Constraints localConstraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(localConstraints.getWidth()).getMinimumValue(), abandonCycles(localConstraints.getHeight()).getMinimumValue(), paramContainer);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    setParent(paramContainer);
    Constraints localConstraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(localConstraints.getWidth()).getPreferredValue(), abandonCycles(localConstraints.getHeight()).getPreferredValue(), paramContainer);
  }
  
  public Dimension maximumLayoutSize(Container paramContainer)
  {
    setParent(paramContainer);
    Constraints localConstraints = getConstraints(paramContainer);
    return addInsets(abandonCycles(localConstraints.getWidth()).getMaximumValue(), abandonCycles(localConstraints.getHeight()).getMaximumValue(), paramContainer);
  }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject)
  {
    if ((paramObject instanceof Constraints)) {
      putConstraints(paramComponent, (Constraints)paramObject);
    }
  }
  
  public float getLayoutAlignmentX(Container paramContainer)
  {
    return 0.5F;
  }
  
  public float getLayoutAlignmentY(Container paramContainer)
  {
    return 0.5F;
  }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void putConstraint(String paramString1, Component paramComponent1, int paramInt, String paramString2, Component paramComponent2)
  {
    putConstraint(paramString1, paramComponent1, Spring.constant(paramInt), paramString2, paramComponent2);
  }
  
  public void putConstraint(String paramString1, Component paramComponent1, Spring paramSpring, String paramString2, Component paramComponent2)
  {
    putConstraint(paramString1, paramComponent1, Spring.sum(paramSpring, getConstraint(paramString2, paramComponent2)));
  }
  
  private void putConstraint(String paramString, Component paramComponent, Spring paramSpring)
  {
    if (paramSpring != null) {
      getConstraints(paramComponent).setConstraint(paramString, paramSpring);
    }
  }
  
  private Constraints applyDefaults(Component paramComponent, Constraints paramConstraints)
  {
    if (paramConstraints == null) {
      paramConstraints = new Constraints();
    }
    if (paramConstraints.c == null) {
      paramConstraints.c = paramComponent;
    }
    if (paramConstraints.horizontalHistory.size() < 2) {
      applyDefaults(paramConstraints, "West", Spring.constant(0), "Width", Spring.width(paramComponent), paramConstraints.horizontalHistory);
    }
    if (paramConstraints.verticalHistory.size() < 2) {
      applyDefaults(paramConstraints, "North", Spring.constant(0), "Height", Spring.height(paramComponent), paramConstraints.verticalHistory);
    }
    return paramConstraints;
  }
  
  private void applyDefaults(Constraints paramConstraints, String paramString1, Spring paramSpring1, String paramString2, Spring paramSpring2, List<String> paramList)
  {
    if (paramList.size() == 0)
    {
      paramConstraints.setConstraint(paramString1, paramSpring1);
      paramConstraints.setConstraint(paramString2, paramSpring2);
    }
    else
    {
      if (paramConstraints.getConstraint(paramString2) == null) {
        paramConstraints.setConstraint(paramString2, paramSpring2);
      } else {
        paramConstraints.setConstraint(paramString1, paramSpring1);
      }
      Collections.rotate(paramList, 1);
    }
  }
  
  private void putConstraints(Component paramComponent, Constraints paramConstraints)
  {
    this.componentConstraints.put(paramComponent, applyDefaults(paramComponent, paramConstraints));
  }
  
  public Constraints getConstraints(Component paramComponent)
  {
    Constraints localConstraints = (Constraints)this.componentConstraints.get(paramComponent);
    if (localConstraints == null)
    {
      if ((paramComponent instanceof JComponent))
      {
        Object localObject = ((JComponent)paramComponent).getClientProperty(SpringLayout.class);
        if ((localObject instanceof Constraints)) {
          return applyDefaults(paramComponent, (Constraints)localObject);
        }
      }
      localConstraints = new Constraints();
      putConstraints(paramComponent, localConstraints);
    }
    return localConstraints;
  }
  
  public Spring getConstraint(String paramString, Component paramComponent)
  {
    paramString = paramString.intern();
    return new SpringProxy(paramString, paramComponent, this);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    setParent(paramContainer);
    int i = paramContainer.getComponentCount();
    getConstraints(paramContainer).reset();
    for (int j = 0; j < i; j++) {
      getConstraints(paramContainer.getComponent(j)).reset();
    }
    Insets localInsets = paramContainer.getInsets();
    Constraints localConstraints1 = getConstraints(paramContainer);
    abandonCycles(localConstraints1.getX()).setValue(0);
    abandonCycles(localConstraints1.getY()).setValue(0);
    abandonCycles(localConstraints1.getWidth()).setValue(paramContainer.getWidth() - localInsets.left - localInsets.right);
    abandonCycles(localConstraints1.getHeight()).setValue(paramContainer.getHeight() - localInsets.top - localInsets.bottom);
    for (int k = 0; k < i; k++)
    {
      Component localComponent = paramContainer.getComponent(k);
      Constraints localConstraints2 = getConstraints(localComponent);
      int m = abandonCycles(localConstraints2.getX()).getValue();
      int n = abandonCycles(localConstraints2.getY()).getValue();
      int i1 = abandonCycles(localConstraints2.getWidth()).getValue();
      int i2 = abandonCycles(localConstraints2.getHeight()).getValue();
      localComponent.setBounds(localInsets.left + m, localInsets.top + n, i1, i2);
    }
  }
  
  public static class Constraints
  {
    private Spring x;
    private Spring y;
    private Spring width;
    private Spring height;
    private Spring east;
    private Spring south;
    private Spring horizontalCenter;
    private Spring verticalCenter;
    private Spring baseline;
    private List<String> horizontalHistory = new ArrayList(2);
    private List<String> verticalHistory = new ArrayList(2);
    private Component c;
    
    public Constraints() {}
    
    public Constraints(Spring paramSpring1, Spring paramSpring2)
    {
      setX(paramSpring1);
      setY(paramSpring2);
    }
    
    public Constraints(Spring paramSpring1, Spring paramSpring2, Spring paramSpring3, Spring paramSpring4)
    {
      setX(paramSpring1);
      setY(paramSpring2);
      setWidth(paramSpring3);
      setHeight(paramSpring4);
    }
    
    public Constraints(Component paramComponent)
    {
      this.c = paramComponent;
      setX(Spring.constant(paramComponent.getX()));
      setY(Spring.constant(paramComponent.getY()));
      setWidth(Spring.width(paramComponent));
      setHeight(Spring.height(paramComponent));
    }
    
    private void pushConstraint(String paramString, Spring paramSpring, boolean paramBoolean)
    {
      int i = 1;
      List localList = paramBoolean ? this.horizontalHistory : this.verticalHistory;
      if (localList.contains(paramString))
      {
        localList.remove(paramString);
        i = 0;
      }
      else if ((localList.size() == 2) && (paramSpring != null))
      {
        localList.remove(0);
        i = 0;
      }
      if (paramSpring != null) {
        localList.add(paramString);
      }
      if (i == 0)
      {
        String[] arrayOfString1 = paramBoolean ? SpringLayout.ALL_HORIZONTAL : SpringLayout.ALL_VERTICAL;
        for (String str : arrayOfString1) {
          if (!localList.contains(str)) {
            setConstraint(str, null);
          }
        }
      }
    }
    
    private Spring sum(Spring paramSpring1, Spring paramSpring2)
    {
      return (paramSpring1 == null) || (paramSpring2 == null) ? null : Spring.sum(paramSpring1, paramSpring2);
    }
    
    private Spring difference(Spring paramSpring1, Spring paramSpring2)
    {
      return (paramSpring1 == null) || (paramSpring2 == null) ? null : Spring.difference(paramSpring1, paramSpring2);
    }
    
    private Spring scale(Spring paramSpring, float paramFloat)
    {
      return paramSpring == null ? null : Spring.scale(paramSpring, paramFloat);
    }
    
    private int getBaselineFromHeight(int paramInt)
    {
      if (paramInt < 0) {
        return -this.c.getBaseline(this.c.getPreferredSize().width, -paramInt);
      }
      return this.c.getBaseline(this.c.getPreferredSize().width, paramInt);
    }
    
    private int getHeightFromBaseLine(int paramInt)
    {
      Dimension localDimension = this.c.getPreferredSize();
      int i = localDimension.height;
      int j = this.c.getBaseline(localDimension.width, i);
      if (j == paramInt) {
        return i;
      }
      switch (SpringLayout.1.$SwitchMap$java$awt$Component$BaselineResizeBehavior[this.c.getBaselineResizeBehavior().ordinal()])
      {
      case 1: 
        return i + (paramInt - j);
      case 2: 
        return i + 2 * (paramInt - j);
      }
      return Integer.MIN_VALUE;
    }
    
    private Spring heightToRelativeBaseline(Spring paramSpring)
    {
      new Spring.SpringMap(paramSpring)
      {
        protected int map(int paramAnonymousInt)
        {
          return SpringLayout.Constraints.this.getBaselineFromHeight(paramAnonymousInt);
        }
        
        protected int inv(int paramAnonymousInt)
        {
          return SpringLayout.Constraints.this.getHeightFromBaseLine(paramAnonymousInt);
        }
      };
    }
    
    private Spring relativeBaselineToHeight(Spring paramSpring)
    {
      new Spring.SpringMap(paramSpring)
      {
        protected int map(int paramAnonymousInt)
        {
          return SpringLayout.Constraints.this.getHeightFromBaseLine(paramAnonymousInt);
        }
        
        protected int inv(int paramAnonymousInt)
        {
          return SpringLayout.Constraints.this.getBaselineFromHeight(paramAnonymousInt);
        }
      };
    }
    
    private boolean defined(List paramList, String paramString1, String paramString2)
    {
      return (paramList.contains(paramString1)) && (paramList.contains(paramString2));
    }
    
    public void setX(Spring paramSpring)
    {
      this.x = paramSpring;
      pushConstraint("West", paramSpring, true);
    }
    
    public Spring getX()
    {
      if (this.x == null) {
        if (defined(this.horizontalHistory, "East", "Width")) {
          this.x = difference(this.east, this.width);
        } else if (defined(this.horizontalHistory, "HorizontalCenter", "Width")) {
          this.x = difference(this.horizontalCenter, scale(this.width, 0.5F));
        } else if (defined(this.horizontalHistory, "HorizontalCenter", "East")) {
          this.x = difference(scale(this.horizontalCenter, 2.0F), this.east);
        }
      }
      return this.x;
    }
    
    public void setY(Spring paramSpring)
    {
      this.y = paramSpring;
      pushConstraint("North", paramSpring, false);
    }
    
    public Spring getY()
    {
      if (this.y == null) {
        if (defined(this.verticalHistory, "South", "Height")) {
          this.y = difference(this.south, this.height);
        } else if (defined(this.verticalHistory, "VerticalCenter", "Height")) {
          this.y = difference(this.verticalCenter, scale(this.height, 0.5F));
        } else if (defined(this.verticalHistory, "VerticalCenter", "South")) {
          this.y = difference(scale(this.verticalCenter, 2.0F), this.south);
        } else if (defined(this.verticalHistory, "Baseline", "Height")) {
          this.y = difference(this.baseline, heightToRelativeBaseline(this.height));
        } else if (defined(this.verticalHistory, "Baseline", "South")) {
          this.y = scale(difference(this.baseline, heightToRelativeBaseline(this.south)), 2.0F);
        }
      }
      return this.y;
    }
    
    public void setWidth(Spring paramSpring)
    {
      this.width = paramSpring;
      pushConstraint("Width", paramSpring, true);
    }
    
    public Spring getWidth()
    {
      if (this.width == null) {
        if (this.horizontalHistory.contains("East")) {
          this.width = difference(this.east, getX());
        } else if (this.horizontalHistory.contains("HorizontalCenter")) {
          this.width = scale(difference(this.horizontalCenter, getX()), 2.0F);
        }
      }
      return this.width;
    }
    
    public void setHeight(Spring paramSpring)
    {
      this.height = paramSpring;
      pushConstraint("Height", paramSpring, false);
    }
    
    public Spring getHeight()
    {
      if (this.height == null) {
        if (this.verticalHistory.contains("South")) {
          this.height = difference(this.south, getY());
        } else if (this.verticalHistory.contains("VerticalCenter")) {
          this.height = scale(difference(this.verticalCenter, getY()), 2.0F);
        } else if (this.verticalHistory.contains("Baseline")) {
          this.height = relativeBaselineToHeight(difference(this.baseline, getY()));
        }
      }
      return this.height;
    }
    
    private void setEast(Spring paramSpring)
    {
      this.east = paramSpring;
      pushConstraint("East", paramSpring, true);
    }
    
    private Spring getEast()
    {
      if (this.east == null) {
        this.east = sum(getX(), getWidth());
      }
      return this.east;
    }
    
    private void setSouth(Spring paramSpring)
    {
      this.south = paramSpring;
      pushConstraint("South", paramSpring, false);
    }
    
    private Spring getSouth()
    {
      if (this.south == null) {
        this.south = sum(getY(), getHeight());
      }
      return this.south;
    }
    
    private Spring getHorizontalCenter()
    {
      if (this.horizontalCenter == null) {
        this.horizontalCenter = sum(getX(), scale(getWidth(), 0.5F));
      }
      return this.horizontalCenter;
    }
    
    private void setHorizontalCenter(Spring paramSpring)
    {
      this.horizontalCenter = paramSpring;
      pushConstraint("HorizontalCenter", paramSpring, true);
    }
    
    private Spring getVerticalCenter()
    {
      if (this.verticalCenter == null) {
        this.verticalCenter = sum(getY(), scale(getHeight(), 0.5F));
      }
      return this.verticalCenter;
    }
    
    private void setVerticalCenter(Spring paramSpring)
    {
      this.verticalCenter = paramSpring;
      pushConstraint("VerticalCenter", paramSpring, false);
    }
    
    private Spring getBaseline()
    {
      if (this.baseline == null) {
        this.baseline = sum(getY(), heightToRelativeBaseline(getHeight()));
      }
      return this.baseline;
    }
    
    private void setBaseline(Spring paramSpring)
    {
      this.baseline = paramSpring;
      pushConstraint("Baseline", paramSpring, false);
    }
    
    public void setConstraint(String paramString, Spring paramSpring)
    {
      paramString = paramString.intern();
      if (paramString == "West") {
        setX(paramSpring);
      } else if (paramString == "North") {
        setY(paramSpring);
      } else if (paramString == "East") {
        setEast(paramSpring);
      } else if (paramString == "South") {
        setSouth(paramSpring);
      } else if (paramString == "HorizontalCenter") {
        setHorizontalCenter(paramSpring);
      } else if (paramString == "Width") {
        setWidth(paramSpring);
      } else if (paramString == "Height") {
        setHeight(paramSpring);
      } else if (paramString == "VerticalCenter") {
        setVerticalCenter(paramSpring);
      } else if (paramString == "Baseline") {
        setBaseline(paramSpring);
      }
    }
    
    public Spring getConstraint(String paramString)
    {
      paramString = paramString.intern();
      return paramString == "Baseline" ? getBaseline() : paramString == "VerticalCenter" ? getVerticalCenter() : paramString == "HorizontalCenter" ? getHorizontalCenter() : paramString == "Height" ? getHeight() : paramString == "Width" ? getWidth() : paramString == "South" ? getSouth() : paramString == "East" ? getEast() : paramString == "North" ? getY() : paramString == "West" ? getX() : null;
    }
    
    void reset()
    {
      Spring[] arrayOfSpring1 = { this.x, this.y, this.width, this.height, this.east, this.south, this.horizontalCenter, this.verticalCenter, this.baseline };
      for (Spring localSpring : arrayOfSpring1) {
        if (localSpring != null) {
          localSpring.setValue(Integer.MIN_VALUE);
        }
      }
    }
  }
  
  private static class SpringProxy
    extends Spring
  {
    private String edgeName;
    private Component c;
    private SpringLayout l;
    
    public SpringProxy(String paramString, Component paramComponent, SpringLayout paramSpringLayout)
    {
      this.edgeName = paramString;
      this.c = paramComponent;
      this.l = paramSpringLayout;
    }
    
    private Spring getConstraint()
    {
      return this.l.getConstraints(this.c).getConstraint(this.edgeName);
    }
    
    public int getMinimumValue()
    {
      return getConstraint().getMinimumValue();
    }
    
    public int getPreferredValue()
    {
      return getConstraint().getPreferredValue();
    }
    
    public int getMaximumValue()
    {
      return getConstraint().getMaximumValue();
    }
    
    public int getValue()
    {
      return getConstraint().getValue();
    }
    
    public void setValue(int paramInt)
    {
      getConstraint().setValue(paramInt);
    }
    
    boolean isCyclic(SpringLayout paramSpringLayout)
    {
      return paramSpringLayout.isCyclic(getConstraint());
    }
    
    public String toString()
    {
      return "SpringProxy for " + this.edgeName + " edge of " + this.c.getName() + ".";
    }
  }
}
