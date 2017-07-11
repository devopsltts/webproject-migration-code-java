package javax.swing.plaf.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;

class LazyActionMap
  extends ActionMapUIResource
{
  private transient Object _loader;
  
  static void installLazyActionMap(JComponent paramJComponent, Class paramClass, String paramString)
  {
    Object localObject = (ActionMap)UIManager.get(paramString);
    if (localObject == null)
    {
      localObject = new LazyActionMap(paramClass);
      UIManager.getLookAndFeelDefaults().put(paramString, localObject);
    }
    SwingUtilities.replaceUIActionMap(paramJComponent, (ActionMap)localObject);
  }
  
  static ActionMap getActionMap(Class paramClass, String paramString)
  {
    Object localObject = (ActionMap)UIManager.get(paramString);
    if (localObject == null)
    {
      localObject = new LazyActionMap(paramClass);
      UIManager.getLookAndFeelDefaults().put(paramString, localObject);
    }
    return localObject;
  }
  
  private LazyActionMap(Class paramClass)
  {
    this._loader = paramClass;
  }
  
  public void put(Action paramAction)
  {
    put(paramAction.getValue("Name"), paramAction);
  }
  
  public void put(Object paramObject, Action paramAction)
  {
    loadIfNecessary();
    super.put(paramObject, paramAction);
  }
  
  public Action get(Object paramObject)
  {
    loadIfNecessary();
    return super.get(paramObject);
  }
  
  public void remove(Object paramObject)
  {
    loadIfNecessary();
    super.remove(paramObject);
  }
  
  public void clear()
  {
    loadIfNecessary();
    super.clear();
  }
  
  public Object[] keys()
  {
    loadIfNecessary();
    return super.keys();
  }
  
  public int size()
  {
    loadIfNecessary();
    return super.size();
  }
  
  public Object[] allKeys()
  {
    loadIfNecessary();
    return super.allKeys();
  }
  
  public void setParent(ActionMap paramActionMap)
  {
    loadIfNecessary();
    super.setParent(paramActionMap);
  }
  
  private void loadIfNecessary()
  {
    if (this._loader != null)
    {
      Object localObject = this._loader;
      this._loader = null;
      Class localClass = (Class)localObject;
      try
      {
        Method localMethod = localClass.getDeclaredMethod("loadActionMap", new Class[] { LazyActionMap.class });
        localMethod.invoke(localClass, new Object[] { this });
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError("LazyActionMap unable to load actions " + localClass);
        }
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError("LazyActionMap unable to load actions " + localIllegalAccessException);
        }
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError("LazyActionMap unable to load actions " + localInvocationTargetException);
        }
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError("LazyActionMap unable to load actions " + localIllegalArgumentException);
        }
      }
    }
  }
}
