package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.io.Serializable;

public class ViewportLayout
  implements LayoutManager, Serializable
{
  static ViewportLayout SHARED_INSTANCE = new ViewportLayout();
  
  public ViewportLayout() {}
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer)
  {
    Component localComponent = ((JViewport)paramContainer).getView();
    if (localComponent == null) {
      return new Dimension(0, 0);
    }
    if ((localComponent instanceof Scrollable)) {
      return ((Scrollable)localComponent).getPreferredScrollableViewportSize();
    }
    return localComponent.getPreferredSize();
  }
  
  public Dimension minimumLayoutSize(Container paramContainer)
  {
    return new Dimension(4, 4);
  }
  
  public void layoutContainer(Container paramContainer)
  {
    JViewport localJViewport = (JViewport)paramContainer;
    Component localComponent = localJViewport.getView();
    Scrollable localScrollable = null;
    if (localComponent == null) {
      return;
    }
    if ((localComponent instanceof Scrollable)) {
      localScrollable = (Scrollable)localComponent;
    }
    Insets localInsets = localJViewport.getInsets();
    Dimension localDimension1 = localComponent.getPreferredSize();
    Dimension localDimension2 = localJViewport.getSize();
    Dimension localDimension3 = localJViewport.toViewCoordinates(localDimension2);
    Dimension localDimension4 = new Dimension(localDimension1);
    if (localScrollable != null)
    {
      if (localScrollable.getScrollableTracksViewportWidth()) {
        localDimension4.width = localDimension2.width;
      }
      if (localScrollable.getScrollableTracksViewportHeight()) {
        localDimension4.height = localDimension2.height;
      }
    }
    Point localPoint = localJViewport.getViewPosition();
    if ((localScrollable == null) || (localJViewport.getParent() == null) || (localJViewport.getParent().getComponentOrientation().isLeftToRight()))
    {
      if (localPoint.x + localDimension3.width > localDimension4.width) {
        localPoint.x = Math.max(0, localDimension4.width - localDimension3.width);
      }
    }
    else if (localDimension3.width > localDimension4.width) {
      localPoint.x = (localDimension4.width - localDimension3.width);
    } else {
      localPoint.x = Math.max(0, Math.min(localDimension4.width - localDimension3.width, localPoint.x));
    }
    if (localPoint.y + localDimension3.height > localDimension4.height) {
      localPoint.y = Math.max(0, localDimension4.height - localDimension3.height);
    }
    if (localScrollable == null)
    {
      if ((localPoint.x == 0) && (localDimension2.width > localDimension1.width)) {
        localDimension4.width = localDimension2.width;
      }
      if ((localPoint.y == 0) && (localDimension2.height > localDimension1.height)) {
        localDimension4.height = localDimension2.height;
      }
    }
    localJViewport.setViewPosition(localPoint);
    localJViewport.setViewSize(localDimension4);
  }
}
