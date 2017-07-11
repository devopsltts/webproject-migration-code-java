package javax.swing.plaf.basic;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JList;

public abstract interface ComboPopup
{
  public abstract void show();
  
  public abstract void hide();
  
  public abstract boolean isVisible();
  
  public abstract JList getList();
  
  public abstract MouseListener getMouseListener();
  
  public abstract MouseMotionListener getMouseMotionListener();
  
  public abstract KeyListener getKeyListener();
  
  public abstract void uninstallingUI();
}
