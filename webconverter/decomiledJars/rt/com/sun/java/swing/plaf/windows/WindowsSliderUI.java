package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;

public class WindowsSliderUI
  extends BasicSliderUI
{
  private boolean rollover = false;
  private boolean pressed = false;
  
  public WindowsSliderUI(JSlider paramJSlider)
  {
    super(paramJSlider);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new WindowsSliderUI((JSlider)paramJComponent);
  }
  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider paramJSlider)
  {
    return new WindowsTrackListener(null);
  }
  
  public void paintTrack(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      int i = this.slider.getOrientation() == 1 ? 1 : 0;
      TMSchema.Part localPart = i != 0 ? TMSchema.Part.TKP_TRACKVERT : TMSchema.Part.TKP_TRACK;
      XPStyle.Skin localSkin = localXPStyle.getSkin(this.slider, localPart);
      int j;
      if (i != 0)
      {
        j = (this.trackRect.width - localSkin.getWidth()) / 2;
        localSkin.paintSkin(paramGraphics, this.trackRect.x + j, this.trackRect.y, localSkin.getWidth(), this.trackRect.height, null);
      }
      else
      {
        j = (this.trackRect.height - localSkin.getHeight()) / 2;
        localSkin.paintSkin(paramGraphics, this.trackRect.x, this.trackRect.y + j, this.trackRect.width, localSkin.getHeight(), null);
      }
    }
    else
    {
      super.paintTrack(paramGraphics);
    }
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMinorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMajorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMinorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null) {
      paramGraphics.setColor(localXPStyle.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black));
    }
    super.paintMajorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  public void paintThumb(Graphics paramGraphics)
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      TMSchema.Part localPart = getXPThumbPart();
      TMSchema.State localState = TMSchema.State.NORMAL;
      if (this.slider.hasFocus()) {
        localState = TMSchema.State.FOCUSED;
      }
      if (this.rollover) {
        localState = TMSchema.State.HOT;
      }
      if (this.pressed) {
        localState = TMSchema.State.PRESSED;
      }
      if (!this.slider.isEnabled()) {
        localState = TMSchema.State.DISABLED;
      }
      localXPStyle.getSkin(this.slider, localPart).paintSkin(paramGraphics, this.thumbRect.x, this.thumbRect.y, localState);
    }
    else
    {
      super.paintThumb(paramGraphics);
    }
  }
  
  protected Dimension getThumbSize()
  {
    XPStyle localXPStyle = XPStyle.getXP();
    if (localXPStyle != null)
    {
      Dimension localDimension = new Dimension();
      XPStyle.Skin localSkin = localXPStyle.getSkin(this.slider, getXPThumbPart());
      localDimension.width = localSkin.getWidth();
      localDimension.height = localSkin.getHeight();
      return localDimension;
    }
    return super.getThumbSize();
  }
  
  private TMSchema.Part getXPThumbPart()
  {
    int i = this.slider.getOrientation() == 1 ? 1 : 0;
    boolean bool = this.slider.getComponentOrientation().isLeftToRight();
    Boolean localBoolean = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
    TMSchema.Part localPart;
    if (((!this.slider.getPaintTicks()) && (localBoolean == null)) || (localBoolean == Boolean.FALSE)) {
      localPart = i != 0 ? TMSchema.Part.TKP_THUMBVERT : TMSchema.Part.TKP_THUMB;
    } else {
      localPart = i != 0 ? TMSchema.Part.TKP_THUMBLEFT : bool ? TMSchema.Part.TKP_THUMBRIGHT : TMSchema.Part.TKP_THUMBBOTTOM;
    }
    return localPart;
  }
  
  private class WindowsTrackListener
    extends BasicSliderUI.TrackListener
  {
    private WindowsTrackListener()
    {
      super();
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      updateRollover(WindowsSliderUI.this.thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mouseMoved(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      updateRollover(WindowsSliderUI.this.thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      updateRollover(false);
      super.mouseExited(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      updatePressed(WindowsSliderUI.this.thumbRect.contains(paramMouseEvent.getX(), paramMouseEvent.getY()));
      super.mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      updatePressed(false);
      super.mouseReleased(paramMouseEvent);
    }
    
    public void updatePressed(boolean paramBoolean)
    {
      if (!WindowsSliderUI.this.slider.isEnabled()) {
        return;
      }
      if (WindowsSliderUI.this.pressed != paramBoolean)
      {
        WindowsSliderUI.this.pressed = paramBoolean;
        WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
      }
    }
    
    public void updateRollover(boolean paramBoolean)
    {
      if (!WindowsSliderUI.this.slider.isEnabled()) {
        return;
      }
      if (WindowsSliderUI.this.rollover != paramBoolean)
      {
        WindowsSliderUI.this.rollover = paramBoolean;
        WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
      }
    }
  }
}
